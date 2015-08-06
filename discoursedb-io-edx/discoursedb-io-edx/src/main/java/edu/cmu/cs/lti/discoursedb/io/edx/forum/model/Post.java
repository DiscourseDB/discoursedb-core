package edu.cmu.cs.lti.discoursedb.io.edx.forum.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object model of an edX forum post. Field descriptions according to @see
 * <a href=
 * "https://edx.readthedocs.org/en/latest/internal_data_formats/discussion_data.html">
 * the edX Research Guide</a>
 * 
 * @author Oliver Ferschke
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {

	/**
	 * The 12-byte MongoDB unique ID for this collection. Like all MongoDB IDs,
	 * the IDs are monotonically increasing and the first four bytes are a
	 * timestamp.
	 */
	private String id;

	/**
	 * CommentThread or Comment depending on the type of object.
	 */
	@JsonProperty("_type")
	private String type; // TODO convert to enum

	/**
	 * If true, this CommentThread or Comment displays in the user interface as
	 * written by “anonymous”, even to those who have course staff or discussion
	 * administration roles in the course.
	 */
	private boolean anonymous;

	/**
	 * If true, this CommentThread or Comment displays in the user interface as
	 * written by “anonymous” to students, but course staff and discussion
	 * administrators see the author’s username.
	 */
	@JsonProperty("anonymous_to_peers")
	private boolean anonymousToPeers;

	/**
	 * Identifies the user who wrote this. Corresponds to the user IDs stored in
	 * the MySQL database as auth_user.id.
	 */
	@JsonProperty("author_id")
	private String authorId;

	/**
	 * The username of the person who wrote the discussion post or comment.
	 */
	@JsonProperty("author_username")
	private String authorUsername;

	/**
	 * Text of the comment in Markdown. UTF-8 encoded.
	 */
	private String body;

	/**
	 * The full course_id of the course that this comment was made in, including
	 * org and run. This value can be seen in the URL when browsing the
	 * courseware section. Example: BerkeleyX/Stat2.1x/2013_Spring.
	 */
	@JsonProperty("course_id")
	private String courseId;

	/**
	 * Timestamp in UTC. Example: ISODate("2013-02-21T03:03:04.587Z").
	 */
	private Date createdAt;

	/**
	 * Timestamp in UTC. Example: ISODate("2013-02-21T03:03:04.587Z").
	 */
	private Date updatedAt;

	/*
	 * Both CommentThread and Comment objects support voting. In the user
	 * interface, students can vote for posts (CommentThread objects) and for
	 * responses, but not for the third-level comments made on responses. All
	 * Comment objects still have this attribute, even though there is no way to
	 * actually vote on the comment-level items in the UI. This attribute is a
	 * dictionary that has the following items inside:
	 */

	/**
	 * list of User IDs that up-voted this comment or thread.
	 */
	@JsonProperty("up")
	private List<String> upvotes;

	/**
	 * total upvotes received.
	 */
	@JsonProperty("up_count")
	private int upvoteCount;

	/**
	 * total votes cast.
	 */
	@JsonProperty("count")
	private int voteCount;

	/*
	 * The following fields are specific to CommentThread objects. Each thread
	 * in the discussion forums is represented by one CommentThread.
	 */

	/**
	 * If true, this thread was closed by a discussion forum moderator or admin.
	 */
	private boolean closed;

	/**
	 * The number of comment replies in this thread. This includes all responses
	 * and replies, but does not include the original post that started the
	 * thread.
	 */
	@JsonProperty("comment_count")
	private int commentCount;

	/**
	 * A course team can attach a discussion to any piece of content in the
	 * course, or to top level categories like “General” and “Troubleshooting”.
	 * When the discussion is a top level category it is specified in the
	 * course’s policy file, and the commentable_id is formatted like this:
	 * “i4x-edX-edX101-course-How_to_Create_an_edX_Course”. When the discussion
	 * is a specific component in the course, the commentable_id identifies that
	 * component: “d9f970a42067413cbb633f81cfb12604”.
	 */
	@JsonProperty("commentable_id")
	private String commentableId;

	/**
	 * Timestamp in UTC indicating the last time there was activity in the
	 * thread (new posts, edits, etc). Closing the thread does not affect the
	 * value in this field.
	 */
	private Date lastActivityAt;

	/**
	 * Title of the thread. UTF-8 string.
	 */
	private String title;

	/**
	 * Identifies the type of post as a “question” or “discussion”.
	 */
	@JsonProperty("thread_type")
	private String threadType; // TODO convert to enum

	/*
	 * The following fields are specific to Comment objects. A Comment is either
	 * a response to a CommentThread (such as an answer to the question), or a
	 * reply to another Comment (a comment about somebody’s answer).
	 */

	/**
	 * Records the user id of each user who selects the Report Misuse flag for a
	 * Comment in the user interface. Stores an array of user ids if more than
	 * one user flags the Comment. This is empty if no users flag the Comment.
	 */
	@JsonProperty("abuse_flaggers")
	private List<String> abuseFlaggers;

	/**
	 * If a discussion moderator removes the Report Misuse flag from a Comment,
	 * all user IDs are removed from the abuse_flaggers field and then written
	 * to this field.
	 */
	@JsonProperty("historical_abuse_flaggers")
	private List<String> historicalAbuseFlaggers;

	/**
	 * Boolean value. True if a forum moderator has marked this response to a
	 * CommentThread with a thread_type of “discussion” as a valuable
	 * contribution, or if a forum moderator or the originator of a
	 * CommentThread with a thread_type of “question” has marked this response
	 * as the correct answer.
	 * 
	 * The endorsed field is present for comments that are made as replies to
	 * responses, but in these cases the value is always false: the user
	 * interface does not offer a way to endorse comments.
	 */
	private boolean endorsed;

	/**
	 * Contains time for the date and time that this response to a post was
	 * endorsed.
	 */
	@JsonIgnore
	private Date endorsementTime;

	/**
	 * Contains the numeric user ID (from auth_user.id) of the person who
	 * endorsed it.
	 */
	@JsonIgnore
	private String endorsementUserId;

	/**
	 * Identifies the CommentThread that the Comment is a part of.
	 */
	private String commentThreadId;

	/**
	 * Applies only to comments made to a response. In the example given for
	 * comment_count above, “A Loco Moco? Only if you want a heart attack!” is a
	 * comment that was made to the response, “Try a Loco Moco, it’s amazing!”
	 * 
	 * The parent_id is the _id of the response-level Comment that this Comment
	 * is a reply to. Note that this field is only present in a Comment that is
	 * a reply to another Comment; it does not appear in a Comment that is a
	 * reply to a CommentThread.
	 */
	private String parentId;

	/**
	 * The parent_ids field appears in all Comment objects, and contains the _id
	 * of all ancestor comments. Since the UI now prevents comments from being
	 * nested more than one layer deep, it will only ever have at most one
	 * element in it. If a Comment has no parent, it is an empty list.
	 */
	@JsonIgnore // TODO need to map the $oids to the list
	private List<String> parent_ids;

	/**
	 * A randomly generated number that drives a sorted index to improve online
	 * performance.
	 */
	private String sk;

	public String getId() {
		return id;
	}

	@JsonProperty("_id")
	public void setId(Map<String, Object> id) {
		this.id = (String) id.get("$oid");
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isAnonymous() {
		return anonymous;
	}
	
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@JsonProperty("created_at")
	public void setCreatedAt(Map<String, Object> created_at) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis((Long) created_at.get("$date"));
		this.createdAt = calendar.getTime();
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	@JsonProperty("updated_at")
	public void setUpdatedAt(Map<String, Object> updated_at) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis((Long) updated_at.get("$date"));
		this.updatedAt = calendar.getTime();
	}

	public List<String> getUpvotes() {
		return upvotes;
	}

	public void setUpvotes(List<String> upvotes) {
		this.upvotes = upvotes;
	}


	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isEndorsed() {
		return endorsed;
	}

	public void setEndorsed(boolean endorsed) {
		this.endorsed = endorsed;
	}

	public String getCommentThreadId() {
		return commentThreadId;
	}

	@JsonProperty("comment_thread_id")
	public void setCommentThreadId(Map<String, Object> comment_thread_id) {
		this.commentThreadId = (String) comment_thread_id.get("$oid");
	}

	public String getParentId() {
		return parentId;
	}

	@JsonProperty("parent_id")
	public void setParentId(Map<String, Object> parent_id) {
		this.parentId = (String) parent_id.get("$oid");
	}

	public String getSk() {
		return sk;
	}

	public void setSk(String sk) {
		this.sk = sk;
	}

	public boolean isAnonymousToPeers() {
		return anonymousToPeers;
	}

	public void setAnonymousToPeers(boolean anonymousToPeers) {
		this.anonymousToPeers = anonymousToPeers;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public String getAuthorUsername() {
		return authorUsername;
	}

	public void setAuthorUsername(String authorUsername) {
		this.authorUsername = authorUsername;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public int getUpvoteCount() {
		return upvoteCount;
	}

	public void setUpvoteCount(int upvoteCount) {
		this.upvoteCount = upvoteCount;
	}

	public int getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public String getCommentableId() {
		return commentableId;
	}

	public void setCommentableId(String commentableId) {
		this.commentableId = commentableId;
	}

	public Date getLastActivityAt() {
		return lastActivityAt;
	}

	@JsonProperty("last_activity_at")
	public void setLastActivityAt(Map<String, Object> lastActivityAt) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis((Long) lastActivityAt.get("$date"));
		this.lastActivityAt = calendar.getTime();
	}

	public String getThreadType() {
		return threadType;
	}

	public void setThreadType(String threadType) {
		this.threadType = threadType;
	}

	public List<String> getAbuseFlaggers() {
		return abuseFlaggers;
	}

	public void setAbuseFlaggers(List<String> abuseFlaggers) {
		this.abuseFlaggers = abuseFlaggers;
	}

	public List<String> getHistoricalAbuseFlaggers() {
		return historicalAbuseFlaggers;
	}

	public void setHistoricalAbuseFlaggers(List<String> historicalAbuseFlaggers) {
		this.historicalAbuseFlaggers = historicalAbuseFlaggers;
	}

	
	
	public Date getEndorsementTime() {
		return endorsementTime;
	}

	public String getEndorsementUserId() {
		return endorsementUserId;
	}

	@JsonProperty("endorsement")
	public void setEndorsements(Map<String, Object> endorsements) {
		if(endorsements!=null){
			String userId = (String) endorsements.get("user_id");
			this.endorsementUserId = userId;

			Calendar calendar = Calendar.getInstance();
			Map<String, Object> timeEntry =  (Map<String, Object>)endorsements.get("time");
			Long timeInMillis = (Long)timeEntry.get("$date");
			calendar.setTimeInMillis(timeInMillis);
			this.endorsementTime = calendar.getTime();			
		}
	}

	/**
	 * TODO: not yet fully implemented
	 * 
	 * @return a String represenatio of this entity
	 */
	public String toString(){
		String nl = System.getProperty("line.separator");
		StringBuilder tString = new StringBuilder();
		tString.append("ID: ");
		tString.append(getId());
		tString.append(nl);
		tString.append("Type: ");
		tString.append(getType());
		tString.append(nl);
		tString.append("Author: ");
		tString.append(getAuthorUsername());
		tString.append(nl);
		tString.append("User Id: ");
		tString.append(getAuthorId());
		tString.append(nl);
		tString.append("Body: ");
		tString.append(getBody().trim());
		tString.append(nl);
		tString.append("Created At: ");
		tString.append(getCreatedAt());
		tString.append(nl);
		tString.append("Updated At: ");
		tString.append(getUpdatedAt());
		tString.append(nl);
		tString.append("Endorsed: ");
		tString.append(isEndorsed());
		tString.append(nl);
		tString.append("Course ID: ");
		tString.append(getCourseId());
		tString.append(nl);		
		if(isEndorsed()){
			tString.append("Endorsed by: ");
			tString.append(getEndorsementUserId());
			tString.append(nl);		
			tString.append("Endorsed at: ");
			tString.append(getEndorsementTime());
			tString.append(nl);					
		}
		return tString.toString();
	}
	

}
