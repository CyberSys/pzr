package zombie.worldMap;

import org.joml.Matrix4f;
import zombie.config.ConfigOption;
import zombie.input.Mouse;
import zombie.inventory.types.MapItem;
import zombie.worldMap.markers.WorldMapMarkers;
import zombie.worldMap.markers.WorldMapMarkersV1;
import zombie.worldMap.styles.WorldMapStyle;
import zombie.worldMap.styles.WorldMapStyleV1;
import zombie.worldMap.symbols.WorldMapSymbols;
import zombie.worldMap.symbols.WorldMapSymbolsV1;


public class UIWorldMapV1 {
	final UIWorldMap m_ui;
	protected final WorldMap m_worldMap;
	protected final WorldMapStyle m_style;
	protected final WorldMapRenderer m_renderer;
	protected final WorldMapMarkers m_markers;
	protected WorldMapSymbols m_symbols;
	protected WorldMapMarkersV1 m_markersV1 = null;
	protected WorldMapStyleV1 m_styleV1 = null;
	protected WorldMapSymbolsV1 m_symbolsV1 = null;

	public UIWorldMapV1(UIWorldMap uIWorldMap) {
		this.m_ui = uIWorldMap;
		this.m_worldMap = this.m_ui.m_worldMap;
		this.m_style = this.m_ui.m_style;
		this.m_renderer = this.m_ui.m_renderer;
		this.m_markers = this.m_ui.m_markers;
		this.m_symbols = this.m_ui.m_symbols;
	}

	public void setMapItem(MapItem mapItem) {
		this.m_ui.setMapItem(mapItem);
		this.m_symbols = this.m_ui.m_symbols;
	}

	public WorldMapRenderer getRenderer() {
		return this.m_renderer;
	}

	public WorldMapMarkers getMarkers() {
		return this.m_markers;
	}

	public WorldMapStyle getStyle() {
		return this.m_style;
	}

	public WorldMapMarkersV1 getMarkersAPI() {
		if (this.m_markersV1 == null) {
			this.m_markersV1 = new WorldMapMarkersV1(this.m_ui);
		}

		return this.m_markersV1;
	}

	public WorldMapStyleV1 getStyleAPI() {
		if (this.m_styleV1 == null) {
			this.m_styleV1 = new WorldMapStyleV1(this.m_ui);
		}

		return this.m_styleV1;
	}

	public WorldMapSymbolsV1 getSymbolsAPI() {
		if (this.m_symbolsV1 == null) {
			this.m_symbolsV1 = new WorldMapSymbolsV1(this.m_ui, this.m_symbols);
		}

		return this.m_symbolsV1;
	}

	public void addData(String string) {
		boolean boolean1 = this.m_worldMap.hasData();
		this.m_worldMap.addData(string);
		if (!boolean1) {
			this.m_renderer.setMap(this.m_worldMap, this.m_ui.getAbsoluteX().intValue(), this.m_ui.getAbsoluteY().intValue(), this.m_ui.getWidth().intValue(), this.m_ui.getHeight().intValue());
			this.resetView();
		}
	}

	public int getDataCount() {
		return this.m_worldMap.getDataCount();
	}

	public String getDataFileByIndex(int int1) {
		WorldMapData worldMapData = this.m_worldMap.getDataByIndex(int1);
		return worldMapData.m_relativeFileName;
	}

	public void clearData() {
		this.m_worldMap.clearData();
	}

	public void endDirectoryData() {
		this.m_worldMap.endDirectoryData();
	}

	public void addImages(String string) {
		boolean boolean1 = this.m_worldMap.hasImages();
		this.m_worldMap.addImages(string);
		if (!boolean1) {
			this.m_renderer.setMap(this.m_worldMap, this.m_ui.getAbsoluteX().intValue(), this.m_ui.getAbsoluteY().intValue(), this.m_ui.getWidth().intValue(), this.m_ui.getHeight().intValue());
			this.resetView();
		}
	}

	public int getImagesCount() {
		return this.m_worldMap.getImagesCount();
	}

