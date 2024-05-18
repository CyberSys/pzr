package zombie.iso.objects.interfaces;

import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoBarricade;


public interface BarricadeAble {

	boolean isBarricaded();

	IsoBarricade getBarricadeOnSameSquare();

	IsoBarricade getBarricadeOnOppositeSquare();

	IsoBarricade getBarricadeForCharacter(IsoGameCharacter gameCharacter);

	IsoBarricade getBarricadeOppositeCharacter(IsoGameCharacter gameCharacter);

	IsoGridSquare getSquare();

	IsoGridSquare getOppositeSquare();

	boolean getNorth();
}
