package zombie.gameStates;

import fmod.fmod.Audio;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.OpenGLException;
import zombie.DebugFileWatcher;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.SoundManager;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.asset.AssetManagers;
import zombie.characters.IsoPlayer;
import zombie.core.BoxedStaticValues;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.ProxyPrintStream;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZipLogs;
import zombie.core.opengl.RenderThread;
import zombie.core.raknet.VoiceManager;
import zombie.core.skinnedmodel.advancedanimation.AnimNodeAsset;
import zombie.core.skinnedmodel.advancedanimation.AnimNodeAssetManager;
import zombie.core.skinnedmodel.model.AiSceneAsset;
import zombie.core.skinnedmodel.model.AiSceneAssetManager;
import zombie.core.skinnedmodel.model.AnimationAsset;
import zombie.core.skinnedmodel.model.AnimationAssetManager;
import zombie.core.skinnedmodel.model.MeshAssetManager;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelAssetManager;
import zombie.core.skinnedmodel.model.ModelMesh;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.ClothingItemAssetManager;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureAssetManager;
import zombie.core.textures.TextureID;
import zombie.core.textures.TextureIDAssetManager;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.input.JoypadManager;
import zombie.modding.ActiveMods;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;


public final class MainScreenState extends GameState {
	public static String Version = "RC 3";
	public static Audio ambient;
	public static float totalScale = 1.0F;
	public float alpha = 1.0F;
	public float alphaStep = 0.03F;
	private int RestartDebounceClickTimer = 10;
	public final ArrayList Elements = new ArrayList(16);
	public float targetAlpha = 1.0F;
	int lastH;
	int lastW;
	MainScreenState.ScreenElement Logo;
	public static MainScreenState instance;
	public boolean showLogo = false;
	private float FadeAlpha = 0.0F;
	float lightningTime = 0.0F;
	float lastLightningTime = 0.0F;
	public float lightningDelta = 0.0F;
	public float lightningTargetDelta = 0.0F;
	public float lightningFullTimer = 0.0F;
	public float lightningCount = 0.0F;
	public float lightOffCount = 0.0F;
	private ConnectToServerState connectToServerState;
	private static GLFWImage windowIcon1;
	private static GLFWImage windowIcon2;
	private static ByteBuffer windowIconBB1;
	private static ByteBuffer windowIconBB2;

	public static void main(String[] stringArray) {
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			if (stringArray[int1] != null && stringArray[int1].startsWith("-cachedir=")) {
				ZomboidFileSystem.instance.setCacheDir(stringArray[int1].replace("-cachedir=", "").trim());
			}
		}

		ZipLogs.addZipFile(false);
		try {
			String string = ZomboidFileSystem.instance.getCacheDir();
			String string2 = string + File.separator + "console.txt";
			FileOutputStream fileOutputStream = new FileOutputStream(string2);
			PrintStream printStream = new PrintStream(fileOutputStream, true);
			System.setOut(new ProxyPrintStream(System.out, printStream));
			System.setErr(new ProxyPrintStream(System.err, printStream));
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
		}

