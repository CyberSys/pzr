package zombie.Lua;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;


public class LuaBackendClass implements KahluaTable {
	KahluaTable table;
	KahluaTable typeTable;

	public String getString(String string) {
		return (String)this.rawget(string);
	}

	public LuaBackendClass(String string) {
		this.typeTable = (KahluaTable)LuaManager.env.rawget(string);
	}

	public void callVoid(String string) {
		LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget(string), (Object)this.table);
	}

	public void callVoid(String string, Object object) {
		LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget(string), new Object[]{this.table, object});
	}

	public void callVoid(String string, Object object, Object object2) {
		LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget(string), new Object[]{this.table, object, object2});
	}

	public void callVoid(String string, Object object, Object object2, Object object3) {
		LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget(string), new Object[]{this.table, object, object2, object3});
	}

	public void callVoid(String string, Object object, Object object2, Object object3, Object object4) {
		LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget(string), new Object[]{this.table, object, object2, object3, object4});
	}

	public void callVoid(String string, Object object, Object object2, Object object3, Object object4, Object object5) {
		LuaManager.caller.pcallvoid(LuaManager.thread, this.typeTable.rawget(string), new Object[]{this.table, object, object2, object3, object4, object5});
	}

	public Object call(String string) {
		return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), (Object)this.table)[1];
	}

	public Object call(String string, Object object) {
		return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object)[1];
	}

	public Object call(String string, Object object, Object object2) {
		return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2)[1];
	}

	public Object call(String string, Object object, Object object2, Object object3) {
		return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2, object3)[1];
	}

	public Object call(String string, Object object, Object object2, Object object3, Object object4) {
		return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2, object3, object4)[1];
	}

	public Object call(String string, Object object, Object object2, Object object3, Object object4, Object object5) {
		return LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2, object3, object4, object5)[1];
	}

	public int callInt(String string) {
		return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), (Object)this.table)[1]).intValue();
	}

	public int callInt(String string, Object object) {
		return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object)[1]).intValue();
	}

	public int callInt(String string, Object object, Object object2) {
		return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2)[1]).intValue();
	}

	public int callInt(String string, Object object, Object object2, Object object3) {
		return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2, object3)[1]).intValue();
	}

	public int callInt(String string, Object object, Object object2, Object object3, Object object4) {
		return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2, object3, object4)[1]).intValue();
	}

	public int callInt(String string, Object object, Object object2, Object object3, Object object4, Object object5) {
		return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2, object3, object4, object5)[1]).intValue();
	}

	public float callFloat(String string) {
		return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), (Object)this.table)[1]).floatValue();
	}

	public float callFloat(String string, Object object) {
		return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object)[1]).floatValue();
	}

	public float callFloat(String string, Object object, Object object2) {
		return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2)[1]).floatValue();
	}

	public float callFloat(String string, Object object, Object object2, Object object3) {
		return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2, object3)[1]).floatValue();
	}

	public float callFloat(String string, Object object, Object object2, Object object3, Object object4) {
		return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2, object3, object4)[1]).floatValue();
	}

	public float callFloat(String string, Object object, Object object2, Object object3, Object object4, Object object5) {
		return ((Double)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2, object3, object4, object5)[1]).floatValue();
	}

	public boolean callBool(String string) {
		return (Boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), (Object)this.table)[1];
	}

	public boolean callBool(String string, Object object) {
		return (Boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object)[1];
	}

	public boolean callBool(String string, Object object, Object object2) {
		return (Boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2)[1];
	}

	public boolean callBool(String string, Object object, Object object2, Object object3) {
		return (Boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2, object3)[1];
	}

	public boolean callBool(String string, Object object, Object object2, Object object3, Object object4) {
		return (Boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2, object3, object4)[1];
	}

	public boolean callBool(String string, Object object, Object object2, Object object3, Object object4, Object object5) {
		return (Boolean)LuaManager.caller.pcall(LuaManager.thread, this.typeTable.rawget(string), this.table, object, object2, object3, object4, object5)[1];
	}

	public void setMetatable(KahluaTable kahluaTable) {
		this.table.setMetatable(kahluaTable);
	}

	public KahluaTable getMetatable() {
		return this.table.getMetatable();
	}

	public void rawset(Object object, Object object2) {
		this.table.rawset(object, object2);
	}

	public Object rawget(Object object) {
		return this.table.rawget(object);
	}

	public void rawset(int int1, Object object) {
		this.table.rawset(int1, object);
	}

	public Object rawget(int int1) {
		return this.table.rawget(int1);
	}

	public int len() {
		return this.table.len();
	}

	public int size() {
		return this.table.len();
	}

	public KahluaTableIterator iterator() {
		return this.table.iterator();
	}

	public boolean isEmpty() {
		return this.table.isEmpty();
	}

	public void wipe() {
		this.table.wipe();
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		this.table.save(byteBuffer);
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.table.load(byteBuffer, int1);
	}

	public void save(DataOutputStream dataOutputStream) throws IOException {
		this.table.save(dataOutputStream);
	}

	public void load(DataInputStream dataInputStream, int int1) throws IOException {
		this.table.load(dataInputStream, int1);
	}
}
