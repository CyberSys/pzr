package zombie.scripting.objects;

import java.util.Iterator;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;


public final class MannequinScript extends BaseScriptObject {
	private String name;
	private boolean bFemale = true;
	private String modelScriptName;
	private String texture;
	private String animSet;
	private String animState;
	private String pose;
	private String outfit;

	public String getName() {
		return this.name;
	}

	public boolean isFemale() {
		return this.bFemale;
	}

	public void setFemale(boolean boolean1) {
		this.bFemale = boolean1;
	}

	public String getModelScriptName() {
		return this.modelScriptName;
	}

	public void setModelScriptName(String string) {
		this.modelScriptName = StringUtils.discardNullOrWhitespace(string);
	}

	public String getTexture() {
		return this.texture;
	}

	public void setTexture(String string) {
		this.texture = StringUtils.discardNullOrWhitespace(string);
	}

	public String getAnimSet() {
		return this.animSet;
	}

	public void setAnimSet(String string) {
		this.animSet = StringUtils.discardNullOrWhitespace(string);
	}

	public String getAnimState() {
		return this.animState;
	}

	public void setAnimState(String string) {
		this.animState = StringUtils.discardNullOrWhitespace(string);
	}

	public String getPose() {
		return this.pose;
	}

	public void setPose(String string) {
		this.pose = StringUtils.discardNullOrWhitespace(string);
	}

	public String getOutfit() {
		return this.outfit;
	}

	public void setOutfit(String string) {
		this.outfit = StringUtils.discardNullOrWhitespace(string);
	}

	public void Load(String string, String string2) {
		this.name = string;
		ScriptParser.Block block = ScriptParser.parse(string2);
		block = (ScriptParser.Block)block.children.get(0);
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string3 = value.getKey().trim();
			String string4 = value.getValue().trim();
			if ("female".equalsIgnoreCase(string3)) {
				this.bFemale = StringUtils.tryParseBoolean(string4);
			} else if ("model".equalsIgnoreCase(string3)) {
				this.modelScriptName = StringUtils.discardNullOrWhitespace(string4);
			} else if ("texture".equalsIgnoreCase(string3)) {
				this.texture = StringUtils.discardNullOrWhitespace(string4);
			} else if ("animSet".equalsIgnoreCase(string3)) {
				this.animSet = StringUtils.discardNullOrWhitespace(string4);
			} else if ("animState".equalsIgnoreCase(string3)) {
				this.animState = StringUtils.discardNullOrWhitespace(string4);
			} else if ("pose".equalsIgnoreCase(string3)) {
				this.pose = StringUtils.discardNullOrWhitespace(string4);
			} else if ("outfit".equalsIgnoreCase(string3)) {
				this.outfit = StringUtils.discardNullOrWhitespace(string4);
			}
		}
	}

	public void reset() {
		this.modelScriptName = null;
		this.texture = null;
		this.animSet = null;
		this.animState = null;
		this.pose = null;
		this.outfit = null;
	}
}
