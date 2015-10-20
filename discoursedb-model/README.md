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

The forum represents a distinct discussion space within the realm of the online course. Therefore, the online course is represented as an DiscourseDB _Discourse_ entity while the forum itself constitutes a _DiscoursePart_. Other discussion spaces, such as a chat would constitute separate DiscourseParts within the same Discourse. 
**STEP 1: CREATE A DISCOURSE ENTITY TO REPRESENT THE BROADER CONTEXT IN WHICH AN INTERACTION HAPPEND (here: a course)**

Since the forum is not organized in several subforums, we only require a single DiscoursePart. Otherwise, we could have represented each subforum as a DiscoursePart which are all related to a parent DiscoursePart with _DiscoursePartRelations_. This way, we can aggregate multiple sub-spaces to a single discussion space within the discourse.

**STEP 2**: ```DiscoursePartService.createOrGetTypedDiscoursePart(Discourse discourse, DiscoursePartTypes type)``` creates a DiscoursePart, relates it to the provided DiscoursePartType (here: FORUM) and links it to the provided discourse.

Once Discourse and DiscoursePart(s) are created, we can import the forum posts. Each Post translates to a DiscourseDB _Contribution_ which contains the meta information about the post such as creation date and number of up/downvotes or likes. The content of the post, i.e. the text, is not directly stored in the Contribution entity, but in a _Content_ entity. The Contribution related to a Content by means of the _current revision_ and _first revision_ relation. In our case, both relations would point to the same Content entity since we are not keeping track of textual edits. If changes in the texts are supposed to be tracked, multiple content entities would a linked list to represent the revision history and the two pointers in the associated Contribution point to the head and the tail of that list. The User entity associated with the Content indicates the author of the Contribution.

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
