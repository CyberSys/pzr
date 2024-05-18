package zombie.gameStates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameTime;
import zombie.VirtualZombieManager;
import zombie.ai.astar.Mover;
import zombie.ai.states.PathFindState;
import zombie.ai.states.WalkTowardState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.chat.ChatElement;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.properties.PropertyContainer;
import zombie.core.textures.Texture;
import zombie.core.utils.BooleanGrid;
import zombie.debug.LineDrawer;
import zombie.erosion.ErosionData;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LightingThread;
import zombie.iso.LosUtil;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.vehicles.BaseVehicle;


public class DebugChunkState extends GameState {
	private boolean keyDown = false;
	private int gridX = -1;
	private int gridY = -1;
	private UIFont FONT;
	static boolean keyQpressed = false;

	public DebugChunkState() {
		this.FONT = UIFont.DebugConsole;
	}

	public void enter() {
		this.keyDown = GameKeyboard.isKeyDown(60);
	}

	public void exit() {
		LightingThread.instance.disable = false;
	}

	public void render() {
		int int1 = IsoPlayer.getPlayerIndex();
		Core.getInstance().StartFrame(int1, true);
		IsoCamera.frameState.set(int1);
		IsoWorld.instance.CurrentCell.render();
		this.drawGrid();
		this.drawCursor();
		Stack stack = IsoWorld.instance.getCell().getLamppostPositions();
		int int2 = 0;
		for (int int3 = 0; int3 < stack.size(); ++int3) {
			IsoLightSource lightSource = (IsoLightSource)stack.get(int3);
			if (lightSource.z == (int)IsoPlayer.instance.getZ()) {
				this.paintSquare(lightSource.x, lightSource.y, lightSource.z, 1.0F, 1.0F, 0.0F, 0.5F);
			}

			if (lightSource.bActive) {
				++int2;
			}
		}

		IsoMetaGrid.Zone zone = IsoPlayer.instance.getCurrentZone();
		if (zone != null) {
			this.DrawIsoLine((float)zone.x, (float)zone.y, (float)(zone.x + zone.w), (float)zone.y, 1.0F, 1.0F, 0.0F, 1.0F, 1);
			this.DrawIsoLine((float)zone.x, (float)(zone.y + zone.h), (float)(zone.x + zone.w), (float)(zone.y + zone.h), 1.0F, 1.0F, 0.0F, 1.0F, 1);
			this.DrawIsoLine((float)zone.x, (float)zone.y, (float)zone.x, (float)(zone.y + zone.h), 1.0F, 1.0F, 0.0F, 1.0F, 1);
			this.DrawIsoLine((float)(zone.x + zone.w), (float)zone.y, (float)(zone.x + zone.w), (float)(zone.y + zone.h), 1.0F, 1.0F, 0.0F, 1.0F, 1);
		}

		IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, (int)IsoPlayer.instance.getZ());
		if (square != null && square.getBuilding() != null) {
			BuildingDef buildingDef = square.getBuilding().getDef();
			this.DrawIsoLine((float)buildingDef.getX(), (float)buildingDef.getY(), (float)buildingDef.getX2(), (float)buildingDef.getY(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
			this.DrawIsoLine((float)buildingDef.getX2(), (float)buildingDef.getY(), (float)buildingDef.getX2(), (float)buildingDef.getY2(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
			this.DrawIsoLine((float)buildingDef.getX2(), (float)buildingDef.getY2(), (float)buildingDef.getX(), (float)buildingDef.getY2(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
			this.DrawIsoLine((float)buildingDef.getX(), (float)buildingDef.getY2(), (float)buildingDef.getX(), (float)buildingDef.getY(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
		}

		LineDrawer.render();
		LineDrawer.clear();
		Core.getInstance().EndFrame(int1);
		Core.getInstance().RenderOffScreenBuffer();
		int int4;
		for (int4 = 0; int4 < IsoPlayer.numPlayers; ++int4) {
			TextDrawObject.NoRender(int4);
			ChatElement.NoRender(int4);
		}

		if (Core.getInstance().StartFrameUI()) {
			int4 = 10;
			boolean boolean1 = false;
			int int5 = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
			int int6 = (int)IsoPlayer.instance.getZ();
			IsoGridSquare square2 = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, int6);
			int int7;
			if (square2 != null) {
				String string = "x,y,z=" + square2.getX() + "," + square2.getY() + "," + square2.getZ();
				if (square2.getZone() != null) {
					string = string + " zone=" + square2.getZone().getName() + "/" + square2.getZone().getType();
				} else {
					string = string + " zone=<none>";
				}

				this.DrawString(10, int4 += int5, string);
				this.DrawString(10, int4 += int5, "chunk: ObjectsSyncCount=" + square2.chunk.ObjectsSyncCount + " Hash=" + square2.chunk.getHashCodeObjects());
				this.DrawString(10, int4 += int5, "square: ObjectsSyncCount=" + square2.ObjectsSyncCount + " Hash=" + square2.getHashCodeObjects());
				this.DrawString(10, int4 += int5, "darkMulti=" + square2.getDarkMulti(int1) + "	targetDarkMulti=" + square2.getTargetDarkMulti(int1) + "	seen=" + square2.getSeen(int1) + "	couldSee=" + square2.isCouldSee(int1) + "	canSee=" + square2.getCanSee(int1));
				this.DrawString(10, int4 += int5, "getVertLight()=" + Integer.toHexString(square2.getVertLight(0, int1)) + "," + Integer.toHexString(square2.getVertLight(1, int1)) + "," + Integer.toHexString(square2.getVertLight(2, int1)) + "," + Integer.toHexString(square2.getVertLight(3, int1)));
				int int8;
				if (square2.getRoom() != null) {
					String string2 = square2.getRoom().building.def.bAlarmed && !square2.getRoom().building.isAllExplored() ? "	ALARM" : "	(no alarm)";
					int8 = 0;
					int7 = square2.getRoom().lightSwitches.size();
					if (int7 != 0) {
						int8 = ((IsoLightSwitch)square2.getRoom().lightSwitches.get(0)).lights.size();
					}

					this.DrawString(10, int4 += int5, "buildingID=" + square2.getBuilding().ID + " roomID=" + square2.getRoomID() + "	room=" + square2.getRoom().RoomDef + "	explored=" + square2.getRoom().def.bExplored + "	#lights=" + int8 + "	#switches=" + int7 + string2);
				} else {
					this.DrawString(10, int4 += int5, "roomID=" + square2.getRoomID() + "	room=<none>");
				}

				if (square2.roofHideBuilding != null) {
					this.DrawString(10, int4 += int5, "ROOF-HIDE=" + square2.roofHideBuilding.def.getID());
				}

				int int9;
				for (int9 = 0; int9 < stack.size(); ++int9) {
					IsoLightSource lightSource2 = (IsoLightSource)stack.get(int9);
					if (lightSource2.getX() == square2.getX() && lightSource2.getY() == square2.getY() && lightSource2.getZ() == square2.getZ()) {
						this.DrawString(10, int4 += int5, "LIGHT SOURCE" + (lightSource2.bHydroPowered ? " hydro=true" : " hydro=false") + " active=" + lightSource2.bActive + " localToBuilding=" + (lightSource2.localToBuilding != null ? lightSource2.localToBuilding.ID : -1) + " " + lightSource2);
					}
				}

				this.DrawString(10, int4 += int5, "exterior=" + square2.getProperties().Is(IsoFlagType.exterior) + "	haveElectricity=" + square2.haveElectricity() + "	haveRoof=" + square2.haveRoof + "	solidfloor=" + square2.getProperties().Is(IsoFlagType.solidfloor) + "	TreatAsSolidFloor=" + (square2.TreatAsSolidFloor() ? "true" : "false") + "	solid=" + square2.Is(IsoFlagType.solid) + "	solidTrans=" + square2.Is(IsoFlagType.solidtrans) + "	burning=" + square2.getProperties().Is(IsoFlagType.burning) + "	burntOut=" + square2.getProperties().Is(IsoFlagType.burntOut));
				this.DrawString(10, int4 += int5, "HasRaindrop=" + square2.getProperties().Is(IsoFlagType.HasRaindrop) + "	HasRainSplashes=" + square2.getProperties().Is(IsoFlagType.HasRainSplashes));
				float float1;
				IsoSprite sprite;
				for (int9 = 0; int9 < square2.getObjects().size(); ++int9) {
					IsoObject object = (IsoObject)square2.getObjects().get(int9);
					this.DrawString(10, int4 += int5, "object=" + object.getClass().getName() + " name=" + object.getName() + " type=" + object.getType() + " alpha=" + object.alpha[0] + " targetAlpha=" + object.targetAlpha[0]);
					if (object instanceof IsoFire) {
						this.DrawString(10, int4 += int5, "IsoFire Energy=" + ((IsoFire)object).Energy);
					}

					sprite = object.getSprite();
					float1 = sprite != null && sprite.def != null ? sprite.def.alpha : 0.0F;
					this.DrawString(20, int4 += int5, "sprite.name=" + (sprite != null ? sprite.name : "<none>") + "	spriteName=" + object.getSpriteName() + "	type=" + (sprite != null ? sprite.getType() : "<none>") + "	alpha=" + float1 + "	ID=" + (sprite != null ? sprite.ID : "<none>") + " renderYOffset=" + object.getRenderYOffset());
					if (sprite != null && sprite.firerequirement > 0) {
						this.DrawString(20, int4 += int5, "fireRequirement=" + sprite.firerequirement);
					}

					if (object.AttachedAnimSpriteActual != null) {
						for (int int10 = 0; int10 < object.AttachedAnimSpriteActual.size(); ++int10) {
							IsoSprite sprite2 = (IsoSprite)object.AttachedAnimSpriteActual.get(int10);
							this.DrawString(20, int4 += int5, "attached.name=" + sprite2.name + "	type=" + sprite2.getType() + "	ID=" + sprite2.ID);
						}
					}

					int int11;
					String string3;
					if (sprite != null && sprite.getProperties() != null) {
						PropertyContainer propertyContainer = sprite.getProperties();
						ArrayList arrayList = propertyContainer.getPropertyNames();
						String string4 = "";
						if (!arrayList.isEmpty()) {
							int int12 = 0;
							int int13 = 0;
							Collections.sort(arrayList);
							for (int11 = 0; int11 < arrayList.size(); ++int11) {
								string3 = (String)arrayList.get(int11);
								string4 = string4 + string3 + "=" + propertyContainer.Val(string3) + "  ";
								if (string4.substring(int13).length() > 80 && int11 + 1 < arrayList.size()) {
									string4 = string4 + "\n	";
									int13 = string4.length();
									++int12;
								}
							}

							if (!string4.isEmpty()) {
								this.DrawString(20, int4 += int5, "properties: " + string4);
								int4 += int12 * int5;
								string4 = "";
							}
						}

						if (propertyContainer.Is(IsoFlagType.collideN)) {
							string4 = string4 + "collideN  ";
						}

						if (propertyContainer.Is(IsoFlagType.collideW)) {
							string4 = string4 + "collideW  ";
						}

						if (propertyContainer.Is(IsoFlagType.cutN)) {
							string4 = string4 + "cutN  ";
						}

						if (propertyContainer.Is(IsoFlagType.cutW)) {
							string4 = string4 + "cutW  ";
						}

						if (propertyContainer.Is(IsoFlagType.doorN)) {
							string4 = string4 + "doorN  ";
						}

						if (propertyContainer.Is(IsoFlagType.doorW)) {
							string4 = string4 + "doorW  ";
						}

						if (propertyContainer.Is(IsoFlagType.windowN)) {
							string4 = string4 + "windowN  ";
						}

						if (propertyContainer.Is(IsoFlagType.windowW)) {
							string4 = string4 + "windowW  ";
						}

						if (propertyContainer.Is(IsoFlagType.climbSheetTopN)) {
							string4 = string4 + "climbSheetTopN  ";
						}

						if (propertyContainer.Is(IsoFlagType.climbSheetTopS)) {
							string4 = string4 + "climbSheetTopS  ";
						}

						if (propertyContainer.Is(IsoFlagType.climbSheetTopW)) {
							string4 = string4 + "climbSheetTopW  ";
						}

						if (propertyContainer.Is(IsoFlagType.climbSheetTopE)) {
							string4 = string4 + "climbSheetTopE  ";
						}

						if (propertyContainer.Is(IsoFlagType.HoppableN)) {
							string4 = string4 + "HoppableN  ";
						}

						if (propertyContainer.Is(IsoFlagType.HoppableW)) {
							string4 = string4 + "HoppableW  ";
						}

						if (propertyContainer.Is(IsoFlagType.solid)) {
							string4 = string4 + "solid  ";
						}

						if (propertyContainer.Is(IsoFlagType.vegitation)) {
							string4 = string4 + "vegitation  ";
						}

						if (!string4.isEmpty()) {
							this.DrawString(20, int4 += int5, "flags: " + string4);
						}
					}

					if (!object.getModData().isEmpty()) {
						String string5 = "modData: ";
						int int14 = 0;
						int int15 = 0;
						KahluaTableIterator kahluaTableIterator = object.getModData().iterator();
						ArrayList arrayList2 = new ArrayList();
						while (kahluaTableIterator.advance()) {
							arrayList2.add(kahluaTableIterator.getKey().toString());
						}

						Collections.sort(arrayList2);
						for (int11 = 0; int11 < arrayList2.size(); ++int11) {
							string3 = (String)arrayList2.get(int11);
							string5 = string5 + string3 + "=" + object.getModData().rawget(string3).toString() + "  ";
							if (string5.substring(int15).length() > 80 && int11 + 1 < arrayList2.size()) {
								string5 = string5 + "\n	";
								int15 = string5.length();
								++int14;
							}
						}

						this.DrawString(20, int4 += int5, string5);
						int4 += int14 * int5;
					}

					if (object instanceof IsoWindow) {
						this.DrawString(20, int4 += int5, "Window: canAddSheetRope=" + ((IsoWindow)object).canAddSheetRope() + " PermaLocked=" + ((IsoWindow)object).isPermaLocked());
					}
				}

				Iterator iterator = square2.getMovingObjects().iterator();
				while (true) {
					IsoMovingObject movingObject;
					if (!iterator.hasNext()) {
						iterator = square2.getStaticMovingObjects().iterator();
						while (iterator.hasNext()) {
							movingObject = (IsoMovingObject)iterator.next();
							this.DrawString(10, int4 += int5, "static-object=" + movingObject.getClass().getName() + " name=" + movingObject.getName());
							sprite = movingObject.getSprite();
							float1 = sprite != null && sprite.def != null ? sprite.def.alpha : 0.0F;
							this.DrawString(20, int4 += int5, "sprite=" + (sprite != null ? sprite.name : "<none>") + "	alpha=" + float1);
							if (movingObject instanceof IsoDeadBody) {
								IsoDeadBody deadBody = (IsoDeadBody)movingObject;
								sprite = deadBody.legsSprite;
								this.DrawString(20, int4 += int5, "legsSprite=" + (sprite != null ? sprite.name : "<none>") + "	alpha=" + float1);
							}
						}

						iterator = square2.getWorldObjects().iterator();
						while (iterator.hasNext()) {
							IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)iterator.next();
							this.DrawString(10, int4 += int5, "world-object=" + worldInventoryObject.getClass().getName() + " name=" + worldInventoryObject.getName());
							sprite = worldInventoryObject.getSprite();
							float1 = sprite != null && sprite.def != null ? sprite.def.alpha : 0.0F;
							this.DrawString(20, int4 += int5, "sprite=" + (sprite != null ? sprite.name : "<none>") + "	alpha=" + float1);
						}

						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append("nav[] ");
						for (int8 = 0; int8 < 8; ++int8) {
							IsoGridSquare square3 = square2.nav[int8];
							stringBuilder.append(IsoDirections.fromIndex(int8).toString().toLowerCase() + " = ");
							stringBuilder.append(square3 != null ? square3.getX() + "," + square3.getY() + "," + square3.getZ() : "null");
							stringBuilder.append("  ");
						}

						this.DrawString(10, int4 += int5, stringBuilder.toString());
						stringBuilder.setLength(0);
						stringBuilder.append("n = " + (square2.n != null ? square2.n.getX() + "," + square2.n.getY() + "," + square2.n.getZ() : "null"));
						stringBuilder.append(" nw = " + (square2.nw != null ? square2.nw.getX() + "," + square2.nw.getY() + "," + square2.nw.getZ() : "null"));
						stringBuilder.append(" w = " + (square2.w != null ? square2.w.getX() + "," + square2.w.getY() + "," + square2.w.getZ() : "null"));
						stringBuilder.append(" sw = " + (square2.sw != null ? square2.sw.getX() + "," + square2.sw.getY() + "," + square2.sw.getZ() : "null"));
						stringBuilder.append(" s = " + (square2.s != null ? square2.s.getX() + "," + square2.s.getY() + "," + square2.s.getZ() : "null"));
						stringBuilder.append(" se = " + (square2.se != null ? square2.se.getX() + "," + square2.se.getY() + "," + square2.se.getZ() : "null"));
						stringBuilder.append(" e = " + (square2.e != null ? square2.e.getX() + "," + square2.e.getY() + "," + square2.e.getZ() : "null"));
						stringBuilder.append(" ne = " + (square2.ne != null ? square2.ne.getX() + "," + square2.ne.getY() + "," + square2.ne.getZ() : "null"));
						this.DrawString(10, int4 += int5, stringBuilder.toString());
						stringBuilder.setLength(0);
						stringBuilder.append("collideMatrix n=" + (square2.collideMatrix[1][0][1] ? "true" : "false"));
						stringBuilder.append(" nw=" + (square2.collideMatrix[0][0][1] ? "true" : "false"));
						stringBuilder.append(" w=" + (square2.collideMatrix[0][1][1] ? "true" : "false"));
						stringBuilder.append(" sw=" + (square2.collideMatrix[0][2][1] ? "true" : "false"));
						stringBuilder.append(" s=" + (square2.collideMatrix[1][2][1] ? "true" : "false"));
						stringBuilder.append(" se=" + (square2.collideMatrix[2][2][1] ? "true" : "false"));
						stringBuilder.append(" e=" + (square2.collideMatrix[2][1][1] ? "true" : "false"));
						stringBuilder.append(" ne=" + (square2.collideMatrix[2][0][1] ? "true" : "false"));
						stringBuilder.append(" above=" + (square2.collideMatrix[0][0][2] ? "true" : "false"));
						this.DrawString(10, int4 += int5, stringBuilder.toString());
						stringBuilder.setLength(0);
						stringBuilder.append("pathMatrix n=" + (square2.pathMatrix[1][0][1] ? "true" : "false"));
						stringBuilder.append(" nw=" + (square2.pathMatrix[0][0][1] ? "true" : "false"));
						stringBuilder.append(" w=" + (square2.pathMatrix[0][1][1] ? "true" : "false"));
						stringBuilder.append(" sw=" + (square2.pathMatrix[0][2][1] ? "true" : "false"));
						stringBuilder.append(" s=" + (square2.pathMatrix[1][2][1] ? "true" : "false"));
						stringBuilder.append(" se=" + (square2.pathMatrix[2][2][1] ? "true" : "false"));
						stringBuilder.append(" e=" + (square2.pathMatrix[2][1][1] ? "true" : "false"));
						stringBuilder.append(" ne=" + (square2.pathMatrix[2][0][1] ? "true" : "false"));
						this.DrawString(10, int4 += int5, stringBuilder.toString());
						stringBuilder.setLength(0);
						stringBuilder.append("pathMatrix(below) n=" + (square2.pathMatrix[1][0][0] ? "true" : "false"));
						stringBuilder.append(" nw=" + (square2.pathMatrix[0][0][0] ? "true" : "false"));
						stringBuilder.append(" w=" + (square2.pathMatrix[0][1][0] ? "true" : "false"));
						stringBuilder.append(" sw=" + (square2.pathMatrix[0][2][0] ? "true" : "false"));
						stringBuilder.append(" s=" + (square2.pathMatrix[1][2][0] ? "true" : "false"));
						stringBuilder.append(" se=" + (square2.pathMatrix[2][2][0] ? "true" : "false"));
						stringBuilder.append(" e=" + (square2.pathMatrix[2][1][0] ? "true" : "false"));
						stringBuilder.append(" ne=" + (square2.pathMatrix[2][0][0] ? "true" : "false"));
						this.DrawString(10, int4 += int5, stringBuilder.toString());
						stringBuilder.setLength(0);
						stringBuilder.append("visionMatrix n=" + (square2.visionMatrix[1][0][1] ? "true" : "false"));
						stringBuilder.append(" nw=" + (square2.visionMatrix[0][0][1] ? "true" : "false"));
						stringBuilder.append(" w=" + (square2.visionMatrix[0][1][1] ? "true" : "false"));
						stringBuilder.append(" sw=" + (square2.visionMatrix[0][2][1] ? "true" : "false"));
						stringBuilder.append(" s=" + (square2.visionMatrix[1][2][1] ? "true" : "false"));
						stringBuilder.append(" se=" + (square2.visionMatrix[2][2][1] ? "true" : "false"));
						stringBuilder.append(" e=" + (square2.visionMatrix[2][1][1] ? "true" : "false"));
						stringBuilder.append(" ne=" + (square2.visionMatrix[2][0][1] ? "true" : "false"));
						this.DrawString(10, int4 += int5, stringBuilder.toString());
						stringBuilder.setLength(0);
						stringBuilder.append("visionMatrix(above) n=" + (square2.visionMatrix[1][0][2] ? "true" : "false"));
						stringBuilder.append(" nw=" + (square2.visionMatrix[0][0][2] ? "true" : "false"));
						stringBuilder.append(" w=" + (square2.visionMatrix[0][1][2] ? "true" : "false"));
						stringBuilder.append(" sw=" + (square2.visionMatrix[0][2][2] ? "true" : "false"));
						stringBuilder.append(" s=" + (square2.visionMatrix[1][2][2] ? "true" : "false"));
						stringBuilder.append(" se=" + (square2.visionMatrix[2][2][2] ? "true" : "false"));
						stringBuilder.append(" e=" + (square2.visionMatrix[2][1][2] ? "true" : "false"));
						stringBuilder.append(" ne=" + (square2.visionMatrix[2][0][2] ? "true" : "false"));
						stringBuilder.append(" @=" + (square2.visionMatrix[1][1][2] ? "true" : "false"));
						this.DrawString(10, int4 += int5, stringBuilder.toString());
						stringBuilder.setLength(0);
						stringBuilder.append("visionMatrix(below) n=" + (square2.visionMatrix[1][0][0] ? "true" : "false"));
						stringBuilder.append(" nw=" + (square2.visionMatrix[0][0][0] ? "true" : "false"));
						stringBuilder.append(" w=" + (square2.visionMatrix[0][1][0] ? "true" : "false"));
						stringBuilder.append(" sw=" + (square2.visionMatrix[0][2][0] ? "true" : "false"));
						stringBuilder.append(" s=" + (square2.visionMatrix[1][2][0] ? "true" : "false"));
						stringBuilder.append(" se=" + (square2.visionMatrix[2][2][0] ? "true" : "false"));
						stringBuilder.append(" e=" + (square2.visionMatrix[2][1][0] ? "true" : "false"));
						stringBuilder.append(" ne=" + (square2.visionMatrix[2][0][0] ? "true" : "false"));
						stringBuilder.append(" @=" + (square2.visionMatrix[1][1][0] ? "true" : "false"));
						this.DrawString(10, int4 += int5, stringBuilder.toString());
						int4 += int5;
						break;
					}

					movingObject = (IsoMovingObject)iterator.next();
					String string6 = "";
					if (movingObject instanceof IsoGameCharacter && ((IsoGameCharacter)movingObject).getCurrentState() != null) {
						string6 = string6 + " " + ((IsoGameCharacter)movingObject).getCurrentState().getClass().getSimpleName();
						if (((IsoGameCharacter)movingObject).getCurrentState() == WalkTowardState.instance() || ((IsoGameCharacter)movingObject).getCurrentState() == PathFindState.instance()) {
							string6 = string6 + " " + ((IsoGameCharacter)movingObject).getPathTargetX() + "," + ((IsoGameCharacter)movingObject).getPathTargetY() + "," + ((IsoGameCharacter)movingObject).getPathTargetZ();
						}
					}

					if (movingObject instanceof IsoZombie && ((IsoZombie)movingObject).hasActiveModel()) {
						string6 = string6 + " dir=" + movingObject.getDir() + " angle=" + ((IsoGameCharacter)movingObject).getAngle().x + "," + ((IsoGameCharacter)movingObject).getAngle().y + " reqMovement=" + ((IsoGameCharacter)movingObject).reqMovement.x + "," + ((IsoGameCharacter)movingObject).reqMovement.y;
					}

					if (movingObject instanceof BaseVehicle) {
						string6 = string6 + " ID=" + ((BaseVehicle)movingObject).VehicleID;
					}

					if (movingObject instanceof IsoZombie && GameClient.bClient) {
						string6 = string6 + " OnlineID=" + ((IsoZombie)movingObject).OnlineID + " DescriptorID=" + ((IsoZombie)movingObject).getDescriptor().getID();
					}

					this.DrawString(10, int4 += int5, "moving-object=" + movingObject + " name=" + movingObject.getName() + string6);
					IsoSprite sprite3 = movingObject.getSprite();
					float float2 = sprite3 != null && sprite3.def != null ? sprite3.def.alpha : 0.0F;
					this.DrawString(20, int4 += int5, "sprite=" + (sprite3 != null ? sprite3.name : "<none>") + "	alpha=" + float2);
					if (movingObject instanceof IsoPlayer && GameClient.bClient) {
						this.DrawString(20, int4 += int5, "username=" + ((IsoPlayer)movingObject).getDisplayName() + " OnlineID=" + ((IsoPlayer)movingObject).OnlineID);
					}

					if (movingObject instanceof IsoZombie && ((IsoZombie)movingObject).getThumpTarget() != null) {
						this.DrawString(20, int4 += int5, "thumpTarget=" + ((IsoZombie)movingObject).getThumpTarget());
					}

					if (movingObject instanceof IsoZombie) {
						this.DrawString(20, int4 += int5, "states=[Client:" + ((IsoZombie)movingObject).getStateMachine().getCurrent().toString() + " Server:" + ((IsoZombie)movingObject).serverState + "]");
					}
				}
			}

			if (UIManager.LastPicked != null) {
				String string7 = UIManager.LastPicked.getSprite().getName();
				if (string7 == null) {
					string7 = UIManager.LastPicked.getSpriteName();
				}

				this.DrawString(10, int4 += int5, "UIManager.LastPicked=" + UIManager.LastPicked.getClass().getName() + " sprite=" + string7 + " x,y,z=" + UIManager.LastPicked.getX() + "," + UIManager.LastPicked.getY() + "," + UIManager.LastPicked.getZ());
			}

			IsoObject object2 = IsoObjectPicker.Instance.PickWindow(Mouse.getXA(), Mouse.getYA());
			this.DrawString(10, int4 += int5, "PickWindow=" + object2);
			object2 = IsoObjectPicker.Instance.PickCorpse(Mouse.getXA(), Mouse.getYA());
			this.DrawString(10, int4 += int5, "PickCorpse=" + object2);
			IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunkForGridSquare(this.gridX, this.gridY, int6);
			if (chunk != null) {
				int4 += 24;
				this.DrawString(10, int4, "FloorBloodSplats " + chunk.FloorBloodSplats.size());
			}

			LosUtil.TestResults testResults = LosUtil.lineClear(IsoWorld.instance.getCell(), (int)IsoPlayer.instance.x, (int)IsoPlayer.instance.y, (int)IsoPlayer.instance.z, this.gridX, this.gridY, (int)IsoPlayer.instance.z, false);
			LosUtil.TestResults testResults2 = LosUtil.lineClear(IsoWorld.instance.getCell(), (int)IsoPlayer.instance.x, (int)IsoPlayer.instance.y, (int)IsoPlayer.instance.z, this.gridX, this.gridY, (int)IsoPlayer.instance.z + 1, false);
			LosUtil.TestResults testResults3 = LosUtil.lineClear(IsoWorld.instance.getCell(), (int)IsoPlayer.instance.x, (int)IsoPlayer.instance.y, (int)IsoPlayer.instance.z, this.gridX, this.gridY, (int)IsoPlayer.instance.z - 1, false);
			this.DrawString(10, int4 + int5, "lineClear=" + testResults.toString() + (int6 < 7 ? "	above=" + testResults2.toString() : "") + (int6 > 0 ? "	below=" + testResults3.toString() : ""));
			int7 = IsoWorld.instance.CurrentCell.getZombieList().size();
			GameTime gameTime = GameTime.getInstance();
			this.DrawString(10, Core.getInstance().getScreenHeight() - 80, "Real zombies: " + int7);
			this.DrawString(10, Core.getInstance().getScreenHeight() - 40, "Dawn/Dusk: " + gameTime.getDawn() + "/" + gameTime.getDusk() + " Night min/cur/max: " + gameTime.getNightMin() + "/" + gameTime.getNight() + "/" + gameTime.getNightMax() + " Night Tint: " + gameTime.getNightTint());
			this.DrawString(10, Core.getInstance().getScreenHeight() - 20, "Lights active/total: " + int2 + "/" + stack.size());
			IsoPlayer player = IsoPlayer.instance;
			TextManager.instance.DrawStringCentre(this.FONT, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 40), "Player current x,y,z=" + player.getX() + "," + player.getY() + "," + player.getZ() + "(" + (player.getCurrentSquare() != null ? player.getCurrentSquare().x + "," + player.getCurrentSquare().y + "," + player.getCurrentSquare().z : "<null>") + ")", 1.0, 1.0, 1.0, 1.0);
			if (IsoPlayer.instance.last != null) {
				TextManager.instance.DrawStringCentre(this.FONT, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 20), "Player last x,y,z=" + player.last.getX() + "," + player.last.getY() + "," + player.last.getZ(), 1.0, 1.0, 1.0, 1.0);
			}

			this.drawModData();
			this.drawPlayerInfo();
		}

		Core.getInstance().EndFrameUI();
	}

	public GameStateMachine.StateAction update() {
		if (GameKeyboard.isKeyDown(60)) {
			if (!this.keyDown) {
				this.keyDown = true;
				return GameStateMachine.StateAction.Continue;
			}

			this.keyDown = true;
		} else if (this.keyDown) {
			this.keyDown = false;
		}

		if (GameKeyboard.isKeyDown(16)) {
			if (!keyQpressed) {
				IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, 0);
				if (square != null) {
					GameClient.instance.worldObjectsSyncReq.putRequestSyncIsoChunk(square.chunk);
				}

				keyQpressed = true;
			}
		} else {
			keyQpressed = false;
		}

		if (GameKeyboard.isKeyDown(19)) {
			if (!keyQpressed) {
				IsoCell.newRender = true;
				keyQpressed = true;
			}
		} else {
			keyQpressed = false;
		}

		if (GameKeyboard.isKeyDown(20)) {
			if (!keyQpressed) {
				IsoCell.newRender = false;
				keyQpressed = true;
			}
		} else {
			keyQpressed = false;
		}

		IsoCamera.update();
		this.updateCursor();
		return GameStateMachine.StateAction.Remain;
	}

	private void updateCursor() {
		int int1 = Core.TileScale;
		float float1 = (float)org.lwjgl.input.Mouse.getX();
		float float2 = (float)(Core.getInstance().getScreenHeight() - org.lwjgl.input.Mouse.getY() - 1);
		float1 -= (float)IsoCamera.getScreenLeft(IsoPlayer.getPlayerIndex());
		float2 -= (float)IsoCamera.getScreenTop(IsoPlayer.getPlayerIndex());
		float1 *= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
		float2 *= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
		int int2 = (int)IsoPlayer.instance.getZ();
		this.gridX = (int)IsoUtils.XToIso(float1 - (float)(0 * int1), float2 - 0.0F, (float)int2);
		this.gridY = (int)IsoUtils.YToIso(float1 - (float)(0 * int1), float2 - 0.0F, (float)int2);
	}

	private void DrawIsoLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1) {
		float float9 = (float)((int)IsoPlayer.instance.getZ());
		float float10 = IsoUtils.XToScreenExact(float1, float2, float9, 0);
		float float11 = IsoUtils.YToScreenExact(float1, float2, float9, 0);
		float float12 = IsoUtils.XToScreenExact(float3, float4, float9, 0);
		float float13 = IsoUtils.YToScreenExact(float3, float4, float9, 0);
		LineDrawer.drawLine(float10, float11, float12, float13, float5, float6, float7, float8, int1);
	}

	private void drawGrid() {
		float float1 = IsoUtils.XToIso(-128.0F, -256.0F, 0.0F);
		float float2 = IsoUtils.YToIso((float)(Core.getInstance().getOffscreenWidth(IsoPlayer.getPlayerIndex()) + 128), -256.0F, 0.0F);
		float float3 = IsoUtils.XToIso((float)(Core.getInstance().getOffscreenWidth(IsoPlayer.getPlayerIndex()) + 128), (float)(Core.getInstance().getOffscreenHeight(IsoPlayer.getPlayerIndex()) + 256), 6.0F);
		float float4 = IsoUtils.YToIso(-128.0F, (float)(Core.getInstance().getOffscreenHeight(IsoPlayer.getPlayerIndex()) + 256), 6.0F);
		int int1 = (int)float2;
		int int2 = (int)float4;
		int int3 = (int)float1;
		int int4 = (int)float3;
		int3 -= 2;
		int1 -= 2;
		int int5;
		for (int5 = int1; int5 <= int2; ++int5) {
			if (int5 % 10 == 0) {
				this.DrawIsoLine((float)int3, (float)int5, (float)int4, (float)int5, 1.0F, 1.0F, 1.0F, 0.5F, 1);
			}
		}

		for (int5 = int3; int5 <= int4; ++int5) {
			if (int5 % 10 == 0) {
				this.DrawIsoLine((float)int5, (float)int1, (float)int5, (float)int2, 1.0F, 1.0F, 1.0F, 0.5F, 1);
			}
		}

		if (GameClient.bClient) {
			for (int5 = int1; int5 <= int2; ++int5) {
				if (int5 % 70 == 0) {
					this.DrawIsoLine((float)int3, (float)int5, (float)int4, (float)int5, 1.0F, 0.0F, 0.0F, 0.5F, 1);
				}
			}

			for (int5 = int3; int5 <= int4; ++int5) {
				if (int5 % 70 == 0) {
					this.DrawIsoLine((float)int5, (float)int1, (float)int5, (float)int2, 1.0F, 0.0F, 0.0F, 0.5F, 1);
				}
			}
		}
	}

	private void drawCursor() {
		int int1 = Core.TileScale;
		float float1 = (float)((int)IsoPlayer.instance.getZ());
		int int2 = (int)IsoUtils.XToScreenExact((float)this.gridX, (float)(this.gridY + 1), float1, 0);
		int int3 = (int)IsoUtils.YToScreenExact((float)this.gridX, (float)(this.gridY + 1), float1, 0);
		SpriteRenderer.instance.renderPoly(int2, int3, int2 + 32 * int1, int3 - 16 * int1, int2 + 64 * int1, int3, int2 + 32 * int1, int3 + 16 * int1, 0.0F, 0.0F, 1.0F, 0.5F);
		IsoChunkMap chunkMap = IsoWorld.instance.getCell().ChunkMap[IsoPlayer.getPlayerIndex()];
		int int4;
		for (int4 = chunkMap.getWorldYMinTiles(); int4 < chunkMap.getWorldYMaxTiles(); ++int4) {
			for (int int5 = chunkMap.getWorldXMinTiles(); int5 < chunkMap.getWorldXMaxTiles(); ++int5) {
				IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare((double)int5, (double)int4, (double)float1);
				if (square != null) {
					if (square != chunkMap.getGridSquare(int5, int4, (int)float1)) {
						int2 = (int)IsoUtils.XToScreenExact((float)int5, (float)(int4 + 1), float1, 0);
						int3 = (int)IsoUtils.YToScreenExact((float)int5, (float)(int4 + 1), float1, 0);
						SpriteRenderer.instance.renderPoly(int2, int3, int2 + 32, int3 - 16, int2 + 64, int3, int2 + 32, int3 + 16, 1.0F, 0.0F, 0.0F, 0.8F);
					}

					if (square == null || square.getX() != int5 || square.getY() != int4 || (float)square.getZ() != float1 || square.e != null && square.e.w != null && square.e.w != square || square.w != null && square.w.e != null && square.w.e != square || square.n != null && square.n.s != null && square.n.s != square || square.s != null && square.s.n != null && square.s.n != square || square.nw != null && square.nw.se != null && square.nw.se != square || square.se != null && square.se.nw != null && square.se.nw != square) {
						int2 = (int)IsoUtils.XToScreenExact((float)int5, (float)(int4 + 1), float1, 0);
						int3 = (int)IsoUtils.YToScreenExact((float)int5, (float)(int4 + 1), float1, 0);
						SpriteRenderer.instance.renderPoly(int2, int3, int2 + 32, int3 - 16, int2 + 64, int3, int2 + 32, int3 + 16, 1.0F, 0.0F, 0.0F, 0.5F);
					}

					if (square != null) {
						IsoGridSquare square2 = square.testPathFindAdjacent((IsoMovingObject)null, -1, 0, 0) ? null : square.nav[IsoDirections.W.index()];
						IsoGridSquare square3 = square.testPathFindAdjacent((IsoMovingObject)null, 0, -1, 0) ? null : square.nav[IsoDirections.N.index()];
						IsoGridSquare square4 = square.testPathFindAdjacent((IsoMovingObject)null, 1, 0, 0) ? null : square.nav[IsoDirections.E.index()];
						IsoGridSquare square5 = square.testPathFindAdjacent((IsoMovingObject)null, 0, 1, 0) ? null : square.nav[IsoDirections.S.index()];
						IsoGridSquare square6 = square.testPathFindAdjacent((IsoMovingObject)null, -1, -1, 0) ? null : square.nav[IsoDirections.NW.index()];
						IsoGridSquare square7 = square.testPathFindAdjacent((IsoMovingObject)null, 1, -1, 0) ? null : square.nav[IsoDirections.NE.index()];
						IsoGridSquare square8 = square.testPathFindAdjacent((IsoMovingObject)null, -1, 1, 0) ? null : square.nav[IsoDirections.SW.index()];
						IsoGridSquare square9 = square.testPathFindAdjacent((IsoMovingObject)null, 1, 1, 0) ? null : square.nav[IsoDirections.SE.index()];
						if (square2 != square.w || square3 != square.n || square4 != square.e || square5 != square.s || square6 != square.nw || square7 != square.ne || square8 != square.sw || square9 != square.se) {
							this.paintSquare(int5, int4, (int)float1, 1.0F, 0.0F, 0.0F, 0.5F);
						}
					}

					if (square != null && (square.nav[IsoDirections.NW.index()] != null && square.nav[IsoDirections.NW.index()].nav[IsoDirections.SE.index()] != square || square.nav[IsoDirections.NE.index()] != null && square.nav[IsoDirections.NE.index()].nav[IsoDirections.SW.index()] != square || square.nav[IsoDirections.SW.index()] != null && square.nav[IsoDirections.SW.index()].nav[IsoDirections.NE.index()] != square || square.nav[IsoDirections.SE.index()] != null && square.nav[IsoDirections.SE.index()].nav[IsoDirections.NW.index()] != square || square.nav[IsoDirections.N.index()] != null && square.nav[IsoDirections.N.index()].nav[IsoDirections.S.index()] != square || square.nav[IsoDirections.S.index()] != null && square.nav[IsoDirections.S.index()].nav[IsoDirections.N.index()] != square || square.nav[IsoDirections.W.index()] != null && square.nav[IsoDirections.W.index()].nav[IsoDirections.E.index()] != square || square.nav[IsoDirections.E.index()] != null && square.nav[IsoDirections.E.index()].nav[IsoDirections.W.index()] != square)) {
						int2 = (int)IsoUtils.XToScreenExact((float)int5, (float)(int4 + 1), float1, 0);
						int3 = (int)IsoUtils.YToScreenExact((float)int5, (float)(int4 + 1), float1, 0);
						SpriteRenderer.instance.renderPoly(int2, int3, int2 + 32, int3 - 16, int2 + 64, int3, int2 + 32, int3 + 16, 1.0F, 0.0F, 0.0F, 0.5F);
					}

					if (square.getObjects().isEmpty()) {
						this.paintSquare(int5, int4, (int)float1, 1.0F, 1.0F, 0.0F, 0.5F);
					}

					if (square.getRoom() != null && square.isFree(false) && !VirtualZombieManager.instance.canSpawnAt(int5, int4, (int)float1)) {
						this.paintSquare(int5, int4, (int)float1, 1.0F, 1.0F, 1.0F, 1.0F);
					}

					if (square.roofHideBuilding != null) {
						this.paintSquare(int5, int4, (int)float1, 0.0F, 0.0F, 1.0F, 0.25F);
					}
				}
			}
		}

		if (Math.abs(this.gridX - (int)IsoPlayer.instance.x) <= 1 && Math.abs(this.gridY - (int)IsoPlayer.instance.y) <= 1) {
			IsoGridSquare square10 = IsoWorld.instance.CurrentCell.getGridSquare(this.gridX, this.gridY, (int)IsoPlayer.instance.z);
			IsoObject object = IsoPlayer.instance.getCurrentSquare().testCollideSpecialObjects(square10);
			if (object != null) {
				object.getSprite().RenderGhostTileRed((int)object.getX(), (int)object.getY(), (int)object.getZ());
			}
		}

		this.lineClearCached(IsoWorld.instance.CurrentCell, this.gridX, this.gridY, (int)float1, (int)IsoPlayer.instance.getX(), (int)IsoPlayer.instance.getY(), (int)IsoPlayer.instance.getZ(), false);
		for (int4 = 0; int4 < IsoWorld.instance.CurrentCell.getVehicles().size(); ++int4) {
			BaseVehicle baseVehicle = (BaseVehicle)IsoWorld.instance.CurrentCell.getVehicles().get(int4);
			for (int int6 = -4; int6 <= 4; ++int6) {
				for (int int7 = -4; int7 <= 4; ++int7) {
					if (baseVehicle.isIntersectingSquare((int)baseVehicle.getX() + int7, (int)baseVehicle.getY() + int6, (int)baseVehicle.getZ())) {
						this.paintSquare((int)baseVehicle.getX() + int7, (int)baseVehicle.getY() + int6, (int)baseVehicle.getZ(), 1.0F, 0.0F, 0.0F, 0.5F);
					}
				}
			}
		}
	}

	private void DrawBehindStuff() {
		this.IsBehindStuff(IsoPlayer.instance.getCurrentSquare());
	}

	private boolean IsBehindStuff(IsoGridSquare square) {
		for (int int1 = 1; int1 < 8 && square.getZ() + int1 < 8; ++int1) {
			for (int int2 = -5; int2 <= 6; ++int2) {
				for (int int3 = -5; int3 <= 6; ++int3) {
					if (int3 >= int2 - 5 && int3 <= int2 + 5) {
						this.paintSquare(square.getX() + int3 + int1 * 3, square.getY() + int2 + int1 * 3, square.getZ() + int1, 1.0F, 1.0F, 0.0F, 0.25F);
					}
				}
			}
		}

		return true;
	}

	private boolean IsBehindStuffRecY(int int1, int int2, int int3) {
		IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (int3 >= 15) {
			return false;
		} else {
			this.paintSquare(int1, int2, int3, 1.0F, 1.0F, 0.0F, 0.25F);
			return this.IsBehindStuffRecY(int1, int2 + 1, int3 + 1);
		}
	}

	private boolean IsBehindStuffRecXY(int int1, int int2, int int3, int int4) {
		IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (int3 >= 15) {
			return false;
		} else {
			this.paintSquare(int1, int2, int3, 1.0F, 1.0F, 0.0F, 0.25F);
			return this.IsBehindStuffRecXY(int1 + int4, int2 + int4, int3 + 1, int4);
		}
	}

	private boolean IsBehindStuffRecX(int int1, int int2, int int3) {
		IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (int3 >= 15) {
			return false;
		} else {
			this.paintSquare(int1, int2, int3, 1.0F, 1.0F, 0.0F, 0.25F);
			return this.IsBehindStuffRecX(int1 + 1, int2, int3 + 1);
		}
	}

	private void paintSquare(int int1, int int2, int int3, float float1, float float2, float float3, float float4) {
		int int4 = Core.TileScale;
		int int5 = (int)IsoUtils.XToScreenExact((float)int1, (float)(int2 + 1), (float)int3, 0);
		int int6 = (int)IsoUtils.YToScreenExact((float)int1, (float)(int2 + 1), (float)int3, 0);
		SpriteRenderer.instance.renderPoly(int5, int6, int5 + 32 * int4, int6 - 16 * int4, int5 + 64 * int4, int6, int5 + 32 * int4, int6 + 16 * int4, float1, float2, float3, float4);
	}

	void drawModData() {
		int int1 = (int)IsoPlayer.instance.getZ();
		IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, int1);
		int int2 = Core.getInstance().getScreenWidth() - 250;
		int int3 = 10;
		int int4 = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
		if (GameClient.bClient && square != null) {
			this.DrawString(int2, int3 += int4, "randomID=" + square.chunk.randomID);
		}

		if (square != null && square.getModData() != null) {
			KahluaTable kahluaTable = square.getModData();
			this.DrawString(int2, int3 += int4, "MOD DATA x,y,z=" + square.getX() + "," + square.getY() + "," + square.getZ());
			KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
			label65: while (true) {
				do {
					if (!kahluaTableIterator.advance()) {
						int3 += int4;
						break label65;
					}

					this.DrawString(int2, int3 += int4, kahluaTableIterator.getKey().toString() + " = " + kahluaTableIterator.getValue().toString());
				}		 while (!(kahluaTableIterator.getValue() instanceof KahluaTable));

				KahluaTableIterator kahluaTableIterator2 = ((KahluaTable)kahluaTableIterator.getValue()).iterator();
				while (kahluaTableIterator2.advance()) {
					this.DrawString(int2 + 8, int3 += int4, kahluaTableIterator2.getKey().toString() + " = " + kahluaTableIterator2.getValue().toString());
				}
			}
		}

		if (square != null) {
			PropertyContainer propertyContainer = square.getProperties();
			ArrayList arrayList = propertyContainer.getPropertyNames();
			if (!arrayList.isEmpty()) {
				this.DrawString(int2, int3 += int4, "PROPERTIES x,y,z=" + square.getX() + "," + square.getY() + "," + square.getZ());
				Collections.sort(arrayList);
				Iterator iterator = arrayList.iterator();
				while (iterator.hasNext()) {
					String string = (String)iterator.next();
					this.DrawString(int2, int3 += int4, string + " = \"" + propertyContainer.Val(string) + "\"");
				}
			}

			IsoFlagType[] flagTypeArray = IsoFlagType.values();
			int int5 = flagTypeArray.length;
			for (int int6 = 0; int6 < int5; ++int6) {
				IsoFlagType flagType = flagTypeArray[int6];
				if (propertyContainer.Is(flagType)) {
					this.DrawString(int2, int3 += int4, flagType.toString());
				}
			}
		}

		if (square != null) {
			ErosionData.Square square2 = square.getErosionData();
			if (square2 != null) {
				int3 += int4;
				this.DrawString(int2, int3 += int4, "EROSION x,y,z=" + square.getX() + "," + square.getY() + "," + square.getZ());
				this.DrawString(int2, int3 += int4, "init=" + square2.init);
				this.DrawString(int2, int3 += int4, "doNothing=" + square2.doNothing);
				this.DrawString(int2, int3 + int4, "chunk.init=" + square.chunk.getErosionData().init);
			}
		}
	}

