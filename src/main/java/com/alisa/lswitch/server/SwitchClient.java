package com.alisa.lswitch.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alisa.lswitch.server.exceptions.SwitchException;
import com.alisa.lswitch.server.lib.requests.StatusRequest;
import com.alisa.lswitch.server.lib.requests.SwitchRequest;

public class SwitchClient implements Switch {

	private static final Logger LOG = LoggerFactory.getLogger(SwitchClient.class);
	
	@Override
	public void broadcastStatus(StatusRequest req) throws SwitchException {
		LOG.info("req = " + req + "sent to server");
	}

	@Override
	public void switchStatus(SwitchRequest req) throws SwitchException {
		LOG.info("req = " + req + "sent to server");
	}

}
