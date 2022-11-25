package prodcons.v1;

public class Message {
	private String content;
	
	public Message(String content) {
		this.content = content;
	}
	
	public int size() {
		return this.content.length();
	}
	
	public String getContent() {
		return this.content;
	}
}
