package zombie.core.skinnedmodel.animation.sharedskele;

import java.util.HashMap;
import zombie.core.skinnedmodel.animation.AnimationClip;


public class SharedSkeleAnimationRepository {
	private final HashMap m_tracksMap = new HashMap();

	public SharedSkeleAnimationTrack getTrack(AnimationClip animationClip) {
		return (SharedSkeleAnimationTrack)this.m_tracksMap.get(animationClip);
	}

	public void setTrack(AnimationClip animationClip, SharedSkeleAnimationTrack sharedSkeleAnimationTrack) {
		this.m_tracksMap.put(animationClip, sharedSkeleAnimationTrack);
	}
}