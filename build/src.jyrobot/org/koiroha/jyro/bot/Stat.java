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

import java.io.Serializable;
import java.text.NumberFormat;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Stat: 統計情報
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * 入出力統計を保持するためのクラスです。
 *
 * @version
 * @author torao
 * @since 2011/11/05 jyro 1.0
 */
public class Stat implements Serializable {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 */
	protected Stat() {
		return;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Session: セッション統計
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * セッション統計です。
	 */
	public static class Session extends Stat{

		// ==================================================================
		// Serial Version
		// ==================================================================
		/**
		 * Serial version of this class.
		 */
		private static final long serialVersionUID = 1L;

		// ==================================================================
		// Total Requests
		// ==================================================================
		/**
		 * セッション場で行われたリクエスト数。
		 */
		private volatile int requests = 0;

		// ==================================================================
		// Total Output Bytes
		// ==================================================================
		/**
		 * 総出力バイト数。
		 */
		private volatile long outputBytes = 0;

		// ==================================================================
		// Total Input Bytes
		// ==================================================================
		/**
		 * 総入力バイト数。
		 */
		private volatile long inputBytes = 0;

		// ==================================================================
		// Constructor
		// ==================================================================
		/**
		 * コンストラクタは何も行いません。
		 */
		public Session() {
			return;
		}

		// ==================================================================
		// Refer Requests
		// ==================================================================
		/**
		 * セッション上で行われたリクエスト数を参照します。
		 *
		 * @return total request count
		 */
		public int getRequests() {
			return requests;
		}

		// ==================================================================
		// Increase Requests
		// ==================================================================
		/**
		 * セッション上で行われたリクエスト数を加算します。
		 *
		 * @param requests additional requests count
		 * @return total requests
		 */
		public int increaseRequests(int requests) {
			this.requests += requests;
			return this.requests;
		}

		// ==================================================================
		// Refer Output Bytes
		// ==================================================================
		/**
		 * 総出力バイト数を参照します。
		 *
		 * @return total output bytes
		 */
		public long getOutputBytes() {
			return outputBytes;
		}

		// ==================================================================
		// Increase Output Bytes
		// ==================================================================
		/**
		 * 出力バイト数を加算します。
		 *
		 * @param bytes additional bytes
		 * @return total output bytes
		 */
		public long increaseOutputBytes(int bytes) {
			outputBytes += bytes;
			return outputBytes;
		}

		// ==================================================================
		// Refer Input Bytes
		// ==================================================================
		/**
		 * 総入力バイト数を参照します。
		 *
		 * @return input bytes
		 */
		public long getInputBytes() {
			return inputBytes;
		}

		// ==================================================================
		// Increase Input Bytes
		// ==================================================================
		/**
		 * 入力バイト数を加算します。
		 *
		 * @param bytes additional bytes
		 * @return total output bytes
		 */
		public long increaseInputBytes(int bytes) {
			inputBytes += bytes;
			return inputBytes;
		}

		// ==================================================================
		// Literalize Instance
		// ==================================================================
		/**
		 * このインスタンスを文字列化します。
		 *
		 * @return total output bytes
		 */
		@Override
		public String toString(){
			NumberFormat nf = NumberFormat.getNumberInstance();
			return "totao " + nf.format(getRequests()) + " requests, " + nf.format(getInputBytes()) + " bytes read, " + nf.format(getOutputBytes()) + " bytes write";
		}

	}
}
