package android.pubcrawl.database;

public class PubElement {

  public final static String PUBID = "PUBID";
  private long id;
  private String name;
  private String descrip;
  private String phone;
  private String location;
  private String rating;
  private double lat;
  private double lng;
  private String zipcode;

  public PubElement(long id, String name, String description, String phone,
          String location, String rating, double lat, double lng, 
          String zipcode) {
    this.id = id;
    this.name = name;
    this.descrip = description;
    this.phone = phone;
    this.location = location;
    this.rating = rating;
    this.lat = lat;
    this.lng = lng;
    this.zipcode = zipcode;
  }

  public PubElement() {
    this.id = 0;
    this.name = "";
    this.descrip = "";
    this.phone = "";
    this.location = "";
    this.rating = "";
    this.lat = 0.0;
    this.lng = 0.0;
    this.zipcode = "";
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getDescrip() {
    return descrip;
  }

  public void setDescrip(String descrip) {
    this.descrip = descrip;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public double getLng() {
    return lng;
  }

  public void setLng(double lng) {
    this.lng = lng;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRating() {
    return rating;
  }

  public void setRating(String rating) {
    this.rating = rating;
  }

  public String getZipcode() {
    return zipcode;
  }

  public void setZipcode(String zipcode) {
    this.zipcode = zipcode;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("PUB ELEMENT:\n");
    sb.append("ID:");
    sb.append(id);
    sb.append("\n");
    sb.append("NAME:");
    sb.append(name);
    sb.append("\n");
    sb.append("DESCRIPTION:");
    sb.append(descrip);
    sb.append("\n");
    sb.append("PHONE:");
    sb.append(phone);
    sb.append("\n");
    sb.append("RATING:");
    sb.append(rating);
    sb.append("\n");
    sb.append("LAT:");
    sb.append(lat);
    sb.append("\n");
    sb.append("LNG:");
    sb.append(lng);
    sb.append("\n");
    sb.append("ZIPCODE:");
    sb.append(zipcode);
    sb.append("\n");
    return sb.toString();
  }
}
