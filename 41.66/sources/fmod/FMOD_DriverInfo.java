package fmod;


public class FMOD_DriverInfo {
	public int id;
	public String name;
	public int guid;
	public int systemrate;
	public int speakermode;
	public int speakerchannels;

	public FMOD_DriverInfo() {
		this.id = -1;
		this.name = "";
		this.guid = 0;
		this.systemrate = 8000;
		this.speakermode = 0;
		this.speakerchannels = 1;
	}

	public FMOD_DriverInfo(int int1, String string) {
		this.id = int1;
		this.name = string;
		this.guid = 0;
		this.systemrate = 8000;
		this.speakermode = 0;
		this.speakerchannels = 1;
	}
}
