package zombie.chat;

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
		this.topLeft = Texture.getTexture(string + "_topleft");
		this.topMid = Texture.getTexture(string + "_topmid");
		this.topRight = Texture.getTexture(string + "_topright");
		this.left = Texture.getTexture(string + "_left");
		this.mid = Texture.getTexture(string + "_mid");
		this.right = Texture.getTexture(string + "_right");
		this.botLeft = Texture.getTexture(string + "_botleft");
		this.botMid = Texture.getTexture(string + "_botmid");
		this.botRight = Texture.getTexture(string + "_botright");
	}

	public void renderInnerBased(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		int2 += 5;
		int4 -= 7;
		SpriteRenderer.instance.render(this.topLeft, int1 - this.outer, int2 - this.outer, this.outer, this.outer, float1, float2, float3, float4);
		SpriteRenderer.instance.render(this.topMid, int1, int2 - this.outer, int3, this.outer, float1, float2, float3, float4);
		SpriteRenderer.instance.render(this.topRight, int1 + int3, int2 - this.outer, this.outer, this.outer, float1, float2, float3, float4);
		SpriteRenderer.instance.render(this.left, int1 - this.outer, int2, this.outer, int4, float1, float2, float3, float4);
		SpriteRenderer.instance.render(this.mid, int1, int2, int3, int4, float1, float2, float3, float4);
		SpriteRenderer.instance.render(this.right, int1 + int3, int2, this.outer, int4, float1, float2, float3, float4);
		SpriteRenderer.instance.render(this.botLeft, int1 - this.outer, int2 + int4, this.outer, this.outer, float1, float2, float3, float4);
		SpriteRenderer.instance.render(this.botMid, int1, int2 + int4, int3, this.outer, float1, float2, float3, float4);
		SpriteRenderer.instance.render(this.botRight, int1 + int3, int2 + int4, this.outer, this.outer, float1, float2, float3, float4);
	}
}
