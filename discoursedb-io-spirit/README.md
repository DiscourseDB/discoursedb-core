# discoursedb-io-spirit

## Data Source

## Importing Forum Data

* Configuring TPZ and DiscourseDB access
  * Chris Bogart can proivde access to the `discoursedb_ext_cloud_computing_s18` database in DiscourseDB. These credentials can be configured in [custom.properties](https://github.com/CloudComputingCourse/discoursedb-core/blob/master/discoursedb-io-spirit/src/main/resources/custom.properties#L10).
  * [spirit.jdbc.*][custom.properties](https://github.com/CloudComputingCourse/discoursedb-core/blob/master/discoursedb-io-spirit/src/main/resources/custom.properties#L36) should be configured with a MySQL user that has access to `tpz.spirit_*`
