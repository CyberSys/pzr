package zombie.core.skinnedmodel.runtime;

import java.util.Iterator;
import java.util.List;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.scripting.ScriptParser;


public final class CopyFrames implements IRuntimeAnimationCommand {
	protected int m_frame;
	protected int m_FPS = 30;
	protected String m_source;
	protected int m_sourceFrame1;
	protected int m_sourceFrame2;
	protected int m_sourceFPS = 30;

	public void parse(ScriptParser.Block block) {
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string = value.getKey().trim();
			String string2 = value.getValue().trim();
			if ("source".equalsIgnoreCase(string)) {
				this.m_source = string2;
			} else if ("frame".equalsIgnoreCase(string)) {
				this.m_frame = PZMath.tryParseInt(string2, 1);
			} else if ("sourceFrame1".equalsIgnoreCase(string)) {
				this.m_sourceFrame1 = PZMath.tryParseInt(string2, 1);
			} else if ("sourceFrame2".equalsIgnoreCase(string)) {
				this.m_sourceFrame2 = PZMath.tryParseInt(string2, 1);
			}
		}
	}

	public void exec(List list) {
		AnimationClip animationClip = ModelManager.instance.getAnimationClip(this.m_source);
		for (int int1 = 0; int1 < 60; ++int1) {
			Keyframe[] keyframeArray = animationClip.getBoneFramesAt(int1);
			if (keyframeArray.length != 0) {
				for (int int2 = this.m_sourceFrame1; int2 <= this.m_sourceFrame2; ++int2) {
					Keyframe keyframe = keyframeArray[0];
					Keyframe keyframe2 = new Keyframe();
					keyframe2.Bone = keyframe.Bone;
					keyframe2.BoneName = keyframe.BoneName;
					keyframe2.Time = (float)(this.m_frame - 1 + (int2 - this.m_sourceFrame1)) / (float)this.m_FPS;
					keyframe2.Position = KeyframeUtil.GetKeyFramePosition(keyframeArray, (float)(int2 - 1) / (float)this.m_sourceFPS, (double)animationClip.Duration);
					keyframe2.Rotation = KeyframeUtil.GetKeyFrameRotation(keyframeArray, (float)(int2 - 1) / (float)this.m_sourceFPS, (double)animationClip.Duration);
					keyframe2.Scale = keyframe.Scale;
					list.add(keyframe2);
				}
			}
		}
	}
}
