package edu.cmu.cs.lti.discoursedb.io.edx.forum.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.extern.log4j.Log4j;

/**
 * Object model of an edX forum post. Field descriptions according to @see
 * <a href=
 * "https://edx.readthedocs.org/en/latest/internal_data_formats/discussion_data.html">date
 * the edX Research Guide</a>
 * 
 * @author Oliver Ferschke
 *
 */
@Log4j
@Data
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
	private String type;

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
	private List<String> upvotes;

	/**
	 * total upvotes received.
	 */
	private int upvoteCount;

	/**
	 * total votes cast.
	 */
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
	private String threadType;

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
	private List<String> parentIds;

	
	/*
	 * Below are non-trivial getter/setter implementation.
	 * Trivial implementations (setting and returning a fild) are auto-generated by Lombok
	 */
	
	@JsonProperty("_id")
	public void setId(Map<String, Object> id) {
		this.id = (String) id.get("$oid");
	}
	
	@JsonProperty("created_at")
	public void setCreatedAt(Map<String, Object> created_at) {
		Calendar calendar = Calendar.getInstance();
		String dateString = (String)created_at.get("$date");
		if(dateString.length()==13){
			calendar.setTimeInMillis(Long.parseLong(dateString));
			this.createdAt = calendar.getTime();			
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			try{
				this.createdAt=sdf.parse(dateString);				
			}catch(ParseException e){
				log.warn("Error parsing date "+dateString,e);
			}
		}

	}

	@JsonProperty("updated_at")
	public void setUpdatedAt(Map<String, Object> updated_at) {
		Calendar calendar = Calendar.getInstance();
		String dateString = (String)updated_at.get("$date");
		if(dateString.length()==13){
			calendar.setTimeInMillis(Long.parseLong(dateString));
			this.updatedAt = calendar.getTime();			
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			try{
				this.updatedAt=sdf.parse(dateString);				
			}catch(ParseException e){
				log.warn("Error parsing date "+dateString,e);
			}
		}
		
	}

	@JsonProperty("comment_thread_id")
	public void setCommentThreadId(Map<String, Object> comment_thread_id) {
		this.commentThreadId = (String) comment_thread_id.get("$oid");
	}

	@SuppressWarnings("unchecked")
	@JsonProperty("parent_ids")
	public void setParentIds(List<Map<String, Object>> parentIds) {
		List<String> result = new ArrayList<>();
		for(Object curIdEntity:parentIds){
			String curId = (String)((Map<String, Object>)curIdEntity).get("$oid");
			result.add(curId);
		}
		this.parentIds = result;
	}

	@SuppressWarnings("unchecked")
	@JsonProperty("votes")
	public void setVotes(Map<String, Object> id) {
		this.upvoteCount = (int) id.get("up_count");
		this.upvotes = (List<String>) id.get("up");
		this.upvoteCount = (int) id.get("count");
		
	}

	@JsonProperty("last_activity_at")
	public void setLastActivityAt(Map<String, Object> lastActivityAt) {
		Calendar calendar = Calendar.getInstance();
		String dateString = (String) lastActivityAt.get("$date");
		if (dateString.length() == 13) {
			calendar.setTimeInMillis(Long.parseLong(dateString));
			this.lastActivityAt = calendar.getTime();
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			try {
				this.lastActivityAt = sdf.parse(dateString);
			} catch (ParseException e) {
				log.warn("Error parsing date " + dateString, e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@JsonProperty("endorsement")
	public void setEndorsements(Map<String, Object> endorsements) {
		if(endorsements!=null){
			String userId = (String) endorsements.get("user_id");
			this.endorsementUserId = userId;
			Calendar calendar = Calendar.getInstance();
			Map<String, Object> timeEntry =  (Map<String, Object>)endorsements.get("time");
			
			String dateString = (String) timeEntry.get("$date");
			if (dateString.length() == 13) {
				calendar.setTimeInMillis(Long.parseLong(dateString));
				this.endorsementTime = calendar.getTime();
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				try {
					this.endorsementTime = sdf.parse(dateString);
				} catch (ParseException e) {
					log.warn("Error parsing date " + dateString, e);
				}
			}		
		}
	}

}
