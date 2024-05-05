package zombie.iso;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameServer;


public final class WorldMarkers {
	private static final float CIRCLE_TEXTURE_SCALE = 1.5F;
	public static final WorldMarkers instance = new WorldMarkers();
	private static int NextGridSquareMarkerID = 0;
	private static int NextHomingPointID = 0;
	private final List gridSquareMarkers = new ArrayList();
	private final WorldMarkers.PlayerHomingPointList[] homingPoints = new WorldMarkers.PlayerHomingPointList[4];
	private final WorldMarkers.DirectionArrowList[] directionArrows = new WorldMarkers.DirectionArrowList[4];
	private static final ColorInfo stCol = new ColorInfo();
	private final WorldMarkers.PlayerScreen playerScreen = new WorldMarkers.PlayerScreen();
	private WorldMarkers.Point intersectPoint = new WorldMarkers.Point(0.0F, 0.0F);
	private WorldMarkers.Point arrowStart = new WorldMarkers.Point(0.0F, 0.0F);
	private WorldMarkers.Point arrowEnd = new WorldMarkers.Point(0.0F, 0.0F);
	private WorldMarkers.Line arrowLine;

	private WorldMarkers() {
		this.arrowLine = new WorldMarkers.Line(this.arrowStart, this.arrowEnd);
	}

	public void init() {
		if (!GameServer.bServer) {
			int int1;
			for (int1 = 0; int1 < this.homingPoints.length; ++int1) {
				this.homingPoints[int1] = new WorldMarkers.PlayerHomingPointList();
			}

			for (int1 = 0; int1 < this.directionArrows.length; ++int1) {
				this.directionArrows[int1] = new WorldMarkers.DirectionArrowList();
			}
		}
	}

	public void reset() {
		int int1;
		for (int1 = 0; int1 < this.homingPoints.length; ++int1) {
			this.homingPoints[int1].clear();
		}

		for (int1 = 0; int1 < this.directionArrows.length; ++int1) {
			this.directionArrows[int1].clear();
		}

		this.gridSquareMarkers.clear();
	}

	private int GetDistance(int int1, int int2, int int3, int int4) {
		return (int)Math.sqrt(Math.pow((double)(int1 - int3), 2.0) + Math.pow((double)(int2 - int4), 2.0));
	}

	private float getAngle(int int1, int int2, int int3, int int4) {
		float float1 = (float)Math.toDegrees(Math.atan2((double)(int4 - int2), (double)(int3 - int1)));
		if (float1 < 0.0F) {
			float1 += 360.0F;
		}

		return float1;
	}

	private float angleDegrees(float float1) {
		if (float1 < 0.0F) {
			float1 += 360.0F;
		}

		if (float1 > 360.0F) {
			float1 -= 360.0F;
		}

		return float1;
	}

	public WorldMarkers.PlayerHomingPoint getHomingPoint(int int1) {
		for (int int2 = 0; int2 < this.homingPoints.length; ++int2) {
			for (int int3 = this.homingPoints[int2].size() - 1; int3 >= 0; ++int3) {
				if (((WorldMarkers.PlayerHomingPoint)this.homingPoints[int2].get(int3)).ID == int1) {
					return (WorldMarkers.PlayerHomingPoint)this.homingPoints[int2].get(int3);
				}
			}
		}

		return null;
	}

	public WorldMarkers.PlayerHomingPoint addPlayerHomingPoint(IsoPlayer player, int int1, int int2) {
		return this.addPlayerHomingPoint(player, int1, int2, "arrow_triangle", 1.0F, 1.0F, 1.0F, 1.0F, true, 20);
	}

	public WorldMarkers.PlayerHomingPoint addPlayerHomingPoint(IsoPlayer player, int int1, int int2, float float1, float float2, float float3, float float4) {
		return this.addPlayerHomingPoint(player, int1, int2, "arrow_triangle", float1, float2, float3, float4, true, 20);
	}

	public WorldMarkers.PlayerHomingPoint addPlayerHomingPoint(IsoPlayer player, int int1, int int2, String string, float float1, float float2, float float3, float float4, boolean boolean1, int int3) {
		if (GameServer.bServer) {
			return null;
		} else {
			WorldMarkers.PlayerHomingPoint playerHomingPoint = new WorldMarkers.PlayerHomingPoint(player.PlayerIndex);
			playerHomingPoint.setActive(true);
			playerHomingPoint.setTexture(string);
			playerHomingPoint.setX(int1);
			playerHomingPoint.setY(int2);
			playerHomingPoint.setR(float1);
			playerHomingPoint.setG(float2);
			playerHomingPoint.setB(float3);
			playerHomingPoint.setA(float4);
			playerHomingPoint.setHomeOnTargetInView(boolean1);
			playerHomingPoint.setHomeOnTargetDist(int3);
			this.homingPoints[player.PlayerIndex].add(playerHomingPoint);
			return playerHomingPoint;
		}
	}

	public boolean removeHomingPoint(WorldMarkers.PlayerHomingPoint playerHomingPoint) {
		return this.removeHomingPoint(playerHomingPoint.getID());
	}

	public boolean removeHomingPoint(int int1) {
		for (int int2 = 0; int2 < this.homingPoints.length; ++int2) {
			for (int int3 = this.homingPoints[int2].size() - 1; int3 >= 0; --int3) {
				if (((WorldMarkers.PlayerHomingPoint)this.homingPoints[int2].get(int3)).ID == int1) {
					((WorldMarkers.PlayerHomingPoint)this.homingPoints[int2].get(int3)).remove();
					this.homingPoints[int2].remove(int3);
					return true;
				}
			}
		}

		return false;
	}

	public boolean removePlayerHomingPoint(IsoPlayer player, WorldMarkers.PlayerHomingPoint playerHomingPoint) {
		return this.removePlayerHomingPoint(player, playerHomingPoint.getID());
	}

	public boolean removePlayerHomingPoint(IsoPlayer player, int int1) {
		for (int int2 = this.homingPoints[player.PlayerIndex].size() - 1; int2 >= 0; --int2) {
			if (((WorldMarkers.PlayerHomingPoint)this.homingPoints[player.PlayerIndex].get(int2)).ID == int1) {
				((WorldMarkers.PlayerHomingPoint)this.homingPoints[player.PlayerIndex].get(int2)).remove();
				this.homingPoints[player.PlayerIndex].remove(int2);
				return true;
			}
		}

		return false;
	}

