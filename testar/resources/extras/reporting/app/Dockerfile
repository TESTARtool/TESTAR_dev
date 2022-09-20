# Stage 1
FROM node:latest as node
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build --prod #> /tmp/build.log

# CMD npm install; npm run build --prod
# Stage 2
FROM nginx:alpine
COPY default.conf /etc/nginx/conf.d/default.conf
#COPY --from=node /tmp /tmp
COPY --from=node /app/dist/testar-frontend /usr/share/nginx/html
