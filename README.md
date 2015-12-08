# DiscourseDB Parent Project
This is the parent project for all DiscourseDB projects. It manages dependency versions and configures necessary Maven plugins.

## Build and Run DiscourseDB
The following command builds all DiscourseDB projects, creates .jar files and creates a ```*-dist``` folder in the target folder of each DiscourseDB module which contains all external libraries necessary to run that module. 

```
mvn clean install -Passemble
```

It is then possible to run a particular class in any DiscourseDB module by calling the following command in a terminal (in this example, we call the edX Forum converter)

```
java -cp "PATH/TO/MODULE/discoursedb-io-edx-0.5-SNAPSHOT.jar:PATH/TO/MODULE/discoursedb-io-edx-0.5-SNAPSHOT-dist/*" edu.cmu.cs.lti.discoursedb.io.edx.forum.converter.EdxForumConverterApplication utarlingtonx-2015-03-22 /path/to/forum.json /path/to/usermapping.tsv
```

## Database Server
DiscourseDB requires a database server. The BaseConfiguration is configured for MySQL, but you can use other relations DBMS and adapt the configuration accordingly. The following instruction will assume a MySQL setup.

DiscourseDB is configured to create a new database in case the database provided in the configuration does not exist. The database will be created with the default character encoding defined in the server configuration. We recommend either to (1) manually create an empty database the database with UTF8 encoding and have DiscourseDB use this database or (2) change the configuration of MySQL to use UTF8 by default so newly created databases will use this encoding.

(1) ```CREATE DATABASE `discoursedb` CHARACTER SET utf8 COLLATE utf8_general_ci;```

or

(2) in my.cnf, add the following configuration
```character-set-server=utf8
collation-server=utf8_general_ci
```
