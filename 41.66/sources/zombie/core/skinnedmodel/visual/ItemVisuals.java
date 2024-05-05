package zombie.core.skinnedmodel.visual;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.core.skinnedmodel.population.ClothingItem;


public final class ItemVisuals extends ArrayList {

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.putShort((short)this.size());
		for (int int1 = 0; int1 < this.size(); ++int1) {
			((ItemVisual)this.get(int1)).save(byteBuffer);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.clear();
		short short1 = byteBuffer.getShort();
		for (int int2 = 0; int2 < short1; ++int2) {
			ItemVisual itemVisual = new ItemVisual();
			itemVisual.load(byteBuffer, int1);
			this.add(itemVisual);
		}
	}

	public ItemVisual findHat() {
		for (int int1 = 0; int1 < this.size(); ++int1) {
			ItemVisual itemVisual = (ItemVisual)this.get(int1);
			ClothingItem clothingItem = itemVisual.getClothingItem();
			if (clothingItem != null && clothingItem.isHat()) {
				return itemVisual;
			}
		}

		return null;
	}

	public ItemVisual findMask() {
		for (int int1 = 0; int1 < this.size(); ++int1) {
			ItemVisual itemVisual = (ItemVisual)this.get(int1);
			ClothingItem clothingItem = itemVisual.getClothingItem();
			if (clothingItem != null && clothingItem.isMask()) {
				return itemVisual;
			}
		}

		return null;
	}
}
