package zombie.iso.weather.fx;

import zombie.debug.LineDrawer;
import zombie.iso.IsoCamera;


public class ParticleRectangle {
	protected boolean DEBUG_BOUNDS = false;
	private int width;
	private int height;
	private WeatherParticle[] particles;
	private int particlesToRender;
	private int particlesReqUpdCnt = 0;

	public ParticleRectangle(int int1, int int2) {
		this.width = int1;
		this.height = int2;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void SetParticles(WeatherParticle[] weatherParticleArray) {
		for (int int1 = 0; int1 < weatherParticleArray.length; ++int1) {
			weatherParticleArray[int1].setParent(this);
		}

		this.particles = weatherParticleArray;
		this.particlesToRender = weatherParticleArray.length;
	}

	public void SetParticlesStrength(float float1) {
		this.particlesToRender = (int)((float)this.particles.length * float1);
	}

	public boolean requiresUpdate() {
		return this.particlesToRender > 0 || this.particlesReqUpdCnt > 0;
	}

	public void update(float float1) {
		this.particlesReqUpdCnt = 0;
		for (int int1 = 0; int1 < this.particles.length; ++int1) {
			WeatherParticle weatherParticle = this.particles[int1];
			if (int1 < this.particlesToRender) {
				weatherParticle.alphaFadeMod.setTarget(1.0F);
			} else if (int1 >= this.particlesToRender) {
				weatherParticle.alphaFadeMod.setTarget(0.0F);
			}

			weatherParticle.update(float1);
			if (weatherParticle.renderAlpha > 0.0F) {
				++this.particlesReqUpdCnt;
			}
		}
	}

	public void render() {
		int int1 = IsoCamera.frameState.playerIndex;
		int int2 = IsoCamera.frameState.OffscreenWidth;
		int int3 = IsoCamera.frameState.OffscreenHeight;
		int int4 = (int)Math.ceil((double)(int2 / this.width)) + 2;
		int int5 = (int)Math.ceil((double)(int3 / this.height)) + 2;
		int int6;
		if (IsoCamera.frameState.OffX >= 0.0F) {
			int6 = (int)IsoCamera.frameState.OffX % this.width;
		} else {
			int6 = this.width - (int)Math.abs(IsoCamera.frameState.OffX) % this.width;
		}

		int int7;
		if (IsoCamera.frameState.OffY >= 0.0F) {
			int7 = (int)IsoCamera.frameState.OffY % this.height;
		} else {
			int7 = this.height - (int)Math.abs(IsoCamera.frameState.OffY) % this.height;
		}

		int int8 = -int6;
		int int9 = -int7;
		for (int int10 = -1; int10 < int5; ++int10) {
			for (int int11 = -1; int11 < int4; ++int11) {
				int int12 = int8 + int11 * this.width;
				int int13 = int9 + int10 * this.height;
				if (this.DEBUG_BOUNDS || IsoWeatherFX.DEBUG_BOUNDS) {
					LineDrawer.drawRect((float)int12, (float)int13, (float)this.width, (float)this.height, 0.0F, 1.0F, 0.0F, 1.0F, 1);
				}

				for (int int14 = 0; int14 < this.particles.length; ++int14) {
					WeatherParticle weatherParticle = this.particles[int14];
					if (!(weatherParticle.renderAlpha <= 0.0F) && weatherParticle.isOnScreen((float)int12, (float)int13)) {
						weatherParticle.render((float)int12, (float)int13);
						if (this.DEBUG_BOUNDS || IsoWeatherFX.DEBUG_BOUNDS) {
							LineDrawer.drawRect((float)(int12 + weatherParticle.bounds.getX()), (float)(int13 + weatherParticle.bounds.getY()), (float)weatherParticle.bounds.getWidth(), (float)weatherParticle.bounds.getHeight(), 0.0F, 0.0F, 1.0F, 0.5F, 1);
						}
					}
				}
			}
		}
	}
}
