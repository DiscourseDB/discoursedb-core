# Web UI for DiscourseDB annotation

This user interface showcases the capabilities of DiscourseDB's annotation subsystem and allows a user to retrieve data and annotate it and save it back to DiscourseDB.

## Project Setup
The project uses the [Frontend Maven Plugin](https://github.com/eirslett/frontend-maven-plugin) to to manage and obtain frontend libraries. The frontend libraries and their versions are defined in [package.json](https://github.com/DiscourseDB/discoursedb-annotation-ui/blob/master/src/main/resources/static/package.json) and will be automatically downloaded by maven using [npm](https://www.npmjs.com/).

The UI is based on [ReactJS](https://facebook.github.io/react/) and connects to DiscourseDB via [Spring Data REST](http://projects.spring.io/spring-data-rest/).

In case the ```npm install``` phase of the project build fails, try to empty your ~/.npm folder. This triggers a redownload of libraries which should fix the issue.

The basic setup of this UI is based on the [Spring REACT+REST tutorial](https://spring.io/guides/tutorials/react-and-spring-data-rest/). 

## Launch Spring Boot Application with Maven
Go to the root folder of the project (the folder containing the pom.xml).
Then execute 
```
mvn spring-boot:run
```
and maven should build the project and launch the run goal of Spring Boot.

If the build fails due to missing DiscourseDB dependencies, make sure you either have checked out and compiled (mvn install) all the projects this project depends on (discoursedb-parent, discoursedb-model) OR add the DiscourseDB artifactory to your settings.xml, which will take care of getting all dependencies for you ([described here](https://github.com/DiscourseDB/discoursedb-model#configuring-maven-repository))

Alternatively, you can package DiscourseDB and run it from a jar as [described here](https://github.com/DiscourseDB/discoursedb-parent#option-2-packaging).
