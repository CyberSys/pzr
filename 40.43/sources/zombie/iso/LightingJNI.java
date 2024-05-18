package zombie.iso;

import java.util.ArrayList;
import java.util.Stack;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.Moodles.MoodleType;
import zombie.core.PerformanceSettings;
import zombie.core.opengl.RenderSettings;
import zombie.core.textures.ColorInfo;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.weather.ClimateManager;
import zombie.meta.Meta;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleLight;
import zombie.vehicles.VehiclePart;


public final class LightingJNI {
	public static boolean newLightingMethod = true;
	public static boolean init = false;
	private static ArrayList torches = new ArrayList();
	private static ArrayList activeTorches = new ArrayList();
	private static ArrayList JNILights = new ArrayList();
	private static int[] updateCounter = new int[4];
	private static boolean bWasElecShut = false;
	private static boolean bWasNight = false;
	public static int[][] ForcedVis = new int[][]{{-1, 0, -1, -1, 0, -1, 1, -1, 1, 0, -2, -2, -1, -2, 0, -2, 1, -2, 2, -2}, {-1, 1, -1, 0, -1, -1, 0, -1, 1, -1, -2, 0, -2, -1, -2, -2, -1, -2, 0, -2}, {0, 1, -1, 1, -1, 0, -1, -1, 0, -1, -2, 2, -2, 1, -2, 0, -2, -1, -2, -2}, {1, 1, 0, 1, -1, 1, -1, 0, -1, -1, 0, 2, -1, 2, -2, 2, -2, 1, -2, 0}, {1, 0, 1, 1, 0, 1, -1, 1, -1, 0, 2, 2, 1, 2, 0, 2, -1, 2, -2, 2}, {-1, 1, 0, 1, 1, 1, 1, 0, 1, -1, 2, 0, 2, 1, 2, 2, 1, 2, 0, 2}, {0, 1, 1, 1, 1, 0, 1, -1, 0, -1, 2, -2, 2, -1, 2, 0, 2, 1, 2, 2}, {-1, -1, 0, -1, 1, -1, 1, 0, 1, 1, 0, -2, 1, -2, 2, -2, 2, -1, 2, 0}};
	public static final int ROOM_SPAWN_DIST = 50;

	public static void init() {
		if (!init) {
			String string = "";
			try {
				if (System.getProperty("os.name").contains("OS X")) {
					System.loadLibrary("Lighting");
				} else if (System.getProperty("os.name").startsWith("Win")) {
					if (System.getProperty("sun.arch.data.model").equals("64")) {
						System.loadLibrary("Lighting64" + string);
					} else {
						System.loadLibrary("Lighting32" + string);
					}
				} else if (System.getProperty("sun.arch.data.model").equals("64")) {
					System.loadLibrary("Lighting64");
				} else {
					System.loadLibrary("Lighting32");
				}

				for (int int1 = 0; int1 < 4; ++int1) {
					updateCounter[int1] = -1;
				}

				configure(0.005F);
				init = true;
			} catch (UnsatisfiedLinkError unsatisfiedLinkError) {
				unsatisfiedLinkError.printStackTrace();
				try {
					Thread.sleep(3000L);
				} catch (InterruptedException interruptedException) {
				}

				System.exit(1);
			}
		}
	}

	private static void checkTorch(IsoPlayer player, int int1) {
		if (player != null && !player.isDead() && player.getTorchStrength() > 0.0F && player.getVehicle() == null) {
			IsoGameCharacter.TorchInfo torchInfo = null;
			for (int int2 = 0; int2 < torches.size(); ++int2) {
				torchInfo = (IsoGameCharacter.TorchInfo)torches.get(int2);
				if (torchInfo.id == int1) {
					break;
				}

				torchInfo = null;
			}

			if (torchInfo == null) {
				torchInfo = IsoGameCharacter.TorchInfo.alloc();
				torches.add(torchInfo);
			}

			torchInfo.set(player);
			if (torchInfo.id == 0) {
				torchInfo.id = int1;
			}

			updateTorch(torchInfo.id, torchInfo.x, torchInfo.y, torchInfo.z, torchInfo.angleX, torchInfo.angleY, torchInfo.dist, torchInfo.strength, torchInfo.bCone, torchInfo.dot, torchInfo.focusing);
			activeTorches.add(torchInfo);
		} else {
			for (int int3 = 0; int3 < torches.size(); ++int3) {
				IsoGameCharacter.TorchInfo torchInfo2 = (IsoGameCharacter.TorchInfo)torches.get(int3);
				if (torchInfo2.id == int1) {
					removeTorch(torchInfo2.id);
					torchInfo2.id = 0;
					IsoGameCharacter.TorchInfo.release(torchInfo2);
					torches.remove(int3--);
				}
			}
		}
	}

