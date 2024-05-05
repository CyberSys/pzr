package zombie.chat;

import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;


public class NineGridTexture {
	private Texture topLeft;
	private Texture topMid;
	private Texture topRight;
	private Texture left;
	private Texture mid;
	private Texture right;
	private Texture botLeft;
	private Texture botMid;
	private Texture botRight;
	private int outer;

	public NineGridTexture(String string, int int1) {
		this.outer = int1;
		this.topLeft = Texture.getSharedTexture(string + "_topleft");
		this.topMid = Texture.getSharedTexture(string + "_topmid");
		this.topRight = Texture.getSharedTexture(string + "_topright");
		this.left = Texture.getSharedTexture(string + "_left");
		this.mid = Texture.getSharedTexture(string + "_mid");
		this.right = Texture.getSharedTexture(string + "_right");
		this.botLeft = Texture.getSharedTexture(string + "_botleft");
		this.botMid = Texture.getSharedTexture(string + "_botmid");
		this.botRight = Texture.getSharedTexture(string + "_botright");
	}

	public void renderInnerBased(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		int2 += 5;
		int4 -= 7;
		SpriteRenderer.instance.renderi(this.topLeft, int1 - this.outer, int2 - this.outer, this.outer, this.outer, float1, float2, float3, float4, (Consumer)null);
		SpriteRenderer.instance.renderi(this.topMid, int1, int2 - this.outer, int3, this.outer, float1, float2, float3, float4, (Consumer)null);
		SpriteRenderer.instance.renderi(this.topRight, int1 + int3, int2 - this.outer, this.outer, this.outer, float1, float2, float3, float4, (Consumer)null);
		SpriteRenderer.instance.renderi(this.left, int1 - this.outer, int2, this.outer, int4, float1, float2, float3, float4, (Consumer)null);
		SpriteRenderer.instance.renderi(this.mid, int1, int2, int3, int4, float1, float2, float3, float4, (Consumer)null);
		SpriteRenderer.instance.renderi(this.right, int1 + int3, int2, this.outer, int4, float1, float2, float3, float4, (Consumer)null);
		SpriteRenderer.instance.renderi(this.botLeft, int1 - this.outer, int2 + int4, this.outer, this.outer, float1, float2, float3, float4, (Consumer)null);
		SpriteRenderer.instance.renderi(this.botMid, int1, int2 + int4, int3, this.outer, float1, float2, float3, float4, (Consumer)null);
		SpriteRenderer.instance.renderi(this.botRight, int1 + int3, int2 + int4, this.outer, this.outer, float1, float2, float3, float4, (Consumer)null);
	}
}