	void drawPlayerInfo() {
		int int1 = Core.getInstance().getScreenWidth() - 250;
		int int2 = Core.getInstance().getScreenHeight() / 2;
		int int3 = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
		this.DrawString(int1, int2 += int3, "bored = " + IsoPlayer.instance.getBodyDamage().getBoredomLevel());
		this.DrawString(int1, int2 += int3, "endurance = " + IsoPlayer.instance.getStats().endurance);
		this.DrawString(int1, int2 += int3, "fatigue = " + IsoPlayer.instance.getStats().fatigue);
		this.DrawString(int1, int2 += int3, "hunger = " + IsoPlayer.instance.getStats().hunger);
		this.DrawString(int1, int2 += int3, "pain = " + IsoPlayer.instance.getStats().Pain);
		this.DrawString(int1, int2 += int3, "panic = " + IsoPlayer.instance.getStats().Panic);
		this.DrawString(int1, int2 += int3, "stress = " + IsoPlayer.instance.getStats().getStress());
		this.DrawString(int1, int2 += int3, "clothingTemp = " + IsoPlayer.instance.getPlayerClothingTemperature());
		this.DrawString(int1, int2 += int3, "temperature = " + IsoPlayer.instance.getTemperature());
		this.DrawString(int1, int2 += int3, "thirst = " + IsoPlayer.instance.getStats().thirst);
		this.DrawString(int1, int2 += int3, "foodPoison = " + IsoPlayer.instance.getBodyDamage().getFoodSicknessLevel());
		this.DrawString(int1, int2 += int3, "poison = " + IsoPlayer.instance.getBodyDamage().getPoisonLevel());
		this.DrawString(int1, int2 += int3, "unhappy = " + IsoPlayer.instance.getBodyDamage().getUnhappynessLevel());
		this.DrawString(int1, int2 += int3, "infected = " + IsoPlayer.instance.getBodyDamage().isInfected());
		this.DrawString(int1, int2 += int3, "InfectionLevel = " + IsoPlayer.instance.getBodyDamage().getInfectionLevel());
		this.DrawString(int1, int2 += int3, "FakeInfectionLevel = " + IsoPlayer.instance.getBodyDamage().getFakeInfectionLevel());
		int2 += int3;
		this.DrawString(int1, int2 += int3, "WORLD");
		this.DrawString(int1, int2 + int3, "globalTemperature = " + IsoWorld.instance.getGlobalTemperature());
	}

