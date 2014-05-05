package com.alisa.lswitch.server.lib.requests;

/**
 * Request to broadcast status.
 */
public class StatusRequest extends Request {

	private static final long serialVersionUID = 1L;

	/**
	 * Not necessary in the current use case where request is targeted 
	 * to a specific device but will be helpful in future versions.
	 */
	private String deviceId;
	
	/**
	 * Default constructor needed for json mapper.
	 */
	public StatusRequest() {
		
	}

	public StatusRequest(int version, String requestId, String signature,
			String deviceId) {
		super(version, requestId, signature);
		this.deviceId = deviceId;
	}

	public String getDeviceId() {
		return deviceId;
	}


	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String toString() {
		return "StatusRequest [deviceId=" + deviceId + ", version=" + version
				+ ", requestId=" + requestId + ", signature=" + signature + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((deviceId == null) ? 0 : deviceId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatusRequest other = (StatusRequest) obj;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		return true;
	}
	
}