	private static void checkTorch(VehiclePart vehiclePart, int int1) {
		VehicleLight vehicleLight = vehiclePart.getLight();
		if (vehicleLight != null && vehicleLight.getActive()) {
			IsoGameCharacter.TorchInfo torchInfo = null;
			for (int int2 = 0; int2 < torches.size(); ++int2) {
				torchInfo = (IsoGameCharacter.TorchInfo)torches.get(int2);
				if (torchInfo.id == int1) {
					break;
				}

				torchInfo = null;
			}

			if (torchInfo == null) {
				torchInfo = IsoGameCharacter.TorchInfo.alloc();
				torches.add(torchInfo);
			}

			torchInfo.set(vehiclePart);
			if (torchInfo.id == 0) {
				torchInfo.id = int1;
			}

			updateTorch(torchInfo.id, torchInfo.x, torchInfo.y, torchInfo.z, torchInfo.angleX, torchInfo.angleY, torchInfo.dist, torchInfo.strength, torchInfo.bCone, torchInfo.dot, torchInfo.focusing);
			activeTorches.add(torchInfo);
		} else {
			for (int int3 = 0; int3 < torches.size(); ++int3) {
				IsoGameCharacter.TorchInfo torchInfo2 = (IsoGameCharacter.TorchInfo)torches.get(int3);
				if (torchInfo2.id == int1) {
					removeTorch(torchInfo2.id);
					torchInfo2.id = 0;
					IsoGameCharacter.TorchInfo.release(torchInfo2);
					torches.remove(int3--);
				}
			}
		}
	}

