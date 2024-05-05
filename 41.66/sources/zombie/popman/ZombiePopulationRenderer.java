package zombie.popman;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;
import zombie.MapCollisionData;
import zombie.ZomboidFileSystem;
import zombie.ai.states.WalkTowardState;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.LotHeader;
import zombie.iso.RoomDef;
import zombie.network.GameClient;
import zombie.ui.TextManager;
import zombie.ui.UIElement;
import zombie.ui.UIFont;
import zombie.vehicles.VehiclesDB2;


public final class ZombiePopulationRenderer {
	private float xPos;
	private float yPos;
	private float offx;
	private float offy;
	private float zoom;
	private float draww;
	private float drawh;
	private static final int VERSION = 1;
	private final ArrayList options = new ArrayList();
	private ZombiePopulationRenderer.BooleanDebugOption CellGrid = new ZombiePopulationRenderer.BooleanDebugOption("CellGrid", true);
	private ZombiePopulationRenderer.BooleanDebugOption MetaGridBuildings = new ZombiePopulationRenderer.BooleanDebugOption("MetaGrid.Buildings", true);
	private ZombiePopulationRenderer.BooleanDebugOption ZombiesStanding = new ZombiePopulationRenderer.BooleanDebugOption("Zombies.Standing", true);
	private ZombiePopulationRenderer.BooleanDebugOption ZombiesMoving = new ZombiePopulationRenderer.BooleanDebugOption("Zombies.Moving", true);
	private ZombiePopulationRenderer.BooleanDebugOption MCDObstacles = new ZombiePopulationRenderer.BooleanDebugOption("MapCollisionData.Obstacles", true);
	private ZombiePopulationRenderer.BooleanDebugOption MCDRegularChunkOutlines = new ZombiePopulationRenderer.BooleanDebugOption("MapCollisionData.RegularChunkOutlines", true);
	private ZombiePopulationRenderer.BooleanDebugOption MCDRooms = new ZombiePopulationRenderer.BooleanDebugOption("MapCollisionData.Rooms", true);
	private ZombiePopulationRenderer.BooleanDebugOption Vehicles = new ZombiePopulationRenderer.BooleanDebugOption("Vehicles", true);

	private native void n_render(float float1, int int1, int int2, float float2, float float3, int int3, int int4);

	private native void n_setWallFollowerStart(int int1, int int2);

	private native void n_setWallFollowerEnd(int int1, int int2);

	private native void n_wallFollowerMouseMove(int int1, int int2);

	private native void n_setDebugOption(String string, String string2);

	public float worldToScreenX(float float1) {
		float1 -= this.xPos;
		float1 *= this.zoom;
		float1 += this.offx;
		float1 += this.draww / 2.0F;
		return float1;
	}

	public float worldToScreenY(float float1) {
		float1 -= this.yPos;
		float1 *= this.zoom;
		float1 += this.offy;
		float1 += this.drawh / 2.0F;
		return float1;
	}

	public float uiToWorldX(float float1) {
		float1 -= this.draww / 2.0F;
		float1 /= this.zoom;
		float1 += this.xPos;
		return float1;
	}

	public float uiToWorldY(float float1) {
		float1 -= this.drawh / 2.0F;
		float1 /= this.zoom;
		float1 += this.yPos;
		return float1;
	}

	public void renderString(float float1, float float2, String string, double double1, double double2, double double3, double double4) {
		float float3 = this.worldToScreenX(float1);
		float float4 = this.worldToScreenY(float2);
		SpriteRenderer.instance.render((Texture)null, float3 - 2.0F, float4 - 2.0F, (float)(TextManager.instance.MeasureStringX(UIFont.Small, string) + 4), (float)(TextManager.instance.font.getLineHeight() + 4), 0.0F, 0.0F, 0.0F, 0.75F, (Consumer)null);
		TextManager.instance.DrawString((double)float3, (double)float4, string, double1, double2, double3, double4);
	}

