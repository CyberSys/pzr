package zombie.popman;

import java.io.File;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.MapCollisionData;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoWorld;
import zombie.iso.LotHeader;
import zombie.iso.RoomDef;
import zombie.network.GameClient;
import zombie.ui.TextManager;
import zombie.ui.UIElement;
import zombie.ui.UIFont;


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
	private ZombiePopulationRenderer.BooleanDebugOption MetaGridBuildings = new ZombiePopulationRenderer.BooleanDebugOption("MetaGrid.Buildings", true);
	private ZombiePopulationRenderer.BooleanDebugOption ZombiesStanding = new ZombiePopulationRenderer.BooleanDebugOption("Zombies.Standing", true);
	private ZombiePopulationRenderer.BooleanDebugOption ZombiesMoving = new ZombiePopulationRenderer.BooleanDebugOption("Zombies.Moving", true);
	private ZombiePopulationRenderer.BooleanDebugOption MCDObstacles = new ZombiePopulationRenderer.BooleanDebugOption("MapCollisionData.Obstacles", true);
	private ZombiePopulationRenderer.BooleanDebugOption MCDRegularChunkOutlines = new ZombiePopulationRenderer.BooleanDebugOption("MapCollisionData.RegularChunkOutlines", true);
	private ZombiePopulationRenderer.BooleanDebugOption MCDRooms = new ZombiePopulationRenderer.BooleanDebugOption("MapCollisionData.Rooms", true);

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

	public void renderRect(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = this.worldToScreenX(float1);
		float float10 = this.worldToScreenY(float2);
		float float11 = this.worldToScreenX(float1 + float3);
		float float12 = this.worldToScreenY(float2 + float4);
		float3 = float11 - float9;
		float4 = float12 - float10;
		if (!(float9 >= (float)Core.getInstance().getScreenWidth()) && !(float11 < 0.0F) && !(float10 >= (float)Core.getInstance().getScreenHeight()) && !(float12 < 0.0F)) {
			SpriteRenderer.instance.render((Texture)null, float9, float10, float3, float4, float5, float6, float7, float8);
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

	public void renderZombie(float float1, float float2, float float3, float float4, float float5) {
		float float6 = 1.0F / this.zoom + 0.5F;
		this.renderRect(float1 - float6 / 2.0F, float2 - float6 / 2.0F, float6, float6, float3, float4, float5, 1.0F);
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

		SpriteRenderer.instance.render((Texture)null, float2 - 2.0F, float3 - 2.0F, (float)(TextManager.instance.MeasureStringX(UIFont.Small, string) + 4), (float)(TextManager.instance.font.getLineHeight() + 4), 0.0F, 0.0F, 0.0F, 0.75F);
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
		IsoMetaCell[][] metaCellArrayArray = IsoWorld.instance.MetaGrid.Grid;
		int int1;
		if (this.MetaGridBuildings.getValue()) {
			for (int1 = 0; int1 < metaCellArrayArray.length; ++int1) {
				for (int int2 = 0; int2 < metaCellArrayArray[0].length; ++int2) {
					LotHeader lotHeader = metaCellArrayArray[int1][int2].info;
					if (lotHeader != null) {
						for (int int3 = 0; int3 < lotHeader.Buildings.size(); ++int3) {
							BuildingDef buildingDef = (BuildingDef)lotHeader.Buildings.get(int3);
							for (int int4 = 0; int4 < buildingDef.rooms.size(); ++int4) {
								if (((RoomDef)buildingDef.rooms.get(int4)).level <= 0) {
									ArrayList arrayList = ((RoomDef)buildingDef.rooms.get(int4)).getRects();
									for (int int5 = 0; int5 < arrayList.size(); ++int5) {
										RoomDef.RoomRect roomRect = (RoomDef.RoomRect)arrayList.get(int5);
										if (!buildingDef.isAllExplored() && buildingDef.bAlarmed) {
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

		if (GameClient.bClient) {
			MPDebugInfo.instance.render(this, float1);
		} else {
			for (int1 = 0; int1 < IsoWorld.instance.CurrentCell.getZombieList().size(); ++int1) {
				IsoZombie zombie = (IsoZombie)IsoWorld.instance.CurrentCell.getZombieList().get(int1);
				float float4 = 1.0F;
				float float5 = 1.0F;
				float float6 = 0.0F;
				this.renderZombie(zombie.x, zombie.y, float4, float5, float6);
			}

			for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null) {
					this.renderZombie(player.x, player.y, 0.0F, 0.5F, 0.0F);
				}
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
		String string = GameWindow.getCacheDir() + File.separator + "popman-options.ini";
		ConfigFile configFile = new ConfigFile();
		configFile.write(string, 1, this.options);
		for (int int1 = 0; int1 < this.options.size(); ++int1) {
			ConfigOption configOption = (ConfigOption)this.options.get(int1);
			this.n_setDebugOption(configOption.getName(), configOption.getValueAsString());
		}
	}

	public void load() {
		String string = GameWindow.getCacheDir() + File.separator + "popman-options.ini";
		ConfigFile configFile = new ConfigFile();
		int int1;
		ConfigOption configOption;
		if (configFile.read(string)) {
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
