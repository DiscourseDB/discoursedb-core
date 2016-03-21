# DiscourseDB Core Model
This project contains the core object model for DiscourseDB which both defines the database schema and constitutes as an access layer to the the database. discoursedb-model is based on the [Spring Framework](http://projects.spring.io/spring-framework/) and [Spring Data JPA](http://projects.spring.io/spring-data-jpa/) with [Hibernate ORM](http://hibernate.org/orm/) as its JPA Provider. Query abstraction is provided by [QueryDSL-JPA](http://www.querydsl.com/).

The JavaDoc of the latest build can be found [here](http://moon.lti.cs.cmu.edu:8080/job/DiscourseDB/edu.cmu.cs.lti$discoursedb-model/javadoc/)

## Requirements and Setup
All DiscourseDB projects require Java 8+ and Maven 3. More information about the setup can be found in the [wiki README](https://github.com/DiscourseDB/discoursedb-core/wiki/General-Info-and-Setup).

## DiscourseDB Model Architecture Overview

### Description of Main Entities 
Please also refer to [this informal overview of the main entities](https://github.com/DiscourseDB/discoursedb-model/raw/master/informal_model_description.pdf) and to the entity class descriptions in the [Javadoc](http://moon.lti.cs.cmu.edu:8080/job/DiscourseDB/edu.cmu.cs.lti$discoursedb-model/javadoc/).

#### Discourse
A Discourse represents the broad context of interactions that might come from multiple datasets. For example, a Discourse could represent an installment of an online course. All interactions in the context of this course - independent from the source dataset - will be associated with this Discourse instance. Another installment of the same course would be represented by a new Discourse instance.
A Discourse is associated to one or more DiscoursePart instances which represent sub-spaces in the realm of the Discourse. That is, an online course with a discussion forum and chat would have two DiscoursePart instances associated with its Discourse instance which represent these two discussion spaces.

#### DiscoursePart
A DiscoursePart represents a distinct sub-space within a Discourse. For instance, a DiscoursePart could represent a discussion forum. That is, it acts as a container for interactions that happen in this discussion forum. DiscourseParts are typed entities, i.e. they are associated with a DiscoursePartType which indicates what the DiscoursePart represents, e.g. a FORUM. Furthermore, DiscourseParts can be related to each other with DiscoursePartRelations in order to indicate embedded structures. For instance, a Forum could consist of multiple sub-forums. DiscoursePartRelations are also typed entities, i.e. they are related to a DiscoursePartRelationType indicating what the relation represents, e.g. an EMBEDDING in the case of forum-subforum.

#### Contribution
A Contribution entity is a representation of a contribution in a discussion space such as a forum post, chat message or equivalent discourse artifact. Contributions only represent meta information about the contribution while the actual content is represented by Content entities (see below). This allows DiscourseDB to capture the revision history of a contribution. Revisions are Content entities that link to their previous and next revision. Thus, the revision history of a contribution is represented by a doubly linked list of Content instances and the Contribution links to the head and the tail of this list. If not revisions are maintained, both pointers link to the same Content entity.

A Contribution is a typed entity, i.e. it is associated with a ContributionType indiciating what the Contribution instance represents, e.g. a POST.

#### Content
Content entities represent the content of Contribution entities. The main payload of a Content entity resides in its text and data field. The content of Contributions usually textual, thus the text field will hold the content of a Contribution. The data field is able to hold arbitrary blobs of data.

Content entities formally represent nodes in a linked list by pointing to a previous and a next content revision. This way, revision histories of Contribution entities can be represented. 

A Content entity is related to a User indicating that this user is the author of the content instance. Other relationships between Users and Content or Contributions can be represented with ContributionInteraction entities (see below).

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
