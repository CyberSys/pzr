package zombie.core.skinnedmodel.advancedanimation;

import javax.xml.bind.annotation.XmlIDREF;


public final class Anim2DBlendTriangle {
	@XmlIDREF
	public Anim2DBlend node1;
	@XmlIDREF
	public Anim2DBlend node2;
	@XmlIDREF
	public Anim2DBlend node3;

	public static double sign(float float1, float float2, float float3, float float4, float float5, float float6) {
		return (double)((float1 - float5) * (float4 - float6) - (float3 - float5) * (float2 - float6));
	}

	static boolean PointInTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		boolean boolean1 = sign(float1, float2, float3, float4, float5, float6) < 0.0;
		boolean boolean2 = sign(float1, float2, float5, float6, float7, float8) < 0.0;
		boolean boolean3 = sign(float1, float2, float7, float8, float3, float4) < 0.0;
		return boolean1 == boolean2 && boolean2 == boolean3;
	}

	public boolean Contains(float float1, float float2) {
		return PointInTriangle(float1, float2, this.node1.m_XPos, this.node1.m_YPos, this.node2.m_XPos, this.node2.m_YPos, this.node3.m_XPos, this.node3.m_YPos);
	}
}
