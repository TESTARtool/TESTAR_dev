FROM maven:3.8.5-openjdk-11 AS builder
COPY . /user/src/app
WORKDIR /user/src/app
RUN mvn clean install

FROM tomcat:9.0
COPY --from=builder /user/src/app/target/api.war /usr/local/tomcat/webapps
CMD catalina.sh run
