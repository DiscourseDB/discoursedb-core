
Instructions for setting up a DiscourseDB server

     * Install Docker
          Docker (www.docker.com) is a service that lets you run many lightweight
          virtual machines.  
     * cd composeddb (this directory!)
     * Edit openssl.cnf to reflect your own organization's identity/location.  The 
          details aren't very important if you're just running this for internal use.
          If you want to do proper https security, you'll need to apply for a certificate
          from your institution.  Instructions about that to come....
     * Run "bash mkcerts"
          This makes key and cert files for two of the images (server and browser) so
          that your discoursedb instance can use https security
     * Edit db password
          The password "smoot" is in docker-compose.yml and in a few files in
          server/custom.properties.*  For better security, change this to something
          else for your installation.
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
     * bash manage add <email> <realname> <username> <password>
          Set up a user for yourself, filling in the brackets as appropriate.  Don't use
          spaces in the names; so if your name is Diogenes Laertius, use
          Diogenes_Laertius.  The username and realname are just useful metadata;
          what matters is the email and password, which you'll use to log in
     * visit https://localhost/discoursedb/index.html in your browser, and log in
          with the credentials you've just created. 
       WARNING: the first time you do this, you'll get a security warning, and you'll
          need to let your browser know this made-up certificate you generated
          (with bash mkcerts) is OK.  that's different in different browsers.  After
          the first time, it should remember.  Eventually you'll want to apply for
          a certificate officially through your institution. 

     
Some useful commands:

Show discourseDB users
   bash manage list users

Show discourseDB databases
   bash manage list users

Add a researcher to access discoursedb (email, real name, arbitrary username, password):
   (This is just an example -- use a real email and name, and invent a username and password)
   add plato@academy.athens.gr "Plato Aristocles" plato kruptos123

Add a discourseDB database called crito_dialogue  (just an example: you'll need your own data here):
   docker exec composeddb_server_1 "sh" "-c" "mkdir /tmp/data/"
   docker cp crito_dialogue.txt composeddb_server_1:/tmp/data/
   docker exec composeddb_server_1 "sh" "-c" "cd /usr/src/discoursedb/discoursedb-io-simplecsv; java -cp <importer jarfile>:target/classes:target/dependency/* <importer's main class> --jdbc.database=xyz <other arguments -- see importer code>"
   bash manage register crito_dialogue
   bash manage grant plato@academy.athens.gr crito_dialogue 

Make crito_dialogue database readable by anyone (continuing from example above!):
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
   mysql --host=composeddb_db_1 --port=3306 -uroot -psmoot

To access the database from *outside* the containers:
   mysql --host=localhost -P 8083 --protocol=tcp -uroot -psmoot

To browse discoursedb:
   visit https://localhost:8082/discoursedb/index.html in your browser

