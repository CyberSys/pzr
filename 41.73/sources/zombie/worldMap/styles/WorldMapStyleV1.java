package zombie.worldMap.styles;

import java.util.ArrayList;
import java.util.Objects;
import zombie.Lua.LuaManager;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.worldMap.UIWorldMap;
import zombie.worldMap.UIWorldMapV1;


public class WorldMapStyleV1 {
	public UIWorldMap m_ui;
	public UIWorldMapV1 m_api;
	public WorldMapStyle m_style;
	public final ArrayList m_layers = new ArrayList();

	public WorldMapStyleV1(UIWorldMap uIWorldMap) {
		Objects.requireNonNull(uIWorldMap);
		this.m_ui = uIWorldMap;
		this.m_api = uIWorldMap.getAPIv1();
		this.m_style = this.m_api.getStyle();
	}

	public WorldMapStyleV1.WorldMapStyleLayerV1 newLineLayer(String string) throws IllegalArgumentException {
		WorldMapStyleV1.WorldMapLineStyleLayerV1 worldMapLineStyleLayerV1 = new WorldMapStyleV1.WorldMapLineStyleLayerV1(this, string);
		this.m_layers.add(worldMapLineStyleLayerV1);
		return worldMapLineStyleLayerV1;
	}

	public WorldMapStyleV1.WorldMapStyleLayerV1 newPolygonLayer(String string) throws IllegalArgumentException {
		WorldMapStyleV1.WorldMapPolygonStyleLayerV1 worldMapPolygonStyleLayerV1 = new WorldMapStyleV1.WorldMapPolygonStyleLayerV1(this, string);
		this.m_layers.add(worldMapPolygonStyleLayerV1);
		return worldMapPolygonStyleLayerV1;
	}

	public WorldMapStyleV1.WorldMapStyleLayerV1 newTextureLayer(String string) throws IllegalArgumentException {
		WorldMapStyleV1.WorldMapTextureStyleLayerV1 worldMapTextureStyleLayerV1 = new WorldMapStyleV1.WorldMapTextureStyleLayerV1(this, string);
		this.m_layers.add(worldMapTextureStyleLayerV1);
		return worldMapTextureStyleLayerV1;
	}

	public int getLayerCount() {
		return this.m_layers.size();
	}

	public WorldMapStyleV1.WorldMapStyleLayerV1 getLayerByIndex(int int1) {
		return (WorldMapStyleV1.WorldMapStyleLayerV1)this.m_layers.get(int1);
	}

	public WorldMapStyleV1.WorldMapStyleLayerV1 getLayerByName(String string) {
		int int1 = this.indexOfLayer(string);
		return int1 == -1 ? null : (WorldMapStyleV1.WorldMapStyleLayerV1)this.m_layers.get(int1);
	}

	public int indexOfLayer(String string) {
		for (int int1 = 0; int1 < this.m_layers.size(); ++int1) {
			WorldMapStyleV1.WorldMapStyleLayerV1 worldMapStyleLayerV1 = (WorldMapStyleV1.WorldMapStyleLayerV1)this.m_layers.get(int1);
			if (worldMapStyleLayerV1.m_layer.m_id.equals(string)) {
				return int1;
			}
		}

		return -1;
	}

	public void moveLayer(int int1, int int2) {
		WorldMapStyleLayer worldMapStyleLayer = (WorldMapStyleLayer)this.m_style.m_layers.remove(int1);
		this.m_style.m_layers.add(int2, worldMapStyleLayer);
		WorldMapStyleV1.WorldMapStyleLayerV1 worldMapStyleLayerV1 = (WorldMapStyleV1.WorldMapStyleLayerV1)this.m_layers.remove(int1);
		this.m_layers.add(int2, worldMapStyleLayerV1);
	}

	public void removeLayerById(String string) {
		int int1 = this.indexOfLayer(string);
		if (int1 != -1) {
			this.removeLayerByIndex(int1);
		}
	}

	public void removeLayerByIndex(int int1) {
		this.m_style.m_layers.remove(int1);
		this.m_layers.remove(int1);
	}

	public void clear() {
		this.m_style.m_layers.clear();
		this.m_layers.clear();
	}

	public static void setExposed(LuaManager.Exposer exposer) {
		exposer.setExposed(WorldMapStyleV1.class);
		exposer.setExposed(WorldMapStyleV1.WorldMapStyleLayerV1.class);
		exposer.setExposed(WorldMapStyleV1.WorldMapLineStyleLayerV1.class);
		exposer.setExposed(WorldMapStyleV1.WorldMapPolygonStyleLayerV1.class);
		exposer.setExposed(WorldMapStyleV1.WorldMapTextureStyleLayerV1.class);
	}

