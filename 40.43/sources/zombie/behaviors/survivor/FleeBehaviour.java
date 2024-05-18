package zombie.behaviors.survivor;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.behaviors.survivor.orders.FollowOrder;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.iso.IsoCamera;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.areas.IsoBuilding;
import zombie.ui.TextManager;
import zombie.ui.UIFont;


public class FleeBehaviour extends Behavior {
	public boolean Started = false;
	boolean OtherRoom = false;
	PathFindBehavior pathFind = new PathFindBehavior();
	IsoGridSquare sq = null;
	IsoGameCharacter character;
	int recalc = 240;
	FollowOrder order;
	public boolean bFollowFlee = false;
	public boolean bPickedFleeStyle = false;
	static Vector2 tempo = new Vector2();

	public void onSwitch() {
		this.bFollowFlee = false;
		this.bPickedFleeStyle = false;
	}

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		if (this.order == null) {
			this.order = new FollowOrder(gameCharacter);
		}

		this.pathFind.name = "FleeBehaviour";
		this.character = gameCharacter;
		Behavior.BehaviorResult behaviorResult = Behavior.BehaviorResult.Failed;
		--this.recalc;
		if (!this.Started) {
			this.Started = true;
			this.OtherRoom = false;
			float float1 = 0.0F;
			float float2 = 0.0F;
			for (int int1 = 0; int1 < gameCharacter.getLocalEnemyList().size(); ++int1) {
				IsoGameCharacter gameCharacter2 = (IsoGameCharacter)gameCharacter.getLocalEnemyList().get(int1);
				float float3 = gameCharacter2.x - gameCharacter.x;
				float float4 = gameCharacter2.y - gameCharacter.y;
				float3 = -float3;
				float4 = -float4;
				Vector2 vector2 = tempo;
				tempo.x = float3;
				tempo.y = float4;
				float float5 = vector2.getLength();
				if (float5 == 0.0F) {
					float5 = 0.1F;
				}

				vector2.normalize();
				float1 += vector2.x / float5;
				float2 += vector2.y / float5;
			}

			tempo.x = float1;
			tempo.y = float2;
			tempo.setLength(4.0F);
			float1 = tempo.x;
			float2 = tempo.y;
			this.sq = IsoWorld.instance.CurrentCell.getGridSquare((double)(gameCharacter.getX() + float1), (double)(gameCharacter.getY() + float2), 0.0);
		}

		if (this.sq != null) {
			if (!this.pathFind.running(gameCharacter)) {
				gameCharacter.setPathSpeed(0.08F);
				this.pathFind.reset();
				this.pathFind.sx = (int)gameCharacter.getX();
				this.pathFind.sy = (int)gameCharacter.getY();
				this.pathFind.sz = (int)gameCharacter.getZ();
				this.pathFind.tx = this.sq.getX();
				this.pathFind.ty = this.sq.getY();
				this.pathFind.tz = this.sq.getZ();
			}

			behaviorResult = this.pathFind.process(decisionPath, gameCharacter);
			if (behaviorResult == Behavior.BehaviorResult.Failed) {
				this.sq = null;
				this.reset();
			} else if (behaviorResult == Behavior.BehaviorResult.Succeeded) {
				gameCharacter.getStats().idleboredom = 0.0F;
				this.sq = null;
				this.reset();
			}
		}

		return Behavior.BehaviorResult.Working;
	}

	private boolean RunToBuilding(float float1, float float2, IsoGameCharacter gameCharacter) {
		boolean boolean1 = false;
		int int1 = 0;
		while (!boolean1) {
			++int1;
			boolean boolean2 = false;
			boolean boolean3 = false;
			int int2 = Rand.Next(5) + 1;
			int int3 = (int)(float1 * (float)int2);
			int int4 = (int)(float2 * (float)int2);
			int int5 = int3 + (Rand.Next(4) - 2);
			int5 = int4 + (Rand.Next(4) - 2);
			IsoBuilding building = gameCharacter.getDescriptor().getGroup().Safehouse;
			if (building == null) {
				return false;
			}

			this.sq = building.getRandomRoom().getFreeTile();
			if (int1 >= 20) {
				return true;
			}

			if (this.sq != null && !this.sq.getProperties().Is(IsoFlagType.solidtrans) && !this.sq.getProperties().Is(IsoFlagType.solid)) {
				boolean1 = true;
			}
		}

		return false;
	}

	public void reset() {
		this.Started = false;
		this.sq = null;
		this.pathFind.reset();
	}

	public boolean valid() {
		return true;
	}

	public float getPriority(IsoGameCharacter gameCharacter) {
		float float1 = 0.0F;
		if (gameCharacter == IsoCamera.CamCharacter) {
			boolean boolean1 = false;
		}

		if (IsoPlayer.DemoMode && gameCharacter.IsArmed()) {
			return gameCharacter.getStats().endurance < gameCharacter.getStats().endurancewarn ? 1.0E8F : -100000.0F;
		} else {
			return !gameCharacter.IsArmed() && gameCharacter.getVeryCloseEnemyList().size() > 0 ? 1.0E8F : -1000000.0F;
		}
	}

	public float getPathSpeed() {
		return 0.08F;
	}

	public int renderDebug(int int1) {
		byte byte1 = 50;
		TextManager.instance.DrawString(UIFont.Small, (double)byte1, (double)int1, "FleeBehaviour", 1.0, 1.0, 1.0, 1.0);
		int1 += 30;
		return int1;
	}
}
