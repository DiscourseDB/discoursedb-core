package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.converter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * This converter loads the forum json file specified in the arguments of the
 * app and parses the json into Post objects and maps each post object to
 * DiscourseDB.
 * 
 * Many of the relations between entities are actually modeled in the form of
 * relation tables which allows us to keep track of the time window in which the
 * relation was active. However, this also entails that we need to explicitly
 * instantiate these relations - i.e. we have to create a "relationship-entity"
 * which makes the code more verbose.
 * 
 * The conversion is split into three phases. Phase 1 (this class) imports all
 * of the data except for the DiscoursRelations. These relations are created
 * between entities and require the entities to be present in the database. That
 * is why they are created in a second pass (Phase2) Phase 3 adds personal
 * information about the user to the database that comes from a different file.
 * 
 * @author Oliver Ferschke
 *
 */
@Component
@Transactional
@Order(1)
public class ProsoloSocialActivityConverterPhase1 implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(ProsoloSocialActivityConverterPhase1.class);

	private Connection con = null;

	@Override
	public void run(String... args) throws Exception {
		
		if (args.length != 4) {
			logger.error("Missing database credentials <prosolo_dbhost> <prosolo_db> <prosolo_dbuser> <prosolo_dbpwd>");
			System.exit(1);
		}
		
		///////////////////////////////////////////////
		
		logger.info("Establishing connection to prosolo database...");
		try {
			con = getConnection(args[0], args[1], args[2], args[3]);
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
			System.exit(1);
		}
		logger.info("...established");

		///////////////////////////////////////////////	
		
		logger.info("Start mapping to DiscourseDB...");
		try {
			map();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
		logger.info("...mapping complete");
	
		///////////////////////////////////////////////
	}
	
	

	private void map() throws SQLException {
		PreparedStatement pst = con.prepareStatement("SELECT * FROM user Limit 2");
		// pst.setString(1, author);
		ResultSet res = pst.executeQuery();
		while (res.next()) {
			System.out.println(res.getString("lastname"));
		}
		con.close();
	}

	private static Connection getConnection(String host, String db, String user, String pwd) throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + db, user, pwd);
	}

}