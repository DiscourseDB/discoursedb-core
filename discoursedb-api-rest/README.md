# discoursedb-api-rest
RESTful API to the discoursedb-model based on Spring Data REST

## Latest JavaDoc
The JavaDoc of the latest build can be found [here](http://moon.lti.cs.cmu.edu:8080/job/DiscourseDB/edu.cmu.cs.lti$discoursedb-api-rest/javadoc/)

## Requirements and Setup
All DiscourseDB projects require Java 8 and Maven 3.

## Launch Spring Boot Application with Maven
Go to the root folder of the project (the folder containing the pom.xml).
Then execute 
```
mvn spring-boot:run
```
and maven should build the project and launch the run goal of Spring Boot.

If the build fails due to missing DiscourseDB dependencies, make sure you either have checked out and compiled (mvn install) all the projects this project depends on (discoursedb-parent, discoursedb-model) OR add the DiscourseDB artifactory to your settings.xml, which will take care of getting all dependencies for you ([described here](https://github.com/DiscourseDB/discoursedb-model#configuring-maven-repository))

Alternatively, you can package DiscourseDB and run it from a jar as [described here](https://github.com/DiscourseDB/discoursedb-parent#option-2-packaging).

### Configuring Maven repository
You can simply add any DiscourseDB project as a dependency to your Maven project. The following configuration needs to be added to your project pom.xml or settings.xml.

```xml
      <repositories>
        <repository>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <id>central</id>
          <name>libs-release</name>
          <url>http://moon.lti.cs.cmu.edu:8081/artifactory/libs-release</url>
        </repository>
        <repository>
          <snapshots />
          <id>snapshots</id>
          <name>libs-snapshot</name>
          <url>http://moon.lti.cs.cmu.edu:8081/artifactory/libs-snapshot</url>
        </repository>
      </repositories>
```

### Check out projects
To import the project into eclipse, simply follow the following steps (Steps 3 and 4 are only necessary the first time you import a Maven project from git):

```
- Select the "Import..." context menu from the Package Explorer view
- Select "Check out Maven projects from SCM" option under the Maven category
- On the window that is presented choose the link "Find more SCM connectors"
- Find connector for Git...install...restart
```

Like all DiscourseDB projects, this project depends on the [discoursedb-parent](https://github.com/DiscourseDB/discoursedb-parent) and the [discoursedb-model](https://github.com/DiscourseDB/discoursedb-model) project. You need to check out these projects or add the Artifactory configuration above to your settings.xml, so that Maven can pull in the artifacts automatically.

DiscourseDB requires write access to a MySQL database. The access credentials are defined in the [hibernate.properties](https://raw.githubusercontent.com/DiscourseDB/discoursedb-model/master/discoursedb-model/src/main/resources/hibernate.properties). The standard configuration expects a local MySQL server running on port 3306 and a user with the login credentials user:user and sufficient permissions. The standard database name is discoursedb. Edit the properties file to change these parameters. DiscourseDB will automatically initialize a fresh DiscourseDB instance if none exists yet. Otherwise, it will import data into the existing database.
