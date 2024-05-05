package zombie.core.skinnedmodel;

import zombie.characters.IsoGameCharacter;
import zombie.core.textures.TextureDraw;
import zombie.iso.IsoMovingObject;
import zombie.popman.ObjectPool;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;


public final class ModelCameraRenderData extends TextureDraw.GenericDrawer {
	private ModelCamera m_camera;
	private float m_angle;
	private boolean m_bUseWorldIso;
	private float m_x;
	private float m_y;
	private float m_z;
	private boolean m_bInVehicle;
	public static final ObjectPool s_pool = new ObjectPool(ModelCameraRenderData::new);

	public ModelCameraRenderData init(ModelCamera modelCamera, ModelManager.ModelSlot modelSlot) {
		IsoMovingObject movingObject = modelSlot.model.object;
		IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo(movingObject, IsoGameCharacter.class);
		this.m_camera = modelCamera;
		this.m_x = movingObject.x;
		this.m_y = movingObject.y;
		this.m_z = movingObject.z;
		if (gameCharacter == null) {
			this.m_angle = 0.0F;
			this.m_bInVehicle = false;
			this.m_bUseWorldIso = !BaseVehicle.RENDER_TO_TEXTURE;
		} else {
			this.m_bInVehicle = gameCharacter.isSeatedInVehicle();
			if (this.m_bInVehicle) {
				this.m_angle = 0.0F;
				BaseVehicle baseVehicle = gameCharacter.getVehicle();
				this.m_x = baseVehicle.x;
				this.m_y = baseVehicle.y;
				this.m_z = baseVehicle.z;
			} else {
				this.m_angle = gameCharacter.getAnimationPlayer().getRenderedAngle();
			}

			this.m_bUseWorldIso = true;
		}

		return this;
	}

	public ModelCameraRenderData init(ModelCamera modelCamera, float float1, boolean boolean1, float float2, float float3, float float4, boolean boolean2) {
		this.m_camera = modelCamera;
		this.m_angle = float1;
		this.m_bUseWorldIso = boolean1;
		this.m_x = float2;
		this.m_y = float3;
		this.m_z = float4;
		this.m_bInVehicle = boolean2;
		return this;
	}

	public void render() {
		this.m_camera.m_useAngle = this.m_angle;
		this.m_camera.m_bUseWorldIso = this.m_bUseWorldIso;
		this.m_camera.m_x = this.m_x;
		this.m_camera.m_y = this.m_y;
		this.m_camera.m_z = this.m_z;
		this.m_camera.m_bInVehicle = this.m_bInVehicle;
		ModelCamera.instance = this.m_camera;
	}

	public void postRender() {
		s_pool.release((Object)this);
	}
}
