package zombie.iso.objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.Util;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.IndieFileLoader;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.textures.ColorInfo;
import zombie.debug.DebugLog;
import zombie.inventory.types.HandWeapon;
import zombie.iso.CellLoader;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.LosUtil;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;


public class IsoTree extends IsoObject {
	public static final int MAX_SIZE = 6;
	public int LogYield = 1;
	public int damage = 500;
	public int size = 4;
	public boolean bRenderFlag;
	public float fadeAlpha;

	public IsoTree(IsoCell cell) {
		super(cell);
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		byteBuffer.putInt(this.LogYield);
		byteBuffer.putInt(this.damage);
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.LogYield = byteBuffer.getInt();
		this.damage = byteBuffer.getInt();
		if (this.sprite != null && this.sprite.getProperties().Val("tree") != null) {
			this.size = Integer.parseInt(this.sprite.getProperties().Val("tree"));
			if (this.size < 1) {
				this.size = 1;
			}

			if (this.size > 6) {
				this.size = 6;
			}
		}
	}

	protected void checkMoveWithWind() {
		this.checkMoveWithWind(true);
	}

	public void reset() {
		super.reset();
	}

	public IsoTree(IsoGridSquare square, String string) {
		super(square, string, false);
		this.initTree();
	}

	public IsoTree(IsoGridSquare square, IsoSprite sprite) {
		super(square.getCell(), square, sprite);
		this.initTree();
	}

	public void initTree() {
		this.setType(IsoObjectType.tree);
		if (this.sprite.getProperties().Val("tree") != null) {
			this.size = Integer.parseInt(this.sprite.getProperties().Val("tree"));
			if (this.size < 1) {
				this.size = 1;
			}

			if (this.size > 6) {
				this.size = 6;
			}
		} else {
			this.size = 4;
		}

		switch (this.size) {
		case 1: 
		
		case 2: 
			this.LogYield = 1;
			break;
		
		case 3: 
		
		case 4: 
			this.LogYield = 2;
			break;
		
		case 5: 
			this.LogYield = 3;
			break;
		
		case 6: 
			this.LogYield = 4;
		
		}
		this.damage = this.LogYield * 80;
	}

	public String getObjectName() {
		return "Tree";
	}

	public void Damage(float float1) {
		WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
		float float2 = float1 * 0.05F;
		this.damage = (int)((float)this.damage - float2);
		if (this.damage <= 0) {
			this.square.transmitRemoveItemFromSquare(this);
			this.square.RecalcAllWithNeighbours(true);
			int int1 = this.LogYield;
			int int2;
			for (int2 = 0; int2 < int1; ++int2) {
				this.square.AddWorldInventoryItem("Base.Log", 0.0F, 0.0F, 0.0F);
				if (Rand.Next(4) == 0) {
					this.square.AddWorldInventoryItem("Base.TreeBranch", 0.0F, 0.0F, 0.0F);
				}

				if (Rand.Next(4) == 0) {
					this.square.AddWorldInventoryItem("Base.Twigs", 0.0F, 0.0F, 0.0F);
				}
			}

			if (GameClient.bClient) {
				this.square.clientModify();
			}

			this.reset();
			CellLoader.isoTreeCache.add(this);
			for (int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
				LosUtil.cachecleared[int2] = true;
			}

			IsoGridSquare.setRecalcLightTime(-1);
			GameTime.instance.lightSourceUpdate = 100.0F;
			LuaEventManager.triggerEvent("OnContainerUpdate");
		}
	}

	public void WeaponHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		gameCharacter.getEmitter().playSound("ChopTree");
		WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
		this.setRenderEffect(RenderEffectType.Hit_Tree_Shudder, true);
		float float1 = (float)handWeapon.getTreeDamage();
		if (gameCharacter.HasTrait("Axeman") && handWeapon.getCategories().contains("Axe")) {
			float1 *= 1.5F;
		}

