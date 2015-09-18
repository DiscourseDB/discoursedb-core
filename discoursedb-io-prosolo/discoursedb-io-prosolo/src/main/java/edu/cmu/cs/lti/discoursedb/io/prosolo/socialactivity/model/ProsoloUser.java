package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model;

/**
 * 
 * Wraps entities from the user table in prosolo. 
 * Comments are based on discussions with the prosolo developers.
 * 
 * @author Oliver Ferschke
 *
 */
public class ProsoloUser {
	private long id;
	private String created;
	private String deleted;
	private String dc_description;
	private String title;
	private String avatar_url;
	private String lastname;
	private Double latitude;	
	private String location_name;
	private Double longitude;
	private String name;
	private String password;
	private Integer password_length;
	private String position;
	private String profile_url;
	private String sytem;
	private String user_type;
	private String email;
	private String user_user_organization;
	
	
	
	public ProsoloUser(long id, String created, String deleted, String dc_description, String title, String avatar_url,
			String lastname, Double latitude, String location_name, Double longitude, String name, String password,
			Integer password_length, String position, String profile_url, String sytem, String user_type, String email,
			String user_user_organization) {
		super();
		this.id = id;
		this.created = created;
		this.deleted = deleted;
		this.dc_description = dc_description;
		this.title = title;
		this.avatar_url = avatar_url;
		this.lastname = lastname;
		this.latitude = latitude;
		this.location_name = location_name;
		this.longitude = longitude;
		this.name = name;
		this.password = password;
		this.password_length = password_length;
		this.position = position;
		this.profile_url = profile_url;
		this.sytem = sytem;
		this.user_type = user_type;
		this.email = email;
		this.user_user_organization = user_user_organization;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
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
	public String getAvatar_url() {
		return avatar_url;
	}
	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public String getLocation_name() {
		return location_name;
	}
	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getPassword_length() {
		return password_length;
	}
	public void setPassword_length(Integer password_length) {
		this.password_length = password_length;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getProfile_url() {
		return profile_url;
	}
	public void setProfile_url(String profile_url) {
		this.profile_url = profile_url;
	}
	public String getSytem() {
		return sytem;
	}
	public void setSytem(String sytem) {
		this.sytem = sytem;
	}
	public String getUser_type() {
		return user_type;
	}
	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUser_user_organization() {
		return user_user_organization;
	}
	public void setUser_user_organization(String user_user_organization) {
		this.user_user_organization = user_user_organization;
	}
}
