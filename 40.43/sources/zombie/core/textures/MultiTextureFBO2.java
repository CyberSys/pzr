package zombie.core.textures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.debug.DebugLog;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.network.GameServer;
import zombie.network.ServerGUI;


public class MultiTextureFBO2 {
	private final float[] zoomLevels1x = new float[]{1.5F, 1.25F, 1.0F, 0.75F, 0.5F};
	private final float[] zoomLevels2x = new float[]{2.5F, 2.25F, 2.0F, 1.75F, 1.5F, 1.25F, 1.0F, 0.75F, 0.5F};
	private float[] zoomLevels;
	public TextureFBO Current;
	public volatile TextureFBO FBOrendered = null;
	public float[] zoom = new float[4];
	public float[] targetZoom = new float[4];
	public float[] startZoom = new float[4];
	private float zoomedInLevel;
	private float zoomedOutLevel;
	public boolean[] bAutoZoom = new boolean[4];
	public boolean bZoomEnabled = true;

	public MultiTextureFBO2() {
		for (int int1 = 0; int1 < 4; ++int1) {
			this.zoom[int1] = this.targetZoom[int1] = this.startZoom[int1] = 1.0F;
		}
	}

	public int getWidth(int int1) {
		return (int)((float)IsoCamera.getScreenWidth(int1) * this.zoom[int1]);
	}

	public int getHeight(int int1) {
		return (int)((float)IsoCamera.getScreenHeight(int1) * this.zoom[int1]);
	}

	public void setTargetZoom(int int1, float float1) {
		if (this.targetZoom[int1] != float1) {
			this.targetZoom[int1] = float1;
			this.startZoom[int1] = this.zoom[int1];
		}
	}

	public void setTargetZoomNoRestart(float float1) {
		if (this.targetZoom[IsoPlayer.getPlayerIndex()] != float1) {
			this.targetZoom[IsoPlayer.getPlayerIndex()] = float1;
		}
	}

	public ArrayList getDefaultZoomLevels() {
		ArrayList arrayList = new ArrayList();
		float[] floatArray = Core.TileScale == 2 ? this.zoomLevels2x : this.zoomLevels1x;
		for (int int1 = 0; int1 < floatArray.length; ++int1) {
			arrayList.add(Math.round(floatArray[int1] * 100.0F));
		}

		return arrayList;
	}

