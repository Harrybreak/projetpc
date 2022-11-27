package prodcons.v2;

public class Message {
	
	private int length;
	private String content;
	
	public Message(String content) {
		this.content = content;
		this.length = content.length();
	}
	
	public int get_length() {
		return length;
	}
	
	public String get_content() {
		return content;
	}
}
