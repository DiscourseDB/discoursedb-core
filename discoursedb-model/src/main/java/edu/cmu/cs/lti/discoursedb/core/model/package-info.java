/**
 * Contains the DiscourseDB Core object model with the object relational mappings using Hibernate 4.
 * Data access is provided by Spring Data repositories that can be found in {@link edu.cmu.cs.lti.discoursedb.core.repository}.
 * 
 * Note that the entity classes make use of Project Lombok"s @Data annotation to gererate Getter, Setters and constructors.<br/>
 * In order to make this work in eclipse, you need to run the lombok.jar on your developer machine to configure setup eclipse
 * to support the generated code. More information about this can be found in the discoursedb-mode documentation on GitHub.
 * 
 * @see <a href="http://hibernate.org/orm/">Hibernate ORM</a>
 * 
 * @author Oliver Ferschke
 */
package edu.cmu.cs.lti.discoursedb.core.model;