package zombie.core.skinnedmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import zombie.characters.IsoGameCharacter;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureFBO;
import zombie.core.textures.TextureID;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;


public class DeadBodyAtlas {
	public static final int ENTRY_WID;
	public static final int ENTRY_HGT;
	private TextureFBO fbo;
	public static DeadBodyAtlas instance;
	private HashMap EntryMap = new HashMap();
	private ArrayList AtlasList = new ArrayList();
	private static Stack JobPool;
	private ArrayList RenderJobs = new ArrayList();
	private ArrayList RenderJobsDone = new ArrayList();

	public Texture getBodyTexture(IsoDeadBody deadBody) {
		String string = this.getBodyKey(deadBody);
		if (this.EntryMap.containsKey(string)) {
			return ((DeadBodyAtlas.AtlasEntry)this.EntryMap.get(string)).tex;
		} else {
			DeadBodyAtlas.Atlas atlas = null;
			for (int int1 = 0; int1 < this.AtlasList.size(); ++int1) {
				if (!((DeadBodyAtlas.Atlas)this.AtlasList.get(int1)).isFull()) {
					atlas = (DeadBodyAtlas.Atlas)this.AtlasList.get(int1);
					break;
				}
			}

			if (atlas == null) {
				atlas = new DeadBodyAtlas.Atlas(1024, 1024);
				if (this.fbo == null) {
					return null;
				}

				this.AtlasList.add(atlas);
			}

			DeadBodyAtlas.AtlasEntry atlasEntry = atlas.addBody(string);
			this.EntryMap.put(string, atlasEntry);
			this.RenderJobs.add(DeadBodyAtlas.RenderJob.getNew().init(deadBody, atlasEntry));
			return atlasEntry.tex;
		}
	}

	private String getBodyPartKey(IsoSprite sprite) {
		if (sprite == null) {
			return "";
		} else {
			String string = ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName();
			return string + "_" + (int)(sprite.TintMod.r * 255.0F) + "_" + (int)(sprite.TintMod.g * 255.0F) + "_" + (int)(sprite.TintMod.b * 255.0F);
		}
	}

	private String getBodyKey(IsoDeadBody deadBody) {
		String string = this.getBodyPartKey(deadBody.legsSprite) + this.getBodyPartKey(deadBody.topSprite) + this.getBodyPartKey(deadBody.bottomsSprite);
		for (int int1 = 0; int1 < deadBody.extraSprites.size(); ++int1) {
			string = string + this.getBodyPartKey((IsoSprite)deadBody.extraSprites.get(int1));
		}

		return string;
	}

	public void render() {
		int int1;
		DeadBodyAtlas.RenderJob renderJob;
		for (int1 = 0; int1 < this.RenderJobsDone.size(); ++int1) {
			renderJob = (DeadBodyAtlas.RenderJob)this.RenderJobsDone.get(int1);
			if (renderJob.done == 1) {
				ModelManager.instance.Remove(renderJob.modelChr);
				renderJob.done = 2;
			} else if (renderJob.done == 2 && renderJob.modelChr.legsSprite.modelSlot == null) {
				renderJob.modelChr.setCurrent((IsoGridSquare)null);
				this.RenderJobsDone.remove(int1--);
				JobPool.push(renderJob);
			}
		}

		for (int1 = 0; int1 < this.RenderJobs.size(); ++int1) {
			renderJob = (DeadBodyAtlas.RenderJob)this.RenderJobs.get(int1);
			SpriteRenderer.instance.drawModel(renderJob.modelChr.legsSprite.modelSlot);
			SpriteRenderer.instance.toBodyAtlas(renderJob);
		}

		this.RenderJobsDone.addAll(this.RenderJobs);
		this.RenderJobs.clear();
	}

	public void renderUI() {
		if (Core.bDebug && DebugOptions.instance.DeadBodyAtlasRender.getValue()) {
			int int1 = 384 / Core.TileScale;
			int int2 = 0;
			int int3 = 0;
			for (int int4 = 0; int4 < this.AtlasList.size(); ++int4) {
				SpriteRenderer.instance.render((Texture)null, int2, int3, int1, int1, 1.0F, 1.0F, 1.0F, 0.75F);
				SpriteRenderer.instance.render(((DeadBodyAtlas.Atlas)this.AtlasList.get(int4)).tex, int2, int3, int1, int1, 1.0F, 1.0F, 1.0F, 1.0F);
				float float1 = (float)int1 / (float)((DeadBodyAtlas.Atlas)this.AtlasList.get(int4)).tex.getWidth();
				int int5;
				for (int5 = 0; int5 < ((DeadBodyAtlas.Atlas)this.AtlasList.get(int4)).tex.getWidth() / ENTRY_WID; ++int5) {
					SpriteRenderer.instance.renderline((Texture)null, (int)((float)int2 + (float)(int5 * ENTRY_WID) * float1), int3, (int)((float)int2 + (float)(int5 * ENTRY_WID) * float1), int3 + int1, 1.0F, 1.0F, 1.0F, 1.0F);
				}

				for (int5 = 0; int5 < ((DeadBodyAtlas.Atlas)this.AtlasList.get(int4)).tex.getHeight() / ENTRY_HGT; ++int5) {
					SpriteRenderer.instance.renderline((Texture)null, int2, (int)((float)int3 + (float)(int5 * ENTRY_HGT) * float1), int2 + int1, (int)((float)int3 + (float)(int5 * ENTRY_HGT) * float1), 1.0F, 1.0F, 1.0F, 1.0F);
				}

				int3 += int1;
				if (int3 + int1 > Core.getInstance().getScreenHeight()) {
					int3 = 0;
					int2 += int1;
				}
			}
		}
	}

