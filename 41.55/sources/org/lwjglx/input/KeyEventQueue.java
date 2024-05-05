package org.lwjglx.input;

import org.lwjglx.Sys;


public final class KeyEventQueue {
	public static final int MAX_EVENTS = 32;
	private final EventQueue queue = new EventQueue(32);
	private final int[] keyEvents = new int[32];
	private final boolean[] keyEventStates = new boolean[32];
	private final long[] nanoTimeEvents = new long[32];
	private final char[] keyEventChars = new char[256];

	public void addKeyEvent(int int1, int int2) {
		switch (int2) {
		case 2: 
			if (!Keyboard.isRepeatEvent()) {
				break;
			}

		
		case 0: 
		
		case 1: 
			this.keyEvents[this.queue.getNextPos()] = KeyCodes.toLwjglKey(int1);
			this.keyEventStates[this.queue.getNextPos()] = int2 == 1 || int2 == 2;
			this.keyEventChars[this.queue.getNextPos()] = 0;
			this.nanoTimeEvents[this.queue.getNextPos()] = Sys.getNanoTime();
			this.queue.add();
		
		}
	}

	public void addCharEvent(char char1) {
		this.keyEvents[this.queue.getNextPos()] = 0;
		this.keyEventStates[this.queue.getNextPos()] = true;
		this.keyEventChars[this.queue.getNextPos()] = char1;
		this.nanoTimeEvents[this.queue.getNextPos()] = Sys.getNanoTime();
		this.queue.add();
	}

	public boolean next() {
		return this.queue.next();
	}

	public int getEventKey() {
		return this.keyEvents[this.queue.getCurrentPos()];
	}

	public char getEventCharacter() {
		return this.keyEventChars[this.queue.getCurrentPos()];
	}

	public boolean getEventKeyState() {
		return this.keyEventStates[this.queue.getCurrentPos()];
	}

	public long getEventNanoseconds() {
		return this.nanoTimeEvents[this.queue.getCurrentPos()];
	}
}
