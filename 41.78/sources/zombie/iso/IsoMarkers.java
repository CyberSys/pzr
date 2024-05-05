package zombie.iso;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.debug.LineDrawer;
import zombie.network.GameServer;
import zombie.util.Type;


public final class IsoMarkers {
	public static final IsoMarkers instance = new IsoMarkers();
	private static int NextIsoMarkerID = 0;
	private final List markers = new ArrayList();
	private final List circlemarkers = new ArrayList();
	private static int NextCircleIsoMarkerID = 0;

	private IsoMarkers() {
	}

	public void init() {
	}

	public void reset() {
		this.markers.clear();
		this.circlemarkers.clear();
	}

	public void update() {
		if (!GameServer.bServer) {
			this.updateIsoMarkers();
			this.updateCircleIsoMarkers();
		}
	}

	private void updateIsoMarkers() {
		if (IsoCamera.frameState.playerIndex == 0) {
			if (this.markers.size() != 0) {
				int int1;
				for (int1 = this.markers.size() - 1; int1 >= 0; --int1) {
					if (((IsoMarkers.IsoMarker)this.markers.get(int1)).isRemoved()) {
						if (((IsoMarkers.IsoMarker)this.markers.get(int1)).hasTempSquareObject()) {
							((IsoMarkers.IsoMarker)this.markers.get(int1)).removeTempSquareObjects();
						}

						this.markers.remove(int1);
					}
				}

				for (int1 = 0; int1 < this.markers.size(); ++int1) {
					IsoMarkers.IsoMarker marker = (IsoMarkers.IsoMarker)this.markers.get(int1);
					if (marker.alphaInc) {
						marker.alpha += GameTime.getInstance().getMultiplier() * marker.fadeSpeed;
						if (marker.alpha > marker.alphaMax) {
							marker.alphaInc = false;
							marker.alpha = marker.alphaMax;
						}
					} else {
						marker.alpha -= GameTime.getInstance().getMultiplier() * marker.fadeSpeed;
						if (marker.alpha < marker.alphaMin) {
							marker.alphaInc = true;
							marker.alpha = 0.3F;
						}
					}
				}
			}
		}
	}

	public boolean removeIsoMarker(IsoMarkers.IsoMarker marker) {
		return this.removeIsoMarker(marker.getID());
	}

	public boolean removeIsoMarker(int int1) {
		for (int int2 = this.markers.size() - 1; int2 >= 0; --int2) {
			if (((IsoMarkers.IsoMarker)this.markers.get(int2)).getID() == int1) {
				((IsoMarkers.IsoMarker)this.markers.get(int2)).remove();
				this.markers.remove(int2);
				return true;
			}
		}

		return false;
	}

	public IsoMarkers.IsoMarker getIsoMarker(int int1) {
		for (int int2 = 0; int2 < this.markers.size(); ++int2) {
			if (((IsoMarkers.IsoMarker)this.markers.get(int2)).getID() == int1) {
				return (IsoMarkers.IsoMarker)this.markers.get(int2);
			}
		}

		return null;
	}

	public IsoMarkers.IsoMarker addIsoMarker(String string, IsoGridSquare square, float float1, float float2, float float3, boolean boolean1, boolean boolean2) {
		if (GameServer.bServer) {
			return null;
		} else {
			IsoMarkers.IsoMarker marker = new IsoMarkers.IsoMarker();
			marker.setSquare(square);
			marker.init(string, square.x, square.y, square.z, square, boolean2);
			marker.setR(float1);
			marker.setG(float2);
			marker.setB(float3);
			marker.setA(1.0F);
			marker.setDoAlpha(boolean1);
			marker.setFadeSpeed(0.006F);
			marker.setAlpha(1.0F);
			marker.setAlphaMin(0.3F);
			marker.setAlphaMax(1.0F);
			this.markers.add(marker);
			return marker;
		}
	}

	public IsoMarkers.IsoMarker addIsoMarker(KahluaTable kahluaTable, KahluaTable kahluaTable2, IsoGridSquare square, float float1, float float2, float float3, boolean boolean1, boolean boolean2) {
		return this.addIsoMarker(kahluaTable, kahluaTable2, square, float1, float2, float3, boolean1, boolean2, 0.006F, 0.3F, 1.0F);
	}

