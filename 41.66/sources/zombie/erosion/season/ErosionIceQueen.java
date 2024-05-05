package zombie.erosion.season;

import java.util.ArrayList;
import zombie.core.Core;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;


public final class ErosionIceQueen {
	public static ErosionIceQueen instance;
	private final ArrayList sprites = new ArrayList();
	private final IsoSpriteManager SprMngr;
	private boolean snowState;

	public void addSprite(String string, String string2) {
		IsoSprite sprite = this.SprMngr.getSprite(string);
		IsoSprite sprite2 = this.SprMngr.getSprite(string2);
		if (sprite != null && sprite2 != null) {
			sprite.setName(string);
			this.sprites.add(new ErosionIceQueen.Sprite(sprite, string, string2));
		}
	}

	public void setSnow(boolean boolean1) {
		if (this.snowState != boolean1) {
			this.snowState = boolean1;
			for (int int1 = 0; int1 < this.sprites.size(); ++int1) {
				ErosionIceQueen.Sprite sprite = (ErosionIceQueen.Sprite)this.sprites.get(int1);
				sprite.sprite.ReplaceCurrentAnimFrames(this.snowState ? sprite.winter : sprite.normal);
			}
		}
	}

	public ErosionIceQueen(IsoSpriteManager spriteManager) {
		instance = this;
		this.SprMngr = spriteManager;
		int int1;
		if (Core.TileScale == 1) {
			this.setRoofSnowOneX();
			for (int1 = 0; int1 < 10; ++int1) {
				this.addSprite("vegetation_ornamental_01_" + int1, "vegetation_ornamental_01_" + (int1 + 48));
			}
		} else {
			this.setRoofSnow();
			for (int1 = 0; int1 < 10; ++int1) {
				this.addSprite("vegetation_ornamental_01_" + int1, "f_bushes_2_" + (int1 + 10));
				this.addSprite("f_bushes_2_" + int1, "f_bushes_2_" + (int1 + 10));
			}
		}
	}

	private void setRoofSnowA() {
		for (int int1 = 0; int1 < 128; ++int1) {
			String string = "e_roof_snow_1_" + int1;
			for (int int2 = 1; int2 <= 5; ++int2) {
				String string2 = "roofs_0" + int2 + "_" + int1;
				this.addSprite(string2, string);
			}
		}
	}

	private void setRoofSnow() {
		for (int int1 = 1; int1 <= 5; ++int1) {
			for (int int2 = 0; int2 < 128; ++int2) {
				int int3 = int2;
				switch (int1) {
				case 1: 
					if (int2 >= 72 && int2 <= 79) {
						int3 = int2 - 8;
					}

					if (int2 == 112 || int2 == 114) {
						int3 = 0;
					}

					if (int2 == 113 || int2 == 115) {
						int3 = 1;
					}

					if (int2 == 116 || int2 == 118) {
						int3 = 4;
					}

					if (int2 == 117 || int2 == 119) {
						int3 = 5;
					}

					break;
				
				case 2: 
					if (int2 == 50) {
						int3 = 106;
					}

					if (int2 == 51) {
						int3 = 107;
					}

					if (int2 >= 72 && int2 <= 79) {
						int3 = int2 - 8;
					}

					if (int2 == 104 || int2 == 106) {
						int3 = 0;
					}

					if (int2 == 105 || int2 == 107) {
						int3 = 1;
					}

					if (int2 == 108 || int2 == 110) {
						int3 = 4;
					}

					if (int2 == 109 || int2 == 111) {
						int3 = 5;
					}

					break;
				
				case 3: 
					if (int2 == 72 || int2 == 74) {
						int3 = 0;
					}

					if (int2 == 73 || int2 == 75) {
						int3 = 1;
					}

					if (int2 == 76 || int2 == 78) {
						int3 = 4;
					}

					if (int2 == 77 || int2 == 79) {
						int3 = 5;
					}

					if (int2 == 102) {
						int3 = 70;
					}

					if (int2 == 103) {
						int3 = 71;
					}

					if (int2 == 104 || int2 == 106) {
						int3 = 0;
					}

					if (int2 == 105 || int2 == 107) {
						int3 = 1;
					}

					if (int2 == 108 || int2 == 110) {
						int3 = 4;
					}

					if (int2 == 109 || int2 == 111) {
						int3 = 5;
					}

					if (int2 >= 120 && int2 <= 127) {
						int3 = int2 - 16;
					}

					break;
				
				case 4: 
					if (int2 == 48) {
						int3 = 106;
					}

					if (int2 == 49) {
						int3 = 107;
					}

					if (int2 == 50) {
						int3 = 108;
					}

					if (int2 == 51) {
						int3 = 109;
					}

					if (int2 == 72 || int2 == 74) {
						int3 = 0;
					}

					if (int2 == 73 || int2 == 75) {
						int3 = 1;
					}

					if (int2 == 76 || int2 == 78) {
						int3 = 4;
					}

					if (int2 == 77 || int2 == 79) {
						int3 = 5;
					}

					if (int2 == 102) {
						int3 = 70;
					}

					if (int2 == 103) {
						int3 = 71;
					}

					if (int2 == 104 || int2 == 106) {
						int3 = 0;
					}

					if (int2 == 105 || int2 == 107) {
						int3 = 1;
					}

					if (int2 == 108 || int2 == 110) {
						int3 = 4;
					}

					if (int2 == 109 || int2 == 111) {
						int3 = 5;
					}

					break;
				
				case 5: 
					if (int2 == 104 || int2 == 106) {
						int3 = 0;
					}

					if (int2 == 105 || int2 == 107) {
						int3 = 1;
					}

					if (int2 == 108 || int2 == 110) {
						int3 = 4;
					}

					if (int2 == 109 || int2 == 111) {
						int3 = 5;
					}

				
				}

				String string = "roofs_0" + int1 + "_" + int2;
				String string2 = "e_roof_snow_1_" + int3;
				this.addSprite(string, string2);
			}
		}
	}

