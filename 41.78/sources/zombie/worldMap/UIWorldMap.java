package zombie.worldMap;

import java.util.ArrayList;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.characters.Faction;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.inventory.types.MapItem;
import zombie.iso.BuildingDef;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.areas.SafeHouse;
import zombie.network.GameClient;
import zombie.network.ServerOptions;
import zombie.ui.TextManager;
import zombie.ui.UIElement;
import zombie.ui.UIFont;
import zombie.util.StringUtils;
import zombie.worldMap.editor.WorldMapEditorState;
import zombie.worldMap.markers.WorldMapGridSquareMarker;
import zombie.worldMap.markers.WorldMapMarkers;
import zombie.worldMap.markers.WorldMapMarkersV1;
import zombie.worldMap.styles.WorldMapStyle;
import zombie.worldMap.styles.WorldMapStyleLayer;
import zombie.worldMap.styles.WorldMapStyleV1;
import zombie.worldMap.symbols.MapSymbolDefinitions;
import zombie.worldMap.symbols.WorldMapSymbols;
import zombie.worldMap.symbols.WorldMapSymbolsV1;


public class UIWorldMap extends UIElement {
	static final ArrayList s_tempFeatures = new ArrayList();
	protected final WorldMap m_worldMap = new WorldMap();
	protected final WorldMapStyle m_style = new WorldMapStyle();
	protected final WorldMapRenderer m_renderer = new WorldMapRenderer();
	protected final WorldMapMarkers m_markers = new WorldMapMarkers();
	protected WorldMapSymbols m_symbols = null;
	protected final WorldMapStyleLayer.RGBAf m_color = (new WorldMapStyleLayer.RGBAf()).init(0.85882354F, 0.84313726F, 0.7529412F, 1.0F);
	protected final UIWorldMapV1 m_APIv1 = new UIWorldMapV1(this);
	private boolean m_dataWasReady = false;
	private final ArrayList m_buildingsWithoutFeatures = new ArrayList();
	private boolean m_bBuildingsWithoutFeatures = false;

	public UIWorldMap(KahluaTable kahluaTable) {
		super(kahluaTable);
	}

	public UIWorldMapV1 getAPI() {
		return this.m_APIv1;
	}

	public UIWorldMapV1 getAPIv1() {
		return this.m_APIv1;
	}

	protected void setMapItem(MapItem mapItem) {
		this.m_symbols = mapItem.getSymbols();
	}

