package zombie.vehicles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import zombie.IndieGL;
import zombie.Lua.LuaManager;
import zombie.characters.action.ActionContext;
import zombie.characters.action.ActionGroup;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.VBOLines;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.advancedanimation.AnimNode;
import zombie.core.skinnedmodel.advancedanimation.AnimState;
import zombie.core.skinnedmodel.advancedanimation.AnimatedModel;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.AnimationMultiTrack;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelInstanceRenderData;
import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.skinnedmodel.shader.ShaderManager;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.input.Mouse;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;
import zombie.popman.ObjectPool;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.UIElement;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;


public final class UI3DScene extends UIElement {
	private final ArrayList m_objects = new ArrayList();
	private UI3DScene.View m_view;
	private UI3DScene.TransformMode m_transformMode;
	private int m_view_x;
	private int m_view_y;
	private final Vector3f m_viewRotation;
	private int m_zoom;
	private int m_zoomMax;
	private int m_gridDivisions;
	private UI3DScene.GridPlane m_gridPlane;
	private final Matrix4f m_projection;
	private final Matrix4f m_modelView;
	private long VIEW_CHANGE_TIME;
	private long m_viewChangeTime;
	private final Quaternionf m_modelViewChange;
	private boolean m_bDrawGrid;
	private boolean m_bDrawGridAxes;
	private boolean m_bDrawGridPlane;
	private final UI3DScene.CharacterSceneModelCamera m_CharacterSceneModelCamera;
	private final UI3DScene.VehicleSceneModelCamera m_VehicleSceneModelCamera;
	private static final ObjectPool s_SetModelCameraPool = new ObjectPool(UI3DScene.SetModelCamera::new);
	private final UI3DScene.StateData[] m_stateData;
	private UI3DScene.Gizmo m_gizmo;
	private final UI3DScene.RotateGizmo m_rotateGizmo;
	private final UI3DScene.ScaleGizmo m_scaleGizmo;
	private final UI3DScene.TranslateGizmo m_translateGizmo;
	private final Vector3f m_gizmoPos;
	private final Vector3f m_gizmoRotate;
	private UI3DScene.SceneObject m_gizmoParent;
	private UI3DScene.SceneObject m_gizmoOrigin;
	private UI3DScene.SceneObject m_gizmoChild;
	private final UI3DScene.OriginAttachment m_originAttachment;
	private final UI3DScene.OriginBone m_originBone;
	private final UI3DScene.OriginGizmo m_originGizmo;
	private float m_gizmoScale;
	private String m_selectedAttachment;
	private final ArrayList m_axes;
	private final UI3DScene.OriginBone m_highlightBone;
	private static final ObjectPool s_posRotPool = new ObjectPool(UI3DScene.PositionRotation::new);
	private final ArrayList m_aabb;
	private static final ObjectPool s_aabbPool = new ObjectPool(UI3DScene.AABB::new);
	private final ArrayList m_box3D;
	private static final ObjectPool s_box3DPool = new ObjectPool(UI3DScene.Box3D::new);
	final Vector3f tempVector3f;
	final Vector4f tempVector4f;
	final int[] m_viewport;
	private final float GRID_DARK;
	private final float GRID_LIGHT;
	private float GRID_ALPHA;
	private final int HALF_GRID;
	private static final VBOLines vboLines = new VBOLines();
	private static final ThreadLocal TL_Ray_pool = ThreadLocal.withInitial(UI3DScene.RayObjectPool::new);
	private static final ThreadLocal TL_Plane_pool = ThreadLocal.withInitial(UI3DScene.PlaneObjectPool::new);
	static final float SMALL_NUM = 1.0E-8F;

	public UI3DScene(KahluaTable kahluaTable) {
		super(kahluaTable);
		this.m_view = UI3DScene.View.Right;
		this.m_transformMode = UI3DScene.TransformMode.Local;
		this.m_view_x = 0;
		this.m_view_y = 0;
		this.m_viewRotation = new Vector3f();
		this.m_zoom = 3;
		this.m_zoomMax = 10;
		this.m_gridDivisions = 1;
		this.m_gridPlane = UI3DScene.GridPlane.YZ;
		this.m_projection = new Matrix4f();
		this.m_modelView = new Matrix4f();
		this.VIEW_CHANGE_TIME = 350L;
		this.m_modelViewChange = new Quaternionf();
		this.m_bDrawGrid = true;
		this.m_bDrawGridAxes = false;
		this.m_bDrawGridPlane = false;
		this.m_CharacterSceneModelCamera = new UI3DScene.CharacterSceneModelCamera();
		this.m_VehicleSceneModelCamera = new UI3DScene.VehicleSceneModelCamera();
		this.m_stateData = new UI3DScene.StateData[3];
		this.m_rotateGizmo = new UI3DScene.RotateGizmo();
		this.m_scaleGizmo = new UI3DScene.ScaleGizmo();
		this.m_translateGizmo = new UI3DScene.TranslateGizmo();
		this.m_gizmoPos = new Vector3f();
		this.m_gizmoRotate = new Vector3f();
		this.m_gizmoParent = null;
		this.m_gizmoOrigin = null;
		this.m_gizmoChild = null;
		this.m_originAttachment = new UI3DScene.OriginAttachment(this);
		this.m_originBone = new UI3DScene.OriginBone(this);
		this.m_originGizmo = new UI3DScene.OriginGizmo(this);
		this.m_gizmoScale = 1.0F;
		this.m_selectedAttachment = null;
		this.m_axes = new ArrayList();
		this.m_highlightBone = new UI3DScene.OriginBone(this);
		this.m_aabb = new ArrayList();
		this.m_box3D = new ArrayList();
		this.tempVector3f = new Vector3f();
		this.tempVector4f = new Vector4f();
		this.m_viewport = new int[]{0, 0, 0, 0};
		this.GRID_DARK = 0.1F;
		this.GRID_LIGHT = 0.2F;
		this.GRID_ALPHA = 1.0F;
		this.HALF_GRID = 5;
		for (int int1 = 0; int1 < this.m_stateData.length; ++int1) {
			this.m_stateData[int1] = new UI3DScene.StateData();
			this.m_stateData[int1].m_gridPlaneDrawer = new UI3DScene.GridPlaneDrawer(this);
			this.m_stateData[int1].m_overlaysDrawer = new UI3DScene.OverlaysDrawer();
		}
	}

	UI3DScene.SceneObject getSceneObjectById(String string, boolean boolean1) {
		for (int int1 = 0; int1 < this.m_objects.size(); ++int1) {
			UI3DScene.SceneObject sceneObject = (UI3DScene.SceneObject)this.m_objects.get(int1);
			if (sceneObject.m_id.equalsIgnoreCase(string)) {
				return sceneObject;
			}
		}

		if (boolean1) {
			throw new NullPointerException("scene object \"" + string + "\" not found");
		} else {
			return null;
		}
	}

	Object getSceneObjectById(String string, Class javaClass, boolean boolean1) {
		for (int int1 = 0; int1 < this.m_objects.size(); ++int1) {
			UI3DScene.SceneObject sceneObject = (UI3DScene.SceneObject)this.m_objects.get(int1);
			if (sceneObject.m_id.equalsIgnoreCase(string)) {
				if (sceneObject.getClass() == javaClass) {
					return javaClass.cast(sceneObject);
				}

				if (boolean1) {
					throw new ClassCastException("scene object \"" + string + "\" is " + sceneObject.getClass().getSimpleName() + " expected " + javaClass.getSimpleName());
				}
			}
		}

		if (boolean1) {
			throw new NullPointerException("scene object \"" + string + "\" not found");
		} else {
			return null;
		}
	}

	public void render() {
		if (this.isVisible()) {
			super.render();
			IndieGL.glClear(256);
			UI3DScene.StateData stateData = this.stateDataMain();
			this.calcMatrices(this.m_projection, this.m_modelView);
			stateData.m_projection.set((Matrix4fc)this.m_projection);
			long long1 = System.currentTimeMillis();
			float float1;
			if (this.m_viewChangeTime + this.VIEW_CHANGE_TIME > long1) {
				float1 = (float)(this.m_viewChangeTime + this.VIEW_CHANGE_TIME - long1) / (float)this.VIEW_CHANGE_TIME;
				Quaternionf quaternionf = allocQuaternionf().setFromUnnormalized((Matrix4fc)this.m_modelView);
				stateData.m_modelView.set((Quaternionfc)this.m_modelViewChange.slerp(quaternionf, 1.0F - float1));
				releaseQuaternionf(quaternionf);
			} else {
				stateData.m_modelView.set((Matrix4fc)this.m_modelView);
			}

			stateData.m_zoom = this.m_zoom;
			if (this.m_bDrawGridPlane) {
				SpriteRenderer.instance.drawGeneric(stateData.m_gridPlaneDrawer);
			}

			PZArrayUtil.forEach((List)stateData.m_objectData, UI3DScene.SceneObjectRenderData::release);
			stateData.m_objectData.clear();
			for (int int1 = 0; int1 < this.m_objects.size(); ++int1) {
				UI3DScene.SceneObject sceneObject = (UI3DScene.SceneObject)this.m_objects.get(int1);
				if (sceneObject.m_visible) {
					if (sceneObject.m_autoRotate) {
						sceneObject.m_autoRotateAngle = (float)((double)sceneObject.m_autoRotateAngle + UIManager.getMillisSinceLastRender() / 30.0);
						if (sceneObject.m_autoRotateAngle > 360.0F) {
							sceneObject.m_autoRotateAngle = 0.0F;
						}
					}

					UI3DScene.SceneObjectRenderData sceneObjectRenderData = sceneObject.renderMain();
					if (sceneObjectRenderData != null) {
						stateData.m_objectData.add(sceneObjectRenderData);
					}
				}
			}

			float1 = (float)(Mouse.getXA() - this.getAbsoluteX().intValue());
			float float2 = (float)(Mouse.getYA() - this.getAbsoluteY().intValue());
			stateData.m_gizmo = this.m_gizmo;
			if (this.m_gizmo != null) {
				stateData.m_gizmoTranslate.set((Vector3fc)this.m_gizmoPos);
				stateData.m_gizmoRotate.set((Vector3fc)this.m_gizmoRotate);
				stateData.m_gizmoTransform.translation(this.m_gizmoPos);
				stateData.m_gizmoTransform.rotateXYZ(this.m_gizmoRotate.x * 0.017453292F, this.m_gizmoRotate.y * 0.017453292F, this.m_gizmoRotate.z * 0.017453292F);
				stateData.m_gizmoAxis = this.m_gizmo.hitTest(float1, float2);
			}

			stateData.m_gizmoChildTransform.identity();
			stateData.m_selectedAttachmentIsChildAttachment = this.m_gizmoChild != null && this.m_gizmoChild.m_attachment != null && this.m_gizmoChild.m_attachment.equals(this.m_selectedAttachment);
			if (this.m_gizmoChild != null) {
				this.m_gizmoChild.getLocalTransform(stateData.m_gizmoChildTransform);
			}

			stateData.m_gizmoOriginTransform.identity();
			stateData.m_hasGizmoOrigin = this.m_gizmoOrigin != null;
			if (this.m_gizmoOrigin != null && this.m_gizmoOrigin != this.m_gizmoParent) {
				this.m_gizmoOrigin.getGlobalTransform(stateData.m_gizmoOriginTransform);
			}

			stateData.m_gizmoParentTransform.identity();
			if (this.m_gizmoParent != null) {
				this.m_gizmoParent.getGlobalTransform(stateData.m_gizmoParentTransform);
			}

			stateData.m_overlaysDrawer.init();
			SpriteRenderer.instance.drawGeneric(stateData.m_overlaysDrawer);
			Vector3f vector3f;
			Vector3f vector3f2;
			if (this.m_bDrawGrid) {
				vector3f2 = this.uiToScene(float1, float2, 0.0F, this.tempVector3f);
				if (this.m_view == UI3DScene.View.UserDefined) {
					vector3f = allocVector3f();
					switch (this.m_gridPlane) {
					case XY: 
						vector3f.set(0.0F, 0.0F, 1.0F);
						break;
					
					case XZ: 
						vector3f.set(0.0F, 1.0F, 0.0F);
						break;
					
					case YZ: 
						vector3f.set(1.0F, 0.0F, 0.0F);
					
					}

					Vector3f vector3f3 = allocVector3f().set(0.0F);
					UI3DScene.Plane plane = allocPlane().set(vector3f, vector3f3);
					releaseVector3f(vector3f);
					releaseVector3f(vector3f3);
					UI3DScene.Ray ray = this.getCameraRay(float1, (float)this.screenHeight() - float2, allocRay());
					if (intersect_ray_plane(plane, ray, vector3f2) != 1) {
						vector3f2.set(0.0F);
					}

					releasePlane(plane);
					releaseRay(ray);
				}

				vector3f2.x = (float)Math.round(vector3f2.x * this.gridMult()) / this.gridMult();
				vector3f2.y = (float)Math.round(vector3f2.y * this.gridMult()) / this.gridMult();
				vector3f2.z = (float)Math.round(vector3f2.z * this.gridMult()) / this.gridMult();
				this.DrawText(UIFont.Small, String.valueOf(vector3f2.x), (double)(this.width - 200.0F), 10.0, 1.0, 0.0, 0.0, 1.0);
				this.DrawText(UIFont.Small, String.valueOf(vector3f2.y), (double)(this.width - 150.0F), 10.0, 0.0, 1.0, 0.0, 1.0);
				this.DrawText(UIFont.Small, String.valueOf(vector3f2.z), (double)(this.width - 100.0F), 10.0, 0.0, 0.5, 1.0, 1.0);
			}

			float float3;
			if (this.m_gizmo == this.m_rotateGizmo && this.m_rotateGizmo.m_trackAxis != UI3DScene.Axis.None) {
				vector3f2 = this.m_rotateGizmo.m_startXfrm.getTranslation(allocVector3f());
				float float4 = this.sceneToUIX(vector3f2.x, vector3f2.y, vector3f2.z);
				float3 = this.sceneToUIY(vector3f2.x, vector3f2.y, vector3f2.z);
				LineDrawer.drawLine(float4, float3, float1, float2, 0.5F, 0.5F, 0.5F, 1.0F, 1);
				releaseVector3f(vector3f2);
			}

			if (this.m_highlightBone.m_boneName != null) {
				Matrix4f matrix4f = this.m_highlightBone.getGlobalTransform(allocMatrix4f());
				this.m_highlightBone.m_character.getGlobalTransform(allocMatrix4f()).mul((Matrix4fc)matrix4f, matrix4f);
				vector3f = matrix4f.getTranslation(allocVector3f());
				float3 = this.sceneToUIX(vector3f.x, vector3f.y, vector3f.z);
				float float5 = this.sceneToUIY(vector3f.x, vector3f.y, vector3f.z);
				LineDrawer.drawCircle(float3, float5, 10.0F, 16, 1.0F, 1.0F, 1.0F);
				releaseVector3f(vector3f);
				releaseMatrix4f(matrix4f);
			}
		}
	}

	private float gridMult() {
		return (float)(100 * this.m_gridDivisions);
	}

	private float zoomMult() {
		return (float)Math.exp((double)((float)this.m_zoom * 0.2F)) * 160.0F / Math.max(1.82F, 1.0F);
	}

