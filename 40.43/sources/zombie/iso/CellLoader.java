package zombie.iso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import zombie.FrameLoader;
import zombie.Lua.LuaEventManager;
import zombie.ai.astar.AStarPathMap;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoJukebox;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWheelieBin;
import zombie.iso.objects.IsoWindow;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.ChunkRevisions;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.scripting.ScriptManager;


public class CellLoader {
	static int wanderX = 0;
	static int wanderY = 0;
	static IsoRoom wanderRoom = null;
	static HashSet missingTiles = new HashSet();
	public static Stack isoObjectCache = new Stack();
	public static Stack isoTreeCache = new Stack();

	public static void DoTileObjectCreation(IsoSprite sprite, IsoObjectType objectType, IsoGridSquare square, IsoCell cell, int int1, int int2, int int3, Stack stack, boolean boolean1, String string) throws NumberFormatException {
		Object object = null;
		boolean boolean2 = false;
		if (boolean1) {
			boolean2 = true;
		}

		if (square != null) {
			if (objectType != IsoObjectType.doorW && objectType != IsoObjectType.doorN) {
				float float1;
				if (objectType == IsoObjectType.lightswitch) {
					object = new IsoLightSwitch(cell, square, sprite, square.getRoomID());
					AddObject(square, (IsoObject)object);
					GameClient.instance.objectSyncReq.putRequest(square, (IsoObject)object);
					if (((IsoObject)object).sprite.getProperties().Is("lightR")) {
						float float2 = Float.parseFloat(((IsoObject)object).sprite.getProperties().Val("lightR")) / 255.0F;
						float float3 = Float.parseFloat(((IsoObject)object).sprite.getProperties().Val("lightG")) / 255.0F;
						float1 = Float.parseFloat(((IsoObject)object).sprite.getProperties().Val("lightB")) / 255.0F;
						IsoLightSource lightSource = new IsoLightSource(((IsoObject)object).square.getX(), ((IsoObject)object).square.getY(), ((IsoObject)object).square.getZ(), float2, float3, float1, 10);
						lightSource.bActive = true;
						lightSource.bHydroPowered = true;
						lightSource.switches.add((IsoLightSwitch)object);
						((IsoLightSwitch)object).lights.add(lightSource);
					} else {
						((IsoLightSwitch)object).lightRoom = true;
					}
				} else if (objectType != IsoObjectType.curtainN && objectType != IsoObjectType.curtainS && objectType != IsoObjectType.curtainE && objectType != IsoObjectType.curtainW) {
					if (!sprite.getProperties().Is(IsoFlagType.windowW) && !sprite.getProperties().Is(IsoFlagType.windowN)) {
						if (sprite.getProperties().Is("container") && sprite.getProperties().Val("container").equals("barbecue")) {
							object = new IsoBarbecue(cell, square, sprite);
							AddObject(square, (IsoObject)object);
						} else if (sprite.getProperties().Is("container") && sprite.getProperties().Val("container").equals("fireplace")) {
							object = new IsoFireplace(cell, square, sprite);
							AddObject(square, (IsoObject)object);
						} else if (sprite.getProperties().Is("container") && sprite.getProperties().Val("container").equals("woodstove")) {
							object = new IsoFireplace(cell, square, sprite);
							AddObject(square, (IsoObject)object);
						} else if (sprite.getProperties().Is("container") && (sprite.getProperties().Val("container").equals("stove") || sprite.getProperties().Val("container").equals("microwave"))) {
							object = new IsoStove(cell, square, sprite);
							AddObject(square, (IsoObject)object);
							GameClient.instance.objectSyncReq.putRequest(square, (IsoObject)object);
						} else if (objectType == IsoObjectType.jukebox) {
							object = new IsoJukebox(cell, square, sprite);
							((IsoObject)object).OutlineOnMouseover = true;
							cell.getStaticUpdaterObjectList().add(object);
							AddObject(square, (IsoObject)object);
						} else if (objectType == IsoObjectType.radio) {
							object = new IsoRadio(cell, square, sprite);
							AddObject(square, (IsoObject)object);
						} else {
							String string2;
							if (sprite.getProperties().Is("signal")) {
								string2 = sprite.getProperties().Val("signal");
								if ("radio".equals(string2)) {
									object = new IsoRadio(cell, square, sprite);
								} else if ("tv".equals(string2)) {
									object = new IsoTelevision(cell, square, sprite);
								}

								AddObject(square, (IsoObject)object);
							} else {
								IsoObject object2;
								if (sprite.getProperties().Is(IsoFlagType.WallOverlay)) {
									object2 = null;
									if (sprite.getProperties().Is(IsoFlagType.attachedW)) {
										object2 = square.getWall(false);
									} else if (sprite.getProperties().Is(IsoFlagType.attachedN)) {
										object2 = square.getWall(true);
									} else {
										for (int int4 = square.getObjects().size() - 1; int4 >= 0; --int4) {
											IsoObject object3 = (IsoObject)square.getObjects().get(int4);
											if (object3.sprite.getProperties().Is(IsoFlagType.cutW) || object3.sprite.getProperties().Is(IsoFlagType.cutN)) {
												object2 = object3;
												break;
											}
										}
									}

									if (object2 != null) {
										if (object2.AttachedAnimSprite == null) {
											object2.AttachedAnimSprite = new ArrayList(4);
											object2.AttachedAnimSpriteActual = new ArrayList(4);
										}

										object2.AttachedAnimSprite.add(IsoSpriteInstance.get(sprite));
										object2.AttachedAnimSpriteActual.add(sprite);
									} else {
										IsoObject object4;
										if (isoObjectCache.isEmpty()) {
											object4 = new IsoObject(cell, square, sprite);
										} else {
											object4 = (IsoObject)isoObjectCache.pop();
											object4.sx = 0;
										}

										object4.sprite = sprite;
										object4.square = square;
										AddObject(square, object4);
									}

									return;
								}

								if (sprite.getProperties().Is(IsoFlagType.FloorOverlay)) {
									object2 = square.getFloor();
									if (object2 != null) {
										if (object2.AttachedAnimSprite == null) {
											object2.AttachedAnimSprite = new ArrayList(4);
											object2.AttachedAnimSpriteActual = new ArrayList(4);
										}

										object2.AttachedAnimSpriteActual.add(sprite);
										object2.AttachedAnimSprite.add(IsoSpriteInstance.get(sprite));
									}
								} else if (objectType == IsoObjectType.tree) {
									if (sprite.getName() != null && sprite.getName().startsWith("vegetation_trees")) {
										object2 = square.getFloor();
										if (object2 == null || object2.getSprite() == null || object2.getSprite().getName() == null || !object2.getSprite().getName().startsWith("blends_natural")) {
											DebugLog.log("ERROR: removed tree at " + square.x + "," + square.y + "," + square.z + " because floor is not blends_natural");
											return;
										}
									}

									if (isoTreeCache.isEmpty()) {
										object = new IsoTree(square, sprite);
									} else {
										object = (IsoObject)isoTreeCache.pop();
										((IsoObject)object).sprite = sprite;
										((IsoObject)object).square = square;
										((IsoObject)object).sx = 0;
										((IsoTree)object).initTree();
									}

									for (int int5 = 0; int5 < square.getObjects().size(); ++int5) {
										IsoObject object5 = (IsoObject)square.getObjects().get(int5);
										if (object5 instanceof IsoTree) {
											square.getObjects().remove(int5);
											object5.reset();
											isoTreeCache.push((IsoTree)object5);
											break;
										}
									}

									AddObject(square, (IsoObject)object);
								} else {
									if ((sprite.CurrentAnim.Frames.isEmpty() || ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) == null) && !GameServer.bServer) {
										if (!missingTiles.contains(string)) {
											if (Core.bDebug) {
												DebugLog.log("ERROR: missing tile " + string);
											}

											missingTiles.add(string);
										}

										sprite.LoadFramesNoDirPageSimple(Core.bDebug ? "media/ui/missing-tile-debug.png" : "media/ui/missing-tile.png");
										if (sprite.CurrentAnim.Frames.isEmpty() || ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) == null) {
											return;
										}
									}

									string2 = GameServer.bServer ? null : ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName();
									boolean boolean3 = true;
									if (!GameServer.bServer && string2.contains("TileObjectsExt") && (string2.contains("_5") || string2.contains("_6") || string2.contains("_7") || string2.contains("_8"))) {
										object = new IsoWheelieBin(cell, int1, int2, int3);
										if (string2.contains("_5")) {
											((IsoObject)object).dir = IsoDirections.S;
										}

										if (string2.contains("_6")) {
											((IsoObject)object).dir = IsoDirections.W;
										}

										if (string2.contains("_7")) {
											((IsoObject)object).dir = IsoDirections.N;
										}

										if (string2.contains("_8")) {
											((IsoObject)object).dir = IsoDirections.E;
										}

										boolean3 = false;
									}

									if (boolean3) {
										if (isoObjectCache.isEmpty()) {
											object = new IsoObject(cell, square, sprite);
										} else {
											object = (IsoObject)isoObjectCache.pop();
											((IsoObject)object).sx = 0;
										}

										((IsoObject)object).sprite = sprite;
										((IsoObject)object).square = square;
										AddObject(square, (IsoObject)object);
										if (((IsoObject)object).sprite.getProperties().Is("lightR")) {
											float1 = Float.parseFloat(((IsoObject)object).sprite.getProperties().Val("lightR"));
											float float4 = Float.parseFloat(((IsoObject)object).sprite.getProperties().Val("lightG"));
											float float5 = Float.parseFloat(((IsoObject)object).sprite.getProperties().Val("lightB"));
											cell.getLamppostPositions().add(new IsoLightSource(((IsoObject)object).square.getX(), ((IsoObject)object).square.getY(), ((IsoObject)object).square.getZ(), float1, float4, float5, 8));
										}
									}
								}
							}
						}
					} else {
						object = new IsoWindow(cell, square, sprite, sprite.getProperties().Is(IsoFlagType.windowN));
						AddSpecialObject(square, (IsoObject)object);
						GameClient.instance.objectSyncReq.putRequest(square, (IsoObject)object);
					}
				} else {
					boolean boolean4 = Integer.parseInt(string.substring(string.lastIndexOf("_") + 1)) % 8 <= 3;
					object = new IsoCurtain(cell, square, sprite, objectType == IsoObjectType.curtainN || objectType == IsoObjectType.curtainS, boolean4);
					AddSpecialObject(square, (IsoObject)object);
					GameClient.instance.objectSyncReq.putRequest(square, (IsoObject)object);
				}
			} else {
				object = new IsoDoor(cell, square, sprite, objectType == IsoObjectType.doorN);
				AddSpecialObject(square, (IsoObject)object);
				GameClient.instance.objectSyncReq.putRequest(square, (IsoObject)object);
			}

