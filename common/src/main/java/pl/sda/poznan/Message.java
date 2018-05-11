package pl.sda.poznan;

import java.io.Serializable;

public class Message implements Serializable {

  private String header;
  private String data;
  private Character playerSign;

  private Message() {
  }

  public String getHeader() {
    return header;
  }

  public String getData() {
    return data;
  }

  public Character getPlayerSign() {
    return playerSign;
  }

  public static MessageBuilder builder() {
    return new MessageBuilder();
  }

  public static class MessageBuilder {

    private String header;
    private String data;
    private Character playerSign;

    public MessageBuilder header(String header) {
      this.header = header;
      return this;
    }

    public MessageBuilder data(String data) {
      this.data = data;
      return this;
    }

    public MessageBuilder playerSign(Character playerSign) {
      this.playerSign = playerSign;
      return this;
    }

    public Message build() {
      Message message = new Message();
      message.header = this.header;
      message.data = this.data;
      message.playerSign = this.playerSign;
      return message;
    }
  }

}
