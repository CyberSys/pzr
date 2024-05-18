package zombie.iso.sprite;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.core.textures.PaletteManager;
import zombie.core.textures.Texture;
import zombie.core.textures.TexturePackPage;
import zombie.network.GameServer;
import zombie.network.ServerGUI;


public class IsoAnim {
	public static HashMap GlobalAnimMap = new HashMap();
	public short FinishUnloopedOnFrame = 0;
	public short FrameDelay = 0;
	public short LastFrame = 0;
	public ArrayList Frames = new ArrayList(8);
	public String name;
	boolean looped = true;
	public int ID = 0;
	private static ThreadLocal tlsStrBuf = new ThreadLocal(){
    
    protected StringBuffer initialValue() {
        return new StringBuffer();
    }
};
	public IsoDirectionFrame[] FramesArray = new IsoDirectionFrame[0];

	public static void DisposeAll() {
		GlobalAnimMap.clear();
	}

	void LoadExtraFrame(String string, String string2, int int1) {
		this.name = string2;
		String string3 = string + "_";
		String string4 = "_" + string2 + "_";
		Integer integer = new Integer(int1);
		IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string3 + "8" + string4 + integer.toString() + ".png"), Texture.getSharedTexture(string3 + "9" + string4 + integer.toString() + ".png"), Texture.getSharedTexture(string3 + "6" + string4 + integer.toString() + ".png"), Texture.getSharedTexture(string3 + "3" + string4 + integer.toString() + ".png"), Texture.getSharedTexture(string3 + "2" + string4 + integer.toString() + ".png"));
		this.Frames.add(directionFrame);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}

	public void LoadFramesReverseAltName(String string, String string2, String string3, int int1) {
		this.name = string3;
		StringBuffer stringBuffer = (StringBuffer)tlsStrBuf.get();
		stringBuffer.setLength(0);
		stringBuffer.append(string);
		stringBuffer.append("_%_");
		stringBuffer.append(string2);
		stringBuffer.append("_^");
		int int2 = stringBuffer.lastIndexOf("^");
		int int3 = stringBuffer.indexOf("_%_") + 1;
		stringBuffer.setCharAt(int3, '9');
		stringBuffer.setCharAt(int2, '0');
		if (GameServer.bServer && !ServerGUI.isCreated()) {
			for (int int4 = 0; int4 < int1; ++int4) {
				this.Frames.add(new IsoDirectionFrame((Texture)null));
			}

			this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
			this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
		}

		Texture texture = Texture.getSharedTexture(stringBuffer.toString());
		if (texture != null) {
			for (int int5 = 0; int5 < int1; ++int5) {
				if (int5 == 10) {
					stringBuffer.setLength(0);
					stringBuffer.append(string);
					stringBuffer.append("_1_");
					stringBuffer.append(string2);
					stringBuffer.append("_10");
				}

				Integer integer = int5;
				IsoDirectionFrame directionFrame = null;
				String string4 = integer.toString();
				String string5;
				String string6;
				String string7;
				String string8;
				String string9;
				if (texture == null) {
					stringBuffer.setCharAt(int3, '8');
					try {
						stringBuffer.setCharAt(int2, integer.toString().charAt(0));
					} catch (Exception exception) {
						this.LoadFramesReverseAltName(string, string2, string3, int1);
					}

					string9 = stringBuffer.toString();
					stringBuffer.setCharAt(int3, '9');
					string5 = stringBuffer.toString();
					stringBuffer.setCharAt(int3, '6');
					string6 = stringBuffer.toString();
					stringBuffer.setCharAt(int3, '3');
					string7 = stringBuffer.toString();
					stringBuffer.setCharAt(int3, '2');
					string8 = stringBuffer.toString();
					directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string9), Texture.getSharedTexture(string5), Texture.getSharedTexture(string6), Texture.getSharedTexture(string7), Texture.getSharedTexture(string8));
				} else {
					stringBuffer.setCharAt(int3, '9');
					for (int int6 = 0; int6 < string4.length(); ++int6) {
						stringBuffer.setCharAt(int2 + int6, string4.charAt(int6));
					}

					string9 = stringBuffer.toString();
					stringBuffer.setCharAt(int3, '6');
					string5 = stringBuffer.toString();
					stringBuffer.setCharAt(int3, '3');
					string6 = stringBuffer.toString();
					stringBuffer.setCharAt(int3, '2');
					string7 = stringBuffer.toString();
					stringBuffer.setCharAt(int3, '1');
					string8 = stringBuffer.toString();
					stringBuffer.setCharAt(int3, '4');
					String string10 = stringBuffer.toString();
					stringBuffer.setCharAt(int3, '7');
					String string11 = stringBuffer.toString();
					stringBuffer.setCharAt(int3, '8');
					String string12 = stringBuffer.toString();
					directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string9), Texture.getSharedTexture(string5), Texture.getSharedTexture(string6), Texture.getSharedTexture(string7), Texture.getSharedTexture(string8), Texture.getSharedTexture(string10), Texture.getSharedTexture(string11), Texture.getSharedTexture(string12));
				}

				this.Frames.add(0, directionFrame);
			}

			this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
			this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
		}
	}

	public void LoadFrames(String string, String string2, int int1) {
		this.name = string2;
		StringBuffer stringBuffer = (StringBuffer)tlsStrBuf.get();
		stringBuffer.setLength(0);
		stringBuffer.append(string);
		stringBuffer.append("_%_");
		stringBuffer.append(string2);
		stringBuffer.append("_^");
		int int2 = stringBuffer.indexOf("_%_") + 1;
		int int3 = stringBuffer.lastIndexOf("^");
		stringBuffer.setCharAt(int2, '9');
		stringBuffer.setCharAt(int3, '0');
		if (GameServer.bServer && !ServerGUI.isCreated()) {
			for (int int4 = 0; int4 < int1; ++int4) {
				this.Frames.add(new IsoDirectionFrame((Texture)null));
			}

			this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		}

		Texture texture = Texture.getSharedTexture(stringBuffer.toString());
		if (texture != null) {
			for (int int5 = 0; int5 < int1; ++int5) {
				if (int5 % 10 == 0 && int5 > 0) {
					stringBuffer.setLength(0);
					stringBuffer.append(string);
					stringBuffer.append("_%_");
					stringBuffer.append(string2);
					stringBuffer.append("_^_");
					int2 = stringBuffer.indexOf("_%_") + 1;
					int3 = stringBuffer.lastIndexOf("^");
				}

				Integer integer = int5;
				IsoDirectionFrame directionFrame = null;
				String string3 = integer.toString();
				int int6;
				String string4;
				String string5;
				String string6;
				String string7;
				String string8;
				if (texture != null) {
					stringBuffer.setCharAt(int2, '9');
					for (int6 = 0; int6 < string3.length(); ++int6) {
						stringBuffer.setCharAt(int3 + int6, string3.charAt(int6));
					}

					string8 = stringBuffer.toString();
					stringBuffer.setCharAt(int2, '6');
					string4 = stringBuffer.toString();
					stringBuffer.setCharAt(int2, '3');
					string5 = stringBuffer.toString();
					stringBuffer.setCharAt(int2, '2');
					string6 = stringBuffer.toString();
					stringBuffer.setCharAt(int2, '1');
					string7 = stringBuffer.toString();
					stringBuffer.setCharAt(int2, '4');
					String string9 = stringBuffer.toString();
					stringBuffer.setCharAt(int2, '7');
					String string10 = stringBuffer.toString();
					stringBuffer.setCharAt(int2, '8');
					String string11 = stringBuffer.toString();
					directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string8), Texture.getSharedTexture(string4), Texture.getSharedTexture(string5), Texture.getSharedTexture(string6), Texture.getSharedTexture(string7), Texture.getSharedTexture(string9), Texture.getSharedTexture(string10), Texture.getSharedTexture(string11));
				} else {
					try {
						stringBuffer.setCharAt(int2, '8');
					} catch (Exception exception) {
						this.LoadFrames(string, string2, int1);
					}

					for (int6 = 0; int6 < string3.length(); ++int6) {
						try {
							stringBuffer.setCharAt(int3 + int6, integer.toString().charAt(int6));
						} catch (Exception exception2) {
							this.LoadFrames(string, string2, int1);
						}
					}

					string8 = stringBuffer.toString();
					stringBuffer.setCharAt(int2, '9');
					string4 = stringBuffer.toString();
					stringBuffer.setCharAt(int2, '6');
					string5 = stringBuffer.toString();
					stringBuffer.setCharAt(int2, '3');
					string6 = stringBuffer.toString();
					stringBuffer.setCharAt(int2, '2');
					string7 = stringBuffer.toString();
					directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string8), Texture.getSharedTexture(string4), Texture.getSharedTexture(string5), Texture.getSharedTexture(string6), Texture.getSharedTexture(string7));
				}

				this.Frames.add(directionFrame);
			}

			this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
			this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
		}
	}

	public void LoadFramesUseOtherFrame(String string, String string2, String string3, String string4, int int1, String string5) {
		this.name = string3;
		String string6 = string4 + "_" + string2 + "_";
		String string7 = "_";
		String string8 = "";
		if (string5 != null) {
			string8 = "_" + string5;
		}

		for (int int2 = 0; int2 < 1; ++int2) {
			Integer integer = new Integer(int1);
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string6 + "8" + string7 + integer.toString() + string8 + ".png"), Texture.getSharedTexture(string6 + "9" + string7 + integer.toString() + string8 + ".png"), Texture.getSharedTexture(string6 + "6" + string7 + integer.toString() + string8 + ".png"), Texture.getSharedTexture(string6 + "3" + string7 + integer.toString() + string8 + ".png"), Texture.getSharedTexture(string6 + "2" + string7 + integer.toString() + string8 + ".png"));
			this.Frames.add(directionFrame);
		}

		this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}

	public void LoadFramesBits(String string, String string2, String string3, int int1) {
		this.name = string3;
		String string4 = string3 + "_" + string2 + "_";
		String string5 = "_";
		for (int int2 = 0; int2 < int1; ++int2) {
			Integer integer = new Integer(int2);
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string4 + "8" + string5 + integer.toString() + ".png"), Texture.getSharedTexture(string4 + "9" + string5 + integer.toString() + ".png"), Texture.getSharedTexture(string4 + "6" + string5 + integer.toString() + ".png"), Texture.getSharedTexture(string4 + "3" + string5 + integer.toString() + ".png"), Texture.getSharedTexture(string4 + "2" + string5 + integer.toString() + ".png"));
			this.Frames.add(directionFrame);
		}

		this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}

	public void LoadFramesBits(String string, String string2, int int1) {
		this.name = string2;
		String string3 = string + "_" + string2 + "_";
		String string4 = "_";
		for (int int2 = 0; int2 < int1; ++int2) {
			Integer integer = new Integer(int2);
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string3 + "8" + string4 + integer.toString() + ".png"), Texture.getSharedTexture(string3 + "9" + string4 + integer.toString() + ".png"), Texture.getSharedTexture(string3 + "6" + string4 + integer.toString() + ".png"), Texture.getSharedTexture(string3 + "3" + string4 + integer.toString() + ".png"), Texture.getSharedTexture(string3 + "2" + string4 + integer.toString() + ".png"));
			this.Frames.add(directionFrame);
		}

		this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}

	public void LoadFramesBitRepeatFrame(String string, String string2, int int1) {
		this.name = string2;
		String string3 = "_";
		String string4 = "";
		Integer integer = new Integer(int1);
		IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string2 + "8" + string3 + integer.toString() + string4 + ".png"), Texture.getSharedTexture(string2 + "9" + string3 + integer.toString() + string4 + ".png"), Texture.getSharedTexture(string2 + "6" + string3 + integer.toString() + string4 + ".png"), Texture.getSharedTexture(string2 + "3" + string3 + integer.toString() + string4 + ".png"), Texture.getSharedTexture(string2 + "2" + string3 + integer.toString() + string4 + ".png"));
		this.Frames.add(directionFrame);
		this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}

	public void LoadFramesBitRepeatFrame(String string, String string2, String string3, int int1, String string4) {
		this.name = string3;
		String string5 = string3 + "_" + string2 + "_";
		String string6 = "_";
		String string7 = "";
		if (string4 != null) {
			string7 = "_" + string4;
		}

		Integer integer = new Integer(int1);
		IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string5 + "8" + string6 + integer.toString() + string7 + ".png"), Texture.getSharedTexture(string5 + "9" + string6 + integer.toString() + string7 + ".png"), Texture.getSharedTexture(string5 + "6" + string6 + integer.toString() + string7 + ".png"), Texture.getSharedTexture(string5 + "3" + string6 + integer.toString() + string7 + ".png"), Texture.getSharedTexture(string5 + "2" + string6 + integer.toString() + string7 + ".png"));
		this.Frames.add(directionFrame);
		this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}

	public void LoadFramesBits(String string, String string2, String string3, int int1, String string4) {
		this.name = string3;
		String string5 = string3 + "_" + string2 + "_";
		String string6 = "_";
		String string7 = "";
		if (string4 != null) {
			string7 = "_" + string4;
		}

		for (int int2 = 0; int2 < int1; ++int2) {
			Integer integer = new Integer(int2);
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string5 + "8" + string6 + integer.toString() + string7 + ".png"), Texture.getSharedTexture(string5 + "9" + string6 + integer.toString() + string7 + ".png"), Texture.getSharedTexture(string5 + "6" + string6 + integer.toString() + string7 + ".png"), Texture.getSharedTexture(string5 + "3" + string6 + integer.toString() + string7 + ".png"), Texture.getSharedTexture(string5 + "2" + string6 + integer.toString() + string7 + ".png"));
			this.Frames.add(directionFrame);
		}

		this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}

	public void LoadFramesPcx(String string, String string2, int int1) {
		this.name = string2;
		String string3 = string + "_";
		String string4 = "_" + string2 + "_";
		for (int int2 = 0; int2 < int1; ++int2) {
			Integer integer = new Integer(int2);
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string3 + "8" + string4 + integer.toString() + ".pcx"), Texture.getSharedTexture(string3 + "9" + string4 + integer.toString() + ".pcx"), Texture.getSharedTexture(string3 + "6" + string4 + integer.toString() + ".pcx"), Texture.getSharedTexture(string3 + "3" + string4 + integer.toString() + ".pcx"), Texture.getSharedTexture(string3 + "2" + string4 + integer.toString() + ".pcx"));
			this.Frames.add(directionFrame);
		}

		this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}

	void Dispose() {
		for (int int1 = 0; int1 < this.Frames.size(); ++int1) {
			IsoDirectionFrame directionFrame = (IsoDirectionFrame)this.Frames.get(int1);
			directionFrame.SetAllDirections((Texture)null);
		}
	}

	Texture LoadFrameExplicit(String string) {
		Texture texture = Texture.getSharedTexture(string);
		IsoDirectionFrame directionFrame = new IsoDirectionFrame(texture);
		this.Frames.add(directionFrame);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
		return texture;
	}

	void LoadFramesNoDir(String string, String string2, int int1) {
		this.name = string2;
		String string3 = "media/" + string;
		String string4 = "_" + string2 + "_";
		for (int int2 = 0; int2 < int1; ++int2) {
			Integer integer = new Integer(int2);
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string3 + string4 + integer.toString() + ".png"));
			this.Frames.add(directionFrame);
		}

		this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}

	void LoadFramesNoDirPage(String string, String string2, int int1) {
		this.name = string2;
		String string3 = string;
		String string4 = "_" + string2 + "_";
		for (int int2 = 0; int2 < int1; ++int2) {
			Integer integer = new Integer(int2);
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(TexturePackPage.getTexture(string3 + string4 + integer.toString()));
			this.Frames.add(directionFrame);
		}

		this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}

	void LoadFramesNoDirPageDirect(String string, String string2, int int1) {
		this.name = string2;
		String string3 = string;
		String string4 = "_" + string2 + "_";
		for (int int2 = 0; int2 < int1; ++int2) {
			Integer integer = new Integer(int2);
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(TexturePackPage.getTexture(string3 + string4 + integer.toString() + ".png"));
			this.Frames.add(directionFrame);
		}

		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
		this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
	}

	void LoadFramesNoDirPage(String string) {
		this.name = "default";
		String string2 = string;
		for (int int1 = 0; int1 < 1; ++int1) {
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string2));
			this.Frames.add(directionFrame);
		}

		this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}

	public void LoadFramesPageSimple(String string, String string2, String string3, String string4) {
		this.name = "default";
		for (int int1 = 0; int1 < 1; ++int1) {
			new Integer(int1);
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(TexturePackPage.getTexture(string), TexturePackPage.getTexture(string2), TexturePackPage.getTexture(string3), TexturePackPage.getTexture(string4));
			this.Frames.add(directionFrame);
		}

		this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}

	void LoadFramesNoDirPalette(String string, String string2, int int1, String string3) {
		this.name = string2;
		String string4 = "media/characters/" + string;
		String string5 = "_" + string2 + "_";
		for (int int2 = 0; int2 < int1; ++int2) {
			Integer integer = new Integer(int2);
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string4 + string5 + integer.toString() + ".pcx", string3));
			this.Frames.add(directionFrame);
		}

		this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}

	void LoadFramesPalette(String string, String string2, int int1, PaletteManager.PaletteInfo paletteInfo) {
		this.name = string2;
		String string3 = "media/characters/" + string + "_";
		String string4 = "_" + string2 + "_";
		for (int int2 = 0; int2 < int1; ++int2) {
			Integer integer = new Integer(int2);
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string3 + "8" + string4 + integer.toString() + ".pcx", paletteInfo.palette, paletteInfo.name), Texture.getSharedTexture(string3 + "9" + string4 + integer.toString() + ".pcx", paletteInfo.palette, paletteInfo.name), Texture.getSharedTexture(string3 + "6" + string4 + integer.toString() + ".pcx", paletteInfo.palette, paletteInfo.name), Texture.getSharedTexture(string3 + "3" + string4 + integer.toString() + ".pcx", paletteInfo.palette, paletteInfo.name), Texture.getSharedTexture(string3 + "2" + string4 + integer.toString() + ".pcx", paletteInfo.palette, paletteInfo.name));
			this.Frames.add(directionFrame);
		}

		this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}

	void LoadFramesPalette(String string, String string2, int int1, String string3) {
		this.name = string2;
		String string4 = string + "_";
		String string5 = "_" + string2 + "_";
		for (int int2 = 0; int2 < int1; ++int2) {
			Integer integer = new Integer(int2);
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(TexturePackPage.getTexture(string4 + "8" + string5 + integer.toString() + "_" + string3), TexturePackPage.getTexture(string4 + "9" + string5 + integer.toString() + "_" + string3), TexturePackPage.getTexture(string4 + "6" + string5 + integer.toString() + "_" + string3), TexturePackPage.getTexture(string4 + "3" + string5 + integer.toString() + "_" + string3), TexturePackPage.getTexture(string4 + "2" + string5 + integer.toString() + "_" + string3));
			this.Frames.add(directionFrame);
		}

		this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}

	void DupeFrame() {
		for (int int1 = 0; int1 < 8; ++int1) {
			IsoDirectionFrame directionFrame = new IsoDirectionFrame();
			directionFrame.directions[int1] = ((IsoDirectionFrame)this.Frames.get(0)).directions[int1];
			directionFrame.bDoFlip = ((IsoDirectionFrame)this.Frames.get(0)).bDoFlip;
			this.Frames.add(directionFrame);
		}

		this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
	}
}
