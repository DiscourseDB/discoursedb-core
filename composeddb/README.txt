
Instructions for setting up a DiscourseDB server

     * Install Docker
          Docker (www.docker.com) is a service that lets you run many lightweight
          virtual machines.  
     * cd composeddb (this directory!)
     * Edit openssl.cnf to reflect your own organization's identity/location.  The 
          details aren't very important if you're just running this for internal use.
          If you want to do proper https security, you'll need to apply for a certificate
          from your institution.  Instructions about that to come....
     * Run mkcerts
          This makes key and cert files for two of the images (server and browser) so
          that your discoursedb instance can use https security
     * docker-compose build --no-cache
          Builds several docker images, which are mini virtual machines that will
          communicate with each other to run your discoursedb server
     * sudo docker-compose up -d
          This runs the images and connects them together.  You must run as root in order
          to serve discoursedb on port 443, the standard https port.  If you don't 
          have root access, change the server and browser configuration to use a different
          port (e.g. 8443)
     * bash import
          Imports a sample database for you and gives permission
     * bash manage add <email> '<real name>' <username> <password>
          Set up a user for yourself, filling in the brackets as appropraite.
     * visit https://localhost/discoursedb/index.html in your browser, and log in

     
Some useful commands:

Show discourseDB users
   bash manage list users

Show discourseDB databases
   bash manage list users

Add a researcher to access discoursedb (email, real name, arbitrary username, password):
   add plato@academy.athens.gr "Plato Aristocles" plato kruptos123

Add a discourseDB database called crito_dialogue:
   docker exec composeddb_server_1 "sh" "-c" "mkdir /tmp/data/"
   docker cp crito_dialogue.txt composeddb_server_1:/tmp/data/
   docker exec composeddb_server_1 "sh" "-c" "cd /usr/src/discoursedb/discoursedb-io-simplecsv; java -cp <importer jarfile>:target/classes:target/dependency/* <importer's main class> --jdbc.database=xyz <other arguments -- see importer code>"
   bash manage register crito_dialogue
   bash manage grant plato@academy.athens.gr crito_dialogue 

Make crito_dialogue database readable by anyone:
   bash manage grant public crito_dialogue

Show discourseDB users
   bash manage list users

Docker Help
   docker-compose help
   docker help

To rebuild from scratch:
   docker-compose build --no-cache

To run DiscourseDB server:
   docker-compose up -d

To show the docker containers running:
   docker-compose ps   

Rebuild and restart a docker container
   docker-compose up -d --build browser

To get a command line into one of the docker containers:
   docker exec -it composeddb_browser_1 bash

To access the database from *inside* one of the containers:
   docker exec -it composeddb_browser_1 bash
   apt-get install mysql-client
   mysql --host=composeddb_db_1 --port=3306 -uroot -proot

To access the database from *outside* the containers:
   mysql --host=localhost -P 8083 --protocol=tcp -uroot -proot

To browse discoursedb:
   visit https://localhost:8082/discoursedb/index.html in your browser

