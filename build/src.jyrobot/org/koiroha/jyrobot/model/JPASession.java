package org.koiroha.jyrobot.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the jyrobot_session_queue database table.
 * 
 */
@Entity
@Table(name="jyrobot_session_queue")
public class JPASession implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private Timestamp accessed;
	private Timestamp activated;
	private String appid;
	private Timestamp created;
	private String host;
	private int port;
	private int priority;
	private String scheme;

    public JPASession() {
    }


	@Id
	@SequenceGenerator(name="JYROBOT_SESSION_QUEUE_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="JYROBOT_SESSION_QUEUE_ID_GENERATOR")
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public Timestamp getAccessed() {
		return this.accessed;
	}

	public void setAccessed(Timestamp accessed) {
		this.accessed = accessed;
	}


	public Timestamp getActivated() {
		return this.activated;
	}

	public void setActivated(Timestamp activated) {
		this.activated = activated;
	}


	public String getAppid() {
		return this.appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}


	public Timestamp getCreated() {
		return this.created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}


	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}


	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}


	public int getPriority() {
		return this.priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}


	public String getScheme() {
		return this.scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

}