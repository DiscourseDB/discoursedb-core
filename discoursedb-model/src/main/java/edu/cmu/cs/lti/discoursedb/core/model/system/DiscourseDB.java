package edu.cmu.cs.lti.discoursedb.core.model.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntity;

@Entity
@Table(name="discoursedb")
public class DiscourseDB extends TimedAnnotatableBaseEntity implements Serializable{

	private static final long serialVersionUID = 3740314651476462251L;

	private long id;
	
	private String schemaVersion;
		
	public DiscourseDB(){}
	
	@Id
	@Column(name="id_discoursedb", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}
	
	@SuppressWarnings("unused") //used by hibernate through reflection, but not exposed to users
	private void setId(long id) {
		this.id = id;
	}
	@Column(name="schema_version")
	public String getSchemaVersion() {
		return schemaVersion;
	}
	
	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}
	
}
