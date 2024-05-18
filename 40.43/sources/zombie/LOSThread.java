package zombie;

import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;


public class LOSThread {
	public static LOSThread instance = new LOSThread();
	public Thread losThread;
	public boolean finished = false;
	public boolean running = false;
	public Stack SeenList = new Stack();
	public Stack Jobs = new Stack();

	public void Start() {
	}

	public void AddJob(IsoGameCharacter gameCharacter) {
	}

	private void run() throws InterruptedException {
	}

	public class LOSJob {
		public IsoGameCharacter POVCharacter;

		private void Execute() {
			LOSThread.this.SeenList.clear();
			for (int int1 = 0; int1 < IsoWorld.instance.CurrentCell.getObjectList().size(); ++int1) {
				IsoMovingObject movingObject = (IsoMovingObject)IsoWorld.instance.CurrentCell.getObjectList().get(int1);
				if (movingObject != this.POVCharacter && movingObject instanceof IsoGameCharacter && (!(movingObject instanceof IsoZombie) || !((IsoZombie)movingObject).Ghost)) {
					float float1 = movingObject.DistTo(this.POVCharacter);
					if (!(float1 > GameTime.getInstance().getViewDist()) && this.POVCharacter.CanSee(movingObject)) {
						LOSThread.this.SeenList.add(movingObject);
					}
				}
			}

			this.POVCharacter.Seen(LOSThread.this.SeenList);
			LOSThread.this.SeenList.clear();
		}
	}
}
