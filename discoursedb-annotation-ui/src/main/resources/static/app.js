'use strict';

const React = require('react');
const when = require('when');
const client = require('./client');
const follow = require('./follow'); // function to hop multiple links by "rel"
const stompClient = require('./websocket-listener');
const root = '/api';

class AnnotatorUI extends React.Component {

	constructor(props) {
		super(props);
		this.state = {contributions: [], attributes: [], page: 1, pageSize: 10, links: {}};
		this.updatePageSize = this.updatePageSize.bind(this);
		this.onCreate = this.onCreate.bind(this);
		this.onUpdate = this.onUpdate.bind(this);
		this.onDelete = this.onDelete.bind(this);
		this.onNavigate = this.onNavigate.bind(this);
		this.refreshCurrentPage = this.refreshCurrentPage.bind(this);
		this.refreshAndGoToLastPage = this.refreshAndGoToLastPage.bind(this);
	}

	/*
	 * Load data from DiscourseDB
	 * 
	 * pageSize can be updated via Websocket connection
	 */
	loadFromServer(pageSize) {
		follow(client, root, [
				{rel: 'contributions', params: {size: pageSize}}]
		).then(contributionCollection => {
				return client({
					method: 'GET',
					path: contributionCollection.entity._links.profile.href,
					headers: {'Accept': 'application/schema+json'}
				}).then(schema => {
					this.schema = schema.entity;
					this.links = contributionCollection.entity._links;
					return contributionCollection;
				});
		}).then(contributionCollection => {
			this.page = contributionCollection.entity.page;
			return contributionCollection.entity._embedded.contributions.map(contribution =>
					client({
						method: 'GET',
						path: contribution._links.self.href
					})
			);
		}).then(contributionPromises => {
			return when.all(contributionPromises);
		}).done(contributions => {
			this.setState({
				page: this.page,
				contributions: contributions,
				attributes: Object.keys(this.schema.properties),
				pageSize: pageSize,
				links: this.links
			});
		});
	}

	// tag::on-create[]
	onCreate(newContribution) {
		follow(client, root, ['contributions']).done(response => {
			client({
				method: 'POST',
				path: response.entity._links.self.href,
				entity: newContribution,
				headers: {'Content-Type': 'application/json'}
			})
		})
	}
	// end::on-create[]

	onUpdate(contribution, updatedContribution) {
		client({
			method: 'PUT',
			path: contribution.entity._links.self.href,
			entity: updatedContribution,
			headers: {
				'Content-Type': 'application/json',
				'If-Match': contribution.headers.Etag
			}
		}).done(response => {
			/* Let the websocket handler update the state */
		}, response => {
			if (response.status.code === 412) {
				alert('DENIED: Unable to update ' + contribution.entity._links.self.href + '. Your copy is stale.');
			}
		});
	}

	onDelete(contribution) {
		client({method: 'DELETE', path: contribution.entity._links.self.href});
	}

	onNavigate(navUri) {
		client({
			method: 'GET',
			path: navUri
		}).then(contributionCollection => {
			this.links = contributionCollection.entity._links;
			this.page = contributionCollection.entity.page;

			return contributionCollection.entity._embedded.contributions.map(contribution =>
					client({
						method: 'GET',
						path: contribution._links.self.href
					})
			);
		}).then(contributionPromises => {
			return when.all(contributionPromises);
		}).done(contributions => {
			this.setState({
				page: this.page,
				contributions: contributions,
				attributes: Object.keys(this.schema.properties),
				pageSize: this.state.pageSize,
				links: this.links
			});
		});
	}

	updatePageSize(pageSize) {
		if (pageSize !== this.state.pageSize) {
			this.loadFromServer(pageSize);
		}
	}

	// tag::websocket-handlers[]
	refreshAndGoToLastPage(message) {
		follow(client, root, [{
			rel: 'contributions',
			params: {size: this.state.pageSize}
		}]).done(response => {
			this.onNavigate(response.entity._links.last.href);
		})
	}

	refreshCurrentPage(message) {
		follow(client, root, [{
			rel: 'contributions',
			params: {
				size: this.state.pageSize,
				page: this.state.page.number
			}
		}]).then(contributionCollection => {
			this.links = contributionCollection.entity._links;
			this.page = contributionCollection.entity.page;

			return contributionCollection.entity._embedded.contributions.map(contribution => {
				return client({
					method: 'GET',
					path: contribution._links.self.href
				})
			});
		}).then(contributionPromises => {
			return when.all(contributionPromises);
		}).then(contributions => {
			this.setState({
				page: this.page,
				contributions: contributions,
				attributes: Object.keys(this.schema.properties),
				pageSize: this.state.pageSize,
				links: this.links
			});
		});
	}
	// end::websocket-handlers[]

	// tag::register-handlers[]
	componentDidMount() {
		this.loadFromServer(this.state.pageSize);
		stompClient.register([
			{route: '/topic/newContribution', callback: this.refreshAndGoToLastPage},
			{route: '/topic/updateContribution', callback: this.refreshCurrentPage},
			{route: '/topic/deleteContribution', callback: this.refreshCurrentPage}
		]);
	}
	// end::register-handlers[]

	render() {
		return (
			<div>
				<CreateDialog attributes={this.state.attributes} onCreate={this.onCreate}/>
				<ContributionList page={this.state.page}
							  contributions={this.state.contributions}
							  links={this.state.links}
							  pageSize={this.state.pageSize}
							  attributes={this.state.attributes}
							  onNavigate={this.onNavigate}
							  onUpdate={this.onUpdate}
							  onDelete={this.onDelete}
							  updatePageSize={this.updatePageSize}/>
			</div>
		)
	}
}

/*
 * Represents a create dialog for new contributions
 */	
