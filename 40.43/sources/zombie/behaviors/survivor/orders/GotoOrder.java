package zombie.behaviors.survivor.orders;

import java.util.Stack;
import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.ui.TextManager;
import zombie.ui.UIFont;


public class GotoOrder extends Order {
	public int failedcount = 0;
	int x;
	int y;
	int z;
	public Stack Waypoints = new Stack();
	public int currentwaypoint = 0;
	float nextpathfind = 10.0F;
	PathFindBehavior PathFind = new PathFindBehavior("Goto");
	public IsoGameCharacter chr;
	static Vector2 vec = new Vector2();
	Behavior.BehaviorResult res;

	private GotoOrder.Waypoint AddIntermediate(float float1, int int1, int int2, int int3, int int4, int int5, int int6) {
		new Vector2((float)int4, (float)int5);
		Vector2 vector2 = new Vector2((float)int1, (float)int2);
		Vector2 vector22 = new Vector2((float)(int4 - int1), (float)(int5 - int2));
		vector22.setLength(float1 / 2.0F);
		vector2.x += vector22.x;
		vector2.y += vector22.y;
		int int7 = 0;
		IsoGridSquare square = null;
		do {
			int int8 = int7 * 3;
			if (int8 == 0) {
				int8 = 1;
			}

			for (int int9 = 0; int9 < int8; ++int9) {
				int int10 = (int)vector2.x + Rand.Next(-int7, int7);
				int int11 = (int)vector2.y + Rand.Next(-int7, int7);
				square = IsoWorld.instance.CurrentCell.getGridSquare(int10, int11, 0);
				if (!square.isFree(false)) {
					square = null;
				}

				if (square != null) {
					int9 = int8;
				}
			}

			if (square == null) {
				++int7;
			}
		} while (square == null);

		return new GotoOrder.Waypoint(square.getX(), square.getY(), 0);
	}

	private void SplitWaypoints(int int1, int int2, int int3, int int4, int int5, int int6) {
		Stack stack = new Stack();
		byte byte1 = 0;
		int int7 = this.Waypoints.size();
		for (int int8 = 0; int8 < int7; ++int8) {
			int int9 = int4;
			int int10 = int5;
			if (int7 > int8 + 1) {
				int9 = ((GotoOrder.Waypoint)this.Waypoints.get(int8 + 1)).x;
				int10 = ((GotoOrder.Waypoint)this.Waypoints.get(int8 + 1)).y;
			}

			float float1 = IsoUtils.DistanceManhatten((float)int1, (float)int2, (float)((GotoOrder.Waypoint)this.Waypoints.get(int8)).x, (float)((GotoOrder.Waypoint)this.Waypoints.get(int8)).y);
			GotoOrder.Waypoint waypoint = this.AddIntermediate(float1, int1, int2, byte1, int9, int10, int6);
			int1 = ((GotoOrder.Waypoint)this.Waypoints.get(int8)).x;
			int2 = ((GotoOrder.Waypoint)this.Waypoints.get(int8)).y;
			stack.add(waypoint);
			stack.add(this.Waypoints.get(int8));
		}

		this.Waypoints.clear();
		this.Waypoints.addAll(stack);
	}

	public int getAttackIfEnemiesAroundBias() {
		return this.character.getCurrentSquare().getRoom() != null ? -1000 : 0;
	}

	private void CalculateWaypoints(IsoGameCharacter gameCharacter, int int1, int int2, int int3, int int4, int int5, int int6) {
		this.Waypoints.clear();
		this.Waypoints.add(new GotoOrder.Waypoint(int4, int5, int6));
		float float1 = IsoUtils.DistanceManhatten((float)int1, (float)int2, (float)int4, (float)int5);
		int int7 = (int)(float1 / 60.0F);
		if (this.failedcount > 2 && float1 > 60.0F) {
			int7 += this.failedcount - 2;
		}

		if (int7 > 4) {
			boolean boolean1 = true;
		}
	}

