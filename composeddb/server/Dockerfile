FROM openjdk:8

# Install maven
RUN apt-get update
RUN apt-get install -y maven netcat

RUN git clone -b dockerized https://github.com/discoursedb/discoursedb-core.git /usr/src/discoursedb
WORKDIR /usr/src/discoursedb

RUN mvn install
RUN mvn dependency:copy-dependencies
WORKDIR /usr/src/discoursedb/discoursedb-api-rest
COPY custom.properties.docker target/classes/custom.properties
COPY localhost_keystore.p12 /usr/src/discoursedb/discoursedb-api-rest/src/main/resources/localhost_keystore.p12
COPY wait-for .
RUN chmod 777 wait-for
RUN mvn install
RUN mkdir /bratdata /lightdata

RUN groupadd -r bratuser
RUN useradd -g bratuser -ms /bin/bash bratuser
RUN chown -R bratuser:bratuser /bratdata

VOLUME /bratdata

EXPOSE 5280

USER bratuser
CMD ["sh","-c","while ! nc -z db 3306; do echo sleeping; sleep 1; done; java -cp  discoursedb-api-rest-0.8-SNAPSHOT.jar:target/classes:target/dependency/* edu.cmu.cs.lti.discoursedb.api.DiscourseApiStarter"]
