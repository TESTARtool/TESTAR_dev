FROM selenium/standalone-chrome

##FROM ubuntu:20.04
##EXPOSE 5900
##RUN echo Europe/Amsterdam >/etc/timezone
##RUN apt-get update && apt-get install -y curl xvfb chromium-bsu openjdk-14-jdk unzip x11vnc fonts-liberation libcairo2 libgbm1 libgdk-pixbuf2.0-0 libpango-1.0.0 libpangocairo-1.0-0 xdg-utils libgtk-3-0 wget
##RUN curl -SLO "https://chromedriver.storage.googleapis.com/85.0.4183.87/chromedriver_linux64.zip" \
##  && unzip "chromedriver_linux64.zip" -d /usr/local/bin \
##  && rm "chromedriver_linux64.zip"
##RUN apt-get clean

##RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb

##RUN dpkg -i google-chrome-stable_current_amd64.deb

##RUN ln -s /usr/bin/xvfb-ch romium /usr/bin/chromium-browser

# COPY pin_nodesource /etc/apt/preferences.d/nodesource
USER root
RUN apt-get -o Acquire::Check-Valid-Until=false -o Acquire::Check-Date=false update && apt-get install -y openjdk-14-jdk

ADD testar/target/distributions/testar.tar .

ENV JAVA_HOME "/usr/lib/jvm/java-14-openjdk-amd64"
ENV DISPLAY=":99.0"

COPY runImage /runImage
COPY README.Docker /README.Docker
RUN chmod 777 /runImage

CMD [ "sh", "/runImage"]

