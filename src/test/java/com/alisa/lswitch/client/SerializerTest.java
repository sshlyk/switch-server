package com.alisa.lswitch.client;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

public class SerializerTest {

  private Serializer serializer;

  @Before
  public void setup() {
    serializer = new Serializer(new Auth("secret".getBytes()));
  }

  @Test
  public void serializeDeserializeSwitchRequestSimpleTest() {
    SwitchRequest request = new SwitchRequest();
    request.setRequestId(UUID.randomUUID());
    request.setTimestampMsec(System.currentTimeMillis());
    request.setOperation(SwitchRequest.Operation.SET_ON);
    verify(request, SwitchRequest.class);
  }

  @Test
  public void serializeDeserializeStatusRequestSimpleTest() {
    StatusRequest request = new StatusRequest();
    request.setRequestId(UUID.randomUUID());
    request.setTimestampMsec(System.currentTimeMillis());
    verify(request, StatusRequest.class);
  }

  private<T extends Request> void verify(final Request expectedRequest, final Class<T> requestType) {
    byte[] requestBytes = serializer.serialize(expectedRequest);
    assertNotNull(requestBytes);
    assertTrue(requestBytes.length != 0);
    Request deserializedRequest = serializer.deserialize(requestBytes, requestType);
    assertEquals(expectedRequest, deserializedRequest);
  }
}
