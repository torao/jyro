package org.koiroha.jyro.workers.crawler;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.*;


/**
 * The persistent class for the jyro_content database table.
 *
 */
@Entity
@Table(name="jyro_content")
public class JyroContent implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;

    @Lob()
	private byte[] content;

	@Column(name="content_type")
	private String contentType;

	@Column(name="created_at")
	private Timestamp createdAt;

    @Lob()
	@Column(name="query_string")
	private byte[] queryString;

    @Lob()
	@Column(name="request_header")
	private String requestHeader;

	@Column(name="request_method")
	private String requestMethod;

	@Column(name="request_uri")
	private String requestUri;

	@Column(name="request_version")
	private String requestVersion;

	@Column(name="response_code")
	private int responseCode;

    @Lob()
	@Column(name="response_header")
	private String responseHeader;

	@Column(name="response_phrase")
	private String responsePhrase;

	@Column(name="response_version")
	private String responseVersion;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	private String url;

    public JyroContent() {
    }

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getContent() {
		return this.content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public byte[] getQueryString() {
		return this.queryString;
	}

	public void setQueryString(byte[] queryString) {
		this.queryString = queryString;
	}

	public String getRequestHeader() {
		return this.requestHeader;
	}

	public void setRequestHeader(String requestHeader) {
		this.requestHeader = requestHeader;
	}

	public String getRequestMethod() {
		return this.requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getRequestUri() {
		return this.requestUri;
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}

	public String getRequestVersion() {
		return this.requestVersion;
	}

	public void setRequestVersion(String requestVersion) {
		this.requestVersion = requestVersion;
	}

	public int getResponseCode() {
		return this.responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseHeader() {
		return this.responseHeader;
	}

	public void setResponseHeader(String responseHeader) {
		this.responseHeader = responseHeader;
	}

	public String getResponsePhrase() {
		return this.responsePhrase;
	}

	public void setResponsePhrase(String responsePhrase) {
		this.responsePhrase = responsePhrase;
	}

	public String getResponseVersion() {
		return this.responseVersion;
	}

	public void setResponseVersion(String responseVersion) {
		this.responseVersion = responseVersion;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}