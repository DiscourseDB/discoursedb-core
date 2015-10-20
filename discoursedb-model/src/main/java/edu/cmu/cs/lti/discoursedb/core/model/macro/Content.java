package edu.cmu.cs.lti.discoursedb.core.model.macro;

import java.io.Serializable;
import java.sql.Blob;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntityWithSource;
import edu.cmu.cs.lti.discoursedb.core.model.user.ContributionInteraction;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;

/**
 * Content entities represent the content of Contribution and Context entities.
 * The main payload of a Content entity resides in its text and data field. The
 * content of Contributions usually textual, thus the text field will hold the
 * content of a Contribution. The data field is able to hold arbitrary blobs of
 * data. This is most likely necessary when used to represent the content of
 * Context entities but will rarely be the case for content of Contribution
 * entities. Content entities formally represent nodes in a linked list by
 * pointing to a previous and a next content revision. This way, revision
 * histories of Contribution and Context entities can be represented. A Content
 * entity is related to a User indicating that this user is the author of the
 * content instance. Other relationships between Users and Content or
 * Contributions can be represented with ContributionUserInteraction entities.
 * 
 * @author Oliver Ferschke
 *
 */
@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="content")
public class Content extends TimedAnnotatableBaseEntityWithSource implements Serializable {

	private static final long serialVersionUID = -1465025480150664388L;

	private long id;
	
	private Content previousRevision;
	
	private Content nextRevision;

	private String title;

	private String text;
	
	private Blob data;
	
	private User author;

	private Set<ContributionInteraction> contributionInteractions = new HashSet<ContributionInteraction>();
	
	public Content(){}

	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_user_id")
	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}
	
	@Id
	@Column(name="id_content", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_previous_revision")
	public Content getPreviousRevision() {
		return previousRevision;
	}

	public void setPreviousRevision(Content previousRevision) {
		this.previousRevision = previousRevision;
	}

	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_next_revision")
	public Content getNextRevision() {
		return nextRevision;
	}

	public void setNextRevision(Content nextRevision) {
		this.nextRevision = nextRevision;
	}

	@Column(columnDefinition="TEXT")
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Lob
	public Blob getData() {
		return data;
	}

	public void setData(Blob data) {
		this.data = data;
	}

    @OneToMany(mappedBy = "content")
	public Set<ContributionInteraction> getContributionInteractions() {
		return contributionInteractions;
	}

	public void setContributionInteractions(Set<ContributionInteraction> contributionInteractions) {
		this.contributionInteractions = contributionInteractions;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