	public IsoMarkers.IsoMarker addIsoMarker(KahluaTable kahluaTable, KahluaTable kahluaTable2, IsoGridSquare square, float float1, float float2, float float3, boolean boolean1, boolean boolean2, float float4, float float5, float float6) {
		if (GameServer.bServer) {
			return null;
		} else {
			IsoMarkers.IsoMarker marker = new IsoMarkers.IsoMarker();
			marker.init(kahluaTable, kahluaTable2, square.x, square.y, square.z, square, boolean2);
			marker.setSquare(square);
			marker.setR(float1);
			marker.setG(float2);
			marker.setB(float3);
			marker.setA(1.0F);
			marker.setDoAlpha(boolean1);
			marker.setFadeSpeed(float4);
			marker.setAlpha(0.0F);
			marker.setAlphaMin(float5);
			marker.setAlphaMax(float6);
			this.markers.add(marker);
			return marker;
		}
	}

	public void renderIsoMarkers(IsoCell.PerPlayerRender perPlayerRender, int int1, int int2) {
		if (!GameServer.bServer && this.markers.size() != 0) {
			IsoPlayer player = IsoPlayer.players[int2];
			if (player != null) {
				for (int int3 = 0; int3 < this.markers.size(); ++int3) {
					IsoMarkers.IsoMarker marker = (IsoMarkers.IsoMarker)this.markers.get(int3);
					if (marker.z == (float)int1 && marker.z == player.getZ() && marker.active) {
						for (int int4 = 0; int4 < marker.textures.size(); ++int4) {
							Texture texture = (Texture)marker.textures.get(int4);
							float float1 = IsoUtils.XToScreen(marker.x, marker.y, marker.z, 0) - IsoCamera.cameras[int2].getOffX() - (float)texture.getWidth() / 2.0F;
							float float2 = IsoUtils.YToScreen(marker.x, marker.y, marker.z, 0) - IsoCamera.cameras[int2].getOffY() - (float)texture.getHeight();
							SpriteRenderer.instance.render(texture, float1, float2, (float)texture.getWidth(), (float)texture.getHeight(), marker.r, marker.g, marker.b, marker.alpha, (Consumer)null);
						}
					}
				}
			}
		}
	}

	public void renderIsoMarkersDeferred(IsoCell.PerPlayerRender perPlayerRender, int int1, int int2) {
		if (!GameServer.bServer && this.markers.size() != 0) {
			IsoPlayer player = IsoPlayer.players[int2];
			if (player != null) {
				for (int int3 = 0; int3 < this.markers.size(); ++int3) {
					IsoMarkers.IsoMarker marker = (IsoMarkers.IsoMarker)this.markers.get(int3);
					if (marker.z == (float)int1 && marker.z == player.getZ() && marker.active) {
						for (int int4 = 0; int4 < marker.overlayTextures.size(); ++int4) {
							Texture texture = (Texture)marker.overlayTextures.get(int4);
							float float1 = IsoUtils.XToScreen(marker.x, marker.y, marker.z, 0) - IsoCamera.cameras[int2].getOffX() - (float)texture.getWidth() / 2.0F;
							float float2 = IsoUtils.YToScreen(marker.x, marker.y, marker.z, 0) - IsoCamera.cameras[int2].getOffY() - (float)texture.getHeight();
							SpriteRenderer.instance.render(texture, float1, float2, (float)texture.getWidth(), (float)texture.getHeight(), marker.r, marker.g, marker.b, marker.alpha, (Consumer)null);
						}
					}
				}
			}
		}
	}

	public void renderIsoMarkersOnSquare(IsoCell.PerPlayerRender perPlayerRender, int int1, int int2) {
		if (!GameServer.bServer && this.markers.size() != 0) {
			IsoPlayer player = IsoPlayer.players[int2];
			if (player != null) {
				for (int int3 = 0; int3 < this.markers.size(); ++int3) {
					IsoMarkers.IsoMarker marker = (IsoMarkers.IsoMarker)this.markers.get(int3);
					if (marker.z == (float)int1 && marker.z == player.getZ() && marker.active) {
						for (int int4 = 0; int4 < marker.overlayTextures.size(); ++int4) {
							Texture texture = (Texture)marker.overlayTextures.get(int4);
							float float1 = IsoUtils.XToScreen(marker.x, marker.y, marker.z, 0) - IsoCamera.cameras[int2].getOffX() - (float)texture.getWidth() / 2.0F;
							float float2 = IsoUtils.YToScreen(marker.x, marker.y, marker.z, 0) - IsoCamera.cameras[int2].getOffY() - (float)texture.getHeight();
							SpriteRenderer.instance.render(texture, float1, float2, (float)texture.getWidth(), (float)texture.getHeight(), marker.r, marker.g, marker.b, marker.alpha, (Consumer)null);
						}
					}
				}
			}
		}
	}

