package android.pubcrawl.database;

public class CrawlPubElement {

  public final static String PUBPOS = "PUBPOS";
  private long crawlPubID;
  private long pubID;
  private int position;

  public CrawlPubElement(long pubID, int position) {
    this.pubID = pubID;
    this.position = position;
  }

  public CrawlPubElement() {
    this.crawlPubID = -1;
    this.pubID = -1;
    this.position = 0;
  }

  public long getCrawlPubID() {
    return crawlPubID;
  }

  public void setCrawlPubID(long crawlPubID) {
    this.crawlPubID = crawlPubID;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public long getPubID() {
    return pubID;
  }

  public void setPubID(long pubID) {
    this.pubID = pubID;
  }

  @Override
  public String toString(){
    StringBuilder elementString = new StringBuilder();
    elementString.append("CRAWLPUBID:");
    elementString.append(crawlPubID);
    elementString.append("\n");
    elementString.append("POSITION:");
    elementString.append(position);
    elementString.append("\n");
    elementString.append("PUBID:");
    elementString.append(pubID);
    elementString.append("\n");
    return elementString.toString();
  }

}
