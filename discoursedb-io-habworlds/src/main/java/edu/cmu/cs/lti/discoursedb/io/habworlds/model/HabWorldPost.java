package edu.cmu.cs.lti.discoursedb.io.habworlds.model;

import lombok.Data;

/**
 * POJO representing a post in habworlds.
 * 
 * Boilerplate code will be auto-generated by Lombok at compile time. 
 * 
 * @author Haitian Gong
 *
 */

@Data
public class HabWorldPost {
	
	private int InteractionID;
	private String sessionID;
	private int userID;
	private int tutorialSessionID;
	private int tutorialID;
	private int activityID;
	private String questionID;
	private int attempt;
	private int questionResult;
	private int questionScore;
	private String serverTime;
	private String snapshot;
	private String trapStateID;
	private int tutorialAttempt;
	private String sequenceId;

}