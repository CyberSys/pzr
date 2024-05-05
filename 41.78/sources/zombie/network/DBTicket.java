package zombie.network;


public class DBTicket {
	private String author = null;
	private String message = "";
	private int ticketID = 0;
	private boolean viewed = false;
	private DBTicket answer = null;
	private boolean isAnswer = false;

	public DBTicket(String string, String string2, int int1) {
		this.author = string;
		this.message = string2;
		this.ticketID = int1;
		this.viewed = this.viewed;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String string) {
		this.author = string;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String string) {
		this.message = string;
	}

	public int getTicketID() {
		return this.ticketID;
	}

	public void setTicketID(int int1) {
		this.ticketID = int1;
	}

	public boolean isViewed() {
		return this.viewed;
	}

	public void setViewed(boolean boolean1) {
		this.viewed = boolean1;
	}

	public DBTicket getAnswer() {
		return this.answer;
	}

	public void setAnswer(DBTicket dBTicket) {
		this.answer = dBTicket;
	}

	public boolean isAnswer() {
		return this.isAnswer;
	}

	public void setIsAnswer(boolean boolean1) {
		this.isAnswer = boolean1;
	}
}