	public void removeAllHomingPoints(IsoPlayer player) {
		this.homingPoints[player.PlayerIndex].clear();
	}

	public WorldMarkers.DirectionArrow getDirectionArrow(int int1) {
		for (int int2 = 0; int2 < this.directionArrows.length; ++int2) {
			for (int int3 = this.directionArrows[int2].size() - 1; int3 >= 0; --int3) {
				if (((WorldMarkers.DirectionArrow)this.directionArrows[int2].get(int3)).ID == int1) {
					return (WorldMarkers.DirectionArrow)this.directionArrows[int2].get(int3);
				}
			}
		}

		return null;
	}

	public WorldMarkers.DirectionArrow addDirectionArrow(IsoPlayer player, int int1, int int2, int int3, String string, float float1, float float2, float float3, float float4) {
		if (GameServer.bServer) {
			return null;
		} else {
			WorldMarkers.DirectionArrow directionArrow = new WorldMarkers.DirectionArrow(player.PlayerIndex);
			directionArrow.setActive(true);
			directionArrow.setTexture(string);
			directionArrow.setTexDown("dir_arrow_down");
			directionArrow.setTexStairsUp("dir_arrow_stairs_up");
			directionArrow.setTexStairsDown("dir_arrow_stairs_down");
			directionArrow.setX(int1);
			directionArrow.setY(int2);
			directionArrow.setZ(int3);
			directionArrow.setR(float1);
			directionArrow.setG(float2);
			directionArrow.setB(float3);
			directionArrow.setA(float4);
			this.directionArrows[player.PlayerIndex].add(directionArrow);
			return directionArrow;
		}
	}

	public boolean removeDirectionArrow(WorldMarkers.DirectionArrow directionArrow) {
		return this.removeDirectionArrow(directionArrow.getID());
	}

	public boolean removeDirectionArrow(int int1) {
		for (int int2 = 0; int2 < this.directionArrows.length; ++int2) {
			for (int int3 = this.directionArrows[int2].size() - 1; int3 >= 0; --int3) {
				if (((WorldMarkers.DirectionArrow)this.directionArrows[int2].get(int3)).ID == int1) {
					((WorldMarkers.DirectionArrow)this.directionArrows[int2].get(int3)).remove();
					this.directionArrows[int2].remove(int3);
					return true;
				}
			}
		}

		return false;
	}

	public boolean removePlayerDirectionArrow(IsoPlayer player, WorldMarkers.DirectionArrow directionArrow) {
		return this.removePlayerDirectionArrow(player, directionArrow.getID());
	}

	public boolean removePlayerDirectionArrow(IsoPlayer player, int int1) {
		for (int int2 = this.directionArrows[player.PlayerIndex].size() - 1; int2 >= 0; --int2) {
			if (((WorldMarkers.DirectionArrow)this.directionArrows[player.PlayerIndex].get(int2)).ID == int1) {
				((WorldMarkers.DirectionArrow)this.directionArrows[player.PlayerIndex].get(int2)).remove();
				this.directionArrows[player.PlayerIndex].remove(int2);
				return true;
			}
		}

		return false;
	}

	public void removeAllDirectionArrows(IsoPlayer player) {
		this.directionArrows[player.PlayerIndex].clear();
	}

	public void update() {
		if (!GameServer.bServer) {
			this.updateGridSquareMarkers();
			this.updateHomingPoints();
			this.updateDirectionArrows();
		}
	}

	private void updateDirectionArrows() {
		int int1 = IsoCamera.frameState.playerIndex;
		for (int int2 = 0; int2 < this.directionArrows.length; ++int2) {
			if (int2 == int1 && this.directionArrows[int2].size() != 0) {
				int int3;
				for (int3 = this.directionArrows[int2].size() - 1; int3 >= 0; --int3) {
					if (((WorldMarkers.DirectionArrow)this.directionArrows[int2].get(int3)).isRemoved()) {
						this.directionArrows[int2].remove(int3);
					}
				}

				this.playerScreen.update(int2);
				for (int3 = 0; int3 < this.directionArrows[int2].size(); ++int3) {
					WorldMarkers.DirectionArrow directionArrow = (WorldMarkers.DirectionArrow)this.directionArrows[int2].get(int3);
					if (directionArrow.active && IsoPlayer.players[int2] != null) {
						IsoPlayer player = IsoPlayer.players[int2];
						if (player.getSquare() != null) {
							PlayerCamera playerCamera = IsoCamera.cameras[int2];
							float float1 = Core.getInstance().getZoom(int2);
							int int4 = player.getSquare().getX();
							int int5 = player.getSquare().getY();
							int int6 = player.getSquare().getZ();
							int int7 = this.GetDistance(int4, int5, directionArrow.x, directionArrow.y);
							boolean boolean1 = false;
							boolean boolean2 = false;
							float float2 = 0.0F;
							float float3 = 0.0F;
							if (int7 < 300) {
								boolean1 = true;
								float2 = playerCamera.XToScreenExact((float)directionArrow.x, (float)directionArrow.y, (float)int6, 0) / float1;
								float3 = playerCamera.YToScreenExact((float)directionArrow.x, (float)directionArrow.y, (float)int6, 0) / float1;
								if (this.playerScreen.isWithinInner(float2, float3)) {
									boolean2 = true;
								}
							}

							if (boolean2) {
								directionArrow.renderWithAngle = false;
								directionArrow.isDrawOnWorld = false;
								directionArrow.renderSizeMod = 1.0F;
								if (float1 > 1.0F) {
									directionArrow.renderSizeMod /= float1;
								}

								directionArrow.renderScreenX = float2;
								directionArrow.renderScreenY = float3;
								if (int6 == directionArrow.z) {
									directionArrow.renderTexture = directionArrow.texDown != null ? directionArrow.texDown : directionArrow.texture;
								} else if (directionArrow.z > int6) {
									directionArrow.renderTexture = directionArrow.texStairsUp != null ? directionArrow.texStairsUp : directionArrow.texture;
								} else {
									directionArrow.renderTexture = directionArrow.texStairsDown != null ? directionArrow.texStairsUp : directionArrow.texture;
								}

								directionArrow.lastWasWithinView = true;
							} else {
								directionArrow.renderWithAngle = true;
								directionArrow.isDrawOnWorld = false;
								directionArrow.renderTexture = directionArrow.texture;
								directionArrow.renderSizeMod = 1.0F;
								float float4 = this.playerScreen.centerX;
								float float5 = this.playerScreen.centerY;
								float float6 = 0.0F;
								if (!boolean1) {
									float6 = this.getAngle(directionArrow.x, directionArrow.y, int4, int5);
									float6 = this.angleDegrees(180.0F - float6);
									float6 = this.angleDegrees(float6 + 45.0F);
								} else {
									float6 = this.getAngle((int)float4, (int)float5, (int)float2, (int)float3);
									float6 = this.angleDegrees(180.0F - float6);
									float6 = this.angleDegrees(float6 - 90.0F);
								}

								if (float6 != directionArrow.angle) {
									if (!directionArrow.lastWasWithinView) {
										directionArrow.angle = PZMath.lerpAngle(PZMath.degToRad(directionArrow.angle), PZMath.degToRad(float6), directionArrow.angleLerpVal * GameTime.instance.getMultiplier());
										directionArrow.angle = PZMath.radToDeg(directionArrow.angle);
									} else {
										directionArrow.angle = float6;
									}
								}

								float float7 = float4 + 32000.0F * (float)Math.sin(Math.toRadians((double)directionArrow.angle));
								float float8 = float5 + 32000.0F * (float)Math.cos(Math.toRadians((double)directionArrow.angle));
								directionArrow.renderScreenX = float4;
								directionArrow.renderScreenY = float5;
								this.arrowStart.set(float4, float5);
								this.arrowEnd.set(float7, float8);
								WorldMarkers.Line[] lineArray = this.playerScreen.getBorders();
								for (int int8 = 0; int8 < lineArray.length; ++int8) {
									this.intersectPoint.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
									if (intersectLineSegments(this.arrowLine, lineArray[int8], this.intersectPoint)) {
										directionArrow.renderScreenX = this.intersectPoint.x;
										directionArrow.renderScreenY = this.intersectPoint.y;
										break;
									}
								}

								directionArrow.lastWasWithinView = false;
							}
						}
					}
				}
			}
		}
	}

