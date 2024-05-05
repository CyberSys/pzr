package zombie.core.skinnedmodel.model.jassimp;

import jassimp.AiScene;
import org.lwjgl.util.vector.Quaternion;
import zombie.core.skinnedmodel.model.SkinningData;


public class ProcessedAiSceneParams {
	public AiScene scene = null;
	public JAssImpImporter.LoadMode mode;
	public SkinningData skinnedTo;
	public String meshName;
	public float animBonesScaleModifier;
	public Quaternion animBonesRotateModifier;

	ProcessedAiSceneParams() {
		this.mode = JAssImpImporter.LoadMode.Normal;
		this.skinnedTo = null;
		this.meshName = null;
		this.animBonesScaleModifier = 1.0F;
		this.animBonesRotateModifier = null;
	}

	public static ProcessedAiSceneParams create() {
		return new ProcessedAiSceneParams();
	}

	protected void set(ProcessedAiSceneParams processedAiSceneParams) {
		this.scene = processedAiSceneParams.scene;
		this.mode = processedAiSceneParams.mode;
		this.skinnedTo = processedAiSceneParams.skinnedTo;
		this.meshName = processedAiSceneParams.meshName;
		this.animBonesScaleModifier = processedAiSceneParams.animBonesScaleModifier;
		this.animBonesRotateModifier = processedAiSceneParams.animBonesRotateModifier;
	}
}
