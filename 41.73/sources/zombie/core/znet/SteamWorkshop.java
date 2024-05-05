package zombie.core.znet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.debug.DebugLog;
import zombie.network.GameServer;


public class SteamWorkshop implements ISteamWorkshopCallback {
	public static final SteamWorkshop instance = new SteamWorkshop();
	private ArrayList stagedItems = new ArrayList();
	private ArrayList callbacks = new ArrayList();

	public static void init() {
		if (SteamUtils.isSteamModeEnabled()) {
			instance.n_Init();
		}

		if (!GameServer.bServer) {
			instance.initWorkshopFolder();
		}
	}

	public static void shutdown() {
		if (SteamUtils.isSteamModeEnabled()) {
			instance.n_Shutdown();
		}
	}

	private void copyFile(File file, File file2) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(file2);
				try {
					fileOutputStream.getChannel().transferFrom(fileInputStream.getChannel(), 0L, file.length());
				} catch (Throwable throwable) {
					try {
						fileOutputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				fileOutputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileInputStream.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private void copyFileOrFolder(File file, File file2) {
		if (file.isDirectory()) {
			if (!file2.mkdirs()) {
				return;
			}

			String[] stringArray = file.list();
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				this.copyFileOrFolder(new File(file, stringArray[int1]), new File(file2, stringArray[int1]));
			}
		} else {
			this.copyFile(file, file2);
		}
	}

	private void initWorkshopFolder() {
		File file = new File(this.getWorkshopFolder());
		if (file.exists() || file.mkdirs()) {
			File file2 = new File("Workshop" + File.separator + "ModTemplate");
			String string = this.getWorkshopFolder();
			File file3 = new File(string + File.separator + "ModTemplate");
			if (file2.exists() && !file3.exists()) {
				this.copyFileOrFolder(file2, file3);
			}
		}
	}

	public ArrayList loadStagedItems() {
		this.stagedItems.clear();
		Iterator iterator = this.getStageFolders().iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			SteamWorkshopItem steamWorkshopItem = new SteamWorkshopItem(string);
			steamWorkshopItem.readWorkshopTxt();
			this.stagedItems.add(steamWorkshopItem);
		}

		return this.stagedItems;
	}

	public String getWorkshopFolder() {
		String string = ZomboidFileSystem.instance.getCacheDir();
		return string + File.separator + "Workshop";
	}

