import sys
import requests
import urllib
import urllib3
from requests.auth import HTTPBasicAuth
import json
import csv

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

config = json.load(open("ddb2kib_config.json"))

class DiscourseDB:
    """Query DiscourseDB's REST interface and extract data.

    This assumes you've set a password on your account; if you are using
    Google to log in, this is *not* your google password, it's a separate
    DiscourseDB password feature.  See project "discoursedb-core/user-management"
    for tools for setting this password; currently only an admin may set
    or change passwords

    Example use is at the bottom of this file.  You must create a named
    selection in the data browser by hand, then this program can download
    the data.    
    """

    def __init__(self, user, password, service):
        self.user= user
        self.password = password
        self.service = service
        self.db = None

    def set_db(self, db=None):
        """Limit interface to a particular database; or set to None to query across databases"""
        self.db = db

    def _upload(self, endpoint, params={}, filename=None, fileparam="file"):
        assert filename is not None, "No upload file specified"
        basic = HTTPBasicAuth(self.user,self.password)
        files = {fileparam: open(filename,"rb")}
        return requests.post("%s/%s" % (self.service, endpoint), data = {}, auth=basic, verify=False, params=params, files=files, stream=True, headers={'Accept-Encoding': 'gzip, deflate, br', "user": "cbogartdenver@gmail.com"}) 
        print("Posted")

    def _request(self, endpoint, params=None, parsejson = True):
        basic = HTTPBasicAuth(self.user,self.password)
        answer = requests.get("%s/%s" % (self.service, endpoint), auth=basic, verify=False, params=params)
        try:
            if parsejson:
                return json.loads(answer.text)
            else:
                return answer.text
        except Exception as e:
            print("Error querying discoursedb: ", e)
            print("    Server returned: ", answer.text)
            return answer.text 

    def list_saved_queries(self):
        url = "browsing/prop_list?ptype=query" 
        self.queries = self._request(url)
        if self.db is None:
            return [entry["propName"] for entry in self.queries]
        else:
            return [entry["propName"] for entry in self.queries 
                  if json.loads(entry["propValue"])["database"] == self.db]

    def query_literal(self, q):
        """Given a query name, returns a string representing the named query"""
        for entry in self.queries:
            if entry["propName"] == q:
                return entry["propValue"]

    def query_content(self, q):
        """Given a query name, returns a data structure representing the named query"""
        return json.loads(self.query_literal(q))

    def dump_query(self, q):
        """Given a query name, pretty-prints the query to stdout"""
        print(json.dumps(self.query_content(q), indent=4))
    
    def download_by_parts(self, query, tofile):
        q = query.copy()
        append = False
        rowcount = 0
        dps = q["rows"]["discourse_part"]
        print("Breaking query into", len(dps),"parts")
        for dp in dps:
           q["rows"]["discourse_part"] = [dp]
           print("Downloading part", json.dumps(dp))
           rc = self.download(q, tofile, append=append)
           rowcount += rc
           print("    Got",rc,"rows, for a total of", rowcount)
           append=True

    def upload_annotated(self, fromfile):
        """Upload the annotated file"""
        return self._upload("browsing/action/database/%s/uploadLightside" % (self.db.replace("discoursedb_ext_",""),),
                       filename=fromfile, fileparam = "file_annotatedFileForUpload")

    def download_for_annotation(self, query, tofile):
        """Run the query and download to a file

        May fail for very large queries, in which case use download_huge.
        If append=True, omit the header line, and append rather than write
        Return the number of rows retrieved"""

        data = self._request("browsing/action/downloadLightsideQuery/for_annotation.csv", 
                       params={"query": json.dumps(query)}, parsejson=False)
        outf = open(tofile, "wb")
        try:
            outf.write(data.encode("utf-8"))
        except Exception as e:
            print(e)
        try:
            return len(list(csv.reader(data.encode("utf-8")))) -1
        except Exception as e:
            print (e)
            return None

    def get_records(self, query):
        """Run the query and return the JSON results"""

        data = self._request("browsing/action/getQueryJson", 
                       params={"query": json.dumps(query)}, parsejson=True)
        return data

if __name__ == "__main__":
    user = "cbogartdenver@gmail.com"   # Your DiscourseDB user id, which is an email address
    password = "123456" # Set this using the github.com/discoursedb-core/discoursedb-management project
    database = "viztest"
    ddb = DiscourseDB(config["user"], config["password"], config["service"])
    ddb.set_db(config["database"])
    print(ddb.list_saved_queries())
    q = ddb.list_saved_queries()[0]    # Download the first saved query
    content = ddb.get_records(ddb.query_content(q))
    headers = {'Content-Type': 'application/x-ndjson'}
    indexline = json.dumps({"index":{"_index":config["kibana_index"]}})
    data="\n".join([indexline + "\n" + json.dumps(item) for item in content["content"]]) + "\n"

    reply = requests.post(config["kibana_url"] + config["kibana_database"] + "/" + config["kibana_mapping"] + "/_bulk?pretty", headers=headers, data=data)
    print(reply.text)
    