	public void renderRect(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = this.worldToScreenX(float1);
		float float10 = this.worldToScreenY(float2);
		float float11 = this.worldToScreenX(float1 + float3);
		float float12 = this.worldToScreenY(float2 + float4);
		float3 = float11 - float9;
		float4 = float12 - float10;
		if (!(float9 >= this.offx + this.draww) && !(float11 < this.offx) && !(float10 >= this.offy + this.drawh) && !(float12 < this.offy)) {
			SpriteRenderer.instance.render((Texture)null, float9, float10, float3, float4, float5, float6, float7, float8, (Consumer)null);
		}
	}

	public void renderLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = this.worldToScreenX(float1);
		float float10 = this.worldToScreenY(float2);
		float float11 = this.worldToScreenX(float3);
		float float12 = this.worldToScreenY(float4);
		if ((!(float9 >= (float)Core.getInstance().getScreenWidth()) || !(float11 >= (float)Core.getInstance().getScreenWidth())) && (!(float10 >= (float)Core.getInstance().getScreenHeight()) || !(float12 >= (float)Core.getInstance().getScreenHeight())) && (!(float9 < 0.0F) || !(float11 < 0.0F)) && (!(float10 < 0.0F) || !(float12 < 0.0F))) {
			SpriteRenderer.instance.renderline((Texture)null, (int)float9, (int)float10, (int)float11, (int)float12, float5, float6, float7, float8);
		}
	}

	public void renderCircle(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		byte byte1 = 32;
		double double1 = (double)float1 + (double)float3 * Math.cos(Math.toRadians((double)(0.0F / (float)byte1)));
		double double2 = (double)float2 + (double)float3 * Math.sin(Math.toRadians((double)(0.0F / (float)byte1)));
		for (int int1 = 1; int1 <= byte1; ++int1) {
			double double3 = (double)float1 + (double)float3 * Math.cos(Math.toRadians((double)((float)int1 * 360.0F / (float)byte1)));
			double double4 = (double)float2 + (double)float3 * Math.sin(Math.toRadians((double)((float)int1 * 360.0F / (float)byte1)));
			int int2 = (int)this.worldToScreenX((float)double1);
			int int3 = (int)this.worldToScreenY((float)double2);
			int int4 = (int)this.worldToScreenX((float)double3);
			int int5 = (int)this.worldToScreenY((float)double4);
			SpriteRenderer.instance.renderline((Texture)null, int2, int3, int4, int5, float4, float5, float6, float7);
			double1 = double3;
			double2 = double4;
		}
	}

	public void renderZombie(float float1, float float2, float float3, float float4, float float5) {
		float float6 = 1.0F / this.zoom + 0.5F;
		this.renderRect(float1 - float6 / 2.0F, float2 - float6 / 2.0F, float6, float6, float3, float4, float5, 1.0F);
	}

	public void renderVehicle(int int1, float float1, float float2, float float3, float float4, float float5) {
		float float6 = 2.0F / this.zoom + 0.5F;
		this.renderRect(float1 - float6 / 2.0F, float2 - float6 / 2.0F, float6, float6, float3, float4, float5, 1.0F);
		this.renderString(float1, float2, String.format("%d", int1), (double)float3, (double)float4, (double)float5, 1.0);
	}

	public void outlineRect(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		this.renderLine(float1, float2, float1 + float3, float2, float5, float6, float7, float8);
		this.renderLine(float1 + float3, float2, float1 + float3, float2 + float4, float5, float6, float7, float8);
		this.renderLine(float1, float2 + float4, float1 + float3, float2 + float4, float5, float6, float7, float8);
		this.renderLine(float1, float2, float1, float2 + float4, float5, float6, float7, float8);
	}

	public void renderCellInfo(int int1, int int2, int int3, int int4, float float1) {
		float float2 = this.worldToScreenX((float)(int1 * 300)) + 4.0F;
		float float3 = this.worldToScreenY((float)(int2 * 300)) + 4.0F;
		String string = int3 + " / " + int4;
		if (float1 > 0.0F) {
			string = string + String.format(" %.2f", float1);
		}

		SpriteRenderer.instance.render((Texture)null, float2 - 2.0F, float3 - 2.0F, (float)(TextManager.instance.MeasureStringX(UIFont.Small, string) + 4), (float)(TextManager.instance.font.getLineHeight() + 4), 0.0F, 0.0F, 0.0F, 0.75F, (Consumer)null);
		TextManager.instance.DrawString((double)float2, (double)float3, string, 1.0, 1.0, 1.0, 1.0);
	}

	public void render(UIElement uIElement, float float1, float float2, float float3) {
		synchronized (MapCollisionData.instance.renderLock) {
			this._render(uIElement, float1, float2, float3);
		}
	}

	private void _render(UIElement uIElement, float float1, float float2, float float3) {
		this.draww = (float)uIElement.getWidth().intValue();
		this.drawh = (float)uIElement.getHeight().intValue();
		this.xPos = float2;
		this.yPos = float3;
		this.offx = (float)uIElement.getAbsoluteX().intValue();
		this.offy = (float)uIElement.getAbsoluteY().intValue();
		this.zoom = float1;
		IsoCell cell = IsoWorld.instance.CurrentCell;
		IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[0];
		IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
		IsoMetaCell[][] metaCellArrayArray = metaGrid.Grid;
		int int1 = (int)(this.uiToWorldX(0.0F) / 300.0F) - metaGrid.minX;
		int int2 = (int)(this.uiToWorldY(0.0F) / 300.0F) - metaGrid.minY;
		int int3 = (int)(this.uiToWorldX(this.draww) / 300.0F) + 1 - metaGrid.minX;
		int int4 = (int)(this.uiToWorldY(this.drawh) / 300.0F) + 1 - metaGrid.minY;
		int1 = PZMath.clamp(int1, 0, metaGrid.getWidth() - 1);
		int2 = PZMath.clamp(int2, 0, metaGrid.getHeight() - 1);
		int3 = PZMath.clamp(int3, 0, metaGrid.getWidth() - 1);
		int4 = PZMath.clamp(int4, 0, metaGrid.getHeight() - 1);
		int int5;
		if (this.MetaGridBuildings.getValue()) {
			for (int5 = int1; int5 <= int3; ++int5) {
				for (int int6 = int2; int6 <= int4; ++int6) {
					LotHeader lotHeader = metaCellArrayArray[int5][int6].info;
					if (lotHeader != null) {
						for (int int7 = 0; int7 < lotHeader.Buildings.size(); ++int7) {
							BuildingDef buildingDef = (BuildingDef)lotHeader.Buildings.get(int7);
							for (int int8 = 0; int8 < buildingDef.rooms.size(); ++int8) {
								if (((RoomDef)buildingDef.rooms.get(int8)).level <= 0) {
									ArrayList arrayList = ((RoomDef)buildingDef.rooms.get(int8)).getRects();
									for (int int9 = 0; int9 < arrayList.size(); ++int9) {
										RoomDef.RoomRect roomRect = (RoomDef.RoomRect)arrayList.get(int9);
										if (buildingDef.bAlarmed) {
											this.renderRect((float)roomRect.getX(), (float)roomRect.getY(), (float)roomRect.getW(), (float)roomRect.getH(), 0.8F, 0.8F, 0.5F, 0.3F);
										} else {
											this.renderRect((float)roomRect.getX(), (float)roomRect.getY(), (float)roomRect.getW(), (float)roomRect.getH(), 0.5F, 0.5F, 0.8F, 0.3F);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		if (this.CellGrid.getValue()) {
			for (int5 = int2; int5 <= int4; ++int5) {
				this.renderLine((float)(metaGrid.minX * 300), (float)((metaGrid.minY + int5) * 300), (float)((metaGrid.maxX + 1) * 300), (float)((metaGrid.minY + int5) * 300), 1.0F, 1.0F, 1.0F, 0.15F);
			}

			for (int5 = int1; int5 <= int3; ++int5) {
				this.renderLine((float)((metaGrid.minX + int5) * 300), (float)(metaGrid.minY * 300), (float)((metaGrid.minX + int5) * 300), (float)((metaGrid.maxY + 1) * 300), 1.0F, 1.0F, 1.0F, 0.15F);
			}
		}

		for (int5 = 0; int5 < IsoWorld.instance.CurrentCell.getZombieList().size(); ++int5) {
			IsoZombie zombie = (IsoZombie)IsoWorld.instance.CurrentCell.getZombieList().get(int5);
			float float4 = 1.0F;
			float float5 = 1.0F;
			float float6 = 0.0F;
			this.renderZombie(zombie.x, zombie.y, float4, float5, float6);
			if (zombie.getCurrentState() == WalkTowardState.instance()) {
				this.renderLine(zombie.x, zombie.y, (float)zombie.getPathTargetX(), (float)zombie.getPathTargetY(), 1.0F, 1.0F, 1.0F, 0.5F);
			}
		}

		for (int5 = 0; int5 < IsoPlayer.numPlayers; ++int5) {
			IsoPlayer player = IsoPlayer.players[int5];
			if (player != null) {
				this.renderZombie(player.x, player.y, 0.0F, 0.5F, 0.0F);
			}
		}

		if (GameClient.bClient) {
			MPDebugInfo.instance.render(this, float1);
		} else {
			if (this.Vehicles.getValue()) {
				VehiclesDB2.instance.renderDebug(this);
			}

			this.n_render(float1, (int)this.offx, (int)this.offy, float2, float3, (int)this.draww, (int)this.drawh);
		}
	}

	public void setWallFollowerStart(int int1, int int2) {
		if (!GameClient.bClient) {
			this.n_setWallFollowerStart(int1, int2);
		}
	}

	public void setWallFollowerEnd(int int1, int int2) {
		if (!GameClient.bClient) {
			this.n_setWallFollowerEnd(int1, int2);
		}
	}

	public void wallFollowerMouseMove(int int1, int int2) {
		if (!GameClient.bClient) {
			this.n_wallFollowerMouseMove(int1, int2);
		}
	}

	public ConfigOption getOptionByName(String string) {
		for (int int1 = 0; int1 < this.options.size(); ++int1) {
			ConfigOption configOption = (ConfigOption)this.options.get(int1);
			if (configOption.getName().equals(string)) {
				return configOption;
			}
		}

		return null;
	}

	public int getOptionCount() {
		return this.options.size();
	}

	public ConfigOption getOptionByIndex(int int1) {
		return (ConfigOption)this.options.get(int1);
	}

	public void setBoolean(String string, boolean boolean1) {
		ConfigOption configOption = this.getOptionByName(string);
		if (configOption instanceof BooleanConfigOption) {
			((BooleanConfigOption)configOption).setValue(boolean1);
		}
	}

	public boolean getBoolean(String string) {
		ConfigOption configOption = this.getOptionByName(string);
		return configOption instanceof BooleanConfigOption ? ((BooleanConfigOption)configOption).getValue() : false;
	}

	public void save() {
		String string = ZomboidFileSystem.instance.getCacheDir();
		String string2 = string + File.separator + "popman-options.ini";
		ConfigFile configFile = new ConfigFile();
		configFile.write(string2, 1, this.options);
		for (int int1 = 0; int1 < this.options.size(); ++int1) {
			ConfigOption configOption = (ConfigOption)this.options.get(int1);
			this.n_setDebugOption(configOption.getName(), configOption.getValueAsString());
		}
	}

	public void load() {
		String string = ZomboidFileSystem.instance.getCacheDir();
		String string2 = string + File.separator + "popman-options.ini";
		ConfigFile configFile = new ConfigFile();
		int int1;
		ConfigOption configOption;
		if (configFile.read(string2)) {
			for (int1 = 0; int1 < configFile.getOptions().size(); ++int1) {
				configOption = (ConfigOption)configFile.getOptions().get(int1);
				ConfigOption configOption2 = this.getOptionByName(configOption.getName());
				if (configOption2 != null) {
					configOption2.parse(configOption.getValueAsString());
				}
			}
		}

		for (int1 = 0; int1 < this.options.size(); ++int1) {
			configOption = (ConfigOption)this.options.get(int1);
			this.n_setDebugOption(configOption.getName(), configOption.getValueAsString());
		}
	}

	public class BooleanDebugOption extends BooleanConfigOption {

		public BooleanDebugOption(String string, boolean boolean1) {
			super(string, boolean1);
			ZombiePopulationRenderer.this.options.add(this);
		}
	}
}