	private static Matrix4f allocMatrix4f() {
		return (Matrix4f)((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).alloc();
	}

	private static void releaseMatrix4f(Matrix4f matrix4f) {
		((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).release(matrix4f);
	}

	private static Quaternionf allocQuaternionf() {
		return (Quaternionf)((BaseVehicle.QuaternionfObjectPool)BaseVehicle.TL_quaternionf_pool.get()).alloc();
	}

	private static void releaseQuaternionf(Quaternionf quaternionf) {
		((BaseVehicle.QuaternionfObjectPool)BaseVehicle.TL_quaternionf_pool.get()).release(quaternionf);
	}

	private static UI3DScene.Ray allocRay() {
		return (UI3DScene.Ray)((ObjectPool)TL_Ray_pool.get()).alloc();
	}

	private static void releaseRay(UI3DScene.Ray ray) {
		((ObjectPool)TL_Ray_pool.get()).release((Object)ray);
	}

	private static UI3DScene.Plane allocPlane() {
		return (UI3DScene.Plane)((ObjectPool)TL_Plane_pool.get()).alloc();
	}

	private static void releasePlane(UI3DScene.Plane plane) {
		((ObjectPool)TL_Plane_pool.get()).release((Object)plane);
	}

	private static Vector2 allocVector2() {
		return (Vector2)((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).alloc();
	}

	private static void releaseVector2(Vector2 vector2) {
		((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).release(vector2);
	}

	private static Vector3f allocVector3f() {
		return (Vector3f)((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).alloc();
	}

	private static void releaseVector3f(Vector3f vector3f) {
		((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(vector3f);
	}

	public Object fromLua0(String string) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -2076678364: 
			if (string.equals("clearBox3Ds")) {
				byte1 = 2;
			}

			break;
		
		case -1899201780: 
			if (string.equals("getGridMult")) {
				byte1 = 6;
			}

			break;
		
		case -1271454958: 
			if (string.equals("clearAxes")) {
				byte1 = 1;
			}

			break;
		
		case -800815632: 
			if (string.equals("getGizmoPos")) {
				byte1 = 5;
			}

			break;
		
		case -762071578: 
			if (string.equals("clearAABBs")) {
				byte1 = 0;
			}

			break;
		
		case -262619928: 
			if (string.equals("clearGizmoRotate")) {
				byte1 = 3;
			}

			break;
		
		case -75062501: 
			if (string.equals("getView")) {
				byte1 = 7;
			}

			break;
		
		case 424225647: 
			if (string.equals("stopGizmoTracking")) {
				byte1 = 10;
			}

			break;
		
		case 921608267: 
			if (string.equals("clearHighlightBone")) {
				byte1 = 4;
			}

			break;
		
		case 1289669561: 
			if (string.equals("getViewRotation")) {
				byte1 = 8;
			}

			break;
		
		case 1781313372: 
			if (string.equals("getModelCount")) {
				byte1 = 9;
			}

		
		}
		switch (byte1) {
		case 0: 
			s_aabbPool.release((List)this.m_aabb);
			this.m_aabb.clear();
			return null;
		
		case 1: 
			s_posRotPool.release((List)this.m_axes);
			this.m_axes.clear();
			return null;
		
		case 2: 
			s_box3DPool.release((List)this.m_box3D);
			this.m_box3D.clear();
			return null;
		
		case 3: 
			this.m_gizmoRotate.set(0.0F);
			return null;
		
		case 4: 
			this.m_highlightBone.m_boneName = null;
			return null;
		
		case 5: 
			return this.m_gizmoPos;
		
		case 6: 
			return BoxedStaticValues.toDouble((double)this.gridMult());
		
		case 7: 
			return this.m_view.name();
		
		case 8: 
			return this.m_viewRotation;
		
		case 9: 
			int int1 = 0;
			for (int int2 = 0; int2 < this.m_objects.size(); ++int2) {
				if (this.m_objects.get(int2) instanceof UI3DScene.SceneModel) {
					++int1;
				}
			}

			return BoxedStaticValues.toDouble((double)int1);
		
		case 10: 
			if (this.m_gizmo != null) {
				this.m_gizmo.stopTracking();
			}

			return null;
		
		default: 
			throw new IllegalArgumentException("unhandled \"" + string + "\"");
		
		}
	}

	public Object fromLua1(String string, Object object) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -1987781608: 
			if (string.equals("setGridMult")) {
				byte1 = 22;
			}

			break;
		
		case -1759645365: 
			if (string.equals("isCharacterFemale")) {
				byte1 = 11;
			}

			break;
		
		case -1706380656: 
			if (string.equals("createVehicle")) {
				byte1 = 1;
			}

			break;
		
		case -1539752816: 
			if (string.equals("setDrawGridPlane")) {
				byte1 = 16;
			}

			break;
		
		case -1494527684: 
			if (string.equals("getObjectTranslation")) {
				byte1 = 9;
			}

			break;
		
		case -1489195916: 
			if (string.equals("setGridPlane")) {
				byte1 = 23;
			}

			break;
		
		case -1030736685: 
			if (string.equals("getObjectRotation")) {
				byte1 = 8;
			}

			break;
		
		case -889395460: 
			if (string.equals("setGizmoPos")) {
				byte1 = 18;
			}

			break;
		
		case -851056855: 
			if (string.equals("isObjectVisible")) {
				byte1 = 12;
			}

			break;
		
		case -682454758: 
			if (string.equals("setGizmoVisible")) {
				byte1 = 21;
			}

			break;
		
		case -477006699: 
			if (string.equals("setMaxZoom")) {
				byte1 = 24;
			}

			break;
		
		case -371350035: 
			if (string.equals("setTransformMode")) {
				byte1 = 26;
			}

			break;
		
		case -352953986: 
			if (string.equals("setGizmoOrigin")) {
				byte1 = 17;
			}

			break;
		
		case -310328059: 
			if (string.equals("removeModel")) {
				byte1 = 13;
			}

			break;
		
		case -269514829: 
			if (string.equals("setGizmoRotate")) {
				byte1 = 19;
			}

			break;
		
		case -166970338: 
			if (string.equals("getModelScript")) {
				byte1 = 4;
			}

			break;
		
		case -8145934: 
			if (string.equals("setGizmoScale")) {
				byte1 = 20;
			}

			break;
		
		case 3744723: 
			if (string.equals("zoom")) {
				byte1 = 29;
			}

			break;
		
		case 24704865: 
			if (string.equals("getVehicleScript")) {
				byte1 = 10;
			}

			break;
		
		case 99063181: 
			if (string.equals("createCharacter")) {
				byte1 = 0;
			}

			break;
		
		case 143181344: 
			if (string.equals("setSelectedAttachment")) {
				byte1 = 25;
			}

			break;
		
		case 241532735: 
			if (string.equals("getObjectParent")) {
				byte1 = 6;
			}

			break;
		
		case 786690498: 
			if (string.equals("getObjectParentAttachment")) {
				byte1 = 7;
			}

			break;
		
		case 1162053932: 
			if (string.equals("setDrawGrid")) {
				byte1 = 14;
			}

			break;
		
		case 1257868287: 
			if (string.equals("getObjectAutoRotate")) {
				byte1 = 5;
			}

			break;
		
		case 1432957189: 
			if (string.equals("getCharacterAnimationDuration")) {
				byte1 = 2;
			}

			break;
		
		case 1985047079: 
			if (string.equals("setView")) {
				byte1 = 28;
			}

			break;
		
		case 1985172309: 
			if (string.equals("setZoom")) {
				byte1 = 27;
			}

			break;
		
		case 2028105329: 
			if (string.equals("setDrawGridAxes")) {
				byte1 = 15;
			}

			break;
		
		case 2079364798: 
			if (string.equals("getCharacterAnimationTime")) {
				byte1 = 3;
			}

		
		}
		int int1;
		String string2;
		Vector3f vector3f;
		UI3DScene.SceneObject sceneObject;
		byte byte2;
		AnimationPlayer animationPlayer;
		UI3DScene.SceneCharacter sceneCharacter;
		AnimationMultiTrack animationMultiTrack;
		switch (byte1) {
		case 0: 
			sceneObject = this.getSceneObjectById((String)object, false);
			if (sceneObject != null) {
				throw new IllegalStateException("scene object \"" + object + "\" exists");
			}

			UI3DScene.SceneCharacter sceneCharacter2 = new UI3DScene.SceneCharacter(this, (String)object);
			this.m_objects.add(sceneCharacter2);
			return sceneCharacter2;
		
		case 1: 
			sceneObject = this.getSceneObjectById((String)object, false);
			if (sceneObject != null) {
				throw new IllegalStateException("scene object \"" + object + "\" exists");
			}

			UI3DScene.SceneVehicle sceneVehicle = new UI3DScene.SceneVehicle(this, (String)object);
			this.m_objects.add(sceneVehicle);
			return null;
		
		case 2: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			animationPlayer = sceneCharacter.m_animatedModel.getAnimationPlayer();
			if (animationPlayer == null) {
				return null;
			} else {
				animationMultiTrack = animationPlayer.getMultiTrack();
				if (animationMultiTrack != null && !animationMultiTrack.getTracks().isEmpty()) {
					return KahluaUtil.toDouble((double)((AnimationTrack)animationMultiTrack.getTracks().get(0)).getDuration());
				}

				return null;
			}

		
		case 3: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			animationPlayer = sceneCharacter.m_animatedModel.getAnimationPlayer();
			if (animationPlayer == null) {
				return null;
			} else {
				animationMultiTrack = animationPlayer.getMultiTrack();
				if (animationMultiTrack != null && !animationMultiTrack.getTracks().isEmpty()) {
					return KahluaUtil.toDouble((double)((AnimationTrack)animationMultiTrack.getTracks().get(0)).getCurrentTimeValue());
				}

				return null;
			}

		
		case 4: 
			int1 = 0;
			for (int int2 = 0; int2 < this.m_objects.size(); ++int2) {
				UI3DScene.SceneModel sceneModel = (UI3DScene.SceneModel)Type.tryCastTo((UI3DScene.SceneObject)this.m_objects.get(int2), UI3DScene.SceneModel.class);
				if (sceneModel != null && int1++ == ((Double)object).intValue()) {
					return sceneModel.m_modelScript;
				}
			}

			return null;
		
		case 5: 
			sceneObject = this.getSceneObjectById((String)object, true);
			return sceneObject.m_autoRotate ? Boolean.TRUE : Boolean.FALSE;
		
		case 6: 
			sceneObject = this.getSceneObjectById((String)object, true);
			return sceneObject.m_parent == null ? null : sceneObject.m_parent.m_id;
		
		case 7: 
			sceneObject = this.getSceneObjectById((String)object, true);
			return sceneObject.m_parentAttachment;
		
		case 8: 
			sceneObject = this.getSceneObjectById((String)object, true);
			return sceneObject.m_rotate;
		
		case 9: 
			sceneObject = this.getSceneObjectById((String)object, true);
			return sceneObject.m_translate;
		
		case 10: 
			UI3DScene.SceneVehicle sceneVehicle2 = (UI3DScene.SceneVehicle)this.getSceneObjectById((String)object, UI3DScene.SceneVehicle.class, true);
			return sceneVehicle2.m_script;
		
		case 11: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			return sceneCharacter.m_animatedModel.isFemale();
		
		case 12: 
			sceneObject = this.getSceneObjectById((String)object, true);
			return sceneObject.m_visible ? Boolean.TRUE : Boolean.FALSE;
		
		case 13: 
			UI3DScene.SceneModel sceneModel2 = (UI3DScene.SceneModel)this.getSceneObjectById((String)object, UI3DScene.SceneModel.class, true);
			this.m_objects.remove(sceneModel2);
			Iterator iterator = this.m_objects.iterator();
			while (iterator.hasNext()) {
				UI3DScene.SceneObject sceneObject2 = (UI3DScene.SceneObject)iterator.next();
				if (sceneObject2.m_parent == sceneModel2) {
					sceneObject2.m_attachment = null;
					sceneObject2.m_parent = null;
					sceneObject2.m_parentAttachment = null;
				}
			}

			return null;
		
		case 14: 
			this.m_bDrawGrid = (Boolean)object;
			return null;
		
		case 15: 
			this.m_bDrawGridAxes = (Boolean)object;
			return null;
		
		case 16: 
			this.m_bDrawGridPlane = (Boolean)object;
			return null;
		
		case 17: 
			string2 = (String)object;
			byte2 = -1;
			switch (string2.hashCode()) {
			case 3387192: 
				if (string2.equals("none")) {
					byte2 = 0;
				}

			
			default: 
				switch (byte2) {
				case 0: 
					this.m_gizmoParent = null;
					this.m_gizmoOrigin = null;
					this.m_gizmoChild = null;
				
				default: 
					return null;
				
				}

			
			}

		
		case 18: 
			vector3f = (Vector3f)object;
			if (!this.m_gizmoPos.equals(vector3f)) {
				this.m_gizmoPos.set((Vector3fc)vector3f);
			}

			return null;
		
		case 19: 
			vector3f = (Vector3f)object;
			if (!this.m_gizmoRotate.equals(vector3f)) {
				this.m_gizmoRotate.set((Vector3fc)vector3f);
			}

			return null;
		
		case 20: 
			this.m_gizmoScale = Math.max(((Double)object).floatValue(), 0.01F);
			return null;
		
		case 21: 
			string2 = (String)object;
			this.m_rotateGizmo.m_visible = "rotate".equalsIgnoreCase(string2);
			this.m_scaleGizmo.m_visible = "scale".equalsIgnoreCase(string2);
			this.m_translateGizmo.m_visible = "translate".equalsIgnoreCase(string2);
			byte2 = -1;
			switch (string2.hashCode()) {
			case -925180581: 
				if (string2.equals("rotate")) {
					byte2 = 0;
				}

				break;
			
			case 109250890: 
				if (string2.equals("scale")) {
					byte2 = 1;
				}

				break;
			
			case 1052832078: 
				if (string2.equals("translate")) {
					byte2 = 2;
				}

			
			}

			switch (byte2) {
			case 0: 
				this.m_gizmo = this.m_rotateGizmo;
				break;
			
			case 1: 
				this.m_gizmo = this.m_scaleGizmo;
				break;
			
			case 2: 
				this.m_gizmo = this.m_translateGizmo;
				break;
			
			default: 
				this.m_gizmo = null;
			
			}

			return null;
		
		case 22: 
			this.m_gridDivisions = PZMath.clamp(((Double)object).intValue(), 1, 100);
			return null;
		
		case 23: 
			this.m_gridPlane = UI3DScene.GridPlane.valueOf((String)object);
			return null;
		
		case 24: 
			this.m_zoomMax = PZMath.clamp(((Double)object).intValue(), 1, 20);
			return null;
		
		case 25: 
			this.m_selectedAttachment = (String)object;
			return null;
		
		case 26: 
			this.m_transformMode = UI3DScene.TransformMode.valueOf((String)object);
			return null;
		
		case 27: 
			this.m_zoom = PZMath.clamp(((Double)object).intValue(), 1, this.m_zoomMax);
			this.calcMatrices(this.m_projection, this.m_modelView);
			return null;
		
		case 28: 
			UI3DScene.View view = this.m_view;
			this.m_view = UI3DScene.View.valueOf((String)object);
			if (view != this.m_view) {
				long long1 = System.currentTimeMillis();
				if (this.m_viewChangeTime + this.VIEW_CHANGE_TIME < long1) {
					this.m_modelViewChange.setFromUnnormalized((Matrix4fc)this.m_modelView);
				}

				this.m_viewChangeTime = long1;
			}

			this.calcMatrices(this.m_projection, this.m_modelView);
			return null;
		
		case 29: 
			int1 = -((Double)object).intValue();
			float float1 = (float)(Mouse.getXA() - this.getAbsoluteX().intValue());
			float float2 = (float)(Mouse.getYA() - this.getAbsoluteY().intValue());
			float float3 = this.uiToSceneX(float1, float2);
			float float4 = this.uiToSceneY(float1, float2);
			this.m_zoom = PZMath.clamp(this.m_zoom + int1, 1, this.m_zoomMax);
			this.calcMatrices(this.m_projection, this.m_modelView);
			float float5 = this.uiToSceneX(float1, float2);
			float float6 = this.uiToSceneY(float1, float2);
			this.m_view_x = (int)((float)this.m_view_x - (float5 - float3) * this.zoomMult());
			this.m_view_y = (int)((float)this.m_view_y + (float6 - float4) * this.zoomMult());
			this.calcMatrices(this.m_projection, this.m_modelView);
			return null;
		
		default: 
			throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\"", string, object));
		
		}
	}

	public Object fromLua2(String string, Object object, Object object2) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -2087654637: 
			if (string.equals("setCharacterFemale")) {
				byte1 = 15;
			}

			break;
		
		case -1943463772: 
			if (string.equals("addAttachment")) {
				byte1 = 0;
			}

			break;
		
		case -1806737963: 
			if (string.equals("setVehicleScript")) {
				byte1 = 25;
			}

			break;
		
		case -1673743631: 
			if (string.equals("setObjectVisible")) {
				byte1 = 24;
			}

			break;
		
		case -1409424298: 
			if (string.equals("setHighlightBone")) {
				byte1 = 20;
			}

			break;
		
		case -1372814515: 
			if (string.equals("setCharacterAnimationClip")) {
				byte1 = 10;
			}

			break;
		
		case -1372310838: 
			if (string.equals("setCharacterAnimationTime")) {
				byte1 = 12;
			}

			break;
		
		case -928360249: 
			if (string.equals("removeAttachment")) {
				byte1 = 7;
			}

			break;
		
		case -914984949: 
			if (string.equals("setCharacterShowBones")) {
				byte1 = 16;
			}

			break;
		
		case -903033673: 
			if (string.equals("setCharacterAlpha")) {
				byte1 = 8;
			}

			break;
		
		case -886186006: 
			if (string.equals("setCharacterState")) {
				byte1 = 19;
			}

			break;
		
		case -841604615: 
			if (string.equals("dragView")) {
				byte1 = 5;
			}

			break;
		
		case -510351475: 
			if (string.equals("createModel")) {
				byte1 = 3;
			}

			break;
		
		case -352953986: 
			if (string.equals("setGizmoOrigin")) {
				byte1 = 18;
			}

			break;
		
		case -333772122: 
			if (string.equals("dragGizmo")) {
				byte1 = 4;
			}

			break;
		
		case -285859573: 
			if (string.equals("setObjectAutoRotate")) {
				byte1 = 23;
			}

			break;
		
		case -181033558: 
			if (string.equals("setCharacterAnimSet")) {
				byte1 = 13;
			}

			break;
		
		case -181019654: 
			if (string.equals("setCharacterAnimate")) {
				byte1 = 9;
			}

			break;
		
		case 407314410: 
			if (string.equals("setCharacterAnimationSpeed")) {
				byte1 = 11;
			}

			break;
		
		case 628034626: 
			if (string.equals("setModelWeaponRotationHack")) {
				byte1 = 22;
			}

			break;
		
		case 886449800: 
			if (string.equals("applyDeltaRotation")) {
				byte1 = 2;
			}

			break;
		
		case 995639366: 
			if (string.equals("addBoneAxis")) {
				byte1 = 1;
			}

			break;
		
		case 1348886365: 
			if (string.equals("setCharacterClearDepthBuffer")) {
				byte1 = 14;
			}

			break;
		
		case 1349998759: 
			if (string.equals("getCharacterAnimationKeyframeTimes")) {
				byte1 = 6;
			}

			break;
		
		case 1623389641: 
			if (string.equals("testGizmoAxis")) {
				byte1 = 26;
			}

			break;
		
		case 1728054510: 
			if (string.equals("setCharacterUseDeferredMovement")) {
				byte1 = 17;
			}

			break;
		
		case 2083897877: 
			if (string.equals("setModelUseWorldAttachment")) {
				byte1 = 21;
			}

		
		}
		int int1;
		int int2;
		UI3DScene.SceneObject sceneObject;
		String string2;
		UI3DScene.SceneModel sceneModel;
		UI3DScene.SceneCharacter sceneCharacter;
		AnimationPlayer animationPlayer;
		ModelAttachment modelAttachment;
		UI3DScene.SceneModel sceneModel2;
		AnimationMultiTrack animationMultiTrack;
		switch (byte1) {
		case 0: 
			sceneModel = (UI3DScene.SceneModel)this.getSceneObjectById((String)object, UI3DScene.SceneModel.class, true);
			if (sceneModel.m_modelScript.getAttachmentById((String)object2) != null) {
				throw new IllegalArgumentException("model script \"" + object + "\" already has attachment named \"" + object2 + "\"");
			}

			modelAttachment = new ModelAttachment((String)object2);
			sceneModel.m_modelScript.addAttachment(modelAttachment);
			return modelAttachment;
		
		case 1: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			string2 = (String)object2;
			UI3DScene.PositionRotation positionRotation = sceneCharacter.getBoneAxis(string2, (UI3DScene.PositionRotation)s_posRotPool.alloc());
			this.m_axes.add(positionRotation);
			return null;
		
		case 2: 
			Vector3f vector3f = (Vector3f)object;
			Vector3f vector3f2 = (Vector3f)object2;
			Quaternionf quaternionf = allocQuaternionf().rotationXYZ(vector3f.x * 0.017453292F, vector3f.y * 0.017453292F, vector3f.z * 0.017453292F);
			Quaternionf quaternionf2 = allocQuaternionf().rotationXYZ(vector3f2.x * 0.017453292F, vector3f2.y * 0.017453292F, vector3f2.z * 0.017453292F);
			quaternionf.mul(quaternionf2);
			quaternionf.getEulerAnglesXYZ(vector3f);
			releaseQuaternionf(quaternionf);
			releaseQuaternionf(quaternionf2);
			vector3f.mul(57.295776F);
			vector3f.x = (float)Math.floor((double)(vector3f.x + 0.5F));
			vector3f.y = (float)Math.floor((double)(vector3f.y + 0.5F));
			vector3f.z = (float)Math.floor((double)(vector3f.z + 0.5F));
			return vector3f;
		
		case 3: 
			sceneObject = this.getSceneObjectById((String)object, false);
			if (sceneObject != null) {
				throw new IllegalStateException("scene object \"" + object + "\" exists");
			} else {
				ModelScript modelScript = ScriptManager.instance.getModelScript((String)object2);
				if (modelScript == null) {
					throw new NullPointerException("model script \"" + object2 + "\" not found");
				} else {
					Model model = ModelManager.instance.getLoadedModel((String)object2);
					if (model == null) {
						throw new NullPointerException("model \"" + object2 + "\" not found");
					}

					sceneModel2 = new UI3DScene.SceneModel(this, (String)object, modelScript, model);
					this.m_objects.add(sceneModel2);
					return null;
				}
			}

		
		case 4: 
			float float1 = ((Double)object).floatValue();
			float float2 = ((Double)object2).floatValue();
			if (this.m_gizmo == null) {
				throw new NullPointerException("gizmo is null");
			}

			this.m_gizmo.updateTracking(float1, float2);
			return null;
		
		case 5: 
			int1 = ((Double)object).intValue();
			int2 = ((Double)object2).intValue();
			this.m_view_x -= int1;
			this.m_view_y -= int2;
			this.calcMatrices(this.m_projection, this.m_modelView);
			return null;
		
		case 6: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			animationPlayer = sceneCharacter.m_animatedModel.getAnimationPlayer();
			if (animationPlayer == null) {
				return null;
			} else {
				animationMultiTrack = animationPlayer.getMultiTrack();
				if (animationMultiTrack != null && !animationMultiTrack.getTracks().isEmpty()) {
					AnimationTrack animationTrack = (AnimationTrack)animationMultiTrack.getTracks().get(0);
					AnimationClip animationClip = animationTrack.getClip();
					if (animationClip == null) {
						return null;
					}

					if (object2 == null) {
						object2 = new ArrayList();
					}

					ArrayList arrayList = (ArrayList)object2;
					arrayList.clear();
					Keyframe[] keyframeArray = animationClip.getKeyframes();
					for (int int3 = 0; int3 < keyframeArray.length; ++int3) {
						Keyframe keyframe = keyframeArray[int3];
						Double Double1 = KahluaUtil.toDouble((double)keyframe.Time);
						if (!arrayList.contains(Double1)) {
							arrayList.add(Double1);
						}
					}

					return arrayList;
				}

				return null;
			}

		
		case 7: 
			sceneModel = (UI3DScene.SceneModel)this.getSceneObjectById((String)object, UI3DScene.SceneModel.class, true);
			modelAttachment = sceneModel.m_modelScript.getAttachmentById((String)object2);
			if (modelAttachment == null) {
				throw new IllegalArgumentException("model script \"" + object + "\" attachment \"" + object2 + "\" not found");
			}

			sceneModel.m_modelScript.removeAttachment(modelAttachment);
			return null;
		
		case 8: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			sceneCharacter.m_animatedModel.setAlpha(((Double)object2).floatValue());
			return null;
		
		case 9: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			sceneCharacter.m_animatedModel.setAnimate((Boolean)object2);
			return null;
		
		case 10: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			AnimationSet animationSet = AnimationSet.GetAnimationSet(sceneCharacter.m_animatedModel.GetAnimSetName(), false);
			if (animationSet == null) {
				return null;
			} else {
				AnimState animState = animationSet.GetState(sceneCharacter.m_animatedModel.getState());
				if (animState != null && !animState.m_Nodes.isEmpty()) {
					AnimNode animNode = (AnimNode)animState.m_Nodes.get(0);
					animNode.m_AnimName = (String)object2;
					sceneCharacter.m_animatedModel.getAdvancedAnimator().OnAnimDataChanged(false);
					sceneCharacter.m_animatedModel.getAdvancedAnimator().SetState(animState.m_Name);
					return null;
				}

				return null;
			}

		
		case 11: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			AnimationMultiTrack animationMultiTrack2 = sceneCharacter.m_animatedModel.getAnimationPlayer().getMultiTrack();
			if (animationMultiTrack2.getTracks().isEmpty()) {
				return null;
			}

			((AnimationTrack)animationMultiTrack2.getTracks().get(0)).SpeedDelta = PZMath.clamp(((Double)object2).floatValue(), 0.0F, 10.0F);
			return null;
		
		case 12: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			sceneCharacter.m_animatedModel.setTrackTime(((Double)object2).floatValue());
			animationPlayer = sceneCharacter.m_animatedModel.getAnimationPlayer();
			if (animationPlayer == null) {
				return null;
			} else {
				animationMultiTrack = animationPlayer.getMultiTrack();
				if (animationMultiTrack != null && !animationMultiTrack.getTracks().isEmpty()) {
					((AnimationTrack)animationMultiTrack.getTracks().get(0)).setCurrentTimeValue(((Double)object2).floatValue());
					return null;
				}

				return null;
			}

		
		case 13: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			string2 = (String)object2;
			if (!string2.equals(sceneCharacter.m_animatedModel.GetAnimSetName())) {
				sceneCharacter.m_animatedModel.setAnimSetName(string2);
				sceneCharacter.m_animatedModel.getAdvancedAnimator().OnAnimDataChanged(false);
				ActionGroup actionGroup = ActionGroup.getActionGroup(sceneCharacter.m_animatedModel.GetAnimSetName());
				ActionContext actionContext = sceneCharacter.m_animatedModel.getActionContext();
				if (actionGroup != actionContext.getGroup()) {
					actionContext.setGroup(actionGroup);
				}

				sceneCharacter.m_animatedModel.getAdvancedAnimator().SetState(actionContext.getCurrentStateName(), PZArrayUtil.listConvert(actionContext.getChildStates(), (var0)->{
					return var0.name;
				}));
			}

			return null;
		
		case 14: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			sceneCharacter.m_bClearDepthBuffer = (Boolean)object2;
			return null;
		
		case 15: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			boolean boolean1 = (Boolean)object2;
			if (boolean1 != sceneCharacter.m_animatedModel.isFemale()) {
				sceneCharacter.m_animatedModel.setOutfitName("Naked", boolean1, false);
			}

			return null;
		
		case 16: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			sceneCharacter.m_bShowBones = (Boolean)object2;
			return null;
		
		case 17: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			sceneCharacter.m_bUseDeferredMovement = (Boolean)object2;
			return null;
		
		case 18: 
			String string3 = (String)object;
			byte byte2 = -1;
			switch (string3.hashCode()) {
			case -2046552227: 
				if (string3.equals("vehicleModel")) {
					byte2 = 4;
				}

				break;
			
			case -177460704: 
				if (string3.equals("centerOfMass")) {
					byte2 = 0;
				}

				break;
			
			case 104069929: 
				if (string3.equals("model")) {
					byte2 = 3;
				}

				break;
			
			case 739104294: 
				if (string3.equals("chassis")) {
					byte2 = 1;
				}

				break;
			
			case 1564195625: 
				if (string3.equals("character")) {
					byte2 = 2;
				}

			
			}

			UI3DScene.SceneVehicle sceneVehicle;
			switch (byte2) {
			case 0: 
				this.m_gizmoParent = (UI3DScene.SceneObject)this.getSceneObjectById((String)object2, UI3DScene.SceneVehicle.class, true);
				this.m_gizmoOrigin = this.m_gizmoParent;
				this.m_gizmoChild = null;
				break;
			
			case 1: 
				sceneVehicle = (UI3DScene.SceneVehicle)this.getSceneObjectById((String)object2, UI3DScene.SceneVehicle.class, true);
				this.m_gizmoParent = sceneVehicle;
				this.m_originGizmo.m_translate.set((Vector3fc)sceneVehicle.m_script.getCenterOfMassOffset());
				this.m_originGizmo.m_rotate.zero();
				this.m_gizmoOrigin = this.m_originGizmo;
				this.m_gizmoChild = null;
				break;
			
			case 2: 
				UI3DScene.SceneCharacter sceneCharacter2 = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object2, UI3DScene.SceneCharacter.class, true);
				this.m_gizmoParent = sceneCharacter2;
				this.m_gizmoOrigin = this.m_gizmoParent;
				this.m_gizmoChild = null;
				break;
			
			case 3: 
				sceneModel2 = (UI3DScene.SceneModel)this.getSceneObjectById((String)object2, UI3DScene.SceneModel.class, true);
				this.m_gizmoParent = sceneModel2;
				this.m_gizmoOrigin = this.m_gizmoParent;
				this.m_gizmoChild = null;
				break;
			
			case 4: 
				sceneVehicle = (UI3DScene.SceneVehicle)this.getSceneObjectById((String)object2, UI3DScene.SceneVehicle.class, true);
				this.m_gizmoParent = sceneVehicle;
				this.m_originGizmo.m_translate.set((Vector3fc)sceneVehicle.m_script.getModel().getOffset());
				this.m_originGizmo.m_rotate.zero();
				this.m_gizmoOrigin = this.m_originGizmo;
				this.m_gizmoChild = null;
			
			}

