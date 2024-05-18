package zombie.behaviors.general;

import zombie.GameTime;
import zombie.PathfindManager;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.AStarPathFinderResult;
import zombie.ai.astar.IPathfinder;
import zombie.ai.astar.Mover;
import zombie.ai.astar.Path;
import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoLivingCharacter;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.debug.DebugLog;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCamera;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoPushableObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.ui.TextManager;
import zombie.ui.UIFont;


public class PathFindBehavior extends Behavior implements IPathfinder {
	public Path path;
	public boolean bDoClosest = false;
	public int pathIndex = 0;
	public int sx;
	public int sy;
	public int sz;
	public int tx;
	public int ty;
	public int tz;
	public int osx;
	public int osy;
	public int osz;
	public int otx;
	public int oty;
	public int otz;
	boolean useScriptXY = false;
	public AStarPathFinderResult finder = new AStarPathFinderResult();
	public String name = "unnamed";
	public int lastCancel = 1000000;
	IsoGameCharacter chr = null;
	static Vector2 tempo = new Vector2(0.0F, 0.0F);

	public String getName() {
		return this.name;
	}

	public PathFindBehavior() {
	}

	public PathFindBehavior(String string) {
		this.name = string;
	}

	public void setData(IsoGameCharacter gameCharacter, int int1, int int2, int int3) {
		this.sx = this.osx = (int)gameCharacter.getX();
		this.sy = this.osy = (int)gameCharacter.getY();
		this.sz = this.osz = (int)gameCharacter.getZ();
		this.tx = this.otx = int1;
		this.ty = this.oty = int2;
		this.tz = this.otz = int3;
	}

