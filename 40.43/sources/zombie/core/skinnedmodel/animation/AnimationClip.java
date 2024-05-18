package zombie.core.skinnedmodel.animation;

import java.util.List;


public class AnimationClip {
	public final String Name;
	public float Duration;
	public List Keyframes;
	public Keyframe[] KeyframeArray = new Keyframe[0];

	public AnimationClip(float float1, List list, String string) {
		this.Duration = float1;
		this.Keyframes = list;
		this.KeyframeArray = (Keyframe[])this.Keyframes.toArray(this.KeyframeArray);
		this.Name = string;
	}
}
