package zombie.network;

import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.core.opengl.RenderThread;
import zombie.core.physics.WorldSimulation;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureID;
import zombie.core.textures.TexturePackPage;
import zombie.debug.LineDrawer;
import zombie.gameStates.MainScreenState;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWindow;
import zombie.iso.sprite.IsoSprite;
import zombie.ui.TextManager;
import zombie.vehicles.BaseVehicle;


public class ServerGUI {
	private static boolean created;
	private static int minX;
	private static int minY;
	private static int maxX;
	private static int maxY;
	private static int maxZ;
	private static final ArrayList GridStack = new ArrayList();
	private static final ArrayList MinusFloorCharacters = new ArrayList(1000);
	private static final ArrayList SolidFloor = new ArrayList(5000);
	private static final ArrayList VegetationCorpses = new ArrayList(5000);
	private static final ColorInfo defColorInfo = new ColorInfo();

	public static boolean isCreated() {
		return created;
	}

	public static void init() {
		created = true;
		try {
			Display.setFullscreen(false);
			Display.setResizable(false);
			Display.setVSyncEnabled(false);
			Display.setTitle("Project Zomboid Server");
			System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
			Core.width = 1366;
			Core.height = 768;
			Display.setDisplayMode(new DisplayMode(Core.width, Core.height));
			Display.create(new PixelFormat(32, 0, 24, 8, 0));
			Display.setIcon(MainScreenState.loadIcons());
			GLVertexBufferObject.init();
			TextureID.bUseCompression = false;
			TexturePackPage.bIgnoreWorldItemTextures = true;
			GameWindow.LoadTexturePack("UI");
			GameWindow.LoadTexturePack("UI2");
			GameWindow.LoadTexturePack("IconsMoveables");
			GameWindow.LoadTexturePack("RadioIcons");
			GameWindow.LoadTexturePack("ApComUI");
			GameWindow.LoadTexturePack("WeatherFx");
			TexturePackPage.bIgnoreWorldItemTextures = false;
			SpriteRenderer.instance = new SpriteRenderer();
			SpriteRenderer.instance.create();
			TextManager.instance.Init();
			GameWindow.LoadTexturePack("Tiles2x");
			GameWindow.LoadTexturePack("JumboTrees2x");
			TextureID.bUseCompression = false;
			GameWindow.LoadTexturePack("Tiles2x.floor");
			TextureID.bUseCompression = TextureID.bUseCompressionOption;
			GameWindow.LoadTexturePackDDS("Characters");
			IsoObjectPicker.Instance.Init();
			GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
			Display.releaseContext();
			RenderThread.init();
			Core.getInstance().initFBOs();
			ModelManager.instance.create();
		} catch (Exception exception) {
			exception.printStackTrace();
			created = false;
		}
	}

	public static void init2() {
		if (created) {
			BaseVehicle.LoadAllVehicleTextures();
		}
	}

	public static void shutdown() {
		if (created) {
			RenderThread.borrowContext();
			Display.destroy();
		}
	}

	public static void update() {
		if (created) {
			Mouse.update();
			GameKeyboard.update();
			Display.processMessages();
			if (Display.isCloseRequested()) {
			}

			if (Core.getInstance().OffscreenBuffer.Current != null) {
				Core.getInstance().OffscreenBuffer.zoom[0] = 2.0F;
				Core.getInstance().OffscreenBuffer.targetZoom[0] = 2.0F;
			}

			byte byte1 = 0;
			Core.getInstance().StartFrame(byte1, true);
			renderWorld();
			Core.getInstance().EndFrame(byte1);
			Core.getInstance().RenderOffScreenBuffer();
			Core.getInstance().StartFrameUI();
			renderUI();
			Core.getInstance().EndFrameUI();
		}
	}

	private static IsoPlayer getPlayerToFollow() {
		for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
			if (udpConnection.isFullyConnected()) {
				for (int int2 = 0; int2 < 4; ++int2) {
					IsoPlayer player = udpConnection.players[int2];
					if (player != null && player.OnlineID != -1) {
						return player;
					}
				}
			}
		}

