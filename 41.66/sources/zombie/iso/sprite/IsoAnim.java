package zombie.iso.sprite;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.core.textures.Texture;
import zombie.network.GameServer;
import zombie.network.ServerGUI;


public final class IsoAnim {
	public static final HashMap GlobalAnimMap = new HashMap();
	public short FinishUnloopedOnFrame = 0;
	public short FrameDelay = 0;
	public short LastFrame = 0;
	public final ArrayList Frames = new ArrayList(8);
	public String name;
	boolean looped = true;
	public int ID = 0;
	private static final ThreadLocal tlsStrBuf = new ThreadLocal(){
    
    protected StringBuilder initialValue() {
        return new StringBuilder();
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
		StringBuilder stringBuilder = (StringBuilder)tlsStrBuf.get();
		stringBuilder.setLength(0);
		stringBuilder.append(string);
		stringBuilder.append("_%_");
		stringBuilder.append(string2);
		stringBuilder.append("_^");
		int int2 = stringBuilder.lastIndexOf("^");
		int int3 = stringBuilder.indexOf("_%_") + 1;
		stringBuilder.setCharAt(int3, '9');
		stringBuilder.setCharAt(int2, '0');
		if (GameServer.bServer && !ServerGUI.isCreated()) {
			for (int int4 = 0; int4 < int1; ++int4) {
				this.Frames.add(new IsoDirectionFrame((Texture)null));
			}

			this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
			this.FramesArray = (IsoDirectionFrame[])this.Frames.toArray(this.FramesArray);
		}

		Texture texture = Texture.getSharedTexture(stringBuilder.toString());
		if (texture != null) {
			for (int int5 = 0; int5 < int1; ++int5) {
				if (int5 == 10) {
					stringBuilder.setLength(0);
					stringBuilder.append(string);
					stringBuilder.append("_1_");
					stringBuilder.append(string2);
					stringBuilder.append("_10");
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
					stringBuilder.setCharAt(int3, '8');
					try {
						stringBuilder.setCharAt(int2, integer.toString().charAt(0));
					} catch (Exception exception) {
						this.LoadFramesReverseAltName(string, string2, string3, int1);
					}

					string9 = stringBuilder.toString();
					stringBuilder.setCharAt(int3, '9');
					string5 = stringBuilder.toString();
					stringBuilder.setCharAt(int3, '6');
					string6 = stringBuilder.toString();
					stringBuilder.setCharAt(int3, '3');
					string7 = stringBuilder.toString();
					stringBuilder.setCharAt(int3, '2');
					string8 = stringBuilder.toString();
					directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string9), Texture.getSharedTexture(string5), Texture.getSharedTexture(string6), Texture.getSharedTexture(string7), Texture.getSharedTexture(string8));
				} else {
					stringBuilder.setCharAt(int3, '9');
					for (int int6 = 0; int6 < string4.length(); ++int6) {
						stringBuilder.setCharAt(int2 + int6, string4.charAt(int6));
					}

					string9 = stringBuilder.toString();
					stringBuilder.setCharAt(int3, '6');
					string5 = stringBuilder.toString();
					stringBuilder.setCharAt(int3, '3');
					string6 = stringBuilder.toString();
					stringBuilder.setCharAt(int3, '2');
					string7 = stringBuilder.toString();
					stringBuilder.setCharAt(int3, '1');
					string8 = stringBuilder.toString();
					stringBuilder.setCharAt(int3, '4');
					String string10 = stringBuilder.toString();
					stringBuilder.setCharAt(int3, '7');
					String string11 = stringBuilder.toString();
					stringBuilder.setCharAt(int3, '8');
					String string12 = stringBuilder.toString();
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
		StringBuilder stringBuilder = (StringBuilder)tlsStrBuf.get();
		stringBuilder.setLength(0);
		stringBuilder.append(string);
		stringBuilder.append("_%_");
		stringBuilder.append(string2);
		stringBuilder.append("_^");
		int int2 = stringBuilder.indexOf("_%_") + 1;
		int int3 = stringBuilder.lastIndexOf("^");
		stringBuilder.setCharAt(int2, '9');
		stringBuilder.setCharAt(int3, '0');
		if (GameServer.bServer && !ServerGUI.isCreated()) {
			for (int int4 = 0; int4 < int1; ++int4) {
				this.Frames.add(new IsoDirectionFrame((Texture)null));
			}

			this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
		}

		Texture texture = Texture.getSharedTexture(stringBuilder.toString());
		if (texture != null) {
			for (int int5 = 0; int5 < int1; ++int5) {
				if (int5 % 10 == 0 && int5 > 0) {
					stringBuilder.setLength(0);
					stringBuilder.append(string);
					stringBuilder.append("_%_");
					stringBuilder.append(string2);
					stringBuilder.append("_^_");
					int2 = stringBuilder.indexOf("_%_") + 1;
					int3 = stringBuilder.lastIndexOf("^");
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
					stringBuilder.setCharAt(int2, '9');
					for (int6 = 0; int6 < string3.length(); ++int6) {
						stringBuilder.setCharAt(int3 + int6, string3.charAt(int6));
					}

					string8 = stringBuilder.toString();
					stringBuilder.setCharAt(int2, '6');
					string4 = stringBuilder.toString();
					stringBuilder.setCharAt(int2, '3');
					string5 = stringBuilder.toString();
					stringBuilder.setCharAt(int2, '2');
					string6 = stringBuilder.toString();
					stringBuilder.setCharAt(int2, '1');
					string7 = stringBuilder.toString();
					stringBuilder.setCharAt(int2, '4');
					String string9 = stringBuilder.toString();
					stringBuilder.setCharAt(int2, '7');
					String string10 = stringBuilder.toString();
					stringBuilder.setCharAt(int2, '8');
					String string11 = stringBuilder.toString();
					directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string8), Texture.getSharedTexture(string4), Texture.getSharedTexture(string5), Texture.getSharedTexture(string6), Texture.getSharedTexture(string7), Texture.getSharedTexture(string9), Texture.getSharedTexture(string10), Texture.getSharedTexture(string11));
				} else {
					try {
						stringBuilder.setCharAt(int2, '8');
					} catch (Exception exception) {
						this.LoadFrames(string, string2, int1);
					}

					for (int6 = 0; int6 < string3.length(); ++int6) {
						try {
							stringBuilder.setCharAt(int3 + int6, integer.toString().charAt(int6));
						} catch (Exception exception2) {
							this.LoadFrames(string, string2, int1);
						}
					}

					string8 = stringBuilder.toString();
					stringBuilder.setCharAt(int2, '9');
					string4 = stringBuilder.toString();
					stringBuilder.setCharAt(int2, '6');
					string5 = stringBuilder.toString();
					stringBuilder.setCharAt(int2, '3');
					string6 = stringBuilder.toString();
					stringBuilder.setCharAt(int2, '2');
					string7 = stringBuilder.toString();
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
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string3 + string4 + integer.toString()));
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
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string3 + string4 + integer.toString() + ".png"));
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
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string), Texture.getSharedTexture(string2), Texture.getSharedTexture(string3), Texture.getSharedTexture(string4));
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

	void LoadFramesPalette(String string, String string2, int int1, String string3) {
		this.name = string2;
		String string4 = string + "_";
		String string5 = "_" + string2 + "_";
		for (int int2 = 0; int2 < int1; ++int2) {
			Integer integer = new Integer(int2);
			IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string4 + "8" + string5 + integer.toString() + "_" + string3), Texture.getSharedTexture(string4 + "9" + string5 + integer.toString() + "_" + string3), Texture.getSharedTexture(string4 + "6" + string5 + integer.toString() + "_" + string3), Texture.getSharedTexture(string4 + "3" + string5 + integer.toString() + "_" + string3), Texture.getSharedTexture(string4 + "2" + string5 + integer.toString() + "_" + string3));
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
