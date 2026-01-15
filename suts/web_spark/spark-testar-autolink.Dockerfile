# Build the docker image
# docker build -t testar_spark_llm:latest -f spark-testar-autolink.Dockerfile .

# Example or docker run execution for first 1_spark_valid_login.goal
# docker run -d -e OPENAI_API="%OPENAI_API%" -e LLM_TEST_GOAL="/webdriver_b00_spark_llm/1_spark_valid_login.goal" -e SUT_URL="http://localhost:3000" --add-host=host.docker.internal:host-gateway --shm-size=1g --name testar_spark_llm_goal_1_login --mount type=bind,source="C:\testardock\testar_spark_llm\output\goal_1_login",target=/home/seluser/testar_autolink/bin/output --security-opt apparmor=unconfined --security-opt seccomp=unconfined testar_spark_llm:latest

# This selenium docker image already contains webdriver and xvfb
FROM selenium/standalone-chrome

# Update image dependencies
RUN sudo apt-get -o Acquire::Check-Valid-Until=false -o Acquire::Check-Date=false update
RUN sudo apt-get install -y git openjdk-17-jdk libxkbcommon-x11-0 socat

# Clone the TESTAR AUTOLINK project
WORKDIR /home/seluser
RUN git clone https://github.com/TESTARtool/TESTAR_dev.git -b autolink

# Build the TESTAR AUTOLINK project
WORKDIR /home/seluser/TESTAR_dev
RUN chmod +x ./gradlew
RUN ./gradlew clean installDist -Dorg.gradle.java.home=/usr/lib/jvm/java-17-openjdk-amd64

# Move the testar distribution
RUN mv /home/seluser/TESTAR_dev/testar/target/install/testar /home/seluser/testar_autolink
RUN sudo chmod -R 777 /home/seluser/testar_autolink

ENV JAVA_HOME "/usr/lib/jvm/java-17-openjdk-amd64"
ENV DISPLAY=":99"

# Default goal (can be overridden at runtime)
ENV LLM_TEST_GOAL="/webdriver_b00_spark_llm/1_spark_valid_login.goal"

# Default SUT_URL to localhost inside container (served by socat forwarder)
ENV SUT_URL="http://localhost:3000"

WORKDIR /home/seluser/testar_autolink/bin
ENTRYPOINT []
CMD ["bash","-lc", "\
set -e; \
echo '[socat] Forwarding localhost:3000 -> host.docker.internal:3000'; \
socat TCP-LISTEN:3000,fork,reuseaddr TCP:host.docker.internal:3000 & \
SOCAT3000_PID=$!; \
\
echo '[socat] Forwarding localhost:80 -> host.docker.internal:80'; \
sudo socat TCP-LISTEN:80,fork,reuseaddr TCP:host.docker.internal:80 & \
SOCAT80_PID=$!; \
\
trap 'echo \"[socat] stopping\"; kill $SOCAT3000_PID $SOCAT80_PID 2>/dev/null || true; sudo kill $SOCAT443_PID 2>/dev/null || true' EXIT; \
\
xvfb-run ./testar \
  sse=webdriver_b00_spark_llm \
  LlmTestGoals=${LLM_TEST_GOAL} \
  Mode=Generate \
  SUTConnector=WEB_DRIVER \
  SUTConnectorValue=${SUT_URL:-http://localhost:3000} \
  Sequences=${SEQUENCES:-1} \
  SequenceLength=${SEQUENCE_LENGTH:-30} \
  ShowVisualSettingsDialogOnStartup=false \
  VisualizeActions=false \
  FlashFeedback=false \
  ProtocolCompileDirectory=/tmp \
  2>&1;"]

