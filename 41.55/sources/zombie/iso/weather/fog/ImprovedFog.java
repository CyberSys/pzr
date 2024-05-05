package zombie.iso.weather.fog;

import org.joml.Vector2i;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.input.GameKeyboard;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.PlayerCamera;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.fx.SteppedUpdateFloat;


public class ImprovedFog {
	private static final ImprovedFog.RectangleIterator rectangleIter = new ImprovedFog.RectangleIterator();
	private static final Vector2i rectangleMatrixPos = new Vector2i();
	private static IsoChunkMap chunkMap;
	private static int minY;
	private static int maxY;
	private static int minX;
	private static int maxX;
	private static int zLayer;
	private static Vector2i lastIterPos = new Vector2i();
	private static ImprovedFog.FogRectangle fogRectangle = new ImprovedFog.FogRectangle();
	private static boolean drawingThisLayer = false;
	private static float ZOOM = 1.0F;
	private static int PlayerIndex;
	private static int playerRow;
	private static float screenWidth;
	private static float screenHeight;
	private static float worldOffsetX;
	private static float worldOffsetY;
	private static float topAlphaHeight = 0.38F;
	private static float bottomAlphaHeight = 0.24F;
	private static float secondLayerAlpha = 0.5F;
	private static float scalingX = 1.0F;
	private static float scalingY = 1.0F;
	private static float colorR = 1.0F;
	private static float colorG = 1.0F;
	private static float colorB = 1.0F;
	private static boolean drawDebugColors = false;
	private static float octaves = 6.0F;
	private static boolean highQuality = true;
	private static boolean enableEditing = false;
	private static float alphaCircleAlpha = 0.3F;
	private static float alphaCircleRad = 2.25F;
	private static int lastRow = -1;
	private static ClimateManager climateManager;
	private static Texture noiseTexture;
	private static boolean renderOnlyOneRow = false;
	private static float baseAlpha = 0.0F;
	private static int renderEveryXRow = 1;
	private static int renderXRowsFromCenter = 0;
	private static boolean renderCurrentLayerOnly = false;
	private static float rightClickOffX = 0.0F;
	private static float rightClickOffY = 0.0F;
	private static float cameraOffscreenLeft = 0.0F;
	private static float cameraOffscreenTop = 0.0F;
	private static float cameraZoom = 0.0F;
	private static int minXOffset = -2;
	private static int maxXOffset = 12;
	private static int maxYOffset = -5;
	private static boolean renderEndOnly = false;
	private static final SteppedUpdateFloat fogIntensity = new SteppedUpdateFloat(0.0F, 0.005F, 0.0F, 1.0F);
	private static int keyPause = 0;
	private static final float[] offsets = new float[]{0.3F, 0.8F, 0.0F, 0.6F, 0.3F, 0.1F, 0.5F, 0.9F, 0.2F, 0.0F, 0.7F, 0.1F, 0.4F, 0.2F, 0.5F, 0.3F, 0.8F, 0.4F, 0.9F, 0.5F, 0.8F, 0.4F, 0.7F, 0.2F, 0.0F, 0.6F, 0.1F, 0.6F, 0.9F, 0.7F};

	public static int getMinXOffset() {
		return minXOffset;
	}

	public static void setMinXOffset(int int1) {
		minXOffset = int1;
	}

	public static int getMaxXOffset() {
		return maxXOffset;
	}

	public static void setMaxXOffset(int int1) {
		maxXOffset = int1;
	}

	public static int getMaxYOffset() {
		return maxYOffset;
	}

	public static void setMaxYOffset(int int1) {
		maxYOffset = int1;
	}

	public static boolean isRenderEndOnly() {
		return renderEndOnly;
	}

	public static void setRenderEndOnly(boolean boolean1) {
		renderEndOnly = boolean1;
	}

	public static float getAlphaCircleAlpha() {
		return alphaCircleAlpha;
	}

	public static void setAlphaCircleAlpha(float float1) {
		alphaCircleAlpha = float1;
	}

	public static float getAlphaCircleRad() {
		return alphaCircleRad;
	}

	public static void setAlphaCircleRad(float float1) {
		alphaCircleRad = float1;
	}

	public static boolean isHighQuality() {
		return highQuality;
	}

	public static void setHighQuality(boolean boolean1) {
		highQuality = boolean1;
	}

	public static boolean isEnableEditing() {
		return enableEditing;
	}

