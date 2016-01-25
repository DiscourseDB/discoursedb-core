package edu.cmu.cs.lti.discoursedb.core.model.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="discoursedb")
public class DiscourseDB extends TimedAnnotatableBaseEntity implements Serializable{

	private static final long serialVersionUID = 3740314651476462251L;

	@Id
	@Column(name="id_discoursedb", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@Column(name="schema_version")
	private String schemaVersion;
			
}
