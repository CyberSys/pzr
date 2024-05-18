package zombie.ui;

import java.util.Iterator;
import zombie.Quests.Quest;
import zombie.Quests.QuestManager;
import zombie.Quests.QuestTask;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.input.Mouse;


public class QuestPanel extends NewWindow implements UIEventHandler {
	public static QuestPanel instance;
	ScrollBar QuestScrollbar = null;
	UITextBox2 QuestTextBox = null;
	private int MaxQuestIcons = 1000;
	int[] QuestIcons;
	int NumUsedIconSlots;
	int MouseOverX;
	int MouseOverY;
	String TempText;
	public Quest ActiveQuest;
	Quest MouseOverQuest;

	public QuestPanel(int int1, int int2) {
		super(int1, int2, 10, 10, true);
		this.QuestIcons = new int[this.MaxQuestIcons];
		this.NumUsedIconSlots = 0;
		this.MouseOverX = 0;
		this.MouseOverY = 0;
		this.TempText = null;
		this.ActiveQuest = null;
		this.MouseOverQuest = null;
		boolean boolean1 = true;
		boolean boolean2 = true;
		this.ResizeToFitY = false;
		this.visible = false;
		instance = this;
		this.width = 340.0F;
		this.height = 400.0F;
		this.Movable = true;
		this.MouseOverX = 0;
		this.MouseOverY = 0;
		this.QuestTextBox = new UITextBox2(UIFont.Small, 4, 21, (int)(this.getWidth() - 20.0), (int)(this.getHeight() - 26.0), "Quest Text", true);
		this.QuestScrollbar = new ScrollBar("QuestScrollBar", this, (int)(this.getWidth() - 15.0), 21, (int)(this.getHeight() - 26.0), true);
		this.QuestScrollbar.SetParentTextBox(this.QuestTextBox);
		this.AddChild(this.QuestTextBox);
		this.AddChild(this.QuestScrollbar);
	}

