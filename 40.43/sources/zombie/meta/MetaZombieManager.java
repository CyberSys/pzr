package zombie.meta;

import zombie.core.Rand;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class MetaZombieManager {
	public static MetaZombieManager instance = new MetaZombieManager();

	public void decayBloodAndCorpse(IsoMetaGrid.Zone zone) {
		if (!GameClient.bClient) {
			int int1 = 20 - zone.hourLastSeen;
			if (int1 < 3) {
				int1 = 3;
			}

			for (int int2 = zone.x; int2 < zone.x + zone.w; ++int2) {
				for (int int3 = zone.y; int3 < zone.y + zone.h; ++int3) {
					IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare(int2, int3, 0);
					if (square != null) {
						int int4;
						for (int4 = 0; int4 < square.getStaticMovingObjects().size(); ++int4) {
							IsoMovingObject movingObject = (IsoMovingObject)square.getStaticMovingObjects().get(int4);
							if (movingObject instanceof IsoDeadBody && Rand.Next(int1) == 0) {
								if (GameServer.bServer) {
									GameServer.removeCorpseFromMap((IsoDeadBody)movingObject);
								}

								movingObject.square = square;
								movingObject.removeFromWorld();
								movingObject.removeFromSquare();
								--int4;
							}
						}

						for (int4 = 0; int4 < square.getChunk().FloorBloodSplats.size(); ++int4) {
							if (Rand.Next(int1) == 0) {
								square.getChunk().FloorBloodSplats.remove(int4);
								--int4;
							}
						}
					}
				}
			}
		}
	}
}