	public LosUtil.TestResults lineClearCached(IsoCell cell, int int1, int int2, int int3, int int4, int int5, int int6, boolean boolean1) {
		int int7 = int2 - int5;
		int int8 = int1 - int4;
		int int9 = int3 - int6;
		int int10 = int8 + 100;
		int int11 = int7 + 100;
		int int12 = int9 + 16;
		if (int10 >= 0 && int11 >= 0 && int12 >= 0 && int10 < 200 && int11 < 200) {
			LosUtil.TestResults testResults = LosUtil.TestResults.Clear;
			byte byte1 = 1;
			float float1 = 0.5F;
			float float2 = 0.5F;
			IsoGridSquare square = cell.getGridSquare(int4, int5, int6);
			int int13;
			int int14;
			float float3;
			float float4;
			IsoGridSquare square2;
			if (Math.abs(int8) > Math.abs(int7) && Math.abs(int8) > Math.abs(int9)) {
				float3 = (float)int7 / (float)int8;
				float4 = (float)int9 / (float)int8;
				float1 += (float)int5;
				float2 += (float)int6;
				int8 = int8 < 0 ? -1 : 1;
				float3 *= (float)int8;
				for (float4 *= (float)int8; int4 != int1; int14 = (int)float2) {
					int4 += int8;
					float1 += float3;
					float2 += float4;
					square2 = cell.getGridSquare(int4, (int)float1, (int)float2);
					this.paintSquare(int4, (int)float1, (int)float2, 1.0F, 1.0F, 1.0F, 0.5F);
					if (square2 != null && square != null && square2.testVisionAdjacent(square.getX() - square2.getX(), square.getY() - square2.getY(), square.getZ() - square2.getZ(), true, boolean1) == LosUtil.TestResults.Blocked) {
						this.paintSquare(int4, (int)float1, (int)float2, 1.0F, 0.0F, 0.0F, 0.5F);
						this.paintSquare(square.getX(), square.getY(), square.getZ(), 1.0F, 0.0F, 0.0F, 0.5F);
						byte1 = 4;
					}

					square = square2;
					int13 = (int)float1;
				}
			} else {
				int int15;
				if (Math.abs(int7) >= Math.abs(int8) && Math.abs(int7) > Math.abs(int9)) {
					float3 = (float)int8 / (float)int7;
					float4 = (float)int9 / (float)int7;
					float1 += (float)int4;
					float2 += (float)int6;
					int7 = int7 < 0 ? -1 : 1;
					float3 *= (float)int7;
					for (float4 *= (float)int7; int5 != int2; int14 = (int)float2) {
						int5 += int7;
						float1 += float3;
						float2 += float4;
						square2 = cell.getGridSquare((int)float1, int5, (int)float2);
						this.paintSquare((int)float1, int5, (int)float2, 1.0F, 1.0F, 1.0F, 0.5F);
						if (square2 != null && square != null && square2.testVisionAdjacent(square.getX() - square2.getX(), square.getY() - square2.getY(), square.getZ() - square2.getZ(), true, boolean1) == LosUtil.TestResults.Blocked) {
							this.paintSquare((int)float1, int5, (int)float2, 1.0F, 0.0F, 0.0F, 0.5F);
							this.paintSquare(square.getX(), square.getY(), square.getZ(), 1.0F, 0.0F, 0.0F, 0.5F);
							byte1 = 4;
						}

						square = square2;
						int15 = (int)float1;
					}
				} else {
					float3 = (float)int8 / (float)int9;
					float4 = (float)int7 / (float)int9;
					float1 += (float)int4;
					float2 += (float)int5;
					int9 = int9 < 0 ? -1 : 1;
					float3 *= (float)int9;
					for (float4 *= (float)int9; int6 != int3; int13 = (int)float2) {
						int6 += int9;
						float1 += float3;
						float2 += float4;
						square2 = cell.getGridSquare((int)float1, (int)float2, int6);
						this.paintSquare((int)float1, (int)float2, int6, 1.0F, 1.0F, 1.0F, 0.5F);
						if (square2 != null && square != null && square2.testVisionAdjacent(square.getX() - square2.getX(), square.getY() - square2.getY(), square.getZ() - square2.getZ(), true, boolean1) == LosUtil.TestResults.Blocked) {
							byte1 = 4;
						}

						square = square2;
						int15 = (int)float1;
					}
				}
			}

			if (byte1 == 1) {
				return LosUtil.TestResults.Clear;
			} else if (byte1 == 2) {
				return LosUtil.TestResults.ClearThroughOpenDoor;
			} else if (byte1 == 3) {
				return LosUtil.TestResults.ClearThroughWindow;
			} else {
				return byte1 == 4 ? LosUtil.TestResults.Blocked : LosUtil.TestResults.Blocked;
			}
		} else {
			return LosUtil.TestResults.Blocked;
		}
	}