	private void updateHomingPoints() {
		int int1 = IsoCamera.frameState.playerIndex;
		for (int int2 = 0; int2 < this.homingPoints.length; ++int2) {
			if (int2 == int1 && this.homingPoints[int2].size() != 0) {
				int int3;
				for (int3 = this.homingPoints[int2].size() - 1; int3 >= 0; --int3) {
					if (((WorldMarkers.PlayerHomingPoint)this.homingPoints[int2].get(int3)).isRemoved) {
						this.homingPoints[int2].remove(int3);
					}
				}

				this.playerScreen.update(int2);
				for (int3 = 0; int3 < this.homingPoints[int2].size(); ++int3) {
					WorldMarkers.PlayerHomingPoint playerHomingPoint = (WorldMarkers.PlayerHomingPoint)this.homingPoints[int2].get(int3);
					if (playerHomingPoint.active && IsoPlayer.players[int2] != null) {
						IsoPlayer player = IsoPlayer.players[int2];
						if (player.getSquare() != null) {
							PlayerCamera playerCamera = IsoCamera.cameras[int2];
							float float1 = Core.getInstance().getZoom(int2);
							playerHomingPoint.renderSizeMod = 1.0F;
							if (float1 > 1.0F) {
								playerHomingPoint.renderSizeMod /= float1;
							}

							int int4 = player.getSquare().getX();
							int int5 = player.getSquare().getY();
							playerHomingPoint.dist = this.GetDistance(int4, int5, playerHomingPoint.x, playerHomingPoint.y);
							playerHomingPoint.targetOnScreen = false;
							if ((float)playerHomingPoint.dist < 200.0F) {
								playerHomingPoint.targetScreenX = playerCamera.XToScreenExact((float)playerHomingPoint.x, (float)playerHomingPoint.y, 0.0F, 0) / float1;
								playerHomingPoint.targetScreenY = playerCamera.YToScreenExact((float)playerHomingPoint.x, (float)playerHomingPoint.y, 0.0F, 0) / float1;
								playerHomingPoint.targetScreenX += playerHomingPoint.homeOnOffsetX / float1;
								playerHomingPoint.targetScreenY += playerHomingPoint.homeOnOffsetY / float1;
								playerHomingPoint.targetOnScreen = this.playerScreen.isOnScreen(playerHomingPoint.targetScreenX, playerHomingPoint.targetScreenY);
							}

							float float2 = this.playerScreen.centerX;
							float float3 = float2 + playerHomingPoint.renderOffsetX / float1;
							float float4 = this.playerScreen.centerY;
							float float5 = float4 + playerHomingPoint.renderOffsetY / float1;
							float float6;
							if (!playerHomingPoint.customTargetAngle) {
								float6 = 0.0F;
								if (!playerHomingPoint.targetOnScreen) {
									float6 = this.getAngle(playerHomingPoint.x, playerHomingPoint.y, int4, int5);
									float6 = this.angleDegrees(180.0F - float6);
									float6 = this.angleDegrees(float6 + 45.0F);
								} else {
									float6 = this.getAngle((int)float3, (int)float5, (int)playerHomingPoint.targetScreenX, (int)playerHomingPoint.targetScreenY);
									float6 = this.angleDegrees(180.0F - float6);
									float6 = this.angleDegrees(float6 - 90.0F);
								}

								playerHomingPoint.targetAngle = float6;
							}

							if (playerHomingPoint.targetAngle != playerHomingPoint.angle) {
								playerHomingPoint.angle = PZMath.lerpAngle(PZMath.degToRad(playerHomingPoint.angle), PZMath.degToRad(playerHomingPoint.targetAngle), playerHomingPoint.angleLerpVal * GameTime.instance.getMultiplier());
								playerHomingPoint.angle = PZMath.radToDeg(playerHomingPoint.angle);
							}

							float6 = playerHomingPoint.stickToCharDist / float1;
							playerHomingPoint.targRenderX = float3 + float6 * (float)Math.sin(Math.toRadians((double)playerHomingPoint.angle));
							playerHomingPoint.targRenderY = float5 + float6 * (float)Math.cos(Math.toRadians((double)playerHomingPoint.angle));
							float float7 = playerHomingPoint.movementLerpVal;
							if (playerHomingPoint.targetOnScreen) {
								float float8 = (float)this.GetDistance((int)playerHomingPoint.targRenderX, (int)playerHomingPoint.targRenderY, (int)playerHomingPoint.targetScreenX, (int)playerHomingPoint.targetScreenY);
								float float9 = (float)this.GetDistance((int)float3, (int)float5, (int)playerHomingPoint.targetScreenX, (int)playerHomingPoint.targetScreenY);
								if (float9 < float8 || playerHomingPoint.homeOnTargetInView && playerHomingPoint.dist <= playerHomingPoint.homeOnTargetDist) {
									float9 *= 0.75F;
									playerHomingPoint.targRenderX = float3 + float9 * (float)Math.sin(Math.toRadians((double)playerHomingPoint.targetAngle));
									playerHomingPoint.targRenderY = float5 + float9 * (float)Math.cos(Math.toRadians((double)playerHomingPoint.targetAngle));
								}
							}

							playerHomingPoint.targRenderX = this.playerScreen.clampToInnerX(playerHomingPoint.targRenderX);
							playerHomingPoint.targRenderY = this.playerScreen.clampToInnerY(playerHomingPoint.targRenderY);
							if (playerHomingPoint.targRenderX != playerHomingPoint.renderX) {
								playerHomingPoint.renderX = PZMath.lerp(playerHomingPoint.renderX, playerHomingPoint.targRenderX, float7 * GameTime.instance.getMultiplier());
							}

							if (playerHomingPoint.targRenderY != playerHomingPoint.renderY) {
								playerHomingPoint.renderY = PZMath.lerp(playerHomingPoint.renderY, playerHomingPoint.targRenderY, float7 * GameTime.instance.getMultiplier());
							}
						}
					}
				}
			}
		}
	}

