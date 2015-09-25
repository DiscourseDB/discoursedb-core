# DiscourseDB Core Model
This project contains the core object model for DiscourseDB which both defines the database schema and constitutes as an access layer to the the database. discoursedb-model is based on the [Spring Framework](http://projects.spring.io/spring-framework/) and [Spring Data JPA](http://projects.spring.io/spring-data-jpa/) with [Hibernate ORM](http://hibernate.org/orm/) as its JPA Provider. Query abstraction is provided by [QueryDSL-JPA](http://www.querydsl.com/).

## Requirements and Setup
All DiscourseDB projects require Java 8 and Maven 3.

To import the project into eclipse, simply follow the following steps (Steps 3 and 4 are only necessary the first time you import a Maven project from git):

```
- Select the "Import..." context menu from the Package Explorer view
- Select "Check out Maven projects from SCM" option under the Maven category
- On the window that is presented choose the link "Find more SCM connectors"
- Find connector for Git...install...restart
```

Like all DiscourseDB projects, this project depends on the [discoursedb-parent](https://github.com/DiscourseDB/discoursedb-parent) project. You need to check out the parent project for the model project to work. We are currently working on setting up an artifactory that serves these repositories automatically so Maven can pull in the dependencies as libraries without you having to check them out.

DiscourseDB requires write access to a MySQL database. The access credentials are defined in the [hibernate.properties](https://raw.githubusercontent.com/DiscourseDB/discoursedb-model/master/discoursedb-model/src/main/resources/hibernate.properties). The standard configuration expects a local MySQL server running on port 3306 and a user with the login credentials user:user and sufficient permissions. The standard database name is discoursedb. Edit the properties file to change these parameters. DiscourseDB will automatically initialize a fresh DiscourseDB instance if none exists yet. Otherwise, it will import data into the existing database.

## DiscourseDB Model Architecture and Components
### Architecture Overview
### DiscourseDB Configuration
### Entity Classes: The DiscourseDB Core Model
The entity classes define the DiscourseDB Core model. They are annotated with ORM annotations that allow hibernate to dynamically create and update the database schema from these classes.

DiscourseDB defines five categories of entities

- **Type entities** extend ```BaseTypeEntity``` which adds version, creation date and type identifier fields to the entity.
- **Untimed entities** extend ```UntimedBaseEntity``` and are the same as type entities but without the type identifier.
- **Timed, annotatable entities** extend ```TimedBaseEntity``` and are the same as untimed entities, but they keep track of the entity lifespan with a start and end date and they can be annotated.
- **Untimed entities with source information** extend ```UntimedBaseEntityWithSource``` and are the same as untimed entities, but they also keep track of what source they were imported from and how they can be identified in that source.
- **Timed, annotatable entities with source information** extend ```TimedBaseEntityWithSource``` and are the same as timed annotable entities, but they also keep track of what source they were imported from and how they can be identified in that source.


### Spring Data Repositories
### Spring Service Components
Spring Service Components offer provide a higher level of abstraction for data access. Rather than directly manipulating entities using the CRUD and custom repository methods, Services encapsulate whole processes and further allow to perform additional consistency and validity checks. Beyond that, they allow to define complex queries using [QueryDSL-JPA](http://www.querydsl.com/).

The following example shows how to use a service-level method that operates on multiple repositories.

```java
@Autowired
private DiscourseService discourseService;

@Autowired
private DiscoursePartService discoursePartService;

public void dummyMethod(){
Discourse discourse = discourseService.createOrGetDiscourse("DUMMYDISCOURSE");
DiscoursePart courseForum = discoursePartService.createOrGetTypedDiscoursePart(discourse,"DUMMYDISCOURSE_FORUM",DiscoursePartTypes.FORUM);
}
```
The first service internally checks whether a Discourse exists and retrieves it if it exists or creates it if it doesn't.
The second service call creates a new DiscoursePart, retrieves or creates a DiscoursePartType and connects it with that DiscoursePart. It then establishes a relation relation between the DiscoursePart and the given Discourse.

### QueryDSL
We have seen that service-level classes can access multiple Spring Data repositories and therefore wrap more complex processes in single methods.
Beyond that, Spring service may also contain even more complex queries using QueryDSL abstraction.

The following example shows how  to define a query in a service class using QueryDSL.

```java
@Service
public class DemoService{

	@Autowired
	private UserRepository userRepository;

	private static Predicate userHasSourceId(String sourceId) {
		if (sourceId == null || sourceId.isEmpty()) {
			return QUser.user.isNull();
		} else {
			return QUser.user.dataSourceAggregate.sources.any().entitySourceId.eq(sourceId);
		}
	}
    
    public Iterable<User> findUsersBySourceId(String sourceId) {
        return userRepo.findAll(UserPredicates.userHasSourceId(sourceId));
    }
}
```

The findUsersBySourceId()-method retrieves all User entities that have an associated DataSourceInstance which contains the provided sourceId.


### Entity Type Definitions
