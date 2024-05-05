package zombie.core.skinnedmodel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.function.Consumer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.AttachedItems.AttachedModelName;
import zombie.characters.AttachedItems.AttachedModelNames;
import zombie.core.Core;
import zombie.core.ImmutableColor;
import zombie.core.SpriteRenderer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.advancedanimation.AnimatedModel;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureFBO;
import zombie.debug.DebugOptions;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoMannequin;
import zombie.popman.ObjectPool;
import zombie.util.StringUtils;
import zombie.vehicles.UI3DScene;


public final class DeadBodyAtlas {
	public static final int ATLAS_SIZE = 1024;
	private TextureFBO fbo;
	public static final DeadBodyAtlas instance = new DeadBodyAtlas();
	private static final Vector2 tempVector2 = new Vector2();
	private final HashMap EntryMap = new HashMap();
	private final ArrayList AtlasList = new ArrayList();
	private final DeadBodyAtlas.BodyParams bodyParams = new DeadBodyAtlas.BodyParams();
	private int updateCounter = -1;
	private final DeadBodyAtlas.Checksummer checksummer = new DeadBodyAtlas.Checksummer();
	private static final Stack JobPool = new Stack();
	private final DeadBodyAtlas.DebugDrawInWorld[] debugDrawInWorld = new DeadBodyAtlas.DebugDrawInWorld[3];
	private long debugDrawTime;
	private final ArrayList RenderJobs = new ArrayList();
	private final DeadBodyAtlas.CharacterTextureVisual characterTextureVisualFemale = new DeadBodyAtlas.CharacterTextureVisual(true);
	private final DeadBodyAtlas.CharacterTextureVisual characterTextureVisualMale = new DeadBodyAtlas.CharacterTextureVisual(false);
	private final CharacterTextures characterTexturesFemale = new CharacterTextures();
	private final CharacterTextures characterTexturesMale = new CharacterTextures();
	private final ObjectPool bodyTextureDrawerPool = new ObjectPool(DeadBodyAtlas.BodyTextureDrawer::new);

	public void lightingUpdate(int int1, boolean boolean1) {
		if (int1 != this.updateCounter && boolean1) {
			this.updateCounter = int1;
		}
	}

	public DeadBodyAtlas.BodyTexture getBodyTexture(IsoDeadBody deadBody) {
		this.bodyParams.init(deadBody);
		return this.getBodyTexture(this.bodyParams);
	}

	public DeadBodyAtlas.BodyTexture getBodyTexture(IsoZombie zombie) {
		this.bodyParams.init(zombie);
		return this.getBodyTexture(this.bodyParams);
	}

	public DeadBodyAtlas.BodyTexture getBodyTexture(IsoMannequin mannequin) {
		this.bodyParams.init(mannequin);
		return this.getBodyTexture(this.bodyParams);
	}

	public DeadBodyAtlas.BodyTexture getBodyTexture(boolean boolean1, String string, String string2, IsoDirections directions, int int1, float float1) {
		CharacterTextures characterTextures = boolean1 ? this.characterTexturesFemale : this.characterTexturesMale;
		DeadBodyAtlas.BodyTexture bodyTexture = characterTextures.getTexture(string, string2, directions, int1);
		if (bodyTexture != null) {
			return bodyTexture;
		} else {
			this.bodyParams.init(boolean1 ? this.characterTextureVisualFemale : this.characterTextureVisualMale, directions, string, string2, float1);
			this.bodyParams.variables.put("zombieWalkType", "1");
			DeadBodyAtlas.BodyTexture bodyTexture2 = this.getBodyTexture(this.bodyParams);
			characterTextures.addTexture(string, string2, directions, int1, bodyTexture2);
			return bodyTexture2;
		}
	}

	public DeadBodyAtlas.BodyTexture getBodyTexture(DeadBodyAtlas.BodyParams bodyParams) {
		String string = this.getBodyKey(bodyParams);
		DeadBodyAtlas.BodyTexture bodyTexture = (DeadBodyAtlas.BodyTexture)this.EntryMap.get(string);
		if (bodyTexture != null) {
			return bodyTexture;
		} else {
			DeadBodyAtlas.AtlasEntry atlasEntry = new DeadBodyAtlas.AtlasEntry();
			atlasEntry.key = string;
			atlasEntry.lightKey = this.getLightKey(bodyParams);
			atlasEntry.updateCounter = this.updateCounter;
			bodyTexture = new DeadBodyAtlas.BodyTexture();
			bodyTexture.entry = atlasEntry;
			this.EntryMap.put(string, bodyTexture);
			this.RenderJobs.add(DeadBodyAtlas.RenderJob.getNew().init(bodyParams, atlasEntry));
			return bodyTexture;
		}
	}

	public void checkLights(Texture texture, IsoDeadBody deadBody) {
		if (texture != null) {
			DeadBodyAtlas.BodyTexture bodyTexture = (DeadBodyAtlas.BodyTexture)this.EntryMap.get(texture.getName());
			if (bodyTexture != null) {
				DeadBodyAtlas.AtlasEntry atlasEntry = bodyTexture.entry;
				if (atlasEntry != null && atlasEntry.tex == texture) {
					if (atlasEntry.updateCounter != this.updateCounter) {
						atlasEntry.updateCounter = this.updateCounter;
						this.bodyParams.init(deadBody);
						String string = this.getLightKey(this.bodyParams);
						if (!atlasEntry.lightKey.equals(string)) {
							this.EntryMap.remove(atlasEntry.key);
							atlasEntry.key = this.getBodyKey(this.bodyParams);
							atlasEntry.lightKey = string;
							texture.setNameOnly(atlasEntry.key);
							this.EntryMap.put(atlasEntry.key, bodyTexture);
							DeadBodyAtlas.RenderJob renderJob = DeadBodyAtlas.RenderJob.getNew().init(this.bodyParams, atlasEntry);
							renderJob.bClearThisSlotOnly = true;
							this.RenderJobs.add(renderJob);
							this.render();
						}
					}
				}
			}
		}
	}

	public void checkLights(Texture texture, IsoZombie zombie) {
		if (texture != null) {
			DeadBodyAtlas.BodyTexture bodyTexture = (DeadBodyAtlas.BodyTexture)this.EntryMap.get(texture.getName());
			if (bodyTexture != null) {
				DeadBodyAtlas.AtlasEntry atlasEntry = bodyTexture.entry;
				if (atlasEntry != null && atlasEntry.tex == texture) {
					if (atlasEntry.updateCounter != this.updateCounter) {
						atlasEntry.updateCounter = this.updateCounter;
						this.bodyParams.init(zombie);
						String string = this.getLightKey(this.bodyParams);
						if (!atlasEntry.lightKey.equals(string)) {
							this.EntryMap.remove(atlasEntry.key);
							atlasEntry.key = this.getBodyKey(this.bodyParams);
							atlasEntry.lightKey = string;
							texture.setNameOnly(atlasEntry.key);
							this.EntryMap.put(atlasEntry.key, bodyTexture);
							DeadBodyAtlas.RenderJob renderJob = DeadBodyAtlas.RenderJob.getNew().init(this.bodyParams, atlasEntry);
							renderJob.bClearThisSlotOnly = true;
							this.RenderJobs.add(renderJob);
							this.render();
						}
					}
				}
			}
		}
	}

