package edu.cmu.cs.lti.discoursedb.core.model.macro;

import java.io.Serializable;
import java.sql.Blob;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntityWithSource;
import edu.cmu.cs.lti.discoursedb.core.model.user.ContributionInteraction;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

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
@Data
@EqualsAndHashCode(callSuper=true, exclude={"contributionInteractions","previousRevision","nextRevision"})
@ToString(callSuper=true, exclude={"contributionInteractions","previousRevision","nextRevision"})
@Entity
@Table(name="content")
public class Content extends TimedAnnotatableBaseEntityWithSource implements Serializable {

	private static final long serialVersionUID = -1465025480150664388L;

	@Id
	@Column(name="id_content", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY) 
	@JoinColumn(name = "fk_previous_revision")
	private Content previousRevision;
	
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY) 
	@JoinColumn(name = "fk_next_revision")
	private Content nextRevision;

	private String title;

	@Column(columnDefinition="LONGTEXT")
	private String text;
	
	@Column(columnDefinition="LONGBLOB")
	private Blob data;
	
	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_user_id")
	private User author;

    @OneToMany(mappedBy = "content")
	private Set<ContributionInteraction> contributionInteractions = new HashSet<ContributionInteraction>();
		
}
