package edu.cmu.cs.lti.discoursedb.io.tags.model;
/**
 * POJO for the tweet info in DALMOOC tweets 11-5-2014.csv file.
 * 
 * @author haitiang
 *
 */
public class TweetInfo {
	
	//Variables
	
	private String id_str;
	private String from_user;
	private String text;
	private String created_at;
	private String time;
	private String geo_coordinates;
	private String user_lang;
	private String in_reply_to_user;
	private String in_reply_to_screen_name;
	private String from_user_id_str;
	private String in_reply_to_user_id_str;
	private String in_reply_to_status_id_str;
	private String source;
	private String profile_image_url;
	private String user_followers_count;
	private String user_friends_count;
	private String user_utc_offset;
	private String status_url;
	private String entities_str;
	
	//Methods
	
	public String getId_str() {
		return id_str;
	}
	public void setId_str(String id_str) {
		this.id_str = id_str;
	}
	public String getFrom_user() {
		return from_user;
	}
	public void setFrom_user(String from_user) {
		this.from_user = from_user;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getGeo_coordinates() {
		return geo_coordinates;
	}
	public void setGeo_coordinates(String geo_coordinates) {
		this.geo_coordinates = geo_coordinates;
	}
	public String getUser_lang() {
		return user_lang;
	}
	public void setUser_lang(String user_lang) {
		this.user_lang = user_lang;
	}
	public String getIn_reply_to_user() {
		return in_reply_to_user;
	}
	public void setIn_reply_to_user(String in_reply_to_user) {
		this.in_reply_to_user = in_reply_to_user;
	}
	public String getIn_reply_to_screen_name() {
		return in_reply_to_screen_name;
	}
	public void setIn_reply_to_screen_name(String in_reply_to_screen_name) {
		this.in_reply_to_screen_name = in_reply_to_screen_name;
	}
	public String getFrom_user_id_str() {
		return from_user_id_str;
	}
	public void setFrom_user_id_str(String from_user_id_str) {
		this.from_user_id_str = from_user_id_str;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getProfile_image_url() {
		return profile_image_url;
	}
	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}
	public String getUser_followers_count() {
		return user_followers_count;
	}
	public void setUser_followers_count(String user_followers_count) {
		this.user_followers_count = user_followers_count;
	}
	public String getUser_friends_count() {
		return user_friends_count;
	}
	public void setUser_friends_count(String user_friends_count) {
		this.user_friends_count = user_friends_count;
	}
	public String getUser_utc_offset() {
		return user_utc_offset;
	}
	public void setUser_utc_offset(String user_utc_offset) {
		this.user_utc_offset = user_utc_offset;
	}
	public String getStatus_url() {
		return status_url;
	}
	public void setStatus_url(String status_url) {
		this.status_url = status_url;
	}
	public String getEntities_str() {
		return entities_str;
	}
	public void setEntities_str(String entities_str) {
		this.entities_str = entities_str;
	}
	public String getIn_reply_to_user_id_str() {
		return in_reply_to_user_id_str;
	}
	public void setIn_reply_to_user_id_str(String in_reply_to_user_id_str) {
		this.in_reply_to_user_id_str = in_reply_to_user_id_str;
	}
	public String getIn_reply_to_status_id_str() {
		return in_reply_to_status_id_str;
	}
	public void setIn_reply_to_status_id_str(String in_reply_to_status_id_str) {
		this.in_reply_to_status_id_str = in_reply_to_status_id_str;
	}
	
	
}
