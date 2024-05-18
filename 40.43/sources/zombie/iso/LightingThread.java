package zombie.iso;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.Display;
import zombie.GameApplet;
import zombie.GameTime;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.ui.FPSGraph;


public class LightingThread {
	public static LightingThread instance = new LightingThread();
	public Thread lightingThread;
	public boolean bFinished = false;
	public boolean bMovedMap = false;
	public static int skip = 0;
	private int[] cacheX = new int[4];
	private int[] cacheY = new int[4];
	private int[] cacheZ = new int[4];
	public volatile boolean Interrupted = false;
	public static boolean DebugLockTime = false;
	public boolean disable = false;
	public boolean UpdateDone = false;
	public int Stage = 0;
	public long UpdateTime = 0L;
	public volatile Boolean bHasLock = new Boolean(false);
	public boolean newLightingMethod = true;
	public boolean jniLighting = true;

	public boolean DoLightingUpdate(IsoCell cell, int int1) throws InterruptedException {
		if (cell == null) {
			return false;
		} else {
			IsoPlayer player = IsoPlayer.players[int1];
			if (player != null && player.getCurrentSquare() != null) {
				if (this.disable) {
					return false;
				} else if (skip < PerformanceSettings.LightingFrameSkip && PerformanceSettings.LightingFrameSkip != 3) {
					++skip;
					return false;
				} else if (skip < PerformanceSettings.LightingFrameSkip - 1 && PerformanceSettings.LightingFrameSkip == 3) {
					++skip;
					return false;
				} else if (GameServer.bServer) {
					return false;
				} else {
					skip = 0;
					byte byte1 = 8;
					IsoGameCharacter.LightInfo lightInfo = player.initLightInfo2();
					if (this.cacheX[int1] != (int)lightInfo.x || this.cacheY[int1] != (int)lightInfo.y || this.cacheZ[int1] != (int)lightInfo.z) {
						this.cacheX[int1] = (int)lightInfo.x;
						this.cacheY[int1] = (int)lightInfo.y;
						this.cacheZ[int1] = (int)lightInfo.z;
						LosUtil.cachecleared[int1] = true;
					}

					int int2;
					int int3;
					int int4;
					if (LosUtil.cachecleared[int1]) {
						int2 = 0;
						while (true) {
							if (int2 >= LosUtil.XSIZE) {
								LosUtil.cachecleared[int1] = false;
								break;
							}

							for (int3 = 0; int3 < LosUtil.YSIZE; ++int3) {
								for (int4 = 0; int4 < LosUtil.ZSIZE; ++int4) {
									LosUtil.cachedresults[int2][int3][int4][int1] = 0;
								}

								if (Thread.interrupted() || this.Interrupted) {
									return false;
								}
							}

							++int2;
						}
					}

					IsoGridSquare.rmodLT = lightInfo.rmod;
					IsoGridSquare.gmodLT = lightInfo.gmod;
					IsoGridSquare.bmodLT = lightInfo.bmod;
					IsoGridSquare square;
					for (int2 = 0; int2 < byte1; ++int2) {
						for (int3 = 0; int3 < cell.getWidthInTiles(); ++int3) {
							for (int4 = 0; int4 < cell.getWidthInTiles(); ++int4) {
								if (Thread.interrupted() || this.Interrupted) {
									return false;
								}

								if (this.bMovedMap) {
									this.bMovedMap = false;
									return false;
								}

								square = cell.getGridSquareDirect(int4, int3, int2, int1);
								if (square != null) {
									square.CalcVisibility(int1);
								}
							}
						}
					}

					for (int2 = 0; int2 < byte1; ++int2) {
						for (int3 = 0; int3 < cell.getWidthInTiles(); ++int3) {
							for (int4 = 0; int4 < cell.getWidthInTiles(); ++int4) {
								if (Thread.interrupted() || this.Interrupted) {
									return false;
								}

								if (this.bMovedMap) {
									this.bMovedMap = false;
									return false;
								}

								square = cell.getGridSquareDirect(int4, int3, int2, int1);
								if (square != null) {
									square.CalcLightInfo(int1);
									if (PerformanceSettings.LightingFrameSkip >= 1 && PerformanceSettings.LightingFrameSkip < 3) {
										cell.CalculateVertColoursForTile(square, 0, 0, 0, int1);
									}
								}
							}
						}
					}

					if (PerformanceSettings.LightingFrameSkip == 0) {
						for (int2 = 0; int2 < byte1; ++int2) {
							for (int3 = 0; int3 < cell.getWidthInTiles(); ++int3) {
								for (int4 = 0; int4 < cell.getWidthInTiles(); ++int4) {
									if (Thread.interrupted() || this.Interrupted) {
										return false;
									}

									if (this.bMovedMap) {
										this.bMovedMap = false;
										return false;
									}

									square = cell.getGridSquareDirect(int4, int3, int2, int1);
									if (square != null) {
										cell.CalculateVertColoursForTile(square, 0, 0, 0, int1);
									}
								}
							}
						}
					}

					if (Core.bDebug && FPSGraph.instance != null) {
						FPSGraph.instance.addLighting(System.currentTimeMillis());
					}

					return true;
				}
			} else {
				return false;
			}
		}
	}

