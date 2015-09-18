# DiscourseDB IO edX
This project provides converters for edX data dumps. Right now, discoursedb-io-edx can import discussion forum data into a DiscourseDB instance. Support for other parts of the edX data dumps will be added to this project in the future. Converters for other sources than edX have their own discoursedb-io-* projects.

## Requirements and Setup
All DiscourseDB projects require Java 8 and Maven 3.

To import the project into eclipse, simply follow the following steps (Steps 3 and 4 are only necessary the first time you import a Maven project from git):

```
- Select the "Import..." context menu from the Package Explorer view
- Select "Check out Maven projects from SCM" option under the Maven category
- On the window that is presented choose the link "Find more SCM connectors"
- Find connector for Git...install...restart
```

This project has dependencies to the [discoursedb-model](https://github.com/DiscourseDB/discoursedb-model) project and the [discoursedb-parent](https://github.com/DiscourseDB/discoursedb-parent) project. You need to check out both of these projects for the converter project to work. We are currently working on setting up an artifactory that serves these repositories automatically so Maven can pull in the dependencies as libraries without you having to check them out.

This converter requires write access to a MySQL database. The access credentials are defined in the [discoursedb-model](https://github.com/DiscourseDB/discoursedb-model) in the [hibernate.properties](https://raw.githubusercontent.com/DiscourseDB/discoursedb-model/master/discoursedb-model/src/main/resources/hibernate.properties). The standard configuration expects a local MySQL server running on port 3306 and a user with the login credentials user:user and sufficient permissions. The standard database name is discoursedb. Edit the properties file to change these parameters. DiscourseDB will automatically initialize a fresh DiscourseDB instance if none exists yet. Otherwise, it will import data into the existing database.

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
Phase 1 covers the non-relational aspects of the data import and creates all the entities necessary to represent a forum post (Contribution, Entity, Discourse, DiscoursePart etc.)
Phase 2 creates relationships between the contributions and creates the thread structure. 
Phase 3 augments the user information which details that are not contained in the forum dump by reading a separate data file from the edx data package.

### Phase 1 
[EdxForumConverterPhase1.java](https://github.com/DiscourseDB/discoursedb-io-edx/blob/master/discoursedb-io-edx/src/main/java/edu/cmu/cs/lti/discoursedb/io/edx/forum/converter/EdxForumConverterPhase1.java)

This component is launched first as indicatd by the ```@Order(1)``` annotation. In classes implementing the CommandLineRunner interface, the run method takes over the role of the main method. It is automatically invoked by the starter class which also passes on the command line arguments. The first argument is supposed to contain the location of the edx forum json dump.

The forum converter uses the [Jackson-Databind](https://github.com/FasterXML/jackson-databind) library to parse the Json forum dump and bind each entity to a POJO. The POJO for a forum post that Jackson maps to can be found [here](https://github.com/DiscourseDB/discoursedb-io-edx/blob/master/discoursedb-io-edx/src/main/java/edu/cmu/cs/lti/discoursedb/io/edx/forum/model/Post.java). The ```@map(Post)``` method of the converter then individually maps each Post object produced by the streaming parser to DiscourseDB entities.

Mapping a post to DiscourseDB involves the creation of several entities. The discoursedb-model documentation will provide a more detailed account of the the discoursedb schem. The following decisions have to made in Phase 1 of the forum import process (step through the code while you read this)

- The Discourse is the context in which the forum conversations took place, i.e. the edX course. First, check whether a Discourse entitiy exists in DiscourseDB for the given edX course and create it if necessary.
- Forums can have nested structures such as sub forums. Forums and sub forums are basically containers for contributions which translate to DiscoursePart entities. Check whether a DiscoursePart for the edX forum for the given edX course exists and create it otherwise. 
- DiscoursePart entities are generic containers. Since we are dealing with a forum we need to fetch or create a DiscoursePartType entity and assign it to the DiscoursePart
- Establish a relationship between Discourse and DiscoursePart. This needs to be done explicitly, since we are keeping track of the time span this relationsship existed, i.e. the relation is represented in a separate relation table.
- Each post has a specific author. Check if the user is already stored in DiscourseDB and create the corresponding entity if not.
- Create a contribution for the post and fill in all the fields for which we have data. The textual content is not part of the contribution but represented in a separate content entity.
-Contributions in edX forums can either be thread starters or comments. Create a ContributionType entity and set the type value to ContributionTypes.THREAD_STARTER or ContributionTypes.POST. Establish a relation between that ContributionType and the Contribution.
- Create a content entity containig the textual content of the contribution. Content entities allow us to keep track of revisions to contributions. Since edX forum dumps do not contain post revisions, a single content entity will be both the first and the current revision of the contribution we just created.
-Finally, create a relationship between the Contribution and the DiscoursePart. Again, this relation holds additional information about when this relationship was active, so it needs to be represented in a separate DiscoursePartContribution entity.

These steps create all the information except for relationships between contributions, since this is not possible when processing the data dump sequentially.

### Phase 2
[EdxForumConverterPhase2.java](https://github.com/DiscourseDB/discoursedb-io-edx/blob/master/discoursedb-io-edx/src/main/java/edu/cmu/cs/lti/discoursedb/io/edx/forum/converter/EdxForumConverterPhase2.java)

This component is launched first as indicatd by the ```@Order(2)``` annotation.

### Phase 3
[EdxForumConverterPhase3.java](https://github.com/DiscourseDB/discoursedb-io-edx/blob/master/discoursedb-io-edx/src/main/java/edu/cmu/cs/lti/discoursedb/io/edx/forum/converter/EdxForumConverterPhase3.java)

This component is launched last as indicatd by the ```@Order(3)``` annotation.