package zombie.iso;

import org.joml.Vector2f;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;


public final class IsoUtils {

	public static float clamp(float float1, float float2, float float3) {
		return Math.min(Math.max(float1, float2), float3);
	}

	public static float lerp(float float1, float float2, float float3) {
		return float3 == float2 ? float2 : (clamp(float1, float2, float3) - float2) / (float3 - float2);
	}

	public static float smoothstep(float float1, float float2, float float3) {
		float float4 = clamp((float3 - float1) / (float2 - float1), 0.0F, 1.0F);
		return float4 * float4 * (3.0F - 2.0F * float4);
	}

	public static float DistanceTo(float float1, float float2, float float3, float float4) {
		return (float)Math.sqrt(Math.pow((double)(float3 - float1), 2.0) + Math.pow((double)(float4 - float2), 2.0));
	}

	public static float DistanceTo2D(float float1, float float2, float float3, float float4) {
		return (float)Math.sqrt(Math.pow((double)(float3 - float1), 2.0) + Math.pow((double)(float4 - float2), 2.0));
	}

	public static float DistanceTo(float float1, float float2, float float3, float float4, float float5, float float6) {
		return (float)Math.sqrt(Math.pow((double)(float4 - float1), 2.0) + Math.pow((double)(float5 - float2), 2.0) + Math.pow((double)(float6 - float3), 2.0));
	}

	public static float DistanceToSquared(float float1, float float2, float float3, float float4, float float5, float float6) {
		return (float)(Math.pow((double)(float4 - float1), 2.0) + Math.pow((double)(float5 - float2), 2.0) + Math.pow((double)(float6 - float3), 2.0));
	}

	public static float DistanceToSquared(float float1, float float2, float float3, float float4) {
		return (float)(Math.pow((double)(float3 - float1), 2.0) + Math.pow((double)(float4 - float2), 2.0));
	}

	public static float DistanceManhatten(float float1, float float2, float float3, float float4) {
		return Math.abs(float3 - float1) + Math.abs(float4 - float2);
	}

	public static float DistanceManhatten(float float1, float float2, float float3, float float4, float float5, float float6) {
		return Math.abs(float3 - float1) + Math.abs(float4 - float2) + Math.abs(float6 - float5) * 2.0F;
	}

	public static float DistanceManhattenSquare(float float1, float float2, float float3, float float4) {
		return Math.max(Math.abs(float3 - float1), Math.abs(float4 - float2));
	}

	public static float XToIso(float float1, float float2, float float3) {
		float float4 = float1 + IsoCamera.getOffX();
		float float5 = float2 + IsoCamera.getOffY();
		float float6 = (float4 + 2.0F * float5) / (64.0F * (float)Core.TileScale);
		float float7 = (float4 - 2.0F * float5) / (-64.0F * (float)Core.TileScale);
		float6 += 3.0F * float3;
		float float8 = float7 + 3.0F * float3;
		return float6;
	}

	public static float XToIsoTrue(float float1, float float2, int int1) {
		float float3 = float1 + (float)((int)IsoCamera.cameras[IsoPlayer.getPlayerIndex()].OffX);
		float float4 = float2 + (float)((int)IsoCamera.cameras[IsoPlayer.getPlayerIndex()].OffY);
		float float5 = (float3 + 2.0F * float4) / (64.0F * (float)Core.TileScale);
		float float6 = (float3 - 2.0F * float4) / (-64.0F * (float)Core.TileScale);
		float5 += (float)(3 * int1);
		float float7 = float6 + (float)(3 * int1);
		return float5;
	}

	public static float XToScreen(float float1, float float2, float float3, int int1) {
		float float4 = 0.0F;
		float4 += float1 * (float)(32 * Core.TileScale);
		float4 -= float2 * (float)(32 * Core.TileScale);
		return float4;
	}

	public static float XToScreenInt(int int1, int int2, int int3, int int4) {
		return XToScreen((float)int1, (float)int2, (float)int3, int4);
	}

	public static float YToScreenExact(float float1, float float2, float float3, int int1) {
		float float4 = YToScreen(float1, float2, float3, int1);
		float4 -= IsoCamera.getOffY();
		return float4;
	}

	public static float XToScreenExact(float float1, float float2, float float3, int int1) {
		float float4 = XToScreen(float1, float2, float3, int1);
		float4 -= IsoCamera.getOffX();
		return float4;
	}

	public static float YToIso(float float1, float float2, float float3) {
		float float4 = float1 + IsoCamera.getOffX();
		float float5 = float2 + IsoCamera.getOffY();
		float float6 = (float4 + 2.0F * float5) / (64.0F * (float)Core.TileScale);
		float float7 = (float4 - 2.0F * float5) / (-64.0F * (float)Core.TileScale);
		float float8 = float6 + 3.0F * float3;
		float7 += 3.0F * float3;
		return float7;
	}

	public static float YToScreen(float float1, float float2, float float3, int int1) {
		float float4 = 0.0F;
		float4 += float2 * (float)(16 * Core.TileScale);
		float4 += float1 * (float)(16 * Core.TileScale);
		float4 += ((float)int1 - float3) * (float)(96 * Core.TileScale);
		return float4;
	}

	public static float YToScreenInt(int int1, int int2, int int3, int int4) {
		return YToScreen((float)int1, (float)int2, (float)int3, int4);
	}

	public static boolean isSimilarDirection(IsoGameCharacter gameCharacter, float float1, float float2, float float3, float float4, float float5) {
		Vector2f vector2f = new Vector2f(float1 - gameCharacter.x, float2 - gameCharacter.y);
		vector2f.normalize();
		Vector2f vector2f2 = new Vector2f(gameCharacter.x - float3, gameCharacter.y - float4);
		vector2f2.normalize();
		vector2f.add(vector2f2);
		return vector2f.length() < float5;
	}
}