		DebugLog.init();
		LoggerManager.init();
		DebugLog.log("cachedir set to \"" + ZomboidFileSystem.instance.getCacheDir() + "\"");
		JAssImpImporter.Init();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		System.out.println(simpleDateFormat.format(Calendar.getInstance().getTime()));
		System.out.println("cachedir is \"" + ZomboidFileSystem.instance.getCacheDir() + "\"");
		System.out.println("LogFileDir is \"" + LoggerManager.getLogsDir() + "\"");
		printSpecs();
		System.getProperties().list(System.out);
		System.out.println("-----");
		System.out.println("versionNumber=" + Core.getInstance().getVersionNumber() + " demo=false");
		Display.setIcon(loadIcons());
		Rand.init();
		for (int int2 = 0; int2 < stringArray.length; ++int2) {
			if (stringArray[int2] != null) {
				if (stringArray[int2].contains("safemode")) {
					Core.SafeMode = true;
					Core.SafeModeForced = true;
				} else if (stringArray[int2].equals("-nosound")) {
					Core.SoundDisabled = true;
				} else if (stringArray[int2].equals("-aitest")) {
					IsoPlayer.isTestAIMode = true;
				} else if (stringArray[int2].equals("-novoip")) {
					VoiceManager.VoipDisabled = true;
				} else if (stringArray[int2].equals("-debug")) {
					Core.bDebug = true;
				} else if (!stringArray[int2].startsWith("-debuglog=")) {
					if (!stringArray[int2].startsWith("-cachedir=")) {
						if (stringArray[int2].equals("+connect")) {
							if (int2 + 1 < stringArray.length) {
								System.setProperty("args.server.connect", stringArray[int2 + 1]);
							}

							++int2;
						} else if (stringArray[int2].equals("+password")) {
							if (int2 + 1 < stringArray.length) {
								System.setProperty("args.server.password", stringArray[int2 + 1]);
							}

							++int2;
						} else if (stringArray[int2].contains("-debugtranslation")) {
							Translator.debug = true;
						} else if ("-modfolders".equals(stringArray[int2])) {
							if (int2 + 1 < stringArray.length) {
								ZomboidFileSystem.instance.setModFoldersOrder(stringArray[int2 + 1]);
							}

							++int2;
						} else if (stringArray[int2].equals("-nosteam")) {
							System.setProperty("zomboid.steam", "0");
						} else {
							DebugLog.log("unknown option \"" + stringArray[int2] + "\"");
						}
					}
				} else {
					String[] stringArray2 = stringArray[int2].replace("-debuglog=", "").split(",");
					int int3 = stringArray2.length;
					for (int int4 = 0; int4 < int3; ++int4) {
						String string3 = stringArray2[int4];
						try {
							char char1 = string3.charAt(0);
							string3 = char1 != '+' && char1 != '-' ? string3 : string3.substring(1);
							DebugLog.setLogEnabled(DebugType.valueOf(string3), char1 != '-');
						} catch (IllegalArgumentException illegalArgumentException) {
						}
					}
				}
			}
		}

