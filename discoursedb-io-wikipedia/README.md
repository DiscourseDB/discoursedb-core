# discoursedb-io-wikipedia
DiscourseDB converters for Wikipedia data

## Mapping

## Artifact Mappings
| Number | Source Artifact  | DiscourseDB Entity | DiscourseDB Type |Comments |
| ------------- | ------------- | ------------- | ------------- | ------------- |
| 1  | Talk Page |  DiscoursePart | TALK_PAGE | Article title -> DiscoursePart.name | 
| 2  | Discussion | DiscoursePart | THREAD | Thread title -> DiscoursePart.name  | 
| 3a  | Turn | Contribution | THREAD_STARTER | First turn in thread.  |
| 3b  | Turn | Contribution | POST | Any turn after first turn in thread.  |
| 3c  | Turn | Content |  |   |
| 4a  | Article | Context | ARTICLE | 
| 4b  | Article | Content |  | 

## Data Sources
| Artifact Number | Source Id | Source Descriptor Enum | Source Descriptor | 
| ------------- | ------------- | ------------- | ------------- |
| 2  |  "revision id of talk page"\_"title of discussion" |WikipediaTalkPageSourceMapping. DISCUSSION_TITLE_ON_TALK_PAGE_TO_DISCOURSEPART | "discoursePart#talkPageRevision\_discussionTitle" | 
| 3  |  "revision id of talk page"\_"title of discussion"\_"turn number"  |WikipediaTalkPageSourceMapping. TURN_NUMBER_IN_DISCUSSION_TO_CONTRIBUTION| "contribution#talkPageRevision\_discussionTitle\_turnNumber" |  
| 3  |  "revision id of talk page"\_"title of discussion"\_"turn number"  |WikipediaTalkPageSourceMapping. TURN_NUMBER_IN_DISCUSSION_TO_CONTENT| "contribution#talkPageRevision\_discussionTitle\_turnNumber" |  

## Relation Mappings