	public static void setEnableEditing(boolean boolean1) {
		enableEditing = boolean1;
	}

	public static float getTopAlphaHeight() {
		return topAlphaHeight;
	}

	public static void setTopAlphaHeight(float float1) {
		topAlphaHeight = float1;
	}

	public static float getBottomAlphaHeight() {
		return bottomAlphaHeight;
	}

	public static void setBottomAlphaHeight(float float1) {
		bottomAlphaHeight = float1;
	}

	public static boolean isDrawDebugColors() {
		return drawDebugColors;
	}

	public static void setDrawDebugColors(boolean boolean1) {
		drawDebugColors = boolean1;
	}

	public static float getOctaves() {
		return octaves;
	}

	public static void setOctaves(float float1) {
		octaves = float1;
	}

	public static float getColorR() {
		return colorR;
	}

	public static void setColorR(float float1) {
		colorR = float1;
	}

	public static float getColorG() {
		return colorG;
	}

	public static void setColorG(float float1) {
		colorG = float1;
	}

	public static float getColorB() {
		return colorB;
	}

	public static void setColorB(float float1) {
		colorB = float1;
	}

	public static float getSecondLayerAlpha() {
		return secondLayerAlpha;
	}

	public static void setSecondLayerAlpha(float float1) {
		secondLayerAlpha = float1;
	}

	public static float getScalingX() {
		return scalingX;
	}

	public static void setScalingX(float float1) {
		scalingX = float1;
	}

	public static float getScalingY() {
		return scalingY;
	}

	public static void setScalingY(float float1) {
		scalingY = float1;
	}

	public static boolean isRenderOnlyOneRow() {
		return renderOnlyOneRow;
	}

	public static void setRenderOnlyOneRow(boolean boolean1) {
		renderOnlyOneRow = boolean1;
	}

	public static float getBaseAlpha() {
		return baseAlpha;
	}

	public static void setBaseAlpha(float float1) {
		baseAlpha = float1;
	}

	public static int getRenderEveryXRow() {
		return renderEveryXRow;
	}

	public static void setRenderEveryXRow(int int1) {
		renderEveryXRow = int1;
	}

	public static boolean isRenderCurrentLayerOnly() {
		return renderCurrentLayerOnly;
	}

	public static void setRenderCurrentLayerOnly(boolean boolean1) {
		renderCurrentLayerOnly = boolean1;
	}

	public static int getRenderXRowsFromCenter() {
		return renderXRowsFromCenter;
	}

	public static void setRenderXRowsFromCenter(int int1) {
		renderXRowsFromCenter = int1;
	}

	public static void update() {
		updateKeys();
		if (noiseTexture == null) {
			noiseTexture = Texture.getSharedTexture("media/textures/weather/fognew/fog_noise.png");
		}

		climateManager = ClimateManager.getInstance();
		if (!enableEditing) {
			highQuality = PerformanceSettings.FogQuality == 0;
			fogIntensity.update(GameTime.getInstance().getMultiplier());
			fogIntensity.setTarget(climateManager.getFogIntensity());
			baseAlpha = fogIntensity.value();
			if (highQuality) {
				renderEveryXRow = 1;
				topAlphaHeight = 0.38F;
				bottomAlphaHeight = 0.24F;
				octaves = 6.0F;
				secondLayerAlpha = 0.5F;
			} else {
				renderEveryXRow = 2;
				topAlphaHeight = 0.32F;
				bottomAlphaHeight = 0.32F;
				octaves = 3.0F;
				secondLayerAlpha = 1.0F;
			}

			colorR = climateManager.getColorNewFog().getExterior().r;
			colorG = climateManager.getColorNewFog().getExterior().g;
			colorB = climateManager.getColorNewFog().getExterior().b;
		}

		if (baseAlpha <= 0.0F) {
			scalingX = 0.0F;
			scalingY = 0.0F;
		} else {
			double double1 = (double)climateManager.getWindAngleRadians();
			double1 -= 2.356194490192345;
			double1 = 3.141592653589793 - double1;
			float float1 = (float)Math.cos(double1);
			float float2 = (float)Math.sin(double1);
			scalingX += float1 * climateManager.getWindIntensity() * GameTime.getInstance().getMultiplier();
			scalingY += float2 * climateManager.getWindIntensity() * GameTime.getInstance().getMultiplier();
		}
	}

