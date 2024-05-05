package zombie.fileSystem;



public enum FileSeekMode {

	BEGIN,
	END,
	CURRENT;

	private static FileSeekMode[] $values() {
		return new FileSeekMode[]{BEGIN, END, CURRENT};
	}
}