		try {
			RenderThread.init();
			AssetManagers assetManagers = GameWindow.assetManagers;
			AiSceneAssetManager.instance.create(AiSceneAsset.ASSET_TYPE, assetManagers);
			AnimationAssetManager.instance.create(AnimationAsset.ASSET_TYPE, assetManagers);
			AnimNodeAssetManager.instance.create(AnimNodeAsset.ASSET_TYPE, assetManagers);
			ClothingItemAssetManager.instance.create(ClothingItem.ASSET_TYPE, assetManagers);
			MeshAssetManager.instance.create(ModelMesh.ASSET_TYPE, assetManagers);
			ModelAssetManager.instance.create(Model.ASSET_TYPE, assetManagers);
			TextureIDAssetManager.instance.create(TextureID.ASSET_TYPE, assetManagers);
			TextureAssetManager.instance.create(Texture.ASSET_TYPE, assetManagers);
			GameWindow.InitGameThread();
			RenderThread.renderLoop();
		} catch (OpenGLException openGLException) {
			String string4 = ZomboidFileSystem.instance.getCacheDir();
			File file = new File(string4 + File.separator + "options2.bin");
			file.delete();
			openGLException.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void DrawTexture(Texture texture, int int1, int int2, int int3, int int4, float float1) {
		SpriteRenderer.instance.renderi(texture, int1, int2, int3, int4, 1.0F, 1.0F, 1.0F, float1, (Consumer)null);
	}

	public static void DrawTexture(Texture texture, int int1, int int2, int int3, int int4, Color color) {
		SpriteRenderer.instance.renderi(texture, int1, int2, int3, int4, color.r, color.g, color.b, color.a, (Consumer)null);
	}

	public void enter() {
		DebugLog.log("EXITDEBUG: MainScreenState.enter 1");
		this.Elements.clear();
		this.targetAlpha = 1.0F;
		TextureID.UseFiltering = true;
		this.RestartDebounceClickTimer = 100;
		totalScale = (float)Core.getInstance().getOffscreenHeight(0) / 1080.0F;
		this.lastW = Core.getInstance().getOffscreenWidth(0);
		this.lastH = Core.getInstance().getOffscreenHeight(0);
		this.alpha = 1.0F;
		this.showLogo = false;
		SoundManager.instance.setMusicState("MainMenu");
		int int1 = (int)((float)Core.getInstance().getOffscreenHeight(0) * 0.7F);
		MainScreenState.ScreenElement screenElement = new MainScreenState.ScreenElement(Texture.getSharedTexture("media/ui/PZ_Logo.png"), Core.getInstance().getOffscreenWidth(0) / 2 - (int)((float)Texture.getSharedTexture("media/ui/PZ_Logo.png").getWidth() * totalScale) / 2, int1 - (int)(350.0F * totalScale), 0.0F, 0.0F, 1);
		screenElement.targetAlpha = 1.0F;
		screenElement.alphaStep *= 0.9F;
		this.Logo = screenElement;
		this.Elements.add(screenElement);
		TextureID.UseFiltering = false;
		LuaEventManager.triggerEvent("OnMainMenuEnter");
		instance = this;
		float float1 = TextureID.totalMemUsed / 1024.0F;
		float float2 = float1 / 1024.0F;
		DebugLog.log("EXITDEBUG: MainScreenState.enter 2");
	}

	public static MainScreenState getInstance() {
		return instance;
	}

	public boolean ShouldShowLogo() {
		return this.showLogo;
	}

	public void exit() {
		DebugLog.log("EXITDEBUG: MainScreenState.exit 1");
		DebugLog.log("LOADED UP A TOTAL OF " + Texture.totalTextureID + " TEXTURES");
		float float1 = (float)Core.getInstance().getOptionMusicVolume() / 10.0F;
		long long1 = Calendar.getInstance().getTimeInMillis();
		while (true) {
			this.FadeAlpha = Math.min(1.0F, (float)(Calendar.getInstance().getTimeInMillis() - long1) / 250.0F);
			this.render();
			if (this.FadeAlpha >= 1.0F) {
				SoundManager.instance.stopMusic("");
				SoundManager.instance.setMusicVolume(float1);
				DebugLog.log("EXITDEBUG: MainScreenState.exit 2");
				return;
			}

			try {
				Thread.sleep(33L);
			} catch (Exception exception) {
			}

			SoundManager.instance.Update();
		}
	}

	public void render() {
		this.lightningTime += 1.0F * GameTime.instance.getMultipliedSecondsSinceLastUpdate();
		Core.getInstance().StartFrame();
		Core.getInstance().EndFrame();
		boolean boolean1 = UIManager.useUIFBO;
		UIManager.useUIFBO = false;
		Core.getInstance().StartFrameUI();
		IndieGL.glBlendFunc(770, 771);
		SpriteRenderer.instance.renderi((Texture)null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0F, 0.0F, 0.0F, 1.0F, (Consumer)null);
		IndieGL.glBlendFunc(770, 770);
		float float1 = SoundManager.instance.getMusicPosition();
		if (this.lightningTargetDelta == 0.0F && this.lightningDelta != 0.0F && this.lightningDelta < 0.6F && this.lightningCount == 0.0F) {
			this.lightningTargetDelta = 1.0F;
			this.lightningCount = 1.0F;
		}

		float float2 = "OldMusic_theme2".equals(SoundManager.instance.getCurrentMusicName()) ? 12000.0F : 29500.0F;
		float float3 = "OldMusic_theme2".equals(SoundManager.instance.getCurrentMusicName()) ? 45000.0F : 107000.0F;
		float float4 = 0.0F;
		if (float1 >= float2 && this.lastLightningTime < float2) {
		}

		if (float1 >= float2 + float4 && this.lastLightningTime < float2 + float4) {
			this.lightningTargetDelta = 1.0F;
		}

		if (float1 >= float3 && this.lastLightningTime < float3) {
		}

		if (float1 >= float3 + float4 && this.lastLightningTime < float3 + float4) {
			this.lightningTargetDelta = 1.0F;
		}

		if (this.lightningTargetDelta == 1.0F && this.lightningDelta == 1.0F && (this.lightningFullTimer > 1.0F && this.lightningCount == 0.0F || this.lightningFullTimer > 10.0F)) {
			this.lightningTargetDelta = 0.0F;
			this.lightningFullTimer = 0.0F;
		}

		if (this.lightningTargetDelta == 1.0F && this.lightningDelta == 1.0F) {
			this.lightningFullTimer += GameTime.getInstance().getMultiplier();
		}

		if (this.lightningDelta != this.lightningTargetDelta) {
			if (this.lightningDelta < this.lightningTargetDelta) {
				this.lightningDelta += 0.17F * GameTime.getInstance().getMultiplier();
				if (this.lightningDelta > this.lightningTargetDelta) {
					this.lightningDelta = this.lightningTargetDelta;
					if (this.lightningDelta == 1.0F) {
						this.showLogo = true;
					}
				}
			}

			if (this.lightningDelta > this.lightningTargetDelta) {
				this.lightningDelta -= 0.025F * GameTime.getInstance().getMultiplier();
				if (this.lightningCount == 0.0F) {
					this.lightningDelta -= 0.1F;
				}

				if (this.lightningDelta < this.lightningTargetDelta) {
					this.lightningDelta = this.lightningTargetDelta;
					this.lightningCount = 0.0F;
				}
			}
		}

		this.lastLightningTime = float1;
		Texture texture = Texture.getSharedTexture("media/ui/Title.png");
		Texture texture2 = Texture.getSharedTexture("media/ui/Title2.png");
		Texture texture3 = Texture.getSharedTexture("media/ui/Title3.png");
		Texture texture4 = Texture.getSharedTexture("media/ui/Title4.png");
		if (Rand.Next(150) == 0) {
			this.lightOffCount = 10.0F;
		}

		Texture texture5 = Texture.getSharedTexture("media/ui/Title_lightning.png");
		Texture texture6 = Texture.getSharedTexture("media/ui/Title_lightning2.png");
		Texture texture7 = Texture.getSharedTexture("media/ui/Title_lightning3.png");
		Texture texture8 = Texture.getSharedTexture("media/ui/Title_lightning4.png");
		float float5 = (float)Core.getInstance().getScreenHeight() / 1080.0F;
		float float6 = (float)texture.getWidth() * float5;
		float float7 = (float)texture2.getWidth() * float5;
		float float8 = (float)Core.getInstance().getScreenWidth() - (float6 + float7);
		if (float8 >= 0.0F) {
			float8 = 0.0F;
		}

		float float9 = 1.0F - this.lightningDelta * 0.6F;
		float float10 = 1024.0F * float5;
		float float11 = 56.0F * float5;
		DrawTexture(texture, (int)float8, 0, (int)float6, (int)float10, float9);
		DrawTexture(texture2, (int)float8 + (int)float6, 0, (int)float6, (int)float10, float9);
		DrawTexture(texture3, (int)float8, (int)float10, (int)float6, (int)((float)texture3.getHeight() * float5), float9);
		DrawTexture(texture4, (int)float8 + (int)float6, (int)float10, (int)float6, (int)((float)texture3.getHeight() * float5), float9);
		IndieGL.glBlendFunc(770, 1);
		DrawTexture(texture5, (int)float8, 0, (int)float6, (int)float10, this.lightningDelta);
		DrawTexture(texture6, (int)float8 + (int)float6, 0, (int)float6, (int)float10, this.lightningDelta);
		DrawTexture(texture7, (int)float8, (int)float10, (int)float6, (int)float10, this.lightningDelta);
		DrawTexture(texture8, (int)float8 + (int)float6, (int)float10, (int)float6, (int)float10, this.lightningDelta);
		IndieGL.glBlendFunc(770, 771);
		UIManager.render();
		if (GameWindow.DrawReloadingLua) {
			int int1 = TextManager.instance.MeasureStringX(UIFont.Small, "Reloading Lua") + 32;
			int int2 = TextManager.instance.font.getLineHeight();
			int int3 = (int)Math.ceil((double)int2 * 1.5);
			SpriteRenderer.instance.renderi((Texture)null, Core.getInstance().getScreenWidth() - int1 - 12, 12, int1, int3, 0.0F, 0.5F, 0.75F, 1.0F, (Consumer)null);
			TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() - int1 / 2 - 12), (double)(12 + (int3 - int2) / 2), "Reloading Lua", 1.0, 1.0, 1.0, 1.0);
		}

