package com.alisa.lswitch.client;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.alisa.lswitch.client.model.BaseRequest;
import com.alisa.lswitch.client.model.StatusReply;
import com.alisa.lswitch.client.model.StatusRequest;
import com.alisa.lswitch.client.model.SwitchRequest;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class SerializerTest {

  @Test
  public void serializeDeserializeSwitchRequestTest() {
    SwitchRequest request = new SwitchRequest();
    request.setRequestId(UUID.randomUUID());
    request.setTimestampMsec(System.currentTimeMillis());
    request.setDeviceId(UUID.randomUUID());
    request.setOperation(SwitchRequest.Operation.SET_ON);
    final ByteBuffer serializedRequest = ByteBuffer.wrap(request.serialize());
    SwitchRequest deserializedRequest = new SwitchRequest(serializedRequest);
    assertEquals(request, deserializedRequest);


    request.sign(new byte[]{1,2,3,4});
  }

  @Test
  public void serializeDeserializeStatusRequestTest() {
    StatusRequest request = new StatusRequest();
    final ByteBuffer serializedRequest = ByteBuffer.wrap(request.serialize());
    StatusRequest deserializedRequest = new StatusRequest(serializedRequest);
    assertEquals(request, deserializedRequest);
  }

  @Test
  public void serializeDeserializeStatusReplyTest() {
    StatusReply reply = new StatusReply();
    reply.setDeviceId(UUID.randomUUID());
    reply.setDeviceType("switch");
    final ByteBuffer serializedReply = ByteBuffer.wrap(reply.serialize());
    StatusReply deserializedReply = new StatusReply(serializedReply);
    assertEquals(reply, deserializedReply);
  }

  @Test
  public void signatureTest() {
    StatusRequest request = new StatusRequest();
    final byte[] secret = new byte[]{1,2,3,4,5,6};
    request.sign(secret);
    assertTrue(request.verifySignature(secret));
    final ByteBuffer serializedRequest = ByteBuffer.wrap(request.serialize());
    StatusRequest deserializedRequest = new StatusRequest (serializedRequest);
    assertEquals(request, deserializedRequest);
  }
}
