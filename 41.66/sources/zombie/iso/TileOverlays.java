package zombie.iso;

import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameServer;
import zombie.util.LocationRNG;
import zombie.util.StringUtils;


public class TileOverlays {
	public static final TileOverlays instance = new TileOverlays();
	private static final THashMap overlayMap = new THashMap();
	private static final ArrayList tempEntries = new ArrayList();

	public void addOverlays(KahluaTableImpl kahluaTableImpl) {
		KahluaTableIterator kahluaTableIterator = kahluaTableImpl.iterator();
		while (true) {
			String string;
			do {
				if (!kahluaTableIterator.advance()) {
					return;
				}

				string = kahluaTableIterator.getKey().toString();
			}	 while ("VERSION".equalsIgnoreCase(string));

			TileOverlays.TileOverlay tileOverlay = new TileOverlays.TileOverlay();
			tileOverlay.tile = string;
			KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableIterator.getValue();
			KahluaTableIterator kahluaTableIterator2 = kahluaTableImpl2.iterator();
			while (kahluaTableIterator2.advance()) {
				KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)kahluaTableIterator2.getValue();
				TileOverlays.TileOverlayEntry tileOverlayEntry = new TileOverlays.TileOverlayEntry();
				tileOverlayEntry.room = kahluaTableImpl3.rawgetStr("name");
				tileOverlayEntry.chance = kahluaTableImpl3.rawgetInt("chance");
				tileOverlayEntry.usage.parse(kahluaTableImpl3.rawgetStr("usage"));
				KahluaTableImpl kahluaTableImpl4 = (KahluaTableImpl)kahluaTableImpl3.rawget("tiles");
				String string2;
				for (KahluaTableIterator kahluaTableIterator3 = kahluaTableImpl4.iterator(); kahluaTableIterator3.advance(); tileOverlayEntry.tiles.add(string2)) {
					string2 = kahluaTableIterator3.getValue().toString();
					if (!StringUtils.isNullOrWhitespace(string2) && !"none".equalsIgnoreCase(string2)) {
						if (Core.bDebug && !GameServer.bServer && Texture.getSharedTexture(string2) == null) {
							System.out.println("BLANK OVERLAY TEXTURE. Set it to \"none\".: " + string2);
						}
					} else {
						string2 = "";
					}
				}

				tileOverlay.entries.add(tileOverlayEntry);
			}

			overlayMap.put(tileOverlay.tile, tileOverlay);
		}
	}

	public boolean hasOverlays(IsoObject object) {
		return object != null && object.sprite != null && object.sprite.name != null && overlayMap.containsKey(object.sprite.name);
	}

	public void updateTileOverlaySprite(IsoObject object) {
		if (object != null) {
			IsoGridSquare square = object.getSquare();
			if (square != null) {
				String string = null;
				float float1 = -1.0F;
				float float2 = -1.0F;
				float float3 = -1.0F;
				float float4 = -1.0F;
				if (object.sprite != null && object.sprite.name != null) {
					TileOverlays.TileOverlay tileOverlay = (TileOverlays.TileOverlay)overlayMap.get(object.sprite.name);
					if (tileOverlay != null) {
						String string2 = "other";
						if (square.getRoom() != null) {
							string2 = square.getRoom().getName();
						}

						TileOverlays.TileOverlayEntry tileOverlayEntry = tileOverlay.pickRandom(string2, square);
						if (tileOverlayEntry == null) {
							tileOverlayEntry = tileOverlay.pickRandom("other", square);
						}

						if (tileOverlayEntry != null) {
							if (tileOverlayEntry.usage.bTableTop && this.hasObjectOnTop(object)) {
								return;
							}

							string = tileOverlayEntry.pickRandom(square.x, square.y, square.z);
							if (tileOverlayEntry.usage.alpha >= 0.0F) {
								float3 = 1.0F;
								float2 = 1.0F;
								float1 = 1.0F;
								float4 = tileOverlayEntry.usage.alpha;
							}
						}
					}
				}

				if (!StringUtils.isNullOrWhitespace(string) && !GameServer.bServer && Texture.getSharedTexture(string) == null) {
					string = null;
				}

				if (!StringUtils.isNullOrWhitespace(string)) {
					if (object.AttachedAnimSprite == null) {
						object.AttachedAnimSprite = new ArrayList(4);
					}

					IsoSprite sprite = IsoSpriteManager.instance.getSprite(string);
					sprite.name = string;
					IsoSpriteInstance spriteInstance = IsoSpriteInstance.get(sprite);
					if (float4 > 0.0F) {
						spriteInstance.tintr = float1;
						spriteInstance.tintg = float2;
						spriteInstance.tintb = float3;
						spriteInstance.alpha = float4;
					}

					spriteInstance.bCopyTargetAlpha = false;
					spriteInstance.bMultiplyObjectAlpha = true;
					object.AttachedAnimSprite.add(spriteInstance);
				}
			}
		}
	}

	private boolean hasObjectOnTop(IsoObject object) {
		if (!object.isTableSurface()) {
			return false;
		} else {
			IsoGridSquare square = object.getSquare();
			for (int int1 = object.getObjectIndex() + 1; int1 < square.getObjects().size(); ++int1) {
				IsoObject object2 = (IsoObject)square.getObjects().get(int1);
				if (object2.isTableTopObject() || object2.isTableSurface()) {
					return true;
				}
			}

			return false;
		}
	}

	public void fixTableTopOverlays(IsoGridSquare square) {
		if (square != null && !square.getObjects().isEmpty()) {
			boolean boolean1 = false;
			for (int int1 = square.getObjects().size() - 1; int1 >= 0; --int1) {
				IsoObject object = (IsoObject)square.getObjects().get(int1);
				if (boolean1 && object.isTableSurface()) {
					this.removeTableTopOverlays(object);
				}

				if (object.isTableSurface() || object.isTableTopObject()) {
					boolean1 = true;
				}
			}
		}
	}

	private void removeTableTopOverlays(IsoObject object) {
		if (object != null && object.isTableSurface()) {
			if (object.sprite != null && object.sprite.name != null) {
				if (object.AttachedAnimSprite != null && !object.AttachedAnimSprite.isEmpty()) {
					TileOverlays.TileOverlay tileOverlay = (TileOverlays.TileOverlay)overlayMap.get(object.sprite.name);
					if (tileOverlay != null) {
						int int1 = object.AttachedAnimSprite.size();
						for (int int2 = 0; int2 < tileOverlay.entries.size(); ++int2) {
							TileOverlays.TileOverlayEntry tileOverlayEntry = (TileOverlays.TileOverlayEntry)tileOverlay.entries.get(int2);
							if (tileOverlayEntry.usage.bTableTop) {
								for (int int3 = 0; int3 < tileOverlayEntry.tiles.size(); ++int3) {
									this.tryRemoveAttachedSprite(object.AttachedAnimSprite, (String)tileOverlayEntry.tiles.get(int3));
								}
							}
						}

						if (int1 != object.AttachedAnimSprite.size()) {
						}
					}
				}
			}
		}
	}

	private void tryRemoveAttachedSprite(ArrayList arrayList, String string) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			IsoSpriteInstance spriteInstance = (IsoSpriteInstance)arrayList.get(int1);
			if (string.equals(spriteInstance.getName())) {
				arrayList.remove(int1--);
				IsoSpriteInstance.add(spriteInstance);
			}
		}
	}

	public void Reset() {
		overlayMap.clear();
	}

	private static final class TileOverlay {
		public String tile;
		public final ArrayList entries = new ArrayList();

		public void getEntries(String string, IsoGridSquare square, ArrayList arrayList) {
			arrayList.clear();
			for (int int1 = 0; int1 < this.entries.size(); ++int1) {
				TileOverlays.TileOverlayEntry tileOverlayEntry = (TileOverlays.TileOverlayEntry)this.entries.get(int1);
				if (tileOverlayEntry.room.equalsIgnoreCase(string) && tileOverlayEntry.matchUsage(square)) {
					arrayList.add(tileOverlayEntry);
				}
			}
		}

		public TileOverlays.TileOverlayEntry pickRandom(String string, IsoGridSquare square) {
			this.getEntries(string, square, TileOverlays.tempEntries);
			if (TileOverlays.tempEntries.isEmpty()) {
				return null;
			} else {
				int int1 = LocationRNG.instance.nextInt(TileOverlays.tempEntries.size(), square.x, square.y, square.z);
				return (TileOverlays.TileOverlayEntry)TileOverlays.tempEntries.get(int1);
			}
		}
	}

	private static final class TileOverlayEntry {
		public String room;
		public int chance;
		public final ArrayList tiles = new ArrayList();
		public final TileOverlays.TileOverlayUsage usage = new TileOverlays.TileOverlayUsage();

		public boolean matchUsage(IsoGridSquare square) {
			return this.usage.match(square);
		}

		public String pickRandom(int int1, int int2, int int3) {
			int int4 = LocationRNG.instance.nextInt(this.chance, int1, int2, int3);
			if (int4 == 0 && !this.tiles.isEmpty()) {
				int4 = LocationRNG.instance.nextInt(this.tiles.size());
				return (String)this.tiles.get(int4);
			} else {
				return null;
			}
		}
	}

	private static final class TileOverlayUsage {
		String usage;
		int zOnly = -1;
		int zGreaterThan = -1;
		float alpha = -1.0F;
		boolean bTableTop = false;

		boolean parse(String string) {
			this.usage = string.trim();
			if (StringUtils.isNullOrWhitespace(this.usage)) {
				return true;
			} else {
				String[] stringArray = string.split(";");
				for (int int1 = 0; int1 < stringArray.length; ++int1) {
					String string2 = stringArray[int1];
					if (string2.startsWith("z=")) {
						this.zOnly = Integer.parseInt(string2.substring(2));
					} else if (string2.startsWith("z>")) {
						this.zGreaterThan = Integer.parseInt(string2.substring(2));
					} else if (string2.startsWith("alpha=")) {
						this.alpha = Float.parseFloat(string2.substring(6));
						this.alpha = PZMath.clamp(this.alpha, 0.0F, 1.0F);
					} else {
						if (!string2.startsWith("tabletop")) {
							return false;
						}

						this.bTableTop = true;
					}
				}

				return true;
			}
		}

		boolean match(IsoGridSquare square) {
			if (this.zOnly != -1 && square.z != this.zOnly) {
				return false;
			} else {
				return this.zGreaterThan == -1 || square.z > this.zGreaterThan;
			}
		}
	}
}
