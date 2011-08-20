/* **************************************************************************
 * Copyright (C) 2008 BJoRFUAN. All Right Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * BSD License, and comes with NO WARRANTY.
 *
 *                                                 torao <torao@bjorfuan.com>
 *                                                       http://www.moyo.biz/
 * $Id:$
*/
package org.koiroha.jyro.workers.crawler;

import java.net.URI;
import java.sql.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.koiroha.jyro.*;
import org.koiroha.jyro.util.IO;
import org.koiroha.xml.parser.HTMLDocumentBuilderFactory;
import org.w3c.dom.*;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Crawler: Crawler Worker
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The worker class to crawl web sites.
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/08/06 Java SE 6
 */
public class Crawler extends Worker {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Crawler.class);

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 *
	 */
	public Crawler() {
		return;
	}

	// ======================================================================
	// Receive specified job
	// ======================================================================
	/**
	 *
	 * @param job
	 * @return
	 * @throws WorkerException
	*/
	@Distribute(name="crawl",params={"url"})
	public void crawl(String url) throws WorkerException {
		List<URI> urls = retrieveLink(url);
		WorkerContext context = getContext();

		Connection con = null;
		try{
			con = DriverManager.getConnection("jdbc:mysql://localhost/jyro", "mysql", "mysql");
			con.setAutoCommit(false);

			PreparedStatement stmt1 = con.prepareStatement(
				"INSERT INTO jyro_urls(scheme,host,port,path,retrieved_at,created_at) VALUES(?,?,?,?,?,?)");
			PreparedStatement stmt = con.prepareStatement(
				"SELECT EXISTS(SELECT * FROM jyro_urls WHERE scheme=? AND host=? AND port=? AND path=?)");
			for(URI uri: urls){
				int port = getPort(uri);
				if(port < 0){
					logger.debug(uri + " is not supported");
					continue;
				}

				stmt.setString(1, uri.getScheme());
				stmt.setString(2, uri.getHost());
				stmt.setInt(3, port);
				stmt.setString(4, uri.getPath());
				ResultSet rs = stmt.executeQuery();
				rs.next();
				boolean exists = rs.getBoolean(1);
				if(! exists){
					Timestamp now = new Timestamp(System.currentTimeMillis());
					stmt1.setString(1, uri.getScheme());
					stmt1.setString(2, uri.getHost());
					stmt1.setInt(3, port);
					stmt1.setString(4, uri.getPath());
					stmt1.setTimestamp(5, now);
					stmt1.setTimestamp(6, now);
					stmt1.executeUpdate();
					con.commit();
					context.call("crawl", uri.toString());
				} else {
					logger.info("URL " + uri + " already retrieved");
				}
			}
			stmt.close();
			stmt1.close();
			con.commit();
		} catch(Exception ex){
			throw new WorkerException(ex);
		} finally {
			IO.close(con);
		}
		return;
	}

	// ======================================================================
	// Determine Port
	// ======================================================================
	/**
	 * Determine the port number of specified uri. Return negative value if
	 * URI scheme is not supported.
	 *
	 * @param uri URI
	 * @return port number
	*/
	private static int getPort(URI uri){
		int port = uri.getPort();
		if(port >= 0){
			return port;
		}
		String scheme = uri.getScheme();
		if(scheme.equals("http")){
			return 80;
		} else if(scheme.equals("https")){
			return 443;
		} else if(scheme.equals("ftp")){
			return 21;
		}
		return -1;
	}

	// ======================================================================
	// Receive specified job
	// ======================================================================
	/**
	 *
	 * @param job
	 * @return
	 * @throws WorkerException
	*/
	public List<URI> retrieveLink(String url) throws WorkerException {
		logger.debug("running crawler to: " + url);

		List<URI> urls = new ArrayList<URI>();
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			request.setHeader("User-Agent", "Mozilla/5.0 (compatible; Jyrobot/" + Jyro.VERSION + "; +http://www.koiroha.org/jyro.html)");
			HttpResponse response = client.execute(request);

			Header header = response.getFirstHeader("Content-Type");
			String contentType = header.getValue();
			logger.debug("Content-Type: " + contentType);
			if(contentType.toLowerCase().startsWith("text/html")){
				DocumentBuilderFactory factory = new HTMLDocumentBuilderFactory();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(response.getEntity().getContent());

				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList nl = (NodeList)xpath.evaluate("//a/@href", doc, XPathConstants.NODESET);
				for(int i=0; i<nl.getLength(); i++){
					String href = ((Attr)nl.item(i)).getValue();
					urls.add(URI.create(url).resolve(href));
				}
			}
		} catch(Exception ex){
			throw new WorkerException(ex);
		}
		return urls;
	}

}
