package android.pubcrawl.database;

public class ServiceElement {

  private long serviceID;
  private long time;
  private String name;
  private String status;

  public ServiceElement(long serviceID, long time, String name, String status) {
    this.serviceID = serviceID;
    this.time = time;
    this.name = name;
    this.status = status;
  }

  public ServiceElement() {
    this.serviceID = -1;
    this.time = 0;
    this.name = "";
    this.status = ServiceDB.STATUSOPT.STOPPED.name();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getServiceID() {
    return serviceID;
  }

  public void setServiceID(long serviceID) {
    this.serviceID = serviceID;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }
}
