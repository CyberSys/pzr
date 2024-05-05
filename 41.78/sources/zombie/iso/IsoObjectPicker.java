package zombie.iso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.textures.Texture;
import zombie.input.Mouse;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;


public final class IsoObjectPicker {
	public static final IsoObjectPicker Instance = new IsoObjectPicker();
	static final ArrayList choices = new ArrayList();
	static final Vector2 tempo = new Vector2();
	static final Vector2 tempo2 = new Vector2();
	static final Comparator comp = new Comparator(){
    
    public int compare(IsoObjectPicker.ClickObject var1, IsoObjectPicker.ClickObject var2) {
        int var3 = var1.getScore();
        int var4 = var2.getScore();
        if (var3 > var4) {
            return 1;
        } else if (var3 < var4) {
            return -1;
        } else {
            return var1.tile != null && var1.tile.square != null && var2.tile != null && var1.tile.square == var2.tile.square ? var1.tile.getObjectIndex() - var2.tile.getObjectIndex() : 0;
        }
    }
};
	public IsoObjectPicker.ClickObject[] ClickObjectStore = new IsoObjectPicker.ClickObject[15000];
	public int count = 0;
	public int counter = 0;
	public int maxcount = 0;
	public final ArrayList ThisFrame = new ArrayList();
	public boolean dirty = true;
	public float xOffSinceDirty = 0.0F;
	public float yOffSinceDirty = 0.0F;
	public boolean wasDirty = false;
	IsoObjectPicker.ClickObject LastPickObject = null;
	float lx = 0.0F;
	float ly = 0.0F;

	public IsoObjectPicker getInstance() {
		return Instance;
	}

