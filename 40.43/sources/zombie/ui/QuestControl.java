package zombie.ui;

import java.util.Iterator;
import java.util.Stack;
import zombie.Quests.Completable;
import zombie.Quests.Quest;
import zombie.Quests.QuestManager;
import zombie.Quests.QuestTask;
import zombie.core.BoxedStaticValues;
import zombie.core.Color;
import zombie.core.textures.Texture;


public class QuestControl extends UIElement {
	public boolean bShort = false;
	public Texture bullet = Texture.getSharedTexture("media/ui/Quest_Bullet.png");
	public Texture failed = Texture.getSharedTexture("media/ui/Quest_Failed.png");
	public Stack IndentLines = new Stack();
	public int originalWidth = 0;
	public Stack Status = new Stack();
	public Texture succeed = Texture.getSharedTexture("media/ui/Quest_Succeed.png");
	Stack Lines = new Stack();
	int maxWidthUsed = 0;

	public QuestControl(boolean boolean1) {
		this.bShort = boolean1;
		this.originalWidth = 500;
		this.width = 500.0F;
	}

	public Double getHeight() {
		int int1 = this.Lines.size() * 17;
		return int1 < 50 ? BoxedStaticValues.toDouble(50.0) : BoxedStaticValues.toDouble((double)int1);
	}

	public void render() {
		this.setWidth((double)this.originalWidth);
		this.Paginate();
		this.setHeight(this.getHeight());
		int int1 = 0;
		int int2 = 0;
		for (Iterator iterator = this.Lines.iterator(); iterator.hasNext(); ++int1) {
			String string = (String)iterator.next();
			Completable completable = (Completable)this.Status.get(int1);
			int int3 = 0;
			if (this.IndentLines.contains(int1)) {
				int3 += 16;
			}

			if (completable != null) {
				Texture texture = this.bullet;
				if (completable.IsComplete()) {
					texture = this.succeed;
				}

				if (completable.IsFailed()) {
					texture = this.failed;
				}

				this.DrawTextureCol(texture, (double)int3, (double)(int2 + 2), Color.white);
			}

			this.DrawText(string, (double)(int3 + 16), (double)int2, 1.0, 1.0, 1.0, 1.0);
			int2 += 17;
		}
	}

	private void Paginate() {
		this.maxWidthUsed = 0;
		boolean boolean1 = false;
		this.Lines.clear();
		this.Status.clear();
		this.IndentLines.clear();
		boolean boolean2 = false;
		boolean boolean3 = false;
		Iterator iterator = QuestManager.instance.QuestStack.iterator();
		label84: while (true) {
			Quest quest;
			String string;
			do {
				do {
					do {
						while (true) {
							do {
								if (!iterator.hasNext()) {
									if (this.getParent() != null) {
										this.getParent().setHeight(this.getHeight() + 32.0);
									}

									this.setWidth((double)this.maxWidthUsed);
									return;
								}

								quest = (Quest)iterator.next();
							}					 while (!quest.Unlocked);

							if (!this.bShort || !quest.Complete && !quest.Failed) {
								break;
							}
						}
					}			 while (this.bShort && boolean2);

					string = quest.getName();
					this.PaginateText(string, false, quest);
				}		 while (quest.Complete);
			}	 while (quest.Failed);

			Iterator iterator2 = quest.QuestTaskStack.iterator();
			while (true) {
				QuestTask questTask;
				do {
					do {
						if (!iterator2.hasNext()) {
							boolean2 = true;
							continue label84;
						}

						questTask = (QuestTask)iterator2.next();
					}			 while (!questTask.Unlocked);
				}		 while (this.bShort && (questTask.Complete || questTask.Failed || questTask.Hidden || questTask.getName().length() == 0));

				string = questTask.getName();
				this.PaginateText(string, true, questTask);
				boolean3 = true;
			}
		}
	}

	private void PaginateText(String string, boolean boolean1, Completable completable) {
		float float1 = (float)this.getWidth().intValue();
		if (boolean1) {
			float1 -= 20.0F;
		}

		boolean boolean2 = false;
		int int1 = 0;
		do {
			int int2 = string.indexOf(" ", int1 + 1);
			int int3 = int2;
			if (int2 == -1) {
				int3 = string.length();
			}

			int int4 = TextManager.instance.MeasureStringX(UIFont.Small, string.substring(0, int3));
			if ((float)int4 >= float1) {
				this.maxWidthUsed = this.originalWidth;
				String string2 = string.substring(0, int1);
				string = string.substring(int1 + 1);
				this.Lines.add(string2);
				if (boolean1) {
					this.IndentLines.add(this.Lines.size() - 1);
				}

				if (!boolean2) {
					this.Status.add(completable);
					boolean2 = true;
				} else {
					this.Status.add((Object)null);
				}

				int2 = 0;
			} else {
				if (int4 > this.maxWidthUsed) {
					this.maxWidthUsed = int4 + 16;
				}

				if (int2 == -1) {
					this.Lines.add(string);
					if (!boolean2) {
						this.Status.add(completable);
						boolean2 = true;
					} else {
						this.Status.add((Object)null);
					}

					if (boolean1) {
						this.IndentLines.add(this.Lines.size() - 1);
					}

					return;
				}
			}

			int1 = int2;
		} while (string.length() > 0);

		if (boolean1) {
			this.maxWidthUsed += 20;
		}

		if (this.maxWidthUsed > this.originalWidth) {
			this.maxWidthUsed = this.originalWidth;
		}
	}
}
