package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wraps entities form the post table in prosolo
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
public class ProsoloPost {
 private String dtype;
 private Long id;
 private Date created;
 private Boolean deleted;
 private String dc_description;
 private String title;
 private String content;
 private String link;
 private String visibility;
 private Boolean connect_with_status;
 private Long maker;
 private Long reshare_of;
 private Long rich_content;
 private Long goal;
 private String post_link;

}
