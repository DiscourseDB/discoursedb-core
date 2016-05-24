# DiscourseDB converter for Twitter

## Input Data Definition and Launch Parameters
This section defines the input for this converter and the startup parameters.

### Data Source
This converter imports Twitter data into DiscourseDB. Source data are stored in csv files. Each line in a csv source file stores all the information (e.g. text, author, id) of a message posted in Twitter platform. For this converter, a source message could be a tweet, retweet or reply. 

### Importing Tweet Data
The import is launched using the class<br/> ```edu.cmu.cs.lti.discoursedb.io.tags.converter.TweetConverterApplication```<br/>

It requires the following startup parameters:<br/>
```TweetConverterApplication  <DiscourseName> <DataSetName> <TweetFilePath>```<br/>
with <br/>

- **DiscourseName** = the name of discourse that the imported Twitter data should be associated with
- **DataSetName** = the name for the dataset (e.g. course01) that is imported
- **RoomFilePath** = the location of a csv file that contains source Twitter data


## Data Mapping
This section outlines how tweets in the source csv file are mapped to DiscourseDB entities, how the original source artifacts can be identified using DiscourseDB data sources, how the entities relate to each other and what additional entities are generated during the mapping process.


### Artifact Mappings
| Artifact Number | Source Artifact | DiscourseDB Entity | DiscourseDB Type | Comments |
| ------------- | ------------- | ------------- | ------------- | ------------- |
| 1a | Twitter_Message | Contribution | Tweet | A retweet or reply |
| 1b | Tweet_Content | Content | n/a| Content of 1a. Content of retweet contains content of the original tweet. |
| 2 |  | Twitter_User | User | Author of a Twitter message. Twitter username -> User.username Twitter language -> User.language |


### Artifact Data Sources
Each Twitter_Message has an unique id in the original source files. Therefore, we combine the original id and certain source descriptor as unique identifier for each entity.

Note: Twitter users in source file don't have their unique id. Therefore, we combine the user name and certain source descriptor as unique identifier for each user entity.

| Artifact Number | Source Id | Source Descriptor Enum | Source Descriptor | 
| ------------- | ------------- | ------------- | ------------- |
| 1a |  "tweet\_message\_id" | TweetSourceMapping.ID\_STR\_TO\_CONTRIBUTION | "contribution#id_str" |
| 1b | "tweet\_message\_id" |TweetSourceMapping.ID\_STR\_TO\_CONTENT | "content#id\_str" |
| 2 | "username" | TweetSourceMapping.FROM_USER_ID_STR_TO_USER | "user#from_user_id_str" |

### Relation Mappings

There are two types of relation between Twitter messages that are mapped to DiscourseDB. One is reply and another is retweet. There is explicit information in the csv file indicating whether a message is a reply or a posted tweet. But there is no information indicating whether a tweet is an original one or a retweet. Therefore, we determine whether a Twitter message is a retweet by examing whether there is "RT @username" in the text of the message.

| DiscourseDB Relation | Relaton Type | Source artifact number |Target artifact number | Comments |
| ------------- | ------------- | ------------- | ------------- |------------- |
| DiscourseRelation | REPLY | 1a | 1a | A message replies to a tweet. |
| DiscourseRelation | RESHARE | 1a | 1a | A message retweets a tweet. |


### Other Generated Entities
The following entity are created during the mapping process, but isn't mapped to an explicit artifact in the data source.

| DiscourseDB Entity | Description |
| ------------- | ------------- |
| Discourse | Manually defined scope. Dataset name and discourse name are assigned at converter startup startup via parameters. |
