/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
package edu.cmu.cs.lti.discoursedb.user.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.Assert;
import org.springframework.boot.Banner;

/**
 * This starter class launches the components necessary for doing commandline user management
 * 
 * @author Chris Bogart
 */
/*
 * @SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
@EnableEntityLinks
@EnableAsync
@EntityScan(basePackageClasses = { DiscourseApiStarter.class, Jsr310JpaConverters.class })
@ComponentScan(value = { "edu.cmu.cs.lti.discoursedb" }, excludeFilters = {
		  @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
				  value = { UnannotatedContributionRemover.class, 
						  TestDiscourseGenerator.class, BratConfigExport.class, BratExport.class, BratImport.class,
						  }) })
 */
@SpringBootApplication
@ComponentScan(basePackages = {"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.system",
		"edu.cmu.cs.lti.discoursedb.user.management"}, 
		excludeFilters= @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE,
		value= {edu.cmu.cs.lti.discoursedb.configuration.DatabaseSelector.class,
				edu.cmu.cs.lti.discoursedb.configuration.BaseConfiguration.class}
		))
public class ManageUsersApplication {

	/**
	 * Launches the SpringBoot application which runs the converter components in the order provided by the Order annotation.
	 * The launch parameters are passed on to each component.
	 * 
	 * @param args <DataSetName> </path/to/*-prod.mongo> </path/to/*-auth_user-prod-analytics.sql>(optional)
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length >= 2, "Usage: ManageUsers (list|add <user> <password>|delete <user>|grant <user> <database>|revoke <user> <database>|grant public <database>|revoke public <database>)");
        SpringApplication app = new SpringApplication(ManageUsersApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

	}
}