	private void assignEntryToAtlas(DeadBodyAtlas.AtlasEntry atlasEntry, int int1, int int2) {
		if (atlasEntry.atlas == null) {
			for (int int3 = 0; int3 < this.AtlasList.size(); ++int3) {
				DeadBodyAtlas.Atlas atlas = (DeadBodyAtlas.Atlas)this.AtlasList.get(int3);
				if (!atlas.isFull() && atlas.ENTRY_WID == int1 && atlas.ENTRY_HGT == int2) {
					atlas.addEntry(atlasEntry);
					return;
				}
			}

			DeadBodyAtlas.Atlas atlas2 = new DeadBodyAtlas.Atlas(1024, 1024, int1, int2);
			atlas2.addEntry(atlasEntry);
			this.AtlasList.add(atlas2);
		}
	}

	private String getBodyKey(DeadBodyAtlas.BodyParams bodyParams) {
		if (bodyParams.humanVisual == this.characterTextureVisualFemale.humanVisual) {
			return "SZF_" + bodyParams.animSetName + "_" + bodyParams.stateName + "_" + bodyParams.dir + "_" + bodyParams.trackTime;
		} else if (bodyParams.humanVisual == this.characterTextureVisualMale.humanVisual) {
			return "SZM_" + bodyParams.animSetName + "_" + bodyParams.stateName + "_" + bodyParams.dir + "_" + bodyParams.trackTime;
		} else {
			try {
				this.checksummer.reset();
				HumanVisual humanVisual = bodyParams.humanVisual;
				this.checksummer.update((byte)bodyParams.dir.index());
				this.checksummer.update((int)(PZMath.wrap(bodyParams.angle, 0.0F, 6.2831855F) * 57.295776F));
				this.checksummer.update(humanVisual.getHairModel());
				this.checksummer.update(humanVisual.getBeardModel());
				this.checksummer.update(humanVisual.getSkinColor());
				this.checksummer.update(humanVisual.getSkinTexture());
				this.checksummer.update((int)(humanVisual.getTotalBlood() * 100.0F));
				this.checksummer.update(bodyParams.primaryHandItem);
				this.checksummer.update(bodyParams.secondaryHandItem);
				for (int int1 = 0; int1 < bodyParams.attachedModelNames.size(); ++int1) {
					AttachedModelName attachedModelName = bodyParams.attachedModelNames.get(int1);
					this.checksummer.update(attachedModelName.attachmentNameSelf);
					this.checksummer.update(attachedModelName.attachmentNameParent);
					this.checksummer.update(attachedModelName.modelName);
					this.checksummer.update((int)(attachedModelName.bloodLevel * 100.0F));
				}

				this.checksummer.update(bodyParams.bFemale);
				this.checksummer.update(bodyParams.bZombie);
				this.checksummer.update(bodyParams.bSkeleton);
				this.checksummer.update(bodyParams.animSetName);
				this.checksummer.update(bodyParams.stateName);
				ItemVisuals itemVisuals = bodyParams.itemVisuals;
				for (int int2 = 0; int2 < itemVisuals.size(); ++int2) {
					ItemVisual itemVisual = (ItemVisual)itemVisuals.get(int2);
					ClothingItem clothingItem = itemVisual.getClothingItem();
					if (clothingItem != null) {
						this.checksummer.update(itemVisual.getBaseTexture(clothingItem));
						this.checksummer.update(itemVisual.getTextureChoice(clothingItem));
						this.checksummer.update(itemVisual.getTint(clothingItem));
						this.checksummer.update(clothingItem.getModel(humanVisual.isFemale()));
						this.checksummer.update((int)(itemVisual.getTotalBlood() * 100.0F));
					}
				}

				this.checksummer.update(bodyParams.fallOnFront);
				this.checksummer.update(bodyParams.bStanding);
				this.checksummer.update(bodyParams.bOutside);
				this.checksummer.update(bodyParams.bRoom);
				float float1 = (float)((int)(bodyParams.ambient.r * 10.0F)) / 10.0F;
				this.checksummer.update((byte)((int)(float1 * 255.0F)));
				float float2 = (float)((int)(bodyParams.ambient.g * 10.0F)) / 10.0F;
				this.checksummer.update((byte)((int)(float2 * 255.0F)));
				float float3 = (float)((int)(bodyParams.ambient.b * 10.0F)) / 10.0F;
				this.checksummer.update((byte)((int)(float3 * 255.0F)));
				this.checksummer.update((int)bodyParams.trackTime);
				for (int int3 = 0; int3 < bodyParams.lights.length; ++int3) {
					this.checksummer.update(bodyParams.lights[int3], bodyParams.x, bodyParams.y, bodyParams.z);
				}

				return this.checksummer.checksumToString();
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
				return "bogus";
			}
		}
	}

	private String getLightKey(DeadBodyAtlas.BodyParams bodyParams) {
		try {
			this.checksummer.reset();
			this.checksummer.update(bodyParams.bOutside);
			this.checksummer.update(bodyParams.bRoom);
			float float1 = (float)((int)(bodyParams.ambient.r * 10.0F)) / 10.0F;
			this.checksummer.update((byte)((int)(float1 * 255.0F)));
			float float2 = (float)((int)(bodyParams.ambient.g * 10.0F)) / 10.0F;
			this.checksummer.update((byte)((int)(float2 * 255.0F)));
			float float3 = (float)((int)(bodyParams.ambient.b * 10.0F)) / 10.0F;
			this.checksummer.update((byte)((int)(float3 * 255.0F)));
			for (int int1 = 0; int1 < bodyParams.lights.length; ++int1) {
				this.checksummer.update(bodyParams.lights[int1], bodyParams.x, bodyParams.y, bodyParams.z);
			}

			return this.checksummer.checksumToString();
		} catch (Throwable throwable) {
			ExceptionLogger.logException(throwable);
			return "bogus";
		}
	}

	public void render() {
		int int1;
		for (int1 = 0; int1 < this.AtlasList.size(); ++int1) {
			DeadBodyAtlas.Atlas atlas = (DeadBodyAtlas.Atlas)this.AtlasList.get(int1);
			if (atlas.clear) {
				SpriteRenderer.instance.drawGeneric(new DeadBodyAtlas.ClearAtlasTexture(atlas));
			}
		}

		if (!this.RenderJobs.isEmpty()) {
			for (int1 = 0; int1 < this.RenderJobs.size(); ++int1) {
				DeadBodyAtlas.RenderJob renderJob = (DeadBodyAtlas.RenderJob)this.RenderJobs.get(int1);
				if (renderJob.done != 1 || renderJob.renderRefCount <= 0) {
					if (renderJob.done == 1 && renderJob.renderRefCount == 0) {
						this.RenderJobs.remove(int1--);
						assert !JobPool.contains(renderJob);
						JobPool.push(renderJob);
					} else if (renderJob.renderMain()) {
						++renderJob.renderRefCount;
						SpriteRenderer.instance.drawGeneric(renderJob);
					}
				}
			}
		}
	}