	private void updateCircleIsoMarkers() {
		if (IsoCamera.frameState.playerIndex == 0) {
			if (this.circlemarkers.size() != 0) {
				int int1;
				for (int1 = this.circlemarkers.size() - 1; int1 >= 0; --int1) {
					if (((IsoMarkers.CircleIsoMarker)this.circlemarkers.get(int1)).isRemoved()) {
						this.circlemarkers.remove(int1);
					}
				}

				for (int1 = 0; int1 < this.circlemarkers.size(); ++int1) {
					IsoMarkers.CircleIsoMarker circleIsoMarker = (IsoMarkers.CircleIsoMarker)this.circlemarkers.get(int1);
					if (circleIsoMarker.alphaInc) {
						circleIsoMarker.alpha += GameTime.getInstance().getMultiplier() * circleIsoMarker.fadeSpeed;
						if (circleIsoMarker.alpha > circleIsoMarker.alphaMax) {
							circleIsoMarker.alphaInc = false;
							circleIsoMarker.alpha = circleIsoMarker.alphaMax;
						}
					} else {
						circleIsoMarker.alpha -= GameTime.getInstance().getMultiplier() * circleIsoMarker.fadeSpeed;
						if (circleIsoMarker.alpha < circleIsoMarker.alphaMin) {
							circleIsoMarker.alphaInc = true;
							circleIsoMarker.alpha = 0.3F;
						}
					}
				}
			}
		}
	}

	public boolean removeCircleIsoMarker(IsoMarkers.CircleIsoMarker circleIsoMarker) {
		return this.removeCircleIsoMarker(circleIsoMarker.getID());
	}

	public boolean removeCircleIsoMarker(int int1) {
		for (int int2 = this.circlemarkers.size() - 1; int2 >= 0; --int2) {
			if (((IsoMarkers.CircleIsoMarker)this.circlemarkers.get(int2)).getID() == int1) {
				((IsoMarkers.CircleIsoMarker)this.circlemarkers.get(int2)).remove();
				this.circlemarkers.remove(int2);
				return true;
			}
		}

		return false;
	}

	public IsoMarkers.CircleIsoMarker getCircleIsoMarker(int int1) {
		for (int int2 = 0; int2 < this.circlemarkers.size(); ++int2) {
			if (((IsoMarkers.CircleIsoMarker)this.circlemarkers.get(int2)).getID() == int1) {
				return (IsoMarkers.CircleIsoMarker)this.circlemarkers.get(int2);
			}
		}

		return null;
	}

	public IsoMarkers.CircleIsoMarker addCircleIsoMarker(IsoGridSquare square, float float1, float float2, float float3, float float4) {
		if (GameServer.bServer) {
			return null;
		} else {
			IsoMarkers.CircleIsoMarker circleIsoMarker = new IsoMarkers.CircleIsoMarker();
			circleIsoMarker.init(square.x, square.y, square.z, square);
			circleIsoMarker.setSquare(square);
			circleIsoMarker.setR(float1);
			circleIsoMarker.setG(float2);
			circleIsoMarker.setB(float3);
			circleIsoMarker.setA(float4);
			circleIsoMarker.setDoAlpha(false);
			circleIsoMarker.setFadeSpeed(0.006F);
			circleIsoMarker.setAlpha(1.0F);
			circleIsoMarker.setAlphaMin(1.0F);
			circleIsoMarker.setAlphaMax(1.0F);
			this.circlemarkers.add(circleIsoMarker);
			return circleIsoMarker;
		}
	}

