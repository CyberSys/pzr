package zombie.iso.objects;

import fmod.fmod.Audio;
import zombie.SoundManager;
import zombie.WorldSoundManager;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoSprite;


public class IsoJukebox extends IsoObject {
	private Audio JukeboxTrack = null;
	private boolean IsPlaying = false;
	private float MusicRadius = 30.0F;
	private boolean Activated = false;
	private int WorldSoundPulseRate = 150;
	private int WorldSoundPulseDelay = 0;

	public IsoJukebox(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		super(cell, square, sprite);
	}

	public String getObjectName() {
		return "Jukebox";
	}

	public IsoJukebox(IsoCell cell) {
		super(cell);
	}

	public IsoJukebox(IsoCell cell, IsoGridSquare square, String string) {
		super(cell, square, string);
		this.JukeboxTrack = null;
		this.IsPlaying = false;
		this.Activated = false;
		this.WorldSoundPulseDelay = 0;
	}

	public void SetPlaying(boolean boolean1) {
		if (this.IsPlaying != boolean1) {
			this.IsPlaying = boolean1;
			if (this.IsPlaying && this.JukeboxTrack == null) {
				String string = null;
				switch (Rand.Next(4)) {
				case 0: 
					string = "paws1";
					break;
				
				case 1: 
					string = "paws2";
					break;
				
				case 2: 
					string = "paws3";
					break;
				
				case 3: 
					string = "paws4";
				
				}

				this.JukeboxTrack = SoundManager.instance.PlaySound(string, false, 0.0F);
			}
		}
	}

	public boolean onMouseLeftClick(int int1, int int2) {
		if (IsoPlayer.getInstance() == null) {
			return false;
		} else if (IsoPlayer.getInstance().getCurrentSquare() == null) {
			return false;
		} else {
			float float1 = 0.0F;
			int int3 = Math.abs(this.square.getX() - IsoPlayer.getInstance().getCurrentSquare().getX()) + Math.abs(this.square.getY() - IsoPlayer.getInstance().getCurrentSquare().getY() + Math.abs(this.square.getZ() - IsoPlayer.getInstance().getCurrentSquare().getZ()));
			if (int3 < 4) {
				if (!this.Activated) {
					if (Core.NumJukeBoxesActive < Core.MaxJukeBoxesActive) {
						this.WorldSoundPulseDelay = 0;
						this.Activated = true;
						this.SetPlaying(true);
						++Core.NumJukeBoxesActive;
					}
				} else {
					this.WorldSoundPulseDelay = 0;
					this.SetPlaying(false);
					this.Activated = false;
					if (this.JukeboxTrack != null) {
						SoundManager.instance.StopSound(this.JukeboxTrack);
						this.JukeboxTrack.stop();
						this.JukeboxTrack = null;
					}

					--Core.NumJukeBoxesActive;
				}
			}

			return true;
		}
	}

	public void update() {
		if (IsoPlayer.getInstance() != null) {
			if (IsoPlayer.getInstance().getCurrentSquare() != null) {
				if (this.Activated) {
					float float1 = 0.0F;
					int int1 = Math.abs(this.square.getX() - IsoPlayer.getInstance().getCurrentSquare().getX()) + Math.abs(this.square.getY() - IsoPlayer.getInstance().getCurrentSquare().getY() + Math.abs(this.square.getZ() - IsoPlayer.getInstance().getCurrentSquare().getZ()));
					if ((float)int1 < this.MusicRadius) {
						this.SetPlaying(true);
						float1 = (this.MusicRadius - (float)int1) / this.MusicRadius;
					}

					if (this.JukeboxTrack != null) {
						float float2 = float1 + 0.2F;
						if (float2 > 1.0F) {
							float2 = 1.0F;
						}

						SoundManager.instance.BlendVolume(this.JukeboxTrack, float1);
						if (this.WorldSoundPulseDelay > 0) {
							--this.WorldSoundPulseDelay;
						}

						if (this.WorldSoundPulseDelay == 0) {
							WorldSoundManager.instance.addSound(IsoPlayer.getInstance(), this.square.getX(), this.square.getY(), this.square.getZ(), 70, 70, true);
							this.WorldSoundPulseDelay = this.WorldSoundPulseRate;
						}

						if (!this.JukeboxTrack.isPlaying()) {
							this.WorldSoundPulseDelay = 0;
							this.SetPlaying(false);
							this.Activated = false;
							if (this.JukeboxTrack != null) {
								SoundManager.instance.StopSound(this.JukeboxTrack);
								this.JukeboxTrack.stop();
								this.JukeboxTrack = null;
							}

							--Core.NumJukeBoxesActive;
						}
					}
				}
			}
		}
	}
}
