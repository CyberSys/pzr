package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.ai.states.ClimbDownSheetRopeState;
import zombie.ai.states.ClimbSheetRopeState;
import zombie.characters.IsoPlayer;
import zombie.characters.NetworkPlayerAI;
import zombie.characters.NetworkPlayerVariables;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.core.network.ByteBufferReader;
import zombie.core.network.ByteBufferWriter;
import zombie.util.StringUtils;

public class EventUpdatePacket implements INetworkPacket {
   public final NetworkPlayerAI.Event event = new NetworkPlayerAI.Event();

   public void parse(ByteBuffer var1) {
      ByteBufferReader var2 = new ByteBufferReader(var1);
      this.event.id = var2.getShort();
      this.event.x = var2.getFloat();
      this.event.y = var2.getFloat();
      this.event.z = var2.getFloat();
      this.event.dir = var2.getByte();
      this.event.name = var2.getByte();
      this.event.type1 = var2.getUTF();
      this.event.type2 = var2.getUTF();
      this.event.type3 = var2.getUTF();
      this.event.type4 = var2.getUTF();
      this.event.param1 = var2.getFloat();
      this.event.param2 = var2.getFloat();
      this.event.walkInjury = var2.getFloat();
      this.event.walkSpeed = var2.getFloat();
      this.event.booleanVariables = var2.getInt();
   }

   public void write(ByteBufferWriter var1) {
      var1.putShort(this.event.id);
      var1.putFloat(this.event.x);
      var1.putFloat(this.event.y);
      var1.putFloat(this.event.z);
      var1.putByte(this.event.dir);
      var1.putByte(this.event.name);
      var1.putUTF(this.event.type1);
      var1.putUTF(this.event.type2);
      var1.putUTF(this.event.type3);
      var1.putUTF(this.event.type4);
      var1.putFloat(this.event.param1);
      var1.putFloat(this.event.param2);
      var1.putFloat(this.event.walkInjury);
      var1.putFloat(this.event.walkSpeed);
      var1.putInt(this.event.booleanVariables);
   }

   public int getPacketSizeBytes() {
      return 0;
   }

