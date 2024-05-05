package zombie.core.textures;

import java.util.ArrayList;
import java.util.function.Consumer;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.utils.ImageUtils;
import zombie.debug.DebugLog;
import zombie.iso.IsoCamera;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.PlayerCamera;
import zombie.iso.sprite.IsoCursor;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.util.Type;


public final class MultiTextureFBO2 {
	private final float[] zoomLevelsDefault = new float[]{2.5F, 2.25F, 2.0F, 1.75F, 1.5F, 1.25F, 1.0F, 0.75F, 0.5F};
	private float[] zoomLevels;
	public TextureFBO Current;
	public volatile TextureFBO FBOrendered = null;
	public final float[] zoom = new float[4];
	public final float[] targetZoom = new float[4];
	public final float[] startZoom = new float[4];
	private float zoomedInLevel;
	private float zoomedOutLevel;
	public final boolean[] bAutoZoom = new boolean[4];
	public boolean bZoomEnabled = true;

	public MultiTextureFBO2() {
		for (int int1 = 0; int1 < 4; ++int1) {
			this.zoom[int1] = this.targetZoom[int1] = this.startZoom[int1] = 1.0F;
		}
	}

	public int getWidth(int int1) {
		return (int)((float)IsoCamera.getScreenWidth(int1) * this.zoom[int1] * ((float)Core.TileScale / 2.0F));
	}

	public int getHeight(int int1) {
		return (int)((float)IsoCamera.getScreenHeight(int1) * this.zoom[int1] * ((float)Core.TileScale / 2.0F));
	}

	public void setTargetZoom(int int1, float float1) {
		if (this.targetZoom[int1] != float1) {
			this.targetZoom[int1] = float1;
			this.startZoom[int1] = this.zoom[int1];
		}
	}

	public ArrayList getDefaultZoomLevels() {
		ArrayList arrayList = new ArrayList();
		float[] floatArray = this.zoomLevelsDefault;
		for (int int1 = 0; int1 < floatArray.length; ++int1) {
			arrayList.add(Math.round(floatArray[int1] * 100.0F));
		}

		return arrayList;
	}

