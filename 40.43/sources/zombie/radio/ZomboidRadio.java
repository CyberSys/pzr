package zombie.radio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatElement;
import zombie.chat.ChatMessage;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.types.Radio;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.radio.StorySounds.SLSoundManager;
import zombie.radio.devices.DeviceData;
import zombie.radio.devices.WaveSignalDevice;
import zombie.radio.scripting.RadioChannel;
import zombie.radio.scripting.RadioScript;
import zombie.radio.scripting.RadioScriptManager;


public class ZomboidRadio {
	public static final String SAVE_FILE = "RADIO_SAVE.txt";
	private ArrayList devices = new ArrayList();
	private ArrayList broadcastDevices = new ArrayList();
	private RadioScriptManager scriptManager;
	private int DaysSinceStart = 0;
	private int lastRecordedHour;
	private ChatMessage[] playerLastLine = new ChatMessage[4];
	private Map channelNames = new HashMap();
	private Map categorizedChannels = new HashMap();
	private RadioDebugConsole debugConsole;
	private boolean hasRecievedServerData = false;
	private SLSoundManager storySoundManager = null;
	private static final String[] staticSounds = new String[]{"<bzzt>", "<fzzt>", "<wzzt>", "<szzt>"};
	public static int DUMMY_VALUE_NO_LONGER_USED = 1;
	public static boolean DEBUG_MODE = false;
	public static boolean DEBUG_XML = false;
	public static boolean DEBUG_SOUND = false;
	public static boolean POST_RADIO_SILENCE = false;
	private static ZomboidRadio instance;
	private HashMap freqlist = new HashMap();
	private boolean hasAppliedRangeDistortion = false;
	private boolean hasAppliedInterference = false;

	public static boolean hasInstance() {
		return instance != null;
	}

	public static ZomboidRadio getInstance() {
		if (instance == null) {
			instance = new ZomboidRadio();
		}

		return instance;
	}

	private ZomboidRadio() {
		this.lastRecordedHour = GameTime.instance.getHour();
		SLSoundManager.DEBUG = DEBUG_SOUND;
		for (int int1 = 0; int1 < staticSounds.length; ++int1) {
			ChatElement.addNoLogText(staticSounds[int1]);
		}

		ChatElement.addNoLogText("~");
	}

	public static boolean isStaticSound(String string) {
		if (string != null) {
			String[] stringArray = staticSounds;
			int int1 = stringArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				String string2 = stringArray[int2];
				if (string.equals(string2)) {
					return true;
				}
			}
		}

