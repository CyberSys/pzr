package zombie.iso.areas;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.network.GameClient;


public class NonPvpZone {
	private int x;
	private int y;
	private int x2;
	private int y2;
	private int size;
	private String title;
	public static ArrayList nonPvpZoneList = new ArrayList();

	public NonPvpZone() {
	}

	public NonPvpZone(String string, int int1, int int2, int int3, int int4) {
		int int5;
		if (int1 > int3) {
			int5 = int3;
			int3 = int1;
			int1 = int5;
		}

		if (int2 > int4) {
			int5 = int4;
			int4 = int2;
			int2 = int5;
		}

		this.setX(int1);
		this.setX2(int3);
		this.setY(int2);
		this.setY2(int4);
		this.title = string;
		this.size = Math.abs(int1 - int3 + (int2 - int4));
		this.syncNonPvpZone(false);
	}

	public static NonPvpZone addNonPvpZone(String string, int int1, int int2, int int3, int int4) {
		NonPvpZone nonPvpZone = new NonPvpZone(string, int1, int2, int3, int4);
		nonPvpZoneList.add(nonPvpZone);
		return nonPvpZone;
	}

	public static void removeNonPvpZone(String string, boolean boolean1) {
		NonPvpZone nonPvpZone = getZoneByTitle(string);
		if (nonPvpZone != null) {
			nonPvpZoneList.remove(nonPvpZone);
			if (!boolean1) {
				nonPvpZone.syncNonPvpZone(true);
			}
		}
	}

	public static NonPvpZone getZoneByTitle(String string) {
		for (int int1 = 0; int1 < nonPvpZoneList.size(); ++int1) {
			NonPvpZone nonPvpZone = (NonPvpZone)nonPvpZoneList.get(int1);
			if (nonPvpZone.getTitle().equals(string)) {
				return nonPvpZone;
			}
		}

		return null;
	}

	public static NonPvpZone getNonPvpZone(int int1, int int2) {
		for (int int3 = 0; int3 < nonPvpZoneList.size(); ++int3) {
			NonPvpZone nonPvpZone = (NonPvpZone)nonPvpZoneList.get(int3);
			if (int1 >= nonPvpZone.getX() && int1 < nonPvpZone.getX2() && int2 >= nonPvpZone.getY() && int2 < nonPvpZone.getY2()) {
				return nonPvpZone;
			}
		}

		return null;
	}

	public static ArrayList getAllZones() {
		return nonPvpZoneList;
	}

	public void syncNonPvpZone(boolean boolean1) {
		if (GameClient.bClient) {
			GameClient.sendNonPvpZone(this, boolean1);
		}
	}

	public void save(ByteBuffer byteBuffer) {
		byteBuffer.putInt(this.getX());
		byteBuffer.putInt(this.getY());
		byteBuffer.putInt(this.getX2());
		byteBuffer.putInt(this.getY2());
		byteBuffer.putInt(this.getSize());
		GameWindow.WriteString(byteBuffer, this.getTitle());
	}

	public void load(ByteBuffer byteBuffer, int int1) {
		this.setX(byteBuffer.getInt());
		this.setY(byteBuffer.getInt());
		this.setX2(byteBuffer.getInt());
		this.setY2(byteBuffer.getInt());
		this.setSize(byteBuffer.getInt());
		this.setTitle(GameWindow.ReadString(byteBuffer));
	}

	public int getX() {
		return this.x;
	}

	public void setX(int int1) {
		this.x = int1;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int int1) {
		this.y = int1;
	}

	public int getX2() {
		return this.x2;
	}

	public void setX2(int int1) {
		this.x2 = int1;
	}

	public int getY2() {
		return this.y2;
	}

	public void setY2(int int1) {
		this.y2 = int1;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String string) {
		this.title = string;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int int1) {
		this.size = int1;
	}
}
