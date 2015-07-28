package edu.cmu.cs.lti.discoursedb.macro;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.annotation.Annotation;

public class Context implements Serializable {

	private static final long serialVersionUID = 6013322457584994562L;

	private long id;
	
	private Content first_revision;
	
	private Content current_revision;
	
	private Timestamp start_time;
	
	private Timestamp end_time;

	private Context_Type type;
	
	private Set<Annotation> annotations = new HashSet<Annotation>();

	public Context(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Content getFirst_revision() {
		return first_revision;
	}

	public void setFirst_revision(Content first_revision) {
		this.first_revision = first_revision;
	}

	public Content getCurrent_revision() {
		return current_revision;
	}

	public void setCurrent_revision(Content current_revision) {
		this.current_revision = current_revision;
	}

	public Timestamp getStart_time() {
		return start_time;
	}

	public void setStart_time(Timestamp start_time) {
		this.start_time = start_time;
	}

	public Timestamp getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Timestamp end_time) {
		this.end_time = end_time;
	}

	public Context_Type getType() {
		return type;
	}

	public void setType(Context_Type type) {
		this.type = type;
	}

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}
}
