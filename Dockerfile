FROM selenium/standalone-chrome

RUN sudo apt-get -o Acquire::Check-Valid-Until=false -o Acquire::Check-Date=false update
RUN sudo apt-get install -y openjdk-17-jdk libxkbcommon-x11-0

ADD testar/target/distributions/testar.tar .

ENV JAVA_HOME "/usr/lib/jvm/java-17-openjdk-amd64"
ENV DISPLAY=":99"

COPY runImage /runImage
COPY README.Docker /README.Docker
RUN sudo chmod 777 /runImage
RUN sudo chmod -R 777 /testar

CMD [ "sh", "/runImage"]

