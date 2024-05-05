package zombie.ui;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.krka.kahlua.vm.KahluaThread;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.Styles.TransparentStyle;
import zombie.core.Styles.UIFBOStyle;
import zombie.core.opengl.RenderThread;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureFBO;
import zombie.debug.DebugOptions;
import zombie.gameStates.GameLoadingState;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
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
import zombie.util.list.PZArrayUtil;


public final class UIManager {
	public static int lastMouseX = 0;
	public static int lastMouseY = 0;
	public static IsoObjectPicker.ClickObject Picked = null;
	public static Clock clock;
	public static final ArrayList UI = new ArrayList();
	public static ObjectTooltip toolTip = null;
	public static Texture mouseArrow;
	public static Texture mouseExamine;
	public static Texture mouseAttack;
	public static Texture mouseGrab;
	public static SpeedControls speedControls;
	public static UIDebugConsole DebugConsole;
	public static UIServerToolbox ServerToolbox;
	public static final MoodlesUI[] MoodleUI = new MoodlesUI[4];
	public static boolean bFadeBeforeUI = false;
	public static final ActionProgressBar[] ProgressBar = new ActionProgressBar[4];
	public static float FadeAlpha = 1.0F;
	public static int FadeInTimeMax = 180;
	public static int FadeInTime = 180;
	public static boolean FadingOut = false;
	public static Texture lastMouseTexture;
	public static IsoObject LastPicked = null;
	public static final ArrayList DoneTutorials = new ArrayList();
	public static float lastOffX = 0.0F;
	public static float lastOffY = 0.0F;
	public static ModalDialog Modal = null;
	public static boolean KeyDownZoomIn = false;
	public static boolean KeyDownZoomOut = false;
	public static boolean doTick;
	public static boolean VisibleAllUI = true;
	public static TextureFBO UIFBO;
	public static boolean useUIFBO = false;
	public static Texture black = null;
	public static boolean bSuspend = false;
	public static float lastAlpha = 10000.0F;
	public static final Vector2 PickedTileLocal = new Vector2();
	public static final Vector2 PickedTile = new Vector2();
	public static IsoObject RightDownObject = null;
	public static long uiUpdateTimeMS = 0L;
	public static long uiUpdateIntervalMS = 0L;
	public static long uiRenderTimeMS = 0L;
	public static long uiRenderIntervalMS = 0L;
	private static final ArrayList tutorialStack = new ArrayList();
	public static final ArrayList toTop = new ArrayList();
	public static KahluaThread defaultthread = null;
	public static KahluaThread previousThread = null;
	static final ArrayList toRemove = new ArrayList();
	static final ArrayList toAdd = new ArrayList();
	static int wheel = 0;
	static int lastwheel = 0;
	static final ArrayList debugUI = new ArrayList();
	static boolean bShowLuaDebuggerOnError = true;
	static final UIManager.Sync sync = new UIManager.Sync();
	private static boolean showPausedMessage = true;
	private static UIElement playerInventoryUI;
	private static UIElement playerLootUI;
	private static UIElement playerInventoryTooltip;
	private static UIElement playerLootTooltip;
	private static final UIManager.FadeInfo[] playerFadeInfo = new UIManager.FadeInfo[4];

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

	public static void closeContainers() {
	}

	public static void CloseContainers() {
	}

