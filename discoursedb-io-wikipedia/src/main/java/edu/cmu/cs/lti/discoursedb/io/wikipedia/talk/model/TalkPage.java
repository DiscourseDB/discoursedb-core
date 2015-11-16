package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.Revision;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.RevisionApi;

public class TalkPage {
	private boolean splitOnIndentation = false;
	private int userTurnTimeWindowSize = 300000; // 5 minutes

	private Revision tpBaseRevision = null;

	public Revision getTpBaseRevision() {
		return tpBaseRevision;
	}

	private List<Topic> topics = null;

	public List<Topic> getTopics() {
		return topics;
	}

	private ParagraphForwardChecker checker = null;
	private RevisionApi revApi = null;
	private boolean aggregateParagraphs;

	/**
	 * @param revApi
	 *            RevisionApi instance
	 * @param rev
	 *            talk page revision to process
	 * @param aggregateParagraphs
	 *            whether to aggregate paragraphs to turns (true) or to consider
	 *            paragraphs as turns on their own (false)
	 */
	public TalkPage(RevisionApi revApi, int revId, boolean aggregateParagraphs) {
		this.revApi = revApi;
		this.aggregateParagraphs = aggregateParagraphs;

		try {
			tpBaseRevision = revApi.getRevision(revId);
		} catch (WikiApiException e) {
			System.err.println(
					"Error checking revisions of origin for paragraphs. Could not process revision. Error accessing Wikipedia database with revision API");
			e.printStackTrace();
		}
		_segmentParagraphs();
		_buildTurns();
	}

	/**
	 * @param revApi
	 *            RevisionApi instance
	 * @param rev
	 *            talk page revision to process
	 * @param aggregateParagraphs
	 *            whether to aggregate paragraphs to turns (true) or to consider
	 *            paragraphs as turns on their own (false)
	 */
	public TalkPage(RevisionApi revApi, Revision rev, boolean aggregateParagraphs) {
		this.tpBaseRevision = rev;
		this.revApi = revApi;
		this.aggregateParagraphs = aggregateParagraphs;
		_segmentParagraphs();
		_buildTurns();
	}

	public void removeTurnsBeforeTimestamp(Timestamp ts) {
		Iterator<Topic> topicIter = topics.iterator();
		while (topicIter.hasNext()) {
			Topic t = topicIter.next();
			for (TalkPageParagraph curPar : t.getParagraphs()) {
				if (curPar != null && curPar.getTimestamp() != null && curPar.getTimestamp().before(ts)) {
					t.removeParagraph(curPar);
				}
			}
			List<Turn> turns = new ArrayList<Turn>();
			turns.addAll(t.getUserTurns());
			for (Turn curTurn : turns) {
				if (curTurn != null && curTurn.getTimestamp() != null && curTurn.getTimestamp().before(ts)) {
					t.removeUserTurn(curTurn);
				}
			}
			if (t.getUserTurns().isEmpty()) {
				topicIter.remove();
			}
		}
	}

	/**
	 * Segments the TalkPage into paragraphs and extracts meta information for
	 * each paragraph
	 */
	private void _segmentParagraphs() {
		try {
			// create a new forward checker for the given revision.
			// this takes a while, because it builds up a revision cache from
			// the database
			checker = new ParagraphForwardChecker(revApi, tpBaseRevision);

			// segment pages into topics and paragraphs
			TopicExtractor tExt = new TopicExtractor();
			this.topics = tExt.getTopics(tpBaseRevision.getRevisionText());

			// extract meta information for paragraphs from revision history
			for (Topic t : topics) {
				for (TalkPageParagraph tpp : t.getParagraphs()) {
					checker.addMetaInfo(revApi, tpp);
				}
			}
		} catch (WikiApiException e) {
			System.err.println(
					"Error checking revisions of origin for paragraphs. Could not process revision. Error accessing Wikipedia database with revision API");
			e.printStackTrace();
		}
	}

