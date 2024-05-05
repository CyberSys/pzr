package zombie.iso;

import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.core.textures.Texture;
import zombie.inventory.ItemContainer;
import zombie.iso.objects.IsoStove;
import zombie.network.GameServer;
import zombie.util.LocationRNG;
import zombie.util.StringUtils;


public class ContainerOverlays {
	public static final ContainerOverlays instance = new ContainerOverlays();
	private static final ArrayList tempEntries = new ArrayList();
	private final THashMap overlayMap = new THashMap();

	private void parseContainerOverlayMapV0(KahluaTableImpl kahluaTableImpl) {
		Iterator iterator = kahluaTableImpl.delegate.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			String string = entry.getKey().toString();
			ContainerOverlays.ContainerOverlay containerOverlay = new ContainerOverlays.ContainerOverlay();
			containerOverlay.name = string;
			this.overlayMap.put(containerOverlay.name, containerOverlay);
			KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)entry.getValue();
			Iterator iterator2 = kahluaTableImpl2.delegate.entrySet().iterator();
			while (iterator2.hasNext()) {
				Entry entry2 = (Entry)iterator2.next();
				String string2 = entry2.getKey().toString();
				KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)entry2.getValue();
				String string3 = null;
				if (kahluaTableImpl3.delegate.containsKey(1.0)) {
					string3 = kahluaTableImpl3.rawget(1.0).toString();
				}

				String string4 = null;
				if (kahluaTableImpl3.delegate.containsKey(2.0)) {
					string4 = kahluaTableImpl3.rawget(2.0).toString();
				}

				ContainerOverlays.ContainerOverlayEntry containerOverlayEntry = new ContainerOverlays.ContainerOverlayEntry();
				containerOverlayEntry.manyItems = string3;
				containerOverlayEntry.fewItems = string4;
				containerOverlayEntry.room = string2;
				containerOverlay.entries.add(containerOverlayEntry);
			}
		}
	}

	private void parseContainerOverlayMapV1(KahluaTableImpl kahluaTableImpl) {
		KahluaTableIterator kahluaTableIterator = kahluaTableImpl.iterator();
		while (true) {
			String string;
			do {
				if (!kahluaTableIterator.advance()) {
					return;
				}

				string = kahluaTableIterator.getKey().toString();
			}	 while ("VERSION".equalsIgnoreCase(string));

			ContainerOverlays.ContainerOverlay containerOverlay = new ContainerOverlays.ContainerOverlay();
			containerOverlay.name = string;
			KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableIterator.getValue();
			KahluaTableIterator kahluaTableIterator2 = kahluaTableImpl2.iterator();
			while (kahluaTableIterator2.advance()) {
				KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)kahluaTableIterator2.getValue();
				String string2 = kahluaTableImpl3.rawgetStr("name");
				KahluaTableImpl kahluaTableImpl4 = (KahluaTableImpl)kahluaTableImpl3.rawget("tiles");
				ContainerOverlays.ContainerOverlayEntry containerOverlayEntry = new ContainerOverlays.ContainerOverlayEntry();
				containerOverlayEntry.manyItems = (String)kahluaTableImpl4.rawget(1);
				containerOverlayEntry.fewItems = (String)kahluaTableImpl4.rawget(2);
				if (StringUtils.isNullOrWhitespace(containerOverlayEntry.manyItems) || "none".equalsIgnoreCase(containerOverlayEntry.manyItems)) {
					containerOverlayEntry.manyItems = null;
				}

				if (StringUtils.isNullOrWhitespace(containerOverlayEntry.fewItems) || "none".equalsIgnoreCase(containerOverlayEntry.fewItems)) {
					containerOverlayEntry.fewItems = null;
				}

				containerOverlayEntry.room = string2;
				containerOverlay.entries.add(containerOverlayEntry);
			}

			this.overlayMap.put(containerOverlay.name, containerOverlay);
		}
	}

	public void addOverlays(KahluaTableImpl kahluaTableImpl) {
		int int1 = kahluaTableImpl.rawgetInt("VERSION");
		if (int1 == -1) {
			this.parseContainerOverlayMapV0(kahluaTableImpl);
		} else {
			if (int1 != 1) {
				throw new RuntimeException("unknown overlayMap.VERSION " + int1);
			}

			this.parseContainerOverlayMapV1(kahluaTableImpl);
		}
	}

	public boolean hasOverlays(IsoObject object) {
		return object != null && object.sprite != null && object.sprite.name != null && this.overlayMap.containsKey(object.sprite.name);
	}

	public void updateContainerOverlaySprite(IsoObject object) {
		if (object != null) {
			if (!(object instanceof IsoStove)) {
				IsoGridSquare square = object.getSquare();
				if (square != null) {
					String string = null;
					ItemContainer itemContainer = object.getContainer();
					if (object.sprite != null && object.sprite.name != null && itemContainer != null && itemContainer.getItems() != null && !itemContainer.isEmpty()) {
						ContainerOverlays.ContainerOverlay containerOverlay = (ContainerOverlays.ContainerOverlay)this.overlayMap.get(object.sprite.name);
						if (containerOverlay != null) {
							String string2 = "other";
							if (square.getRoom() != null) {
								string2 = square.getRoom().getName();
							}

							ContainerOverlays.ContainerOverlayEntry containerOverlayEntry = containerOverlay.pickRandom(string2, square.x, square.y, square.z);
							if (containerOverlayEntry == null) {
								containerOverlayEntry = containerOverlay.pickRandom("other", square.x, square.y, square.z);
							}

							if (containerOverlayEntry != null) {
								string = containerOverlayEntry.manyItems;
								if (containerOverlayEntry.fewItems != null && itemContainer.getItems().size() < 7) {
									string = containerOverlayEntry.fewItems;
								}
							}
						}
					}

					if (!StringUtils.isNullOrWhitespace(string) && !GameServer.bServer && Texture.getSharedTexture(string) == null) {
						string = null;
					}

					object.setOverlaySprite(string);
				}
			}
		}
	}

	public void Reset() {
		this.overlayMap.clear();
	}

	private static final class ContainerOverlay {
		public String name;
		public final ArrayList entries = new ArrayList();

		public void getEntries(String string, ArrayList arrayList) {
			arrayList.clear();
			for (int int1 = 0; int1 < this.entries.size(); ++int1) {
				ContainerOverlays.ContainerOverlayEntry containerOverlayEntry = (ContainerOverlays.ContainerOverlayEntry)this.entries.get(int1);
				if (containerOverlayEntry.room.equalsIgnoreCase(string)) {
					arrayList.add(containerOverlayEntry);
				}
			}
		}

		public ContainerOverlays.ContainerOverlayEntry pickRandom(String string, int int1, int int2, int int3) {
			this.getEntries(string, ContainerOverlays.tempEntries);
			if (ContainerOverlays.tempEntries.isEmpty()) {
				return null;
			} else {
				int int4 = LocationRNG.instance.nextInt(ContainerOverlays.tempEntries.size(), int1, int2, int3);
				return (ContainerOverlays.ContainerOverlayEntry)ContainerOverlays.tempEntries.get(int4);
			}
		}
	}

	private static final class ContainerOverlayEntry {
		public String room;
		public String manyItems;
		public String fewItems;
	}
}
