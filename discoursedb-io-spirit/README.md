# discoursedb-io-spirit

## Data Source

* [Spirit](https://github.com/CloudComputingCourse/Spirit) is a Django application that is installed as part of TPZ.
  * The `tpz.spirit_*` tables contain the forum data

## Importing Forum Data

* Configuring TPZ and DiscourseDB access
  * Chris Bogart can proivde access to the `discoursedb_ext_cloud_computing_s18` database in DiscourseDB. These credentials can be configured in [custom.properties](https://github.com/CloudComputingCourse/discoursedb-core/blob/master/discoursedb-io-spirit/src/main/resources/custom.properties#L10).
  * `spirit.jdbc.*` in [custom.properties](https://github.com/CloudComputingCourse/discoursedb-core/blob/master/discoursedb-io-spirit/src/main/resources/custom.properties#L36) should be configured with a MySQL user that has access to `tpz.spirit_*`

### Import

* `Usage: SpiritConverterApplication <DataSetName> <DiscourseName>`
* [SpiritConverter](https://github.com/CloudComputingCourse/discoursedb-core/blob/master/discoursedb-io-spirit/src/main/java/edu/cmu/cs/lti/discoursedb/io/spirit/converter/SpiritConverter.java#L33) is the entrypoint for the command line application.
  * DataSetName - This is unique per semester (e.g. `f18-cloud-computing-spirit`)
  * DiscourseName - This is same per semester, but should be different for new discourse data sets (e.g. `scs-cloud-computing`) 

* Importing the forum data via the CLI
 
    ```
    mvn spring-boot:run -Drun.arguments="f18-cloud-computing-spirit,scs-cloud-computing" -Djdbc.database=discoursedb_ext_cloud_computing_f18
    ```
