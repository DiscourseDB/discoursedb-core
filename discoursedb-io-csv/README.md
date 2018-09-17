# DiscourseDB general-purpose data importer

Import a single CSV file into its own discoursedb database

This is a general-purpose data importer; export data from some other
source into a CSV file with one row per human text contribution.
All fields except "post" are optional, and will be expanded out to
create appropriate DiscourseDB entitites.

Not all of DiscourseDB's structures can currently be populated
from this file; if you need some field or DB entity to be created,
submit an issue requesting it (or a PR implementing it!) and we'll
add it.

# CSV format 
uses the default settings of Python 2.7's csv module:
Use unix-style line endings, separate fields by commas, and enclose
fields in double-quote characters (") if the field contains double-quote,
comma, carriage return, or linefeed.  Double quotes characters inside
a field are escaped by doubling them (He says he's a "doctor" => 
"He says he's a ""doctor""")

## Fields:
<dl>
<dt>id
<dd>A unique identifier for the text comment.  It can be any string,
and it's best if it's some unique database index field or GUID used in your original data
(If omitted, the row number in the CSV file will be used)
<dt>username
<dd>Who said it (string identifying the user)
(if omitted, "unknown" will be used)
<dt>user_email
<dd>Username's email, if present; username and username_email should have
a 1-1 relationship (the same username should always be associated with
the same email).  Blank if omitted
<dt>
<dt>when
<dd>Time and date in ISO-8601 format.  If omitted, the time of import
time will be used for the first row, and a second will be added
for each subsequent row -- this just ensures that sorting by time will
keep the file in order.
<dt>title
<dd>A title for the post.  If it has no title, leave blank.  If
it's in a thread that has a title, put the thread title for every post
in the thread.
<dt>post
<dd>The text itself.  Required!
<dt>replyto
<dd>If this is a reply to previous comment, use id of that
comment, otherwise leave blank.  If omitted, each row is considered
a reply to the previous row IFF the forum field is the same.
<dt>forum
<dd>Invent a hierarchy to store forums, threads, conversations,
etc, and represent the path to a particular discussion area as
strings separated by /.  If omitted, the name of the CSV
file will be used as the forum name.
<dt>discourse
<dd>This string represents the name for the universe of conversations
that some community has.  For example when representing various conversations
within an online learning platform, this might be the name of a course and
the term it was offered.  This is a scientific category, as opposed to
dataset_name (see below) which is a data-management category.  If omitted,
the name of the CSV file will be used.
<dt>dataset_name
<dd>A single nickname for the set of exported files you're
drawing from to build this CSV file.  Often the same as a discourse,
but could include multiple discourses (if you're importing multiple courses
in from one big dump), or a discourse could cross multiple dataset_names,
(if you're importing a discourse in multiple pieces, e.g. from
repeated data releases over the course of a semester).  If omitted,
the name of the CSV file will be used.
<dt>dataset_file
<dd>If the dataset you are drawing on to build this CSV file 
contains multiple files, this helps point to the
provenance of each row -- what file, originally, did this come from?
The dataset_name, dataset_file, dataset_id_schema, and id together can
uniquely identify a data item from its original source, so if you import
the same data item twice, you won't get two records in discoursedb.
If omitted, the name of the CSV file will be used.
<dt>dataset_id_schema
<dd>The name of the column or field in
dataset_file where the id field was drawn from.  If omitted, the
string "row" will be used.
<dt>annotation_XYZ
<dd>Add an annotation to the contribution (the post) with the name
of XYZ, and containing the value in the column.  If no such columns
exist, no annotations will be added.
<dt>annotation_owner
<dd>The email address of the DiscourseDB user who will own the
annotation -- i.e. will have the right to read, alter or delete it.  Note
this user is a researcher with access to DiscourseDB, not a user
in the dataset.  If left blank, the annotation will be unowned;
everyone will be able to read it, and no one will be able to change it.  
(This is useful for annotations
that are meant to be part of the raw data, as opposed to researcher-owned
annotations that are an analytical interpretation of the data)
</dd>

# Running the import

Set up the custom.properties file with jdbc.host, jdbc.username, jdbc.password, jdbc.system_database fields.
Then run CsvImportApplication with the csv file path as the first argument., then --jdbc.database=<DB NAME> 

```
java -cp discoursedb-io-csv-0.9-SNAPSHOT.jar:target/classes:target/dependency/* edu.cmu.cs.lti.discoursedb.io.csvimporter.CsvImportApplication <CSV FILE PATH> --jdbc.database=<DB NAME>
```

java -cp discoursedb-io-csv-0.9-SNAPSHOT.jar:target/classes:target/dependency/* edu.cmu.cs.lti.discoursedb.io.csvimporter.CsvImportApplication /tmp/tmp.csv --jdbc.host=db --jdbc.username=root --jdbc.password=smoot --jdbc.database=$2.

To make this database visible to users, use the user-management project to either make it visible to the public, or to grant access to particular users.

```
cd ../user-management
bash manage register <DB NAME>
bash manage grant public <DB NAME>
bash maange grant researcher@college.edu <DB NAME>
```