	public static void startRender(int int1, int int2) {
		climateManager = ClimateManager.getInstance();
		if (int2 < 2 && !(baseAlpha <= 0.0F) && PerformanceSettings.FogQuality != 2) {
			drawingThisLayer = true;
			IsoPlayer player = IsoPlayer.players[int1];
			if (renderCurrentLayerOnly && player.getZ() != (float)int2) {
				drawingThisLayer = false;
			} else if (player.isInARoom() && int2 > 0) {
				drawingThisLayer = false;
			} else {
				playerRow = (int)player.getX() + (int)player.getY();
				ZOOM = Core.getInstance().getZoom(int1);
				zLayer = int2;
				PlayerIndex = int1;
				PlayerCamera playerCamera = IsoCamera.cameras[int1];
				screenWidth = (float)IsoCamera.getOffscreenWidth(int1);
				screenHeight = (float)IsoCamera.getOffscreenHeight(int1);
				worldOffsetX = playerCamera.getOffX() - (float)IsoCamera.getOffscreenLeft(PlayerIndex) * ZOOM;
				worldOffsetY = playerCamera.getOffY() + (float)IsoCamera.getOffscreenTop(PlayerIndex) * ZOOM;
				rightClickOffX = playerCamera.RightClickX;
				rightClickOffY = playerCamera.RightClickY;
				cameraOffscreenLeft = (float)IsoCamera.getOffscreenLeft(int1);
				cameraOffscreenTop = (float)IsoCamera.getOffscreenTop(int1);
				cameraZoom = ZOOM;
				if (!enableEditing) {
					if (player.getVehicle() != null) {
						alphaCircleAlpha = 0.0F;
						alphaCircleRad = highQuality ? 2.0F : 2.6F;
					} else if (player.isInARoom()) {
						alphaCircleAlpha = 0.0F;
						alphaCircleRad = highQuality ? 1.25F : 1.5F;
					} else {
						alphaCircleAlpha = highQuality ? 0.1F : 0.16F;
						alphaCircleRad = highQuality ? 2.5F : 3.0F;
						if (climateManager.getWeatherPeriod().isRunning() && (climateManager.getWeatherPeriod().isTropicalStorm() || climateManager.getWeatherPeriod().isThunderStorm())) {
							alphaCircleRad *= 0.6F;
						}
					}
				}

				byte byte1 = 0;
				byte byte2 = 0;
				int int3 = byte1 + IsoCamera.getOffscreenWidth(int1);
				int int4 = byte2 + IsoCamera.getOffscreenHeight(int1);
				float float1 = IsoUtils.XToIso((float)byte1, (float)byte2, (float)zLayer);
				float float2 = IsoUtils.YToIso((float)byte1, (float)byte2, (float)zLayer);
				float float3 = IsoUtils.XToIso((float)int3, (float)int4, (float)zLayer);
				float float4 = IsoUtils.YToIso((float)int3, (float)int4, (float)zLayer);
				float float5 = IsoUtils.YToIso((float)byte1, (float)int4, (float)zLayer);
				minY = (int)float2;
				maxY = (int)float4;
				minX = (int)float1;
				maxX = (int)float3;
				if (IsoPlayer.numPlayers > 1) {
					maxX = Math.max(maxX, IsoWorld.instance.CurrentCell.getMaxX());
					maxY = Math.max(maxY, IsoWorld.instance.CurrentCell.getMaxY());
				}

				minX += minXOffset;
				maxX += maxXOffset;
				maxY += maxYOffset;
				int int5 = maxX - minX;
				int int6 = int5;
				if (minY != maxY) {
					int6 = (int)((float)int5 + PZMath.abs((float)(minY - maxY)));
				}

				rectangleIter.reset(int5, int6);
				lastRow = -1;
				fogRectangle.hasStarted = false;
				chunkMap = IsoWorld.instance.getCell().getChunkMap(int1);
			}
		} else {
			drawingThisLayer = false;
		}
	}