			return null;
		
		case 19: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			sceneCharacter.m_animatedModel.setState((String)object2);
			return null;
		
		case 20: 
			sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			string2 = (String)object2;
			this.m_highlightBone.m_character = sceneCharacter;
			this.m_highlightBone.m_boneName = string2;
			return null;
		
		case 21: 
			sceneModel = (UI3DScene.SceneModel)this.getSceneObjectById((String)object, UI3DScene.SceneModel.class, true);
			sceneModel.m_useWorldAttachment = (Boolean)object2;
			return null;
		
		case 22: 
			sceneModel = (UI3DScene.SceneModel)this.getSceneObjectById((String)object, UI3DScene.SceneModel.class, true);
			sceneModel.m_weaponRotationHack = (Boolean)object2;
			return null;
		
		case 23: 
			sceneObject = this.getSceneObjectById((String)object, true);
			sceneObject.m_autoRotate = (Boolean)object2;
			if (!sceneObject.m_autoRotate) {
				sceneObject.m_autoRotateAngle = 0.0F;
			}

			return null;
		
		case 24: 
			sceneObject = this.getSceneObjectById((String)object, true);
			sceneObject.m_visible = (Boolean)object2;
			return null;
		
		case 25: 
			UI3DScene.SceneVehicle sceneVehicle2 = (UI3DScene.SceneVehicle)this.getSceneObjectById((String)object, UI3DScene.SceneVehicle.class, true);
			sceneVehicle2.setScriptName((String)object2);
			return null;
		
		case 26: 
			int1 = ((Double)object).intValue();
			int2 = ((Double)object2).intValue();
			if (this.m_gizmo == null) {
				return "None";
			}

			return this.m_gizmo.hitTest((float)int1, (float)int2).toString();
		
