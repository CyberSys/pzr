package zombie.characters;

import java.util.ArrayList;
import java.util.Stack;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaEventManager;
import zombie.behaviors.survivor.orders.GotoOrder;
import zombie.behaviors.survivor.orders.Needs.Need;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoBuilding;


public class SurvivorGroup {
	public IsoBuilding Safehouse = null;
	public ArrayList Members = new ArrayList();
	public SurvivorDesc Leader = null;
	public Stack GroupNeeds = new Stack();
	private KahluaTable luaGroup;

	public SurvivorGroup(KahluaTable kahluaTable) {
		this.luaGroup = kahluaTable;
	}

	public SurvivorGroup(SurvivorDesc survivorDesc) {
		this.Leader = survivorDesc;
		this.addMember(survivorDesc);
	}

	public void setLuaGroup(KahluaTable kahluaTable) {
		this.luaGroup = kahluaTable;
	}

	public ArrayList getMembers() {
		return this.Members;
	}

	public void addAll(SurvivorGroup survivorGroup) {
		for (int int1 = 0; int1 < survivorGroup.Members.size(); ++int1) {
			this.addMember((SurvivorDesc)survivorGroup.Members.get(int1));
		}
	}

	public KahluaTable getLuaGroup() {
		return this.luaGroup;
	}

	public void gotoOrder(int int1, int int2) {
		for (int int3 = 0; int3 < this.Members.size(); ++int3) {
			SurvivorDesc survivorDesc = (SurvivorDesc)this.Members.get(int3);
			if (survivorDesc.Instance != null) {
				survivorDesc.Instance.GiveOrder(new GotoOrder(survivorDesc.Instance, int1, int2, 0), true);
			}
		}
	}

	public void gotoBuildingOrder(BuildingDef buildingDef) {
		if (this.Leader == null) {
			this.pickNewLeader();
		}

		if (this.Leader.Instance != null) {
			boolean boolean1 = false;
		}

		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(buildingDef.getX(), buildingDef.getY(), 0);
		IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(buildingDef.getX2(), buildingDef.getY2(), 0);
		int int1;
		SurvivorDesc survivorDesc;
		if (square == null && square2 == null) {
			for (int1 = 0; int1 < this.Members.size(); ++int1) {
				survivorDesc = (SurvivorDesc)this.Members.get(int1);
				if (survivorDesc.Instance != null) {
					survivorDesc.Instance.GiveOrder(new GotoOrder(survivorDesc.Instance, buildingDef.getFirstRoom().getX(), buildingDef.getFirstRoom().getY(), 0), true);
				}
			}
		} else {
			square = buildingDef.getFreeSquareInRoom();
			for (int1 = 0; int1 < this.Members.size(); ++int1) {
				survivorDesc = (SurvivorDesc)this.Members.get(int1);
				if (survivorDesc.Instance != null) {
					survivorDesc.Instance.GiveOrder(new GotoOrder(survivorDesc.Instance, square.getX(), square.getY(), 0), true);
				}
			}
		}
	}

	public void setLeader(SurvivorDesc survivorDesc) {
		this.Leader = survivorDesc;
	}

	public SurvivorDesc getLeader() {
		return this.Leader;
	}

	public void addMember(SurvivorDesc survivorDesc) {
		if (survivorDesc != null) {
			survivorDesc.Group = this;
			if (!this.Members.contains(survivorDesc)) {
				this.Members.add(survivorDesc);
			}

			if (this.Leader == null || this.Leader.Group != this) {
				this.Leader = survivorDesc;
			}
		}
	}

	public boolean isInstanced() {
		return this.Leader != null && this.Leader.getInstance() != null;
	}

	public void removeMember(SurvivorDesc survivorDesc) {
		this.Members.remove(survivorDesc);
		if (this.Leader == survivorDesc) {
			this.pickNewLeader();
		}
	}

	public boolean isMember(SurvivorDesc survivorDesc) {
		return this.Members.contains(survivorDesc);
	}

	public boolean isMember(IsoGameCharacter gameCharacter) {
		return gameCharacter.descriptor == null ? false : this.Members.contains(gameCharacter.descriptor);
	}

	public boolean isLeader(SurvivorDesc survivorDesc) {
		return this.Leader == survivorDesc;
	}

