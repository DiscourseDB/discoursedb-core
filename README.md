# DiscourseDB Core

**Latest stable release:** 0.5<br/>
**Latest developer snapshot:** 0.6-SNAPSHOT<br/>

DiscourseDB requires Java 8+ and Maven 3. Eclipse furthermore has to be configured to support [Lombok (see below)](https://github.com/DiscourseDB/discoursedb-core/blob/master/README.md#setting-up-eclipse-to-support-project-lombok) for full DiscourseDB compatibility.

## How to get DiscourseDB

### Option 1: Configure Maven repository to automatically get DiscourseDB artifacts
You can simply add any DiscourseDB project as a dependency to your Maven project. The following configuration needs to be added to your project pom.xml or settings.xml.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd" xmlns="http://maven.apache.org/SETTINGS/1.1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <profiles>
    <profile>
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
      <id>artifactory</id>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>artifactory</activeProfile>
  </activeProfiles>
</settings>
```

### Option 2: Check out projects
To import the DiscourseDB with all sub-modules into eclipse, simply follow the following steps (Steps 3 and 4 are only necessary the first time you import a Maven project from git):

```
- Select the "Import..." context menu from the Package Explorer view
- Select "Check out Maven projects from SCM" option under the Maven category
- On the window that is presented choose the link "Find more SCM connectors"
- Find connector for Git...install...restart
```

## Configure DiscourseDB

DiscourseDB requires write access to a MySQL database. The standard configuration expects a local MySQL server running on port 3306 and a user with the login credentials local:local and sufficient permissions. The standard database name is discoursedb. Edit the properties file to change these parameters. DiscourseDB will automatically initialize a fresh DiscourseDB instance if none exists yet. You can customize the configuration as follows:

DiscourseDB is centrally configured using a [Java-based container configuration](http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-java). The default configuration is provided by the [BaseConfiguration](https://github.com/DiscourseDB/discoursedb-core/blob/master/discoursedb-model/src/main/java/edu/cmu/cs/lti/discoursedb/configuration/BaseConfiguration.java) class in the [discoursedb-model](https://github.com/DiscourseDB/discoursedb-core/tree/master/discoursedb-model) project, which can be replaced or extended by a custom configuration if you substantially need to substantially change the configuration. In most cases, the BaseConfiguration doesn't need to be altered since configuration parameters are read from properties files and thus don't require changes to the class.

Changing configuration parameters such as the credentials for the database connection can be achieved by overriding the default values with a **custom.properties** file that you _provide in the classpath_. The following values are defined by the default properties. **Any key-value-pair that is provided in a custom.properties file will override the respective default value.** You only need to specify the parameters you want to override.

```
# Default jdcb.properties
jdbc.driverClassName = com.mysql.jdbc.Driver
jdbc.host = localhost
jdbc.port = 3306
jdbc.database = discoursedb
jdbc.username = local
jdbc.password = local

# Default hibernate.properties
hibernate.dialect = edu.cmu.cs.lti.discoursedb.configuration.DiscourseDBMysqlDialect
hibernate.ejb.naming_strategy=org.hibernate.cfg.ImprovedNamingStrategy
hibernate.show_sql = false
hibernate.format_sql = false
hibernate.hbm2ddl.auto = update
hibernate.jdbc.batch_size = 100
hibernate.id.new_generator_mappings = false

# Default c3p0 properties
c3p0.acquireIncrement = 5 
c3p0.idleConnectionTestPeriod = 60
c3p0.maxStatements = 50
c3p0.minPoolSize = 1
c3p0.maxPoolSize = 100
```


## Build and Run DiscourseDB
### Option 1: Launch Spring Boot Application with Maven
Go to the root folder of the module you want to launch.
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

## Additional Developer Setup

### Setting up eclipse to support Project Lombok
This is only necessary if you want to check out DiscourseDB and view or edit the code in eclipse. DiscourseDB uses [Project Lombok](https://projectlombok.org) as a code generator for boiler plate code. Maven already takes care that Lombok generates its code during a regular build, but in order to get Eclipse to recognize the auto-generated code, you need to set up Lombok in your eclipse environment.

You have two options to set up your eclipse:
(1) simply go to any DiscourseDB Project you have checked out, expand the "Maven Dependencies" tab, find lombok.jar, right-click the jar and select Run as > Java Application. This will open a window where you can select the location of your eclipse and have Lombok set it up to recognize the auto-generated code.
(2) Download the [lombok.jar](https://search.maven.org/remotecontent?filepath=org/projectlombok/lombok/1.16.6/lombok-1.16.6.jar) manually, execute it (doubleclick it, or run java -jar lombok.jar) and then follow instructions.

After this setup, if eclipse reports that your project definitions are outdated, rightclick any project, select Maven > Update Project and select all DiscourseDB projects to trigger a Maven update.

### Database Server
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
