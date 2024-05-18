package zombie.behaviors.survivor.orders;

import java.util.Stack;
import zombie.behaviors.survivor.orders.LittleTasks.AquireSheetAndBlockWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.characters.SurvivorDesc;
import zombie.core.Rand;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoWindow;
import zombie.ui.TextManager;
import zombie.ui.UIFont;


public class BlockWindows extends OrderSequence {
	Stack windowsAll = new Stack();
	Stack windowsFloorAB = new Stack();
	IsoBuilding b;

	public BlockWindows(IsoGameCharacter gameCharacter, IsoBuilding building) {
		super(gameCharacter);
		this.b = building;
	}

	public int renderDebug(int int1) {
		byte byte1 = 50;
		TextManager.instance.DrawString(UIFont.Small, (double)byte1, (double)int1, "BlockWindows", 1.0, 1.0, 1.0, 1.0);
		int1 += 30;
		if (this.ID < this.Orders.size()) {
			((Order)this.Orders.get(this.ID)).renderDebug(int1);
		}

		return int1;
	}

	public void initOrder() {
		int int1;
		if (((IsoSurvivor)this.character).getDescriptor().getGroup().Leader == this.character.getDescriptor()) {
			for (int1 = 0; int1 < this.character.getDescriptor().getGroup().Members.size(); ++int1) {
				SurvivorDesc survivorDesc = (SurvivorDesc)this.character.getDescriptor().getGroup().Members.get(int1);
				if (survivorDesc.getInstance() != null && (survivorDesc.getInstance().getOrder() == null || survivorDesc.getInstance().getOrder() instanceof FollowOrder)) {
					survivorDesc.getInstance().GiveOrder(new BlockWindows(survivorDesc.getInstance(), this.b), false);
				}
			}
		}

		if (this.b.Windows.size() > 0) {
			IsoWindow window;
			for (; !this.b.Windows.isEmpty(); this.b.Windows.remove(window)) {
				window = (IsoWindow)this.b.Windows.get(Rand.Next(this.b.Windows.size()));
				if (!this.windowsAll.contains(window)) {
					IsoCurtain curtain = window.HasCurtains();
					this.windowsAll.add(window);
					if (window.square.getZ() < 2 && (curtain == null || curtain.open)) {
						this.windowsFloorAB.add(window);
					}
				}
			}

			this.b.Windows.addAll(this.windowsAll);
			for (int1 = 0; int1 < this.windowsFloorAB.size(); ++int1) {
				IsoWindow window2 = (IsoWindow)this.windowsFloorAB.get(int1);
				IsoCurtain curtain2 = window2.HasCurtains();
				this.Orders.add(new AquireSheetAndBlockWindow(this.character, window2, curtain2));
			}
		}
	}
}
