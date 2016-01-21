# Web UI for DiscourseDB annotation

This user interface showcases the capabilities of DiscourseDB's annotation subsystem and allows a user to retrieve data and annotate it and save it back to DiscourseDB.

## Project Setup
The project uses the [Frontend Maven Plugin](https://github.com/eirslett/frontend-maven-plugin) to to manage and obtain frontend libraries. The frontend libraries and their versions are defined in [package.json](https://github.com/DiscourseDB/discoursedb-annotation-ui/blob/master/src/main/resources/static/package.json) and will be automatically downloaded by maven using [npm](https://www.npmjs.com/).

The UI is based on [ReactJS](https://facebook.github.io/react/) and connects to DiscourseDB via [Spring Data REST](http://projects.spring.io/spring-data-rest/).

In case the ```npm install``` phase of the project build fails, try to empty your ~/.npm folder. This triggers a redownload of libraries which should fix the issue.

The basic setup of this UI is based on the [Spring REACT+REST tutorial](https://spring.io/guides/tutorials/react-and-spring-data-rest/). 
