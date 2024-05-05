package zombie.characters.action;


public final class ActionContextEvents {
	private ActionContextEvents.Event m_firstEvent;
	private ActionContextEvents.Event m_eventPool;

	public void add(String string, int int1) {
		if (!this.contains(string, int1, false)) {
			ActionContextEvents.Event event = this.allocEvent();
			event.name = string;
			event.layer = int1;
			event.next = this.m_firstEvent;
			this.m_firstEvent = event;
		}
	}

	public boolean contains(String string, int int1) {
		return this.contains(string, int1, true);
	}

	public boolean contains(String string, int int1, boolean boolean1) {
		for (ActionContextEvents.Event event = this.m_firstEvent; event != null; event = event.next) {
			if (event.name.equalsIgnoreCase(string)) {
				if (int1 == -1) {
					return true;
				}

				if (event.layer == int1) {
					return true;
				}

				if (boolean1 && event.layer == -1) {
					return true;
				}
			}
		}

		return false;
	}

	public void clear() {
		if (this.m_firstEvent != null) {
			ActionContextEvents.Event event;
			for (event = this.m_firstEvent; event.next != null; event = event.next) {
			}

			event.next = this.m_eventPool;
			this.m_eventPool = this.m_firstEvent;
			this.m_firstEvent = null;
		}
	}

	public void clearEvent(String string) {
		ActionContextEvents.Event event = null;
		ActionContextEvents.Event event2;
		for (ActionContextEvents.Event event3 = this.m_firstEvent; event3 != null; event3 = event2) {
			event2 = event3.next;
			if (event3.name.equalsIgnoreCase(string)) {
				this.releaseEvent(event3, event);
			} else {
				event = event3;
			}
		}
	}

	private ActionContextEvents.Event allocEvent() {
		if (this.m_eventPool == null) {
			return new ActionContextEvents.Event();
		} else {
			ActionContextEvents.Event event = this.m_eventPool;
			this.m_eventPool = event.next;
			return event;
		}
	}

	private void releaseEvent(ActionContextEvents.Event event, ActionContextEvents.Event event2) {
		if (event2 == null) {
			assert event == this.m_firstEvent;
			this.m_firstEvent = event.next;
		} else {
			assert event != this.m_firstEvent;
			assert event2.next == event;
			event2.next = event.next;
		}

		event.next = this.m_eventPool;
		this.m_eventPool = event;
	}

	private static final class Event {
		int layer;
		String name;
		ActionContextEvents.Event next;
	}
}
