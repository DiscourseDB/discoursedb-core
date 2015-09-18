# DiscourseDB IO edX
This project provides converters for edX data dumps. Right now, discoursedb-io-edx can import discussion forum data into a DiscourseDB instance. Support for other parts of the edX data dumps will be added to this project in the future. Converters for other sources than edX have their own discoursedb-io-* projects.

## Requirements
All DiscourseDB components require Java 8 and Maven 3.

## DiscourseDB

## Forum Converter Architecture
All DiscourseDB-IO projects are [Spring Boot Applications](http://projects.spring.io/spring-boot/). Spring Boot is a Spring project that makes it easy to create stand-alone Spring based applications with a minimum of configuration. A single [starter class](https://github.com/DiscourseDB/discoursedb-io-edx/blob/master/discoursedb-io-edx/src/main/java/edu/cmu/cs/lti/discoursedb/io/edx/forum/converter/EdxForumConverterApplication.java) is launched by the user which configures the runtime environment and establishes a connection to DiscourseDB. The necessary DiscourseDB configurations are automatically pulled in from the [discoursedb-model](https://github.com/DiscourseDB/discoursedb-model) project which contains the DiscourseDB core components.
Once the environment is set up, SpringBoot launches all classes with an @Component annotation in the order provided by the @Order annotations. The forum conversion requires three of these components which are described in more detail below. For more information about SpringBoot, have a look at the [reference documentation](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/).

ALl DiscourseDB-IO components implement the [CommandLineRunner](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-command-line-runner) interface, which allows the few configuration parameters (i.e. location of the data dumps that should be imported) to be passed to the converters in a terminal. 

## Converter Components

The main processing units of a DiscourseDB-IO project are Spring Components that have access to the DiscourseDB database via autowired [Spring Data Repositories](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories). A Spring Data Repository provides access methods to the data in the DiscourseDB database. Autowired means that Spring takes care of establishing the connection to the database by injecting a fully repository instance into your field or constructor. 
For instance, the field

```java
@Autoconfigured
private UserRepository userRepo;
```
will be instantiated automatically by Spring and we can use userRepo in our Components to access User data in DiscoursDB.
For more information about Spring Data JPA, have a look at the [reference documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/).

The forum conversion is split into three phases. Each phase corresponds to a separate component.

### Phase 1 
[EdxForumConverterPhase1.java](https://github.com/DiscourseDB/discoursedb-io-edx/blob/master/discoursedb-io-edx/src/main/java/edu/cmu/cs/lti/discoursedb/io/edx/forum/converter/EdxForumConverterPhase1.java)

### Phase 2
[EdxForumConverterPhase2.java](https://github.com/DiscourseDB/discoursedb-io-edx/blob/master/discoursedb-io-edx/src/main/java/edu/cmu/cs/lti/discoursedb/io/edx/forum/converter/EdxForumConverterPhase2.java)

### Phase 3
[EdxForumConverterPhase3.java](https://github.com/DiscourseDB/discoursedb-io-edx/blob/master/discoursedb-io-edx/src/main/java/edu/cmu/cs/lti/discoursedb/io/edx/forum/converter/EdxForumConverterPhase3.java)