	public void render() {
		if (this.isVisible()) {
			if (this.Parent == null || this.Parent.getMaxDrawHeight() == -1.0 || !(this.Parent.getMaxDrawHeight() <= this.getY())) {
				this.DrawTextureScaledColor((Texture)null, 0.0, 0.0, this.getWidth(), this.getHeight(), (double)this.m_color.r, (double)this.m_color.g, (double)this.m_color.b, (double)this.m_color.a);
				if (!this.m_worldMap.hasData()) {
				}

				this.setStencilRect(0.0, 0.0, this.getWidth(), this.getHeight());
				this.m_renderer.setMap(this.m_worldMap, this.getAbsoluteX().intValue(), this.getAbsoluteY().intValue(), this.getWidth().intValue(), this.getHeight().intValue());
				this.m_renderer.updateView();
				float float1 = this.m_renderer.getDisplayZoomF();
				float float2 = this.m_renderer.getCenterWorldX();
				float float3 = this.m_renderer.getCenterWorldY();
				this.m_APIv1.getWorldScale(float1);
				if (this.m_renderer.getBoolean("HideUnvisited") && WorldMapVisited.getInstance() != null) {
					this.m_renderer.setVisited(WorldMapVisited.getInstance());
				} else {
					this.m_renderer.setVisited((WorldMapVisited)null);
				}

				this.m_renderer.render(this);
				if (this.m_renderer.getBoolean("Symbols")) {
					this.m_symbols.render(this);
				}

				this.m_markers.render(this);
				this.renderLocalPlayers();
				this.renderRemotePlayers();
				int int1 = TextManager.instance.getFontHeight(UIFont.Small);
				float float4;
				float float5;
				double double1;
				int int2;
				if (Core.bDebug && this.m_renderer.getBoolean("DebugInfo")) {
					this.DrawTextureScaledColor((Texture)null, 0.0, 0.0, 200.0, (double)int1 * 4.0, 1.0, 1.0, 1.0, 1.0);
					float4 = this.m_APIv1.mouseToWorldX();
					float5 = this.m_APIv1.mouseToWorldY();
					double1 = 0.0;
					double double2 = 0.0;
					double double3 = 0.0;
					double double4 = 1.0;
					byte byte1 = 0;
					this.DrawText("SQUARE = " + (int)float4 + "," + (int)float5, 0.0, (double)byte1, double1, double2, double3, double4);
					int2 = byte1 + int1;
					this.DrawText("CELL = " + (int)(float4 / 300.0F) + "," + (int)(float5 / 300.0F), 0.0, (double)int1, double1, double2, double3, double4);
					int2 += int1;
					this.DrawText("ZOOM = " + this.m_renderer.getDisplayZoomF(), 0.0, (double)int2, double1, double2, double3, double4);
					int2 += int1;
					WorldMapRenderer worldMapRenderer = this.m_renderer;
					this.DrawText("SCALE = " + worldMapRenderer.getWorldScale(this.m_renderer.getZoomF()), 0.0, (double)int2, double1, double2, double3, double4);
					int int3 = int2 + int1;
				}

				this.clearStencilRect();
				this.repaintStencilRect(0.0, 0.0, (double)this.width, (double)this.height);
				if (Core.bDebug && DebugOptions.instance.UIRenderOutline.getValue()) {
					Double Double1 = -this.getXScroll();
					Double Double2 = -this.getYScroll();
					double1 = this.isMouseOver() ? 0.0 : 1.0;
					this.DrawTextureScaledColor((Texture)null, Double1, Double2, 1.0, (double)this.height, double1, 1.0, 1.0, 0.5);
					this.DrawTextureScaledColor((Texture)null, Double1 + 1.0, Double2, (double)this.width - 2.0, 1.0, double1, 1.0, 1.0, 0.5);
					this.DrawTextureScaledColor((Texture)null, Double1 + (double)this.width - 1.0, Double2, 1.0, (double)this.height, double1, 1.0, 1.0, 0.5);
					this.DrawTextureScaledColor((Texture)null, Double1 + 1.0, Double2 + (double)this.height - 1.0, (double)this.width - 2.0, 1.0, double1, 1.0, 1.0, 0.5);
				}

				if (Core.bDebug && this.m_renderer.getBoolean("HitTest")) {
					float4 = this.m_APIv1.mouseToWorldX();
					float5 = this.m_APIv1.mouseToWorldY();
					s_tempFeatures.clear();
					Iterator iterator = this.m_worldMap.m_data.iterator();
					while (iterator.hasNext()) {
						WorldMapData worldMapData = (WorldMapData)iterator.next();
						if (worldMapData.isReady()) {
							worldMapData.hitTest(float4, float5, s_tempFeatures);
						}
					}

					if (!s_tempFeatures.isEmpty()) {
						WorldMapFeature worldMapFeature = (WorldMapFeature)s_tempFeatures.get(s_tempFeatures.size() - 1);
						int int4 = worldMapFeature.m_cell.m_x * 300;
						int int5 = worldMapFeature.m_cell.m_y * 300;
						int int6 = this.getAbsoluteX().intValue();
						int int7 = this.getAbsoluteY().intValue();
						WorldMapPoints worldMapPoints = (WorldMapPoints)((WorldMapGeometry)worldMapFeature.m_geometries.get(0)).m_points.get(0);
						for (int int8 = 0; int8 < worldMapPoints.numPoints(); ++int8) {
							int int9 = worldMapPoints.getX(int8);
							int2 = worldMapPoints.getY(int8);
							int int10 = worldMapPoints.getX((int8 + 1) % worldMapPoints.numPoints());
							int int11 = worldMapPoints.getY((int8 + 1) % worldMapPoints.numPoints());
							float float6 = this.m_APIv1.worldToUIX((float)(int4 + int9), (float)(int5 + int2));
							float float7 = this.m_APIv1.worldToUIY((float)(int4 + int9), (float)(int5 + int2));
							float float8 = this.m_APIv1.worldToUIX((float)(int4 + int10), (float)(int5 + int11));
							float float9 = this.m_APIv1.worldToUIY((float)(int4 + int10), (float)(int5 + int11));
							SpriteRenderer.instance.renderline((Texture)null, int6 + (int)float6, int7 + (int)float7, int6 + (int)float8, int7 + (int)float9, 1.0F, 0.0F, 0.0F, 1.0F);
						}
					}
				}

				if (Core.bDebug && this.m_renderer.getBoolean("BuildingsWithoutFeatures") && !this.m_renderer.getBoolean("Isometric")) {
					this.renderBuildingsWithoutFeatures();
				}

				super.render();
			}
		}
	}

