package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.Rand;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCell;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoSpriteManager;


public class IsoBrokenGlass extends IsoObject {

	public IsoBrokenGlass(IsoCell cell) {
		super(cell);
		int int1 = Rand.Next(4);
		this.sprite = IsoSpriteManager.instance.getSprite("brokenglass_1_" + int1);
	}

	public String getObjectName() {
		return "IsoBrokenGlass";
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
	}

	public void addToWorld() {
		super.addToWorld();
	}

	public void removeFromWorld() {
		super.removeFromWorld();
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		super.render(float1, float2, float3, colorInfo, boolean1, boolean2, shader);
	}

	public void renderObjectPicker(float float1, float float2, float float3, ColorInfo colorInfo) {
	}
}
