package edu.cmu.cs.lti.discoursedb.io.coursera;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.lti.discoursedb.io.coursera.io.CourseraDB;
import edu.cmu.cs.lti.discoursedb.io.coursera.model.Thread;

public class Test {

	public static void main(String[] args) throws SQLException {
		CourseraDB database = new CourseraDB("localhost", "coursera", "local", "local");
		List<Integer> ids = (ArrayList<Integer>) database.getIds("forum");
		System.out.println(ids.size());
		Thread t = (Thread) database.getDbEntity("thread", 1);
		System.out.println(t.getTitle());
		System.out.println(t.getForum_id()+" "+t.getVotes()+" "+t.getLast_updated_user());
	}

}
