# DiscourseDB Core Model
This project contains the core object model for DiscourseDB which both defines the database schema and constitutes as an access layer to the the database. discoursedb-model is based on the [Spring Framework](http://projects.spring.io/spring-framework/) and [Spring Data JPA](http://projects.spring.io/spring-data-jpa/) with [Hibernate ORM](http://hibernate.org/orm/) as its JPA Provider. Query abstraction is provided by [QueryDSL-JPA](http://www.querydsl.com/).

## Requirements and Setup
All DiscourseDB projects require Java 8 and Maven 3.

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

Like all DiscourseDB projects, this project depends on the [discoursedb-parent](https://github.com/DiscourseDB/discoursedb-parent) project. You need to check out the parent project for the model project to work and/or add the Artifactory configuration above to you settings.xml, so that Maven can pull in the artifacts automatically.

DiscourseDB requires write access to a MySQL database. The access credentials are defined in the [hibernate.properties](https://raw.githubusercontent.com/DiscourseDB/discoursedb-model/master/discoursedb-model/src/main/resources/hibernate.properties). The standard configuration expects a local MySQL server running on port 3306 and a user with the login credentials user:user and sufficient permissions. The standard database name is discoursedb. Edit the properties file to change these parameters. DiscourseDB will automatically initialize a fresh DiscourseDB instance if none exists yet. Otherwise, it will import data into the existing database.

## DiscourseDB Model Architecture Overview
### High Level Overview: An Example
Assume we want to represent interactions in a simple discussion forum that is part of an online course in DiscourseDB. This discussion forum consists of a number of tree-like discussion threads. There are no other sub-spaces like sub-forums. We assume that the forum posts cannot be edited once they have been posted.

The forum represents a distinct discussion space within the realm of the online course. Therefore, the online course is represented as an DiscourseDB _Discourse_ entity while the forum itself constitutes a _DiscoursePart_. Other discussion spaces, such as a chat would constitute separate DiscourseParts within the same Discourse.<br/>
**STEP 1:** ```DiscourseService.createOrGetDiscourse(String name)``` creates a new Discourse entity or retrieves an existing one with the same name. In case another dataset from the same course has already been imported, the same Discourse entity should be used.

Since the forum is not organized in several subforums, we only require a single DiscoursePart. Otherwise, we could have represented each subforum as a DiscoursePart which are all related to a parent DiscoursePart with _DiscoursePartRelations_. This way, we can aggregate multiple sub-spaces to a single discussion space within the discourse.<br/>
**STEP 2**: ```DiscoursePartService.createOrGetTypedDiscoursePart(Discourse discourse, DiscoursePartTypes type)``` creates a DiscoursePart, relates it to the provided DiscoursePartType (here: FORUM) and links it to the provided discourse.

Once Discourse and DiscoursePart(s) are created, we can import the forum posts. Each Post translates to a DiscourseDB _Contribution_ which contains the meta information about the post such as creation date and number of up/downvotes or likes. The content of the post, i.e. the text, is not directly stored in the Contribution entity, but in a _Content_ entity. The Contribution related to a Content by means of the _current revision_ and _first revision_ relation. In our case, both relations would point to the same Content entity since we are not keeping track of textual edits. If changes in the texts are supposed to be tracked, multiple content entities would a linked list to represent the revision history and the two pointers in the associated Contribution point to the head and the tail of that list. The Contribution needs to have a ContributionType that representes its role in the discourse. In our case, we assign the type THREAD_STARTER for thread starter posts and POST for all other posts.<br/>
**STEP 3**: ```contributionService.createTypedContribution(ContributionTypes type)``` creates a new Contribution with the given type (ContributionTypes.THREAD_STARTER or ContributionTypes.POST). ```contentService.createContent()``` creates a new Content entity. Fill in the Contribution and Content details using the corresponding setters and then set the content as first and last revision using ```contribution.setFirstRevision(content)``` and ```contribution.setLastRevision(content)```

For many reasons, we might want to keep track of where the data for the newly created contribution came from. For this reason, we can link back to the data source and identify where in the data source the data can be found. This is done with a DataSourceInstance entity that can be associated with all _entities with source information_ (see [below](https://github.com/DiscourseDB/discoursedb-model#entity-classes-the-discoursedb-core-model))<br/>
**STEP 4:**: A DataSource can be added to a contribution entity like this: 		```dataSourceService.addSource(CONTRIBUTION_ENTITY, new DataSourceInstance(SOURCE_ID,SOURCE_ID_DESCRIPTOR,SOURCE_TYPE, DATA_SET_NAME))``` with <i>CONTRIBUTION_ENTITY</i> referring to the contribution object, <i>SOURCE_ID</i> defining the id of the cotribution in the dataset, SOURCE_ID_DESCRIPTOR defining where this id could be found (e.g. field name of the id), <i>SOURCE_TYPE</i>(optional) defining the type of data source (e.g. DataSourceTypes.EDX) and <i>DATA_SET_NAME</i> defining the dataset that is being imported (e.g. filename, database version, etc). Adding a data source to the content works similarly.
	
The User entity associated with the Content indicates the author of the Contribution. <br/>
**STEP 5:**: A User can be created and linked to a content entity like this: 	```userService.createOrGetUser(Discourse discourse, String username)```. In most cases, you want to keep track of the source of the user. To automatically associate a user with a DataSourceIntance upon creation, user ```createOrGetUser(Discourse discourse, String username, String sourceId, String sourceIdDescriptor, DataSourceTypes dataSourceType, String dataSetName)```. The additional fields correspond to the fields in the DataSourceInstance description above. Once the user is created, ```content.setAuthor(user)``` establishes a relations between the user and the content they created.

At this point, the contributions and the immediately related entities are fully set up. As a final stept the contribution needs to be associated with the DiscoursePart we set up in the beginning.
**STEP 6:**: A contribution can be associated with a DiscoursPart using ```discoursePartService.addContributionToDiscoursePart(Contribution contrib, DiscoursePart part)```

At this point all contributions, users etc are successfully imported, but we still need to establish relationships between contributions to represent the reply structure of the forum. This can be achieved by establishing _DiscoursRelation_ entities that connect pairs of Contribution entities. Since these entities need to reference existing Contribution entities, we need to assure that the Contributions are in the database before creating the relations. This can e.g. be done by adding relations in a second iteration after importing all contributions.<br/>
**STEP 7:**: A DiscourseRelation between two Contributions can be created using ```discourseRelationService.createDiscourseRelation(Contribution sourceContribution, Contribution targetContribution, DiscourseRelationTypes type)```. A Contribution _reply_ that replies to a Cotribution _parent_ would be linked by ```discourseRelationService.createDiscourseRelation(parent, reply, DiscourseRelationTypes,REPLY)```.



### Main Entities 
Please also refer to [this informal overview of the main entities](https://github.com/DiscourseDB/discoursedb-model/raw/master/informal_model_description.pdf).

#### Discourse
#### DiscoursePart
#### Contribution
#### Content
#### User, Audience, Group
#### Context
#### User Interactions
#### Annotations

## DiscourseDB Core Components
### DiscourseDB Configuration
DiscourseDB is centrally configured using a [Java-based container configuration](http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-java) (see Spring docs for more details). The default configuration is provided by the [BaseConfiguration](https://github.com/DiscourseDB/discoursedb-model/blob/master/src/main/java/edu/cmu/cs/lti/discoursedb/configuration/BaseConfiguration.java) class which can be replaced by custom configuration (just exclude the BaseConfiguration in your project. 

Parameters that most likely need to be changed (i.e. databse credentials) are read from the hibernate.properties file. In most cases, the BaseConfiguration doesn't need to be accessed. The BaseConfiguration uses [c3p0](http://www.mchange.com/projects/c3p0/) for connection pooling.

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
