package zombie.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import se.krka.kahlua.vm.KahluaThread;
import zombie.GameApplet;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.opengl.RenderThread;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureFBO;
import zombie.core.textures.TextureID;
import zombie.gameStates.IngameState;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCamera;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.areas.SafeHouse;
import zombie.network.CoopMaster;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class UIManager {
	public static int lastMouseX = 0;
	public static int lastMouseY = 0;
	public static IsoObjectPicker.ClickObject Picked = null;
	public static Clock clock;
	public static EnduranceWidget Endurance;
	public static Stack UI = new Stack();
	public static ObjectTooltip toolTip = null;
	public static Texture mouseArrow;
	public static Texture mouseExamine;
	public static Texture mouseAttack;
	public static Texture mouseGrab;
	public static StatsPage StatsPage;
	public static TutorialPanel Tutorial;
	public static HUDButton Inv;
	public static HUDButton Hea;
	public static DoubleSizer Resizer;
	public static DirectionSwitcher directionSwitcher;
	public static MovementBlender BlendTest;
	public static Sidebar sidebar;
	public static SpeedControls speedControls;
	public static InventoryItem DragInventory;
	public static NewCraftingPanel crafting;
	public static NewHealthPanel HealthPanel;
	public static ClothingPanel clothingPanel;
	public static QuestPanel questPanel;
	public static UIDebugConsole DebugConsole;
	public static UIServerToolbox ServerToolbox;
	public static MoodlesUI[] MoodleUI = new MoodlesUI[4];
	public static QuestControl tempQuest;
	public static QuestHUD onscreenQuest;
	public static NewWindow tempQuestWin;
	public static boolean bFadeBeforeUI = false;
	public static ActionProgressBar[] ProgressBar = new ActionProgressBar[4];
	private static boolean showPausedMessage = true;
	public static BodyDamage BD_Test = null;
	public static float FadeAlpha = 1.0F;
	public static int FadeInTimeMax = 180;
	public static int FadeInTime = 180;
	public static boolean FadingOut = false;
	public static Texture lastMouseTexture;
	public static IsoObject LastPicked = null;
	public static Stack DoneTutorials = new Stack();
	public static float lastOffX = 0.0F;
	public static float lastOffY = 0.0F;
	public static boolean lastDoubleSize = false;
	public static boolean DoMouseControls = true;
	public static ModalDialog Modal = null;
	public static boolean KeyDownZoomIn = false;
	public static boolean KeyDownZoomOut = false;
	public static boolean doTick;
	public static boolean VisibleAllUI = true;
	static ArrayList toRemove = new ArrayList();
	static ArrayList toAdd = new ArrayList();
	public static Cursor nativeCursor = null;
	public static TextureFBO UIFBO;
	public static boolean useUIFBO = false;
	public static float FBO_ALPHA_MULT = 1.15F;
	public static Texture black = null;
	public static boolean DOUNUSED = false;
	public static boolean bSuspend = false;
	public static float lastAlpha = 10000.0F;
	public static Vector2 PickedTileLocal = new Vector2();
	public static Vector2 PickedTile = new Vector2();
	static int wheel = 0;
	static int lastwheel = 0;
	public static IsoObject RightDownObject = null;
	static Texture mouse = null;
	public static float uiUpdateCounter = 0.0F;
	public static long uiUpdateTimeMS = 0L;
	public static long uiUpdateIntervalMS = 0L;
	public static long uiRenderTimeMS = 0L;
	public static long uiRenderIntervalMS = 0L;
	public static ArrayList toTop = new ArrayList();
	static ArrayList debugUI = new ArrayList();
	public static KahluaThread defaultthread = null;
	private static UIElement playerInventoryUI;
	private static UIElement playerLootUI;
	private static UIElement playerInventoryTooltip;
	private static UIElement playerLootTooltip;
	static UIManager.Sync sync = new UIManager.Sync();
	private static UIManager.FadeInfo[] playerFadeInfo = new UIManager.FadeInfo[4];

	public static void AddTutorial(float float1, float float2, String string, String string2, boolean boolean1) {
		if (HelpIcon.doOthers) {
			if (!getDoneTutorials().contains(string)) {
				getDoneTutorials().add(string);
				int int1 = (int)float1;
				int int2 = (int)float2;
				HelpIcon helpIcon = new HelpIcon(int1, int2, string, string2);
				helpIcon.setFollowGameWorld(false);
				if (boolean1) {
					helpIcon.Closed = false;
				}

				getUI().add(helpIcon);
			}
		}
	}

	public static void DoModal(String string, String string2, boolean boolean1) {
		if (getModal() != null) {
			getUI().remove(getModal());
		}

		setModal(new ModalDialog(string, string2, boolean1));
		getUI().add(getModal());
	}

	public static void AddUI(UIElement uIElement) {
		toRemove.remove(uIElement);
		toRemove.add(uIElement);
		toAdd.remove(uIElement);
		toAdd.add(uIElement);
	}

	public static void RemoveElement(UIElement uIElement) {
		toAdd.remove(uIElement);
		toRemove.remove(uIElement);
		toRemove.add(uIElement);
	}

	public static void DoModal(String string, String string2, boolean boolean1, UIEventHandler uIEventHandler) {
		if (getModal() != null) {
			getUI().remove(getModal());
		}

		setModal(new ModalDialog(string, string2, boolean1));
		Modal.handler = uIEventHandler;
		getUI().add(getModal());
	}

	public static void AddTutorial(UIElement uIElement, double double1, double double2, String string, String string2, boolean boolean1) {
		if (HelpIcon.doOthers) {
			if (!getDoneTutorials().contains(string)) {
				getDoneTutorials().add(string);
				int int1 = (int)double1;
				int int2 = (int)double2;
				HelpIcon helpIcon = new HelpIcon(int1, int2, string, string2);
				helpIcon.setFollowGameWorld(false);
				if (boolean1) {
					helpIcon.Closed = false;
				}

				helpIcon.follow = uIElement;
				getUI().add(helpIcon);
			}
		}
	}

	public static void AddTutorial(float float1, float float2, float float3, String string, String string2, boolean boolean1, float float4) {
		if (HelpIcon.doOthers) {
			if (!getDoneTutorials().contains(string)) {
				getDoneTutorials().add(string);
				int int1 = (int)IsoUtils.XToScreen(float1, float2, float3, 0);
				int int2 = (int)IsoUtils.YToScreen(float1, float2, float3, 0);
				int1 -= (int)IsoCamera.getOffX();
				int2 -= (int)IsoCamera.getOffY();
				int2 = (int)((float)int2 + float4);
				int1 += 32;
				int2 += 320;
				HelpIcon helpIcon = new HelpIcon(int1, int2, string, string2);
				if (boolean1) {
					helpIcon.Closed = false;
				}

				getUI().add(helpIcon);
			}
		}
	}

	public static void closeContainers() {
	}

	public static void CloseContainers() {
		for (int int1 = getUI().size() - 1; int1 >= 0; --int1) {
			UIElement uIElement = (UIElement)getUI().get(int1);
			if (uIElement instanceof NewContainerPanel) {
				if (getDragInventory() != null && ((NewContainerPanel)uIElement).Flow.Container == getDragInventory().getContainer()) {
					((NewContainerPanel)uIElement).Flow.Container.Items.remove(getDragInventory());
					((NewContainerPanel)uIElement).Flow.Container.dirty = true;
					IsoPlayer.getInstance().getInventory().AddItem(getDragInventory());
				}

				getUI().remove(uIElement);
				--int1;
			}
		}
	}

	public static void DoTutorialEndMessage() {
		EndTutorialMessage endTutorialMessage = new EndTutorialMessage();
		getUI().add(endTutorialMessage);
	}

	public static void DrawTexture(Texture texture, double double1, double double2) {
		double double3 = double1 + (double)texture.offsetX;
		double double4 = double2 + (double)texture.offsetY;
		SpriteRenderer.instance.render(texture, (int)double3, (int)double4, texture.getWidth(), texture.getHeight(), 1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void DrawTexture(Texture texture, double double1, double double2, double double3, double double4, double double5) {
		double double6 = double1 + (double)texture.offsetX;
		double double7 = double2 + (double)texture.offsetY;
		SpriteRenderer.instance.render(texture, (int)double6, (int)double7, (int)double3, (int)double4, 1.0F, 1.0F, 1.0F, (float)double5);
	}

	public static void FadeIn(double double1) {
		setFadeInTimeMax((double)((int)(double1 * 30.0 * (double)((float)PerformanceSettings.LockFPS / 30.0F))));
		setFadeInTime(getFadeInTimeMax());
		setFadingOut(false);
	}

	public static void FadeOut(double double1) {
		setFadeInTimeMax((double)((int)(double1 * 30.0 * (double)((float)PerformanceSettings.LockFPS / 30.0F))));
		setFadeInTime(getFadeInTimeMax());
		setFadingOut(true);
	}

	public static void initCharCreation() {
		getUI().clear();
		setPicked((IsoObjectPicker.ClickObject)null);
		setLastPicked((IsoObject)null);
	}

	public static void CreateFBO(int int1, int int2) {
		if (Core.SafeMode) {
			useUIFBO = false;
		} else {
			if (useUIFBO && (UIFBO == null || UIFBO.getTexture().getWidth() != int1 || UIFBO.getTexture().getHeight() != int2)) {
				if (UIFBO != null) {
					RenderThread.borrowContext();
					UIFBO.destroy();
					RenderThread.returnContext();
				}

				try {
					UIFBO = createTexture((float)int1, (float)int2, false);
				} catch (Exception exception) {
					useUIFBO = false;
					exception.printStackTrace();
				}
			}
		}
	}

	public static TextureFBO createTexture(float float1, float float2, boolean boolean1) throws Exception {
		Texture texture;
		TextureFBO textureFBO;
		if (boolean1) {
			try {
				TextureID.bUseCompression = false;
				texture = new Texture((int)float1, (int)float2);
				textureFBO = new TextureFBO(texture);
				textureFBO.destroy();
			} finally {
				TextureID.bUseCompression = TextureID.bUseCompressionOption;
			}

			return null;
		} else {
			try {
				TextureID.bUseCompression = false;
				texture = new Texture((int)float1, (int)float2);
				textureFBO = new TextureFBO(texture);
			} finally {
				TextureID.bUseCompression = TextureID.bUseCompressionOption;
			}

			return textureFBO;
		}
	}

	public static void init() {
		getUI().clear();
		debugUI.clear();
		clock = null;
		int int1;
		for (int1 = 0; int1 < 4; ++int1) {
			MoodleUI[int1] = null;
		}

		setSpeedControls(new SpeedControls());
		SpeedControls.instance = getSpeedControls();
		setbFadeBeforeUI(false);
		VisibleAllUI = true;
		for (int1 = 0; int1 < 4; ++int1) {
			playerFadeInfo[int1].setFadeBeforeUI(false);
			playerFadeInfo[int1].setFadeTime(0);
			playerFadeInfo[int1].setFadingOut(false);
		}

		setPicked((IsoObjectPicker.ClickObject)null);
		setLastPicked((IsoObject)null);
		RightDownObject = null;
		if (IsoPlayer.getInstance() != null) {
			if (!Core.GameMode.equals("LastStand") && !GameClient.bClient) {
				getUI().add(getSpeedControls());
			}

			if (!GameServer.bServer) {
				if (DOUNUSED) {
					try {
						setTutorial(new TutorialPanel());
					} catch (FileNotFoundException fileNotFoundException) {
						Logger.getLogger(UIManager.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
					}

					setOnscreenQuest(new QuestHUD());
					onscreenQuest.setX((double)(Core.getInstance().getOffscreenWidth(0) - 400));
					onscreenQuest.setWidth(280.0);
					onscreenQuest.setY(4.0);
					getUI().add(getTutorial());
					getUI().add(getOnscreenQuest());
					setDirectionSwitcher(new DirectionSwitcher(Core.getInstance().getOffscreenWidth(0) - 80, Core.getInstance().getOffscreenHeight(0) - 40));
				}

				setToolTip(new ObjectTooltip());
				setClock(new Clock(Core.getInstance().getOffscreenWidth(0) - 110, 10));
				if (!Core.GameMode.equals("LastStand")) {
					getUI().add(getClock());
				}

				getUI().add(getToolTip());
				if (DOUNUSED) {
					setSidebar(new Sidebar(-53, 0));
					setCrafting(new NewCraftingPanel(63, Core.getInstance().getOffscreenHeight(0)));
					crafting.setVisible(false);
					setClothingPanel(new ClothingPanel(63, Core.getInstance().getOffscreenHeight(0), IsoPlayer.getInstance()));
					clothingPanel.setVisible(false);
					setQuestPanel(new QuestPanel(Core.getInstance().getOffscreenWidth(0) - 463, 66));
					questPanel.setVisible(false);
				}

				setDebugConsole(new UIDebugConsole(20, Core.getInstance().getScreenHeight() - 265));
				setServerToolbox(new UIServerToolbox(100, 200));
				if (Core.bDebug) {
					DebugConsole.setVisible(true);
				} else {
					DebugConsole.setVisible(false);
				}

				if (CoopMaster.instance.isRunning()) {
					ServerToolbox.setVisible(true);
				} else {
					ServerToolbox.setVisible(false);
				}

				for (int1 = 0; int1 < 4; ++int1) {
					MoodlesUI moodlesUI = new MoodlesUI();
					setMoodleUI((double)int1, moodlesUI);
					moodlesUI.setVisible(true);
					getUI().add(moodlesUI);
				}

				if (DOUNUSED) {
					MovementBlender movementBlender = new MovementBlender(getSidebar());
					setBlendTest(movementBlender);
					getUI().add(movementBlender);
					getUI().add(getCrafting());
					getUI().add(getClothingPanel());
					getUI().add(getQuestPanel());
				}

				getUI().add(getDebugConsole());
				getUI().add(getServerToolbox());
				setLastMouseTexture(getMouseArrow());
				resize();
				for (int1 = 0; int1 < 4; ++int1) {
					ActionProgressBar actionProgressBar = new ActionProgressBar(300, 300);
					setProgressBar((double)int1, actionProgressBar);
					getUI().add(actionProgressBar);
					actionProgressBar.setValue(1.0F);
					actionProgressBar.setVisible(false);
				}

				playerInventoryUI = null;
				playerLootUI = null;
				LuaEventManager.triggerEvent("OnCreateUI");
			}
		}
	}

	public static void render() {
		if (!useUIFBO || Core.getInstance().UIRenderTick <= 0) {
			if (!bSuspend) {
				long long1 = System.currentTimeMillis();
				uiRenderIntervalMS = Math.min(long1 - uiRenderTimeMS, 1000L);
				uiRenderTimeMS = long1;
				UIElement.StencilLevel = 0;
				FBO_ALPHA_MULT = useUIFBO ? 1.15F : 1.0F;
				UITransition.UpdateAll();
				if (getBlack() == null) {
					setBlack(Texture.getSharedTexture("black.png"));
				}

				if (LuaManager.thread == defaultthread) {
					LuaEventManager.triggerEvent("OnPreUIDraw");
				}

				int int1 = Mouse.getXA();
				int int2 = Mouse.getYA();
				if (IsoPlayer.instance != null && IsoPlayer.instance.IsAiming() && IsoPlayer.instance.getPrimaryHandItem() != null && IsoPlayer.instance.getPrimaryHandItem() instanceof HandWeapon && (IsoPlayer.instance.isCharging || ((HandWeapon)IsoPlayer.instance.getPrimaryHandItem()).isAimedFirearm()) && IsoPlayer.instance.IsUsingAimWeapon()) {
					if (Core.bDoubleSize) {
						Core.getInstance().DrawCircle(IsoPlayer.instance.AimRadius * 2.0F, (float)int1, (float)int2);
					} else {
						Core.getInstance().DrawCircle(IsoPlayer.instance.AimRadius, (float)int1, (float)int2);
					}
				}

				if (isbFadeBeforeUI()) {
					setFadeAlpha((double)(getFadeInTime().floatValue() / getFadeInTimeMax().floatValue()));
					if (getFadeAlpha() > 1.0) {
						setFadeAlpha(1.0);
					}

					if (getFadeAlpha() < 0.0) {
						setFadeAlpha(0.0);
					}

					if (isFadingOut()) {
						setFadeAlpha(1.0 - getFadeAlpha());
					}

					if (IsoCamera.CamCharacter != null && getFadeAlpha() > 0.0) {
						DrawTexture(getBlack(), 0.0, 0.0, (double)Core.getInstance().getScreenWidth(), (double)Core.getInstance().getScreenHeight(), getFadeAlpha());
					}
				}

				setLastAlpha(getFadeAlpha().floatValue());
				int int3;
				for (int3 = 0; int3 < IsoPlayer.numPlayers; ++int3) {
					if (IsoPlayer.players[int3] != null && playerFadeInfo[int3].isFadeBeforeUI()) {
						playerFadeInfo[int3].render();
					}
				}

				for (int3 = 0; int3 < getUI().size(); ++int3) {
					if ((((UIElement)UI.get(int3)).isIgnoreLossControl() || !TutorialManager.instance.StealControl) && !(UI.get(int3) instanceof ActionProgressBar)) {
						try {
							if (((UIElement)getUI().get(int3)).isDefaultDraw()) {
								((UIElement)getUI().get(int3)).render();
							}
						} catch (Exception exception) {
							Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
						}
					}
				}

				if (getToolTip() != null) {
					getToolTip().render();
				}

				if (getDragInventory() != null) {
					if (Core.bDoubleSize) {
						DrawTexture(getDragInventory().getTex(), (double)int1, (double)int2);
						if (getDragInventory().getUses() > 1) {
							TextManager.instance.DrawStringRight((double)(int1 + 32), (double)int2, "x" + (new Integer(getDragInventory().getUses())).toString(), 1.0, 1.0, 1.0, 1.0);
						}
					} else {
						DrawTexture(getDragInventory().getTex(), (double)(int1 + 10), (double)(int2 + 12));
						if (getDragInventory().getUses() > 1) {
							TextManager.instance.DrawStringRight((double)(int1 + 32 + 10), (double)(int2 + 12), "x" + (new Integer(getDragInventory().getUses())).toString(), 1.0, 1.0, 1.0, 1.0);
						}
					}
				}

				if (isShowPausedMessage() && getSpeedControls() != null && getSpeedControls().getCurrentGameSpeed() == 0 && (getModal() == null || !Modal.isVisible()) && VisibleAllUI) {
					String string = Translator.getText("IGUI_GamePaused");
					int int4 = TextManager.instance.MeasureStringX(UIFont.Small, string) + 32;
					int int5 = TextManager.instance.font.getLineHeight();
					int int6 = (int)Math.ceil((double)int5 * 1.5);
					SpriteRenderer.instance.render((Texture)null, Core.getInstance().getScreenWidth() / 2 - int4 / 2, Core.getInstance().getScreenHeight() / 2 - int6 / 2, int4, int6, 0.0F, 0.0F, 0.0F, 0.75F);
					TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2 - int5 / 2), string, 1.0, 1.0, 1.0, 1.0);
				}

				if (!isbFadeBeforeUI()) {
					setFadeAlpha(getFadeInTime() / getFadeInTimeMax());
					if (getFadeAlpha() > 1.0) {
						setFadeAlpha(1.0);
					}

					if (getFadeAlpha() < 0.0) {
						setFadeAlpha(0.0);
					}

					if (isFadingOut()) {
						setFadeAlpha(1.0 - getFadeAlpha());
					}

					if (IsoCamera.CamCharacter != null && getFadeAlpha() > 0.0) {
						DrawTexture(getBlack(), 0.0, 0.0, (double)Core.getInstance().getScreenWidth(), (double)Core.getInstance().getScreenHeight(), getFadeAlpha());
					}
				}

				for (int3 = 0; int3 < IsoPlayer.numPlayers; ++int3) {
					if (IsoPlayer.players[int3] != null && !playerFadeInfo[int3].isFadeBeforeUI()) {
						playerFadeInfo[int3].render();
					}
				}

				PZConsole.instance.render();
				if (LuaManager.thread == defaultthread) {
					LuaEventManager.triggerEvent("OnPostUIDraw");
				}
			}
		}
	}

	public static void resizeCharCreation() {
	}

	public static void resize() {
		if (useUIFBO && UIFBO != null) {
			CreateFBO(Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight());
		}

		if (getClock() != null) {
			setLastDoubleSize(Core.bDoubleSize);
			setLastOffX((float)Core.getInstance().getScreenWidth());
			setLastOffY((float)Core.getInstance().getScreenHeight());
			if (DOUNUSED) {
				Tutorial.setX((double)(Core.getInstance().getScreenWidth() / 2));
				Tutorial.setX(Tutorial.getX() - 300.0);
				Tutorial.setY((double)(Core.getInstance().getScreenHeight() - 60));
			}

			for (int int1 = 0; int1 < 4; ++int1) {
				int int2 = Core.getInstance().getScreenWidth();
				int int3 = Core.getInstance().getScreenHeight();
				byte byte1;
				if (!Clock.instance.isVisible()) {
					byte1 = 24;
				} else {
					byte1 = 64;
				}

				if (int1 == 0 && IsoPlayer.numPlayers > 1 || int1 == 2) {
					int2 /= 2;
				}

				MoodleUI[int1].setX((double)(int2 - 50));
				if ((int1 == 0 || int1 == 1) && IsoPlayer.numPlayers > 1) {
					MoodleUI[int1].setY((double)byte1);
				}

				if (int1 == 2 || int1 == 3) {
					MoodleUI[int1].setY((double)(int3 / 2 + byte1));
				}

				MoodleUI[int1].setVisible(VisibleAllUI && IsoPlayer.players[int1] != null);
			}

			if (DOUNUSED) {
				directionSwitcher.setX((double)(Core.getInstance().getScreenWidth() - 80));
				directionSwitcher.setY((double)(Core.getInstance().getScreenHeight() - 40));
				onscreenQuest.setX((double)(Core.getInstance().getScreenWidth() - 400));
				onscreenQuest.setVisible(false);
				if (tempQuest != null) {
					tempQuest.setVisible(false);
				}
			}

			clock.resize();
			if (IsoPlayer.numPlayers == 1) {
				clock.setX((double)(Core.getInstance().getScreenWidth() - 110));
			} else {
				clock.setX((double)(Core.getInstance().getScreenWidth() / 2 - 50));
				clock.setY((double)(Core.getInstance().getScreenHeight() - 70));
			}

			if (IsoPlayer.numPlayers == 1) {
				speedControls.setX((double)(Core.getInstance().getScreenWidth() - 110));
			} else {
				speedControls.setX((double)(Core.getInstance().getScreenWidth() / 2 - 50));
			}

			if (IsoPlayer.numPlayers == 1 && !clock.isVisible()) {
				speedControls.setY(clock.getY());
			} else {
				speedControls.setY(clock.getY() + clock.getHeight() + 6.0);
			}

			speedControls.setVisible(VisibleAllUI && !IsoPlayer.allPlayersDead());
			if (DOUNUSED) {
				sidebar.height = (float)Core.getInstance().getScreenHeight();
			}
		}
	}

	public static Vector2 getTileFromMouse(double double1, double double2, double double3) {
		PickedTile.x = IsoUtils.XToIso((float)(double1 - 0.0), (float)(double2 - 0.0), (float)double3);
		PickedTile.y = IsoUtils.YToIso((float)(double1 - 0.0), (float)(double2 - 0.0), (float)double3);
		PickedTileLocal.x = getPickedTile().x - (float)((int)getPickedTile().x);
		PickedTileLocal.y = getPickedTile().y - (float)((int)getPickedTile().y);
		PickedTile.x = (float)((int)getPickedTile().x);
		PickedTile.y = (float)((int)getPickedTile().y);
		return getPickedTile();
	}

	public static void update() {
		if (!bSuspend) {
			if (nativeCursor == null) {
			}

			if (!IngameState.DebugPathfinding && !IngameState.AlwaysDebugPathfinding) {
				if (!toRemove.isEmpty()) {
					UI.removeAll(toRemove);
				}

				toRemove.clear();
				if (!toAdd.isEmpty()) {
					UI.addAll(toAdd);
				}

				toAdd.clear();
				setFadeInTime(getFadeInTime() - 1.0);
				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					playerFadeInfo[int1].update();
				}

				if (getHealthPanel() != null) {
					getHealthPanel().SetCharacter(IsoCamera.CamCharacter);
				}

				Stack stack = new Stack();
				if (getDragInventory() != null && !IsoPlayer.instance.getInventory().contains(DragInventory.getType()) && getDragInventory().getContainer() == IsoPlayer.getInstance().getInventory()) {
					setDragInventory((InventoryItem)null);
				}

				uiUpdateCounter += GameTime.instance.getMultiplier();
				if (uiUpdateCounter >= 5.0F) {
					doTick = true;
					long long1 = System.currentTimeMillis();
					uiUpdateIntervalMS = Math.min(long1 - uiUpdateTimeMS, 1000L);
					uiUpdateTimeMS = long1;
					uiUpdateCounter = 0.0F;
				} else {
					doTick = false;
				}

				boolean boolean1 = false;
				boolean boolean2 = false;
				boolean boolean3 = false;
				int int2 = Mouse.getXA();
				int int3 = Mouse.getYA();
				int int4 = Mouse.getX();
				int int5 = Mouse.getY();
				if (getBlendTest() != null && !BlendTest.Running()) {
				}

				int int6;
				UIElement uIElement;
				for (int6 = getUI().size() - 1; int6 >= 0; --int6) {
					if (((UIElement)getUI().get(int6)).getParent() != null) {
						getUI().remove(int6);
						throw new IllegalStateException();
					}

					if (((UIElement)getUI().get(int6)).isFollowGameWorld() || getUI().get(int6) instanceof HelpIcon) {
						stack.add(getUI().get(int6));
					}

					if (getUI().get(int6) instanceof ObjectTooltip) {
						uIElement = (UIElement)getUI().remove(int6);
						getUI().add(uIElement);
					}

					if (getUI().get(int6) instanceof TutorialPanel) {
						uIElement = (UIElement)getUI().remove(int6);
						getUI().add(uIElement);
					}
				}

				for (int6 = 0; int6 < getUI().size(); ++int6) {
					if (((UIElement)getUI().get(int6)).alwaysOnTop || toTop.contains(getUI().get(int6))) {
						uIElement = (UIElement)getUI().remove(int6);
						--int6;
						toAdd.add(uIElement);
					}
				}

				getUI().addAll(toAdd);
				toAdd.clear();
				toTop.clear();
				for (int6 = 0; int6 < getUI().size(); ++int6) {
					if (((UIElement)getUI().get(int6)).alwaysBack) {
						uIElement = (UIElement)getUI().remove(int6);
						getUI().insertElementAt(uIElement, 0);
					}
				}

				for (int6 = 0; int6 < stack.size(); ++int6) {
					getUI().remove(stack.get(int6));
					getUI().insertElementAt(stack.elementAt(int6), 0);
				}

				if (Mouse.isLeftPressed()) {
					Core.UnfocusActiveTextEntryBox();
					for (int6 = getUI().size() - 1; int6 >= 0; --int6) {
						uIElement = (UIElement)getUI().get(int6);
						if ((getModal() == null || getModal() == uIElement || !getModal().isVisible()) && (uIElement.isIgnoreLossControl() || !TutorialManager.instance.StealControl) && uIElement.isVisible()) {
							if ((!((double)int2 >= uIElement.getX()) || !((double)int3 >= uIElement.getY()) || !((double)int2 < uIElement.getX() + uIElement.getWidth()) || !((double)int3 < uIElement.getY() + uIElement.getHeight())) && !uIElement.isCapture()) {
								uIElement.onMouseDownOutside((double)(int2 - uIElement.getX().intValue()), (double)(int3 - uIElement.getY().intValue()));
							} else if (uIElement.onMouseDown((double)(int2 - uIElement.getX().intValue()), (double)(int3 - uIElement.getY().intValue()))) {
								boolean1 = true;
								break;
							}
						}
					}

					if (getSidebar() != null) {
						sidebar.setClickedValue((String)null);
					}

					if (Picked != null && Picked.tile != null && !boolean1) {
						LuaEventManager.triggerEvent("OnObjectLeftMouseButtonDown", Picked.tile, int2, int3);
					}

					if (!boolean1 && !GameKeyboard.isKeyDown(57)) {
						if (getBlendTest() != null) {
							getBlendTest().MoveTo(-53.0F, 0.0F, 0.4F);
						}

						if (NewCraftingPanel.instance != null) {
							NewCraftingPanel.instance.setVisible(false);
						}
					}

					if (!boolean1) {
						if (IsoPlayer.getInstance() != null && IsoPlayer.getInstance().getGuardModeUI() > 0 && getPicked().square.isFree(false)) {
							if (IsoPlayer.getInstance().getGuardModeUI() == 1) {
								IsoPlayer.instance.setGuardStand(getPicked().square);
								IsoPlayer.instance.setGuardModeUI(IsoPlayer.instance.getGuardModeUI() + 1);
							} else if (IsoPlayer.getInstance().getGuardModeUI() == 2) {
								IsoPlayer.instance.setGuardFace(getPicked().square);
								IsoPlayer.instance.setGuardModeUI(0);
								IsoPlayer.getInstance().getGuardChosen().DoGuard(IsoPlayer.getInstance());
							}
						}

						LuaEventManager.triggerEvent("OnMouseDown", int2, int3);
						CloseContainers();
						if (IsoWorld.instance.CurrentCell != null && !IsoWorld.instance.CurrentCell.DoBuilding(0, false) && getPicked() != null && (getSpeedControls() == null || getSpeedControls().getCurrentGameSpeed() > 0) && IsoPlayer.getInstance() != null && !IsoPlayer.instance.isIsAiming() && !IsoPlayer.instance.isAsleep()) {
							getPicked().tile.onMouseLeftClick(getPicked().lx, getPicked().ly);
						}
					} else {
						Mouse.UIBlockButtonDown(0);
					}
				}

				UIElement uIElement2;
				int int7;
				boolean boolean4;
				if (Mouse.isLeftReleased()) {
					boolean4 = false;
					for (int7 = getUI().size() - 1; int7 >= 0; --int7) {
						uIElement2 = (UIElement)getUI().get(int7);
						if ((uIElement2.isIgnoreLossControl() || !TutorialManager.instance.StealControl) && uIElement2.isVisible() && (getModal() == null || getModal() == uIElement2 || !getModal().isVisible())) {
							if ((!((double)int2 >= uIElement2.getX()) || !((double)int3 >= uIElement2.getY()) || !((double)int2 < uIElement2.getX() + uIElement2.getWidth()) || !((double)int3 < uIElement2.getY() + uIElement2.getHeight())) && !uIElement2.isCapture()) {
								uIElement2.onMouseUpOutside((double)(int2 - uIElement2.getX().intValue()), (double)(int3 - uIElement2.getY().intValue()));
							} else if (uIElement2.onMouseUp((double)(int2 - uIElement2.getX().intValue()), (double)(int3 - uIElement2.getY().intValue()))) {
								boolean4 = true;
								break;
							}
						}
					}

					if (!boolean4) {
						LuaEventManager.triggerEvent("OnMouseUp", int2, int3);
						if (Picked != null && Picked.tile != null && !boolean1) {
							LuaEventManager.triggerEvent("OnObjectLeftMouseButtonUp", Picked.tile, int2, int3);
						}
					}
				}

				if (Mouse.isRightPressed()) {
					for (int6 = getUI().size() - 1; int6 >= 0; --int6) {
						uIElement = (UIElement)getUI().get(int6);
						if (uIElement.isVisible() && (getModal() == null || getModal() == uIElement || !getModal().isVisible())) {
							if ((!((double)int2 >= uIElement.getX()) || !((double)int3 >= uIElement.getY()) || !((double)int2 < uIElement.getX() + uIElement.getWidth()) || !((double)int3 < uIElement.getY() + uIElement.getHeight())) && !uIElement.isCapture()) {
								uIElement.onRightMouseDownOutside((double)(int2 - uIElement.getX().intValue()), (double)(int3 - uIElement.getY().intValue()));
							} else if (uIElement.onRightMouseDown((double)(int2 - uIElement.getX().intValue()), (double)(int3 - uIElement.getY().intValue()))) {
								boolean2 = true;
								break;
							}
						}
					}

					if (!boolean2) {
						LuaEventManager.triggerEvent("OnRightMouseDown", int2, int3);
						if (Picked != null && Picked.tile != null && !boolean2) {
							LuaEventManager.triggerEvent("OnObjectRightMouseButtonDown", Picked.tile, int2, int3);
						}
					} else {
						Mouse.UIBlockButtonDown(1);
					}

					if (IsoWorld.instance.CurrentCell != null && getPicked() != null && getSpeedControls() != null && !IsoPlayer.instance.isIsAiming() && !IsoPlayer.instance.isAsleep() && getSpeedControls().getCurrentGameSpeed() > 0) {
						getSpeedControls().SetCurrentGameSpeed(1);
						getPicked().tile.onMouseRightClick(getPicked().lx, getPicked().ly);
						setRightDownObject(getPicked().tile);
					}
				}

				boolean boolean5;
				if (Mouse.isRightReleased()) {
					boolean4 = false;
					if (IsoPlayer.getInstance() != null && IsoPlayer.getInstance().getGuardModeUI() > 0) {
						IsoPlayer.instance.setGuardModeUI(0);
					}

					boolean5 = false;
					for (int6 = getUI().size() - 1; int6 >= 0; --int6) {
						uIElement2 = (UIElement)getUI().get(int6);
						if ((uIElement2.isIgnoreLossControl() || !TutorialManager.instance.StealControl) && uIElement2.isVisible() && (getModal() == null || getModal() == uIElement2 || !getModal().isVisible())) {
							if ((!((double)int2 >= uIElement2.getX()) || !((double)int3 >= uIElement2.getY()) || !((double)int2 < uIElement2.getX() + uIElement2.getWidth()) || !((double)int3 < uIElement2.getY() + uIElement2.getHeight())) && !uIElement2.isCapture()) {
								uIElement2.onRightMouseUpOutside((double)(int2 - uIElement2.getX().intValue()), (double)(int3 - uIElement2.getY().intValue()));
							} else if (uIElement2.onRightMouseUp((double)(int2 - uIElement2.getX().intValue()), (double)(int3 - uIElement2.getY().intValue()))) {
								boolean5 = true;
								break;
							}
						}
					}

					if (!boolean5) {
						LuaEventManager.triggerEvent("OnRightMouseUp", int2, int3);
						if (Picked != null && Picked.tile != null) {
							boolean boolean6 = true;
							if (GameClient.bClient && Picked.tile.getSquare() != null) {
								SafeHouse safeHouse = SafeHouse.isSafeHouse(Picked.tile.getSquare(), IsoPlayer.getInstance().getUsername(), true);
								if (safeHouse != null) {
									boolean6 = false;
								}
							}

							if (boolean6) {
								LuaEventManager.triggerEvent("OnObjectRightMouseButtonUp", Picked.tile, int2, int3);
							}
						}
					}

					if (int6 == -1) {
						setDragInventory((InventoryItem)null);
						if (getPicked() != null) {
						}
					}

					if (IsoPlayer.getInstance() != null && IsoPlayer.getInstance() != null) {
						IsoPlayer.instance.setDragObject((IsoMovingObject)null);
					}

					if (IsoWorld.instance.CurrentCell != null && getRightDownObject() != null && IsoPlayer.getInstance() != null && !IsoPlayer.instance.IsAiming() && !IsoPlayer.instance.isAsleep()) {
						getRightDownObject().onMouseRightReleased();
						setRightDownObject((IsoObject)null);
					}
				}

				lastwheel = 0;
				wheel = Mouse.getWheelState();
				boolean4 = false;
				if (wheel != lastwheel) {
					int7 = wheel - lastwheel < 0 ? 1 : -1;
					for (int int8 = getUI().size() - 1; int8 >= 0; --int8) {
						UIElement uIElement3 = (UIElement)getUI().get(int8);
						if ((uIElement3.isIgnoreLossControl() || !TutorialManager.instance.StealControl) && uIElement3.isVisible() && (uIElement3.isPointOver((double)int2, (double)int3) || uIElement3.isCapture()) && uIElement3.onMouseWheel((double)int7)) {
							boolean4 = true;
							break;
						}
					}

					if (!boolean4) {
						Core.getInstance().doZoomScroll(0, int7);
					}
				}

				if (getLastMouseX() != (double)int2 || getLastMouseY() != (double)int3) {
					setDoMouseControls(true);
					for (int7 = getUI().size() - 1; int7 >= 0; --int7) {
						uIElement2 = (UIElement)getUI().get(int7);
						if ((uIElement2.isIgnoreLossControl() || !TutorialManager.instance.StealControl) && uIElement2.isVisible()) {
							if ((!((double)int2 >= uIElement2.getX()) || !((double)int3 >= uIElement2.getY()) || !((double)int2 < uIElement2.getX() + uIElement2.getWidth()) || !((double)int3 < uIElement2.getY() + uIElement2.getHeight())) && !uIElement2.isCapture()) {
								uIElement2.onMouseMoveOutside((double)int2 - getLastMouseX(), (double)int3 - getLastMouseY());
							} else if (!boolean3 && uIElement2.onMouseMove((double)int2 - getLastMouseX(), (double)int3 - getLastMouseY())) {
								boolean3 = true;
							}
						}
					}
				}

				if (!isDoMouseControls() && GameWindow.ActivatedJoyPad != null) {
					setPicked(IsoObjectPicker.Instance.ClickObjectStore[IsoObjectPicker.Instance.ThisFrame.size() + 1000]);
					Picked.tile = IsoPlayer.getInstance().getInteract();
					if (getPicked().tile == null) {
						setPicked((IsoObjectPicker.ClickObject)null);
						setLastPicked((IsoObject)null);
					}
				} else if (!boolean3 && IsoPlayer.players[0] != null) {
					setPicked(IsoObjectPicker.Instance.ContextPick(int2, int3));
					if (IsoCamera.CamCharacter != null) {
						setPickedTile(getTileFromMouse((double)int4, (double)int5, (double)((int)IsoPlayer.players[0].getZ())));
					}

					LuaEventManager.triggerEvent("OnMouseMove", BoxedStaticValues.toDouble((double)int2), BoxedStaticValues.toDouble((double)int3), BoxedStaticValues.toDouble((double)int4), BoxedStaticValues.toDouble((double)int5));
				} else {
					Mouse.UIBlockButtonDown(2);
				}

				setLastMouseX((double)int2);
				setLastMouseY((double)int3);
				for (int7 = 0; int7 < getUI().size(); ++int7) {
					((UIElement)getUI().get(int7)).update();
				}

				updateTooltip((double)int2, (double)int3);
				handleZoomKeys();
				if (Math.abs(IsoCamera.OffX[IsoPlayer.getPlayerIndex()] - IsoCamera.lastOffX[IsoPlayer.getPlayerIndex()]) > 10.0F || Math.abs(IsoCamera.OffY[IsoPlayer.getPlayerIndex()] - IsoCamera.lastOffY[IsoPlayer.getPlayerIndex()]) > 10.0F) {
					boolean5 = false;
				}

				IsoCamera.lastOffX = IsoCamera.OffX;
				IsoCamera.lastOffY = IsoCamera.OffY;
				UpdateMouse();
			}
		}
	}

	private static void UpdateMouse() {
		Texture texture = getLastMouseTexture();
		if (getPicked() != null) {
			if (getPicked().tile instanceof IsoZombie) {
				IsoZombie zombie = (IsoZombie)getPicked().tile;
				if (IsoPlayer.getInstance().IsAttackRange(zombie.getX(), zombie.getY(), zombie.getZ())) {
					texture = getMouseAttack();
				}
			} else if (IsoPlayer.instance != null && IsoPlayer.instance.isIsAiming()) {
				texture = getMouseAttack();
			} else {
				texture = getMouseArrow();
			}
		}

		PZConsole.instance.update();
	}

	private static void handleZoomKeys() {
		boolean boolean1 = true;
		if (Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.IsEditable && Core.CurrentTextEntryBox.DoingTextEntry) {
			boolean1 = false;
		}

		if (getSpeedControls() == null || getSpeedControls().getCurrentGameSpeed() == 0) {
			boolean1 = false;
		}

		if (Keyboard.isKeyDown(Core.getInstance().getKey("Zoom in"))) {
			if (boolean1 && !KeyDownZoomIn) {
				Core.getInstance().doZoomScroll(0, -1);
			}

			KeyDownZoomIn = true;
		} else {
			KeyDownZoomIn = false;
		}

		if (Keyboard.isKeyDown(Core.getInstance().getKey("Zoom out"))) {
			if (boolean1 && !KeyDownZoomOut) {
				Core.getInstance().doZoomScroll(0, 1);
			}

			KeyDownZoomOut = true;
		} else {
			KeyDownZoomOut = false;
		}
	}

	public static NewContainerPanel getOpenContainer() {
		for (int int1 = 0; int1 < getUI().size(); ++int1) {
			if (getUI().get(int1) instanceof NewContainerPanel) {
				return (NewContainerPanel)getUI().get(int1);
			}
		}

		return null;
	}

	public static Double getLastMouseX() {
		return BoxedStaticValues.toDouble((double)lastMouseX);
	}

	public static void setLastMouseX(double double1) {
		lastMouseX = (int)double1;
	}

	public static Double getLastMouseY() {
		return BoxedStaticValues.toDouble((double)lastMouseY);
	}

	public static void setLastMouseY(double double1) {
		lastMouseY = (int)double1;
	}

	public static IsoObjectPicker.ClickObject getPicked() {
		return Picked;
	}

	public static void setPicked(IsoObjectPicker.ClickObject clickObject) {
		Picked = clickObject;
	}

	public static Clock getClock() {
		return clock;
	}

	public static void setClock(Clock clock) {
		clock = clock;
	}

	public static EnduranceWidget getEndurance() {
		return Endurance;
	}

	public static void setEndurance(EnduranceWidget enduranceWidget) {
		Endurance = enduranceWidget;
	}

	public static Stack getUI() {
		return UI;
	}

	public static void setUI(Stack stack) {
		UI = stack;
	}

	public static ObjectTooltip getToolTip() {
		return toolTip;
	}

	public static void setToolTip(ObjectTooltip objectTooltip) {
		toolTip = objectTooltip;
	}

	public static Texture getMouseArrow() {
		return mouseArrow;
	}

	public static void setMouseArrow(Texture texture) {
		mouseArrow = texture;
	}

	public static Texture getMouseExamine() {
		return mouseExamine;
	}

	public static void setMouseExamine(Texture texture) {
		mouseExamine = texture;
	}

	public static Texture getMouseAttack() {
		return mouseAttack;
	}

	public static void setMouseAttack(Texture texture) {
		mouseAttack = texture;
	}

	public static Texture getMouseGrab() {
		return mouseGrab;
	}

	public static void setMouseGrab(Texture texture) {
		mouseGrab = texture;
	}

	public static StatsPage getStatsPage() {
		return StatsPage;
	}

	public static void setStatsPage(StatsPage statsPage) {
		StatsPage = statsPage;
	}

	public static TutorialPanel getTutorial() {
		return Tutorial;
	}

	public static void setTutorial(TutorialPanel tutorialPanel) {
		Tutorial = tutorialPanel;
	}

	public static HUDButton getInv() {
		return Inv;
	}

	public static void setInv(HUDButton hUDButton) {
		Inv = hUDButton;
	}

	public static HUDButton getHea() {
		return Hea;
	}

	public static void setHea(HUDButton hUDButton) {
		Hea = hUDButton;
	}

	public static DoubleSizer getResizer() {
		return Resizer;
	}

	public static void setResizer(DoubleSizer doubleSizer) {
		Resizer = doubleSizer;
	}

	public static DirectionSwitcher getDirectionSwitcher() {
		return directionSwitcher;
	}

	public static void setDirectionSwitcher(DirectionSwitcher directionSwitcher) {
		directionSwitcher = directionSwitcher;
	}

	public static MovementBlender getBlendTest() {
		return BlendTest;
	}

	public static void setBlendTest(MovementBlender movementBlender) {
		BlendTest = movementBlender;
	}

	public static Sidebar getSidebar() {
		return sidebar;
	}

	public static void setSidebar(Sidebar sidebar) {
		sidebar = sidebar;
	}

	public static SpeedControls getSpeedControls() {
		return speedControls;
	}

	public static void setSpeedControls(SpeedControls speedControls) {
		speedControls = speedControls;
	}

	public static InventoryItem getDragInventory() {
		return DragInventory;
	}

	public static void setDragInventory(InventoryItem inventoryItem) {
		DragInventory = inventoryItem;
	}

	public static NewCraftingPanel getCrafting() {
		return crafting;
	}

	public static void setCrafting(NewCraftingPanel newCraftingPanel) {
		crafting = newCraftingPanel;
	}

	public static NewHealthPanel getHealthPanel() {
		return HealthPanel;
	}

	public static void setHealthPanel(NewHealthPanel newHealthPanel) {
		HealthPanel = newHealthPanel;
	}

	public static ClothingPanel getClothingPanel() {
		return clothingPanel;
	}

	public static void setClothingPanel(ClothingPanel clothingPanel) {
		clothingPanel = clothingPanel;
	}

	public static QuestPanel getQuestPanel() {
		return questPanel;
	}

	public static void setQuestPanel(QuestPanel questPanel) {
		questPanel = questPanel;
	}

	public static UIDebugConsole getDebugConsole() {
		return DebugConsole;
	}

	public static void setDebugConsole(UIDebugConsole uIDebugConsole) {
		DebugConsole = uIDebugConsole;
	}

	public static UIServerToolbox getServerToolbox() {
		return ServerToolbox;
	}

	public static void setServerToolbox(UIServerToolbox uIServerToolbox) {
		ServerToolbox = uIServerToolbox;
	}

	public static MoodlesUI getMoodleUI(double double1) {
		return MoodleUI[(int)double1];
	}

	public static void setMoodleUI(double double1, MoodlesUI moodlesUI) {
		MoodleUI[(int)double1] = moodlesUI;
	}

	public static QuestControl getTempQuest() {
		return tempQuest;
	}

	public static void setTempQuest(QuestControl questControl) {
		tempQuest = questControl;
	}

	public static QuestHUD getOnscreenQuest() {
		return onscreenQuest;
	}

	public static void setOnscreenQuest(QuestHUD questHUD) {
		onscreenQuest = questHUD;
	}

	public static NewWindow getTempQuestWin() {
		return tempQuestWin;
	}

	public static void setTempQuestWin(NewWindow newWindow) {
		tempQuestWin = newWindow;
	}

	public static boolean isbFadeBeforeUI() {
		return bFadeBeforeUI;
	}

	public static void setbFadeBeforeUI(boolean boolean1) {
		bFadeBeforeUI = boolean1;
	}

	public static ActionProgressBar getProgressBar(double double1) {
		return ProgressBar[(int)double1];
	}

	public static void setProgressBar(double double1, ActionProgressBar actionProgressBar) {
		ProgressBar[(int)double1] = actionProgressBar;
	}

	public static BodyDamage getBD_Test() {
		return BD_Test;
	}

	public static void setBD_Test(BodyDamage bodyDamage) {
		BD_Test = bodyDamage;
	}

	public static Double getFadeAlpha() {
		return BoxedStaticValues.toDouble((double)FadeAlpha);
	}

	public static void setFadeAlpha(double double1) {
		FadeAlpha = (float)double1;
	}

	public static Double getFadeInTimeMax() {
		return BoxedStaticValues.toDouble((double)FadeInTimeMax);
	}

	public static void setFadeInTimeMax(double double1) {
		FadeInTimeMax = (int)double1;
	}

	public static Double getFadeInTime() {
		return BoxedStaticValues.toDouble((double)FadeInTime);
	}

	public static void setFadeInTime(double double1) {
		FadeInTime = Math.max((int)double1, 0);
	}

	public static Boolean isFadingOut() {
		return FadingOut ? Boolean.TRUE : Boolean.FALSE;
	}

	public static void setFadingOut(boolean boolean1) {
		FadingOut = boolean1;
	}

	public static Texture getLastMouseTexture() {
		return lastMouseTexture;
	}

	public static void setLastMouseTexture(Texture texture) {
		lastMouseTexture = texture;
	}

	public static IsoObject getLastPicked() {
		return LastPicked;
	}

	public static void setLastPicked(IsoObject object) {
		LastPicked = object;
	}

	public static Stack getDoneTutorials() {
		return DoneTutorials;
	}

	public static void setDoneTutorials(Stack stack) {
		DoneTutorials = stack;
	}

	public static float getLastOffX() {
		return lastOffX;
	}

	public static void setLastOffX(float float1) {
		lastOffX = float1;
	}

	public static float getLastOffY() {
		return lastOffY;
	}

	public static void setLastOffY(float float1) {
		lastOffY = float1;
	}

	public static boolean isLastDoubleSize() {
		return lastDoubleSize;
	}

	public static void setLastDoubleSize(boolean boolean1) {
		lastDoubleSize = boolean1;
	}

	public static boolean isDoMouseControls() {
		return DoMouseControls;
	}

	public static void setDoMouseControls(boolean boolean1) {
		DoMouseControls = boolean1;
	}

	public static ModalDialog getModal() {
		return Modal;
	}

	public static void setModal(ModalDialog modalDialog) {
		Modal = modalDialog;
	}

	public static Texture getBlack() {
		return black;
	}

	public static void setBlack(Texture texture) {
		black = texture;
	}

	public static float getLastAlpha() {
		return lastAlpha;
	}

	public static void setLastAlpha(float float1) {
		lastAlpha = float1;
	}

	public static Vector2 getPickedTileLocal() {
		return PickedTileLocal;
	}

	public static void setPickedTileLocal(Vector2 vector2) {
		PickedTileLocal = vector2;
	}

	public static Vector2 getPickedTile() {
		return PickedTile;
	}

	public static void setPickedTile(Vector2 vector2) {
		PickedTile = vector2;
	}

	public static IsoObject getRightDownObject() {
		return RightDownObject;
	}

	public static void setRightDownObject(IsoObject object) {
		RightDownObject = object;
	}

	static void pushToTop(UIElement uIElement) {
		toTop.add(uIElement);
	}

	public static boolean isShowPausedMessage() {
		return showPausedMessage;
	}

	public static void setShowPausedMessage(boolean boolean1) {
		showPausedMessage = boolean1;
	}

	public static void debugBreakpoint(String string, long long1) {
		if (!GameServer.bServer) {
			defaultthread = LuaManager.debugthread;
			int int1 = Core.getInstance().frameStage;
			if (int1 != 0) {
				if (int1 <= 1) {
					Core.getInstance().EndFrame(0);
				}

				if (int1 <= 2) {
					Core.getInstance().StartFrameUI();
				}

				if (int1 <= 3) {
					Core.getInstance().EndFrameUI();
				}
			}

			ArrayList arrayList = new ArrayList();
			boolean boolean1 = bSuspend;
			arrayList.addAll(UI);
			UI.clear();
			bSuspend = false;
			setShowPausedMessage(false);
			boolean boolean2 = false;
			boolean[] booleanArray = new boolean[11];
			for (int int2 = 0; int2 < 11; ++int2) {
				booleanArray[int2] = true;
			}

			boolean boolean3 = true;
			if (debugUI.size() == 0) {
				LuaManager.debugcaller.pcall(LuaManager.debugthread, LuaManager.env.rawget("DoLuaDebugger"), string, long1);
			} else {
				UI.addAll(debugUI);
				LuaManager.debugcaller.pcall(LuaManager.debugthread, LuaManager.env.rawget("DoLuaDebuggerOnBreak"), string, long1);
			}

			sync.begin();
			while (!boolean2) {
				if (Display.isCloseRequested()) {
					System.exit(0);
				}

				if (!boolean3 && Keyboard.isKeyDown(Core.getInstance().getKey("Toggle Lua Debugger"))) {
					executeGame(arrayList, boolean1, int1);
					return;
				}

				sync.startFrame();
				for (int int3 = 0; int3 < 11; ++int3) {
					boolean boolean4 = Keyboard.isKeyDown(59 + int3);
					if (boolean4) {
						if (!booleanArray[int3]) {
							if (int3 + 1 == 5) {
								LuaManager.thread.bStep = true;
								LuaManager.thread.bStepInto = true;
								executeGame(arrayList, boolean1, int1);
								return;
							}

							if (int3 + 1 == 6) {
								LuaManager.thread.bStep = true;
								LuaManager.thread.bStepInto = false;
								LuaManager.thread.lastCallFrame = LuaManager.thread.getCurrentCoroutine().getCallframeTop();
								executeGame(arrayList, boolean1, int1);
								return;
							}
						}

						booleanArray[int3] = true;
					} else {
						booleanArray[int3] = false;
					}
				}

				Mouse.update();
				GameKeyboard.update();
				Core.getInstance().DoFrameReady();
				update();
				Core.getInstance().StartFrame(0, true);
				Core.getInstance().EndFrame(0);
				Core.getInstance().RenderOffScreenBuffer();
				Core.getInstance().StartFrameUI();
				if (!Keyboard.isKeyDown(Core.getInstance().getKey("Toggle Lua Debugger"))) {
					boolean3 = false;
				}

				render();
				Core.getInstance().EndFrameUI();
				resize();
				sync.endFrame();
				Core.getInstance().setScreenSize(Display.getWidth(), Display.getHeight());
			}
		}
	}

	private static void executeGame(ArrayList arrayList, boolean boolean1, int int1) {
		debugUI.clear();
		debugUI.addAll(UI);
		UI.clear();
		UI.addAll(arrayList);
		bSuspend = boolean1;
		setShowPausedMessage(true);
		if (!LuaManager.thread.bStep && int1 != 0) {
			if (int1 == 1) {
				Core.getInstance().StartFrame(0, true);
			}

			if (int1 == 2) {
				Core.getInstance().StartFrame(0, true);
				Core.getInstance().EndFrame(0);
			}

			if (int1 == 3) {
				Core.getInstance().StartFrame(0, true);
				Core.getInstance().EndFrame(0);
				Core.getInstance().StartFrameUI();
			}
		}

		defaultthread = LuaManager.thread;
	}

	public static KahluaThread getDefaultThread() {
		if (defaultthread == null) {
			defaultthread = LuaManager.thread;
		}

		return defaultthread;
	}

	public static Double getDoubleClickInterval() {
		return BoxedStaticValues.toDouble(500.0);
	}

	public static Double getDoubleClickDist() {
		return BoxedStaticValues.toDouble(5.0);
	}

	public static Boolean isDoubleClick(double double1, double double2, double double3, double double4, double double5) {
		if (Math.abs(double3 - double1) > getDoubleClickDist()) {
			return false;
		} else if (Math.abs(double4 - double2) > getDoubleClickDist()) {
			return false;
		} else {
			return (double)System.currentTimeMillis() - double5 > getDoubleClickInterval() ? Boolean.FALSE : Boolean.TRUE;
		}
	}

	protected static void updateTooltip(double double1, double double2) {
		UIElement uIElement = null;
		for (int int1 = getUI().size() - 1; int1 >= 0; --int1) {
			UIElement uIElement2 = (UIElement)getUI().get(int1);
			if (uIElement2 != toolTip && uIElement2.isVisible() && double1 >= uIElement2.getX() && double2 >= uIElement2.getY() && double1 < uIElement2.getX() + uIElement2.getWidth() && double2 < uIElement2.getY() + uIElement2.getHeight() && (uIElement2.maxDrawHeight == -1 || double2 < uIElement2.getY() + (double)uIElement2.maxDrawHeight)) {
				uIElement = uIElement2;
				break;
			}
		}

		IsoObject object = null;
		if (uIElement == null && getPicked() != null) {
			object = getPicked().tile;
			if (object != getLastPicked() && toolTip != null) {
				toolTip.targetAlpha = 0.0F;
				if (object.haveSpecialTooltip()) {
					if (getToolTip().Object != object) {
						getToolTip().show(object, (double)((int)double1 + 8), (double)((int)double2 + 16));
						if (toolTip.isVisible()) {
							toolTip.showDelay = 0;
						}
					} else {
						toolTip.targetAlpha = 1.0F;
					}
				}
			}
		}

		setLastPicked(object);
		if (toolTip != null && (object == null || toolTip.alpha <= 0.0F && toolTip.targetAlpha <= 0.0F)) {
			toolTip.hide();
		}
	}

	public static void setPlayerInventory(int int1, UIElement uIElement, UIElement uIElement2) {
		if (int1 == 0) {
			playerInventoryUI = uIElement;
			playerLootUI = uIElement2;
		}
	}

	public static void setPlayerInventoryTooltip(int int1, UIElement uIElement, UIElement uIElement2) {
		if (int1 == 0) {
			playerInventoryTooltip = uIElement;
			playerLootTooltip = uIElement2;
		}
	}

	public static boolean isMouseOverInventory() {
		if (playerInventoryTooltip != null && playerInventoryTooltip.isMouseOver()) {
			return true;
		} else if (playerLootTooltip != null && playerLootTooltip.isMouseOver()) {
			return true;
		} else if (playerInventoryUI != null && playerLootUI != null) {
			return playerInventoryUI.isMouseOver() || playerLootUI.isMouseOver();
		} else {
			return false;
		}
	}

	public static void updateBeforeFadeOut() {
		if (!toRemove.isEmpty()) {
			UI.removeAll(toRemove);
			toRemove.clear();
		}

		if (!toAdd.isEmpty()) {
			UI.addAll(toAdd);
			toAdd.clear();
		}
	}

	public static void setVisibleAllUI(boolean boolean1) {
		VisibleAllUI = boolean1;
	}

	public static void setFadeBeforeUI(int int1, boolean boolean1) {
		playerFadeInfo[int1].setFadeBeforeUI(boolean1);
	}

	public static float getFadeAlpha(double double1) {
		return playerFadeInfo[(int)double1].getFadeAlpha();
	}

	public static void setFadeTime(double double1, double double2) {
		playerFadeInfo[(int)double1].setFadeTime((int)double2);
	}

	public static void FadeIn(double double1, double double2) {
		playerFadeInfo[(int)double1].FadeIn((int)double2);
	}

	public static void FadeOut(double double1, double double2) {
		playerFadeInfo[(int)double1].FadeOut((int)double2);
	}

	public static boolean isFBOActive() {
		return useUIFBO;
	}

	public static double getMillisSinceLastUpdate() {
		return (double)uiUpdateIntervalMS;
	}

	public static double getSecondsSinceLastUpdate() {
		return (double)uiUpdateIntervalMS / 1000.0;
	}

	public static double getMillisSinceLastRender() {
		return (double)uiRenderIntervalMS;
	}

	public static double getSecondsSinceLastRender() {
		return (double)uiRenderIntervalMS / 1000.0;
	}

	static  {
	for (int var0 = 0; var0 < 4; ++var0) {
		playerFadeInfo[var0] = new UIManager.FadeInfo(var0);
	}
	}

	private static class FadeInfo {
		public int playerIndex;
		public boolean bFadeBeforeUI = false;
		public float FadeAlpha = 0.0F;
		public int FadeTime = 2;
		public int FadeTimeMax = 2;
		public boolean FadingOut = false;

		public FadeInfo(int int1) {
			this.playerIndex = int1;
		}

		public boolean isFadeBeforeUI() {
			return this.bFadeBeforeUI;
		}

		public void setFadeBeforeUI(boolean boolean1) {
			this.bFadeBeforeUI = boolean1;
		}

		public float getFadeAlpha() {
			return this.FadeAlpha;
		}

		public void setFadeAlpha(float float1) {
			this.FadeAlpha = float1;
		}

		public int getFadeTime() {
			return this.FadeTime;
		}

		public void setFadeTime(int int1) {
			this.FadeTime = int1;
		}

		public int getFadeTimeMax() {
			return this.FadeTimeMax;
		}

		public void setFadeTimeMax(int int1) {
			this.FadeTimeMax = int1;
		}

		public boolean isFadingOut() {
			return this.FadingOut;
		}

		public void setFadingOut(boolean boolean1) {
			this.FadingOut = boolean1;
		}

		public void FadeIn(int int1) {
			this.setFadeTimeMax((int)((float)(int1 * 30) * ((float)PerformanceSettings.LockFPS / 30.0F)));
			this.setFadeTime(this.getFadeTimeMax());
			this.setFadingOut(false);
		}

		public void FadeOut(int int1) {
			this.setFadeTimeMax((int)((float)(int1 * 30) * ((float)PerformanceSettings.LockFPS / 30.0F)));
			this.setFadeTime(this.getFadeTimeMax());
			this.setFadingOut(true);
		}

		public void update() {
			this.setFadeTime(this.getFadeTime() - 1);
		}

		public void render() {
			this.setFadeAlpha((float)this.getFadeTime() / (float)this.getFadeTimeMax());
			if (this.getFadeAlpha() > 1.0F) {
				this.setFadeAlpha(1.0F);
			}

			if (this.getFadeAlpha() < 0.0F) {
				this.setFadeAlpha(0.0F);
			}

			if (this.isFadingOut()) {
				this.setFadeAlpha(1.0F - this.getFadeAlpha());
			}

			if (!(this.getFadeAlpha() <= 0.0F)) {
				int int1 = IsoCamera.getScreenLeft(this.playerIndex);
				int int2 = IsoCamera.getScreenTop(this.playerIndex);
				int int3 = IsoCamera.getScreenWidth(this.playerIndex);
				int int4 = IsoCamera.getScreenHeight(this.playerIndex);
				UIManager.DrawTexture(UIManager.getBlack(), (double)int1, (double)int2, (double)int3, (double)int4, (double)this.getFadeAlpha());
			}
		}
	}

	static class Sync {
		private int fps = 30;
		private long period;
		private long excess;
		private long beforeTime;
		private long overSleepTime;

		Sync() {
			this.period = 1000000000L / (long)this.fps;
			this.beforeTime = System.nanoTime();
			this.overSleepTime = 0L;
		}

		void begin() {
			this.beforeTime = System.nanoTime();
			this.overSleepTime = 0L;
		}

		void startFrame() {
			this.excess = 0L;
		}

		void endFrame() {
			long long1 = System.nanoTime();
			long long2 = long1 - this.beforeTime;
			long long3 = this.period - long2 - this.overSleepTime;
			if (long3 > 0L) {
				try {
					Thread.sleep(long3 / 1000000L);
				} catch (InterruptedException interruptedException) {
				}

				this.overSleepTime = System.nanoTime() - long1 - long3;
			} else {
				this.excess -= long3;
				this.overSleepTime = 0L;
			}

			this.beforeTime = System.nanoTime();
		}
	}
}
