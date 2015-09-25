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