		return null;
	}

	private static void updateCamera(IsoPlayer player) {
		byte byte1 = 0;
		float float1 = IsoUtils.XToScreen(player.x + IsoCamera.DeferedX[byte1], player.y + IsoCamera.DeferedY[byte1], player.z, 0);
		float float2 = IsoUtils.YToScreen(player.x + IsoCamera.DeferedX[byte1], player.y + IsoCamera.DeferedY[byte1], player.z, 0);
		float1 -= (float)(IsoCamera.getOffscreenWidth(byte1) / 2);
		float2 -= (float)(IsoCamera.getOffscreenHeight(byte1) / 2);
		float2 -= player.getOffsetY() * 1.5F;
		float1 = (float)((int)float1);
		float2 = (float)((int)float2);
		float1 += (float)IsoCamera.PLAYER_OFFSET_X;
		float2 += (float)IsoCamera.PLAYER_OFFSET_Y;
		IsoCamera.OffX[IsoPlayer.getPlayerIndex()] = float1;
		IsoCamera.OffY[IsoPlayer.getPlayerIndex()] = float2;
		IsoCamera.FrameState frameState = IsoCamera.frameState;
		frameState.Paused = false;
		frameState.playerIndex = byte1;
		frameState.CamCharacter = player;
		frameState.CamCharacterX = IsoCamera.CamCharacter.getX();
		frameState.CamCharacterY = IsoCamera.CamCharacter.getY();
		frameState.CamCharacterZ = IsoCamera.CamCharacter.getZ();
		frameState.CamCharacterSquare = IsoCamera.CamCharacter.getCurrentSquare();
		frameState.CamCharacterRoom = frameState.CamCharacterSquare == null ? null : frameState.CamCharacterSquare.getRoom();
		frameState.OffX = IsoCamera.getOffX();
		frameState.OffY = IsoCamera.getOffY();
		frameState.OffscreenWidth = IsoCamera.getOffscreenWidth(byte1);
		frameState.OffscreenHeight = IsoCamera.getOffscreenHeight(byte1);
	}

	private static void renderWorld() {
		IsoPlayer player = getPlayerToFollow();
		if (player != null) {
			byte byte1 = 0;
			IsoPlayer.instance = player;
			IsoPlayer.players[0] = player;
			IsoCamera.CamCharacter = player;
			updateCamera(player);
			IsoSprite.globalOffsetX = -1;
			byte byte2 = 0;
			byte byte3 = 0;
			int int1 = byte2 + IsoCamera.getOffscreenWidth(byte1);
			int int2 = byte3 + IsoCamera.getOffscreenHeight(byte1);
			float float1 = IsoUtils.XToIso((float)byte2, (float)byte3, 0.0F);
			float float2 = IsoUtils.YToIso((float)int1, (float)byte3, 0.0F);
			float float3 = IsoUtils.XToIso((float)int1, (float)int2, 6.0F);
			float float4 = IsoUtils.YToIso((float)byte2, (float)int2, 6.0F);
			minY = (int)float2;
			maxY = (int)float4;
			minX = (int)float1;
			maxX = (int)float3;
			minX -= 2;
			minY -= 2;
			maxZ = (int)player.getZ();
			IsoCell cell = IsoWorld.instance.CurrentCell;
			cell.DrawStencilMask();
			IsoObjectPicker.Instance.StartRender();
			RenderTiles();
			int int3;
			for (int3 = 0; int3 < cell.getObjectList().size(); ++int3) {
				IsoMovingObject movingObject = (IsoMovingObject)cell.getObjectList().get(int3);
				movingObject.renderlast();
			}

			for (int3 = 0; int3 < cell.getStaticUpdaterObjectList().size(); ++int3) {
				IsoObject object = (IsoObject)cell.getStaticUpdaterObjectList().get(int3);
				object.renderlast();
			}

			if (WorldSimulation.instance.created) {
				TextureDraw.GenericDrawer genericDrawer = WorldSimulation.getDrawer(byte1);
				SpriteRenderer.instance.drawGeneric(genericDrawer);
			}

			WorldSoundManager.instance.render();
			LineDrawer.drawLines();
		}
	}

	private static void RenderTiles() {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		if (IsoCell.perPlayerRender[0] == null) {
			IsoCell.perPlayerRender[0] = new IsoCell.PerPlayerRender();
		}

		IsoCell.PerPlayerRender perPlayerRender = IsoCell.perPlayerRender[0];
		if (perPlayerRender == null) {
			IsoCell.perPlayerRender[0] = new IsoCell.PerPlayerRender();
		}

		perPlayerRender.setSize(maxX - minX + 1, maxY - minY + 1);
		short[][][] shortArrayArrayArray = perPlayerRender.StencilValues;
		for (int int1 = 0; int1 <= maxZ; ++int1) {
			GridStack.clear();
			int int2;
			int int3;
			label104: for (int2 = minY; int2 < maxY; ++int2) {
				int int4 = minX;
				IsoGridSquare square = ServerMap.instance.getGridSquare(int4, int2, int1);
				int3 = IsoDirections.E.index();
				while (true) {
					while (true) {
						if (int4 >= maxX) {
							continue label104;
						}

						if (int1 == 0) {
							shortArrayArrayArray[int4 - minX][int2 - minY][0] = 0;
							shortArrayArrayArray[int4 - minX][int2 - minY][1] = 0;
						}

						if (square != null && square.getY() != int2) {
							square = null;
						}

						if (square == null) {
							square = ServerMap.instance.getGridSquare(int4, int2, int1);
							if (square == null) {
								++int4;
								continue;
							}
						}

						IsoChunk chunk = square.getChunk();
						if (chunk != null && square.IsOnScreen()) {
							GridStack.add(square);
						}

						square = square.nav[int3];
						++int4;
					}
				}
			}

			SolidFloor.clear();
			VegetationCorpses.clear();
			MinusFloorCharacters.clear();
			IsoGridSquare square2;
			for (int2 = 0; int2 < GridStack.size(); ++int2) {
				square2 = (IsoGridSquare)GridStack.get(int2);
				square2.setLightInfoServerGUIOnly(defColorInfo);
				int int5 = renderFloor(square2);
				if (!square2.getStaticMovingObjects().isEmpty()) {
					int5 |= 2;
				}

				for (int3 = 0; int3 < square2.getMovingObjects().size(); ++int3) {
					IsoMovingObject movingObject = (IsoMovingObject)square2.getMovingObjects().get(int3);
					boolean boolean1 = movingObject.isOnFloor();
					if (boolean1 && movingObject instanceof IsoZombie) {
						IsoZombie zombie = (IsoZombie)movingObject;
						boolean1 = zombie.bCrawling || zombie.legsSprite.CurrentAnim != null && zombie.legsSprite.CurrentAnim.name.equals("ZombieDeath") && zombie.def.isFinished();
					}

					if (boolean1) {
						int5 |= 2;
					} else {
						int5 |= 4;
					}
				}

				if ((int5 & 1) != 0) {
					SolidFloor.add(square2);
				}

				if ((int5 & 2) != 0) {
					VegetationCorpses.add(square2);
				}

				if ((int5 & 4) != 0) {
					MinusFloorCharacters.add(square2);
				}
			}

			LuaEventManager.triggerEvent("OnPostFloorLayerDraw", int1);
			for (int2 = 0; int2 < VegetationCorpses.size(); ++int2) {
				square2 = (IsoGridSquare)VegetationCorpses.get(int2);
				renderMinusFloor(square2, false, true);
				renderCharacters(square2, true);
			}

			for (int2 = 0; int2 < MinusFloorCharacters.size(); ++int2) {
				square2 = (IsoGridSquare)MinusFloorCharacters.get(int2);
				boolean boolean2 = renderMinusFloor(square2, false, false);
				renderCharacters(square2, false);
				if (boolean2) {
					renderMinusFloor(square2, true, false);
				}
			}
		}

		MinusFloorCharacters.clear();
		SolidFloor.clear();
		VegetationCorpses.clear();
	}

	private static int renderFloor(IsoGridSquare square) {
		int int1 = 0;
		byte byte1 = 0;
		for (int int2 = 0; int2 < square.getObjects().size(); ++int2) {
			IsoObject object = (IsoObject)square.getObjects().get(int2);
			boolean boolean1 = true;
			if (object.sprite != null && !object.sprite.Properties.Is(IsoFlagType.solidfloor)) {
				boolean1 = false;
				int1 |= 4;
			}

			if (boolean1) {
				IndieGL.glAlphaFunc(516, 0.0F);
				object.alpha[byte1] = 1.0F;
				object.targetAlpha[byte1] = 1.0F;
				object.render((float)square.x, (float)square.y, (float)square.z, defColorInfo, true);
				object.renderObjectPicker((float)square.x, (float)square.y, (float)square.z, defColorInfo);
				if ((object.highlightFlags & 2) != 0) {
					object.highlightFlags &= -2;
				}

				int1 |= 1;
			}

			if (!boolean1 && object.sprite != null && (object.sprite.Properties.Is(IsoFlagType.canBeRemoved) || object.sprite.Properties.Is(IsoFlagType.attachedFloor))) {
				int1 |= 2;
			}
		}

		return int1;
	}

	private static boolean isSpriteOnSouthOrEastWall(IsoObject object) {
		if (object instanceof IsoBarricade) {
			return object.getDir() == IsoDirections.S || object.getDir() == IsoDirections.E;
		} else if (!(object instanceof IsoCurtain)) {
			return false;
		} else {
			IsoCurtain curtain = (IsoCurtain)object;
			return curtain.getType() == IsoObjectType.curtainS || curtain.getType() == IsoObjectType.curtainE;
		}
	}

	private static int DoWallLightingN(IsoGridSquare square, IsoObject object, int int1) {
		object.render((float)square.x, (float)square.y, (float)square.z, defColorInfo, true, false);
		return int1;
	}

	private static int DoWallLightingW(IsoGridSquare square, IsoObject object, int int1) {
		object.render((float)square.x, (float)square.y, (float)square.z, defColorInfo, true, false);
		return int1;
	}

	private static int DoWallLightingNW(IsoGridSquare square, IsoObject object, int int1) {
		object.render((float)square.x, (float)square.y, (float)square.z, defColorInfo, true, false);
		return int1;
	}

	private static boolean renderMinusFloor(IsoGridSquare square, boolean boolean1, boolean boolean2) {
		int int1 = boolean1 ? square.getObjects().size() - 1 : 0;
		int int2 = boolean1 ? 0 : square.getObjects().size() - 1;
		int int3 = IsoCamera.frameState.playerIndex;
		IsoGridSquare square2 = IsoCamera.frameState.CamCharacterSquare;
		IsoRoom room = IsoCamera.frameState.CamCharacterRoom;
		int int4 = (int)(IsoUtils.XToScreenInt(square.x, square.y, square.z, 0) - IsoCamera.frameState.OffX);
		int int5 = (int)(IsoUtils.YToScreenInt(square.x, square.y, square.z, 0) - IsoCamera.frameState.OffY);
		boolean boolean3 = true;
		IsoCell cell = square.getCell();
		if (int4 + 32 * Core.TileScale <= cell.StencilX1 || int4 - 32 * Core.TileScale >= cell.StencilX2 || int5 + 32 * Core.TileScale <= cell.StencilY1 || int5 - 96 * Core.TileScale >= cell.StencilY2) {
			boolean3 = false;
		}

		int int6 = 0;
		boolean boolean4 = false;
		int int7 = int1;
		while (true) {
			if (boolean1) {
				if (int7 < int2) {
					break;
				}
			} else if (int7 > int2) {
				break;
			}

			IsoObject object = (IsoObject)square.getObjects().get(int7);
			boolean boolean5 = true;
			IsoGridSquare.CircleStencil = false;
			if (object.sprite != null && object.sprite.getProperties().Is(IsoFlagType.solidfloor)) {
				boolean5 = false;
			}

			if ((!boolean2 || object.sprite == null || object.sprite.Properties.Is(IsoFlagType.canBeRemoved) || object.sprite.Properties.Is(IsoFlagType.attachedFloor)) && (boolean2 || object.sprite == null || !object.sprite.Properties.Is(IsoFlagType.canBeRemoved) && !object.sprite.Properties.Is(IsoFlagType.attachedFloor))) {
				if (object.sprite != null && (object.sprite.getType() == IsoObjectType.WestRoofB || object.sprite.getType() == IsoObjectType.WestRoofM || object.sprite.getType() == IsoObjectType.WestRoofT) && square.z == maxZ && square.z == (int)IsoCamera.CamCharacter.getZ()) {
					boolean5 = false;
				}

				if (IsoCamera.CamCharacter.isClimbing() && object.sprite != null && !object.sprite.getProperties().Is(IsoFlagType.solidfloor)) {
					boolean5 = true;
				}

				if (isSpriteOnSouthOrEastWall(object)) {
					if (!boolean1) {
						boolean5 = false;
					}

					boolean4 = true;
				} else if (boolean1) {
					boolean5 = false;
				}

				if (boolean5) {
					IndieGL.glAlphaFunc(516, 0.0F);
					IsoGridSquare square3;
					if (object.sprite != null && !square.getProperties().Is(IsoFlagType.blueprint) && (object.sprite.getType() == IsoObjectType.doorFrW || object.sprite.getType() == IsoObjectType.doorFrN || object.sprite.getType() == IsoObjectType.doorW || object.sprite.getType() == IsoObjectType.doorN || object.sprite.getProperties().Is(IsoFlagType.cutW) || object.sprite.getProperties().Is(IsoFlagType.cutN)) && PerformanceSettings.LightingFrameSkip < 3) {
						if (object.targetAlpha[int3] < 1.0F) {
							boolean boolean6 = false;
							if (boolean6) {
								if (object.sprite.getProperties().Is(IsoFlagType.cutW) && square.getProperties().Is(IsoFlagType.WallSE)) {
									square3 = square.nav[IsoDirections.NW.index()];
									if (square3 == null || square3.getRoom() == null) {
										boolean6 = false;
									}
								} else if (object.sprite.getType() != IsoObjectType.doorFrW && object.sprite.getType() != IsoObjectType.doorW && !object.sprite.getProperties().Is(IsoFlagType.cutW)) {
									if (object.sprite.getType() == IsoObjectType.doorFrN || object.sprite.getType() == IsoObjectType.doorN || object.sprite.getProperties().Is(IsoFlagType.cutN)) {
										square3 = square.nav[IsoDirections.N.index()];
										if (square3 == null || square3.getRoom() == null) {
											boolean6 = false;
										}
									}
								} else {
									square3 = square.nav[IsoDirections.W.index()];
									if (square3 == null || square3.getRoom() == null) {
										boolean6 = false;
									}
								}
							}

							if (!boolean6) {
								IsoGridSquare.CircleStencil = boolean3;
							}

							object.targetAlpha[int3] = 1.0F;
							object.alpha[int3] = 1.0F;
						}

						if (object.sprite.getProperties().Is(IsoFlagType.cutW) && object.sprite.getProperties().Is(IsoFlagType.cutN)) {
							int6 = DoWallLightingNW(square, object, int6);
						} else if (object.sprite.getType() != IsoObjectType.doorFrW && object.sprite.getType() != IsoObjectType.doorW && !object.sprite.getProperties().Is(IsoFlagType.cutW)) {
							if (object.sprite.getType() == IsoObjectType.doorFrN || object.sprite.getType() == IsoObjectType.doorN || object.sprite.getProperties().Is(IsoFlagType.cutN)) {
								int6 = DoWallLightingN(square, object, int6);
							}
						} else {
							int6 = DoWallLightingW(square, object, int6);
						}
					} else {
						if (square2 != null) {
						}

						object.targetAlpha[int3] = 1.0F;
						if (IsoCamera.CamCharacter != null && object.getProperties() != null && (object.getProperties().Is(IsoFlagType.solid) || object.getProperties().Is(IsoFlagType.solidtrans))) {
							int int8 = square.getX() - (int)IsoCamera.CamCharacter.getX();
							int int9 = square.getY() - (int)IsoCamera.CamCharacter.getY();
							if (int8 > 0 && int8 < 3 && int9 >= 0 && int9 < 3 || int9 > 0 && int9 < 3 && int8 >= 0 && int8 < 3) {
								object.targetAlpha[int3] = 0.99F;
							}
						}

						if (object instanceof IsoWindow && object.targetAlpha[int3] < 1.0E-4F) {
							IsoWindow window = (IsoWindow)object;
							square3 = window.getOppositeSquare();
							if (square3 != null && square3 != square && square3.lighting[int3].bSeen()) {
								object.targetAlpha[int3] = square3.lighting[int3].darkMulti() * 2.0F;
							}
						}

						if (object instanceof IsoTree) {
							if (boolean3 && square.x >= (int)IsoCamera.frameState.CamCharacterX && square.y >= (int)IsoCamera.frameState.CamCharacterY && square2 != null && square2.Is(IsoFlagType.exterior)) {
								((IsoTree)object).bRenderFlag = true;
							} else {
								((IsoTree)object).bRenderFlag = false;
							}
						}

						object.render((float)square.x, (float)square.y, (float)square.z, defColorInfo, true);
					}

					if (object.sprite != null) {
						object.renderObjectPicker((float)square.x, (float)square.y, (float)square.z, defColorInfo);
					}

					if ((object.highlightFlags & 2) != 0) {
						object.highlightFlags &= -2;
					}
				}
			}

			int7 += boolean1 ? -1 : 1;
		}

		return boolean4;
	}

	private static void renderCharacters(IsoGridSquare square, boolean boolean1) {
		int int1 = square.getStaticMovingObjects().size();
		int int2;
		IsoMovingObject movingObject;
		for (int2 = 0; int2 < int1; ++int2) {
			movingObject = (IsoMovingObject)square.getStaticMovingObjects().get(int2);
			if (movingObject.sprite != null && (!boolean1 || movingObject instanceof IsoDeadBody) && (boolean1 || !(movingObject instanceof IsoDeadBody))) {
				movingObject.render(movingObject.getX(), movingObject.getY(), movingObject.getZ(), defColorInfo, true);
				movingObject.renderObjectPicker(movingObject.getX(), movingObject.getY(), movingObject.getZ(), defColorInfo);
			}
		}

		int1 = square.getMovingObjects().size();
		for (int2 = 0; int2 < int1; ++int2) {
			movingObject = (IsoMovingObject)square.getMovingObjects().get(int2);
			if (movingObject != null && movingObject.sprite != null) {
				boolean boolean2 = movingObject.isOnFloor();
				if (boolean2 && movingObject instanceof IsoZombie) {
					IsoZombie zombie = (IsoZombie)movingObject;
					boolean2 = zombie.bCrawling || zombie.legsSprite.CurrentAnim != null && zombie.legsSprite.CurrentAnim.name.equals("ZombieDeath") && zombie.def.isFinished();
				}

				if ((!boolean1 || boolean2) && (boolean1 || !boolean2)) {
					movingObject.alpha[0] = movingObject.targetAlpha[0] = 1.0F;
					if (movingObject instanceof IsoGameCharacter) {
						IsoGameCharacter gameCharacter = (IsoGameCharacter)movingObject;
						float float1 = (float)Core.TileScale;
						float float2 = gameCharacter.offsetX + (float)IsoGameCharacter.RENDER_OFFSET_X * float1;
						float float3 = gameCharacter.offsetY + (float)IsoGameCharacter.RENDER_OFFSET_Y * float1;
						if (gameCharacter.sprite != null) {
							gameCharacter.def.setScale(float1, float1);
							if (!gameCharacter.isbUseParts()) {
								gameCharacter.sprite.render(gameCharacter.def, gameCharacter, gameCharacter.x, gameCharacter.y, gameCharacter.z, gameCharacter.dir, float2, float3, defColorInfo);
							} else {
								gameCharacter.def.Flip = false;
								gameCharacter.legsSprite.render(gameCharacter.def, gameCharacter, gameCharacter.x, gameCharacter.y, gameCharacter.z, gameCharacter.dir, float2, float3, defColorInfo);
								if (!gameCharacter.hasActiveModel()) {
									if (gameCharacter.torsoSprite != null) {
										gameCharacter.torsoSprite.render(gameCharacter.def, gameCharacter, gameCharacter.x, gameCharacter.y, gameCharacter.z, gameCharacter.dir, float2, float3, defColorInfo);
									}

									if (gameCharacter.shoeSprite != null) {
										gameCharacter.shoeSprite.render(gameCharacter.def, gameCharacter, gameCharacter.x, gameCharacter.y, gameCharacter.z, gameCharacter.dir, float2, float3, defColorInfo);
									}

									if (gameCharacter.bottomsSprite != null) {
										gameCharacter.bottomsSprite.render(gameCharacter.def, gameCharacter, gameCharacter.x, gameCharacter.y, gameCharacter.z, gameCharacter.dir, float2, float3, defColorInfo);
									}

									if (gameCharacter.topSprite != null) {
										gameCharacter.topSprite.render(gameCharacter.def, gameCharacter, gameCharacter.x, gameCharacter.y, gameCharacter.z, gameCharacter.dir, float2, float3, defColorInfo);
									}

									if (gameCharacter.headSprite != null) {
										gameCharacter.headSprite.render(gameCharacter.def, gameCharacter, gameCharacter.x, gameCharacter.y, gameCharacter.z, gameCharacter.dir, float2, float3, defColorInfo);
									}

									if (gameCharacter.hairSprite != null) {
										gameCharacter.hairSprite.render(gameCharacter.def, gameCharacter, gameCharacter.x, gameCharacter.y, gameCharacter.z, gameCharacter.dir, float2, float3, defColorInfo);
									}

									for (int int3 = 0; int3 < gameCharacter.extraSprites.size(); ++int3) {
										((IsoSprite)gameCharacter.extraSprites.get(int3)).render(gameCharacter.def, gameCharacter, gameCharacter.x, gameCharacter.y, gameCharacter.z, gameCharacter.dir, float2, float3, defColorInfo);
									}
								}
							}
						}
					} else {
						movingObject.render(movingObject.getX(), movingObject.getY(), movingObject.getZ(), defColorInfo, true);
					}

					movingObject.renderObjectPicker(movingObject.getX(), movingObject.getY(), movingObject.getZ(), defColorInfo);
				}
			}
		}
	}

	private static void renderUI() {
	}
}