	public boolean isLeader(IsoGameCharacter gameCharacter) {
		if (gameCharacter.descriptor == null) {
			return false;
		} else {
			return this.Leader == gameCharacter.descriptor;
		}
	}

	public void update() {
		for (int int1 = 0; int1 < this.Members.size(); ++int1) {
			if (((SurvivorDesc)this.Members.get(int1)).Group != this || ((SurvivorDesc)this.Members.get(int1)).bDead) {
				this.removeMember((SurvivorDesc)this.Members.get(int1));
				--int1;
			}
		}

		if (this.Leader == null || this.Leader.Group != this || this.Leader.Instance != null && this.Leader.Instance.isDead()) {
			this.pickNewLeader();
		}

		if (!this.Members.contains(this.Leader) && this.Leader != null) {
			this.Members.add(this.Leader);
		}
	}

	private void pickNewLeader() {
		if (!this.Members.isEmpty()) {
			this.setLeader((SurvivorDesc)this.Members.get(Rand.Next(this.Members.size())));
		}
	}

	IsoGameCharacter getRandomMemberExcept(IsoGameCharacter gameCharacter) {
		if (this.Members.size() == 1) {
			return null;
		} else {
			IsoGameCharacter gameCharacter2 = null;
			do {
				gameCharacter2 = ((SurvivorDesc)this.Members.get(Rand.Next(this.Members.size()))).Instance;
			}	 while (gameCharacter2 == gameCharacter);

			return gameCharacter2;
		}
	}

	public boolean HasOtherMembers(SurvivorDesc survivorDesc) {
		return this.Members.contains(survivorDesc) && this.Members.size() > 1;
	}

	public int getTotalNeedPriority() {
		int int1 = 0;
		for (int int2 = 0; int2 < this.GroupNeeds.size(); ++int2) {
			int1 += ((Need)this.GroupNeeds.get(int2)).priority;
		}

		return int1;
	}

	public void AddNeed(String string, int int1) {
		for (int int2 = 0; int2 < this.GroupNeeds.size(); ++int2) {
			if (((Need)this.GroupNeeds.get(int2)).item.equals(string)) {
				++((Need)this.GroupNeeds.get(int2)).numToSatisfy;
				if (((Need)this.GroupNeeds.get(int2)).priority < int1) {
					((Need)this.GroupNeeds.get(int2)).priority = int1;
				}

				return;
			}
		}

		this.GroupNeeds.add(new Need(string, int1));
	}

	public boolean HasNeed(String string) {
		for (int int1 = 0; int1 < this.GroupNeeds.size(); ++int1) {
			if (((Need)this.GroupNeeds.get(int1)).item.equals(string)) {
				return true;
			}
		}

		return false;
	}

	public void setLuaTable(KahluaTable kahluaTable) {
		this.luaGroup = kahluaTable;
	}

	public void setSafehouse(IsoBuilding building) {
		this.Safehouse = building;
		LuaEventManager.triggerEvent("OnPlayerSetSafehouse", this.luaGroup, building.def);
	}

	public void instanceGroup(int int1, int int2) {
		int int3;
		SurvivorDesc survivorDesc;
		for (int3 = 0; int3 < this.Members.size(); ++int3) {
			survivorDesc = (SurvivorDesc)this.Members.get(int3);
			if (survivorDesc.Instance == null) {
				survivorDesc.Instance = new IsoSurvivor(survivorDesc, IsoWorld.instance.CurrentCell, int1, int2, 0);
			}
		}

		for (int3 = 0; int3 < this.Members.size(); ++int3) {
			survivorDesc = (SurvivorDesc)this.Members.get(int3);
			if (survivorDesc != this.Leader) {
			}
		}
	}

	public void Despawn() {
		for (int int1 = 0; int1 < this.Members.size(); ++int1) {
			SurvivorDesc survivorDesc = (SurvivorDesc)this.Members.get(int1);
			if (survivorDesc.Instance != null && !(survivorDesc.Instance instanceof IsoPlayer)) {
				IsoWorld.instance.CurrentCell.Remove(survivorDesc.Instance);
				survivorDesc.Instance = null;
			}
		}
	}
}