	public void setBoundsInCells(int int1, int int2, int int3, int int4) {
		boolean boolean1 = int1 * 300 != this.m_worldMap.m_minX || int2 * 300 != this.m_worldMap.m_minY || int3 * 300 + 299 != this.m_worldMap.m_maxX || int4 + 300 + 299 != this.m_worldMap.m_maxY;
		this.m_worldMap.setBoundsInCells(int1, int2, int3, int4);
		if (boolean1 && this.m_worldMap.hasData()) {
			this.resetView();
		}
	}

	public void setBoundsInSquares(int int1, int int2, int int3, int int4) {
		boolean boolean1 = int1 != this.m_worldMap.m_minX || int2 != this.m_worldMap.m_minY || int3 != this.m_worldMap.m_maxX || int4 != this.m_worldMap.m_maxY;
		this.m_worldMap.setBoundsInSquares(int1, int2, int3, int4);
		if (boolean1 && this.m_worldMap.hasData()) {
			this.resetView();
		}
	}

	public void setBoundsFromWorld() {
		this.m_worldMap.setBoundsFromWorld();
	}

	public void setBoundsFromData() {
		this.m_worldMap.setBoundsFromData();
	}

	public int getMinXInCells() {
		return this.m_worldMap.getMinXInCells();
	}

	public int getMinYInCells() {
		return this.m_worldMap.getMinYInCells();
	}

	public int getMaxXInCells() {
		return this.m_worldMap.getMaxXInCells();
	}

	public int getMaxYInCells() {
		return this.m_worldMap.getMaxYInCells();
	}

	public int getWidthInCells() {
		return this.m_worldMap.getWidthInCells();
	}

	public int getHeightInCells() {
		return this.m_worldMap.getHeightInCells();
	}

	public int getMinXInSquares() {
		return this.m_worldMap.getMinXInSquares();
	}

	public int getMinYInSquares() {
		return this.m_worldMap.getMinYInSquares();
	}

	public int getMaxXInSquares() {
		return this.m_worldMap.getMaxXInSquares();
	}

	public int getMaxYInSquares() {
		return this.m_worldMap.getMaxYInSquares();
	}

	public int getWidthInSquares() {
		return this.m_worldMap.getWidthInSquares();
	}

	public int getHeightInSquares() {
		return this.m_worldMap.getHeightInSquares();
	}

