package edu.cmu.cs.lti.discoursedb.github.converter;

public class GithubConverterUtil {

	public static String standardIssueIdentifier(String project, long issue) {
		if (issue == 0) {
			return project + " commit messages";
		} else {
			return "Issue " + project + "#" + issue;			
		}
	}
}
