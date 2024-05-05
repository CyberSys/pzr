package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;


public final class ParameterCurrentZone extends FMODLocalParameter {
	private final IsoObject object;
	private IsoMetaGrid.Zone metaZone;
	private ParameterCurrentZone.Zone zone;

	public ParameterCurrentZone(IsoObject object) {
		super("CurrentZone");
		this.zone = ParameterCurrentZone.Zone.None;
		this.object = object;
	}

	public float calculateCurrentValue() {
		IsoGridSquare square = this.object.getSquare();
		if (square == null) {
			this.zone = ParameterCurrentZone.Zone.None;
			return (float)this.zone.label;
		} else if (square.zone == this.metaZone) {
			return (float)this.zone.label;
		} else {
			this.metaZone = square.zone;
			if (this.metaZone == null) {
				this.zone = ParameterCurrentZone.Zone.None;
				return (float)this.zone.label;
			} else {
				String string = this.metaZone.type;
				byte byte1 = -1;
				switch (string.hashCode()) {
				case -687878786: 
					if (string.equals("TownZone")) {
						byte1 = 4;
					}

					break;
				
				case -650999246: 
					if (string.equals("Vegitation")) {
						byte1 = 6;
					}

					break;
				
				case 78083: 
					if (string.equals("Nav")) {
						byte1 = 3;
					}

					break;
				
				case 2182230: 
					if (string.equals("Farm")) {
						byte1 = 1;
					}

					break;
				
				case 14106697: 
					if (string.equals("DeepForest")) {
						byte1 = 0;
					}

					break;
				
				case 1894728605: 
					if (string.equals("TrailerPark")) {
						byte1 = 5;
					}

					break;
				
				case 2110048317: 
					if (string.equals("Forest")) {
						byte1 = 2;
					}

				
				}

				ParameterCurrentZone.Zone zone;
				switch (byte1) {
				case 0: 
					zone = ParameterCurrentZone.Zone.DeepForest;
					break;
				
				case 1: 
					zone = ParameterCurrentZone.Zone.Farm;
					break;
				
				case 2: 
					zone = ParameterCurrentZone.Zone.Forest;
					break;
				
				case 3: 
					zone = ParameterCurrentZone.Zone.Nav;
					break;
				
				case 4: 
					zone = ParameterCurrentZone.Zone.Town;
					break;
				
				case 5: 
					zone = ParameterCurrentZone.Zone.TrailerPark;
					break;
				
				case 6: 
					zone = ParameterCurrentZone.Zone.Vegetation;
					break;
				
				default: 
					zone = ParameterCurrentZone.Zone.None;
				
				}

				this.zone = zone;
				return (float)this.zone.label;
			}
		}
	}

	static enum Zone {

		None,
		DeepForest,
		Farm,
		Forest,
		Nav,
		Town,
		TrailerPark,
		Vegetation,
		label;

		private Zone(int int1) {
			this.label = int1;
		}
		private static ParameterCurrentZone.Zone[] $values() {
			return new ParameterCurrentZone.Zone[]{None, DeepForest, Farm, Forest, Nav, Town, TrailerPark, Vegetation};
		}
	}
}