	private static void checkLights() {
		if (IsoWorld.instance.CurrentCell != null) {
			if (GameClient.bClient) {
				IsoGenerator.updateSurroundingNow();
			}

			boolean boolean1 = GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier();
			Stack stack = IsoWorld.instance.CurrentCell.getLamppostPositions();
			int int1;
			IsoLightSource lightSource;
			int int2;
			boolean boolean2;
			for (int1 = 0; int1 < stack.size(); ++int1) {
				lightSource = (IsoLightSource)stack.get(int1);
				IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunkForGridSquare(lightSource.x, lightSource.y, lightSource.z);
				if (chunk != null && lightSource.chunk != null && lightSource.chunk != chunk) {
					lightSource.life = 0;
				}

				if (lightSource.life != 0 && lightSource.isInBounds()) {
					if (lightSource.bHydroPowered) {
						if (lightSource.switches.isEmpty()) {
							assert false;
							boolean2 = boolean1;
							if (!boolean1) {
								IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(lightSource.x, lightSource.y, lightSource.z);
								boolean2 = square != null && square.haveElectricity();
							}

							if (lightSource.bActive != boolean2) {
								lightSource.bActive = boolean2;
								GameTime.instance.lightSourceUpdate = 100.0F;
							}
						} else {
							IsoLightSwitch lightSwitch = (IsoLightSwitch)lightSource.switches.get(0);
							boolean2 = lightSwitch.canSwitchLight();
							if (lightSwitch.bStreetLight && GameTime.getInstance().getNight() < 0.5F) {
								boolean2 = false;
							}

							if (lightSource.bActive && !boolean2) {
								lightSource.bActive = false;
								GameTime.instance.lightSourceUpdate = 100.0F;
							} else if (!lightSource.bActive && boolean2 && lightSwitch.isActivated()) {
								lightSource.bActive = true;
								GameTime.instance.lightSourceUpdate = 100.0F;
							}
						}
					}

					if (lightSource.ID == 0) {
						lightSource.ID = IsoLightSource.NextID++;
						if (lightSource.life != -1) {
							addTempLight(lightSource.ID, lightSource.x, lightSource.y, lightSource.z, lightSource.radius, lightSource.r, lightSource.g, lightSource.b, (int)((float)(lightSource.life * PerformanceSettings.LockFPS) / 30.0F));
							stack.remove(int1--);
						} else {
							lightSource.rJNI = lightSource.r;
							lightSource.gJNI = lightSource.g;
							lightSource.bJNI = lightSource.b;
							lightSource.bActiveJNI = lightSource.bActive;
							JNILights.add(lightSource);
							addLight(lightSource.ID, lightSource.x, lightSource.y, lightSource.z, lightSource.radius, lightSource.r, lightSource.g, lightSource.b, lightSource.localToBuilding == null ? -1 : lightSource.localToBuilding.ID, lightSource.bActive);
						}
					} else {
						if (lightSource.r != lightSource.rJNI || lightSource.g != lightSource.gJNI || lightSource.b != lightSource.bJNI) {
							lightSource.rJNI = lightSource.r;
							lightSource.gJNI = lightSource.g;
							lightSource.bJNI = lightSource.b;
							setLightColor(lightSource.ID, lightSource.r, lightSource.g, lightSource.b);
						}

						if (lightSource.bActiveJNI != lightSource.bActive) {
							lightSource.bActiveJNI = lightSource.bActive;
							setLightActive(lightSource.ID, lightSource.bActive);
						}
					}
				} else {
					stack.remove(int1);
					if (lightSource.ID != 0) {
						int2 = lightSource.ID;
						lightSource.ID = 0;
						JNILights.remove(lightSource);
						removeLight(int2);
						GameTime.instance.lightSourceUpdate = 100.0F;
					}

					--int1;
				}
			}

			int int3;
			for (int1 = 0; int1 < JNILights.size(); ++int1) {
				lightSource = (IsoLightSource)JNILights.get(int1);
				if (!stack.contains(lightSource)) {
					int3 = lightSource.ID;
					lightSource.ID = 0;
					JNILights.remove(int1--);
					removeLight(int3);
				}
			}

			ArrayList arrayList = IsoWorld.instance.CurrentCell.roomLights;
			int int4;
			for (int4 = 0; int4 < arrayList.size(); ++int4) {
				IsoRoomLight roomLight = (IsoRoomLight)arrayList.get(int4);
				if (!roomLight.isInBounds()) {
					arrayList.remove(int4--);
					if (roomLight.ID != 0) {
						int2 = roomLight.ID;
						roomLight.ID = 0;
						removeRoomLight(int2);
					}
				} else {
					roomLight.bActive = roomLight.room.def.bLightsActive;
					if (!boolean1) {
						boolean2 = false;
						for (int int5 = 0; !boolean2 && int5 < roomLight.room.lightSwitches.size(); ++int5) {
							IsoLightSwitch lightSwitch2 = (IsoLightSwitch)roomLight.room.lightSwitches.get(int5);
							if (lightSwitch2.square != null && lightSwitch2.square.haveElectricity()) {
								boolean2 = true;
							}
						}

						if (!boolean2 && roomLight.bActive) {
							roomLight.bActive = false;
							if (roomLight.bActiveJNI) {
								IsoGridSquare.RecalcLightTime = -1;
								GameTime.instance.lightSourceUpdate = 100.0F;
							}
						} else if (boolean2 && roomLight.bActive && !roomLight.bActiveJNI) {
							IsoGridSquare.RecalcLightTime = -1;
							GameTime.instance.lightSourceUpdate = 100.0F;
						}
					}

					if (roomLight.ID == 0) {
						roomLight.ID = IsoRoomLight.NextID++;
						addRoomLight(roomLight.ID, roomLight.room.building.def.ID * 1000 + roomLight.room.def.ID, roomLight.x, roomLight.y, roomLight.z, roomLight.width, roomLight.height, roomLight.bActive);
						roomLight.bActiveJNI = roomLight.bActive;
					} else if (roomLight.bActiveJNI != roomLight.bActive) {
						setRoomLightActive(roomLight.ID, roomLight.bActive);
						roomLight.bActiveJNI = roomLight.bActive;
					}
				}
			}

			activeTorches.clear();
			if (GameClient.bClient) {
				ArrayList arrayList2 = GameClient.instance.getPlayers();
				for (int3 = 0; int3 < arrayList2.size(); ++int3) {
					IsoPlayer player = (IsoPlayer)arrayList2.get(int3);
					checkTorch(player, player.OnlineID + 1);
				}
			} else {
				for (int4 = 0; int4 < IsoPlayer.numPlayers; ++int4) {
					IsoPlayer player2 = IsoPlayer.players[int4];
					checkTorch(player2, int4 + 1);
				}
			}

			for (int4 = 0; int4 < IsoWorld.instance.CurrentCell.getVehicles().size(); ++int4) {
				BaseVehicle baseVehicle = (BaseVehicle)IsoWorld.instance.CurrentCell.getVehicles().get(int4);
				if (baseVehicle.VehicleID != -1) {
					for (int2 = 0; int2 < baseVehicle.getLightCount(); ++int2) {
						VehiclePart vehiclePart = baseVehicle.getLightByIndex(int2);
						checkTorch(vehiclePart, 256 + baseVehicle.VehicleID * 10 + int2);
					}
				}
			}

			for (int4 = 0; int4 < torches.size(); ++int4) {
				IsoGameCharacter.TorchInfo torchInfo = (IsoGameCharacter.TorchInfo)torches.get(int4);
				if (!activeTorches.contains(torchInfo)) {
					removeTorch(torchInfo.id);
					torchInfo.id = 0;
					IsoGameCharacter.TorchInfo.release(torchInfo);
					torches.remove(int4--);
				}
			}
		}
	}

