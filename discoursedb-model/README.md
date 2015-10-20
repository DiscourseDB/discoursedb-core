# DiscourseDB Core Model
This project contains the core object model for DiscourseDB which both defines the database schema and constitutes as an access layer to the the database. discoursedb-model is based on the [Spring Framework](http://projects.spring.io/spring-framework/) and [Spring Data JPA](http://projects.spring.io/spring-data-jpa/) with [Hibernate ORM](http://hibernate.org/orm/) as its JPA Provider. Query abstraction is provided by [QueryDSL-JPA](http://www.querydsl.com/).

## Additional Documentation
### Latest JavaDoc
The JavaDoc of the latest build can be found [here](http://moon.lti.cs.cmu.edu:8080/job/DiscourseDB/edu.cmu.cs.lti$discoursedb-model/javadoc/)

### Informal Description of the Data Model
[Chris Bogart](http://quetzal.bogarthome.net/) compiled an [informal description](https://github.com/DiscourseDB/discoursedb-model/raw/master/informal_model_description.pdf) of main DiscourseDB entities.

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

## DiscourseDB Model Architecture and Components
### Architecture Overview
TBA. For now, please refer to [this informal overview of the main entities](https://github.com/DiscourseDB/discoursedb-model/raw/master/informal_model_description.pdf).

### DiscourseDB Configuration
DiscourseDB is centrally configured using a [Java-based container configuration](http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-java) (see Spring docs for more details). The default configuration is provided by the [BaseConfiguration](https://github.com/DiscourseDB/discoursedb-model/blob/master/src/main/java/edu/cmu/cs/lti/discoursedb/configuration/BaseConfiguration.java) class which can be replaced by custom configuration (just exclude the BaseConfiguration in your project. 

Parameters that most likely need to be changed (i.e. databse credentials) are read from the hibernate.properties file. In most cases, the BaseConfiguration doesn't need to be accessed. The BaseConfiguration uses uses [c3p0](http://www.mchange.com/projects/c3p0/) for connection pooling.

### Entity Classes: The DiscourseDB Core Model
The entity classes define the DiscourseDB Core model. They are annotated with ORM annotations that allow hibernate to dynamically create and update the database schema from these classes.

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
