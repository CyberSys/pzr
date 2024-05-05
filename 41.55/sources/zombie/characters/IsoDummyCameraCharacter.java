package zombie.characters;

import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;


public final class IsoDummyCameraCharacter extends IsoGameCharacter {

	public IsoDummyCameraCharacter(float float1, float float2, float float3) {
		super((IsoCell)null, float1, float2, float3);
		IsoCamera.CamCharacter = this;
	}

	public void update() {
	}
}