	public static void updatePlayer(int int1) {
		IsoPlayer player = IsoPlayer.players[int1];
		if (player != null) {
			float float1 = player.getStats().fatigue - 0.6F;
			if (float1 < 0.0F) {
				float1 = 0.0F;
			}

			float1 *= 2.5F;
			if (player.HasTrait("HardOfHearing") && float1 < 0.7F) {
				float1 = 0.7F;
			}

			float float2 = 2.0F;
			if (player.HasTrait("KeenHearing")) {
				float2 += 3.0F;
			}

			float float3;
			if (player.getVehicle() == null) {
				float3 = -0.2F;
				float3 -= player.getStats().fatigue - 0.6F;
				if (float3 > -0.2F) {
					float3 = -0.2F;
				}

				if (player.getStats().fatigue >= 1.0F) {
					float3 -= 0.2F;
				}

				if (player.getMoodles().getMoodleLevel(MoodleType.Panic) == 4) {
					float3 -= 0.2F;
				}

				if (player.isInARoom()) {
					float3 -= 0.2F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
				} else {
					float3 -= 0.7F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
				}

				if (float3 < -0.9F) {
					float3 = -0.9F;
				}

				if (player.HasTrait("EagleEyed")) {
					float3 += 0.2F * ClimateManager.getInstance().getDayLightStrength();
				}

				if (player.HasTrait("NightVision")) {
					float3 += 0.2F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
				}

				if (float3 > 0.0F) {
					float3 = 0.0F;
				}
			} else {
				if (player.getVehicle().getHeadlightsOn() && player.getVehicle().getHeadlightCanEmmitLight()) {
					float3 = 1.5F - 3.0F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
					if (float3 < -0.8F) {
						float3 = -0.8F;
					}
				} else {
					float3 = 1.5F - 3.0F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
					if (float3 < -0.99F) {
						float3 = -0.99F;
					}
				}

				if (player.HasTrait("NightVision")) {
					float3 += 0.2F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
				}

				if (float3 > 1.0F) {
					float3 = 1.0F;
				}
			}

			playerSet(player.x, player.y, player.z, player.getAngle().getX(), player.getAngle().getY(), player.isDead(), player.ReanimatedCorpse != null, player.GhostMode, player.HasTrait("ShortSighted"), float1, float2, float3);
		}
	}

