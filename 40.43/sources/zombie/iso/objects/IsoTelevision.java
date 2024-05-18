package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.radio.ZomboidRadio;


public class IsoTelevision extends IsoWaveSignal {
	protected ArrayList screenSprites = new ArrayList();
	protected boolean defaultToNoise = false;
	private IsoSprite cacheObjectSprite;
	protected IsoDirections facing;
	private boolean hasSetupScreens;
	private boolean tickIsLightUpdate;
	private IsoTelevision.Screens currentScreen;
	private int spriteIndex;

	public String getObjectName() {
		return "Television";
	}

	public IsoTelevision(IsoCell cell) {
		super(cell);
		this.facing = IsoDirections.Max;
		this.hasSetupScreens = false;
		this.tickIsLightUpdate = false;
		this.currentScreen = IsoTelevision.Screens.OFFSCREEN;
		this.spriteIndex = 0;
	}

	public IsoTelevision(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		super(cell, square, sprite);
		this.facing = IsoDirections.Max;
		this.hasSetupScreens = false;
		this.tickIsLightUpdate = false;
		this.currentScreen = IsoTelevision.Screens.OFFSCREEN;
		this.spriteIndex = 0;
	}

	protected void init(boolean boolean1) {
		super.init(boolean1);
	}

	private void setupDefaultScreens() {
		this.hasSetupScreens = true;
		this.cacheObjectSprite = this.sprite;
		if (this.screenSprites.size() == 0) {
			for (int int1 = 16; int1 <= 64; int1 += 16) {
				IsoSprite sprite = IsoSprite.getSprite(this.getCell().SpriteManager, this.sprite.getName(), int1);
				if (sprite != null) {
					this.addTvScreenSprite(sprite);
				}
			}
		}

		this.facing = IsoDirections.Max;
		if (this.sprite != null && this.sprite.getProperties().Is("Facing")) {
			String string = this.sprite.getProperties().Val("Facing");
			byte byte1 = -1;
			switch (string.hashCode()) {
			case 69: 
				if (string.equals("E")) {
					byte1 = 3;
				}

				break;
			
			case 78: 
				if (string.equals("N")) {
					byte1 = 0;
				}

				break;
			
			case 83: 
				if (string.equals("S")) {
					byte1 = 1;
				}

				break;
			
			case 87: 
				if (string.equals("W")) {
					byte1 = 2;
				}

			
			}

			switch (byte1) {
			case 0: 
				this.facing = IsoDirections.N;
				break;
			
			case 1: 
				this.facing = IsoDirections.S;
				break;
			
			case 2: 
				this.facing = IsoDirections.W;
				break;
			
			case 3: 
				this.facing = IsoDirections.E;
			
			}
		}
	}

	public void update() {
		super.update();
		if (this.cacheObjectSprite != null && this.cacheObjectSprite != this.sprite) {
			this.hasSetupScreens = false;
			this.screenSprites.clear();
			this.currentScreen = IsoTelevision.Screens.OFFSCREEN;
			this.nextLightUpdate = 0.0F;
		}

		if (!this.hasSetupScreens) {
			this.setupDefaultScreens();
		}

		this.updateTvScreen();
	}

