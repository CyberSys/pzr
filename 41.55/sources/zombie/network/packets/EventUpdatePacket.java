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

	public void parse(ByteBuffer byteBuffer) {
		ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
		this.event.id = byteBufferReader.getShort();
		this.event.x = byteBufferReader.getFloat();
		this.event.y = byteBufferReader.getFloat();
		this.event.z = byteBufferReader.getFloat();
		this.event.dir = byteBufferReader.getByte();
		this.event.name = byteBufferReader.getByte();
		this.event.type1 = byteBufferReader.getUTF();
		this.event.type2 = byteBufferReader.getUTF();
		this.event.type3 = byteBufferReader.getUTF();
		this.event.type4 = byteBufferReader.getUTF();
		this.event.param1 = byteBufferReader.getFloat();
		this.event.param2 = byteBufferReader.getFloat();
		this.event.walkInjury = byteBufferReader.getFloat();
		this.event.walkSpeed = byteBufferReader.getFloat();
		this.event.booleanVariables = byteBufferReader.getInt();
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putShort(this.event.id);
		byteBufferWriter.putFloat(this.event.x);
		byteBufferWriter.putFloat(this.event.y);
		byteBufferWriter.putFloat(this.event.z);
		byteBufferWriter.putByte(this.event.dir);
		byteBufferWriter.putByte(this.event.name);
		byteBufferWriter.putUTF(this.event.type1);
		byteBufferWriter.putUTF(this.event.type2);
		byteBufferWriter.putUTF(this.event.type3);
		byteBufferWriter.putUTF(this.event.type4);
		byteBufferWriter.putFloat(this.event.param1);
		byteBufferWriter.putFloat(this.event.param2);
		byteBufferWriter.putFloat(this.event.walkInjury);
		byteBufferWriter.putFloat(this.event.walkSpeed);
		byteBufferWriter.putInt(this.event.booleanVariables);
	}

	public int getPacketSizeBytes() {
		return 0;
	}

	public boolean set(IsoPlayer player, String string, boolean boolean1) {
		boolean boolean2 = false;
		this.event.id = (short)player.OnlineID;
		this.event.x = player.getX();
		this.event.y = player.getY();
		this.event.z = player.getZ();
		this.event.dir = (byte)player.dir.index();
		this.event.type1 = null;
		this.event.type2 = null;
		this.event.type3 = null;
		this.event.type4 = null;
		this.event.param1 = 0.0F;
		this.event.param2 = 0.0F;
		this.event.booleanVariables = NetworkPlayerVariables.getBooleanVariables(player);
		this.event.walkInjury = player.getVariableFloat("WalkInjury", 0.0F);
		this.event.walkSpeed = player.getVariableFloat("WalkSpeed", 0.0F);
		if (boolean1) {
			if (string == null) {
				this.event.name = -2;
			} else {
				this.event.name = -1;
				this.event.type1 = string;
				BaseAction baseAction = player.getCharacterActions().isEmpty() ? null : (BaseAction)player.getCharacterActions().get(0);
				if (baseAction != null && baseAction.overrideHandModels) {
					if (baseAction.getPrimaryHandItem() != null) {
						this.event.type2 = baseAction.getPrimaryHandItem().getStaticModel();
					}

					if (baseAction.getSecondaryHandItem() != null) {
						this.event.type3 = baseAction.getSecondaryHandItem().getStaticModel();
					}

					if (!StringUtils.isNullOrEmpty(baseAction.getPrimaryHandMdl())) {
						this.event.type2 = baseAction.getPrimaryHandMdl();
					}

					if (!StringUtils.isNullOrEmpty(baseAction.getSecondaryHandMdl())) {
						this.event.type3 = baseAction.getSecondaryHandMdl();
					}
				}
			}

			boolean2 = true;
		} else {
			EventUpdatePacket.EventUpdate[] eventUpdateArray = EventUpdatePacket.EventUpdate.values();
			int int1 = eventUpdateArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				EventUpdatePacket.EventUpdate eventUpdate = eventUpdateArray[int2];
				if (eventUpdate.name().equals(string)) {
					this.event.name = (byte)eventUpdate.ordinal();
					BaseAction baseAction2;
					switch (eventUpdate) {
					case EventCleanBlood: 
						this.event.type1 = player.getVariableString("PerformingAction");
						this.event.type2 = player.getVariableString("LootPosition");
						baseAction2 = player.getCharacterActions().isEmpty() ? null : (BaseAction)player.getCharacterActions().get(0);
						if (baseAction2 != null && baseAction2.overrideHandModels) {
							this.event.type3 = baseAction2.getPrimaryHandMdl();
							if (baseAction2.getPrimaryHandItem() != null) {
								this.event.type3 = baseAction2.getPrimaryHandItem().getStaticModel();
							}

							this.event.type4 = baseAction2.getSecondaryHandMdl();
							if (baseAction2.getSecondaryHandItem() != null) {
								this.event.type3 = baseAction2.getSecondaryHandItem().getStaticModel();
							}
						}

						break;
					
					case EventToggleTorch: 
						this.event.param1 = player.getActiveLightItem() != null ? 1.0F : 0.0F;
						break;
					
					case EventFallClimb: 
						if (!StringUtils.isNullOrEmpty(player.getVariableString("ClimbFenceOutcome"))) {
							this.event.type1 = player.getVariableString("ClimbFenceOutcome");
							this.event.param1 = 1.0F;
						}

						break;
					
					case EventWashClothing: 
						this.event.type1 = player.getVariableString("PerformingAction");
						break;
					
					case EventClimbFence: 
						if (player.getVariableBoolean("VaultOverRun")) {
							this.event.param1 = -1.0F;
						}

						if (player.getVariableBoolean("VaultOverSprint")) {
							this.event.param1 = 1.0F;
						}

						break;
					
					case collideWithWall: 
						this.event.type1 = player.getCollideType();
						break;
					
					case EventTakeWater: 
						this.event.type1 = player.getVariableString("PerformingAction");
						this.event.type2 = player.getVariableString("FoodType");
						baseAction2 = player.getCharacterActions().isEmpty() ? null : (BaseAction)player.getCharacterActions().get(0);
						if (baseAction2 != null && baseAction2.overrideHandModels) {
							this.event.type3 = baseAction2.getPrimaryHandMdl();
							if (baseAction2.getPrimaryHandItem() != null) {
								this.event.type3 = baseAction2.getPrimaryHandItem().getStaticModel();
							}

							this.event.type4 = baseAction2.getSecondaryHandMdl();
							if (baseAction2.getSecondaryHandItem() != null) {
								this.event.type3 = baseAction2.getSecondaryHandItem().getStaticModel();
							}
						}

						break;
					
					case EventLootItem: 
						this.event.type1 = player.getVariableString("LootPosition");
						break;
					
					case EventAttachItem: 
						this.event.type1 = player.getVariableString("PerformingAction");
						this.event.type2 = player.getVariableString("AttachAnim");
						break;
					
					case EventReloading: 
						this.event.type1 = player.getVariableString("WeaponReloadType");
						this.event.type2 = player.getVariableString("isLoading");
						this.event.type3 = player.getVariableString("isRacking");
						this.event.type4 = player.getVariableString("isUnloading");
						break;
					
					case EventEmote: 
						this.event.type1 = player.getVariableString("emote");
						break;
					
					case EventFishing: 
						this.event.type1 = player.getVariableString("FishingStage");
						break;
					
					case EventRead: 
						this.event.type1 = player.getVariableString("ReadType");
						baseAction2 = player.getCharacterActions().isEmpty() ? null : (BaseAction)player.getCharacterActions().get(0);
						if (baseAction2 != null && baseAction2.overrideHandModels) {
							this.event.type2 = baseAction2.getSecondaryHandItem().getStaticModel();
						}

						break;
					
					case EventBandage: 
						this.event.type1 = player.getVariableString("BandageType");
						break;
					
					case EventWearClothing: 
						this.event.type1 = player.getVariableString("WearClothingLocation");
						break;
					
					case EventEating: 
						this.event.type1 = player.getVariableString("PerformingAction");
						this.event.type2 = player.getVariableString("FoodType");
						baseAction2 = player.getCharacterActions().isEmpty() ? null : (BaseAction)player.getCharacterActions().get(0);
						if (baseAction2 != null && baseAction2.overrideHandModels) {
							if (baseAction2.getPrimaryHandItem() != null) {
								this.event.type3 = baseAction2.getPrimaryHandItem().getStaticModel();
							}

							if (baseAction2.getSecondaryHandItem() != null) {
								this.event.type4 = baseAction2.getSecondaryHandItem().getStaticModel();
							}
						}

						break;
					
					case EventDrinking: 
						this.event.type1 = player.getVariableString("FoodType");
						break;
					
					case EventFitness: 
						this.event.type1 = player.getVariableString("ExerciseType");
						break;
					
					case EventUpdateFitness: 
						this.event.type1 = player.getVariableString("ExerciseHand");
						this.event.type2 = player.getVariableString("ExerciseType");
						this.event.param1 = player.getVariableBoolean("FitnessStruggle") ? 1.0F : 0.0F;
						if (player.getPrimaryHandItem() != null) {
							this.event.type3 = player.getPrimaryHandItem().getStaticModel();
						}

						if (player.getSecondaryHandItem() != null && player.getSecondaryHandItem() != player.getPrimaryHandItem()) {
							this.event.type4 = player.getSecondaryHandItem().getStaticModel();
						}

						break;
					
					case EventClimbRope: 
					
					case EventClimbDownRope: 
						this.event.param1 = ClimbSheetRopeState.instance().getClimbSheetRopeSpeed(player);
						this.event.param2 = ClimbDownSheetRopeState.instance().getClimbDownSheetRopeSpeed(player);
						break;
					
					case wasBumped: 
						this.event.type1 = player.getBumpType();
						this.event.type2 = player.getBumpFallType();
						this.event.param1 = player.isBumpFall() ? 1.0F : 0.0F;
						this.event.param1 = player.isBumpStaggered() ? 1.0F : 0.0F;
					
					}

					boolean2 = !ClimbDownSheetRopeState.instance().equals(player.getCurrentState()) && !ClimbSheetRopeState.instance().equals(player.getCurrentState());
				}
			}
		}

		return boolean2;
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
