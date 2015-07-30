package edu.cmu.cs.lti.discoursedb.core.model.user;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.Annotations;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="\"group\"")
public class Group implements Serializable {

	private static final long serialVersionUID = -8400689664755883198L;

	private long id;

	private GroupType type;

	private String name;
	
	private Date startTime;
	
	private Date endTime;
	
	private Annotations annotations;
	
	private Set<AudienceGroup> groupAudiences = new HashSet<AudienceGroup>();

	private Set<GroupUser> groupMembers = new HashSet<GroupUser>();
	
	public Group(){}

	private Date version;
	@Version
	public Date getVersion() {
		return version;
	}
	public void setVersion(Date version) {
		this.version = version;
	}
	
	@Id
	@Column(name="id_group", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_group_type")
	public GroupType getType() {
		return type;
	}

	public void setType(GroupType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "start_time")
	@Temporal(TemporalType.DATE)
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Column(name = "end_time")
	@Temporal(TemporalType.DATE)
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_annotation")
	public Annotations getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotations annotations) {
		this.annotations = annotations;
	}

    @OneToMany(mappedBy = "group")
	public Set<AudienceGroup> getGroupAudiences() {
		return groupAudiences;
	}

	public void setGroupAudiences(Set<AudienceGroup> groupAudiences) {
		this.groupAudiences = groupAudiences;
	}

    @OneToMany(mappedBy = "group")
	public Set<GroupUser> getGroupMembers() {
		return groupMembers;
	}

	public void setGroupMembers(Set<GroupUser> groupMembers) {
		this.groupMembers = groupMembers;
	}
}
