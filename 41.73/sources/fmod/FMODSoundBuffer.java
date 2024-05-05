package fmod;


public class FMODSoundBuffer {
	private long sound;
	private byte[] buf1;
	private Long buf1size;
	private Long vadStatus;
	private Long loudness;
	private boolean intError;

	public FMODSoundBuffer(long long1) {
		this.sound = long1;
		this.buf1 = new byte[2048];
		this.buf1size = new Long(0L);
		this.vadStatus = new Long(0L);
		this.loudness = new Long(0L);
		this.intError = false;
	}

	public boolean pull(long long1) {
		int int1 = javafmod.FMOD_Sound_GetData(this.sound, this.buf1, this.buf1size, this.vadStatus, this.loudness);
		this.intError = int1 == -1;
		return int1 == 0;
	}

	public byte[] buf() {
		return this.buf1;
	}

	public long get_size() {
		return this.buf1size;
	}

	public long get_vad() {
		return this.vadStatus;
	}

	public long get_loudness() {
		return this.loudness;
	}

	public boolean get_interror() {
		return this.intError;
	}
}
