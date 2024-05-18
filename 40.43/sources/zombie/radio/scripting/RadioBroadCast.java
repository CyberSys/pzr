package zombie.radio.scripting;

import java.util.ArrayList;


public class RadioBroadCast {
	private static RadioLine pauseLine = new RadioLine("~", 0.5F, 0.5F, 0.5F);
	private ArrayList lines = new ArrayList();
	private String ID = "";
	private int startStamp = 0;
	private int endStamp = 0;
	private int lineCount = 0;
	private RadioBroadCast preSegment = null;
	private RadioBroadCast postSegment = null;
	private boolean hasDonePreSegment = false;
	private boolean hasDonePostSegment = false;
	private boolean hasDonePostPause = false;

	public RadioBroadCast(String string, int int1, int int2) {
		this.ID = string;
		this.startStamp = int1;
		this.endStamp = int2;
	}

	public String getID() {
		return this.ID;
	}

	public int getStartStamp() {
		return this.startStamp;
	}

	public int getEndStamp() {
		return this.endStamp;
	}

	public void resetLineCounter() {
		this.resetLineCounter(true);
	}

	public void resetLineCounter(boolean boolean1) {
		this.lineCount = 0;
		if (boolean1) {
			if (this.preSegment != null) {
				this.preSegment.resetLineCounter(false);
			}

			if (this.postSegment != null) {
				this.postSegment.resetLineCounter(false);
			}
		}
	}

	public void setPreSegment(RadioBroadCast radioBroadCast) {
		this.preSegment = radioBroadCast;
	}

	public void setPostSegment(RadioBroadCast radioBroadCast) {
		this.postSegment = radioBroadCast;
	}

	public RadioLine getNextLine() {
		return this.getNextLine(true);
	}

	public RadioLine getNextLine(boolean boolean1) {
		RadioLine radioLine = null;
		if (boolean1 && !this.hasDonePreSegment && this.lineCount == 0 && this.preSegment != null) {
			radioLine = this.preSegment.getNextLine();
			if (radioLine != null) {
				return radioLine;
			} else {
				this.hasDonePreSegment = true;
				return pauseLine;
			}
		} else {
			if (this.lineCount >= 0 && this.lineCount < this.lines.size()) {
				radioLine = (RadioLine)this.lines.get(this.lineCount);
			}

			if (boolean1 && radioLine == null && this.postSegment != null) {
				if (!this.hasDonePostPause) {
					this.hasDonePostPause = true;
					return pauseLine;
				} else {
					radioLine = this.postSegment.getNextLine();
					return radioLine;
				}
			} else {
				++this.lineCount;
				return radioLine;
			}
		}
	}

	public int getCurrentLineNumber() {
		return this.lineCount;
	}

	public void setCurrentLineNumber(int int1) {
		this.lineCount = int1;
		if (this.lineCount < 0) {
			this.lineCount = 0;
		}
	}

	public RadioLine getCurrentLine() {
		return this.lineCount >= 0 && this.lineCount < this.lines.size() ? (RadioLine)this.lines.get(this.lineCount) : null;
	}

	public String PeekNextLineText() {
		if (this.lineCount >= 0 && this.lineCount < this.lines.size()) {
			return this.lines.get(this.lineCount) != null && ((RadioLine)this.lines.get(this.lineCount)).getText() != null ? ((RadioLine)this.lines.get(this.lineCount)).getText() : "Error";
		} else {
			return "None";
		}
	}

	public void AddRadioLine(RadioLine radioLine) {
		if (radioLine != null) {
			this.lines.add(radioLine);
		}
	}
}