	public float uiToWorldX(float float1, float float2, float float3, float float4, float float5) {
		return this.m_renderer.uiToWorldX(float1, float2, float3, float4, float5, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
	}

	public float uiToWorldY(float float1, float float2, float float3, float float4, float float5) {
		return this.m_renderer.uiToWorldY(float1, float2, float3, float4, float5, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
	}

	protected float worldToUIX(float float1, float float2, float float3, float float4, float float5, Matrix4f matrix4f, Matrix4f matrix4f2) {
		return this.m_renderer.worldToUIX(float1, float2, float3, float4, float5, matrix4f, matrix4f2);
	}

	protected float worldToUIY(float float1, float float2, float float3, float float4, float float5, Matrix4f matrix4f, Matrix4f matrix4f2) {
		return this.m_renderer.worldToUIY(float1, float2, float3, float4, float5, matrix4f, matrix4f2);
	}

	protected float worldOriginUIX(float float1, float float2) {
		return this.m_renderer.worldOriginUIX(float1, float2);
	}

	protected float worldOriginUIY(float float1, float float2) {
		return this.m_renderer.worldOriginUIY(float1, float2);
	}

	protected float zoomMult() {
		return this.m_renderer.zoomMult();
	}

	protected float getWorldScale(float float1) {
		return this.m_renderer.getWorldScale(float1);
	}

	public float worldOriginX() {
		return this.m_renderer.worldOriginUIX(this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldX());
	}

	public float worldOriginY() {
		return this.m_renderer.worldOriginUIY(this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldY());
	}

	public float getBaseZoom() {
		return this.m_renderer.getBaseZoom();
	}

	public float getZoomF() {
		return this.m_renderer.getDisplayZoomF();
	}

	public float getWorldScale() {
		return this.m_renderer.getWorldScale(this.m_renderer.getDisplayZoomF());
	}

	public float getCenterWorldX() {
		return this.m_renderer.getCenterWorldX();
	}

	public float getCenterWorldY() {
		return this.m_renderer.getCenterWorldY();
	}

	public float uiToWorldX(float float1, float float2) {
		return !this.m_worldMap.hasData() && !this.m_worldMap.hasImages() ? 0.0F : this.uiToWorldX(float1, float2, this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldX(), this.m_renderer.getCenterWorldY());
	}

	public float uiToWorldY(float float1, float float2) {
		return !this.m_worldMap.hasData() && !this.m_worldMap.hasImages() ? 0.0F : this.uiToWorldY(float1, float2, this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldY(), this.m_renderer.getCenterWorldY());
	}

	public float worldToUIX(float float1, float float2) {
		return !this.m_worldMap.hasData() && !this.m_worldMap.hasImages() ? 0.0F : this.worldToUIX(float1, float2, this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldX(), this.m_renderer.getCenterWorldY(), this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
	}

	public float worldToUIY(float float1, float float2) {
		return !this.m_worldMap.hasData() && !this.m_worldMap.hasImages() ? 0.0F : this.worldToUIY(float1, float2, this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldX(), this.m_renderer.getCenterWorldY(), this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
	}

	public void centerOn(float float1, float float2) {
		if (this.m_worldMap.hasData() || this.m_worldMap.hasImages()) {
			this.m_renderer.centerOn(float1, float2);
		}
	}

	public void moveView(float float1, float float2) {
		if (this.m_worldMap.hasData() || this.m_worldMap.hasImages()) {
			this.m_renderer.moveView((int)float1, (int)float2);
		}
	}

	public void zoomAt(float float1, float float2, float float3) {
		if (this.m_worldMap.hasData() || this.m_worldMap.hasImages()) {
			this.m_renderer.zoomAt((int)float1, (int)float2, -((int)float3));
		}
	}

	public void setZoom(float float1) {
		this.m_renderer.setZoom(float1);
	}

	public void resetView() {
		if (this.m_worldMap.hasData() || this.m_worldMap.hasImages()) {
			this.m_renderer.resetView();
		}
	}

	public float mouseToWorldX() {
		float float1 = (float)(Mouse.getXA() - this.m_ui.getAbsoluteX().intValue());
		float float2 = (float)(Mouse.getYA() - this.m_ui.getAbsoluteY().intValue());
		return this.uiToWorldX(float1, float2);
	}

	public float mouseToWorldY() {
		float float1 = (float)(Mouse.getXA() - this.m_ui.getAbsoluteX().intValue());
		float float2 = (float)(Mouse.getYA() - this.m_ui.getAbsoluteY().intValue());
		return this.uiToWorldY(float1, float2);
	}

	public void setBackgroundRGBA(float float1, float float2, float float3, float float4) {
		this.m_ui.m_color.init(float1, float2, float3, float4);
	}

	public void setDropShadowWidth(int int1) {
		this.m_ui.m_renderer.setDropShadowWidth(int1);
	}

	public void setUnvisitedRGBA(float float1, float float2, float float3, float float4) {
		WorldMapVisited.getInstance().setUnvisitedRGBA(float1, float2, float3, float4);
	}

	public void setUnvisitedGridRGBA(float float1, float float2, float float3, float float4) {
		WorldMapVisited.getInstance().setUnvisitedGridRGBA(float1, float2, float3, float4);
	}

	public int getOptionCount() {
		return this.m_renderer.getOptionCount();
	}

	public ConfigOption getOptionByIndex(int int1) {
		return this.m_renderer.getOptionByIndex(int1);
	}

	public void setBoolean(String string, boolean boolean1) {
		this.m_renderer.setBoolean(string, boolean1);
	}

	public boolean getBoolean(String string) {
		return this.m_renderer.getBoolean(string);
	}

	public void setDouble(String string, double double1) {
		this.m_renderer.setDouble(string, double1);
	}

	public double getDouble(String string, double double1) {
		return this.m_renderer.getDouble(string, double1);
	}
}
