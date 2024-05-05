package zombie.iso;

import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;


public class SearchMode {
	private static SearchMode instance;
	private float fadeTime = 1.0F;
	private SearchMode.PlayerSearchMode[] plrModes = new SearchMode.PlayerSearchMode[4];

	public static SearchMode getInstance() {
		if (instance == null) {
			instance = new SearchMode();
		}

		return instance;
	}

	private SearchMode() {
		for (int int1 = 0; int1 < this.plrModes.length; ++int1) {
			this.plrModes[int1] = new SearchMode.PlayerSearchMode(int1, this);
			this.plrModes[int1].blur.setTargets(1.0F, 1.0F);
			this.plrModes[int1].desat.setTargets(0.85F, 0.85F);
			this.plrModes[int1].radius.setTargets(4.0F, 4.0F);
			this.plrModes[int1].darkness.setTargets(0.0F, 0.0F);
			this.plrModes[int1].gradientWidth.setTargets(4.0F, 4.0F);
		}
	}

	public SearchMode.PlayerSearchMode getSearchModeForPlayer(int int1) {
		return this.plrModes[int1];
	}

	public float getFadeTime() {
		return this.fadeTime;
	}

	public void setFadeTime(float float1) {
		this.fadeTime = float1;
	}

	public boolean isOverride(int int1) {
		return this.plrModes[int1].override;
	}

	public void setOverride(int int1, boolean boolean1) {
		this.plrModes[int1].override = boolean1;
	}

	public SearchMode.SearchModeFloat getRadius(int int1) {
		return this.plrModes[int1].radius;
	}

	public SearchMode.SearchModeFloat getGradientWidth(int int1) {
		return this.plrModes[int1].gradientWidth;
	}

	public SearchMode.SearchModeFloat getBlur(int int1) {
		return this.plrModes[int1].blur;
	}

	public SearchMode.SearchModeFloat getDesat(int int1) {
		return this.plrModes[int1].desat;
	}

	public SearchMode.SearchModeFloat getDarkness(int int1) {
		return this.plrModes[int1].darkness;
	}

	public boolean isEnabled(int int1) {
		return this.plrModes[int1].enabled;
	}

	public void setEnabled(int int1, boolean boolean1) {
		SearchMode.PlayerSearchMode playerSearchMode = this.plrModes[int1];
		if (boolean1 && !playerSearchMode.enabled) {
			playerSearchMode.enabled = true;
			this.FadeIn(int1);
		} else if (!boolean1 && playerSearchMode.enabled) {
			playerSearchMode.enabled = false;
			this.FadeOut(int1);
		}
	}

	private void FadeIn(int int1) {
		SearchMode.PlayerSearchMode playerSearchMode = this.plrModes[int1];
		playerSearchMode.timer = Math.max(playerSearchMode.timer, 0.0F);
		playerSearchMode.doFadeIn = true;
		playerSearchMode.doFadeOut = false;
	}

	private void FadeOut(int int1) {
		SearchMode.PlayerSearchMode playerSearchMode = this.plrModes[int1];
		playerSearchMode.timer = Math.min(playerSearchMode.timer, this.fadeTime);
		playerSearchMode.doFadeIn = false;
		playerSearchMode.doFadeOut = true;
	}

	public void update() {
		for (int int1 = 0; int1 < this.plrModes.length; ++int1) {
			SearchMode.PlayerSearchMode playerSearchMode = this.plrModes[int1];
			playerSearchMode.update();
		}
	}

	public static void reset() {
		instance = null;
	}

	public static class PlayerSearchMode {
		private final int plrIndex;
		private final SearchMode parent;
		private boolean override = false;
		private boolean enabled = false;
		private final SearchMode.SearchModeFloat radius = new SearchMode.SearchModeFloat(0.0F, 50.0F, 1.0F);
		private final SearchMode.SearchModeFloat gradientWidth = new SearchMode.SearchModeFloat(0.0F, 20.0F, 1.0F);
		private final SearchMode.SearchModeFloat blur = new SearchMode.SearchModeFloat(0.0F, 1.0F, 0.01F);
		private final SearchMode.SearchModeFloat desat = new SearchMode.SearchModeFloat(0.0F, 1.0F, 0.01F);
		private final SearchMode.SearchModeFloat darkness = new SearchMode.SearchModeFloat(0.0F, 1.0F, 0.01F);
		private float timer;
		private boolean doFadeOut;
		private boolean doFadeIn;

		public PlayerSearchMode(int int1, SearchMode searchMode) {
			this.plrIndex = int1;
			this.parent = searchMode;
		}

		public boolean isShaderEnabled() {
			return this.enabled || this.doFadeIn || this.doFadeOut;
		}

		private boolean isPlayerExterior() {
			IsoPlayer player = IsoPlayer.players[this.plrIndex];
			return player != null && player.getCurrentSquare() != null && !player.getCurrentSquare().isInARoom();
		}

