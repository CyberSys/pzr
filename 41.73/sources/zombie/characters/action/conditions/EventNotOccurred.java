package zombie.characters.action.conditions;

import org.w3c.dom.Element;
import zombie.characters.action.ActionContext;
import zombie.characters.action.IActionCondition;


public final class EventNotOccurred implements IActionCondition {
	public String eventName;

	public String getDescription() {
		return "EventNotOccurred(" + this.eventName + ")";
	}

	private boolean load(Element element) {
		this.eventName = element.getTextContent().toLowerCase();
		return true;
	}

	public boolean passes(ActionContext actionContext, int int1) {
		return !actionContext.hasEventOccurred(this.eventName, int1);
	}

	public IActionCondition clone() {
		return null;
	}

	public static class Factory implements IActionCondition.IFactory {

		public IActionCondition create(Element element) {
			EventNotOccurred eventNotOccurred = new EventNotOccurred();
			return eventNotOccurred.load(element) ? eventNotOccurred : null;
		}
	}
}
