package zombie.characters;

import java.util.HashMap;
import zombie.IndieGL;
import zombie.core.bucket.BucketManager;
import zombie.core.textures.ColorInfo;
import zombie.inventory.types.HandWeapon;
import zombie.iso.sprite.IsoAnim;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;


public class WeaponOverlayUtils {
	static HashMap SpriteMap = new HashMap();

	public static void DrawWeapon(HandWeapon handWeapon, IsoGameCharacter gameCharacter, IsoSprite sprite, float float1, float float2, float float3, ColorInfo colorInfo) {
		if (handWeapon != null && handWeapon.getWeaponSprite() != null) {
			IsoSprite sprite2;
			if (!SpriteMap.containsKey(handWeapon.getWeaponSprite())) {
				IndieGL.End();
				sprite2 = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
				for (int int1 = 0; int1 < sprite.AnimStack.size(); ++int1) {
					String string = ((IsoAnim)sprite.AnimStack.get(int1)).name;
					if (string.endsWith("_R")) {
						sprite2.LoadFramesReverseAltName(handWeapon.getWeaponSprite(), string.substring(0, string.length() - 2), string, ((IsoAnim)sprite.AnimStack.get(int1)).Frames.size());
					} else {
						sprite2.LoadFrames(handWeapon.getWeaponSprite(), ((IsoAnim)sprite.AnimStack.get(int1)).name, ((IsoAnim)sprite.AnimStack.get(int1)).Frames.size());
					}
				}

				SpriteMap.put(handWeapon.getWeaponSprite(), sprite2);
			}

			sprite2 = (IsoSprite)SpriteMap.get(handWeapon.getWeaponSprite());
			if (sprite2.def == null) {
				sprite2.def = IsoSpriteInstance.get(sprite2);
			}

			sprite2.PlayAnim(gameCharacter.sprite.CurrentAnim.name);
			sprite2.def.Frame = gameCharacter.def.Frame;
			float float4 = colorInfo.a;
			colorInfo.a = gameCharacter.alpha[IsoPlayer.getPlayerIndex()];
			sprite2.render(gameCharacter.def, gameCharacter, float1, float2, float3, gameCharacter.dir, gameCharacter.offsetX + (float)IsoGameCharacter.RENDER_OFFSET_X * gameCharacter.def.getScaleX(), gameCharacter.offsetY + (float)IsoGameCharacter.RENDER_OFFSET_Y * gameCharacter.def.getScaleY(), colorInfo);
			colorInfo.a = float4;
		}
	}
}
