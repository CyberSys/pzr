package zombie.scripting.commands.Character;

import java.security.InvalidParameterException;
import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoWorld;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;


public class WalkCommand extends BaseCommand {
	int x;
	int y;
	int z;
	Behavior.BehaviorResult res;
	PathFindBehavior behavior;
	String owner;
	IsoGameCharacter aowner;

	public void updateskip() {
		IsoGameCharacter gameCharacter = this.module.getCharacterActual(this.owner);
		gameCharacter.setX((float)this.x + 0.5F);
		gameCharacter.setY((float)this.y + 0.5F);
		gameCharacter.setZ((float)this.z);
		gameCharacter.getCurrentSquare().getMovingObjects().remove(gameCharacter);
		gameCharacter.setCurrent(IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z));
		if (gameCharacter.getCurrentSquare() != null) {
			gameCharacter.getCurrentSquare().getMovingObjects().add(gameCharacter);
		}
	}

	public WalkCommand() {
		this.res = Behavior.BehaviorResult.Working;
		this.behavior = new PathFindBehavior(true);
	}

	public void init(String string, String[] stringArray) {
		if (stringArray.length == 1) {
			Waypoint waypoint = this.module.getWaypoint(stringArray[0].trim());
			if (waypoint != null) {
				this.x = waypoint.x;
				this.y = waypoint.y;
				this.z = waypoint.z;
			}

			this.owner = string;
		}
	}

	public void begin() {
		if (this.module.getCharacter(this.owner).Actual == null) {
			throw new InvalidParameterException();
		} else {
			this.aowner = this.module.getCharacter(this.owner).Actual;
			this.behavior.sx = (int)this.aowner.getX();
			this.behavior.sy = (int)this.aowner.getY();
			this.behavior.sz = (int)this.aowner.getZ();
			this.behavior.tx = this.x;
			this.behavior.ty = this.y;
			this.behavior.tz = this.z;
			this.behavior.pathIndex = 0;
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
		return this.res == Behavior.BehaviorResult.Succeeded;
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