	public void toBodyAtlas(DeadBodyAtlas.RenderJob renderJob) {
		GL11.glPushAttrib(2048);
		this.fbo.startDrawing();
		if (this.fbo.getTexture() != renderJob.entry.atlas.tex) {
			this.fbo.swapTexture(renderJob.entry.atlas.tex);
		}

		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		int int1 = renderJob.entry.atlas.tex.getWidth();
		int int2 = renderJob.entry.atlas.tex.getHeight();
		GLU.gluOrtho2D(0.0F, (float)int1, 0.0F, (float)int2);
		GL11.glMatrixMode(5888);
		GL11.glLoadIdentity();
		GL11.glEnable(3553);
		GL11.glDisable(3089);
		if (renderJob.entry.atlas.clear) {
			GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
			GL11.glClear(16640);
			GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
			renderJob.entry.atlas.clear = false;
		}

		ModelManager.instance.bitmap.getTexture().bind();
		int int3 = ModelManager.instance.bitmap.getTexture().getWidth() / 8 * Core.TileScale;
		int int4 = ModelManager.instance.bitmap.getTexture().getHeight() / 8 * Core.TileScale;
		int int5 = -32 * Core.TileScale;
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		GL11.glBegin(7);
		GL11.glVertex2i(renderJob.entry.x - (int3 - ENTRY_WID) / 2, renderJob.entry.y - (int4 - ENTRY_HGT) / 2 + int5);
		GL11.glTexCoord2f(1.0F, 0.0F);
		GL11.glVertex2i(renderJob.entry.x - (int3 - ENTRY_WID) / 2 + int3, renderJob.entry.y - (int4 - ENTRY_HGT) / 2 + int5);
		GL11.glTexCoord2f(1.0F, 1.0F);
		GL11.glVertex2i(renderJob.entry.x - (int3 - ENTRY_WID) / 2 + int3, renderJob.entry.y - (int4 - ENTRY_HGT) / 2 + int5 + int4);
		GL11.glTexCoord2f(0.0F, 1.0F);
		GL11.glVertex2i(renderJob.entry.x - (int3 - ENTRY_WID) / 2, renderJob.entry.y - (int4 - ENTRY_HGT) / 2 + int5 + int4);
		GL11.glTexCoord2f(0.0F, 0.0F);
		GL11.glEnd();
		this.fbo.endDrawing();
		GL11.glEnable(3089);
		GL11.glMatrixMode(5889);
		GL11.glPopMatrix();
		GL11.glMatrixMode(5888);
		GL11.glPopAttrib();
		renderJob.done = 1;
	}

	static  {
		ENTRY_WID = 85 * Core.TileScale;
		ENTRY_HGT = 51 * Core.TileScale;
		instance = new DeadBodyAtlas();
		JobPool = new Stack();
	}

	public static class RenderJob {
		public IsoDeadBody body;
		public DeadBodyAtlas.AtlasEntry entry;
		public IsoGameCharacter modelChr;
		public int done = 0;

		public static DeadBodyAtlas.RenderJob getNew() {
			return DeadBodyAtlas.JobPool.isEmpty() ? new DeadBodyAtlas.RenderJob() : (DeadBodyAtlas.RenderJob)DeadBodyAtlas.JobPool.pop();
		}

