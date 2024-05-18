package zombie.iso.objects.interfaces;

import zombie.iso.IsoMovingObject;


public interface Thumpable {

	boolean isDestroyed();

	void Thump(IsoMovingObject movingObject);
}
