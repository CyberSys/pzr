package zombie.core.skinnedmodel.runtime;

import java.util.ArrayList;
import java.util.Iterator;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.scripting.ScriptParser;
import zombie.scripting.objects.BaseScriptObject;


public final class RuntimeAnimationScript extends BaseScriptObject {
	protected String m_name = this.toString();
	protected final ArrayList m_commands = new ArrayList();

	public void Load(String string, String string2) {
		this.m_name = string;
		ScriptParser.Block block = ScriptParser.parse(string2);
		block = (ScriptParser.Block)block.children.get(0);
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string3 = value.getKey().trim();
			String string4 = value.getValue().trim();
			if ("xxx".equals(string3)) {
			}
		}

		iterator = block.children.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Block block2 = (ScriptParser.Block)iterator.next();
			if ("CopyFrame".equals(block2.type)) {
				CopyFrame copyFrame = new CopyFrame();
				copyFrame.parse(block2);
				this.m_commands.add(copyFrame);
			} else if ("CopyFrames".equals(block2.type)) {
				CopyFrames copyFrames = new CopyFrames();
				copyFrames.parse(block2);
				this.m_commands.add(copyFrames);
			}
		}
	}

	public void exec() {
		ArrayList arrayList = new ArrayList();
		Iterator iterator = this.m_commands.iterator();
		while (iterator.hasNext()) {
			IRuntimeAnimationCommand iRuntimeAnimationCommand = (IRuntimeAnimationCommand)iterator.next();
			iRuntimeAnimationCommand.exec(arrayList);
		}

		float float1 = 0.0F;
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			float1 = Math.max(float1, ((Keyframe)arrayList.get(int1)).Time);
		}

		AnimationClip animationClip = new AnimationClip(float1, arrayList, this.m_name, true);
		arrayList.clear();
		ModelManager.instance.addAnimationClip(animationClip.Name, animationClip);
		arrayList.clear();
	}

	public void reset() {
		this.m_name = this.toString();
		this.m_commands.clear();
	}
}
