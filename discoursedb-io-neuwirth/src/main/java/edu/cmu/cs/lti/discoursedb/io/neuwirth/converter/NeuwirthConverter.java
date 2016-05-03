package edu.cmu.cs.lti.discoursedb.io.neuwirth.converter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@Component
public class NeuwirthConverter implements CommandLineRunner{
		
	@Autowired private NeuwirthConverterService converterService;
	
	@Override
	public void run(String... args) throws IOException, ParseException {
		
		Assert.isTrue(args.length==2,"Usage: NeuwirthConverterApplication <DatasetName> </path/to/datafolder>");
		String dataSetName = args[0];
		String folderPath = args[1];		
		Assert.isTrue(new File(folderPath).isDirectory(),"Input file does not exist or is not readable.");
		
		convert(dataSetName, folderPath);		
	}
	
	private void convert(String dataSetName, String folderPath) throws IOException, ParseException {		
		File folder = new File(folderPath);
		Arrays.stream(folder.listFiles()).forEach(file-> converterService.mapFile(dataSetName, file));
	}
	
}