	public void setZoomLevelsFromOption(String string) {
		this.zoomLevels = this.zoomLevelsDefault;
		if (string != null && !string.isEmpty()) {
			String[] stringArray = string.split(";");
			if (stringArray.length != 0) {
				ArrayList arrayList = new ArrayList();
				String[] stringArray2 = stringArray;
				int int1 = stringArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					String string2 = stringArray2[int2];
					if (!string2.isEmpty()) {
						try {
							int int3 = Integer.parseInt(string2);
							float[] floatArray = this.zoomLevels;
							int int4 = floatArray.length;
							for (int int5 = 0; int5 < int4; ++int5) {
								float float1 = floatArray[int5];
								if (Math.round(float1 * 100.0F) == int3) {
									if (!arrayList.contains(int3)) {
										arrayList.add(int3);
									}

									break;
								}
							}
						} catch (NumberFormatException numberFormatException) {
						}
					}
				}

				if (!arrayList.contains(100)) {
					arrayList.add(100);
				}

				arrayList.sort((var0,stringx)->{
					return stringx - var0;
				});

				this.zoomLevels = new float[arrayList.size()];
				for (int int6 = 0; int6 < arrayList.size(); ++int6) {
					int1 = IsoPlayer.getPlayerIndex();
					if (Core.getInstance().getOffscreenHeight(int1) > 1440) {
						this.zoomLevels[int6] = (float)(Integer)arrayList.get(int6) / 100.0F - 0.25F;
					} else {
						this.zoomLevels[int6] = (float)(Integer)arrayList.get(int6) / 100.0F;
					}
				}
			}
		}
	}

	public void destroy() {
		if (this.Current != null) {
			this.Current.destroy();
			this.Current = null;
			this.FBOrendered = null;
			for (int int1 = 0; int1 < 4; ++int1) {
				this.zoom[int1] = this.targetZoom[int1] = 1.0F;
			}
		}
	}

	public void create(int int1, int int2) throws Exception {
		if (this.bZoomEnabled) {
			if (this.zoomLevels == null) {
				this.zoomLevels = this.zoomLevelsDefault;
			}

			this.zoomedInLevel = this.zoomLevels[this.zoomLevels.length - 1];
			this.zoomedOutLevel = this.zoomLevels[0];
			int int3 = ImageUtils.getNextPowerOfTwoHW(int1);
			int int4 = ImageUtils.getNextPowerOfTwoHW(int2);
			this.Current = this.createTexture(int3, int4, false);
		}
	}

	public void update() {
		int int1 = IsoPlayer.getPlayerIndex();
		if (!this.bZoomEnabled) {
			this.zoom[int1] = this.targetZoom[int1] = 1.0F;
		}

		float float1;
		if (this.bAutoZoom[int1] && IsoCamera.CamCharacter != null && this.bZoomEnabled) {
			float1 = IsoUtils.DistanceTo(IsoCamera.getRightClickOffX(), IsoCamera.getRightClickOffY(), 0.0F, 0.0F);
			float float2 = float1 / 300.0F;
			if (float2 > 1.0F) {
				float2 = 1.0F;
			}

			float float3 = this.shouldAutoZoomIn() ? this.zoomedInLevel : this.zoomedOutLevel;
			float3 += float2;
			if (float3 > this.zoomLevels[0]) {
				float3 = this.zoomLevels[0];
			}

			if (IsoCamera.CamCharacter.getVehicle() != null) {
				float3 = this.getMaxZoom();
			}

			this.setTargetZoom(int1, float3);
		}

		float1 = 0.004F * GameTime.instance.getMultiplier() / GameTime.instance.getTrueMultiplier() * (Core.TileScale == 2 ? 1.5F : 1.5F);
		if (!this.bAutoZoom[int1]) {
			float1 *= 5.0F;
		} else if (this.targetZoom[int1] > this.zoom[int1]) {
			float1 *= 1.0F;
		}

		float[] floatArray;
		if (this.targetZoom[int1] > this.zoom[int1]) {
			floatArray = this.zoom;
			floatArray[int1] += float1;
			IsoPlayer.players[int1].dirtyRecalcGridStackTime = 2.0F;
			if (this.zoom[int1] > this.targetZoom[int1] || Math.abs(this.zoom[int1] - this.targetZoom[int1]) < 0.001F) {
				this.zoom[int1] = this.targetZoom[int1];
			}
		}

		if (this.targetZoom[int1] < this.zoom[int1]) {
			floatArray = this.zoom;
			floatArray[int1] -= float1;
			IsoPlayer.players[int1].dirtyRecalcGridStackTime = 2.0F;
			if (this.zoom[int1] < this.targetZoom[int1] || Math.abs(this.zoom[int1] - this.targetZoom[int1]) < 0.001F) {
				this.zoom[int1] = this.targetZoom[int1];
			}
		}

		this.setCameraToCentre();
	}

	private boolean shouldAutoZoomIn() {
		if (IsoCamera.CamCharacter == null) {
			return false;
		} else {
			IsoGridSquare square = IsoCamera.CamCharacter.getCurrentSquare();
			if (square != null && !square.isOutside()) {
				return true;
			} else {
				IsoPlayer player = (IsoPlayer)Type.tryCastTo(IsoCamera.CamCharacter, IsoPlayer.class);
				if (player == null) {
					return false;
				} else if (!player.isRunning() && !player.isSprinting()) {
					if (player.closestZombie < 6.0F && player.isTargetedByZombie()) {
						return true;
					} else {
						return player.lastTargeted < (float)(PerformanceSettings.getLockFPS() * 4);
					}
				} else {
					return false;
				}
			}
		}
	}

	private void setCameraToCentre() {
		PlayerCamera playerCamera = IsoCamera.cameras[IsoPlayer.getPlayerIndex()];
		playerCamera.center();
	}

	private TextureFBO createTexture(int int1, int int2, boolean boolean1) {
		Texture texture;
		if (boolean1) {
			texture = new Texture(int1, int2, 16);
			TextureFBO textureFBO = new TextureFBO(texture);
			textureFBO.destroy();
			return null;
		} else {
			texture = new Texture(int1, int2, 19);
			return new TextureFBO(texture);
		}
	}

	public void render() {
		if (this.Current != null) {
			int int1 = 0;
			int int2;
			for (int2 = 3; int2 >= 0; --int2) {
				if (IsoPlayer.players[int2] != null) {
					int1 = int2 > 1 ? 3 : int2;
					break;
				}
			}

			int1 = Math.max(int1, IsoPlayer.numPlayers - 1);
			for (int2 = 0; int2 <= int1; ++int2) {
				if (Core.getInstance().RenderShader != null) {
					IndieGL.StartShader(Core.getInstance().RenderShader, int2);
				}

				int int3 = IsoCamera.getScreenLeft(int2);
				int int4 = IsoCamera.getScreenTop(int2);
				int int5 = IsoCamera.getScreenWidth(int2);
				int int6 = IsoCamera.getScreenHeight(int2);
				if (IsoPlayer.players[int2] != null || GameServer.bServer && ServerGUI.isCreated()) {
					((Texture)this.Current.getTexture()).rendershader2((float)int3, (float)int4, (float)int5, (float)int6, int3, int4, int5, int6, 1.0F, 1.0F, 1.0F, 1.0F);
				} else {
					SpriteRenderer.instance.renderi((Texture)null, int3, int4, int5, int6, 0.0F, 0.0F, 0.0F, 1.0F, (Consumer)null);
				}
			}

			if (Core.getInstance().RenderShader != null) {
				IndieGL.EndShader();
			}

			IsoCursor.getInstance().render(0);
		}
	}

	public TextureFBO getCurrent(int int1) {
		return this.Current;
	}

	public Texture getTexture(int int1) {
		return (Texture)this.Current.getTexture();
	}

	public void doZoomScroll(int int1, int int2) {
		this.targetZoom[int1] = this.getNextZoom(int1, int2);
	}

	public float getNextZoom(int int1, int int2) {
		if (this.bZoomEnabled && this.zoomLevels != null) {
			int int3;
			if (int2 > 0) {
				for (int3 = this.zoomLevels.length - 1; int3 > 0; --int3) {
					if (this.targetZoom[int1] == this.zoomLevels[int3]) {
						return this.zoomLevels[int3 - 1];
					}
				}
			} else if (int2 < 0) {
				for (int3 = 0; int3 < this.zoomLevels.length - 1; ++int3) {
					if (this.targetZoom[int1] == this.zoomLevels[int3]) {
						return this.zoomLevels[int3 + 1];
					}
				}
			}

			return this.targetZoom[int1];
		} else {
			return 1.0F;
		}
	}

	public float getMinZoom() {
		return this.bZoomEnabled && this.zoomLevels != null && this.zoomLevels.length != 0 ? this.zoomLevels[this.zoomLevels.length - 1] : 1.0F;
	}

	public float getMaxZoom() {
		return this.bZoomEnabled && this.zoomLevels != null && this.zoomLevels.length != 0 ? this.zoomLevels[0] : 1.0F;
	}

	public boolean test() {
		try {
			this.createTexture(16, 16, true);
			return true;
		} catch (Exception exception) {
			DebugLog.General.error("Failed to create Test FBO");
			exception.printStackTrace();
			Core.SafeMode = true;
			return false;
		}
	}
}