	private void renderLocalPlayers() {
		if (this.m_renderer.getBoolean("Players")) {
			float float1 = this.m_renderer.getDisplayZoomF();
			if (!(float1 >= 20.0F)) {
				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					IsoPlayer player = IsoPlayer.players[int1];
					if (player != null && !player.isDead()) {
						float float2 = player.x;
						float float3 = player.y;
						if (player.getVehicle() != null) {
							float2 = player.getVehicle().getX();
							float3 = player.getVehicle().getY();
						}

						this.renderPlayer(float2, float3);
						if (GameClient.bClient) {
							this.renderPlayerName(float2, float3, player.getUsername());
						}
					}
				}
			}
		}
	}

	private void renderRemotePlayers() {
		if (GameClient.bClient) {
			if (this.m_renderer.getBoolean("Players")) {
				if (this.m_renderer.getBoolean("RemotePlayers")) {
					ArrayList arrayList = WorldMapRemotePlayers.instance.getPlayers();
					for (int int1 = 0; int1 < arrayList.size(); ++int1) {
						WorldMapRemotePlayer worldMapRemotePlayer = (WorldMapRemotePlayer)arrayList.get(int1);
						if (this.shouldShowRemotePlayer(worldMapRemotePlayer)) {
							this.renderPlayer(worldMapRemotePlayer.getX(), worldMapRemotePlayer.getY());
							this.renderPlayerName(worldMapRemotePlayer.getX(), worldMapRemotePlayer.getY(), worldMapRemotePlayer.getUsername());
						}
					}
				}
			}
		}
	}

	private boolean shouldShowRemotePlayer(WorldMapRemotePlayer worldMapRemotePlayer) {
		if (!worldMapRemotePlayer.hasFullData()) {
			return false;
		} else if (worldMapRemotePlayer.isInvisible()) {
			return this.isAdminSeeRemotePlayers();
		} else if (ServerOptions.getInstance().MapRemotePlayerVisibility.getValue() == 3) {
			return true;
		} else if (this.isAdminSeeRemotePlayers()) {
			return true;
		} else if (ServerOptions.getInstance().MapRemotePlayerVisibility.getValue() == 1) {
			return false;
		} else {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null) {
					if (this.isInSameFaction(player, worldMapRemotePlayer)) {
						return true;
					}

					if (this.isInSameSafehouse(player, worldMapRemotePlayer)) {
						return true;
					}
				}
			}

			return false;
		}
	}

	private boolean isAdminSeeRemotePlayers() {
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player = IsoPlayer.players[int1];
			if (player != null && !player.isAccessLevel("none")) {
				return true;
			}
		}

		return false;
	}

	private boolean isInSameFaction(IsoPlayer player, WorldMapRemotePlayer worldMapRemotePlayer) {
		Faction faction = Faction.getPlayerFaction(player);
		Faction faction2 = Faction.getPlayerFaction(worldMapRemotePlayer.getUsername());
		return faction != null && faction == faction2;
	}

	private boolean isInSameSafehouse(IsoPlayer player, WorldMapRemotePlayer worldMapRemotePlayer) {
		ArrayList arrayList = SafeHouse.getSafehouseList();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			SafeHouse safeHouse = (SafeHouse)arrayList.get(int1);
			if (safeHouse.playerAllowed(player.getUsername()) && safeHouse.playerAllowed(worldMapRemotePlayer.getUsername())) {
				return true;
			}
		}

		return false;
	}

	private void renderPlayer(float float1, float float2) {
		float float3 = this.m_renderer.getDisplayZoomF();
		float float4 = this.m_renderer.getCenterWorldX();
		float float5 = this.m_renderer.getCenterWorldY();
		float float6 = this.m_APIv1.worldToUIX(float1, float2, float3, float4, float5, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
		float float7 = this.m_APIv1.worldToUIY(float1, float2, float3, float4, float5, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
		float6 = PZMath.floor(float6);
		float7 = PZMath.floor(float7);
		this.DrawTextureScaledColor((Texture)null, (double)float6 - 3.0, (double)float7 - 3.0, 6.0, 6.0, 1.0, 0.0, 0.0, 1.0);
	}

	private void renderPlayerName(float float1, float float2, String string) {
		if (this.m_renderer.getBoolean("PlayerNames")) {
			if (!StringUtils.isNullOrWhitespace(string)) {
				float float3 = this.m_renderer.getDisplayZoomF();
				float float4 = this.m_renderer.getCenterWorldX();
				float float5 = this.m_renderer.getCenterWorldY();
				float float6 = this.m_APIv1.worldToUIX(float1, float2, float3, float4, float5, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
				float float7 = this.m_APIv1.worldToUIY(float1, float2, float3, float4, float5, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
				float6 = PZMath.floor(float6);
				float7 = PZMath.floor(float7);
				int int1 = TextManager.instance.MeasureStringX(UIFont.Small, string) + 16;
				int int2 = TextManager.instance.font.getLineHeight();
				int int3 = (int)Math.ceil((double)int2 * 1.25);
				this.DrawTextureScaledColor((Texture)null, (double)float6 - (double)int1 / 2.0, (double)float7 + 4.0, (double)int1, (double)int3, 0.5, 0.5, 0.5, 0.5);
				this.DrawTextCentre(string, (double)float6, (double)(float7 + 4.0F) + (double)(int3 - int2) / 2.0, 0.0, 0.0, 0.0, 1.0);
			}
		}
	}

	public void update() {
		super.update();
	}

	public Boolean onMouseDown(double double1, double double2) {
		if (GameKeyboard.isKeyDown(42)) {
			this.m_renderer.resetView();
		}

		return super.onMouseDown(double1, double2);
	}

	public Boolean onMouseUp(double double1, double double2) {
		return super.onMouseUp(double1, double2);
	}

	public void onMouseUpOutside(double double1, double double2) {
		super.onMouseUpOutside(double1, double2);
	}

	public Boolean onMouseMove(double double1, double double2) {
		return super.onMouseMove(double1, double2);
	}

	public Boolean onMouseWheel(double double1) {
		return super.onMouseWheel(double1);
	}

	public static void setExposed(LuaManager.Exposer exposer) {
		exposer.setExposed(MapItem.class);
		exposer.setExposed(MapSymbolDefinitions.class);
		exposer.setExposed(MapSymbolDefinitions.MapSymbolDefinition.class);
		exposer.setExposed(UIWorldMap.class);
		exposer.setExposed(UIWorldMapV1.class);
		exposer.setExposed(WorldMapGridSquareMarker.class);
		exposer.setExposed(WorldMapMarkers.class);
		exposer.setExposed(WorldMapRenderer.WorldMapBooleanOption.class);
		exposer.setExposed(WorldMapRenderer.WorldMapDoubleOption.class);
		exposer.setExposed(WorldMapVisited.class);
		WorldMapMarkersV1.setExposed(exposer);
		WorldMapStyleV1.setExposed(exposer);
		WorldMapSymbolsV1.setExposed(exposer);
		exposer.setExposed(WorldMapEditorState.class);
		exposer.setExposed(WorldMapSettings.class);
	}

	private void renderBuildingsWithoutFeatures() {
		if (this.m_bBuildingsWithoutFeatures) {
			Iterator iterator = this.m_buildingsWithoutFeatures.iterator();
			while (iterator.hasNext()) {
				BuildingDef buildingDef = (BuildingDef)iterator.next();
				this.debugRenderBuilding(buildingDef, 1.0F, 0.0F, 0.0F, 1.0F);
			}
		} else {
			this.m_bBuildingsWithoutFeatures = true;
			this.m_buildingsWithoutFeatures.clear();
			IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
			for (int int1 = 0; int1 < metaGrid.Buildings.size(); ++int1) {
				BuildingDef buildingDef2 = (BuildingDef)metaGrid.Buildings.get(int1);
				boolean boolean1 = false;
				for (int int2 = 0; int2 < buildingDef2.rooms.size(); ++int2) {
					RoomDef roomDef = (RoomDef)buildingDef2.rooms.get(int2);
					if (roomDef.level <= 0) {
						ArrayList arrayList = roomDef.getRects();
						for (int int3 = 0; int3 < arrayList.size(); ++int3) {
							RoomDef.RoomRect roomRect = (RoomDef.RoomRect)arrayList.get(int3);
							s_tempFeatures.clear();
							Iterator iterator2 = this.m_worldMap.m_data.iterator();
							while (iterator2.hasNext()) {
								WorldMapData worldMapData = (WorldMapData)iterator2.next();
								if (worldMapData.isReady()) {
									worldMapData.hitTest((float)roomRect.x + (float)roomRect.w / 2.0F, (float)roomRect.y + (float)roomRect.h / 2.0F, s_tempFeatures);
								}
							}

							if (!s_tempFeatures.isEmpty()) {
								boolean1 = true;
								break;
							}
						}

						if (boolean1) {
							break;
						}
					}
				}

				if (!boolean1) {
					this.m_buildingsWithoutFeatures.add(buildingDef2);
				}
			}
		}
	}

	private void debugRenderBuilding(BuildingDef buildingDef, float float1, float float2, float float3, float float4) {
		for (int int1 = 0; int1 < buildingDef.rooms.size(); ++int1) {
			ArrayList arrayList = ((RoomDef)buildingDef.rooms.get(int1)).getRects();
			for (int int2 = 0; int2 < arrayList.size(); ++int2) {
				RoomDef.RoomRect roomRect = (RoomDef.RoomRect)arrayList.get(int2);
				float float5 = this.m_APIv1.worldToUIX((float)roomRect.x, (float)roomRect.y);
				float float6 = this.m_APIv1.worldToUIY((float)roomRect.x, (float)roomRect.y);
				float float7 = this.m_APIv1.worldToUIX((float)roomRect.getX2(), (float)roomRect.getY2());
				float float8 = this.m_APIv1.worldToUIY((float)roomRect.getX2(), (float)roomRect.getY2());
				this.DrawTextureScaledColor((Texture)null, (double)float5, (double)float6, (double)(float7 - float5), (double)(float8 - float6), (double)float1, (double)float2, (double)float3, (double)float4);
			}
		}
	}
}
