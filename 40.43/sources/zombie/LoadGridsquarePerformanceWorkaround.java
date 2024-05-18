package zombie;

import zombie.characters.IsoPlayer;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class LoadGridsquarePerformanceWorkaround {

	public static void init(int int1, int int2) {
		if (!GameClient.bClient) {
			LoadGridsquarePerformanceWorkaround.ItemPicker.instance.init();
		}
	}

	public static void LoadGridsquare(IsoGridSquare square) {
		if (!GameClient.bClient) {
			LoadGridsquarePerformanceWorkaround.ItemPicker.instance.begin(square);
			for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
				IsoObject object = (IsoObject)square.getObjects().get(int1);
				if (!(object instanceof IsoWorldInventoryObject) && LoadGridsquarePerformanceWorkaround.ItemPicker.instance.square != null && object.sprite != null && object.sprite.name != null && ItemPickerJava.overlayMap.containsKey(object.sprite.name)) {
					LoadGridsquarePerformanceWorkaround.ItemPicker.instance.checkObject(object);
				}
			}

			LoadGridsquarePerformanceWorkaround.ItemPicker.instance.end(square);
		}
	}

	private static class ItemPicker {
		public static LoadGridsquarePerformanceWorkaround.ItemPicker instance = new LoadGridsquarePerformanceWorkaround.ItemPicker();
		private IsoGridSquare square;

		public void init() {
		}

		public void begin(IsoGridSquare square) {
			if (square.isOverlayDone()) {
				this.square = null;
			} else {
				this.square = square;
			}
		}

		public void checkObject(IsoObject object) {
			if (this.square != null) {
				IsoSprite sprite = object.getSprite();
				if (sprite != null && sprite.getName() != null) {
					ItemContainer itemContainer = object.getContainer();
					if (itemContainer != null && !itemContainer.isExplored()) {
						ItemPickerJava.fillContainer(itemContainer, IsoPlayer.getInstance());
						if (itemContainer != null) {
							itemContainer.setExplored(true);
						}

						if (GameServer.bServer) {
							GameServer.sendItemsInContainer(object, itemContainer);
						}
					}

					if (itemContainer == null || !itemContainer.getItems().isEmpty()) {
						ItemPickerJava.updateOverlaySprite(object);
					}
				}
			}
		}

		public void end(IsoGridSquare square) {
			square.setOverlayDone(true);
		}
	}
}