	public static void updateChunk(IsoChunk chunk) {
		chunkBeginUpdate(chunk.wx, chunk.wy);
		for (int int1 = 0; int1 < IsoCell.MaxHeight; ++int1) {
			for (int int2 = 0; int2 < 10; ++int2) {
				for (int int3 = 0; int3 < 10; ++int3) {
					IsoGridSquare square = chunk.getGridSquare(int3, int2, int1);
					if (square == null) {
						squareSetNull(int3, int2, int1);
					} else {
						squareBeginUpdate(int3, int2, int1);
						int int4 = 0;
						int int5;
						for (int int6 = 0; int6 < 3; ++int6) {
							for (int5 = 0; int5 < 3; ++int5) {
								for (int int7 = 0; int7 < 3; ++int7) {
									if (square.visionMatrix[int6][int5][int7]) {
										int4 |= 1 << int6 + int5 * 3 + int7 * 9;
									}
								}
							}
						}

						boolean boolean1 = square.Has(IsoObjectType.stairsTN) || square.Has(IsoObjectType.stairsMN) || square.Has(IsoObjectType.stairsTW) || square.Has(IsoObjectType.stairsMW);
						squareSet(square.w != null, square.n != null, square.e != null, square.s != null, boolean1, int4, square.getRoom() != null ? square.getBuilding().ID : -1, square.getRoomID());
						for (int5 = 0; int5 < square.getSpecialObjects().size(); ++int5) {
							IsoObject object = (IsoObject)square.getSpecialObjects().get(int5);
							if (object instanceof IsoCurtain) {
								IsoCurtain curtain = (IsoCurtain)object;
								int int8 = 0;
								if (curtain.getType() == IsoObjectType.curtainW) {
									int8 |= 4;
								} else if (curtain.getType() == IsoObjectType.curtainN) {
									int8 |= 8;
								} else if (curtain.getType() == IsoObjectType.curtainE) {
									int8 |= 16;
								} else if (curtain.getType() == IsoObjectType.curtainS) {
									int8 |= 32;
								}

								squareAddCurtain(int8, curtain.open);
							} else {
								boolean boolean2;
								IsoBarricade barricade;
								IsoBarricade barricade2;
								if (!(object instanceof IsoDoor)) {
									if (object instanceof IsoThumpable) {
										IsoThumpable thumpable = (IsoThumpable)object;
										squareAddThumpable(thumpable.north, thumpable.open, thumpable.isDoor, thumpable.getSprite().getProperties().Is("doorTrans"));
										IsoThumpable thumpable2 = (IsoThumpable)object;
										boolean boolean3 = false;
										barricade2 = thumpable2.getBarricadeOnSameSquare();
										IsoBarricade barricade3 = thumpable2.getBarricadeOnOppositeSquare();
										if (barricade2 != null) {
											boolean3 |= barricade2.isBlockVision();
										}

										if (barricade3 != null) {
											boolean3 |= barricade3.isBlockVision();
										}

										squareAddWindow(thumpable2.north, thumpable2.open, boolean3);
									} else if (object instanceof IsoWindow) {
										IsoWindow window = (IsoWindow)object;
										boolean2 = false;
										barricade = window.getBarricadeOnSameSquare();
										barricade2 = window.getBarricadeOnOppositeSquare();
										if (barricade != null) {
											boolean2 |= barricade.isBlockVision();
										}

										if (barricade2 != null) {
											boolean2 |= barricade2.isBlockVision();
										}

										squareAddWindow(window.north, window.open, boolean2);
									}
								} else {
									IsoDoor door = (IsoDoor)object;
									boolean2 = door.sprite != null && door.sprite.getProperties().Is("doorTrans");
									boolean2 = boolean2 && (door.HasCurtains() == null || door.isCurtainOpen());
									barricade = door.getBarricadeOnSameSquare();
									barricade2 = door.getBarricadeOnOppositeSquare();
									if (barricade != null && barricade.isBlockVision()) {
										boolean2 = false;
									}

									if (barricade2 != null && barricade2.isBlockVision()) {
										boolean2 = false;
									}

									squareAddDoor(door.north, door.open, boolean2);
								}
							}
						}

						squareEndUpdate();
					}
				}
			}
		}

		chunkEndUpdate();
	}

