package zombie.radio.StorySounds;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.lwjgl.input.Keyboard;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.Vector2;
import zombie.ui.TextManager;
import zombie.ui.UIFont;


public class SLSoundManager {
	public static boolean ENABLED = false;
	public static boolean DEBUG = false;
	public static boolean LUA_DEBUG = false;
	public static StoryEmitter Emitter = new StoryEmitter();
	private static SLSoundManager instance;
	private HashMap state = new HashMap();
	private ArrayList storySounds = new ArrayList();
	private int nextTick = 0;
	private float borderCenterX = 10500.0F;
	private float borderCenterY = 9000.0F;
	private float borderRadiusMin = 12000.0F;
	private float borderRadiusMax = 16000.0F;
	private float borderScale = 1.0F;

	public static SLSoundManager getInstance() {
		if (instance == null) {
			instance = new SLSoundManager();
		}

		return instance;
	}

	private SLSoundManager() {
		this.state.put(12, false);
		this.state.put(13, false);
	}

	public boolean getDebug() {
		return DEBUG;
	}

	public boolean getLuaDebug() {
		return LUA_DEBUG;
	}

	public ArrayList getStorySounds() {
		return this.storySounds;
	}

	public void print(String string) {
		if (DEBUG) {
			System.out.println(string);
		}
	}

	public void init() {
		this.loadSounds();
	}

	public void loadSounds() {
		this.storySounds.clear();
		try {
			File file = new File("media" + File.separator + "sound" + File.separator);
			if (file.exists() && file.isDirectory()) {
				File[] fileArray = file.listFiles();
				for (int int1 = 0; int1 < fileArray.length; ++int1) {
					if (fileArray[int1].isFile()) {
						String string = fileArray[int1].getName();
						if (string.lastIndexOf(".") != -1 && string.lastIndexOf(".") != 0 && string.substring(string.lastIndexOf(".") + 1).equals("ogg")) {
							String string2 = string.substring(0, string.lastIndexOf("."));
							this.print("Adding sound: " + string2);
							this.addStorySound(new StorySound(string2, 1.0F));
						}
					}
				}
			}
		} catch (Exception exception) {
			System.out.print(exception.getMessage());
		}
	}

	private void addStorySound(StorySound storySound) {
		this.storySounds.add(storySound);
	}

	public void updateKeys() {
		boolean boolean1;
		Entry entry;
		for (Iterator iterator = this.state.entrySet().iterator(); iterator.hasNext(); entry.setValue(boolean1)) {
			entry = (Entry)iterator.next();
			boolean1 = Keyboard.isKeyDown((Integer)entry.getKey());
			if (boolean1 && (Boolean)entry.getValue() != boolean1) {
				switch ((Integer)entry.getKey()) {
				case 12: 
				
				case 26: 
				
				case 53: 
				
				default: 
					break;
				
				case 13: 
					Emitter.coordinate3D = !Emitter.coordinate3D;
				
				}
			}
		}
	}

	public void update(int int1, int int2, int int3) {
		this.updateKeys();
		Emitter.tick();
	}

	public void thunderTest() {
		--this.nextTick;
		if (this.nextTick <= 0) {
			this.nextTick = Rand.Next(10, 180);
			float float1 = Rand.Next(0.0F, 8000.0F);
			double double1 = Math.random() * 3.141592653589793 * 2.0;
			float float2 = this.borderCenterX + (float)(Math.cos(double1) * (double)float1);
			float float3 = this.borderCenterY + (float)(Math.sin(double1) * (double)float1);
			if (Rand.Next(0, 100) < 60) {
				Emitter.playSound("thunder", 1.0F, float2, float3, 0.0F, 100.0F, 8500.0F);
			} else {
				Emitter.playSound("thundereffect", 1.0F, float2, float3, 0.0F, 100.0F, 8500.0F);
			}
		}
	}

	public void render() {
		this.renderDebug();
	}

	public void renderDebug() {
		if (DEBUG) {
			String string = Emitter.coordinate3D ? "3D coordinates, X-Z-Y" : "2D coordinates X-Y-Z";
			int int1 = TextManager.instance.MeasureStringX(UIFont.Large, string) / 2;
			int int2 = TextManager.instance.MeasureStringY(UIFont.Large, string);
			int int3 = Core.getInstance().getScreenWidth() / 2;
			int int4 = Core.getInstance().getScreenHeight() / 2;
			this.renderLine(UIFont.Large, string, int3 - int1, int4);
		}
	}

	private void renderLine(UIFont uIFont, String string, int int1, int int2) {
		TextManager.instance.DrawString(uIFont, (double)(int1 + 1), (double)(int2 + 1), string, 0.0, 0.0, 0.0, 1.0);
		TextManager.instance.DrawString(uIFont, (double)(int1 - 1), (double)(int2 - 1), string, 0.0, 0.0, 0.0, 1.0);
		TextManager.instance.DrawString(uIFont, (double)(int1 + 1), (double)(int2 - 1), string, 0.0, 0.0, 0.0, 1.0);
		TextManager.instance.DrawString(uIFont, (double)(int1 - 1), (double)(int2 + 1), string, 0.0, 0.0, 0.0, 1.0);
		TextManager.instance.DrawString(uIFont, (double)int1, (double)int2, string, 1.0, 1.0, 1.0, 1.0);
	}

	public Vector2 getRandomBorderPosition() {
		float float1 = Rand.Next(this.borderRadiusMin * this.borderScale, this.borderRadiusMax * this.borderScale);
		double double1 = Math.random() * 3.141592653589793 * 2.0;
		float float2 = this.borderCenterX + (float)(Math.cos(double1) * (double)float1);
		float float3 = this.borderCenterY + (float)(Math.sin(double1) * (double)float1);
		return new Vector2(float2, float3);
	}

	public float getRandomBorderRange() {
		return Rand.Next(this.borderRadiusMin * this.borderScale * 1.5F, this.borderRadiusMax * this.borderScale * 1.5F);
	}
}
