package zombie.characters;

import zombie.Lua.LuaEventManager;
import zombie.core.Color;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoPushableObject;


public final class IsoSurvivor extends IsoLivingCharacter {
	public boolean NoGoreDeath = false;
	public boolean Draggable = false;
	public IsoGameCharacter following = null;
	boolean Dragging;
	int repathDelay = 0;
	public int nightsSurvived = 0;
	public int ping = 0;
	public IsoPushableObject collidePushable;
	private boolean tryToTeamUp = true;
	int NeightbourUpdate = 20;
	int NeightbourUpdateMax = 20;

	public void Despawn() {
		if (this.descriptor != null) {
			this.descriptor.Instance = null;
		}
	}

	public String getObjectName() {
		return "Survivor";
	}

	public IsoSurvivor(IsoCell cell) {
		super(cell, 0.0F, 0.0F, 0.0F);
		this.OutlineOnMouseover = true;
		this.getCell().getSurvivorList().add(this);
		LuaEventManager.triggerEvent("OnCreateSurvivor", this);
		this.initWornItems("Human");
		this.initAttachedItems("Human");
	}

	public IsoSurvivor(IsoCell cell, int int1, int int2, int int3) {
		super(cell, (float)int1, (float)int2, (float)int3);
		this.getCell().getSurvivorList().add(this);
		this.OutlineOnMouseover = true;
		this.descriptor = new SurvivorDesc();
		this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
		this.sprite.LoadFramesPcx("Wife", "death", 1);
		this.sprite.LoadFramesPcx("Wife", "dragged", 1);
		this.sprite.LoadFramesPcx("Wife", "asleep_normal", 1);
		this.sprite.LoadFramesPcx("Wife", "asleep_bandaged", 1);
		this.sprite.LoadFramesPcx("Wife", "asleep_bleeding", 1);
		this.name = "Kate";
		this.solid = false;
		this.IgnoreStaggerBack = true;
		this.SpeakColour = new Color(204, 100, 100);
		this.dir = IsoDirections.S;
		this.OutlineOnMouseover = true;
		this.finder.maxSearchDistance = 120;
		LuaEventManager.triggerEvent("OnCreateSurvivor", this);
		LuaEventManager.triggerEvent("OnCreateLivingCharacter", this, this.descriptor);
		this.initWornItems("Human");
		this.initAttachedItems("Human");
	}

	public IsoSurvivor(SurvivorDesc survivorDesc, IsoCell cell, int int1, int int2, int int3) {
		super(cell, (float)int1, (float)int2, (float)int3);
		this.setFemale(survivorDesc.isFemale());
		this.descriptor = survivorDesc;
		survivorDesc.setInstance(this);
		this.OutlineOnMouseover = true;
		String string = "Zombie_palette";
		string = string + "01";
		this.InitSpriteParts(survivorDesc);
		this.SpeakColour = new Color(Rand.Next(200) + 55, Rand.Next(200) + 55, Rand.Next(200) + 55, 255);
		this.finder.maxSearchDistance = 120;
		this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
		this.Dressup(survivorDesc);
		LuaEventManager.triggerEventGarbage("OnCreateSurvivor", this);
		LuaEventManager.triggerEventGarbage("OnCreateLivingCharacter", this, this.descriptor);
		this.initWornItems("Human");
		this.initAttachedItems("Human");
	}

	public void reloadSpritePart() {
	}

	public IsoSurvivor(SurvivorDesc survivorDesc, IsoCell cell, int int1, int int2, int int3, boolean boolean1) {
		super(cell, (float)int1, (float)int2, (float)int3);
		this.setFemale(survivorDesc.isFemale());
		this.descriptor = survivorDesc;
		if (boolean1) {
			survivorDesc.setInstance(this);
		}

		this.OutlineOnMouseover = true;
		this.InitSpriteParts(survivorDesc);
		this.SpeakColour = new Color(Rand.Next(200) + 55, Rand.Next(200) + 55, Rand.Next(200) + 55, 255);
		this.finder.maxSearchDistance = 120;
		this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
		this.Dressup(survivorDesc);
		LuaEventManager.triggerEvent("OnCreateSurvivor", this);
	}
}
