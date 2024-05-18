package zombie.scripting.commands;

import zombie.scripting.objects.Conditional;
import zombie.scripting.objects.Script;


public class ConditionalCommand extends BaseCommand {
	boolean bDoIt = false;
	Script.ScriptInstance inst;
	public Script parent;
	Conditional con;
	Conditional elsecon;
	Script.ScriptInstance elseinst;

	public ConditionalCommand(String string, String string2, Script script) {
		this.con = new Conditional(string, string2, this);
		this.inst = new Script.ScriptInstance();
		this.inst.theScript = this.con;
		this.parent = script;
	}

	public void init(String string, String[] stringArray) {
	}

	public boolean AllowCharacterBehaviour(String string) {
		if (this.bDoIt) {
			return this.con.AllowCharacterBehaviour(string, this.inst);
		} else {
			return this.elsecon != null ? this.elsecon.AllowCharacterBehaviour(string, this.inst) : true;
		}
	}

	public void begin() {
		this.bDoIt = this.con.ConditionPassed(this.currentinstance);
		if (this.bDoIt) {
			this.inst.CopyAliases(this.currentinstance);
			this.inst.theScript = this.con;
			this.inst.begin();
		} else if (this.elsecon != null) {
			this.elseinst.CopyAliases(this.currentinstance);
			this.elseinst.theScript = this.elsecon;
			this.elseinst.begin();
		}
	}

	public boolean IsFinished() {
		return !this.bDoIt && this.elsecon == null || this.bDoIt && this.inst.finished() || !this.bDoIt && this.elsecon != null && this.elseinst.finished();
	}

	public void update() {
		if (this.bDoIt) {
			this.inst.CopyAliases(this.currentinstance);
			this.inst.update();
		} else if (this.elsecon != null) {
			this.elseinst.CopyAliases(this.currentinstance);
			this.elseinst.update();
		}
	}

	public boolean DoesInstantly() {
		return false;
	}

	public void AddElse(String string) {
		this.elsecon = new Conditional((String)null, string);
		this.elseinst = new Script.ScriptInstance();
		this.elseinst.theScript = this.elsecon;
	}
}
