/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package sample;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.koiroha.jyro.bot.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// SampleBotClient:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version
 * @author torao
 * @since 2011/11/02 jyro 1.0
 */
public class SampleBotClient implements BotClient {

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param config
	 */
	@Override
	public void configure(Config config) {
		// TODO Auto-generated method stub

	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param session
	 * @return
	 */
	@Override
	public boolean startSession(Session session) {
		// TODO Auto-generated method stub
		return true;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param session
	 */
	@Override
	public void endSession(Session session) {
		// TODO Auto-generated method stub

	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param session
	 * @param url
	 * @return
	 */
	@Override
	public boolean startRequest(Session session, URL url) {
		// TODO Auto-generated method stub
		return true;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param session
	 * @param request
	 * @param response
	 */
	@Override
	public void endRequest(Session session, Request request, Response response) {
		try {
			response.getContent();
		} catch(IOException ex){

		}
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param session
	 * @param request
	 * @param ex
	 */
	@Override
	public void requestFailed(Session session, Request request, Throwable ex) {
		// TODO Auto-generated method stub

	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param url
	 * @return
	 */
	@Override
	public boolean accept(URL url) {
		// TODO Auto-generated method stub
		return true;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param request
	 */
	@Override
	public void prepare(Request request) {
		// TODO Auto-generated method stub

	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 * @throws CrawlerException
	 */
	@Override
	public Iterable<URL> parse(Session session, Request request,
			Response response) throws CrawlerException {
		// TODO Auto-generated method stub
		return new ArrayList<URL>();
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param request
	 * @param ex
	 */
	@Override
	public void failure(Request request, Throwable ex) {
		// TODO Auto-generated method stub

	}

}
