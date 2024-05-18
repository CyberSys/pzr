package fmod.fmod;

import fmod.FMOD_STUDIO_EVENT_PROPERTY;
import fmod.javafmod;
import fmod.javafmodJNI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import zombie.GameSounds;
import zombie.SoundManager;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.characters.IsoPlayer;
import zombie.core.PerformanceSettings;
import zombie.debug.DebugLog;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class FMODSoundEmitter extends BaseSoundEmitter {
	private ArrayList ToStart = new ArrayList();
	private ArrayList Instances = new ArrayList();
	public float x;
	public float y;
	public float z;
	public EmitterType emitterType;
	public IsoObject parent;
	private ArrayDeque eventSoundPool = new ArrayDeque();
	private ArrayDeque fileSoundPool = new ArrayDeque();

	public FMODSoundEmitter() {
		SoundManager.instance.registerEmitter(this);
	}

	public void randomStart() {
	}

	public void setPos(float float1, float float2, float float3) {
		this.x = float1;
		this.y = float2;
		this.z = float3;
	}

	public int stopSound(long long1) {
		int int1;
		FMODSoundEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			if (sound.getRef() == long1) {
				sound.release();
				this.ToStart.remove(int1--);
			}
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			if (sound.getRef() == long1) {
				sound.stop();
				sound.release();
				this.Instances.remove(int1--);
			}
		}

		return 0;
	}

	public int stopSoundByName(String string) {
		GameSound gameSound = GameSounds.getSound(string);
		if (gameSound == null) {
			return 0;
		} else {
			int int1;
			FMODSoundEmitter.Sound sound;
			for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
				sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
				if (gameSound.clips.contains(sound.clip)) {
					sound.release();
					this.ToStart.remove(int1--);
				}
			}

			for (int1 = 0; int1 < this.Instances.size(); ++int1) {
				sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
				if (gameSound.clips.contains(sound.clip)) {
					sound.stop();
					sound.release();
					this.Instances.remove(int1--);
				}
			}

			return 0;
		}
	}

	public void setVolume(long long1, float float1) {
		int int1;
		FMODSoundEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			if (sound.getRef() == long1) {
				sound.volume = float1;
			}
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			if (sound.getRef() == long1) {
				sound.volume = float1;
			}
		}
	}

	public void setPitch(long long1, float float1) {
		int int1;
		FMODSoundEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			if (sound.getRef() == long1) {
				sound.pitch = float1;
			}
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			if (sound.getRef() == long1) {
				sound.pitch = float1;
			}
		}
	}

	public void setVolumeAll(float float1) {
		int int1;
		FMODSoundEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			sound.volume = float1;
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			sound.volume = float1;
		}
	}

	public void stopAll() {
		int int1;
		FMODSoundEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			sound.release();
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			sound.stop();
			sound.release();
		}

		this.ToStart.clear();
		this.Instances.clear();
	}

	public long playSound(String string) {
		if (GameClient.bClient) {
			GameClient.instance.PlayWorldSound(string, false, (int)this.x, (int)this.y, (int)this.z);
		}

		return GameServer.bServer ? 0L : this.playSoundImpl(string, (IsoObject)null);
	}

	public long playSound(String string, int int1, int int2, int int3) {
		this.x = (float)int1;
		this.y = (float)int2;
		this.z = (float)int3;
		return this.playSound(string);
	}

	public long playSound(String string, IsoGridSquare square) {
		this.x = (float)square.x + 0.5F;
		this.y = (float)square.y + 0.5F;
		this.z = (float)square.z;
		return this.playSound(string);
	}

	public long playSoundImpl(String string, IsoGridSquare square) {
		this.x = (float)square.x + 0.5F;
		this.y = (float)square.y + 0.5F;
		this.z = (float)square.z + 0.5F;
		return this.playSoundImpl(string, (IsoObject)null);
	}

	public long playSound(String string, boolean boolean1) {
		return this.playSound(string);
	}

	public long playSoundImpl(String string, boolean boolean1, IsoObject object) {
		return this.playSoundImpl(string, object);
	}

	public long playSoundLooped(String string) {
		if (GameClient.bClient) {
			GameClient.instance.PlayWorldSound(string, true, (int)this.x, (int)this.y, (int)this.z);
		}

		return this.playSoundLoopedImpl(string);
	}

	public long playSoundLoopedImpl(String string) {
		return this.playSoundImpl(string, false, (IsoObject)null);
	}

	public long playSound(String string, IsoObject object) {
		if (GameClient.bClient) {
			GameClient.instance.PlayWorldSound(string, false, (int)this.x, (int)this.y, (int)this.z);
		}

		return GameServer.bServer ? 0L : this.playSoundImpl(string, object);
	}

	public long playSoundImpl(String string, IsoObject object) {
		if (FMODManager.instance.getNumListeners() == 0) {
			return 0L;
		} else {
			GameSound gameSound = GameSounds.getSound(string);
			if (gameSound == null) {
				return 0L;
			} else {
				GameSoundClip gameSoundClip = gameSound.getRandomClip();
				return this.playClip(gameSoundClip, object);
			}
		}
	}

	public long playClip(GameSoundClip gameSoundClip, IsoObject object) {
		FMODSoundEmitter.Sound sound = this.addSound(gameSoundClip, 1.0F, object);
		return sound == null ? 0L : sound.getRef();
	}

	public long playAmbientSound(String string) {
		if (FMODManager.instance.getNumListeners() == 0) {
			return 0L;
		} else if (GameServer.bServer) {
			return 0L;
		} else {
			GameSound gameSound = GameSounds.getSound(string);
			if (gameSound == null) {
				return 0L;
			} else {
				GameSoundClip gameSoundClip = gameSound.getRandomClip();
				FMODSoundEmitter.Sound sound = this.addSound(gameSoundClip, 1.0F, (IsoObject)null);
				if (sound instanceof FMODSoundEmitter.FileSound) {
					((FMODSoundEmitter.FileSound)sound).ambient = true;
				}

				return sound == null ? 0L : sound.getRef();
			}
		}
	}

	public long playAmbientLoopedImpl(String string) {
		if (FMODManager.instance.getNumListeners() == 0) {
			return 0L;
		} else if (GameServer.bServer) {
			return 0L;
		} else {
			GameSound gameSound = GameSounds.getSound(string);
			if (gameSound == null) {
				return 0L;
			} else {
				GameSoundClip gameSoundClip = gameSound.getRandomClip();
				FMODSoundEmitter.Sound sound = this.addSound(gameSoundClip, 1.0F, (IsoObject)null);
				return sound == null ? 0L : sound.getRef();
			}
		}
	}

	public void set3D(long long1, boolean boolean1) {
		int int1;
		FMODSoundEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			if (sound.getRef() == long1) {
				sound.set3D(boolean1);
			}
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			if (sound.getRef() == long1) {
				sound.set3D(boolean1);
			}
		}
	}

	public void tick() {
		int int1;
		FMODSoundEmitter.Sound sound;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.ToStart.get(int1);
			this.Instances.add(sound);
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			sound = (FMODSoundEmitter.Sound)this.Instances.get(int1);
			boolean boolean1 = this.ToStart.contains(sound);
			if (sound.tick(boolean1)) {
				this.Instances.remove(int1--);
				sound.release();
			}
		}

		this.ToStart.clear();
	}

	public boolean hasSoundsToStart() {
		return !this.ToStart.isEmpty();
	}

	public boolean isEmpty() {
		return this.ToStart.isEmpty() && this.Instances.isEmpty();
	}

	public boolean isPlaying(long long1) {
		int int1;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			if (((FMODSoundEmitter.Sound)this.ToStart.get(int1)).getRef() == long1) {
				return true;
			}
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			if (((FMODSoundEmitter.Sound)this.Instances.get(int1)).getRef() == long1) {
				return true;
			}
		}

		return false;
	}

	public boolean isPlaying(String string) {
		int int1;
		for (int1 = 0; int1 < this.ToStart.size(); ++int1) {
			if (string.equals(((FMODSoundEmitter.Sound)this.ToStart.get(int1)).name)) {
				return true;
			}
		}

		for (int1 = 0; int1 < this.Instances.size(); ++int1) {
			if (string.equals(((FMODSoundEmitter.Sound)this.Instances.get(int1)).name)) {
				return true;
			}
		}

		return false;
	}

	private FMODSoundEmitter.EventSound allocEventSound() {
		return this.eventSoundPool.isEmpty() ? new FMODSoundEmitter.EventSound(this) : (FMODSoundEmitter.EventSound)this.eventSoundPool.pop();
	}

	private void releaseEventSound(FMODSoundEmitter.EventSound eventSound) {
		assert !this.eventSoundPool.contains(eventSound);
		this.eventSoundPool.push(eventSound);
	}

	private FMODSoundEmitter.FileSound allocFileSound() {
		return this.fileSoundPool.isEmpty() ? new FMODSoundEmitter.FileSound(this) : (FMODSoundEmitter.FileSound)this.fileSoundPool.pop();
	}

	private void releaseFileSound(FMODSoundEmitter.FileSound fileSound) {
		assert !this.fileSoundPool.contains(fileSound);
		this.fileSoundPool.push(fileSound);
	}

	private FMODSoundEmitter.Sound addSound(GameSoundClip gameSoundClip, float float1, IsoObject object) {
		if (gameSoundClip == null) {
			DebugLog.log("null sound passed to SoundEmitter.playSoundImpl");
			return null;
		} else {
			long long1;
			long long2;
			if (gameSoundClip.event != null && !gameSoundClip.event.isEmpty()) {
				long1 = javafmod.FMOD_Studio_System_GetEvent("event:/" + gameSoundClip.getEvent());
				if (long1 < 0L) {
					return null;
				} else {
					long2 = javafmod.FMOD_Studio_System_CreateEventInstance(long1);
					if (long2 < 0L) {
						return null;
					} else {
						if (gameSoundClip.hasMinDistance()) {
							javafmodJNI.FMOD_Studio_EventInstance_SetProperty(long2, FMOD_STUDIO_EVENT_PROPERTY.FMOD_STUDIO_EVENT_PROPERTY_MINIMUM_DISTANCE.ordinal(), gameSoundClip.getMinDistance());
						}

						if (gameSoundClip.hasMaxDistance()) {
							javafmodJNI.FMOD_Studio_EventInstance_SetProperty(long2, FMOD_STUDIO_EVENT_PROPERTY.FMOD_STUDIO_EVENT_PROPERTY_MAXIMUM_DISTANCE.ordinal(), gameSoundClip.getMaxDistance());
						}

						FMODSoundEmitter.EventSound eventSound = this.allocEventSound();
						eventSound.clip = gameSoundClip;
						eventSound.name = gameSoundClip.gameSound.getName();
						eventSound.eventInstance = long2;
						eventSound.volume = float1;
						eventSound.parent = object;
						eventSound.setVolume = 1.0F;
						eventSound.occlusion = -1.0F;
						this.ToStart.add(eventSound);
						return eventSound;
					}
				}
			} else if (gameSoundClip.file != null && !gameSoundClip.file.isEmpty()) {
				long1 = FMODManager.instance.loadSound(gameSoundClip.file);
				if (long1 == 0L) {
					return null;
				} else {
					long2 = javafmod.FMOD_System_PlaySound(long1, true);
					javafmod.FMOD_Channel_SetVolume(long2, 0.0F);
					javafmod.FMOD_Channel_SetPriority(long2, 9 - gameSoundClip.priority);
					javafmod.FMOD_Channel_SetChannelGroup(long2, FMODManager.instance.channelGroupInGameNonBankSounds);
					if (gameSoundClip.distanceMax == 0.0F || this.x == 0.0F && this.y == 0.0F) {
						javafmod.FMOD_Channel_SetMode(long2, (long)FMODManager.FMOD_2D);
					}

					FMODSoundEmitter.FileSound fileSound = this.allocFileSound();
					fileSound.clip = gameSoundClip;
					fileSound.name = gameSoundClip.gameSound.getName();
					fileSound.sound = long1;
					fileSound.pitch = gameSoundClip.pitch;
					fileSound.channel = long2;
					fileSound.parent = object;
					fileSound.volume = float1;
					fileSound.setVolume = 1.0F;
					fileSound.is3D = -1;
					fileSound.ambient = false;
					this.ToStart.add(fileSound);
					return fileSound;
				}
			} else {
				return null;
			}
		}
	}

	private static final class FileSound extends FMODSoundEmitter.Sound {
		long sound;
		long channel;
		byte is3D = -1;
		boolean ambient;
		float lx;
		float ly;
		float lz;
		private static ArrayDeque pool = new ArrayDeque();

		FileSound(FMODSoundEmitter fMODSoundEmitter) {
			super(fMODSoundEmitter);
		}

		long getRef() {
			return this.channel;
		}

		void stop() {
			if (this.channel != 0L) {
				javafmod.FMOD_Channel_Stop(this.channel);
				this.sound = 0L;
				this.channel = 0L;
			}
		}

		void set3D(boolean boolean1) {
			if (this.is3D != (byte)(boolean1 ? 1 : 0)) {
				javafmod.FMOD_Channel_SetMode(this.channel, boolean1 ? (long)FMODManager.FMOD_3D : (long)FMODManager.FMOD_2D);
				if (boolean1) {
					javafmod.FMOD_Channel_Set3DAttributes(this.channel, this.emitter.x, this.emitter.y, this.emitter.z * 3.0F, 0.0F, 0.0F, 0.0F);
				}

				this.is3D = (byte)(boolean1 ? 1 : 0);
			}
		}

		void release() {
			this.stop();
			this.emitter.releaseFileSound(this);
		}

		boolean tick(boolean boolean1) {
			if (boolean1 && this.clip.gameSound.isLooped()) {
				javafmod.FMOD_Channel_SetMode(this.channel, (long)FMODManager.FMOD_LOOP_NORMAL);
			}

			float float1 = this.clip.distanceMin;
			if (!boolean1 && !javafmod.FMOD_Channel_IsPlaying(this.channel)) {
				return true;
			} else {
				float float2 = this.emitter.x;
				float float3 = this.emitter.y;
				float float4 = this.emitter.z;
				if (this.clip.gameSound.is3D && (float2 != 0.0F || float3 != 0.0F)) {
					this.lx = float2;
					this.ly = float3;
					this.lz = float4;
					javafmod.FMOD_Channel_Set3DAttributes(this.channel, float2, float3, float4 * 3.0F, float2 - this.lx, float3 - this.ly, float4 * 3.0F - this.lz * 3.0F);
					if (IsoPlayer.numPlayers > 1) {
						if (boolean1) {
							javafmod.FMOD_System_SetReverbDefault(0, FMODManager.FMOD_PRESET_OFF);
							javafmod.FMOD_Channel_Set3DMinMaxDistance(this.channel, this.clip.distanceMin, this.clip.distanceMax);
							javafmod.FMOD_Channel_Set3DOcclusion(this.channel, 0.0F, 0.0F);
						}

						javafmod.FMOD_Channel_SetVolume(this.channel, this.getVolume());
						if (boolean1) {
							javafmod.FMOD_Channel_SetPaused(this.channel, false);
						}

						javafmod.FMOD_Channel_SetReverbProperties(this.channel, 0, 0.0F);
						javafmod.FMOD_Channel_SetReverbProperties(this.channel, 1, 0.0F);
						javafmod.FMOD_System_SetReverbDefault(1, FMODManager.FMOD_PRESET_OFF);
						javafmod.FMOD_Channel_Set3DOcclusion(this.channel, 0.0F, 0.0F);
						return false;
					} else {
						float float5 = this.clip.reverbMaxRange;
						float float6 = IsoUtils.DistanceManhatten(float2, float3, IsoPlayer.instance.x, IsoPlayer.instance.y, float4, IsoPlayer.instance.z) / float5;
						IsoGridSquare square = IsoPlayer.instance.getCurrentSquare();
						if (square == null) {
							javafmod.FMOD_Channel_Set3DMinMaxDistance(this.channel, float1, this.clip.distanceMax);
							javafmod.FMOD_Channel_SetVolume(this.channel, this.getVolume());
							if (boolean1) {
								javafmod.FMOD_Channel_SetPaused(this.channel, false);
							}

							return false;
						} else {
							if (square.getRoom() == null) {
								if (!this.ambient) {
									float6 += IsoPlayer.instance.numNearbyBuildingsRooms / 32.0F;
								}

								if (!this.ambient) {
									float6 += 0.08F;
								}
							} else {
								float float7 = (float)square.getRoom().Squares.size();
								if (!this.ambient) {
									float6 += float7 / 500.0F;
								}
							}

							if (float6 > 1.0F) {
								float6 = 1.0F;
							}

							float6 *= float6;
							float6 *= float6;
							float6 *= this.clip.reverbFactor;
							float6 *= 10.0F;
							if (IsoPlayer.instance.getCurrentSquare().getRoom() == null && float6 < 0.1F) {
								float6 = 0.1F;
							}

							byte byte1;
							byte byte2;
							byte byte3;
							if (!this.ambient) {
								if (square.getRoom() != null) {
									byte3 = 0;
									byte1 = 1;
									byte2 = 2;
								} else {
									byte3 = 2;
									byte1 = 0;
									byte2 = 1;
								}
							} else {
								byte3 = 2;
								byte1 = 0;
								byte2 = 1;
							}

							IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare((double)float2, (double)float3, (double)float4);
							if (square2 != null && square2.getZone() != null && (square2.getZone().getType().equals("Forest") || square2.getZone().getType().equals("DeepForest"))) {
								byte3 = 1;
								byte1 = 0;
								byte2 = 2;
							}

							javafmod.FMOD_Channel_SetReverbProperties(this.channel, byte3, 0.0F);
							javafmod.FMOD_Channel_SetReverbProperties(this.channel, byte1, 0.0F);
							javafmod.FMOD_Channel_SetReverbProperties(this.channel, byte2, 0.0F);
							javafmod.FMOD_Channel_Set3DMinMaxDistance(this.channel, float1, this.clip.distanceMax);
							IsoGridSquare square3 = IsoWorld.instance.CurrentCell.getGridSquare((double)float2, (double)float3, (double)float4);
							float float8 = 0.0F;
							float float9 = 0.0F;
							IsoRoom room;
							if (square3 != null) {
								if (!(this.emitter.parent instanceof IsoWindow) && !(this.emitter.parent instanceof IsoDoor)) {
									if (square3.getRoom() != null) {
										room = IsoPlayer.instance.getCurrentSquare().getRoom();
										if (room == null) {
											float8 = 0.33F;
											float9 = 0.23F;
										} else if (room != square3.getRoom()) {
											float8 = 0.24F;
											float9 = 0.24F;
										}

										if (room != null && square3.getRoom().getBuilding() != room.getBuilding()) {
											float8 = 1.0F;
											float9 = 0.8F;
										}

										if (room != null && square3.getRoom().def.level != (int)IsoPlayer.instance.z) {
											float8 = 0.6F;
											float9 = 0.6F;
										}
									} else {
										room = IsoPlayer.instance.getCurrentSquare().getRoom();
										if (room != null) {
											float8 = 0.79F;
											float9 = 0.59F;
										}
									}
								} else {
									room = IsoPlayer.instance.getCurrentSquare().getRoom();
									if (room != this.emitter.parent.square.getRoom()) {
										if (room != null && room.getBuilding() == this.emitter.parent.square.getBuilding()) {
											float8 = 0.33F;
											float9 = 0.33F;
										} else {
											IsoGridSquare square4 = null;
											if (this.emitter.parent instanceof IsoDoor) {
												IsoDoor door = (IsoDoor)this.emitter.parent;
												if (door.north) {
													square4 = IsoWorld.instance.CurrentCell.getGridSquare((double)door.getX(), (double)(door.getY() - 1.0F), (double)door.getZ());
												} else {
													square4 = IsoWorld.instance.CurrentCell.getGridSquare((double)(door.getX() - 1.0F), (double)door.getY(), (double)door.getZ());
												}
											} else {
												IsoWindow window = (IsoWindow)this.emitter.parent;
												if (window.north) {
													square4 = IsoWorld.instance.CurrentCell.getGridSquare((double)window.getX(), (double)(window.getY() - 1.0F), (double)window.getZ());
												} else {
													square4 = IsoWorld.instance.CurrentCell.getGridSquare((double)(window.getX() - 1.0F), (double)window.getY(), (double)window.getZ());
												}
											}

											if (square4 != null) {
												room = IsoPlayer.instance.getCurrentSquare().getRoom();
												if (room != null || square4.getRoom() == null) {
													if (room != null && square4.getRoom() != null && room.building == square4.getBuilding()) {
														if (room != square4.getRoom()) {
															if (room.def.level == square4.getZ()) {
																float8 = 0.33F;
																float9 = 0.33F;
															} else {
																float8 = 0.6F;
																float9 = 0.6F;
															}
														}
													} else {
														float8 = 0.33F;
														float9 = 0.33F;
													}
												}
											}
										}
									}
								}

								if (!square3.isCouldSee(IsoPlayer.getPlayerIndex()) && square3 != IsoPlayer.instance.getCurrentSquare()) {
									float8 += 0.4F;
								}
							} else {
								if (IsoWorld.instance.MetaGrid.getRoomAt((int)float2, (int)float3, (int)float4) != null) {
									float8 = 1.0F;
									float9 = 1.0F;
								}

								room = IsoPlayer.instance.getCurrentSquare().getRoom();
								if (room != null) {
									float8 += 0.94F;
								} else {
									float8 += 0.6F;
								}
							}

							if (square3 != null && (int)IsoPlayer.instance.z != square3.getZ()) {
								float8 *= 1.3F;
							}

							if (float8 > 0.9F) {
								float8 = 0.9F;
							}

							if (float9 > 0.9F) {
								float9 = 0.9F;
							}

							if (this.emitter.emitterType == EmitterType.Footstep && float4 > IsoPlayer.instance.z && square3.getBuilding() == IsoPlayer.instance.getBuilding()) {
								float8 = 0.0F;
								float9 = 0.0F;
							}

							if ("HouseAlarm".equals(this.name)) {
								float8 = 0.0F;
								float9 = 0.0F;
							}

							javafmod.FMOD_Channel_Set3DOcclusion(this.channel, float8, float9);
							javafmod.FMOD_Channel_SetVolume(this.channel, this.getVolume());
							javafmod.FMOD_Channel_SetPitch(this.channel, this.pitch);
							if (boolean1) {
								javafmod.FMOD_Channel_SetPaused(this.channel, false);
							}

							this.lx = float2;
							this.ly = float3;
							this.lz = float4;
							return false;
						}
					}
				} else {
					if ((float2 != 0.0F || float3 != 0.0F) && (boolean1 || float2 != this.lx || float3 != this.ly) && this.is3D == 1) {
						javafmod.FMOD_Channel_Set3DAttributes(this.channel, float2, float3, float4 * 3.0F, 0.0F, 0.0F, 0.0F);
					}

					javafmod.FMOD_Channel_SetVolume(this.channel, this.getVolume());
					javafmod.FMOD_Channel_SetPitch(this.channel, this.pitch);
					if (boolean1) {
						javafmod.FMOD_Channel_SetPaused(this.channel, false);
					}

					return false;
				}
			}
		}

		static FMODSoundEmitter.FileSound alloc(FMODSoundEmitter fMODSoundEmitter) {
			return pool.isEmpty() ? new FMODSoundEmitter.FileSound(fMODSoundEmitter) : (FMODSoundEmitter.FileSound)pool.pop();
		}
	}

	private static final class EventSound extends FMODSoundEmitter.Sound {
		long eventInstance;
		float occlusion;

		EventSound(FMODSoundEmitter fMODSoundEmitter) {
			super(fMODSoundEmitter);
		}

		long getRef() {
			return this.eventInstance;
		}

		void stop() {
			if (this.eventInstance != 0L) {
				javafmod.FMOD_Studio_StopInstance(this.eventInstance);
				javafmod.FMOD_Studio_ReleaseEventInstance(this.eventInstance);
				this.eventInstance = 0L;
			}
		}

		void set3D(boolean boolean1) {
		}

		void release() {
			this.stop();
			this.emitter.releaseEventSound(this);
		}

		boolean tick(boolean boolean1) {
			IsoPlayer player = IsoPlayer.instance;
			if (IsoPlayer.numPlayers > 1) {
				player = null;
			}

			boolean boolean2 = player != null && player.HasTrait("HardOfHearing");
			if (!boolean1) {
				int int1 = javafmod.FMOD_Studio_GetPlaybackState(this.eventInstance);
				if (int1 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index) {
					return false;
				}

				if (int1 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index) {
					javafmod.FMOD_Studio_ReleaseEventInstance(this.eventInstance);
					this.eventInstance = 0L;
					return true;
				}
			}

			javafmod.FMOD_Studio_EventInstance3D(this.eventInstance, this.emitter.x, this.emitter.y, this.emitter.z * 3.0F);
			float float1 = this.getVolume() * (boolean2 ? 0.75F : 1.0F);
			if (float1 != this.setVolume) {
				this.setVolume = float1;
				javafmod.FMOD_Studio_SetVolume(this.eventInstance, float1);
			}

			if (player != null && this.emitter.x != 0.0F && this.emitter.y != 0.0F) {
				IsoGridSquare square = player.getCurrentSquare();
				IsoGridSquare square2 = IsoWorld.instance.getCell().getGridSquare((double)this.emitter.x, (double)this.emitter.y, (double)this.emitter.z);
				if (square2 == null) {
					boolean boolean3 = true;
				}

				float float2 = 0.0F;
				if (square != null && square2 != null && !square2.isCouldSee(player.PlayerIndex)) {
					float2 = 1.0F;
				}

				if (float2 < 0.8F && boolean2) {
					float2 = 0.8F;
				}

				if (float2 != this.occlusion) {
					if (boolean1) {
						this.occlusion = float2;
					} else if (this.occlusion < float2) {
						this.occlusion += 0.1F * (30.0F / (float)PerformanceSettings.LockFPS);
						if (this.occlusion > float2) {
							this.occlusion = float2;
						}
					} else {
						this.occlusion -= 0.1F * (30.0F / (float)PerformanceSettings.LockFPS);
						if (this.occlusion < float2) {
							this.occlusion = float2;
						}
					}

					javafmod.FMOD_Studio_SetParameter(this.eventInstance, "Occlusion", this.occlusion);
				}
			}

			if (boolean1) {
				javafmod.FMOD_Studio_StartEvent(this.eventInstance);
			}

			return false;
		}
	}

	private abstract static class Sound {
		FMODSoundEmitter emitter;
		public GameSoundClip clip;
		public String name;
		public float volume = 1.0F;
		public float pitch = 1.0F;
		public IsoObject parent;
		public float setVolume = 1.0F;

		Sound(FMODSoundEmitter fMODSoundEmitter) {
			this.emitter = fMODSoundEmitter;
		}

		abstract long getRef();

		abstract void stop();

		abstract void set3D(boolean boolean1);

		abstract void release();

		abstract boolean tick(boolean boolean1);

		float getVolume() {
			this.clip = this.clip.checkReloaded();
			return this.volume * this.clip.getEffectiveVolume();
		}
	}
}
