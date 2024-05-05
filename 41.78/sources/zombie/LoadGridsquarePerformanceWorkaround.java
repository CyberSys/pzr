package zombie;

import zombie.characters.IsoPlayer;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.iso.ContainerOverlays;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.TileOverlays;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class LoadGridsquarePerformanceWorkaround {
	public static void init(int int1, int int2) {
		if (!GameClient.bClient) {
			LoadGridsquarePerformanceWorkaround.ItemPicker.instance.init();
		}
	}

	public static void LoadGridsquare(IsoGridSquare square) {
		if (LoadGridsquarePerformanceWorkaround.ItemPicker.instance.begin(square)) {
			IsoObject[] objectArray = (IsoObject[])square.getObjects().getElements();
			int int1 = square.getObjects().size();
			for (int int2 = 0; int2 < int1; ++int2) {
				IsoObject object = objectArray[int2];
				if (!(object instanceof IsoWorldInventoryObject)) {
					if (!GameClient.bClient) {
						LoadGridsquarePerformanceWorkaround.ItemPicker.instance.checkObject(object);
					}

					if (object.sprite != null && object.sprite.name != null && !ContainerOverlays.instance.hasOverlays(object)) {
						TileOverlays.instance.updateTileOverlaySprite(object);
					}
				}
			}
		}

		LoadGridsquarePerformanceWorkaround.ItemPicker.instance.end(square);
	}

	private static class ItemPicker {
		public static final LoadGridsquarePerformanceWorkaround.ItemPicker instance = new LoadGridsquarePerformanceWorkaround.ItemPicker();
		private IsoGridSquare square;

		public void init() {
		}

		public boolean begin(IsoGridSquare square) {
			if (square.isOverlayDone()) {
				this.square = null;
				return false;
			} else {
				this.square = square;
				return true;
			}
		}

		public void checkObject(IsoObject object) {
			IsoSprite sprite = object.getSprite();
			if (sprite != null && sprite.getName() != null) {
				ItemContainer itemContainer = object.getContainer();
				if (itemContainer != null && !itemContainer.isExplored()) {
					ItemPickerJava.fillContainer(itemContainer, IsoPlayer.getInstance());
					itemContainer.setExplored(true);
					if (GameServer.bServer) {
						GameServer.sendItemsInContainer(object, itemContainer);
					}
				}

				if (itemContainer == null || !itemContainer.isEmpty()) {
					ItemPickerJava.updateOverlaySprite(object);
				}
			}
		}

		public void end(IsoGridSquare square) {
			square.setOverlayDone(true);
		}
	}
}