	public void setZoomLevelsFromOption(String string) {
		this.zoomLevels = Core.TileScale == 1 ? this.zoomLevels1x : this.zoomLevels2x;
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

				Collections.sort(arrayList, new Comparator(){
					
					public int compare(Integer string, Integer stringArray) {
						return stringArray - string;
					}
				});

				this.zoomLevels = new float[arrayList.size()];
				for (int int6 = 0; int6 < arrayList.size(); ++int6) {
					this.zoomLevels[int6] = (float)(Integer)arrayList.get(int6) / 100.0F;
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
				this.zoomLevels = Core.TileScale == 1 ? this.zoomLevels1x : this.zoomLevels2x;
			}

			this.zoomedInLevel = this.zoomLevels[this.zoomLevels.length - 1];
			this.zoomedOutLevel = this.zoomLevels[0];
			for (int int3 = 0; int3 < this.zoomLevels.length; ++int3) {
				float float1 = (float)int1 * this.zoomLevels[int3];
				float float2 = (float)int2 * this.zoomLevels[int3];
				try {
					this.Current = this.createTexture(float1, float2, false);
					if (this.Current != null) {
						break;
					}
				} catch (Exception exception) {
					exception.printStackTrace();
					DebugLog.log("Failed to create FBO w:" + float1 + " h:" + float2);
					this.bZoomEnabled = false;
				}
			}
		}
	}

	public void update() {
		int int1 = IsoPlayer.getPlayerIndex();
		if (!this.bZoomEnabled) {
			this.zoom[int1] = this.targetZoom[int1] = 1.0F;
		}

		if (this.Current == null) {
			this.setCameraToCentre();
		} else {
			float float1;
			if (this.bAutoZoom[IsoPlayer.getPlayerIndex()] && IsoCamera.CamCharacter != null && this.bZoomEnabled) {
				float1 = 1.0F;
				if (IsoCamera.CamCharacter.getCurrentSquare().getRoom() == null && ((!(IsoPlayer.instance.closestZombie < 6.0F) || !IsoPlayer.instance.isTargetedByZombie()) && !(IsoPlayer.instance.lastTargeted < (float)(PerformanceSettings.LockFPS * 4)) || IsoPlayer.instance.IsRunning())) {
					float1 = this.zoomedOutLevel;
				} else {
					float1 = this.zoomedInLevel;
				}

				float float2 = IsoUtils.DistanceTo(IsoCamera.RightClickX[IsoPlayer.assumedPlayer], IsoCamera.RightClickY[IsoPlayer.assumedPlayer], 0.0F, 0.0F);
				float float3 = float2 / 300.0F;
				if (float3 > 1.0F) {
					float3 = 1.0F;
				}

				float1 += float3;
				if (float1 > this.zoomLevels[0]) {
					float1 = this.zoomLevels[0];
				}

				if (IsoCamera.CamCharacter.getVehicle() != null) {
					float1 = 1.5F;
				}

				this.setTargetZoom(int1, float1);
			}

			float1 = 0.004F * GameTime.instance.getMultiplier() / GameTime.instance.getTrueMultiplier() * (Core.TileScale == 2 ? 1.5F : 1.0F);
			if (!this.bAutoZoom[IsoPlayer.getPlayerIndex()]) {
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
	}

	private void setCameraToCentre() {
		float float1 = IsoCamera.OffX[IsoPlayer.getPlayerIndex()];
		float float2 = IsoCamera.OffY[IsoPlayer.getPlayerIndex()];
		if (IsoCamera.CamCharacter != null) {
			IsoGameCharacter gameCharacter = IsoCamera.CamCharacter;
			float1 = IsoUtils.XToScreen(gameCharacter.x + IsoCamera.DeferedX[IsoPlayer.getPlayerIndex()], gameCharacter.y + IsoCamera.DeferedY[IsoPlayer.getPlayerIndex()], gameCharacter.z, 0);
			float2 = IsoUtils.YToScreen(gameCharacter.x + IsoCamera.DeferedX[IsoPlayer.getPlayerIndex()], gameCharacter.y + IsoCamera.DeferedY[IsoPlayer.getPlayerIndex()], gameCharacter.z, 0);
			float1 -= (float)(IsoCamera.getOffscreenWidth(IsoPlayer.getPlayerIndex()) / 2);
			float2 -= (float)(IsoCamera.getOffscreenHeight(IsoPlayer.getPlayerIndex()) / 2);
			float2 -= gameCharacter.getOffsetY() * 1.5F;
			float1 = (float)((int)float1);
			float2 = (float)((int)float2);
			float1 += (float)IsoCamera.PLAYER_OFFSET_X;
			float2 += (float)IsoCamera.PLAYER_OFFSET_Y;
		}

		IsoCamera.OffX[IsoPlayer.getPlayerIndex()] = float1;
		IsoCamera.OffY[IsoPlayer.getPlayerIndex()] = float2;
		IsoCamera.TOffX[IsoPlayer.getPlayerIndex()] = float1;
		IsoCamera.TOffY[IsoPlayer.getPlayerIndex()] = float2;
	}

	private TextureFBO createTexture(float float1, float float2, boolean boolean1) throws Exception {
		Texture texture;
		TextureFBO textureFBO;
		if (boolean1) {
			try {
				TextureID.bUseCompression = false;
				texture = new Texture((int)float1, (int)float2);
				textureFBO = new TextureFBO(texture);
				textureFBO.destroy();
			} finally {
				TextureID.bUseCompression = TextureID.bUseCompressionOption;
			}

			return null;
		} else {
			try {
				TextureID.bUseCompression = false;
				texture = new Texture((int)float1, (int)float2);
				textureFBO = new TextureFBO(texture);
			} finally {
				TextureID.bUseCompression = TextureID.bUseCompressionOption;
			}

			return textureFBO;
		}
	}

	public void render() {
		if (this.Current != null) {
			if (this.bZoomEnabled) {
				IndieGL.glBind((Texture)this.Current.getTexture());
			}

			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				if (Core.getInstance().RenderShader != null) {
					IndieGL.StartShader(Core.getInstance().RenderShader.getID(), int1);
				}

				int int2 = IsoCamera.getScreenLeft(int1);
				int int3 = IsoCamera.getScreenTop(int1);
				int int4 = IsoCamera.getScreenWidth(int1);
				int int5 = IsoCamera.getScreenHeight(int1);
				if (IsoPlayer.players[int1] != null || GameServer.bServer && ServerGUI.isCreated()) {
					int int6 = IsoCamera.getOffscreenLeft(int1);
					int int7 = IsoCamera.getOffscreenTop(int1);
					int int8 = IsoCamera.getOffscreenWidth(int1);
					int int9 = IsoCamera.getOffscreenHeight(int1);
					if (this.bZoomEnabled && this.zoom[int1] > 0.5F) {
						IndieGL.glTexParameteri(3553, 10241, 9729);
					} else {
						IndieGL.glTexParameteri(3553, 10241, 9728);
					}

					if (this.zoom[int1] == 0.5F) {
						IndieGL.glTexParameteri(3553, 10240, 9728);
					} else {
						IndieGL.glTexParameteri(3553, 10240, 9729);
					}

					((Texture)this.Current.getTexture()).rendershader2(int2, int3, int4, int5, int6, int7, int8, int9, 1.0F, 1.0F, 1.0F, 1.0F);
				} else {
					SpriteRenderer.instance.render((Texture)null, int2, int3, int4, int5, 0.0F, 0.0F, 0.0F, 1.0F);
				}
			}

			if (Core.getInstance().RenderShader != null) {
				IndieGL.StartShader(0, 0);
			}
		}
	}

	public TextureFBO getCurrent(int int1) {
		return this.Current;
	}

	public Texture getTexture(int int1) {
		return (Texture)this.Current.getTexture();
	}

	public void updateMipMaps() {
		this.getTexture(0).dataid.generateMipmap();
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
			this.createTexture(16.0F, 16.0F, true);
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			DebugLog.log("Failed to create Test FBO");
			Core.SafeMode = true;
			return false;
		}
	}
}
