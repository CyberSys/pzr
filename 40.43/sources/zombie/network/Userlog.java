package zombie.network;


public class Userlog {
	private String username;
	private String type;
	private String text;
	private String issuedBy;
	private int amount;

	public Userlog(String string, String string2, String string3, String string4, int int1) {
		this.username = string;
		this.type = string2;
		this.text = string3;
		this.issuedBy = string4;
		this.amount = int1;
	}

	public String getUsername() {
		return this.username;
	}

	public String getType() {
		return this.type;
	}

	public String getText() {
		return this.text;
	}

	public String getIssuedBy() {
		return this.issuedBy;
	}

	public int getAmount() {
		return this.amount;
	}

	public void setAmount(int int1) {
		this.amount = int1;
	}

	public static enum UserlogType {

		AdminLog,
		Kicked,
		Banned,
		DupeItem,
		LuaChecksum,
		WarningPoint,
		index;

		private UserlogType(int int1) {
			this.index = int1;
		}
		public int index() {
			return this.index;
		}
		public static Userlog.UserlogType fromIndex(int int1) {
			return ((Userlog.UserlogType[])Userlog.UserlogType.class.getEnumConstants())[int1];
		}
		public static Userlog.UserlogType FromString(String string) {
			return valueOf(string);
		}
	}
}
