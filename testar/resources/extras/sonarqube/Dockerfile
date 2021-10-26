FROM sonarqube:8.2-community
RUN apt-get update && apt-get install nodejs npm -y && npm install -g typescript
COPY sonar-project.properties /opt/sonarqube/conf/