	public void renderCircleIsoMarkers(IsoCell.PerPlayerRender perPlayerRender, int int1, int int2) {
		if (!GameServer.bServer && this.circlemarkers.size() != 0) {
			IsoPlayer player = IsoPlayer.players[int2];
			if (player != null) {
				for (int int3 = 0; int3 < this.circlemarkers.size(); ++int3) {
					IsoMarkers.CircleIsoMarker circleIsoMarker = (IsoMarkers.CircleIsoMarker)this.circlemarkers.get(int3);
					if (circleIsoMarker.z == (float)int1 && circleIsoMarker.z == player.getZ() && circleIsoMarker.active) {
						LineDrawer.DrawIsoCircle(circleIsoMarker.x, circleIsoMarker.y, circleIsoMarker.z, circleIsoMarker.size, 32, circleIsoMarker.r, circleIsoMarker.g, circleIsoMarker.b, circleIsoMarker.a);
					}
				}
			}
		}
	}

	public void render() {
		this.update();
	}

	public static final class IsoMarker {
		private int ID;
		private ArrayList textures = new ArrayList();
		private ArrayList overlayTextures = new ArrayList();
		private ArrayList tempObjects = new ArrayList();
		private IsoGridSquare square;
		private float x;
		private float y;
		private float z;
		private float r;
		private float g;
		private float b;
		private float a;
		private boolean doAlpha;
		private float fadeSpeed = 0.006F;
		private float alpha = 0.0F;
		private float alphaMax = 1.0F;
		private float alphaMin = 0.3F;
		private boolean alphaInc = true;
		private boolean active = true;
		private boolean isRemoved = false;

		public IsoMarker() {
			this.ID = IsoMarkers.NextIsoMarkerID++;
		}

		public int getID() {
			return this.ID;
		}

		public void remove() {
			this.isRemoved = true;
		}

		public boolean isRemoved() {
			return this.isRemoved;
		}

		public void init(KahluaTable kahluaTable, KahluaTable kahluaTable2, int int1, int int2, int int3, IsoGridSquare square) {
			this.square = square;
			int int4;
			int int5;
			String string;
			Texture texture;
			if (kahluaTable != null) {
				int4 = kahluaTable.len();
				for (int5 = 1; int5 <= int4; ++int5) {
					string = (String)Type.tryCastTo(kahluaTable.rawget(int5), String.class);
					texture = Texture.trygetTexture(string);
					if (texture != null) {
						this.textures.add(texture);
						this.setPos(int1, int2, int3);
					}
				}
			}

			if (kahluaTable2 != null) {
				int4 = kahluaTable2.len();
				for (int5 = 1; int5 <= int4; ++int5) {
					string = (String)Type.tryCastTo(kahluaTable2.rawget(int5), String.class);
					texture = Texture.trygetTexture(string);
					if (texture != null) {
						this.overlayTextures.add(texture);
						this.setPos(int1, int2, int3);
					}
				}
			}
		}

		public void init(KahluaTable kahluaTable, KahluaTable kahluaTable2, int int1, int int2, int int3, IsoGridSquare square, boolean boolean1) {
			this.square = square;
			if (boolean1) {
				if (kahluaTable != null) {
					int int4 = kahluaTable.len();
					for (int int5 = 1; int5 <= int4; ++int5) {
						String string = (String)Type.tryCastTo(kahluaTable.rawget(int5), String.class);
						Texture texture = Texture.trygetTexture(string);
						if (texture != null) {
							IsoObject object = new IsoObject(square.getCell(), square, texture.getName());
							this.tempObjects.add(object);
							this.addTempSquareObject(object);
							this.setPos(int1, int2, int3);
						}
					}
				}
			} else {
				this.init(kahluaTable, kahluaTable2, int1, int2, int3, square);
			}
		}

		public void init(String string, int int1, int int2, int int3, IsoGridSquare square, boolean boolean1) {
			this.square = square;
			if (boolean1 && string != null) {
				IsoObject object = IsoObject.getNew(square, string, string, false);
				this.tempObjects.add(object);
				this.addTempSquareObject(object);
				this.setPos(int1, int2, int3);
			}
		}

		public boolean hasTempSquareObject() {
			return this.tempObjects.size() > 0;
		}

		public void addTempSquareObject(IsoObject object) {
			this.square.localTemporaryObjects.add(object);
		}

		public void removeTempSquareObjects() {
			this.square.localTemporaryObjects.clear();
		}

		public float getX() {
			return this.x;
		}

		public float getY() {
			return this.y;
		}

		public float getZ() {
			return this.z;
		}

		public float getR() {
			return this.r;
		}

		public float getG() {
			return this.g;
		}

