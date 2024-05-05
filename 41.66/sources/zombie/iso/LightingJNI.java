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
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.textures.ColorInfo;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.iso.SpriteDetails.IsoObjectType;
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
	public static final int ROOM_SPAWN_DIST = 50;
	public static boolean init = false;
	public static final int[][] ForcedVis = new int[][]{{-1, 0, -1, -1, 0, -1, 1, -1, 1, 0, -2, -2, -1, -2, 0, -2, 1, -2, 2, -2}, {-1, 1, -1, 0, -1, -1, 0, -1, 1, -1, -2, 0, -2, -1, -2, -2, -1, -2, 0, -2}, {0, 1, -1, 1, -1, 0, -1, -1, 0, -1, -2, 2, -2, 1, -2, 0, -2, -1, -2, -2}, {1, 1, 0, 1, -1, 1, -1, 0, -1, -1, 0, 2, -1, 2, -2, 2, -2, 1, -2, 0}, {1, 0, 1, 1, 0, 1, -1, 1, -1, 0, 2, 2, 1, 2, 0, 2, -1, 2, -2, 2}, {-1, 1, 0, 1, 1, 1, 1, 0, 1, -1, 2, 0, 2, 1, 2, 2, 1, 2, 0, 2}, {0, 1, 1, 1, 1, 0, 1, -1, 0, -1, 2, -2, 2, -1, 2, 0, 2, 1, 2, 2}, {-1, -1, 0, -1, 1, -1, 1, 0, 1, 1, 0, -2, 1, -2, 2, -2, 2, -1, 2, 0}};
	private static final ArrayList torches = new ArrayList();
	private static final ArrayList activeTorches = new ArrayList();
	private static final ArrayList JNILights = new ArrayList();
	private static final int[] updateCounter = new int[4];
	private static boolean bWasElecShut = false;
	private static boolean bWasNight = false;
	private static final Vector2 tempVector2 = new Vector2();
	private static final int MAX_PLAYERS = 256;
	private static final int MAX_LIGHTS_PER_PLAYER = 4;
	private static final int MAX_LIGHTS_PER_VEHICLE = 10;
	private static final ArrayList tempItems = new ArrayList();

	public static void init() {
		if (!init) {
			String string = "";
			if ("1".equals(System.getProperty("zomboid.debuglibs.lighting"))) {
				DebugLog.log("***** Loading debug version of Lighting");
				string = "d";
			}

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

	private static int getTorchIndexById(int int1) {
		for (int int2 = 0; int2 < torches.size(); ++int2) {
			IsoGameCharacter.TorchInfo torchInfo = (IsoGameCharacter.TorchInfo)torches.get(int2);
			if (torchInfo.id == int1) {
				return int2;
			}
		}

		return -1;
	}

	private static void checkTorch(IsoPlayer player, InventoryItem inventoryItem, int int1) {
		int int2 = getTorchIndexById(int1);
		IsoGameCharacter.TorchInfo torchInfo;
		if (int2 == -1) {
			torchInfo = IsoGameCharacter.TorchInfo.alloc();
			torches.add(torchInfo);
		} else {
			torchInfo = (IsoGameCharacter.TorchInfo)torches.get(int2);
		}

		torchInfo.set(player, inventoryItem);
		if (torchInfo.id == 0) {
			torchInfo.id = int1;
		}

		updateTorch(torchInfo.id, torchInfo.x, torchInfo.y, torchInfo.z, torchInfo.angleX, torchInfo.angleY, torchInfo.dist, torchInfo.strength, torchInfo.bCone, torchInfo.dot, torchInfo.focusing);
		activeTorches.add(torchInfo);
	}

	private static int checkPlayerTorches(IsoPlayer player, int int1) {
		ArrayList arrayList = tempItems;
		arrayList.clear();
		player.getActiveLightItems(arrayList);
		int int2 = Math.min(arrayList.size(), 4);
		for (int int3 = 0; int3 < int2; ++int3) {
			checkTorch(player, (InventoryItem)arrayList.get(int3), int1 * 4 + int3 + 1);
		}

		return int2;
	}

	private static void clearPlayerTorches(int int1, int int2) {
		for (int int3 = int2; int3 < 4; ++int3) {
			int int4 = int1 * 4 + int3 + 1;
			int int5 = getTorchIndexById(int4);
			if (int5 != -1) {
				IsoGameCharacter.TorchInfo torchInfo = (IsoGameCharacter.TorchInfo)torches.get(int5);
				removeTorch(torchInfo.id);
				torchInfo.id = 0;
				IsoGameCharacter.TorchInfo.release(torchInfo);
				torches.remove(int5);
				break;
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
							addTempLight(lightSource.ID, lightSource.x, lightSource.y, lightSource.z, lightSource.radius, lightSource.r, lightSource.g, lightSource.b, (int)((float)(lightSource.life * PerformanceSettings.getLockFPS()) / 30.0F));
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
						GameTime.instance.lightSourceUpdate = 100.0F;
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
						roomLight.ID = 100000 + IsoRoomLight.NextID++;
						addRoomLight(roomLight.ID, roomLight.room.building.ID, roomLight.room.def.ID, roomLight.x, roomLight.y, roomLight.z, roomLight.width, roomLight.height, roomLight.bActive);
						roomLight.bActiveJNI = roomLight.bActive;
						GameTime.instance.lightSourceUpdate = 100.0F;
					} else if (roomLight.bActiveJNI != roomLight.bActive) {
						setRoomLightActive(roomLight.ID, roomLight.bActive);
						roomLight.bActiveJNI = roomLight.bActive;
						GameTime.instance.lightSourceUpdate = 100.0F;
					}
				}
			}

			activeTorches.clear();
			if (GameClient.bClient) {
				ArrayList arrayList2 = GameClient.instance.getPlayers();
				for (int3 = 0; int3 < arrayList2.size(); ++int3) {
					IsoPlayer player = (IsoPlayer)arrayList2.get(int3);
					checkPlayerTorches(player, player.OnlineID + 1);
				}
			} else {
				for (int4 = 0; int4 < IsoPlayer.numPlayers; ++int4) {
					IsoPlayer player2 = IsoPlayer.players[int4];
					if (player2 != null && !player2.isDead() && player2.getVehicle() == null) {
						int2 = checkPlayerTorches(player2, int4);
						clearPlayerTorches(int4, int2);
					} else {
						clearPlayerTorches(int4, 0);
					}
				}
			}

			for (int4 = 0; int4 < IsoWorld.instance.CurrentCell.getVehicles().size(); ++int4) {
				BaseVehicle baseVehicle = (BaseVehicle)IsoWorld.instance.CurrentCell.getVehicles().get(int4);
				if (baseVehicle.VehicleID != -1) {
					for (int2 = 0; int2 < baseVehicle.getLightCount(); ++int2) {
						VehiclePart vehiclePart = baseVehicle.getLightByIndex(int2);
						checkTorch(vehiclePart, 1024 + baseVehicle.VehicleID * 10 + int2);
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

	public static float calculateVisionCone(IsoGameCharacter gameCharacter) {
		float float1;
		if (gameCharacter.getVehicle() == null) {
			float1 = -0.2F;
			float1 -= gameCharacter.getStats().fatigue - 0.6F;
			if (float1 > -0.2F) {
				float1 = -0.2F;
			}

			if (gameCharacter.getStats().fatigue >= 1.0F) {
				float1 -= 0.2F;
			}

			if (gameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) == 4) {
				float1 -= 0.2F;
			}

			if (gameCharacter.isInARoom()) {
				float1 -= 0.2F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
			} else {
				float1 -= 0.7F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
			}

			if (float1 < -0.9F) {
				float1 = -0.9F;
			}

			if (gameCharacter.Traits.EagleEyed.isSet()) {
				float1 += 0.2F * ClimateManager.getInstance().getDayLightStrength();
			}

			if (gameCharacter.Traits.NightVision.isSet()) {
				float1 += 0.2F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
			}

			if (float1 > 0.0F) {
				float1 = 0.0F;
			}
		} else {
			if (gameCharacter.getVehicle().getHeadlightsOn() && gameCharacter.getVehicle().getHeadlightCanEmmitLight()) {
				float1 = 0.8F - 3.0F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
				if (float1 < -0.8F) {
					float1 = -0.8F;
				}
			} else {
				float1 = 0.8F - 3.0F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
				if (float1 < -0.95F) {
					float1 = -0.95F;
				}
			}

			if (gameCharacter.Traits.NightVision.isSet()) {
				float1 += 0.2F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
			}

			if (float1 > 1.0F) {
				float1 = 1.0F;
			}
		}

		return float1;
	}

	public static void updatePlayer(int int1) {
		IsoPlayer player = IsoPlayer.players[int1];
		if (player != null) {
			float float1 = player.getStats().fatigue - 0.6F;
			if (float1 < 0.0F) {
				float1 = 0.0F;
			}

			float1 *= 2.5F;
			if (player.Traits.HardOfHearing.isSet() && float1 < 0.7F) {
				float1 = 0.7F;
			}

			float float2 = 2.0F;
			if (player.Traits.KeenHearing.isSet()) {
				float2 += 3.0F;
			}

			float float3 = calculateVisionCone(player);
			Vector2 vector2 = player.getLookVector(tempVector2);
			BaseVehicle baseVehicle = player.getVehicle();
			if (baseVehicle != null && !player.isAiming() && !player.isLookingWhileInVehicle() && baseVehicle.isDriver(player) && baseVehicle.getCurrentSpeedKmHour() < -1.0F) {
				vector2.rotate(3.1415927F);
			}

			playerSet(player.x, player.y, player.z, vector2.x, vector2.y, false, player.ReanimatedCorpse != null, player.isGhostMode(), player.Traits.ShortSighted.isSet(), float1, float2, float3);
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
						int int4 = square.visionMatrix;
						boolean boolean1 = square.Has(IsoObjectType.stairsTN) || square.Has(IsoObjectType.stairsMN) || square.Has(IsoObjectType.stairsTW) || square.Has(IsoObjectType.stairsMW);
						squareSet(square.w != null, square.n != null, square.e != null, square.s != null, boolean1, int4, square.getRoom() != null ? square.getBuilding().ID : -1, square.getRoomID());
						for (int int5 = 0; int5 < square.getSpecialObjects().size(); ++int5) {
							IsoObject object = (IsoObject)square.getSpecialObjects().get(int5);
							if (object instanceof IsoCurtain) {
								IsoCurtain curtain = (IsoCurtain)object;
								int int6 = 0;
								if (curtain.getType() == IsoObjectType.curtainW) {
									int6 |= 4;
								} else if (curtain.getType() == IsoObjectType.curtainN) {
									int6 |= 8;
								} else if (curtain.getType() == IsoObjectType.curtainE) {
									int6 |= 16;
								} else if (curtain.getType() == IsoObjectType.curtainS) {
									int6 |= 32;
								}

								squareAddCurtain(int6, curtain.open);
							} else {
								boolean boolean2;
								IsoBarricade barricade;
								IsoBarricade barricade2;
								if (!(object instanceof IsoDoor)) {
									if (object instanceof IsoThumpable) {
										IsoThumpable thumpable = (IsoThumpable)object;
										boolean2 = thumpable.getSprite().getProperties().Is("doorTrans");
										if (thumpable.isDoor && thumpable.open) {
											boolean2 = true;
										}

										squareAddThumpable(thumpable.north, thumpable.open, thumpable.isDoor, boolean2);
										IsoThumpable thumpable2 = (IsoThumpable)object;
										boolean boolean3 = false;
										IsoBarricade barricade3 = thumpable2.getBarricadeOnSameSquare();
										IsoBarricade barricade4 = thumpable2.getBarricadeOnOppositeSquare();
										if (barricade3 != null) {
											boolean3 |= barricade3.isBlockVision();
										}

										if (barricade4 != null) {
											boolean3 |= barricade4.isBlockVision();
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
									if (door.open) {
										boolean2 = true;
									} else {
										boolean2 = boolean2 && (door.HasCurtains() == null || door.isCurtainOpen());
									}

									barricade = door.getBarricadeOnSameSquare();
									barricade2 = door.getBarricadeOnOppositeSquare();
									if (barricade != null && barricade.isBlockVision()) {
										boolean2 = false;
									}

									if (barricade2 != null && barricade2.isBlockVision()) {
										boolean2 = false;
									}

									if (door.IsOpen() && IsoDoor.getGarageDoorIndex(door) != -1) {
										boolean2 = true;
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
					if (LosUtil.cachecleared[int1]) {
						LosUtil.cachecleared[int1] = false;
						IsoWorld.instance.CurrentCell.invalidatePeekedRoom(int1);
					}

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

			DeadBodyAtlas.instance.lightingUpdate(updateCounter[0], gameTime.lightSourceUpdate > 0.0F);
			gameTime.lightSourceUpdate = 0.0F;
		}
	}

	public static void getTorches(ArrayList arrayList) {
		arrayList.addAll(torches);
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
		IsoLightSource.NextID = 1;
		IsoRoomLight.NextID = 1;
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

	public static native void playerSet(float float1, float float2, float float3, float float4, float float5, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, float float6, float float7, float float8);

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

	public static native void addRoomLight(int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, boolean boolean1);

	public static native void removeRoomLight(int int1);

	public static native void setRoomLightActive(int int1, boolean boolean1);

	public static native void updateTorch(int int1, float float1, float float2, float float3, float float4, float float5, float float6, float float7, boolean boolean1, float float8, int int2);

	public static native void removeTorch(int int1);

	public static native void destroy();

	public static final class JNILighting implements IsoGridSquare.ILighting {
		private static final int RESULT_LIGHTS_PER_SQUARE = 5;
		private static final int[] lightInts = new int[43];
		private static final byte VIS_SEEN = 1;
		private static final byte VIS_CAN_SEE = 2;
		private static final byte VIS_COULD_SEE = 4;
		private int playerIndex;
		private final IsoGridSquare square;
		private ColorInfo lightInfo = new ColorInfo();
		private byte vis;
		private float cacheDarkMulti;
		private float cacheTargetDarkMulti;
		private int[] cacheVertLight;
		private int updateTick = -1;
		private int lightsCount;
		private IsoGridSquare.ResultLight[] lights;

		public JNILighting(int int1, IsoGridSquare square) {
			this.playerIndex = int1;
			this.square = square;
			this.cacheDarkMulti = 0.0F;
			this.cacheTargetDarkMulti = 0.0F;
			this.cacheVertLight = new int[8];
			for (int int2 = 0; int2 < 8; ++int2) {
				this.cacheVertLight[int2] = 0;
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

		public int resultLightCount() {
			return this.lightsCount;
		}

		public IsoGridSquare.ResultLight getResultLight(int int1) {
			return this.lights[int1];
		}

		public void reset() {
			this.updateTick = -1;
		}

		private void update() {
			if (LightingJNI.updateCounter[this.playerIndex] != -1) {
				if (this.updateTick != LightingJNI.updateCounter[this.playerIndex] && LightingJNI.getSquareLighting(this.playerIndex, this.square.x, this.square.y, this.square.z, lightInts)) {
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
						int2 = this.square.z - (int)player.z;
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
							int int5 = this.square.x - int3;
							int int6 = this.square.y - int4;
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

					this.lightsCount = lightInts[int1++];
					for (int2 = 0; int2 < this.lightsCount; ++int2) {
						if (this.lights == null) {
							this.lights = new IsoGridSquare.ResultLight[5];
						}

						if (this.lights[int2] == null) {
							this.lights[int2] = new IsoGridSquare.ResultLight();
						}

						this.lights[int2].id = lightInts[int1++];
						this.lights[int2].x = lightInts[int1++];
						this.lights[int2].y = lightInts[int1++];
						this.lights[int2].z = lightInts[int1++];
						this.lights[int2].radius = lightInts[int1++];
						int3 = lightInts[int1++];
						this.lights[int2].r = (float)(int3 & 255) / 255.0F;
						this.lights[int2].g = (float)(int3 >> 8 & 255) / 255.0F;
						this.lights[int2].b = (float)(int3 >> 16 & 255) / 255.0F;
						this.lights[int2].flags = int3 >> 24 & 255;
					}

					this.updateTick = LightingJNI.updateCounter[this.playerIndex];
					if ((this.vis & 1) != 0) {
						if (boolean1 && this.square.getRoom() != null && this.square.getRoom().def != null && !this.square.getRoom().def.bExplored) {
							boolean boolean2 = true;
						}

						this.square.checkRoomSeen(this.playerIndex);
						if (!boolean1) {
							assert !GameServer.bServer;
							if (!GameClient.bClient) {
								Meta.instance.dealWithSquareSeen(this.square);
							}
						}
					}
				}
			}
		}
	}
}
