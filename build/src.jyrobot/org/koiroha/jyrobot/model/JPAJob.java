package org.koiroha.jyrobot.model;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the jyrobot_session_jobs database table.
 *
 */
@Entity
@Table(name="jyrobot_session_jobs")
public class JPAJob implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	private String path;

	private String referer;

	private int session;

    public JPAJob() {
    }

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getReferer() {
		return this.referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public int getSession() {
		return this.session;
	}

	public void setSession(int session) {
		this.session = session;
	}

}