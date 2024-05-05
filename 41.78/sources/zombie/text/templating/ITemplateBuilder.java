package zombie.text.templating;

import se.krka.kahlua.j2se.KahluaTableImpl;


public interface ITemplateBuilder {

	String Build(String string);

	String Build(String string, IReplaceProvider iReplaceProvider);

	String Build(String string, KahluaTableImpl kahluaTableImpl);

	void RegisterKey(String string, KahluaTableImpl kahluaTableImpl);

	void RegisterKey(String string, IReplace iReplace);

	void Reset();
}
