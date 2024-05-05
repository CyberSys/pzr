package zombie.characters;

import java.util.Iterator;
import zombie.ai.states.PathFindState;
import zombie.debug.DebugOptions;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;
import zombie.network.GameClient;
import zombie.vehicles.PathFindBehavior2;


public class MPDebugAI {
	private static final Vector2 tempo = new Vector2();
	private static final Vector2 tempo2 = new Vector2();

	public static IsoPlayer getNearestPlayer(IsoPlayer player) {
		IsoPlayer player2 = null;
		Iterator iterator = GameClient.IDToPlayerMap.values().iterator();
		while (true) {
			IsoPlayer player3;
			do {
				do {
					if (!iterator.hasNext()) {
						return player2;
					}

					player3 = (IsoPlayer)iterator.next();
				}		 while (player3 == player);
			}	 while (player2 != null && !(player2.getDistanceSq(player) > player3.getDistanceSq(player)));

			player2 = player3;
		}
	}

	public static boolean updateMovementFromInput(IsoPlayer player, IsoPlayer.MoveVars moveVars) {
		if (GameClient.bClient && player.isLocalPlayer() && (DebugOptions.instance.MultiplayerAttackPlayer.getValue() || DebugOptions.instance.MultiplayerFollowPlayer.getValue())) {
			IsoPlayer player2 = getNearestPlayer(player);
			if (player2 != null) {
				Vector2 vector2 = new Vector2(player2.x - player.x, player.y - player2.y);
				vector2.rotate(-0.7853982F);
				vector2.normalize();
				moveVars.moveX = vector2.x;
				moveVars.moveY = vector2.y;
				moveVars.NewFacing = IsoDirections.fromAngle(vector2);
				if (!player2.isTeleporting() && !(player2.getDistanceSq(player) > 10.0F)) {
					if (player2.getDistanceSq(player) > 5.0F) {
						player.setRunning(true);
						player.setSprinting(true);
					} else if (player2.getDistanceSq(player) > 2.5F) {
						player.setRunning(true);
					} else if (player2.getDistanceSq(player) < 1.25F) {
						moveVars.moveX = 0.0F;
						moveVars.moveY = 0.0F;
					}
				} else {
					player.removeFromSquare();
					player.setX(player2.realx);
					player.setY(player2.realy);
					player.setZ((float)player2.realz);
					player.setLx(player2.realx);
					player.setLy(player2.realy);
					player.setLz((float)player2.realz);
					player.ensureOnTile();
				}
			}

			PathFindBehavior2 pathFindBehavior2 = player.getPathFindBehavior2();
			if (moveVars.moveX == 0.0F && moveVars.moveY == 0.0F && player.getPath2() != null && pathFindBehavior2.isStrafing() && !pathFindBehavior2.bStopping) {
				Vector2 vector22 = tempo.set(pathFindBehavior2.getTargetX() - player.x, pathFindBehavior2.getTargetY() - player.y);
				Vector2 vector23 = tempo2.set(-1.0F, 0.0F);
				float float1 = 1.0F;
				float float2 = vector22.dot(vector23);
				float float3 = float2 / float1;
				vector23 = tempo2.set(0.0F, -1.0F);
				float2 = vector22.dot(vector23);
				float float4 = float2 / float1;
				tempo.set(float4, float3);
				tempo.normalize();
				tempo.rotate(0.7853982F);
				moveVars.moveX = tempo.x;
				moveVars.moveY = tempo.y;
			}

			if (moveVars.moveX != 0.0F || moveVars.moveY != 0.0F) {
				if (player.stateMachine.getCurrent() == PathFindState.instance()) {
					player.setDefaultState();
				}

				player.setJustMoved(true);
				player.setMoveDelta(1.0F);
				if (player.isStrafing()) {
					tempo.set(moveVars.moveX, moveVars.moveY);
					tempo.normalize();
					float float5 = player.legsSprite.modelSlot.model.AnimPlayer.getRenderedAngle();
					float5 = (float)((double)float5 + 0.7853981633974483);
					if ((double)float5 > 6.283185307179586) {
						float5 = (float)((double)float5 - 6.283185307179586);
					}

					if (float5 < 0.0F) {
						float5 = (float)((double)float5 + 6.283185307179586);
					}

					tempo.rotate(float5);
					moveVars.strafeX = tempo.x;
					moveVars.strafeY = tempo.y;
				} else {
					tempo.set(moveVars.moveX, -moveVars.moveY);
					tempo.normalize();
					tempo.rotate(-0.7853982F);
					player.setForwardDirection(tempo);
				}
			}

			return true;
		} else {
			return false;
		}
	}

	public static boolean updateInputState(IsoPlayer player, IsoPlayer.InputState inputState) {
		IsoPlayer player2;
		if (GameClient.bClient && player.isLocalPlayer() && DebugOptions.instance.MultiplayerAttackPlayer.getValue()) {
			player2 = getNearestPlayer(player);
			inputState.bMelee = false;
			inputState.isAttacking = false;
			inputState.isCharging = false;
			inputState.isAiming = false;
			inputState.bRunning = false;
			inputState.bSprinting = false;
			if (player2 != null) {
				inputState.isCharging = true;
				inputState.isAiming = false;
				if (player2.getDistanceSq(player) < 0.5F) {
					inputState.bMelee = true;
					inputState.isAttacking = true;
				}
			}

			return true;
		} else if (GameClient.bClient && player.isLocalPlayer() && DebugOptions.instance.MultiplayerFollowPlayer.getValue()) {
			player2 = getNearestPlayer(player);
			inputState.bMelee = false;
			inputState.isAttacking = false;
			inputState.isCharging = false;
			inputState.isAiming = false;
			inputState.bRunning = false;
			inputState.bSprinting = false;
			return true;
		} else {
			return false;
		}
	}
}
