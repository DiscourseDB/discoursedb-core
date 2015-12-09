# DiscourseDB Core Model
This project contains the core object model for DiscourseDB which both defines the database schema and constitutes as an access layer to the the database. discoursedb-model is based on the [Spring Framework](http://projects.spring.io/spring-framework/) and [Spring Data JPA](http://projects.spring.io/spring-data-jpa/) with [Hibernate ORM](http://hibernate.org/orm/) as its JPA Provider. Query abstraction is provided by [QueryDSL-JPA](http://www.querydsl.com/).

## Requirements and Setup
All DiscourseDB projects require Java 8 and Maven 3.

### Database Server
DiscourseDB requires a database server. The BaseConfiguration is configured for MySQL, but you can use other relations DBMS and adapt the configuration accordingly. The following instruction will assume a MySQL setup.

DiscourseDB is configured to create a new database in case the database provided in the configuration does not exist. The database will be created with the default character encoding defined in the server configuration. We recommend either to (1) manually create an empty database the database with UTF8 encoding and have DiscourseDB use this database or (2) change the configuration of MySQL to use UTF8 by default so newly created databases will use this encoding.

### Database Server
DiscourseDB requires a database server. The BaseConfiguration is configured for MySQL, but you can use other relations DBMS and adapt the configuration accordingly. The following instruction will assume a MySQL setup.

DiscourseDB is configured to create a new database in case the database provided in the configuration does not exist. The database will be created with the default character encoding defined in the server configuration. We recommend either to (1) manually create an empty database the database with UTF8 encoding and have DiscourseDB use this database or (2) change the configuration of MySQL to use UTF8 by default so newly created databases will use this encoding.

(1) ```CREATE DATABASE `discoursedb` CHARACTER SET utf8 COLLATE utf8_general_ci;```

or

(2) in my.cnf, add the following configuration
```
character-set-server=utf8
collation-server=utf8_general_ci
```

### Configuring Maven repository
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

### Check out projects
To import the project into eclipse, simply follow the following steps (Steps 3 and 4 are only necessary the first time you import a Maven project from git):

```
- Select the "Import..." context menu from the Package Explorer view
- Select "Check out Maven projects from SCM" option under the Maven category
- On the window that is presented choose the link "Find more SCM connectors"
- Find connector for Git...install...restart
```

Like all DiscourseDB projects, this project depends on the [discoursedb-parent](https://github.com/DiscourseDB/discoursedb-parent) project. You need to check out the parent project for the model project to work and/or add the Artifactory configuration above to you settings.xml, so that Maven can pull in the artifacts automatically.

DiscourseDB requires write access to a MySQL database. The access credentials are defined in the [hibernate.properties](https://raw.githubusercontent.com/DiscourseDB/discoursedb-model/master/discoursedb-model/src/main/resources/hibernate.properties). The standard configuration expects a local MySQL server running on port 3306 and a user with the login credentials local:local and sufficient permissions. The standard database name is discoursedb. Edit the properties file to change these parameters. DiscourseDB will automatically initialize a fresh DiscourseDB instance if none exists yet. Otherwise, it will import data into the existing database.

## DiscourseDB Configuration
DiscourseDB is centrally configured using a [Java-based container configuration](http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-java) (see Spring docs for more details). The default configuration is provided by the [BaseConfiguration](https://github.com/DiscourseDB/discoursedb-model/blob/master/src/main/java/edu/cmu/cs/lti/discoursedb/configuration/BaseConfiguration.java) class which can be replaced or extended by custom configuration  if you substantially need to  substantially change the configuration. In most cases, the BaseConfiguration doesn't need to be altered since configuration parameters are read from properties files and thus don't require changes to the class.

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

## DiscourseDB Model Architecture Overview

### Description of Main Entities 
Please also refer to [this informal overview of the main entities](https://github.com/DiscourseDB/discoursedb-model/raw/master/informal_model_description.pdf) and to the entity class descriptions in the [Javadoc](http://moon.lti.cs.cmu.edu:8080/job/DiscourseDB/edu.cmu.cs.lti$discoursedb-model/javadoc/). A high level overview can be found <a href="https://github.com/DiscourseDB/discoursedb-model#high-level-overview-a-step-by-step-example">below</a>.

#### Discourse
A Discourse represents the broad context of interactions that might come from multiple datasets. For example, a Discourse could represent an installment of an online course. All interactions in the context of this course - independent from the source dataset - will be associated with this Discourse instance. Another installment of the same course would be represented by a new Discourse instance.
A Discourse is associated to one or more DiscoursePart instances which represent sub-spaces in the realm of the Discourse. That is, an online course with a discussion forum and chat would have two DiscoursePart instances associated with its Discourse instance which represent these two discussion spaces.

#### DiscoursePart
A DiscoursePart represents a distinct sub-space within a Discourse. For instance, a DiscoursePart could represent a discussion forum. That is, it acts as a container for interactions that happen in this discussion forum. DiscourseParts are typed entities, i.e. they are associated with a DiscoursePartType which indicates what the DiscoursePart represents, e.g. a FORUM. Furthermore, DiscourseParts can be related to each other with DiscoursePartRelations in order to indicate embedded structures. For instance, a Forum could consist of multiple sub-forums. DiscoursePartRelations are also typed entities, i.e. they are related to a DiscoursePartRelationType indicating what the relation represents, e.g. an EMBEDDING in the case of forum-subforum.

#### Contribution
A Contribution entity is a representation of a contribution in a discussion space such as a forum post, chat message or equivalent discourse artifact. Contributions only represent meta information about the contribution while the actual content is represented by Content entities (see below). This allows DiscourseDB to capture the revision history of a contribution. Revisions are Content entities that link to their previous and next revision. Thus, the revision history of a contribution is represented by a doubly linked list of Content instances and the Contribution links to the head and the tail of this list. If not revisions are maintained, both pointers link to the same Content entity.

A Contribution is a typed entity, i.e. it is associated with a ContributionType indiciating what the Contribution instance represents, e.g. a POST.

#### Content
Content entities represent the content of Contribution and Context entities. The main payload of a Content entity resides in its text and data field. The content of Contributions usually textual, thus the text field will hold the content of a Contribution. The data field is able to hold arbitrary blobs of data. This is most likely necessary when used to represent the content of Context entities but will rarely be the case for content of Contribution entities.

Content entities formally represent nodes in a linked list by pointing to a previous and a next content revision. This way, revision histories of Contribution and Context entities can be represented. 

A Content entity is related to a User indicating that this user is the author of the content instance. Other relationships between Users and Content or Contributions can be represented with ContributionInteraction entities (see below).

#### Context
Context is whatever a Contribution is referring to. For example if the contributions are comments on a Wikipedia talk page, then context might be version of the wiki page at the point in time the comment was made.  
Context entities are associated with Contribution entities via a ContributionContext entity. Apart from that, Context entities resemble Contribution entities, ie. the content of a Context entity is represented by one or more Content instances (see above).

#### DataSource
For many reasons, we might want to keep track of where the data for a DiscourseDB entity came from. This is either relevant in case we need to get details from the original dataset that are not represented in DiscourseDB or is important during the data import phase where we have to refer to ids and primare keys in the original dataset in order to make connections between entities.

DataSourceInstance entities keep track of where data came from. Such entities can be associated with all _entities with source information_ (see [below](https://github.com/DiscourseDB/discoursedb-model#entity-classes-the-discoursedb-core-model)). A single DiscourseDB entity can be associated with one or more DataSourceInstance entities. In most cases, a single DataSourceInstance is sufficient. However, there are cases (such as User data) that might relate to data points in multiple datasets, so we need to keep track of all its sources. In turn, a single DataSourceInstance can only be related to a single DiscourseDB entity.

A DataSourceInstance consists of four main components. 
- The _dataSourceId_ contains the id of the entity in the source dataset (i.e. how is the instance identified in the source). 
- The _entitySourceDescriptor_ identifies the name/descriptor of the field that was used as the sourceId (i.e. how can i find the id in the source dataset.)..) Even though this descriptor can be any arbitrary String, it is good practice to use the format "_DiscourseDbEntity_#_idIdentifier_". That is, the descriptor identifies the type of entity that the source relates to and the identifier of the source id in the dataset. For example, if you create a contribution from a row in a database identified by the primary key post.id, a good value for the _entitySourceDescriptor_ would be _contribution#post.id_. If the same row in the dataset is also supposed to be associated with a DiscourseDB content, then the corresponding descriptor would be _content#post.id_
- The _sourceType_ identifies the category of the data source (e.g. EDX, Wikipedia, PROSOLO).)
- The _dataSetName_ identifies the particular file or database from which the data was imported.

#### User, Audience, Group
TBA
#### Interactions
TBA
#### Annotation Subsystem
Annotations attach to almost every entity in the database. They have been designed as a general purpose way of tagging and labeling entities. 
An annotation can either refer to an entity as a whole (entity annotation) or to a particular span within the text field of a Content entity. The latter resembles the stand-off annotations in [UIMA](https://uima.apache.org/d/uimaj-current/index.html). An AnnotationInstance has an AnnotationType and a set of features associated with it. The AnnotationType identifies what the Annoation is about while the Featuers provide additional information that is necessary to make sense of the annotation.

For example, a part of speech tagger might tokenize the text in a Content entity and produce a set of annotation of the AnnotationType TOKEN. Each of these annotations then has a Feature of the FeatureType POS associated with it the value of which identifies the part of speech of the given token.

### High Level Overview: A step-by-step Example
Assume we want to represent the interactions in a simple discussion forum that is part of an online course. This discussion forum consists of a number of tree-like discussion threads. There are no other sub-spaces such as sub-forums. We assume that the forum posts cannot be edited once they have been posted.

The forum represents a distinct discussion space within the realm of the online course. Therefore, the online course is represented as an DiscourseDB _Discourse_ entity while the forum itself constitutes a _DiscoursePart_. Other discussion spaces, such as a chat would constitute separate DiscourseParts within the same Discourse.<br/>
**STEP 1:** ```DiscourseService#createOrGetDiscourse(String name)``` creates a new Discourse entity or retrieves an existing one with the same name. In case another dataset from the same course have already been imported before, the same Discourse entity should be used.

Since the forum is not organized in several subforums, we only require a single DiscoursePart. Otherwise, we could have represented each subforum as a DiscoursePart which are all related to a parent DiscoursePart with _DiscoursePartRelations_. This way, we can aggregate multiple sub-spaces to a single discussion space within the discourse.<br/>
**STEP 2**: ```DiscoursePartService#createOrGetTypedDiscoursePart(Discourse discourse, DiscoursePartTypes type)``` creates a DiscoursePart, relates it to the provided DiscoursePartType (here: FORUM) and links it to the provided discourse.

Once Discourse and DiscoursePart(s) are created, we can import the forum posts. Each Post translates to a DiscourseDB _Contribution_ which contains the meta information about the post such as creation date and number of up/downvotes or likes. The content of the post, i.e. the text, is not directly stored in the Contribution entity, but in a _Content_ entity. The Contribution related to a Content by means of the _current revision_ and _first revision_ relation. In our case, both relations would point to the same Content entity since we are not keeping track of textual edits. If changes in the texts are supposed to be tracked, multiple content entities would a linked list to represent the revision history and the two pointers in the associated Contribution point to the head and the tail of that list. The Contribution needs to have a ContributionType that representes its role in the discourse. In our case, we assign the type THREAD_STARTER for thread starter posts and POST for all other posts.<br/>
**STEP 3**: ```ContributionService#createTypedContribution(ContributionTypes type)``` creates a new Contribution with the given type (```ContributionTypes.THREAD_STARTER``` or ```ContributionTypes.POST```). ```ContentService#createContent()``` creates a new Content entity. Fill in the Contribution and Content details using the corresponding setters and then attached the content entity as first and last revision of the contribution using ```contribution.setFirstRevision(content)``` and ```contribution.setLastRevision(content)```

For many reasons, we might want to keep track of where the data for the newly created contribution came from. For this reason, we can link back to the data source and identify where in the data source the data can be found. This is done with a DataSourceInstance entity that can be associated with all _entities with source information_ (see [below](https://github.com/DiscourseDB/discoursedb-model#entity-classes-the-discoursedb-core-model))<br/>
**STEP 4:**: A DataSource can be added to a contribution entity like this: 		```dataSourceService.addSource(CONTRIBUTION_ENTITY, new DataSourceInstance(SOURCE_ID,SOURCE_ID_DESCRIPTOR,SOURCE_TYPE, DATA_SET_NAME))``` with <i>CONTRIBUTION_ENTITY</i> referring to the contribution object, <i>SOURCE_ID</i> defining the id of the cotribution in the dataset, SOURCE_ID_DESCRIPTOR defining where this id could be found (e.g. field name of the id), <i>SOURCE_TYPE</i>(optional) defining the type of data source (e.g. DataSourceTypes.EDX) and <i>DATA_SET_NAME</i> defining the dataset that is being imported (e.g. filename, database version, etc). Adding a data source to the content works similarly.
	
The User entity associated with the Content indicates the author of the Contribution. <br/>
**STEP 5:**: A User can be created and linked to a content entity like this: 	```UserService#createOrGetUser(Discourse discourse, String username)```. In most cases, you want to keep track of the source of the user. To automatically associate a user with a DataSourceIntance upon creation, user ```UserService#createOrGetUser(Discourse discourse, String username, String sourceId, String sourceIdDescriptor, DataSourceTypes dataSourceType, String dataSetName)```. The additional fields correspond to the fields in the DataSourceInstance description above. Once the user is created, ```content.setAuthor(user)``` establishes a relations between the user and the content they created.

At this point, the contributions and the immediately related entities are fully set up. As a final stept the contribution needs to be associated with the DiscoursePart we set up in the beginning.<br/>
**STEP 6:**: A contribution can be associated with a DiscoursePart using ```DiscoursePartService#addContributionToDiscoursePart(Contribution contrib, DiscoursePart part)```

At this point all contributions, users etc are successfully imported, but we still need to establish relationships between contributions to represent the reply structure of the forum. This can be achieved by establishing _DiscoursRelation_ entities that connect pairs of Contribution entities. Since these entities need to reference existing Contribution entities, we need to assure that the Contributions are in the database before creating the relations. This can e.g. be done by adding relations in a second iteration after importing all contributions.<br/>
**STEP 7:**: A DiscourseRelation between two Contributions can be created using ```DiscourseRelationService#createDiscourseRelation(Contribution sourceContribution, Contribution targetContribution, DiscourseRelationTypes type)```. In our forum example, a Contribution _reply_ that replies to a Cotribution _parent_ would be linked with ```discourseRelationService.createDiscourseRelation(parent, reply, DiscourseRelationTypes,REPLY)```.

## DiscourseDB Core Components

### Entity Classes: The DiscourseDB Core Model
The entity classes are POJOs that define the DiscourseDB Core model. They are annotated with Hibernate 4 ORM annotations that allow hibernate to dynamically create and update the database schema from these classes.

DiscourseDB defines five categories of entities

- **Type entities** extend ```BaseTypeEntity``` which adds version, creation date and type identifier fields to the entity.
- **Untimed entities** extend ```UntimedBaseEntity``` and are the same as type entities but without the type identifier.
- **Timed, annotatable entities** extend ```TimedBaseEntity``` and are the same as untimed entities, but they keep track of the entity lifespan with a start and end date and they can be annotated.
- **Untimed entities with source information** extend ```UntimedBaseEntityWithSource``` and are the same as untimed entities, but they also keep track of what source they were imported from and how they can be identified in that source.
- **Timed, annotatable entities with source information** extend ```TimedBaseEntityWithSource``` and are the same as timed annotable entities, but they also keep track of what source they were imported from and how they can be identified in that source.

### Spring Data Repositories
TBA

### Spring Service Components
Spring Service Components offer provide a higher level of abstraction for data access. Rather than directly manipulating entities using the CRUD and custom repository methods, Services encapsulate whole processes and further allow to perform additional consistency and validity checks. Beyond that, they allow to define complex queries using [QueryDSL-JPA](http://www.querydsl.com/).

The following example shows how to use service-level methods that operate on multiple repositories.

```java
@Component
public class ExampleComponent{
  @Autowired
  private DiscourseService discourseService;
  
  @Autowired
  private DiscoursePartService discoursePartService;
  
  public void dummyMethod(){
 	 Discourse discourse = discourseService.createOrGetDiscourse("DUMMYDISCOURSE");
  	DiscoursePart courseForum = discoursePartService.createOrGetTypedDiscoursePart(discourse,"DUMMYDISCOURSE_FORUM",DiscoursePartTypes.FORUM);
  }
}
```
The call of the first service-level method internally checks whether a Discourse exists and retrieves it if it exists or creates it if it doesn't.
The second service call creates a new DiscoursePart, retrieves or creates a DiscoursePartType and connects it with that DiscoursePart. It then establishes a relation relation between the DiscoursePart and the given Discourse.

### QueryDSL
We have seen that service-level classes can access multiple Spring Data repositories and therefore wrap more complex processes in single methods.
Beyond that, Spring service may also contain even more complex queries using QueryDSL abstraction.

The following example shows how  to define a query in a service class using QueryDSL.

```java
@Service
public class ExampleService{

	@Autowired
	private UserRepository userRepository;
    
    public Iterable<User> findUsersBySourceId(String sourceId) {
        return userRepo.findAll(
       		QUser.user.dataSourceAggregate.sources.any().entitySourceId.eq(sourceId)
        );
    }
}
```
The findUsersBySourceId() method retrieves all User entities that have an associated DataSourceInstance which contains the provided sourceId. The QUser class is autogenerated by QueryDSL. (DiscourseDB uses Maven to to autogenerate QueryDSL classes for all entity classes) The Predicate (the argument of the findAll() method) is usually stored in a separate Predicate class so it can be re-used in multiple queries.

## Additional Documentation
### Latest JavaDoc
The JavaDoc of the latest build can be found [here](http://moon.lti.cs.cmu.edu:8080/job/DiscourseDB/edu.cmu.cs.lti$discoursedb-model/javadoc/)

### Informal Description of the Data Model
[Chris Bogart](http://quetzal.bogarthome.net/) compiled an [informal description](https://github.com/DiscourseDB/discoursedb-model/raw/master/informal_model_description.pdf) of main DiscourseDB entities.