		return false;
	}

	public RadioScriptManager getScriptManager() {
		return this.scriptManager;
	}

	public int getDaysSinceStart() {
		return this.DaysSinceStart;
	}

	public ArrayList getDevices() {
		return this.devices;
	}

	public ArrayList getBroadcastDevices() {
		return this.broadcastDevices;
	}

	public void setHasRecievedServerData(boolean boolean1) {
		this.hasRecievedServerData = boolean1;
	}

	public void addChannelName(String string, int int1, String string2) {
		this.addChannelName(string, int1, string2, true);
	}

	public void addChannelName(String string, int int1, String string2, boolean boolean1) {
		if (boolean1 || !this.channelNames.containsKey(int1)) {
			if (!this.categorizedChannels.containsKey(string2)) {
				this.categorizedChannels.put(string2, new HashMap());
			}

			((Map)this.categorizedChannels.get(string2)).put(int1, string);
			this.channelNames.put(int1, string);
		}
	}

	public void removeChannelName(int int1) {
		if (this.channelNames.containsKey(int1)) {
			this.channelNames.remove(int1);
			Iterator iterator = this.categorizedChannels.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry)iterator.next();
				if (((Map)entry.getValue()).containsKey(int1)) {
					((Map)entry.getValue()).remove(int1);
				}
			}
		}
	}

	public Map GetChannelList(String string) {
		return this.categorizedChannels.containsKey(string) ? (Map)this.categorizedChannels.get(string) : null;
	}

	public String getChannelName(int int1) {
		return this.channelNames.containsKey(int1) ? (String)this.channelNames.get(int1) : null;
	}

	public Map getFullChannelList() {
		return this.categorizedChannels;
	}

	public void WriteRadioServerDataPacket(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putInt(this.categorizedChannels.size());
		Iterator iterator = this.categorizedChannels.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			GameWindow.WriteString(byteBufferWriter.bb, (String)entry.getKey());
			byteBufferWriter.putInt(((Map)entry.getValue()).size());
			Iterator iterator2 = ((Map)entry.getValue()).entrySet().iterator();
			while (iterator2.hasNext()) {
				Entry entry2 = (Entry)iterator2.next();
				byteBufferWriter.putInt((Integer)entry2.getKey());
				GameWindow.WriteString(byteBufferWriter.bb, (String)entry2.getValue());
			}
		}
	}

	public void Init(int int1) {
		boolean boolean1 = false;
		System.out.println("");
		System.out.println("################## Radio Init ##################");
		RadioAPI.getInstance();
		GameMode gameMode = this.getGameMode();
		if (DEBUG_MODE && !gameMode.equals(GameMode.Server)) {
			DebugLog.enableLog(DebugType.Radio, true);
			this.debugConsole = new RadioDebugConsole();
		}

		if (gameMode.equals(GameMode.Client)) {
			GameClient.sendRadioServerDataRequest();
			System.out.println("Radio (Client) loaded.");
			System.out.println("################################################");
		} else {
			this.scriptManager = RadioScriptManager.getInstance();
			this.scriptManager.init(int1);
			try {
				if (!Core.getInstance().isNoSave()) {
					(new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "radio" + File.separator + "data")).mkdirs();
				}

				ArrayList arrayList = RadioData.fetchAllRadioData();
				Iterator iterator = arrayList.iterator();
				label80: while (iterator.hasNext()) {
					RadioData radioData = (RadioData)iterator.next();
					Iterator iterator2 = radioData.getRadioChannels().iterator();
					while (true) {
						while (true) {
							if (!iterator2.hasNext()) {
								continue label80;
							}

							RadioChannel radioChannel = (RadioChannel)iterator2.next();
							RadioChannel radioChannel2 = null;
							if (this.scriptManager.getChannels().containsKey(radioChannel.GetFrequency())) {
								radioChannel2 = (RadioChannel)this.scriptManager.getChannels().get(radioChannel.GetFrequency());
							}

							if (radioChannel2 != null && (!radioChannel2.getRadioData().isVanilla() || radioChannel.getRadioData().isVanilla())) {
								System.out.println("Unable to add channel: " + radioChannel.GetName() + ", frequency \'" + radioChannel.GetFrequency() + "\' taken.");
							} else {
								this.scriptManager.AddChannel(radioChannel, true);
							}
						}
					}
				}

				LuaEventManager.triggerEvent("OnLoadRadioScripts", this.scriptManager);
				if (int1 == -1) {
					DebugLog.log(DebugType.Radio, "Radio setting new game start times");
					SandboxOptions sandboxOptions = SandboxOptions.instance;
					int int2 = sandboxOptions.TimeSinceApo.getValue() - 1;
					if (int2 < 0) {
						int2 = 0;
					}

					DebugLog.log(DebugType.Radio, "Time since the apocalypse: " + sandboxOptions.TimeSinceApo);
					if (int2 > 0) {
						this.DaysSinceStart = (int)((float)int2 * 30.5F);
						DebugLog.log(DebugType.Radio, "Time since the apocalypse in days: " + this.DaysSinceStart);
						this.scriptManager.simulateScriptsUntil(this.DaysSinceStart, true);
					}

					this.checkGameModeSpecificStart();
				} else {
					boolean boolean2 = this.Load();
					if (!boolean2) {
						SandboxOptions sandboxOptions2 = SandboxOptions.instance;
						int int3 = sandboxOptions2.TimeSinceApo.getValue() - 1;
						if (int3 < 0) {
							int3 = 0;
						}

						this.DaysSinceStart = (int)((float)int3 * 30.5F);
						this.DaysSinceStart += GameTime.instance.getNightsSurvived();
					}

					if (this.DaysSinceStart > 0) {
						this.scriptManager.simulateScriptsUntil(this.DaysSinceStart, false);
					}
				}

				boolean1 = true;
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}

			if (boolean1) {
				System.out.println("Radio loaded.");
			}

			System.out.println("################################################");
			System.out.println("");
		}
	}

	private void checkGameModeSpecificStart() {
		Iterator iterator;
		Entry entry;
		if (Core.GameMode.equals("Initial Infection")) {
			iterator = this.scriptManager.getChannels().entrySet().iterator();
			while (iterator.hasNext()) {
				entry = (Entry)iterator.next();
				RadioScript radioScript = ((RadioChannel)entry.getValue()).getRadioScript("init_infection");
				if (radioScript != null) {
					radioScript.clearExitOptions();
					radioScript.AddExitOption(((RadioChannel)entry.getValue()).getCurrentScript().GetName(), 100, 0);
					((RadioChannel)entry.getValue()).setActiveScript("init_infection", this.DaysSinceStart);
				} else {
					((RadioChannel)entry.getValue()).getCurrentScript().setStartDayStamp(this.DaysSinceStart + 1);
				}
			}
		} else if (Core.GameMode.equals("Six Months Later")) {
			iterator = this.scriptManager.getChannels().entrySet().iterator();
			while (iterator.hasNext()) {
				entry = (Entry)iterator.next();
				if (((RadioChannel)entry.getValue()).GetName().equals("Classified M1A1")) {
					((RadioChannel)entry.getValue()).setActiveScript("numbers", this.DaysSinceStart);
				} else if (((RadioChannel)entry.getValue()).GetName().equals("NNR Radio")) {
					((RadioChannel)entry.getValue()).setActiveScript("pastor", this.DaysSinceStart);
				}
			}
		}
	}

	public void Save() throws FileNotFoundException, IOException {
		if (!Core.getInstance().isNoSave()) {
			GameMode gameMode = this.getGameMode();
			if (gameMode.equals(GameMode.Server) || gameMode.equals(GameMode.SinglePlayer)) {
				if (this.scriptManager == null) {
					return;
				}

				File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "radio" + File.separator + "data");
				if (file.exists() && file.isDirectory()) {
					String string = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "radio" + File.separator + "data" + File.separator + "RADIO_SAVE.txt";
					File file2 = new File(string);
					DebugLog.log(DebugType.Radio, "Saving radio: " + string);
					try {
						FileWriter fileWriter = new FileWriter(file2, false);
						Throwable throwable = null;
						try {
							fileWriter.write("DaysSinceStart = " + this.DaysSinceStart + System.lineSeparator());
							this.scriptManager.Save(fileWriter);
						} catch (Throwable throwable2) {
							throwable = throwable2;
							throw throwable2;
						} finally {
							if (fileWriter != null) {
								if (throwable != null) {
									try {
										fileWriter.close();
									} catch (Throwable throwable3) {
										throwable.addSuppressed(throwable3);
									}
								} else {
									fileWriter.close();
								}
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		}
	}

	public boolean Load() throws FileNotFoundException, IOException {
		boolean boolean1 = false;
		GameMode gameMode = this.getGameMode();
		if (gameMode.equals(GameMode.Server) || gameMode.equals(GameMode.SinglePlayer)) {
			Iterator iterator = this.scriptManager.getChannels().entrySet().iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry)iterator.next();
				((RadioChannel)entry.getValue()).setActiveScriptNull();
			}

			String string = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "radio" + File.separator + "data" + File.separator + "RADIO_SAVE.txt";
			File file = new File(string);
			if (!file.exists()) {
				return false;
			}

			DebugLog.log(DebugType.Radio, "Loading radio save:" + string);
			try {
				FileReader fileReader = new FileReader(file);
				Throwable throwable = null;
				try {
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					Throwable throwable2 = null;
					try {
						String string2;
						try {
							while ((string2 = bufferedReader.readLine()) != null) {
								string2 = string2.trim();
								if (string2.startsWith("DaysSinceStart")) {
									String[] stringArray = string2.split("=");
									this.DaysSinceStart = Integer.parseInt(stringArray[1].trim());
									this.scriptManager.Load(bufferedReader);
									boolean1 = true;
									break;
								}
							}
						} catch (Throwable throwable3) {
							throwable2 = throwable3;
							throw throwable3;
						}
					} finally {
						if (bufferedReader != null) {
							if (throwable2 != null) {
								try {
									bufferedReader.close();
								} catch (Throwable throwable4) {
									throwable2.addSuppressed(throwable4);
								}
							} else {
								bufferedReader.close();
							}
						}
					}
				} catch (Throwable throwable5) {
					throwable = throwable5;
					throw throwable5;
				} finally {
					if (fileReader != null) {
						if (throwable != null) {
							try {
								fileReader.close();
							} catch (Throwable throwable6) {
								throwable.addSuppressed(throwable6);
							}
						} else {
							fileReader.close();
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				return false;
			}
		}

		return boolean1;
	}

	public void Reset() {
		instance = null;
		if (this.scriptManager != null) {
			this.scriptManager.reset();
		}
	}

	public void UpdateScripts(int int1, int int2) {
		GameMode gameMode = this.getGameMode();
		if (gameMode.equals(GameMode.Server) || gameMode.equals(GameMode.SinglePlayer)) {
			if (int1 == 0 && this.lastRecordedHour != 0) {
				++this.DaysSinceStart;
			}

			this.lastRecordedHour = int1;
			if (this.scriptManager != null) {
				this.scriptManager.UpdateScripts(this.DaysSinceStart, int1, int2);
			}

			try {
				this.Save();
			} catch (Exception exception) {
				System.out.println(exception.getMessage());
			}
		}

		if (gameMode.equals(GameMode.Client) || gameMode.equals(GameMode.SinglePlayer)) {
			Iterator iterator = this.devices.iterator();
			while (iterator.hasNext()) {
				WaveSignalDevice waveSignalDevice = (WaveSignalDevice)iterator.next();
				if (waveSignalDevice.getDeviceData().getIsTurnedOn() && waveSignalDevice.HasPlayerInRange()) {
					waveSignalDevice.getDeviceData().TriggerPlayerListening(true);
				}
			}
		}

		if (gameMode.equals(GameMode.Client) && !this.hasRecievedServerData) {
			GameClient.sendRadioServerDataRequest();
		}
	}

	public void render() {
		GameMode gameMode = this.getGameMode();
		if (DEBUG_MODE && !gameMode.equals(GameMode.Server) && this.debugConsole != null) {
			this.debugConsole.render();
		}

		if (!gameMode.equals(GameMode.Server) && this.storySoundManager != null) {
			this.storySoundManager.render();
		}
	}

	private void addFrequencyListEntry(boolean boolean1, DeviceData deviceData, int int1, int int2) {
		if (deviceData != null) {
			if (!this.freqlist.containsKey(deviceData.getChannel())) {
				this.freqlist.put(deviceData.getChannel(), new ZomboidRadio.FreqListEntry(boolean1, deviceData, int1, int2));
			} else if (((ZomboidRadio.FreqListEntry)this.freqlist.get(deviceData.getChannel())).deviceData.getTransmitRange() < deviceData.getTransmitRange()) {
				ZomboidRadio.FreqListEntry freqListEntry = (ZomboidRadio.FreqListEntry)this.freqlist.get(deviceData.getChannel());
				freqListEntry.isInvItem = boolean1;
				freqListEntry.deviceData = deviceData;
				freqListEntry.sourceX = int1;
				freqListEntry.sourceY = int2;
			}
		}
	}

	public void update() {
		if (DEBUG_MODE && this.debugConsole != null) {
			this.debugConsole.update();
		}

		GameMode gameMode = this.getGameMode();
		if (!gameMode.equals(GameMode.Server) && this.storySoundManager != null) {
			this.storySoundManager.update(this.DaysSinceStart, GameTime.instance.getHour(), GameTime.instance.getMinutes());
		}

		if ((gameMode.equals(GameMode.Server) || gameMode.equals(GameMode.SinglePlayer)) && this.scriptManager != null) {
			this.scriptManager.update();
		}

		if (gameMode.equals(GameMode.SinglePlayer) || gameMode.equals(GameMode.Client)) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && (this.playerLastLine[int1] == null || !this.playerLastLine[int1].equals(player.getLastChatMessage()))) {
					ChatMessage chatMessage = player.getLastChatMessage();
					if (chatMessage != null && !chatMessage.equals(this.playerLastLine[int1])) {
						this.playerLastLine[int1] = chatMessage;
						if (!gameMode.equals(GameMode.Client) || (!player.accessLevel.equals("admin") && !player.accessLevel.equals("gm") && !player.accessLevel.equals("overseer") && !player.accessLevel.equals("moderator") || !ServerOptions.instance.DisableRadioStaff.getValue() && (!ServerOptions.instance.DisableRadioAdmin.getValue() || !player.accessLevel.equals("admin")) && (!ServerOptions.instance.DisableRadioGM.getValue() || !player.accessLevel.equals("gm")) && (!ServerOptions.instance.DisableRadioOverseer.getValue() || !player.accessLevel.equals("overseer")) && (!ServerOptions.instance.DisableRadioModerator.getValue() || !player.accessLevel.equals("moderator"))) && (!ServerOptions.instance.DisableRadioInvisible.getValue() || !player.invisible)) {
							this.freqlist.clear();
							if (!GameClient.bClient && !GameServer.bServer) {
								for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
									this.checkPlayerForDevice(IsoPlayer.players[int2], player);
								}
							} else if (GameClient.bClient) {
								ArrayList arrayList = GameClient.instance.getPlayers();
								for (int int3 = 0; int3 < arrayList.size(); ++int3) {
									this.checkPlayerForDevice((IsoPlayer)arrayList.get(int3), player);
								}
							}

							Iterator iterator = this.broadcastDevices.iterator();
							while (iterator.hasNext()) {
								WaveSignalDevice waveSignalDevice = (WaveSignalDevice)iterator.next();
								if (waveSignalDevice != null && waveSignalDevice.getDeviceData() != null && waveSignalDevice.getDeviceData().getIsTurnedOn() && waveSignalDevice.getDeviceData().getIsTwoWay() && waveSignalDevice.HasPlayerInRange() && !waveSignalDevice.getDeviceData().getMicIsMuted() && this.GetDistance((int)player.getX(), (int)player.getY(), (int)waveSignalDevice.getX(), (int)waveSignalDevice.getY()) < waveSignalDevice.getDeviceData().getMicRange()) {
									this.addFrequencyListEntry(true, waveSignalDevice.getDeviceData(), (int)waveSignalDevice.getX(), (int)waveSignalDevice.getY());
								}
							}

							if (this.freqlist.size() > 0) {
								Color color = player.getSpeakColour();
								Iterator iterator2 = this.freqlist.entrySet().iterator();
								while (iterator2.hasNext()) {
									Entry entry = (Entry)iterator2.next();
									ZomboidRadio.FreqListEntry freqListEntry = (ZomboidRadio.FreqListEntry)entry.getValue();
									this.SendTransmission(freqListEntry.sourceX, freqListEntry.sourceY, (Integer)entry.getKey(), this.playerLastLine[int1], (String)null, color.r, color.g, color.b, freqListEntry.deviceData.getTransmitRange(), false);
								}
							}
						}
					}
				}
			}
		}
	}

	private void checkPlayerForDevice(IsoPlayer player, IsoPlayer player2) {
		boolean boolean1 = player == player2;
		if (player != null) {
			Radio radio = player.getEquipedRadio();
			if (radio != null && radio.getDeviceData() != null && radio.getDeviceData().getIsPortable() && radio.getDeviceData().getIsTwoWay() && radio.getDeviceData().getIsTurnedOn() && !radio.getDeviceData().getMicIsMuted() && (boolean1 || this.GetDistance((int)player2.getX(), (int)player2.getY(), (int)player.getX(), (int)player.getY()) < radio.getDeviceData().getMicRange())) {
				this.addFrequencyListEntry(true, radio.getDeviceData(), (int)player.getX(), (int)player.getY());
			}
		}
	}

	private boolean DeviceInRange(int int1, int int2, int int3, int int4, int int5) {
		return int1 > int3 - int5 && int1 < int3 + int5 && int2 > int4 - int5 && int2 < int4 + int5 && Math.sqrt(Math.pow((double)(int1 - int3), 2.0) + Math.pow((double)(int2 - int4), 2.0)) < (double)int5;
	}

	private int GetDistance(int int1, int int2, int int3, int int4) {
		return (int)Math.sqrt(Math.pow((double)(int1 - int3), 2.0) + Math.pow((double)(int2 - int4), 2.0));
	}

	private void DistributeToPlayer(IsoPlayer player, int int1, int int2, int int3, ChatMessage chatMessage, String string, float float1, float float2, float float3, int int4, boolean boolean1) {
		if (player != null) {
			Radio radio = player.getEquipedRadio();
			if (radio != null && radio.getDeviceData() != null && radio.getDeviceData().getIsPortable() && radio.getDeviceData().getIsTurnedOn() && radio.getDeviceData().getChannel() == int3) {
				if (radio.getDeviceData().getDeviceVolume() <= 0.0F) {
					return;
				}

				boolean boolean2 = false;
				int int5 = -1;
				if (int4 < 0) {
					boolean2 = true;
				} else {
					int5 = this.GetDistance((int)player.getX(), (int)player.getY(), int1, int2);
					if (int5 > 3 && int5 < int4) {
						boolean2 = true;
					}
				}

				if (boolean2) {
					if (int4 > 0) {
						this.hasAppliedRangeDistortion = false;
						chatMessage = this.doDeviceRangeDistortion(chatMessage, int4, int5);
					}

					if (!this.hasAppliedRangeDistortion) {
						radio.AddDeviceText(chatMessage, float1, float2, float3, string, int5);
					} else {
						radio.AddDeviceText(chatMessage, 0.5F, 0.5F, 0.5F, string, int5);
					}
				}
			}
		}
	}

	private void DistributeTransmission(int int1, int int2, int int3, ChatMessage chatMessage, String string, float float1, float float2, float float3, int int4, boolean boolean1) {
		int int5;
		if (!boolean1) {
			if (!GameClient.bClient && !GameServer.bServer) {
				for (int5 = 0; int5 < IsoPlayer.numPlayers; ++int5) {
					this.DistributeToPlayer(IsoPlayer.players[int5], int1, int2, int3, chatMessage, string, float1, float2, float3, int4, boolean1);
				}
			} else if (GameClient.bClient) {
				Iterator iterator = GameClient.IDToPlayerMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry)iterator.next();
					this.DistributeToPlayer((IsoPlayer)entry.getValue(), int1, int2, int3, chatMessage, string, float1, float2, float3, int4, boolean1);
				}
			}
		}

		if (this.devices.size() != 0) {
			for (int5 = 0; int5 < this.devices.size(); ++int5) {
				WaveSignalDevice waveSignalDevice = (WaveSignalDevice)this.devices.get(int5);
				if (waveSignalDevice != null && waveSignalDevice.getDeviceData() != null && waveSignalDevice.getDeviceData().getIsTurnedOn() && boolean1 == waveSignalDevice.getDeviceData().getIsTelevision() && int3 == waveSignalDevice.getDeviceData().getChannel()) {
					boolean boolean2 = false;
					if (int4 == -1) {
						boolean2 = true;
					} else if (int1 != (int)waveSignalDevice.getX() && int2 != (int)waveSignalDevice.getY()) {
						boolean2 = true;
					}

					if (boolean2) {
						int int6 = -1;
						if (int4 > 0) {
							this.hasAppliedRangeDistortion = false;
							int6 = this.GetDistance((int)waveSignalDevice.getX(), (int)waveSignalDevice.getY(), int1, int2);
							chatMessage = this.doDeviceRangeDistortion(chatMessage, int4, int6);
						}

						if (!this.hasAppliedRangeDistortion) {
							waveSignalDevice.AddDeviceText(chatMessage.getText(), float1, float2, float3, string, int6);
						} else {
							waveSignalDevice.AddDeviceText(chatMessage.getText(), 0.5F, 0.5F, 0.5F, string, int6);
						}
					}
				}
			}
		}
	}

	private ChatMessage doDeviceRangeDistortion(ChatMessage chatMessage, int int1, int int2) {
		float float1 = (float)int1 * 0.9F;
		if (float1 < (float)int1 && (float)int2 > float1) {
			float float2 = 100.0F * (((float)int2 - float1) / ((float)int1 - float1));
			chatMessage.setScrambledText(this.scrambleString(chatMessage.getText(), (int)float2, false));
			this.hasAppliedRangeDistortion = true;
		}

		return chatMessage;
	}

	public GameMode getGameMode() {
		if (!GameClient.bClient && !GameServer.bServer) {
			return GameMode.SinglePlayer;
		} else {
			return GameServer.bServer ? GameMode.Server : GameMode.Client;
		}
	}

	public String getRandomBzztFzzt() {
		int int1 = Rand.Next(staticSounds.length);
		return staticSounds[int1];
	}

	private String applyWeatherInterference(String string, int int1) {
		if (ClimateManager.getInstance().getWeatherInterference() <= 0.0F) {
			return string;
		} else {
			int int2 = (int)(ClimateManager.getInstance().getWeatherInterference() * 100.0F);
			return this.scrambleString(string, int2, int1 == -1);
		}
	}

	private String scrambleString(String string, int int1, boolean boolean1) {
		return this.scrambleString(string, int1, boolean1, (String)null);
	}

	public String scrambleString(String string, int int1, boolean boolean1, String string2) {
		this.hasAppliedInterference = false;
		String string3 = "";
		if (int1 <= 0) {
			string3 = string;
		} else if (int1 >= 100) {
			string3 = string2 != null ? string2 : this.getRandomBzztFzzt();
		} else {
			this.hasAppliedInterference = true;
			if (boolean1) {
				char[] charArray = string.toCharArray();
				boolean boolean2 = false;
				boolean boolean3 = false;
				String string4 = "";
				for (int int2 = 0; int2 < charArray.length; ++int2) {
					char char1 = charArray[int2];
					if (boolean3) {
						string4 = string4 + char1;
						if (char1 == ']') {
							string3 = string3 + string4;
							string4 = "";
							boolean3 = false;
						}
					} else if (char1 == '[' || Character.isWhitespace(char1) && int2 > 0 && !Character.isWhitespace(charArray[int2 - 1])) {
						int int3 = Rand.Next(100);
						if (int3 > int1) {
							string3 = string3 + string4 + " ";
							boolean2 = false;
						} else if (!boolean2) {
							string3 = string3 + (string2 != null ? string2 : this.getRandomBzztFzzt()) + " ";
							boolean2 = true;
						}

						if (char1 == '[') {
							string4 = "[";
							boolean3 = true;
						} else {
							string4 = "";
						}
					} else {
						string4 = string4 + char1;
					}
				}
			} else {
				boolean boolean4 = false;
				String[] stringArray = string.split("\\s+");
				int int4 = stringArray.length;
				for (int int5 = 0; int5 < int4; ++int5) {
					String string5 = stringArray[int5];
					int int6 = Rand.Next(100);
					if (int6 > int1) {
						string3 = string3 + string5 + " ";
						boolean4 = false;
					} else if (!boolean4) {
						string3 = string3 + (string2 != null ? string2 : this.getRandomBzztFzzt()) + " ";
						boolean4 = true;
					}
				}
			}
		}

		return string3;
	}

	public void ReceiveTransmission(int int1, int int2, int int3, ChatMessage chatMessage, String string, float float1, float float2, float float3, int int4, boolean boolean1) {
		GameMode gameMode = this.getGameMode();
		if (gameMode.equals(GameMode.Server)) {
			this.SendTransmission(int1, int2, int3, chatMessage, string, float1, float2, float3, int4, boolean1);
		} else {
			this.DistributeTransmission(int1, int2, int3, chatMessage, string, float1, float2, float3, int4, boolean1);
		}
	}

	public void SendTransmission(int int1, int int2, ChatMessage chatMessage, int int3) {
		Color color = chatMessage.getTextColor();
		int int4 = chatMessage.getRadioChannel();
		this.SendTransmission(int1, int2, int4, chatMessage, (String)null, color.r, color.g, color.b, int3, false);
	}

	public void SendTransmission(int int1, int int2, int int3, ChatMessage chatMessage, String string, float float1, float float2, float float3, int int4, boolean boolean1) {
		GameMode gameMode = this.getGameMode();
		if (!boolean1 && (gameMode == GameMode.Server || gameMode == GameMode.SinglePlayer)) {
			this.hasAppliedInterference = false;
			chatMessage.setText(this.applyWeatherInterference(chatMessage.getText(), int4));
			if (this.hasAppliedInterference) {
				float1 = 0.5F;
				float2 = 0.5F;
				float3 = 0.5F;
			}
		}

		if (gameMode.equals(GameMode.SinglePlayer)) {
			this.ReceiveTransmission(int1, int2, int3, chatMessage, string, float1, float2, float3, int4, boolean1);
		} else if (gameMode.equals(GameMode.Server)) {
			GameServer.sendIsoWaveSignal(int1, int2, int3, chatMessage, string, float1, float2, float3, int4, boolean1);
		} else if (gameMode.equals(GameMode.Client)) {
			GameClient.sendIsoWaveSignal(int1, int2, int3, chatMessage, string, float1, float2, float3, int4, boolean1);
		}
	}

	public void PlayerListensChannel(int int1, boolean boolean1, boolean boolean2) {
		GameMode gameMode = this.getGameMode();
		if (!gameMode.equals(GameMode.SinglePlayer) && !gameMode.equals(GameMode.Server)) {
			if (gameMode.equals(GameMode.Client)) {
				GameClient.sendPlayerListensChannel(int1, boolean1, boolean2);
			}
		} else if (this.scriptManager != null) {
			this.scriptManager.PlayerListensChannel(int1, boolean1, boolean2);
		}
	}

	public void RegisterDevice(WaveSignalDevice waveSignalDevice) {
		if (waveSignalDevice != null) {
			if (!GameServer.bServer && !this.devices.contains(waveSignalDevice)) {
				this.devices.add(waveSignalDevice);
			}

			if (!GameServer.bServer && waveSignalDevice.getDeviceData().getIsTwoWay() && !this.broadcastDevices.contains(waveSignalDevice)) {
				this.broadcastDevices.add(waveSignalDevice);
			}
		}
	}

	public void UnRegisterDevice(WaveSignalDevice waveSignalDevice) {
		if (waveSignalDevice != null) {
			if (!GameServer.bServer && this.devices.contains(waveSignalDevice)) {
				this.devices.remove(waveSignalDevice);
			}

			if (!GameServer.bServer && waveSignalDevice.getDeviceData().getIsTwoWay() && this.broadcastDevices.contains(waveSignalDevice)) {
				this.broadcastDevices.remove(waveSignalDevice);
			}
		}
	}

	public Object clone() {
		return null;
	}

	private class FreqListEntry {
		public boolean isInvItem = false;
		public DeviceData deviceData;
		public int sourceX = 0;
		public int sourceY = 0;

		public FreqListEntry(boolean boolean1, DeviceData deviceData, int int1, int int2) {
			this.isInvItem = boolean1;
			this.deviceData = deviceData;
			this.sourceX = int1;
			this.sourceY = int2;
		}
	}
}
