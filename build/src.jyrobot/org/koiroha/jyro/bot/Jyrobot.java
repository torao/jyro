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
import java.util.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.Jyro;
import org.koiroha.jyro.util.IO;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Jyrobot: Jyrobot アプリケーション
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
	// User Agent
	// ======================================================================
	/**
	 * ユーザエージェントです。
	 */
	private final UserAgent userAgent;

	// ======================================================================
	// Session Queue
	// ======================================================================
	/**
	 * このボットが使用するセッションキューです。
	 */
	private final SessionQueue queue;

	// ======================================================================
	// Client
	// ======================================================================
	/**
	 * 取得したコンテンツを渡すクライアントです。
	 */
	private final Map<String,BotClient> clients = new HashMap<String,BotClient>();

	// ======================================================================
	// Crawler Thread Pool
	// ======================================================================
	/**
	 * クローラーを実行するためのスレッドプールです。
	 */
	private ThreadGroup crawlers = null;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * アプリケーション設定を指定して構築を行います。
	 *
	 * @param config application configuration
	 * @throws CrawlerException if fail to initialize
	 */
	private Jyrobot(Config config) throws CrawlerException{
		this.config = config;

		// ユーザエージェントの構築
		this.userAgent = new UserAgent(config.getSubconfig("user_agent"));

		// セッションキューの構築
		String className = config.getString("session_queue", "class");
		try {
			this.queue = (SessionQueue)Class.forName(className).newInstance();
		} catch(Exception ex){
			throw new ConfigurationException(config.getPathname("session_queue", "class") + "=" + className, ex);
		}
		this.queue.configure(this, config.getSubconfig("session_queue"));

		// クライアントの構築
		for(Config c: config.getSubconfigs("crawler", "clients")){
			String id = c.getString("id");
			String clazz = c.getString("class");
			try {
				BotClient client = (BotClient)Class.forName(clazz).newInstance();
				client.configure(c);
				clients.put(id, client);
			} catch(Exception ex){
				logger.fatal("fail to instantiate bot client: [" + id + "] " + clazz, ex);
			}
		}
		return;
	}

	// ======================================================================
	// Refer Configuration
	// ======================================================================
	/**
	 * このボットの設定を参照します。
	 *
	 * @return configuration of this bot
	 */
	public Config getConfig(){
		return config;
	}

	// ======================================================================
	// Refer User-Agent
	// ======================================================================
	/**
	 * ユーザエージェントを参照します。
	 *
	 * @return user agent
	 */
	public UserAgent getUserAgent() {
		return userAgent;
	}

	// ======================================================================
	// Retrieve Session Queue
	// ======================================================================
	/**
	 * このボットが使用するセッションキューを参照します。
	 *
	 * @return session queue
	 */
	public SessionQueue getSessionQueue(){
		return queue;
	}

	// ======================================================================
	// Start Crawling
	// ======================================================================
	/**
	 * クローリングを開始します。
	 */
	public synchronized void start(){
		logger.debug("start()");

		// 既に処理が起動している場合はなにもしない
		if(crawlers != null){
			logger.warn("jyrobot already working");
			return;
		}

		// 並列処理を行うセッション数を参照
		int parallel = config.getInt("crawler", "parallel");

		// 指定個数のクローラーを起動
		crawlers = new ThreadGroup("Jyrobot");
		for(int i=0; i<parallel; i++){
			Crawler crawler = new Crawler(this);
			Thread thread = new Thread(crawlers, crawler, "Crawler[" + i + "]");
			thread.start();
		}
		return;
	}

	// ======================================================================
	// Shutdown Crawler Bot
	// ======================================================================
	/**
	 * このクローリング処理をシャットダウンします。
	 *
	 * @param maxWait max waittime per crawler threads in milliseconds
	 * @throws InterruptedException if interrupted while waiting crawler stop
	 */
	public synchronized void shutdown(long maxWait) throws InterruptedException{
		logger.debug("stop()");

		// 既に処理が停止している場合はなにもしない
		if(crawlers == null){
			return;
		}

		// すべてのクローラに対して割り込みを実行し停止を待機
		crawlers.interrupt();
		Thread[] threads = new Thread[crawlers.activeCount()];
		int count = crawlers.enumerate(threads, false);
		for(int i=0; i<count; i++){
			threads[i].join(maxWait);
		}

		// スレッドグループを破棄
		crawlers = null;
		return;
	}

	// ======================================================================
	// Iterate BotClient
	// ======================================================================
	/**
	 * このインスタンスに登録されている BotClient を列挙します。
	 *
	 * @return iterable of bot clients
	 */
	public Iterable<BotClient> getBotClients(){
		return clients.values();
	}

	// ======================================================================
	// Create Instance
	// ======================================================================
	/**
	 * 指定された設定ファイルからインスタンスを生成します。
	 *
	 * @param url URL for configuration file
	 * @return jyrobot instance
	 * @throws CrawlerException if fail to initialize
	 */
	public static final Jyrobot newInstance(URL url) throws CrawlerException {
		logger.debug("newInstance(" + url + ")");
		InputStream in = null;
		try {
			in = url.openStream();
			return newInstance(Config.newInstance(in));
		} catch(IOException ex){
			throw new CrawlerException(ex);
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
	 * @throws CrawlerException if fail to initialize
	 */
	public static final Jyrobot newInstance(Config config) throws CrawlerException{
		return new Jyrobot(config);
	}

	// ======================================================================
	// Execute from Commandline
	// ======================================================================
	/**
	 * コマンドラインからアプリケーションを実行します。
	 *
	 * @param args commandline parameters
	 * @throws Exception if fail to execute jyrobot
	 */
	public static void main(String[] args) throws Exception{

		// コマンドラインパラメータの解析
		List<URL> urls = new ArrayList<URL>();
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
			if(args[i].matches("-c|--config") && i+1<args.length){
				i ++;
				URI uri = new URI(args[i]);
				if(uri.isAbsolute()){
					config = uri.toURL();
				} else {
					config = new File(args[i]).toURI().toURL();
				}
			}

			// 設定ファイルの指定
			if(args[i].matches("-p|--post") && i+1<args.length){
				i ++;
				urls.add(new URL(args[i]));
			}
		}

		// インスタンスの作成
		final Jyrobot jyrobot = newInstance(config);
		for(URL url: urls){
			jyrobot.getSessionQueue().offer(url);
		}
		jyrobot.start();

		// 終了処理の登録
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run(){
				try {
					jyrobot.shutdown(10 * 1000);
				} catch(InterruptedException ex){/* */}
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
