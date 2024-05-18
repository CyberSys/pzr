package zombie.ai.astar;

import gnu.trove.set.hash.TIntHashSet;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.Display;
import zombie.GameWindow;
import zombie.PathfindManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.core.utils.ExpandableBooleanList;
import zombie.gameStates.IngameState;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirectionSet;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;


public class AStarPathFinder {
	public static int IDToUseInSort = 0;
	static Vector2 zfindangle = new Vector2();
	public boolean allowDiagMovement;
	public int cyclesPerSlice = 3;
	public int delay = 0;
	public int maxSearchDistance;
	public AStarPathFinder.PathFindProgress progress;
	public int startX;
	public int startY;
	public int startZ;
	public int targetX;
	public int targetY;
	public int targetZ;
	public static int NumPathfinds = 0;
	static AStarPathMap astarmap;
	Path foundPath;
	int maxDepth;
	Mover mover;
	public IsoGameCharacter character;
	private AStarHeuristic heuristic;
	private AStarPathFinder.SortedList open;
	private ExpandableBooleanList closed;
	public ArrayList IsoGridSquaresUnfurled;
	static int unfurledmax = 0;
	IsoDirectionSet set;
	private boolean bClosest;
	static Vector2 temp = new Vector2();

	private void PathfindIsoGridSquare(int int1, IsoGridSquare square, Mover mover, IsoGridSquare square2, int int2, int int3, int int4, int int5) {
		if (square2.searchData[int1] == null) {
			square2.searchData[int1] = new SearchData();
		}

		float float1 = square.searchData[int1].cost + astarmap.getMovementCost(mover, square.x, square.y, square.z, square2.x, square2.y, square2.z);
		if (float1 < square2.searchData[int1].cost) {
			if (this.inOpenList(square2)) {
				this.removeFromOpen(int1, square2);
			}

			if (this.inClosedList(square2)) {
				this.removeFromClosed(square2);
			}
		}

		if (!this.inOpenList(square2) && !this.inClosedList(square2)) {
			square2.searchData[int1].cost = float1;
			square2.searchData[int1].heuristic = this.getHeuristicCost(mover, square2.x, square2.y, square2.z, int2, int3, int4);
			this.maxDepth = Math.max(this.maxDepth, square2.setParent(int1, 0, square));
			this.addToOpen(int1, 0, square2);
			++int5;
		}
	}

