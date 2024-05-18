package zombie.ai.states;

import zombie.SoundManager;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;


public class WanderState extends State {
	static WanderState _instance = new WanderState();
	static Vector2 vec = new Vector2();

	public static WanderState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		this.chooseNewDirection(gameCharacter);
	}

	public void chooseNewDirection(IsoGameCharacter gameCharacter) {
		if (!(gameCharacter instanceof IsoZombie) || !((IsoZombie)gameCharacter).isUseless()) {
			boolean boolean1 = false;
			int int1 = 0;
			if (gameCharacter.getCurrentSquare() == null) {
				gameCharacter.ensureOnTile();
			}

			int int2;
			do {
				++int1;
				gameCharacter.setNextWander(300);
				gameCharacter.setPathTargetX((int)(gameCharacter.getX() + (float)Rand.Next(IsoWorld.instance.CurrentCell.getWidthInTiles()) - 75.0F));
				gameCharacter.setPathTargetY((int)(gameCharacter.getY() + (float)Rand.Next(IsoWorld.instance.CurrentCell.getHeightInTiles()) - 75.0F));
				int2 = LosUtil.lineClearCollideCount(gameCharacter, gameCharacter.getCell(), gameCharacter.getPathTargetX(), gameCharacter.getPathTargetY(), (int)gameCharacter.getZ(), (int)gameCharacter.getX(), (int)gameCharacter.getY(), (int)gameCharacter.getZ(), 1);
				vec.x = (float)gameCharacter.getPathTargetX();
				vec.y = (float)gameCharacter.getPathTargetY();
				Vector2 vector2 = vec;
				vector2.x -= gameCharacter.x;
				vector2 = vec;
				vector2.y -= gameCharacter.y;
				vec.setLength(((IsoZombie)gameCharacter).wanderSpeed);
				gameCharacter.reqMovement.x = vec.x;
				gameCharacter.reqMovement.y = vec.y;
				vec.normalize();
				gameCharacter.DirectionFromVector(vec);
			}	 while (int2 < 1 && int1 < 100);
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (!((IsoZombie)gameCharacter).bCrawling) {
			gameCharacter.setOnFloor(false);
		}

		zombie.bRunning = false;
		--zombie.iIgnoreDirectionChange;
		Vector2 vector2;
		float float1;
		float float2;
		float float3;
		if ((Rand.Next(100) != 0 || zombie.iIgnoreDirectionChange > 0) && !gameCharacter.isCollidedThisFrame() && (!(Math.abs((float)gameCharacter.getPathTargetX() - gameCharacter.getX()) <= 1.0F) || !(Math.abs((float)gameCharacter.getPathTargetY() - gameCharacter.getY()) <= 1.0F))) {
			vec.x = gameCharacter.reqMovement.x;
			vec.y = gameCharacter.reqMovement.y;
			float1 = vec.x;
			float2 = vec.y;
			float3 = IsoUtils.DistanceManhatten(IsoCamera.CamCharacter.x, IsoCamera.CamCharacter.y, gameCharacter.x, gameCharacter.y);
			float3 /= 30.0F;
			if (float3 > 1.0F) {
				float3 = 1.0F;
			}

			if (float3 < 0.0F) {
				float3 = 0.0F;
			}

			float3 = 1.0F - float3;
			vector2 = vec;
			vector2.x *= float3;
			vector2 = vec;
			vector2.y *= float3;
			gameCharacter.Move(gameCharacter.reqMovement);
			vec.normalize();
			gameCharacter.DirectionFromVector(vec);
			gameCharacter.reqMovement.x = float1;
			gameCharacter.reqMovement.y = float2;
		} else {
			this.chooseNewDirection(gameCharacter);
			vec.x = (float)gameCharacter.getPathTargetX();
			vec.y = (float)gameCharacter.getPathTargetY();
			vector2 = vec;
			vector2.x -= gameCharacter.x;
			vector2 = vec;
			vector2.y -= gameCharacter.y;
			vec.x = gameCharacter.reqMovement.x;
			vec.y = gameCharacter.reqMovement.y;
			float1 = vec.x;
			float2 = vec.y;
			float3 = IsoUtils.DistanceManhatten(IsoCamera.CamCharacter.x, IsoCamera.CamCharacter.y, gameCharacter.x, gameCharacter.y);
			if (float3 > 20.0F) {
				float3 /= 40.0F;
				if (float3 > 1.0F) {
					float3 = 1.0F;
				}

				if (float3 < 0.0F) {
					float3 = 0.0F;
				}

				float3 = 1.0F - float3;
				float3 *= 0.5F;
			} else {
				float3 = 1.0F;
			}

			vec.setLength(((IsoZombie)gameCharacter).wanderSpeed * float3);
			gameCharacter.DirectionFromVector(vec);
			gameCharacter.Move(vec);
			gameCharacter.reqMovement.x = float1;
			gameCharacter.reqMovement.y = float2;
		}
	}

	void calculate() {
		SoundManager.instance.update1();
	}
}