	public static void DrawTexture(Texture texture, double double1, double double2) {
		double double3 = double1 + (double)texture.offsetX;
		double double4 = double2 + (double)texture.offsetY;
		SpriteRenderer.instance.renderi(texture, (int)double3, (int)double4, texture.getWidth(), texture.getHeight(), 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
	}

	public static void DrawTexture(Texture texture, double double1, double double2, double double3, double double4, double double5) {
		double double6 = double1 + (double)texture.offsetX;
		double double7 = double2 + (double)texture.offsetY;
		SpriteRenderer.instance.renderi(texture, (int)double6, (int)double7, (int)double3, (int)double4, 1.0F, 1.0F, 1.0F, (float)double5, (Consumer)null);
	}

	public static void FadeIn(double double1) {
		setFadeInTimeMax((double)((int)(double1 * 30.0 * (double)((float)PerformanceSettings.getLockFPS() / 30.0F))));
		setFadeInTime(getFadeInTimeMax());
		setFadingOut(false);
	}

	public static void FadeOut(double double1) {
		setFadeInTimeMax((double)((int)(double1 * 30.0 * (double)((float)PerformanceSettings.getLockFPS() / 30.0F))));
		setFadeInTime(getFadeInTimeMax());
		setFadingOut(true);
	}

	public static void CreateFBO(int int1, int int2) {
		if (Core.SafeMode) {
			useUIFBO = false;
		} else {
			if (useUIFBO && (UIFBO == null || UIFBO.getTexture().getWidth() != int1 || UIFBO.getTexture().getHeight() != int2)) {
				if (UIFBO != null) {
					RenderThread.invokeOnRenderContext(()->{
						UIFBO.destroy();
					});
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
		if (boolean1) {
			texture = new Texture((int)float1, (int)float2, 16);
			TextureFBO textureFBO = new TextureFBO(texture);
			textureFBO.destroy();
			return null;
		} else {
			texture = new Texture((int)float1, (int)float2, 16);
			return new TextureFBO(texture);
		}
	}

	public static void init() {
		showPausedMessage = true;
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
				setToolTip(new ObjectTooltip());
				if (Core.getInstance().getOptionClockSize() == 2) {
					setClock(new Clock(Core.getInstance().getOffscreenWidth(0) - 166, 10));
				} else {
					setClock(new Clock(Core.getInstance().getOffscreenWidth(0) - 91, 10));
				}

				if (!Core.GameMode.equals("LastStand")) {
					getUI().add(getClock());
				}

				getUI().add(getToolTip());
				setDebugConsole(new UIDebugConsole(20, Core.getInstance().getScreenHeight() - 265));
				setServerToolbox(new UIServerToolbox(100, 200));
				if (Core.bDebug && DebugOptions.instance.UIDebugConsoleStartVisible.getValue()) {
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

				getUI().add(getDebugConsole());
				getUI().add(getServerToolbox());
				setLastMouseTexture(getMouseArrow());
				resize();
				for (int1 = 0; int1 < 4; ++int1) {
					ActionProgressBar actionProgressBar = new ActionProgressBar(300, 300);
					actionProgressBar.setRenderThisPlayerOnly(int1);
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
		if (!useUIFBO || Core.getInstance().UIRenderThisFrame) {
			if (!bSuspend) {
				long long1 = System.currentTimeMillis();
				uiRenderIntervalMS = Math.min(long1 - uiRenderTimeMS, 1000L);
				uiRenderTimeMS = long1;
				UIElement.StencilLevel = 0;
				if (useUIFBO) {
					SpriteRenderer.instance.setDefaultStyle(UIFBOStyle.instance);
				}

				UITransition.UpdateAll();
				if (getBlack() == null) {
					setBlack(Texture.getSharedTexture("black.png"));
				}

				if (LuaManager.thread == defaultthread) {
					LuaEventManager.triggerEvent("OnPreUIDraw");
				}

				int int1 = Mouse.getXA();
				int int2 = Mouse.getYA();
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
					if ((((UIElement)UI.get(int3)).isIgnoreLossControl() || !TutorialManager.instance.StealControl) && !((UIElement)UI.get(int3)).isFollowGameWorld()) {
						try {
							if (((UIElement)getUI().get(int3)).isDefaultDraw()) {
								((UIElement)getUI().get(int3)).render();
							}
						} catch (Exception exception) {
							Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, (String)null, exception);
						}
					}
				}

				if (getToolTip() != null) {
					getToolTip().render();
				}

				if (isShowPausedMessage() && GameTime.isGamePaused() && (getModal() == null || !Modal.isVisible()) && VisibleAllUI) {
					String string = Translator.getText("IGUI_GamePaused");
					int int4 = TextManager.instance.MeasureStringX(UIFont.Small, string) + 32;
					int int5 = TextManager.instance.font.getLineHeight();
					int int6 = (int)Math.ceil((double)int5 * 1.5);
					SpriteRenderer.instance.renderi((Texture)null, Core.getInstance().getScreenWidth() / 2 - int4 / 2, Core.getInstance().getScreenHeight() / 2 - int6 / 2, int4, int6, 0.0F, 0.0F, 0.0F, 0.75F, (Consumer)null);
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

				if (LuaManager.thread == defaultthread) {
					LuaEventManager.triggerEvent("OnPostUIDraw");
				}

				if (useUIFBO) {
					SpriteRenderer.instance.setDefaultStyle(TransparentStyle.instance);
				}
			}
		}
	}

	public static void resize() {
		if (useUIFBO && UIFBO != null) {
			CreateFBO(Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight());
		}

		if (getClock() != null) {
			setLastOffX((float)Core.getInstance().getScreenWidth());
			setLastOffY((float)Core.getInstance().getScreenHeight());
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

			clock.resize();
			if (IsoPlayer.numPlayers == 1) {
				if (Core.getInstance().getOptionClockSize() == 2) {
					clock.setX((double)(Core.getInstance().getScreenWidth() - 166));
				} else {
					clock.setX((double)(Core.getInstance().getScreenWidth() - 91));
				}
			} else {
				if (Core.getInstance().getOptionClockSize() == 2) {
					clock.setX((double)((float)Core.getInstance().getScreenWidth() / 2.0F - 83.0F));
				} else {
					clock.setX((double)((float)Core.getInstance().getScreenWidth() / 2.0F - 45.5F));
				}

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

			long long1 = System.currentTimeMillis();
			if (long1 - uiUpdateTimeMS >= 100L) {
				doTick = true;
				uiUpdateIntervalMS = Math.min(long1 - uiUpdateTimeMS, 1000L);
				uiUpdateTimeMS = long1;
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
			tutorialStack.clear();
			int int6;
			UIElement uIElement;
			UIElement uIElement2;
			for (int6 = UI.size() - 1; int6 >= 0; --int6) {
				uIElement = (UIElement)UI.get(int6);
				if (uIElement.getParent() != null) {
					UI.remove(int6);
					throw new IllegalStateException();
				}

				if (uIElement.isFollowGameWorld()) {
					tutorialStack.add(uIElement);
				}

				if (uIElement instanceof ObjectTooltip) {
					uIElement2 = (UIElement)UI.remove(int6);
					UI.add(uIElement2);
				}
			}

			for (int6 = 0; int6 < UI.size(); ++int6) {
				uIElement = (UIElement)UI.get(int6);
				if (uIElement.alwaysOnTop || toTop.contains(uIElement)) {
					uIElement2 = (UIElement)UI.remove(int6);
					--int6;
					toAdd.add(uIElement2);
				}
			}

			if (!toAdd.isEmpty()) {
				UI.addAll(toAdd);
				toAdd.clear();
			}

			toTop.clear();
			for (int6 = 0; int6 < UI.size(); ++int6) {
				uIElement = (UIElement)UI.get(int6);
				if (uIElement.alwaysBack) {
					uIElement2 = (UIElement)UI.remove(int6);
					UI.add(0, uIElement2);
				}
			}

			for (int6 = 0; int6 < tutorialStack.size(); ++int6) {
				UI.remove(tutorialStack.get(int6));
				UI.add(0, (UIElement)tutorialStack.get(int6));
			}

			if (Mouse.isLeftPressed()) {
				Core.UnfocusActiveTextEntryBox();
				for (int6 = UI.size() - 1; int6 >= 0; --int6) {
					uIElement = (UIElement)UI.get(int6);
					if ((getModal() == null || getModal() == uIElement || !getModal().isVisible()) && (uIElement.isIgnoreLossControl() || !TutorialManager.instance.StealControl) && uIElement.isVisible()) {
						if ((!((double)int2 >= uIElement.getX()) || !((double)int3 >= uIElement.getY()) || !((double)int2 < uIElement.getX() + uIElement.getWidth()) || !((double)int3 < uIElement.getY() + uIElement.getHeight())) && !uIElement.isCapture()) {
							uIElement.onMouseDownOutside((double)(int2 - uIElement.getX().intValue()), (double)(int3 - uIElement.getY().intValue()));
						} else if (uIElement.onMouseDown((double)(int2 - uIElement.getX().intValue()), (double)(int3 - uIElement.getY().intValue()))) {
							boolean1 = true;
							break;
						}
					}
				}

				if (checkPicked() && !boolean1) {
					LuaEventManager.triggerEvent("OnObjectLeftMouseButtonDown", Picked.tile, BoxedStaticValues.toDouble((double)int2), BoxedStaticValues.toDouble((double)int3));
				}

				if (!boolean1) {
					LuaEventManager.triggerEvent("OnMouseDown", BoxedStaticValues.toDouble((double)int2), BoxedStaticValues.toDouble((double)int3));
					CloseContainers();
					if (IsoWorld.instance.CurrentCell != null && !IsoWorld.instance.CurrentCell.DoBuilding(0, false) && getPicked() != null && !GameTime.isGamePaused() && IsoPlayer.getInstance() != null && !IsoPlayer.getInstance().isAiming() && !IsoPlayer.getInstance().isAsleep()) {
						getPicked().tile.onMouseLeftClick(getPicked().lx, getPicked().ly);
					}
				} else {
					Mouse.UIBlockButtonDown(0);
				}
			}

			int int7;
			boolean boolean4;
			if (Mouse.isLeftReleased()) {
				boolean4 = false;
				for (int7 = UI.size() - 1; int7 >= 0; --int7) {
					uIElement2 = (UIElement)UI.get(int7);
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
					LuaEventManager.triggerEvent("OnMouseUp", BoxedStaticValues.toDouble((double)int2), BoxedStaticValues.toDouble((double)int3));
					if (checkPicked() && !boolean1) {
						LuaEventManager.triggerEvent("OnObjectLeftMouseButtonUp", Picked.tile, BoxedStaticValues.toDouble((double)int2), BoxedStaticValues.toDouble((double)int3));
					}
				}
			}

			if (Mouse.isRightPressed()) {
				for (int6 = UI.size() - 1; int6 >= 0; --int6) {
					uIElement = (UIElement)UI.get(int6);
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
					LuaEventManager.triggerEvent("OnRightMouseDown", BoxedStaticValues.toDouble((double)int2), BoxedStaticValues.toDouble((double)int3));
					if (checkPicked() && !boolean2) {
						LuaEventManager.triggerEvent("OnObjectRightMouseButtonDown", Picked.tile, BoxedStaticValues.toDouble((double)int2), BoxedStaticValues.toDouble((double)int3));
					}
				} else {
					Mouse.UIBlockButtonDown(1);
				}

				if (IsoWorld.instance.CurrentCell != null && getPicked() != null && getSpeedControls() != null && !IsoPlayer.getInstance().isAiming() && !IsoPlayer.getInstance().isAsleep() && !GameTime.isGamePaused()) {
					getSpeedControls().SetCurrentGameSpeed(1);
					getPicked().tile.onMouseRightClick(getPicked().lx, getPicked().ly);
					setRightDownObject(getPicked().tile);
				}
			}

			if (Mouse.isRightReleased()) {
				boolean4 = false;
				boolean boolean5 = false;
				for (int6 = UI.size() - 1; int6 >= 0; --int6) {
					uIElement2 = (UIElement)UI.get(int6);
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
					LuaEventManager.triggerEvent("OnRightMouseUp", BoxedStaticValues.toDouble((double)int2), BoxedStaticValues.toDouble((double)int3));
					if (checkPicked()) {
						boolean boolean6 = true;
						if (GameClient.bClient && Picked.tile.getSquare() != null) {
							SafeHouse safeHouse = SafeHouse.isSafeHouse(Picked.tile.getSquare(), IsoPlayer.getInstance().getUsername(), true);
							if (safeHouse != null) {
								boolean6 = false;
							}
						}

						if (boolean6) {
							LuaEventManager.triggerEvent("OnObjectRightMouseButtonUp", Picked.tile, BoxedStaticValues.toDouble((double)int2), BoxedStaticValues.toDouble((double)int3));
						}
					}
				}

				if (IsoPlayer.getInstance() != null) {
					IsoPlayer.getInstance().setDragObject((IsoMovingObject)null);
				}

				if (IsoWorld.instance.CurrentCell != null && getRightDownObject() != null && IsoPlayer.getInstance() != null && !IsoPlayer.getInstance().IsAiming() && !IsoPlayer.getInstance().isAsleep()) {
					getRightDownObject().onMouseRightReleased();
					setRightDownObject((IsoObject)null);
				}
			}

			lastwheel = 0;
			wheel = Mouse.getWheelState();
			boolean4 = false;
			if (wheel != lastwheel) {
				int7 = wheel - lastwheel < 0 ? 1 : -1;
				for (int int8 = UI.size() - 1; int8 >= 0; --int8) {
					UIElement uIElement3 = (UIElement)UI.get(int8);
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
				for (int7 = UI.size() - 1; int7 >= 0; --int7) {
					uIElement2 = (UIElement)UI.get(int7);
					if ((uIElement2.isIgnoreLossControl() || !TutorialManager.instance.StealControl) && uIElement2.isVisible()) {
						if ((!((double)int2 >= uIElement2.getX()) || !((double)int3 >= uIElement2.getY()) || !((double)int2 < uIElement2.getX() + uIElement2.getWidth()) || !((double)int3 < uIElement2.getY() + uIElement2.getHeight())) && !uIElement2.isCapture()) {
							uIElement2.onMouseMoveOutside((double)int2 - getLastMouseX(), (double)int3 - getLastMouseY());
						} else if (!boolean3 && uIElement2.onMouseMove((double)int2 - getLastMouseX(), (double)int3 - getLastMouseY())) {
							boolean3 = true;
						}
					}
				}
			}

			if (!boolean3 && IsoPlayer.players[0] != null) {
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
			for (int7 = 0; int7 < UI.size(); ++int7) {
				((UIElement)UI.get(int7)).update();
			}

			updateTooltip((double)int2, (double)int3);
			handleZoomKeys();
			IsoCamera.cameras[0].lastOffX = (float)((int)IsoCamera.cameras[0].OffX);
			IsoCamera.cameras[0].lastOffY = (float)((int)IsoCamera.cameras[0].OffY);
		}
	}

	private static boolean checkPicked() {
		return Picked != null && Picked.tile != null && Picked.tile.getObjectIndex() != -1;
	}

	private static void handleZoomKeys() {
		boolean boolean1 = true;
		if (Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.IsEditable && Core.CurrentTextEntryBox.DoingTextEntry) {
			boolean1 = false;
		}

		if (GameTime.isGamePaused()) {
			boolean1 = false;
		}

		if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Zoom in"))) {
			if (boolean1 && !KeyDownZoomIn) {
				Core.getInstance().doZoomScroll(0, -1);
			}

			KeyDownZoomIn = true;
		} else {
			KeyDownZoomIn = false;
		}

		if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Zoom out"))) {
			if (boolean1 && !KeyDownZoomOut) {
				Core.getInstance().doZoomScroll(0, 1);
			}

			KeyDownZoomOut = true;
		} else {
			KeyDownZoomOut = false;
		}
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

	public static ArrayList getUI() {
		return UI;
	}

	public static void setUI(ArrayList arrayList) {
		PZArrayUtil.copy(UI, arrayList);
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

	public static SpeedControls getSpeedControls() {
		return speedControls;
	}

	public static void setSpeedControls(SpeedControls speedControls) {
		speedControls = speedControls;
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

	public static ArrayList getDoneTutorials() {
		return DoneTutorials;
	}

	public static void setDoneTutorials(ArrayList arrayList) {
		PZArrayUtil.copy(DoneTutorials, arrayList);
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
		PickedTileLocal.set(vector2);
	}

	public static Vector2 getPickedTile() {
		return PickedTile;
	}

	public static void setPickedTile(Vector2 vector2) {
		PickedTile.set(vector2);
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

	public static void setShowLuaDebuggerOnError(boolean boolean1) {
		bShowLuaDebuggerOnError = boolean1;
	}

	public static boolean isShowLuaDebuggerOnError() {
		return bShowLuaDebuggerOnError;
	}

	public static void debugBreakpoint(String string, long long1) {
		if (bShowLuaDebuggerOnError) {
			if (Core.CurrentTextEntryBox != null) {
				Core.CurrentTextEntryBox.DoingTextEntry = false;
				Core.CurrentTextEntryBox = null;
			}

			if (!GameServer.bServer) {
				if (!(GameWindow.states.current instanceof GameLoadingState)) {
					previousThread = defaultthread;
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

					LuaManager.thread.bStep = false;
					LuaManager.thread.bStepInto = false;
					ArrayList arrayList = new ArrayList();
					boolean boolean1 = bSuspend;
					arrayList.addAll(UI);
					UI.clear();
					bSuspend = false;
					setShowPausedMessage(false);
					boolean boolean2 = false;
					boolean[] booleanArray = new boolean[11];
					int int2;
					for (int2 = 0; int2 < 11; ++int2) {
						booleanArray[int2] = true;
					}

					if (debugUI.size() == 0) {
						LuaManager.debugcaller.pcall(LuaManager.debugthread, LuaManager.env.rawget("DoLuaDebugger"), string, long1);
					} else {
						UI.addAll(debugUI);
						LuaManager.debugcaller.pcall(LuaManager.debugthread, LuaManager.env.rawget("DoLuaDebuggerOnBreak"), string, long1);
					}

					Mouse.setCursorVisible(true);
					sync.begin();
					while (!boolean2) {
						if (RenderThread.isCloseRequested()) {
							System.exit(0);
						}

						if (!GameWindow.bLuaDebuggerKeyDown && GameKeyboard.isKeyDown(Core.getInstance().getKey("Toggle Lua Debugger"))) {
							GameWindow.bLuaDebuggerKeyDown = true;
							executeGame(arrayList, boolean1, int1);
							return;
						}

						sync.startFrame();
						for (int2 = 0; int2 < 11; ++int2) {
							boolean boolean3 = GameKeyboard.isKeyDown(59 + int2);
							if (boolean3) {
								if (!booleanArray[int2]) {
									if (int2 + 1 == 5) {
										LuaManager.thread.bStep = true;
										LuaManager.thread.bStepInto = true;
										executeGame(arrayList, boolean1, int1);
										return;
									}

									if (int2 + 1 == 6) {
										LuaManager.thread.bStep = true;
										LuaManager.thread.bStepInto = false;
										LuaManager.thread.lastCallFrame = LuaManager.thread.getCurrentCoroutine().getCallframeTop();
										executeGame(arrayList, boolean1, int1);
										return;
									}
								}

								booleanArray[int2] = true;
							} else {
								booleanArray[int2] = false;
							}
						}

						Mouse.update();
						GameKeyboard.update();
						Core.getInstance().DoFrameReady();
						update();
						Core.getInstance().StartFrame(0, true);
						Core.getInstance().EndFrame(0);
						Core.getInstance().RenderOffScreenBuffer();
						if (Core.getInstance().StartFrameUI()) {
							render();
						}

						Core.getInstance().EndFrameUI();
						resize();
						if (!GameKeyboard.isKeyDown(Core.getInstance().getKey("Toggle Lua Debugger"))) {
							GameWindow.bLuaDebuggerKeyDown = false;
						}

						sync.endFrame();
						Core.getInstance().setScreenSize(RenderThread.getDisplayWidth(), RenderThread.getDisplayHeight());
					}
				}
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

		defaultthread = previousThread;
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
			if (playerInventoryUI.getMaxDrawHeight() == -1.0 && playerInventoryUI.isMouseOver()) {
				return true;
			} else {
				return playerLootUI.getMaxDrawHeight() == -1.0 && playerLootUI.isMouseOver();
			}
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

	public static boolean onKeyPress(int int1) {
		for (int int2 = UI.size() - 1; int2 >= 0; --int2) {
			UIElement uIElement = (UIElement)UI.get(int2);
			if (uIElement.isVisible() && uIElement.isWantKeyEvents()) {
				uIElement.onKeyPress(int1);
				if (uIElement.isKeyConsumed(int1)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean onKeyRepeat(int int1) {
		for (int int2 = UI.size() - 1; int2 >= 0; --int2) {
			UIElement uIElement = (UIElement)UI.get(int2);
			if (uIElement.isVisible() && uIElement.isWantKeyEvents()) {
				uIElement.onKeyRepeat(int1);
				if (uIElement.isKeyConsumed(int1)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean onKeyRelease(int int1) {
		for (int int2 = UI.size() - 1; int2 >= 0; --int2) {
			UIElement uIElement = (UIElement)UI.get(int2);
			if (uIElement.isVisible() && uIElement.isWantKeyEvents()) {
				uIElement.onKeyRelease(int1);
				if (uIElement.isKeyConsumed(int1)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isForceCursorVisible() {
		for (int int1 = UI.size() - 1; int1 >= 0; --int1) {
			UIElement uIElement = (UIElement)UI.get(int1);
			if (uIElement.isVisible() && (uIElement.isForceCursorVisible() || uIElement.isMouseOver())) {
				return true;
			}
		}

		return false;
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
			this.setFadeTimeMax((int)((float)(int1 * 30) * ((float)PerformanceSettings.getLockFPS() / 30.0F)));
			this.setFadeTime(this.getFadeTimeMax());
			this.setFadingOut(false);
		}

		public void FadeOut(int int1) {
			this.setFadeTimeMax((int)((float)(int1 * 30) * ((float)PerformanceSettings.getLockFPS() / 30.0F)));
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
