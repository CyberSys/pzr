package zombie.ai.states;

import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.core.Rand;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoDirections;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoWindow;


public class OpenWindowState extends State {
	static OpenWindowState _instance = new OpenWindowState();

	public static OpenWindowState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		byte byte1 = 0;
		String string = null;
		byte byte2 = 0;
		boolean boolean1 = false;
		boolean boolean2 = false;
		if (gameCharacter.StateMachineParams.size() > 1) {
			string = (String)gameCharacter.StateMachineParams.get(1);
		}

		if (string == null) {
			string = "WindowOpenIn";
		}

		IsoWindow window = (IsoWindow)gameCharacter.StateMachineParams.get(0);
		if ("WindowSmash".equals(string)) {
			byte1 = 1;
		} else if (window.Locked && gameCharacter.getCurrentSquare().Is(IsoFlagType.exterior) || window.PermaLocked) {
			byte1 = 1;
		}

		if ("WindowSmash".equals(string) && gameCharacter.getPrimaryHandItem() instanceof HandWeapon) {
			HandWeapon handWeapon = (HandWeapon)gameCharacter.getPrimaryHandItem();
			if (handWeapon.getSwingAnim() != null && !handWeapon.isRanged() && gameCharacter.legsSprite.CurrentAnim.name.equals("Attack_" + handWeapon.getSwingAnim())) {
				gameCharacter.def.Finished = false;
				gameCharacter.def.Frame = 0.0F;
			}
		}

