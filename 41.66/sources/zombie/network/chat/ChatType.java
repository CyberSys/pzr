package zombie.network.chat;



public enum ChatType {

	notDefined,
	general,
	whisper,
	say,
	shout,
	faction,
	safehouse,
	radio,
	admin,
	server,
	value,
	titleID;

	public static ChatType valueOf(Integer integer) {
		if (general.value == integer) {
			return general;
		} else if (whisper.value == integer) {
			return whisper;
		} else if (say.value == integer) {
			return say;
		} else if (shout.value == integer) {
			return shout;
		} else if (faction.value == integer) {
			return faction;
		} else if (safehouse.value == integer) {
			return safehouse;
		} else if (radio.value == integer) {
			return radio;
		} else if (admin.value == integer) {
			return admin;
		} else {
			return server.value == integer ? server : notDefined;
		}
	}
	private ChatType(Integer integer, String string) {
		this.value = integer;
		this.titleID = string;
	}
	public int getValue() {
		return this.value;
	}
	public String getTitleID() {
		return this.titleID;
	}
	private static ChatType[] $values() {
		return new ChatType[]{notDefined, general, whisper, say, shout, faction, safehouse, radio, admin, server};
	}
}