class CreateDialog extends React.Component {

	constructor(props) {
		super(props);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleSubmit(e) {
		e.preventDefault();
		var newContribution = {};
		this.props.attributes.forEach(attribute => {
			newContribution[attribute] = React.findDOMNode(this.refs[attribute]).value.trim();
		});
		this.props.onCreate(newContribution);
		this.props.attributes.forEach(attribute => {
			React.findDOMNode(this.refs[attribute]).value = ''; // clear out the dialog's inputs
		});
		window.location = "#";
	}

	render() {
		var inputs = this.props.attributes.map(attribute =>
				<p key={attribute}>
					<input type="text" placeholder={attribute} ref={attribute} className="field" />
				</p>
		);
		return (
			<div>
				<a href="#createContribution">Create</a>

				<div id="createContribution" className="modalDialog">
					<div>
						<a href="#" title="Close" className="close">X</a>

						<h2>Create new contribution</h2>

						<form>
							{inputs}
							<button onClick={this.handleSubmit}>Create</button>
						</form>
					</div>
				</div>
			</div>
		)
	}
}

class UpdateDialog extends React.Component {

	constructor(props) {
		super(props);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleSubmit(e) {
		e.preventDefault();
		var updatedContribution = {};
		this.props.attributes.forEach(attribute => {
			updatedContribution[attribute] = React.findDOMNode(this.refs[attribute]).value.trim();
		});
		this.props.onUpdate(this.props.contribution, updatedContribution);
		window.location = "#";
	}

	render() {
		var inputs = this.props.attributes.map(attribute =>
				<p key={this.props.contribution.entity[attribute]}>
					<input type="text" placeholder={attribute}
						   defaultValue={this.props.contribution.entity[attribute]}
						   ref={attribute} className="field" />
				</p>
		);

		var dialogId = "updateContribution-" + this.props.contribution.entity._links.self.href;

		return (
			<div>
				<a href={"#" + dialogId}>Update</a>

				<div id={dialogId} className="modalDialog">
					<div>
						<a href="#" title="Close" className="close">X</a>

						<h2>Update a contribution</h2>

						<form>
							{inputs}
							<button onClick={this.handleSubmit}>Update</button>
						</form>
					</div>
				</div>
			</div>
		)
	}

}

class ContributionList extends React.Component {

	constructor(props) {
		super(props);
		this.handleNavFirst = this.handleNavFirst.bind(this);
		this.handleNavPrev = this.handleNavPrev.bind(this);
		this.handleNavNext = this.handleNavNext.bind(this);
		this.handleNavLast = this.handleNavLast.bind(this);
		this.handleInput = this.handleInput.bind(this);
	}

	handleInput(e) {
		e.preventDefault();
		var pageSize = React.findDOMNode(this.refs.pageSize).value;
		if (/^[0-9]+$/.test(pageSize)) {
			this.props.updatePageSize(pageSize);
		} else {
			React.findDOMNode(this.refs.pageSize).value = pageSize.substring(0, pageSize.length - 1);
		}
	}

	handleNavFirst(e) {
		e.preventDefault();
		this.props.onNavigate(this.props.links.first.href);
	}

	handleNavPrev(e) {
		e.preventDefault();
		this.props.onNavigate(this.props.links.prev.href);
	}

	handleNavNext(e) {
		e.preventDefault();
		this.props.onNavigate(this.props.links.next.href);
	}

	handleNavLast(e) {
		e.preventDefault();
		this.props.onNavigate(this.props.links.last.href);
	}

	render() {
		var pageInfo = this.props.page.hasOwnProperty("number") ?
			<h3>Contributions - Page {this.props.page.number + 1} of {this.props.page.totalPages}</h3> : null;

		var contributions = this.props.contributions.map(contribution =>
			<Contribution key={contribution.entity._links.self.href}
					  contribution={contribution}
					  attributes={this.props.attributes}
					  onUpdate={this.props.onUpdate}
					  onDelete={this.props.onDelete}/>
		);

		var navLinks = [];
		if ("first" in this.props.links) {
			navLinks.push(<button key="first" onClick={this.handleNavFirst}>&lt;&lt;</button>);
		}
		if ("prev" in this.props.links) {
			navLinks.push(<button key="prev" onClick={this.handleNavPrev}>&lt;</button>);
		}
		if ("next" in this.props.links) {
			navLinks.push(<button key="next" onClick={this.handleNavNext}>&gt;</button>);
		}
		if ("last" in this.props.links) {
			navLinks.push(<button key="last" onClick={this.handleNavLast}>&gt;&gt;</button>);
		}

		return (
			<div>
				{pageInfo}
				<input ref="pageSize" defaultValue={this.props.pageSize} onInput={this.handleInput}/>
				<table>
					<tr>
						<th>Time</th>
						<th></th>
						<th></th>
					</tr>
					{contributions}
				</table>
				<div>
					{navLinks}
				</div>
			</div>
		)
	}
}

/*
 * Represents a contribution
 */
class Contribution extends React.Component {

	constructor(props) {
		super(props);
		this.handleDelete = this.handleDelete.bind(this);
	}

	handleDelete() {
		this.props.onDelete(this.props.contribution);
	}

	render() {
		return (
			<tr>
				<td>{this.props.contribution.entity.startTime}</td>
				<td>
					<UpdateDialog contribution={this.props.contribution}
								  attributes={this.props.attributes}
								  onUpdate={this.props.onUpdate}/>
				</td>
				<td>
					<button onClick={this.handleDelete}>Delete</button>
				</td>
			</tr>
		)
	}
}

/*
 * Renders the UI
 */
React.render(
	<AnnotatorUI />,
	document.getElementById('react')
)
