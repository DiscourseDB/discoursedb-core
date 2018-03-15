FROM nginx

# Install maven
RUN apt-get update
RUN apt-get install -y git netcat

RUN git clone -b username-password-login https://github.com/discoursedb/discoursedb-browser.git /usr/share/nginx/html/discoursedb
WORKDIR /usr/share/nginx/html/discoursedb
RUN echo 'var baseUrl = "https://localhost:5280";\nvar auths = ["basic"];\n' > config.js

COPY nginx.conf.docker /etc/nginx/sites-enabled/default
COPY nginx.conf /etc/nginx/nginx.conf
COPY openssl.cnf /etc/nginx/
WORKDIR /etc/nginx/
RUN openssl req -x509 -nodes -days 3650 -newkey rsa:2048 -keyout key.pem -out cert.pem -config openssl.cnf
CMD ["sh","-c","while ! nc -z server 5280; do echo sleeping; sleep 1; done"]

