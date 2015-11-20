package edu.cmu.cs.lti.discoursedb.core.model.user;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntity;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;

@Entity
@Table(name="discourse_part_interaction")
public class DiscoursePartInteraction extends TimedAnnotatableBaseEntity implements Serializable{

	private static final long serialVersionUID = -7782010595781927999L;

	private long id;
	
	private User user;
	
	private DiscoursePart discoursepart;
	
	private DiscoursePartInteractionType type;
	
	public DiscoursePartInteraction(){}
	
	@Id
	@Column(name="id_content_interaction", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	@SuppressWarnings("unused") //used by hibernate through reflection, but not exposed to users
	private void setId(long id) {
		this.id = id;
	}
	 @ManyToOne(cascade = CascadeType.ALL)
	 @JoinColumn(name = "fk_user")
	 public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_discourse_part")
	public DiscoursePart getDiscoursePart() {
		return discoursepart;
	}

	public void setDiscoursePart(DiscoursePart discoursepart) {
		this.discoursepart = discoursepart;
	}

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_discourse_part_interaction_type")
	public DiscoursePartInteractionType getType() {
		return type;
	}

	public void setType(DiscoursePartInteractionType type) {
		this.type = type;
	}

}
