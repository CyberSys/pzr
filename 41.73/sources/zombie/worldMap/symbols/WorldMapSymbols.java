package zombie.worldMap.symbols;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.iso.IsoUtils;
import zombie.network.GameServer;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.vehicles.BaseVehicle;
import zombie.worldMap.UIWorldMap;


public class WorldMapSymbols {
	public static final int SAVEFILE_VERSION = 1;
	public final float MIN_VISIBLE_ZOOM = 14.5F;
	public static final float COLLAPSED_RADIUS = 3.0F;
	private final ArrayList m_symbols = new ArrayList();
	private final WorldMapSymbolCollisions m_collision = new WorldMapSymbolCollisions();
	private float m_layoutWorldScale = 0.0F;
	private final Quaternionf m_layoutRotation = new Quaternionf();
	private boolean m_layoutIsometric = true;
	private boolean m_layoutMiniMapSymbols = false;

	public WorldMapTextSymbol addTranslatedText(String string, UIFont uIFont, float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.addText(string, true, uIFont, float1, float2, 0.0F, 0.0F, WorldMapBaseSymbol.DEFAULT_SCALE, float3, float4, float5, float6);
	}

	public WorldMapTextSymbol addUntranslatedText(String string, UIFont uIFont, float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.addText(string, false, uIFont, float1, float2, 0.0F, 0.0F, WorldMapBaseSymbol.DEFAULT_SCALE, float3, float4, float5, float6);
	}

	public WorldMapTextSymbol addText(String string, boolean boolean1, UIFont uIFont, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		WorldMapTextSymbol worldMapTextSymbol = new WorldMapTextSymbol(this);
		worldMapTextSymbol.m_text = string;
		worldMapTextSymbol.m_translated = boolean1;
		worldMapTextSymbol.m_font = uIFont;
		worldMapTextSymbol.m_x = float1;
		worldMapTextSymbol.m_y = float2;
		if (!GameServer.bServer) {
			worldMapTextSymbol.m_width = (float)TextManager.instance.MeasureStringX(uIFont, worldMapTextSymbol.getTranslatedText());
			worldMapTextSymbol.m_height = (float)TextManager.instance.getFontHeight(uIFont);
		}

		worldMapTextSymbol.m_anchorX = PZMath.clamp(float3, 0.0F, 1.0F);
		worldMapTextSymbol.m_anchorY = PZMath.clamp(float4, 0.0F, 1.0F);
		worldMapTextSymbol.m_scale = float5;
		worldMapTextSymbol.m_r = float6;
		worldMapTextSymbol.m_g = float7;
		worldMapTextSymbol.m_b = float8;
		worldMapTextSymbol.m_a = float9;
		this.m_symbols.add(worldMapTextSymbol);
		this.m_layoutWorldScale = 0.0F;
		return worldMapTextSymbol;
	}

	public WorldMapTextureSymbol addTexture(String string, float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.addTexture(string, float1, float2, 0.0F, 0.0F, WorldMapBaseSymbol.DEFAULT_SCALE, float3, float4, float5, float6);
	}

	public WorldMapTextureSymbol addTexture(String string, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		WorldMapTextureSymbol worldMapTextureSymbol = new WorldMapTextureSymbol(this);
		worldMapTextureSymbol.setSymbolID(string);
		MapSymbolDefinitions.MapSymbolDefinition mapSymbolDefinition = MapSymbolDefinitions.getInstance().getSymbolById(string);
		if (mapSymbolDefinition == null) {
			worldMapTextureSymbol.m_width = 18.0F;
			worldMapTextureSymbol.m_height = 18.0F;
		} else {
			worldMapTextureSymbol.m_texture = GameServer.bServer ? null : Texture.getSharedTexture(mapSymbolDefinition.getTexturePath());
			worldMapTextureSymbol.m_width = (float)mapSymbolDefinition.getWidth();
			worldMapTextureSymbol.m_height = (float)mapSymbolDefinition.getHeight();
		}

		if (worldMapTextureSymbol.m_texture == null && !GameServer.bServer) {
			worldMapTextureSymbol.m_texture = Texture.getErrorTexture();
		}

		worldMapTextureSymbol.m_x = float1;
		worldMapTextureSymbol.m_y = float2;
		worldMapTextureSymbol.m_anchorX = PZMath.clamp(float3, 0.0F, 1.0F);
		worldMapTextureSymbol.m_anchorY = PZMath.clamp(float4, 0.0F, 1.0F);
		worldMapTextureSymbol.m_scale = float5;
		worldMapTextureSymbol.m_r = float6;
		worldMapTextureSymbol.m_g = float7;
		worldMapTextureSymbol.m_b = float8;
		worldMapTextureSymbol.m_a = float9;
		this.m_symbols.add(worldMapTextureSymbol);
		this.m_layoutWorldScale = 0.0F;
		return worldMapTextureSymbol;
	}