	private void updateGridSquareMarkers() {
		if (IsoCamera.frameState.playerIndex == 0) {
			if (this.gridSquareMarkers.size() != 0) {
				int int1;
				for (int1 = this.gridSquareMarkers.size() - 1; int1 >= 0; --int1) {
					if (((WorldMarkers.GridSquareMarker)this.gridSquareMarkers.get(int1)).isRemoved()) {
						this.gridSquareMarkers.remove(int1);
					}
				}

				for (int1 = 0; int1 < this.gridSquareMarkers.size(); ++int1) {
					WorldMarkers.GridSquareMarker gridSquareMarker = (WorldMarkers.GridSquareMarker)this.gridSquareMarkers.get(int1);
					if (gridSquareMarker.alphaInc) {
						gridSquareMarker.alpha += GameTime.getInstance().getMultiplier() * gridSquareMarker.fadeSpeed;
						if (gridSquareMarker.alpha > gridSquareMarker.alphaMax) {
							gridSquareMarker.alphaInc = false;
							gridSquareMarker.alpha = gridSquareMarker.alphaMax;
						}
					} else {
						gridSquareMarker.alpha -= GameTime.getInstance().getMultiplier() * gridSquareMarker.fadeSpeed;
						if (gridSquareMarker.alpha < gridSquareMarker.alphaMin) {
							gridSquareMarker.alphaInc = true;
							gridSquareMarker.alpha = 0.3F;
						}
					}
				}
			}
		}
	}

	public boolean removeGridSquareMarker(WorldMarkers.GridSquareMarker gridSquareMarker) {
		return this.removeGridSquareMarker(gridSquareMarker.getID());
	}

	public boolean removeGridSquareMarker(int int1) {
		for (int int2 = this.gridSquareMarkers.size() - 1; int2 >= 0; --int2) {
			if (((WorldMarkers.GridSquareMarker)this.gridSquareMarkers.get(int2)).getID() == int1) {
				((WorldMarkers.GridSquareMarker)this.gridSquareMarkers.get(int2)).remove();
				this.gridSquareMarkers.remove(int2);
				return true;
			}
		}

		return false;
	}

	public WorldMarkers.GridSquareMarker getGridSquareMarker(int int1) {
		for (int int2 = 0; int2 < this.gridSquareMarkers.size(); ++int2) {
			if (((WorldMarkers.GridSquareMarker)this.gridSquareMarkers.get(int2)).getID() == int1) {
				return (WorldMarkers.GridSquareMarker)this.gridSquareMarkers.get(int2);
			}
		}

		return null;
	}

	public WorldMarkers.GridSquareMarker addGridSquareMarker(IsoGridSquare square, float float1, float float2, float float3, boolean boolean1, float float4) {
		return this.addGridSquareMarker("circle_center", "circle_only_highlight", square, float1, float2, float3, boolean1, float4, 0.006F, 0.3F, 1.0F);
	}

	public WorldMarkers.GridSquareMarker addGridSquareMarker(String string, String string2, IsoGridSquare square, float float1, float float2, float float3, boolean boolean1, float float4) {
		return this.addGridSquareMarker(string, string2, square, float1, float2, float3, boolean1, float4, 0.006F, 0.3F, 1.0F);
	}

	public WorldMarkers.GridSquareMarker addGridSquareMarker(String string, String string2, IsoGridSquare square, float float1, float float2, float float3, boolean boolean1, float float4, float float5, float float6, float float7) {
		if (GameServer.bServer) {
			return null;
		} else {
			WorldMarkers.GridSquareMarker gridSquareMarker = new WorldMarkers.GridSquareMarker();
			gridSquareMarker.init(string, string2, square.x, square.y, square.z, float4);
			gridSquareMarker.setR(float1);
			gridSquareMarker.setG(float2);
			gridSquareMarker.setB(float3);
			gridSquareMarker.setA(1.0F);
			gridSquareMarker.setDoAlpha(boolean1);
			gridSquareMarker.setFadeSpeed(float5);
			gridSquareMarker.setAlpha(0.0F);
			gridSquareMarker.setAlphaMin(float6);
			gridSquareMarker.setAlphaMax(float7);
			this.gridSquareMarkers.add(gridSquareMarker);
			return gridSquareMarker;
		}
	}

