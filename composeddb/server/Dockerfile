FROM openjdk:8

# Install maven
RUN apt-get update
RUN apt-get install -y maven netcat

#RUN git clone -b dockerized https://github.com/discoursedb/discoursedb-core.git /usr/src/discoursedb
WORKDIR /usr/src/discoursedb
COPY . ./

RUN mvn clean
RUN mvn install
RUN mvn dependency:copy-dependencies
WORKDIR /usr/src/discoursedb/discoursedb-api-rest
COPY composeddb/server/custom.properties.user.management ../custom.properties.temp
RUN find .. -name custom.properties -exec cp ../custom.properties.temp {} \;
RUN mvn install
RUN mvn dependency:copy-dependencies
COPY composeddb/server/custom.properties.docker target/classes/custom.properties
COPY composeddb/server/localhost_keystore.p12 /usr/src/discoursedb/discoursedb-api-rest/src/main/resources/localhost_keystore.p12
COPY composeddb/server/wait-for .
WORKDIR /usr/src/discoursedb/user-management
RUN mvn install
RUN mvn dependency:copy-dependencies
WORKDIR /usr/src/discoursedb/discoursedb-api-rest
RUN chmod 777 wait-for
RUN mkdir /bratdata /lightdata

RUN groupadd -r bratuser
RUN useradd -g bratuser -ms /bin/bash bratuser
RUN chown -R bratuser:bratuser /bratdata
RUN chown -R bratuser:bratuser /lightdata

VOLUME /bratdata

EXPOSE 5280

USER bratuser
CMD ["sh","-c","while ! nc -z db 3306; do echo sleeping; sleep 1; done; java -cp  discoursedb-api-rest-0.9-SNAPSHOT.jar:target/classes:target/dependency/* edu.cmu.cs.lti.discoursedb.api.DiscourseApiStarter"]
