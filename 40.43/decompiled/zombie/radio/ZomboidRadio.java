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

      for(int var1 = 0; var1 < staticSounds.length; ++var1) {
         ChatElement.addNoLogText(staticSounds[var1]);
      }

      ChatElement.addNoLogText("~");
   }

   public static boolean isStaticSound(String var0) {
      if (var0 != null) {
         String[] var1 = staticSounds;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            String var4 = var1[var3];
            if (var0.equals(var4)) {
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

   public void setHasRecievedServerData(boolean var1) {
      this.hasRecievedServerData = var1;
   }

   public void addChannelName(String var1, int var2, String var3) {
      this.addChannelName(var1, var2, var3, true);
   }

   public void addChannelName(String var1, int var2, String var3, boolean var4) {
      if (var4 || !this.channelNames.containsKey(var2)) {
         if (!this.categorizedChannels.containsKey(var3)) {
            this.categorizedChannels.put(var3, new HashMap());
         }

         ((Map)this.categorizedChannels.get(var3)).put(var2, var1);
         this.channelNames.put(var2, var1);
      }

   }

   public void removeChannelName(int var1) {
      if (this.channelNames.containsKey(var1)) {
         this.channelNames.remove(var1);
         Iterator var2 = this.categorizedChannels.entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            if (((Map)var3.getValue()).containsKey(var1)) {
               ((Map)var3.getValue()).remove(var1);
            }
         }
      }

   }

   public Map GetChannelList(String var1) {
      return this.categorizedChannels.containsKey(var1) ? (Map)this.categorizedChannels.get(var1) : null;
   }

   public String getChannelName(int var1) {
      return this.channelNames.containsKey(var1) ? (String)this.channelNames.get(var1) : null;
   }

   public Map getFullChannelList() {
      return this.categorizedChannels;
   }

   public void WriteRadioServerDataPacket(ByteBufferWriter var1) {
      var1.putInt(this.categorizedChannels.size());
      Iterator var2 = this.categorizedChannels.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         GameWindow.WriteString(var1.bb, (String)var3.getKey());
         var1.putInt(((Map)var3.getValue()).size());
         Iterator var4 = ((Map)var3.getValue()).entrySet().iterator();

         while(var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            var1.putInt((Integer)var5.getKey());
            GameWindow.WriteString(var1.bb, (String)var5.getValue());
         }
      }

   }

   public void Init(int var1) {
      boolean var2 = false;
      System.out.println("");
      System.out.println("################## Radio Init ##################");
      RadioAPI.getInstance();
      GameMode var3 = this.getGameMode();
      if (DEBUG_MODE && !var3.equals(GameMode.Server)) {
         DebugLog.enableLog(DebugType.Radio, true);
         this.debugConsole = new RadioDebugConsole();
      }

      if (var3.equals(GameMode.Client)) {
         GameClient.sendRadioServerDataRequest();
         System.out.println("Radio (Client) loaded.");
         System.out.println("################################################");
      } else {
         this.scriptManager = RadioScriptManager.getInstance();
         this.scriptManager.init(var1);

         try {
            if (!Core.getInstance().isNoSave()) {
               (new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "radio" + File.separator + "data")).mkdirs();
            }

            ArrayList var4 = RadioData.fetchAllRadioData();
            Iterator var5 = var4.iterator();

            label80:
            while(var5.hasNext()) {
               RadioData var6 = (RadioData)var5.next();
               Iterator var7 = var6.getRadioChannels().iterator();

               while(true) {
                  while(true) {
                     if (!var7.hasNext()) {
                        continue label80;
                     }

                     RadioChannel var8 = (RadioChannel)var7.next();
                     RadioChannel var9 = null;
                     if (this.scriptManager.getChannels().containsKey(var8.GetFrequency())) {
                        var9 = (RadioChannel)this.scriptManager.getChannels().get(var8.GetFrequency());
                     }

                     if (var9 != null && (!var9.getRadioData().isVanilla() || var8.getRadioData().isVanilla())) {
                        System.out.println("Unable to add channel: " + var8.GetName() + ", frequency '" + var8.GetFrequency() + "' taken.");
                     } else {
                        this.scriptManager.AddChannel(var8, true);
                     }
                  }
               }
            }

            LuaEventManager.triggerEvent("OnLoadRadioScripts", this.scriptManager);
            if (var1 == -1) {
               DebugLog.log(DebugType.Radio, "Radio setting new game start times");
               SandboxOptions var11 = SandboxOptions.instance;
               int var13 = var11.TimeSinceApo.getValue() - 1;
               if (var13 < 0) {
                  var13 = 0;
               }

               DebugLog.log(DebugType.Radio, "Time since the apocalypse: " + var11.TimeSinceApo);
               if (var13 > 0) {
                  this.DaysSinceStart = (int)((float)var13 * 30.5F);
                  DebugLog.log(DebugType.Radio, "Time since the apocalypse in days: " + this.DaysSinceStart);
                  this.scriptManager.simulateScriptsUntil(this.DaysSinceStart, true);
               }

               this.checkGameModeSpecificStart();
            } else {
               boolean var12 = this.Load();
               if (!var12) {
                  SandboxOptions var14 = SandboxOptions.instance;
                  int var15 = var14.TimeSinceApo.getValue() - 1;
                  if (var15 < 0) {
                     var15 = 0;
                  }

                  this.DaysSinceStart = (int)((float)var15 * 30.5F);
                  this.DaysSinceStart += GameTime.instance.getNightsSurvived();
               }

               if (this.DaysSinceStart > 0) {
                  this.scriptManager.simulateScriptsUntil(this.DaysSinceStart, false);
               }
            }

            var2 = true;
         } catch (Exception var10) {
            ExceptionLogger.logException(var10);
         }

         if (var2) {
            System.out.println("Radio loaded.");
         }

         System.out.println("################################################");
         System.out.println("");
      }
   }

   private void checkGameModeSpecificStart() {
      Iterator var1;
      Entry var2;
      if (Core.GameMode.equals("Initial Infection")) {
         var1 = this.scriptManager.getChannels().entrySet().iterator();

         while(var1.hasNext()) {
            var2 = (Entry)var1.next();
            RadioScript var3 = ((RadioChannel)var2.getValue()).getRadioScript("init_infection");
            if (var3 != null) {
               var3.clearExitOptions();
               var3.AddExitOption(((RadioChannel)var2.getValue()).getCurrentScript().GetName(), 100, 0);
               ((RadioChannel)var2.getValue()).setActiveScript("init_infection", this.DaysSinceStart);
            } else {
               ((RadioChannel)var2.getValue()).getCurrentScript().setStartDayStamp(this.DaysSinceStart + 1);
            }
         }
      } else if (Core.GameMode.equals("Six Months Later")) {
         var1 = this.scriptManager.getChannels().entrySet().iterator();

         while(var1.hasNext()) {
            var2 = (Entry)var1.next();
            if (((RadioChannel)var2.getValue()).GetName().equals("Classified M1A1")) {
               ((RadioChannel)var2.getValue()).setActiveScript("numbers", this.DaysSinceStart);
            } else if (((RadioChannel)var2.getValue()).GetName().equals("NNR Radio")) {
               ((RadioChannel)var2.getValue()).setActiveScript("pastor", this.DaysSinceStart);
            }
         }
      }

   }

   public void Save() throws FileNotFoundException, IOException {
      if (!Core.getInstance().isNoSave()) {
         GameMode var1 = this.getGameMode();
         if (var1.equals(GameMode.Server) || var1.equals(GameMode.SinglePlayer)) {
            if (this.scriptManager == null) {
               return;
            }

            File var2 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "radio" + File.separator + "data");
            if (var2.exists() && var2.isDirectory()) {
               String var3 = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "radio" + File.separator + "data" + File.separator + "RADIO_SAVE.txt";
               File var4 = new File(var3);
               DebugLog.log(DebugType.Radio, "Saving radio: " + var3);

               try {
                  FileWriter var5 = new FileWriter(var4, false);
                  Throwable var6 = null;

                  try {
                     var5.write("DaysSinceStart = " + this.DaysSinceStart + System.lineSeparator());
                     this.scriptManager.Save(var5);
                  } catch (Throwable var16) {
                     var6 = var16;
                     throw var16;
                  } finally {
                     if (var5 != null) {
                        if (var6 != null) {
                           try {
                              var5.close();
                           } catch (Throwable var15) {
                              var6.addSuppressed(var15);
                           }
                        } else {
                           var5.close();
                        }
                     }

                  }
               } catch (Exception var18) {
                  var18.printStackTrace();
               }
            }
         }

      }
   }

   public boolean Load() throws FileNotFoundException, IOException {
      boolean var1 = false;
      GameMode var2 = this.getGameMode();
      if (var2.equals(GameMode.Server) || var2.equals(GameMode.SinglePlayer)) {
         Iterator var3 = this.scriptManager.getChannels().entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            ((RadioChannel)var4.getValue()).setActiveScriptNull();
         }

         String var39 = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "radio" + File.separator + "data" + File.separator + "RADIO_SAVE.txt";
         File var40 = new File(var39);
         if (!var40.exists()) {
            return false;
         }

         DebugLog.log(DebugType.Radio, "Loading radio save:" + var39);

         try {
            FileReader var5 = new FileReader(var40);
            Throwable var6 = null;

            try {
               BufferedReader var7 = new BufferedReader(var5);
               Throwable var8 = null;

               try {
                  String var9;
                  try {
                     while((var9 = var7.readLine()) != null) {
                        var9 = var9.trim();
                        if (var9.startsWith("DaysSinceStart")) {
                           String[] var10 = var9.split("=");
                           this.DaysSinceStart = Integer.parseInt(var10[1].trim());
                           this.scriptManager.Load(var7);
                           var1 = true;
                           break;
                        }
                     }
                  } catch (Throwable var34) {
                     var8 = var34;
                     throw var34;
                  }
               } finally {
                  if (var7 != null) {
                     if (var8 != null) {
                        try {
                           var7.close();
                        } catch (Throwable var33) {
                           var8.addSuppressed(var33);
                        }
                     } else {
                        var7.close();
                     }
                  }

               }
            } catch (Throwable var36) {
               var6 = var36;
               throw var36;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var32) {
                        var6.addSuppressed(var32);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } catch (Exception var38) {
            var38.printStackTrace();
            return false;
         }
      }

      return var1;
   }

   public void Reset() {
      instance = null;
      if (this.scriptManager != null) {
         this.scriptManager.reset();
      }

   }

   public void UpdateScripts(int var1, int var2) {
      GameMode var3 = this.getGameMode();
      if (var3.equals(GameMode.Server) || var3.equals(GameMode.SinglePlayer)) {
         if (var1 == 0 && this.lastRecordedHour != 0) {
            ++this.DaysSinceStart;
         }

         this.lastRecordedHour = var1;
         if (this.scriptManager != null) {
            this.scriptManager.UpdateScripts(this.DaysSinceStart, var1, var2);
         }

         try {
            this.Save();
         } catch (Exception var6) {
            System.out.println(var6.getMessage());
         }
      }

      if (var3.equals(GameMode.Client) || var3.equals(GameMode.SinglePlayer)) {
         Iterator var4 = this.devices.iterator();

         while(var4.hasNext()) {
            WaveSignalDevice var5 = (WaveSignalDevice)var4.next();
            if (var5.getDeviceData().getIsTurnedOn() && var5.HasPlayerInRange()) {
               var5.getDeviceData().TriggerPlayerListening(true);
            }
         }
      }

      if (var3.equals(GameMode.Client) && !this.hasRecievedServerData) {
         GameClient.sendRadioServerDataRequest();
      }

   }

   public void render() {
      GameMode var1 = this.getGameMode();
      if (DEBUG_MODE && !var1.equals(GameMode.Server) && this.debugConsole != null) {
         this.debugConsole.render();
      }

      if (!var1.equals(GameMode.Server) && this.storySoundManager != null) {
         this.storySoundManager.render();
      }

   }

   private void addFrequencyListEntry(boolean var1, DeviceData var2, int var3, int var4) {
      if (var2 != null) {
         if (!this.freqlist.containsKey(var2.getChannel())) {
            this.freqlist.put(var2.getChannel(), new ZomboidRadio.FreqListEntry(var1, var2, var3, var4));
         } else if (((ZomboidRadio.FreqListEntry)this.freqlist.get(var2.getChannel())).deviceData.getTransmitRange() < var2.getTransmitRange()) {
            ZomboidRadio.FreqListEntry var5 = (ZomboidRadio.FreqListEntry)this.freqlist.get(var2.getChannel());
            var5.isInvItem = var1;
            var5.deviceData = var2;
            var5.sourceX = var3;
            var5.sourceY = var4;
         }

      }
   }

   public void update() {
      if (DEBUG_MODE && this.debugConsole != null) {
         this.debugConsole.update();
      }

      GameMode var1 = this.getGameMode();
      if (!var1.equals(GameMode.Server) && this.storySoundManager != null) {
         this.storySoundManager.update(this.DaysSinceStart, GameTime.instance.getHour(), GameTime.instance.getMinutes());
      }

      if ((var1.equals(GameMode.Server) || var1.equals(GameMode.SinglePlayer)) && this.scriptManager != null) {
         this.scriptManager.update();
      }

      if (var1.equals(GameMode.SinglePlayer) || var1.equals(GameMode.Client)) {
         for(int var2 = 0; var2 < IsoPlayer.numPlayers; ++var2) {
            IsoPlayer var3 = IsoPlayer.players[var2];
            if (var3 != null && (this.playerLastLine[var2] == null || !this.playerLastLine[var2].equals(var3.getLastChatMessage()))) {
               ChatMessage var4 = var3.getLastChatMessage();
               if (var4 != null && !var4.equals(this.playerLastLine[var2])) {
                  this.playerLastLine[var2] = var4;
                  if (!var1.equals(GameMode.Client) || (!var3.accessLevel.equals("admin") && !var3.accessLevel.equals("gm") && !var3.accessLevel.equals("overseer") && !var3.accessLevel.equals("moderator") || !ServerOptions.instance.DisableRadioStaff.getValue() && (!ServerOptions.instance.DisableRadioAdmin.getValue() || !var3.accessLevel.equals("admin")) && (!ServerOptions.instance.DisableRadioGM.getValue() || !var3.accessLevel.equals("gm")) && (!ServerOptions.instance.DisableRadioOverseer.getValue() || !var3.accessLevel.equals("overseer")) && (!ServerOptions.instance.DisableRadioModerator.getValue() || !var3.accessLevel.equals("moderator"))) && (!ServerOptions.instance.DisableRadioInvisible.getValue() || !var3.invisible)) {
                     this.freqlist.clear();
                     if (!GameClient.bClient && !GameServer.bServer) {
                        for(int var9 = 0; var9 < IsoPlayer.numPlayers; ++var9) {
                           this.checkPlayerForDevice(IsoPlayer.players[var9], var3);
                        }
                     } else if (GameClient.bClient) {
                        ArrayList var5 = GameClient.instance.getPlayers();

                        for(int var6 = 0; var6 < var5.size(); ++var6) {
                           this.checkPlayerForDevice((IsoPlayer)var5.get(var6), var3);
                        }
                     }

                     Iterator var10 = this.broadcastDevices.iterator();

                     while(var10.hasNext()) {
                        WaveSignalDevice var12 = (WaveSignalDevice)var10.next();
                        if (var12 != null && var12.getDeviceData() != null && var12.getDeviceData().getIsTurnedOn() && var12.getDeviceData().getIsTwoWay() && var12.HasPlayerInRange() && !var12.getDeviceData().getMicIsMuted() && this.GetDistance((int)var3.getX(), (int)var3.getY(), (int)var12.getX(), (int)var12.getY()) < var12.getDeviceData().getMicRange()) {
                           this.addFrequencyListEntry(true, var12.getDeviceData(), (int)var12.getX(), (int)var12.getY());
                        }
                     }

                     if (this.freqlist.size() > 0) {
                        Color var11 = var3.getSpeakColour();
                        Iterator var13 = this.freqlist.entrySet().iterator();

                        while(var13.hasNext()) {
                           Entry var7 = (Entry)var13.next();
                           ZomboidRadio.FreqListEntry var8 = (ZomboidRadio.FreqListEntry)var7.getValue();
                           this.SendTransmission(var8.sourceX, var8.sourceY, (Integer)var7.getKey(), this.playerLastLine[var2], (String)null, var11.r, var11.g, var11.b, var8.deviceData.getTransmitRange(), false);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private void checkPlayerForDevice(IsoPlayer var1, IsoPlayer var2) {
      boolean var3 = var1 == var2;
      if (var1 != null) {
         Radio var4 = var1.getEquipedRadio();
         if (var4 != null && var4.getDeviceData() != null && var4.getDeviceData().getIsPortable() && var4.getDeviceData().getIsTwoWay() && var4.getDeviceData().getIsTurnedOn() && !var4.getDeviceData().getMicIsMuted() && (var3 || this.GetDistance((int)var2.getX(), (int)var2.getY(), (int)var1.getX(), (int)var1.getY()) < var4.getDeviceData().getMicRange())) {
            this.addFrequencyListEntry(true, var4.getDeviceData(), (int)var1.getX(), (int)var1.getY());
         }
      }

   }

   private boolean DeviceInRange(int var1, int var2, int var3, int var4, int var5) {
      return var1 > var3 - var5 && var1 < var3 + var5 && var2 > var4 - var5 && var2 < var4 + var5 && Math.sqrt(Math.pow((double)(var1 - var3), 2.0D) + Math.pow((double)(var2 - var4), 2.0D)) < (double)var5;
   }

   private int GetDistance(int var1, int var2, int var3, int var4) {
      return (int)Math.sqrt(Math.pow((double)(var1 - var3), 2.0D) + Math.pow((double)(var2 - var4), 2.0D));
   }

   private void DistributeToPlayer(IsoPlayer var1, int var2, int var3, int var4, ChatMessage var5, String var6, float var7, float var8, float var9, int var10, boolean var11) {
      if (var1 != null) {
         Radio var12 = var1.getEquipedRadio();
         if (var12 != null && var12.getDeviceData() != null && var12.getDeviceData().getIsPortable() && var12.getDeviceData().getIsTurnedOn() && var12.getDeviceData().getChannel() == var4) {
            if (var12.getDeviceData().getDeviceVolume() <= 0.0F) {
               return;
            }

            boolean var13 = false;
            int var14 = -1;
            if (var10 < 0) {
               var13 = true;
            } else {
               var14 = this.GetDistance((int)var1.getX(), (int)var1.getY(), var2, var3);
               if (var14 > 3 && var14 < var10) {
                  var13 = true;
               }
            }

            if (var13) {
               if (var10 > 0) {
                  this.hasAppliedRangeDistortion = false;
                  var5 = this.doDeviceRangeDistortion(var5, var10, var14);
               }

               if (!this.hasAppliedRangeDistortion) {
                  var12.AddDeviceText(var5, var7, var8, var9, var6, var14);
               } else {
                  var12.AddDeviceText(var5, 0.5F, 0.5F, 0.5F, var6, var14);
               }
            }
         }
      }

   }

   private void DistributeTransmission(int var1, int var2, int var3, ChatMessage var4, String var5, float var6, float var7, float var8, int var9, boolean var10) {
      int var15;
      if (!var10) {
         if (!GameClient.bClient && !GameServer.bServer) {
            for(var15 = 0; var15 < IsoPlayer.numPlayers; ++var15) {
               this.DistributeToPlayer(IsoPlayer.players[var15], var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
            }
         } else if (GameClient.bClient) {
            Iterator var11 = GameClient.IDToPlayerMap.entrySet().iterator();

            while(var11.hasNext()) {
               Entry var12 = (Entry)var11.next();
               this.DistributeToPlayer((IsoPlayer)var12.getValue(), var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
            }
         }
      }

      if (this.devices.size() != 0) {
         for(var15 = 0; var15 < this.devices.size(); ++var15) {
            WaveSignalDevice var16 = (WaveSignalDevice)this.devices.get(var15);
            if (var16 != null && var16.getDeviceData() != null && var16.getDeviceData().getIsTurnedOn() && var10 == var16.getDeviceData().getIsTelevision() && var3 == var16.getDeviceData().getChannel()) {
               boolean var13 = false;
               if (var9 == -1) {
                  var13 = true;
               } else if (var1 != (int)var16.getX() && var2 != (int)var16.getY()) {
                  var13 = true;
               }

               if (var13) {
                  int var14 = -1;
                  if (var9 > 0) {
                     this.hasAppliedRangeDistortion = false;
                     var14 = this.GetDistance((int)var16.getX(), (int)var16.getY(), var1, var2);
                     var4 = this.doDeviceRangeDistortion(var4, var9, var14);
                  }

                  if (!this.hasAppliedRangeDistortion) {
                     var16.AddDeviceText(var4.getText(), var6, var7, var8, var5, var14);
                  } else {
                     var16.AddDeviceText(var4.getText(), 0.5F, 0.5F, 0.5F, var5, var14);
                  }
               }
            }
         }

      }
   }

   private ChatMessage doDeviceRangeDistortion(ChatMessage var1, int var2, int var3) {
      float var4 = (float)var2 * 0.9F;
      if (var4 < (float)var2 && (float)var3 > var4) {
         float var5 = 100.0F * (((float)var3 - var4) / ((float)var2 - var4));
         var1.setScrambledText(this.scrambleString(var1.getText(), (int)var5, false));
         this.hasAppliedRangeDistortion = true;
      }

      return var1;
   }

   public GameMode getGameMode() {
      if (!GameClient.bClient && !GameServer.bServer) {
         return GameMode.SinglePlayer;
      } else {
         return GameServer.bServer ? GameMode.Server : GameMode.Client;
      }
   }

   public String getRandomBzztFzzt() {
      int var1 = Rand.Next(staticSounds.length);
      return staticSounds[var1];
   }

   private String applyWeatherInterference(String var1, int var2) {
      if (ClimateManager.getInstance().getWeatherInterference() <= 0.0F) {
         return var1;
      } else {
         int var3 = (int)(ClimateManager.getInstance().getWeatherInterference() * 100.0F);
         return this.scrambleString(var1, var3, var2 == -1);
      }
   }

   private String scrambleString(String var1, int var2, boolean var3) {
      return this.scrambleString(var1, var2, var3, (String)null);
   }

   public String scrambleString(String var1, int var2, boolean var3, String var4) {
      this.hasAppliedInterference = false;
      String var5 = "";
      if (var2 <= 0) {
         var5 = var1;
      } else if (var2 >= 100) {
         var5 = var4 != null ? var4 : this.getRandomBzztFzzt();
      } else {
         this.hasAppliedInterference = true;
         if (var3) {
            char[] var6 = var1.toCharArray();
            boolean var7 = false;
            boolean var8 = false;
            String var9 = "";

            for(int var10 = 0; var10 < var6.length; ++var10) {
               char var11 = var6[var10];
               if (var8) {
                  var9 = var9 + var11;
                  if (var11 == ']') {
                     var5 = var5 + var9;
                     var9 = "";
                     var8 = false;
                  }
               } else if (var11 == '[' || Character.isWhitespace(var11) && var10 > 0 && !Character.isWhitespace(var6[var10 - 1])) {
                  int var12 = Rand.Next(100);
                  if (var12 > var2) {
                     var5 = var5 + var9 + " ";
                     var7 = false;
                  } else if (!var7) {
                     var5 = var5 + (var4 != null ? var4 : this.getRandomBzztFzzt()) + " ";
                     var7 = true;
                  }

                  if (var11 == '[') {
                     var9 = "[";
                     var8 = true;
                  } else {
                     var9 = "";
                  }
               } else {
                  var9 = var9 + var11;
               }
            }
         } else {
            boolean var13 = false;
            String[] var14 = var1.split("\\s+");
            int var15 = var14.length;

            for(int var17 = 0; var17 < var15; ++var17) {
               String var16 = var14[var17];
               int var18 = Rand.Next(100);
               if (var18 > var2) {
                  var5 = var5 + var16 + " ";
                  var13 = false;
               } else if (!var13) {
                  var5 = var5 + (var4 != null ? var4 : this.getRandomBzztFzzt()) + " ";
                  var13 = true;
               }
            }
         }
      }

      return var5;
   }

   public void ReceiveTransmission(int var1, int var2, int var3, ChatMessage var4, String var5, float var6, float var7, float var8, int var9, boolean var10) {
      GameMode var11 = this.getGameMode();
      if (var11.equals(GameMode.Server)) {
         this.SendTransmission(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      } else {
         this.DistributeTransmission(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }

   }

   public void SendTransmission(int var1, int var2, ChatMessage var3, int var4) {
      Color var5 = var3.getTextColor();
      int var6 = var3.getRadioChannel();
      this.SendTransmission(var1, var2, var6, var3, (String)null, var5.r, var5.g, var5.b, var4, false);
   }

   public void SendTransmission(int var1, int var2, int var3, ChatMessage var4, String var5, float var6, float var7, float var8, int var9, boolean var10) {
      GameMode var11 = this.getGameMode();
      if (!var10 && (var11 == GameMode.Server || var11 == GameMode.SinglePlayer)) {
         this.hasAppliedInterference = false;
         var4.setText(this.applyWeatherInterference(var4.getText(), var9));
         if (this.hasAppliedInterference) {
            var6 = 0.5F;
            var7 = 0.5F;
            var8 = 0.5F;
         }
      }

      if (var11.equals(GameMode.SinglePlayer)) {
         this.ReceiveTransmission(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      } else if (var11.equals(GameMode.Server)) {
         GameServer.sendIsoWaveSignal(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      } else if (var11.equals(GameMode.Client)) {
         GameClient.sendIsoWaveSignal(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }

   }

   public void PlayerListensChannel(int var1, boolean var2, boolean var3) {
      GameMode var4 = this.getGameMode();
      if (!var4.equals(GameMode.SinglePlayer) && !var4.equals(GameMode.Server)) {
         if (var4.equals(GameMode.Client)) {
            GameClient.sendPlayerListensChannel(var1, var2, var3);
         }
      } else if (this.scriptManager != null) {
         this.scriptManager.PlayerListensChannel(var1, var2, var3);
      }

   }

   public void RegisterDevice(WaveSignalDevice var1) {
      if (var1 != null) {
         if (!GameServer.bServer && !this.devices.contains(var1)) {
            this.devices.add(var1);
         }

         if (!GameServer.bServer && var1.getDeviceData().getIsTwoWay() && !this.broadcastDevices.contains(var1)) {
            this.broadcastDevices.add(var1);
         }

      }
   }

   public void UnRegisterDevice(WaveSignalDevice var1) {
      if (var1 != null) {
         if (!GameServer.bServer && this.devices.contains(var1)) {
            this.devices.remove(var1);
         }

         if (!GameServer.bServer && var1.getDeviceData().getIsTwoWay() && this.broadcastDevices.contains(var1)) {
            this.broadcastDevices.remove(var1);
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

      public FreqListEntry(boolean var2, DeviceData var3, int var4, int var5) {
         this.isInvItem = var2;
         this.deviceData = var3;
         this.sourceX = var4;
         this.sourceY = var5;
      }
   }
}
