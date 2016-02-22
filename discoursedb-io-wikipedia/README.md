# discoursedb-io-wikipedia
DiscourseDB converters for Wikipedia data

## Data Mapping

## Artifact Mappings
| Artifact Number | Source Artifact  | DiscourseDB Entity | DiscourseDB Type |Comments |
| ------------- | ------------- | ------------- | ------------- | ------------- |
| 1  | Talk Page |  DiscoursePart | TALK_PAGE | Article title -> DiscoursePart.name | 
| 2  | Discussion | DiscoursePart | THREAD | Thread title -> DiscoursePart.name  | 
| 3a  | Turn | Contribution | THREAD_STARTER | if first turn in thread  |
| 3b  | Turn | Contribution | POST | if not first turn in thread  |
| 3c  | Turn | Content | n/a |   |
| 4a  | Article | Context | ARTICLE | 
| 4b  | Article | Content | n/a | 

## Artifact Data Sources
The Wikipedia artifacts that are mapped to DiscourseDB are extracted with an automatic segmentation algorithm. They do not have idenfiers in the original data source. Therefore, we generate ids for each entity using existing identifiers such as revision ids and thread titles.

Known limitation: Discussions on a Talk page are identified by their title. If two or more discussions on the same talk page have identical titles , they will map to the same identifier.

| Artifact Number | Source Id | Source Descriptor Enum | Source Descriptor | 
| ------------- | ------------- | ------------- | ------------- |
| 2  |  "revision id of talk page"\_"title of discussion" |WikipediaTalkPageSourceMapping. DISCUSSION_TITLE_ON_TALK_PAGE_TO_DISCOURSEPART | "discoursePart#talkPageRevision\_discussionTitle" | 
| 3a/b  |  "revision id of talk page"\_"title of discussion"\_"turn number"  |WikipediaTalkPageSourceMapping. TURN_NUMBER_IN_DISCUSSION_TO_CONTRIBUTION| "contribution#talkPageRevision\_discussionTitle\_turnNumber" |  
| 3c  |  "revision id of talk page"\_"title of discussion"\_"turn number"  |WikipediaTalkPageSourceMapping. TURN_NUMBER_IN_DISCUSSION_TO_CONTENT| "contribution#talkPageRevision\_discussionTitle\_turnNumber" |  

## Generated Entities
| DiscourseDB Entity | Description |
| ------------- | ------------- |
| Discourse | Manually defined scope. Dataset Type is WIKIPEDIA. Dataset name and discourse name are assigned at converter startup startup via parameters. |
| User | User information is extracted from the Wikipedia revision history. The username is the Wikipedia login if the user was registered, the IP if the user was not registerd and ANONMYOUS if no data was available. All extracted content entities have a user assigned to them. |


## Relation Mappings