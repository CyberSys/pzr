package zombie.core.skinnedmodel.advancedanimation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import zombie.core.logger.ExceptionLogger;
import zombie.core.skinnedmodel.animation.BoneAxis;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;


@XmlRootElement
public final class AnimNode {
	private static final Comparator s_eventsComparator = (var0,var1)->{
    return Float.compare(var0.m_TimePc, var1.m_TimePc);
};
	public String m_Name = "";
	public int m_Priority = 5;
	public String m_AnimName = "";
	public String m_DeferredBoneName = "";
	public BoneAxis m_deferredBoneAxis;
	public boolean m_useDeferedRotation;
	public boolean m_Looped;
	public float m_BlendTime;
	public float m_BlendOutTime;
	public boolean m_StopAnimOnExit;
	public boolean m_EarlyTransitionOut;
	public String m_SpeedScale;
	public String m_SpeedScaleVariable;
	public float m_SpeedScaleRandomMultiplierMin;
	public float m_SpeedScaleRandomMultiplierMax;
	@XmlTransient
	private float m_SpeedScaleF;
	public float m_randomAdvanceFraction;
	public float m_maxTorsoTwist;
	public String m_Scalar;
	public String m_Scalar2;
	public boolean m_AnimReverse;
	public boolean m_SyncTrackingEnabled;
	public List m_2DBlends;
	public List m_Conditions;
	public List m_Events;
	public List m_2DBlendTri;
	public List m_Transitions;
	public List m_SubStateBoneWeights;
	@XmlTransient
	public Anim2DBlendPicker m_picker;
	@XmlTransient
	public AnimState m_State;
	@XmlTransient
	private AnimTransition m_transitionOut;

	public AnimNode() {
		this.m_deferredBoneAxis = BoneAxis.Y;
		this.m_useDeferedRotation = false;
		this.m_Looped = true;
		this.m_BlendTime = 0.0F;
		this.m_BlendOutTime = -1.0F;
		this.m_StopAnimOnExit = false;
		this.m_EarlyTransitionOut = false;
		this.m_SpeedScale = "1.00";
		this.m_SpeedScaleVariable = null;
		this.m_SpeedScaleRandomMultiplierMin = 1.0F;
		this.m_SpeedScaleRandomMultiplierMax = 1.0F;
		this.m_SpeedScaleF = Float.POSITIVE_INFINITY;
		this.m_randomAdvanceFraction = 0.0F;
		this.m_maxTorsoTwist = 15.0F;
		this.m_Scalar = "";
		this.m_Scalar2 = "";
		this.m_AnimReverse = false;
		this.m_SyncTrackingEnabled = true;
		this.m_2DBlends = new ArrayList();
		this.m_Conditions = new ArrayList();
		this.m_Events = new ArrayList();
		this.m_2DBlendTri = new ArrayList();
		this.m_Transitions = new ArrayList();
		this.m_SubStateBoneWeights = new ArrayList();
		this.m_State = null;
	}

