package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Iterator;
import zombie.core.skinnedmodel.model.ModelMesh;
import zombie.scripting.ScriptParser;


public final class AnimationsMesh extends BaseScriptObject {
	public String name = null;
	public String meshFile = null;
	public final ArrayList animationDirectories = new ArrayList();
	public ModelMesh modelMesh = null;

	public void Load(String string, String string2) {
		this.name = string;
		ScriptParser.Block block = ScriptParser.parse(string2);
		block = (ScriptParser.Block)block.children.get(0);
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string3 = value.getKey().trim();
			String string4 = value.getValue().trim();
			if ("meshFile".equalsIgnoreCase(string3)) {
				this.meshFile = string4;
			} else if ("animationDirectory".equalsIgnoreCase(string3)) {
				this.animationDirectories.add(string4);
			}
		}
	}

	public void reset() {
		this.meshFile = null;
		this.animationDirectories.clear();
		this.modelMesh = null;
	}
}
