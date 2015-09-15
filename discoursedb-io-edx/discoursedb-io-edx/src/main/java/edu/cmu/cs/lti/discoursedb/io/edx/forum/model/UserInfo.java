package edu.cmu.cs.lti.discoursedb.io.edx.forum.model;

import java.util.Date;

/**
 * POJO for the user info in *-auth_user-prod-analytics.sql files.
 * 
 * @author oliverf
 *
 */
public class UserInfo {
	private long id;      
	private String username;        
	private String first_name;      
	private String last_name;       
	private String email;
	private String password;        
	private boolean is_staff;        
	private boolean is_active;       
	private boolean is_superuser;    
	private Date last_login;      
	private Date date_joined;     
	private String status;  
	private String email_key;       
	private String avatar_type;     
	private String country; 
	private boolean show_country;    
	private Date date_of_birth;   
	private String interesting_tags;        
	private String ignored_tags;    
	private String email_tag_filter_strategy;       
	private boolean display_tag_filter_strategy;     
	private int consecutive_days_visit_count;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isIs_staff() {
		return is_staff;
	}
	public void setIs_staff(boolean is_staff) {
		this.is_staff = is_staff;
	}
	public boolean isIs_active() {
		return is_active;
	}
	public void setIs_active(boolean is_active) {
		this.is_active = is_active;
	}
	public boolean isIs_superuser() {
		return is_superuser;
	}
	public void setIs_superuser(boolean is_superuser) {
		this.is_superuser = is_superuser;
	}
	public Date getLast_login() {
		return last_login;
	}
	public void setLast_login(Date last_login) {
		this.last_login = last_login;
	}
	public Date getDate_joined() {
		return date_joined;
	}
	public void setDate_joined(Date date_joined) {
		this.date_joined = date_joined;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getEmail_key() {
		return email_key;
	}
	public void setEmail_key(String email_key) {
		this.email_key = email_key;
	}
	public String getAvatar_type() {
		return avatar_type;
	}
	public void setAvatar_type(String avatar_type) {
		this.avatar_type = avatar_type;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public boolean isShow_country() {
		return show_country;
	}
	public void setShow_country(boolean show_country) {
		this.show_country = show_country;
	}
	public Date getDate_of_birth() {
		return date_of_birth;
	}
	public void setDate_of_birth(Date date_of_birth) {
		this.date_of_birth = date_of_birth;
	}
	public String getInteresting_tags() {
		return interesting_tags;
	}
	public void setInteresting_tags(String interesting_tags) {
		this.interesting_tags = interesting_tags;
	}
	public String getIgnored_tags() {
		return ignored_tags;
	}
	public void setIgnored_tags(String ignored_tags) {
		this.ignored_tags = ignored_tags;
	}
	public String getEmail_tag_filter_strategy() {
		return email_tag_filter_strategy;
	}
	public void setEmail_tag_filter_strategy(String email_tag_filter_strategy) {
		this.email_tag_filter_strategy = email_tag_filter_strategy;
	}
	public boolean isDisplay_tag_filter_strategy() {
		return display_tag_filter_strategy;
	}
	public void setDisplay_tag_filter_strategy(boolean display_tag_filter_strategy) {
		this.display_tag_filter_strategy = display_tag_filter_strategy;
	}
	public int getConsecutive_days_visit_count() {
		return consecutive_days_visit_count;
	}
	public void setConsecutive_days_visit_count(int consecutive_days_visit_count) {
		this.consecutive_days_visit_count = consecutive_days_visit_count;
	}
	
	

}
