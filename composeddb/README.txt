
Setup instructions:
   Go to https://console.developers.google.com/apis/credentials and set up a new credential.
     * Click the key icon
     * Click "Create credentials"
     * Choose "Oauth Client Id"
     * choose "web application"
     * Name it
     * Put the base URL of your site into "Authorized javascript origins"
     * Get the google client secret and id off the top of this page
     * fill them in the custom.properties.docker file as:
           google.client_secret= <secret>
           google.client_id= <id>
           google.registered_url = http://127.0.0.1:5980
     * docker-compose build --no-cache
     * docker-compose up
     * mysql --host=localhost -P 8083 --protocol=tcp -uroot -proot < openfl.sql
     * visit https://localhost:8082/discoursedb/index.html in your browser
     
Help
   docker-compose help

To rebuild from scratch:
   docker-compose build --no-cache

To run:
   docker-compose up -d

To show the containers running:
   docker-compose ps   

Rebuild and restart a container
   docker-compose up -d --build browser

To jump into one of the containers:
   docker exec -it composeddb_browser_1 bash

To access the database from *inside* one of the containers:
   docker exec -it composeddb_browser_1 bash
   # apt-get install mysql-client
   mysql --host=composeddb_db_1 --port=3306 -uroot -proot

To access the database from *outside* the containers:
   mysql --host=localhost -P 8083 --protocol=tcp -uroot -proot

To browse discoursedb:
   visit https://localhost:8082/discoursedb/index.html in your browser



Things to consider:
 https://en.abnerchou.me/Blog/75027340/
 https://stackoverflow.com/questions/47869317/docker-compose-nginx-ssl-reverse-proxy
 https://runnable.com/docker/rails/docker-container-linking
 https://cloud.google.com/community/tutorials/nginx-reverse-proxy-docker