	public static void update() {
		if (newLightingMethod) {
			if (IsoWorld.instance != null && IsoWorld.instance.CurrentCell != null) {
				checkLights();
				GameTime gameTime = GameTime.getInstance();
				RenderSettings renderSettings = RenderSettings.getInstance();
				boolean boolean1 = gameTime.getNightsSurvived() < SandboxOptions.instance.getElecShutModifier();
				boolean boolean2 = GameTime.getInstance().getNight() < 0.5F;
				if (boolean1 != bWasElecShut || boolean2 != bWasNight) {
					bWasElecShut = boolean1;
					bWasNight = boolean2;
					IsoGridSquare.RecalcLightTime = -1;
					gameTime.lightSourceUpdate = 100.0F;
				}

				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int1];
					if (chunkMap != null && !chunkMap.ignore) {
						RenderSettings.PlayerRenderSettings playerRenderSettings = renderSettings.getPlayerSettings(int1);
						stateBeginUpdate(int1, chunkMap.getWorldXMin(), chunkMap.getWorldYMin(), IsoChunkMap.ChunkGridWidth, IsoChunkMap.ChunkGridWidth);
						updatePlayer(int1);
						stateEndFrame(playerRenderSettings.getRmod(), playerRenderSettings.getGmod(), playerRenderSettings.getBmod(), playerRenderSettings.getAmbient(), playerRenderSettings.getNight(), playerRenderSettings.getViewDistance(), gameTime.getViewDistMax(), LosUtil.cachecleared[int1], gameTime.lightSourceUpdate);
						LosUtil.cachecleared[int1] = false;
						for (int int2 = 0; int2 < IsoChunkMap.ChunkGridWidth; ++int2) {
							for (int int3 = 0; int3 < IsoChunkMap.ChunkGridWidth; ++int3) {
								IsoChunk chunk = chunkMap.getChunk(int3, int2);
								if (chunk != null && chunk.lightCheck[int1]) {
									updateChunk(chunk);
									chunk.lightCheck[int1] = false;
								}

								if (chunk != null) {
									chunk.bLightingNeverDone[int1] = !chunkLightingDone(chunk.wx, chunk.wy);
								}
							}
						}

						stateEndUpdate();
						updateCounter[int1] = stateUpdateCounter(int1);
						if (gameTime.lightSourceUpdate > 0.0F && IsoPlayer.players[int1] != null) {
							IsoPlayer.players[int1].dirtyRecalcGridStackTime = 20.0F;
						}
					}
				}

				gameTime.lightSourceUpdate = 0.0F;
			}
		}
	}

	public static void getTorches(ArrayList arrayList) {
		if (newLightingMethod) {
			arrayList.addAll(torches);
		}
	}

	public static void stop() {
		torches.clear();
		JNILights.clear();
		destroy();
		for (int int1 = 0; int1 < updateCounter.length; ++int1) {
			updateCounter[int1] = -1;
		}

		bWasElecShut = false;
		bWasNight = false;
	}

	public static native void configure(float float1);

	public static native void scrollLeft(int int1);

	public static native void scrollRight(int int1);

	public static native void scrollUp(int int1);

	public static native void scrollDown(int int1);

	public static native void stateBeginUpdate(int int1, int int2, int int3, int int4, int int5);

	public static native void stateEndFrame(float float1, float float2, float float3, float float4, float float5, float float6, float float7, boolean boolean1, float float8);

	public static native void stateEndUpdate();

	public static native int stateUpdateCounter(int int1);

	public static native void teleport(int int1, int int2, int int3);

	public static native void DoLightingUpdateNew(long long1);

	public static native boolean WaitingForMain();

	public static native void playerBeginUpdate();

	public static native void playerSet(float float1, float float2, float float3, float float4, float float5, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, float float6, float float7, float float8);

	public static native void playerEndUpdate();

	public static native boolean chunkLightingDone(int int1, int int2);

	public static native void chunkBeginUpdate(int int1, int int2);

	public static native void chunkEndUpdate();

	public static native void squareSetNull(int int1, int int2, int int3);

	public static native void squareBeginUpdate(int int1, int int2, int int3);

	public static native void squareSet(boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, boolean boolean5, int int1, int int2, int int3);

	public static native void squareAddCurtain(int int1, boolean boolean1);

	public static native void squareAddDoor(boolean boolean1, boolean boolean2, boolean boolean3);

	public static native void squareAddThumpable(boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4);

	public static native void squareAddWindow(boolean boolean1, boolean boolean2, boolean boolean3);

	public static native void squareEndUpdate();

	public static native int getVertLight(int int1, int int2, int int3, int int4, int int5);

	public static native float getLightInfo(int int1, int int2, int int3, int int4, int int5);

	public static native float getDarkMulti(int int1, int int2, int int3, int int4);

	public static native float getTargetDarkMulti(int int1, int int2, int int3, int int4);

	public static native boolean getSeen(int int1, int int2, int int3, int int4);

	public static native boolean getCanSee(int int1, int int2, int int3, int int4);

	public static native boolean getCouldSee(int int1, int int2, int int3, int int4);

	public static native boolean getSquareLighting(int int1, int int2, int int3, int int4, int[] intArray);

	public static native void addLight(int int1, int int2, int int3, int int4, int int5, float float1, float float2, float float3, int int6, boolean boolean1);

	public static native void addTempLight(int int1, int int2, int int3, int int4, int int5, float float1, float float2, float float3, int int6);

	public static native void removeLight(int int1);

	public static native void setLightActive(int int1, boolean boolean1);

	public static native void setLightColor(int int1, float float1, float float2, float float3);

	public static native void addRoomLight(int int1, int int2, int int3, int int4, int int5, int int6, int int7, boolean boolean1);

	public static native void removeRoomLight(int int1);

	public static native void setRoomLightActive(int int1, boolean boolean1);

	public static native void updateTorch(int int1, float float1, float float2, float float3, float float4, float float5, float float6, float float7, boolean boolean1, float float8, int int2);

	public static native void removeTorch(int int1);

	public static native void destroy();

	public static final class JNILighting implements IsoGridSquare.ILighting {
		private int playerIndex;
		private int x;
		private int y;
		private int z;
		private ColorInfo lightInfo = new ColorInfo();
		private byte vis;
		private float cacheDarkMulti;
		private float cacheTargetDarkMulti;
		private int[] cacheVertLight;
		private int updateTick = -1;
		private static final int[] lightInts = new int[12];

		public JNILighting(int int1, int int2, int int3, int int4) {
			this.playerIndex = int1;
			this.x = int2;
			this.y = int3;
			this.z = int4;
			this.cacheDarkMulti = 0.0F;
			this.cacheTargetDarkMulti = 0.0F;
			this.cacheVertLight = new int[8];
			for (int int5 = 0; int5 < 8; ++int5) {
				this.cacheVertLight[int5] = 0;
			}
		}

		public int lightverts(int int1) {
			return this.cacheVertLight[int1];
		}

		public float lampostTotalR() {
			return 0.0F;
		}

		public float lampostTotalG() {
			return 0.0F;
		}

		public float lampostTotalB() {
			return 0.0F;
		}

		public boolean bSeen() {
			this.update();
			return (this.vis & 1) != 0;
		}

		public boolean bCanSee() {
			this.update();
			return (this.vis & 2) != 0;
		}

		public boolean bCouldSee() {
			this.update();
			return (this.vis & 4) != 0;
		}

		public float darkMulti() {
			return this.cacheDarkMulti;
		}

		public float targetDarkMulti() {
			return this.cacheTargetDarkMulti;
		}

		public ColorInfo lightInfo() {
			this.update();
			return this.lightInfo;
		}

		public void lightverts(int int1, int int2) {
			throw new IllegalStateException();
		}

		public void lampostTotalR(float float1) {
			throw new IllegalStateException();
		}

		public void lampostTotalG(float float1) {
			throw new IllegalStateException();
		}

		public void lampostTotalB(float float1) {
			throw new IllegalStateException();
		}

		public void bSeen(boolean boolean1) {
			throw new IllegalStateException();
		}

		public void bCanSee(boolean boolean1) {
			throw new IllegalStateException();
		}

		public void bCouldSee(boolean boolean1) {
			throw new IllegalStateException();
		}

		public void darkMulti(float float1) {
			throw new IllegalStateException();
		}

		public void targetDarkMulti(float float1) {
			throw new IllegalStateException();
		}

		public void setPos(int int1, int int2, int int3) {
			this.x = int1;
			this.y = int2;
			this.z = int3;
		}

		public void reset() {
			this.updateTick = -1;
		}

		private void update() {
			if (LightingJNI.updateCounter[this.playerIndex] != -1) {
				if (this.updateTick != LightingJNI.updateCounter[this.playerIndex] && LightingJNI.getSquareLighting(this.playerIndex, this.x, this.y, this.z, lightInts)) {
					IsoPlayer player = IsoPlayer.players[this.playerIndex];
					boolean boolean1 = (this.vis & 1) != 0;
					byte byte1 = 0;
					int int1 = byte1 + 1;
					this.vis = (byte)(lightInts[byte1] & 7);
					this.lightInfo.r = (float)(lightInts[int1] & 255) / 255.0F;
					this.lightInfo.g = (float)(lightInts[int1] >> 8 & 255) / 255.0F;
					this.lightInfo.b = (float)(lightInts[int1++] >> 16 & 255) / 255.0F;
					this.cacheDarkMulti = (float)lightInts[int1++] / 100000.0F;
					this.cacheTargetDarkMulti = (float)lightInts[int1++] / 100000.0F;
					float float1 = 1.0F;
					float float2 = 1.0F;
					int int2;
					int int3;
					if (player != null) {
						int2 = this.z - (int)player.z;
						if (int2 == -1) {
							float1 = 1.0F;
							float2 = 0.85F;
						} else if (int2 < -1) {
							float1 = 0.85F;
							float2 = 0.85F;
						}

						if ((this.vis & 2) == 0 && (this.vis & 4) != 0) {
							int3 = (int)player.x;
							int int4 = (int)player.y;
							int int5 = this.x - int3;
							int int6 = this.y - int4;
							if (player.dir != IsoDirections.Max && Math.abs(int5) <= 2 && Math.abs(int6) <= 2) {
								int[] intArray = LightingJNI.ForcedVis[player.dir.index()];
								for (int int7 = 0; int7 < intArray.length; int7 += 2) {
									if (int5 == intArray[int7] && int6 == intArray[int7 + 1]) {
										this.vis = (byte)(this.vis | 2);
										break;
									}
								}
							}
						}
					}

					float float3;
					float float4;
					float float5;
					for (int2 = 0; int2 < 4; ++int2) {
						int3 = lightInts[int1++];
						float3 = (float)(int3 & 255) * float2;
						float4 = (float)((int3 & '＀') >> 8) * float2;
						float5 = (float)((int3 & 16711680) >> 16) * float2;
						this.cacheVertLight[int2] = (int)float3 << 0 | (int)float4 << 8 | (int)float5 << 16 | -16777216;
					}

					for (int2 = 4; int2 < 8; ++int2) {
						int3 = lightInts[int1++];
						float3 = (float)(int3 & 255) * float1;
						float4 = (float)((int3 & '＀') >> 8) * float1;
						float5 = (float)((int3 & 16711680) >> 16) * float1;
						this.cacheVertLight[int2] = (int)float3 << 0 | (int)float4 << 8 | (int)float5 << 16 | -16777216;
					}

					this.updateTick = LightingJNI.updateCounter[this.playerIndex];
					if (!boolean1 && (this.vis & 1) != 0) {
						IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z);
						if (square == null) {
							return;
						}

						IsoRoom room = square.getRoom();
						assert !GameServer.bServer;
						if (room != null && room.def != null && !room.def.bExplored) {
							byte byte2 = 10;
							if (player != null && player.getBuilding() == room.building) {
								byte2 = 50;
							}

							if (player != null && IsoUtils.DistanceManhatten(player.x, player.y, (float)this.x, (float)this.y) < (float)byte2 && this.z == (int)player.z) {
								room.def.bExplored = true;
								room.onSee();
								room.seen = 0;
							}
						}

						if (!GameClient.bClient) {
							Meta.instance.dealWithSquareSeen(square);
						}
					}
				}
			}
		}
	}
}