   public boolean set(IsoPlayer var1, String var2, boolean var3) {
      boolean var4 = false;
      this.event.id = (short)var1.OnlineID;
      this.event.x = var1.getX();
      this.event.y = var1.getY();
      this.event.z = var1.getZ();
      this.event.dir = (byte)var1.dir.index();
      this.event.type1 = null;
      this.event.type2 = null;
      this.event.type3 = null;
      this.event.type4 = null;
      this.event.param1 = 0.0F;
      this.event.param2 = 0.0F;
      this.event.booleanVariables = NetworkPlayerVariables.getBooleanVariables(var1);
      this.event.walkInjury = var1.getVariableFloat("WalkInjury", 0.0F);
      this.event.walkSpeed = var1.getVariableFloat("WalkSpeed", 0.0F);
      if (var3) {
         if (var2 == null) {
            this.event.name = -2;
         } else {
            this.event.name = -1;
            this.event.type1 = var2;
            BaseAction var5 = var1.getCharacterActions().isEmpty() ? null : (BaseAction)var1.getCharacterActions().get(0);
            if (var5 != null && var5.overrideHandModels) {
               if (var5.getPrimaryHandItem() != null) {
                  this.event.type2 = var5.getPrimaryHandItem().getStaticModel();
               }

               if (var5.getSecondaryHandItem() != null) {
                  this.event.type3 = var5.getSecondaryHandItem().getStaticModel();
               }

               if (!StringUtils.isNullOrEmpty(var5.getPrimaryHandMdl())) {
                  this.event.type2 = var5.getPrimaryHandMdl();
               }

               if (!StringUtils.isNullOrEmpty(var5.getSecondaryHandMdl())) {
                  this.event.type3 = var5.getSecondaryHandMdl();
               }
            }
         }

         var4 = true;
      } else {
         EventUpdatePacket.EventUpdate[] var10 = EventUpdatePacket.EventUpdate.values();
         int var6 = var10.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            EventUpdatePacket.EventUpdate var8 = var10[var7];
            if (var8.name().equals(var2)) {
               this.event.name = (byte)var8.ordinal();
               BaseAction var9;
               switch(var8) {
               case EventCleanBlood:
                  this.event.type1 = var1.getVariableString("PerformingAction");
                  this.event.type2 = var1.getVariableString("LootPosition");
                  var9 = var1.getCharacterActions().isEmpty() ? null : (BaseAction)var1.getCharacterActions().get(0);
                  if (var9 != null && var9.overrideHandModels) {
                     this.event.type3 = var9.getPrimaryHandMdl();
                     if (var9.getPrimaryHandItem() != null) {
                        this.event.type3 = var9.getPrimaryHandItem().getStaticModel();
                     }

                     this.event.type4 = var9.getSecondaryHandMdl();
                     if (var9.getSecondaryHandItem() != null) {
                        this.event.type3 = var9.getSecondaryHandItem().getStaticModel();
                     }
                  }
                  break;
               case EventToggleTorch:
                  this.event.param1 = var1.getActiveLightItem() != null ? 1.0F : 0.0F;
                  break;
               case EventFallClimb:
                  if (!StringUtils.isNullOrEmpty(var1.getVariableString("ClimbFenceOutcome"))) {
                     this.event.type1 = var1.getVariableString("ClimbFenceOutcome");
                     this.event.param1 = 1.0F;
                  }
                  break;
               case EventWashClothing:
                  this.event.type1 = var1.getVariableString("PerformingAction");
                  break;
               case EventClimbFence:
                  if (var1.getVariableBoolean("VaultOverRun")) {
                     this.event.param1 = -1.0F;
                  }

                  if (var1.getVariableBoolean("VaultOverSprint")) {
                     this.event.param1 = 1.0F;
                  }
                  break;
               case collideWithWall:
                  this.event.type1 = var1.getCollideType();
                  break;
               case EventTakeWater:
                  this.event.type1 = var1.getVariableString("PerformingAction");
                  this.event.type2 = var1.getVariableString("FoodType");
                  var9 = var1.getCharacterActions().isEmpty() ? null : (BaseAction)var1.getCharacterActions().get(0);
                  if (var9 != null && var9.overrideHandModels) {
                     this.event.type3 = var9.getPrimaryHandMdl();
                     if (var9.getPrimaryHandItem() != null) {
                        this.event.type3 = var9.getPrimaryHandItem().getStaticModel();
                     }

                     this.event.type4 = var9.getSecondaryHandMdl();
                     if (var9.getSecondaryHandItem() != null) {
                        this.event.type3 = var9.getSecondaryHandItem().getStaticModel();
                     }
                  }
                  break;
               case EventLootItem:
                  this.event.type1 = var1.getVariableString("LootPosition");
                  break;
               case EventAttachItem:
                  this.event.type1 = var1.getVariableString("PerformingAction");
                  this.event.type2 = var1.getVariableString("AttachAnim");
                  break;
               case EventReloading:
                  this.event.type1 = var1.getVariableString("WeaponReloadType");
                  this.event.type2 = var1.getVariableString("isLoading");
                  this.event.type3 = var1.getVariableString("isRacking");
                  this.event.type4 = var1.getVariableString("isUnloading");
                  break;
               case EventEmote:
                  this.event.type1 = var1.getVariableString("emote");
                  break;
               case EventFishing:
                  this.event.type1 = var1.getVariableString("FishingStage");
                  break;
               case EventRead:
                  this.event.type1 = var1.getVariableString("ReadType");
                  var9 = var1.getCharacterActions().isEmpty() ? null : (BaseAction)var1.getCharacterActions().get(0);
                  if (var9 != null && var9.overrideHandModels) {
                     this.event.type2 = var9.getSecondaryHandItem().getStaticModel();
                  }
                  break;
               case EventBandage:
                  this.event.type1 = var1.getVariableString("BandageType");
                  break;
               case EventWearClothing:
                  this.event.type1 = var1.getVariableString("WearClothingLocation");
                  break;
               case EventEating:
                  this.event.type1 = var1.getVariableString("PerformingAction");
                  this.event.type2 = var1.getVariableString("FoodType");
                  var9 = var1.getCharacterActions().isEmpty() ? null : (BaseAction)var1.getCharacterActions().get(0);
                  if (var9 != null && var9.overrideHandModels) {
                     if (var9.getPrimaryHandItem() != null) {
                        this.event.type3 = var9.getPrimaryHandItem().getStaticModel();
                     }

                     if (var9.getSecondaryHandItem() != null) {
                        this.event.type4 = var9.getSecondaryHandItem().getStaticModel();
                     }
                  }
                  break;
               case EventDrinking:
                  this.event.type1 = var1.getVariableString("FoodType");
                  break;
               case EventFitness:
                  this.event.type1 = var1.getVariableString("ExerciseType");
                  break;
               case EventUpdateFitness:
                  this.event.type1 = var1.getVariableString("ExerciseHand");
                  this.event.type2 = var1.getVariableString("ExerciseType");
                  this.event.param1 = var1.getVariableBoolean("FitnessStruggle") ? 1.0F : 0.0F;
                  if (var1.getPrimaryHandItem() != null) {
                     this.event.type3 = var1.getPrimaryHandItem().getStaticModel();
                  }

                  if (var1.getSecondaryHandItem() != null && var1.getSecondaryHandItem() != var1.getPrimaryHandItem()) {
                     this.event.type4 = var1.getSecondaryHandItem().getStaticModel();
                  }
                  break;
               case EventClimbRope:
               case EventClimbDownRope:
                  this.event.param1 = ClimbSheetRopeState.instance().getClimbSheetRopeSpeed(var1);
                  this.event.param2 = ClimbDownSheetRopeState.instance().getClimbDownSheetRopeSpeed(var1);
                  break;
               case wasBumped:
                  this.event.type1 = var1.getBumpType();
                  this.event.type2 = var1.getBumpFallType();
                  this.event.param1 = var1.isBumpFall() ? 1.0F : 0.0F;
                  this.event.param1 = var1.isBumpStaggered() ? 1.0F : 0.0F;
               }

               var4 = !ClimbDownSheetRopeState.instance().equals(var1.getCurrentState()) && !ClimbSheetRopeState.instance().equals(var1.getCurrentState());
            }
         }
      }