	public void render() {
		if (this.isVisible()) {
			super.render();
			this.DrawTextCentre("Quests", this.getWidth() / 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
			if (this.QuestTextBox.Lines != null) {
				int int1 = this.QuestTextBox.TopLineIndex;
				for (int int2 = 0; int1 < this.MaxQuestIcons && int1 < this.QuestTextBox.Lines.size() && int2 < this.QuestTextBox.NumVisibleLines; ++int1) {
					Texture texture = null;
					if (this.QuestIcons[int1] != 0) {
						switch (this.QuestIcons[int1]) {
						case 1: 
							texture = Texture.getSharedTexture("media/ui/Quest_Succeed.png");
							break;
						
						case 2: 
							texture = Texture.getSharedTexture("media/ui/Quest_Failed.png");
							break;
						
						case 3: 
							texture = Texture.getSharedTexture("media/ui/Quest_Bullet.png");
						
						}
					}

					if (texture != null) {
						this.DrawTextureCol(texture, 8.0, (double)(28 + int2 * 14), Color.white);
					}

					++int2;
				}
			}
		}
	}

	public void update() {
		if (this.isVisible()) {
			super.update();
			float float1 = (float)this.getAbsoluteY().intValue();
			float float2 = float1 - 40.0F;
			float float3 = (float)Core.getInstance().getOffscreenHeight(0) - float1;
			if (float3 > 0.0F) {
				float2 /= float3;
			} else {
				float2 = 1.0F;
			}

			float2 *= 4.0F;
			float2 = 1.0F - float2;
			if (float2 < 0.0F) {
				float2 = 0.0F;
			}

			String string = "";
			String string2 = "";
			int int1 = 0;
			int int2 = 0;
			this.NumUsedIconSlots = 0;
			for (int int3 = 0; int3 < this.MaxQuestIcons; ++int3) {
				this.QuestIcons[int3] = 0;
			}

			this.MouseOverQuest = null;
			this.QuestTextBox.ClearHighlights();
			if (QuestManager.instance.QuestStack != null) {
				Iterator iterator = QuestManager.instance.QuestStack.iterator();
				label128: while (true) {
					Quest quest;
					do {
						do {
							if (!iterator.hasNext()) {
								break label128;
							}

							quest = (Quest)iterator.next();
						}				 while (quest == null);
					}			 while (!quest.Unlocked);

					if (quest.Complete) {
						this.QuestIcons[int2] = 1;
					} else if (quest.Failed) {
						this.QuestIcons[int2] = 2;
					}

					if (!quest.Complete && !quest.Failed) {
						if (quest == this.ActiveQuest) {
							this.QuestTextBox.HighlightLines[int2] = 1;
						}

						if (int1 == 0) {
							this.TempText = quest.getName() + ".\n";
							++int2;
						} else {
							this.TempText = this.TempText + quest.getName() + ".\n";
							++int2;
						}
					} else if (int1 == 0) {
						this.TempText = "	" + quest.getName() + ".\n";
						++int2;
					} else {
						this.TempText = this.TempText + "	" + quest.getName() + ".\n";
						++int2;
					}

					++int1;
					if (this.ActiveQuest != quest && !quest.Complete && !quest.Failed) {
						int int4 = int2 - 1 - this.QuestTextBox.TopLineIndex;
						int int5 = 28 + int4 * 14;
						if (this.MouseOverX > 10 && this.MouseOverX < 276 && this.MouseOverY >= int5 - 6 && this.MouseOverY <= int5 + 19) {
							this.MouseOverQuest = quest;
							this.QuestTextBox.HighlightLines[int2 - 1] = 2;
						}
					}

					if (this.ActiveQuest == quest && !quest.Complete && !quest.Failed) {
						Iterator iterator2 = quest.QuestTaskStack.iterator();
						while (iterator2.hasNext()) {
							QuestTask questTask = (QuestTask)iterator2.next();
							if (questTask.Unlocked && !questTask.Hidden) {
								if (questTask.Complete) {
									this.QuestIcons[int2] = 1;
								} else if (questTask.Failed) {
									this.QuestIcons[int2] = 2;
								} else {
									this.QuestIcons[int2] = 3;
								}

								this.TempText = this.TempText + "   " + questTask.getName() + ".\n";
								this.QuestTextBox.HighlightLines[int2] = 1;
								++int2;
							}
						}
					}

					this.TempText = this.TempText + "	\n";
					++int2;
				}
			}

			this.NumUsedIconSlots = int2;
			if (this.TempText != null) {
				this.QuestTextBox.SetText(this.TempText);
			}

			if (this.ActiveQuest != null && (this.ActiveQuest.Complete || this.ActiveQuest.Failed)) {
				this.ActiveQuest = null;
				if (UIManager.getOnscreenQuest() != null) {
					UIManager.getOnscreenQuest().TriggerQuestWiggle();
				}
			}
		}
	}

	public void DoubleClick(String string, int int1, int int2) {
	}

	public void Selected(String string, int int1, int int2) {
	}

	public void SetActiveQuest(Quest quest) {
		this.ActiveQuest = quest;
	}

	public Boolean onMouseDown(double double1, double double2) {
		if (!this.isVisible()) {
			return false;
		} else {
			super.onMouseDown(double1, double2);
			if (this.MouseOverQuest != null) {
				this.ActiveQuest = this.MouseOverQuest;
				if (UIManager.getOnscreenQuest() != null) {
					UIManager.getOnscreenQuest().TriggerQuestWiggle();
				}
			}

			return Boolean.TRUE;
		}
	}

	public Boolean onMouseMove(double double1, double double2) {
		if (!this.isVisible()) {
			return Boolean.FALSE;
		} else {
			super.onMouseMove(double1, double2);
			this.MouseOverX = Mouse.getXA() - this.getAbsoluteX().intValue();
			this.MouseOverY = Mouse.getYA() - this.getAbsoluteY().intValue();
			return Boolean.FALSE;
		}
	}

	public void onMouseMoveOutside(double double1, double double2) {
		super.onMouseMoveOutside(double1, double2);
		this.MouseOverX = 0;
		this.MouseOverY = 0;
		this.MouseOverQuest = null;
	}

	public void ModalClick(String string, String string2) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