	protected void updateLightSource() {
		this.tickIsLightUpdate = false;
		if (this.lightSource == null) {
			this.lightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), 0.0F, 0.0F, 1.0F, this.lightSourceRadius);
			this.lightWasRemoved = true;
		}

		if (this.lightWasRemoved) {
			IsoWorld.instance.CurrentCell.addLamppost(this.lightSource);
			IsoGridSquare.RecalcLightTime = -1;
			GameTime.instance.lightSourceUpdate = 100.0F;
			this.lightWasRemoved = false;
		}

		this.lightUpdateCnt += GameTime.getInstance().getMultiplier();
		if (this.lightUpdateCnt >= this.nextLightUpdate) {
			float float1 = 300.0F;
			float float2 = 0.0F;
			if (!this.hasChatToDisplay()) {
				float2 = 0.6F;
				float1 = (float)Rand.Next(200, 400);
			} else {
				float1 = (float)Rand.Next(15, 300);
			}

			float float3 = Rand.Next(float2, 1.0F);
			this.tickIsLightUpdate = true;
			float float4 = 0.58F + 0.25F * float3;
			float float5 = Rand.Next(0.65F, 0.85F);
			int int1 = 1 + (int)((float)(this.lightSourceRadius - 1) * float3);
			IsoGridSquare.RecalcLightTime = -1;
			GameTime.instance.lightSourceUpdate = 100.0F;
			this.lightSource.setRadius(int1);
			this.lightSource.setR(float4);
			this.lightSource.setG(float5);
			this.lightSource.setB(float5);
			this.lightUpdateCnt = 0.0F;
			this.nextLightUpdate = float1;
		}
	}

	private void setScreen(IsoTelevision.Screens screens) {
		if (screens == IsoTelevision.Screens.OFFSCREEN) {
			this.currentScreen = IsoTelevision.Screens.OFFSCREEN;
			if (this.overlaySprite != null) {
				this.overlaySprite = null;
			}
		} else {
			if (this.currentScreen != screens || screens == IsoTelevision.Screens.ALTERNATESCREEN) {
				this.currentScreen = screens;
				IsoSprite sprite = null;
				switch (screens) {
				case TESTSCREEN: 
					if (this.screenSprites.size() > 0) {
						sprite = (IsoSprite)this.screenSprites.get(0);
					}

					break;
				
				case DEFAULTSCREEN: 
					if (this.screenSprites.size() > 1) {
						sprite = (IsoSprite)this.screenSprites.get(1);
					}

					break;
				
				case ALTERNATESCREEN: 
					if (this.screenSprites.size() == 3) {
						sprite = (IsoSprite)this.screenSprites.get(2);
					} else if (this.screenSprites.size() > 3) {
						++this.spriteIndex;
						if (this.spriteIndex < 2) {
							this.spriteIndex = 2;
						}

						if (this.spriteIndex > this.screenSprites.size() - 1) {
							this.spriteIndex = 2;
						}

						sprite = (IsoSprite)this.screenSprites.get(this.spriteIndex);
					}

				
				}

				this.overlaySprite = sprite;
			}
		}
	}

	protected void updateTvScreen() {
		if (this.deviceData.getIsTurnedOn() && this.screenSprites.size() > 0) {
			if (this.deviceData != null && this.deviceData.isReceivingSignal()) {
				if (this.tickIsLightUpdate || this.currentScreen != IsoTelevision.Screens.ALTERNATESCREEN) {
					this.setScreen(IsoTelevision.Screens.ALTERNATESCREEN);
				}
			} else if (!ZomboidRadio.POST_RADIO_SILENCE && (this.deviceData == null || this.deviceData.isReceivingSignal())) {
				this.setScreen(IsoTelevision.Screens.DEFAULTSCREEN);
			} else {
				this.setScreen(IsoTelevision.Screens.TESTSCREEN);
			}
		} else if (this.currentScreen != IsoTelevision.Screens.OFFSCREEN) {
			this.setScreen(IsoTelevision.Screens.OFFSCREEN);
		}
	}

	public void addTvScreenSprite(IsoSprite sprite) {
		this.screenSprites.add(sprite);
	}

	public void clearTvScreenSprites() {
		this.screenSprites.clear();
	}

	public void removeTvScreenSprite(IsoSprite sprite) {
		this.screenSprites.remove(sprite);
	}

	public void renderlast() {
		super.renderlast();
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.overlaySprite = null;
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
	}

	public boolean isFacing(IsoPlayer player) {
		if (player != null && player.isLocalPlayer()) {
			if (this.getObjectIndex() == -1) {
				return false;
			} else if (!this.square.isCanSee(player.PlayerIndex)) {
				return false;
			} else if (this.facing == IsoDirections.Max) {
				return false;
			} else {
				switch (this.facing) {
				case N: 
					if (player.y >= (float)this.square.y) {
						return false;
					}

					return player.dir == IsoDirections.SW || player.dir == IsoDirections.S || player.dir == IsoDirections.SE;
				
				case S: 
					if (player.y < (float)(this.square.y + 1)) {
						return false;
					}

					return player.dir == IsoDirections.NW || player.dir == IsoDirections.N || player.dir == IsoDirections.NE;
				
				case W: 
					if (player.x >= (float)this.square.x) {
						return false;
					}

					return player.dir == IsoDirections.SE || player.dir == IsoDirections.E || player.dir == IsoDirections.NE;
				
				case E: 
					if (player.x < (float)(this.square.x + 1)) {
						return false;
					}

					return player.dir == IsoDirections.SW || player.dir == IsoDirections.W || player.dir == IsoDirections.NW;
				
				default: 
					return false;
				
				}
			}
		} else {
			return false;
		}
	}
	private static enum Screens {

		OFFSCREEN,
		TESTSCREEN,
		DEFAULTSCREEN,
		ALTERNATESCREEN;
	}
}