      return var4;
   }

   public static enum EventUpdate {
      EventFishing,
      EventFitness,
      EventEmote,
      EventClimbFence,
      EventClimbDownRope,
      EventClimbRope,
      EventClimbWall,
      EventClimbWindow,
      EventOpenWindow,
      EventCloseWindow,
      EventSmashWindow,
      EventSitOnGround,
      wasBumped,
      EventRead,
      EventBandage,
      EventWearClothing,
      EventEating,
      EventDrinking,
      EventReloading,
      EventAttachItem,
      EventLootItem,
      EventTakeWater,
      collideWithWall,
      EventWashClothing,
      EventUpdateFitness,
      EventFallClimb,
      EventToggleTorch,
      EventCleanBlood;

      // $FF: synthetic method
      private static EventUpdatePacket.EventUpdate[] $values() {
         return new EventUpdatePacket.EventUpdate[]{EventFishing, EventFitness, EventEmote, EventClimbFence, EventClimbDownRope, EventClimbRope, EventClimbWall, EventClimbWindow, EventOpenWindow, EventCloseWindow, EventSmashWindow, EventSitOnGround, wasBumped, EventRead, EventBandage, EventWearClothing, EventEating, EventDrinking, EventReloading, EventAttachItem, EventLootItem, EventTakeWater, collideWithWall, EventWashClothing, EventUpdateFitness, EventFallClimb, EventToggleTorch, EventCleanBlood};
      }
   }

   public static class l_send {
      public static EventUpdatePacket eventUpdatePacket = new EventUpdatePacket();
   }

   public static class l_receive {
      public static EventUpdatePacket eventUpdatePacket = new EventUpdatePacket();
   }
}
