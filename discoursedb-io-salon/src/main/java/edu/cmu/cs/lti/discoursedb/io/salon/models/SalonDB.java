package edu.cmu.cs.lti.discoursedb.io.salon.models;

/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 * Contributor Haitian Gong, Chris Bogart
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.lambda.SQL;
import org.jooq.lambda.Seq;
import org.jooq.lambda.Unchecked;


/**
 * Establishes a JDBC database connection to a salon database and provides
 * methods to access salon data using the POJOs in the model package.
 * 
 * @author Haitian Gong
 *
 */

public class SalonDB {
	
	private Connection con = null;
	private String host;
	private String db;
	private String user;
	private String pwd;
	

	/**
	 * Creates a database access object for accessing salon data from a MySQL database.
	 * 
	 * @param host host of the salon db
	 * @param db salon database
	 * @param usr username with read access to the salon db
	 * @param pwd user password
	 */
	public SalonDB(String host, String db, String usr, String pwd) {
		this.host = host;
		this.db = db;
		this.user = usr;
		this.pwd = pwd;
	}

	
	
	public PreparedStatement prep(String sql) throws SQLException {
		return getConnection().prepareStatement(sql);
	}
	

	
	Connection getConnection() throws SQLException {
		if (con == null || con.isClosed()) {
			con = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + 3306 + "/" + 
					this.db+ "?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&useSSL=false",
					this.user,this.pwd);			
		} 
		return con;
		
	}

}