		default: 
			throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \"%s\"", string, object, object2));
		
		}
	}

	public Object fromLua3(String string, Object object, Object object2, Object object3) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -2094545211: 
			if (string.equals("setViewRotation")) {
				byte1 = 5;
			}

			break;
		
		case -1900967153: 
			if (string.equals("startGizmoTracking")) {
				byte1 = 4;
			}

			break;
		
		case -1149133854: 
			if (string.equals("addAxis")) {
				byte1 = 0;
			}

			break;
		
		case -1147577620: 
			if (string.equals("pickCharacterBone")) {
				byte1 = 1;
			}

			break;
		
		case -889388479: 
			if (string.equals("setGizmoXYZ")) {
				byte1 = 3;
			}

			break;
		
		case -352953986: 
			if (string.equals("setGizmoOrigin")) {
				byte1 = 2;
			}

		
		}
		float float1;
		float float2;
		float float3;
		switch (byte1) {
		case 0: 
			float1 = ((Double)object).floatValue();
			float2 = ((Double)object2).floatValue();
			float3 = ((Double)object3).floatValue();
			this.m_axes.add(((UI3DScene.PositionRotation)s_posRotPool.alloc()).set(float1, float2, float3));
			return null;
		
		case 1: 
			UI3DScene.SceneCharacter sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			float2 = ((Double)object2).floatValue();
			float3 = ((Double)object3).floatValue();
			return sceneCharacter.pickBone(float2, float3);
		
		case 2: 
			String string2 = (String)object;
			byte byte2 = -1;
			switch (string2.hashCode()) {
			case 3029700: 
				if (string2.equals("bone")) {
					byte2 = 0;
				}

			
			default: 
				switch (byte2) {
				case 0: 
					UI3DScene.SceneCharacter sceneCharacter2 = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object2, UI3DScene.SceneCharacter.class, true);
					this.m_gizmoParent = sceneCharacter2;
					this.m_originBone.m_character = sceneCharacter2;
					this.m_originBone.m_boneName = (String)object3;
					this.m_gizmoOrigin = this.m_originBone;
					this.m_gizmoChild = null;
				
				default: 
					return null;
				
				}

			
			}

		
		case 3: 
			float1 = ((Double)object).floatValue();
			float2 = ((Double)object2).floatValue();
			float3 = ((Double)object3).floatValue();
			this.m_gizmoPos.set(float1, float2, float3);
			return null;
		
		case 4: 
			float1 = ((Double)object).floatValue();
			float2 = ((Double)object2).floatValue();
			UI3DScene.Axis axis = UI3DScene.Axis.valueOf((String)object3);
			if (this.m_gizmo != null) {
				this.m_gizmo.startTracking(float1, float2, axis);
			}

			return null;
		
		case 5: 
			float1 = ((Double)object).floatValue();
			float2 = ((Double)object2).floatValue();
			float3 = ((Double)object3).floatValue();
			float1 %= 360.0F;
			float2 %= 360.0F;
			float3 %= 360.0F;
			this.m_viewRotation.set(float1, float2, float3);
			return null;
		
		default: 
			throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \"%s\" \"%s\"", string, object, object2, object3));
		
		}
	}

	public Object fromLua4(String string, Object object, Object object2, Object object3, Object object4) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -1384272991: 
			if (string.equals("setPassengerPosition")) {
				byte1 = 3;
			}

			break;
		
		case -1182783862: 
			if (string.equals("setObjectPosition")) {
				byte1 = 2;
			}

			break;
		
		case -352953986: 
			if (string.equals("setGizmoOrigin")) {
				byte1 = 0;
			}

			break;
		
		case 1152285259: 
			if (string.equals("setObjectParent")) {
				byte1 = 1;
			}

		
		}
		UI3DScene.SceneObject sceneObject;
		switch (byte1) {
		case 0: 
			String string2 = (String)object;
			byte byte2 = -1;
			switch (string2.hashCode()) {
			case -1963501277: 
				if (string2.equals("attachment")) {
					byte2 = 0;
				}

			
			default: 
				switch (byte2) {
				case 0: 
					UI3DScene.SceneObject sceneObject2 = this.getSceneObjectById((String)object2, true);
					this.m_gizmoParent = this.getSceneObjectById((String)object3, true);
					this.m_originAttachment.m_object = this.m_gizmoParent;
					this.m_originAttachment.m_attachmentName = (String)object4;
					this.m_gizmoOrigin = this.m_originAttachment;
					this.m_gizmoChild = sceneObject2;
				
				default: 
					return null;
				
				}

			
			}

		
		case 1: 
			sceneObject = this.getSceneObjectById((String)object, true);
			sceneObject.m_translate.zero();
			sceneObject.m_rotate.zero();
			sceneObject.m_attachment = (String)object2;
			sceneObject.m_parent = this.getSceneObjectById((String)object3, false);
			sceneObject.m_parentAttachment = (String)object4;
			if (sceneObject.m_parent != null && sceneObject.m_parent.m_parent == sceneObject) {
				sceneObject.m_parent.m_parent = null;
			}

			return null;
		
		case 2: 
			sceneObject = this.getSceneObjectById((String)object, true);
			sceneObject.m_translate.set(((Double)object2).floatValue(), ((Double)object3).floatValue(), ((Double)object4).floatValue());
			return null;
		
		case 3: 
			UI3DScene.SceneCharacter sceneCharacter = (UI3DScene.SceneCharacter)this.getSceneObjectById((String)object, UI3DScene.SceneCharacter.class, true);
			UI3DScene.SceneVehicle sceneVehicle = (UI3DScene.SceneVehicle)this.getSceneObjectById((String)object2, UI3DScene.SceneVehicle.class, true);
			VehicleScript.Passenger passenger = sceneVehicle.m_script.getPassengerById((String)object3);
			if (passenger == null) {
				return null;
			}

			VehicleScript.Position position = passenger.getPositionById((String)object4);
			if (position != null) {
				this.tempVector3f.set((Vector3fc)sceneVehicle.m_script.getModel().getOffset());
				this.tempVector3f.add(position.getOffset());
				Vector3f vector3f = this.tempVector3f;
				vector3f.z *= -1.0F;
				sceneCharacter.m_translate.set((Vector3fc)this.tempVector3f);
				sceneCharacter.m_rotate.set((Vector3fc)position.rotate);
				sceneCharacter.m_parent = sceneVehicle;
				if (sceneCharacter.m_animatedModel != null) {
					String string3 = "inside".equalsIgnoreCase(position.getId()) ? "player-vehicle" : "player-editor";
					if (!string3.equals(sceneCharacter.m_animatedModel.GetAnimSetName())) {
						sceneCharacter.m_animatedModel.setAnimSetName(string3);
						sceneCharacter.m_animatedModel.getAdvancedAnimator().OnAnimDataChanged(false);
						ActionGroup actionGroup = ActionGroup.getActionGroup(sceneCharacter.m_animatedModel.GetAnimSetName());
						ActionContext actionContext = sceneCharacter.m_animatedModel.getActionContext();
						if (actionGroup != actionContext.getGroup()) {
							actionContext.setGroup(actionGroup);
						}

						sceneCharacter.m_animatedModel.getAdvancedAnimator().SetState(actionContext.getCurrentStateName(), PZArrayUtil.listConvert(actionContext.getChildStates(), (var0)->{
							return var0.name;
						}));
					}
				}
			}

			return null;
		
		default: 
			throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \"%s\" \"%s\"", string, object, object2, object3));
		
		}
	}

	public Object fromLua6(String string, Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -1262743205: 
			if (string.equals("addBox3D")) {
				byte1 = 2;
			}

			break;
		
		case -1149187967: 
			if (string.equals("addAABB")) {
				byte1 = 0;
			}

			break;
		
		case -1149133854: 
			if (string.equals("addAxis")) {
				byte1 = 1;
			}

		
		}
		float float1;
		float float2;
		float float3;
		float float4;
		float float5;
		float float6;
		switch (byte1) {
		case 0: 
			float4 = ((Double)object).floatValue();
			float5 = ((Double)object2).floatValue();
			float6 = ((Double)object3).floatValue();
			float1 = ((Double)object4).floatValue();
			float2 = ((Double)object5).floatValue();
			float3 = ((Double)object6).floatValue();
			this.m_aabb.add(((UI3DScene.AABB)s_aabbPool.alloc()).set(float4, float5, float6, float1, float2, float3, 1.0F, 1.0F, 1.0F));
			return null;
		
		case 1: 
			float4 = ((Double)object).floatValue();
			float5 = ((Double)object2).floatValue();
			float6 = ((Double)object3).floatValue();
			float1 = ((Double)object4).floatValue();
			float2 = ((Double)object5).floatValue();
			float3 = ((Double)object6).floatValue();
			this.m_axes.add(((UI3DScene.PositionRotation)s_posRotPool.alloc()).set(float4, float5, float6, float1, float2, float3));
			return null;
		
		case 2: 
			Vector3f vector3f = (Vector3f)object;
			Vector3f vector3f2 = (Vector3f)object2;
			Vector3f vector3f3 = (Vector3f)object3;
			float1 = ((Double)object4).floatValue();
			float2 = ((Double)object5).floatValue();
			float3 = ((Double)object6).floatValue();
			this.m_box3D.add(((UI3DScene.Box3D)s_box3DPool.alloc()).set(vector3f.x, vector3f.y, vector3f.z, vector3f2.x, vector3f2.y, vector3f2.z, vector3f3.x, vector3f3.y, vector3f3.z, float1, float2, float3));
			return null;
		
		default: 
			throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\"", string, object, object2, object3, object4, object5, object6));
		
		}
	}

	public Object fromLua9(String string, Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -1149187967: 
			if (string.equals("addAABB")) {
				byte1 = 0;
			}

		
		default: 
			switch (byte1) {
			case 0: 
				float float1 = ((Double)object).floatValue();
				float float2 = ((Double)object2).floatValue();
				float float3 = ((Double)object3).floatValue();
				float float4 = ((Double)object4).floatValue();
				float float5 = ((Double)object5).floatValue();
				float float6 = ((Double)object6).floatValue();
				float float7 = ((Double)object7).floatValue();
				float float8 = ((Double)object8).floatValue();
				float float9 = ((Double)object9).floatValue();
				this.m_aabb.add(((UI3DScene.AABB)s_aabbPool.alloc()).set(float1, float2, float3, float4, float5, float6, float7, float8, float9));
				return null;
			
			default: 
				throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\"", string, object, object2, object3, object4, object5, object6, object7, object8, object9));
			
			}

		
		}
	}

	private int screenWidth() {
		return (int)this.width;
	}

	private int screenHeight() {
		return (int)this.height;
	}

	public float uiToSceneX(float float1, float float2) {
		float float3 = float1 - (float)this.screenWidth() / 2.0F;
		float3 += (float)this.m_view_x;
		float3 /= this.zoomMult();
		return float3;
	}

	public float uiToSceneY(float float1, float float2) {
		float float3 = float2 - (float)this.screenHeight() / 2.0F;
		float3 *= -1.0F;
		float3 -= (float)this.m_view_y;
		float3 /= this.zoomMult();
		return float3;
	}

	public Vector3f uiToScene(float float1, float float2, float float3, Vector3f vector3f) {
		this.uiToScene((Matrix4f)null, float1, float2, float3, vector3f);
		switch (this.m_view) {
		case Left: 
		
		case Right: 
			vector3f.x = 0.0F;
			break;
		
		case Top: 
		
		case Bottom: 
			vector3f.y = 0.0F;
			break;
		
		case Front: 
		
		case Back: 
			vector3f.z = 0.0F;
		
		}
		return vector3f;
	}

	public Vector3f uiToScene(Matrix4f matrix4f, float float1, float float2, float float3, Vector3f vector3f) {
		float2 = (float)this.screenHeight() - float2;
		Matrix4f matrix4f2 = allocMatrix4f();
		matrix4f2.set((Matrix4fc)this.m_projection);
		matrix4f2.mul((Matrix4fc)this.m_modelView);
		if (matrix4f != null) {
			matrix4f2.mul((Matrix4fc)matrix4f);
		}

		matrix4f2.invert();
		this.m_viewport[2] = this.screenWidth();
		this.m_viewport[3] = this.screenHeight();
		matrix4f2.unprojectInv(float1, float2, float3, this.m_viewport, vector3f);
		releaseMatrix4f(matrix4f2);
		return vector3f;
	}

	public float sceneToUIX(float float1, float float2, float float3) {
		this.tempVector4f.set(float1, float2, float3, 1.0F);
		Matrix4f matrix4f = allocMatrix4f();
		matrix4f.set((Matrix4fc)this.m_projection);
		matrix4f.mul((Matrix4fc)this.m_modelView);
		this.m_viewport[2] = this.screenWidth();
		this.m_viewport[3] = this.screenHeight();
		matrix4f.project(float1, float2, float3, this.m_viewport, this.tempVector3f);
		releaseMatrix4f(matrix4f);
		return this.tempVector3f.x();
	}

	public float sceneToUIY(float float1, float float2, float float3) {
		this.tempVector4f.set(float1, float2, float3, 1.0F);
		Matrix4f matrix4f = allocMatrix4f();
		matrix4f.set((Matrix4fc)this.m_projection);
		matrix4f.mul((Matrix4fc)this.m_modelView);
		int[] intArray = new int[]{0, 0, this.screenWidth(), this.screenHeight()};
		matrix4f.project(float1, float2, float3, intArray, this.tempVector3f);
		releaseMatrix4f(matrix4f);
		return (float)this.screenHeight() - this.tempVector3f.y();
	}

	private void renderGridXY(int int1) {
		int int2;
		int int3;
		for (int2 = -5; int2 < 5; ++int2) {
			for (int3 = 1; int3 < int1; ++int3) {
				vboLines.addLine((float)int2 + (float)int3 / (float)int1, -5.0F, 0.0F, (float)int2 + (float)int3 / (float)int1, 5.0F, 0.0F, 0.2F, 0.2F, 0.2F, this.GRID_ALPHA);
			}
		}

		for (int2 = -5; int2 < 5; ++int2) {
			for (int3 = 1; int3 < int1; ++int3) {
				vboLines.addLine(-5.0F, (float)int2 + (float)int3 / (float)int1, 0.0F, 5.0F, (float)int2 + (float)int3 / (float)int1, 0.0F, 0.2F, 0.2F, 0.2F, this.GRID_ALPHA);
			}
		}

		for (int2 = -5; int2 <= 5; ++int2) {
			vboLines.addLine((float)int2, -5.0F, 0.0F, (float)int2, 5.0F, 0.0F, 0.1F, 0.1F, 0.1F, this.GRID_ALPHA);
		}

		for (int2 = -5; int2 <= 5; ++int2) {
			vboLines.addLine(-5.0F, (float)int2, 0.0F, 5.0F, (float)int2, 0.0F, 0.1F, 0.1F, 0.1F, this.GRID_ALPHA);
		}

		if (this.m_bDrawGridAxes) {
			byte byte1 = 0;
			vboLines.addLine(-5.0F, 0.0F, (float)byte1, 5.0F, 0.0F, (float)byte1, 1.0F, 0.0F, 0.0F, this.GRID_ALPHA);
			byte1 = 0;
			vboLines.addLine(0.0F, -5.0F, (float)byte1, 0.0F, 5.0F, (float)byte1, 0.0F, 1.0F, 0.0F, this.GRID_ALPHA);
		}
	}

	private void renderGridXZ(int int1) {
		int int2;
		int int3;
		for (int2 = -5; int2 < 5; ++int2) {
			for (int3 = 1; int3 < int1; ++int3) {
				vboLines.addLine((float)int2 + (float)int3 / (float)int1, 0.0F, -5.0F, (float)int2 + (float)int3 / (float)int1, 0.0F, 5.0F, 0.2F, 0.2F, 0.2F, this.GRID_ALPHA);
			}
		}

		for (int2 = -5; int2 < 5; ++int2) {
			for (int3 = 1; int3 < int1; ++int3) {
				vboLines.addLine(-5.0F, 0.0F, (float)int2 + (float)int3 / (float)int1, 5.0F, 0.0F, (float)int2 + (float)int3 / (float)int1, 0.2F, 0.2F, 0.2F, this.GRID_ALPHA);
			}
		}

		for (int2 = -5; int2 <= 5; ++int2) {
			vboLines.addLine((float)int2, 0.0F, -5.0F, (float)int2, 0.0F, 5.0F, 0.1F, 0.1F, 0.1F, this.GRID_ALPHA);
		}

		for (int2 = -5; int2 <= 5; ++int2) {
			vboLines.addLine(-5.0F, 0.0F, (float)int2, 5.0F, 0.0F, (float)int2, 0.1F, 0.1F, 0.1F, this.GRID_ALPHA);
		}

		if (this.m_bDrawGridAxes) {
			byte byte1 = 0;
			vboLines.addLine(-5.0F, 0.0F, (float)byte1, 5.0F, 0.0F, (float)byte1, 1.0F, 0.0F, 0.0F, this.GRID_ALPHA);
			byte1 = 0;
			vboLines.addLine((float)byte1, 0.0F, -5.0F, (float)byte1, 0.0F, 5.0F, 0.0F, 0.0F, 1.0F, this.GRID_ALPHA);
		}
	}

	private void renderGridYZ(int int1) {
		int int2;
		int int3;
		for (int2 = -5; int2 < 5; ++int2) {
			for (int3 = 1; int3 < int1; ++int3) {
				vboLines.addLine(0.0F, (float)int2 + (float)int3 / (float)int1, -5.0F, 0.0F, (float)int2 + (float)int3 / (float)int1, 5.0F, 0.2F, 0.2F, 0.2F, this.GRID_ALPHA);
			}
		}

		for (int2 = -5; int2 < 5; ++int2) {
			for (int3 = 1; int3 < int1; ++int3) {
				vboLines.addLine(0.0F, -5.0F, (float)int2 + (float)int3 / (float)int1, 0.0F, 5.0F, (float)int2 + (float)int3 / (float)int1, 0.2F, 0.2F, 0.2F, this.GRID_ALPHA);
			}
		}

		for (int2 = -5; int2 <= 5; ++int2) {
			vboLines.addLine(0.0F, (float)int2, -5.0F, 0.0F, (float)int2, 5.0F, 0.1F, 0.1F, 0.1F, this.GRID_ALPHA);
		}

		for (int2 = -5; int2 <= 5; ++int2) {
			vboLines.addLine(0.0F, -5.0F, (float)int2, 0.0F, 5.0F, (float)int2, 0.1F, 0.1F, 0.1F, this.GRID_ALPHA);
		}

		if (this.m_bDrawGridAxes) {
			byte byte1 = 0;
			vboLines.addLine(0.0F, -5.0F, (float)byte1, 0.0F, 5.0F, (float)byte1, 0.0F, 1.0F, 0.0F, this.GRID_ALPHA);
			byte1 = 0;
			vboLines.addLine((float)byte1, 0.0F, -5.0F, (float)byte1, 0.0F, 5.0F, 0.0F, 0.0F, 1.0F, this.GRID_ALPHA);
		}
	}

	private void renderGrid() {
		vboLines.setLineWidth(1.0F);
		this.GRID_ALPHA = 1.0F;
		long long1 = System.currentTimeMillis();
		if (this.m_viewChangeTime + this.VIEW_CHANGE_TIME > long1) {
			float float1 = (float)(this.m_viewChangeTime + this.VIEW_CHANGE_TIME - long1) / (float)this.VIEW_CHANGE_TIME;
			this.GRID_ALPHA = 1.0F - float1;
			this.GRID_ALPHA *= this.GRID_ALPHA;
		}

		switch (this.m_view) {
		case Left: 
		
		case Right: 
			this.renderGridYZ(10);
			return;
		
		case Top: 
		
		case Bottom: 
			this.renderGridXZ(10);
			return;
		
		case Front: 
		
		case Back: 
			this.renderGridXY(10);
			return;
		
		default: 
			switch (this.m_gridPlane) {
			case XY: 
				this.renderGridXY(10);
				break;
			
			case XZ: 
				this.renderGridXZ(10);
				break;
			
			case YZ: 
				this.renderGridYZ(10);
			
			}

		
		}
	}

	void renderAxis(UI3DScene.PositionRotation positionRotation) {
		this.renderAxis(positionRotation.pos, positionRotation.rot);
	}

	void renderAxis(Vector3f vector3f, Vector3f vector3f2) {
		UI3DScene.StateData stateData = this.stateDataRender();
		vboLines.flush();
		Matrix4f matrix4f = allocMatrix4f();
		matrix4f.set((Matrix4fc)stateData.m_gizmoParentTransform);
		matrix4f.mul((Matrix4fc)stateData.m_gizmoOriginTransform);
		matrix4f.mul((Matrix4fc)stateData.m_gizmoChildTransform);
		matrix4f.translate(vector3f);
		matrix4f.rotateXYZ(vector3f2.x * 0.017453292F, vector3f2.y * 0.017453292F, vector3f2.z * 0.017453292F);
		stateData.m_modelView.mul((Matrix4fc)matrix4f, matrix4f);
		PZGLUtil.pushAndLoadMatrix(5888, matrix4f);
		releaseMatrix4f(matrix4f);
		float float1 = 0.1F;
		vboLines.setLineWidth(3.0F);
		vboLines.addLine(0.0F, 0.0F, 0.0F, float1, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F);
		vboLines.addLine(0.0F, 0.0F, 0.0F, 0.0F, 0.0F + float1, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F);
		vboLines.addLine(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F + float1, 0.0F, 0.0F, 1.0F, 1.0F);
		vboLines.flush();
		PZGLUtil.popMatrix(5888);
	}

	private void renderAABB(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		float float10 = float4 / 2.0F;
		float float11 = float5 / 2.0F;
		float float12 = float6 / 2.0F;
		vboLines.setOffset(float1, float2, float3);
		vboLines.setLineWidth(1.0F);
		float float13 = 1.0F;
		vboLines.addLine(float10, float11, float12, -float10, float11, float12, float7, float8, float9, float13);
		vboLines.addLine(float10, float11, float12, float10, -float11, float12, float7, float8, float9, float13);
		vboLines.addLine(float10, float11, float12, float10, float11, -float12, float7, float8, float9, float13);
		vboLines.addLine(-float10, float11, float12, -float10, -float11, float12, float7, float8, float9, float13);
		vboLines.addLine(-float10, float11, float12, -float10, float11, -float12, float7, float8, float9, float13);
		vboLines.addLine(float10, float11, -float12, float10, -float11, -float12, float7, float8, float9, float13);
		vboLines.addLine(float10, float11, -float12, -float10, float11, -float12, float7, float8, float9, float13);
		vboLines.addLine(-float10, float11, -float12, -float10, -float11, -float12, float7, float8, float9, float13);
		vboLines.addLine(float10, -float11, -float12, -float10, -float11, -float12, float7, float8, float9, float13);
		vboLines.addLine(float10, -float11, float12, float10, -float11, -float12, float7, float8, float9, float13);
		vboLines.addLine(-float10, -float11, float12, -float10, -float11, -float12, float7, float8, float9, float13);
		vboLines.addLine(float10, -float11, float12, -float10, -float11, float12, float7, float8, float9, float13);
		vboLines.setOffset(0.0F, 0.0F, 0.0F);
	}

	private void renderAABB(float float1, float float2, float float3, Vector3f vector3f, Vector3f vector3f2, float float4, float float5, float float6) {
		vboLines.setOffset(float1, float2, float3);
		vboLines.setLineWidth(1.0F);
		float float7 = 1.0F;
		vboLines.addLine(vector3f2.x, vector3f2.y, vector3f2.z, vector3f.x, vector3f2.y, vector3f2.z, float4, float5, float6, float7);
		vboLines.addLine(vector3f2.x, vector3f2.y, vector3f2.z, vector3f2.x, vector3f.y, vector3f2.z, float4, float5, float6, float7);
		vboLines.addLine(vector3f2.x, vector3f2.y, vector3f2.z, vector3f2.x, vector3f2.y, vector3f.z, float4, float5, float6, float7);
		vboLines.addLine(vector3f.x, vector3f2.y, vector3f2.z, vector3f.x, vector3f.y, vector3f2.z, float4, float5, float6, float7);
		vboLines.addLine(vector3f.x, vector3f2.y, vector3f2.z, vector3f.x, vector3f2.y, vector3f.z, float4, float5, float6, float7);
		vboLines.addLine(vector3f2.x, vector3f2.y, vector3f.z, vector3f2.x, vector3f.y, vector3f.z, float4, float5, float6, float7);
		vboLines.addLine(vector3f2.x, vector3f2.y, vector3f.z, vector3f.x, vector3f2.y, vector3f.z, float4, float5, float6, float7);
		vboLines.addLine(vector3f.x, vector3f2.y, vector3f.z, vector3f.x, vector3f.y, vector3f.z, float4, float5, float6, float7);
		vboLines.addLine(vector3f2.x, vector3f.y, vector3f.z, vector3f.x, vector3f.y, vector3f.z, float4, float5, float6, float7);
		vboLines.addLine(vector3f2.x, vector3f.y, vector3f2.z, vector3f2.x, vector3f.y, vector3f.z, float4, float5, float6, float7);
		vboLines.addLine(vector3f.x, vector3f.y, vector3f2.z, vector3f.x, vector3f.y, vector3f.z, float4, float5, float6, float7);
		vboLines.addLine(vector3f2.x, vector3f.y, vector3f2.z, vector3f.x, vector3f.y, vector3f2.z, float4, float5, float6, float7);
		vboLines.setOffset(0.0F, 0.0F, 0.0F);
	}

	private void renderBox3D(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		UI3DScene.StateData stateData = this.stateDataRender();
		vboLines.flush();
		Matrix4f matrix4f = allocMatrix4f();
		matrix4f.identity();
		matrix4f.translate(float1, float2, float3);
		matrix4f.rotateXYZ(float7 * 0.017453292F, float8 * 0.017453292F, float9 * 0.017453292F);
		stateData.m_modelView.mul((Matrix4fc)matrix4f, matrix4f);
		PZGLUtil.pushAndLoadMatrix(5888, matrix4f);
		releaseMatrix4f(matrix4f);
		this.renderAABB(float1 * 0.0F, float2 * 0.0F, float3 * 0.0F, float4, float5, float6, float10, float11, float12);
		vboLines.flush();
		PZGLUtil.popMatrix(5888);
	}

	private void calcMatrices(Matrix4f matrix4f, Matrix4f matrix4f2) {
		float float1 = (float)this.screenWidth();
		float float2 = 1366.0F / float1;
		float float3 = (float)this.screenHeight() * float2;
		float1 = 1366.0F;
		float1 /= this.zoomMult();
		float3 /= this.zoomMult();
		matrix4f.setOrtho(-float1 / 2.0F, float1 / 2.0F, -float3 / 2.0F, float3 / 2.0F, -10.0F, 10.0F);
		float float4 = (float)this.m_view_x / this.zoomMult() * float2;
		float float5 = (float)this.m_view_y / this.zoomMult() * float2;
		matrix4f.translate(-float4, float5, 0.0F);
		matrix4f2.identity();
		float float6 = 0.0F;
		float float7 = 0.0F;
		float float8 = 0.0F;
		switch (this.m_view) {
		case Left: 
			float7 = 270.0F;
			break;
		
		case Right: 
			float7 = 90.0F;
			break;
		
		case Top: 
			float7 = 90.0F;
			float8 = 90.0F;
			break;
		
		case Bottom: 
			float7 = 90.0F;
			float8 = 270.0F;
		
		case Front: 
		
		default: 
			break;
		
		case Back: 
			float7 = 180.0F;
			break;
		
		case UserDefined: 
			float6 = this.m_viewRotation.x;
			float7 = this.m_viewRotation.y;
			float8 = this.m_viewRotation.z;
		
		}
		matrix4f2.rotateXYZ(float6 * 0.017453292F, float7 * 0.017453292F, float8 * 0.017453292F);
	}

	UI3DScene.Ray getCameraRay(float float1, float float2, UI3DScene.Ray ray) {
		return this.getCameraRay(float1, float2, this.m_projection, this.m_modelView, ray);
	}

	UI3DScene.Ray getCameraRay(float float1, float float2, Matrix4f matrix4f, Matrix4f matrix4f2, UI3DScene.Ray ray) {
		Matrix4f matrix4f3 = allocMatrix4f();
		matrix4f3.set((Matrix4fc)matrix4f);
		matrix4f3.mul((Matrix4fc)matrix4f2);
		matrix4f3.invert();
		this.m_viewport[2] = this.screenWidth();
		this.m_viewport[3] = this.screenHeight();
		Vector3f vector3f = matrix4f3.unprojectInv(float1, float2, 0.0F, this.m_viewport, allocVector3f());
		Vector3f vector3f2 = matrix4f3.unprojectInv(float1, float2, 1.0F, this.m_viewport, allocVector3f());
		ray.origin.set((Vector3fc)vector3f);
		ray.direction.set((Vector3fc)vector3f2.sub(vector3f).normalize());
		releaseVector3f(vector3f2);
		releaseVector3f(vector3f);
		releaseMatrix4f(matrix4f3);
		return ray;
	}

	float closest_distance_between_lines(UI3DScene.Ray ray, UI3DScene.Ray ray2) {
		Vector3f vector3f = allocVector3f().set((Vector3fc)ray.direction);
		Vector3f vector3f2 = allocVector3f().set((Vector3fc)ray2.direction);
		Vector3f vector3f3 = allocVector3f().set((Vector3fc)ray.origin).sub(ray2.origin);
		float float1 = vector3f.dot(vector3f);
		float float2 = vector3f.dot(vector3f2);
		float float3 = vector3f2.dot(vector3f2);
		float float4 = vector3f.dot(vector3f3);
		float float5 = vector3f2.dot(vector3f3);
		float float6 = float1 * float3 - float2 * float2;
		float float7;
		float float8;
		if (float6 < 1.0E-8F) {
			float7 = 0.0F;
			float8 = float2 > float3 ? float4 / float2 : float5 / float3;
		} else {
			float7 = (float2 * float5 - float3 * float4) / float6;
			float8 = (float1 * float5 - float2 * float4) / float6;
		}

		Vector3f vector3f4 = vector3f3.add(vector3f.mul(float7)).sub(vector3f2.mul(float8));
		ray.t = float7;
		ray2.t = float8;
		releaseVector3f(vector3f);
		releaseVector3f(vector3f2);
		releaseVector3f(vector3f3);
		return vector3f4.length();
	}

	Vector3f project(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3) {
		return vector3f3.set((Vector3fc)vector3f2).mul(vector3f.dot(vector3f2) / vector3f2.dot(vector3f2));
	}

	Vector3f reject(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3) {
		Vector3f vector3f4 = this.project(vector3f, vector3f2, allocVector3f());
		vector3f3.set((Vector3fc)vector3f).sub(vector3f4);
		releaseVector3f(vector3f4);
		return vector3f3;
	}

	public static int intersect_ray_plane(UI3DScene.Plane plane, UI3DScene.Ray ray, Vector3f vector3f) {
		Vector3f vector3f2 = allocVector3f().set((Vector3fc)ray.direction).mul(100.0F);
		Vector3f vector3f3 = allocVector3f().set((Vector3fc)ray.origin).sub(plane.point);
		byte byte1;
		try {
			float float1 = plane.normal.dot(vector3f2);
			float float2 = -plane.normal.dot(vector3f3);
			if (Math.abs(float1) < 1.0E-8F) {
				byte byte2;
				if (float2 == 0.0F) {
					byte2 = 2;
					return byte2;
				}

				byte2 = 0;
				return byte2;
			}

			float float3 = float2 / float1;
			if (!(float3 < 0.0F) && !(float3 > 1.0F)) {
				vector3f.set((Vector3fc)ray.origin).add(vector3f2.mul(float3));
				byte1 = 1;
				return byte1;
			}

			byte1 = 0;
		} finally {
			releaseVector3f(vector3f2);
			releaseVector3f(vector3f3);
		}

		return byte1;
	}

	float distance_between_point_ray(Vector3f vector3f, UI3DScene.Ray ray) {
		Vector3f vector3f2 = allocVector3f().set((Vector3fc)ray.direction).mul(100.0F);
		Vector3f vector3f3 = allocVector3f().set((Vector3fc)vector3f).sub(ray.origin);
		float float1 = vector3f3.dot(vector3f2);
		float float2 = vector3f2.dot(vector3f2);
		float float3 = float1 / float2;
		Vector3f vector3f4 = vector3f2.mul(float3).add(ray.origin);
		float float4 = vector3f4.sub(vector3f).length();
		releaseVector3f(vector3f3);
		releaseVector3f(vector3f2);
		return float4;
	}

	float closest_distance_line_circle(UI3DScene.Ray ray, UI3DScene.Circle circle, Vector3f vector3f) {
		UI3DScene.Plane plane = allocPlane().set(circle.orientation, circle.center);
		Vector3f vector3f2 = allocVector3f();
		float float1;
		if (intersect_ray_plane(plane, ray, vector3f2) == 1) {
			vector3f.set((Vector3fc)vector3f2).sub(circle.center).normalize().mul(circle.radius).add(circle.center);
			float1 = vector3f2.sub(vector3f).length();
		} else {
			Vector3f vector3f3 = allocVector3f().set((Vector3fc)ray.origin).sub(circle.center);
			Vector3f vector3f4 = this.reject(vector3f3, circle.orientation, allocVector3f());
			vector3f.set((Vector3fc)vector3f4.normalize().mul(circle.radius).add(circle.center));
			float1 = this.distance_between_point_ray(vector3f, ray);
			releaseVector3f(vector3f4);
			releaseVector3f(vector3f3);
		}

		releaseVector3f(vector3f2);
		releasePlane(plane);
		return float1;
	}

	private UI3DScene.StateData stateDataMain() {
		return this.m_stateData[SpriteRenderer.instance.getMainStateIndex()];
	}

	private UI3DScene.StateData stateDataRender() {
		return this.m_stateData[SpriteRenderer.instance.getRenderStateIndex()];
	}

	private static enum View {

		Left,
		Right,
		Top,
		Bottom,
		Front,
		Back,
		UserDefined;

		private static UI3DScene.View[] $values() {
			return new UI3DScene.View[]{Left, Right, Top, Bottom, Front, Back, UserDefined};
		}
	}
	private static enum TransformMode {

		Global,
		Local;

		private static UI3DScene.TransformMode[] $values() {
			return new UI3DScene.TransformMode[]{Global, Local};
		}
	}
	private static enum GridPlane {

		XY,
		XZ,
		YZ;

		private static UI3DScene.GridPlane[] $values() {
			return new UI3DScene.GridPlane[]{XY, XZ, YZ};
		}
	}

	private final class CharacterSceneModelCamera extends UI3DScene.SceneModelCamera {
		private CharacterSceneModelCamera() {
			super();
		}

		public void Begin() {
			UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
			GL11.glViewport(UI3DScene.this.getAbsoluteX().intValue(), Core.getInstance().getScreenHeight() - UI3DScene.this.getAbsoluteY().intValue() - UI3DScene.this.getHeight().intValue(), UI3DScene.this.getWidth().intValue(), UI3DScene.this.getHeight().intValue());
			PZGLUtil.pushAndLoadMatrix(5889, stateData.m_projection);
			Matrix4f matrix4f = UI3DScene.allocMatrix4f();
			matrix4f.set((Matrix4fc)stateData.m_modelView);
			matrix4f.mul((Matrix4fc)this.m_renderData.m_transform);
			PZGLUtil.pushAndLoadMatrix(5888, matrix4f);
			UI3DScene.releaseMatrix4f(matrix4f);
		}

		public void End() {
			PZGLUtil.popMatrix(5889);
			PZGLUtil.popMatrix(5888);
		}
	}

	private final class VehicleSceneModelCamera extends UI3DScene.SceneModelCamera {

		private VehicleSceneModelCamera() {
			super();
		}

		public void Begin() {
			UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
			GL11.glViewport(UI3DScene.this.getAbsoluteX().intValue(), Core.getInstance().getScreenHeight() - UI3DScene.this.getAbsoluteY().intValue() - UI3DScene.this.getHeight().intValue(), UI3DScene.this.getWidth().intValue(), UI3DScene.this.getHeight().intValue());
			PZGLUtil.pushAndLoadMatrix(5889, stateData.m_projection);
			Matrix4f matrix4f = UI3DScene.allocMatrix4f();
			matrix4f.set((Matrix4fc)stateData.m_modelView);
			matrix4f.mul((Matrix4fc)this.m_renderData.m_transform);
			PZGLUtil.pushAndLoadMatrix(5888, matrix4f);
			UI3DScene.releaseMatrix4f(matrix4f);
			GL11.glDepthRange(0.0, 1.0);
			GL11.glDepthMask(true);
		}

		public void End() {
			PZGLUtil.popMatrix(5889);
			PZGLUtil.popMatrix(5888);
		}
	}

	private static final class StateData {
		final Matrix4f m_projection = new Matrix4f();
		final Matrix4f m_modelView = new Matrix4f();
		int m_zoom;
		UI3DScene.GridPlaneDrawer m_gridPlaneDrawer;
		UI3DScene.OverlaysDrawer m_overlaysDrawer;
		final ArrayList m_objectData = new ArrayList();
		UI3DScene.Gizmo m_gizmo = null;
		final Vector3f m_gizmoTranslate = new Vector3f();
		final Vector3f m_gizmoRotate = new Vector3f();
		final Matrix4f m_gizmoParentTransform = new Matrix4f();
		final Matrix4f m_gizmoOriginTransform = new Matrix4f();
		final Matrix4f m_gizmoChildTransform = new Matrix4f();
		final Matrix4f m_gizmoTransform = new Matrix4f();
		boolean m_hasGizmoOrigin;
		boolean m_selectedAttachmentIsChildAttachment;
		UI3DScene.Axis m_gizmoAxis;
		final UI3DScene.TranslateGizmoRenderData m_translateGizmoRenderData;
		final ArrayList m_axes;
		final ArrayList m_aabb;
		final ArrayList m_box3D;

		private StateData() {
			this.m_gizmoAxis = UI3DScene.Axis.None;
			this.m_translateGizmoRenderData = new UI3DScene.TranslateGizmoRenderData();
			this.m_axes = new ArrayList();
			this.m_aabb = new ArrayList();
			this.m_box3D = new ArrayList();
		}

		private float zoomMult() {
			return (float)Math.exp((double)((float)this.m_zoom * 0.2F)) * 160.0F / Math.max(1.82F, 1.0F);
		}
	}

	private final class RotateGizmo extends UI3DScene.Gizmo {
		UI3DScene.Axis m_trackAxis;
		final UI3DScene.Circle m_trackCircle;
		final Matrix4f m_startXfrm;
		final Matrix4f m_startInvXfrm;
		final Vector3f m_startPointOnCircle;
		final Vector3f m_currentPointOnCircle;
		final ArrayList m_circlePointsMain;
		final ArrayList m_circlePointsRender;

		private RotateGizmo() {
			super();
			this.m_trackAxis = UI3DScene.Axis.None;
			this.m_trackCircle = new UI3DScene.Circle();
			this.m_startXfrm = new Matrix4f();
			this.m_startInvXfrm = new Matrix4f();
			this.m_startPointOnCircle = new Vector3f();
			this.m_currentPointOnCircle = new Vector3f();
			this.m_circlePointsMain = new ArrayList();
			this.m_circlePointsRender = new ArrayList();
		}

		UI3DScene.Axis hitTest(float float1, float float2) {
			if (!this.m_visible) {
				return UI3DScene.Axis.None;
			} else {
				UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
				float2 = (float)UI3DScene.this.screenHeight() - float2;
				UI3DScene.Ray ray = UI3DScene.this.getCameraRay(float1, float2, UI3DScene.allocRay());
				Matrix4f matrix4f = UI3DScene.allocMatrix4f();
				matrix4f.set((Matrix4fc)stateData.m_gizmoParentTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoOriginTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoChildTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoTransform);
				Vector3f vector3f = matrix4f.getScale(UI3DScene.allocVector3f());
				matrix4f.scale(1.0F / vector3f.x, 1.0F / vector3f.y, 1.0F / vector3f.z);
				UI3DScene.releaseVector3f(vector3f);
				if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
					matrix4f.setRotationXYZ(0.0F, 0.0F, 0.0F);
				}

				float float3 = UI3DScene.this.m_gizmoScale / stateData.zoomMult() * 1000.0F;
				float float4 = this.LENGTH * float3;
				Vector3f vector3f2 = matrix4f.transformProject(UI3DScene.allocVector3f().set(0.0F, 0.0F, 0.0F));
				Vector3f vector3f3 = matrix4f.transformDirection(UI3DScene.allocVector3f().set(1.0F, 0.0F, 0.0F)).normalize();
				Vector3f vector3f4 = matrix4f.transformDirection(UI3DScene.allocVector3f().set(0.0F, 1.0F, 0.0F)).normalize();
				Vector3f vector3f5 = matrix4f.transformDirection(UI3DScene.allocVector3f().set(0.0F, 0.0F, 1.0F)).normalize();
				Vector2 vector2 = UI3DScene.allocVector2();
				this.getCircleSegments(vector3f2, float4, vector3f4, vector3f5, this.m_circlePointsMain);
				float float5 = this.hitTestCircle(ray, this.m_circlePointsMain, vector2);
				((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(this.m_circlePointsMain);
				this.m_circlePointsMain.clear();
				this.getCircleSegments(vector3f2, float4, vector3f3, vector3f5, this.m_circlePointsMain);
				float float6 = this.hitTestCircle(ray, this.m_circlePointsMain, vector2);
				((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(this.m_circlePointsMain);
				this.m_circlePointsMain.clear();
				this.getCircleSegments(vector3f2, float4, vector3f3, vector3f4, this.m_circlePointsMain);
				float float7 = this.hitTestCircle(ray, this.m_circlePointsMain, vector2);
				((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(this.m_circlePointsMain);
				this.m_circlePointsMain.clear();
				UI3DScene.releaseVector2(vector2);
				UI3DScene.releaseVector3f(vector3f3);
				UI3DScene.releaseVector3f(vector3f4);
				UI3DScene.releaseVector3f(vector3f5);
				UI3DScene.releaseVector3f(vector3f2);
				UI3DScene.releaseRay(ray);
				UI3DScene.releaseMatrix4f(matrix4f);
				float float8 = 8.0F;
				if (float5 < float6 && float5 < float7) {
					return float5 <= float8 ? UI3DScene.Axis.X : UI3DScene.Axis.None;
				} else if (float6 < float5 && float6 < float7) {
					return float6 <= float8 ? UI3DScene.Axis.Y : UI3DScene.Axis.None;
				} else if (float7 < float5 && float7 < float6) {
					return float7 <= float8 ? UI3DScene.Axis.Z : UI3DScene.Axis.None;
				} else {
					return UI3DScene.Axis.None;
				}
			}
		}

		void startTracking(float float1, float float2, UI3DScene.Axis axis) {
			UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
			this.m_startXfrm.set((Matrix4fc)stateData.m_gizmoParentTransform);
			this.m_startXfrm.mul((Matrix4fc)stateData.m_gizmoOriginTransform);
			this.m_startXfrm.mul((Matrix4fc)stateData.m_gizmoChildTransform);
			this.m_startXfrm.mul((Matrix4fc)stateData.m_gizmoTransform);
			if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
				this.m_startXfrm.setRotationXYZ(0.0F, 0.0F, 0.0F);
			}

			this.m_startInvXfrm.set((Matrix4fc)stateData.m_gizmoParentTransform);
			this.m_startInvXfrm.mul((Matrix4fc)stateData.m_gizmoOriginTransform);
			this.m_startInvXfrm.mul((Matrix4fc)stateData.m_gizmoChildTransform);
			this.m_startInvXfrm.mul((Matrix4fc)stateData.m_gizmoTransform);
			this.m_startInvXfrm.invert();
			this.m_trackAxis = axis;
			this.getPointOnAxis(float1, float2, axis, this.m_trackCircle, this.m_startXfrm, this.m_startPointOnCircle);
		}

		void updateTracking(float float1, float float2) {
			Vector3f vector3f = this.getPointOnAxis(float1, float2, this.m_trackAxis, this.m_trackCircle, this.m_startXfrm, UI3DScene.allocVector3f());
			if (this.m_currentPointOnCircle.equals(vector3f)) {
				UI3DScene.releaseVector3f(vector3f);
			} else {
				this.m_currentPointOnCircle.set((Vector3fc)vector3f);
				UI3DScene.releaseVector3f(vector3f);
				float float3 = this.calculateRotation(this.m_startPointOnCircle, this.m_currentPointOnCircle, this.m_trackCircle);
				switch (this.m_trackAxis) {
				case X: 
					this.m_trackCircle.orientation.set(1.0F, 0.0F, 0.0F);
					break;
				
				case Y: 
					this.m_trackCircle.orientation.set(0.0F, 1.0F, 0.0F);
					break;
				
				case Z: 
					this.m_trackCircle.orientation.set(0.0F, 0.0F, 1.0F);
				
				}

				Vector3f vector3f2 = UI3DScene.allocVector3f().set((Vector3fc)this.m_trackCircle.orientation);
				if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
					this.m_startInvXfrm.transformDirection(vector3f2);
				}

				UI3DScene.Ray ray = UI3DScene.this.getCameraRay(float1, float2, UI3DScene.allocRay());
				Vector3f vector3f3 = this.m_startXfrm.transformDirection(UI3DScene.allocVector3f().set((Vector3fc)vector3f2)).normalize();
				float float4 = ray.direction.dot(vector3f3);
				UI3DScene.releaseVector3f(vector3f3);
				UI3DScene.releaseRay(ray);
				if (UI3DScene.this.m_gizmoParent instanceof UI3DScene.SceneCharacter) {
					if (float4 > 0.0F) {
						float3 *= -1.0F;
					}
				} else if (float4 < 0.0F) {
					float3 *= -1.0F;
				}

				Quaternionf quaternionf = UI3DScene.allocQuaternionf().fromAxisAngleDeg(vector3f2, float3);
				UI3DScene.releaseVector3f(vector3f2);
				vector3f3 = quaternionf.getEulerAnglesXYZ(new Vector3f());
				UI3DScene.releaseQuaternionf(quaternionf);
				vector3f3.x = (float)Math.floor((double)(vector3f3.x * 57.295776F + 0.5F));
				vector3f3.y = (float)Math.floor((double)(vector3f3.y * 57.295776F + 0.5F));
				vector3f3.z = (float)Math.floor((double)(vector3f3.z * 57.295776F + 0.5F));
				LuaManager.caller.pcall(UIManager.getDefaultThread(), UI3DScene.this.getTable().rawget("onGizmoChanged"), UI3DScene.this.table, vector3f3);
			}
		}

		void stopTracking() {
			this.m_trackAxis = UI3DScene.Axis.None;
		}

		void render() {
			if (this.m_visible) {
				UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
				Matrix4f matrix4f = UI3DScene.allocMatrix4f();
				matrix4f.set((Matrix4fc)stateData.m_gizmoParentTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoOriginTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoChildTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoTransform);
				Vector3f vector3f = matrix4f.getScale(UI3DScene.allocVector3f());
				matrix4f.scale(1.0F / vector3f.x, 1.0F / vector3f.y, 1.0F / vector3f.z);
				UI3DScene.releaseVector3f(vector3f);
				if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
					matrix4f.setRotationXYZ(0.0F, 0.0F, 0.0F);
				}

				float float1 = (float)(Mouse.getXA() - UI3DScene.this.getAbsoluteX().intValue());
				float float2 = (float)(Mouse.getYA() - UI3DScene.this.getAbsoluteY().intValue());
				UI3DScene.Ray ray = UI3DScene.this.getCameraRay(float1, (float)UI3DScene.this.screenHeight() - float2, stateData.m_projection, stateData.m_modelView, UI3DScene.allocRay());
				float float3 = UI3DScene.this.m_gizmoScale / stateData.zoomMult() * 1000.0F;
				float float4 = this.LENGTH * float3;
				Vector3f vector3f2 = matrix4f.transformProject(UI3DScene.allocVector3f().set(0.0F, 0.0F, 0.0F));
				Vector3f vector3f3 = matrix4f.transformDirection(UI3DScene.allocVector3f().set(1.0F, 0.0F, 0.0F)).normalize();
				Vector3f vector3f4 = matrix4f.transformDirection(UI3DScene.allocVector3f().set(0.0F, 1.0F, 0.0F)).normalize();
				Vector3f vector3f5 = matrix4f.transformDirection(UI3DScene.allocVector3f().set(0.0F, 0.0F, 1.0F)).normalize();
				GL11.glClear(256);
				GL11.glEnable(2929);
				UI3DScene.Axis axis = this.m_trackAxis == UI3DScene.Axis.None ? stateData.m_gizmoAxis : this.m_trackAxis;
				float float5;
				float float6;
				float float7;
				if (this.m_trackAxis == UI3DScene.Axis.None || this.m_trackAxis == UI3DScene.Axis.X) {
					float5 = axis == UI3DScene.Axis.X ? 1.0F : 0.5F;
					float6 = 0.0F;
					float7 = 0.0F;
					this.renderAxis(vector3f2, float4, vector3f4, vector3f5, float5, float6, float7, ray);
				}

				if (this.m_trackAxis == UI3DScene.Axis.None || this.m_trackAxis == UI3DScene.Axis.Y) {
					float5 = 0.0F;
					float6 = axis == UI3DScene.Axis.Y ? 1.0F : 0.5F;
					float7 = 0.0F;
					this.renderAxis(vector3f2, float4, vector3f3, vector3f5, float5, float6, float7, ray);
				}

				if (this.m_trackAxis == UI3DScene.Axis.None || this.m_trackAxis == UI3DScene.Axis.Z) {
					float5 = 0.0F;
					float6 = 0.0F;
					float7 = axis == UI3DScene.Axis.Z ? 1.0F : 0.5F;
					this.renderAxis(vector3f2, float4, vector3f3, vector3f4, float5, float6, float7, ray);
				}

				UI3DScene.releaseVector3f(vector3f2);
				UI3DScene.releaseVector3f(vector3f3);
				UI3DScene.releaseVector3f(vector3f4);
				UI3DScene.releaseVector3f(vector3f5);
				UI3DScene.releaseRay(ray);
				UI3DScene.releaseMatrix4f(matrix4f);
				GL11.glColor3f(1.0F, 1.0F, 1.0F);
				this.renderLineToOrigin();
			}
		}

		void getCircleSegments(Vector3f vector3f, float float1, Vector3f vector3f2, Vector3f vector3f3, ArrayList arrayList) {
			Vector3f vector3f4 = UI3DScene.allocVector3f();
			Vector3f vector3f5 = UI3DScene.allocVector3f();
			byte byte1 = 32;
			double double1 = 0.0 / (double)byte1 * 0.01745329238474369;
			double double2 = Math.cos(double1);
			double double3 = Math.sin(double1);
			vector3f2.mul((float)double2, vector3f4);
			vector3f3.mul((float)double3, vector3f5);
			vector3f4.add(vector3f5).mul(float1);
			arrayList.add(UI3DScene.allocVector3f().set((Vector3fc)vector3f).add(vector3f4));
			for (int int1 = 1; int1 <= byte1; ++int1) {
				double1 = (double)int1 * 360.0 / (double)byte1 * 0.01745329238474369;
				double2 = Math.cos(double1);
				double3 = Math.sin(double1);
				vector3f2.mul((float)double2, vector3f4);
				vector3f3.mul((float)double3, vector3f5);
				vector3f4.add(vector3f5).mul(float1);
				arrayList.add(UI3DScene.allocVector3f().set((Vector3fc)vector3f).add(vector3f4));
			}

			UI3DScene.releaseVector3f(vector3f4);
			UI3DScene.releaseVector3f(vector3f5);
		}

		private float hitTestCircle(UI3DScene.Ray ray, ArrayList arrayList, Vector2 vector2) {
			UI3DScene.Ray ray2 = UI3DScene.allocRay();
			Vector3f vector3f = UI3DScene.allocVector3f();
			float float1 = UI3DScene.this.sceneToUIX(ray.origin.x, ray.origin.y, ray.origin.z);
			float float2 = UI3DScene.this.sceneToUIY(ray.origin.x, ray.origin.y, ray.origin.z);
			float float3 = Float.MAX_VALUE;
			Vector3f vector3f2 = (Vector3f)arrayList.get(0);
			for (int int1 = 1; int1 < arrayList.size(); ++int1) {
				Vector3f vector3f3 = (Vector3f)arrayList.get(int1);
				float float4 = UI3DScene.this.sceneToUIX(vector3f2.x, vector3f2.y, vector3f2.z);
				float float5 = UI3DScene.this.sceneToUIY(vector3f2.x, vector3f2.y, vector3f2.z);
				float float6 = UI3DScene.this.sceneToUIX(vector3f3.x, vector3f3.y, vector3f3.z);
				float float7 = UI3DScene.this.sceneToUIY(vector3f3.x, vector3f3.y, vector3f3.z);
				double double1 = Math.pow((double)(float6 - float4), 2.0) + Math.pow((double)(float7 - float5), 2.0);
				if (double1 < 0.001) {
					vector3f2 = vector3f3;
				} else {
					double double2 = (double)((float1 - float4) * (float6 - float4) + (float2 - float5) * (float7 - float5)) / double1;
					double double3 = (double)float4 + double2 * (double)(float6 - float4);
					double double4 = (double)float5 + double2 * (double)(float7 - float5);
					if (double2 <= 0.0) {
						double3 = (double)float4;
						double4 = (double)float5;
					} else if (double2 >= 1.0) {
						double3 = (double)float6;
						double4 = (double)float7;
					}

					float float8 = IsoUtils.DistanceTo2D(float1, float2, (float)double3, (float)double4);
					if (float8 < float3) {
						float3 = float8;
						vector2.set((float)double3, (float)double4);
					}

					vector3f2 = vector3f3;
				}
			}

			UI3DScene.releaseVector3f(vector3f);
			UI3DScene.releaseRay(ray2);
			return float3;
		}

		void renderAxis(Vector3f vector3f, float float1, Vector3f vector3f2, Vector3f vector3f3, float float2, float float3, float float4, UI3DScene.Ray ray) {
			UI3DScene.vboLines.flush();
			UI3DScene.vboLines.setLineWidth(6.0F);
			this.getCircleSegments(vector3f, float1, vector3f2, vector3f3, this.m_circlePointsRender);
			Vector3f vector3f4 = UI3DScene.allocVector3f();
			Vector3f vector3f5 = (Vector3f)this.m_circlePointsRender.get(0);
			for (int int1 = 1; int1 < this.m_circlePointsRender.size(); ++int1) {
				Vector3f vector3f6 = (Vector3f)this.m_circlePointsRender.get(int1);
				vector3f4.set(vector3f6.x - vector3f.x, vector3f6.y - vector3f.y, vector3f6.z - vector3f.z).normalize();
				float float5 = vector3f4.dot(ray.direction);
				if (float5 < 0.1F) {
					UI3DScene.vboLines.addLine(vector3f5.x, vector3f5.y, vector3f5.z, vector3f6.x, vector3f6.y, vector3f6.z, float2, float3, float4, 1.0F);
				} else {
					UI3DScene.vboLines.addLine(vector3f5.x, vector3f5.y, vector3f5.z, vector3f6.x, vector3f6.y, vector3f6.z, float2 / 2.0F, float3 / 2.0F, float4 / 2.0F, 0.25F);
				}

				vector3f5 = vector3f6;
			}

			((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(this.m_circlePointsRender);
			this.m_circlePointsRender.clear();
			UI3DScene.releaseVector3f(vector3f4);
			UI3DScene.vboLines.flush();
		}

		Vector3f getPointOnAxis(float float1, float float2, UI3DScene.Axis axis, UI3DScene.Circle circle, Matrix4f matrix4f, Vector3f vector3f) {
			float float3 = 1.0F;
			circle.radius = this.LENGTH * float3;
			matrix4f.getTranslation(circle.center);
			float float4 = UI3DScene.this.sceneToUIX(circle.center.x, circle.center.y, circle.center.z);
			float float5 = UI3DScene.this.sceneToUIY(circle.center.x, circle.center.y, circle.center.z);
			circle.center.set(float4, float5, 0.0F);
			circle.orientation.set(0.0F, 0.0F, 1.0F);
			UI3DScene.Ray ray = UI3DScene.allocRay();
			ray.origin.set(float1, float2, 0.0F);
			ray.direction.set(0.0F, 0.0F, -1.0F);
			UI3DScene.this.closest_distance_line_circle(ray, circle, vector3f);
			UI3DScene.releaseRay(ray);
			return vector3f;
		}

		float calculateRotation(Vector3f vector3f, Vector3f vector3f2, UI3DScene.Circle circle) {
			if (vector3f.equals(vector3f2)) {
				return 0.0F;
			} else {
				Vector3f vector3f3 = UI3DScene.allocVector3f().set((Vector3fc)vector3f).sub(circle.center).normalize();
				Vector3f vector3f4 = UI3DScene.allocVector3f().set((Vector3fc)vector3f2).sub(circle.center).normalize();
				float float1 = (float)Math.acos((double)vector3f4.dot(vector3f3));
				Vector3f vector3f5 = vector3f3.cross(vector3f4, UI3DScene.allocVector3f());
				int int1 = (int)Math.signum(vector3f5.dot(circle.orientation));
				UI3DScene.releaseVector3f(vector3f3);
				UI3DScene.releaseVector3f(vector3f4);
				UI3DScene.releaseVector3f(vector3f5);
				return (float)int1 * float1 * 57.295776F;
			}
		}
	}

	private final class ScaleGizmo extends UI3DScene.Gizmo {
		final Matrix4f m_startXfrm = new Matrix4f();
		final Matrix4f m_startInvXfrm = new Matrix4f();
		final Vector3f m_startPos = new Vector3f();
		final Vector3f m_currentPos = new Vector3f();
		UI3DScene.Axis m_trackAxis;
		boolean m_hideX;
		boolean m_hideY;
		boolean m_hideZ;
		final Cylinder cylinder;

		private ScaleGizmo() {
			super();
			this.m_trackAxis = UI3DScene.Axis.None;
			this.cylinder = new Cylinder();
		}

		UI3DScene.Axis hitTest(float float1, float float2) {
			if (!this.m_visible) {
				return UI3DScene.Axis.None;
			} else {
				UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
				Matrix4f matrix4f = UI3DScene.allocMatrix4f();
				matrix4f.set((Matrix4fc)stateData.m_gizmoParentTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoOriginTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoTransform);
				if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
					matrix4f.setRotationXYZ(0.0F, 0.0F, 0.0F);
				}

				float2 = (float)UI3DScene.this.screenHeight() - float2;
				UI3DScene.Ray ray = UI3DScene.this.getCameraRay(float1, float2, UI3DScene.allocRay());
				UI3DScene.Ray ray2 = UI3DScene.allocRay();
				matrix4f.transformProject(ray2.origin.set(0.0F, 0.0F, 0.0F));
				float float3 = UI3DScene.this.m_gizmoScale / stateData.zoomMult() * 1000.0F;
				float float4 = this.LENGTH * float3;
				float float5 = this.THICKNESS * float3;
				float float6 = 0.1F * float3;
				matrix4f.transformDirection(ray2.direction.set(1.0F, 0.0F, 0.0F)).normalize();
				float float7 = UI3DScene.this.closest_distance_between_lines(ray2, ray);
				float float8 = ray2.t;
				float float9 = ray.t;
				if (float8 < float6 || float8 >= float6 + float4) {
					float8 = Float.MAX_VALUE;
					float7 = Float.MAX_VALUE;
				}

				float float10 = ray2.direction.dot(ray.direction);
				this.m_hideX = Math.abs(float10) > 0.9F;
				matrix4f.transformDirection(ray2.direction.set(0.0F, 1.0F, 0.0F)).normalize();
				float float11 = UI3DScene.this.closest_distance_between_lines(ray2, ray);
				float float12 = ray2.t;
				float float13 = ray.t;
				if (float12 < float6 || float12 >= float6 + float4) {
					float12 = Float.MAX_VALUE;
					float11 = Float.MAX_VALUE;
				}

				float float14 = ray2.direction.dot(ray.direction);
				this.m_hideY = Math.abs(float14) > 0.9F;
				matrix4f.transformDirection(ray2.direction.set(0.0F, 0.0F, 1.0F)).normalize();
				float float15 = UI3DScene.this.closest_distance_between_lines(ray2, ray);
				float float16 = ray2.t;
				float float17 = ray.t;
				if (float16 < float6 || float16 >= float6 + float4) {
					float16 = Float.MAX_VALUE;
					float15 = Float.MAX_VALUE;
				}

				float float18 = ray2.direction.dot(ray.direction);
				this.m_hideZ = Math.abs(float18) > 0.9F;
				UI3DScene.releaseRay(ray2);
				UI3DScene.releaseRay(ray);
				UI3DScene.releaseMatrix4f(matrix4f);
				if (float8 >= float6 && float8 < float6 + float4 && float7 < float11 && float7 < float15) {
					return float7 <= float5 / 2.0F ? UI3DScene.Axis.X : UI3DScene.Axis.None;
				} else if (float12 >= float6 && float12 < float6 + float4 && float11 < float7 && float11 < float15) {
					return float11 <= float5 / 2.0F ? UI3DScene.Axis.Y : UI3DScene.Axis.None;
				} else if (float16 >= float6 && float16 < float6 + float4 && float15 < float7 && float15 < float11) {
					return float15 <= float5 / 2.0F ? UI3DScene.Axis.Z : UI3DScene.Axis.None;
				} else {
					return UI3DScene.Axis.None;
				}
			}
		}

		void startTracking(float float1, float float2, UI3DScene.Axis axis) {
			UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
			this.m_startXfrm.set((Matrix4fc)stateData.m_gizmoParentTransform);
			this.m_startXfrm.mul((Matrix4fc)stateData.m_gizmoOriginTransform);
			this.m_startXfrm.mul((Matrix4fc)stateData.m_gizmoTransform);
			this.m_startXfrm.setRotationXYZ(0.0F, 0.0F, 0.0F);
			this.m_startInvXfrm.set((Matrix4fc)this.m_startXfrm);
			this.m_startInvXfrm.invert();
			this.m_trackAxis = axis;
			this.getPointOnAxis(float1, float2, axis, this.m_startXfrm, this.m_startPos);
		}

		void updateTracking(float float1, float float2) {
			Vector3f vector3f = this.getPointOnAxis(float1, float2, this.m_trackAxis, this.m_startXfrm, UI3DScene.allocVector3f());
			if (this.m_currentPos.equals(vector3f)) {
				UI3DScene.releaseVector3f(vector3f);
			} else {
				UI3DScene.releaseVector3f(vector3f);
				this.m_currentPos.set((Vector3fc)vector3f);
				UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
				Vector3f vector3f2 = (new Vector3f(this.m_currentPos)).sub(this.m_startPos);
				Vector3f vector3f3;
				Vector3f vector3f4;
				if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
					vector3f3 = this.m_startInvXfrm.transformPosition(this.m_startPos, new Vector3f());
					vector3f4 = this.m_startInvXfrm.transformPosition(this.m_currentPos, new Vector3f());
					Matrix4f matrix4f = (new Matrix4f(stateData.m_gizmoParentTransform)).invert();
					matrix4f.transformPosition(vector3f3);
					matrix4f.transformPosition(vector3f4);
					vector3f2.set((Vector3fc)vector3f4).sub(vector3f3);
				} else {
					vector3f3 = this.m_startInvXfrm.transformPosition(this.m_startPos, new Vector3f());
					vector3f4 = this.m_startInvXfrm.transformPosition(this.m_currentPos, new Vector3f());
					vector3f2.set((Vector3fc)vector3f4).sub(vector3f3);
				}

				vector3f2.x = (float)Math.floor((double)(vector3f2.x * UI3DScene.this.gridMult())) / UI3DScene.this.gridMult();
				vector3f2.y = (float)Math.floor((double)(vector3f2.y * UI3DScene.this.gridMult())) / UI3DScene.this.gridMult();
				vector3f2.z = (float)Math.floor((double)(vector3f2.z * UI3DScene.this.gridMult())) / UI3DScene.this.gridMult();
				LuaManager.caller.pcall(UIManager.getDefaultThread(), UI3DScene.this.getTable().rawget("onGizmoChanged"), UI3DScene.this.table, vector3f2);
			}
		}

		void stopTracking() {
			this.m_trackAxis = UI3DScene.Axis.None;
		}

		void render() {
			if (this.m_visible) {
				UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
				float float1 = UI3DScene.this.m_gizmoScale / stateData.zoomMult() * 1000.0F;
				float float2 = this.LENGTH * float1;
				float float3 = this.THICKNESS * float1;
				float float4 = 0.1F * float1;
				Matrix4f matrix4f = UI3DScene.allocMatrix4f();
				matrix4f.set((Matrix4fc)stateData.m_gizmoParentTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoOriginTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoChildTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoTransform);
				if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
					matrix4f.setRotationXYZ(0.0F, 0.0F, 0.0F);
				}

				stateData.m_modelView.mul((Matrix4fc)matrix4f, matrix4f);
				PZGLUtil.pushAndLoadMatrix(5888, matrix4f);
				UI3DScene.releaseMatrix4f(matrix4f);
				if (!this.m_hideX) {
					GL11.glColor3f(stateData.m_gizmoAxis == UI3DScene.Axis.X ? 1.0F : 0.5F, 0.0F, 0.0F);
					GL11.glRotated(90.0, 0.0, 1.0, 0.0);
					GL11.glTranslatef(0.0F, 0.0F, float4);
					this.cylinder.draw(float3 / 2.0F, float3 / 2.0F, float2, 8, 1);
					GL11.glTranslatef(0.0F, 0.0F, float2);
					this.cylinder.draw(float3, float3, 0.1F * float1, 8, 1);
					GL11.glTranslatef(0.0F, 0.0F, -float4 - float2);
					GL11.glRotated(-90.0, 0.0, 1.0, 0.0);
				}

				if (!this.m_hideY) {
					GL11.glColor3f(0.0F, stateData.m_gizmoAxis == UI3DScene.Axis.Y ? 1.0F : 0.5F, 0.0F);
					GL11.glRotated(-90.0, 1.0, 0.0, 0.0);
					GL11.glTranslatef(0.0F, 0.0F, float4);
					this.cylinder.draw(float3 / 2.0F, float3 / 2.0F, float2, 8, 1);
					GL11.glTranslatef(0.0F, 0.0F, float2);
					this.cylinder.draw(float3, float3, 0.1F * float1, 8, 1);
					GL11.glTranslatef(0.0F, 0.0F, -float4 - float2);
					GL11.glRotated(90.0, 1.0, 0.0, 0.0);
				}

				if (!this.m_hideZ) {
					GL11.glColor3f(0.0F, 0.0F, stateData.m_gizmoAxis == UI3DScene.Axis.Z ? 1.0F : 0.5F);
					GL11.glTranslatef(0.0F, 0.0F, float4);
					this.cylinder.draw(float3 / 2.0F, float3 / 2.0F, float2, 8, 1);
					GL11.glTranslatef(0.0F, 0.0F, float2);
					this.cylinder.draw(float3, float3, 0.1F * float1, 8, 1);
					GL11.glTranslatef(0.0F, 0.0F, -0.1F - float2);
				}

				GL11.glColor3f(1.0F, 1.0F, 1.0F);
				PZGLUtil.popMatrix(5888);
				this.renderLineToOrigin();
			}
		}
	}

	private final class TranslateGizmo extends UI3DScene.Gizmo {
		final Matrix4f m_startXfrm = new Matrix4f();
		final Matrix4f m_startInvXfrm = new Matrix4f();
		final Vector3f m_startPos = new Vector3f();
		final Vector3f m_currentPos = new Vector3f();
		UI3DScene.Axis m_trackAxis;
		Cylinder cylinder;

		private TranslateGizmo() {
			super();
			this.m_trackAxis = UI3DScene.Axis.None;
			this.cylinder = new Cylinder();
		}

		UI3DScene.Axis hitTest(float float1, float float2) {
			if (!this.m_visible) {
				return UI3DScene.Axis.None;
			} else {
				UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
				Matrix4f matrix4f = UI3DScene.allocMatrix4f();
				matrix4f.set((Matrix4fc)stateData.m_gizmoParentTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoOriginTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoChildTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoTransform);
				if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
					matrix4f.setRotationXYZ(0.0F, 0.0F, 0.0F);
				}

				float2 = (float)UI3DScene.this.screenHeight() - float2;
				UI3DScene.Ray ray = UI3DScene.this.getCameraRay(float1, float2, UI3DScene.allocRay());
				UI3DScene.Ray ray2 = UI3DScene.allocRay();
				matrix4f.transformPosition(ray2.origin.set(0.0F, 0.0F, 0.0F));
				float float3 = UI3DScene.this.m_gizmoScale / stateData.zoomMult() * 1000.0F;
				float float4 = this.LENGTH * float3;
				float float5 = this.THICKNESS * float3;
				float float6 = 0.1F * float3;
				matrix4f.transformDirection(ray2.direction.set(1.0F, 0.0F, 0.0F)).normalize();
				float float7 = UI3DScene.this.closest_distance_between_lines(ray2, ray);
				float float8 = ray2.t;
				float float9 = ray.t;
				if (float8 < float6 || float8 >= float6 + float4) {
					float8 = Float.MAX_VALUE;
					float7 = Float.MAX_VALUE;
				}

				float float10 = ray2.direction.dot(ray.direction);
				stateData.m_translateGizmoRenderData.m_hideX = Math.abs(float10) > 0.9F;
				matrix4f.transformDirection(ray2.direction.set(0.0F, 1.0F, 0.0F)).normalize();
				float float11 = UI3DScene.this.closest_distance_between_lines(ray2, ray);
				float float12 = ray2.t;
				float float13 = ray.t;
				if (float12 < float6 || float12 >= float6 + float4) {
					float12 = Float.MAX_VALUE;
					float11 = Float.MAX_VALUE;
				}

				float float14 = ray2.direction.dot(ray.direction);
				stateData.m_translateGizmoRenderData.m_hideY = Math.abs(float14) > 0.9F;
				matrix4f.transformDirection(ray2.direction.set(0.0F, 0.0F, 1.0F)).normalize();
				float float15 = UI3DScene.this.closest_distance_between_lines(ray2, ray);
				float float16 = ray2.t;
				float float17 = ray.t;
				if (float16 < float6 || float16 >= float6 + float4) {
					float16 = Float.MAX_VALUE;
					float15 = Float.MAX_VALUE;
				}

				float float18 = ray2.direction.dot(ray.direction);
				stateData.m_translateGizmoRenderData.m_hideZ = Math.abs(float18) > 0.9F;
				UI3DScene.releaseRay(ray2);
				UI3DScene.releaseRay(ray);
				UI3DScene.releaseMatrix4f(matrix4f);
				if (float8 >= float6 && float8 < float6 + float4 && float7 < float11 && float7 < float15) {
					return float7 <= float5 / 2.0F ? UI3DScene.Axis.X : UI3DScene.Axis.None;
				} else if (float12 >= float6 && float12 < float6 + float4 && float11 < float7 && float11 < float15) {
					return float11 <= float5 / 2.0F ? UI3DScene.Axis.Y : UI3DScene.Axis.None;
				} else if (float16 >= float6 && float16 < float6 + float4 && float15 < float7 && float15 < float11) {
					return float15 <= float5 / 2.0F ? UI3DScene.Axis.Z : UI3DScene.Axis.None;
				} else {
					return UI3DScene.Axis.None;
				}
			}
		}

		void startTracking(float float1, float float2, UI3DScene.Axis axis) {
			UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
			this.m_startXfrm.set((Matrix4fc)stateData.m_gizmoParentTransform);
			this.m_startXfrm.mul((Matrix4fc)stateData.m_gizmoOriginTransform);
			this.m_startXfrm.mul((Matrix4fc)stateData.m_gizmoChildTransform);
			this.m_startXfrm.mul((Matrix4fc)stateData.m_gizmoTransform);
			if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
				this.m_startXfrm.setRotationXYZ(0.0F, 0.0F, 0.0F);
			}

			this.m_startInvXfrm.set((Matrix4fc)this.m_startXfrm);
			this.m_startInvXfrm.invert();
			this.m_trackAxis = axis;
			this.getPointOnAxis(float1, float2, axis, this.m_startXfrm, this.m_startPos);
		}

		void updateTracking(float float1, float float2) {
			Vector3f vector3f = this.getPointOnAxis(float1, float2, this.m_trackAxis, this.m_startXfrm, UI3DScene.allocVector3f());
			if (this.m_currentPos.equals(vector3f)) {
				UI3DScene.releaseVector3f(vector3f);
			} else {
				UI3DScene.releaseVector3f(vector3f);
				this.m_currentPos.set((Vector3fc)vector3f);
				UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
				Vector3f vector3f2 = (new Vector3f(this.m_currentPos)).sub(this.m_startPos);
				Vector3f vector3f3;
				Vector3f vector3f4;
				Matrix4f matrix4f;
				if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
					vector3f3 = this.m_startInvXfrm.transformPosition(this.m_startPos, UI3DScene.allocVector3f());
					vector3f4 = this.m_startInvXfrm.transformPosition(this.m_currentPos, UI3DScene.allocVector3f());
					matrix4f = UI3DScene.allocMatrix4f();
					matrix4f.set((Matrix4fc)stateData.m_gizmoParentTransform);
					matrix4f.mul((Matrix4fc)stateData.m_gizmoOriginTransform);
					matrix4f.mul((Matrix4fc)stateData.m_gizmoChildTransform);
					matrix4f.invert();
					matrix4f.transformPosition(vector3f3);
					matrix4f.transformPosition(vector3f4);
					UI3DScene.releaseMatrix4f(matrix4f);
					vector3f2.set((Vector3fc)vector3f4).sub(vector3f3);
					UI3DScene.releaseVector3f(vector3f3);
					UI3DScene.releaseVector3f(vector3f4);
				} else {
					vector3f3 = this.m_startInvXfrm.transformPosition(this.m_startPos, UI3DScene.allocVector3f());
					vector3f4 = this.m_startInvXfrm.transformPosition(this.m_currentPos, UI3DScene.allocVector3f());
					matrix4f = UI3DScene.allocMatrix4f();
					matrix4f.set((Matrix4fc)stateData.m_gizmoTransform);
					matrix4f.transformPosition(vector3f3);
					matrix4f.transformPosition(vector3f4);
					UI3DScene.releaseMatrix4f(matrix4f);
					vector3f2.set((Vector3fc)vector3f4).sub(vector3f3);
					UI3DScene.releaseVector3f(vector3f3);
					UI3DScene.releaseVector3f(vector3f4);
				}

				vector3f2.x = (float)Math.floor((double)(vector3f2.x * UI3DScene.this.gridMult())) / UI3DScene.this.gridMult();
				vector3f2.y = (float)Math.floor((double)(vector3f2.y * UI3DScene.this.gridMult())) / UI3DScene.this.gridMult();
				vector3f2.z = (float)Math.floor((double)(vector3f2.z * UI3DScene.this.gridMult())) / UI3DScene.this.gridMult();
				if (stateData.m_selectedAttachmentIsChildAttachment) {
					vector3f2.mul(-1.0F);
				}

				LuaManager.caller.pcall(UIManager.getDefaultThread(), UI3DScene.this.getTable().rawget("onGizmoChanged"), UI3DScene.this.table, vector3f2);
			}
		}

		void stopTracking() {
			this.m_trackAxis = UI3DScene.Axis.None;
		}

		void render() {
			if (this.m_visible) {
				UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
				Matrix4f matrix4f = UI3DScene.allocMatrix4f();
				matrix4f.set((Matrix4fc)stateData.m_gizmoParentTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoOriginTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoChildTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoTransform);
				Vector3f vector3f = matrix4f.getScale(UI3DScene.allocVector3f());
				matrix4f.scale(1.0F / vector3f.x, 1.0F / vector3f.y, 1.0F / vector3f.z);
				UI3DScene.releaseVector3f(vector3f);
				if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
					matrix4f.setRotationXYZ(0.0F, 0.0F, 0.0F);
				}

				stateData.m_modelView.mul((Matrix4fc)matrix4f, matrix4f);
				PZGLUtil.pushAndLoadMatrix(5888, matrix4f);
				UI3DScene.releaseMatrix4f(matrix4f);
				float float1 = UI3DScene.this.m_gizmoScale / stateData.zoomMult() * 1000.0F;
				float float2 = this.THICKNESS * float1;
				float float3 = this.LENGTH * float1;
				float float4 = 0.1F * float1;
				if (!stateData.m_translateGizmoRenderData.m_hideX) {
					GL11.glColor3f(stateData.m_gizmoAxis == UI3DScene.Axis.X ? 1.0F : 0.5F, 0.0F, 0.0F);
					GL11.glRotated(90.0, 0.0, 1.0, 0.0);
					GL11.glTranslatef(0.0F, 0.0F, float4);
					this.cylinder.draw(float2 / 2.0F, float2 / 2.0F, float3, 8, 1);
					GL11.glTranslatef(0.0F, 0.0F, float3);
					this.cylinder.draw(float2 / 2.0F * 2.0F, 0.0F, 0.1F * float1, 8, 1);
					GL11.glTranslatef(0.0F, 0.0F, -float4 - float3);
					GL11.glRotated(-90.0, 0.0, 1.0, 0.0);
				}

				if (!stateData.m_translateGizmoRenderData.m_hideY) {
					GL11.glColor3f(0.0F, stateData.m_gizmoAxis == UI3DScene.Axis.Y ? 1.0F : 0.5F, 0.0F);
					GL11.glRotated(-90.0, 1.0, 0.0, 0.0);
					GL11.glTranslatef(0.0F, 0.0F, float4);
					this.cylinder.draw(float2 / 2.0F, float2 / 2.0F, float3, 8, 1);
					GL11.glTranslatef(0.0F, 0.0F, float3);
					this.cylinder.draw(float2 / 2.0F * 2.0F, 0.0F, 0.1F * float1, 8, 1);
					GL11.glTranslatef(0.0F, 0.0F, -float4 - float3);
					GL11.glRotated(90.0, 1.0, 0.0, 0.0);
				}

				if (!stateData.m_translateGizmoRenderData.m_hideZ) {
					GL11.glColor3f(0.0F, 0.0F, stateData.m_gizmoAxis == UI3DScene.Axis.Z ? 1.0F : 0.5F);
					GL11.glTranslatef(0.0F, 0.0F, float4);
					this.cylinder.draw(float2 / 2.0F, float2 / 2.0F, float3, 8, 1);
					GL11.glTranslatef(0.0F, 0.0F, float3);
					this.cylinder.draw(float2 / 2.0F * 2.0F, 0.0F, 0.1F * float1, 8, 1);
					GL11.glTranslatef(0.0F, 0.0F, -float4 - float3);
				}

				GL11.glColor3f(1.0F, 1.0F, 1.0F);
				PZGLUtil.popMatrix(5888);
				this.renderLineToOrigin();
			}
		}
	}

	private abstract static class SceneObject {
		final UI3DScene m_scene;
		final String m_id;
		boolean m_visible = true;
		final Vector3f m_translate = new Vector3f();
		final Vector3f m_rotate = new Vector3f();
		UI3DScene.SceneObject m_parent;
		String m_attachment;
		String m_parentAttachment;
		boolean m_autoRotate = false;
		float m_autoRotateAngle = 0.0F;

		SceneObject(UI3DScene uI3DScene, String string) {
			this.m_scene = uI3DScene;
			this.m_id = string;
		}

		abstract UI3DScene.SceneObjectRenderData renderMain();

		Matrix4f getLocalTransform(Matrix4f matrix4f) {
			UI3DScene.SceneModel sceneModel = (UI3DScene.SceneModel)Type.tryCastTo(this, UI3DScene.SceneModel.class);
			if (sceneModel != null && sceneModel.m_useWorldAttachment) {
				matrix4f.translation(-this.m_translate.x, this.m_translate.y, this.m_translate.z);
				matrix4f.scale(-1.5F, 1.5F, 1.5F);
			} else {
				matrix4f.translation(this.m_translate);
			}

			float float1 = this.m_rotate.y;
			if (this.m_autoRotate) {
				float1 += this.m_autoRotateAngle;
			}

			matrix4f.rotateXYZ(this.m_rotate.x * 0.017453292F, float1 * 0.017453292F, this.m_rotate.z * 0.017453292F);
			if (this.m_attachment != null) {
				Matrix4f matrix4f2 = this.getAttachmentTransform(this.m_attachment, UI3DScene.allocMatrix4f());
				matrix4f2.invert();
				matrix4f.mul((Matrix4fc)matrix4f2);
				UI3DScene.releaseMatrix4f(matrix4f2);
			}

			return matrix4f;
		}

		Matrix4f getGlobalTransform(Matrix4f matrix4f) {
			this.getLocalTransform(matrix4f);
			if (this.m_parent != null) {
				Matrix4f matrix4f2;
				if (this.m_parentAttachment != null) {
					matrix4f2 = this.m_parent.getAttachmentTransform(this.m_parentAttachment, UI3DScene.allocMatrix4f());
					matrix4f2.mul((Matrix4fc)matrix4f, matrix4f);
					UI3DScene.releaseMatrix4f(matrix4f2);
				}

				matrix4f2 = this.m_parent.getGlobalTransform(UI3DScene.allocMatrix4f());
				matrix4f2.mul((Matrix4fc)matrix4f, matrix4f);
				UI3DScene.releaseMatrix4f(matrix4f2);
			}

			return matrix4f;
		}

		Matrix4f getAttachmentTransform(String string, Matrix4f matrix4f) {
			matrix4f.identity();
			return matrix4f;
		}
	}

	private static final class OriginAttachment extends UI3DScene.SceneObject {
		UI3DScene.SceneObject m_object;
		String m_attachmentName;

		OriginAttachment(UI3DScene uI3DScene) {
			super(uI3DScene, "OriginAttachment");
		}

		UI3DScene.SceneObjectRenderData renderMain() {
			return null;
		}

		Matrix4f getGlobalTransform(Matrix4f matrix4f) {
			return this.m_object.getAttachmentTransform(this.m_attachmentName, matrix4f);
		}
	}

	private static final class OriginBone extends UI3DScene.SceneObject {
		UI3DScene.SceneCharacter m_character;
		String m_boneName;

		OriginBone(UI3DScene uI3DScene) {
			super(uI3DScene, "OriginBone");
		}

		UI3DScene.SceneObjectRenderData renderMain() {
			return null;
		}

		Matrix4f getGlobalTransform(Matrix4f matrix4f) {
			return this.m_character.getBoneMatrix(this.m_boneName, matrix4f);
		}
	}

	private static final class OriginGizmo extends UI3DScene.SceneObject {

		OriginGizmo(UI3DScene uI3DScene) {
			super(uI3DScene, "OriginGizmo");
		}

		UI3DScene.SceneObjectRenderData renderMain() {
			return null;
		}
	}

	private final class GridPlaneDrawer extends TextureDraw.GenericDrawer {
		final UI3DScene m_scene;

		GridPlaneDrawer(UI3DScene uI3DScene) {
			this.m_scene = uI3DScene;
		}

		public void render() {
			UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
			PZGLUtil.pushAndLoadMatrix(5889, stateData.m_projection);
			PZGLUtil.pushAndLoadMatrix(5888, stateData.m_modelView);
			GL11.glPushAttrib(2048);
			GL11.glViewport(UI3DScene.this.getAbsoluteX().intValue(), Core.getInstance().getScreenHeight() - UI3DScene.this.getAbsoluteY().intValue() - UI3DScene.this.getHeight().intValue(), UI3DScene.this.getWidth().intValue(), UI3DScene.this.getHeight().intValue());
			Objects.requireNonNull(this.m_scene);
			float float1 = 5.0F;
			UI3DScene.vboLines.setMode(4);
			UI3DScene.vboLines.setDepthTest(true);
			if (this.m_scene.m_gridPlane == UI3DScene.GridPlane.XZ) {
				UI3DScene.vboLines.addTriangle(-float1, 0.0F, -float1, float1, 0.0F, -float1, -float1, 0.0F, float1, 0.5F, 0.5F, 0.5F, 1.0F);
				UI3DScene.vboLines.addTriangle(float1, 0.0F, float1, -float1, 0.0F, float1, float1, 0.0F, -float1, 0.5F, 0.5F, 0.5F, 1.0F);
			}

			UI3DScene.vboLines.setMode(1);
			UI3DScene.vboLines.setDepthTest(false);
			GL11.glPopAttrib();
			PZGLUtil.popMatrix(5889);
			PZGLUtil.popMatrix(5888);
		}
	}

	private final class OverlaysDrawer extends TextureDraw.GenericDrawer {

		void init() {
			UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
			UI3DScene.s_aabbPool.release((List)stateData.m_aabb);
			stateData.m_aabb.clear();
			int int1;
			for (int1 = 0; int1 < UI3DScene.this.m_aabb.size(); ++int1) {
				UI3DScene.AABB aABB = (UI3DScene.AABB)UI3DScene.this.m_aabb.get(int1);
				stateData.m_aabb.add(((UI3DScene.AABB)UI3DScene.s_aabbPool.alloc()).set(aABB));
			}

			UI3DScene.s_box3DPool.release((List)stateData.m_box3D);
			stateData.m_box3D.clear();
			for (int1 = 0; int1 < UI3DScene.this.m_box3D.size(); ++int1) {
				UI3DScene.Box3D box3D = (UI3DScene.Box3D)UI3DScene.this.m_box3D.get(int1);
				stateData.m_box3D.add(((UI3DScene.Box3D)UI3DScene.s_box3DPool.alloc()).set(box3D));
			}

			UI3DScene.s_posRotPool.release((List)stateData.m_axes);
			stateData.m_axes.clear();
			for (int1 = 0; int1 < UI3DScene.this.m_axes.size(); ++int1) {
				UI3DScene.PositionRotation positionRotation = (UI3DScene.PositionRotation)UI3DScene.this.m_axes.get(int1);
				stateData.m_axes.add(((UI3DScene.PositionRotation)UI3DScene.s_posRotPool.alloc()).set(positionRotation));
			}
		}

		public void render() {
			UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
			PZGLUtil.pushAndLoadMatrix(5889, stateData.m_projection);
			PZGLUtil.pushAndLoadMatrix(5888, stateData.m_modelView);
			GL11.glPushAttrib(2048);
			GL11.glViewport(UI3DScene.this.getAbsoluteX().intValue(), Core.getInstance().getScreenHeight() - UI3DScene.this.getAbsoluteY().intValue() - UI3DScene.this.getHeight().intValue(), UI3DScene.this.getWidth().intValue(), UI3DScene.this.getHeight().intValue());
			UI3DScene.vboLines.setOffset(0.0F, 0.0F, 0.0F);
			if (UI3DScene.this.m_bDrawGrid) {
				UI3DScene.this.renderGrid();
			}

			int int1;
			for (int1 = 0; int1 < stateData.m_aabb.size(); ++int1) {
				UI3DScene.AABB aABB = (UI3DScene.AABB)stateData.m_aabb.get(int1);
				UI3DScene.this.renderAABB(aABB.x, aABB.y, aABB.z, aABB.w, aABB.h, aABB.L, aABB.r, aABB.g, aABB.b);
			}

			for (int1 = 0; int1 < stateData.m_box3D.size(); ++int1) {
				UI3DScene.Box3D box3D = (UI3DScene.Box3D)stateData.m_box3D.get(int1);
				UI3DScene.this.renderBox3D(box3D.x, box3D.y, box3D.z, box3D.w, box3D.h, box3D.L, box3D.rx, box3D.ry, box3D.rz, box3D.r, box3D.g, box3D.b);
			}

			for (int1 = 0; int1 < stateData.m_axes.size(); ++int1) {
				UI3DScene.this.renderAxis((UI3DScene.PositionRotation)stateData.m_axes.get(int1));
			}

			UI3DScene.vboLines.flush();
			if (stateData.m_gizmo != null) {
				stateData.m_gizmo.render();
			}

			UI3DScene.vboLines.flush();
			GL11.glPopAttrib();
			PZGLUtil.popMatrix(5889);
			PZGLUtil.popMatrix(5888);
		}
	}

	private static class SceneObjectRenderData {
		UI3DScene.SceneObject m_object;
		final Matrix4f m_transform = new Matrix4f();
		private static final ObjectPool s_pool = new ObjectPool(UI3DScene.SceneObjectRenderData::new);

		UI3DScene.SceneObjectRenderData init(UI3DScene.SceneObject sceneObject) {
			this.m_object = sceneObject;
			sceneObject.getGlobalTransform(this.m_transform);
			return this;
		}

		void release() {
			s_pool.release((Object)this);
		}
	}

	private abstract class Gizmo {
		float LENGTH = 0.5F;
		float THICKNESS = 0.05F;
		boolean m_visible = false;

		abstract UI3DScene.Axis hitTest(float float1, float float2);

		abstract void startTracking(float float1, float float2, UI3DScene.Axis axis);

		abstract void updateTracking(float float1, float float2);

		abstract void stopTracking();

		abstract void render();

		Vector3f getPointOnAxis(float float1, float float2, UI3DScene.Axis axis, Matrix4f matrix4f, Vector3f vector3f) {
			UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
			float2 = (float)UI3DScene.this.screenHeight() - float2;
			UI3DScene.Ray ray = UI3DScene.this.getCameraRay(float1, float2, UI3DScene.allocRay());
			UI3DScene.Ray ray2 = UI3DScene.allocRay();
			matrix4f.transformPosition(ray2.origin.set(0.0F, 0.0F, 0.0F));
			switch (axis) {
			case X: 
				ray2.direction.set(1.0F, 0.0F, 0.0F);
				break;
			
			case Y: 
				ray2.direction.set(0.0F, 1.0F, 0.0F);
				break;
			
			case Z: 
				ray2.direction.set(0.0F, 0.0F, 1.0F);
			
			}
			matrix4f.transformDirection(ray2.direction).normalize();
			UI3DScene.this.closest_distance_between_lines(ray2, ray);
			UI3DScene.releaseRay(ray);
			vector3f.set((Vector3fc)ray2.direction).mul(ray2.t).add(ray2.origin);
			UI3DScene.releaseRay(ray2);
			return vector3f;
		}

		boolean hitTestRect(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
			float float9 = UI3DScene.this.sceneToUIX(float3, float4, float5);
			float float10 = UI3DScene.this.sceneToUIY(float3, float4, float5);
			float float11 = UI3DScene.this.sceneToUIX(float6, float7, float8);
			float float12 = UI3DScene.this.sceneToUIY(float6, float7, float8);
			float float13 = this.THICKNESS / 2.0F * UI3DScene.this.zoomMult();
			float float14 = this.THICKNESS / 2.0F * UI3DScene.this.zoomMult();
			float float15 = Math.min(float9 - float13, float11 - float13);
			float float16 = Math.max(float9 + float13, float11 + float13);
			float float17 = Math.min(float10 - float14, float12 - float14);
			float float18 = Math.max(float10 + float14, float12 + float14);
			return float1 >= float15 && float2 >= float17 && float1 < float16 && float2 < float18;
		}

		void renderLineToOrigin() {
			UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
			if (stateData.m_hasGizmoOrigin) {
				UI3DScene.this.renderAxis(stateData.m_gizmoTranslate, stateData.m_gizmoRotate);
				Vector3f vector3f = stateData.m_gizmoTranslate;
				UI3DScene.vboLines.flush();
				Matrix4f matrix4f = UI3DScene.allocMatrix4f();
				matrix4f.set((Matrix4fc)stateData.m_modelView);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoParentTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoOriginTransform);
				matrix4f.mul((Matrix4fc)stateData.m_gizmoChildTransform);
				PZGLUtil.pushAndLoadMatrix(5888, matrix4f);
				UI3DScene.releaseMatrix4f(matrix4f);
				UI3DScene.vboLines.setLineWidth(1.0F);
				UI3DScene.vboLines.addLine(vector3f.x, vector3f.y, vector3f.z, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
				UI3DScene.vboLines.flush();
				PZGLUtil.popMatrix(5888);
			}
		}
	}

	static enum Axis {

		None,
		X,
		Y,
		Z;

		private static UI3DScene.Axis[] $values() {
			return new UI3DScene.Axis[]{None, X, Y, Z};
		}
	}

	public static final class Plane {
		public final Vector3f point = new Vector3f();
		public final Vector3f normal = new Vector3f();

		public Plane() {
		}

		public Plane(Vector3f vector3f, Vector3f vector3f2) {
			this.point.set((Vector3fc)vector3f2);
			this.normal.set((Vector3fc)vector3f);
		}

		public UI3DScene.Plane set(Vector3f vector3f, Vector3f vector3f2) {
			this.point.set((Vector3fc)vector3f2);
			this.normal.set((Vector3fc)vector3f);
			return this;
		}
	}

	public static final class Ray {
		public final Vector3f origin = new Vector3f();
		public final Vector3f direction = new Vector3f();
		public float t;

		public Ray() {
		}

		Ray(UI3DScene.Ray ray) {
			this.origin.set((Vector3fc)ray.origin);
			this.direction.set((Vector3fc)ray.direction);
			this.t = ray.t;
		}
	}

	private static final class SceneCharacter extends UI3DScene.SceneObject {
		final AnimatedModel m_animatedModel = new AnimatedModel();
		boolean m_bShowBones = false;
		boolean m_bClearDepthBuffer = true;
		boolean m_bUseDeferredMovement = false;

		SceneCharacter(UI3DScene uI3DScene, String string) {
			super(uI3DScene, string);
			this.m_animatedModel.setAnimSetName("player-vehicle");
			this.m_animatedModel.setState("idle");
			this.m_animatedModel.setOutfitName("Naked", false, false);
			this.m_animatedModel.setVisual(new HumanVisual(this.m_animatedModel));
			this.m_animatedModel.getHumanVisual().setHairModel("Bald");
			this.m_animatedModel.getHumanVisual().setBeardModel("");
			this.m_animatedModel.getHumanVisual().setSkinTextureIndex(0);
			this.m_animatedModel.setAlpha(0.5F);
			this.m_animatedModel.setAnimate(false);
		}

		UI3DScene.SceneObjectRenderData renderMain() {
			this.m_animatedModel.update();
			UI3DScene.CharacterRenderData characterRenderData = (UI3DScene.CharacterRenderData)UI3DScene.CharacterRenderData.s_pool.alloc();
			characterRenderData.initCharacter(this);
			SpriteRenderer.instance.drawGeneric(characterRenderData.m_drawer);
			return characterRenderData;
		}

		Matrix4f getLocalTransform(Matrix4f matrix4f) {
			matrix4f.identity();
			matrix4f.rotateY(3.1415927F);
			matrix4f.translate(-this.m_translate.x, this.m_translate.y, this.m_translate.z);
			matrix4f.scale(-1.5F, 1.5F, 1.5F);
			float float1 = this.m_rotate.y;
			if (this.m_autoRotate) {
				float1 += this.m_autoRotateAngle;
			}

			matrix4f.rotateXYZ(this.m_rotate.x * 0.017453292F, float1 * 0.017453292F, this.m_rotate.z * 0.017453292F);
			if (this.m_animatedModel.getAnimationPlayer().getMultiTrack().getTracks().isEmpty()) {
				return matrix4f;
			} else {
				if (this.m_bUseDeferredMovement) {
					AnimationMultiTrack animationMultiTrack = this.m_animatedModel.getAnimationPlayer().getMultiTrack();
					float float2 = ((AnimationTrack)animationMultiTrack.getTracks().get(0)).getCurrentDeferredRotation();
					org.lwjgl.util.vector.Vector3f vector3f = new org.lwjgl.util.vector.Vector3f();
					((AnimationTrack)animationMultiTrack.getTracks().get(0)).getCurrentDeferredPosition(vector3f);
					matrix4f.translate(vector3f.x, vector3f.y, vector3f.z);
				}

				return matrix4f;
			}
		}

		Matrix4f getAttachmentTransform(String string, Matrix4f matrix4f) {
			matrix4f.identity();
			boolean boolean1 = this.m_animatedModel.isFemale();
			ModelScript modelScript = ScriptManager.instance.getModelScript(boolean1 ? "FemaleBody" : "MaleBody");
			if (modelScript == null) {
				return matrix4f;
			} else {
				ModelAttachment modelAttachment = modelScript.getAttachmentById(string);
				if (modelAttachment == null) {
					return matrix4f;
				} else {
					matrix4f.translation(modelAttachment.getOffset());
					Vector3f vector3f = modelAttachment.getRotate();
					matrix4f.rotateXYZ(vector3f.x * 0.017453292F, vector3f.y * 0.017453292F, vector3f.z * 0.017453292F);
					if (modelAttachment.getBone() != null) {
						Matrix4f matrix4f2 = this.getBoneMatrix(modelAttachment.getBone(), UI3DScene.allocMatrix4f());
						matrix4f2.mul((Matrix4fc)matrix4f, matrix4f);
						UI3DScene.releaseMatrix4f(matrix4f2);
					}

					return matrix4f;
				}
			}
		}

		int hitTestBone(int int1, UI3DScene.Ray ray, UI3DScene.Ray ray2, Matrix4f matrix4f) {
			AnimationPlayer animationPlayer = this.m_animatedModel.getAnimationPlayer();
			SkinningData skinningData = animationPlayer.getSkinningData();
			int int2 = (Integer)skinningData.SkeletonHierarchy.get(int1);
			if (int2 == -1) {
				return -1;
			} else {
				org.lwjgl.util.vector.Matrix4f matrix4f2 = animationPlayer.modelTransforms[int2];
				ray.origin.set(matrix4f2.m03, matrix4f2.m13, matrix4f2.m23);
				matrix4f.transformPosition(ray.origin);
				matrix4f2 = animationPlayer.modelTransforms[int1];
				Vector3f vector3f = UI3DScene.allocVector3f();
				vector3f.set(matrix4f2.m03, matrix4f2.m13, matrix4f2.m23);
				matrix4f.transformPosition(vector3f);
				ray.direction.set((Vector3fc)vector3f).sub(ray.origin);
				float float1 = ray.direction.length();
				ray.direction.normalize();
				this.m_scene.closest_distance_between_lines(ray2, ray);
				float float2 = this.m_scene.sceneToUIX(ray2.origin.x + ray2.direction.x * ray2.t, ray2.origin.y + ray2.direction.y * ray2.t, ray2.origin.z + ray2.direction.z * ray2.t);
				float float3 = this.m_scene.sceneToUIY(ray2.origin.x + ray2.direction.x * ray2.t, ray2.origin.y + ray2.direction.y * ray2.t, ray2.origin.z + ray2.direction.z * ray2.t);
				float float4 = this.m_scene.sceneToUIX(ray.origin.x + ray.direction.x * ray.t, ray.origin.y + ray.direction.y * ray.t, ray.origin.z + ray.direction.z * ray.t);
				float float5 = this.m_scene.sceneToUIY(ray.origin.x + ray.direction.x * ray.t, ray.origin.y + ray.direction.y * ray.t, ray.origin.z + ray.direction.z * ray.t);
				int int3 = -1;
				float float6 = 10.0F;
				float float7 = (float)Math.sqrt(Math.pow((double)(float4 - float2), 2.0) + Math.pow((double)(float5 - float3), 2.0));
				if (float7 < float6) {
					if (ray.t >= 0.0F && ray.t < float1 * 0.5F) {
						int3 = int2;
					} else if (ray.t >= float1 * 0.5F && ray.t < float1) {
						int3 = int1;
					}
				}

				UI3DScene.releaseVector3f(vector3f);
				return int3;
			}
		}

		String pickBone(float float1, float float2) {
			if (this.m_animatedModel.getAnimationPlayer().modelTransforms == null) {
				return "";
			} else {
				float2 = (float)this.m_scene.screenHeight() - float2;
				UI3DScene.Ray ray = this.m_scene.getCameraRay(float1, float2, UI3DScene.allocRay());
				Matrix4f matrix4f = UI3DScene.allocMatrix4f();
				this.getLocalTransform(matrix4f);
				UI3DScene.Ray ray2 = UI3DScene.allocRay();
				int int1 = -1;
				for (int int2 = 0; int2 < this.m_animatedModel.getAnimationPlayer().modelTransforms.length; ++int2) {
					int1 = this.hitTestBone(int2, ray2, ray, matrix4f);
					if (int1 != -1) {
						break;
					}
				}

				UI3DScene.releaseRay(ray2);
				UI3DScene.releaseRay(ray);
				UI3DScene.releaseMatrix4f(matrix4f);
				return int1 == -1 ? "" : this.m_animatedModel.getAnimationPlayer().getSkinningData().getBoneAt(int1).Name;
			}
		}

		Matrix4f getBoneMatrix(String string, Matrix4f matrix4f) {
			matrix4f.identity();
			if (this.m_animatedModel.getAnimationPlayer().modelTransforms == null) {
				return matrix4f;
			} else {
				SkinningBone skinningBone = this.m_animatedModel.getAnimationPlayer().getSkinningData().getBone(string);
				if (skinningBone == null) {
					return matrix4f;
				} else {
					matrix4f = PZMath.convertMatrix(this.m_animatedModel.getAnimationPlayer().modelTransforms[skinningBone.Index], matrix4f);
					matrix4f.transpose();
					return matrix4f;
				}
			}
		}

		UI3DScene.PositionRotation getBoneAxis(String string, UI3DScene.PositionRotation positionRotation) {
			Matrix4f matrix4f = UI3DScene.allocMatrix4f().identity();
			matrix4f.getTranslation(positionRotation.pos);
			UI3DScene.releaseMatrix4f(matrix4f);
			Quaternionf quaternionf = matrix4f.getUnnormalizedRotation(UI3DScene.allocQuaternionf());
			quaternionf.getEulerAnglesXYZ(positionRotation.rot);
			UI3DScene.releaseQuaternionf(quaternionf);
			return positionRotation;
		}
	}

	private static final class SceneModel extends UI3DScene.SceneObject {
		ModelScript m_modelScript;
		Model m_model;
		boolean m_useWorldAttachment = false;
		boolean m_weaponRotationHack = false;

		SceneModel(UI3DScene uI3DScene, String string, ModelScript modelScript, Model model) {
			super(uI3DScene, string);
			Objects.requireNonNull(modelScript);
			Objects.requireNonNull(model);
			this.m_modelScript = modelScript;
			this.m_model = model;
		}

		UI3DScene.SceneObjectRenderData renderMain() {
			if (!this.m_model.isReady()) {
				return null;
			} else {
				UI3DScene.ModelRenderData modelRenderData = (UI3DScene.ModelRenderData)UI3DScene.ModelRenderData.s_pool.alloc();
				modelRenderData.initModel(this);
				SpriteRenderer.instance.drawGeneric(modelRenderData.m_drawer);
				return modelRenderData;
			}
		}

		Matrix4f getLocalTransform(Matrix4f matrix4f) {
			super.getLocalTransform(matrix4f);
			return matrix4f;
		}

		Matrix4f getAttachmentTransform(String string, Matrix4f matrix4f) {
			matrix4f.identity();
			ModelAttachment modelAttachment = this.m_modelScript.getAttachmentById(string);
			if (modelAttachment == null) {
				return matrix4f;
			} else {
				matrix4f.translation(modelAttachment.getOffset());
				Vector3f vector3f = modelAttachment.getRotate();
				matrix4f.rotateXYZ(vector3f.x * 0.017453292F, vector3f.y * 0.017453292F, vector3f.z * 0.017453292F);
				return matrix4f;
			}
		}
	}

	private static final class SceneVehicle extends UI3DScene.SceneObject {
		String m_scriptName = "Base.ModernCar";
		VehicleScript m_script;
		Model m_model;

		SceneVehicle(UI3DScene uI3DScene, String string) {
			super(uI3DScene, string);
			this.setScriptName("Base.ModernCar");
		}

		UI3DScene.SceneObjectRenderData renderMain() {
			if (this.m_script == null) {
				this.m_model = null;
				return null;
			} else {
				String string = this.m_script.getModel().file;
				this.m_model = ModelManager.instance.getLoadedModel(string);
				if (this.m_model == null) {
					return null;
				} else {
					if (this.m_script.getSkinCount() > 0) {
						this.m_model.tex = Texture.getSharedTexture("media/textures/" + this.m_script.getSkin(0).texture + ".png");
					}

					UI3DScene.VehicleRenderData vehicleRenderData = (UI3DScene.VehicleRenderData)UI3DScene.VehicleRenderData.s_pool.alloc();
					vehicleRenderData.initVehicle(this);
					UI3DScene.SetModelCamera setModelCamera = (UI3DScene.SetModelCamera)UI3DScene.s_SetModelCameraPool.alloc();
					SpriteRenderer.instance.drawGeneric(setModelCamera.init(this.m_scene.m_VehicleSceneModelCamera, vehicleRenderData));
					SpriteRenderer.instance.drawGeneric(vehicleRenderData.m_drawer);
					return vehicleRenderData;
				}
			}
		}

		void setScriptName(String string) {
			this.m_scriptName = string;
			this.m_script = ScriptManager.instance.getVehicle(string);
		}
	}

	private static final class PositionRotation {
		final Vector3f pos = new Vector3f();
		final Vector3f rot = new Vector3f();

		UI3DScene.PositionRotation set(UI3DScene.PositionRotation positionRotation) {
			this.pos.set((Vector3fc)positionRotation.pos);
			this.rot.set((Vector3fc)positionRotation.rot);
			return this;
		}

		UI3DScene.PositionRotation set(float float1, float float2, float float3) {
			this.pos.set(float1, float2, float3);
			this.rot.set(0.0F, 0.0F, 0.0F);
			return this;
		}

		UI3DScene.PositionRotation set(float float1, float float2, float float3, float float4, float float5, float float6) {
			this.pos.set(float1, float2, float3);
			this.rot.set(float4, float5, float6);
			return this;
		}
	}

	private static final class AABB {
		float x;
		float y;
		float z;
		float w;
		float h;
		float L;
		float r;
		float g;
		float b;

		UI3DScene.AABB set(UI3DScene.AABB aABB) {
			return this.set(aABB.x, aABB.y, aABB.z, aABB.w, aABB.h, aABB.L, aABB.r, aABB.g, aABB.b);
		}

		UI3DScene.AABB set(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
			this.x = float1;
			this.y = float2;
			this.z = float3;
			this.w = float4;
			this.h = float5;
			this.L = float6;
			this.r = float7;
			this.g = float8;
			this.b = float9;
			return this;
		}
	}

	private static final class Box3D {
		float x;
		float y;
		float z;
		float w;
		float h;
		float L;
		float rx;
		float ry;
		float rz;
		float r;
		float g;
		float b;

		UI3DScene.Box3D set(UI3DScene.Box3D box3D) {
			return this.set(box3D.x, box3D.y, box3D.z, box3D.w, box3D.h, box3D.L, box3D.rx, box3D.ry, box3D.rz, box3D.r, box3D.g, box3D.b);
		}

		UI3DScene.Box3D set(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
			this.x = float1;
			this.y = float2;
			this.z = float3;
			this.w = float4;
			this.h = float5;
			this.L = float6;
			this.rx = float7;
			this.ry = float8;
			this.rz = float9;
			this.r = float10;
			this.g = float11;
			this.b = float12;
			return this;
		}
	}

	private static final class Circle {
		final Vector3f center = new Vector3f();
		final Vector3f orientation = new Vector3f();
		float radius = 1.0F;
	}

	private static final class VehicleDrawer extends TextureDraw.GenericDrawer {
		UI3DScene.SceneVehicle m_vehicle;
		UI3DScene.VehicleRenderData m_renderData;
		boolean bRendered;
		final float[] fzeroes = new float[16];
		final Vector3f paintColor = new Vector3f(0.0F, 0.5F, 0.5F);
		final Matrix4f IDENTITY = new Matrix4f();

		public void init(UI3DScene.SceneVehicle sceneVehicle, UI3DScene.VehicleRenderData vehicleRenderData) {
			this.m_vehicle = sceneVehicle;
			this.m_renderData = vehicleRenderData;
			this.bRendered = false;
		}

		public void render() {
			for (int int1 = 0; int1 < this.m_renderData.m_models.size(); ++int1) {
				GL11.glPushAttrib(1048575);
				GL11.glPushClientAttrib(-1);
				this.render(int1);
				GL11.glPopAttrib();
				GL11.glPopClientAttrib();
				Texture.lastTextureID = -1;
				SpriteRenderer.ringBuffer.restoreBoundTextures = true;
				SpriteRenderer.ringBuffer.restoreVBOs = true;
			}
		}

		private void render(int int1) {
			this.m_renderData.m_transform.set((Matrix4fc)this.m_renderData.m_transforms.get(int1));
			ModelCamera.instance.Begin();
			Model model = (Model)this.m_renderData.m_models.get(int1);
			boolean boolean1 = model.bStatic;
			Shader shader;
			if (Core.bDebug && DebugOptions.instance.ModelRenderWireframe.getValue()) {
				GL11.glPolygonMode(1032, 6913);
				GL11.glEnable(2848);
				GL11.glLineWidth(0.75F);
				shader = ShaderManager.instance.getOrCreateShader("vehicle_wireframe", boolean1);
				if (shader != null) {
					shader.Start();
					shader.setTransformMatrix(this.IDENTITY.identity(), false);
					model.Mesh.Draw(shader);
					shader.End();
				}

				GL11.glDisable(2848);
				ModelCamera.instance.End();
			} else {
				shader = model.Effect;
				int int2;
				if (shader != null && shader.isVehicleShader()) {
					GL11.glDepthFunc(513);
					GL11.glDepthMask(true);
					GL11.glDepthRange(0.0, 1.0);
					GL11.glEnable(2929);
					GL11.glColor3f(1.0F, 1.0F, 1.0F);
					shader.Start();
					if (model.tex != null) {
						shader.setTexture(model.tex, "Texture0", 0);
						GL11.glTexEnvi(8960, 8704, 7681);
						if (this.m_vehicle.m_script.getSkinCount() > 0 && this.m_vehicle.m_script.getSkin(0).textureMask != null) {
							Texture texture = Texture.getSharedTexture("media/textures/" + this.m_vehicle.m_script.getSkin(0).textureMask + ".png");
							shader.setTexture(texture, "TextureMask", 2);
							GL11.glTexEnvi(8960, 8704, 7681);
						}
					}

					shader.setDepthBias(0.0F);
					shader.setAmbient(1.0F);
					shader.setLightingAmount(1.0F);
					shader.setHueShift(0.0F);
					shader.setTint(1.0F, 1.0F, 1.0F);
					shader.setAlpha(1.0F);
					for (int2 = 0; int2 < 5; ++int2) {
						shader.setLight(int2, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Float.NaN, 0.0F, 0.0F, 0.0F, (IsoMovingObject)null);
					}

					shader.setTextureUninstall1(this.fzeroes);
					shader.setTextureUninstall2(this.fzeroes);
					shader.setTextureLightsEnables2(this.fzeroes);
					shader.setTextureDamage1Enables1(this.fzeroes);
					shader.setTextureDamage1Enables2(this.fzeroes);
					shader.setTextureDamage2Enables1(this.fzeroes);
					shader.setTextureDamage2Enables2(this.fzeroes);
					shader.setMatrixBlood1(this.fzeroes, this.fzeroes);
					shader.setMatrixBlood2(this.fzeroes, this.fzeroes);
					shader.setTextureRustA(0.0F);
					shader.setTexturePainColor(this.paintColor, 1.0F);
					shader.setTransformMatrix(this.IDENTITY.identity(), false);
					model.Mesh.Draw(shader);
					shader.End();
				} else if (shader != null && model.Mesh != null && model.Mesh.isReady()) {
					GL11.glDepthFunc(513);
					GL11.glDepthMask(true);
					GL11.glDepthRange(0.0, 1.0);
					GL11.glEnable(2929);
					GL11.glColor3f(1.0F, 1.0F, 1.0F);
					shader.Start();
					if (model.tex != null) {
						shader.setTexture(model.tex, "Texture", 0);
					}

					shader.setDepthBias(0.0F);
					shader.setAmbient(1.0F);
					shader.setLightingAmount(1.0F);
					shader.setHueShift(0.0F);
					shader.setTint(1.0F, 1.0F, 1.0F);
					shader.setAlpha(1.0F);
					for (int2 = 0; int2 < 5; ++int2) {
						shader.setLight(int2, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Float.NaN, 0.0F, 0.0F, 0.0F, (IsoMovingObject)null);
					}

					shader.setTransformMatrix(this.IDENTITY.identity(), false);
					model.Mesh.Draw(shader);
					shader.End();
				}

				ModelCamera.instance.End();
				this.bRendered = true;
			}
		}

		public void postRender() {
		}
	}

	private static final class ModelDrawer extends TextureDraw.GenericDrawer {
		UI3DScene.SceneModel m_model;
		UI3DScene.ModelRenderData m_renderData;
		boolean bRendered;

		public void init(UI3DScene.SceneModel sceneModel, UI3DScene.ModelRenderData modelRenderData) {
			this.m_model = sceneModel;
			this.m_renderData = modelRenderData;
			this.bRendered = false;
		}

		public void render() {
			UI3DScene.StateData stateData = this.m_model.m_scene.stateDataRender();
			PZGLUtil.pushAndLoadMatrix(5889, stateData.m_projection);
			PZGLUtil.pushAndLoadMatrix(5888, stateData.m_modelView);
			Model model = this.m_model.m_model;
			Shader shader = model.Effect;
			if (shader != null && model.Mesh != null && model.Mesh.isReady()) {
				GL11.glPushAttrib(1048575);
				GL11.glPushClientAttrib(-1);
				UI3DScene uI3DScene = this.m_renderData.m_object.m_scene;
				GL11.glViewport(uI3DScene.getAbsoluteX().intValue(), Core.getInstance().getScreenHeight() - uI3DScene.getAbsoluteY().intValue() - uI3DScene.getHeight().intValue(), uI3DScene.getWidth().intValue(), uI3DScene.getHeight().intValue());
				GL11.glDepthFunc(513);
				GL11.glDepthMask(true);
				GL11.glDepthRange(0.0, 1.0);
				GL11.glEnable(2929);
				GL11.glColor3f(1.0F, 1.0F, 1.0F);
				shader.Start();
				if (model.tex != null) {
					shader.setTexture(model.tex, "Texture", 0);
				}

				shader.setDepthBias(0.0F);
				shader.setAmbient(1.0F);
				shader.setLightingAmount(1.0F);
				shader.setHueShift(0.0F);
				shader.setTint(1.0F, 1.0F, 1.0F);
				shader.setAlpha(1.0F);
				for (int int1 = 0; int1 < 5; ++int1) {
					shader.setLight(int1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Float.NaN, 0.0F, 0.0F, 0.0F, (IsoMovingObject)null);
				}

				shader.setTransformMatrix(this.m_renderData.m_transform, false);
				model.Mesh.Draw(shader);
				shader.End();
				if (Core.bDebug) {
				}

				GL11.glPopAttrib();
				GL11.glPopClientAttrib();
				Texture.lastTextureID = -1;
				SpriteRenderer.ringBuffer.restoreBoundTextures = true;
				SpriteRenderer.ringBuffer.restoreVBOs = true;
			}

			PZGLUtil.popMatrix(5889);
			PZGLUtil.popMatrix(5888);
			this.bRendered = true;
		}

		public void postRender() {
		}
	}

	private static final class CharacterDrawer extends TextureDraw.GenericDrawer {
		UI3DScene.SceneCharacter m_character;
		UI3DScene.CharacterRenderData m_renderData;
		boolean bRendered;

		public void init(UI3DScene.SceneCharacter sceneCharacter, UI3DScene.CharacterRenderData characterRenderData) {
			this.m_character = sceneCharacter;
			this.m_renderData = characterRenderData;
			this.bRendered = false;
			this.m_character.m_animatedModel.renderMain();
		}

		public void render() {
			if (this.m_character.m_bClearDepthBuffer) {
				GL11.glClear(256);
			}

			boolean boolean1 = DebugOptions.instance.ModelRenderBones.getValue();
			DebugOptions.instance.ModelRenderBones.setValue(this.m_character.m_bShowBones);
			this.m_character.m_scene.m_CharacterSceneModelCamera.m_renderData = this.m_renderData;
			this.m_character.m_animatedModel.DoRender(this.m_character.m_scene.m_CharacterSceneModelCamera);
			DebugOptions.instance.ModelRenderBones.setValue(boolean1);
			this.bRendered = true;
			GL11.glDepthMask(true);
		}

		public void postRender() {
			this.m_character.m_animatedModel.postRender(this.bRendered);
		}
	}

	private static final class TranslateGizmoRenderData {
		boolean m_hideX;
		boolean m_hideY;
		boolean m_hideZ;
	}

	public static final class PlaneObjectPool extends ObjectPool {
		int allocated = 0;

		public PlaneObjectPool() {
			super(UI3DScene.Plane::new);
		}

		protected UI3DScene.Plane makeObject() {
			++this.allocated;
			return (UI3DScene.Plane)super.makeObject();
		}
	}

	public static final class RayObjectPool extends ObjectPool {
		int allocated = 0;

		public RayObjectPool() {
			super(UI3DScene.Ray::new);
		}

		protected UI3DScene.Ray makeObject() {
			++this.allocated;
			return (UI3DScene.Ray)super.makeObject();
		}
	}

	private static final class SetModelCamera extends TextureDraw.GenericDrawer {
		UI3DScene.SceneModelCamera m_camera;
		UI3DScene.SceneObjectRenderData m_renderData;

		UI3DScene.SetModelCamera init(UI3DScene.SceneModelCamera sceneModelCamera, UI3DScene.SceneObjectRenderData sceneObjectRenderData) {
			this.m_camera = sceneModelCamera;
			this.m_renderData = sceneObjectRenderData;
			return this;
		}

		public void render() {
			this.m_camera.m_renderData = this.m_renderData;
			ModelCamera.instance = this.m_camera;
		}

		public void postRender() {
			UI3DScene.s_SetModelCameraPool.release((Object)this);
		}
	}

	private abstract class SceneModelCamera extends ModelCamera {
		UI3DScene.SceneObjectRenderData m_renderData;
	}

	private static class VehicleRenderData extends UI3DScene.SceneObjectRenderData {
		final ArrayList m_models = new ArrayList();
		final ArrayList m_transforms = new ArrayList();
		final UI3DScene.VehicleDrawer m_drawer = new UI3DScene.VehicleDrawer();
		private static final ObjectPool s_pool = new ObjectPool(UI3DScene.VehicleRenderData::new);

		UI3DScene.SceneObjectRenderData initVehicle(UI3DScene.SceneVehicle sceneVehicle) {
			super.init(sceneVehicle);
			this.m_models.clear();
			BaseVehicle.Matrix4fObjectPool matrix4fObjectPool = (BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get();
			matrix4fObjectPool.release(this.m_transforms);
			this.m_transforms.clear();
			VehicleScript vehicleScript = sceneVehicle.m_script;
			if (vehicleScript.getModel() == null) {
				return null;
			} else {
				this.initVehicleModel(sceneVehicle);
				float float1 = vehicleScript.getModelScale();
				Vector3f vector3f = vehicleScript.getModel().getOffset();
				Matrix4f matrix4f = UI3DScene.allocMatrix4f();
				matrix4f.translationRotateScale(vector3f.x * 1.0F, vector3f.y, vector3f.z, 0.0F, 0.0F, 0.0F, 1.0F, float1);
				this.m_transform.mul((Matrix4fc)matrix4f, matrix4f);
				for (int int1 = 0; int1 < vehicleScript.getPartCount(); ++int1) {
					VehicleScript.Part part = vehicleScript.getPart(int1);
					if (part.wheel != null) {
						this.initWheelModel(sceneVehicle, part, matrix4f);
					}
				}

				UI3DScene.releaseMatrix4f(matrix4f);
				this.m_drawer.init(sceneVehicle, this);
				return this;
			}
		}

		private void initVehicleModel(UI3DScene.SceneVehicle sceneVehicle) {
			VehicleScript vehicleScript = sceneVehicle.m_script;
			float float1 = vehicleScript.getModelScale();
			float float2 = 1.0F;
			ModelScript modelScript = ScriptManager.instance.getModelScript(vehicleScript.getModel().file);
			if (modelScript != null && modelScript.scale != 1.0F) {
				float2 = modelScript.scale;
			}

			float float3 = 1.0F;
			if (modelScript != null) {
				float3 = modelScript.invertX ? -1.0F : 1.0F;
			}

			float3 *= -1.0F;
			Quaternionf quaternionf = UI3DScene.allocQuaternionf();
			Matrix4f matrix4f = UI3DScene.allocMatrix4f();
			Vector3f vector3f = vehicleScript.getModel().getRotate();
			quaternionf.rotationXYZ(vector3f.x * 0.017453292F, vector3f.y * 0.017453292F, vector3f.z * 0.017453292F);
			Vector3f vector3f2 = vehicleScript.getModel().getOffset();
			matrix4f.translationRotateScale(vector3f2.x * 1.0F, vector3f2.y, vector3f2.z, quaternionf.x, quaternionf.y, quaternionf.z, quaternionf.w, float1 * float2 * float3, float1 * float2, float1 * float2);
			if (sceneVehicle.m_model.Mesh != null && sceneVehicle.m_model.Mesh.isReady() && sceneVehicle.m_model.Mesh.m_transform != null) {
				sceneVehicle.m_model.Mesh.m_transform.transpose();
				matrix4f.mul((Matrix4fc)sceneVehicle.m_model.Mesh.m_transform);
				sceneVehicle.m_model.Mesh.m_transform.transpose();
			}

			this.m_transform.mul((Matrix4fc)matrix4f, matrix4f);
			UI3DScene.releaseQuaternionf(quaternionf);
			this.m_models.add(sceneVehicle.m_model);
			this.m_transforms.add(matrix4f);
		}

		private void initWheelModel(UI3DScene.SceneVehicle sceneVehicle, VehicleScript.Part part, Matrix4f matrix4f) {
			VehicleScript vehicleScript = sceneVehicle.m_script;
			float float1 = vehicleScript.getModelScale();
			VehicleScript.Wheel wheel = vehicleScript.getWheelById(part.wheel);
			if (wheel != null && !part.models.isEmpty()) {
				VehicleScript.Model model = (VehicleScript.Model)part.models.get(0);
				Vector3f vector3f = model.getOffset();
				Vector3f vector3f2 = model.getRotate();
				Model model2 = ModelManager.instance.getLoadedModel(model.file);
				if (model2 != null) {
					float float2 = model.scale;
					float float3 = 1.0F;
					float float4 = 1.0F;
					ModelScript modelScript = ScriptManager.instance.getModelScript(model.file);
					if (modelScript != null) {
						float3 = modelScript.scale;
						float4 = modelScript.invertX ? -1.0F : 1.0F;
					}

					Quaternionf quaternionf = UI3DScene.allocQuaternionf();
					quaternionf.rotationXYZ(vector3f2.x * 0.017453292F, vector3f2.y * 0.017453292F, vector3f2.z * 0.017453292F);
					Matrix4f matrix4f2 = UI3DScene.allocMatrix4f();
					matrix4f2.translation(wheel.offset.x / float1 * 1.0F, wheel.offset.y / float1, wheel.offset.z / float1);
					Matrix4f matrix4f3 = UI3DScene.allocMatrix4f();
					matrix4f3.translationRotateScale(vector3f.x * 1.0F, vector3f.y, vector3f.z, quaternionf.x, quaternionf.y, quaternionf.z, quaternionf.w, float2 * float3 * float4, float2 * float3, float2 * float3);
					matrix4f2.mul((Matrix4fc)matrix4f3);
					UI3DScene.releaseMatrix4f(matrix4f3);
					matrix4f.mul((Matrix4fc)matrix4f2, matrix4f2);
					if (model2.Mesh != null && model2.Mesh.isReady() && model2.Mesh.m_transform != null) {
						model2.Mesh.m_transform.transpose();
						matrix4f2.mul((Matrix4fc)model2.Mesh.m_transform);
						model2.Mesh.m_transform.transpose();
					}

					UI3DScene.releaseQuaternionf(quaternionf);
					this.m_models.add(model2);
					this.m_transforms.add(matrix4f2);
				}
			}
		}

		void release() {
			s_pool.release((Object)this);
		}
	}

	private static class ModelRenderData extends UI3DScene.SceneObjectRenderData {
		final UI3DScene.ModelDrawer m_drawer = new UI3DScene.ModelDrawer();
		private static final ObjectPool s_pool = new ObjectPool(UI3DScene.ModelRenderData::new);

		UI3DScene.SceneObjectRenderData initModel(UI3DScene.SceneModel sceneModel) {
			super.init(sceneModel);
			if (sceneModel.m_useWorldAttachment) {
				if (sceneModel.m_weaponRotationHack) {
					this.m_transform.rotateXYZ(0.0F, 3.1415927F, 1.5707964F);
				}

				if (sceneModel.m_modelScript != null) {
					ModelAttachment modelAttachment = sceneModel.m_modelScript.getAttachmentById("world");
					if (modelAttachment != null) {
						Matrix4f matrix4f = ModelInstanceRenderData.makeAttachmentTransform(modelAttachment, UI3DScene.allocMatrix4f());
						matrix4f.invert();
						this.m_transform.mul((Matrix4fc)matrix4f);
						UI3DScene.releaseMatrix4f(matrix4f);
					}
				}
			}

			if (sceneModel.m_model.isReady() && sceneModel.m_model.Mesh.m_transform != null) {
				sceneModel.m_model.Mesh.m_transform.transpose();
				this.m_transform.mul((Matrix4fc)sceneModel.m_model.Mesh.m_transform);
				sceneModel.m_model.Mesh.m_transform.transpose();
			}

			if (sceneModel.m_modelScript != null && sceneModel.m_modelScript.scale != 1.0F) {
				this.m_transform.scale(sceneModel.m_modelScript.scale);
			}

			this.m_drawer.init(sceneModel, this);
			return this;
		}

		void release() {
			s_pool.release((Object)this);
		}
	}

	private static class CharacterRenderData extends UI3DScene.SceneObjectRenderData {
		final UI3DScene.CharacterDrawer m_drawer = new UI3DScene.CharacterDrawer();
		private static final ObjectPool s_pool = new ObjectPool(UI3DScene.CharacterRenderData::new);

		UI3DScene.SceneObjectRenderData initCharacter(UI3DScene.SceneCharacter sceneCharacter) {
			this.m_drawer.init(sceneCharacter, this);
			super.init(sceneCharacter);
			return this;
		}

		void release() {
			s_pool.release((Object)this);
		}
	}
}
