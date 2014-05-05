package com.alisa.Iswitch.server.lib.serialization;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import org.junit.Assert;
import org.junit.Test;

import com.alisa.Iswitch.command.Command;
import com.alisa.lswitch.server.lib.requests.Request;
import com.alisa.lswitch.server.lib.requests.StatusRequest;
import com.alisa.lswitch.server.lib.requests.SwitchRequest;

public class SerializerTest {

	private final StatusRequest statusReq = new StatusRequest(1, UUID.randomUUID()
			.toString(), "test", UUID.randomUUID().toString());
	
	private final SwitchRequest switchReq = new SwitchRequest(1, UUID.randomUUID()
			.toString(), "test", UUID.randomUUID().toString(), Command.ON);
	
	private List<Request> requests = new ArrayList<Request>(){{
		add(statusReq);
		add(switchReq);
	}};
	
	@SuppressWarnings("serial")
	private List<Serializer<? extends Request>> serializers =  new ArrayList<Serializer<? extends Request>>() {{
		add(new JsonSerializer<? extends Request>());
		add(new ObjectSerializer<? extends Request>());
	}};
	

	@Test
	public void testSerializers() throws Exception {
		System.out.println("test");
			for(Serializer<StatusRequest> serializer : serializers) {
				for(Request req: requests) {
				
					byte[] serialize = serializer.serialize(statusReq);
					Assert.assertEquals(req, serializer.deserialize(serialize, req.getClass()));
				
				}
			}
	}
}
