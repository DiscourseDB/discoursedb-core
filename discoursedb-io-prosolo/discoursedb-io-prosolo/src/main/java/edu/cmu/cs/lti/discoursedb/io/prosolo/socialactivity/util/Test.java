package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.util;

public class Test {

	public static void main(String[] args) {
		String edXid = "https://courses.edx.org/openid/provider/login/RobertPena";
		System.out.println(edXid.substring(edXid.lastIndexOf("/")+1));
	}

}