	public void Add(int int1, int int2, int int3, int int4, IsoGridSquare square, IsoObject object, boolean boolean1, float float1, float float2) {
		if (!((float)(int1 + int3) <= this.lx - 32.0F) && !((float)int1 >= this.lx + 32.0F) && !((float)(int2 + int4) <= this.ly - 32.0F) && !((float)int2 >= this.ly + 32.0F)) {
			if (this.ThisFrame.size() < 15000) {
				if (!object.NoPicking) {
					boolean boolean2;
					if (object instanceof IsoSurvivor) {
						boolean2 = false;
					}

					if (object instanceof IsoDoor) {
						boolean2 = false;
					}

					if (int1 <= Core.getInstance().getOffscreenWidth(0)) {
						if (int2 <= Core.getInstance().getOffscreenHeight(0)) {
							if (int1 + int3 >= 0) {
								if (int2 + int4 >= 0) {
									IsoObjectPicker.ClickObject clickObject = this.ClickObjectStore[this.ThisFrame.size()];
									this.ThisFrame.add(clickObject);
									this.count = this.ThisFrame.size();
									clickObject.x = int1;
									clickObject.y = int2;
									clickObject.width = int3;
									clickObject.height = int4;
									clickObject.square = square;
									clickObject.tile = object;
									clickObject.flip = boolean1;
									clickObject.scaleX = float1;
									clickObject.scaleY = float2;
									if (clickObject.tile instanceof IsoGameCharacter) {
										clickObject.flip = false;
									}

									if (this.count > this.maxcount) {
										this.maxcount = this.count;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void Init() {
		this.ThisFrame.clear();
		this.LastPickObject = null;
		for (int int1 = 0; int1 < 15000; ++int1) {
			this.ClickObjectStore[int1] = new IsoObjectPicker.ClickObject();
		}
	}

	public IsoObjectPicker.ClickObject ContextPick(int int1, int int2) {
		float float1 = (float)int1 * Core.getInstance().getZoom(0);
		float float2 = (float)int2 * Core.getInstance().getZoom(0);
		choices.clear();
		++this.counter;
		int int3;
		IsoObjectPicker.ClickObject clickObject;
		for (int3 = this.ThisFrame.size() - 1; int3 >= 0; --int3) {
			clickObject = (IsoObjectPicker.ClickObject)this.ThisFrame.get(int3);
			if ((!(clickObject.tile instanceof IsoPlayer) || clickObject.tile != IsoPlayer.players[0]) && (clickObject.tile.sprite == null || clickObject.tile.getTargetAlpha(0) != 0.0F && (!clickObject.tile.sprite.Properties.Is(IsoFlagType.cutW) && !clickObject.tile.sprite.Properties.Is(IsoFlagType.cutN) || clickObject.tile instanceof IsoWindow || !(clickObject.tile.getTargetAlpha(0) < 1.0F)))) {
				if (clickObject.tile != null && clickObject.tile.sprite != null) {
				}

				if (float1 > (float)clickObject.x && float2 > (float)clickObject.y && float1 <= (float)(clickObject.x + clickObject.width) && float2 <= (float)(clickObject.y + clickObject.height)) {
					if (clickObject.tile instanceof IsoPlayer) {
						if (clickObject.tile.sprite == null || clickObject.tile.sprite.def == null || clickObject.tile.sprite.CurrentAnim == null || clickObject.tile.sprite.CurrentAnim.Frames == null || clickObject.tile.sprite.def.Frame < 0.0F || clickObject.tile.sprite.def.Frame >= (float)clickObject.tile.sprite.CurrentAnim.Frames.size()) {
							continue;
						}

						int int4 = (int)(float1 - (float)clickObject.x);
						int int5 = (int)(float2 - (float)clickObject.y);
						Texture texture = ((IsoDirectionFrame)clickObject.tile.sprite.CurrentAnim.Frames.get((int)clickObject.tile.sprite.def.Frame)).directions[clickObject.tile.dir.index()];
						int int6 = Core.TileScale;
						if (clickObject.flip) {
							int5 = (int)((float)int5 - texture.offsetY);
							int4 = texture.getWidth() - int5;
						} else {
							int4 = (int)((float)int4 - texture.offsetX * (float)int6);
							int5 = (int)((float)int5 - texture.offsetY * (float)int6);
						}

						if (int4 >= 0 && int5 >= 0 && int4 <= texture.getWidth() * int6 && int5 <= texture.getHeight() * int6) {
							clickObject.lx = (int)float1 - clickObject.x;
							clickObject.ly = (int)float2 - clickObject.y;
							this.LastPickObject = clickObject;
							choices.clear();
							choices.add(clickObject);
							break;
						}
					}

					if (clickObject.scaleX == 1.0F && clickObject.scaleY == 1.0F) {
						if (clickObject.tile.isMaskClicked((int)(float1 - (float)clickObject.x), (int)(float2 - (float)clickObject.y), clickObject.flip)) {
							if (clickObject.tile.rerouteMask != null) {
								clickObject.tile = clickObject.tile.rerouteMask;
							}

							clickObject.lx = (int)float1 - clickObject.x;
							clickObject.ly = (int)float2 - clickObject.y;
							this.LastPickObject = clickObject;
							choices.add(clickObject);
						}
					} else {
						float float3 = (float)clickObject.x + (float1 - (float)clickObject.x) / clickObject.scaleX;
						float float4 = (float)clickObject.y + (float2 - (float)clickObject.y) / clickObject.scaleY;
						if (clickObject.tile.isMaskClicked((int)(float3 - (float)clickObject.x), (int)(float4 - (float)clickObject.y), clickObject.flip)) {
							if (clickObject.tile.rerouteMask != null) {
								clickObject.tile = clickObject.tile.rerouteMask;
							}

							clickObject.lx = (int)float1 - clickObject.x;
							clickObject.ly = (int)float2 - clickObject.y;
							this.LastPickObject = clickObject;
							choices.add(clickObject);
						}
					}
				}
			}
		}

		if (choices.isEmpty()) {
			return null;
		} else {
			for (int3 = 0; int3 < choices.size(); ++int3) {
				clickObject = (IsoObjectPicker.ClickObject)choices.get(int3);
				clickObject.score = clickObject.calculateScore();
			}

			try {
				Collections.sort(choices, comp);
			} catch (IllegalArgumentException illegalArgumentException) {
				if (Core.bDebug) {
					ExceptionLogger.logException(illegalArgumentException);
				}

				return null;
			}

			IsoObjectPicker.ClickObject clickObject2 = (IsoObjectPicker.ClickObject)choices.get(choices.size() - 1);
			return clickObject2;
		}
	}

	public IsoObjectPicker.ClickObject Pick(int int1, int int2) {
		float float1 = (float)int1;
		float float2 = (float)int2;
		float float3 = (float)Core.getInstance().getScreenWidth();
		float float4 = (float)Core.getInstance().getScreenHeight();
		float float5 = float3 * Core.getInstance().getZoom(0);
		float float6 = float4 * Core.getInstance().getZoom(0);
		float float7 = (float)Core.getInstance().getOffscreenWidth(0);
		float float8 = (float)Core.getInstance().getOffscreenHeight(0);
		float float9 = float7 / float5;
		float float10 = float8 / float6;
		float1 -= float3 / 2.0F;
		float2 -= float4 / 2.0F;
		float1 /= float9;
		float2 /= float10;
		float1 += float3 / 2.0F;
		float2 += float4 / 2.0F;
		++this.counter;
		for (int int3 = this.ThisFrame.size() - 1; int3 >= 0; --int3) {
			IsoObjectPicker.ClickObject clickObject = (IsoObjectPicker.ClickObject)this.ThisFrame.get(int3);
			if (clickObject.tile.square != null) {
			}

			if (!(clickObject.tile instanceof IsoPlayer) && (clickObject.tile.sprite == null || clickObject.tile.getTargetAlpha(0) != 0.0F)) {
				if (clickObject.tile != null && clickObject.tile.sprite != null) {
				}

				if (float1 > (float)clickObject.x && float2 > (float)clickObject.y && float1 <= (float)(clickObject.x + clickObject.width) && float2 <= (float)(clickObject.y + clickObject.height)) {
					if (clickObject.tile instanceof IsoSurvivor) {
						boolean boolean1 = false;
					} else if (clickObject.tile.isMaskClicked((int)(float1 - (float)clickObject.x), (int)(float2 - (float)clickObject.y), clickObject.flip)) {
						if (clickObject.tile.rerouteMask != null) {
							clickObject.tile = clickObject.tile.rerouteMask;
						}

						clickObject.lx = (int)float1 - clickObject.x;
						clickObject.ly = (int)float2 - clickObject.y;
						this.LastPickObject = clickObject;
						return clickObject;
					}
				}
			}
		}

		return null;
	}

	public void StartRender() {
		float float1 = (float)Mouse.getX();
		float float2 = (float)Mouse.getY();
		if (float1 != this.lx || float2 != this.ly) {
			this.dirty = true;
		}

		this.lx = float1;
		this.ly = float2;
		if (this.dirty) {
			this.ThisFrame.clear();
			this.count = 0;
			this.wasDirty = true;
			this.dirty = false;
			this.xOffSinceDirty = 0.0F;
			this.yOffSinceDirty = 0.0F;
		} else {
			this.wasDirty = false;
		}
	}

	public IsoMovingObject PickTarget(int int1, int int2) {
		float float1 = (float)int1;
		float float2 = (float)int2;
		float float3 = (float)Core.getInstance().getScreenWidth();
		float float4 = (float)Core.getInstance().getScreenHeight();
		float float5 = float3 * Core.getInstance().getZoom(0);
		float float6 = float4 * Core.getInstance().getZoom(0);
		float float7 = (float)Core.getInstance().getOffscreenWidth(0);
		float float8 = (float)Core.getInstance().getOffscreenHeight(0);
		float float9 = float7 / float5;
		float float10 = float8 / float6;
		float1 -= float3 / 2.0F;
		float2 -= float4 / 2.0F;
		float1 /= float9;
		float2 /= float10;
		float1 += float3 / 2.0F;
		float2 += float4 / 2.0F;
		++this.counter;
		for (int int3 = this.ThisFrame.size() - 1; int3 >= 0; --int3) {
			IsoObjectPicker.ClickObject clickObject = (IsoObjectPicker.ClickObject)this.ThisFrame.get(int3);
			if (clickObject.tile.square != null) {
			}

			if (clickObject.tile != IsoPlayer.getInstance() && (clickObject.tile.sprite == null || clickObject.tile.getTargetAlpha() != 0.0F)) {
				if (clickObject.tile != null && clickObject.tile.sprite != null) {
				}

				if (float1 > (float)clickObject.x && float2 > (float)clickObject.y && float1 <= (float)(clickObject.x + clickObject.width) && float2 <= (float)(clickObject.y + clickObject.height) && clickObject.tile instanceof IsoMovingObject && clickObject.tile.isMaskClicked((int)(float1 - (float)clickObject.x), (int)(float2 - (float)clickObject.y), clickObject.flip)) {
					if (clickObject.tile.rerouteMask != null) {
					}

					clickObject.lx = (int)(float1 - (float)clickObject.x);
					clickObject.ly = (int)(float2 - (float)clickObject.y);
					this.LastPickObject = clickObject;
					return (IsoMovingObject)clickObject.tile;
				}
			}
		}

		return null;
	}

	public IsoObject PickDoor(int int1, int int2, boolean boolean1) {
		float float1 = (float)int1 * Core.getInstance().getZoom(0);
		float float2 = (float)int2 * Core.getInstance().getZoom(0);
		int int3 = IsoPlayer.getPlayerIndex();
		for (int int4 = this.ThisFrame.size() - 1; int4 >= 0; --int4) {
			IsoObjectPicker.ClickObject clickObject = (IsoObjectPicker.ClickObject)this.ThisFrame.get(int4);
			if (clickObject.tile instanceof IsoDoor && clickObject.tile.getTargetAlpha(int3) != 0.0F && boolean1 == clickObject.tile.getTargetAlpha(int3) < 1.0F && float1 >= (float)clickObject.x && float2 >= (float)clickObject.y && float1 < (float)(clickObject.x + clickObject.width) && float2 < (float)(clickObject.y + clickObject.height)) {
				int int5 = (int)(float1 - (float)clickObject.x);
				int int6 = (int)(float2 - (float)clickObject.y);
				if (clickObject.tile.isMaskClicked(int5, int6, clickObject.flip)) {
					return clickObject.tile;
				}
			}
		}

		return null;
	}

	public IsoObject PickWindow(int int1, int int2) {
		float float1 = (float)int1 * Core.getInstance().getZoom(0);
		float float2 = (float)int2 * Core.getInstance().getZoom(0);
		for (int int3 = this.ThisFrame.size() - 1; int3 >= 0; --int3) {
			IsoObjectPicker.ClickObject clickObject = (IsoObjectPicker.ClickObject)this.ThisFrame.get(int3);
			if ((clickObject.tile instanceof IsoWindow || clickObject.tile instanceof IsoCurtain) && (clickObject.tile.sprite == null || clickObject.tile.getTargetAlpha() != 0.0F) && float1 >= (float)clickObject.x && float2 >= (float)clickObject.y && float1 < (float)(clickObject.x + clickObject.width) && float2 < (float)(clickObject.y + clickObject.height)) {
				int int4 = (int)(float1 - (float)clickObject.x);
				int int5 = (int)(float2 - (float)clickObject.y);
				if (clickObject.tile.isMaskClicked(int4, int5, clickObject.flip)) {
					return clickObject.tile;
				}

				if (clickObject.tile instanceof IsoWindow) {
					boolean boolean1 = false;
					boolean boolean2 = false;
					int int6;
					for (int6 = int5; int6 >= 0; --int6) {
						if (clickObject.tile.isMaskClicked(int4, int6)) {
							boolean1 = true;
							break;
						}
					}

					for (int6 = int5; int6 < clickObject.height; ++int6) {
						if (clickObject.tile.isMaskClicked(int4, int6)) {
							boolean2 = true;
							break;
						}
					}

					if (boolean1 && boolean2) {
						return clickObject.tile;
					}
				}
			}
		}

		return null;
	}

	public IsoObject PickWindowFrame(int int1, int int2) {
		float float1 = (float)int1 * Core.getInstance().getZoom(0);
		float float2 = (float)int2 * Core.getInstance().getZoom(0);
		for (int int3 = this.ThisFrame.size() - 1; int3 >= 0; --int3) {
			IsoObjectPicker.ClickObject clickObject = (IsoObjectPicker.ClickObject)this.ThisFrame.get(int3);
			if (IsoWindowFrame.isWindowFrame(clickObject.tile) && (clickObject.tile.sprite == null || clickObject.tile.getTargetAlpha() != 0.0F) && float1 >= (float)clickObject.x && float2 >= (float)clickObject.y && float1 < (float)(clickObject.x + clickObject.width) && float2 < (float)(clickObject.y + clickObject.height)) {
				int int4 = (int)(float1 - (float)clickObject.x);
				int int5 = (int)(float2 - (float)clickObject.y);
				if (clickObject.tile.isMaskClicked(int4, int5, clickObject.flip)) {
					return clickObject.tile;
				}

				boolean boolean1 = false;
				boolean boolean2 = false;
				int int6;
				for (int6 = int5; int6 >= 0; --int6) {
					if (clickObject.tile.isMaskClicked(int4, int6)) {
						boolean1 = true;
						break;
					}
				}

				for (int6 = int5; int6 < clickObject.height; ++int6) {
					if (clickObject.tile.isMaskClicked(int4, int6)) {
						boolean2 = true;
						break;
					}
				}

				if (boolean1 && boolean2) {
					return clickObject.tile;
				}
			}
		}

		return null;
	}

	public IsoObject PickThumpable(int int1, int int2) {
		float float1 = (float)int1 * Core.getInstance().getZoom(0);
		float float2 = (float)int2 * Core.getInstance().getZoom(0);
		for (int int3 = this.ThisFrame.size() - 1; int3 >= 0; --int3) {
			IsoObjectPicker.ClickObject clickObject = (IsoObjectPicker.ClickObject)this.ThisFrame.get(int3);
			if (clickObject.tile instanceof IsoThumpable) {
				IsoThumpable thumpable = (IsoThumpable)clickObject.tile;
				if ((clickObject.tile.sprite == null || clickObject.tile.getTargetAlpha() != 0.0F) && float1 >= (float)clickObject.x && float2 >= (float)clickObject.y && float1 < (float)(clickObject.x + clickObject.width) && float2 < (float)(clickObject.y + clickObject.height)) {
					int int4 = (int)(float1 - (float)clickObject.x);
					int int5 = (int)(float2 - (float)clickObject.y);
					if (clickObject.tile.isMaskClicked(int4, int5, clickObject.flip)) {
						return clickObject.tile;
					}
				}
			}
		}

		return null;
	}

	public IsoObject PickHoppable(int int1, int int2) {
		float float1 = (float)int1 * Core.getInstance().getZoom(0);
		float float2 = (float)int2 * Core.getInstance().getZoom(0);
		for (int int3 = this.ThisFrame.size() - 1; int3 >= 0; --int3) {
			IsoObjectPicker.ClickObject clickObject = (IsoObjectPicker.ClickObject)this.ThisFrame.get(int3);
			if (clickObject.tile.isHoppable() && (clickObject.tile.sprite == null || clickObject.tile.getTargetAlpha() != 0.0F) && float1 >= (float)clickObject.x && float2 >= (float)clickObject.y && float1 < (float)(clickObject.x + clickObject.width) && float2 < (float)(clickObject.y + clickObject.height)) {
				int int4 = (int)(float1 - (float)clickObject.x);
				int int5 = (int)(float2 - (float)clickObject.y);
				if (clickObject.tile.isMaskClicked(int4, int5, clickObject.flip)) {
					return clickObject.tile;
				}
			}
		}

		return null;
	}

	public IsoObject PickCorpse(int int1, int int2) {
		float float1 = (float)int1 * Core.getInstance().getZoom(0);
		float float2 = (float)int2 * Core.getInstance().getZoom(0);
		for (int int3 = this.ThisFrame.size() - 1; int3 >= 0; --int3) {
			IsoObjectPicker.ClickObject clickObject = (IsoObjectPicker.ClickObject)this.ThisFrame.get(int3);
			if (float1 >= (float)clickObject.x && float2 >= (float)clickObject.y && float1 < (float)(clickObject.x + clickObject.width) && float2 < (float)(clickObject.y + clickObject.height) && !(clickObject.tile.getTargetAlpha() < 1.0F)) {
				if (clickObject.tile.isMaskClicked((int)(float1 - (float)clickObject.x), (int)(float2 - (float)clickObject.y), clickObject.flip) && !(clickObject.tile instanceof IsoWindow)) {
					return null;
				}

				if (clickObject.tile instanceof IsoDeadBody && ((IsoDeadBody)clickObject.tile).isMouseOver(float1, float2)) {
					return clickObject.tile;
				}
			}
		}

		return null;
	}

	public IsoObject PickTree(int int1, int int2) {
		float float1 = (float)int1 * Core.getInstance().getZoom(0);
		float float2 = (float)int2 * Core.getInstance().getZoom(0);
		for (int int3 = this.ThisFrame.size() - 1; int3 >= 0; --int3) {
			IsoObjectPicker.ClickObject clickObject = (IsoObjectPicker.ClickObject)this.ThisFrame.get(int3);
			if (clickObject.tile instanceof IsoTree && (clickObject.tile.sprite == null || clickObject.tile.getTargetAlpha() != 0.0F) && float1 >= (float)clickObject.x && float2 >= (float)clickObject.y && float1 < (float)(clickObject.x + clickObject.width) && float2 < (float)(clickObject.y + clickObject.height)) {
				int int4 = (int)(float1 - (float)clickObject.x);
				int int5 = (int)(float2 - (float)clickObject.y);
				if (clickObject.tile.isMaskClicked(int4, int5, clickObject.flip)) {
					return clickObject.tile;
				}
			}
		}

		return null;
	}

	public BaseVehicle PickVehicle(int int1, int int2) {
		float float1 = IsoUtils.XToIso((float)int1, (float)int2, 0.0F);
		float float2 = IsoUtils.YToIso((float)int1, (float)int2, 0.0F);
		for (int int3 = 0; int3 < IsoWorld.instance.CurrentCell.getVehicles().size(); ++int3) {
			BaseVehicle baseVehicle = (BaseVehicle)IsoWorld.instance.CurrentCell.getVehicles().get(int3);
			if (baseVehicle.isInBounds(float1, float2)) {
				return baseVehicle;
			}
		}

		return null;
	}

	public static final class ClickObject {
		public int height;
		public IsoGridSquare square;
		public IsoObject tile;
		public int width;
		public int x;
		public int y;
		public int lx;
		public int ly;
		public float scaleX;
		public float scaleY;
		private boolean flip;
		private int score;

		public int calculateScore() {
			float float1 = 1.0F;
			IsoPlayer player = IsoPlayer.getInstance();
			IsoGridSquare square = player.getCurrentSquare();
			IsoObjectPicker.tempo.x = (float)this.square.getX() + 0.5F;
			IsoObjectPicker.tempo.y = (float)this.square.getY() + 0.5F;
			Vector2 vector2 = IsoObjectPicker.tempo;
			vector2.x -= player.getX();
			vector2 = IsoObjectPicker.tempo;
			vector2.y -= player.getY();
			IsoObjectPicker.tempo.normalize();
			Vector2 vector22 = player.getVectorFromDirection(IsoObjectPicker.tempo2);
			float float2 = vector22.dot(IsoObjectPicker.tempo);
			float1 += Math.abs(float2 * 4.0F);
			IsoGridSquare square2 = this.square;
			IsoObject object = this.tile;
			IsoSprite sprite = object.sprite;
			IsoDoor door = (IsoDoor)Type.tryCastTo(object, IsoDoor.class);
			IsoThumpable thumpable = (IsoThumpable)Type.tryCastTo(object, IsoThumpable.class);
			if (door == null && (thumpable == null || !thumpable.isDoor())) {
				if (object instanceof IsoWindow) {
					float1 += 4.0F;
					if (player.getZ() > (float)square2.getZ()) {
						float1 -= 1000.0F;
					}
				} else {
					if (square != null && square2.getRoom() == square.getRoom()) {
						++float1;
					} else {
						float1 -= 100000.0F;
					}

					if (player.getZ() > (float)square2.getZ()) {
						float1 -= 1000.0F;
					}

					if (object instanceof IsoPlayer) {
						float1 -= 100000.0F;
					} else if (object instanceof IsoThumpable && object.getTargetAlpha() < 0.99F && (object.getTargetAlpha() < 0.5F || object.getContainer() == null)) {
						float1 -= 100000.0F;
					}

					if (object instanceof IsoCurtain) {
						float1 += 3.0F;
					} else if (object instanceof IsoLightSwitch) {
						float1 += 20.0F;
					} else if (sprite.Properties.Is(IsoFlagType.bed)) {
						float1 += 2.0F;
					} else if (object.container != null) {
						float1 += 10.0F;
					} else if (object instanceof IsoWaveSignal) {
						float1 += 20.0F;
					} else if (thumpable != null && thumpable.getLightSource() != null) {
						float1 += 3.0F;
					} else if (sprite.Properties.Is(IsoFlagType.waterPiped)) {
						float1 += 3.0F;
					} else if (sprite.Properties.Is(IsoFlagType.solidfloor)) {
						float1 -= 100.0F;
					} else if (sprite.getType() == IsoObjectType.WestRoofB) {
						float1 -= 100.0F;
					} else if (sprite.getType() == IsoObjectType.WestRoofM) {
						float1 -= 100.0F;
					} else if (sprite.getType() == IsoObjectType.WestRoofT) {
						float1 -= 100.0F;
					} else if (sprite.Properties.Is(IsoFlagType.cutW) || sprite.Properties.Is(IsoFlagType.cutN)) {
						float1 -= 2.0F;
					}
				}
			} else {
				float1 += 6.0F;
				if (door != null && door.isAdjacentToSquare(square) || thumpable != null && thumpable.isAdjacentToSquare(square)) {
					++float1;
				}

				if (player.getZ() > (float)square2.getZ()) {
					float1 -= 1000.0F;
				}
			}

			float float3 = IsoUtils.DistanceManhatten((float)square2.getX() + 0.5F, (float)square2.getY() + 0.5F, player.getX(), player.getY());
			float1 -= float3 / 2.0F;
			return (int)float1;
		}

		public int getScore() {
			return this.score;
		}
	}
}
