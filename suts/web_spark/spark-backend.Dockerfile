FROM node:20-alpine

WORKDIR /usr/src/app

RUN apk add --no-cache git

ARG BACKEND_REPO
ARG BACKEND_BRANCH

# Clone backend repo into spark-server-backend folder
RUN git clone -b ${BACKEND_BRANCH} ${BACKEND_REPO} spark-server-backend

WORKDIR /usr/src/app/spark-server-backend

# Remove local .env so container env vars are used
RUN rm -f *.env

RUN npm install && npm run build

EXPOSE 80

CMD ["npm", "run", "start-sw"]