	public void renderGridSquareMarkers(IsoCell.PerPlayerRender perPlayerRender, int int1, int int2) {
		if (!GameServer.bServer && this.gridSquareMarkers.size() != 0) {
			IsoPlayer player = IsoPlayer.players[int2];
			if (player != null) {
				for (int int3 = 0; int3 < this.gridSquareMarkers.size(); ++int3) {
					WorldMarkers.GridSquareMarker gridSquareMarker = (WorldMarkers.GridSquareMarker)this.gridSquareMarkers.get(int3);
					if (gridSquareMarker.z == (float)int1 && gridSquareMarker.z == player.getZ() && gridSquareMarker.active) {
						float float1 = 0.0F;
						float float2 = 0.0F;
						stCol.set(gridSquareMarker.r, gridSquareMarker.g, gridSquareMarker.b, gridSquareMarker.a);
						if (gridSquareMarker.doBlink) {
							gridSquareMarker.sprite.alpha = Core.blinkAlpha;
						} else {
							gridSquareMarker.sprite.alpha = gridSquareMarker.doAlpha ? gridSquareMarker.alpha : 1.0F;
						}

						gridSquareMarker.sprite.render((IsoObject)null, gridSquareMarker.x, gridSquareMarker.y, gridSquareMarker.z, IsoDirections.N, float1, float2, stCol);
						if (gridSquareMarker.spriteOverlay != null) {
							gridSquareMarker.spriteOverlay.alpha = 1.0F;
							gridSquareMarker.spriteOverlay.render((IsoObject)null, gridSquareMarker.x, gridSquareMarker.y, gridSquareMarker.z, IsoDirections.N, float1, float2, stCol);
						}
					}
				}
			}
		}
	}

	public void debugRender() {
	}

	public void render() {
		this.update();
		this.renderHomingPoint();
		this.renderDirectionArrow(false);
	}

	public void renderHomingPoint() {
		if (!GameServer.bServer) {
			int int1 = IsoCamera.frameState.playerIndex;
			for (int int2 = 0; int2 < this.homingPoints.length; ++int2) {
				if (int2 == int1 && this.homingPoints[int2].size() != 0) {
					for (int int3 = 0; int3 < this.homingPoints[int2].size(); ++int3) {
						WorldMarkers.PlayerHomingPoint playerHomingPoint = (WorldMarkers.PlayerHomingPoint)this.homingPoints[int2].get(int3);
						if (playerHomingPoint.active && playerHomingPoint.texture != null) {
							float float1 = 180.0F - playerHomingPoint.angle;
							if (float1 < 0.0F) {
								float1 += 360.0F;
							}

							float float2 = playerHomingPoint.a;
							if (ClimateManager.getInstance().getFogIntensity() > 0.0F && float2 < 1.0F) {
								float float3 = 1.0F - float2;
								float2 += float3 * ClimateManager.getInstance().getFogIntensity() * 2.0F;
								float2 = PZMath.clamp_01(float2);
							}

							this.DrawTextureAngle(playerHomingPoint.texture, playerHomingPoint.renderWidth, playerHomingPoint.renderHeight, (double)playerHomingPoint.renderX, (double)playerHomingPoint.renderY, (double)float1, playerHomingPoint.r, playerHomingPoint.g, playerHomingPoint.b, float2, playerHomingPoint.renderSizeMod);
						}
					}
				}
			}
		}
	}

	public void renderDirectionArrow(boolean boolean1) {
		if (!GameServer.bServer) {
			int int1 = IsoCamera.frameState.playerIndex;
			for (int int2 = 0; int2 < this.directionArrows.length; ++int2) {
				if (int2 == int1 && this.directionArrows[int2].size() != 0) {
					for (int int3 = 0; int3 < this.directionArrows[int2].size(); ++int3) {
						WorldMarkers.DirectionArrow directionArrow = (WorldMarkers.DirectionArrow)this.directionArrows[int2].get(int3);
						if (directionArrow.active && directionArrow.renderTexture != null && directionArrow.isDrawOnWorld == boolean1) {
							float float1 = 0.0F;
							if (directionArrow.renderWithAngle) {
								float1 = 180.0F - directionArrow.angle;
								if (float1 < 0.0F) {
									float1 += 360.0F;
								}
							}

							this.DrawTextureAngle(directionArrow.renderTexture, directionArrow.renderWidth, directionArrow.renderHeight, (double)directionArrow.renderScreenX, (double)directionArrow.renderScreenY, (double)float1, directionArrow.r, directionArrow.g, directionArrow.b, directionArrow.a, directionArrow.renderSizeMod);
						}
					}
				}
			}
		}
	}

	private void DrawTextureAngle(Texture texture, float float1, float float2, double double1, double double2, double double3, float float3, float float4, float float5, float float6, float float7) {
		float float8 = float1 * float7 / 2.0F;
		float float9 = float2 * float7 / 2.0F;
		double double4 = Math.toRadians(180.0 + double3);
		double double5 = Math.cos(double4) * (double)float8;
		double double6 = Math.sin(double4) * (double)float8;
		double double7 = Math.cos(double4) * (double)float9;
		double double8 = Math.sin(double4) * (double)float9;
		double double9 = double5 - double8;
		double double10 = double7 + double6;
		double double11 = -double5 - double8;
		double double12 = double7 - double6;
		double double13 = -double5 + double8;
		double double14 = -double7 - double6;
		double double15 = double5 + double8;
		double double16 = -double7 + double6;
		double9 += double1;
		double10 += double2;
		double11 += double1;
		double12 += double2;
		double13 += double1;
		double14 += double2;
		double15 += double1;
		double16 += double2;
		SpriteRenderer.instance.render(texture, double9, double10, double11, double12, double13, double14, double15, double16, float3, float4, float5, float6, float3, float4, float5, float6, float3, float4, float5, float6, float3, float4, float5, float6, (Consumer)null);
	}

