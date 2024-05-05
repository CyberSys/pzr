package zombie.core.stash;

import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import zombie.util.Type;


public final class StashAnnotation {
	public String symbol;
	public String text;
	public float x;
	public float y;
	public float r;
	public float g;
	public float b;

	public void fromLua(KahluaTable kahluaTable) {
		KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)kahluaTable;
		this.symbol = (String)Type.tryCastTo(kahluaTable.rawget("symbol"), String.class);
		this.text = (String)Type.tryCastTo(kahluaTable.rawget("text"), String.class);
		this.x = kahluaTableImpl.rawgetFloat("x");
		this.y = kahluaTableImpl.rawgetFloat("y");
		this.r = kahluaTableImpl.rawgetFloat("r");
		this.g = kahluaTableImpl.rawgetFloat("g");
		this.b = kahluaTableImpl.rawgetFloat("b");
	}
}