	public void renderDebug() {
		if (Core.bDebug && DebugOptions.instance.DeadBodyAtlasRender.getValue()) {
			if (JobPool.isEmpty()) {
				return;
			}

			if (((DeadBodyAtlas.RenderJob)JobPool.get(JobPool.size() - 1)).entry.atlas == null) {
				return;
			}

			int int1;
			if (this.debugDrawInWorld[0] == null) {
				for (int1 = 0; int1 < this.debugDrawInWorld.length; ++int1) {
					this.debugDrawInWorld[int1] = new DeadBodyAtlas.DebugDrawInWorld();
				}
			}

			int1 = SpriteRenderer.instance.getMainStateIndex();
			long long1 = System.currentTimeMillis();
			DeadBodyAtlas.RenderJob renderJob;
			if (long1 - this.debugDrawTime < 500L) {
				renderJob = (DeadBodyAtlas.RenderJob)JobPool.pop();
				renderJob.done = 0;
				renderJob.bClearThisSlotOnly = true;
				this.RenderJobs.add(renderJob);
			} else if (long1 - this.debugDrawTime < 1000L) {
				renderJob = (DeadBodyAtlas.RenderJob)JobPool.pop();
				renderJob.done = 0;
				renderJob.renderMain();
				this.debugDrawInWorld[int1].init(renderJob);
				SpriteRenderer.instance.drawGeneric(this.debugDrawInWorld[int1]);
			} else {
				this.debugDrawTime = long1;
			}
		}
	}

