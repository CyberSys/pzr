package zombie.iso.areas.isoregion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.MapCollisionData;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoPlayer;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.core.utils.Bits;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.LotHeader;
import zombie.iso.RoomDef;
import zombie.iso.areas.isoregion.data.DataChunk;
import zombie.iso.areas.isoregion.data.DataRoot;
import zombie.iso.areas.isoregion.regions.IsoChunkRegion;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;
import zombie.iso.objects.IsoThumpable;
import zombie.ui.TextManager;
import zombie.ui.UIElement;
import zombie.ui.UIFont;


public class IsoRegionsRenderer {
	private final List tempChunkList = new ArrayList();
	private final List debugLines = new ArrayList();
	private float xPos;
	private float yPos;
	private float offx;
	private float offy;
	private float zoom;
	private float draww;
	private float drawh;
	private boolean hasSelected = false;
	private boolean validSelection = false;
	private int selectedX;
	private int selectedY;
	private int selectedZ;
	private final HashSet drawnCells = new HashSet();
	private boolean editSquareInRange = false;
	private int editSquareX;
	private int editSquareY;
	private final ArrayList editOptions = new ArrayList();
	private boolean EditingEnabled = false;
	private final IsoRegionsRenderer.BooleanDebugOption EditWallN;
	private final IsoRegionsRenderer.BooleanDebugOption EditWallW;
	private final IsoRegionsRenderer.BooleanDebugOption EditDoorN;
	private final IsoRegionsRenderer.BooleanDebugOption EditDoorW;
	private final IsoRegionsRenderer.BooleanDebugOption EditFloor;
	private final ArrayList zLevelOptions;
	private final IsoRegionsRenderer.BooleanDebugOption zLevelPlayer;
	private final IsoRegionsRenderer.BooleanDebugOption zLevel0;
	private final IsoRegionsRenderer.BooleanDebugOption zLevel1;
	private final IsoRegionsRenderer.BooleanDebugOption zLevel2;
	private final IsoRegionsRenderer.BooleanDebugOption zLevel3;
	private final IsoRegionsRenderer.BooleanDebugOption zLevel4;
	private final IsoRegionsRenderer.BooleanDebugOption zLevel5;
	private final IsoRegionsRenderer.BooleanDebugOption zLevel6;
	private final IsoRegionsRenderer.BooleanDebugOption zLevel7;
	private static final int VERSION = 1;
	private final ArrayList options;
	private final IsoRegionsRenderer.BooleanDebugOption CellGrid;
	private final IsoRegionsRenderer.BooleanDebugOption MetaGridBuildings;
	private final IsoRegionsRenderer.BooleanDebugOption IsoRegionRender;
	private final IsoRegionsRenderer.BooleanDebugOption IsoRegionRenderChunks;
	private final IsoRegionsRenderer.BooleanDebugOption IsoRegionRenderChunksPlus;

	public IsoRegionsRenderer() {
		this.EditWallN = new IsoRegionsRenderer.BooleanDebugOption(this.editOptions, "Edit.WallN", false);
		this.EditWallW = new IsoRegionsRenderer.BooleanDebugOption(this.editOptions, "Edit.WallW", false);
		this.EditDoorN = new IsoRegionsRenderer.BooleanDebugOption(this.editOptions, "Edit.DoorN", false);
		this.EditDoorW = new IsoRegionsRenderer.BooleanDebugOption(this.editOptions, "Edit.DoorW", false);
		this.EditFloor = new IsoRegionsRenderer.BooleanDebugOption(this.editOptions, "Edit.Floor", false);
		this.zLevelOptions = new ArrayList();
		this.zLevelPlayer = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.Player", true);
		this.zLevel0 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.0", false, 0);
		this.zLevel1 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.1", false, 1);
		this.zLevel2 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.2", false, 2);
		this.zLevel3 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.3", false, 3);
		this.zLevel4 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.4", false, 4);
		this.zLevel5 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.5", false, 5);
		this.zLevel6 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.6", false, 6);
		this.zLevel7 = new IsoRegionsRenderer.BooleanDebugOption(this.zLevelOptions, "zLevel.7", false, 7);
		this.options = new ArrayList();
		this.CellGrid = new IsoRegionsRenderer.BooleanDebugOption(this.options, "CellGrid", true);
		this.MetaGridBuildings = new IsoRegionsRenderer.BooleanDebugOption(this.options, "MetaGrid.Buildings", true);
		this.IsoRegionRender = new IsoRegionsRenderer.BooleanDebugOption(this.options, "IsoRegion.Render", true);
		this.IsoRegionRenderChunks = new IsoRegionsRenderer.BooleanDebugOption(this.options, "IsoRegion.RenderChunks", false);
		this.IsoRegionRenderChunksPlus = new IsoRegionsRenderer.BooleanDebugOption(this.options, "IsoRegion.RenderChunksPlus", false);
	}

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

