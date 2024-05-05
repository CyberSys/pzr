package zombie.worldMap.symbols;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.worldMap.UIWorldMap;


public abstract class WorldMapBaseSymbol {
	public static float DEFAULT_SCALE = 0.666F;
	WorldMapSymbols m_owner;
	float m_x;
	float m_y;
	float m_width;
	float m_height;
	float m_anchorX = 0.0F;
	float m_anchorY = 0.0F;
	float m_scale;
	float m_r;
	float m_g;
	float m_b;
	float m_a;
	boolean m_collide;
	boolean m_collided;
	float m_layoutX;
	float m_layoutY;
	boolean m_visible;

	public WorldMapBaseSymbol(WorldMapSymbols worldMapSymbols) {
		this.m_scale = DEFAULT_SCALE;
		this.m_collide = false;
		this.m_collided = false;
		this.m_visible = true;
		this.m_owner = worldMapSymbols;
	}

	public abstract WorldMapSymbols.WorldMapSymbolType getType();

	public void setAnchor(float float1, float float2) {
		this.m_anchorX = PZMath.clamp(float1, 0.0F, 1.0F);
		this.m_anchorY = PZMath.clamp(float2, 0.0F, 1.0F);
	}

	public void setPosition(float float1, float float2) {
		this.m_x = float1;
		this.m_y = float2;
	}

	public void setCollide(boolean boolean1) {
		this.m_collide = boolean1;
	}

	public void setRGBA(float float1, float float2, float float3, float float4) {
		this.m_r = PZMath.clamp_01(float1);
		this.m_g = PZMath.clamp_01(float2);
		this.m_b = PZMath.clamp_01(float3);
		this.m_a = PZMath.clamp_01(float4);
	}

	public void setScale(float float1) {
		this.m_scale = float1;
	}

	public float getDisplayScale(UIWorldMap uIWorldMap) {
		if (this.m_scale <= 0.0F) {
			return this.m_scale;
		} else {
			return this.m_owner.getMiniMapSymbols() ? PZMath.min(this.m_owner.getLayoutWorldScale(), 1.0F) : this.m_owner.getLayoutWorldScale() * this.m_scale;
		}
	}

	public void layout(UIWorldMap uIWorldMap, WorldMapSymbolCollisions worldMapSymbolCollisions, float float1, float float2) {
		float float3 = uIWorldMap.getAPI().worldToUIX(this.m_x, this.m_y) - float1;
		float float4 = uIWorldMap.getAPI().worldToUIY(this.m_x, this.m_y) - float2;
		this.m_layoutX = float3 - this.widthScaled(uIWorldMap) * this.m_anchorX;
		this.m_layoutY = float4 - this.heightScaled(uIWorldMap) * this.m_anchorY;
		this.m_collided = worldMapSymbolCollisions.addBox(this.m_layoutX, this.m_layoutY, this.widthScaled(uIWorldMap), this.heightScaled(uIWorldMap), this.m_collide);
		if (this.m_collided) {
		}
	}

	public float widthScaled(UIWorldMap uIWorldMap) {
		return this.m_scale <= 0.0F ? this.m_width : this.m_width * this.getDisplayScale(uIWorldMap);
	}

	public float heightScaled(UIWorldMap uIWorldMap) {
		return this.m_scale <= 0.0F ? this.m_height : this.m_height * this.getDisplayScale(uIWorldMap);
	}

	public void setVisible(boolean boolean1) {
		this.m_visible = boolean1;
	}

	public boolean isVisible() {
		return this.m_visible;
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.putFloat(this.m_x);
		byteBuffer.putFloat(this.m_y);
		byteBuffer.putFloat(this.m_anchorX);
		byteBuffer.putFloat(this.m_anchorY);
		byteBuffer.putFloat(this.m_scale);
		byteBuffer.put((byte)((int)(this.m_r * 255.0F)));
		byteBuffer.put((byte)((int)(this.m_g * 255.0F)));
		byteBuffer.put((byte)((int)(this.m_b * 255.0F)));
		byteBuffer.put((byte)((int)(this.m_a * 255.0F)));
		byteBuffer.put((byte)(this.m_collide ? 1 : 0));
	}

	public void load(ByteBuffer byteBuffer, int int1, int int2) throws IOException {
		this.m_x = byteBuffer.getFloat();
		this.m_y = byteBuffer.getFloat();
		this.m_anchorX = byteBuffer.getFloat();
		this.m_anchorY = byteBuffer.getFloat();
		this.m_scale = byteBuffer.getFloat();
		this.m_r = (float)(byteBuffer.get() & 255) / 255.0F;
		this.m_g = (float)(byteBuffer.get() & 255) / 255.0F;
		this.m_b = (float)(byteBuffer.get() & 255) / 255.0F;
		this.m_a = (float)(byteBuffer.get() & 255) / 255.0F;
		this.m_collide = byteBuffer.get() == 1;
	}

	public abstract void render(UIWorldMap uIWorldMap, float float1, float float2);

	void renderCollided(UIWorldMap uIWorldMap, float float1, float float2) {
		float float3 = float1 + this.m_layoutX + this.widthScaled(uIWorldMap) / 2.0F;
		float float4 = float2 + this.m_layoutY + this.heightScaled(uIWorldMap) / 2.0F;
		uIWorldMap.DrawTextureScaledCol((Texture)null, (double)(float3 - 3.0F), (double)(float4 - 3.0F), 6.0, 6.0, (double)this.m_r, (double)this.m_g, (double)this.m_b, (double)this.m_a);
	}

	public abstract void release();
}
