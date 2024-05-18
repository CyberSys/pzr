package zombie.iso.sprite;

import java.util.ArrayList;


public class AnimState {
	public ArrayList entries = new ArrayList(0);
	public AnimStateMachine machine;
	public IsoSpriteInstance inst;
	public int loopEntry = 0;
	public int currentEntry = 0;
	public float lastFrame = 0.0F;

	public AnimState(String string, AnimStateMachine animStateMachine, IsoSpriteInstance spriteInstance) {
		this.inst = spriteInstance;
		this.machine = animStateMachine;
	}

	public AnimState.AnimStateEntry addState(IsoAnim anim, float float1) {
		AnimState.AnimStateEntry animStateEntry = new AnimState.AnimStateEntry(anim, float1);
		this.entries.add(animStateEntry);
		return animStateEntry;
	}

	public void update() {
		if (this.currentEntry < this.entries.size()) {
			AnimState.AnimStateEntry animStateEntry = (AnimState.AnimStateEntry)this.entries.get(this.currentEntry);
			this.inst.parentSprite.PlayAnim(animStateEntry.anim);
			this.inst.AnimFrameIncrease = animStateEntry.AnimSpeedPerFrame;
			if (this.inst.Frame < this.lastFrame) {
				++this.currentEntry;
				if (this.loopEntry != -1) {
					this.currentEntry = this.loopEntry;
				}
			}

			this.lastFrame = this.inst.Frame;
		}
	}

	public static class AnimStateEntry {
		public IsoAnim anim;
		public float AnimSpeedPerFrame = 0.0F;

		public AnimStateEntry(IsoAnim anim, float float1) {
			this.anim = anim;
			this.AnimSpeedPerFrame = float1;
		}
	}
}
