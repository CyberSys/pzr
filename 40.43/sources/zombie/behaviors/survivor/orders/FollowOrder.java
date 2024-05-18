package zombie.behaviors.survivor.orders;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.core.Rand;
import zombie.iso.IsoCamera;
import zombie.ui.TextManager;
import zombie.ui.UIFont;


public class FollowOrder extends Order {
	public int Range = 1;
	public int pathfindtimer = 60;
	public IsoGameCharacter target;
	public boolean bStrict = false;
	PathFindBehavior PathFind = new PathFindBehavior();
	float rangelast = 0.0F;
	public float lastDist = 0.0F;
	public float currentDist = 0.0F;

	public FollowOrder(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, int int1) {
		super(gameCharacter);
		this.target = gameCharacter2;
		this.Range = int1;
		this.PathFind.reset();
		this.PathFind.name = "FollowOrder";
	}

	public FollowOrder(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
	}

	public FollowOrder(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, int int1, boolean boolean1) {
		super(gameCharacter);
		this.target = gameCharacter2;
		this.Range = int1;
		this.PathFind.reset();
		this.PathFind.name = "FollowOrder";
		this.bStrict = true;
	}

	private boolean InDistanceOfPlayer(IsoGameCharacter gameCharacter, int int1, int int2) {
		if (this.target.getCurrentSquare() == null) {
			return true;
		} else {
			this.target.ensureOnTile();
			gameCharacter.ensureOnTile();
			if (this.target.getCurrentSquare() != null && this.target.getCurrentSquare().getRoom() != null && gameCharacter.getCurrentSquare().getRoom() != this.target.getCurrentSquare().getRoom()) {
				return false;
			} else if (this.target.getCurrentSquare() != null && gameCharacter.getCurrentSquare().getRoom() != null && this.target.getCurrentSquare().getRoom() == null) {
				return false;
			} else {
				this.rangelast = this.currentDist;
				return this.rangelast < (float)this.Range;
			}
		}
	}

	public Behavior.BehaviorResult process() {
		if (this.target == null) {
			return Behavior.BehaviorResult.Failed;
		} else {
			boolean boolean1;
			if (this.character == IsoCamera.CamCharacter) {
				boolean1 = false;
			}

			this.lastDist = this.currentDist;
			this.currentDist = this.character.DistTo(this.target);
			--this.pathfindtimer;
			boolean1 = this.InDistanceOfPlayer(this.character, (int)this.character.getX(), (int)this.character.getY());
			if (!boolean1 && this.pathfindtimer < 0 && this.currentDist > (float)this.Range) {
				this.PathFind.reset();
				this.PathFind.sx = (int)this.character.getX();
				this.PathFind.sy = (int)this.character.getY();
				this.PathFind.sz = (int)this.character.getZ();
				this.PathFind.tx = (int)this.target.getX() + (Rand.Next(6) - 3);
				this.PathFind.ty = (int)this.target.getY() + (Rand.Next(6) - 3);
				this.PathFind.tz = (int)this.target.getZ();
				this.pathfindtimer = 120;
			}

			Behavior.BehaviorResult behaviorResult = this.PathFind.process((DecisionPath)null, this.character);
			if (behaviorResult != Behavior.BehaviorResult.Working) {
				this.pathfindtimer = -1;
			}

			return behaviorResult;
		}
	}

	public boolean isCancelledOnAttack() {
		return false;
	}

	public float getPriority(IsoGameCharacter gameCharacter) {
		float float1 = 0.0F;
		if (this.target == null) {
			return -1000000.0F;
		} else {
			float float2 = gameCharacter.DistTo(this.target);
			this.lastDist = this.currentDist;
			this.currentDist = float2;
			float2 -= (float)this.Range;
			if (float2 < 0.0F) {
				float2 = 0.0F;
			}

			float1 += float2 * 6.0F;
			float1 += (float)(gameCharacter.getThreatLevel() * 5);
			float1 += gameCharacter.getDescriptor().getLoyalty() * 5.0F;
			if (this.target != null && this.target.getLegsSprite().CurrentAnim.name.equals("Run")) {
				float1 *= 2.0F;
			}

			boolean boolean1 = this.InDistanceOfPlayer(gameCharacter, (int)gameCharacter.getX(), (int)gameCharacter.getY());
			if (boolean1) {
				float1 = 0.0F;
			} else {
				float1 *= 20000.0F;
			}

			return gameCharacter instanceof IsoSurvivor && !((IsoSurvivor)gameCharacter).getVeryCloseEnemyList().isEmpty() ? 0.0F : float1;
		}
	}

	public float getPathSpeed() {
		return this.currentDist > (float)this.Range * 3.0F ? 0.08F : 0.06F;
	}

	public int renderDebug(int int1) {
		byte byte1 = 50;
		TextManager.instance.DrawString(UIFont.Small, (double)byte1, (double)int1, "FollowOrder", 1.0, 1.0, 1.0, 1.0);
		int1 += 30;
		return int1;
	}

	public boolean complete() {
		return this.target == null ? true : this.target.isDead();
	}

	public void update() {
	}
}
