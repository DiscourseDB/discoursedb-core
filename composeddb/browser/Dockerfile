FROM nginx

# Install maven
RUN apt-get update
RUN apt-get install -y git netcat

RUN git clone -b username-password-login https://github.com/discoursedb/discoursedb-browser.git /usr/share/nginx/html/discoursedb
WORKDIR /usr/share/nginx/html/discoursedb
RUN echo 'var baseUrl = "https://localhost/";\nvar auths = ["basic"];\n' > config.js

COPY nginx.conf.docker /etc/nginx/sites-enabled/default
COPY nginx.conf /etc/nginx/nginx.conf
COPY key.pem /etc/nginx
COPY cert.pem /etc/nginx

#CMD ["sh","-c","while ! nc -z server 5280; do echo sleeping; sleep 1; done"]

