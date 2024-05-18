package com.sixlegs.png;


class TextChunkImpl implements TextChunk {
	private final String keyword;
	private final String text;
	private final String language;
	private final String translated;
	private final int type;

	public TextChunkImpl(String string, String string2, String string3, String string4, int int1) {
		this.keyword = string;
		this.text = string2;
		this.language = string3;
		this.translated = string4;
		this.type = int1;
	}

	public String getKeyword() {
		return this.keyword;
	}

	public String getTranslatedKeyword() {
		return this.translated;
	}

	public String getLanguage() {
		return this.language;
	}

	public String getText() {
		return this.text;
	}

	public int getType() {
		return this.type;
	}
}
