package zombie.meta;

import gnu.trove.set.hash.TIntHashSet;
import java.util.ArrayList;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.areas.SafeHouse;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class Meta {
	public static final Meta instance = new Meta();
	final ArrayList SquaresProcessing = new ArrayList();
	private final ArrayList SquaresSeen = new ArrayList(2000);
	private final TIntHashSet SquaresSeenSet = new TIntHashSet();

	public void dealWithSquareSeen(IsoGridSquare square) {
		if (!GameClient.bClient) {
			if (square.hourLastSeen != (int)GameTime.getInstance().getWorldAgeHours()) {
				synchronized (this.SquaresSeen) {
					if (!this.SquaresSeenSet.contains(square.getID())) {
						this.SquaresSeen.add(square);
						this.SquaresSeenSet.add(square.getID());
					}
				}
			}
		}
	}

	public void dealWithSquareSeenActual(IsoGridSquare square) {
		if (!GameClient.bClient) {
			IsoMetaGrid.Zone zone = square.zone;
			if (zone != null) {
				zone.setHourSeenToCurrent();
			}

			if (GameServer.bServer) {
				SafeHouse safeHouse = SafeHouse.getSafeHouse(square);
				if (safeHouse != null) {
					safeHouse.updateSafehouse((IsoPlayer)null);
				}
			}

			square.setHourSeenToCurrent();
		}
	}

	public void update() {
		if (!GameClient.bClient) {
			this.SquaresProcessing.clear();
			synchronized (this.SquaresSeen) {
				this.SquaresProcessing.addAll(this.SquaresSeen);
				this.SquaresSeen.clear();
				this.SquaresSeenSet.clear();
			}

			for (int int1 = 0; int1 < this.SquaresProcessing.size(); ++int1) {
				this.dealWithSquareSeenActual((IsoGridSquare)this.SquaresProcessing.get(int1));
			}

			this.SquaresProcessing.clear();
		}
	}
}