			if (object != null) {
				((IsoObject)object).tile = string;
				((IsoObject)object).createContainersFromSpriteProperties();
				if (Rand.Next(70) == 0) {
				}

				if (((IsoObject)object).sprite.getProperties().Is(IsoFlagType.vegitation)) {
					((IsoObject)object).tintr = 0.7F + (float)Rand.Next(30) / 100.0F;
					((IsoObject)object).tintg = 0.7F + (float)Rand.Next(30) / 100.0F;
					((IsoObject)object).tintb = 0.7F + (float)Rand.Next(30) / 100.0F;
					if (Rand.Next(10) == 0) {
					}
				}
			}
		}
	}

	public static boolean LoadCellBinaryChunk(IsoCell cell, int int1, int int2, IsoChunk chunk) {
		Integer integer = int1 / 30;
		Integer integer2 = int2 / 30;
		float float1 = (float)int1;
		float float2 = (float)int2;
		boolean boolean1 = false;
		float1 /= 30.0F;
		float2 /= 30.0F;
		int int3 = (int)Math.floor((double)float1);
		int int4 = (int)Math.floor((double)float2);
		String string = "world_" + int1 / 30 + "_" + int2 / 30 + ".lotpack";
		if (!IsoLot.InfoFileNames.containsKey(string)) {
			DebugLog.log("LoadCellBinaryChunk: NO SUCH LOT " + string);
			return false;
		} else {
			File file = new File((String)IsoLot.InfoFileNames.get(string));
			if (file.exists()) {
				IsoLot lot = IsoLot.get(int1 / 30, int2 / 30, int1, int2, chunk);
				cell.PlaceLot(lot, 0, 0, 0, chunk, int1, int2, false);
				IsoLot.put(lot);
				return true;
			} else {
				DebugLog.log("LoadCellBinaryChunk: NO SUCH LOT " + string);
				return false;
			}
		}
	}

	public static IsoCell LoadCellBinaryChunk(IsoSpriteManager spriteManager, int int1, int int2) throws IOException {
		wanderX = 0;
		wanderY = 0;
		wanderRoom = null;
		wanderX = 0;
		wanderY = 0;
		IsoCell cell = new IsoCell(spriteManager, 300, 300);
		int int3 = IsoPlayer.numPlayers;
		if (!FrameLoader.bDedicated) {
			int3 = 1;
		}

		int int4;
		if (!GameServer.bServer) {
			if (ChunkRevisions.USE_CHUNK_REVISIONS) {
				if (GameClient.bClient) {
					ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
					PacketTypes.doPacket((short)24, byteBufferWriter);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putInt(IsoChunkMap.ChunkGridWidth);
					GameClient.connection.endPacketImmediate();
					ChunkRevisions.instance.requestStartupChunkRevisions(int1 - ChunkRevisions.UpdateArea / 2, int2 - ChunkRevisions.UpdateArea / 2, ChunkRevisions.UpdateArea, ChunkRevisions.UpdateArea);
				}
			} else if (GameClient.bClient) {
				WorldStreamer.instance.requestLargeAreaZip(int1, int2, IsoChunkMap.ChunkGridWidth / 2 + 2);
				IsoChunk.bDoServerRequests = false;
			}

			for (int4 = 0; int4 < int3; ++int4) {
				cell.ChunkMap[int4].setInitialPos(int1, int2);
				IsoPlayer.assumedPlayer = int4;
				IsoChunkMap chunkMap = cell.ChunkMap[int4];
				int int5 = int1 - IsoChunkMap.ChunkGridWidth / 2;
				chunkMap = cell.ChunkMap[int4];
				int int6 = int2 - IsoChunkMap.ChunkGridWidth / 2;
				chunkMap = cell.ChunkMap[int4];
				int int7 = int1 + IsoChunkMap.ChunkGridWidth / 2 + 1;
				chunkMap = cell.ChunkMap[int4];
				int int8 = int2 + IsoChunkMap.ChunkGridWidth / 2 + 1;
				for (int int9 = int5; int9 < int7; ++int9) {
					for (int int10 = int6; int10 < int8; ++int10) {
						if (IsoWorld.instance.getMetaGrid().isValidChunk(int9, int10)) {
							cell.ChunkMap[int4].LoadChunk(int9, int10, int9 - int5, int10 - int6);
						}
					}
				}
			}
		}

		IsoPlayer.assumedPlayer = 0;
		ScriptManager.instance.Trigger("OnPostMapLoad");
		LuaEventManager.triggerEvent("OnPostMapLoad", cell, int1, int2);
		ConnectMultitileObjects(cell);
		if (!GameServer.bServer) {
			cell.setPathMap(new AStarPathMap(cell));
		}

		for (int4 = 0; int4 < cell.roomDefs.size(); ++int4) {
			IsoGridSquare square = cell.getGridSquare(((IsoCell.Zone)cell.roomDefs.get(int4)).X, ((IsoCell.Zone)cell.roomDefs.get(int4)).Y, ((IsoCell.Zone)cell.roomDefs.get(int4)).Z);
			if (square != null && square.room != null) {
				square.room.RoomDef = ((IsoCell.Zone)cell.roomDefs.get(int4)).Name;
			}
		}

		return cell;
	}

	private static void RecurseMultitileObjects(IsoCell cell, IsoGridSquare square, IsoGridSquare square2, ArrayList arrayList) {
		Iterator iterator = square2.getMovingObjects().iterator();
		IsoPushableObject pushableObject = null;
		boolean boolean1 = false;
		while (iterator != null && iterator.hasNext()) {
			IsoMovingObject movingObject = (IsoMovingObject)iterator.next();
			if (movingObject instanceof IsoPushableObject) {
				IsoPushableObject pushableObject2 = (IsoPushableObject)movingObject;
				int int1 = square.getX() - square2.getX();
				int int2 = square.getY() - square2.getY();
				int int3;
				if (int2 != 0 && movingObject.sprite.getProperties().Is("connectY")) {
					int3 = Integer.parseInt(movingObject.sprite.getProperties().Val("connectY"));
					if (int3 == int2) {
						pushableObject2.connectList = arrayList;
						arrayList.add(pushableObject2);
						pushableObject = pushableObject2;
						boolean1 = false;
						break;
					}
				}

				if (int1 != 0 && movingObject.sprite.getProperties().Is("connectX")) {
					int3 = Integer.parseInt(movingObject.sprite.getProperties().Val("connectX"));
					if (int3 == int1) {
						pushableObject2.connectList = arrayList;
						arrayList.add(pushableObject2);
						pushableObject = pushableObject2;
						boolean1 = true;
						break;
					}
				}
			}
		}

		if (pushableObject != null) {
			int int4;
			IsoGridSquare square3;
			if (pushableObject.sprite.getProperties().Is("connectY") && boolean1) {
				int4 = Integer.parseInt(pushableObject.sprite.getProperties().Val("connectY"));
				square3 = cell.getGridSquare(pushableObject.getCurrentSquare().getX(), pushableObject.getCurrentSquare().getY() + int4, pushableObject.getCurrentSquare().getZ());
				RecurseMultitileObjects(cell, pushableObject.getCurrentSquare(), square3, pushableObject.connectList);
			}

			if (pushableObject.sprite.getProperties().Is("connectX") && !boolean1) {
				int4 = Integer.parseInt(pushableObject.sprite.getProperties().Val("connectX"));
				square3 = cell.getGridSquare(pushableObject.getCurrentSquare().getX() + int4, pushableObject.getCurrentSquare().getY(), pushableObject.getCurrentSquare().getZ());
				RecurseMultitileObjects(cell, pushableObject.getCurrentSquare(), square3, pushableObject.connectList);
			}
		}
	}

	private static void ConnectMultitileObjects(IsoCell cell) {
		Iterator iterator = cell.getObjectList().iterator();
		while (iterator != null && iterator.hasNext()) {
			IsoMovingObject movingObject = (IsoMovingObject)iterator.next();
			if (movingObject instanceof IsoPushableObject) {
				IsoPushableObject pushableObject = (IsoPushableObject)movingObject;
				if ((movingObject.sprite.getProperties().Is("connectY") || movingObject.sprite.getProperties().Is("connectX")) && pushableObject.connectList == null) {
					pushableObject.connectList = new ArrayList();
					pushableObject.connectList.add(pushableObject);
					int int1;
					IsoGridSquare square;
					if (movingObject.sprite.getProperties().Is("connectY")) {
						int1 = Integer.parseInt(movingObject.sprite.getProperties().Val("connectY"));
						square = cell.getGridSquare(movingObject.getCurrentSquare().getX(), movingObject.getCurrentSquare().getY() + int1, movingObject.getCurrentSquare().getZ());
						if (square == null) {
							boolean boolean1 = false;
						}

						RecurseMultitileObjects(cell, pushableObject.getCurrentSquare(), square, pushableObject.connectList);
					}

					if (movingObject.sprite.getProperties().Is("connectX")) {
						int1 = Integer.parseInt(movingObject.sprite.getProperties().Val("connectX"));
						square = cell.getGridSquare(movingObject.getCurrentSquare().getX() + int1, movingObject.getCurrentSquare().getY(), movingObject.getCurrentSquare().getZ());
						RecurseMultitileObjects(cell, pushableObject.getCurrentSquare(), square, pushableObject.connectList);
					}
				}
			}
		}
	}

	private static void AddObject(IsoGridSquare square, IsoObject object) {
		square.getObjects().add(object);
	}

	private static void AddSpecialObject(IsoGridSquare square, IsoObject object) {
		square.getObjects().add(object);
		square.getSpecialObjects().add(object);
	}
}
