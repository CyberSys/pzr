package zombie.characters.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Element;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.debug.DebugLogStream;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.network.GameClient;
import zombie.util.Lambda;
import zombie.util.PZXmlUtil;
import zombie.util.StringUtils;


public final class ActionTransition implements Cloneable {
	String transitionTo;
	boolean asSubstate;
	boolean transitionOut;
	boolean forceParent;
	final List conditions = new ArrayList();

	public static boolean parse(Element element, String string, List list) {
		if (element.getNodeName().equals("transitions")) {
			parseTransitions(element, string, list);
			return true;
		} else if (element.getNodeName().equals("transition")) {
			parseTransition(element, list);
			return true;
		} else {
			return false;
		}
	}

	public static void parseTransition(Element element, List list) {
		list.clear();
		ActionTransition actionTransition = new ActionTransition();
		if (actionTransition.load(element)) {
			list.add(actionTransition);
		}
	}

	public static void parseTransitions(Element element, String string, List list) {
		list.clear();
		Lambda.forEachFrom(PZXmlUtil::forEachElement, (Object)element, string, list, (elementx,stringx,listx)->{
			if (!elementx.getNodeName().equals("transition")) {
				DebugLogStream debugLogStream = DebugLog.ActionSystem;
				String string2 = elementx.getNodeName();
				debugLogStream.warn("Warning: Unrecognised element \'" + string2 + "\' in " + stringx);
			} else {
				ActionTransition actionTransition = new ActionTransition();
				if (actionTransition.load(elementx)) {
					listx.add(actionTransition);
				}
			}
		});
	}

	private boolean load(Element element) {
		try {
			PZXmlUtil.forEachElement(element, (elementx)->{
				try {
					String string = elementx.getNodeName();
					if ("transitionTo".equalsIgnoreCase(string)) {
						this.transitionTo = elementx.getTextContent();
					} else if ("transitionOut".equalsIgnoreCase(string)) {
						this.transitionOut = StringUtils.tryParseBoolean(elementx.getTextContent());
					} else if ("forceParent".equalsIgnoreCase(string)) {
						this.forceParent = StringUtils.tryParseBoolean(elementx.getTextContent());
					} else if ("asSubstate".equalsIgnoreCase(string)) {
						this.asSubstate = StringUtils.tryParseBoolean(elementx.getTextContent());
					} else if ("conditions".equalsIgnoreCase(string)) {
						PZXmlUtil.forEachElement(elementx, (element)->{
							IActionCondition string = IActionCondition.createInstance(element);
							if (string != null) {
								this.conditions.add(string);
							}
						});
					}
				} catch (Exception exception) {
					DebugLog.ActionSystem.error("Error while parsing xml element: " + elementx.getNodeName());
					DebugLog.ActionSystem.error(exception);
				}
			});

			return true;
		} catch (Exception exception) {
			DebugLog.ActionSystem.error("Error while loading an ActionTransition element");
			DebugLog.ActionSystem.error(exception);
			return false;
		}
	}

	public String getTransitionTo() {
		return this.transitionTo;
	}

	public boolean passes(ActionContext actionContext, int int1) {
		for (int int2 = 0; int2 < this.conditions.size(); ++int2) {
			IActionCondition iActionCondition = (IActionCondition)this.conditions.get(int2);
			if (!iActionCondition.passes(actionContext, int1)) {
				return false;
			}
		}

		if (Core.bDebug && GameClient.bClient && (DebugOptions.instance.MultiplayerShowPlayerStatus.getValue() && actionContext.getOwner() instanceof IsoPlayer || DebugOptions.instance.MultiplayerShowZombieStatus.getValue() && actionContext.getOwner() instanceof IsoZombie)) {
			StringBuilder stringBuilder = (new StringBuilder("Character ")).append(actionContext.getOwner().getClass().getSimpleName()).append(" ").append("id=").append(actionContext.getOwner().getOnlineID()).append(" transition to \"").append(this.transitionTo).append("\":");
			Iterator iterator = this.conditions.iterator();
			while (iterator.hasNext()) {
				IActionCondition iActionCondition2 = (IActionCondition)iterator.next();
				stringBuilder.append(" [").append(iActionCondition2.getDescription()).append("]");
			}

			DebugLog.log(DebugType.ActionSystem, stringBuilder.toString());
		}

		return true;
	}

	public ActionTransition clone() {
		ActionTransition actionTransition = new ActionTransition();
		actionTransition.transitionTo = this.transitionTo;
		actionTransition.asSubstate = this.asSubstate;
		actionTransition.transitionOut = this.transitionOut;
		actionTransition.forceParent = this.forceParent;
		Iterator iterator = this.conditions.iterator();
		while (iterator.hasNext()) {
			IActionCondition iActionCondition = (IActionCondition)iterator.next();
			actionTransition.conditions.add(iActionCondition.clone());
		}

		return actionTransition;
	}
}
