package com.alisa.lswitch.server.lib.requests;

import com.alisa.Iswitch.command.Command;

/**
 * Request to operate switch.
 */
public class SwitchRequest extends Request {
	
	private static final long serialVersionUID = 1L;

	private String deviceId;
	
	private Command command;

	public SwitchRequest(int version, String requestId, String signature,
			String deviceId) {
		super(version, requestId, signature);
		this.deviceId = deviceId;
	}
	
	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * Default constructor required for json.
	 */
	public SwitchRequest() {
	}

	@Override
	public String toString() {
		return "SwitchRequest [deviceId=" + deviceId + ", command=" + command
				+ ", version=" + version + ", requestId=" + requestId
				+ ", signature=" + signature + "]";
	}

	public SwitchRequest(int version, String requestId, String signature, String deviceId, Command command) {
		super(version, requestId, signature);
		this.deviceId = deviceId;
		this.command = command;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((command == null) ? 0 : command.hashCode());
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
		SwitchRequest other = (SwitchRequest) obj;
		if (command != other.command)
			return false;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		return true;
	}
		
}
