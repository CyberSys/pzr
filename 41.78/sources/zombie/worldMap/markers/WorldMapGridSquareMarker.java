package zombie.worldMap.markers;

import java.util.function.Consumer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.worldMap.UIWorldMap;


public final class WorldMapGridSquareMarker extends WorldMapMarker {
	Texture m_texture1 = Texture.getSharedTexture("media/textures/worldMap/circle_center.png");
	Texture m_texture2 = Texture.getSharedTexture("media/textures/worldMap/circle_only_highlight.png");
	float m_r = 1.0F;
	float m_g = 1.0F;
	float m_b = 1.0F;
	float m_a = 1.0F;
	int m_worldX;
	int m_worldY;
	int m_radius = 10;
	int m_minScreenRadius = 64;
	boolean m_blink = true;

	WorldMapGridSquareMarker init(int int1, int int2, int int3, float float1, float float2, float float3, float float4) {
		this.m_worldX = int1;
		this.m_worldY = int2;
		this.m_radius = int3;
		this.m_r = float1;
		this.m_g = float2;
		this.m_b = float3;
		this.m_a = float4;
		return this;
	}

	public void setBlink(boolean boolean1) {
		this.m_blink = boolean1;
	}

	public void setMinScreenRadius(int int1) {
		this.m_minScreenRadius = int1;
	}

	void render(UIWorldMap uIWorldMap) {
		float float1 = PZMath.max((float)this.m_radius, (float)this.m_minScreenRadius / uIWorldMap.getAPI().getWorldScale());
		float float2 = uIWorldMap.getAPI().worldToUIX((float)this.m_worldX - float1, (float)this.m_worldY - float1);
		float float3 = uIWorldMap.getAPI().worldToUIY((float)this.m_worldX - float1, (float)this.m_worldY - float1);
		float float4 = uIWorldMap.getAPI().worldToUIX((float)this.m_worldX + float1, (float)this.m_worldY - float1);
		float float5 = uIWorldMap.getAPI().worldToUIY((float)this.m_worldX + float1, (float)this.m_worldY - float1);
		float float6 = uIWorldMap.getAPI().worldToUIX((float)this.m_worldX + float1, (float)this.m_worldY + float1);
		float float7 = uIWorldMap.getAPI().worldToUIY((float)this.m_worldX + float1, (float)this.m_worldY + float1);
		float float8 = uIWorldMap.getAPI().worldToUIX((float)this.m_worldX - float1, (float)this.m_worldY + float1);
		float float9 = uIWorldMap.getAPI().worldToUIY((float)this.m_worldX - float1, (float)this.m_worldY + float1);
		float2 = (float)((double)float2 + uIWorldMap.getAbsoluteX());
		float3 = (float)((double)float3 + uIWorldMap.getAbsoluteY());
		float4 = (float)((double)float4 + uIWorldMap.getAbsoluteX());
		float5 = (float)((double)float5 + uIWorldMap.getAbsoluteY());
		float6 = (float)((double)float6 + uIWorldMap.getAbsoluteX());
		float7 = (float)((double)float7 + uIWorldMap.getAbsoluteY());
		float8 = (float)((double)float8 + uIWorldMap.getAbsoluteX());
		float9 = (float)((double)float9 + uIWorldMap.getAbsoluteY());
		float float10 = this.m_a * (this.m_blink ? Core.blinkAlpha : 1.0F);
		if (this.m_texture1 != null && this.m_texture1.isReady()) {
			SpriteRenderer.instance.render(this.m_texture1, (double)float2, (double)float3, (double)float4, (double)float5, (double)float6, (double)float7, (double)float8, (double)float9, this.m_r, this.m_g, this.m_b, float10, (Consumer)null);
		}

		if (this.m_texture2 != null && this.m_texture2.isReady()) {
			SpriteRenderer.instance.render(this.m_texture2, (double)float2, (double)float3, (double)float4, (double)float5, (double)float6, (double)float7, (double)float8, (double)float9, this.m_r, this.m_g, this.m_b, float10, (Consumer)null);
		}
	}
}
