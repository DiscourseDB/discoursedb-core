package edu.cmu.cs.lti.discoursedb.io.neuwirth.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.neuwirth.converter"})
public class NeuwirthConverterApplication {

	public static void main(String[] args) {
		Assert.isTrue(args.length==2, "Usage: NeuwirthConverterApplication <DataSetName> <NeuwirthFileFolderPath>");
		SpringApplication.run(NeuwirthConverterApplication.class, args);
	}

}