	public static class WorldMapLineStyleLayerV1 extends WorldMapStyleV1.WorldMapStyleLayerV1 {
		WorldMapLineStyleLayer m_lineStyle;

		WorldMapLineStyleLayerV1(WorldMapStyleV1 worldMapStyleV1, String string) {
			super(worldMapStyleV1, new WorldMapLineStyleLayer(string));
			this.m_lineStyle = (WorldMapLineStyleLayer)this.m_layer;
		}

		public void setFilter(String string, String string2) {
			this.m_lineStyle.m_filterKey = string;
			this.m_lineStyle.m_filterValue = string2;
			this.m_lineStyle.m_filter = (string2x,var3)->{
				return string2x.hasLineString() && string2.equals(string2x.m_properties.get(string));
			};
		}

		public void addFill(float float1, int int1, int int2, int int3, int int4) {
			this.m_lineStyle.m_fill.add(new WorldMapStyleLayer.ColorStop(float1, int1, int2, int3, int4));
		}

		public void addLineWidth(float float1, float float2) {
			this.m_lineStyle.m_lineWidth.add(new WorldMapStyleLayer.FloatStop(float1, float2));
		}
	}

	public static class WorldMapPolygonStyleLayerV1 extends WorldMapStyleV1.WorldMapStyleLayerV1 {
		WorldMapPolygonStyleLayer m_polygonStyle;

		WorldMapPolygonStyleLayerV1(WorldMapStyleV1 worldMapStyleV1, String string) {
			super(worldMapStyleV1, new WorldMapPolygonStyleLayer(string));
			this.m_polygonStyle = (WorldMapPolygonStyleLayer)this.m_layer;
		}

		public void setFilter(String string, String string2) {
			this.m_polygonStyle.m_filterKey = string;
			this.m_polygonStyle.m_filterValue = string2;
			this.m_polygonStyle.m_filter = (string2x,var3)->{
				return string2x.hasPolygon() && string2.equals(string2x.m_properties.get(string));
			};
		}

		public String getFilterKey() {
			return this.m_polygonStyle.m_filterKey;
		}

		public String getFilterValue() {
			return this.m_polygonStyle.m_filterValue;
		}

		public void addFill(float float1, int int1, int int2, int int3, int int4) {
			this.m_polygonStyle.m_fill.add(new WorldMapStyleLayer.ColorStop(float1, int1, int2, int3, int4));
		}

		public void addScale(float float1, float float2) {
			this.m_polygonStyle.m_scale.add(new WorldMapStyleLayer.FloatStop(float1, float2));
		}

		public void addTexture(float float1, String string) {
			this.m_polygonStyle.m_texture.add(new WorldMapStyleLayer.TextureStop(float1, string));
		}

		public void removeFill(int int1) {
			this.m_polygonStyle.m_fill.remove(int1);
		}

		public void removeTexture(int int1) {
			this.m_polygonStyle.m_texture.remove(int1);
		}

		public void moveFill(int int1, int int2) {
			WorldMapStyleLayer.ColorStop colorStop = (WorldMapStyleLayer.ColorStop)this.m_polygonStyle.m_fill.remove(int1);
			this.m_polygonStyle.m_fill.add(int2, colorStop);
		}

		public void moveTexture(int int1, int int2) {
			WorldMapStyleLayer.TextureStop textureStop = (WorldMapStyleLayer.TextureStop)this.m_polygonStyle.m_texture.remove(int1);
			this.m_polygonStyle.m_texture.add(int2, textureStop);
		}

		public int getFillStops() {
			return this.m_polygonStyle.m_fill.size();
		}

		public void setFillRGBA(int int1, int int2, int int3, int int4, int int5) {
			((WorldMapStyleLayer.ColorStop)this.m_polygonStyle.m_fill.get(int1)).r = int2;
			((WorldMapStyleLayer.ColorStop)this.m_polygonStyle.m_fill.get(int1)).g = int3;
			((WorldMapStyleLayer.ColorStop)this.m_polygonStyle.m_fill.get(int1)).b = int4;
			((WorldMapStyleLayer.ColorStop)this.m_polygonStyle.m_fill.get(int1)).a = int5;
		}

		public void setFillZoom(int int1, float float1) {
			((WorldMapStyleLayer.ColorStop)this.m_polygonStyle.m_fill.get(int1)).m_zoom = PZMath.clamp(float1, 0.0F, 24.0F);
		}

