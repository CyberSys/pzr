package zombie.core.skinnedmodel.animation;

import java.util.ArrayList;
import java.util.List;


public final class AnimationMultiTrack {
	private final ArrayList m_tracks = new ArrayList();
	private static final ArrayList tempTracks = new ArrayList();

	public AnimationTrack findTrack(String string) {
		int int1 = 0;
		for (int int2 = this.m_tracks.size(); int1 < int2; ++int1) {
			AnimationTrack animationTrack = (AnimationTrack)this.m_tracks.get(int1);
			if (animationTrack.name.equals(string)) {
				return animationTrack;
			}
		}

		return null;
	}

	public void addTrack(AnimationTrack animationTrack) {
		this.m_tracks.add(animationTrack);
	}

	public void removeTrack(AnimationTrack animationTrack) {
		int int1 = this.getIndexOfTrack(animationTrack);
		if (int1 > -1) {
			this.removeTrackAt(int1);
		}
	}

	public void removeTracks(List list) {
		tempTracks.clear();
		tempTracks.addAll(list);
		for (int int1 = 0; int1 < tempTracks.size(); ++int1) {
			this.removeTrack((AnimationTrack)tempTracks.get(int1));
		}
	}

	public void removeTrackAt(int int1) {
		((AnimationTrack)this.m_tracks.remove(int1)).release();
	}

	public int getIndexOfTrack(AnimationTrack animationTrack) {
		if (animationTrack == null) {
			return -1;
		} else {
			int int1 = -1;
			for (int int2 = 0; int2 < this.m_tracks.size(); ++int2) {
				AnimationTrack animationTrack2 = (AnimationTrack)this.m_tracks.get(int2);
				if (animationTrack2 == animationTrack) {
					int1 = int2;
					break;
				}
			}

			return int1;
		}
	}

	public void Update(float float1) {
		for (int int1 = 0; int1 < this.m_tracks.size(); ++int1) {
			AnimationTrack animationTrack = (AnimationTrack)this.m_tracks.get(int1);
			animationTrack.Update(float1);
			if (animationTrack.CurrentClip == null) {
				this.removeTrackAt(int1);
				--int1;
			}
		}
	}

	public float getDuration() {
		float float1 = 0.0F;
		for (int int1 = 0; int1 < this.m_tracks.size(); ++int1) {
			AnimationTrack animationTrack = (AnimationTrack)this.m_tracks.get(int1);
			float float2 = animationTrack.getDuration();
			if (animationTrack.CurrentClip != null && float2 > float1) {
				float1 = float2;
			}
		}

		return float1;
	}

	public void reset() {
		int int1 = 0;
		for (int int2 = this.m_tracks.size(); int1 < int2; ++int1) {
			AnimationTrack animationTrack = (AnimationTrack)this.m_tracks.get(int1);
			animationTrack.reset();
		}

		AnimationPlayer.releaseTracks(this.m_tracks);
		this.m_tracks.clear();
	}

	public List getTracks() {
		return this.m_tracks;
	}

	public int getTrackCount() {
		return this.m_tracks.size();
	}

	public AnimationTrack getTrackAt(int int1) {
		return (AnimationTrack)this.m_tracks.get(int1);
	}
}
