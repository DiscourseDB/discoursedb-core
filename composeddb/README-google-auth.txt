
To enable Google ID
   Go to https://console.developers.google.com/apis/credentials and set up a new credential.
     * Click the key icon
     * Click "Create credentials"
     * Choose "Oauth Client Id"
     * choose "web application"
     * Name it (pick any name you want, e.g. "DiscourseDB")
     * Put the base URL of your site into "Authorized javascript origins"
          (you need to know at this point what URL you'll be using)
     * Get the google client secret and id off the top of this page
   In the server configuration:
     * fill them in the custom.properties.docker file as:
           google.client_secret= <secret>
           google.client_id= <id>
           google.registered_url = http://127.0.0.1:5980
     * Rebuild the server
   In the browser configuration:
     * Find the file "config.js"
     * Make sure it has this line: var auths = ["basic","google"];