		public float getFillZoom(int int1) {
			return ((WorldMapStyleLayer.ColorStop)this.m_polygonStyle.m_fill.get(int1)).m_zoom;
		}

		public int getFillRed(int int1) {
			return ((WorldMapStyleLayer.ColorStop)this.m_polygonStyle.m_fill.get(int1)).r;
		}

		public int getFillGreen(int int1) {
			return ((WorldMapStyleLayer.ColorStop)this.m_polygonStyle.m_fill.get(int1)).g;
		}

		public int getFillBlue(int int1) {
			return ((WorldMapStyleLayer.ColorStop)this.m_polygonStyle.m_fill.get(int1)).b;
		}

		public int getFillAlpha(int int1) {
			return ((WorldMapStyleLayer.ColorStop)this.m_polygonStyle.m_fill.get(int1)).a;
		}

		public int getTextureStops() {
			return this.m_polygonStyle.m_texture.size();
		}

		public void setTextureZoom(int int1, float float1) {
			((WorldMapStyleLayer.TextureStop)this.m_polygonStyle.m_texture.get(int1)).m_zoom = PZMath.clamp(float1, 0.0F, 24.0F);
		}

		public float getTextureZoom(int int1) {
			return ((WorldMapStyleLayer.TextureStop)this.m_polygonStyle.m_texture.get(int1)).m_zoom;
		}

		public void setTexturePath(int int1, String string) {
			((WorldMapStyleLayer.TextureStop)this.m_polygonStyle.m_texture.get(int1)).texturePath = string;
			((WorldMapStyleLayer.TextureStop)this.m_polygonStyle.m_texture.get(int1)).texture = Texture.getTexture(string);
		}

		public String getTexturePath(int int1) {
			return ((WorldMapStyleLayer.TextureStop)this.m_polygonStyle.m_texture.get(int1)).texturePath;
		}

		public Texture getTexture(int int1) {
			return ((WorldMapStyleLayer.TextureStop)this.m_polygonStyle.m_texture.get(int1)).texture;
		}
	}

	public static class WorldMapTextureStyleLayerV1 extends WorldMapStyleV1.WorldMapStyleLayerV1 {
		WorldMapTextureStyleLayer m_textureStyle;

		WorldMapTextureStyleLayerV1(WorldMapStyleV1 worldMapStyleV1, String string) {
			super(worldMapStyleV1, new WorldMapTextureStyleLayer(string));
			this.m_textureStyle = (WorldMapTextureStyleLayer)this.m_layer;
		}

		public void addFill(float float1, int int1, int int2, int int3, int int4) {
			this.m_textureStyle.m_fill.add(new WorldMapStyleLayer.ColorStop(float1, int1, int2, int3, int4));
		}

		public void addTexture(float float1, String string) {
			this.m_textureStyle.m_texture.add(new WorldMapStyleLayer.TextureStop(float1, string));
		}

		public void removeFill(int int1) {
			this.m_textureStyle.m_fill.remove(int1);
		}

		public void removeAllFill() {
			this.m_textureStyle.m_fill.clear();
		}

		public void removeTexture(int int1) {
			this.m_textureStyle.m_texture.remove(int1);
		}

		public void removeAllTexture() {
			this.m_textureStyle.m_texture.clear();
		}

		public void moveFill(int int1, int int2) {
			WorldMapStyleLayer.ColorStop colorStop = (WorldMapStyleLayer.ColorStop)this.m_textureStyle.m_fill.remove(int1);
			this.m_textureStyle.m_fill.add(int2, colorStop);
		}

		public void moveTexture(int int1, int int2) {
			WorldMapStyleLayer.TextureStop textureStop = (WorldMapStyleLayer.TextureStop)this.m_textureStyle.m_texture.remove(int1);
			this.m_textureStyle.m_texture.add(int2, textureStop);
		}

		public void setBoundsInSquares(int int1, int int2, int int3, int int4) {
			this.m_textureStyle.m_worldX1 = int1;
			this.m_textureStyle.m_worldY1 = int2;
			this.m_textureStyle.m_worldX2 = int3;
			this.m_textureStyle.m_worldY2 = int4;
		}

		public int getMinXInSquares() {
			return this.m_textureStyle.m_worldX1;
		}

		public int getMinYInSquares() {
			return this.m_textureStyle.m_worldY1;
		}

		public int getMaxXInSquares() {
			return this.m_textureStyle.m_worldX2;
		}

