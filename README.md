# DiscourseDB Parent Project
This is the parent project for all DiscourseDB projects. It manages dependency versions and configures necessary Maven plugins.

All DiscourseDB projects require Java 8+ and Maven 3. Eclipse furthermore has to be configured to support [Lombok (see below)](https://github.com/DiscourseDB/discoursedb-parent/blob/master/README.md#lombok-in-eclipse) for full DiscourseDB compatibility.

## Build and Run DiscourseDB
### Option 1: Launch Spring Boot Application with Maven
Go to the root folder of the project you want to launch (the folder containing the pom.xml).
Then execute 
```
mvn spring-boot:run
```
and maven should build the project and launch the run goal of Spring Boot.

If the build fails due to missing DiscourseDB dependencies, make sure you either have checked out and compiled (mvn install) all the projects your Boot projects depends on OR add the DiscourseDB artifactory to your settings.xml, which will take care of getting all dependencies for you.

### Option 2: Packaging
The following command builds all DiscourseDB projects, creates .jar files and creates a ```*-dist``` folder in the target folder of each DiscourseDB module which contains all external libraries necessary to run that module. 

```
mvn clean install -Passemble
```

It is then possible to run a particular class in any DiscourseDB module by calling the following command in a terminal (in this example, we call the edX Forum converter)

```
java -cp ".:PATH/TO/MODULE/discoursedb-io-edx-0.5-SNAPSHOT.jar:PATH/TO/MODULE/discoursedb-io-edx-0.5-SNAPSHOT-dist/*" edu.cmu.cs.lti.discoursedb.io.edx.forum.converter.EdxForumConverterApplication utarlingtonx-2015-03-22 /path/to/forum.json /path/to/usermapping.tsv
```
If you provide a ```custom.properties``` file on the classpath, e.g. the folder in which the above command is executed, the configuration provided in this properties file will override the standard configuration. This way, you can e.g. define different database access credentials for the DiscourseDB database. More information about this can be found [here](https://github.com/DiscourseDB/discoursedb-model/blob/master/README.md#discoursedb-configuration) in the discoursedb-model project.


## Database Server
DiscourseDB requires a database server. The BaseConfiguration is configured for MySQL, but you can use other relations DBMS and adapt the configuration accordingly. The following instruction will assume a MySQL setup.

DiscourseDB is configured to create a new database in case the database provided in the configuration does not exist. The database will be created with the default character encoding defined in the server configuration. We recommend either to (1) manually create an empty database the database with UTF8 encoding and have DiscourseDB use this database or (2) change the configuration of MySQL to use UTF8 by default so newly created databases will use this encoding.

(1) ```CREATE DATABASE discoursedb
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;```

or

(2) in my.cnf, add the following configuration
```
character-set-server=utf8
collation-server=utf8_general_ci
```
## Lombok in eclipse
DiscourseDB uses [Project Lombok](https://projectlombok.org) as a code generator for boiler plate code. Maven already takes care that Lombok generates its code during a regular build, but in order to get Eclipse to recognize the auto-generated code, you need to set up Lombok in your eclipse environment.

You have two options to set up your eclipse:
(1) simply go to any DiscourseDB Project you have checked out, expand the "Maven Dependencies" tab, find lombok.jar, right-click the jar and select Run as > Java Application. This will open a window where you can select the location of your eclipse and have Lombok set it up to recognize the auto-generated code.
(2) Download the [lombok.jar](https://search.maven.org/remotecontent?filepath=org/projectlombok/lombok/1.16.6/lombok-1.16.6.jar) manually, execute it (doubleclick it, or run java -jar lombok.jar) and then follow instructions.

After this setup, if eclipse reports that your project definitions are outdated, rightclick any project, select Maven > Update Project and select all DiscourseDB projects to trigger a Maven update.
