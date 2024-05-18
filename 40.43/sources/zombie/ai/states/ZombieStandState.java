package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.Vector2;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class ZombieStandState extends State {
	static ZombieStandState _instance = new ZombieStandState();
	static Vector2 vec = new Vector2();

	public static ZombieStandState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		if (!GameClient.bClient && ((IsoZombie)gameCharacter).isFakeDead() && !((IsoZombie)gameCharacter).bCrawling && !"Zombie_CrawlLunge".equals(gameCharacter.getSprite().CurrentAnim.name)) {
			gameCharacter.getStateMachine().Lock = false;
			gameCharacter.getStateMachine().changeState(StaggerBackDieState.instance());
		} else {
			((IsoZombie)gameCharacter).chasingSound = false;
			((IsoZombie)gameCharacter).soundSourceTarget = null;
			((IsoZombie)gameCharacter).soundAttract = 0.0F;
			gameCharacter.setIgnoreMovementForDirection(true);
			((IsoZombie)gameCharacter).movex = 0.0F;
			((IsoZombie)gameCharacter).movey = 0.0F;
			if (!((IsoZombie)gameCharacter).bCrawling) {
				boolean boolean1 = false;
				if (((IsoZombie)gameCharacter).NetRemoteState != 1) {
					boolean1 = true;
				}

				((IsoZombie)gameCharacter).NetRemoteState = 1;
				((IsoZombie)gameCharacter).movex = 0.0F;
				((IsoZombie)gameCharacter).movey = 0.0F;
				gameCharacter.PlayAnim("ZombieIdle");
				gameCharacter.def.AnimFrameIncrease = 0.08F + (float)Rand.Next(1000) / 8000.0F;
				IsoSpriteInstance spriteInstance = gameCharacter.def;
				spriteInstance.AnimFrameIncrease *= 0.5F;
				gameCharacter.def.Frame = (float)Rand.Next(20);
				if (boolean1) {
					GameServer.sendZombie((IsoZombie)gameCharacter);
				}
			} else {
				((IsoZombie)gameCharacter).NetRemoteState = 1;
				gameCharacter.PlayAnim("ZombieCrawl");
				gameCharacter.def.AnimFrameIncrease = 0.0F;
			}

			GameServer.sendZombie((IsoZombie)gameCharacter);
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		zombie.NetRemoteState = 1;
		zombie.setRemoteMoveX(0.0F);
		zombie.setRemoteMoveY(0.0F);
		zombie.movex = 0.0F;
		zombie.movey = 0.0F;
		int int1;
		if (Core.bLastStand) {
			IsoPlayer player = null;
			float float1 = 1000000.0F;
			for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				if (IsoPlayer.players[int1] != null && IsoPlayer.players[int1].DistTo(gameCharacter) < float1 && !IsoPlayer.players[int1].isDead()) {
					float1 = IsoPlayer.players[int1].DistTo(gameCharacter);
					player = IsoPlayer.players[int1];
				}
			}

			if (player != null) {
				zombie.pathToCharacter(player);
			}
		} else {
			if (!((IsoZombie)gameCharacter).bCrawling) {
				gameCharacter.setOnFloor(false);
				gameCharacter.PlayAnim("ZombieIdle");
				((IsoZombie)gameCharacter).reqMovement.x = gameCharacter.angle.x;
				((IsoZombie)gameCharacter).reqMovement.y = gameCharacter.angle.y;
				gameCharacter.def.AnimFrameIncrease = 0.08F + (float)Rand.Next(1000) / 8000.0F;
				IsoSpriteInstance spriteInstance = gameCharacter.def;
				spriteInstance.AnimFrameIncrease *= 0.5F;
			} else {
				gameCharacter.PlayAnim("ZombieCrawl");
				gameCharacter.def.AnimFrameIncrease = 0.0F;
			}

			if (!((IsoZombie)gameCharacter).bIndoorZombie) {
				if (((IsoZombie)gameCharacter).isFakeDead()) {
					gameCharacter.getStateMachine().changeState(StaggerBackDieState.instance());
				} else {
					int int2 = RainManager.isRaining() ? 700 : 1100;
					int2 = Rand.AdjustForFramerate(int2);
					if (GameTime.getInstance().getTrueMultiplier() == 1.0F && gameCharacter.getStateMachine().getCurrent() == instance() && Rand.Next(int2) == 0 && !((IsoZombie)gameCharacter).isUseless()) {
						int int3 = (int)gameCharacter.getX() + (Rand.Next(8) - 4);
						int1 = (int)gameCharacter.getY() + (Rand.Next(8) - 4);
						if (gameCharacter.getCell().getGridSquare((double)int3, (double)int1, (double)gameCharacter.getZ()) != null && gameCharacter.getCell().getGridSquare((double)int3, (double)int1, (double)gameCharacter.getZ()).isFree(true)) {
							gameCharacter.pathToLocation(int3, int1, (int)gameCharacter.getZ());
							((IsoZombie)gameCharacter).AllowRepathDelay = 200.0F;
						}
					}
				}
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovementForDirection(false);
	}
}