	private void DrawString(int int1, int int2, String string) {
		int int3 = TextManager.instance.MeasureStringX(this.FONT, string);
		int int4 = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
		SpriteRenderer.instance.render((Texture)null, int1 - 1, int2, int3 + 2, int4, 0.0F, 0.0F, 0.0F, 0.8F);
		TextManager.instance.DrawString(this.FONT, (double)int1, (double)int2, string, 1.0, 1.0, 1.0, 1.0);
	}

	private class FloodFill {
		private IsoGridSquare start = null;
		private final int FLOOD_SIZE = 11;
		private BooleanGrid visited = new BooleanGrid(11, 11);
		private Stack stack = new Stack();
		private IsoBuilding building = null;
		private Mover mover = null;

		void calculate(Mover mover, IsoGridSquare square) {
			this.start = square;
			this.mover = mover;
			if (this.start.getRoom() != null) {
				this.building = this.start.getRoom().getBuilding();
			}

			boolean boolean1 = false;
			boolean boolean2 = false;
			if (this.push(this.start.getX(), this.start.getY())) {
				while ((square = this.pop()) != null) {
					int int1 = square.getX();
					int int2;
					for (int2 = square.getY(); this.shouldVisit(int1, int2, int1, int2 - 1); --int2) {
					}

					boolean2 = false;
					boolean1 = false;
					while (true) {
						this.visited.setValue(this.gridX(int1), this.gridY(int2), true);
						if (!boolean1 && this.shouldVisit(int1, int2, int1 - 1, int2)) {
							if (!this.push(int1 - 1, int2)) {
								return;
							}

							boolean1 = true;
						} else if (boolean1 && !this.shouldVisit(int1, int2, int1 - 1, int2)) {
							boolean1 = false;
						} else if (boolean1 && !this.shouldVisit(int1 - 1, int2, int1 - 1, int2 - 1) && !this.push(int1 - 1, int2)) {
							return;
						}

						if (!boolean2 && this.shouldVisit(int1, int2, int1 + 1, int2)) {
							if (!this.push(int1 + 1, int2)) {
								return;
							}

							boolean2 = true;
						} else if (boolean2 && !this.shouldVisit(int1, int2, int1 + 1, int2)) {
							boolean2 = false;
						} else if (boolean2 && !this.shouldVisit(int1 + 1, int2, int1 + 1, int2 - 1) && !this.push(int1 + 1, int2)) {
							return;
						}

						++int2;
						if (!this.shouldVisit(int1, int2 - 1, int1, int2)) {
							break;
						}
					}
				}
			}
		}

