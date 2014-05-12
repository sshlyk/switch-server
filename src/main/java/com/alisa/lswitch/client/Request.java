package com.alisa.lswitch.client;

import java.util.UUID;

/**
 * Base class for all com.alisa.lswitch.client.
 */
public abstract class Request {

  private UUID requestId;
  private long timestampMsec;

  public UUID getRequestId() {
    return requestId;
  }

  public void setRequestId(UUID requestId) {
    this.requestId = requestId;
  }

  public long getTimestampMsec() {
    return timestampMsec;
  }

  public void setTimestampMsec(long timestampMsec) {
    this.timestampMsec = timestampMsec;
  }

  @Override
  public String toString() {
    return "Request{" +
        "requestId=" + requestId +
        ", timestampMsec=" + timestampMsec +
        '}';
  }

  /* Auto-generated */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Request request = (Request) o;

    if (timestampMsec != request.timestampMsec) return false;
    if (requestId != null ? !requestId.equals(request.requestId) : request.requestId != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = requestId != null ? requestId.hashCode() : 0;
    result = 31 * result + (int) (timestampMsec ^ (timestampMsec >>> 32));
    return result;
  }
}
