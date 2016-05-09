package edu.cmu.cs.lti.discoursedb.core.type;

/**
 * Defines the types of data sources supported by DiscourseDB.
 * This is used in the DataSourceInstance entities in addition to the entity source ids and concrete dataset names. 
 * 
 * @author Oliver Ferschke
 */
public enum DataSourceTypes {
	/**
	 * An edX dataset
	 */
	EDX,
	/**
	 * A ProSolo dataset
	 */
	PROSOLO,
	/**
	 * A ProSolo blog dataset
	 */
	PROSOLO_BLOG,
	/**
	 * A TAGS dataset 
	 */
	TAGS,
	/**
	 * Github dataset
	 */
	GITHUB,
	/**
	 * Google Groups dataset
	 */
	GOOGLE_GROUPS,
	/**
	 * A Bazaar chat dataset
	 */
	BAZAAR,
	/**
	 * A Coursera dataset
	 */
	COURSERA,
	/**
	 * A Piazza dataset
	 */
	PIAZZA,
	/**
	 * A Habworlds dataset
	 */
<<<<<<< Upstream, based on branch 'master' of https://github.com/discoursedb/discoursedb-core
	HABWORLDS,
	/**
	 * A Neuwirth dataset
	 */
	NEUWIRTH
=======
	HABWORLDS
>>>>>>> bcf0921 type changes for githubarchive

}