		private IsoSprite copySprite(IsoSprite sprite) {
			if (sprite == null) {
				return null;
			} else {
				IsoSprite sprite2 = new IsoSprite(IsoWorld.instance.spriteManager);
				sprite2.LoadFramesNoDirPageSimple(((IsoDirectionFrame)sprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName());
				sprite2.TintMod.r = sprite.TintMod.r;
				sprite2.TintMod.g = sprite.TintMod.g;
				sprite2.TintMod.b = sprite.TintMod.b;
				return sprite2;
			}
		}

		private static boolean isInteger(String string) {
			try {
				Integer.parseInt(string);
				return true;
			} catch (NumberFormatException numberFormatException) {
				return false;
			}
		}

		public DeadBodyAtlas.RenderJob init(IsoDeadBody deadBody, DeadBodyAtlas.AtlasEntry atlasEntry) {
			this.body = deadBody;
			this.entry = atlasEntry;
			if (this.modelChr == null) {
				this.modelChr = new DeadBodyAtlas.AtlasCharacter(IsoWorld.instance.CurrentCell, 0.0F, 0.0F, 0.0F);
				this.modelChr.def.Looped = false;
				this.modelChr.setbUseParts(true);
			}

			this.modelChr.sprite = this.modelChr.legsSprite = this.copySprite(deadBody.legsSprite);
			IsoDirectionFrame directionFrame = (IsoDirectionFrame)this.modelChr.legsSprite.CurrentAnim.Frames.get(0);
			String string = directionFrame.getTexture(IsoDirections.N).getName();
			if (!string.startsWith("Male_") && !string.startsWith("Kate_")) {
				this.modelChr.legsSprite.name = string.substring(0, string.indexOf("_"));
			} else {
				String[] stringArray = string.split("_");
				if (isInteger(stringArray[1]) && isInteger(stringArray[2])) {
					this.modelChr.legsSprite.name = stringArray[0] + "_" + stringArray[1];
				} else {
					this.modelChr.legsSprite.name = stringArray[0];
				}
			}

			byte byte1;
			byte byte2;
			if (directionFrame != null && directionFrame.getTexture(IsoDirections.N).getName().contains("ZombieDeath")) {
				this.modelChr.legsSprite.CurrentAnim.name = "ZombieDeath";
				byte1 = 13;
				byte2 = 14;
			} else {
				this.modelChr.legsSprite.CurrentAnim.name = "ZombieCrawl";
				byte1 = 4;
				byte2 = 11;
			}

			this.modelChr.def.parentSprite = this.modelChr.legsSprite;
			this.modelChr.def.Frame = 0.0F;
			this.modelChr.def.Looped = false;
			this.modelChr.topSprite = this.copySprite(deadBody.topSprite);
			this.modelChr.bottomsSprite = this.copySprite(deadBody.bottomsSprite);
			this.modelChr.extraSprites.clear();
			for (int int1 = 0; int1 < deadBody.extraSprites.size(); ++int1) {
				this.modelChr.extraSprites.add(this.copySprite((IsoSprite)deadBody.extraSprites.get(int1)));
			}

			this.modelChr.dir = deadBody.dir;
			this.modelChr.getVectorFromDirection(this.modelChr.angle);
			this.modelChr.x = deadBody.x;
			this.modelChr.y = deadBody.y;
			this.modelChr.z = deadBody.z;
			this.modelChr.setCurrent(deadBody.square);
			if (deadBody.square == null) {
				DebugLog.log("ERROR: body.square == null, no 3D corpse will be rendered");
			}

			ModelManager.instance.Add(this.modelChr);
			AnimationPlayer animationPlayer = this.modelChr.legsSprite.modelSlot.model.AnimPlayer;
			AnimationTrack animationTrack = (AnimationTrack)animationPlayer.Tracks.get(0);
			animationTrack.currentTimeValue = ((float)byte1 + 1.0F) / (float)byte2 * animationTrack.CurrentClip.Duration;
			animationPlayer.Update(0.0F, true, (Matrix4f)null);
			this.done = 0;
			return this;
		}
	}

	public static final class AtlasCharacter extends IsoGameCharacter {

		public AtlasCharacter(IsoCell cell, float float1, float float2, float float3) {
			super(cell, float1, float2, float3);
		}
	}

	private class Atlas {
		public Texture tex;
		public ArrayList EntryList = new ArrayList();
		public boolean clear = true;

		public Atlas(int int1, int int2) {
			try {
				try {
					TextureID.bUseCompression = false;
					this.tex = new Texture(int1, int2);
				} finally {
					TextureID.bUseCompression = TextureID.bUseCompressionOption;
				}

				if (DeadBodyAtlas.this.fbo == null) {
					DeadBodyAtlas.this.fbo = new TextureFBO(this.tex, true);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		public boolean isFull() {
			int int1 = this.tex.getWidth() / DeadBodyAtlas.ENTRY_WID;
			int int2 = this.tex.getHeight() / DeadBodyAtlas.ENTRY_HGT;
			return this.EntryList.size() >= int1 * int2;
		}

		public DeadBodyAtlas.AtlasEntry addBody(String string) {
			int int1 = this.tex.getWidth() / DeadBodyAtlas.ENTRY_WID;
			int int2 = this.EntryList.size();
			int int3 = int2 % int1;
			int int4 = int2 / int1;
			DeadBodyAtlas.AtlasEntry atlasEntry = DeadBodyAtlas.this.new AtlasEntry();
			atlasEntry.atlas = this;
			atlasEntry.key = string;
			atlasEntry.x = int3 * DeadBodyAtlas.ENTRY_WID;
			atlasEntry.y = int4 * DeadBodyAtlas.ENTRY_HGT;
			atlasEntry.w = DeadBodyAtlas.ENTRY_WID;
			atlasEntry.h = DeadBodyAtlas.ENTRY_HGT;
			atlasEntry.tex = this.tex.split(atlasEntry.x, atlasEntry.y, atlasEntry.w, atlasEntry.h);
			this.EntryList.add(atlasEntry);
			return atlasEntry;
		}
	}

	private class AtlasEntry {
		public DeadBodyAtlas.Atlas atlas;
		public String key;
		public int age;
		public int x;
		public int y;
		public int w;
		public int h;
		public Texture tex;

		private AtlasEntry() {
		}

		AtlasEntry(Object object) {
			this();
		}
	}
}