	public void renderUI() {
		if (Core.bDebug && DebugOptions.instance.DeadBodyAtlasRender.getValue()) {
			int int1 = 512 / Core.TileScale;
			int int2 = 0;
			int int3 = 0;
			for (int int4 = 0; int4 < this.AtlasList.size(); ++int4) {
				DeadBodyAtlas.Atlas atlas = (DeadBodyAtlas.Atlas)this.AtlasList.get(int4);
				SpriteRenderer.instance.renderi((Texture)null, int2, int3, int1, int1, 1.0F, 1.0F, 1.0F, 0.75F, (Consumer)null);
				SpriteRenderer.instance.renderi(atlas.tex, int2, int3, int1, int1, 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
				float float1 = (float)int1 / (float)atlas.tex.getWidth();
				int int5;
				for (int5 = 0; int5 <= atlas.tex.getWidth() / atlas.ENTRY_WID; ++int5) {
					SpriteRenderer.instance.renderline((Texture)null, (int)((float)int2 + (float)(int5 * atlas.ENTRY_WID) * float1), int3, (int)((float)int2 + (float)(int5 * atlas.ENTRY_WID) * float1), int3 + int1, 0.5F, 0.5F, 0.5F, 1.0F);
				}

				for (int5 = 0; int5 <= atlas.tex.getHeight() / atlas.ENTRY_HGT; ++int5) {
					SpriteRenderer.instance.renderline((Texture)null, int2, (int)((float)(int3 + int1) - (float)(int5 * atlas.ENTRY_HGT) * float1), int2 + int1, (int)((float)(int3 + int1) - (float)(int5 * atlas.ENTRY_HGT) * float1), 0.5F, 0.5F, 0.5F, 1.0F);
				}

				int3 += int1;
				if (int3 + int1 > Core.getInstance().getScreenHeight()) {
					int3 = 0;
					int2 += int1;
				}
			}

			SpriteRenderer.instance.renderi((Texture)null, int2, int3, int1, int1, 1.0F, 1.0F, 1.0F, 0.5F, (Consumer)null);
			SpriteRenderer.instance.renderi((Texture)ModelManager.instance.bitmap.getTexture(), int2, int3, int1, int1, 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
		}
	}

	public void Reset() {
		if (this.fbo != null) {
			this.fbo.destroyLeaveTexture();
			this.fbo = null;
		}

		this.AtlasList.forEach(DeadBodyAtlas.Atlas::Reset);
		this.AtlasList.clear();
		this.EntryMap.clear();
		this.characterTexturesFemale.clear();
		this.characterTexturesMale.clear();
		JobPool.forEach(DeadBodyAtlas.RenderJob::Reset);
		JobPool.clear();
		this.RenderJobs.clear();
	}

	private void toBodyAtlas(DeadBodyAtlas.RenderJob renderJob) {
		GL11.glPushAttrib(2048);
		if (this.fbo.getTexture() != renderJob.entry.atlas.tex) {
			this.fbo.setTexture(renderJob.entry.atlas.tex);
		}

		this.fbo.startDrawing();
		GL11.glViewport(0, 0, this.fbo.getWidth(), this.fbo.getHeight());
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		int int1 = renderJob.entry.atlas.tex.getWidth();
		int int2 = renderJob.entry.atlas.tex.getHeight();
		GLU.gluOrtho2D(0.0F, (float)int1, (float)int2, 0.0F);
		GL11.glMatrixMode(5888);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glEnable(3553);
		GL11.glDisable(3089);
		if (renderJob.entry.atlas.clear) {
			GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
			GL11.glClear(16640);
			GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
			renderJob.entry.atlas.clear = false;
		}

		int int3;
		int int4;
		int int5;
		int int6;
		if (renderJob.bClearThisSlotOnly) {
			GL11.glEnable(3089);
			GL11.glScissor(renderJob.entry.x, 1024 - renderJob.entry.y - renderJob.entry.h, renderJob.entry.w, renderJob.entry.h);
			GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
			GL11.glClear(16640);
			GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
			int3 = SpriteRenderer.instance.getRenderingPlayerIndex();
			int4 = int3 != 0 && int3 != 2 ? Core.getInstance().getOffscreenTrueWidth() / 2 : 0;
			int5 = int3 != 0 && int3 != 1 ? Core.getInstance().getOffscreenTrueHeight() / 2 : 0;
			int6 = Core.getInstance().getOffscreenTrueWidth();
			int int7 = Core.getInstance().getOffscreenTrueHeight();
			if (IsoPlayer.numPlayers > 1) {
				int6 /= 2;
			}

			if (IsoPlayer.numPlayers > 2) {
				int7 /= 2;
			}

			GL11.glScissor(int4, int5, int6, int7);
			GL11.glDisable(3089);
		}

		int3 = ModelManager.instance.bitmap.getTexture().getWidth() / 8 * Core.TileScale;
		int4 = ModelManager.instance.bitmap.getTexture().getHeight() / 8 * Core.TileScale;
		int5 = renderJob.entry.x - (int3 - renderJob.entry.atlas.ENTRY_WID) / 2;
		int6 = renderJob.entry.y - (int4 - renderJob.entry.atlas.ENTRY_HGT) / 2;
		ModelManager.instance.bitmap.getTexture().bind();
		GL11.glBegin(7);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		GL11.glTexCoord2f(0.0F, 0.0F);
		GL11.glVertex2i(int5, int6);
		GL11.glTexCoord2f(1.0F, 0.0F);
		GL11.glVertex2i(int5 + int3, int6);
		GL11.glTexCoord2f(1.0F, 1.0F);
		GL11.glVertex2i(int5 + int3, int6 + int4);
		GL11.glTexCoord2f(0.0F, 1.0F);
		GL11.glVertex2i(int5, int6 + int4);
		GL11.glEnd();
		Texture.lastTextureID = 0;
		GL11.glBindTexture(3553, 0);
		this.fbo.endDrawing();
		GL11.glEnable(3089);
		GL11.glMatrixMode(5889);
		GL11.glPopMatrix();
		GL11.glMatrixMode(5888);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		renderJob.entry.ready = true;
		renderJob.done = 1;
	}

	private static final class BodyParams {
		HumanVisual humanVisual;
		final ItemVisuals itemVisuals = new ItemVisuals();
		IsoDirections dir;
		float angle;
		boolean bFemale;
		boolean bZombie;
		boolean bSkeleton;
		String animSetName;
		String stateName;
		final HashMap variables = new HashMap();
		boolean bStanding;
		String primaryHandItem;
		String secondaryHandItem;
		final AttachedModelNames attachedModelNames = new AttachedModelNames();
		float x;
		float y;
		float z;
		float trackTime;
		boolean bOutside;
		boolean bRoom;
		final ColorInfo ambient = new ColorInfo();
		boolean fallOnFront = false;
		final IsoGridSquare.ResultLight[] lights = new IsoGridSquare.ResultLight[5];

		BodyParams() {
			for (int int1 = 0; int1 < this.lights.length; ++int1) {
				this.lights[int1] = new IsoGridSquare.ResultLight();
			}
		}

		void init(DeadBodyAtlas.BodyParams bodyParams) {
			this.humanVisual = bodyParams.humanVisual;
			this.itemVisuals.clear();
			this.itemVisuals.addAll(bodyParams.itemVisuals);
			this.dir = bodyParams.dir;
			this.angle = bodyParams.angle;
			this.bFemale = bodyParams.bFemale;
			this.bZombie = bodyParams.bZombie;
			this.bSkeleton = bodyParams.bSkeleton;
			this.animSetName = bodyParams.animSetName;
			this.stateName = bodyParams.stateName;
			this.variables.clear();
			this.variables.putAll(bodyParams.variables);
			this.bStanding = bodyParams.bStanding;
			this.primaryHandItem = bodyParams.primaryHandItem;
			this.secondaryHandItem = bodyParams.secondaryHandItem;
			this.attachedModelNames.copyFrom(bodyParams.attachedModelNames);
			this.x = bodyParams.x;
			this.y = bodyParams.y;
			this.z = bodyParams.z;
			this.trackTime = bodyParams.trackTime;
			this.fallOnFront = bodyParams.fallOnFront;
			this.bOutside = bodyParams.bOutside;
			this.bRoom = bodyParams.bRoom;
			this.ambient.set(bodyParams.ambient.r, bodyParams.ambient.g, bodyParams.ambient.b, 1.0F);
			for (int int1 = 0; int1 < this.lights.length; ++int1) {
				this.lights[int1].copyFrom(bodyParams.lights[int1]);
			}
		}

		void init(IsoDeadBody deadBody) {
			this.humanVisual = deadBody.getHumanVisual();
			deadBody.getItemVisuals(this.itemVisuals);
			this.dir = deadBody.dir;
			this.angle = deadBody.getAngle();
			this.bFemale = deadBody.isFemale();
			this.bZombie = deadBody.isZombie();
			this.bSkeleton = deadBody.isSkeleton();
			this.primaryHandItem = null;
			this.secondaryHandItem = null;
			this.attachedModelNames.initFrom(deadBody.getAttachedItems());
			this.animSetName = "zombie";
			this.stateName = "onground";
			this.variables.clear();
			this.bStanding = false;
			if (deadBody.getPrimaryHandItem() != null || deadBody.getSecondaryHandItem() != null) {
				if (deadBody.getPrimaryHandItem() != null && !StringUtils.isNullOrEmpty(deadBody.getPrimaryHandItem().getStaticModel())) {
					this.primaryHandItem = deadBody.getPrimaryHandItem().getStaticModel();
				}

				if (deadBody.getSecondaryHandItem() != null && !StringUtils.isNullOrEmpty(deadBody.getSecondaryHandItem().getStaticModel())) {
					this.secondaryHandItem = deadBody.getSecondaryHandItem().getStaticModel();
				}

				this.animSetName = "player";
				this.stateName = "deadbody";
			}

			this.x = deadBody.x;
			this.y = deadBody.y;
			this.z = deadBody.z;
			this.trackTime = 0.0F;
			this.fallOnFront = deadBody.isFallOnFront();
			this.bOutside = deadBody.square != null && deadBody.square.isOutside();
			this.bRoom = deadBody.square != null && deadBody.square.getRoom() != null;
			this.initAmbient(deadBody.square);
			this.initLights(deadBody.square);
		}

		void init(IsoZombie zombie) {
			this.humanVisual = zombie.getHumanVisual();
			zombie.getItemVisuals(this.itemVisuals);
			this.dir = zombie.dir;
			this.angle = zombie.getAnimAngleRadians();
			this.bFemale = zombie.isFemale();
			this.bZombie = true;
			this.bSkeleton = zombie.isSkeleton();
			this.primaryHandItem = null;
			this.secondaryHandItem = null;
			this.attachedModelNames.initFrom(zombie.getAttachedItems());
			this.animSetName = "zombie";
			this.stateName = "onground";
			this.variables.clear();
			this.bStanding = false;
			this.x = zombie.x;
			this.y = zombie.y;
			this.z = zombie.z;
			this.trackTime = 0.0F;
			this.fallOnFront = zombie.isFallOnFront();
			this.bOutside = zombie.getCurrentSquare() != null && zombie.getCurrentSquare().isOutside();
			this.bRoom = zombie.getCurrentSquare() != null && zombie.getCurrentSquare().getRoom() != null;
			this.initAmbient(zombie.getCurrentSquare());
			this.initLights(zombie.getCurrentSquare());
		}

		void init(IsoMannequin mannequin) {
			this.humanVisual = mannequin.getHumanVisual();
			mannequin.getItemVisuals(this.itemVisuals);
			this.dir = mannequin.dir;
			this.angle = this.dir.ToVector().getDirection();
			this.bFemale = mannequin.isFemale();
			this.bZombie = mannequin.isZombie();
			this.bSkeleton = mannequin.isSkeleton();
			this.primaryHandItem = null;
			this.secondaryHandItem = null;
			this.attachedModelNames.clear();
			this.animSetName = mannequin.getAnimSetName();
			this.stateName = mannequin.getAnimStateName();
			this.variables.clear();
			mannequin.getVariables(this.variables);
			this.bStanding = true;
			this.x = mannequin.getX();
			this.y = mannequin.getY();
			this.z = mannequin.getZ();
			this.trackTime = 0.0F;
			this.fallOnFront = false;
			this.bOutside = mannequin.square != null && mannequin.square.isOutside();
			this.bRoom = mannequin.square != null && mannequin.square.getRoom() != null;
			this.initAmbient(mannequin.square);
			this.initLights((IsoGridSquare)null);
		}

		void init(IHumanVisual iHumanVisual, IsoDirections directions, String string, String string2, float float1) {
			this.humanVisual = iHumanVisual.getHumanVisual();
			iHumanVisual.getItemVisuals(this.itemVisuals);
			this.dir = directions;
			this.angle = directions.ToVector().getDirection();
			this.bFemale = iHumanVisual.isFemale();
			this.bZombie = iHumanVisual.isZombie();
			this.bSkeleton = iHumanVisual.isSkeleton();
			this.primaryHandItem = null;
			this.secondaryHandItem = null;
			this.attachedModelNames.clear();
			this.animSetName = string;
			this.stateName = string2;
			this.variables.clear();
			this.bStanding = true;
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 0.0F;
			this.trackTime = float1;
			this.fallOnFront = false;
			this.bOutside = true;
			this.bRoom = false;
			this.ambient.set(1.0F, 1.0F, 1.0F, 1.0F);
			this.initLights((IsoGridSquare)null);
		}

		void initAmbient(IsoGridSquare square) {
			this.ambient.set(1.0F, 1.0F, 1.0F, 1.0F);
		}

		void initLights(IsoGridSquare square) {
			for (int int1 = 0; int1 < this.lights.length; ++int1) {
				this.lights[int1].radius = 0;
			}

			if (square != null) {
				IsoGridSquare.ILighting iLighting = square.lighting[0];
				int int2 = iLighting.resultLightCount();
				for (int int3 = 0; int3 < int2; ++int3) {
					this.lights[int3].copyFrom(iLighting.getResultLight(int3));
				}
			}
		}

		void Reset() {
			this.humanVisual = null;
			this.itemVisuals.clear();
			Arrays.fill(this.lights, (Object)null);
		}
	}

	private static final class Checksummer {
		private MessageDigest md;
		private final StringBuilder sb = new StringBuilder();

		public void reset() throws NoSuchAlgorithmException {
			if (this.md == null) {
				this.md = MessageDigest.getInstance("MD5");
			}

			this.md.reset();
		}

		public void update(byte byte1) {
			this.md.update(byte1);
		}

		public void update(boolean boolean1) {
			this.md.update((byte)(boolean1 ? 1 : 0));
		}

		public void update(int int1) {
			this.md.update((byte)(int1 & 255));
			this.md.update((byte)(int1 >> 8 & 255));
			this.md.update((byte)(int1 >> 16 & 255));
			this.md.update((byte)(int1 >> 24 & 255));
		}

		public void update(String string) {
			if (string != null && !string.isEmpty()) {
				this.md.update(string.getBytes());
			}
		}

		public void update(ImmutableColor immutableColor) {
			this.update((byte)((int)(immutableColor.r * 255.0F)));
			this.update((byte)((int)(immutableColor.g * 255.0F)));
			this.update((byte)((int)(immutableColor.b * 255.0F)));
		}

		public void update(IsoGridSquare.ResultLight resultLight, float float1, float float2, float float3) {
			if (resultLight != null && resultLight.radius > 0) {
				this.update((int)((float)resultLight.x - float1));
				this.update((int)((float)resultLight.y - float2));
				this.update((int)((float)resultLight.z - float3));
				this.update((byte)((int)(resultLight.r * 255.0F)));
				this.update((byte)((int)(resultLight.g * 255.0F)));
				this.update((byte)((int)(resultLight.b * 255.0F)));
				this.update((byte)resultLight.radius);
			}
		}

		public String checksumToString() {
			byte[] byteArray = this.md.digest();
			this.sb.setLength(0);
			for (int int1 = 0; int1 < byteArray.length; ++int1) {
				this.sb.append(byteArray[int1] & 255);
			}

			return this.sb.toString();
		}
	}

	private static final class DebugDrawInWorld extends TextureDraw.GenericDrawer {
		DeadBodyAtlas.RenderJob job;
		boolean bRendered;

		public void init(DeadBodyAtlas.RenderJob renderJob) {
			this.job = renderJob;
			this.bRendered = false;
		}

		public void render() {
			this.job.animatedModel.DoRenderToWorld(this.job.body.x, this.job.body.y, this.job.body.z, this.job.m_animPlayerAngle);
			this.bRendered = true;
		}

		public void postRender() {
			if (this.job.animatedModel != null) {
				if (this.bRendered) {
					assert !DeadBodyAtlas.JobPool.contains(this.job);
					DeadBodyAtlas.JobPool.push(this.job);
				} else {
					assert !DeadBodyAtlas.JobPool.contains(this.job);
					DeadBodyAtlas.JobPool.push(this.job);
				}

				this.job.animatedModel.postRender(this.bRendered);
			}
		}
	}

	private static final class CharacterTextureVisual implements IHumanVisual {
		final HumanVisual humanVisual = new HumanVisual(this);
		boolean bFemale;

		CharacterTextureVisual(boolean boolean1) {
			this.bFemale = boolean1;
			this.humanVisual.setHairModel("");
			this.humanVisual.setBeardModel("");
		}

		public HumanVisual getHumanVisual() {
			return this.humanVisual;
		}

		public void getItemVisuals(ItemVisuals itemVisuals) {
			itemVisuals.clear();
		}

		public boolean isFemale() {
			return this.bFemale;
		}

		public boolean isZombie() {
			return true;
		}

		public boolean isSkeleton() {
			return false;
		}
	}

	public static final class BodyTexture {
		DeadBodyAtlas.AtlasEntry entry;

		public void render(float float1, float float2, float float3, float float4, float float5, float float6) {
			if (this.entry.ready && this.entry.tex.isReady()) {
				this.entry.tex.render(float1 - (float)this.entry.w / 2.0F - this.entry.offsetX, float2 - (float)this.entry.h / 2.0F - this.entry.offsetY, (float)this.entry.w, (float)this.entry.h, float3, float4, float5, float6, (Consumer)null);
			} else {
				SpriteRenderer.instance.drawGeneric(((DeadBodyAtlas.BodyTextureDrawer)DeadBodyAtlas.instance.bodyTextureDrawerPool.alloc()).init(this, float1, float2, float3, float4, float5, float6));
			}
		}

		public void renderObjectPicker(float float1, float float2, ColorInfo colorInfo, IsoGridSquare square, IsoObject object) {
			if (this.entry.ready) {
				IsoObjectPicker.Instance.Add((int)(float1 - (float)(this.entry.w / 2)), (int)(float2 - (float)(this.entry.h / 2)), this.entry.w, this.entry.h, square, object, false, 1.0F, 1.0F);
			}
		}
	}

	private static final class AtlasEntry {
		public DeadBodyAtlas.Atlas atlas;
		public String key;
		public String lightKey;
		public int updateCounter;
		public int x;
		public int y;
		public int w;
		public int h;
		public float offsetX;
		public float offsetY;
		public Texture tex;
		public boolean ready = false;

		public void Reset() {
			this.atlas = null;
			this.tex.destroy();
			this.tex = null;
			this.ready = false;
		}
	}

	private static final class RenderJob extends TextureDraw.GenericDrawer {
		static float SIZEV = 42.75F;
		public final DeadBodyAtlas.BodyParams body = new DeadBodyAtlas.BodyParams();
		public DeadBodyAtlas.AtlasEntry entry;
		public AnimatedModel animatedModel;
		public float m_animPlayerAngle;
		public int done = 0;
		public int renderRefCount;
		public boolean bClearThisSlotOnly;
		int entryW;
		int entryH;
		final int[] m_viewport = new int[4];
		final Matrix4f m_matri4f = new Matrix4f();
		final Matrix4f m_projection = new Matrix4f();
		final Matrix4f m_modelView = new Matrix4f();
		final Vector3f m_scenePos = new Vector3f();
		final float[] m_bounds = new float[4];
		final float[] m_offset = new float[2];

		public static DeadBodyAtlas.RenderJob getNew() {
			return DeadBodyAtlas.JobPool.isEmpty() ? new DeadBodyAtlas.RenderJob() : (DeadBodyAtlas.RenderJob)DeadBodyAtlas.JobPool.pop();
		}

		public DeadBodyAtlas.RenderJob init(DeadBodyAtlas.BodyParams bodyParams, DeadBodyAtlas.AtlasEntry atlasEntry) {
			this.body.init(bodyParams);
			this.entry = atlasEntry;
			if (this.animatedModel == null) {
				this.animatedModel = new AnimatedModel();
				this.animatedModel.setAnimate(false);
			}

			this.animatedModel.setAnimSetName(bodyParams.animSetName);
			this.animatedModel.setState(bodyParams.stateName);
			this.animatedModel.setPrimaryHandModelName(bodyParams.primaryHandItem);
			this.animatedModel.setSecondaryHandModelName(bodyParams.secondaryHandItem);
			this.animatedModel.setAttachedModelNames(bodyParams.attachedModelNames);
			this.animatedModel.setAmbient(bodyParams.ambient, bodyParams.bOutside, bodyParams.bRoom);
			this.animatedModel.setLights(bodyParams.lights, bodyParams.x, bodyParams.y, bodyParams.z);
			this.animatedModel.setModelData(bodyParams.humanVisual, bodyParams.itemVisuals);
			this.animatedModel.setAngle(DeadBodyAtlas.tempVector2.setLengthAndDirection(bodyParams.angle, 1.0F));
			this.animatedModel.setVariable("FallOnFront", bodyParams.fallOnFront);
			bodyParams.variables.forEach((bodyParamsx,atlasEntryx)->{
				this.animatedModel.setVariable(bodyParamsx, atlasEntryx);
			});
			this.animatedModel.setTrackTime(bodyParams.trackTime);
			this.animatedModel.update();
			this.bClearThisSlotOnly = false;
			this.done = 0;
			this.renderRefCount = 0;
			return this;
		}

		public boolean renderMain() {
			if (this.animatedModel.isReadyToRender()) {
				this.animatedModel.renderMain();
				this.m_animPlayerAngle = this.animatedModel.getAnimationPlayer().getRenderedAngle();
				return true;
			} else {
				return false;
			}
		}

		public void render() {
			if (this.done != 1) {
				GL11.glDepthMask(true);
				GL11.glColorMask(true, true, true, true);
				GL11.glDisable(3089);
				GL11.glPushAttrib(2048);
				ModelManager.instance.bitmap.startDrawing(true, true);
				GL11.glViewport(0, 0, ModelManager.instance.bitmap.getWidth(), ModelManager.instance.bitmap.getHeight());
				this.calcModelOffset(this.m_offset);
				this.animatedModel.setOffset(this.m_offset[0], 0.0F, this.m_offset[1]);
				this.animatedModel.DoRender(0, 0, ModelManager.instance.bitmap.getTexture().getWidth(), ModelManager.instance.bitmap.getTexture().getHeight(), SIZEV, this.m_animPlayerAngle);
				if (this.animatedModel.isRendered()) {
					this.renderAABB();
				}

				ModelManager.instance.bitmap.endDrawing();
				GL11.glPopAttrib();
				if (this.animatedModel.isRendered()) {
					DeadBodyAtlas.instance.assignEntryToAtlas(this.entry, this.entryW, this.entryH);
					DeadBodyAtlas.instance.toBodyAtlas(this);
				}
			}
		}

		public void postRender() {
			if (this.animatedModel != null) {
				this.animatedModel.postRender(this.done == 1);
				assert this.renderRefCount > 0;
				--this.renderRefCount;
			}
		}

		void calcMatrices(Matrix4f matrix4f, Matrix4f matrix4f2, float float1, float float2) {
			int int1 = ModelManager.instance.bitmap.getWidth();
			int int2 = ModelManager.instance.bitmap.getHeight();
			float float3 = SIZEV;
			float float4 = (float)int1 / (float)int2;
			boolean boolean1 = true;
			matrix4f.identity();
			if (boolean1) {
				matrix4f.ortho(-float3 * float4, float3 * float4, float3, -float3, -100.0F, 100.0F);
			} else {
				matrix4f.ortho(-float3 * float4, float3 * float4, -float3, float3, -100.0F, 100.0F);
			}

			float float5 = (float)Math.sqrt(2048.0);
			matrix4f.scale(-float5, float5, float5);
			matrix4f2.identity();
			boolean boolean2 = true;
			if (boolean2) {
				matrix4f2.rotate(0.5235988F, 1.0F, 0.0F, 0.0F);
				matrix4f2.rotate(this.m_animPlayerAngle + 0.7853982F, 0.0F, 1.0F, 0.0F);
			} else {
				matrix4f2.rotate(this.m_animPlayerAngle, 0.0F, 1.0F, 0.0F);
			}

			matrix4f2.translate(float1, 0.0F, float2);
		}

		void calcModelBounds(float[] floatArray) {
			float float1 = Float.MAX_VALUE;
			float float2 = Float.MAX_VALUE;
			float float3 = -3.4028235E38F;
			float float4 = -3.4028235E38F;
			for (int int1 = 0; int1 < this.animatedModel.getAnimationPlayer().modelTransforms.length; ++int1) {
				if (int1 != 44) {
					org.lwjgl.util.vector.Matrix4f matrix4f = this.animatedModel.getAnimationPlayer().modelTransforms[int1];
					this.sceneToUI(matrix4f.m03, matrix4f.m13, matrix4f.m23, this.m_projection, this.m_modelView, this.m_scenePos);
					float1 = PZMath.min(float1, this.m_scenePos.x);
					float3 = PZMath.max(float3, this.m_scenePos.x);
					float2 = PZMath.min(float2, this.m_scenePos.y);
					float4 = PZMath.max(float4, this.m_scenePos.y);
				}
			}

			floatArray[0] = float1;
			floatArray[1] = float2;
			floatArray[2] = float3;
			floatArray[3] = float4;
		}

		void calcModelOffset(float[] floatArray) {
			int int1 = ModelManager.instance.bitmap.getWidth();
			int int2 = ModelManager.instance.bitmap.getHeight();
			float float1 = 0.0F;
			float float2 = this.body.bStanding ? -0.0F : 0.0F;
			this.calcMatrices(this.m_projection, this.m_modelView, float1, float2);
			this.calcModelBounds(this.m_bounds);
			float float3 = this.m_bounds[0];
			float float4 = this.m_bounds[1];
			float float5 = this.m_bounds[2];
			float float6 = this.m_bounds[3];
			this.uiToScene(this.m_projection, this.m_modelView, float3, float4, this.m_scenePos);
			float float7 = this.m_scenePos.x;
			float float8 = this.m_scenePos.z;
			float float9 = ((float)int1 - (float5 - float3)) / 2.0F;
			float float10 = ((float)int2 - (float6 - float4)) / 2.0F;
			this.uiToScene(this.m_projection, this.m_modelView, float9, float10, this.m_scenePos);
			float1 += this.m_scenePos.x - float7;
			float2 += this.m_scenePos.z - float8;
			floatArray[0] = 1.0F * float1 + 0.0F;
			floatArray[1] = 1.0F * float2 + 0.0F;
			this.entry.offsetX = (float9 - float3) / (8.0F / (float)Core.TileScale);
			this.entry.offsetY = (float10 - float4) / (8.0F / (float)Core.TileScale);
		}

		void renderAABB() {
			this.calcMatrices(this.m_projection, this.m_modelView, this.m_offset[0], this.m_offset[1]);
			this.calcModelBounds(this.m_bounds);
			float float1 = this.m_bounds[0];
			float float2 = this.m_bounds[1];
			float float3 = this.m_bounds[2];
			float float4 = this.m_bounds[3];
			int int1 = ModelManager.instance.bitmap.getWidth();
			int int2 = ModelManager.instance.bitmap.getHeight();
			float float5 = 128.0F;
			float1 -= float5;
			float2 -= float5;
			float3 += float5;
			float4 += float5;
			float1 = (float)Math.floor((double)(float1 / 128.0F)) * 128.0F;
			float3 = (float)Math.ceil((double)(float3 / 128.0F)) * 128.0F;
			float2 = (float)Math.floor((double)(float2 / 128.0F)) * 128.0F;
			float4 = (float)Math.ceil((double)(float4 / 128.0F)) * 128.0F;
			this.entryW = (int)(float3 - float1) / (8 / Core.TileScale);
			this.entryH = (int)(float4 - float2) / (8 / Core.TileScale);
		}

		UI3DScene.Ray getCameraRay(float float1, float float2, Matrix4f matrix4f, Matrix4f matrix4f2, UI3DScene.Ray ray) {
			Matrix4f matrix4f3 = DeadBodyAtlas.RenderJob.L_getCameraRay.matrix4f;
			matrix4f3.set((Matrix4fc)matrix4f);
			matrix4f3.mul((Matrix4fc)matrix4f2);
			matrix4f3.invert();
			this.m_viewport[0] = 0;
			this.m_viewport[1] = 0;
			this.m_viewport[2] = ModelManager.instance.bitmap.getWidth();
			this.m_viewport[3] = ModelManager.instance.bitmap.getHeight();
			Vector3f vector3f = matrix4f3.unprojectInv(float1, float2, 0.0F, this.m_viewport, DeadBodyAtlas.RenderJob.L_getCameraRay.ray_start);
			Vector3f vector3f2 = matrix4f3.unprojectInv(float1, float2, 1.0F, this.m_viewport, DeadBodyAtlas.RenderJob.L_getCameraRay.ray_end);
			ray.origin.set((Vector3fc)vector3f);
			ray.direction.set((Vector3fc)vector3f2.sub(vector3f).normalize());
			return ray;
		}

		Vector3f sceneToUI(float float1, float float2, float float3, Matrix4f matrix4f, Matrix4f matrix4f2, Vector3f vector3f) {
			Matrix4f matrix4f3 = this.m_matri4f;
			matrix4f3.set((Matrix4fc)matrix4f);
			matrix4f3.mul((Matrix4fc)matrix4f2);
			this.m_viewport[0] = 0;
			this.m_viewport[1] = 0;
			this.m_viewport[2] = ModelManager.instance.bitmap.getWidth();
			this.m_viewport[3] = ModelManager.instance.bitmap.getHeight();
			matrix4f3.project(float1, float2, float3, this.m_viewport, vector3f);
			return vector3f;
		}

		Vector3f uiToScene(Matrix4f matrix4f, Matrix4f matrix4f2, float float1, float float2, Vector3f vector3f) {
			UI3DScene.Plane plane = DeadBodyAtlas.RenderJob.L_uiToScene.plane;
			plane.point.set(0.0F);
			plane.normal.set(0.0F, 1.0F, 0.0F);
			UI3DScene.Ray ray = this.getCameraRay(float1, float2, matrix4f, matrix4f2, DeadBodyAtlas.RenderJob.L_uiToScene.ray);
			if (UI3DScene.intersect_ray_plane(plane, ray, vector3f) != 1) {
				vector3f.set(0.0F);
			}

			return vector3f;
		}

		public void Reset() {
			this.body.Reset();
			this.entry = null;
			if (this.animatedModel != null) {
				this.animatedModel.releaseAnimationPlayer();
				this.animatedModel = null;
			}
		}

		static final class L_getCameraRay {
			static final Matrix4f matrix4f = new Matrix4f();
			static final Vector3f ray_start = new Vector3f();
			static final Vector3f ray_end = new Vector3f();
		}

		static final class L_uiToScene {
			static final UI3DScene.Plane plane = new UI3DScene.Plane();
			static final UI3DScene.Ray ray = new UI3DScene.Ray();
		}
	}

	private final class Atlas {
		public final int ENTRY_WID;
		public final int ENTRY_HGT;
		public Texture tex;
		public final ArrayList EntryList = new ArrayList();
		public boolean clear = true;

		public Atlas(int int1, int int2, int int3, int int4) {
			this.ENTRY_WID = int3;
			this.ENTRY_HGT = int4;
			this.tex = new Texture(int1, int2, 16);
			if (DeadBodyAtlas.this.fbo == null) {
				DeadBodyAtlas.this.fbo = new TextureFBO(this.tex, false);
			}
		}

		public boolean isFull() {
			int int1 = this.tex.getWidth() / this.ENTRY_WID;
			int int2 = this.tex.getHeight() / this.ENTRY_HGT;
			return this.EntryList.size() >= int1 * int2;
		}

		public DeadBodyAtlas.AtlasEntry addBody(String string) {
			int int1 = this.tex.getWidth() / this.ENTRY_WID;
			int int2 = this.EntryList.size();
			int int3 = int2 % int1;
			int int4 = int2 / int1;
			DeadBodyAtlas.AtlasEntry atlasEntry = new DeadBodyAtlas.AtlasEntry();
			atlasEntry.atlas = this;
			atlasEntry.key = string;
			atlasEntry.x = int3 * this.ENTRY_WID;
			atlasEntry.y = int4 * this.ENTRY_HGT;
			atlasEntry.w = this.ENTRY_WID;
			atlasEntry.h = this.ENTRY_HGT;
			atlasEntry.tex = this.tex.split(string, atlasEntry.x, this.tex.getHeight() - (atlasEntry.y + this.ENTRY_HGT), atlasEntry.w, atlasEntry.h);
			atlasEntry.tex.setName(string);
			this.EntryList.add(atlasEntry);
			return atlasEntry;
		}

		public void addEntry(DeadBodyAtlas.AtlasEntry atlasEntry) {
			int int1 = this.tex.getWidth() / this.ENTRY_WID;
			int int2 = this.EntryList.size();
			int int3 = int2 % int1;
			int int4 = int2 / int1;
			atlasEntry.atlas = this;
			atlasEntry.x = int3 * this.ENTRY_WID;
			atlasEntry.y = int4 * this.ENTRY_HGT;
			atlasEntry.w = this.ENTRY_WID;
			atlasEntry.h = this.ENTRY_HGT;
			atlasEntry.tex = this.tex.split(atlasEntry.key, atlasEntry.x, this.tex.getHeight() - (atlasEntry.y + this.ENTRY_HGT), atlasEntry.w, atlasEntry.h);
			atlasEntry.tex.setName(atlasEntry.key);
			this.EntryList.add(atlasEntry);
		}

		public void Reset() {
			this.EntryList.forEach(DeadBodyAtlas.AtlasEntry::Reset);
			this.EntryList.clear();
			if (!this.tex.isDestroyed()) {
				RenderThread.invokeOnRenderContext(()->{
					GL11.glDeleteTextures(this.tex.getID());
				});
			}

			this.tex = null;
		}
	}

	private static final class ClearAtlasTexture extends TextureDraw.GenericDrawer {
		DeadBodyAtlas.Atlas m_atlas;

		ClearAtlasTexture(DeadBodyAtlas.Atlas atlas) {
			this.m_atlas = atlas;
		}

		public void render() {
			TextureFBO textureFBO = DeadBodyAtlas.instance.fbo;
			if (textureFBO != null && this.m_atlas.tex != null) {
				if (this.m_atlas.clear) {
					if (textureFBO.getTexture() != this.m_atlas.tex) {
						textureFBO.setTexture(this.m_atlas.tex);
					}

					textureFBO.startDrawing(false, false);
					GL11.glPushAttrib(2048);
					GL11.glViewport(0, 0, textureFBO.getWidth(), textureFBO.getHeight());
					GL11.glMatrixMode(5889);
					GL11.glPushMatrix();
					GL11.glLoadIdentity();
					int int1 = this.m_atlas.tex.getWidth();
					int int2 = this.m_atlas.tex.getHeight();
					GLU.gluOrtho2D(0.0F, (float)int1, (float)int2, 0.0F);
					GL11.glMatrixMode(5888);
					GL11.glPushMatrix();
					GL11.glLoadIdentity();
					GL11.glDisable(3089);
					GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
					GL11.glClear(16640);
					GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
					textureFBO.endDrawing();
					GL11.glEnable(3089);
					GL11.glMatrixMode(5889);
					GL11.glPopMatrix();
					GL11.glMatrixMode(5888);
					GL11.glPopMatrix();
					GL11.glPopAttrib();
					this.m_atlas.clear = false;
				}
			}
		}
	}

	private static final class BodyTextureDrawer extends TextureDraw.GenericDrawer {
		DeadBodyAtlas.BodyTexture bodyTexture;
		float x;
		float y;
		float r;
		float g;
		float b;
		float a;

		DeadBodyAtlas.BodyTextureDrawer init(DeadBodyAtlas.BodyTexture bodyTexture, float float1, float float2, float float3, float float4, float float5, float float6) {
			this.bodyTexture = bodyTexture;
			this.x = float1;
			this.y = float2;
			this.r = float3;
			this.g = float4;
			this.b = float5;
			this.a = float6;
			return this;
		}

		public void render() {
			DeadBodyAtlas.AtlasEntry atlasEntry = this.bodyTexture.entry;
			if (atlasEntry.ready && atlasEntry.tex.isReady()) {
				int int1 = (int)(this.x - (float)atlasEntry.w / 2.0F - atlasEntry.offsetX);
				int int2 = (int)(this.y - (float)atlasEntry.h / 2.0F - atlasEntry.offsetY);
				int int3 = atlasEntry.w;
				int int4 = atlasEntry.h;
				atlasEntry.tex.bind();
				GL11.glBegin(7);
				GL11.glColor4f(this.r, this.g, this.b, this.a);
				GL11.glTexCoord2f(atlasEntry.tex.xStart, atlasEntry.tex.yStart);
				GL11.glVertex2i(int1, int2);
				GL11.glTexCoord2f(atlasEntry.tex.xEnd, atlasEntry.tex.yStart);
				GL11.glVertex2i(int1 + int3, int2);
				GL11.glTexCoord2f(atlasEntry.tex.xEnd, atlasEntry.tex.yEnd);
				GL11.glVertex2i(int1 + int3, int2 + int4);
				GL11.glTexCoord2f(atlasEntry.tex.xStart, atlasEntry.tex.yEnd);
				GL11.glVertex2i(int1, int2 + int4);
				GL11.glEnd();
				SpriteRenderer.ringBuffer.restoreBoundTextures = true;
			}
		}

		public void postRender() {
			this.bodyTexture = null;
			DeadBodyAtlas.instance.bodyTextureDrawerPool.release((Object)this);
		}
	}
}
