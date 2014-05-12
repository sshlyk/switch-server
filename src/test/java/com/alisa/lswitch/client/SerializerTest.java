package com.alisa.lswitch.client;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.alisa.lswitch.client.model.StatusReply;
import com.alisa.lswitch.client.model.StatusRequest;
import com.alisa.lswitch.client.model.SwitchRequest;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class SerializerTest {

  @Test
  public void serializeDeserializeSwitchRequestTest() {
    SwitchRequest request = new SwitchRequest();
    request.setRequestId(UUID.randomUUID());
    request.setTimestampMsec(System.currentTimeMillis());
    request.setOperation(SwitchRequest.Operation.SET_ON);
    SwitchRequest deserializedRequest = new SwitchRequest(ByteBuffer.wrap(request.serialize()));
    assertEquals(request, deserializedRequest);
  }

  @Test
  public void serializeDeserializeStatusRequestTest() {
    StatusRequest request = new StatusRequest();
    request.setRequestId(UUID.randomUUID());
    request.setTimestampMsec(System.currentTimeMillis());
    StatusRequest deserializedRequest = new StatusRequest(ByteBuffer.wrap(request.serialize()));
    assertEquals(request, deserializedRequest);
  }

  @Test
  public void serializeDeserializeStatusReplyTest() {
    StatusReply reply = new StatusReply();
    reply.setDeviceId(UUID.randomUUID());
    StatusReply deserializedReply = new StatusReply(ByteBuffer.wrap(reply.serialize()));
    assertEquals(reply, deserializedReply);
  }
}
