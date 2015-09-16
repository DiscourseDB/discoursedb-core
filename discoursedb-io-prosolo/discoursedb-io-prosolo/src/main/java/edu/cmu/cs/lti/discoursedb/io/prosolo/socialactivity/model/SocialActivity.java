package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Wraps entities from the social_activity table in prosolo. Comments are based
 * on discussions with the prosolo developers.
 * 
 * Currently, all fields String types but could be changed to more appropriate
 * types with the necessary datatype conversion.
 * 
 * @author Oliver Ferschke
 */
@JsonPropertyOrder({ "dtype", "id", "created", "deleted", "dc_description", "title", "action", "bookmark_count",
		"comments_disabled", "dislike_count", "last_action", "like_count", "share_count", "text", "visibility",
		"avatar_url", "name", "nickname", "post_link", "profile_url", "service_type", "user_type", "actor", "maker",
		"reason", "rich_content", "goal_target", "post_object", "user_target", "node_object", "user_object",
		"node_target", "node", "social_activity", "enrollment_object", "course_object", "course_enrollment_object" })
public class SocialActivity {

	/**
	 * Type of activity
	 */
	private String dtype;
	private String id;
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
	private String bookmark_count;
	private String comments_disabled;
	private String dislike_count;
	/**
	 * Date when the social activity was carried out
	 */
	private String last_action;
	private String like_count;
	private String share_count;
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
	private String service_type;
	/**
	 * Twitter user or Prosolo user
	 */
	private String user_type;
	/**
	 * Removed in new schema
	 */
	private String actor;
	/**
	 * The user who performed the action in this activity
	 */
	private String maker;
	/**
	 * Relation to another social activity
	 */
	private String reason;
	private String rich_content;
	/**
	 * Pointer to goal note activity for which this action has been performed
	 * Null for other types of activities.
	 */
	private String goal_target;
	/**
	 * Pointer to post activityfor which this action has been performed Null for
	 * other types of activities.
	 */
	private String post_object;
	/**
	 * Pointer to user activity for which this action has been performed. Null
	 * for other types of activities.
	 */
	private String user_target;
	private String node_object;
	private String user_object;
	/**
	 * Pointer to node activity for which this action has been performed. Null
	 * for other types of activities.
	 */
	private String node_target;
	private String node;
	/**
	 * Relation to another social activity
	 */
	private String social_activity;
	private String enrollment_object;
	private String course_object;
	private String course_enrollment_object;

	public String getDtype() {
		return dtype;
	}

	public void setDtype(String dtype) {
		this.dtype = dtype;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getBookmark_count() {
		return bookmark_count;
	}

	public void setBookmark_count(String bookmark_count) {
		this.bookmark_count = bookmark_count;
	}

	public String getComments_disabled() {
		return comments_disabled;
	}

	public void setComments_disabled(String comments_disabled) {
		this.comments_disabled = comments_disabled;
	}

	public String getDislike_count() {
		return dislike_count;
	}

	public void setDislike_count(String dislike_count) {
		this.dislike_count = dislike_count;
	}

	public String getLast_action() {
		return last_action;
	}

	public void setLast_action(String last_action) {
		this.last_action = last_action;
	}

	public String getLike_count() {
		return like_count;
	}

	public void setLike_count(String like_count) {
		this.like_count = like_count;
	}

	public String getShare_count() {
		return share_count;
	}

	public void setShare_count(String share_count) {
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

	public String getService_type() {
		return service_type;
	}

	public void setService_type(String service_type) {
		this.service_type = service_type;
	}

	public String getUser_type() {
		return user_type;
	}

	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getRich_content() {
		return rich_content;
	}

	public void setRich_content(String rich_content) {
		this.rich_content = rich_content;
	}

	public String getGoal_target() {
		return goal_target;
	}

	public void setGoal_target(String goal_target) {
		this.goal_target = goal_target;
	}

	public String getPost_object() {
		return post_object;
	}

	public void setPost_object(String post_object) {
		this.post_object = post_object;
	}

	public String getUser_target() {
		return user_target;
	}

	public void setUser_target(String user_target) {
		this.user_target = user_target;
	}

	public String getNode_object() {
		return node_object;
	}

	public void setNode_object(String node_object) {
		this.node_object = node_object;
	}

	public String getUser_object() {
		return user_object;
	}

	public void setUser_object(String user_object) {
		this.user_object = user_object;
	}

	public String getNode_target() {
		return node_target;
	}

	public void setNode_target(String node_target) {
		this.node_target = node_target;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getSocial_activity() {
		return social_activity;
	}

	public void setSocial_activity(String social_activity) {
		this.social_activity = social_activity;
	}

	public String getEnrollment_object() {
		return enrollment_object;
	}

	public void setEnrollment_object(String enrollment_object) {
		this.enrollment_object = enrollment_object;
	}

	public String getCourse_object() {
		return course_object;
	}

	public void setCourse_object(String course_object) {
		this.course_object = course_object;
	}

	public String getCourse_enrollment_object() {
		return course_enrollment_object;
	}

	public void setCourse_enrollment_object(String course_enrollment_object) {
		this.course_enrollment_object = course_enrollment_object;
	}

}