	public void removeSymbolByIndex(int int1) {
		WorldMapBaseSymbol worldMapBaseSymbol = (WorldMapBaseSymbol)this.m_symbols.remove(int1);
		worldMapBaseSymbol.release();
	}

	public void clear() {
		for (int int1 = 0; int1 < this.m_symbols.size(); ++int1) {
			((WorldMapBaseSymbol)this.m_symbols.get(int1)).release();
		}

		this.m_symbols.clear();
		this.m_layoutWorldScale = 0.0F;
	}

	public void invalidateLayout() {
		this.m_layoutWorldScale = 0.0F;
	}

	public void render(UIWorldMap uIWorldMap) {
		float float1 = uIWorldMap.getAPI().worldOriginX();
		float float2 = uIWorldMap.getAPI().worldOriginY();
		this.checkLayout(uIWorldMap);
		if (Core.bDebug) {
		}

		boolean boolean1 = false;
		for (int int1 = 0; int1 < this.m_symbols.size(); ++int1) {
			WorldMapBaseSymbol worldMapBaseSymbol = (WorldMapBaseSymbol)this.m_symbols.get(int1);
			if (this.isSymbolVisible(uIWorldMap, worldMapBaseSymbol)) {
				float float3 = float1 + worldMapBaseSymbol.m_layoutX;
				float float4 = float2 + worldMapBaseSymbol.m_layoutY;
				if (!(float3 + worldMapBaseSymbol.widthScaled(uIWorldMap) <= 0.0F) && !((double)float3 >= uIWorldMap.getWidth()) && !(float4 + worldMapBaseSymbol.heightScaled(uIWorldMap) <= 0.0F) && !((double)float4 >= uIWorldMap.getHeight())) {
					if (boolean1) {
						uIWorldMap.DrawTextureScaledColor((Texture)null, (double)float3, (double)float4, (double)worldMapBaseSymbol.widthScaled(uIWorldMap), (double)worldMapBaseSymbol.heightScaled(uIWorldMap), 1.0, 1.0, 1.0, 0.3);
					}

					worldMapBaseSymbol.render(uIWorldMap, float1, float2);
				}
			}
		}
	}

	void checkLayout(UIWorldMap uIWorldMap) {
		Quaternionf quaternionf = ((Quaternionf)((BaseVehicle.QuaternionfObjectPool)BaseVehicle.TL_quaternionf_pool.get()).alloc()).setFromUnnormalized((Matrix4fc)uIWorldMap.getAPI().getRenderer().getModelViewMatrix());
		if (this.m_layoutWorldScale == uIWorldMap.getAPI().getWorldScale() && this.m_layoutIsometric == uIWorldMap.getAPI().getBoolean("Isometric") && this.m_layoutMiniMapSymbols == uIWorldMap.getAPI().getBoolean("MiniMapSymbols") && this.m_layoutRotation.equals(quaternionf)) {
			((BaseVehicle.QuaternionfObjectPool)BaseVehicle.TL_quaternionf_pool.get()).release(quaternionf);
		} else {
			this.m_layoutWorldScale = uIWorldMap.getAPI().getWorldScale();
			this.m_layoutIsometric = uIWorldMap.getAPI().getBoolean("Isometric");
			this.m_layoutMiniMapSymbols = uIWorldMap.getAPI().getBoolean("MiniMapSymbols");
			this.m_layoutRotation.set((Quaternionfc)quaternionf);
			((BaseVehicle.QuaternionfObjectPool)BaseVehicle.TL_quaternionf_pool.get()).release(quaternionf);
			float float1 = uIWorldMap.getAPI().worldOriginX();
			float float2 = uIWorldMap.getAPI().worldOriginY();
			this.m_collision.m_boxes.clear();
			boolean boolean1 = false;
			int int1;
			WorldMapBaseSymbol worldMapBaseSymbol;
			for (int1 = 0; int1 < this.m_symbols.size(); ++int1) {
				worldMapBaseSymbol = (WorldMapBaseSymbol)this.m_symbols.get(int1);
				worldMapBaseSymbol.layout(uIWorldMap, this.m_collision, float1, float2);
				boolean1 |= worldMapBaseSymbol.m_collided;
			}

			if (boolean1) {
				for (int1 = 0; int1 < this.m_symbols.size(); ++int1) {
					worldMapBaseSymbol = (WorldMapBaseSymbol)this.m_symbols.get(int1);
					if (!worldMapBaseSymbol.m_collided && this.m_collision.isCollision(int1)) {
						worldMapBaseSymbol.m_collided = true;
					}
				}
			}
		}
	}

