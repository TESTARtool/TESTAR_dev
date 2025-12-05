FROM node:20-alpine

WORKDIR /usr/src/app

# Add git dependency
RUN apk add --no-cache git

##############
# SPARK CORE #
##############

ARG CORE_REPO
ARG CORE_BRANCH

# Clone core into its own folder and build it
RUN git clone -b ${CORE_BRANCH} ${CORE_REPO} spark-core

WORKDIR /usr/src/app/spark-core
RUN rm -f package-lock.json \
 && npm install \
 && npm run build

###################
# SPARK DASHBOARD #
###################

WORKDIR /usr/src/app

ARG DASHBOARD_REPO
ARG DASHBOARD_BRANCH
ARG SERVER_API_URL

# Clone dashboard into its own folder
RUN git clone -b ${DASHBOARD_BRANCH} ${DASHBOARD_REPO} spark-dashboard

WORKDIR /usr/src/app/spark-dashboard

# delete lock file
RUN rm -f package-lock.json

# Remove local .env so container env vars are used
RUN rm -f *.env

# Create the config.json file
RUN printf '{\n  "SERVER_URL": "${SERVER_API_URL}",\n  "DEFAULT_LANGUAGE": "en-US"\n}\n' > config.json

# Install deps
RUN npm install \
 && npm install react@17.0.2 react-dom@17.0.2 \
 && npm install @mui/system@^5.18.0 @mui/material@^5.18.0 @mui/icons-material@^5.18.0 \
 && npm install --save-dev @types/react@17 @types/react-dom@17 http-server

# Patch buildEnv.js to always use SERVER_URL
RUN sed -i 's@`REACT_APP_API_URL=.*@                `REACT_APP_API_URL=${config.SERVER_URL}`,@' buildEnv.js

###########
# RUNTIME #
###########

WORKDIR /usr/src/app/spark-dashboard

EXPOSE 3000

# Dev server accessible from outside the container
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0", "--port", "3000"]
