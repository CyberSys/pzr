package zombie.core.skinnedmodel.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;
import zombie.core.skinnedmodel.shader.Shader;


public final class ModelMesh extends Asset {
	public VertexBufferObject vb;
	public final Vector3f minXYZ = new Vector3f(Float.MAX_VALUE);
	public final Vector3f maxXYZ = new Vector3f(-3.4028235E38F);
	public SkinningData skinningData;
	public SoftwareModelMesh softwareMesh;
	public ModelMesh.MeshAssetParams assetParams;
	public Matrix4f m_transform;
	public boolean m_bHasVBO = false;
	protected boolean bStatic;
	public ModelMesh m_animationsMesh;
	public String m_fullPath;
	public static final AssetType ASSET_TYPE = new AssetType("Mesh");

	public ModelMesh(AssetPath assetPath, AssetManager assetManager, ModelMesh.MeshAssetParams meshAssetParams) {
		super(assetPath, assetManager);
		this.assetParams = meshAssetParams;
		this.bStatic = this.assetParams != null && this.assetParams.bStatic;
		this.m_animationsMesh = this.assetParams == null ? null : this.assetParams.animationsMesh;
	}

	protected void onLoadedX(ProcessedAiScene processedAiScene) {
		JAssImpImporter.LoadMode loadMode = this.assetParams.bStatic ? JAssImpImporter.LoadMode.StaticMesh : JAssImpImporter.LoadMode.Normal;
		SkinningData skinningData = this.assetParams.animationsMesh == null ? null : this.assetParams.animationsMesh.skinningData;
		processedAiScene.applyToMesh(this, loadMode, false, skinningData);
	}

	protected void onLoadedTxt(ModelTxt modelTxt) {
		SkinningData skinningData = this.assetParams.animationsMesh == null ? null : this.assetParams.animationsMesh.skinningData;
		ModelLoader.instance.applyToMesh(modelTxt, this, skinningData);
	}

	public void SetVertexBuffer(VertexBufferObject vertexBufferObject) {
		this.clear();
		this.vb = vertexBufferObject;
		this.bStatic = vertexBufferObject == null || vertexBufferObject.bStatic;
	}

	public void Draw(Shader shader) {
		if (this.vb != null) {
			this.vb.Draw(shader);
		}
	}

	public void onBeforeReady() {
		super.onBeforeReady();
		if (this.assetParams != null) {
			this.assetParams.animationsMesh = null;
			this.assetParams = null;
		}
	}

	public boolean isReady() {
		return super.isReady() && (!this.m_bHasVBO || this.vb != null);
	}

	public void setAssetParams(AssetManager.AssetParams assetParams) {
		this.assetParams = (ModelMesh.MeshAssetParams)assetParams;
	}

	public AssetType getType() {
		return ASSET_TYPE;
	}

	public void clear() {
		if (this.vb != null) {
			this.vb.clear();
			this.vb = null;
		}
	}

	public static final class MeshAssetParams extends AssetManager.AssetParams {
		public boolean bStatic;
		public ModelMesh animationsMesh;
	}
}
