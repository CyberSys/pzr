package zombie.core.skinnedmodel.animation;


public interface IAnimListener {

	void onAnimStarted(AnimationTrack animationTrack);

	void onLoopedAnim(AnimationTrack animationTrack);

	void onNonLoopedAnimFadeOut(AnimationTrack animationTrack);

	void onNonLoopedAnimFinished(AnimationTrack animationTrack);

	void onTrackDestroyed(AnimationTrack animationTrack);
}