		gameCharacter.StateMachineParams.put(1, string);
		gameCharacter.StateMachineParams.put(2, Integer.valueOf(byte1));
		gameCharacter.StateMachineParams.put(3, boolean2);
		gameCharacter.StateMachineParams.put(4, boolean1);
		gameCharacter.StateMachineParams.put(5, Integer.valueOf(byte2));
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (!IsoPlayer.instance.pressedMovement() && !IsoPlayer.instance.pressedCancelAction() && gameCharacter.StateMachineParams.size() >= 6) {
			IsoWindow window = (IsoWindow)gameCharacter.StateMachineParams.get(0);
			String string = (String)gameCharacter.StateMachineParams.get(1);
			if (window == null) {
				gameCharacter.getStateMachine().changeState(gameCharacter.getDefaultState());
			} else {
				if (string == null) {
					this.enter(gameCharacter);
				}

				int int1 = (Integer)gameCharacter.StateMachineParams.get(2);
				boolean boolean1 = (Boolean)gameCharacter.StateMachineParams.get(3);
				boolean boolean2 = (Boolean)gameCharacter.StateMachineParams.get(4);
				int int2 = (Integer)gameCharacter.StateMachineParams.get(5);
				if (IsoPlayer.instance.ContextPanic > 5.0F) {
					boolean2 = true;
					boolean1 = true;
					IsoPlayer.instance.ContextPanic = 0.0F;
					string = "WindowSmash";
					int2 = 0;
					int1 = 1;
				}

				IsoPlayer player = (IsoPlayer)gameCharacter;
				player.setCollidable(true);
				player.updateLOS();
				if (gameCharacter.getPrimaryHandItem() instanceof HandWeapon && ((HandWeapon)gameCharacter.getPrimaryHandItem()).getSwingAnim() != null && "WindowSmash".equals(string) && !((HandWeapon)gameCharacter.getPrimaryHandItem()).isRanged()) {
					gameCharacter.PlayAnimUnlooped("Attack_" + ((HandWeapon)gameCharacter.getPrimaryHandItem()).getSwingAnim());
				} else {
					gameCharacter.PlayAnimUnlooped(string);
				}

				if (gameCharacter.sprite != null) {
					gameCharacter.sprite.Animate = true;
				}

				if (gameCharacter.sprite != null && gameCharacter.sprite.CurrentAnim != null && "WindowOpenStruggle".equals(gameCharacter.sprite.CurrentAnim.name)) {
					gameCharacter.sprite.CurrentAnim.FinishUnloopedOnFrame = 0;
				}

				if (string == "WindowOpenIn") {
					gameCharacter.getSpriteDef().AnimFrameIncrease = 0.23F;
				} else {
					gameCharacter.getSpriteDef().AnimFrameIncrease = 0.18F;
				}

				if (string == "WindowOpenSuccess" && (int)gameCharacter.getSpriteDef().Frame == 3 && !window.open) {
					IsoPlayer.instance.ContextPanic = 0.0F;
					window.ToggleWindow(gameCharacter);
				}

				if (string == "WindowSmash" && (int)gameCharacter.getSpriteDef().Frame == 5 && window.Health > 0 && int1 > 0) {
					IsoPlayer.instance.ContextPanic = 0.0F;
					window.WeaponHit(gameCharacter, (HandWeapon)null);
					int1 = -1;
					IsoPlayer.instance.ContextPanic = 0.0F;
					if (!(gameCharacter.getPrimaryHandItem() instanceof HandWeapon) && !(gameCharacter.getSecondaryHandItem() instanceof HandWeapon)) {
						gameCharacter.getBodyDamage().setScratchedWindow();
					}
				}

				if (window.north) {
					if ((float)window.getSquare().getY() < gameCharacter.getY()) {
						gameCharacter.setDir(IsoDirections.N);
					} else {
						gameCharacter.setDir(IsoDirections.S);
					}
				} else if ((float)window.getSquare().getX() < gameCharacter.getX()) {
					gameCharacter.setDir(IsoDirections.W);
				} else {
					gameCharacter.setDir(IsoDirections.E);
				}

				if (gameCharacter.getSpriteDef().Finished) {
					if (!"WindowSmash".equals(string)) {
						if (window.PermaLocked) {
							if ("WindowOpenStruggle".equals(string)) {
								gameCharacter.StateMachineParams.clear();
								gameCharacter.getStateMachine().changeState(gameCharacter.getDefaultState());
								return;
							}

							gameCharacter.getEmitter().playSound("WindowIsLocked", window);
						} else if (window.Locked && gameCharacter.getCurrentSquare().Is(IsoFlagType.exterior)) {
							int1 = 1;
							if (Rand.Next(100) < 10) {
								gameCharacter.getEmitter().playSound("BreakLockOnWindow", window);
								window.setPermaLocked(true);
								window.syncIsoObject(false, (byte)0, (UdpConnection)null, (ByteBuffer)null);
								gameCharacter.StateMachineParams.clear();
								gameCharacter.getStateMachine().changeState(gameCharacter.getDefaultState());
							} else if (gameCharacter.getPerkLevel(PerkFactory.Perks.Strength) > 7 && Rand.Next(100) < 20) {
								int1 = 0;
							} else if (gameCharacter.getPerkLevel(PerkFactory.Perks.Strength) > 5 && Rand.Next(100) < 10) {
								int1 = 0;
							} else if (gameCharacter.getPerkLevel(PerkFactory.Perks.Strength) > 3 && Rand.Next(100) < 6) {
								int1 = 0;
							} else if (gameCharacter.getPerkLevel(PerkFactory.Perks.Strength) > 1 && Rand.Next(100) < 4) {
								int1 = 0;
							} else if (Rand.Next(100) <= 1) {
								int1 = 0;
							} else {
								gameCharacter.getEmitter().playSound("WindowIsLocked", window);
							}
						}
					}

					if (boolean2 && int1 > -1) {
						IsoPlayer.instance.ContextPanic = 0.0F;
						string = "WindowSmash";
					} else if (int1 > 0) {
						if (string == "WindowOpenStruggle") {
							gameCharacter.getSpriteDef().Finished = false;
						}

						string = "WindowOpenStruggle";
					} else if (int1 == 0) {
						IsoPlayer.instance.ContextPanic = 0.0F;
						string = "WindowOpenSuccess";
						gameCharacter.getEmitter().playSound("OpenWindow");
					} else if (boolean1) {
						gameCharacter.getStateMachine().changeState(ClimbThroughWindowState.instance());
					} else {
						gameCharacter.getStateMachine().changeState(gameCharacter.getDefaultState());
						gameCharacter.StateMachineParams.clear();
					}
				}

				if ((float)int2 > gameCharacter.getSpriteDef().Frame) {
					--int1;
					float float1 = GameTime.getInstance().getMultiplier() / 1.6F;
					switch (gameCharacter.getPerkLevel(PerkFactory.Perks.Fitness)) {
					case 1: 
						gameCharacter.exert(0.01F * float1);
						break;
					
					case 2: 
						gameCharacter.exert(0.009F * float1);
						break;
					
					case 3: 
						gameCharacter.exert(0.008F * float1);
						break;
					
					case 4: 
						gameCharacter.exert(0.007F * float1);
						break;
					
					case 5: 
						gameCharacter.exert(0.006F * float1);
						break;
					
					case 6: 
						gameCharacter.exert(0.005F * float1);
						break;
					
					case 7: 
						gameCharacter.exert(0.004F * float1);
						break;
					
					case 8: 
						gameCharacter.exert(0.003F * float1);
						break;
					
					case 9: 
						gameCharacter.exert(0.0025F * float1);
						break;
					
					case 10: 
						gameCharacter.exert(0.002F * float1);
					
					}
				}

				if (gameCharacter.getCurrentState() == this) {
					int2 = (int)gameCharacter.getSpriteDef().Frame;
					gameCharacter.StateMachineParams.put(1, string);
					gameCharacter.StateMachineParams.put(2, int1);
					gameCharacter.StateMachineParams.put(3, boolean1);
					gameCharacter.StateMachineParams.put(4, boolean2);
					gameCharacter.StateMachineParams.put(5, int2);
				}
			}
		} else {
			gameCharacter.getStateMachine().changeState(gameCharacter.getDefaultState());
		}
	}
}
