package zombie;

import java.util.ArrayList;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.core.textures.Texture;


public class ZombieTemplateManager {

	public Texture addOverlayToTexture(ArrayList arrayList, Texture texture) {
		return null;
	}

	public class ZombieTemplate {
		public Texture tex;
	}

	public class BodyOverlay {
		public BodyPartType location;
		public ZombieTemplateManager.OverlayType type;
	}

	public static enum OverlayType {

		BloodLight,
		BloodMedium,
		BloodHeavy;

		private static ZombieTemplateManager.OverlayType[] $values() {
			return new ZombieTemplateManager.OverlayType[]{BloodLight, BloodMedium, BloodHeavy};
		}
	}
}
