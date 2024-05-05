package zombie.network;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.znet.ISteamWorkshopCallback;
import zombie.core.znet.SteamUGCDetails;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.core.znet.SteamWorkshopItem;
import zombie.debug.DebugLog;


public class GameServerWorkshopItems {

	private static void noise(String string) {
		DebugLog.log("Workshop: " + string);
	}

	public static boolean Install(ArrayList arrayList) {
		if (!GameServer.bServer) {
			return false;
		} else if (arrayList.isEmpty()) {
			return true;
		} else {
			ArrayList arrayList2 = new ArrayList();
			Iterator iterator = arrayList.iterator();
			long long1;
			while (iterator.hasNext()) {
				long1 = (Long)iterator.next();
				GameServerWorkshopItems.WorkshopItem workshopItem = new GameServerWorkshopItems.WorkshopItem(long1);
				arrayList2.add(workshopItem);
			}

			if (!QueryItemDetails(arrayList2)) {
				return false;
			} else {
				while (true) {
					SteamUtils.runLoop();
					boolean boolean1 = false;
					for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
						GameServerWorkshopItems.WorkshopItem workshopItem2 = (GameServerWorkshopItems.WorkshopItem)arrayList2.get(int1);
						workshopItem2.update();
						if (workshopItem2.state == GameServerWorkshopItems.WorkshopInstallState.Fail) {
							return false;
						}

						if (workshopItem2.state != GameServerWorkshopItems.WorkshopInstallState.Ready) {
							boolean1 = true;
							break;
						}
					}

					if (!boolean1) {
						GameServer.WorkshopInstallFolders = new String[arrayList.size()];
						GameServer.WorkshopTimeStamps = new long[arrayList.size()];
						for (int int2 = 0; int2 < arrayList.size(); ++int2) {
							long1 = (Long)arrayList.get(int2);
							String string = SteamWorkshop.instance.GetItemInstallFolder(long1);
							if (string == null) {
								noise("GetItemInstallFolder() failed ID=" + long1);
								return false;
							}

							noise(long1 + " installed to " + string);
							GameServer.WorkshopInstallFolders[int2] = string;
							GameServer.WorkshopTimeStamps[int2] = SteamWorkshop.instance.GetItemInstallTimeStamp(long1);
						}

						return true;
					}

					try {
						Thread.sleep(33L);
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		}
	}

	private static boolean QueryItemDetails(ArrayList arrayList) {
		long[] longArray = new long[arrayList.size()];
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			GameServerWorkshopItems.WorkshopItem workshopItem = (GameServerWorkshopItems.WorkshopItem)arrayList.get(int1);
			longArray[int1] = workshopItem.ID;
		}

		GameServerWorkshopItems.ItemQuery itemQuery = new GameServerWorkshopItems.ItemQuery();
		itemQuery.handle = SteamWorkshop.instance.CreateQueryUGCDetailsRequest(longArray, itemQuery);
		if (itemQuery.handle == 0L) {
			return false;
		} else {
			while (true) {
				SteamUtils.runLoop();
				if (itemQuery.isCompleted()) {
					Iterator iterator = itemQuery.details.iterator();
					while (true) {
						while (iterator.hasNext()) {
							SteamUGCDetails steamUGCDetails = (SteamUGCDetails)iterator.next();
							Iterator iterator2 = arrayList.iterator();
							while (iterator2.hasNext()) {
								GameServerWorkshopItems.WorkshopItem workshopItem2 = (GameServerWorkshopItems.WorkshopItem)iterator2.next();
								if (workshopItem2.ID == steamUGCDetails.getID()) {
									workshopItem2.details = steamUGCDetails;
									break;
								}
							}
						}

						return true;
					}
				}

				if (itemQuery.isNotCompleted()) {
					return false;
				}

				try {
					Thread.sleep(33L);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	private static class WorkshopItem implements ISteamWorkshopCallback {
		long ID;
		GameServerWorkshopItems.WorkshopInstallState state;
		long downloadStartTime;
		long downloadQueryTime;
		String error;
		SteamUGCDetails details;

		WorkshopItem(long long1) {
			this.state = GameServerWorkshopItems.WorkshopInstallState.CheckItemState;
			this.ID = long1;
		}

		void update() {
			switch (this.state) {
			case CheckItemState: 
				this.CheckItemState();
				break;
			
			case DownloadPending: 
				this.DownloadPending();
			
			case Ready: 
			
			}
		}

		void setState(GameServerWorkshopItems.WorkshopInstallState workshopInstallState) {
			GameServerWorkshopItems.noise("item state " + this.state + " -> " + workshopInstallState + " ID=" + this.ID);
			this.state = workshopInstallState;
		}

		void CheckItemState() {
			long long1 = SteamWorkshop.instance.GetItemState(this.ID);
			String string = SteamWorkshopItem.ItemState.toString(long1);
			GameServerWorkshopItems.noise("GetItemState()=" + string + " ID=" + this.ID);
			if (SteamWorkshopItem.ItemState.Installed.and(long1) && this.details != null && this.details.getTimeCreated() != 0L && this.details.getTimeUpdated() != SteamWorkshop.instance.GetItemInstallTimeStamp(this.ID)) {
				GameServerWorkshopItems.noise("Installed status but timeUpdated doesn\'t match!!!");
				this.RemoveFolderForReinstall();
				long1 |= (long)SteamWorkshopItem.ItemState.NeedsUpdate.getValue();
			}

			if (long1 != (long)SteamWorkshopItem.ItemState.None.getValue() && !SteamWorkshopItem.ItemState.NeedsUpdate.and(long1)) {
				if (SteamWorkshopItem.ItemState.Installed.and(long1)) {
					this.setState(GameServerWorkshopItems.WorkshopInstallState.Ready);
				} else {
					this.error = "UnknownItemState";
					this.setState(GameServerWorkshopItems.WorkshopInstallState.Fail);
				}
			} else if (SteamWorkshop.instance.DownloadItem(this.ID, true, this)) {
				this.setState(GameServerWorkshopItems.WorkshopInstallState.DownloadPending);
				this.downloadStartTime = System.currentTimeMillis();
			} else {
				this.error = "DownloadItemFalse";
				this.setState(GameServerWorkshopItems.WorkshopInstallState.Fail);
			}
		}

		void RemoveFolderForReinstall() {
			String string = SteamWorkshop.instance.GetItemInstallFolder(this.ID);
			if (string == null) {
				GameServerWorkshopItems.noise("not removing install folder because GetItemInstallFolder() failed ID=" + this.ID);
			} else {
				Path path = Paths.get(string);
				if (!Files.exists(path, new LinkOption[0])) {
					GameServerWorkshopItems.noise("not removing install folder because it does not exist : \"" + string + "\"");
				} else {
					try {
						Files.walkFileTree(path, new SimpleFileVisitor(){
							
							public FileVisitResult visitFile(Path string, BasicFileAttributes path) throws IOException {
								Files.delete(string);
								return FileVisitResult.CONTINUE;
							}

							
							public FileVisitResult postVisitDirectory(Path string, IOException path) throws IOException {
								Files.delete(string);
								return FileVisitResult.CONTINUE;
							}
						});
					} catch (Exception exception) {
						ExceptionLogger.logException(exception);
					}
				}
			}
		}

		void DownloadPending() {
			long long1 = System.currentTimeMillis();
			if (this.downloadQueryTime + 100L <= long1) {
				this.downloadQueryTime = long1;
				long long2 = SteamWorkshop.instance.GetItemState(this.ID);
				String string = SteamWorkshopItem.ItemState.toString(long2);
				GameServerWorkshopItems.noise("DownloadPending GetItemState()=" + string + " ID=" + this.ID);
				if (SteamWorkshopItem.ItemState.NeedsUpdate.and(long2)) {
					long[] longArray = new long[2];
					if (SteamWorkshop.instance.GetItemDownloadInfo(this.ID, longArray)) {
						GameServerWorkshopItems.noise("download " + longArray[0] + "/" + longArray[1] + " ID=" + this.ID);
					}
				}
			}
		}

		public void onItemCreated(long long1, boolean boolean1) {
		}

		public void onItemNotCreated(int int1) {
		}

		public void onItemUpdated(boolean boolean1) {
		}

		public void onItemNotUpdated(int int1) {
		}

		public void onItemSubscribed(long long1) {
			GameServerWorkshopItems.noise("onItemSubscribed itemID=" + long1);
		}

		public void onItemNotSubscribed(long long1, int int1) {
			GameServerWorkshopItems.noise("onItemNotSubscribed itemID=" + long1 + " result=" + int1);
		}

		public void onItemDownloaded(long long1) {
			GameServerWorkshopItems.noise("onItemDownloaded itemID=" + long1 + " time=" + (System.currentTimeMillis() - this.downloadStartTime) + " ms");
			if (long1 == this.ID) {
				SteamWorkshop.instance.RemoveCallback(this);
				this.setState(GameServerWorkshopItems.WorkshopInstallState.CheckItemState);
			}
		}

		public void onItemNotDownloaded(long long1, int int1) {
			GameServerWorkshopItems.noise("onItemNotDownloaded itemID=" + long1 + " result=" + int1);
			if (long1 == this.ID) {
				SteamWorkshop.instance.RemoveCallback(this);
				this.error = "ItemNotDownloaded";
				this.setState(GameServerWorkshopItems.WorkshopInstallState.Fail);
			}
		}

		public void onItemQueryCompleted(long long1, int int1) {
			GameServerWorkshopItems.noise("onItemQueryCompleted handle=" + long1 + " numResult=" + int1);
		}

		public void onItemQueryNotCompleted(long long1, int int1) {
			GameServerWorkshopItems.noise("onItemQueryNotCompleted handle=" + long1 + " result=" + int1);
		}
	}

	private static enum WorkshopInstallState {

		CheckItemState,
		DownloadPending,
		Ready,
		Fail;

		private static GameServerWorkshopItems.WorkshopInstallState[] $values() {
			return new GameServerWorkshopItems.WorkshopInstallState[]{CheckItemState, DownloadPending, Ready, Fail};
		}
	}

	private static final class ItemQuery implements ISteamWorkshopCallback {
		long handle;
		ArrayList details;
		boolean bCompleted;
		boolean bNotCompleted;

		public boolean isCompleted() {
			return this.bCompleted;
		}

		public boolean isNotCompleted() {
			return this.bNotCompleted;
		}

		public void onItemCreated(long long1, boolean boolean1) {
		}

		public void onItemNotCreated(int int1) {
		}

		public void onItemUpdated(boolean boolean1) {
		}

		public void onItemNotUpdated(int int1) {
		}

		public void onItemSubscribed(long long1) {
		}

		public void onItemNotSubscribed(long long1, int int1) {
		}

		public void onItemDownloaded(long long1) {
		}

		public void onItemNotDownloaded(long long1, int int1) {
		}

		public void onItemQueryCompleted(long long1, int int1) {
			GameServerWorkshopItems.noise("onItemQueryCompleted handle=" + long1 + " numResult=" + int1);
			if (long1 == this.handle) {
				SteamWorkshop.instance.RemoveCallback(this);
				ArrayList arrayList = new ArrayList();
				for (int int2 = 0; int2 < int1; ++int2) {
					SteamUGCDetails steamUGCDetails = SteamWorkshop.instance.GetQueryUGCResult(long1, int2);
					if (steamUGCDetails != null) {
						arrayList.add(steamUGCDetails);
					}
				}

				this.details = arrayList;
				SteamWorkshop.instance.ReleaseQueryUGCRequest(long1);
				this.bCompleted = true;
			}
		}

		public void onItemQueryNotCompleted(long long1, int int1) {
			GameServerWorkshopItems.noise("onItemQueryNotCompleted handle=" + long1 + " result=" + int1);
			if (long1 == this.handle) {
				SteamWorkshop.instance.RemoveCallback(this);
				SteamWorkshop.instance.ReleaseQueryUGCRequest(long1);
				this.bNotCompleted = true;
			}
		}
	}
}
