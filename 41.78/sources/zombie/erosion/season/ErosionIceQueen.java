package zombie.erosion.season;

import java.util.ArrayList;
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
		this.setRoofSnow();
		for (int int1 = 0; int1 < 10; ++int1) {
			this.addSprite("vegetation_ornamental_01_" + int1, "f_bushes_2_" + (int1 + 10));
			this.addSprite("f_bushes_2_" + int1, "f_bushes_2_" + (int1 + 10));
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
		int int1;
		int int2;
		String string;
		String string2;
		for (int int3 = 1; int3 <= 5; ++int3) {
			for (int1 = 0; int1 < 128; ++int1) {
				int2 = int1;
				switch (int3) {
				case 1: 
					if (int1 >= 72 && int1 <= 79) {
						int2 = int1 - 8;
					}

					if (int1 == 112 || int1 == 114) {
						int2 = 0;
					}

					if (int1 == 113 || int1 == 115) {
						int2 = 1;
					}

					if (int1 == 116 || int1 == 118) {
						int2 = 4;
					}

					if (int1 == 117 || int1 == 119) {
						int2 = 5;
					}

					break;
				
				case 2: 
					if (int1 == 50) {
						int2 = 106;
					}

					if (int1 == 51) {
						int2 = 107;
					}

					if (int1 >= 72 && int1 <= 79) {
						int2 = int1 - 8;
					}

					if (int1 == 104 || int1 == 106) {
						int2 = 0;
					}

					if (int1 == 105 || int1 == 107) {
						int2 = 1;
					}

					if (int1 == 108 || int1 == 110) {
						int2 = 4;
					}

					if (int1 == 109 || int1 == 111) {
						int2 = 5;
					}

					break;
				
				case 3: 
					if (int1 == 72 || int1 == 74) {
						int2 = 0;
					}

					if (int1 == 73 || int1 == 75) {
						int2 = 1;
					}

					if (int1 == 76 || int1 == 78) {
						int2 = 4;
					}

					if (int1 == 77 || int1 == 79) {
						int2 = 5;
					}

					if (int1 == 102) {
						int2 = 70;
					}

					if (int1 == 103) {
						int2 = 71;
					}

					if (int1 == 104 || int1 == 106) {
						int2 = 0;
					}

					if (int1 == 105 || int1 == 107) {
						int2 = 1;
					}

					if (int1 == 108 || int1 == 110) {
						int2 = 4;
					}

					if (int1 == 109 || int1 == 111) {
						int2 = 5;
					}

					if (int1 >= 120 && int1 <= 127) {
						int2 = int1 - 16;
					}

					break;
				
				case 4: 
					if (int1 == 48) {
						int2 = 106;
					}

					if (int1 == 49) {
						int2 = 107;
					}

					if (int1 == 50) {
						int2 = 108;
					}

					if (int1 == 51) {
						int2 = 109;
					}

					if (int1 == 72 || int1 == 74) {
						int2 = 0;
					}

					if (int1 == 73 || int1 == 75) {
						int2 = 1;
					}

					if (int1 == 76 || int1 == 78) {
						int2 = 4;
					}

					if (int1 == 77 || int1 == 79) {
						int2 = 5;
					}

					if (int1 == 102) {
						int2 = 70;
					}

					if (int1 == 103) {
						int2 = 71;
					}

					if (int1 == 104 || int1 == 106) {
						int2 = 0;
					}

					if (int1 == 105 || int1 == 107) {
						int2 = 1;
					}

					if (int1 == 108 || int1 == 110) {
						int2 = 4;
					}

					if (int1 == 109 || int1 == 111) {
						int2 = 5;
					}

					break;
				
				case 5: 
					if (int1 >= 72 && int1 <= 79) {
						int2 = int1 - 8;
					}

					if (int1 == 104 || int1 == 106) {
						int2 = 0;
					}

					if (int1 == 105 || int1 == 107) {
						int2 = 1;
					}

					if (int1 == 108 || int1 == 110) {
						int2 = 4;
					}

					if (int1 == 109 || int1 == 111) {
						int2 = 5;
					}

					if (int1 >= 112 && int1 <= 119) {
						int2 = int1 - 32;
					}

				
				}

				string = "roofs_0" + int3 + "_" + int1;
				string2 = "e_roof_snow_1_" + int2;
				this.addSprite(string, string2);
			}
		}

		byte byte1 = 5;
		for (int1 = 128; int1 < 176; ++int1) {
			if (int1 != 136 && int1 != 138) {
				if (int1 != 137 && int1 != 139) {
					if (int1 != 140 && int1 != 142) {
						if (int1 != 141 && int1 != 143) {
							if (int1 < 128 || int1 > 135) {
								continue;
							}

							int2 = int1 - 128 + 96;
						} else {
							int2 = 5;
						}
					} else {
						int2 = 4;
					}
				} else {
					int2 = 1;
				}
			} else {
				int2 = 0;
			}

			string = "roofs_0" + byte1 + "_" + int1;
			string2 = "e_roof_snow_1_" + int2;
			this.addSprite(string, string2);
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
