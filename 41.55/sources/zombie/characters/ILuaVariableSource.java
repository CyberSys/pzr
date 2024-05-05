package zombie.characters;


public interface ILuaVariableSource {

	String GetVariable(String string);

	void SetVariable(String string, String string2);

	void ClearVariable(String string);
}
