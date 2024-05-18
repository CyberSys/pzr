package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class CommentHeader {
	public static final String TITLE = "TITLE";
	public static final String ARTIST = "ARTIST";
	public static final String ALBUM = "ALBUM";
	public static final String TRACKNUMBER = "TRACKNUMBER";
	public static final String VERSION = "VERSION";
	public static final String PERFORMER = "PERFORMER";
	public static final String COPYRIGHT = "COPYRIGHT";
	public static final String LICENSE = "LICENSE";
	public static final String ORGANIZATION = "ORGANIZATION";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String GENRE = "GENRE";
	public static final String DATE = "DATE";
	public static final String LOCATION = "LOCATION";
	public static final String CONTACT = "CONTACT";
	public static final String ISRC = "ISRC";
	private String vendor;
	private HashMap comments = new HashMap();
	private boolean framingBit;
	private static final long HEADER = 126896460427126L;

	public CommentHeader(BitInputStream bitInputStream) throws VorbisFormatException, IOException {
		if (bitInputStream.getLong(48) != 126896460427126L) {
			throw new VorbisFormatException("The identification header has an illegal leading.");
		} else {
			this.vendor = this.getString(bitInputStream);
			int int1 = bitInputStream.getInt(32);
			for (int int2 = 0; int2 < int1; ++int2) {
				String string = this.getString(bitInputStream);
				int int3 = string.indexOf(61);
				String string2 = string.substring(0, int3);
				String string3 = string.substring(int3 + 1);
				this.addComment(string2, string3);
			}

			this.framingBit = bitInputStream.getInt(8) != 0;
		}
	}

	private void addComment(String string, String string2) {
		ArrayList arrayList = (ArrayList)this.comments.get(string);
		if (arrayList == null) {
			arrayList = new ArrayList();
			this.comments.put(string, arrayList);
		}

		arrayList.add(string2);
	}

	public String getVendor() {
		return this.vendor;
	}

	public String getComment(String string) {
		ArrayList arrayList = (ArrayList)this.comments.get(string);
		return arrayList == null ? (String)null : (String)arrayList.get(0);
	}

	public String[] getComments(String string) {
		ArrayList arrayList = (ArrayList)this.comments.get(string);
		return arrayList == null ? new String[0] : (String[])((String[])arrayList.toArray(new String[arrayList.size()]));
	}

	public String getTitle() {
		return this.getComment("TITLE");
	}

	public String[] getTitles() {
		return this.getComments("TITLE");
	}

	public String getVersion() {
		return this.getComment("VERSION");
	}

	public String[] getVersions() {
		return this.getComments("VERSION");
	}

	public String getAlbum() {
		return this.getComment("ALBUM");
	}

	public String[] getAlbums() {
		return this.getComments("ALBUM");
	}

	public String getTrackNumber() {
		return this.getComment("TRACKNUMBER");
	}

	public String[] getTrackNumbers() {
		return this.getComments("TRACKNUMBER");
	}

	public String getArtist() {
		return this.getComment("ARTIST");
	}

	public String[] getArtists() {
		return this.getComments("ARTIST");
	}

	public String getPerformer() {
		return this.getComment("PERFORMER");
	}

	public String[] getPerformers() {
		return this.getComments("PERFORMER");
	}

	public String getCopyright() {
		return this.getComment("COPYRIGHT");
	}

	public String[] getCopyrights() {
		return this.getComments("COPYRIGHT");
	}

	public String getLicense() {
		return this.getComment("LICENSE");
	}

	public String[] getLicenses() {
		return this.getComments("LICENSE");
	}

	public String getOrganization() {
		return this.getComment("ORGANIZATION");
	}

	public String[] getOrganizations() {
		return this.getComments("ORGANIZATION");
	}

	public String getDescription() {
		return this.getComment("DESCRIPTION");
	}

	public String[] getDescriptions() {
		return this.getComments("DESCRIPTION");
	}

	public String getGenre() {
		return this.getComment("GENRE");
	}

	public String[] getGenres() {
		return this.getComments("GENRE");
	}

	public String getDate() {
		return this.getComment("DATE");
	}

	public String[] getDates() {
		return this.getComments("DATE");
	}

	public String getLocation() {
		return this.getComment("LOCATION");
	}

	public String[] getLocations() {
		return this.getComments("LOCATION");
	}

	public String getContact() {
		return this.getComment("CONTACT");
	}

	public String[] getContacts() {
		return this.getComments("CONTACT");
	}

	public String getIsrc() {
		return this.getComment("ISRC");
	}

	public String[] getIsrcs() {
		return this.getComments("ISRC");
	}

	private String getString(BitInputStream bitInputStream) throws IOException, VorbisFormatException {
		int int1 = bitInputStream.getInt(32);
		byte[] byteArray = new byte[int1];
		for (int int2 = 0; int2 < int1; ++int2) {
			byteArray[int2] = (byte)bitInputStream.getInt(8);
		}

		return new String(byteArray, "UTF-8");
	}
}