		if (this.FadeAlpha > 0.0F) {
			UIManager.DrawTexture(UIManager.getBlack(), 0.0, 0.0, (double)Core.getInstance().getScreenWidth(), (double)Core.getInstance().getScreenHeight(), (double)this.FadeAlpha);
		}

		ActiveMods.renderUI();
		JoypadManager.instance.renderUI();
		Core.getInstance().EndFrameUI();
		UIManager.useUIFBO = boolean1;
	}

	public GameStateMachine.StateAction update() {
		if (this.connectToServerState != null) {
			GameStateMachine.StateAction stateAction = this.connectToServerState.update();
			if (stateAction == GameStateMachine.StateAction.Continue) {
				this.connectToServerState.exit();
				this.connectToServerState = null;
				return GameStateMachine.StateAction.Remain;
			}
		}

		LuaEventManager.triggerEvent("OnFETick", BoxedStaticValues.toDouble(0.0));
		if (this.RestartDebounceClickTimer > 0) {
			--this.RestartDebounceClickTimer;
		}

		for (int int1 = 0; int1 < this.Elements.size(); ++int1) {
			MainScreenState.ScreenElement screenElement = (MainScreenState.ScreenElement)this.Elements.get(int1);
			screenElement.update();
		}

		this.lastW = Core.getInstance().getOffscreenWidth(0);
		this.lastH = Core.getInstance().getOffscreenHeight(0);
		DebugFileWatcher.instance.update();
		ZomboidFileSystem.instance.update();
		try {
			Core.getInstance().CheckDelayResetLua();
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}

		return GameStateMachine.StateAction.Remain;
	}

	public void setConnectToServerState(ConnectToServerState connectToServerState) {
		this.connectToServerState = connectToServerState;
	}

	public GameState redirectState() {
		return null;
	}

	public static Buffer loadIcons() {
		Buffer buffer = null;
		String string = System.getProperty("os.name").toUpperCase(Locale.ENGLISH);
		BufferedImage bufferedImage;
		ByteBuffer byteBuffer;
		GLFWImage gLFWImage;
		if (string.contains("WIN")) {
			try {
				buffer = GLFWImage.create(2);
				bufferedImage = ImageIO.read((new File("media" + File.separator + "ui" + File.separator + "zomboidIcon16.png")).getAbsoluteFile());
				windowIconBB1 = byteBuffer = loadInstance(bufferedImage, 16);
				windowIcon1 = gLFWImage = GLFWImage.create().set(16, 16, byteBuffer);
				buffer.put(0, gLFWImage);
				bufferedImage = ImageIO.read((new File("media" + File.separator + "ui" + File.separator + "zomboidIcon32.png")).getAbsoluteFile());
				windowIconBB2 = byteBuffer = loadInstance(bufferedImage, 32);
				windowIcon2 = gLFWImage = GLFWImage.create().set(32, 32, byteBuffer);
				buffer.put(1, gLFWImage);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} else if (string.contains("MAC")) {
			try {
				buffer = GLFWImage.create(1);
				bufferedImage = ImageIO.read((new File("media" + File.separator + "ui" + File.separator + "zomboidIcon128.png")).getAbsoluteFile());
				windowIconBB1 = byteBuffer = loadInstance(bufferedImage, 128);
				windowIcon1 = gLFWImage = GLFWImage.create().set(128, 128, byteBuffer);
				buffer.put(0, gLFWImage);
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}
		} else {
			try {
				buffer = GLFWImage.create(1);
				bufferedImage = ImageIO.read((new File("media" + File.separator + "ui" + File.separator + "zomboidIcon32.png")).getAbsoluteFile());
				windowIconBB1 = byteBuffer = loadInstance(bufferedImage, 32);
				windowIcon1 = gLFWImage = GLFWImage.create().set(32, 32, byteBuffer);
				buffer.put(0, gLFWImage);
			} catch (IOException ioException3) {
				ioException3.printStackTrace();
			}
		}

		return buffer;
	}

	private static ByteBuffer loadInstance(BufferedImage bufferedImage, int int1) {
		BufferedImage bufferedImage2 = new BufferedImage(int1, int1, 3);
		Graphics2D graphics2D = bufferedImage2.createGraphics();
		double double1 = getIconRatio(bufferedImage, bufferedImage2);
		double double2 = (double)bufferedImage.getWidth() * double1;
		double double3 = (double)bufferedImage.getHeight() * double1;
		graphics2D.drawImage(bufferedImage, (int)(((double)bufferedImage2.getWidth() - double2) / 2.0), (int)(((double)bufferedImage2.getHeight() - double3) / 2.0), (int)double2, (int)double3, (ImageObserver)null);
		graphics2D.dispose();
		return convertToByteBuffer(bufferedImage2);
	}

	private static double getIconRatio(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
		double double1 = 1.0;
		if (bufferedImage.getWidth() > bufferedImage2.getWidth()) {
			double1 = (double)bufferedImage2.getWidth() / (double)bufferedImage.getWidth();
		} else {
			double1 = (double)(bufferedImage2.getWidth() / bufferedImage.getWidth());
		}

		double double2;
		if (bufferedImage.getHeight() > bufferedImage2.getHeight()) {
			double2 = (double)bufferedImage2.getHeight() / (double)bufferedImage.getHeight();
			if (double2 < double1) {
				double1 = double2;
			}
		} else {
			double2 = (double)(bufferedImage2.getHeight() / bufferedImage.getHeight());
			if (double2 < double1) {
				double1 = double2;
			}
		}

		return double1;
	}

	public static ByteBuffer convertToByteBuffer(BufferedImage bufferedImage) {
		byte[] byteArray = new byte[bufferedImage.getWidth() * bufferedImage.getHeight() * 4];
		int int1 = 0;
		for (int int2 = 0; int2 < bufferedImage.getHeight(); ++int2) {
			for (int int3 = 0; int3 < bufferedImage.getWidth(); ++int3) {
				int int4 = bufferedImage.getRGB(int3, int2);
				byteArray[int1 + 0] = (byte)(int4 << 8 >> 24);
				byteArray[int1 + 1] = (byte)(int4 << 16 >> 24);
				byteArray[int1 + 2] = (byte)(int4 << 24 >> 24);
				byteArray[int1 + 3] = (byte)(int4 >> 24);
				int1 += 4;
			}
		}

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(byteArray.length);
		byteBuffer.put(byteArray);
		byteBuffer.flip();
		return byteBuffer;
	}

	private static void printSpecs() {
		try {
			System.out.println("===== System specs =====");
			long long1 = 1024L;
			long long2 = long1 * 1024L;
			long long3 = long2 * 1024L;
			Map map = System.getenv();
			PrintStream printStream = System.out;
			String string = System.getProperty("os.name");
			printStream.println("OS: " + string + ", version: " + System.getProperty("os.version") + ", arch: " + System.getProperty("os.arch"));
			if (map.containsKey("PROCESSOR_IDENTIFIER")) {
				System.out.println("Processor: " + (String)map.get("PROCESSOR_IDENTIFIER"));
			}

			if (map.containsKey("NUMBER_OF_PROCESSORS")) {
				System.out.println("Processor cores: " + (String)map.get("NUMBER_OF_PROCESSORS"));
			}

			System.out.println("Available processors (cores): " + Runtime.getRuntime().availableProcessors());
			System.out.println("Memory free: " + (float)Runtime.getRuntime().freeMemory() / (float)long2 + " MB");
			long long4 = Runtime.getRuntime().maxMemory();
			Object object = long4 == Long.MAX_VALUE ? "no limit" : (float)long4 / (float)long2;
			System.out.println("Memory max: " + object + " MB");
			System.out.println("Memory  total available to JVM: " + (float)Runtime.getRuntime().totalMemory() / (float)long2 + " MB");
			File[] fileArray = File.listRoots();
			File[] fileArray2 = fileArray;
			int int1 = fileArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				File file = fileArray2[int2];
				printStream = System.out;
				string = file.getAbsolutePath();
				printStream.println(string + ", Total: " + (float)file.getTotalSpace() / (float)long3 + " GB, Free: " + (float)file.getFreeSpace() / (float)long3 + " GB");
			}

			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				printStream = System.out;
				String[] stringArray = new String[]{"Product"};
				printStream.println("Mobo = " + wmic("baseboard", stringArray));
				printStream = System.out;
				stringArray = new String[]{"Manufacturer", "MaxClockSpeed", "Name"};
				printStream.println("CPU = " + wmic("cpu", stringArray));
				printStream = System.out;
				stringArray = new String[]{"AdapterRAM", "DriverVersion", "Name"};
				printStream.println("Graphics = " + wmic("path Win32_videocontroller", stringArray));
				printStream = System.out;
				stringArray = new String[]{"VideoModeDescription"};
				printStream.println("VideoMode = " + wmic("path Win32_videocontroller", stringArray));
				printStream = System.out;
				stringArray = new String[]{"Manufacturer", "Name"};
				printStream.println("Sound = " + wmic("path Win32_sounddevice", stringArray));
				printStream = System.out;
				stringArray = new String[]{"Capacity", "Manufacturer"};
				printStream.println("Memory RAM = " + wmic("memorychip", stringArray));
			}

			System.out.println("========================");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private static String wmic(String string, String[] stringArray) {
		String string2 = "";
		try {
			String string3 = "WMIC " + string + " GET";
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				string3 = string3 + " " + stringArray[int1];
				if (int1 < stringArray.length - 1) {
					string3 = string3 + ",";
				}
			}

			Process process = Runtime.getRuntime().exec(new String[]{"CMD", "/C", string3});
			process.getOutputStream().close();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String string4;
			String string5;
			for (string4 = ""; (string5 = bufferedReader.readLine()) != null; string4 = string4 + string5) {
			}

			String[] stringArray2 = stringArray;
			int int2 = stringArray.length;
			int int3;
			for (int3 = 0; int3 < int2; ++int3) {
				String string6 = stringArray2[int3];
				string4 = string4.replaceAll(string6, "");
			}

			string4 = string4.trim().replaceAll(" ( )+", "=");
			stringArray2 = string4.split("=");
			if (stringArray2.length > stringArray.length) {
				string2 = "{ ";
				int2 = stringArray2.length / stringArray.length;
				for (int3 = 0; int3 < int2; ++int3) {
					string2 = string2 + "[";
					for (int int4 = 0; int4 < stringArray.length; ++int4) {
						int int5 = int3 * stringArray.length + int4;
						string2 = string2 + stringArray[int4] + "=" + stringArray2[int5];
						if (int4 < stringArray.length - 1) {
							string2 = string2 + ",";
						}
					}

					string2 = string2 + "]";
					if (int3 < int2 - 1) {
						string2 = string2 + ", ";
					}
				}

				string2 = string2 + " }";
			} else {
				string2 = "[";
				for (int2 = 0; int2 < stringArray2.length; ++int2) {
					string2 = string2 + stringArray[int2] + "=" + stringArray2[int2];
					if (int2 < stringArray2.length - 1) {
						string2 = string2 + ",";
					}
				}

				string2 = string2 + "]";
			}

			return string2;
		} catch (Exception exception) {
			return "Couldnt get info...";
		}
	}

	public static class ScreenElement {
		public float alpha = 0.0F;
		public float alphaStep = 0.2F;
		public boolean jumpBack = true;
		public float sx = 0.0F;
		public float sy = 0.0F;
		public float targetAlpha = 0.0F;
		public Texture tex;
		public int TicksTillTargetAlpha = 0;
		public float x = 0.0F;
		public int xCount = 1;
		public float xVel = 0.0F;
		public float xVelO = 0.0F;
		public float y = 0.0F;
		public float yVel = 0.0F;
		public float yVelO = 0.0F;

		public ScreenElement(Texture texture, int int1, int int2, float float1, float float2, int int3) {
			this.x = this.sx = (float)int1;
			this.y = this.sy = (float)int2 - (float)texture.getHeight() * MainScreenState.totalScale;
			this.xVel = float1;
			this.yVel = float2;
			this.tex = texture;
			this.xCount = int3;
		}

		public void render() {
			int int1 = (int)this.x;
			int int2 = (int)this.y;
			for (int int3 = 0; int3 < this.xCount; ++int3) {
				MainScreenState.DrawTexture(this.tex, int1, int2, (int)((float)this.tex.getWidth() * MainScreenState.totalScale), (int)((float)this.tex.getHeight() * MainScreenState.totalScale), this.alpha);
				int1 = (int)((float)int1 + (float)this.tex.getWidth() * MainScreenState.totalScale);
			}

			TextManager.instance.DrawStringRight((double)(Core.getInstance().getOffscreenWidth(0) - 5), (double)(Core.getInstance().getOffscreenHeight(0) - 15), "Version: " + MainScreenState.Version, 1.0, 1.0, 1.0, 1.0);
		}

		public void setY(float float1) {
			this.y = this.sy = float1 - (float)this.tex.getHeight() * MainScreenState.totalScale;
		}

		public void update() {
			this.x += this.xVel * MainScreenState.totalScale;
			this.y += this.yVel * MainScreenState.totalScale;
			--this.TicksTillTargetAlpha;
			if (this.TicksTillTargetAlpha <= 0) {
				this.targetAlpha = 1.0F;
			}

			if (this.jumpBack && this.sx - this.x > (float)this.tex.getWidth() * MainScreenState.totalScale) {
				this.x += (float)this.tex.getWidth() * MainScreenState.totalScale;
			}

			if (this.alpha < this.targetAlpha) {
				this.alpha += this.alphaStep;
				if (this.alpha > this.targetAlpha) {
					this.alpha = this.targetAlpha;
				}
			} else if (this.alpha > this.targetAlpha) {
				this.alpha -= this.alphaStep;
				if (this.alpha < this.targetAlpha) {
					this.alpha = this.targetAlpha;
				}
			}
		}
	}

	public class Credit {
		public int disappearDelay = 200;
		public Texture name;
		public float nameAlpha = 0.0F;
		public float nameAppearDelay = 40.0F;
		public float nameTargetAlpha = 0.0F;
		public Texture title;
		public float titleAlpha = 0.0F;
		public float titleTargetAlpha = 1.0F;

		public Credit(Texture texture, Texture texture2) {
			this.title = texture;
			this.name = texture2;
		}
	}
}
