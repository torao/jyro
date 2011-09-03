package org.koiroha.jyrobot.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the jyrobot_request_queue database table.
 * 
 */
@Entity
@Table(name="jyrobot_request_queue")
public class JPARequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private Timestamp accessed;
	private String path;
	private int response;
	private int session;

    public JPARequest() {
    }


	@Id
	@SequenceGenerator(name="JYROBOT_REQUEST_QUEUE_ID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="JYROBOT_REQUEST_QUEUE_ID_GENERATOR")
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public Timestamp getAccessed() {
		return this.accessed;
	}

	public void setAccessed(Timestamp accessed) {
		this.accessed = accessed;
	}


	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}


	public int getResponse() {
		return this.response;
	}

	public void setResponse(int response) {
		this.response = response;
	}


	public int getSession() {
		return this.session;
	}

	public void setSession(int session) {
		this.session = session;
	}

}