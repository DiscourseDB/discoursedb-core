# discoursedb-io-wikipedia
DiscourseDB converters for Wikipedia data

## Mapping

## Artifact Mappings
| Number | Source Artifact  | DiscourseDB Entity | DiscourseDB Type |Comments |
| ------------- | ------------- | ------------- | ------------- | ------------- |
| 1  | Talk Page |  DiscoursePart | TALK_PAGE | Article title -> DiscoursePart.name | 
| 2  | Discussion | DiscoursePart | THREAD | Thread title -> DiscoursePart.name  | 
| 3a  | Turn | Contribution | THREAD_STARTER | if first turn in thread  |
| 3b  | Turn | Contribution | POST | if not first turn in thread  |
| 3c  | Turn | Content | n/a |   |
| 4a  | Article | Context | ARTICLE | 
| 4b  | Article | Content | n/a | 

## Data Sources
The Wikipedia artifacts that are mapped to DiscourseDB are extracted with an automatic segmentation algorithm. They do not have idenfiers in the original data source. Therefore, we generate ids for each entity.

Known limitation: Discussions on a Talk page are identified by their title. If multiple discussions have identical titles on the same talk page, they will map to the same identifier.

| Artifact Number | Source Id | Source Descriptor Enum | Source Descriptor | 
| ------------- | ------------- | ------------- | ------------- |
| 2  |  "revision id of talk page"\_"title of discussion" |WikipediaTalkPageSourceMapping. DISCUSSION_TITLE_ON_TALK_PAGE_TO_DISCOURSEPART | "discoursePart#talkPageRevision\_discussionTitle" | 
| 3  |  "revision id of talk page"\_"title of discussion"\_"turn number"  |WikipediaTalkPageSourceMapping. TURN_NUMBER_IN_DISCUSSION_TO_CONTRIBUTION| "contribution#talkPageRevision\_discussionTitle\_turnNumber" |  
| 3  |  "revision id of talk page"\_"title of discussion"\_"turn number"  |WikipediaTalkPageSourceMapping. TURN_NUMBER_IN_DISCUSSION_TO_CONTENT| "contribution#talkPageRevision\_discussionTitle\_turnNumber" |  

## Relation Mappings