	public void renderStringUI(float float1, float float2, String string, Color color) {
		this.renderStringUI(float1, float2, string, (double)color.r, (double)color.g, (double)color.b, (double)color.a);
	}

	public void renderStringUI(float float1, float float2, String string, double double1, double double2, double double3, double double4) {
		float float3 = this.offx + float1;
		float float4 = this.offy + float2;
		SpriteRenderer.instance.render((Texture)null, float3 - 2.0F, float4 - 2.0F, (float)(TextManager.instance.MeasureStringX(UIFont.Small, string) + 4), (float)(TextManager.instance.font.getLineHeight() + 4), 0.0F, 0.0F, 0.0F, 0.75F, (Consumer)null);
		TextManager.instance.DrawString((double)float3, (double)float4, string, double1, double2, double3, double4);
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

	public void renderZombie(float float1, float float2, float float3, float float4, float float5) {
		float float6 = 1.0F / this.zoom + 0.5F;
		this.renderRect(float1 - float6 / 2.0F, float2 - float6 / 2.0F, float6, float6, float3, float4, float5, 1.0F);
	}

	public void renderSquare(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = 1.0F;
		this.renderRect(float1, float2, float7, float7, float3, float4, float5, float6);
	}

	public void renderEntity(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		float float8 = float1 / this.zoom + 0.5F;
		this.renderRect(float2 - float8 / 2.0F, float3 - float8 / 2.0F, float8, float8, float4, float5, float6, float7);
	}

	public void render(UIElement uIElement, float float1, float float2, float float3) {
		synchronized (MapCollisionData.instance.renderLock) {
			this._render(uIElement, float1, float2, float3);
		}
	}

	private void debugLine(String string) {
		this.debugLines.add(string);
	}

	public void recalcSurroundings() {
		IsoRegions.forceRecalcSurroundingChunks();
	}

	public boolean hasChunkRegion(int int1, int int2) {
		int int3 = this.getZLevel();
		DataRoot dataRoot = IsoRegions.getDataRoot();
		return dataRoot.getIsoChunkRegion(int1, int2, int3) != null;
	}

	public IsoChunkRegion getChunkRegion(int int1, int int2) {
		int int3 = this.getZLevel();
		DataRoot dataRoot = IsoRegions.getDataRoot();
		return dataRoot.getIsoChunkRegion(int1, int2, int3);
	}

	public void setSelected(int int1, int int2) {
		this.setSelectedWorld((int)this.uiToWorldX((float)int1), (int)this.uiToWorldY((float)int2));
	}

	public void setSelectedWorld(int int1, int int2) {
		this.selectedZ = this.getZLevel();
		this.hasSelected = true;
		this.selectedX = int1;
		this.selectedY = int2;
	}

	public void unsetSelected() {
		this.hasSelected = false;
	}

	public boolean isHasSelected() {
		return this.hasSelected;
	}

	private void _render(UIElement uIElement, float float1, float float2, float float3) {
		this.debugLines.clear();
		this.drawnCells.clear();
		this.draww = (float)uIElement.getWidth().intValue();
		this.drawh = (float)uIElement.getHeight().intValue();
		this.xPos = float2;
		this.yPos = float3;
		this.offx = (float)uIElement.getAbsoluteX().intValue();
		this.offy = (float)uIElement.getAbsoluteY().intValue();
		this.zoom = float1;
		this.debugLine("Zoom: " + float1);
		this.debugLine("zLevel: " + this.getZLevel());
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
		float float4 = Math.max(1.0F - float1 / 2.0F, 0.1F);
		IsoChunkRegion chunkRegion = null;
		IsoWorldRegion worldRegion = null;
		this.validSelection = false;
		DataRoot dataRoot;
		DataChunk dataChunk;
		int int5;
		int int6;
		float float5;
		if (this.IsoRegionRender.getValue()) {
			IsoPlayer player = IsoPlayer.getInstance();
			dataRoot = IsoRegions.getDataRoot();
			this.tempChunkList.clear();
			dataRoot.getAllChunks(this.tempChunkList);
			this.debugLine("DataChunks: " + this.tempChunkList.size());
			this.debugLine("IsoChunkRegions: " + dataRoot.regionManager.getChunkRegionCount());
			this.debugLine("IsoWorldRegions: " + dataRoot.regionManager.getWorldRegionCount());
			if (this.hasSelected) {
				chunkRegion = dataRoot.getIsoChunkRegion(this.selectedX, this.selectedY, this.selectedZ);
				worldRegion = dataRoot.getIsoWorldRegion(this.selectedX, this.selectedY, this.selectedZ);
				if (worldRegion != null && !worldRegion.isEnclosed() && (!this.IsoRegionRenderChunks.getValue() || !this.IsoRegionRenderChunksPlus.getValue())) {
					worldRegion = null;
					chunkRegion = null;
				}

				if (chunkRegion != null) {
					this.validSelection = true;
				}
			}

			for (int int7 = 0; int7 < this.tempChunkList.size(); ++int7) {
				dataChunk = (DataChunk)this.tempChunkList.get(int7);
				int5 = dataChunk.getChunkX() * 10;
				int6 = dataChunk.getChunkY() * 10;
				if (float1 > 0.1F) {
					float float6 = this.worldToScreenX((float)int5);
					float float7 = this.worldToScreenY((float)int6);
					float float8 = this.worldToScreenX((float)(int5 + 10));
					float5 = this.worldToScreenY((float)(int6 + 10));
					if (!(float6 >= this.offx + this.draww) && !(float8 < this.offx) && !(float7 >= this.offy + this.drawh) && !(float5 < this.offy)) {
						this.renderRect((float)int5, (float)int6, 10.0F, 10.0F, 0.0F, float4, 0.0F, 1.0F);
					}
				}
			}
		}

		float float9;
		int int8;
		int int9;
		if (this.MetaGridBuildings.getValue()) {
			float9 = PZMath.clamp(0.3F * (float1 / 5.0F), 0.15F, 0.3F);
			for (int8 = int1; int8 < int3; ++int8) {
				for (int9 = int2; int9 < int4; ++int9) {
					LotHeader lotHeader = metaCellArrayArray[int8][int9].info;
					if (lotHeader != null) {
						for (int6 = 0; int6 < lotHeader.Buildings.size(); ++int6) {
							BuildingDef buildingDef = (BuildingDef)lotHeader.Buildings.get(int6);
							for (int int10 = 0; int10 < buildingDef.rooms.size(); ++int10) {
								if (((RoomDef)buildingDef.rooms.get(int10)).level <= 0) {
									ArrayList arrayList = ((RoomDef)buildingDef.rooms.get(int10)).getRects();
									for (int int11 = 0; int11 < arrayList.size(); ++int11) {
										RoomDef.RoomRect roomRect = (RoomDef.RoomRect)arrayList.get(int11);
										if (buildingDef.bAlarmed) {
											this.renderRect((float)roomRect.getX(), (float)roomRect.getY(), (float)roomRect.getW(), (float)roomRect.getH(), 0.8F * float9, 0.8F * float9, 0.5F * float9, 1.0F);
										} else {
											this.renderRect((float)roomRect.getX(), (float)roomRect.getY(), (float)roomRect.getW(), (float)roomRect.getH(), 0.5F * float9, 0.5F * float9, 0.8F * float9, 1.0F);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		int int12;
		if (this.IsoRegionRender.getValue()) {
			int12 = this.getZLevel();
			dataRoot = IsoRegions.getDataRoot();
			this.tempChunkList.clear();
			dataRoot.getAllChunks(this.tempChunkList);
			float float10 = 1.0F;
			for (int int13 = 0; int13 < this.tempChunkList.size(); ++int13) {
				dataChunk = (DataChunk)this.tempChunkList.get(int13);
				int5 = dataChunk.getChunkX() * 10;
				int6 = dataChunk.getChunkY() * 10;
				int int14;
				int int15;
				int int16;
				if (float1 <= 0.1F) {
					int14 = int5 / 300;
					int15 = int6 / 300;
					int16 = IsoRegions.hash(int14, int15);
					if (!this.drawnCells.contains(int16)) {
						this.drawnCells.add(int16);
						this.renderRect((float)(int14 * 300), (float)(int15 * 300), 300.0F, 300.0F, 0.0F, float4, 0.0F, 1.0F);
					}
				} else if (!(float1 < 1.0F)) {
					float5 = this.worldToScreenX((float)int5);
					float float11 = this.worldToScreenY((float)int6);
					float float12 = this.worldToScreenX((float)(int5 + 10));
					float float13 = this.worldToScreenY((float)(int6 + 10));
					if (!(float5 >= this.offx + this.draww) && !(float12 < this.offx) && !(float11 >= this.offy + this.drawh) && !(float13 < this.offy)) {
						for (int14 = 0; int14 < 10; ++int14) {
							for (int15 = 0; int15 < 10; ++int15) {
								int16 = int12 > 0 ? int12 - 1 : int12;
								for (int int17 = int16; int17 <= int12; ++int17) {
									float float14 = int17 < int12 ? 0.25F : 1.0F;
									byte byte1 = dataChunk.getSquare(int14, int15, int17);
									if (byte1 >= 0) {
										IsoChunkRegion chunkRegion2 = dataChunk.getIsoChunkRegion(int14, int15, int17);
										IsoWorldRegion worldRegion2;
										if (chunkRegion2 != null) {
											Color color;
											if (float1 > 6.0F && this.IsoRegionRenderChunks.getValue() && this.IsoRegionRenderChunksPlus.getValue()) {
												color = chunkRegion2.getColor();
												float10 = 1.0F;
												if (chunkRegion != null && chunkRegion2 != chunkRegion) {
													float10 = 0.25F;
												}

												this.renderSquare((float)(int5 + int14), (float)(int6 + int15), color.r, color.g, color.b, float10 * float14);
											} else {
												worldRegion2 = chunkRegion2.getIsoWorldRegion();
												if (worldRegion2 != null && worldRegion2.isEnclosed()) {
													float10 = 1.0F;
													if (this.IsoRegionRenderChunks.getValue()) {
														color = chunkRegion2.getColor();
														if (chunkRegion != null && chunkRegion2 != chunkRegion) {
															float10 = 0.25F;
														}
													} else {
														color = worldRegion2.getColor();
														if (worldRegion != null && worldRegion2 != worldRegion) {
															float10 = 0.25F;
														}
													}

													this.renderSquare((float)(int5 + int14), (float)(int6 + int15), color.r, color.g, color.b, float10 * float14);
												}
											}
										}

										if (int17 > 0 && int17 == int12) {
											chunkRegion2 = dataChunk.getIsoChunkRegion(int14, int15, int17);
											worldRegion2 = chunkRegion2 != null ? chunkRegion2.getIsoWorldRegion() : null;
											boolean boolean1 = chunkRegion2 == null || worldRegion2 == null || !worldRegion2.isEnclosed();
											if (boolean1 && Bits.hasFlags((byte)byte1, 16)) {
												this.renderSquare((float)(int5 + int14), (float)(int6 + int15), 0.5F, 0.5F, 0.5F, 1.0F);
											}
										}

										if (Bits.hasFlags((byte)byte1, 1) || Bits.hasFlags((byte)byte1, 4)) {
											this.renderRect((float)(int5 + int14), (float)(int6 + int15), 1.0F, 0.1F, 1.0F, 1.0F, 1.0F, 1.0F * float14);
										}

										if (Bits.hasFlags((byte)byte1, 2) || Bits.hasFlags((byte)byte1, 8)) {
											this.renderRect((float)(int5 + int14), (float)(int6 + int15), 0.1F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F * float14);
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
			float9 = 1.0F;
			if (float1 < 0.1F) {
				float9 = Math.max(float1 / 0.1F, 0.25F);
			}

			for (int8 = int2; int8 <= int4; ++int8) {
				this.renderLine((float)(metaGrid.minX * 300), (float)((metaGrid.minY + int8) * 300), (float)((metaGrid.maxX + 1) * 300), (float)((metaGrid.minY + int8) * 300), 1.0F, 1.0F, 1.0F, 0.15F * float9);
				if (float1 > 1.0F) {
					for (int9 = 1; int9 < 30; ++int9) {
						this.renderLine((float)(metaGrid.minX * 300), (float)((metaGrid.minY + int8) * 300 + int9 * 10), (float)((metaGrid.maxX + 1) * 300), (float)((metaGrid.minY + int8) * 300 + int9 * 10), 1.0F, 1.0F, 1.0F, 0.0325F);
					}
				} else if (float1 > 0.15F) {
					this.renderLine((float)(metaGrid.minX * 300), (float)((metaGrid.minY + int8) * 300 + 100), (float)((metaGrid.maxX + 1) * 300), (float)((metaGrid.minY + int8) * 300 + 100), 1.0F, 1.0F, 1.0F, 0.075F);
					this.renderLine((float)(metaGrid.minX * 300), (float)((metaGrid.minY + int8) * 300 + 200), (float)((metaGrid.maxX + 1) * 300), (float)((metaGrid.minY + int8) * 300 + 200), 1.0F, 1.0F, 1.0F, 0.075F);
				}
			}

			for (int8 = int1; int8 <= int3; ++int8) {
				this.renderLine((float)((metaGrid.minX + int8) * 300), (float)(metaGrid.minY * 300), (float)((metaGrid.minX + int8) * 300), (float)((metaGrid.maxY + 1) * 300), 1.0F, 1.0F, 1.0F, 0.15F * float9);
				if (float1 > 1.0F) {
					for (int9 = 1; int9 < 30; ++int9) {
						this.renderLine((float)((metaGrid.minX + int8) * 300 + int9 * 10), (float)(metaGrid.minY * 300), (float)((metaGrid.minX + int8) * 300 + int9 * 10), (float)((metaGrid.maxY + 1) * 300), 1.0F, 1.0F, 1.0F, 0.0325F);
					}
				} else if (float1 > 0.15F) {
					this.renderLine((float)((metaGrid.minX + int8) * 300 + 100), (float)(metaGrid.minY * 300), (float)((metaGrid.minX + int8) * 300 + 100), (float)((metaGrid.maxY + 1) * 300), 1.0F, 1.0F, 1.0F, 0.075F);
					this.renderLine((float)((metaGrid.minX + int8) * 300 + 200), (float)(metaGrid.minY * 300), (float)((metaGrid.minX + int8) * 300 + 200), (float)((metaGrid.maxY + 1) * 300), 1.0F, 1.0F, 1.0F, 0.075F);
				}
			}
		}

		for (int12 = 0; int12 < IsoPlayer.numPlayers; ++int12) {
			IsoPlayer player2 = IsoPlayer.players[int12];
			if (player2 != null) {
				this.renderZombie(player2.x, player2.y, 0.0F, 0.5F, 0.0F);
			}
		}

		if (this.isEditingEnabled()) {
			float9 = this.editSquareInRange ? 0.0F : 1.0F;
			float float15 = this.editSquareInRange ? 1.0F : 0.0F;
			if (!this.EditWallN.getValue() && !this.EditDoorN.getValue()) {
				if (!this.EditWallW.getValue() && !this.EditDoorW.getValue()) {
					this.renderRect((float)this.editSquareX, (float)this.editSquareY, 1.0F, 1.0F, float9, float15, 0.0F, 0.5F);
					this.renderRect((float)this.editSquareX, (float)this.editSquareY, 1.0F, 0.05F, float9, float15, 0.0F, 1.0F);
					this.renderRect((float)this.editSquareX, (float)this.editSquareY, 0.05F, 1.0F, float9, float15, 0.0F, 1.0F);
					this.renderRect((float)this.editSquareX, (float)this.editSquareY + 0.95F, 1.0F, 0.05F, float9, float15, 0.0F, 1.0F);
					this.renderRect((float)this.editSquareX + 0.95F, (float)this.editSquareY, 0.05F, 1.0F, float9, float15, 0.0F, 1.0F);
				} else {
					this.renderRect((float)this.editSquareX, (float)this.editSquareY, 0.25F, 1.0F, float9, float15, 0.0F, 0.5F);
					this.renderRect((float)this.editSquareX, (float)this.editSquareY, 0.25F, 0.05F, float9, float15, 0.0F, 1.0F);
					this.renderRect((float)this.editSquareX, (float)this.editSquareY, 0.05F, 1.0F, float9, float15, 0.0F, 1.0F);
					this.renderRect((float)this.editSquareX, (float)this.editSquareY + 0.95F, 0.25F, 0.05F, float9, float15, 0.0F, 1.0F);
					this.renderRect((float)this.editSquareX + 0.2F, (float)this.editSquareY, 0.05F, 1.0F, float9, float15, 0.0F, 1.0F);
				}
			} else {
				this.renderRect((float)this.editSquareX, (float)this.editSquareY, 1.0F, 0.25F, float9, float15, 0.0F, 0.5F);
				this.renderRect((float)this.editSquareX, (float)this.editSquareY, 1.0F, 0.05F, float9, float15, 0.0F, 1.0F);
				this.renderRect((float)this.editSquareX, (float)this.editSquareY, 0.05F, 0.25F, float9, float15, 0.0F, 1.0F);
				this.renderRect((float)this.editSquareX, (float)this.editSquareY + 0.2F, 1.0F, 0.05F, float9, float15, 0.0F, 1.0F);
				this.renderRect((float)this.editSquareX + 0.95F, (float)this.editSquareY, 0.05F, 0.25F, float9, float15, 0.0F, 1.0F);
			}
		}

		if (chunkRegion != null) {
			this.debugLine("- ChunkRegion -");
			this.debugLine("ID: " + chunkRegion.getID());
			this.debugLine("Squares: " + chunkRegion.getSquareSize());
			this.debugLine("Roofs: " + chunkRegion.getRoofCnt());
			this.debugLine("Neighbors: " + chunkRegion.getNeighborCount());
			this.debugLine("ConnectedNeighbors: " + chunkRegion.getConnectedNeighbors().size());
			this.debugLine("FullyEnclosed: " + chunkRegion.getIsEnclosed());
		}

		if (worldRegion != null) {
			this.debugLine("- WorldRegion -");
			this.debugLine("ID: " + worldRegion.getID());
			this.debugLine("Squares: " + worldRegion.getSquareSize());
			this.debugLine("Roofs: " + worldRegion.getRoofCnt());
			this.debugLine("IsFullyRoofed: " + worldRegion.isFullyRoofed());
			this.debugLine("RoofPercentage: " + worldRegion.getRoofedPercentage());
			this.debugLine("IsEnclosed: " + worldRegion.isEnclosed());
			this.debugLine("Neighbors: " + worldRegion.getNeighbors().size());
			this.debugLine("ChunkRegionCount: " + worldRegion.size());
		}

		int12 = 15;
		for (int8 = 0; int8 < this.debugLines.size(); ++int8) {
			this.renderStringUI(10.0F, (float)int12, (String)this.debugLines.get(int8), Colors.CornFlowerBlue);
			int12 += 18;
		}
	}

	public void setEditSquareCoord(int int1, int int2) {
		this.editSquareX = int1;
		this.editSquareY = int2;
		this.editSquareInRange = false;
		if (this.editCoordInRange(int1, int2)) {
			this.editSquareInRange = true;
		}
	}

	private boolean editCoordInRange(int int1, int int2) {
		IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare(int1, int2, 0);
		return square != null;
	}

	public void editSquare(int int1, int int2) {
		if (this.isEditingEnabled()) {
			int int3 = this.getZLevel();
			IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare(int1, int2, int3);
			DataRoot dataRoot = IsoRegions.getDataRoot();
			byte byte1 = dataRoot.getSquareFlags(int1, int2, int3);
			if (this.editCoordInRange(int1, int2)) {
				if (square == null) {
					square = IsoWorld.instance.getCell().createNewGridSquare(int1, int2, int3, true);
					if (square == null) {
						return;
					}
				}

				this.editSquareInRange = true;
				for (int int4 = 0; int4 < this.editOptions.size(); ++int4) {
					IsoRegionsRenderer.BooleanDebugOption booleanDebugOption = (IsoRegionsRenderer.BooleanDebugOption)this.editOptions.get(int4);
					if (booleanDebugOption.getValue()) {
						String string = booleanDebugOption.getName();
						byte byte2 = -1;
						switch (string.hashCode()) {
						case -1465489028: 
							if (string.equals("Edit.DoorN")) {
								byte2 = 3;
							}

							break;
						
						case -1465489019: 
							if (string.equals("Edit.DoorW")) {
								byte2 = 2;
							}

							break;
						
						case -1463731416: 
							if (string.equals("Edit.Floor")) {
								byte2 = 4;
							}

							break;
						
						case -1448362272: 
							if (string.equals("Edit.WallN")) {
								byte2 = 1;
							}

							break;
						
						case -1448362263: 
							if (string.equals("Edit.WallW")) {
								byte2 = 0;
							}

						
						}

						IsoThumpable thumpable;
						switch (byte2) {
						case 0: 
						
						case 1: 
							if (booleanDebugOption.getName().equals("Edit.WallN")) {
								if (byte1 > 0 && Bits.hasFlags((byte)byte1, 1)) {
									return;
								}

								thumpable = new IsoThumpable(IsoWorld.instance.getCell(), square, "walls_exterior_wooden_01_25", true, (KahluaTable)null);
							} else {
								if (byte1 > 0 && Bits.hasFlags((byte)byte1, 2)) {
									return;
								}

								thumpable = new IsoThumpable(IsoWorld.instance.getCell(), square, "walls_exterior_wooden_01_24", true, (KahluaTable)null);
							}

							thumpable.setMaxHealth(100);
							thumpable.setName("Wall Debug");
							thumpable.setBreakSound("BreakObject");
							square.AddSpecialObject(thumpable);
							square.RecalcAllWithNeighbours(true);
							thumpable.transmitCompleteItemToServer();
							if (square.getZone() != null) {
								square.getZone().setHaveConstruction(true);
							}

							break;
						
						case 2: 
						
						case 3: 
							if (booleanDebugOption.getName().equals("Edit.DoorN")) {
								if (byte1 > 0 && Bits.hasFlags((byte)byte1, 1)) {
									return;
								}

								thumpable = new IsoThumpable(IsoWorld.instance.getCell(), square, "walls_exterior_wooden_01_35", true, (KahluaTable)null);
							} else {
								if (byte1 > 0 && Bits.hasFlags((byte)byte1, 2)) {
									return;
								}

								thumpable = new IsoThumpable(IsoWorld.instance.getCell(), square, "walls_exterior_wooden_01_34", true, (KahluaTable)null);
							}

							thumpable.setMaxHealth(100);
							thumpable.setName("Door Frame Debug");
							thumpable.setBreakSound("BreakObject");
							square.AddSpecialObject(thumpable);
							square.RecalcAllWithNeighbours(true);
							thumpable.transmitCompleteItemToServer();
							if (square.getZone() != null) {
								square.getZone().setHaveConstruction(true);
							}

							break;
						
						case 4: 
							if (byte1 > 0 && Bits.hasFlags((byte)byte1, 16)) {
								return;
							}

							if (int3 == 0) {
								return;
							}

							square.addFloor("carpentry_02_56");
							if (square.getZone() != null) {
								square.getZone().setHaveConstruction(true);
							}

						
						}
					}
				}
			} else {
				this.editSquareInRange = false;
			}
		}
	}

	public boolean isEditingEnabled() {
		return this.EditingEnabled;
	}

	public void editRotate() {
		if (this.EditWallN.getValue()) {
			this.EditWallN.setValue(false);
			this.EditWallW.setValue(true);
		} else if (this.EditWallW.getValue()) {
			this.EditWallW.setValue(false);
			this.EditWallN.setValue(true);
		}

		if (this.EditDoorN.getValue()) {
			this.EditDoorN.setValue(false);
			this.EditDoorW.setValue(true);
		} else if (this.EditDoorW.getValue()) {
			this.EditDoorW.setValue(false);
			this.EditDoorN.setValue(true);
		}
	}

	public ConfigOption getEditOptionByName(String string) {
		for (int int1 = 0; int1 < this.editOptions.size(); ++int1) {
			ConfigOption configOption = (ConfigOption)this.editOptions.get(int1);
			if (configOption.getName().equals(string)) {
				return configOption;
			}
		}

		return null;
	}

	public int getEditOptionCount() {
		return this.editOptions.size();
	}

	public ConfigOption getEditOptionByIndex(int int1) {
		return (ConfigOption)this.editOptions.get(int1);
	}

	public void setEditOption(int int1, boolean boolean1) {
		for (int int2 = 0; int2 < this.editOptions.size(); ++int2) {
			IsoRegionsRenderer.BooleanDebugOption booleanDebugOption = (IsoRegionsRenderer.BooleanDebugOption)this.editOptions.get(int2);
			if (int2 != int1) {
				booleanDebugOption.setValue(false);
			} else {
				booleanDebugOption.setValue(boolean1);
				this.EditingEnabled = boolean1;
			}
		}
	}

	public int getZLevel() {
		if (this.zLevelPlayer.getValue()) {
			return (int)IsoPlayer.getInstance().getZ();
		} else {
			for (int int1 = 0; int1 < this.zLevelOptions.size(); ++int1) {
				IsoRegionsRenderer.BooleanDebugOption booleanDebugOption = (IsoRegionsRenderer.BooleanDebugOption)this.zLevelOptions.get(int1);
				if (booleanDebugOption.getValue()) {
					return booleanDebugOption.zLevel;
				}
			}

			return 0;
		}
	}

	public ConfigOption getZLevelOptionByName(String string) {
		for (int int1 = 0; int1 < this.zLevelOptions.size(); ++int1) {
			ConfigOption configOption = (ConfigOption)this.zLevelOptions.get(int1);
			if (configOption.getName().equals(string)) {
				return configOption;
			}
		}

		return null;
	}

	public int getZLevelOptionCount() {
		return this.zLevelOptions.size();
	}

	public ConfigOption getZLevelOptionByIndex(int int1) {
		return (ConfigOption)this.zLevelOptions.get(int1);
	}

	public void setZLevelOption(int int1, boolean boolean1) {
		for (int int2 = 0; int2 < this.zLevelOptions.size(); ++int2) {
			IsoRegionsRenderer.BooleanDebugOption booleanDebugOption = (IsoRegionsRenderer.BooleanDebugOption)this.zLevelOptions.get(int2);
			if (int2 != int1) {
				booleanDebugOption.setValue(false);
			} else {
				booleanDebugOption.setValue(boolean1);
			}
		}

		if (!boolean1) {
			this.zLevelPlayer.setValue(true);
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
		String string2 = string + File.separator + "isoregions-options.ini";
		ConfigFile configFile = new ConfigFile();
		configFile.write(string2, 1, this.options);
	}

	public void load() {
		String string = ZomboidFileSystem.instance.getCacheDir();
		String string2 = string + File.separator + "isoregions-options.ini";
		ConfigFile configFile = new ConfigFile();
		if (configFile.read(string2)) {
			for (int int1 = 0; int1 < configFile.getOptions().size(); ++int1) {
				ConfigOption configOption = (ConfigOption)configFile.getOptions().get(int1);
				ConfigOption configOption2 = this.getOptionByName(configOption.getName());
				if (configOption2 != null) {
					configOption2.parse(configOption.getValueAsString());
				}
			}
		}
	}

	public static class BooleanDebugOption extends BooleanConfigOption {
		private int index;
		private int zLevel = 0;

		public BooleanDebugOption(ArrayList arrayList, String string, boolean boolean1, int int1) {
			super(string, boolean1);
			this.index = arrayList.size();
			this.zLevel = int1;
			arrayList.add(this);
		}

		public BooleanDebugOption(ArrayList arrayList, String string, boolean boolean1) {
			super(string, boolean1);
			this.index = arrayList.size();
			arrayList.add(this);
		}

		public int getIndex() {
			return this.index;
		}
	}
}
