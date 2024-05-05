package zombie.characters;

import java.util.ArrayList;
import zombie.iso.BuildingDef;


public final class SurvivorGroup {
	public final ArrayList Members = new ArrayList();
	public String Order;
	public BuildingDef Safehouse;

	public void addMember(SurvivorDesc survivorDesc) {
	}

	public void removeMember(SurvivorDesc survivorDesc) {
	}

	public SurvivorDesc getLeader() {
		return null;
	}

	public boolean isLeader(SurvivorDesc survivorDesc) {
		return false;
	}
}
