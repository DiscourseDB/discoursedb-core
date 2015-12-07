package edu.cmu.cs.lti.discoursedb.configuration;

import org.hibernate.dialect.MySQL5InnoDBDialect;

/**
 * Extends MySQL5InnoDBDialect, but uses UTF8 as the default charset
 * 
 * @author Oliver Ferschke
 */
public class DiscourseDBMysqlDialect extends MySQL5InnoDBDialect {
	public String getTableTypeString() {
		return " ENGINE=InnoDB DEFAULT CHARSET=utf8";
	}

}