	public PathFindBehavior(boolean boolean1) {
		this.useScriptXY = boolean1;
	}

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		if (gameCharacter.getCurrentSquare() == null) {
			return Behavior.BehaviorResult.Failed;
		} else {
			this.chr = gameCharacter;
			this.finder.maxSearchDistance = 800;
			if (gameCharacter instanceof IsoSurvivor && this.lastCancel > 120 && ((IsoSurvivor)gameCharacter).dangerTile - ((IsoSurvivor)gameCharacter).lastDangerTile > 30 && ((IsoSurvivor)gameCharacter).dangerTile > 0) {
				this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
				this.path = null;
				this.sx = (int)gameCharacter.getX();
				this.sy = (int)gameCharacter.getY();
				this.sz = (int)gameCharacter.getZ();
				this.lastCancel = 0;
				return Behavior.BehaviorResult.Working;
			} else {
				++this.lastCancel;
				if (this.tx == 0 && this.ty == 0 && this.tz == 0) {
					this.reset();
					this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
					return Behavior.BehaviorResult.Failed;
				} else if (this.sx == this.tx && this.sy == this.ty && this.sz == this.tz) {
					this.reset();
					this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
					return Behavior.BehaviorResult.Succeeded;
				} else {
					boolean boolean1;
					boolean boolean2;
					int int1;
					int int2;
					int int3;
					int int4;
					if (this.tx != this.otx || this.ty != this.oty || this.tz != this.otz || this.sx != this.osx || this.sy != this.osy || this.sz != this.osz) {
						if (this.tx == this.otx && this.ty == this.oty && this.tz == this.otz && this.path != null) {
							boolean1 = false;
							boolean2 = false;
							for (int4 = this.pathIndex - 1; int4 < this.path.getLength(); ++int4) {
								if (int4 < 0) {
									int4 = 0;
								}

								int1 = this.path.getX(int4);
								int2 = this.path.getY(int4);
								int3 = this.path.getZ(int4);
								if ((int)gameCharacter.getX() == this.sx && (int)gameCharacter.getY() == this.sy && (int)gameCharacter.getZ() == this.sz) {
									boolean1 = true;
									break;
								}

								if (int1 == (int)gameCharacter.getX() && int2 == (int)gameCharacter.getY() && int3 == (int)gameCharacter.getZ()) {
									boolean1 = true;
									break;
								}

								if (Math.abs(int1 - (int)gameCharacter.getX()) <= 1 && Math.abs(int2 - (int)gameCharacter.getY()) <= 1 && Math.abs(int3 - (int)gameCharacter.getZ()) <= 1) {
									boolean1 = true;
									break;
								}
							}

							if (boolean1) {
								this.pathIndex = int4;
							} else {
								this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
								this.path = null;
							}
						} else {
							this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
							this.path = null;
						}
					}

					if ((int)gameCharacter.getX() == this.tx && (int)gameCharacter.getY() == this.ty && (int)gameCharacter.getZ() == this.tz && Math.abs(gameCharacter.getX() - (float)this.tx - 0.5F) < 0.2F && Math.abs(gameCharacter.getY() - (float)this.ty - 0.5F) < 0.2F) {
						if (gameCharacter == IsoCamera.CamCharacter) {
							boolean1 = false;
						}

						this.reset();
						this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
						return Behavior.BehaviorResult.Succeeded;
					} else {
						if (this.path == null) {
							if (this.finder.progress == AStarPathFinder.PathFindProgress.notyetfound) {
								return Behavior.BehaviorResult.Working;
							}

							if (this.finder.progress == AStarPathFinder.PathFindProgress.failed) {
								if (gameCharacter == IsoCamera.CamCharacter) {
									boolean1 = false;
								}

								this.reset();
								this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
								return Behavior.BehaviorResult.Failed;
							}

							if (this.finder.progress == AStarPathFinder.PathFindProgress.notrunning) {
								if (this.sx != (int)gameCharacter.getX()) {
									this.sx = (int)gameCharacter.getX();
								}

								if (this.sy != (int)gameCharacter.getY()) {
									this.sy = (int)gameCharacter.getY();
								}

								if (this.sz != (int)gameCharacter.getZ()) {
									this.sz = (int)gameCharacter.getZ();
								}

								PathfindManager.instance.AddJob(this, gameCharacter, this.sx, this.sy, this.sz, this.tx, this.ty, this.tz, this.bDoClosest);
								this.osx = this.sx;
								this.osy = this.sy;
								this.osz = this.sz;
								this.otx = this.tx;
								this.oty = this.ty;
								this.otz = this.tz;
								this.finder.progress = AStarPathFinder.PathFindProgress.notyetfound;
								return Behavior.BehaviorResult.Working;
							}
						}

						if (this.path == null) {
							if (gameCharacter == IsoCamera.CamCharacter) {
								boolean1 = false;
							}

							this.reset();
							this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
							return Behavior.BehaviorResult.Failed;
						} else {
							int int5 = this.pathIndex + 6;
							if (int5 > this.path.getLength()) {
								int5 = this.path.getLength();
							}

							int int6;
							int int7;
							for (int4 = this.pathIndex + 1; int4 < int5; ++int4) {
								int1 = this.path.getX(int4);
								int2 = this.path.getY(int4);
								int3 = this.path.getZ(int4);
								IsoGridSquare square = gameCharacter.getCell().getGridSquare(int1, int2, int3);
								if (gameCharacter.getCurrentSquare() == null) {
									return Behavior.BehaviorResult.Failed;
								}

								if (square == null) {
									return Behavior.BehaviorResult.Failed;
								}

								if (gameCharacter.getCurrentSquare().getMovingObjects().size() < square.getMovingObjects().size()) {
									int6 = 0;
									if (!square.getMovingObjects().isEmpty()) {
										for (int7 = 0; int7 < square.getMovingObjects().size(); ++int7) {
											if (this.lastCancel > 120 && square.getMovingObjects().get(int7) instanceof IsoZombie) {
												++int6;
											}
										}
									}

									if (int6 > 3) {
										this.path = null;
										this.sx = (int)gameCharacter.getX();
										this.sy = (int)gameCharacter.getY();
										this.sz = (int)gameCharacter.getZ();
										this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
										this.lastCancel = 0;
										return Behavior.BehaviorResult.Working;
									}
								}
							}

							boolean2 = false;
							int int8;
							for (int1 = 0; int1 < this.path.getLength(); ++int1) {
								int2 = this.path.getX(int1);
								int3 = this.path.getY(int1);
								int8 = this.path.getZ(int1);
								if ((int)gameCharacter.getX() == this.sx && (int)gameCharacter.getY() == this.sy && (int)gameCharacter.getZ() == this.sz) {
									boolean2 = true;
									break;
								}

								if (int2 == (int)gameCharacter.getX() && int3 == (int)gameCharacter.getY() && int8 == (int)gameCharacter.getZ()) {
									boolean2 = true;
									break;
								}

								if (Math.abs(int2 - (int)gameCharacter.getX()) <= 1 && Math.abs(int3 - (int)gameCharacter.getY()) <= 1 && Math.abs(int8 - (int)gameCharacter.getZ()) <= 1) {
									boolean2 = true;
									break;
								}
							}

							boolean boolean3;
							if (!boolean2) {
								if (gameCharacter == IsoCamera.CamCharacter) {
									boolean3 = false;
								}

								this.reset();
								this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
								this.path = null;
								return Behavior.BehaviorResult.Failed;
							} else {
								gameCharacter.setPath(this.path);
								int int9;
								float float1;
								float float2;
								float float3;
								float float4;
								if (((IsoLivingCharacter)gameCharacter).isCollidedWithPushableThisFrame()) {
									IsoPushableObject pushableObject = ((IsoSurvivor)gameCharacter).collidePushable;
									tempo.x = gameCharacter.getMovementLastFrame().x;
									tempo.y = gameCharacter.getMovementLastFrame().y;
									tempo.normalize();
									tempo.rotate((float)Math.toRadians(-90.0));
									float1 = pushableObject.x + tempo.x * 5.0F;
									float2 = pushableObject.y + tempo.y * 5.0F;
									float3 = pushableObject.x - tempo.x * 5.0F;
									float4 = pushableObject.y - tempo.y * 5.0F;
									int7 = LosUtil.lineClearCollideCount(this.chr, IsoWorld.instance.CurrentCell, (int)pushableObject.x, (int)pushableObject.y, (int)pushableObject.z, (int)float1, (int)float2, (int)pushableObject.z);
									int9 = LosUtil.lineClearCollideCount(this.chr, IsoWorld.instance.CurrentCell, (int)pushableObject.x, (int)pushableObject.y, (int)pushableObject.z, (int)float3, (int)float4, (int)pushableObject.z);
									if (int7 > int9) {
										pushableObject.setImpulsex(pushableObject.getImpulsex() + tempo.x * 0.1F);
										pushableObject.setImpulsey(pushableObject.getImpulsey() + tempo.y * 0.1F);
									} else if (int7 < int9) {
										pushableObject.setImpulsex(pushableObject.getImpulsex() - tempo.x * 0.1F);
										pushableObject.setImpulsey(pushableObject.getImpulsey() - tempo.y * 0.1F);
									}
								}

								if (gameCharacter.isCollidedThisFrame()) {
									if (gameCharacter == IsoCamera.CamCharacter) {
										boolean3 = false;
									}

									if (gameCharacter.isCollidedE() || gameCharacter.isCollidedN() || gameCharacter.isCollidedS() || gameCharacter.isCollidedW()) {
										boolean3 = false;
										int2 = this.path.getX(this.pathIndex);
										int3 = this.path.getY(this.pathIndex);
										int8 = int2 - (int)gameCharacter.getX();
										int6 = int3 - (int)gameCharacter.getY();
										if (int8 > 0 && gameCharacter.isCollidedE()) {
											boolean3 = true;
										} else if (int8 < 0 && gameCharacter.isCollidedW()) {
											boolean3 = true;
										}

										if (int6 > 0 && gameCharacter.isCollidedS()) {
											boolean3 = true;
										} else if (int6 < 0 && gameCharacter.isCollidedN()) {
											boolean3 = true;
										}

										if (boolean3) {
											if (gameCharacter == IsoCamera.CamCharacter) {
												boolean boolean4 = false;
											}

											this.reset();
											this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
											return Behavior.BehaviorResult.Failed;
										}
									}
								}

								if (this.pathIndex >= this.path.getLength()) {
									return Behavior.BehaviorResult.Succeeded;
								} else {
									Vector2 vector2 = new Vector2((float)this.tx, (float)this.ty);
									float1 = (float)this.path.getX(this.pathIndex);
									float2 = (float)this.path.getY(this.pathIndex);
									float3 = 1.0F;
									float4 = gameCharacter.getPathSpeed();
									int7 = this.path.getX(this.pathIndex);
									int9 = this.path.getY(this.pathIndex);
									int int10 = this.path.getZ(this.pathIndex);
									float float5;
									float float6;
									if ((int)gameCharacter.getX() == int7 && (int)gameCharacter.getY() == int9 && (int)gameCharacter.getZ() == int10) {
										float5 = (float)this.path.getX(this.pathIndex) + 0.5F;
										float6 = (float)this.path.getY(this.pathIndex) + 0.5F;
										vector2.x = float5;
										vector2.y = float6;
										boolean boolean5 = true;
										if (IsoUtils.DistanceManhatten(gameCharacter.getX(), gameCharacter.getY(), float5, float6) < float4 * GameTime.instance.getMultiplier() * 2.0F) {
											++this.pathIndex;
											if (this.pathIndex >= this.path.getLength()) {
												this.path = null;
												this.reset();
												this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
												return Behavior.BehaviorResult.Succeeded;
											}

											if (GameTime.instance.getMultiplier() >= 10.0F && this.path.getZ(this.pathIndex) == (int)gameCharacter.getZ()) {
												gameCharacter.FaceNextPathNode(this.path.getX(this.pathIndex), this.path.getY(this.pathIndex));
												boolean5 = false;
												IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(this.path.getX(this.pathIndex), this.path.getY(this.pathIndex), (int)gameCharacter.getZ());
												if (square2 != null && gameCharacter.getCurrentSquare() != null) {
													IsoObject object = gameCharacter.getCurrentSquare().getDoorTo(square2);
													boolean boolean6 = false;
													if (object instanceof IsoThumpable) {
														IsoThumpable thumpable = (IsoThumpable)object;
														if (!thumpable.IsOpen() && (thumpable.isBarricaded() || thumpable.isLocked())) {
															boolean6 = true;
														}
													} else if (object instanceof IsoDoor) {
														IsoDoor door = (IsoDoor)object;
														if (!door.IsOpen() && (door.isBarricaded() || door.isLocked())) {
															boolean6 = true;
														}
													}

													if (boolean6) {
														this.reset();
														return Behavior.BehaviorResult.Failed;
													}
												}

												gameCharacter.setX((float)this.path.getX(this.pathIndex) + 0.5F);
												gameCharacter.setY((float)this.path.getY(this.pathIndex) + 0.5F);
											} else {
												gameCharacter.setX((float)this.path.getX(this.pathIndex - 1) + 0.5F);
												gameCharacter.setY((float)this.path.getY(this.pathIndex - 1) + 0.5F);
											}

											int int11 = this.path.getX(this.pathIndex);
											int int12 = this.path.getY(this.pathIndex);
											vector2.x = (float)int11;
											vector2.y = (float)int12;
											IsoGridSquare square3 = IsoWorld.instance.CurrentCell.getGridSquare(int11, int12, (int)gameCharacter.getZ());
											if (square3 != null) {
												IsoObject object2 = gameCharacter.getCurrentSquare().getDoorTo(square3);
												if (object2 != null) {
													if (object2 instanceof IsoThumpable && !((IsoThumpable)object2).open) {
														((IsoThumpable)object2).ToggleDoor(gameCharacter);
													} else if (object2 instanceof IsoDoor && !((IsoDoor)object2).open) {
														((IsoDoor)object2).ToggleDoor(gameCharacter);
													}
												}

												IsoWindow window = gameCharacter.getCurrentSquare().getWindowTo(square3);
												if (window != null) {
													if (window.Locked) {
														window.WeaponHit(gameCharacter, (HandWeapon)null);
													} else {
														window.ToggleWindow(gameCharacter);
													}
												}
											}
										}

										IsoDirections directions = gameCharacter.dir;
										if (boolean5) {
											gameCharacter.FaceNextPathNode(this.path.getX(this.pathIndex), this.path.getY(this.pathIndex));
										}

										if (IsoCamera.CamCharacter == gameCharacter && IsoDirections.reverse(directions) == gameCharacter.dir) {
											boolean boolean7 = false;
										}

										vector2.x -= gameCharacter.getX();
										vector2.y -= gameCharacter.getY();
										if (vector2.getLength() > 0.0F) {
											vector2.normalize();
										}

										if (GameTime.instance.getMultiplier() < 10.0F) {
											gameCharacter.def.Finished = false;
											gameCharacter.MoveForward(float4, vector2.x, vector2.y, float3);
										}
									} else {
										gameCharacter.FaceNextPathNode(this.path.getX(this.pathIndex), this.path.getY(this.pathIndex));
										float5 = (float)this.path.getX(this.pathIndex) + 0.5F;
										float6 = (float)this.path.getY(this.pathIndex) + 0.5F;
										vector2.x = float5;
										vector2.y = float6;
										vector2.x -= gameCharacter.getX();
										vector2.y -= gameCharacter.getY();
										if (vector2.getLength() > 0.0F) {
											vector2.normalize();
										}

										gameCharacter.def.Finished = false;
										gameCharacter.MoveForward(float4, vector2.x, vector2.y, float3);
									}

									if (this.useScriptXY) {
										gameCharacter.setScriptnx(gameCharacter.getNx());
										gameCharacter.setScriptny(gameCharacter.getNy());
									}

									return Behavior.BehaviorResult.Working;
								}
							}
						}
					}
				}
			}
		}
	}

	public void reset() {
		this.path = null;
		this.sx = 0;
		this.sy = 0;
		this.sz = 0;
		this.tx = 0;
		this.ty = 0;
		this.tz = 0;
		this.osx = 0;
		this.osy = 0;
		this.osz = 0;
		this.otx = 0;
		this.oty = 0;
		this.otz = 0;
		if (this.finder != null) {
			this.finder.progress = AStarPathFinder.PathFindProgress.notrunning;
		}

		this.lastCancel = 1000000000;
	}

	public boolean running(IsoGameCharacter gameCharacter) {
		if (this.finder == null) {
			return false;
		} else {
			return this.finder.progress != AStarPathFinder.PathFindProgress.notrunning;
		}
	}

	public boolean valid() {
		return true;
	}

	public int renderDebug(int int1) {
		short short1 = 300;
		byte byte1 = 50;
		TextManager.instance.DrawString(UIFont.Small, (double)short1, (double)byte1, "Pathfind", 1.0, 1.0, 1.0, 1.0);
		int int2 = byte1 + 30;
		if (this.path == null) {
			return int1;
		} else {
			int int3 = -1000;
			int int4 = 0;
			int int5 = 0;
			for (int int6 = this.pathIndex; int6 < this.path.getLength(); ++int6) {
				Integer integer = this.path.getX(int6);
				Integer integer2 = this.path.getY(int6);
				Integer integer3 = this.path.getZ(int6);
				if (int3 != -1000) {
					integer = integer - int3;
					integer2 = integer2 - int4;
					integer3 = integer3 - int5;
				}

				int3 = integer;
				int4 = integer2;
				int5 = integer3;
				TextManager.instance.DrawString(UIFont.Small, (double)short1, (double)int2, "PathNode " + int6 + " - x: " + integer + " y: " + integer2 + " z: " + integer3, 0.0, 1.0, 1.0, 0.4000000059604645);
				int2 += 30;
			}

			return int1;
		}
	}

	public void Failed(Mover mover) {
		this.finder.progress = AStarPathFinder.PathFindProgress.failed;
		DebugLog.log("Pathfind failed");
		this.reset();
	}

	public void Succeeded(Path path, Mover mover) {
		Path path2 = this.path;
		if (path2 != null) {
			for (int int1 = 0; int1 < path2.getLength(); ++int1) {
				Path.stepstore.push(path2.getStep(int1));
			}
		}

		this.path = path;
		this.lastCancel = 0;
		this.pathIndex = 0;
		this.finder.progress = AStarPathFinder.PathFindProgress.found;
	}
}
