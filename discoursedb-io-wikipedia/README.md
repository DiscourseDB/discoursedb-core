# discoursedb-io-wikipedia
DiscourseDB converters for Wikipedia data

## Mapping

## Artifact Mappings
| Number | Source Artifact  | Source Id | Source Descriptor | DiscourseDB Entity | DiscourseDB Type |Comments |
| ------------- | ------------- | ------------- | ------------- | ------------- | ------------- | ------------- |
| 1  | Talk Page |  |  | DiscoursePart | TALK_PAGE | Article title -> DiscoursePart.name | 
| 2  | Discussion | "revision id of talk page"_"title of discussion" |  | DiscoursePart | THREAD | Thread title -> DiscoursePart.name  | 
| 3  | Turn |  | "revision id of talk page"_"title of discussion"_"turn number" |  |  |    | 
| 4  | Article |  |  |  |  |    | 

## Relation Mappings