	public ArrayList getStageFolders() {
		ArrayList arrayList = new ArrayList();
		Path path = FileSystems.getDefault().getPath(this.getWorkshopFolder());
		try {
			if (!Files.isDirectory(path, new LinkOption[0])) {
				Files.createDirectories(path);
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return arrayList;
		}

		Filter filter = new Filter(){
    
    public boolean accept(Path arrayList) throws IOException {
        return Files.isDirectory(arrayList, new LinkOption[0]);
    }
};
		try {
			DirectoryStream directoryStream = Files.newDirectoryStream(path, filter);
			try {
				Iterator iterator = directoryStream.iterator();
				while (iterator.hasNext()) {
					Path path2 = (Path)iterator.next();
					String string = path2.toAbsolutePath().toString();
					arrayList.add(string);
				}
			} catch (Throwable throwable) {
				if (directoryStream != null) {
					try {
						directoryStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}
				}

				throw throwable;
			}

			if (directoryStream != null) {
				directoryStream.close();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return arrayList;
	}

	public boolean CreateWorkshopItem(SteamWorkshopItem steamWorkshopItem) {
		if (steamWorkshopItem.getID() != null) {
			throw new RuntimeException("can\'t recreate an existing item");
		} else {
			return this.n_CreateItem();
		}
	}

	public boolean SubmitWorkshopItem(SteamWorkshopItem steamWorkshopItem) {
		if (steamWorkshopItem.getID() != null && SteamUtils.isValidSteamID(steamWorkshopItem.getID())) {
			long long1 = SteamUtils.convertStringToSteamID(steamWorkshopItem.getID());
			if (!this.n_StartItemUpdate(long1)) {
				return false;
			} else if (!this.n_SetItemTitle(steamWorkshopItem.getTitle())) {
				return false;
			} else if (!this.n_SetItemDescription(steamWorkshopItem.getSubmitDescription())) {
				return false;
			} else {
				int int1 = steamWorkshopItem.getVisibilityInteger();
				if ("Mod Template".equals(steamWorkshopItem.getTitle())) {
					int1 = 2;
				}

				if (!this.n_SetItemVisibility(int1)) {
					return false;
				} else {
					if (!this.n_SetItemTags(steamWorkshopItem.getSubmitTags())) {
					}

					if (!this.n_SetItemContent(steamWorkshopItem.getContentFolder())) {
						return false;
					} else if (!this.n_SetItemPreview(steamWorkshopItem.getPreviewImage())) {
						return false;
					} else {
						return this.n_SubmitItemUpdate(steamWorkshopItem.getChangeNote());
					}
				}
			}
		} else {
			throw new RuntimeException("workshop ID is required");
		}
	}

	public boolean GetItemUpdateProgress(long[] longArray) {
		return this.n_GetItemUpdateProgress(longArray);
	}

	public String[] GetInstalledItemFolders() {
		return GameServer.bServer ? GameServer.WorkshopInstallFolders : this.n_GetInstalledItemFolders();
	}

	public long GetItemState(long long1) {
		return this.n_GetItemState(long1);
	}

	public String GetItemInstallFolder(long long1) {
		return this.n_GetItemInstallFolder(long1);
	}

	public long GetItemInstallTimeStamp(long long1) {
		return this.n_GetItemInstallTimeStamp(long1);
	}

	public boolean SubscribeItem(long long1, ISteamWorkshopCallback iSteamWorkshopCallback) {
		if (!this.callbacks.contains(iSteamWorkshopCallback)) {
			this.callbacks.add(iSteamWorkshopCallback);
		}

		return this.n_SubscribeItem(long1);
	}

	public boolean DownloadItem(long long1, boolean boolean1, ISteamWorkshopCallback iSteamWorkshopCallback) {
		if (!this.callbacks.contains(iSteamWorkshopCallback)) {
			this.callbacks.add(iSteamWorkshopCallback);
		}

		return this.n_DownloadItem(long1, boolean1);
	}

	public boolean GetItemDownloadInfo(long long1, long[] longArray) {
		return this.n_GetItemDownloadInfo(long1, longArray);
	}

	public long CreateQueryUGCDetailsRequest(long[] longArray, ISteamWorkshopCallback iSteamWorkshopCallback) {
		if (!this.callbacks.contains(iSteamWorkshopCallback)) {
			this.callbacks.add(iSteamWorkshopCallback);
		}

		return this.n_CreateQueryUGCDetailsRequest(longArray);
	}

	public SteamUGCDetails GetQueryUGCResult(long long1, int int1) {
		return this.n_GetQueryUGCResult(long1, int1);
	}

	public long[] GetQueryUGCChildren(long long1, int int1) {
		return this.n_GetQueryUGCChildren(long1, int1);
	}

	public boolean ReleaseQueryUGCRequest(long long1) {
		return this.n_ReleaseQueryUGCRequest(long1);
	}

	public void RemoveCallback(ISteamWorkshopCallback iSteamWorkshopCallback) {
		this.callbacks.remove(iSteamWorkshopCallback);
	}

	public String getIDFromItemInstallFolder(String string) {
		if (string != null && string.replace("\\", "/").contains("/workshop/content/108600/")) {
			File file = new File(string);
			String string2 = file.getName();
			if (SteamUtils.isValidSteamID(string2)) {
				return string2;
			}

			DebugLog.log("ERROR: " + string2 + " isn\'t a valid workshop item ID");
		}

		return null;
	}

	private native void n_Init();

	private native void n_Shutdown();

	private native boolean n_CreateItem();

	private native boolean n_StartItemUpdate(long long1);

	private native boolean n_SetItemTitle(String string);

	private native boolean n_SetItemDescription(String string);

	private native boolean n_SetItemVisibility(int int1);

	private native boolean n_SetItemTags(String[] stringArray);

	private native boolean n_SetItemContent(String string);

	private native boolean n_SetItemPreview(String string);

	private native boolean n_SubmitItemUpdate(String string);

	private native boolean n_GetItemUpdateProgress(long[] longArray);

	private native String[] n_GetInstalledItemFolders();

	private native long n_GetItemState(long long1);

	private native boolean n_SubscribeItem(long long1);

	private native boolean n_DownloadItem(long long1, boolean boolean1);

	private native String n_GetItemInstallFolder(long long1);

	private native long n_GetItemInstallTimeStamp(long long1);

	private native boolean n_GetItemDownloadInfo(long long1, long[] longArray);

	private native long n_CreateQueryUGCDetailsRequest(long[] longArray);

	private native SteamUGCDetails n_GetQueryUGCResult(long long1, int int1);

	private native long[] n_GetQueryUGCChildren(long long1, int int1);

	private native boolean n_ReleaseQueryUGCRequest(long long1);

	public void onItemCreated(long long1, boolean boolean1) {
		LuaEventManager.triggerEvent("OnSteamWorkshopItemCreated", SteamUtils.convertSteamIDToString(long1), boolean1);
	}

	public void onItemNotCreated(int int1) {
		LuaEventManager.triggerEvent("OnSteamWorkshopItemNotCreated", int1);
	}

	public void onItemUpdated(boolean boolean1) {
		LuaEventManager.triggerEvent("OnSteamWorkshopItemUpdated", boolean1);
	}

	public void onItemNotUpdated(int int1) {
		LuaEventManager.triggerEvent("OnSteamWorkshopItemNotUpdated", int1);
	}

	public void onItemSubscribed(long long1) {
		for (int int1 = 0; int1 < this.callbacks.size(); ++int1) {
			((ISteamWorkshopCallback)this.callbacks.get(int1)).onItemSubscribed(long1);
		}
	}

	public void onItemNotSubscribed(long long1, int int1) {
		for (int int2 = 0; int2 < this.callbacks.size(); ++int2) {
			((ISteamWorkshopCallback)this.callbacks.get(int2)).onItemNotSubscribed(long1, int1);
		}
	}

	public void onItemDownloaded(long long1) {
		for (int int1 = 0; int1 < this.callbacks.size(); ++int1) {
			((ISteamWorkshopCallback)this.callbacks.get(int1)).onItemDownloaded(long1);
		}
	}

	public void onItemNotDownloaded(long long1, int int1) {
		for (int int2 = 0; int2 < this.callbacks.size(); ++int2) {
			((ISteamWorkshopCallback)this.callbacks.get(int2)).onItemNotDownloaded(long1, int1);
		}
	}

	public void onItemQueryCompleted(long long1, int int1) {
		for (int int2 = 0; int2 < this.callbacks.size(); ++int2) {
			((ISteamWorkshopCallback)this.callbacks.get(int2)).onItemQueryCompleted(long1, int1);
		}
	}

	public void onItemQueryNotCompleted(long long1, int int1) {
		for (int int2 = 0; int2 < this.callbacks.size(); ++int2) {
			((ISteamWorkshopCallback)this.callbacks.get(int2)).onItemQueryNotCompleted(long1, int1);
		}
	}
}
