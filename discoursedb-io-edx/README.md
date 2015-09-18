# DiscourseDB IO edX
This project provides converters for edX data dumps. Right now, discoursedb-io-edx can import discussion forum data into a DiscourseDB instance. Support for other parts of the edX data dumps will be added to this project in the future. Converters for other sources than edX will have their own discoursedb-io-* projects.

## Requirements
All DiscourseDB components require Java 8 and Maven 3.

## DiscourseDB

## Discussion Forum Import Architecture
All DiscourseDB-IO projects are [Spring Boot Applications](http://projects.spring.io/spring-boot/). Spring Boot is a Spring project that makes it easy to create stand-alone Spring based applications with a minimum of configuration. DiscourseDB-IO components implement the [CommandLineRunner](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-command-line-runner) interface, which allows the few configuration parameters (i.e. location of the data dumps that should be imported) to be passed to the converters in a terminal. The necessary DiscourseDB configurations are automatically pulled in from the [discoursedb-model](https://github.com/DiscourseDB/discoursedb-model) project which contains the DiscourseDB core components.

The import of edX forum data into DiscourseDB is broken down in three phases.

### Phase 1

### Phase 2

### Phase 3