	/**
	 * Creates turns from paragraphs. Note that begin and end index are no
	 * longer used and might be removed in the future. If
	 * <code>aggregateParagraphs</code> is true, multiple paragraphs are
	 * heuristically aggregated to turns. Otherwise, each paragraph is used as a
	 * separate turn.
	 */
	private void _buildTurns() {
		// work through all topics
		for (Topic t : topics) {
			int curTurnNumInTopic = 1;
			Set<TalkPageParagraph> pars = t.getParagraphs(); // this is a sorted set of pars

			// heuristically aggregate neighboring paragraphs to turns
			if (aggregateParagraphs) {
				Turn turn = null; // Current unsaved Turn
				StringBuffer curTurnText = new StringBuffer();
				for (TalkPageParagraph par : t.getParagraphs()) { // Iterate over paragraphs
					// Check if it is a new turn or we are in the first paragraph
					if (turn == null || isNewUserTurn(turn, par)) {
						if (turn != null) {
							turn.setText(curTurnText.toString());
							curTurnText = new StringBuffer();
							t.addUserTurn(turn); // commit turn to topic							
						}
						turn = new Turn(); // Create new user turn
						if(!cleanText(par.getText()).isEmpty()){
							curTurnText.append(cleanText(par.getText()));							
						}
						turn.setBegin(par.getBegin());
						turn.setEnd(par.getEnd());
						turn.setIndentAmount(par.getIndentAmount());
						turn.setContributor(par.getContributor());
						turn.setTimestamp(par.getTimestamp());
						turn.setTurnNr(curTurnNumInTopic++);
					} else {
						turn.setEnd(par.getEnd());
						if(!cleanText(par.getText()).isEmpty()){
							curTurnText.append(System.lineSeparator()).append(cleanText(par.getText()));							
						}						
					}
				}
			}
			// no aggregation. we consider one paragraph = one turn
			else {
				for (TalkPageParagraph curPar : pars) {
					Turn curTurn = new Turn();
					curTurn.setBegin(curPar.getBegin());
					curTurn.setEnd(curPar.getEnd());
					curTurn.setContributor(curPar.getContributor());
					curTurn.setRevisionId(curPar.getRevisionId());
					curTurn.setIndentAmount(curPar.getIndentAmount());
					curTurn.setText(curPar.getText());
					curTurn.setTimestamp(curPar.getTimestamp());
					curTurn.setTurnNr(curTurnNumInTopic++);
					t.addUserTurn(curTurn);
				}
			}

			// update the newly built turns with reference information
			// might not make too much sense if we didn't aggregate
			new ReferenceExtractor().setReferences(topics);

		}
	}

	/**
	 * Checks if new paragraph belongs to new user turn
	 * 
	 * @param currentTurn the current turn
	 * @param par the paragraph we want to check whether it's part of the current turn
	 * @return true, if the paragraph is not part of the current turn and rather starts a new turn
	 */
	private boolean isNewUserTurn(Turn currentTurn, TalkPageParagraph par) {
		return !par.contributorIsBot()&&( //a bot cannot start a new turn
						!par.getContributor().equals(currentTurn.getContributor()) //if the user name changes we have a new turn 
						|| !checkTimeInWindow(currentTurn.getTimestamp(), par.getTimestamp(), userTurnTimeWindowSize) //if we are outside of the allowed time window, we have a new turn
						|| (splitOnIndentation && currentTurn.getIndentAmount() != par.getIndentAmount()) //in case we want to start a new turn whenever a message is indented (usually not)
				); 

	}

	private boolean checkTimeInWindow(Timestamp t1, Timestamp t2, long windowSize) {
		return Math.abs(t1.getTime() - t2.getTime()) <= windowSize;
	}

	private String cleanText(String text){
		return text.replaceAll("center|thumb|", "IMAGE:").replaceAll("||", "IMAGE:").trim();
	}
	
}