	public static boolean intersectLineSegments(WorldMarkers.Line line, WorldMarkers.Line line2, WorldMarkers.Point point) {
		float float1 = line.s.x;
		float float2 = line.s.y;
		float float3 = line.e.x;
		float float4 = line.e.y;
		float float5 = line2.s.x;
		float float6 = line2.s.y;
		float float7 = line2.e.x;
		float float8 = line2.e.y;
		float float9 = (float8 - float6) * (float3 - float1) - (float7 - float5) * (float4 - float2);
		if (float9 == 0.0F) {
			return false;
		} else {
			float float10 = float2 - float6;
			float float11 = float1 - float5;
			float float12 = ((float7 - float5) * float10 - (float8 - float6) * float11) / float9;
			if (!(float12 < 0.0F) && !(float12 > 1.0F)) {
				float float13 = ((float3 - float1) * float10 - (float4 - float2) * float11) / float9;
				if (!(float13 < 0.0F) && !(float13 > 1.0F)) {
					if (point != null) {
						point.set(float1 + (float3 - float1) * float12, float2 + (float4 - float2) * float12);
					}

					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	public class DirectionArrow {
		public static final boolean doDebug = false;
		private WorldMarkers.DirectionArrow.DebugStuff debugStuff;
		private int ID;
		private boolean active = true;
		private boolean isRemoved = false;
		private boolean isDrawOnWorld = false;
		private Texture renderTexture;
		private Texture texture;
		private Texture texStairsUp;
		private Texture texStairsDown;
		private Texture texDown;
		private int x;
		private int y;
		private int z;
		private float r;
		private float g;
		private float b;
		private float a;
		private float renderWidth = 32.0F;
		private float renderHeight = 32.0F;
		private float angle;
		private float angleLerpVal = 0.25F;
		private boolean lastWasWithinView = true;
		private float renderScreenX;
		private float renderScreenY;
		private boolean renderWithAngle = true;
		private float renderSizeMod = 1.0F;

		public DirectionArrow(int int1) {
			if (Core.bDebug) {
			}

			this.ID = WorldMarkers.NextHomingPointID++;
		}

		public void setTexture(String string) {
			if (string == null) {
				string = "dir_arrow_up";
			}

			this.texture = Texture.getSharedTexture("media/textures/highlights/" + string + ".png");
		}

		public void setTexDown(String string) {
			this.texDown = Texture.getSharedTexture("media/textures/highlights/" + string + ".png");
		}

		public void setTexStairsDown(String string) {
			this.texStairsDown = Texture.getSharedTexture("media/textures/highlights/" + string + ".png");
		}

		public void setTexStairsUp(String string) {
			this.texStairsUp = Texture.getSharedTexture("media/textures/highlights/" + string + ".png");
		}

		public void remove() {
			this.isRemoved = true;
		}

		public boolean isRemoved() {
			return this.isRemoved;
		}

		public boolean isActive() {
			return this.active;
		}

		public void setActive(boolean boolean1) {
			this.active = boolean1;
		}

		public float getR() {
			return this.r;
		}

		public void setR(float float1) {
			this.r = float1;
		}

		public float getB() {
			return this.b;
		}

		public void setB(float float1) {
			this.b = float1;
		}

		public float getG() {
			return this.g;
		}

		public void setG(float float1) {
			this.g = float1;
		}

		public float getA() {
			return this.a;
		}

		public void setA(float float1) {
			this.a = float1;
		}

		public void setRGBA(float float1, float float2, float float3, float float4) {
			this.r = float1;
			this.g = float2;
			this.b = float3;
			this.a = float4;
		}

		public int getID() {
			return this.ID;
		}

		public int getX() {
			return this.x;
		}

		public void setX(int int1) {
			this.x = int1;
		}

		public int getY() {
			return this.y;
		}

		public void setY(int int1) {
			this.y = int1;
		}

		public int getZ() {
			return this.z;
		}

		public void setZ(int int1) {
			this.z = int1;
		}

		public float getRenderWidth() {
			return this.renderWidth;
		}

		public void setRenderWidth(float float1) {
			this.renderWidth = float1;
		}

		public float getRenderHeight() {
			return this.renderHeight;
		}

		public void setRenderHeight(float float1) {
			this.renderHeight = float1;
		}

		private class DebugStuff {
			private float centerX;
			private float centerY;
			private float endX;
			private float endY;
		}
	}

	class PlayerHomingPointList extends ArrayList {
	}

	class DirectionArrowList extends ArrayList {
	}

	class PlayerScreen {
		private float centerX;
		private float centerY;
		private float x;
		private float y;
		private float width;
		private float height;
		private float padTop = 100.0F;
		private float padLeft = 100.0F;
		private float padBot = 100.0F;
		private float padRight = 100.0F;
		private float innerX;
		private float innerY;
		private float innerX2;
		private float innerY2;
		private WorldMarkers.Line borderTop = new WorldMarkers.Line(new WorldMarkers.Point(0.0F, 0.0F), new WorldMarkers.Point(0.0F, 0.0F));
		private WorldMarkers.Line borderRight = new WorldMarkers.Line(new WorldMarkers.Point(0.0F, 0.0F), new WorldMarkers.Point(0.0F, 0.0F));
		private WorldMarkers.Line borderBot = new WorldMarkers.Line(new WorldMarkers.Point(0.0F, 0.0F), new WorldMarkers.Point(0.0F, 0.0F));
		private WorldMarkers.Line borderLeft = new WorldMarkers.Line(new WorldMarkers.Point(0.0F, 0.0F), new WorldMarkers.Point(0.0F, 0.0F));
		private WorldMarkers.Line[] borders = new WorldMarkers.Line[4];

		private void update(int int1) {
			this.x = 0.0F;
			this.y = 0.0F;
			this.width = (float)IsoCamera.getScreenWidth(int1);
			this.height = (float)IsoCamera.getScreenHeight(int1);
			this.centerX = this.x + this.width / 2.0F;
			this.centerY = this.y + this.height / 2.0F;
			this.innerX = this.x + this.padLeft;
			this.innerY = this.y + this.padTop;
			float float1 = this.width - (this.padLeft + this.padRight);
			float float2 = this.height - (this.padTop + this.padBot);
			this.innerX2 = this.innerX + float1;
			this.innerY2 = this.innerY + float2;
		}

		private WorldMarkers.Line[] getBorders() {
			this.borders[0] = this.getBorderTop();
			this.borders[1] = this.getBorderRight();
			this.borders[2] = this.getBorderBot();
			this.borders[3] = this.getBorderLeft();
			return this.borders;
		}

		private WorldMarkers.Line getBorderTop() {
			this.borderTop.s.set(this.innerX, this.innerY);
			this.borderTop.e.set(this.innerX2, this.innerY);
			return this.borderTop;
		}

		private WorldMarkers.Line getBorderRight() {
			this.borderRight.s.set(this.innerX2, this.innerY);
			this.borderRight.e.set(this.innerX2, this.innerY2);
			return this.borderRight;
		}

		private WorldMarkers.Line getBorderBot() {
			this.borderBot.s.set(this.innerX, this.innerY2);
			this.borderBot.e.set(this.innerX2, this.innerY2);
			return this.borderBot;
		}

		private WorldMarkers.Line getBorderLeft() {
			this.borderLeft.s.set(this.innerX, this.innerY);
			this.borderLeft.e.set(this.innerX, this.innerY2);
			return this.borderLeft;
		}

		private float clampToInnerX(float float1) {
			return PZMath.clamp(float1, this.innerX, this.innerX2);
		}

		private float clampToInnerY(float float1) {
			return PZMath.clamp(float1, this.innerY, this.innerY2);
		}

		private boolean isOnScreen(float float1, float float2) {
			return float1 >= this.x && float1 < this.x + this.width && float2 >= this.y && float2 < this.y + this.height;
		}

		private boolean isWithinInner(float float1, float float2) {
			return float1 >= this.innerX && float1 < this.innerX2 && float2 >= this.innerY && float2 < this.innerY2;
		}
	}

	private static class Point {
		float x;
		float y;

		Point(float float1, float float2) {
			this.x = float1;
			this.y = float2;
		}

		public WorldMarkers.Point set(float float1, float float2) {
			this.x = float1;
			this.y = float2;
			return this;
		}

		public boolean notInfinite() {
			return !Float.isInfinite(this.x) && !Float.isInfinite(this.y);
		}

		public String toString() {
			return String.format("{%f, %f}", this.x, this.y);
		}
	}

	private static class Line {
		WorldMarkers.Point s;
		WorldMarkers.Point e;

		Line(WorldMarkers.Point point, WorldMarkers.Point point2) {
			this.s = point;
			this.e = point2;
		}

		public String toString() {
			return String.format("{s: %s, e: %s}", this.s.toString(), this.e.toString());
		}
	}

	public static class PlayerHomingPoint {
		private int ID;
		private Texture texture;
		private int x;
		private int y;
		private float r;
		private float g;
		private float b;
		private float a;
		private float angle = 0.0F;
		private float targetAngle = 0.0F;
		private boolean customTargetAngle = false;
		private float angleLerpVal = 0.25F;
		private float movementLerpVal = 0.25F;
		private int dist = 0;
		private float targRenderX = (float)Core.getInstance().getScreenWidth() / 2.0F;
		private float targRenderY = (float)Core.getInstance().getScreenHeight() / 2.0F;
		private float renderX;
		private float renderY;
		private float renderOffsetX;
		private float renderOffsetY;
		private float renderWidth;
		private float renderHeight;
		private float renderSizeMod;
		private float targetScreenX;
		private float targetScreenY;
		private boolean targetOnScreen;
		private float stickToCharDist;
		private boolean active;
		private boolean homeOnTargetInView;
		private int homeOnTargetDist;
		private float homeOnOffsetX;
		private float homeOnOffsetY;
		private boolean isRemoved;

		public PlayerHomingPoint(int int1) {
			this.renderX = this.targRenderX;
			this.renderY = this.targRenderY;
			this.renderOffsetX = 0.0F;
			this.renderOffsetY = 50.0F;
			this.renderWidth = 32.0F;
			this.renderHeight = 32.0F;
			this.renderSizeMod = 1.0F;
			this.targetOnScreen = false;
			this.stickToCharDist = 130.0F;
			this.homeOnTargetInView = true;
			this.homeOnTargetDist = 20;
			this.homeOnOffsetX = 0.0F;
			this.homeOnOffsetY = 0.0F;
			this.isRemoved = false;
			this.ID = WorldMarkers.NextHomingPointID++;
			float float1 = (float)IsoCamera.getScreenLeft(int1);
			float float2 = (float)IsoCamera.getScreenTop(int1);
			float float3 = (float)IsoCamera.getScreenWidth(int1);
			float float4 = (float)IsoCamera.getScreenHeight(int1);
			this.targRenderX = float1 + float3 / 2.0F;
			this.targRenderY = float2 + float4 / 2.0F;
		}

		public void setTexture(String string) {
			if (string == null) {
				string = "arrow_triangle";
			}

			this.texture = Texture.getSharedTexture("media/textures/highlights/" + string + ".png");
		}

		public void remove() {
			this.isRemoved = true;
		}

		public boolean isRemoved() {
			return this.isRemoved;
		}

		public boolean isActive() {
			return this.active;
		}

		public void setActive(boolean boolean1) {
			this.active = boolean1;
		}

		public float getR() {
			return this.r;
		}

		public void setR(float float1) {
			this.r = float1;
		}

		public float getB() {
			return this.b;
		}

		public void setB(float float1) {
			this.b = float1;
		}

		public float getG() {
			return this.g;
		}

		public void setG(float float1) {
			this.g = float1;
		}

		public float getA() {
			return this.a;
		}

		public void setA(float float1) {
			this.a = float1;
		}

		public int getHomeOnTargetDist() {
			return this.homeOnTargetDist;
		}

		public void setHomeOnTargetDist(int int1) {
			this.homeOnTargetDist = int1;
		}

		public int getID() {
			return this.ID;
		}

		public float getTargetAngle() {
			return this.targetAngle;
		}

		public void setTargetAngle(float float1) {
			this.targetAngle = float1;
		}

		public boolean isCustomTargetAngle() {
			return this.customTargetAngle;
		}

		public void setCustomTargetAngle(boolean boolean1) {
			this.customTargetAngle = boolean1;
		}

		public int getX() {
			return this.x;
		}

		public void setX(int int1) {
			this.x = int1;
		}

		public int getY() {
			return this.y;
		}

		public void setY(int int1) {
			this.y = int1;
		}

		public float getAngleLerpVal() {
			return this.angleLerpVal;
		}

		public void setAngleLerpVal(float float1) {
			this.angleLerpVal = float1;
		}

		public float getMovementLerpVal() {
			return this.movementLerpVal;
		}

		public void setMovementLerpVal(float float1) {
			this.movementLerpVal = float1;
		}

		public boolean isHomeOnTargetInView() {
			return this.homeOnTargetInView;
		}

		public void setHomeOnTargetInView(boolean boolean1) {
			this.homeOnTargetInView = boolean1;
		}

		public float getRenderWidth() {
			return this.renderWidth;
		}

		public void setRenderWidth(float float1) {
			this.renderWidth = float1;
		}

		public float getRenderHeight() {
			return this.renderHeight;
		}

		public void setRenderHeight(float float1) {
			this.renderHeight = float1;
		}

		public float getStickToCharDist() {
			return this.stickToCharDist;
		}

		public void setStickToCharDist(float float1) {
			this.stickToCharDist = float1;
		}

		public float getRenderOffsetX() {
			return this.renderOffsetX;
		}

		public void setRenderOffsetX(float float1) {
			this.renderOffsetX = float1;
		}

		public float getRenderOffsetY() {
			return this.renderOffsetY;
		}

		public void setRenderOffsetY(float float1) {
			this.renderOffsetY = float1;
		}

		public float getHomeOnOffsetX() {
			return this.homeOnOffsetX;
		}

		public void setHomeOnOffsetX(float float1) {
			this.homeOnOffsetX = float1;
		}

		public float getHomeOnOffsetY() {
			return this.homeOnOffsetY;
		}

		public void setHomeOnOffsetY(float float1) {
			this.homeOnOffsetY = float1;
		}

		public void setTableSurface() {
			this.homeOnOffsetY = -30.0F * (float)Core.TileScale;
		}

		public void setHighCounter() {
			this.homeOnOffsetY = -50.0F * (float)Core.TileScale;
		}

		public void setYOffsetScaled(float float1) {
			this.homeOnOffsetY = float1 * (float)Core.TileScale;
		}

		public void setXOffsetScaled(float float1) {
			this.homeOnOffsetX = float1 * (float)Core.TileScale;
		}
	}

	public static final class GridSquareMarker {
		private int ID;
		private IsoSpriteInstance sprite;
		private IsoSpriteInstance spriteOverlay;
		private float orig_x;
		private float orig_y;
		private float orig_z;
		private float x;
		private float y;
		private float z;
		private float scaleRatio;
		private float r;
		private float g;
		private float b;
		private float a;
		private float size;
		private boolean doBlink = false;
		private boolean doAlpha;
		private boolean bScaleCircleTexture = false;
		private float fadeSpeed = 0.006F;
		private float alpha = 0.0F;
		private float alphaMax = 1.0F;
		private float alphaMin = 0.3F;
		private boolean alphaInc = true;
		private boolean active = true;
		private boolean isRemoved = false;

		public GridSquareMarker() {
			this.ID = WorldMarkers.NextGridSquareMarkerID++;
		}

		public int getID() {
			return this.ID;
		}

		public void remove() {
			this.isRemoved = true;
		}

		public boolean isRemoved() {
			return this.isRemoved;
		}

		public void init(String string, String string2, int int1, int int2, int int3, float float1) {
			if (string == null) {
				string = "circle_center";
			}

			Texture texture = Texture.getSharedTexture("media/textures/highlights/" + string + ".png");
			float float2 = (float)texture.getWidth();
			float float3 = 64.0F * (float)Core.TileScale;
			this.scaleRatio = 1.0F / (float2 / float3);
			this.sprite = new IsoSpriteInstance(IsoSpriteManager.instance.getSprite("media/textures/highlights/" + string + ".png"));
			if (string2 != null) {
				this.spriteOverlay = new IsoSpriteInstance(IsoSpriteManager.instance.getSprite("media/textures/highlights/" + string2 + ".png"));
			}

			this.setPosAndSize(int1, int2, int3, float1);
		}

		public void setPosAndSize(int int1, int int2, int int3, float float1) {
			float float2 = float1 * (this.bScaleCircleTexture ? 1.5F : 1.0F);
			float float3 = this.scaleRatio * float2;
			this.sprite.setScale(float3, float3);
			if (this.spriteOverlay != null) {
				this.spriteOverlay.setScale(float3, float3);
			}

			this.size = float1;
			this.orig_x = (float)int1;
			this.orig_y = (float)int2;
			this.orig_z = (float)int3;
			this.x = (float)int1 - (float2 - 0.5F);
			this.y = (float)int2 + 0.5F;
			this.z = (float)int3;
		}

		public void setPos(int int1, int int2, int int3) {
			float float1 = this.size * (this.bScaleCircleTexture ? 1.5F : 1.0F);
			this.orig_x = (float)int1;
			this.orig_y = (float)int2;
			this.orig_z = (float)int3;
			this.x = (float)int1 - (float1 - 0.5F);
			this.y = (float)int2 + 0.5F;
			this.z = (float)int3;
		}

		public void setSize(float float1) {
			float float2 = float1 * (this.bScaleCircleTexture ? 1.5F : 1.0F);
			float float3 = this.scaleRatio * float2;
			this.sprite.setScale(float3, float3);
			if (this.spriteOverlay != null) {
				this.spriteOverlay.setScale(float3, float3);
			}

			this.size = float1;
			this.x = this.orig_x - (float2 - 0.5F);
			this.y = this.orig_y + 0.5F;
			this.z = this.orig_z;
		}

		public boolean isActive() {
			return this.active;
		}

		public void setActive(boolean boolean1) {
			this.active = boolean1;
		}

		public float getSize() {
			return this.size;
		}

		public float getX() {
			return this.x;
		}

		public float getY() {
			return this.y;
		}

		public float getZ() {
			return this.z;
		}

		public float getR() {
			return this.r;
		}

		public void setR(float float1) {
			this.r = float1;
		}

		public float getG() {
			return this.g;
		}

		public void setG(float float1) {
			this.g = float1;
		}

		public float getB() {
			return this.b;
		}

		public void setB(float float1) {
			this.b = float1;
		}

		public float getA() {
			return this.a;
		}

		public void setA(float float1) {
			this.a = float1;
		}

		public float getAlpha() {
			return this.alpha;
		}

		public void setAlpha(float float1) {
			this.alpha = float1;
		}

		public float getAlphaMax() {
			return this.alphaMax;
		}

		public void setAlphaMax(float float1) {
			this.alphaMax = float1;
		}

		public float getAlphaMin() {
			return this.alphaMin;
		}

		public void setAlphaMin(float float1) {
			this.alphaMin = float1;
		}

		public boolean isDoAlpha() {
			return this.doAlpha;
		}

		public void setDoAlpha(boolean boolean1) {
			this.doAlpha = boolean1;
		}

		public float getFadeSpeed() {
			return this.fadeSpeed;
		}

		public void setFadeSpeed(float float1) {
			this.fadeSpeed = float1;
		}

		public boolean isDoBlink() {
			return this.doBlink;
		}

		public void setDoBlink(boolean boolean1) {
			this.doBlink = boolean1;
		}

		public boolean isScaleCircleTexture() {
			return this.bScaleCircleTexture;
		}

		public void setScaleCircleTexture(boolean boolean1) {
			this.bScaleCircleTexture = boolean1;
			float float1 = this.size * (this.bScaleCircleTexture ? 1.5F : 1.0F);
			float float2 = this.scaleRatio * float1;
			if (this.sprite != null) {
				this.sprite.setScale(float2, float2);
			}

			if (this.spriteOverlay != null) {
				this.spriteOverlay.setScale(float2, float2);
			}

			this.x = this.orig_x - (float1 - 0.5F);
		}
	}
}