	public static AnimNode Parse(String string) {
		try {
			AnimNode animNode = (AnimNode)PZXmlUtil.parse(AnimNode.class, string);
			if (animNode.m_2DBlendTri.size() > 0) {
				animNode.m_picker = new Anim2DBlendPicker();
				animNode.m_picker.SetPickTriangles(animNode.m_2DBlendTri);
			}

			PZArrayUtil.forEach(animNode.m_Events, (stringx)->{
				if ("SetVariable".equalsIgnoreCase(stringx.m_EventName)) {
					String[] animNode = stringx.m_ParameterValue.split("=");
					if (animNode.length == 2) {
						stringx.m_SetVariable1 = animNode[0];
						stringx.m_SetVariable2 = animNode[1];
					}
				}
			});

			animNode.m_Events.sort(s_eventsComparator);
			try {
				animNode.m_SpeedScaleF = Float.parseFloat(animNode.m_SpeedScale);
			} catch (NumberFormatException numberFormatException) {
				animNode.m_SpeedScaleVariable = animNode.m_SpeedScale;
			}

			if (animNode.m_SubStateBoneWeights.isEmpty()) {
				animNode.m_SubStateBoneWeights.add(new AnimBoneWeight("Bip01_Spine1", 0.5F));
				animNode.m_SubStateBoneWeights.add(new AnimBoneWeight("Bip01_Neck", 1.0F));
				animNode.m_SubStateBoneWeights.add(new AnimBoneWeight("Bip01_BackPack", 1.0F));
				animNode.m_SubStateBoneWeights.add(new AnimBoneWeight("Bip01_Prop1", 1.0F));
				animNode.m_SubStateBoneWeights.add(new AnimBoneWeight("Bip01_Prop2", 1.0F));
			}

			int int1;
			for (int1 = 0; int1 < animNode.m_SubStateBoneWeights.size(); ++int1) {
				AnimBoneWeight animBoneWeight = (AnimBoneWeight)animNode.m_SubStateBoneWeights.get(int1);
				animBoneWeight.boneName = JAssImpImporter.getSharedString(animBoneWeight.boneName, "AnimBoneWeight.boneName");
			}

			animNode.m_transitionOut = null;
			for (int1 = 0; int1 < animNode.m_Transitions.size(); ++int1) {
				AnimTransition animTransition = (AnimTransition)animNode.m_Transitions.get(int1);
				if (StringUtils.isNullOrWhitespace(animTransition.m_Target)) {
					animNode.m_transitionOut = animTransition;
				}
			}

			return animNode;
		} catch (PZXmlParserException pZXmlParserException) {
			System.err.println("AnimNode.Parse threw an exception reading file: " + string);
			ExceptionLogger.logException(pZXmlParserException);
			return null;
		}
	}

	public boolean checkConditions(IAnimationVariableSource iAnimationVariableSource) {
		List list = this.m_Conditions;
		return AnimCondition.pass(iAnimationVariableSource, list);
	}

	public float getSpeedScale(IAnimationVariableSource iAnimationVariableSource) {
		return this.m_SpeedScaleF != Float.POSITIVE_INFINITY ? this.m_SpeedScaleF : iAnimationVariableSource.getVariableFloat(this.m_SpeedScale, 1.0F);
	}

	public boolean isIdleAnim() {
		return this.m_Name.contains("Idle");
	}

	public AnimTransition findTransitionTo(IAnimationVariableSource iAnimationVariableSource, String string) {
		AnimTransition animTransition = null;
		int int1 = 0;
		for (int int2 = this.m_Transitions.size(); int1 < int2; ++int1) {
			AnimTransition animTransition2 = (AnimTransition)this.m_Transitions.get(int1);
			if (StringUtils.equalsIgnoreCase(animTransition2.m_Target, string) && AnimCondition.pass(iAnimationVariableSource, animTransition2.m_Conditions)) {
				animTransition = animTransition2;
				break;
			}
		}

		return animTransition;
	}

	public String toString() {
		return String.format("AnimNode{ Name: %s, AnimName: %s, Conditions: %s }", this.m_Name, this.m_AnimName, this.getConditionsString());
	}

	public String getConditionsString() {
		return PZArrayUtil.arrayToString(this.m_Conditions, AnimCondition::getConditionString, "( ", " )", ", ");
	}

	public boolean isAbstract() {
		if (!StringUtils.isNullOrWhitespace(this.m_AnimName)) {
			return false;
		} else {
			return this.m_2DBlends.isEmpty();
		}
	}

	public float getBlendOutTime() {
		if (this.m_transitionOut != null) {
			return this.m_transitionOut.m_blendOutTime;
		} else {
			return this.m_BlendOutTime >= 0.0F ? this.m_BlendOutTime : this.m_BlendTime;
		}
	}

	public String getDeferredBoneName() {
		return StringUtils.isNullOrWhitespace(this.m_DeferredBoneName) ? "Translation_Data" : this.m_DeferredBoneName;
	}

	public BoneAxis getDeferredBoneAxis() {
		return this.m_deferredBoneAxis;
	}

	public int getPriority() {
		return this.m_Priority;
	}
}
