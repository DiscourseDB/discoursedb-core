FROM openjdk:8

# Install maven
RUN apt-get update
RUN apt-get install -y maven

COPY . /usr/src/discoursedb
WORKDIR /usr/src/discoursedb

RUN mvn install
RUN mvn dependency:copy-dependencies
WORKDIR /usr/src/discoursedb/discoursedb-api-rest
RUN mvn install

#cp custom.properties target/classes/custom.properties

CMD ["java","-cp", "discoursedb-api-rest-0.8-SNAPSHOT.jar:target/classes:target/dependency/*", "edu.cmu.cs.lti.discoursedb.api.DiscourseApiStarter"]
