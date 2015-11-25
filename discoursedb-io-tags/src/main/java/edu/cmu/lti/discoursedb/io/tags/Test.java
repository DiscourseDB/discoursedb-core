package edu.cmu.lti.discoursedb.io.tags;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Test {

	public static void main(String[] args) throws IOException {
		
		/*
		String filepath1 = "/Users/haitian/Desktop/DALMOOC tweets 11-5-2014.csv";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filepath1)), "utf-8"));
		String test = br.readLine();
		test = br.readLine();
		
		StringTokenizer tokenizer = new StringTokenizer(test, ",", false);
		
		while(tokenizer.hasMoreTokens()) {
			System.out.println(tokenizer.nextToken());
		}
		
		br.close();
		*/
		
		String test = "jack,eric,,david";
		
		String[] strs = test.split(",");
		
		boolean isNull = false;
		
		if(strs[2].isEmpty()) isNull = true;
		
		System.out.println(isNull);
		
	}

}
