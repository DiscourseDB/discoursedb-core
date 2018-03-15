FROM nginx

# Install maven
RUN apt-get update
RUN apt-get install -y git netcat

RUN git clone -b username-password-login https://github.com/discoursedb/discoursedb-browser.git /usr/share/nginx/html/discoursedb
WORKDIR /usr/share/nginx/html/discoursedb
RUN echo 'var baseUrl = "https://server:5280";\nvar auths = ["basic"];\n' > config.js

COPY nginx.conf.docker /etc/nginx/sites-enabled/default
COPY nginx.conf /etc/nginx/nginx.conf
COPY cert.pem /etc/nginx/
COPY key.pem /etc/nginx/
#CMD ["sh","-c","while ! nc -z server 5280; do echo sleeping; sleep 1; done"]

