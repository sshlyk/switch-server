package com.alisa.lswitch.server;

import com.alisa.lswitch.server.exceptions.SwitchException;
import com.alisa.lswitch.server.lib.requests.StatusRequest;
import com.alisa.lswitch.server.lib.requests.SwitchRequest;

public interface Switch {

	/**
	 * Ideally these two can be converted into a single
	 * API, sendCommand, and broadcast status can just be another
	 * command.
	 */
	public void broadcastStatus(StatusRequest req) throws SwitchException;
	public void switchStatus(SwitchRequest req) throws SwitchException;
	
	/**
	 * The API's that I thin we should add down the line.
	 * 
	 * public void sendCommand(CommandRequest req);
	 * public List<DeviceInfo> listDevices(ListRequest req); //may be paginated response
	 * public DeviceInfo getDeviceInfo(DeviceInfoRequest req);
 	 */
}
