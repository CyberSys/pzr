package zombie.characters;

import zombie.iso.IsoCell;


public class IsoLuaCharacter extends IsoGameCharacter {

	public IsoLuaCharacter(float float1, float float2, float float3) {
		super((IsoCell)null, float1, float2, float3);
		this.descriptor = SurvivorFactory.CreateSurvivor();
		this.descriptor.setInstance(this);
		SurvivorDesc survivorDesc = this.descriptor;
		this.InitSpriteParts(survivorDesc, survivorDesc.legs, survivorDesc.torso, survivorDesc.head, survivorDesc.top, survivorDesc.bottoms, survivorDesc.shoes, survivorDesc.skinpal, survivorDesc.toppal, survivorDesc.bottomspal, survivorDesc.shoespal, survivorDesc.hair, survivorDesc.extra);
	}

	public void update() {
	}
}