	public GotoOrder(IsoGameCharacter gameCharacter, int int1, int int2, int int3) {
		super(gameCharacter);
		this.res = Behavior.BehaviorResult.Working;
		this.PathFind.bDoClosest = true;
		this.chr = gameCharacter;
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.PathFind.tx = int1;
		this.PathFind.ty = int2;
		this.PathFind.tz = int3;
		this.nextpathfind = (float)Rand.Next(10);
		this.CalculateWaypoints(gameCharacter, (int)gameCharacter.getX(), (int)gameCharacter.getY(), (int)gameCharacter.getZ(), int1, int2, int3);
		this.currentwaypoint = 0;
		this.PathFind.reset();
		this.PathFind.sx = (int)gameCharacter.getX();
		this.PathFind.sy = (int)gameCharacter.getY();
		this.PathFind.sz = (int)gameCharacter.getZ();
		this.PathFind.tx = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).x;
		this.PathFind.ty = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).y;
		this.PathFind.tz = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).z;
		this.TestOutStreamRange(gameCharacter, int3);
		this.PathFind.otx = this.PathFind.tx;
		this.PathFind.oty = this.PathFind.ty;
		this.PathFind.otz = this.PathFind.tz;
	}

	private void TestOutStreamRange(IsoGameCharacter gameCharacter, int int1) {
		IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()];
		if (this.PathFind.tx >= chunkMap.getWorldXMinTiles() && this.PathFind.tx <= chunkMap.getWorldXMaxTiles() && this.PathFind.ty >= chunkMap.getWorldYMinTiles() && this.PathFind.ty <= chunkMap.getWorldYMaxTiles()) {
			boolean boolean1 = false;
		} else {
			vec.x = (float)this.PathFind.tx;
			vec.y = (float)this.PathFind.ty;
			Vector2 vector2 = vec;
			vector2.x -= (float)this.PathFind.sx;
			vector2 = vec;
			vector2.y -= (float)this.PathFind.sy;
			if (vec.x != 0.0F || vec.y != 0.0F) {
				vector2 = vec;
				vector2.x += (float)(Rand.Next(50) - 25);
				vector2 = vec;
				vector2.y += (float)(Rand.Next(50) - 25);
				vec.normalize();
				vector2 = vec;
				vector2.x *= 2.0F;
				vector2 = vec;
				vector2.y *= 2.0F;
				IsoGridSquare square = gameCharacter.getCurrentSquare();
				IsoGridSquare square2 = null;
				float float1 = (float)this.PathFind.sx;
				float float2 = (float)this.PathFind.sy;
				do {
					square2 = square;
					square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)int1);
					float1 += vec.x;
					float2 += vec.y;
				}		 while (square != null);

				while (square2 == null || !square2.isFree(false)) {
					float1 -= vec.x;
					float2 -= vec.y;
					square2 = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)int1);
				}

				this.PathFind.tx = square2.getX();
				this.PathFind.ty = square2.getY();
				this.PathFind.bDoClosest = true;
			}
		}
	}

	public GotoOrder(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		this.res = Behavior.BehaviorResult.Working;
		this.chr = gameCharacter;
	}

	public void init(int int1, int int2, int int3) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.PathFind.tx = int1;
		this.PathFind.ty = int2;
		this.PathFind.tz = int3;
		this.nextpathfind = (float)Rand.Next(120);
		this.CalculateWaypoints(this.chr, (int)this.chr.getX(), (int)this.chr.getY(), (int)this.chr.getZ(), int1, int2, int3);
		this.currentwaypoint = 0;
		this.PathFind.reset();
		this.PathFind.sx = (int)this.chr.getX();
		this.PathFind.sy = (int)this.chr.getY();
		this.PathFind.sz = (int)this.chr.getZ();
		this.PathFind.tx = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).x;
		this.PathFind.ty = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).y;
		this.PathFind.tz = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).z;
		this.PathFind.osx = this.PathFind.sx;
		this.PathFind.osy = this.PathFind.sy;
		this.PathFind.osz = this.PathFind.sz;
		this.TestOutStreamRange(this.chr, int3);
		this.PathFind.otx = this.PathFind.tx;
		this.PathFind.oty = this.PathFind.ty;
		this.PathFind.otz = this.PathFind.tz;
	}

	public boolean complete() {
		return this.currentwaypoint >= this.Waypoints.size() || this.failedcount > 20;
	}

	public void update() {
		if (this.res != Behavior.BehaviorResult.Working) {
			--this.nextpathfind;
		}
	}

	public Behavior.BehaviorResult process() {
		boolean boolean1;
		if (this.character == IsoCamera.CamCharacter) {
			boolean1 = false;
		}

		this.res = this.PathFind.process((DecisionPath)null, this.character);
		if (this.res == Behavior.BehaviorResult.Succeeded && this.currentwaypoint < this.Waypoints.size()) {
			GotoOrder.Waypoint waypoint = (GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint);
			if ((int)this.character.getX() == waypoint.x && (int)this.character.getY() == waypoint.y && (int)this.character.getZ() == waypoint.z) {
				this.nextpathfind = -1.0F;
			} else {
				this.res = Behavior.BehaviorResult.Failed;
			}
		}

		if (this.res == Behavior.BehaviorResult.Failed && this.nextpathfind < 0.0F) {
			++this.failedcount;
			if (this.failedcount > 100) {
			}

			if (this.character == IsoCamera.CamCharacter) {
				boolean1 = false;
			}

			this.nextpathfind = 10.0F;
			this.CalculateWaypoints(this.chr, (int)this.chr.getX(), (int)this.chr.getY(), (int)this.chr.getZ(), this.x, this.y, this.z);
			this.currentwaypoint = 0;
			this.PathFind.reset();
			this.PathFind.sx = (int)this.chr.getX();
			this.PathFind.sy = (int)this.chr.getY();
			this.PathFind.sz = (int)this.chr.getZ();
			this.PathFind.tx = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).x;
			this.PathFind.ty = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).y;
			this.PathFind.tz = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).z;
			this.PathFind.osx = this.PathFind.sx;
			this.PathFind.osy = this.PathFind.sy;
			this.PathFind.osz = this.PathFind.sz;
			this.TestOutStreamRange(this.chr, this.z);
			this.PathFind.otx = this.PathFind.tx;
			this.PathFind.oty = this.PathFind.ty;
			this.PathFind.otz = this.PathFind.tz;
			this.res = this.PathFind.process((DecisionPath)null, this.character);
		}

		if (this.res == Behavior.BehaviorResult.Succeeded && this.currentwaypoint < this.Waypoints.size() && this.nextpathfind < 0.0F) {
			if (this.x != this.PathFind.tx || this.y != this.PathFind.ty) {
				this.PathFind.reset();
				Behavior.BehaviorResult behaviorResult = this.res;
				return Behavior.BehaviorResult.Working;
			}

			if (this.character == IsoCamera.CamCharacter) {
				boolean1 = false;
			}

			++this.currentwaypoint;
			this.nextpathfind = 10.0F;
			if (this.currentwaypoint < this.Waypoints.size()) {
				int int1 = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).x;
				int int2 = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).y;
				int int3 = ((GotoOrder.Waypoint)this.Waypoints.get(this.currentwaypoint)).z;
				this.x = int1;
				this.y = int2;
				this.z = int3;
				this.PathFind.reset();
				this.PathFind.tx = int1;
				this.PathFind.ty = int2;
				this.PathFind.tz = int3;
				this.PathFind.sx = (int)this.chr.getX();
				this.PathFind.sy = (int)this.chr.getY();
				this.PathFind.sz = (int)this.chr.getZ();
				this.res = this.PathFind.process((DecisionPath)null, this.character);
			} else {
				this.res = Behavior.BehaviorResult.Succeeded;
			}
		}

		--this.nextpathfind;
		return this.currentwaypoint >= this.Waypoints.size() ? Behavior.BehaviorResult.Succeeded : Behavior.BehaviorResult.Working;
	}

	public int renderDebug(int int1) {
		byte byte1 = 50;
		TextManager.instance.DrawString(UIFont.Small, (double)byte1, (double)int1, "GotoOrder", 1.0, 1.0, 1.0, 1.0);
		int1 += 30;
		for (int int2 = this.currentwaypoint; int2 < this.Waypoints.size(); ++int2) {
			GotoOrder.Waypoint waypoint = (GotoOrder.Waypoint)this.Waypoints.get(int2);
			Integer integer = waypoint.x;
			Integer integer2 = waypoint.y;
			Integer integer3 = waypoint.z;
			TextManager.instance.DrawString(UIFont.Small, (double)byte1, (double)int1, "Waypoint " + int2 + " - x: " + integer + " y: " + integer2 + " z: " + integer3, 1.0, 1.0, 1.0, 1.0);
			int1 += 30;
		}

		this.PathFind.renderDebug(int1);
		return int1;
	}

	public float getPriority(IsoGameCharacter gameCharacter) {
		return this.res != Behavior.BehaviorResult.Working && !(this.nextpathfind < 0.0F) ? -100.0F : 200.0F;
	}

	public static class Waypoint {
		public int x;
		public int y;
		public int z;

		public Waypoint(int int1, int int2, int int3) {
			this.x = int1;
			this.y = int2;
			this.z = int3;
		}
	}
}
