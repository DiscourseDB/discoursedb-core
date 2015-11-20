# DiscourseDB Parent Project
This is the parent project for all DiscourseDB projects. It manages dependency versions and configures necessary Maven plugins.

## Build DiscourseDB
The following command builds all DiscourseDB projects, creates .jar files and creates a ```*-dist``` folder in the target folder of each DiscourseDB module which contains all external libraries necessary to run that module. 

``` mvn clean install -Passemble ```

It is then possible to run a particular class in any DiscourseDB module by calling the following command in a terminal (in this example, we call the edX Forum converter)

```java -cp "PATH/TO/MODULE/discoursedb-io-edx-0.5-SNAPSHOT.jar:PATH/TO/MODULE/discoursedb-io-edx-0.5-SNAPSHOT-dist/*" edu.cmu.cs.lti.discoursedb.io.edx.forum.converter.EdxForumConverterApplication utarlingtonx-2015-03-22 /path/to/forum.json /path/to/usermapping.tsv```