	public static void renderRowsBehind(IsoGridSquare square) {
		if (drawingThisLayer) {
			int int1 = -1;
			if (square != null) {
				int1 = square.getX() + square.getY();
				if (int1 < minX + minY) {
					return;
				}
			}

			if (lastRow < 0 || lastRow != int1) {
				Vector2i vector2i = rectangleMatrixPos;
				while (rectangleIter.next(vector2i)) {
					if (vector2i != null) {
						int int2 = vector2i.x + minX;
						int int3 = vector2i.y + minY;
						int int4 = int2 + int3;
						if (int4 != lastRow) {
							if (lastRow >= 0 && (!renderEndOnly || square == null)) {
								endFogRectangle(lastIterPos.x, lastIterPos.y, zLayer);
							}

							lastRow = int4;
						}

						IsoGridSquare square2 = chunkMap.getGridSquare(int2, int3, zLayer);
						boolean boolean1 = true;
						if (square2 != null && (!square2.isExteriorCache || square2.isInARoom())) {
							boolean1 = false;
						}

						if (boolean1) {
							if (!renderEndOnly || square == null) {
								startFogRectangle(int2, int3, zLayer);
							}
						} else if (!renderEndOnly || square == null) {
							endFogRectangle(lastIterPos.x, lastIterPos.y, zLayer);
						}

						lastIterPos.set(int2, int3);
						if (int1 != -1 && int4 == int1) {
							break;
						}
					}
				}
			}
		}
	}

	public static void endRender() {
		if (drawingThisLayer) {
			renderRowsBehind((IsoGridSquare)null);
			if (fogRectangle.hasStarted) {
				endFogRectangle(lastIterPos.x, lastIterPos.y, zLayer);
			}
		}
	}

	private static void startFogRectangle(int int1, int int2, int int3) {
		if (!fogRectangle.hasStarted) {
			fogRectangle.hasStarted = true;
			fogRectangle.startX = int1;
			fogRectangle.startY = int2;
			fogRectangle.Z = int3;
		}
	}

	private static void endFogRectangle(int int1, int int2, int int3) {
		if (fogRectangle.hasStarted) {
			fogRectangle.hasStarted = false;
			fogRectangle.endX = int1;
			fogRectangle.endY = int2;
			fogRectangle.Z = int3;
			renderFogSegment();
		}
	}

	private static void renderFogSegment() {
		int int1 = fogRectangle.startX + fogRectangle.startY;
		int int2 = fogRectangle.endX + fogRectangle.endY;
		if (Core.bDebug && int1 != int2) {
			DebugLog.log("ROWS NOT EQUAL");
		}

		if (renderOnlyOneRow) {
			if (int1 != playerRow) {
				return;
			}
		} else if (int1 % renderEveryXRow != 0) {
			return;
		}

		if (!Core.bDebug || renderXRowsFromCenter < 1 || int1 >= playerRow - renderXRowsFromCenter && int1 <= playerRow + renderXRowsFromCenter) {
			float float1 = baseAlpha;
			ImprovedFog.FogRectangle fogRectangle = fogRectangle;
			float float2 = IsoUtils.XToScreenExact((float)fogRectangle.startX, (float)fogRectangle.startY, (float)fogRectangle.Z, 0);
			float float3 = IsoUtils.YToScreenExact((float)fogRectangle.startX, (float)fogRectangle.startY, (float)fogRectangle.Z, 0);
			float float4 = IsoUtils.XToScreenExact((float)fogRectangle.endX, (float)fogRectangle.endY, (float)fogRectangle.Z, 0);
			float float5 = IsoUtils.YToScreenExact((float)fogRectangle.endX, (float)fogRectangle.endY, (float)fogRectangle.Z, 0);
			float2 -= 32.0F * (float)Core.TileScale;
			float3 -= 80.0F * (float)Core.TileScale;
			float4 += 32.0F * (float)Core.TileScale;
			float float6 = 96.0F * (float)Core.TileScale;
			float float7 = (float4 - float2) / (64.0F * (float)Core.TileScale);
			float float8 = (float)fogRectangle.startX % 6.0F;
			float float9 = float8 / 6.0F;
			float float10 = float7 / 6.0F;
			float float11 = float10 + float9;
			if (FogShader.instance.StartShader()) {
				FogShader.instance.setScreenInfo(screenWidth, screenHeight, ZOOM, zLayer > 0 ? secondLayerAlpha : 1.0F);
				FogShader.instance.setTextureInfo(drawDebugColors ? 1.0F : 0.0F, octaves, float1, (float)Core.TileScale);
				FogShader.instance.setRectangleInfo((float)((int)float2), (float)((int)float3), (float)((int)(float4 - float2)), (float)((int)float6));
				FogShader.instance.setWorldOffset(worldOffsetX, worldOffsetY, rightClickOffX, rightClickOffY);
				FogShader.instance.setScalingInfo(scalingX, scalingY, (float)zLayer, highQuality ? 0.0F : 1.0F);
				FogShader.instance.setColorInfo(colorR, colorG, colorB, 1.0F);
				FogShader.instance.setParamInfo(topAlphaHeight, bottomAlphaHeight, alphaCircleAlpha, alphaCircleRad);
				FogShader.instance.setCameraInfo(cameraOffscreenLeft, cameraOffscreenTop, cameraZoom, offsets[int1 % offsets.length]);
				SpriteRenderer.instance.render(noiseTexture, (float)((int)float2), (float)((int)float3), (float)((int)(float4 - float2)), (float)((int)float6), 1.0F, 1.0F, 1.0F, float1, float9, 0.0F, float11, 0.0F, float11, 1.0F, float9, 1.0F);
				IndieGL.EndShader();
			}
		}
	}

