FROM nginx:stable-alpine

COPY css /usr/share/nginx/html/css
COPY js /usr/share/nginx/html/js
COPY report.html /usr/share/nginx/html/index.html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
