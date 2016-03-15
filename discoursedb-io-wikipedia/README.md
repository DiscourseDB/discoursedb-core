# DiscourseDB converter for Wikipedia Talk pages

## Input Data Definition and Launch Parameters
This section defines the input for this converter and the startup parameters.

### Data Source
This converter uses [JWPL 1.1.0-SNAPSHOT](https://dkpro.github.io/dkpro-jwpl/) to import Wikipedia data. It requires a JWPL database created with JWPL 1.0.0 or later. The converter requires both the JWPL Core representation as well as the revision history created by the JWPL RevisionMachine. Documentation for how to create a new JWPL Core database and adding revision data with the RevisionMachine can be found on the [JWPL website](https://dkpro.github.io/dkpro-jwpl/).

### Importing Wikipedia Discussions
The import is launched using the class<br/> ```edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.converter.WikipediaTalkPageConverterApplication```<br/>

It requires the following startup parameters:<br/>
```WikipediaTalkPageConverterApplication  <DiscourseName> <DataSetName> <tileListFile> <DB_HOST> <DB> <DB_USER> <DB_PWD> <LANGUAGE>```<br/>
with <br/>

- **DiscourseName** = the name of the discourse the imported discussion should be associated with
- **DataSetName** = a name for the dataset (e.g. the JWPL database or the list of title) that is imported
- **tileListFile** = the location of a text file containing a line-separated list of Wikipedia article titles
- **DB_HOST** = host of the JWPL database
- **DB** = name of the JWPL database
- **DB_USER** = database username
- **DB_PWD** = database password
- **LANGUAGE** = language of the Wikipedia version to be imported

### Importing Discussion Context
After the import of discussions, it is possible to optionally import the articles associated with the imported discussion.
Articles are represented as context and associated with all contributions that have been written on the respective article talk page. The revision history of the article is represented as multiple content entities.

The import is launched using the class<br/> ```edu.cmu.cs.lti.discoursedb.io.wikipedia.article.converter.WikipediaContextArticleConverterApplication```
<br/>
It requires the following startup parameters:<br/>
```WikipediaContextArticleConverterApplication <DB_HOST> <DB> <DB_USER> <DB_PWD> <LANGUAGE>```
with <br/>
- **DB_HOST** = host of the JWPL database
- **DB** = name of the JWPL database
- **DB_USER** = database username
- **DB_PWD** = database password
- **LANGUAGE** = language of the Wikipedia version to be imported


## Data Mapping
This section outlines how artifacts from the Wikipedia source dataset are mapped to DiscourseDB entities, how the original source artifacts can be identified using DiscourseDB data sources, how the entities relate to each other and what additional entities are generated during the mapping process.

### Visual Overview
<img src="https://raw.githubusercontent.com/DiscourseDB/discoursedb-core/master/discoursedb-io-wikipedia/norelations.jpg"/>
<table width="100%" border="0">
  <tr border="0">
    <td align="center"><img src="https://raw.githubusercontent.com/DiscourseDB/discoursedb-core/master/discoursedb-io-wikipedia/allrelations.jpg"/><p>All Relations</p></td>
    <td align="center"> <img src="https://raw.githubusercontent.com/DiscourseDB/discoursedb-core/master/discoursedb-io-wikipedia/contextrelations.jpg"/><p>Context Relations</p></td>
    <td align="center"><img src="https://raw.githubusercontent.com/DiscourseDB/discoursedb-core/master/discoursedb-io-wikipedia/contentrelations.jpg"/><p>Content Relations</p></td>
 <td align="center"><img src="https://raw.githubusercontent.com/DiscourseDB/discoursedb-core/master/discoursedb-io-wikipedia/dpcontribrelations.jpg"/><p>DiscoursePart Relations</p></tr>
  </tr>
</table>
### Artifact Mappings
| Artifact Number | Source Artifact  | DiscourseDB Entity | DiscourseDB Type |Comments |
| ------------- | ------------- | ------------- | ------------- | ------------- |
| 1  | Talk Page |  DiscoursePart | TALK_PAGE | Article title -> DiscoursePart.name | 
| 2  | Discussion | DiscoursePart | THREAD | Thread title -> DiscoursePart.name  | 
| 3a  | Turn | Contribution | THREAD_STARTER | if first turn in thread  |
| 3b  | Turn | Contribution | POST | if not first turn in thread  |
| 3c  | Turn | Content | n/a | first and last revision of 3a/b |
| 4a  | Article | Contribution | ARTICLE | represents the article context related to the TalkPage. Every 3a/b artifact from the same Talk page has the same 4a artifact as context.|
| 4b  | Article | Content (multiple) | n/a|each article revision is translated to a content entity. 4a points to the oldes and newest of these revisions as firstRevision and currentRevision | 

### Artifact Data Sources
The Wikipedia artifacts that are mapped to DiscourseDB are extracted with an automatic segmentation algorithm. They do not have idenfiers in the original data source. Therefore, we generate ids for each entity using existing identifiers such as revision ids and thread titles.

Known limitation: Discussions on a Talk page are identified by their title. If two or more discussions on the same talk page have identical titles , they will map to the same identifier.

| Artifact Number | Source Id | Source Descriptor Enum | Source Descriptor | 
| ------------- | ------------- | ------------- | ------------- |
| 2  |  "revision id of talk page"\_"title of discussion" |WikipediaTalkPageSourceMapping. DISCUSSION_TITLE_ON_TALK_PAGE_TO_DISCOURSEPART | "discoursePart#talkPageRevision\_discussionTitle" | 
| 3a/b  |  "revision id of talk page"\_"title of discussion"\_"turn number"  |WikipediaTalkPageSourceMapping. TURN_NUMBER_IN_DISCUSSION_TO_CONTRIBUTION| "contribution#talkPageRevision\_discussionTitle\_turnNumber" |  
| 3c  |  "revision id of talk page"\_"title of discussion"\_"turn number"  |WikipediaTalkPageSourceMapping. TURN_NUMBER_IN_DISCUSSION_TO_CONTENT| "contribution#talkPageRevision\_discussionTitle\_turnNumber" |  

### Relation Mappings

| DiscourseDB Relation | Relaton Type | Source artifact number |Target artifact number | Comments |
| ------------- | ------------- | ------------- | ------------- |------------- |
|DiscourseRelation|DESCENDANT| 3a | 3b | All posts are related to their thread starter.|
|DiscoursePartRelation|TALK_PAGE_HAS_DISCUSSION| 1 | 2 | DiscourseParts representing discussions are part of DiscourseParts representing a Talk page.|
|ContributionContext|n/a| 3a/b | 4a | Contributions that represent turns are related to Contributions that represents articles that constitute the context of the turns.|

### Other Generated Entities
The following entities are created during the mapping process, but don't map to an explicit artifact in the data source.

| DiscourseDB Entity | Description |
| ------------- | ------------- |
| Discourse | Manually defined scope. Dataset Type is WIKIPEDIA. Dataset name and discourse name are assigned at converter startup startup via parameters. |
| User | User information is extracted from the Wikipedia revision history. The username is the Wikipedia login if the user was registered, the IP if the user was not registerd and ANONMYOUS if no data was available. All extracted content entities have a user assigned to them. |
