# This selenium docker image already contains chromedriver and xvfb
FROM selenium/standalone-chrome

# Update image dependencies and java
RUN sudo apt-get -o Acquire::Check-Valid-Until=false -o Acquire::Check-Date=false update
RUN sudo apt-get install -y openjdk-17-jdk libxkbcommon-x11-0

# To include TESTAR in the docker image, we should have previously compiled testar
# gradlew distTar do this task
ADD testar/target/distributions/testar.tar .

ENV JAVA_HOME "/usr/lib/jvm/java-17-openjdk-amd64"
ENV DISPLAY=":99"

# Prepare the TESTAR files and permissions
COPY runImage /runImage
COPY README.Docker /README.Docker
RUN sudo chmod 777 /runImage
RUN sudo chmod -R 777 /testar

CMD [ "sh", "/runImage"]