	public static void DrawSubTextureRGBA(Texture texture, double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12) {
		if (texture != null && !(double3 <= 0.0) && !(double4 <= 0.0) && !(double7 <= 0.0) && !(double8 <= 0.0)) {
			double double13 = double5 + (double)texture.offsetX;
			double double14 = double6 + (double)texture.offsetY;
			if (!(double14 + double8 < 0.0) && !(double14 > 4096.0)) {
				float float1 = PZMath.clamp((float)double1, 0.0F, (float)texture.getWidth());
				float float2 = PZMath.clamp((float)double2, 0.0F, (float)texture.getHeight());
				float float3 = PZMath.clamp((float)((double)float1 + double3), 0.0F, (float)texture.getWidth()) - float1;
				float float4 = PZMath.clamp((float)((double)float2 + double4), 0.0F, (float)texture.getHeight()) - float2;
				float float5 = float1 / (float)texture.getWidth();
				float float6 = float2 / (float)texture.getHeight();
				float float7 = (float1 + float3) / (float)texture.getWidth();
				float float8 = (float2 + float4) / (float)texture.getHeight();
				float float9 = texture.getXEnd() - texture.getXStart();
				float float10 = texture.getYEnd() - texture.getYStart();
				float5 = texture.getXStart() + float5 * float9;
				float7 = texture.getXStart() + float7 * float9;
				float6 = texture.getYStart() + float6 * float10;
				float8 = texture.getYStart() + float8 * float10;
				SpriteRenderer.instance.render(texture, (float)double13, (float)double14, (float)double7, (float)double8, (float)double9, (float)double10, (float)double11, (float)double12, float5, float6, float7, float6, float7, float8, float5, float8);
			}
		}
	}

	public static void updateKeys() {
		if (Core.bDebug) {
			if (keyPause > 0) {
				--keyPause;
			}

			if (keyPause <= 0 && GameKeyboard.isKeyDown(72)) {
				DebugLog.log("Reloading fog shader...");
				keyPause = 30;
				FogShader.instance.reloadShader();
			}
		}
	}

	private static class RectangleIterator {
		private int curX = 0;
		private int curY = 0;
		private int sX;
		private int sY;
		private int rowLen = 0;
		private boolean altRow = false;
		private int curRow = 0;
		private int rowIndex = 0;
		private int maxRows = 0;

		public void reset(int int1, int int2) {
			this.sX = 0;
			this.sY = 0;
			this.curX = 0;
			this.curY = 0;
			this.curRow = 0;
			this.altRow = false;
			this.rowIndex = 0;
			this.rowLen = (int)PZMath.ceil((float)int2 / 2.0F);
			this.maxRows = int1;
		}

		public boolean next(Vector2i vector2i) {
			if (this.rowLen > 0 && this.maxRows > 0 && this.curRow < this.maxRows) {
				vector2i.set(this.curX, this.curY);
				++this.rowIndex;
				if (this.rowIndex == this.rowLen) {
					this.rowLen = this.altRow ? this.rowLen - 1 : this.rowLen + 1;
					this.rowIndex = 0;
					this.sX = this.altRow ? this.sX + 1 : this.sX;
					this.sY = this.altRow ? this.sY : this.sY + 1;
					this.altRow = !this.altRow;
					this.curX = this.sX;
					this.curY = this.sY;
					++this.curRow;
					return this.curRow != this.maxRows;
				} else {
					++this.curX;
					--this.curY;
					return true;
				}
			} else {
				vector2i.set(0, 0);
				return false;
			}
		}
	}

	private static class FogRectangle {
		int startX;
		int startY;
		int endX;
		int endY;
		int Z;
		boolean hasStarted = false;
	}
}
