FROM nginx

# Install maven
RUN apt-get update
RUN apt-get install -y git

RUN git clone https://github.com/discoursedb/discoursedb-browser.git /usr/share/nginx/html/discoursedb
WORKDIR /usr/share/nginx/html/discoursedb
RUN mv index.js index.js1
RUN echo 'var baseUrl = "https://server:5280"\n' > index.js
RUN grep -v "var baseUrl" index.js1 >> index.js
RUN rm index.js1

COPY nginx.conf.docker /etc/nginx/sites-enabled/default
COPY cert.pem /etc/nginx/
COPY key.pem /etc/nginx/

