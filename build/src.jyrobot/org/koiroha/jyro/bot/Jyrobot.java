/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.bot;

import java.io.*;
import java.net.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.Jyro;
import org.koiroha.jyro.util.IO;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Jyrobot:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version
 * @author torao
 * @since 2011/10/19 jyro 1.0
 */
public final class Jyrobot {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Jyrobot.class);

	// ======================================================================
	// Jyrobot Configuration
	// ======================================================================
	/**
	 * Jyrobot の設定です。
	 */
	private final Config config;

	// ======================================================================
	// Scheduler
	// ======================================================================
	/**
	 * スケジューラです。
	 */
	private final Scheduler scheduler;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * アプリケーション設定を指定して構築を行います。
	 *
	 * @param config application configuration
	 */
	private Jyrobot(Config config) {
		this.config = config;

		// スケジューラの構築
		this.scheduler = new Scheduler(config.getSubconfig("scheduler"));
		return;
	}

	// ======================================================================
	// Start Crawling
	// ======================================================================
	/**
	 * クローリングを開始します。
	 */
	public void start(){
		return;
	}

	// ======================================================================
	// Stop Crawlink
	// ======================================================================
	/**
	 * クローリングを終了します。
	 */
	public void stop(){
		return;
	}

	// ======================================================================
	// Create Instance
	// ======================================================================
	/**
	 * 指定された設定ファイルからインスタンスを生成します。
	 *
	 * @param url URL for configuration file
	 * @return jyrobot instance
	 * @throws IOException if fail to load configuration
	 */
	public static final Jyrobot newInstance(URL url) throws IOException {
		InputStream in = url.openStream();
		try {
			return newInstance(Config.newInstance(in));
		} finally {
			IO.close(in);
		}
	}

	// ======================================================================
	// Create Instance
	// ======================================================================
	/**
	 * 指定された設定からインスタンスを生成します。
	 *
	 * @param config application configuration
	 * @return jyrobot instance
	 */
	public static final Jyrobot newInstance(Config config) {
		return new Jyrobot(config);
	}

	// ======================================================================
	// Execute from Commandline
	// ======================================================================
	/**
	 * コマンドラインからアプリケーションを実行します。
	 *
	 * @param args commandline parameters
	 * @throws IOException if fail to load configuration
	 * @throws URISyntaxException invalid uri specified
	 */
	public static void main(String[] args) throws IOException, URISyntaxException{

		// コマンドラインパラメータの解析
		URL config = null;
		for(int i=0; i<args.length; i++){

			// オンラインヘルプの表示
			if(args[i].matches("-h|--help")){
				help();
				return;
			}

			// バージョン情報の表示
			if(args[i].matches("-v|--version")){
				System.out.printf("%s %s (build %s)%n", Jyro.NAME, Jyro.VERSION, Jyro.BUILD);
				return;
			}

			// 設定ファイルの指定
			URI uri = new URI(args[i]);
			if(uri.isAbsolute()){
				config = uri.toURL();
			} else {
				config = new File(args[i]).toURI().toURL();
			}
		}

		// インスタンスの作成
		final Jyrobot jyrobot = newInstance(config);
		jyrobot.start();

		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run(){
				jyrobot.stop();
			}
		});
		return;
	}

	// ======================================================================
	// Show Help
	// ======================================================================
	/**
	 * 標準出力にヘルプを表示します。
	 */
	private static void help(){
		System.out.println("");
		return;
	}

}
