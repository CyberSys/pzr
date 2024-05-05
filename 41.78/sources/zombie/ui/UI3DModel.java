package zombie.ui;

import se.krka.kahlua.vm.KahluaTable;
import zombie.characters.IsoGameCharacter;
import zombie.characters.SurvivorDesc;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.skinnedmodel.advancedanimation.AnimatedModel;
import zombie.core.skinnedmodel.population.IClothingItemListener;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.textures.TextureDraw;
import zombie.iso.IsoDirections;
import zombie.util.StringUtils;


public final class UI3DModel extends UIElement implements IClothingItemListener {
	private final AnimatedModel animatedModel = new AnimatedModel();
	private IsoDirections dir;
	private boolean bDoExt;
	private long nextExt;
	private final UI3DModel.Drawer[] drawers;
	private float zoom;
	private float yOffset;
	private float xOffset;

	public UI3DModel(KahluaTable kahluaTable) {
		super(kahluaTable);
		this.dir = IsoDirections.E;
		this.bDoExt = false;
		this.nextExt = -1L;
		this.drawers = new UI3DModel.Drawer[3];
		this.zoom = 0.0F;
		this.yOffset = 0.0F;
		this.xOffset = 0.0F;
		for (int int1 = 0; int1 < this.drawers.length; ++int1) {
			this.drawers[int1] = new UI3DModel.Drawer();
		}

		if (OutfitManager.instance != null) {
			OutfitManager.instance.addClothingItemListener(this);
		}
	}

	public void render() {
		if (this.isVisible()) {
			super.render();
			if (this.Parent == null || this.Parent.maxDrawHeight == -1 || !((double)this.Parent.maxDrawHeight <= this.y)) {
				if (this.bDoExt) {
					long long1 = System.currentTimeMillis();
					if (this.nextExt < 0L) {
						this.nextExt = long1 + (long)Rand.Next(5000, 10000);
					}

					if (this.nextExt < long1) {
						this.animatedModel.getActionContext().reportEvent("EventDoExt");
						this.animatedModel.setVariable("Ext", (float)(Rand.Next(0, 6) + 1));
						this.nextExt = -1L;
					}
				}

				this.animatedModel.update();
				UI3DModel.Drawer drawer = this.drawers[SpriteRenderer.instance.getMainStateIndex()];
				drawer.init(this.getAbsoluteX().intValue(), this.getAbsoluteY().intValue());
				SpriteRenderer.instance.drawGeneric(drawer);
			}
		}
	}

	public void setDirection(IsoDirections directions) {
		this.dir = directions;
		this.animatedModel.setAngle(directions.ToVector());
	}

	public IsoDirections getDirection() {
		return this.dir;
	}

	public void setAnimate(boolean boolean1) {
		this.animatedModel.setAnimate(boolean1);
	}

	public void setAnimSetName(String string) {
		this.animatedModel.setAnimSetName(string);
	}

	public void setDoRandomExtAnimations(boolean boolean1) {
		this.bDoExt = boolean1;
	}

	public void setIsometric(boolean boolean1) {
		this.animatedModel.setIsometric(boolean1);
	}

	public void setOutfitName(String string, boolean boolean1, boolean boolean2) {
		this.animatedModel.setOutfitName(string, boolean1, boolean2);
	}

	public void setCharacter(IsoGameCharacter gameCharacter) {
		this.animatedModel.setCharacter(gameCharacter);
	}

	public void setSurvivorDesc(SurvivorDesc survivorDesc) {
		this.animatedModel.setSurvivorDesc(survivorDesc);
	}

	public void setState(String string) {
		this.animatedModel.setState(string);
	}

	public void reportEvent(String string) {
		if (!StringUtils.isNullOrWhitespace(string)) {
			this.animatedModel.getActionContext().reportEvent(string);
		}
	}

	public void clothingItemChanged(String string) {
		this.animatedModel.clothingItemChanged(string);
	}

	public void setZoom(float float1) {
		this.zoom = float1;
	}

	public void setYOffset(float float1) {
		this.yOffset = float1;
	}

	public void setXOffset(float float1) {
		this.xOffset = float1;
	}

	private final class Drawer extends TextureDraw.GenericDrawer {
		int absX;
		int absY;
		float m_animPlayerAngle;
		boolean bRendered;

		public void init(int int1, int int2) {
			this.absX = int1;
			this.absY = int2;
			this.m_animPlayerAngle = UI3DModel.this.animatedModel.getAnimationPlayer().getRenderedAngle();
			this.bRendered = false;
			float float1 = UI3DModel.this.animatedModel.isIsometric() ? -0.45F : -0.5F;
			if (UI3DModel.this.yOffset != 0.0F) {
				float1 = UI3DModel.this.yOffset;
			}

			UI3DModel.this.animatedModel.setOffset(UI3DModel.this.xOffset, float1, 0.0F);
			UI3DModel.this.animatedModel.renderMain();
		}

		public void render() {
			float float1 = UI3DModel.this.animatedModel.isIsometric() ? 22.0F : 25.0F;
			if (UI3DModel.this.zoom > 0.0F) {
				float1 -= UI3DModel.this.zoom;
			}

			UI3DModel.this.animatedModel.DoRender(this.absX, Core.height - this.absY - (int)UI3DModel.this.height, (int)UI3DModel.this.width, (int)UI3DModel.this.height, float1, this.m_animPlayerAngle);
			this.bRendered = true;
		}

		public void postRender() {
			UI3DModel.this.animatedModel.postRender(this.bRendered);
		}
	}
}
