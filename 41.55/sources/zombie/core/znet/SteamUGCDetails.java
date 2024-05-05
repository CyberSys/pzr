package zombie.core.znet;

import zombie.debug.DebugLog;


public class SteamUGCDetails {
	private long ID;
	private String title;
	private int fileSize;
	private long[] childIDs;

	public SteamUGCDetails(long long1, String string, int int1, long[] longArray) {
		this.ID = long1;
		this.title = string;
		this.fileSize = int1;
		this.childIDs = longArray;
	}

	public long getID() {
		return this.ID;
	}

	public String getIDString() {
		return SteamUtils.convertSteamIDToString(this.ID);
	}

	public String getTitle() {
		return this.title;
	}

	public int getFileSize() {
		return this.fileSize;
	}

	public long[] getChildren() {
		return this.childIDs;
	}

	public int getNumChildren() {
		return this.childIDs == null ? 0 : this.childIDs.length;
	}

	public long getChildID(int int1) {
		if (int1 >= 0 && int1 < this.getNumChildren()) {
			return this.childIDs[int1];
		} else {
			throw new IndexOutOfBoundsException("invalid child index");
		}
	}

	public String getState() {
		long long1 = SteamWorkshop.instance.GetItemState(this.ID);
		if (!SteamWorkshopItem.ItemState.Subscribed.and(long1)) {
			return "NotSubscribed";
		} else if (SteamWorkshopItem.ItemState.DownloadPending.and(long1)) {
			String string = SteamWorkshopItem.ItemState.toString(long1);
			DebugLog.log(string + " ID=" + this.ID);
			return "Downloading";
		} else if (SteamWorkshopItem.ItemState.NeedsUpdate.and(long1)) {
			return "NeedsUpdate";
		} else {
			return SteamWorkshopItem.ItemState.Installed.and(long1) ? "Installed" : "Error";
		}
	}
}
