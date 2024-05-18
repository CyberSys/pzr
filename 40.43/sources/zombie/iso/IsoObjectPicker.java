package zombie.iso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.core.Core;
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
import zombie.vehicles.BaseVehicle;


public class IsoObjectPicker {
	public static IsoObjectPicker Instance = new IsoObjectPicker();
	public IsoObjectPicker.ClickObject[] ClickObjectStore = new IsoObjectPicker.ClickObject[15000];
	public int count = 0;
	public int counter = 0;
	public int maxcount = 0;
	public ArrayList ThisFrame = new ArrayList();
	IsoObjectPicker.ClickObject LastPickObject = null;
	public boolean dirty = true;
	public float xOffSinceDirty = 0.0F;
	public float yOffSinceDirty = 0.0F;
	static ArrayList choices = new ArrayList();
	static Comparator comp = new Comparator(){
    
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
	float lx = 0.0F;
	float ly = 0.0F;
	public boolean wasDirty = false;
	static Vector2 tempo = new Vector2();
	static Vector2 tempo2 = new Vector2();

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
		for (int int3 = this.ThisFrame.size() - 1; int3 >= 0; --int3) {
			IsoObjectPicker.ClickObject clickObject = (IsoObjectPicker.ClickObject)this.ThisFrame.get(int3);
			if ((!(clickObject.tile instanceof IsoPlayer) || clickObject.tile != IsoPlayer.players[0]) && (clickObject.tile.sprite == null || clickObject.tile.targetAlpha[0] != 0.0F)) {
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
							int4 = (int)((float)int4 - ((float)(texture.getWidthOrig() - texture.getWidth()) - texture.offsetX));
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
			Collections.sort(choices, comp);
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
		float float7 = (float)Core.getInstance().OffscreenBuffer.getWidth(0);
		float float8 = (float)Core.getInstance().OffscreenBuffer.getHeight(0);
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

			if (!(clickObject.tile instanceof IsoPlayer) && (clickObject.tile.sprite == null || clickObject.tile.targetAlpha[0] != 0.0F)) {
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
		float float7 = (float)Core.getInstance().OffscreenBuffer.getWidth(0);
		float float8 = (float)Core.getInstance().OffscreenBuffer.getHeight(0);
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

			if (clickObject.tile != IsoPlayer.instance && (clickObject.tile.sprite == null || clickObject.tile.targetAlpha[IsoPlayer.getPlayerIndex()] != 0.0F)) {
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

	public boolean IsHeadShot(IsoMovingObject movingObject, int int1, int int2) {
		if (movingObject instanceof IsoSurvivor) {
			return true;
		} else {
			for (int int3 = this.ThisFrame.size() - 1; int3 >= 0; --int3) {
				IsoObjectPicker.ClickObject clickObject = (IsoObjectPicker.ClickObject)this.ThisFrame.get(int3);
				if (clickObject.tile == movingObject && clickObject.tile.getMaskClickedY(int1 - clickObject.x, int2 - clickObject.y, clickObject.flip) < 15.0F) {
					return true;
				}
			}

			return false;
		}
	}

	public IsoObject PickWindow(int int1, int int2) {
		float float1 = (float)int1 * Core.getInstance().getZoom(0);
		float float2 = (float)int2 * Core.getInstance().getZoom(0);
		for (int int3 = this.ThisFrame.size() - 1; int3 >= 0; --int3) {
			IsoObjectPicker.ClickObject clickObject = (IsoObjectPicker.ClickObject)this.ThisFrame.get(int3);
			if ((clickObject.tile instanceof IsoWindow || clickObject.tile instanceof IsoCurtain) && (clickObject.tile.sprite == null || clickObject.tile.targetAlpha[IsoPlayer.getPlayerIndex()] != 0.0F) && float1 >= (float)clickObject.x && float2 >= (float)clickObject.y && float1 < (float)(clickObject.x + clickObject.width) && float2 < (float)(clickObject.y + clickObject.height)) {
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
			if (IsoWindowFrame.isWindowFrame(clickObject.tile) && (clickObject.tile.sprite == null || clickObject.tile.targetAlpha[IsoPlayer.getPlayerIndex()] != 0.0F) && float1 >= (float)clickObject.x && float2 >= (float)clickObject.y && float1 < (float)(clickObject.x + clickObject.width) && float2 < (float)(clickObject.y + clickObject.height)) {
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
				if ((clickObject.tile.sprite == null || clickObject.tile.targetAlpha[IsoPlayer.getPlayerIndex()] != 0.0F) && float1 >= (float)clickObject.x && float2 >= (float)clickObject.y && float1 < (float)(clickObject.x + clickObject.width) && float2 < (float)(clickObject.y + clickObject.height)) {
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

	public IsoObject PickCorpse(int int1, int int2) {
		float float1 = (float)int1 * Core.getInstance().getZoom(0);
		float float2 = (float)int2 * Core.getInstance().getZoom(0);
		for (int int3 = this.ThisFrame.size() - 1; int3 >= 0; --int3) {
			IsoObjectPicker.ClickObject clickObject = (IsoObjectPicker.ClickObject)this.ThisFrame.get(int3);
			if (float1 >= (float)clickObject.x && float2 >= (float)clickObject.y && float1 < (float)(clickObject.x + clickObject.width) && float2 < (float)(clickObject.y + clickObject.height) && !(clickObject.tile.targetAlpha[IsoPlayer.getPlayerIndex()] < 1.0F)) {
				if (clickObject.tile.isMaskClicked((int)(float1 - (float)clickObject.x), (int)(float2 - (float)clickObject.y), clickObject.flip) && !(clickObject.tile instanceof IsoWindow)) {
					return null;
				}

				if (clickObject.tile instanceof IsoDeadBody && clickObject.tile.sprite != null && clickObject.tile.sprite.def != null && clickObject.tile.sprite.CurrentAnim != null && clickObject.tile.sprite.CurrentAnim.Frames != null && !(clickObject.tile.sprite.def.Frame >= (float)clickObject.tile.sprite.CurrentAnim.Frames.size())) {
					Texture texture = ((IsoDirectionFrame)clickObject.tile.sprite.CurrentAnim.Frames.get((int)clickObject.tile.sprite.def.Frame)).getTexture(clickObject.tile.dir);
					if (texture != null) {
						int int4 = Core.TileScale;
						if (float1 >= (float)clickObject.x + texture.offsetX * (float)int4 && float1 < (float)clickObject.x + (texture.offsetX + (float)texture.getWidth()) * (float)int4 && float2 >= (float)clickObject.y + texture.offsetY * (float)int4 && float2 < (float)clickObject.y + (texture.offsetY + (float)texture.getHeight()) * (float)int4) {
							return clickObject.tile;
						}
					}
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
			if (clickObject.tile instanceof IsoTree && (clickObject.tile.sprite == null || clickObject.tile.targetAlpha[IsoPlayer.getPlayerIndex()] != 0.0F) && float1 >= (float)clickObject.x && float2 >= (float)clickObject.y && float1 < (float)(clickObject.x + clickObject.width) && float2 < (float)(clickObject.y + clickObject.height)) {
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

	public class ClickObject {
		public int height;
		public IsoGridSquare square;
		public IsoObject tile;
		public int width;
		public int x;
		public int y;
		private boolean flip;
		public int lx;
		public int ly;
		public float scaleX;
		public float scaleY;

		public int getScore() {
			float float1 = 1.0F;
			IsoObjectPicker.tempo.x = (float)this.square.getX() + 0.5F;
			IsoObjectPicker.tempo.y = (float)this.square.getY() + 0.5F;
			Vector2 vector2 = IsoObjectPicker.tempo;
			vector2.x -= IsoPlayer.instance.getX();
			vector2 = IsoObjectPicker.tempo;
			vector2.y -= IsoPlayer.instance.getY();
			IsoObjectPicker.tempo.normalize();
			Vector2 vector22 = IsoPlayer.instance.getVectorFromDirection(IsoObjectPicker.tempo2);
			float float2 = vector22.dot(IsoObjectPicker.tempo);
			float1 += Math.abs(float2 * 4.0F);
			if (this.tile instanceof IsoDoor || this.tile instanceof IsoThumpable && ((IsoThumpable)this.tile).isDoor()) {
				float1 += 6.0F;
				if (IsoPlayer.instance.getZ() > (float)this.square.getZ()) {
					float1 -= 1000.0F;
				}
			} else if (this.tile instanceof IsoWindow) {
				float1 += 4.0F;
				if (IsoPlayer.instance.getZ() > (float)this.square.getZ()) {
					float1 -= 1000.0F;
				}
			} else {
				if (IsoPlayer.instance.getCurrentSquare() != null && this.square.getRoom() == IsoPlayer.instance.getCurrentSquare().getRoom()) {
					++float1;
				} else {
					float1 -= 100000.0F;
				}

				if (IsoPlayer.instance.getZ() > (float)this.square.getZ()) {
					float1 -= 1000.0F;
				}

				if (this.tile instanceof IsoPlayer) {
					float1 -= 100000.0F;
				} else if (this.tile instanceof IsoThumpable && this.tile.targetAlpha[IsoPlayer.getPlayerIndex()] < 0.99F) {
					float1 -= 100000.0F;
				}

				if (this.tile instanceof IsoCurtain) {
					float1 += 3.0F;
				} else if (this.tile instanceof IsoLightSwitch) {
					float1 += 20.0F;
				} else if (this.tile.sprite.Properties.Is(IsoFlagType.bed)) {
					float1 += 2.0F;
				} else if (this.tile.container != null) {
					float1 += 10.0F;
				} else if (this.tile instanceof IsoWaveSignal) {
					float1 += 20.0F;
				} else if (this.tile instanceof IsoThumpable && ((IsoThumpable)this.tile).getLightSource() != null) {
					float1 += 3.0F;
				} else if (this.tile.sprite.Properties.Is(IsoFlagType.waterPiped)) {
					float1 += 3.0F;
				} else if (this.tile.sprite.Properties.Is(IsoFlagType.solidfloor)) {
					float1 -= 100.0F;
				} else if (this.tile.sprite.getType() == IsoObjectType.WestRoofB) {
					float1 -= 100.0F;
				} else if (this.tile.sprite.getType() == IsoObjectType.WestRoofM) {
					float1 -= 100.0F;
				} else if (this.tile.sprite.getType() == IsoObjectType.WestRoofT) {
					float1 -= 100.0F;
				} else if (this.tile.sprite.Properties.Is(IsoFlagType.cutW) || this.tile.sprite.Properties.Is(IsoFlagType.cutN)) {
					float1 -= 2.0F;
				}
			}

			float float3 = IsoUtils.DistanceManhatten((float)this.square.getX() + 0.5F, (float)this.square.getY() + 0.5F, IsoPlayer.instance.getX(), IsoPlayer.instance.getY());
			float1 -= float3 / 2.0F;
			return (int)float1;
		}
	}
}
