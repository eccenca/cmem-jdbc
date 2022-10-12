package com.eccenca.jena.core;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdfconnection.RDFConnection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.condition.DisabledIf;

import com.eccenca.jena.core.OAUTH2Authenticator.GrantType;

/**
 * 
 * @author edgardmarx
 *
 *         This Unit Class tests the SPARQL Insertion, Delete and Select
 *         capabilities. It does not run if the environmental variables
 *         CMEM_BASE_URI, OAUTH_CLIENT_SECRET and OAUTH_CLIENT_ID are not
 *         properly set.
 *
 */
@DisabledIf("java.lang.System.getProperty('CMEM_BASE_URI') == null || "
		+ "java.lang.System.getProperty('OAUTH_CLIENT_SECRET') == null ||"
		+ "java.lang.System.getProperty('OAUTH_CLIENT_ID')")
public class QueryTest {

	private CMEMOAUTH2RemoteRequestBuilder connectionBuilder = null;

	private final static String UPDATE_QUERY = "INSERT DATA { GRAPH <http://cmem.jdbc.test> {\n"
			+ "  <person0> <firstname> \"Jay\" .\n" + "  <person0> <lastname> \"Stevens\" .\n"
			+ "  <person0> <state> \"CA\" .\n" + " }\n" + "}";

	private final static String SELECT_QUERY = "Select ?s ?p ?o where { graph <http://cmem.jdbc.test> {?s ?p ?o}}";
	private final static String DELETE_QUERY = "DROP graph <http://cmem.jdbc.test>";

	@Before
	public void start() {
		CMEMOAUTH2Authenticator authenticator = new CMEMOAUTH2Authenticator()
				.clientId(System.getenv("OAUTH_CLIENT_ID"))
				.clientSecret(System.getenv("OAUTH_CLIENT_SECRET"))
				.grantType(GrantType.CLIENT_CREDENTIALS);
		connectionBuilder = new CMEMOAUTH2RemoteRequestBuilder(authenticator)
				.host(System.getenv("CMEM_BASE_URI"));
	}

	@After
	public void stop() {
		// remove any remaining data in case some test fail
		clear();
	}

	private void clear() {
		try (RDFConnection conn = connectionBuilder.build()) {
			conn.update(DELETE_QUERY);
		}
	}

	private void assertSelect(int n) {
		try (RDFConnection conn = connectionBuilder.build()) {
			try (QueryExecution qExec = conn.query(SELECT_QUERY);) {
				ResultSet rs = qExec.execSelect();
				int i = 0;
				while (rs.hasNext()) {
					rs.next();
					i++;
				}
				Assert.assertEquals(n, i);
			}
		}
	}

	/**
	 * This method performs the three basic functionalities of the cmem client.
	 * Inserting an arbitrary data - Check if the data and the graph exist with a
	 * select query - Delete the inserted data - Check if the data was deleted
	 */
	@Test
	public void selectTest() {

		// check existence if the graph is empty
		assertSelect(0);

		try (RDFConnection conn = connectionBuilder.build()) {
			conn.update(UPDATE_QUERY);
		}

		// check if the graph contains the inserted data
		assertSelect(3);

		// remove inserted data
		clear();

		// check if the data was removed
		assertSelect(0);
	}
}