	public void stop() {
		if (PerformanceSettings.LightingThread) {
			this.bFinished = true;
			while (this.lightingThread.isAlive()) {
			}

			if (this.jniLighting) {
				LightingJNI.stop();
			}

			this.lightingThread = null;
		}
	}

	public void create() {
		if (GameServer.bServer) {
			this.newLightingMethod = false;
			this.jniLighting = false;
		} else if (PerformanceSettings.LightingThread) {
			this.bFinished = false;
			if (this.jniLighting) {
				this.lightingThread = new Thread(new Runnable(){
					
					public void run() {
						while (!LightingThread.this.bFinished) {
							if (IsoWorld.instance.CurrentCell == null) {
								return;
							}

							try {
								Display.sync(PerformanceSettings.LightingFPS);
								LightingJNI.DoLightingUpdateNew(System.nanoTime());
								while (LightingJNI.WaitingForMain() && !LightingThread.this.bFinished) {
									Thread.sleep(13L);
								}

								if (Core.bDebug && FPSGraph.instance != null) {
									FPSGraph.instance.addLighting(System.currentTimeMillis());
								}
							} catch (Exception var2) {
								var2.printStackTrace();
							}
						}
					}
				});
			} else {
				this.lightingThread = new Thread(new Runnable(){
					
					public void run() {
						IsoCell var1 = IsoWorld.instance.CurrentCell;
						if (var1 != null) {
							if (!GameServer.bServer) {
								while (!LightingThread.this.bFinished) {
									if (IsoWorld.instance.CurrentCell == null) {
										LightingThread.this.bFinished = true;
									}

									while (LightingThread.this.UpdateDone) {
										if (LightingThread.this.bFinished) {
											return;
										}

										try {
											Thread.sleep((long)PerformanceSettings.LightingFPS);
										} catch (InterruptedException var15) {
										}
									}

									LightingThread.this.UpdateDone = false;
									try {
										if (!LightingThread.this.Interrupted && LightingThread.this.Stage == 0) {
											Display.sync(PerformanceSettings.LightingFPS);
										}

										long var2 = System.nanoTime();
										double var4 = 0.0;
										try {
											LightingThread.this.Interrupted = false;
											synchronized (LightingThread.this.bHasLock) {
												IsoChunkMap.bSettingChunkLighting.lock();
												LightingThread.this.bHasLock = true;
											}

											long var6 = System.nanoTime();
											if (LightingThread.this.Stage == 0) {
												GameTime.getInstance().lightingUpdate();
												LightingThread.this.Stage = 1;
											} else {
												boolean var8 = LightingThread.this.DoLightingUpdate(var1, LightingThread.this.Stage - 1);
												if (var8 || IsoPlayer.players[LightingThread.this.Stage - 1] == null || IsoPlayer.players[LightingThread.this.Stage - 1].current == null) {
													++LightingThread.this.Stage;
													if (LightingThread.this.Stage > IsoPlayer.numPlayers) {
														LightingThread.this.UpdateTime = (long)((double)System.nanoTime() / 1000000.0);
														LightingThread.this.Stage = 0;
														LightingThread.this.UpdateDone = true;
													}
												}
											}

											var4 = (double)(System.nanoTime() - var6) / 1000000.0;
										} catch (InterruptedException var17) {
											LightingThread.this.Interrupted = true;
										} finally {
											LightingThread.this.bHasLock = false;
											IsoChunkMap.bSettingChunkLighting.unlock();
										}

										double var20 = (double)(System.nanoTime() - var2) / 1000000.0;
										if (LightingThread.DebugLockTime && var20 > 10.0) {
											DebugLog.log("LightingThread time " + var4 + "/" + var20 + " ms");
										}
									} catch (Exception var19) {
										Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var19);
									}
								}
							}
						}
					}
				});
			}

			this.lightingThread.setPriority(5);
			this.lightingThread.setDaemon(true);
			this.lightingThread.setName("Lighting Thread");
			this.lightingThread.start();
		}
	}

	public void GameLoadingUpdate() {
		if (PerformanceSettings.LightingThread && !this.newLightingMethod) {
			while (!this.UpdateDone) {
				try {
					Thread.sleep(100L);
				} catch (InterruptedException interruptedException) {
				}
			}

			GameTime gameTime = GameTime.getInstance();
			gameTime.setAmbient(gameTime.TimeLerp(gameTime.getAmbientMin(), gameTime.getAmbientMax(), (float)gameTime.getDusk(), (float)gameTime.getDawn()));
			gameTime.setNight(gameTime.TimeLerp(gameTime.getNightMax(), gameTime.getNightMin(), (float)gameTime.getDusk(), (float)gameTime.getDawn()));
			float float1 = gameTime.Moon * 20.0F;
			gameTime.setViewDist(gameTime.TimeLerp(gameTime.getViewDistMin() + float1, gameTime.getViewDistMax(), (float)gameTime.getDusk(), (float)gameTime.getDawn()));
			gameTime.setNightTint(gameTime.TimeLerp(1.0F, 0.0F, (float)gameTime.getDusk(), (float)gameTime.getDawn()));
			if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
				gameTime.setNightTint(0.0F);
			}

			IsoPlayer.players[0].updateLightInfo();
			LosUtil.cachecleared[0] = true;
			GameTime.instance.lightSourceUpdate = 100.0F;
			IsoCell.bReadAltLight = !IsoCell.bReadAltLight;
			this.UpdateDone = false;
			while (!this.UpdateDone) {
				try {
					Thread.sleep(100L);
				} catch (InterruptedException interruptedException2) {
				}
			}

			IsoCell.bReadAltLight = !IsoCell.bReadAltLight;
			this.UpdateDone = false;
		}
	}

	public void update() {
		if (this.newLightingMethod) {
			if (IsoWorld.instance != null && IsoWorld.instance.CurrentCell != null) {
				if (this.jniLighting && LightingJNI.init) {
					LightingJNI.update();
				}
			}
		}
	}

	public void scrollLeft(int int1) {
		if (LightingJNI.init) {
			LightingJNI.scrollLeft(int1);
		}
	}

	public void scrollRight(int int1) {
		if (LightingJNI.init) {
			LightingJNI.scrollRight(int1);
		}
	}

	public void scrollUp(int int1) {
		if (LightingJNI.init) {
			LightingJNI.scrollUp(int1);
		}
	}

	public void scrollDown(int int1) {
		if (LightingJNI.init) {
			LightingJNI.scrollDown(int1);
		}
	}
}
