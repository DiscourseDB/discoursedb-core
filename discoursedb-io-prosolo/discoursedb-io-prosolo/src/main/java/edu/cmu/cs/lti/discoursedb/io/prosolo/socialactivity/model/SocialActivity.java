package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Wraps entities from the social_activity table in prosolo. 
 * Comments are based on discussions with the prosolo developers.
 * 
 * @author Oliver Ferschke
 */
@JsonPropertyOrder({ "dtype", "id", "created", "deleted", "dc_description", "title", "action", "bookmark_count",
		"comments_disabled", "dislike_count", "last_action", "like_count", "share_count", "text", "visibility",
		"avatar_url", "name", "nickname", "post_link", "profile_url", "service_type", "user_type", "actor", "maker",
		"reason", "rich_content", "goal_target", "post_object", "user_target", "node_object", "user_object",
		"node_target", "node", "social_activity", "enrollment_object", "course_object", "course_enrollment_object" })
public class SocialActivity {

	

	public SocialActivity(String dtype, Long id, String created, String deleted, String dc_description, String title,
			String action, Long bookmark_count, String comments_disabled, Integer dislike_count, String last_action,
			Integer like_count, Integer share_count, String text, String visibility, String avatar_url, String name,
			String nickname, String post_link, String profile_url, Integer service_type, Integer user_type, Long actor,
			Long maker, Long reason, Long rich_content, Long goal_target, Long post_object, Long user_target,
			Long node_object, Long user_object, Long node_target, Long node, Long social_activity,
			Long enrollment_object, Long course_object, Long course_enrollment_object) {
		super();
		this.dtype = dtype;
		this.id = id;
		this.created = created;
		this.deleted = deleted;
		this.dc_description = dc_description;
		this.title = title;
		this.action = action;
		this.bookmark_count = bookmark_count;
		this.comments_disabled = comments_disabled;
		this.dislike_count = dislike_count;
		this.last_action = last_action;
		this.like_count = like_count;
		this.share_count = share_count;
		this.text = text;
		this.visibility = visibility;
		this.avatar_url = avatar_url;
		this.name = name;
		this.nickname = nickname;
		this.post_link = post_link;
		this.profile_url = profile_url;
		this.service_type = service_type;
		this.user_type = user_type;
		this.actor = actor;
		this.maker = maker;
		this.reason = reason;
		this.rich_content = rich_content;
		this.goal_target = goal_target;
		this.post_object = post_object;
		this.user_target = user_target;
		this.node_object = node_object;
		this.user_object = user_object;
		this.node_target = node_target;
		this.node = node;
		this.social_activity = social_activity;
		this.enrollment_object = enrollment_object;
		this.course_object = course_object;
		this.course_enrollment_object = course_enrollment_object;
	}
	/**
	 * Type of activity
	 */
	private String dtype;
	private Long id;
	private String created;
	/**
	 * Whether activity has been deleted or not
	 */
	private String deleted;
	/**
	 * not used
	 */
	private String dc_description;
	private String title;
	/**
	 * Type of action performed in an activity, e.g. liking, creating, deleting
	 * etc.
	 */
	private String action;
	private Long bookmark_count;
	private String comments_disabled;
	private Integer dislike_count;
	/**
	 * Date when the social activity was carried out
	 */
	private String last_action;
	private Integer like_count;
	private Integer share_count;
	/**
	 * Textual content of the activity
	 */
	private String text;
	private String visibility;
	/**
	 * Related to TwitterPostSocialActivity. Null for all other activities.
	 */
	private String avatar_url;
	/**
	 * Related to TwitterPostSocialActivity. Null for all other activities.
	 */
	private String name;
	/**
	 * Related to TwitterPostSocialActivity. Null for all other activities.
	 */
	private String nickname;
	/**
	 * Related to TwitterPostSocialActivity. Null for all other activities.
	 */
	private String post_link;
	/**
	 * Related to TwitterPostSocialActivity. Null for all other activities.
	 */
	private String profile_url;
	/**
	 * Related to TwitterPostSocialActivity. Null for all other activities.
	 */
	private Integer service_type;
	/**
	 * Twitter user or Prosolo user
	 */
	private Integer user_type;
	/**
	 * Removed in new schema
	 */
	private Long actor;
	/**
	 * The user who performed the action in this activity
	 */
	private Long maker;
	/**
	 * Relation to another social activity
	 */
	private Long reason;
	private Long rich_content;
	/**
	 * Pointer to goal note activity for which this action has been performed
	 * Null for other types of activities.
	 */
	private Long goal_target;
	/**
	 * Pointer to post activityfor which this action has been performed Null for
	 * other types of activities.
	 */
	private Long post_object;
	/**
	 * Pointer to user activity for which this action has been performed. Null
	 * for other types of activities.
	 */
	private Long user_target;
	private Long node_object;
	private Long user_object;
	/**
	 * Pointer to node activity for which this action has been performed. Null
	 * for other types of activities.
	 */
	private Long node_target;
	private Long node;
	/**
	 * Relation to another social activity
	 */
	private Long social_activity;
	private Long enrollment_object;
	private Long course_object;
	private Long course_enrollment_object;
	
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
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
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
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Long getBookmark_count() {
		return bookmark_count;
	}
	public void setBookmark_count(Long bookmark_count) {
		this.bookmark_count = bookmark_count;
	}
	public String getComments_disabled() {
		return comments_disabled;
	}
	public void setComments_disabled(String comments_disabled) {
		this.comments_disabled = comments_disabled;
	}
	public Integer getDislike_count() {
		return dislike_count;
	}
	public void setDislike_count(Integer dislike_count) {
		this.dislike_count = dislike_count;
	}
	public String getLast_action() {
		return last_action;
	}
	public void setLast_action(String last_action) {
		this.last_action = last_action;
	}
	public Integer getLike_count() {
		return like_count;
	}
	public void setLike_count(Integer like_count) {
		this.like_count = like_count;
	}
	public Integer getShare_count() {
		return share_count;
	}
	public void setShare_count(Integer share_count) {
		this.share_count = share_count;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	public String getAvatar_url() {
		return avatar_url;
	}
	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPost_link() {
		return post_link;
	}
	public void setPost_link(String post_link) {
		this.post_link = post_link;
	}
	public String getProfile_url() {
		return profile_url;
	}
	public void setProfile_url(String profile_url) {
		this.profile_url = profile_url;
	}
	public Integer getService_type() {
		return service_type;
	}
	public void setService_type(Integer service_type) {
		this.service_type = service_type;
	}
	public Integer getUser_type() {
		return user_type;
	}
	public void setUser_type(Integer user_type) {
		this.user_type = user_type;
	}
	public Long getActor() {
		return actor;
	}
	public void setActor(Long actor) {
		this.actor = actor;
	}
	public Long getMaker() {
		return maker;
	}
	public void setMaker(Long maker) {
		this.maker = maker;
	}
	public Long getReason() {
		return reason;
	}
	public void setReason(Long reason) {
		this.reason = reason;
	}
	public Long getRich_content() {
		return rich_content;
	}
	public void setRich_content(Long rich_content) {
		this.rich_content = rich_content;
	}
	public Long getGoal_target() {
		return goal_target;
	}
	public void setGoal_target(Long goal_target) {
		this.goal_target = goal_target;
	}
	public Long getPost_object() {
		return post_object;
	}
	public void setPost_object(Long post_object) {
		this.post_object = post_object;
	}
	public Long getUser_target() {
		return user_target;
	}
	public void setUser_target(Long user_target) {
		this.user_target = user_target;
	}
	public Long getNode_object() {
		return node_object;
	}
	public void setNode_object(Long node_object) {
		this.node_object = node_object;
	}
	public Long getUser_object() {
		return user_object;
	}
	public void setUser_object(Long user_object) {
		this.user_object = user_object;
	}
	public Long getNode_target() {
		return node_target;
	}
	public void setNode_target(Long node_target) {
		this.node_target = node_target;
	}
	public Long getNode() {
		return node;
	}
	public void setNode(Long node) {
		this.node = node;
	}
	public Long getSocial_activity() {
		return social_activity;
	}
	public void setSocial_activity(Long social_activity) {
		this.social_activity = social_activity;
	}
	public Long getEnrollment_object() {
		return enrollment_object;
	}
	public void setEnrollment_object(Long enrollment_object) {
		this.enrollment_object = enrollment_object;
	}
	public Long getCourse_object() {
		return course_object;
	}
	public void setCourse_object(Long course_object) {
		this.course_object = course_object;
	}
	public Long getCourse_enrollment_object() {
		return course_enrollment_object;
	}
	public void setCourse_enrollment_object(Long course_enrollment_object) {
		this.course_enrollment_object = course_enrollment_object;
	}


}
