package zombie.audio.parameters;

import java.util.ArrayList;
import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;


public final class ParameterZone extends FMODGlobalParameter {
	private final String m_zoneName;
	private final ArrayList m_zones = new ArrayList();

	public ParameterZone(String string, String string2) {
		super(string);
		this.m_zoneName = string2;
	}

	public float calculateCurrentValue() {
		IsoGameCharacter gameCharacter = this.getCharacter();
		if (gameCharacter == null) {
			return 40.0F;
		} else {
			byte byte1 = 0;
			this.m_zones.clear();
			IsoWorld.instance.MetaGrid.getZonesIntersecting((int)gameCharacter.x - 40, (int)gameCharacter.y - 40, byte1, 80, 80, this.m_zones);
			float float1 = Float.MAX_VALUE;
			for (int int1 = 0; int1 < this.m_zones.size(); ++int1) {
				IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)this.m_zones.get(int1);
				if (this.m_zoneName.equalsIgnoreCase(zone.getType())) {
					if (zone.contains((int)gameCharacter.x, (int)gameCharacter.y, byte1)) {
						return 0.0F;
					}

					float float2 = (float)zone.x + (float)zone.w / 2.0F;
					float float3 = (float)zone.y + (float)zone.h / 2.0F;
					float float4 = PZMath.max(PZMath.abs(gameCharacter.x - float2) - (float)zone.w / 2.0F, 0.0F);
					float float5 = PZMath.max(PZMath.abs(gameCharacter.y - float3) - (float)zone.h / 2.0F, 0.0F);
					float1 = PZMath.min(float1, float4 * float4 + float5 * float5);
				}
			}

			return (float)((int)PZMath.clamp(PZMath.sqrt(float1), 0.0F, 40.0F));
		}
	}

	private IsoGameCharacter getCharacter() {
		IsoPlayer player = null;
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player2 = IsoPlayer.players[int1];
			if (player2 != null && (player == null || player.isDead() && player2.isAlive() || player.Traits.Deaf.isSet() && !player2.Traits.Deaf.isSet())) {
				player = player2;
			}
		}

		return player;
	}
}
