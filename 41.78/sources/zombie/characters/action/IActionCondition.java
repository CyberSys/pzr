package zombie.characters.action;

import java.util.HashMap;
import org.w3c.dom.Element;


public interface IActionCondition {
	HashMap s_factoryMap = new HashMap();

	String getDescription();

	boolean passes(ActionContext actionContext, int int1);

	IActionCondition clone();

	static IActionCondition createInstance(Element element) {
		IActionCondition.IFactory iFactory = (IActionCondition.IFactory)s_factoryMap.get(element.getNodeName());
		return iFactory != null ? iFactory.create(element) : null;
	}

	static void registerFactory(String string, IActionCondition.IFactory iFactory) {
		s_factoryMap.put(string, iFactory);
	}

	public interface IFactory {

		IActionCondition create(Element element);
	}
}
