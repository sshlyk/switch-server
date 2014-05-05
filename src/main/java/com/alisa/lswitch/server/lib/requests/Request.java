package com.alisa.lswitch.server.lib.requests;

import java.io.Serializable;

/**
 * Base class for all requests.
 */
public abstract class Request implements Serializable {

	private static final long serialVersionUID = 1L;
	protected int version;
	protected String requestId;
	protected String signature;
	
	/**
	 * Default constructor needed for json mapper.
	 */
	public Request() {
		
	}
	
	public Request(int version, String requestId, String signature) {
		this.version = version;
		this.requestId = requestId;
		this.signature = signature;
	}
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}

	@Override
	public String toString() {
		return "Request [version=" + version + ", requestId=" + requestId
				+ ", signature=" + signature + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((requestId == null) ? 0 : requestId.hashCode());
		result = prime * result
				+ ((signature == null) ? 0 : signature.hashCode());
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Request other = (Request) obj;
		if (requestId == null) {
			if (other.requestId != null)
				return false;
		} else if (!requestId.equals(other.requestId))
			return false;
		if (signature == null) {
			if (other.signature != null)
				return false;
		} else if (!signature.equals(other.signature))
			return false;
		if (version != other.version)
			return false;
		return true;
	}
	
}
