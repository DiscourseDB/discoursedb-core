# DiscourseDB IO edX
This project provides converters for edX data dumps. Right now, discoursedb-io-edx can import discussion forum data into a DiscourseDB instance. Support for other parts of the edX data dumps will be added to this project in the future. Converters for other sources than edX have their own discoursedb-io-* projects.

## Requirements and Setup
All DiscourseDB projects require Java 8 and Maven 3.

This project has dependencies to the [discoursedb-model](https://github.com/DiscourseDB/discoursedb-model) project and the [discoursedb-parent](https://github.com/DiscourseDB/discoursedb-parent) project. You need to check out both of these projects for the converter project to work. We are currently working on setting up an artifactory that serves these repositories automatically so Maven can pull in the dependencies as libraries without you having to check them out.

This converter requires write access to a MySQL database. The access credentials are defined in the [discoursedb-model](https://github.com/DiscourseDB/discoursedb-model) in the [hibernate.properties](https://raw.githubusercontent.com/DiscourseDB/discoursedb-model/master/discoursedb-model/src/main/resources/hibernate.properties). The standard configuration expects a local MySQL server running on port 3306 and a user with the login credentials user:user and sufficient permissions. The standard database name is discoursedb. Edit the properties file to change these parameters.


## DiscourseDB

## Forum Converter Architecture
All DiscourseDB-IO projects are [Spring Boot Applications](http://projects.spring.io/spring-boot/). Spring Boot is a Spring project that makes it easy to create stand-alone Spring based applications with a minimum of configuration. A single [starter class](https://github.com/DiscourseDB/discoursedb-io-edx/blob/master/discoursedb-io-edx/src/main/java/edu/cmu/cs/lti/discoursedb/io/edx/forum/converter/EdxForumConverterApplication.java) is launched by the user which configures the runtime environment and establishes a connection to DiscourseDB. The necessary DiscourseDB configurations are automatically pulled in from the [discoursedb-model](https://github.com/DiscourseDB/discoursedb-model) project which contains the DiscourseDB core components.
Once the environment is set up, SpringBoot launches all classes with an ```@Component``` annotation in the order provided by the ```@Order``` annotations. The forum conversion requires three of these components which are described in more detail below. For more information about SpringBoot, have a look at the [reference documentation](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/).

All DiscourseDB-IO components implement the [CommandLineRunner](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-command-line-runner) interface, which allows the few configuration parameters (i.e. location of the data dumps that should be imported) to be passed to the converters in a terminal. 

## Converter Components

The main processing units of a DiscourseDB-IO project are Spring Components that have access to the DiscourseDB database via autowired [Spring Data Repositories](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories). A Spring Data Repository provides access methods to the data in the DiscourseDB database. Autowired means that Spring takes care of establishing the connection to the database by injecting a fully repository instance into your field or constructor. 
For instance, the field

```java
@Autowired
private UserRepository userRepo;
```
will be instantiated by Spring automatically and we can then use ```userRepo``` in the component to access User data in DiscoursDB without any further setup.
For more information about Spring Data JPA, have a look at the [reference documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/).

The responsibility of a converter is to load the source data, produce a mapping to the DiscourseDB schema and then store it in DiscourseDB using the required Data Repositories.

Currently, all DiscourseDB Core repositories provide CRUD operations. In most cases, it is necessary to add custom data access methods to the repositories when mapping data to DiscourseDB. In that case, these methods are to be added to the Repository interfaces in ```edu.cmu.cs.lti.discoursedb.core.repository``` in the [discoursedb-model](https://github.com/DiscourseDB/discoursedb-model) project. Documentation for this will be added to the discoursedb-model project. A detailed description of how to define data access and query methods can already be found [here](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.details).

## Forum Conversion
The forum conversion is split into three phases. Each phase corresponds to a separate component.

### Phase 1 
[EdxForumConverterPhase1.java](https://github.com/DiscourseDB/discoursedb-io-edx/blob/master/discoursedb-io-edx/src/main/java/edu/cmu/cs/lti/discoursedb/io/edx/forum/converter/EdxForumConverterPhase1.java)

This component is launched first as indicatd by the ```@Order(1)``` annotation. In classes implementing the CommandLineRunner interface, the run method takes over the role of the main method. It is automatically invoked by the starter class which also passes on the command line arguments. The first argument is supposed to contain the location of the edx forum json dump.

The forum converter uses the  https://github.com/FasterXML/jackson-databind



### Phase 2
[EdxForumConverterPhase2.java](https://github.com/DiscourseDB/discoursedb-io-edx/blob/master/discoursedb-io-edx/src/main/java/edu/cmu/cs/lti/discoursedb/io/edx/forum/converter/EdxForumConverterPhase2.java)

### Phase 3
[EdxForumConverterPhase3.java](https://github.com/DiscourseDB/discoursedb-io-edx/blob/master/discoursedb-io-edx/src/main/java/edu/cmu/cs/lti/discoursedb/io/edx/forum/converter/EdxForumConverterPhase3.java)

