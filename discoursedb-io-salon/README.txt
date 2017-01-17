Mapping Salon concepts and database tables to DiscourseDB entities:


INITIAL MAPPINGS
   Salon -> discourse
   documents, threads, classes, discussion -> discourse parts
   paragraph -> contribution within document
   annotation -> contribution within document, plus contribution_has_context pointing to paragraph
   salonDiscussion -> contribution within "discussion" dp
   questions and responses -> contributions within document dp
   responseParagraph -> contribution_has_context pointing from response to paragraph
   
   
More detail:
* A particular "salon" is a discourse.  e.g. Rhetoric 2015
* Discourse_parts: a document, a thread, a class
* Contribution: a paragraph is a kind of contribution, but its content gets pointed to by other stuff
* A salon annotation -> a discoursedb contribution, which points to the paragraph's content as context.  It even has offsets
* A "salonDiscussion" -> a contribution
* network -> ddb group;  user membership -> user_memberof_group
* salon group --> ddb group
* salon class --> ddb group
* questions & responses --> contributions threaded within an assignment as discourse_part.  Ea q needn't be a d_p.
* responseParagraph --> context link for a response. 

Notes on Salon's structure
==========================
salons.xml  <---- Discourse; has salon_id
    docFolders.xml  <---- nested discourse_part; has name only and id and salon_id 
    docActiveListTable.xml  <--- many-many salon <--> doc (probably as anno salondocs: with instructions, start/end time, etc)
                 docId matches salondocs.doc_id  (ONLY ONE!)
    salondocs.xml <--- many-many salon <--> doc .   "Salondoc_id" matches documents.document_id;  
      documents.xml  <--- has title, desc, body; has comments, annotations, view- and upload dates, url, category, status uses salondoc_id
         paragraph.xml  <--- like annotations; start/end within document and are numbered.  Uses salondoc_id
              annotationParagraph.xml <-- points to anno and para.  not sure how it works
         annotation.xml  <-- title="comment_text"; body="comment_area". also psitivity, time, reply_to, user_id (uses salondoc_id)
             [annotationRevisionHistory] <--- content?
         thread.xml  <--- has owner, anno, document, "group"; text, dates.  "annotation_count, user_count"
                    doc_id matches salondocs.salondoc_id
        documentAccessDateTime.xml   <--- user interaction
        documentAccess.xml    <--- not sure what; maybe permission category?  "accessType and accTypeId" per doc
                docId --> salondocs.salondoc_id
    network.xml  <- per salon; and has an owner
        [networkUsers] <-- maybe links users to networks?  Are these user groups?
    salonusers.xml <--- maps salon to users many-many
    salonRequests.xml  <--- salon-to-user with "status" -- probably join requests
    salonDiscussion.xml <--- discussion threaded attached to salon itself; with replyTo & topic, no nesting
classes.xml
      userclasses.xml  <--- many-to-many
             user.xml
      [assignments] <--- but none in current dataset; name, desc, id, starttime endtime
           question  <--- per assignment, document, and user; with text and title and create date
                 votes.xml  <-- per document, user
                 response <-- per question, user, with long text
           topic.xml  <--- pts to assignment and document; text topic name.  Document is salondocs.salondoc_id
                [videoAnnotation, videoTopic only pointers to this]
                
                
Other notes:
 * It seems like annotations point to documents, paragraph refer to parts of documents, and 
 annotations also (via an annotationParagraph) can refer to parts of paragraphs.  But the n
 numbering is redundant; we can actually sort of ignore paragraphs.
 
 
 
 