	public int getSymbolCount() {
		return this.m_symbols.size();
	}

	public WorldMapBaseSymbol getSymbolByIndex(int int1) {
		return (WorldMapBaseSymbol)this.m_symbols.get(int1);
	}

	boolean isSymbolVisible(UIWorldMap uIWorldMap, WorldMapBaseSymbol worldMapBaseSymbol) {
		return worldMapBaseSymbol.isVisible() && (worldMapBaseSymbol.m_scale <= 0.0F || uIWorldMap.getAPI().getZoomF() >= 14.5F);
	}

	int hitTest(UIWorldMap uIWorldMap, float float1, float float2) {
		float1 -= uIWorldMap.getAPI().worldOriginX();
		float2 -= uIWorldMap.getAPI().worldOriginY();
		this.checkLayout(uIWorldMap);
		float float3 = Float.MAX_VALUE;
		int int1 = -1;
		for (int int2 = 0; int2 < this.m_symbols.size(); ++int2) {
			WorldMapBaseSymbol worldMapBaseSymbol = (WorldMapBaseSymbol)this.m_symbols.get(int2);
			if (this.isSymbolVisible(uIWorldMap, worldMapBaseSymbol)) {
				float float4 = worldMapBaseSymbol.m_layoutX;
				float float5 = worldMapBaseSymbol.m_layoutY;
				float float6 = float4 + worldMapBaseSymbol.widthScaled(uIWorldMap);
				float float7 = float5 + worldMapBaseSymbol.heightScaled(uIWorldMap);
				if (worldMapBaseSymbol.m_collided) {
					float4 += worldMapBaseSymbol.widthScaled(uIWorldMap) / 2.0F - 1.5F;
					float5 += worldMapBaseSymbol.heightScaled(uIWorldMap) / 2.0F - 1.5F;
					float6 = float4 + 6.0F;
					float7 = float5 + 6.0F;
					float float8 = IsoUtils.DistanceToSquared((float4 + float6) / 2.0F, (float5 + float7) / 2.0F, float1, float2);
					if (float8 < float3) {
						float3 = float8;
						int1 = int2;
					}
				}

				if (float1 >= float4 && float1 < float6 && float2 >= float5 && float2 < float7) {
					return int2;
				}
			}
		}

		if (int1 != -1 && float3 < 100.0F) {
			return int1;
		} else {
			return -1;
		}
	}

	public boolean getMiniMapSymbols() {
		return this.m_layoutMiniMapSymbols;
	}

	public float getLayoutWorldScale() {
		return this.m_layoutWorldScale;
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.putShort((short)1);
		byteBuffer.putInt(this.m_symbols.size());
		for (int int1 = 0; int1 < this.m_symbols.size(); ++int1) {
			WorldMapBaseSymbol worldMapBaseSymbol = (WorldMapBaseSymbol)this.m_symbols.get(int1);
			byteBuffer.put((byte)worldMapBaseSymbol.getType().index());
			worldMapBaseSymbol.save(byteBuffer);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		short short1 = byteBuffer.getShort();
		if (short1 >= 1 && short1 <= 1) {
			int int2 = byteBuffer.getInt();
			for (int int3 = 0; int3 < int2; ++int3) {
				byte byte1 = byteBuffer.get();
				if (byte1 == WorldMapSymbols.WorldMapSymbolType.Text.index()) {
					WorldMapTextSymbol worldMapTextSymbol = new WorldMapTextSymbol(this);
					worldMapTextSymbol.load(byteBuffer, int1, short1);
					this.m_symbols.add(worldMapTextSymbol);
				} else {
					if (byte1 != WorldMapSymbols.WorldMapSymbolType.Texture.index()) {
						throw new IOException("unknown map symbol type " + byte1);
					}

					WorldMapTextureSymbol worldMapTextureSymbol = new WorldMapTextureSymbol(this);
					worldMapTextureSymbol.load(byteBuffer, int1, short1);
					this.m_symbols.add(worldMapTextureSymbol);
				}
			}
		} else {
			throw new IOException("unknown map symbols version " + short1);
		}
	}

	public static enum WorldMapSymbolType {

		NONE,
		Text,
		Texture,
		m_type;

		private WorldMapSymbolType(int int1) {
			this.m_type = (byte)int1;
		}
		int index() {
			return this.m_type;
		}
		private static WorldMapSymbols.WorldMapSymbolType[] $values() {
			return new WorldMapSymbols.WorldMapSymbolType[]{NONE, Text, Texture};
		}
	}
}