	private void setRoofSnowOneX() {
		for (int int1 = 1; int1 <= 5; ++int1) {
			for (int int2 = 0; int2 < 128; ++int2) {
				int int3 = int2;
				switch (int1) {
				case 1: 
					if (int2 >= 96 && int2 <= 98) {
						int3 = int2 - 16;
					}

					if (int2 == 99) {
						int3 = int2 - 19;
					}

					if (int2 == 100) {
						int3 = int2 - 13;
					}

					if (int2 >= 101 && int2 <= 103) {
						int3 = int2 - 16;
					}

					if (int2 >= 112 && int2 <= 113) {
						int3 = int2 - 112;
					}

					if (int2 >= 114 && int2 <= 115) {
						int3 = int2 - 114;
					}

					if (int2 == 116 || int2 == 118) {
						int3 = 5;
					}

					if (int2 == 117 || int2 == 119) {
						int3 = 4;
					}

					break;
				
				case 2: 
					if (int2 >= 96 && int2 <= 98) {
						int3 = int2 - 16;
					}

					if (int2 == 99) {
						int3 = int2 - 19;
					}

					if (int2 == 100) {
						int3 = int2 - 13;
					}

					if (int2 >= 101 && int2 <= 103) {
						int3 = int2 - 16;
					}

					if (int2 >= 104 && int2 <= 105) {
						int3 = int2 - 104;
					}

					if (int2 >= 106 && int2 <= 107) {
						int3 = int2 - 106;
					}

					if (int2 >= 108 && int2 <= 109) {
						int3 = int2 - 104;
					}

					if (int2 >= 110 && int2 <= 111) {
						int3 = int2 - 106;
					}

					break;
				
				case 3: 
					if (int2 >= 18 && int2 <= 19) {
						int3 = int2 - 12;
					}

					if (int2 >= 50 && int2 <= 51) {
						int3 = int2 - 44;
					}

					if (int2 >= 72 && int2 <= 73) {
						int3 = int2 - 72;
					}

					if (int2 >= 74 && int2 <= 75) {
						int3 = int2 - 74;
					}

					if (int2 >= 76 && int2 <= 77) {
						int3 = int2 - 72;
					}

					if (int2 >= 78 && int2 <= 79) {
						int3 = int2 - 74;
					}

					if (int2 >= 102 && int2 <= 103) {
						int3 = int2 - 88;
					}

					if (int2 >= 122 && int2 <= 125) {
						int3 = int2 - 16;
					}

					break;
				
				case 4: 
					if (int2 >= 18 && int2 <= 19) {
						int3 = int2 - 12;
					}

					break;
				
				case 5: 
					if (int2 >= 72 && int2 <= 74) {
						int3 = int2 + 8;
					}

					if (int2 == 75) {
						int3 = int2 + 7;
					}

					if (int2 == 76) {
						int3 = int2 + 11;
					}

					if (int2 >= 77 && int2 <= 79) {
						int3 = int2 + 8;
					}

					if (int2 >= 112 && int2 <= 113) {
						int3 = int2 - 112;
					}

					if (int2 >= 114 && int2 <= 115) {
						int3 = int2 - 114;
					}

					if (int2 == 116 || int2 == 118) {
						int3 = 5;
					}

					if (int2 == 117 || int2 == 119) {
						int3 = 4;
					}

				
				}

				String string = "roofs_0" + int1 + "_" + int2;
				String string2 = "e_roof_snow_1_" + int3;
				this.addSprite(string, string2);
			}
		}
	}

	public static void Reset() {
		if (instance != null) {
			instance.sprites.clear();
			instance = null;
		}
	}

	private static class Sprite {
		public IsoSprite sprite;
		public String normal;
		public String winter;

		public Sprite(IsoSprite sprite, String string, String string2) {
			this.sprite = sprite;
			this.normal = string;
			this.winter = string2;
		}
	}
}
