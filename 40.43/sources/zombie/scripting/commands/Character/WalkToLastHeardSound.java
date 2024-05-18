package zombie.scripting.commands.Character;

import java.security.InvalidParameterException;
import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;


public class WalkToLastHeardSound extends BaseCommand {
	String chara;
	int x;
	int y;
	int z;
	int timer = 0;
	Behavior.BehaviorResult res;
	PathFindBehavior behavior;
	String owner;
	boolean bDone;
	IsoGameCharacter aowner;

	public WalkToLastHeardSound() {
		this.res = Behavior.BehaviorResult.Working;
		this.behavior = new PathFindBehavior(true);
		this.bDone = false;
	}

	public void init(String string, String[] stringArray) {
		this.owner = string;
	}

	public void begin() {
		if (this.module.getCharacter(this.owner).Actual == null) {
			throw new InvalidParameterException();
		} else {
			this.aowner = this.module.getCharacter(this.owner).Actual;
			IsoGameCharacter.Location location = this.aowner.getLastHeardSound();
			if (location != null && location.x != -1) {
				this.behavior.sx = (int)this.aowner.getX();
				this.behavior.sy = (int)this.aowner.getY();
				this.behavior.sz = (int)this.aowner.getZ();
				this.behavior.tx = location.x;
				this.behavior.ty = location.y;
				this.behavior.tz = location.z;
				this.behavior.pathIndex = 0;
			} else {
				this.bDone = true;
			}

			this.timer = 10;
		}
	}

	public boolean AllowCharacterBehaviour(String string) {
		return false;
	}

	public void Finish() {
		this.aowner = null;
		this.res = Behavior.BehaviorResult.Working;
		this.behavior.reset();
	}

	public boolean IsFinished() {
		return this.bDone || this.res == Behavior.BehaviorResult.Succeeded;
	}

	public void update() {
		if (this.res == Behavior.BehaviorResult.Failed) {
			this.begin();
		}

		this.res = this.behavior.process((DecisionPath)null, this.aowner);
	}

	public boolean DoesInstantly() {
		return false;
	}
}