		public int getMaxYInSquares() {
			return this.m_textureStyle.m_worldY2;
		}

		public int getWidthInSquares() {
			return this.m_textureStyle.m_worldX2 - this.m_textureStyle.m_worldX1;
		}

		public int getHeightInSquares() {
			return this.m_textureStyle.m_worldY2 - this.m_textureStyle.m_worldY1;
		}

		public void setTile(boolean boolean1) {
			this.m_textureStyle.m_tile = boolean1;
		}

		public boolean isTile() {
			return this.m_textureStyle.m_tile;
		}

		public void setUseWorldBounds(boolean boolean1) {
			this.m_textureStyle.m_useWorldBounds = boolean1;
		}

		public boolean isUseWorldBounds() {
			return this.m_textureStyle.m_useWorldBounds;
		}

		public int getFillStops() {
			return this.m_textureStyle.m_fill.size();
		}

		public void setFillRGBA(int int1, int int2, int int3, int int4, int int5) {
			((WorldMapStyleLayer.ColorStop)this.m_textureStyle.m_fill.get(int1)).r = int2;
			((WorldMapStyleLayer.ColorStop)this.m_textureStyle.m_fill.get(int1)).g = int3;
			((WorldMapStyleLayer.ColorStop)this.m_textureStyle.m_fill.get(int1)).b = int4;
			((WorldMapStyleLayer.ColorStop)this.m_textureStyle.m_fill.get(int1)).a = int5;
		}

		public void setFillZoom(int int1, float float1) {
			((WorldMapStyleLayer.ColorStop)this.m_textureStyle.m_fill.get(int1)).m_zoom = PZMath.clamp(float1, 0.0F, 24.0F);
		}

		public float getFillZoom(int int1) {
			return ((WorldMapStyleLayer.ColorStop)this.m_textureStyle.m_fill.get(int1)).m_zoom;
		}

		public int getFillRed(int int1) {
			return ((WorldMapStyleLayer.ColorStop)this.m_textureStyle.m_fill.get(int1)).r;
		}

		public int getFillGreen(int int1) {
			return ((WorldMapStyleLayer.ColorStop)this.m_textureStyle.m_fill.get(int1)).g;
		}

		public int getFillBlue(int int1) {
			return ((WorldMapStyleLayer.ColorStop)this.m_textureStyle.m_fill.get(int1)).b;
		}

		public int getFillAlpha(int int1) {
			return ((WorldMapStyleLayer.ColorStop)this.m_textureStyle.m_fill.get(int1)).a;
		}

		public int getTextureStops() {
			return this.m_textureStyle.m_texture.size();
		}

		public void setTextureZoom(int int1, float float1) {
			((WorldMapStyleLayer.TextureStop)this.m_textureStyle.m_texture.get(int1)).m_zoom = PZMath.clamp(float1, 0.0F, 24.0F);
		}

		public float getTextureZoom(int int1) {
			return ((WorldMapStyleLayer.TextureStop)this.m_textureStyle.m_texture.get(int1)).m_zoom;
		}

		public void setTexturePath(int int1, String string) {
			((WorldMapStyleLayer.TextureStop)this.m_textureStyle.m_texture.get(int1)).texturePath = string;
			((WorldMapStyleLayer.TextureStop)this.m_textureStyle.m_texture.get(int1)).texture = Texture.getTexture(string);
		}

		public String getTexturePath(int int1) {
			return ((WorldMapStyleLayer.TextureStop)this.m_textureStyle.m_texture.get(int1)).texturePath;
		}

		public Texture getTexture(int int1) {
			return ((WorldMapStyleLayer.TextureStop)this.m_textureStyle.m_texture.get(int1)).texture;
		}
	}

	public static class WorldMapStyleLayerV1 {
		WorldMapStyleV1 m_owner;
		WorldMapStyleLayer m_layer;

		WorldMapStyleLayerV1(WorldMapStyleV1 worldMapStyleV1, WorldMapStyleLayer worldMapStyleLayer) {
			this.m_owner = worldMapStyleV1;
			this.m_layer = worldMapStyleLayer;
			worldMapStyleV1.m_style.m_layers.add(this.m_layer);
		}

		public String getTypeString() {
			return this.m_layer.getTypeString();
		}

		public void setId(String string) {
			this.m_layer.m_id = string;
		}

		public String getId() {
			return this.m_layer.m_id;
		}

		public void setMinZoom(float float1) {
			this.m_layer.m_minZoom = float1;
		}

		public float getMinZoom() {
			return this.m_layer.m_minZoom;
		}
	}
}
