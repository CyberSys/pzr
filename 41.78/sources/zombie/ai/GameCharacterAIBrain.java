package zombie.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import zombie.ai.states.ThumpState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Stance;
import zombie.characters.Stats;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorGroup;
import zombie.iso.IsoMovingObject;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.Vector3;


public final class GameCharacterAIBrain {
	private final IsoGameCharacter character;
	public final ArrayList spottedCharacters = new ArrayList();
	public boolean StepBehaviors;
	public Stance stance;
	public boolean controlledByAdvancedPathfinder;
	public boolean isInMeta;
	public final HashMap BlockedMemories = new HashMap();
	public final Vector2 AIFocusPoint = new Vector2();
	public final Vector3 nextPathTarget = new Vector3();
	public IsoMovingObject aiTarget;
	public boolean NextPathNodeInvalidated;
	public final AIBrainPlayerControlVars HumanControlVars = new AIBrainPlayerControlVars();
	String order;
	public ArrayList teammateChasingZombies = new ArrayList();
	public ArrayList chasingZombies = new ArrayList();
	public boolean allowLongTermTick = true;
	public boolean isAI = false;
	static ArrayList tempZombies = new ArrayList();
	static IsoGameCharacter compare;
	private static final Stack Vectors = new Stack();

	public IsoGameCharacter getCharacter() {
		return this.character;
	}

	public GameCharacterAIBrain(IsoGameCharacter gameCharacter) {
		this.character = gameCharacter;
	}

	public void update() {
	}

	public void postUpdateHuman(IsoPlayer player) {
	}

	public String getOrder() {
		return this.order;
	}

	public void setOrder(String string) {
		this.order = string;
	}

	public SurvivorGroup getGroup() {
		return this.character.getDescriptor().getGroup();
	}

	public int getCloseZombieCount() {
		this.character.getStats();
		return Stats.NumCloseZombies;
	}

	public IsoZombie getClosestChasingZombie(boolean boolean1) {
		IsoZombie zombie = null;
		float float1 = 1.0E7F;
		int int1;
		for (int1 = 0; int1 < this.chasingZombies.size(); ++int1) {
			IsoZombie zombie2 = (IsoZombie)this.chasingZombies.get(int1);
			float float2 = zombie2.DistTo(this.character);
			if (zombie2.isOnFloor()) {
				float2 += 2.0F;
			}

			if (!LosUtil.lineClearCollide((int)zombie2.x, (int)zombie2.y, (int)zombie2.z, (int)this.character.x, (int)this.character.y, (int)this.character.z, false) && zombie2.getStateMachine().getCurrent() != ThumpState.instance() && float2 < float1 && zombie2.target == this.character) {
				float1 = float2;
				zombie = (IsoZombie)this.chasingZombies.get(int1);
			}
		}

		float float3;
		IsoGameCharacter gameCharacter;
		IsoZombie zombie3;
		if (zombie == null && boolean1) {
			for (int1 = 0; int1 < this.getGroup().Members.size(); ++int1) {
				gameCharacter = ((SurvivorDesc)this.getGroup().Members.get(int1)).getInstance();
				zombie3 = gameCharacter.getGameCharacterAIBrain().getClosestChasingZombie(false);
				if (zombie3 != null) {
					float3 = zombie3.DistTo(this.character);
					if (float3 < float1) {
						float1 = float3;
						zombie = zombie3;
					}
				}
			}
		}

		if (zombie == null && boolean1) {
			for (int1 = 0; int1 < this.spottedCharacters.size(); ++int1) {
				gameCharacter = (IsoGameCharacter)this.spottedCharacters.get(int1);
				zombie3 = gameCharacter.getGameCharacterAIBrain().getClosestChasingZombie(false);
				if (zombie3 != null) {
					float3 = zombie3.DistTo(this.character);
					if (float3 < float1) {
						float1 = float3;
						zombie = zombie3;
					}
				}
			}
		}

		return zombie != null && zombie.DistTo(this.character) > 30.0F ? null : zombie;
	}

	public IsoZombie getClosestChasingZombie() {
		return this.getClosestChasingZombie(true);
	}

	public ArrayList getClosestChasingZombies(int int1) {
		tempZombies.clear();
		Object object = null;
		float float1 = 1.0E7F;
		int int2;
		for (int2 = 0; int2 < this.chasingZombies.size(); ++int2) {
			IsoZombie zombie = (IsoZombie)this.chasingZombies.get(int2);
			zombie.DistTo(this.character);
			if (!LosUtil.lineClearCollide((int)zombie.x, (int)zombie.y, (int)zombie.z, (int)this.character.x, (int)this.character.y, (int)this.character.z, false)) {
				tempZombies.add(zombie);
			}
		}

		compare = this.character;
		tempZombies.sort((var0,int1x)->{
			float object = compare.DistTo(var0);
			float float1 = compare.DistTo(int1x);
			if (object > float1) {
				return 1;
			} else {
				return object < float1 ? -1 : 0;
			}
		});
		int2 = int1 - tempZombies.size();
		if (int2 > tempZombies.size() - 2) {
			int2 = tempZombies.size() - 2;
		}

		for (int int3 = 0; int3 < int2; ++int3) {
			tempZombies.remove(tempZombies.size() - 1);
		}

		return tempZombies;
	}

	public void AddBlockedMemory(int int1, int int2, int int3) {
		synchronized (this.BlockedMemories) {
			Vector3 vector3 = new Vector3((float)((int)this.character.x), (float)((int)this.character.y), (float)((int)this.character.z));
			if (!this.BlockedMemories.containsKey(vector3)) {
				this.BlockedMemories.put(vector3, new ArrayList());
			}

			ArrayList arrayList = (ArrayList)this.BlockedMemories.get(vector3);
			Vector3 vector32 = new Vector3((float)int1, (float)int2, (float)int3);
			if (!arrayList.contains(vector32)) {
				arrayList.add(vector32);
			}
		}
	}

	public boolean HasBlockedMemory(int int1, int int2, int int3, int int4, int int5, int int6) {
		synchronized (this.BlockedMemories) {
			boolean boolean1;
			synchronized (Vectors) {
				Vector3 vector3;
				if (Vectors.isEmpty()) {
					vector3 = new Vector3();
				} else {
					vector3 = (Vector3)Vectors.pop();
				}

				Vector3 vector32;
				if (Vectors.isEmpty()) {
					vector32 = new Vector3();
				} else {
					vector32 = (Vector3)Vectors.pop();
				}

				vector3.x = (float)int1;
				vector3.y = (float)int2;
				vector3.z = (float)int3;
				vector32.x = (float)int4;
				vector32.y = (float)int5;
				vector32.z = (float)int6;
				if (!this.BlockedMemories.containsKey(vector3)) {
					Vectors.push(vector3);
					Vectors.push(vector32);
					boolean1 = false;
					return boolean1;
				}

				if (!((ArrayList)this.BlockedMemories.get(vector3)).contains(vector32)) {
					Vectors.push(vector3);
					Vectors.push(vector32);
					return false;
				}

				Vectors.push(vector3);
				Vectors.push(vector32);
				boolean1 = true;
			}

			return boolean1;
		}
	}

	public void renderlast() {
	}
}