		public float getB() {
			return this.b;
		}

		public float getA() {
			return this.a;
		}

		public void setR(float float1) {
			this.r = float1;
		}

		public void setG(float float1) {
			this.g = float1;
		}

		public void setB(float float1) {
			this.b = float1;
		}

		public void setA(float float1) {
			this.a = float1;
		}

		public float getAlpha() {
			return this.alpha;
		}

		public void setAlpha(float float1) {
			this.alpha = float1;
		}

		public float getAlphaMax() {
			return this.alphaMax;
		}

		public void setAlphaMax(float float1) {
			this.alphaMax = float1;
		}

		public float getAlphaMin() {
			return this.alphaMin;
		}

		public void setAlphaMin(float float1) {
			this.alphaMin = float1;
		}

		public boolean isDoAlpha() {
			return this.doAlpha;
		}

		public void setDoAlpha(boolean boolean1) {
			this.doAlpha = boolean1;
		}

		public float getFadeSpeed() {
			return this.fadeSpeed;
		}

		public void setFadeSpeed(float float1) {
			this.fadeSpeed = float1;
		}

		public IsoGridSquare getSquare() {
			return this.square;
		}

		public void setSquare(IsoGridSquare square) {
			this.square = square;
		}

		public void setPos(int int1, int int2, int int3) {
			this.x = (float)int1 + 0.5F;
			this.y = (float)int2 + 0.5F;
			this.z = (float)int3;
		}

		public boolean isActive() {
			return this.active;
		}

		public void setActive(boolean boolean1) {
			this.active = boolean1;
		}
	}

	public static final class CircleIsoMarker {
		private int ID;
		private IsoGridSquare square;
		private float x;
		private float y;
		private float z;
		private float r;
		private float g;
		private float b;
		private float a;
		private float size;
		private boolean doAlpha;
		private float fadeSpeed = 0.006F;
		private float alpha = 0.0F;
		private float alphaMax = 1.0F;
		private float alphaMin = 0.3F;
		private boolean alphaInc = true;
		private boolean active = true;
		private boolean isRemoved = false;

		public CircleIsoMarker() {
			this.ID = IsoMarkers.NextCircleIsoMarkerID++;
		}

		public int getID() {
			return this.ID;
		}

		public void remove() {
			this.isRemoved = true;
		}

		public boolean isRemoved() {
			return this.isRemoved;
		}

		public void init(int int1, int int2, int int3, IsoGridSquare square) {
			this.square = square;
		}

		public float getX() {
			return this.x;
		}

		public float getY() {
			return this.y;
		}

		public float getZ() {
			return this.z;
		}

		public float getR() {
			return this.r;
		}

		public float getG() {
			return this.g;
		}

		public float getB() {
			return this.b;
		}

		public float getA() {
			return this.a;
		}

		public void setR(float float1) {
			this.r = float1;
		}

		public void setG(float float1) {
			this.g = float1;
		}

		public void setB(float float1) {
			this.b = float1;
		}

		public void setA(float float1) {
			this.a = float1;
		}

		public float getSize() {
			return this.size;
		}

		public void setSize(float float1) {
			this.size = float1;
		}

		public float getAlpha() {
			return this.alpha;
		}

		public void setAlpha(float float1) {
			this.alpha = float1;
		}

		public float getAlphaMax() {
			return this.alphaMax;
		}

		public void setAlphaMax(float float1) {
			this.alphaMax = float1;
		}

		public float getAlphaMin() {
			return this.alphaMin;
		}

		public void setAlphaMin(float float1) {
			this.alphaMin = float1;
		}

		public boolean isDoAlpha() {
			return this.doAlpha;
		}

		public void setDoAlpha(boolean boolean1) {
			this.doAlpha = boolean1;
		}

		public float getFadeSpeed() {
			return this.fadeSpeed;
		}

		public void setFadeSpeed(float float1) {
			this.fadeSpeed = float1;
		}

		public IsoGridSquare getSquare() {
			return this.square;
		}

		public void setSquare(IsoGridSquare square) {
			this.square = square;
		}

		public void setPos(int int1, int int2, int int3) {
			this.x = (float)int1;
			this.y = (float)int2;
			this.z = (float)int3;
		}

		public boolean isActive() {
			return this.active;
		}

		public void setActive(boolean boolean1) {
			this.active = boolean1;
		}
	}
}