		boolean shouldVisit(int int1, int int2, int int3, int int4) {
			if (this.gridX(int3) < 11 && this.gridX(int3) >= 0) {
				if (this.gridY(int4) < 11 && this.gridY(int4) >= 0) {
					if (this.visited.getValue(this.gridX(int3), this.gridY(int4))) {
						return false;
					} else {
						IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int3, int4, this.start.getZ());
						if (square == null) {
							return false;
						} else if (!square.Has(IsoObjectType.stairsBN) && !square.Has(IsoObjectType.stairsMN) && !square.Has(IsoObjectType.stairsTN)) {
							if (!square.Has(IsoObjectType.stairsBW) && !square.Has(IsoObjectType.stairsMW) && !square.Has(IsoObjectType.stairsTW)) {
								if (square.getRoom() != null && this.building == null) {
									return false;
								} else if (square.getRoom() == null && this.building != null) {
									return false;
								} else {
									return !IsoWorld.instance.CurrentCell.blocked(this.mover, int3, int4, this.start.getZ(), int1, int2, this.start.getZ());
								}
							} else {
								return false;
							}
						} else {
							return false;
						}
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		boolean push(int int1, int int2) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, this.start.getZ());
			this.stack.push(square);
			return true;
		}

		IsoGridSquare pop() {
			return this.stack.isEmpty() ? null : (IsoGridSquare)this.stack.pop();
		}

		int gridX(int int1) {
			return int1 - (this.start.getX() - 5);
		}

		int gridY(int int1) {
			return int1 - (this.start.getY() - 5);
		}

		int gridX(IsoGridSquare square) {
			return square.getX() - (this.start.getX() - 5);
		}

		int gridY(IsoGridSquare square) {
			return square.getY() - (this.start.getY() - 5);
		}

		void draw() {
			int int1 = this.start.getX() - 5;
			int int2 = this.start.getY() - 5;
			for (int int3 = 0; int3 < 11; ++int3) {
				for (int int4 = 0; int4 < 11; ++int4) {
					if (this.visited.getValue(int4, int3)) {
						int int5 = (int)IsoUtils.XToScreenExact((float)(int1 + int4), (float)(int2 + int3 + 1), (float)this.start.getZ(), 0);
						int int6 = (int)IsoUtils.YToScreenExact((float)(int1 + int4), (float)(int2 + int3 + 1), (float)this.start.getZ(), 0);
						SpriteRenderer.instance.renderPoly(int5, int6, int5 + 32, int6 - 16, int5 + 64, int6, int5 + 32, int6 + 16, 1.0F, 1.0F, 0.0F, 0.5F);
					}
				}
			}
		}
	}
}
