package zombie.iso;

import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.core.bucket.BucketManager;
import zombie.core.textures.ColorInfo;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.ui.UIManager;


public class IsoLuaMover extends IsoGameCharacter {
	public KahluaTable luaMoverTable;

	public IsoLuaMover(KahluaTable kahluaTable) {
		super((IsoCell)null, 0.0F, 0.0F, 0.0F);
		this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
		this.luaMoverTable = kahluaTable;
		if (this.def == null) {
			this.def = IsoSpriteInstance.get(this.sprite);
		}
	}

	public void playAnim(String string, float float1, boolean boolean1, boolean boolean2) {
		this.sprite.PlayAnim(string);
		float float2 = (float)this.sprite.CurrentAnim.Frames.size();
		float float3 = 1000.0F / float2;
		float float4 = float3 * float1;
		this.def.AnimFrameIncrease = float4 * GameTime.getInstance().getMultiplier();
		this.def.Finished = !boolean2;
		this.def.Looped = boolean1;
	}

	public String getObjectName() {
		return "IsoLuaMover";
	}

	public void update() {
		this.setBlendSpeed(1.0F);
		try {
			LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.luaMoverTable.rawget("update"), (Object)this.luaMoverTable);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		this.sprite.update(this.def);
		super.update();
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1) {
		float float4 = this.offsetY;
		float4 -= 100.0F;
		float float5 = this.offsetX;
		float5 -= 34.0F;
		this.sprite.render(this.def, this, this.x, this.y, this.z, this.dir, float5, float4, colorInfo);
		try {
			LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.luaMoverTable.rawget("postrender"), this.luaMoverTable, colorInfo, boolean1);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
