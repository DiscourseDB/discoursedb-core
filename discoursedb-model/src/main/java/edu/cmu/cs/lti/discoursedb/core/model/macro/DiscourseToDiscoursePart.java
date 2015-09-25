package edu.cmu.cs.lti.discoursedb.core.model.macro;

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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntity;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="discourse_has_discourse_part", uniqueConstraints = @UniqueConstraint(columnNames = { "fk_discourse", "fk_discourse_part" }) )
public class DiscourseToDiscoursePart extends TimedAnnotatableBaseEntity implements Serializable{

	private static final long serialVersionUID = 6916868753034800946L;
	
	private long id;
	
    private Discourse discourse;
    
    private DiscoursePart discoursePart;
    
	public DiscourseToDiscoursePart() {}
	
	@Id
	@Column(name="id_discourse_has_discourse_part", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_discourse")  
    public Discourse getDiscourse() {
		return discourse;
	}

	public void setDiscourse(Discourse discourse) {
		this.discourse = discourse;
	}

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_discourse_part")
	public DiscoursePart getDiscoursePart() {
		return discoursePart;
	}

	public void setDiscoursePart(DiscoursePart discoursePart) {
		this.discoursePart = discoursePart;
	}
	
}