		public float getShaderBlur() {
			return this.isPlayerExterior() ? this.blur.getExterior() : this.blur.getInterior();
		}

		public float getShaderDesat() {
			return this.isPlayerExterior() ? this.desat.getExterior() : this.desat.getInterior();
		}

		public float getShaderRadius() {
			return this.isPlayerExterior() ? this.radius.getExterior() : this.radius.getInterior();
		}

		public float getShaderGradientWidth() {
			return this.isPlayerExterior() ? this.gradientWidth.getExterior() : this.gradientWidth.getInterior();
		}

		public float getShaderDarkness() {
			return this.isPlayerExterior() ? this.darkness.getExterior() : this.darkness.getInterior();
		}

		public SearchMode.SearchModeFloat getBlur() {
			return this.blur;
		}

		public SearchMode.SearchModeFloat getDesat() {
			return this.desat;
		}

		public SearchMode.SearchModeFloat getRadius() {
			return this.radius;
		}

		public SearchMode.SearchModeFloat getGradientWidth() {
			return this.gradientWidth;
		}

		public SearchMode.SearchModeFloat getDarkness() {
			return this.darkness;
		}

		private void update() {
			if (!this.override) {
				float float1;
				if (this.doFadeIn) {
					this.timer += GameTime.getInstance().getTimeDelta();
					this.timer = PZMath.clamp(this.timer, 0.0F, this.parent.fadeTime);
					float1 = PZMath.clamp(this.timer / this.parent.fadeTime, 0.0F, 1.0F);
					this.blur.update(float1);
					this.desat.update(float1);
					this.radius.update(float1);
					this.darkness.update(float1);
					this.gradientWidth.equalise();
					if (this.timer >= this.parent.fadeTime) {
						this.doFadeIn = false;
					}
				} else if (this.doFadeOut) {
					this.timer -= GameTime.getInstance().getTimeDelta();
					this.timer = PZMath.clamp(this.timer, 0.0F, this.parent.fadeTime);
					float1 = PZMath.clamp(this.timer / this.parent.fadeTime, 0.0F, 1.0F);
					this.blur.update(float1);
					this.desat.update(float1);
					this.radius.update(float1);
					this.darkness.update(float1);
					this.gradientWidth.equalise();
					if (this.timer <= 0.0F) {
						this.doFadeOut = false;
					}
				} else {
					if (this.enabled) {
						this.blur.equalise();
						this.desat.equalise();
						this.radius.equalise();
						this.darkness.equalise();
						this.gradientWidth.equalise();
					} else {
						this.blur.reset();
						this.desat.reset();
						this.radius.reset();
						this.darkness.reset();
						this.gradientWidth.equalise();
					}
				}
			}
		}
	}

	public static class SearchModeFloat {
		private final float min;
		private final float max;
		private final float stepsize;
		private float exterior;
		private float targetExterior;
		private float interior;
		private float targetInterior;

		private SearchModeFloat(float float1, float float2, float float3) {
			this.min = float1;
			this.max = float2;
			this.stepsize = float3;
		}

		public void set(float float1, float float2, float float3, float float4) {
			this.setExterior(float1);
			this.setTargetExterior(float2);
			this.setInterior(float3);
			this.setTargetInterior(float4);
		}

		public void setTargets(float float1, float float2) {
			this.setTargetExterior(float1);
			this.setTargetInterior(float2);
		}

		public float getExterior() {
			return this.exterior;
		}

		public void setExterior(float float1) {
			this.exterior = float1;
		}

		public float getTargetExterior() {
			return this.targetExterior;
		}

		public void setTargetExterior(float float1) {
			this.targetExterior = float1;
		}

		public float getInterior() {
			return this.interior;
		}

		public void setInterior(float float1) {
			this.interior = float1;
		}

		public float getTargetInterior() {
			return this.targetInterior;
		}

		public void setTargetInterior(float float1) {
			this.targetInterior = float1;
		}

		public void update(float float1) {
			this.exterior = float1 * this.targetExterior;
			this.interior = float1 * this.targetInterior;
		}

		public void equalise() {
			if (!PZMath.equal(this.exterior, this.targetExterior, 0.001F)) {
				this.exterior = PZMath.lerp(this.exterior, this.targetExterior, 0.01F);
			} else {
				this.exterior = this.targetExterior;
			}

			if (!PZMath.equal(this.interior, this.targetInterior, 0.001F)) {
				this.interior = PZMath.lerp(this.interior, this.targetInterior, 0.01F);
			} else {
				this.interior = this.targetInterior;
			}
		}

		public void reset() {
			this.exterior = 0.0F;
			this.interior = 0.0F;
		}

		public float getMin() {
			return this.min;
		}

		public float getMax() {
			return this.max;
		}

		public float getStepsize() {
			return this.stepsize;
		}
	}
}
