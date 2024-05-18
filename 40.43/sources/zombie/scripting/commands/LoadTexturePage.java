package zombie.scripting.commands;

import java.util.Stack;
import zombie.core.textures.TexturePackPage;
import zombie.network.GameServer;


public class LoadTexturePage extends BaseCommand {
	String page = null;
	public Stack Pairs = new Stack();

	public void init(String string, String[] stringArray) {
		this.page = stringArray[0];
		if (stringArray.length > 1) {
			String string2 = null;
			String string3 = null;
			boolean boolean1 = false;
			for (int int1 = 1; int1 < stringArray.length; ++int1) {
				if (!boolean1) {
					string3 = null;
					string2 = stringArray[int1];
				} else {
					string3 = stringArray[int1];
					this.Pairs.add(new LoadTexturePage.WatchPair(string2, string3));
					string2 = null;
					string3 = null;
				}

				boolean1 = !boolean1;
			}
		}
	}

	public void begin() {
		if (!GameServer.bServer) {
			if (!this.Pairs.isEmpty()) {
				TexturePackPage.getPackPage(this.page, this.Pairs);
			} else {
				TexturePackPage.getPackPage(this.page);
			}
		}
	}

	public void Finish() {
	}

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public boolean DoesInstantly() {
		return true;
	}

	public class WatchPair {
		public String name;
		public String token;

		public WatchPair(String string, String string2) {
			this.name = string;
			this.token = string2;
		}
	}
}