		this.damage = (int)((float)this.damage - float1);
		if (this.damage <= 0) {
			this.square.transmitRemoveItemFromSquare(this);
			gameCharacter.getEmitter().playSound("FallingTree");
			this.square.RecalcAllWithNeighbours(true);
			int int1 = this.LogYield;
			int int2;
			for (int2 = 0; int2 < int1; ++int2) {
				this.square.AddWorldInventoryItem("Base.Log", 0.0F, 0.0F, 0.0F);
				if (Rand.Next(4) == 0) {
					this.square.AddWorldInventoryItem("Base.TreeBranch", 0.0F, 0.0F, 0.0F);
				}

				if (Rand.Next(4) == 0) {
					this.square.AddWorldInventoryItem("Base.Twigs", 0.0F, 0.0F, 0.0F);
				}
			}

			if (GameClient.bClient) {
				this.square.clientModify();
			}

			this.reset();
			CellLoader.isoTreeCache.add(this);
			for (int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
				LosUtil.cachecleared[int2] = true;
			}

			IsoGridSquare.setRecalcLightTime(-1);
			GameTime.instance.lightSourceUpdate = 100.0F;
			LuaEventManager.triggerEvent("OnContainerUpdate");
		}

		LuaEventManager.triggerEvent("OnWeaponHitTree", gameCharacter, handWeapon);
	}

	public void setHealth(int int1) {
		this.damage = Math.max(int1, 0);
	}

	public int getHealth() {
		return this.damage;
	}

	public int getMaxHealth() {
		return this.LogYield * 80;
	}

	public int getSize() {
		return this.size;
	}

	public float getSlowFactor(IsoMovingObject movingObject) {
		float float1 = 1.0F;
		if (movingObject instanceof IsoGameCharacter) {
			if ("parkranger".equals(((IsoGameCharacter)movingObject).getDescriptor().getProfession())) {
				float1 = 1.5F;
			}

			if ("lumberjack".equals(((IsoGameCharacter)movingObject).getDescriptor().getProfession())) {
				float1 = 1.2F;
			}
		}

		if (this.size != 1 && this.size != 2) {
			return this.size != 3 && this.size != 4 ? 0.3F * float1 : 0.5F * float1;
		} else {
			return 0.8F * float1;
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1) {
		int int1 = IsoCamera.frameState.playerIndex;
		if (!this.bRenderFlag && !(this.fadeAlpha < this.targetAlpha[int1])) {
			this.renderInner(float1, float2, float3, colorInfo, boolean1);
		} else {
			IndieGL.enableStencilTest();
			IndieGL.glStencilFunc(517, 128, 128);
			this.renderInner(float1, float2, float3, colorInfo, boolean1);
			if (this.bRenderFlag && this.fadeAlpha > 0.25F) {
				this.fadeAlpha -= IsoObject.alphaStep;
				if (this.fadeAlpha < 0.25F) {
					this.fadeAlpha = 0.25F;
				}
			}

			float float4;
			if (!this.bRenderFlag) {
				float4 = this.targetAlpha[int1];
				if (this.fadeAlpha < float4) {
					this.fadeAlpha += IsoObject.alphaStep;
					if (this.fadeAlpha > float4) {
						this.fadeAlpha = float4;
					}
				}
			}

			float4 = this.alpha[int1];
			this.alpha[0] = this.fadeAlpha;
			IndieGL.glStencilFunc(514, 128, 128);
			this.renderInner(float1, float2, float3, colorInfo, true);
			this.alpha[0] = float4;
			if (IsoTree.TreeShader.instance.ShaderID == -1) {
				RenderThread.borrowContext();
				try {
					IsoTree.TreeShader.instance.initShader();
				} finally {
					RenderThread.returnContext();
				}
			}

			if (IsoTree.TreeShader.instance.ShaderID > 0) {
				IndieGL.StartShader(IsoTree.TreeShader.instance.ShaderID, 0);
				SpriteRenderer.instance.ShaderUpdate(IsoTree.TreeShader.instance.ShaderID, IsoTree.TreeShader.instance.outlineAlpha, 1.0F - this.fadeAlpha);
				this.renderInner(float1, float2, float3, colorInfo, true);
				IndieGL.StartShader(0, 0);
			}

			IndieGL.glStencilFunc(519, 255, 255);
		}
	}

	private void renderInner(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1) {
		float float4;
		float float5;
		if (this.sprite != null && this.sprite.name != null && this.sprite.name.contains("JUMBO")) {
			float4 = this.offsetX;
			float5 = this.offsetY;
			this.offsetX = (float)(384 * Core.TileScale / 2 - 96 * Core.TileScale);
			this.offsetY = (float)(256 * Core.TileScale - 32 * Core.TileScale);
			if (this.offsetX != float4 || this.offsetY != float5) {
				this.sx = 0;
			}
		} else {
			float4 = this.offsetX;
			float5 = this.offsetY;
			this.offsetX = (float)(32 * Core.TileScale);
			this.offsetY = (float)(96 * Core.TileScale);
			if (this.offsetX != float4 || this.offsetY != float5) {
				this.sx = 0;
			}
		}

		super.render(float1, float2, float3, colorInfo, false);
		if (this.AttachedAnimSprite != null) {
			int int1 = this.AttachedAnimSprite.size();
			for (int int2 = 0; int2 < int1; ++int2) {
				IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int2);
				float float6 = this.targetAlpha[IsoCamera.frameState.playerIndex];
				this.targetAlpha[IsoCamera.frameState.playerIndex] = 1.0F;
				spriteInstance.render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY, colorInfo);
				this.targetAlpha[IsoCamera.frameState.playerIndex] = float6;
				spriteInstance.update();
			}
		}
	}

	public void setSprite(IsoSprite sprite) {
		super.setSprite(sprite);
		this.initTree();
	}

	public boolean isMaskClicked(int int1, int int2, boolean boolean1) {
		if (super.isMaskClicked(int1, int2, boolean1)) {
			return true;
		} else if (this.AttachedAnimSpriteActual == null) {
			return false;
		} else {
			for (int int3 = 0; int3 < this.AttachedAnimSpriteActual.size(); ++int3) {
				if (((IsoSprite)this.AttachedAnimSpriteActual.get(int3)).isMaskClicked(this.dir, int1, int2, boolean1)) {
					return true;
				}
			}

			return false;
		}
	}

	private static class TreeShader {
		public static final IsoTree.TreeShader instance = new IsoTree.TreeShader();
		private int ShaderID = -1;
		private int FragID = 0;
		private int VertID = 0;
		private int stepSize;
		private int outlineAlpha;

		private void initShader() {
			this.ShaderID = ARBShaderObjects.glCreateProgramObjectARB();
			if (this.ShaderID != 0) {
				this.FragID = this.createFragShader("media/shaders/outline.frag");
				this.VertID = this.createVertShader("media/shaders/outline.vert");
				if (this.VertID != 0 && this.FragID != 0) {
					ARBShaderObjects.glAttachObjectARB(this.ShaderID, this.VertID);
					ARBShaderObjects.glAttachObjectARB(this.ShaderID, this.FragID);
					ARBShaderObjects.glLinkProgramARB(this.ShaderID);
					ARBShaderObjects.glValidateProgramARB(this.ShaderID);
					if (ARBShaderObjects.glGetObjectParameteriARB(this.ShaderID, 35715) == 0) {
						DebugLog.log(getLogInfo(this.ShaderID));
						this.VertID = 0;
						this.ShaderID = 0;
						this.FragID = 0;
						return;
					}

					this.stepSize = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "stepSize");
					this.outlineAlpha = ARBShaderObjects.glGetUniformLocationARB(this.ShaderID, "outlineAlpha");
					ARBShaderObjects.glUseProgramObjectARB(this.ShaderID);
					ARBShaderObjects.glUniform2fARB(this.stepSize, 0.001F, 0.001F);
					ARBShaderObjects.glUseProgramObjectARB(0);
				} else {
					ARBShaderObjects.glDeleteObjectARB(this.ShaderID);
					this.ShaderID = 0;
				}
			}

			Util.checkGLError();
		}

		private int createVertShader(String string) {
			int int1 = ARBShaderObjects.glCreateShaderObjectARB(35633);
			if (int1 == 0) {
				return 0;
			} else {
				String string2 = "";
				try {
					InputStreamReader inputStreamReader = IndieFileLoader.getStreamReader(string, false);
					Throwable throwable = null;
					try {
						BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
						Throwable throwable2 = null;
						try {
							String string3;
							try {
								while ((string3 = bufferedReader.readLine()) != null) {
									string2 = string2 + string3.trim() + System.lineSeparator();
								}
							} catch (Throwable throwable3) {
								throwable2 = throwable3;
								throw throwable3;
							}
						} finally {
							if (bufferedReader != null) {
								if (throwable2 != null) {
									try {
										bufferedReader.close();
									} catch (Throwable throwable4) {
										throwable2.addSuppressed(throwable4);
									}
								} else {
									bufferedReader.close();
								}
							}
						}
					} catch (Throwable throwable5) {
						throwable = throwable5;
						throw throwable5;
					} finally {
						if (inputStreamReader != null) {
							if (throwable != null) {
								try {
									inputStreamReader.close();
								} catch (Throwable throwable6) {
									throwable.addSuppressed(throwable6);
								}
							} else {
								inputStreamReader.close();
							}
						}
					}
				} catch (Exception exception) {
					DebugLog.log("Fail reading vertex shading code");
					return 0;
				}

				while (string2.indexOf("#") != 0) {
					string2 = string2.substring(1);
				}

				ARBShaderObjects.glShaderSourceARB(int1, string2);
				ARBShaderObjects.glCompileShaderARB(int1);
				if (!this.printLogInfo("vertex shader", int1)) {
					int1 = 0;
				}

				return int1;
			}
		}

		private int createFragShader(String string) {
			int int1 = ARBShaderObjects.glCreateShaderObjectARB(35632);
			if (int1 == 0) {
				return 0;
			} else {
				String string2 = "";
				try {
					InputStreamReader inputStreamReader = IndieFileLoader.getStreamReader(string, false);
					Throwable throwable = null;
					try {
						BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
						Throwable throwable2 = null;
						try {
							String string3;
							try {
								while ((string3 = bufferedReader.readLine()) != null) {
									string2 = string2 + string3.trim() + System.lineSeparator();
								}
							} catch (Throwable throwable3) {
								throwable2 = throwable3;
								throw throwable3;
							}
						} finally {
							if (bufferedReader != null) {
								if (throwable2 != null) {
									try {
										bufferedReader.close();
									} catch (Throwable throwable4) {
										throwable2.addSuppressed(throwable4);
									}
								} else {
									bufferedReader.close();
								}
							}
						}
					} catch (Throwable throwable5) {
						throwable = throwable5;
						throw throwable5;
					} finally {
						if (inputStreamReader != null) {
							if (throwable != null) {
								try {
									inputStreamReader.close();
								} catch (Throwable throwable6) {
									throwable.addSuppressed(throwable6);
								}
							} else {
								inputStreamReader.close();
							}
						}
					}
				} catch (Exception exception) {
					DebugLog.log("Fail reading fragment shading code");
					return 0;
				}

				while (string2.indexOf("#") != 0) {
					string2 = string2.substring(1);
				}

				ARBShaderObjects.glShaderSourceARB(int1, string2);
				ARBShaderObjects.glCompileShaderARB(int1);
				if (!this.printLogInfo("fragment shader", int1)) {
					int1 = 0;
				}

				return int1;
			}
		}

		private boolean printLogInfo(String string, int int1) {
			IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
			ARBShaderObjects.glGetObjectParameterARB(int1, 35716, intBuffer);
			int int2 = intBuffer.get();
			if (int2 > 1) {
				ByteBuffer byteBuffer = BufferUtils.createByteBuffer(int2);
				intBuffer.flip();
				ARBShaderObjects.glGetInfoLogARB(int1, intBuffer, byteBuffer);
				byte[] byteArray = new byte[int2];
				byteBuffer.get(byteArray);
				String string2 = new String(byteArray);
				DebugLog.log("Info log (" + string + "):\n" + string2 + "-----");
			}

			return true;
		}

		private static String getLogInfo(int int1) {
			return ARBShaderObjects.glGetInfoLogARB(int1, ARBShaderObjects.glGetObjectParameteriARB(int1, 35716));
		}
	}
}