	public void findPathActualZombie(Mover mover, int int1, int int2, int int3, int int4, int int5, int int6) {
		zfindangle.x = (float)int4 + 0.5F;
		zfindangle.y = (float)int5 + 0.5F;
		Vector2 vector2 = zfindangle;
		vector2.x -= ((IsoZombie)mover).getX();
		vector2 = zfindangle;
		vector2.y -= ((IsoZombie)mover).getY();
		zfindangle.normalize();
		IsoDirections directions = IsoDirections.fromAngle(zfindangle);
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5, int6);
		boolean boolean1 = true;
		while (square != square2) {
		}
	}

	private boolean IsInDirection(IsoGridSquare square, IsoDirections directions) {
		if (this.targetX == -1) {
			return false;
		} else if (square.z != this.targetZ) {
			return false;
		} else {
			switch (directions) {
			case N: 
				if (square.x == this.targetX && square.y > this.targetY) {
					return true;
				}

				break;
			
			case S: 
				if (square.x == this.targetX && square.y < this.targetY) {
					return true;
				}

				break;
			
			case NW: 
				if (this.targetX < square.x && this.targetY < square.y && this.targetX - square.x == this.targetY - square.y) {
					return true;
				}

				break;
			
			case NE: 
				if (this.targetX > square.x && this.targetY < square.y && -(this.targetX - square.x) == this.targetY - square.y) {
					return true;
				}

				break;
			
			case SW: 
				if (this.targetX < square.x && this.targetY > square.y && this.targetX - square.x == -(this.targetY - square.y)) {
					return true;
				}

				break;
			
			case SE: 
				if (this.targetX > square.x && this.targetY > square.y && -(this.targetX - square.x) == -(this.targetY - square.y)) {
					return true;
				}

				break;
			
			case E: 
				if (this.targetX > square.x && this.startY == this.targetY) {
					return true;
				}

				break;
			
			case W: 
				if (this.targetX < square.x && this.startY == this.targetY) {
					return true;
				}

			
			}

			return false;
		}
	}

	public AStarPathFinder(IsoGameCharacter gameCharacter, AStarPathMap aStarPathMap, int int1, boolean boolean1, AStarHeuristic aStarHeuristic) {
		this.progress = AStarPathFinder.PathFindProgress.notrunning;
		this.startX = 0;
		this.startY = 0;
		this.startZ = 0;
		this.targetX = 0;
		this.targetY = 0;
		this.targetZ = 0;
		this.maxDepth = 0;
		this.character = null;
		this.open = new AStarPathFinder.SortedList();
		this.closed = new ExpandableBooleanList(1);
		this.IsoGridSquaresUnfurled = new ArrayList(0);
		this.set = new IsoDirectionSet();
		this.character = gameCharacter;
		astarmap = aStarPathMap;
		this.heuristic = aStarHeuristic;
		this.maxSearchDistance = int1;
		this.allowDiagMovement = boolean1;
	}

	public AStarPathFinder.PathFindProgress Cycle(int int1) {
		if (astarmap == null) {
			astarmap = new AStarPathMap(IsoWorld.instance.CurrentCell);
			IsoWorld.instance.CurrentCell.setPathMap(astarmap);
		}

		++NumPathfinds;
		this.foundPath = this.findPath(int1, this.mover, this.startX, this.startY, this.startZ, this.targetX, this.targetY, this.targetZ);
		--NumPathfinds;
		if (this.foundPath != null && this.foundPath.getLength() > 0) {
			this.progress = AStarPathFinder.PathFindProgress.found;
		} else {
			this.progress = AStarPathFinder.PathFindProgress.failed;
		}

		return this.progress;
	}

	public AStarPathFinder.PathFindProgress Cycle(int int1, PathfindManager.PathfindJob pathfindJob) {
		if (astarmap == null) {
			astarmap = new AStarPathMap(IsoWorld.instance.CurrentCell);
			IsoWorld.instance.CurrentCell.setPathMap(astarmap);
		}

		++NumPathfinds;
		this.foundPath = this.findPath(int1, this.mover, this.startX, this.startY, this.startZ, this.targetX, this.targetY, this.targetZ, pathfindJob);
		--NumPathfinds;
		if (this.foundPath != null && this.foundPath.getLength() > 0) {
			this.progress = AStarPathFinder.PathFindProgress.found;
		} else {
			this.progress = AStarPathFinder.PathFindProgress.failed;
		}

		return this.progress;
	}

	public Path findPath(int int1, Mover mover, int int2, int int3, int int4, int int5, int int6, int int7) {
		this.maxSearchDistance = 600;
		this.IsoGridSquaresUnfurled.clear();
		this.targetX = int5;
		this.targetY = int6;
		this.targetZ = int7;
		byte byte1 = 0;
		if (astarmap == null) {
			astarmap = new AStarPathMap(IsoWorld.instance.CurrentCell);
			IsoWorld.instance.CurrentCell.setPathMap(astarmap);
		}

		if (int5 >= 0 && int6 >= 0 && int5 < IsoWorld.instance.CurrentCell.getWidthInTiles() && int6 < IsoWorld.instance.CurrentCell.getHeightInTiles()) {
			try {
				if (mover == IsoCamera.CamCharacter) {
					boolean boolean1 = true;
				}

				IsoGridSquare square = astarmap.getNode(int2, int3, int4);
				if (square == null) {
					return null;
				} else {
					IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(int5, int6, int7);
					if (square2 == null && !this.bClosest) {
						return null;
					} else if (!square2.isFree(false) && !this.bClosest) {
						return null;
					} else if (square2.getN() == null && square2.getS() == null && square2.getE() == null && square2.getW() == null && !this.bClosest) {
						return null;
					} else {
						square.searchData[int1].cost = 0.0F;
						square.searchData[int1].depth = 0;
						square.searchData[int1].parent = null;
						this.open.clear();
						this.closed.clear();
						this.open.add(int1, 0, square);
						IsoGridSquare square3 = astarmap.getNode(int5, int6, int7);
						if (square3.searchData[int1] == null) {
							square3.searchData[int1] = new SearchData();
						}

						square3.searchData[int1].parent = null;
						this.maxDepth = 0;
						Thread thread = Thread.currentThread();
						while (true) {
							int int8;
							int int9;
							if (this.maxDepth < this.maxSearchDistance && this.open.size() != 0) {
								if (PathfindManager.instance.threads[int1].SwitchingCells) {
									return null;
								}

								if (mover == IsoCamera.CamCharacter) {
									boolean boolean2 = false;
								}

								if (IngameState.DebugPathfinding && mover == IsoCamera.CamCharacter && !(thread instanceof PathfindManager.PathfindThread) || IngameState.AlwaysDebugPathfinding) {
									Core.getInstance().StartFrame();
								}

								IsoGridSquare square4 = this.getFirstInOpen();
								if (IngameState.AlwaysDebugPathfinding && !this.IsoGridSquaresUnfurled.contains(square4)) {
									this.IsoGridSquaresUnfurled.add(square4);
								}

								if (square4 != square3) {
									this.removeFromOpen(int1, square4);
									this.addToClosed(square4);
									for (int int10 = -1; int10 <= 1; ++int10) {
										for (int8 = -1; int8 <= 1; ++int8) {
											for (int9 = -1; int9 <= 1; ++int9) {
												if (int9 == -1 && int8 == 0 && int10 == 0) {
													boolean boolean3 = false;
												}

												if (astarmap.isValidLocation(mover, square4.x, square4.y, square4.z, square4.x + int8, square4.y + int9, square4.z + int10, square4.x, square4.y, square4.z)) {
													IsoGridSquare square5 = astarmap.getNode(square4.x + int8, square4.y + int9, square4.z + int10);
													if (square5 != null) {
														this.PathfindIsoGridSquare(int1, square4, mover, square5, int5, int6, int7, byte1);
													}
												}
											}
										}
									}

									if (IngameState.AlwaysDebugPathfinding) {
										Core.getInstance().EndFrame(0);
										Core.getInstance().StartFrameUI();
										this.renderOverhead();
										Core.getInstance().EndFrameUI();
										Display.update();
										Thread.sleep(30L);
									}

									continue;
								}
							}

							if (this.IsoGridSquaresUnfurled.size() > unfurledmax) {
								unfurledmax = this.IsoGridSquaresUnfurled.size();
							}

							if (square3.searchData[int1].parent == null) {
								return null;
							}

							Path path = new Path();
							IsoGridSquare square6 = square3;
							path.cost += square3.searchData[int1].cost;
							int8 = square3.x;
							int9 = square3.y;
							int int11;
							for (int11 = square3.z; square6 != square; square6 = square6.searchData[int1].parent) {
								int int12 = square6.x;
								int int13 = square6.y;
								int int14 = square6.z;
								if (int12 == int8 && int13 == int9 && int14 == int11) {
									path.prependStep(int8, int9, int11);
								}

								for (; int12 != int8 || int13 != int9 || int14 != int11; path.prependStep(int8, int9, int11)) {
									if (int12 > int8) {
										++int8;
									} else if (int12 < int8) {
										--int8;
									}

									if (int13 > int9) {
										++int9;
									} else if (int13 < int9) {
										--int9;
									}

									if (int14 > int11) {
										++int11;
									} else if (int14 < int11) {
										--int11;
									}
								}

								int8 = square6.x;
								int9 = square6.y;
								int11 = square6.z;
							}

							if (int2 == int8 && int3 == int9 && int4 == int11) {
								path.prependStep(int8, int9, int11);
							}

							for (; int8 != int2 || int9 != int3 || int11 != int4; path.prependStep(int8, int9, int11)) {
								if (int2 > int8) {
									++int8;
								} else if (int2 < int8) {
									--int8;
								}

								if (int3 > int9) {
									++int9;
								} else if (int3 < int9) {
									--int9;
								}

								if (int4 > int11) {
									++int11;
								} else if (int4 < int11) {
									--int11;
								}
							}

							if (IngameState.DebugPathfinding && mover == IsoCamera.CamCharacter && !(thread instanceof PathfindManager.PathfindThread) || IngameState.AlwaysDebugPathfinding) {
								Core.getInstance().StartFrame();
								this.renderOverhead();
								this.renderPath(path);
								Core.getInstance().EndFrame(0);
								Display.update();
								Thread.sleep(2000L);
								IngameState.DebugPathfinding = false;
							}

							return path;
						}
					}
				}
			} catch (Exception exception) {
				Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, (String)null, exception);
				return null;
			}
		} else {
			return null;
		}
	}

	public Path findPath(int int1, Mover mover, int int2, int int3, int int4, int int5, int int6, int int7, PathfindManager.PathfindJob pathfindJob) {
		int int8 = int2;
		int int9 = int3;
		int int10 = int5;
		int int11 = int6;
		temp.x = (float)(int5 - int2);
		temp.y = (float)(int6 - int3);
		temp.setLength((float)IsoWorld.instance.CurrentCell.ChunkMap[0].getWidthInTiles() / 2.5F);
		this.maxSearchDistance = 600;
		this.IsoGridSquaresUnfurled.clear();
		this.targetX = int5;
		this.targetY = int6;
		this.targetZ = int7;
		byte byte1 = 0;
		if (astarmap == null) {
			astarmap = new AStarPathMap(IsoWorld.instance.CurrentCell);
			IsoWorld.instance.CurrentCell.setPathMap(astarmap);
		}

		IsoGridSquare square = null;
		float float1 = 1.0E9F;
		try {
			if (mover == IsoCamera.CamCharacter) {
				boolean boolean1 = true;
			}

			IsoGridSquare square2 = astarmap.getNode(int8, int9, int4);
			if (square2 == null) {
				return null;
			} else {
				IsoGridSquare square3 = IsoWorld.instance.CurrentCell.getGridSquare(int5, int6, int7);
				if (square3 == null && !this.bClosest) {
					return null;
				} else if (square3 != null && !square3.isFree(false) && !this.bClosest) {
					return null;
				} else if (square3 != null && square3.getN() == null && square3.getS() == null && square3.getE() == null && square3.getW() == null && !this.bClosest) {
					return null;
				} else {
					if (square2.searchData == null) {
						square2.searchData = new SearchData[PathfindManager.instance.MaxThreads];
					}

					if (square2.searchData[int1] == null) {
						square2.searchData[int1] = new SearchData();
					}

					square2.searchData[int1].cost = 0.0F;
					square2.searchData[int1].depth = 0;
					square2.searchData[int1].parent = null;
					this.open.clear();
					this.closed.clear();
					this.open.add(int1, 0, square2);
					IsoGridSquare square4 = astarmap.getNode(int10, int11, int7);
					if (square4 == null) {
						return null;
					} else {
						if (square4.searchData[int1] == null) {
							square4.searchData[int1] = new SearchData();
						}

						square4.searchData[int1].parent = null;
						this.maxDepth = 0;
						Thread thread = Thread.currentThread();
						while (true) {
							int int12;
							int int13;
							if (this.maxDepth < this.maxSearchDistance && this.open.size() != 0) {
								if (PathfindManager.instance.threads[int1].SwitchingCells) {
									return null;
								}

								if (pathfindJob.finished) {
									return null;
								}

								if (mover == IsoCamera.CamCharacter) {
									boolean boolean2 = false;
								}

								if (IngameState.DebugPathfinding && mover == IsoCamera.CamCharacter && !(thread instanceof PathfindManager.PathfindThread) || IngameState.AlwaysDebugPathfinding) {
									Core.getInstance().StartFrame();
								}

								IsoGridSquare square5 = this.getFirstInOpen();
								if (IngameState.AlwaysDebugPathfinding && !this.IsoGridSquaresUnfurled.contains(square5)) {
									this.IsoGridSquaresUnfurled.add(square5);
								}

								if (square5 != square4) {
									this.removeFromOpen(int1, square5);
									this.addToClosed(square5);
									for (int int14 = -1; int14 <= 1; ++int14) {
										for (int12 = -1; int12 <= 1; ++int12) {
											for (int13 = -1; int13 <= 1; ++int13) {
												if (int13 == -1 && int12 == 0 && int14 == 0) {
													boolean boolean3 = false;
												}

												if (astarmap.isValidLocation(mover, square5.x, square5.y, square5.z, square5.x + int12, square5.y + int13, square5.z + int14, square5.x, square5.y, square5.z)) {
													IsoGridSquare square6 = astarmap.getNode(square5.x + int12, square5.y + int13, square5.z + int14);
													if (square6 != null) {
														if (this.bClosest) {
															float float2 = IsoUtils.DistanceManhatten((float)square4.x, (float)square4.y, (float)square5.x, (float)square5.y, 0.0F, 0.0F);
															if (float2 < float1 && square5 != null) {
																float1 = float2;
																square = square5;
															}
														}

														this.PathfindIsoGridSquare(int1, square5, mover, square6, int5, int6, int7, byte1);
													}
												}
											}
										}
									}

									if (IngameState.AlwaysDebugPathfinding) {
										Core.getInstance().EndFrame(0);
										Core.getInstance().StartFrameUI();
										this.renderOverhead();
										Core.getInstance().EndFrameUI();
										Display.update();
										Thread.sleep(30L);
									}

									continue;
								}
							}

							if (this.IsoGridSquaresUnfurled.size() > unfurledmax) {
								unfurledmax = this.IsoGridSquaresUnfurled.size();
							}

							if (square4.searchData[int1].parent == null) {
								if (this.bClosest && square != null) {
									return this.findPath(int1, mover, int2, int3, int4, square.x, square.y, square.z);
								}

								return null;
							}

							Path path = new Path();
							IsoGridSquare square7 = square4;
							path.cost += square4.searchData[int1].cost;
							int12 = square4.x;
							int13 = square4.y;
							int int15 = square4.z;
							for (long long1 = System.currentTimeMillis(); square7 != square2; square7 = square7.searchData[int1].parent) {
								int int16 = square7.x;
								int int17 = square7.y;
								int int18 = square7.z;
								if (int16 == int12 && int17 == int13 && int18 == int15) {
									path.prependStep(int12, int13, int15);
								}

								if (System.currentTimeMillis() - long1 > 500L) {
									throw new RuntimeException("AStarPathFinder infinite loop?");
								}

								for (; int16 != int12 || int17 != int13 || int18 != int15; path.prependStep(int12, int13, int15)) {
									if (int16 > int12) {
										++int12;
									} else if (int16 < int12) {
										--int12;
									}

									if (int17 > int13) {
										++int13;
									} else if (int17 < int13) {
										--int13;
									}

									if (int18 > int15) {
										++int15;
									} else if (int18 < int15) {
										--int15;
									}
								}

								int12 = square7.x;
								int13 = square7.y;
								int15 = square7.z;
							}

							if (int2 == int12 && int3 == int13 && int4 == int15) {
								path.prependStep(int12, int13, int15);
							}

							for (; int12 != int2 || int13 != int3 || int15 != int4; path.prependStep(int12, int13, int15)) {
								if (int2 > int12) {
									++int12;
								} else if (int2 < int12) {
									--int12;
								}

								if (int3 > int13) {
									++int13;
								} else if (int3 < int13) {
									--int13;
								}

								if (int4 > int15) {
									++int15;
								} else if (int4 < int15) {
									--int15;
								}
							}

							if (IngameState.DebugPathfinding && mover == IsoCamera.CamCharacter && !(thread instanceof PathfindManager.PathfindThread) || IngameState.AlwaysDebugPathfinding) {
								Core.getInstance().StartFrame();
								this.renderOverhead();
								this.renderPath(path);
								Core.getInstance().EndFrame(0);
								Display.update();
								Thread.sleep(2000L);
								IngameState.DebugPathfinding = false;
							}

							return path;
						}
					}
				}
			}
		} catch (Exception exception) {
			Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, (String)null, exception);
			return null;
		}
	}

	private void renderPath(Path path) {
		TextureID.UseFiltering = true;
		Texture.getSharedTexture("media/ui/white.png");
		IsoCell cell = IsoWorld.instance.CurrentCell;
		Texture texture = Texture.getSharedTexture("media/ui/white.png");
		boolean boolean1 = false;
		byte byte1 = 2;
		int int1 = Core.getInstance().getOffscreenWidth(0) - cell.getWidthInTiles() * byte1;
		int int2 = Core.getInstance().getOffscreenHeight(0) - cell.getHeightInTiles() * byte1;
		for (int int3 = 0; int3 < path.getLength() - 1; ++int3) {
			int int4 = path.getX(int3);
			int int5 = path.getY(int3);
			int int6 = path.getX(int3 + 1);
			for (int int7 = path.getY(int3 + 1); int4 != int6 || int5 != int7; texture.render(int1 + int4 * byte1, int2 + int5 * byte1, byte1, byte1, 0.0F, 0.0F, 1.0F, 1.0F)) {
				if (int4 < int6) {
					++int4;
				}

				if (int4 > int6) {
					--int4;
				}

				if (int5 < int7) {
					++int5;
				}

				if (int5 > int7) {
					--int5;
				}
			}
		}
	}

	private void renderOverhead() {
		TextureID.UseFiltering = true;
		Texture.getSharedTexture("media/ui/white.png");
		IsoCell cell = IsoWorld.instance.CurrentCell;
		Texture texture = Texture.getSharedTexture("media/ui/white.png");
		byte byte1 = 0;
		byte byte2 = 2;
		int int1 = Core.getInstance().getOffscreenWidth(0) - cell.getWidthInTiles() * byte2;
		int int2 = Core.getInstance().getOffscreenHeight(0) - cell.getHeightInTiles() * byte2;
		texture.render(int1, int2, byte2 * cell.getWidthInTiles(), byte2 * cell.getHeightInTiles(), 0.7F, 0.7F, 0.7F, 1.0F);
		int int3;
		for (int3 = 0; int3 < cell.getWidthInTiles(); ++int3) {
			for (int int4 = 0; int4 < cell.getHeightInTiles(); ++int4) {
				IsoGridSquare square = cell.getGridSquare(int3, int4, byte1);
				if (square != null) {
					if (!square.getProperties().Is(IsoFlagType.solid) && !square.getProperties().Is(IsoFlagType.solidtrans)) {
						if (!square.getProperties().Is(IsoFlagType.exterior)) {
							texture.render(int1 + int3 * byte2, int2 + int4 * byte2, byte2, byte2, 0.8F, 0.8F, 0.8F, 1.0F);
						}
					} else {
						texture.render(int1 + int3 * byte2, int2 + int4 * byte2, byte2, byte2, 0.5F, 0.5F, 0.5F, 255.0F);
					}

					if (square.getProperties().Is(IsoFlagType.collideN)) {
						texture.render(int1 + int3 * byte2, int2 + int4 * byte2, byte2, 1, 0.2F, 0.2F, 0.2F, 1.0F);
					}

					if (square.getProperties().Is(IsoFlagType.collideW)) {
						texture.render(int1 + int3 * byte2, int2 + int4 * byte2, 1, byte2, 0.2F, 0.2F, 0.2F, 1.0F);
					}
				}
			}
		}

		for (int3 = 0; int3 < this.IsoGridSquaresUnfurled.size(); ++int3) {
			IsoGridSquare square2 = (IsoGridSquare)this.IsoGridSquaresUnfurled.get(int3);
			texture.render(int1 + square2.x * byte2, int2 + square2.y * byte2, byte2, byte2, 1.0F, 0.0F, 0.0F, 1.0F);
		}

		texture.render(int1 + this.startX * byte2, int2 + this.startY * byte2, byte2, byte2, 0.0F, 1.0F, 0.0F, 1.0F);
		texture.render(int1 + this.targetX * byte2, int2 + this.targetY * byte2, byte2, byte2, 1.0F, 1.0F, 0.0F, 1.0F);
		TextureID.UseFiltering = false;
	}

	public AStarPathFinder.PathFindProgress findPathActual(Mover mover, int int1, int int2, int int3, int int4, int int5, int int6) {
		if (astarmap == null) {
			astarmap = new AStarPathMap(IsoWorld.instance.CurrentCell);
			IsoWorld.instance.CurrentCell.setPathMap(astarmap);
		}

		return this.findPathActual(mover, int1, int2, int3, int4, int5, int6, false);
	}

	public AStarPathFinder.PathFindProgress findPathActual(Mover mover, int int1, int int2, int int3, int int4, int int5, int int6, boolean boolean1) {
		if (astarmap == null) {
			astarmap = new AStarPathMap(IsoWorld.instance.CurrentCell);
			IsoWorld.instance.CurrentCell.setPathMap(astarmap);
		}

		this.foundPath = null;
		this.targetX = int4;
		this.targetY = int5;
		this.targetZ = int6;
		this.startX = int1;
		this.startY = int2;
		this.startZ = int3;
		this.mover = mover;
		this.bClosest = boolean1;
		this.progress = AStarPathFinder.PathFindProgress.notyetfound;
		return AStarPathFinder.PathFindProgress.notyetfound;
	}

	public AStarPathFinder.PathFindProgress findPathSlice(int int1, IPathfinder iPathfinder, Mover mover, int int2, int int3, int int4, int int5, int int6, int int7) {
		if (!IngameState.DebugPathfinding && !IngameState.AlwaysDebugPathfinding) {
			PathfindManager.instance.AddJob(iPathfinder, mover, int2, int3, int4, int5, int6, int7);
			this.progress = AStarPathFinder.PathFindProgress.notyetfound;
			return AStarPathFinder.PathFindProgress.notyetfound;
		} else {
			this.startX = int2;
			this.startY = int3;
			this.startZ = int4;
			this.targetX = int5;
			this.targetY = int6;
			this.targetZ = int7;
			this.mover = mover;
			this.maxSearchDistance = 1000;
			this.Cycle(int1);
			if (this.progress == AStarPathFinder.PathFindProgress.found) {
				iPathfinder.Succeeded(this.getPath(), mover);
			} else {
				iPathfinder.Failed(mover);
			}

			return this.progress;
		}
	}

	public int getFreeIndex(IsoGameCharacter gameCharacter) {
		if (astarmap == null) {
			astarmap = new AStarPathMap(IsoWorld.instance.CurrentCell);
			IsoWorld.instance.CurrentCell.setPathMap(astarmap);
		}

		return 0;
	}

	public float getHeuristicCost(Mover mover, int int1, int int2, int int3, int int4, int int5, int int6) {
		return this.heuristic.getCost(astarmap.map, mover, int1, int2, int3, int4, int5, int6);
	}

	public Path getPath() {
		return this.foundPath;
	}

	protected void addToClosed(IsoGridSquare square) {
		this.closed.setValue(square.ID, true);
	}

	protected void addToOpen(int int1, int int2, IsoGridSquare square) {
		this.open.add(int1, int2, square);
	}

	protected IsoGridSquare getFirstInOpen() {
		return this.open.first();
	}

	protected boolean inClosedList(IsoGridSquare square) {
		return this.closed.getValue(square.ID);
	}

	protected boolean inOpenList(IsoGridSquare square) {
		return this.open.contains(square);
	}

	protected void removeFromClosed(IsoGridSquare square) {
		this.closed.setValue(square.ID, false);
	}

	protected void removeFromOpen(int int1, IsoGridSquare square) {
		this.open.remove(int1, square);
	}

	private class SortedList {
		private PriorityQueue list;
		TIntHashSet set;

		private SortedList() {
			this.list = new PriorityQueue(10000);
			this.set = new TIntHashSet(10000);
		}

		public void add(int int1, int int2, IsoGridSquare square) {
			this.list.add(square);
			this.set.add(square.ID);
			AStarPathFinder.IDToUseInSort = int2;
		}

		public void clear() {
			this.list.clear();
			this.set.clear();
		}

		public boolean contains(IsoGridSquare square) {
			return this.set.contains(square.ID);
		}

		public IsoGridSquare first() {
			return (IsoGridSquare)this.list.peek();
		}

		public void remove(int int1, IsoGridSquare square) {
			this.list.remove(square);
			this.set.remove(square.ID);
		}

		public int size() {
			return this.list.size();
		}

		SortedList(Object object) {
			this();
		}
	}
	public static enum PathFindProgress {

		notrunning,
		failed,
		found,
		notyetfound;
	}
}
