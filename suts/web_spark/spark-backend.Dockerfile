FROM node:20-alpine

WORKDIR /usr/src/app

RUN apk add --no-cache git

ARG BACKEND_REPO
ARG BACKEND_BRANCH

# Bring the docker build context into the image to be able to check for local files
COPY . /tmp/build-context

# Clone backend repo into spark-server-backend folder
RUN git clone -b ${BACKEND_BRANCH} ${BACKEND_REPO} spark-server-backend

# Patch Bootstrap.ts from local Bootstrap-testar-autolink.ts, or fail with message
RUN if [ ! -f /tmp/build-context/Bootstrap-testar-autolink.ts ]; then \
      echo "ERROR: Bootstrap-testar-autolink.ts not found in the docker build context." >&2; \
      echo "Place Bootstrap-testar-autolink.ts in the directory you run 'docker build' from (or within that build context) and rebuild." >&2; \
      exit 1; \
    fi && \
    cp /tmp/build-context/Bootstrap-testar-autolink.ts /usr/src/app/spark-server-backend/src/repository/Bootstrap.ts

# Patch index.ts from local index-testar-autolink.ts, or fail with message
RUN if [ ! -f /tmp/build-context/index-testar-autolink.ts ]; then \
      echo "ERROR: index-testar-autolink.ts not found in the docker build context." >&2; \
      echo "Place index-testar-autolink.ts in the directory you run 'docker build' from (or within that build context) and rebuild." >&2; \
      exit 1; \
    fi && \
    cp /tmp/build-context/index-testar-autolink.ts /usr/src/app/spark-server-backend/src/service/index.ts

# Patch TestFixtureService.ts from local TestFixtureService-testar-autolink.ts, or fail with message
RUN if [ ! -f /tmp/build-context/TestFixtureService-testar-autolink.ts ]; then \
      echo "ERROR: TestFixtureService-testar-autolink.ts not found in the docker build context." >&2; \
      echo "Place TestFixtureService-testar-autolink.ts in the directory you run 'docker build' from (or within that build context) and rebuild." >&2; \
      exit 1; \
    fi && \
    cp /tmp/build-context/TestFixtureService-testar-autolink.ts /usr/src/app/spark-server-backend/src/service/TestFixtureService.ts

WORKDIR /usr/src/app/spark-server-backend

# Remove local .env so container env vars are used
RUN rm -f *.env

RUN npm install && npm run build

EXPOSE 80

ENV NODE_ENV=test

CMD ["npm", "run", "start-sw"]
