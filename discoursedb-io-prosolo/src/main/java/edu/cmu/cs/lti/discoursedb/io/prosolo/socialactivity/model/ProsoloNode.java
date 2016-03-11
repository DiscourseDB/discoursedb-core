package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model;

import java.util.Date;

/**
 * Wraps entities form the node table in prosolo
 * 
 * @author Oliver Ferschke
 *
 */
public class ProsoloNode {
	private String dtype;	
	private Long id;
	private Date created;
	private Boolean deleted;
	private String dc_description;
	private String title;
	private String visibility;
	private Date deadline;
	private Boolean free_to_join;
	private Boolean archived;
	private Date completed_date;
	private Integer progress;
	private Boolean progress_activity_dependent;
	private Integer duration;
	private String type;
	private Integer validity_period;
	private Boolean compl;
	private Date completed_day;
	private Date date_finished;
	private Date date_started;
	private String assignment_link;
	private String assignment_title;
	private Boolean completed;
	private Date date_completed;
	private Long ta_position;
	private Boolean mandatory;
	private Integer max_files_number;
	private Boolean visible_to_everyone;
	private Long maker;
	private Long course_enrollment;
	private Long learning_goal;
	private Long competence;
	private Long parent_goal;
	private Long activity;
	private Long parent_competence;
	private Long rich_content;

	public ProsoloNode(String dtype, Long id, Date created, Boolean deleted, String dc_description, String title,
			String visibility, Date deadline, Boolean free_to_join, Boolean archived, Date completed_date,
			Integer progress, Boolean progress_activity_dependent, Integer duration, String type,
			Integer validity_period, Boolean compl, Date completed_day, Date date_finished, Date date_started,
			String assignment_link, String assignment_title, Boolean completed, Date date_completed, Long ta_position,
			Boolean mandatory, Integer max_files_number, Boolean visible_to_everyone, Long maker,
			Long course_enrollment, Long learning_goal, Long competence, Long parent_goal, Long activity,
			Long parent_competence, Long rich_content) {
		super();
		this.dtype = dtype;
		this.id = id;
		this.created = created;
		this.deleted = deleted;
		this.dc_description = dc_description;
		this.title = title;
		this.visibility = visibility;
		this.deadline = deadline;
		this.free_to_join = free_to_join;
		this.archived = archived;
		this.completed_date = completed_date;
		this.progress = progress;
		this.progress_activity_dependent = progress_activity_dependent;
		this.duration = duration;
		this.type = type;
		this.validity_period = validity_period;
		this.compl = compl;
		this.completed_day = completed_day;
		this.date_finished = date_finished;
		this.date_started = date_started;
		this.assignment_link = assignment_link;
		this.assignment_title = assignment_title;
		this.completed = completed;
		this.date_completed = date_completed;
		this.ta_position = ta_position;
		this.mandatory = mandatory;
		this.max_files_number = max_files_number;
		this.visible_to_everyone = visible_to_everyone;
		this.maker = maker;
		this.course_enrollment = course_enrollment;
		this.learning_goal = learning_goal;
		this.competence = competence;
		this.parent_goal = parent_goal;
		this.activity = activity;
		this.parent_competence = parent_competence;
		this.rich_content = rich_content;
	}

	public String getDtype() {
		return dtype;
	}

	public void setDtype(String dtype) {
		this.dtype = dtype;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getDc_description() {
		return dc_description;
	}

	public void setDc_description(String dc_description) {
		this.dc_description = dc_description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Boolean getFree_to_join() {
		return free_to_join;
	}

	public void setFree_to_join(Boolean free_to_join) {
		this.free_to_join = free_to_join;
	}

	public Boolean getArchived() {
		return archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}

	public Date getCompleted_date() {
		return completed_date;
	}

	public void setCompleted_date(Date completed_date) {
		this.completed_date = completed_date;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	public Boolean getProgress_activity_dependent() {
		return progress_activity_dependent;
	}

	public void setProgress_activity_dependent(Boolean progress_activity_dependent) {
		this.progress_activity_dependent = progress_activity_dependent;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getValidity_period() {
		return validity_period;
	}

	public void setValidity_period(Integer validity_period) {
		this.validity_period = validity_period;
	}

	public Boolean getCompl() {
		return compl;
	}

	public void setCompl(Boolean compl) {
		this.compl = compl;
	}

	public Date getCompleted_day() {
		return completed_day;
	}

	public void setCompleted_day(Date completed_day) {
		this.completed_day = completed_day;
	}

	public Date getDate_finished() {
		return date_finished;
	}

	public void setDate_finished(Date date_finished) {
		this.date_finished = date_finished;
	}

	public Date getDate_started() {
		return date_started;
	}

	public void setDate_started(Date date_started) {
		this.date_started = date_started;
	}

	public String getAssignment_link() {
		return assignment_link;
	}

	public void setAssignment_link(String assignment_link) {
		this.assignment_link = assignment_link;
	}

	public String getAssignment_title() {
		return assignment_title;
	}

	public void setAssignment_title(String assignment_title) {
		this.assignment_title = assignment_title;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public Date getDate_completed() {
		return date_completed;
	}

	public void setDate_completed(Date date_completed) {
		this.date_completed = date_completed;
	}

	public Long getTa_position() {
		return ta_position;
	}

	public void setTa_position(Long ta_position) {
		this.ta_position = ta_position;
	}

	public Boolean getMandatory() {
		return mandatory;
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}

	public Integer getMax_files_number() {
		return max_files_number;
	}

	public void setMax_files_number(Integer max_files_number) {
		this.max_files_number = max_files_number;
	}

	public Boolean getVisible_to_everyone() {
		return visible_to_everyone;
	}

	public void setVisible_to_everyone(Boolean visible_to_everyone) {
		this.visible_to_everyone = visible_to_everyone;
	}

	public Long getMaker() {
		return maker;
	}

	public void setMaker(Long maker) {
		this.maker = maker;
	}

	public Long getCourse_enrollment() {
		return course_enrollment;
	}

	public void setCourse_enrollment(Long course_enrollment) {
		this.course_enrollment = course_enrollment;
	}

	public Long getLearning_goal() {
		return learning_goal;
	}

	public void setLearning_goal(Long learning_goal) {
		this.learning_goal = learning_goal;
	}

	public Long getCompetence() {
		return competence;
	}

	public void setCompetence(Long competence) {
		this.competence = competence;
	}

	public Long getParent_goal() {
		return parent_goal;
	}

	public void setParent_goal(Long parent_goal) {
		this.parent_goal = parent_goal;
	}

	public Long getActivity() {
		return activity;
	}

	public void setActivity(Long activity) {
		this.activity = activity;
	}

	public Long getParent_competence() {
		return parent_competence;
	}

	public void setParent_competence(Long parent_competence) {
		this.parent_competence = parent_competence;
	}

	public Long getRich_content() {
		return rich_content;
	}

	public void setRich_content(Long rich_content) {
		this.rich_content = rich_content;
	}
	
}
