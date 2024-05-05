package zombie.worldMap;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import zombie.characters.IsoPlayer;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.VBOLines;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.model.ModelSlotRenderData;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureID;
import zombie.iso.IsoCamera;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.popman.ObjectPool;
import zombie.ui.UIManager;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.UI3DScene;
import zombie.worldMap.styles.WorldMapStyle;
import zombie.worldMap.styles.WorldMapStyleLayer;
import zombie.worldMap.styles.WorldMapTextureStyleLayer;


public final class WorldMapRenderer {
	private WorldMap m_worldMap;
	private int m_x;
	private int m_y;
	private int m_width;
	private int m_height;
	private int m_zoom = 0;
	private float m_zoomF = 0.0F;
	private float m_displayZoomF = 0.0F;
	private float m_centerWorldX;
	private float m_centerWorldY;
	private float m_zoomUIX;
	private float m_zoomUIY;
	private float m_zoomWorldX;
	private float m_zoomWorldY;
	private final Matrix4f m_projection = new Matrix4f();
	private final Matrix4f m_modelView = new Matrix4f();
	private final Quaternionf m_modelViewChange = new Quaternionf();
	private long m_viewChangeTime;
	private static long VIEW_CHANGE_TIME = 350L;
	private boolean m_isometric;
	private boolean m_firstUpdate = false;
	private WorldMapVisited m_visited;
	private final WorldMapRenderer.Drawer[] m_drawer = new WorldMapRenderer.Drawer[3];
	private final WorldMapRenderer.CharacterModelCamera m_CharacterModelCamera = new WorldMapRenderer.CharacterModelCamera();
	private int m_dropShadowWidth = 12;
	public WorldMapStyle m_style = null;
	protected static final VBOLines m_vboLines = new VBOLines();
	protected static final VBOLinesUV m_vboLinesUV = new VBOLinesUV();
	private final int[] m_viewport = new int[]{0, 0, 0, 0};
	private static final ThreadLocal TL_Plane_pool = ThreadLocal.withInitial(UI3DScene.PlaneObjectPool::new);
	private static final ThreadLocal TL_Ray_pool = ThreadLocal.withInitial(UI3DScene.RayObjectPool::new);
	static final float SMALL_NUM = 1.0E-8F;
	private final ArrayList options = new ArrayList();
	private final WorldMapRenderer.WorldMapBooleanOption BlurUnvisited = new WorldMapRenderer.WorldMapBooleanOption("BlurUnvisited", true);
	private final WorldMapRenderer.WorldMapBooleanOption BuildingsWithoutFeatures = new WorldMapRenderer.WorldMapBooleanOption("BuildingsWithoutFeatures", false);
	private final WorldMapRenderer.WorldMapBooleanOption DebugInfo = new WorldMapRenderer.WorldMapBooleanOption("DebugInfo", false);
	private final WorldMapRenderer.WorldMapBooleanOption CellGrid = new WorldMapRenderer.WorldMapBooleanOption("CellGrid", false);
	private final WorldMapRenderer.WorldMapBooleanOption TileGrid = new WorldMapRenderer.WorldMapBooleanOption("TileGrid", false);
	private final WorldMapRenderer.WorldMapBooleanOption UnvisitedGrid = new WorldMapRenderer.WorldMapBooleanOption("UnvisitedGrid", true);
	private final WorldMapRenderer.WorldMapBooleanOption Features = new WorldMapRenderer.WorldMapBooleanOption("Features", true);
	private final WorldMapRenderer.WorldMapBooleanOption ForestZones = new WorldMapRenderer.WorldMapBooleanOption("ForestZones", false);
	private final WorldMapRenderer.WorldMapBooleanOption HideUnvisited = new WorldMapRenderer.WorldMapBooleanOption("HideUnvisited", false);
	private final WorldMapRenderer.WorldMapBooleanOption HitTest = new WorldMapRenderer.WorldMapBooleanOption("HitTest", false);
	private final WorldMapRenderer.WorldMapBooleanOption ImagePyramid = new WorldMapRenderer.WorldMapBooleanOption("ImagePyramid", false);
	private final WorldMapRenderer.WorldMapBooleanOption Isometric = new WorldMapRenderer.WorldMapBooleanOption("Isometric", true);
	private final WorldMapRenderer.WorldMapBooleanOption LineString = new WorldMapRenderer.WorldMapBooleanOption("LineString", true);
	private final WorldMapRenderer.WorldMapBooleanOption Players = new WorldMapRenderer.WorldMapBooleanOption("Players", false);
	private final WorldMapRenderer.WorldMapBooleanOption RemotePlayers = new WorldMapRenderer.WorldMapBooleanOption("RemotePlayers", false);
	private final WorldMapRenderer.WorldMapBooleanOption PlayerNames = new WorldMapRenderer.WorldMapBooleanOption("PlayerNames", false);
	private final WorldMapRenderer.WorldMapBooleanOption Symbols = new WorldMapRenderer.WorldMapBooleanOption("Symbols", true);
	private final WorldMapRenderer.WorldMapBooleanOption Wireframe = new WorldMapRenderer.WorldMapBooleanOption("Wireframe", false);
	private final WorldMapRenderer.WorldMapBooleanOption WorldBounds = new WorldMapRenderer.WorldMapBooleanOption("WorldBounds", true);
	private final WorldMapRenderer.WorldMapBooleanOption MiniMapSymbols = new WorldMapRenderer.WorldMapBooleanOption("MiniMapSymbols", false);
	private final WorldMapRenderer.WorldMapBooleanOption VisibleCells = new WorldMapRenderer.WorldMapBooleanOption("VisibleCells", false);

	public WorldMapRenderer() {
		PZArrayUtil.arrayPopulate(this.m_drawer, WorldMapRenderer.Drawer::new);
	}

	public int getAbsoluteX() {
		return this.m_x;
	}

	public int getAbsoluteY() {
		return this.m_y;
	}

	public int getWidth() {
		return this.m_width;
	}

	public int getHeight() {
		return this.m_height;
	}

	private void calcMatrices(float float1, float float2, float float3, Matrix4f matrix4f, Matrix4f matrix4f2) {
		int int1 = this.getWidth();
		int int2 = this.getHeight();
		matrix4f.setOrtho((float)(-int1) / 2.0F, (float)int1 / 2.0F, (float)int2 / 2.0F, (float)(-int2) / 2.0F, -2000.0F, 2000.0F);
		matrix4f2.identity();
		if (this.Isometric.getValue()) {
			matrix4f2.rotateXYZ(1.0471976F, 0.0F, 0.7853982F);
		}
	}

	public Vector3f uiToScene(float float1, float float2, Matrix4f matrix4f, Matrix4f matrix4f2, Vector3f vector3f) {
		UI3DScene.Plane plane = allocPlane();
		plane.point.set(0.0F);
		plane.normal.set(0.0F, 0.0F, 1.0F);
		UI3DScene.Ray ray = this.getCameraRay(float1, (float)this.getHeight() - float2, matrix4f, matrix4f2, allocRay());
		if (this.intersect_ray_plane(plane, ray, vector3f) != 1) {
			vector3f.set(0.0F);
		}

		releasePlane(plane);
		releaseRay(ray);
		return vector3f;
	}

	public Vector3f sceneToUI(float float1, float float2, float float3, Matrix4f matrix4f, Matrix4f matrix4f2, Vector3f vector3f) {
		Matrix4f matrix4f3 = allocMatrix4f();
		matrix4f3.set((Matrix4fc)matrix4f);
		matrix4f3.mul((Matrix4fc)matrix4f2);
		this.m_viewport[0] = 0;
		this.m_viewport[1] = 0;
		this.m_viewport[2] = this.getWidth();
		this.m_viewport[3] = this.getHeight();
		matrix4f3.project(float1, float2, float3, this.m_viewport, vector3f);
		releaseMatrix4f(matrix4f3);
		return vector3f;
	}

	public float uiToWorldX(float float1, float float2, float float3, float float4, float float5) {
		Matrix4f matrix4f = allocMatrix4f();
		Matrix4f matrix4f2 = allocMatrix4f();
		this.calcMatrices(float4, float5, float3, matrix4f, matrix4f2);
		float float6 = this.uiToWorldX(float1, float2, float3, float4, float5, matrix4f, matrix4f2);
		releaseMatrix4f(matrix4f);
		releaseMatrix4f(matrix4f2);
		return float6;
	}

	public float uiToWorldY(float float1, float float2, float float3, float float4, float float5) {
		Matrix4f matrix4f = allocMatrix4f();
		Matrix4f matrix4f2 = allocMatrix4f();
		this.calcMatrices(float4, float5, float3, matrix4f, matrix4f2);
		float float6 = this.uiToWorldY(float1, float2, float3, float4, float5, matrix4f, matrix4f2);
		releaseMatrix4f(matrix4f);
		releaseMatrix4f(matrix4f2);
		return float6;
	}

	public float uiToWorldX(float float1, float float2, float float3, float float4, float float5, Matrix4f matrix4f, Matrix4f matrix4f2) {
		Vector3f vector3f = this.uiToScene(float1, float2, matrix4f, matrix4f2, allocVector3f());
		float float6 = this.getWorldScale(float3);
		vector3f.mul(1.0F / float6);
		float float7 = vector3f.x() + float4;
		releaseVector3f(vector3f);
		return float7;
	}

	public float uiToWorldY(float float1, float float2, float float3, float float4, float float5, Matrix4f matrix4f, Matrix4f matrix4f2) {
		Vector3f vector3f = this.uiToScene(float1, float2, matrix4f, matrix4f2, allocVector3f());
		float float6 = this.getWorldScale(float3);
		vector3f.mul(1.0F / float6);
		float float7 = vector3f.y() + float5;
		releaseVector3f(vector3f);
		return float7;
	}

	public float worldToUIX(float float1, float float2, float float3, float float4, float float5, Matrix4f matrix4f, Matrix4f matrix4f2) {
		float float6 = this.getWorldScale(float3);
		Vector3f vector3f = this.sceneToUI((float1 - float4) * float6, (float2 - float5) * float6, 0.0F, matrix4f, matrix4f2, allocVector3f());
		float float7 = vector3f.x();
		releaseVector3f(vector3f);
		return float7;
	}

	public float worldToUIY(float float1, float float2, float float3, float float4, float float5, Matrix4f matrix4f, Matrix4f matrix4f2) {
		float float6 = this.getWorldScale(float3);
		Vector3f vector3f = this.sceneToUI((float1 - float4) * float6, (float2 - float5) * float6, 0.0F, matrix4f, matrix4f2, allocVector3f());
		float float7 = (float)this.getHeight() - vector3f.y();
		releaseVector3f(vector3f);
		return float7;
	}

	public float worldOriginUIX(float float1, float float2) {
		return this.worldToUIX(0.0F, 0.0F, float1, float2, this.m_centerWorldY, this.m_projection, this.m_modelView);
	}

	public float worldOriginUIY(float float1, float float2) {
		return this.worldToUIY(0.0F, 0.0F, float1, this.m_centerWorldX, float2, this.m_projection, this.m_modelView);
	}

	public int getZoom() {
		return this.m_zoom;
	}

	public float getZoomF() {
		return this.m_zoomF;
	}

	public float getDisplayZoomF() {
		return this.m_displayZoomF;
	}

	public float zoomMult() {
		return this.zoomMult(this.m_zoomF);
	}

	public float zoomMult(float float1) {
		return (float)Math.pow(2.0, (double)float1);
	}

	public float getWorldScale(float float1) {
		int int1 = this.getHeight();
		double double1 = MapProjection.metersPerPixelAtZoom((double)float1, (double)int1);
		return (float)(1.0 / double1);
	}

	public void zoomAt(int int1, int int2, int int3) {
		float float1 = this.uiToWorldX((float)int1, (float)int2, this.m_displayZoomF, this.m_centerWorldX, this.m_centerWorldY);
		float float2 = this.uiToWorldY((float)int1, (float)int2, this.m_displayZoomF, this.m_centerWorldX, this.m_centerWorldY);
		this.m_zoomF = PZMath.clamp(this.m_zoomF + (float)int3 / 2.0F, this.getBaseZoom(), 24.0F);
		this.m_zoom = (int)this.m_zoomF;
		this.m_zoomWorldX = float1;
		this.m_zoomWorldY = float2;
		this.m_zoomUIX = (float)int1;
		this.m_zoomUIY = (float)int2;
	}

	public float getCenterWorldX() {
		return this.m_centerWorldX;
	}

	public float getCenterWorldY() {
		return this.m_centerWorldY;
	}

	public void centerOn(float float1, float float2) {
		this.m_centerWorldX = float1;
		this.m_centerWorldY = float2;
		if (this.m_displayZoomF != this.m_zoomF) {
			this.m_zoomWorldX = float1;
			this.m_zoomWorldY = float2;
			this.m_zoomUIX = (float)this.m_width / 2.0F;
			this.m_zoomUIY = (float)this.m_height / 2.0F;
		}
	}

	public void moveView(int int1, int int2) {
		this.centerOn(this.m_centerWorldX + (float)int1, this.m_centerWorldY + (float)int2);
	}

	public double log2(double double1) {
		return Math.log(double1) / Math.log(2.0);
	}

	public float getBaseZoom() {
		double double1 = MapProjection.zoomAtMetersPerPixel((double)this.m_worldMap.getHeightInSquares() / (double)this.getHeight(), (double)this.getHeight());
		if ((float)this.m_worldMap.getWidthInSquares() * this.getWorldScale((float)double1) > (float)this.getWidth()) {
			double1 = MapProjection.zoomAtMetersPerPixel((double)this.m_worldMap.getWidthInSquares() / (double)this.getWidth(), (double)this.getHeight());
		}

		double1 = (double)((int)(double1 * 2.0)) / 2.0;
		return (float)double1;
	}

	public void setZoom(float float1) {
		this.m_zoomF = PZMath.clamp(float1, this.getBaseZoom(), 24.0F);
		this.m_zoom = (int)this.m_zoomF;
		this.m_displayZoomF = this.m_zoomF;
	}

	public void resetView() {
		this.m_zoomF = this.getBaseZoom();
		this.m_zoom = (int)this.m_zoomF;
		this.m_centerWorldX = (float)this.m_worldMap.getMinXInSquares() + (float)this.m_worldMap.getWidthInSquares() / 2.0F;
		this.m_centerWorldY = (float)this.m_worldMap.getMinYInSquares() + (float)this.m_worldMap.getHeightInSquares() / 2.0F;
		this.m_zoomWorldX = this.m_centerWorldX;
		this.m_zoomWorldY = this.m_centerWorldY;
		this.m_zoomUIX = (float)this.getWidth() / 2.0F;
		this.m_zoomUIY = (float)this.getHeight() / 2.0F;
	}

	public Matrix4f getProjectionMatrix() {
		return this.m_projection;
	}

	public Matrix4f getModelViewMatrix() {
		return this.m_modelView;
	}

	public void setMap(WorldMap worldMap, int int1, int int2, int int3, int int4) {
		this.m_worldMap = worldMap;
		this.m_x = int1;
		this.m_y = int2;
		this.m_width = int3;
		this.m_height = int4;
	}

	public WorldMap getWorldMap() {
		return this.m_worldMap;
	}

	public void setVisited(WorldMapVisited worldMapVisited) {
		this.m_visited = worldMapVisited;
	}

	public void updateView() {
		float float1;
		if (this.m_displayZoomF != this.m_zoomF) {
			float float2 = (float)(UIManager.getMillisSinceLastRender() / 750.0);
			float float3 = Math.abs(this.m_zoomF - this.m_displayZoomF);
			float1 = float3 > 0.25F ? float3 / 0.25F : 1.0F;
			if (this.m_displayZoomF < this.m_zoomF) {
				this.m_displayZoomF = PZMath.min(this.m_displayZoomF + float2 * float1, this.m_zoomF);
			} else if (this.m_displayZoomF > this.m_zoomF) {
				this.m_displayZoomF = PZMath.max(this.m_displayZoomF - float2 * float1, this.m_zoomF);
			}

			float float4 = this.uiToWorldX(this.m_zoomUIX, this.m_zoomUIY, this.m_displayZoomF, 0.0F, 0.0F);
			float float5 = this.uiToWorldY(this.m_zoomUIX, this.m_zoomUIY, this.m_displayZoomF, 0.0F, 0.0F);
			this.m_centerWorldX = this.m_zoomWorldX - float4;
			this.m_centerWorldY = this.m_zoomWorldY - float5;
		}

		if (!this.m_firstUpdate) {
			this.m_firstUpdate = true;
			this.m_isometric = this.Isometric.getValue();
		}

		long long1;
		if (this.m_isometric != this.Isometric.getValue()) {
			this.m_isometric = this.Isometric.getValue();
			long1 = System.currentTimeMillis();
			if (this.m_viewChangeTime + VIEW_CHANGE_TIME < long1) {
				this.m_modelViewChange.setFromUnnormalized((Matrix4fc)this.m_modelView);
			}

			this.m_viewChangeTime = long1;
		}

		this.calcMatrices(this.m_centerWorldX, this.m_centerWorldY, this.m_displayZoomF, this.m_projection, this.m_modelView);
		long1 = System.currentTimeMillis();
		if (this.m_viewChangeTime + VIEW_CHANGE_TIME > long1) {
			float1 = (float)(this.m_viewChangeTime + VIEW_CHANGE_TIME - long1) / (float)VIEW_CHANGE_TIME;
			Quaternionf quaternionf = allocQuaternionf().setFromUnnormalized((Matrix4fc)this.m_modelView);
			this.m_modelView.set((Quaternionfc)this.m_modelViewChange.slerp(quaternionf, 1.0F - float1));
			releaseQuaternionf(quaternionf);
		}
	}

	public void render(UIWorldMap uIWorldMap) {
		this.m_style = uIWorldMap.getAPI().getStyle();
		int int1 = SpriteRenderer.instance.getMainStateIndex();
		this.m_drawer[int1].init(this, uIWorldMap);
		SpriteRenderer.instance.drawGeneric(this.m_drawer[int1]);
	}

	public void setDropShadowWidth(int int1) {
		this.m_dropShadowWidth = int1;
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

	UI3DScene.Ray getCameraRay(float float1, float float2, UI3DScene.Ray ray) {
		return this.getCameraRay(float1, float2, this.m_projection, this.m_modelView, ray);
	}

	UI3DScene.Ray getCameraRay(float float1, float float2, Matrix4f matrix4f, Matrix4f matrix4f2, UI3DScene.Ray ray) {
		Matrix4f matrix4f3 = allocMatrix4f();
		matrix4f3.set((Matrix4fc)matrix4f);
		matrix4f3.mul((Matrix4fc)matrix4f2);
		matrix4f3.invert();
		this.m_viewport[0] = 0;
		this.m_viewport[1] = 0;
		this.m_viewport[2] = this.getWidth();
		this.m_viewport[3] = this.getHeight();
		Vector3f vector3f = matrix4f3.unprojectInv(float1, float2, 0.0F, this.m_viewport, allocVector3f());
		Vector3f vector3f2 = matrix4f3.unprojectInv(float1, float2, 1.0F, this.m_viewport, allocVector3f());
		ray.origin.set((Vector3fc)vector3f);
		ray.direction.set((Vector3fc)vector3f2.sub(vector3f).normalize());
		releaseVector3f(vector3f2);
		releaseVector3f(vector3f);
		releaseMatrix4f(matrix4f3);
		return ray;
	}

	int intersect_ray_plane(UI3DScene.Plane plane, UI3DScene.Ray ray, Vector3f vector3f) {
		Vector3f vector3f2 = allocVector3f().set((Vector3fc)ray.direction).mul(10000.0F);
		Vector3f vector3f3 = allocVector3f().set((Vector3fc)ray.origin).sub(plane.point);
		byte byte1;
		try {
			float float1 = plane.normal.dot(vector3f2);
			float float2 = -plane.normal.dot(vector3f3);
			if (!(Math.abs(float1) < 1.0E-8F)) {
				float float3 = float2 / float1;
				byte byte2;
				if (!(float3 < 0.0F) && !(float3 > 1.0F)) {
					vector3f.set((Vector3fc)ray.origin).add(vector3f2.mul(float3));
					byte2 = 1;
					return byte2;
				}

				byte2 = 0;
				return byte2;
			}

			if (float2 != 0.0F) {
				byte1 = 0;
				return byte1;
			}

			byte1 = 2;
		} finally {
			releaseVector3f(vector3f2);
			releaseVector3f(vector3f3);
		}

		return byte1;
	}

	public ConfigOption getOptionByName(String string) {
		for (int int1 = 0; int1 < this.options.size(); ++int1) {
			ConfigOption configOption = (ConfigOption)this.options.get(int1);
			if (configOption.getName().equals(string)) {
				return configOption;
			}
		}

		return null;
	}

	public int getOptionCount() {
		return this.options.size();
	}

	public ConfigOption getOptionByIndex(int int1) {
		return (ConfigOption)this.options.get(int1);
	}

	public void setBoolean(String string, boolean boolean1) {
		ConfigOption configOption = this.getOptionByName(string);
		if (configOption instanceof BooleanConfigOption) {
			((BooleanConfigOption)configOption).setValue(boolean1);
		}
	}

	public boolean getBoolean(String string) {
		ConfigOption configOption = this.getOptionByName(string);
		return configOption instanceof BooleanConfigOption ? ((BooleanConfigOption)configOption).getValue() : false;
	}

	public void setDouble(String string, double double1) {
		ConfigOption configOption = this.getOptionByName(string);
		if (configOption instanceof DoubleConfigOption) {
			((DoubleConfigOption)configOption).setValue(double1);
		}
	}

	public double getDouble(String string, double double1) {
		ConfigOption configOption = this.getOptionByName(string);
		return configOption instanceof DoubleConfigOption ? ((DoubleConfigOption)configOption).getValue() : double1;
	}

	public static final class Drawer extends TextureDraw.GenericDrawer {
		WorldMapRenderer m_renderer;
		final WorldMapStyle m_style = new WorldMapStyle();
		WorldMap m_worldMap;
		int m_x;
		int m_y;
		int m_width;
		int m_height;
		float m_centerWorldX;
		float m_centerWorldY;
		int m_zoom = 0;
		public float m_zoomF = 0.0F;
		float m_worldScale;
		float m_renderOriginX;
		float m_renderOriginY;
		float m_renderCellX;
		float m_renderCellY;
		private final Matrix4f m_projection = new Matrix4f();
		private final Matrix4f m_modelView = new Matrix4f();
		private final WorldMapRenderer.PlayerRenderData[] m_playerRenderData = new WorldMapRenderer.PlayerRenderData[4];
		final WorldMapStyleLayer.FilterArgs m_filterArgs = new WorldMapStyleLayer.FilterArgs();
		final WorldMapStyleLayer.RenderArgs m_renderArgs = new WorldMapStyleLayer.RenderArgs();
		final ArrayList m_renderLayers = new ArrayList();
		final ArrayList m_features = new ArrayList();
		final ArrayList m_zones = new ArrayList();
		final HashSet m_zoneSet = new HashSet();
		WorldMapStyleLayer.RGBAf m_fill;
		int m_triangulationsThisFrame = 0;
		float[] m_floatArray;
		final Vector2f m_vector2f = new Vector2f();
		final TIntArrayList m_rasterizeXY = new TIntArrayList();
		final TIntSet m_rasterizeSet = new TIntHashSet();
		float m_rasterizeMinTileX;
		float m_rasterizeMinTileY;
		float m_rasterizeMaxTileX;
		float m_rasterizeMaxTileY;
		final Rasterize m_rasterize = new Rasterize();
		int[] m_rasterizeXY_ints;
		int m_rasterizeMult = 1;

		Drawer() {
			PZArrayUtil.arrayPopulate(this.m_playerRenderData, WorldMapRenderer.PlayerRenderData::new);
		}

		void init(WorldMapRenderer worldMapRenderer, UIWorldMap uIWorldMap) {
			this.m_renderer = worldMapRenderer;
			this.m_style.copyFrom(this.m_renderer.m_style);
			this.m_worldMap = worldMapRenderer.m_worldMap;
			this.m_x = worldMapRenderer.m_x;
			this.m_y = worldMapRenderer.m_y;
			this.m_width = worldMapRenderer.m_width;
			this.m_height = worldMapRenderer.m_height;
			this.m_centerWorldX = worldMapRenderer.m_centerWorldX;
			this.m_centerWorldY = worldMapRenderer.m_centerWorldY;
			this.m_zoomF = worldMapRenderer.m_displayZoomF;
			this.m_zoom = (int)this.m_zoomF;
			this.m_worldScale = this.getWorldScale();
			this.m_renderOriginX = ((float)this.m_renderer.m_worldMap.getMinXInSquares() - this.m_centerWorldX) * this.m_worldScale;
			this.m_renderOriginY = ((float)this.m_renderer.m_worldMap.getMinYInSquares() - this.m_centerWorldY) * this.m_worldScale;
			this.m_projection.set((Matrix4fc)worldMapRenderer.m_projection);
			this.m_modelView.set((Matrix4fc)worldMapRenderer.m_modelView);
			this.m_fill = uIWorldMap.m_color;
			this.m_triangulationsThisFrame = 0;
			if (this.m_renderer.m_visited != null) {
				this.m_renderer.m_visited.renderMain();
			}

			int int1;
			for (int1 = 0; int1 < 4; ++int1) {
				this.m_playerRenderData[int1].m_modelSlotRenderData = null;
			}

			if (this.m_renderer.Players.getValue() && this.m_zoomF >= 20.0F) {
				for (int1 = 0; int1 < 4; ++int1) {
					IsoPlayer player = IsoPlayer.players[int1];
					if (player != null && !player.isDead() && player.legsSprite.hasActiveModel()) {
						float float1 = player.x;
						float float2 = player.y;
						if (player.getVehicle() != null) {
							float1 = player.getVehicle().getX();
							float2 = player.getVehicle().getY();
						}

						float float3 = this.m_renderer.worldToUIX(float1, float2, this.m_zoomF, this.m_centerWorldX, this.m_centerWorldY, this.m_projection, this.m_modelView);
						float float4 = this.m_renderer.worldToUIY(float1, float2, this.m_zoomF, this.m_centerWorldX, this.m_centerWorldY, this.m_projection, this.m_modelView);
						if (!(float3 < -100.0F) && !(float3 > (float)(this.m_width + 100)) && !(float4 < -100.0F) && !(float4 > (float)(this.m_height + 100))) {
							this.m_playerRenderData[int1].m_angle = player.getVehicle() == null ? player.getAnimationPlayer().getAngle() : 4.712389F;
							this.m_playerRenderData[int1].m_x = float1 - this.m_centerWorldX;
							this.m_playerRenderData[int1].m_y = float2 - this.m_centerWorldY;
							player.legsSprite.modelSlot.model.updateLights();
							int int2 = IsoCamera.frameState.playerIndex;
							IsoCamera.frameState.playerIndex = int1;
							player.checkUpdateModelTextures();
							this.m_playerRenderData[int1].m_modelSlotRenderData = ModelSlotRenderData.alloc().init(player.legsSprite.modelSlot);
							this.m_playerRenderData[int1].m_modelSlotRenderData.centerOfMassY = 0.0F;
							IsoCamera.frameState.playerIndex = int2;
							++player.legsSprite.modelSlot.renderRefCount;
						}
					}
				}
			}
		}

		public int getAbsoluteX() {
			return this.m_x;
		}

		public int getAbsoluteY() {
			return this.m_y;
		}

		public int getWidth() {
			return this.m_width;
		}

		public int getHeight() {
			return this.m_height;
		}

		public float getWorldScale() {
			return this.m_renderer.getWorldScale(this.m_zoomF);
		}

		public float uiToWorldX(float float1, float float2) {
			return this.m_renderer.uiToWorldX(float1, float2, this.m_zoomF, this.m_centerWorldX, this.m_centerWorldY, this.m_projection, this.m_modelView);
		}

		public float uiToWorldY(float float1, float float2) {
			return this.m_renderer.uiToWorldY(float1, float2, this.m_zoomF, this.m_centerWorldX, this.m_centerWorldY, this.m_projection, this.m_modelView);
		}

		public float worldOriginUIX(float float1) {
			return this.m_renderer.worldOriginUIX(this.m_zoomF, float1);
		}

		public float worldOriginUIY(float float1) {
			return this.m_renderer.worldOriginUIY(this.m_zoomF, float1);
		}

		private void renderCellFeatures() {
			for (int int1 = 0; int1 < this.m_rasterizeXY.size() - 1; int1 += 2) {
				int int2 = this.m_rasterizeXY_ints[int1];
				int int3 = this.m_rasterizeXY_ints[int1 + 1];
				if (this.m_renderer.m_visited == null || this.m_renderer.m_visited.isCellVisible(int2, int3)) {
					this.m_features.clear();
					int int4;
					for (int4 = 0; int4 < this.m_worldMap.m_data.size(); ++int4) {
						WorldMapData worldMapData = (WorldMapData)this.m_worldMap.m_data.get(int4);
						if (worldMapData.isReady()) {
							WorldMapCell worldMapCell = worldMapData.getCell(int2, int3);
							if (worldMapCell != null && !worldMapCell.m_features.isEmpty()) {
								this.m_features.addAll(worldMapCell.m_features);
								if (this.m_worldMap.isLastDataInDirectory(worldMapData)) {
									break;
								}
							}
						}
					}

					if (this.m_features.isEmpty()) {
						this.m_renderArgs.renderer = this.m_renderer;
						this.m_renderArgs.drawer = this;
						this.m_renderArgs.cellX = int2;
						this.m_renderArgs.cellY = int3;
						this.m_renderCellX = this.m_renderOriginX + (float)(int2 * 300 - this.m_worldMap.getMinXInSquares()) * this.m_worldScale;
						this.m_renderCellY = this.m_renderOriginY + (float)(int3 * 300 - this.m_worldMap.getMinYInSquares()) * this.m_worldScale;
						for (int4 = 0; int4 < this.m_style.m_layers.size(); ++int4) {
							WorldMapStyleLayer worldMapStyleLayer = (WorldMapStyleLayer)this.m_style.m_layers.get(int4);
							if (worldMapStyleLayer instanceof WorldMapTextureStyleLayer) {
								worldMapStyleLayer.renderCell(this.m_renderArgs);
							}
						}
					} else {
						this.renderCell(int2, int3, this.m_features);
					}
				}
			}
		}

		private void renderCell(int int1, int int2, ArrayList arrayList) {
			this.m_renderCellX = this.m_renderOriginX + (float)(int1 * 300 - this.m_worldMap.getMinXInSquares()) * this.m_worldScale;
			this.m_renderCellY = this.m_renderOriginY + (float)(int2 * 300 - this.m_worldMap.getMinYInSquares()) * this.m_worldScale;
			WorldMapRenderLayer.s_pool.release((List)this.m_renderLayers);
			this.m_renderLayers.clear();
			this.m_filterArgs.renderer = this.m_renderer;
			this.filterFeatures(arrayList, this.m_filterArgs, this.m_renderLayers);
			this.m_renderArgs.renderer = this.m_renderer;
			this.m_renderArgs.drawer = this;
			this.m_renderArgs.cellX = int1;
			this.m_renderArgs.cellY = int2;
			for (int int3 = 0; int3 < this.m_renderLayers.size(); ++int3) {
				WorldMapRenderLayer worldMapRenderLayer = (WorldMapRenderLayer)this.m_renderLayers.get(int3);
				worldMapRenderLayer.m_styleLayer.renderCell(this.m_renderArgs);
				for (int int4 = 0; int4 < worldMapRenderLayer.m_features.size(); ++int4) {
					WorldMapFeature worldMapFeature = (WorldMapFeature)worldMapRenderLayer.m_features.get(int4);
					worldMapRenderLayer.m_styleLayer.render(worldMapFeature, this.m_renderArgs);
				}
			}
		}

		void filterFeatures(ArrayList arrayList, WorldMapStyleLayer.FilterArgs filterArgs, ArrayList arrayList2) {
			for (int int1 = 0; int1 < this.m_style.m_layers.size(); ++int1) {
				WorldMapStyleLayer worldMapStyleLayer = (WorldMapStyleLayer)this.m_style.m_layers.get(int1);
				if (!(worldMapStyleLayer.m_minZoom > this.m_zoomF)) {
					if (worldMapStyleLayer.m_id.equals("mylayer")) {
						boolean boolean1 = true;
					}

					WorldMapRenderLayer worldMapRenderLayer = null;
					if (worldMapStyleLayer instanceof WorldMapTextureStyleLayer) {
						worldMapRenderLayer = (WorldMapRenderLayer)WorldMapRenderLayer.s_pool.alloc();
						worldMapRenderLayer.m_styleLayer = worldMapStyleLayer;
						worldMapRenderLayer.m_features.clear();
						arrayList2.add(worldMapRenderLayer);
					} else {
						for (int int2 = 0; int2 < arrayList.size(); ++int2) {
							WorldMapFeature worldMapFeature = (WorldMapFeature)arrayList.get(int2);
							if (worldMapStyleLayer.filter(worldMapFeature, filterArgs)) {
								if (worldMapRenderLayer == null) {
									worldMapRenderLayer = (WorldMapRenderLayer)WorldMapRenderLayer.s_pool.alloc();
									worldMapRenderLayer.m_styleLayer = worldMapStyleLayer;
									worldMapRenderLayer.m_features.clear();
									arrayList2.add(worldMapRenderLayer);
								}

								worldMapRenderLayer.m_features.add(worldMapFeature);
							}
						}
					}
				}
			}
		}

		void renderCellGrid(int int1, int int2, int int3, int int4) {
			float float1 = this.m_renderOriginX + (float)(int1 * 300 - this.m_worldMap.getMinXInSquares()) * this.m_worldScale;
			float float2 = this.m_renderOriginY + (float)(int2 * 300 - this.m_worldMap.getMinYInSquares()) * this.m_worldScale;
			float float3 = float1 + (float)((int3 - int1 + 1) * 300) * this.m_worldScale;
			float float4 = float2 + (float)((int4 - int2 + 1) * 300) * this.m_worldScale;
			WorldMapRenderer.m_vboLines.setMode(1);
			WorldMapRenderer.m_vboLines.setLineWidth(1.0F);
			int int5;
			for (int5 = int1; int5 <= int3 + 1; ++int5) {
				WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + (float)(int5 * 300 - this.m_worldMap.getMinXInSquares()) * this.m_worldScale, float2, 0.0F, this.m_renderOriginX + (float)(int5 * 300 - this.m_worldMap.getMinXInSquares()) * this.m_worldScale, float4, 0.0F, 0.25F, 0.25F, 0.25F, 1.0F);
			}

			for (int5 = int2; int5 <= int4 + 1; ++int5) {
				WorldMapRenderer.m_vboLines.addLine(float1, this.m_renderOriginY + (float)(int5 * 300 - this.m_worldMap.getMinYInSquares()) * this.m_worldScale, 0.0F, float3, this.m_renderOriginY + (float)(int5 * 300 - this.m_worldMap.getMinYInSquares()) * this.m_worldScale, 0.0F, 0.25F, 0.25F, 0.25F, 1.0F);
			}

			WorldMapRenderer.m_vboLines.flush();
		}

		void renderPlayers() {
			boolean boolean1 = true;
			for (int int1 = 0; int1 < this.m_playerRenderData.length; ++int1) {
				WorldMapRenderer.PlayerRenderData playerRenderData = this.m_playerRenderData[int1];
				if (playerRenderData.m_modelSlotRenderData != null) {
					if (boolean1) {
						GL11.glClear(256);
						boolean1 = false;
					}

					this.m_renderer.m_CharacterModelCamera.m_worldScale = this.m_worldScale;
					this.m_renderer.m_CharacterModelCamera.m_bUseWorldIso = true;
					this.m_renderer.m_CharacterModelCamera.m_angle = playerRenderData.m_angle;
					this.m_renderer.m_CharacterModelCamera.m_playerX = playerRenderData.m_x;
					this.m_renderer.m_CharacterModelCamera.m_playerY = playerRenderData.m_y;
					this.m_renderer.m_CharacterModelCamera.m_bVehicle = playerRenderData.m_modelSlotRenderData.bInVehicle;
					ModelCamera.instance = this.m_renderer.m_CharacterModelCamera;
					playerRenderData.m_modelSlotRenderData.render();
				}
			}

			if (UIManager.useUIFBO) {
				GL14.glBlendFuncSeparate(770, 771, 1, 771);
			}
		}

		public void drawLineStringXXX(WorldMapStyleLayer.RenderArgs renderArgs, WorldMapFeature worldMapFeature, WorldMapStyleLayer.RGBAf rGBAf, float float1) {
			float float2 = this.m_renderCellX;
			float float3 = this.m_renderCellY;
			float float4 = this.m_worldScale;
			float float5 = rGBAf.r;
			float float6 = rGBAf.g;
			float float7 = rGBAf.b;
			float float8 = rGBAf.a;
			for (int int1 = 0; int1 < worldMapFeature.m_geometries.size(); ++int1) {
				WorldMapGeometry worldMapGeometry = (WorldMapGeometry)worldMapFeature.m_geometries.get(int1);
				switch (worldMapGeometry.m_type) {
				case LineString: 
					WorldMapRenderer.m_vboLines.setMode(1);
					WorldMapRenderer.m_vboLines.setLineWidth(float1);
					for (int int2 = 0; int2 < worldMapGeometry.m_points.size(); ++int2) {
						WorldMapPoints worldMapPoints = (WorldMapPoints)worldMapGeometry.m_points.get(int2);
						for (int int3 = 0; int3 < worldMapPoints.numPoints() - 1; ++int3) {
							float float9 = (float)worldMapPoints.getX(int3);
							float float10 = (float)worldMapPoints.getY(int3);
							float float11 = (float)worldMapPoints.getX(int3 + 1);
							float float12 = (float)worldMapPoints.getY(int3 + 1);
							WorldMapRenderer.m_vboLines.addLine(float2 + float9 * float4, float3 + float10 * float4, 0.0F, float2 + float11 * float4, float3 + float12 * float4, 0.0F, float5, float6, float7, float8);
						}
					}

				
				}
			}
		}

		public void drawLineStringYYY(WorldMapStyleLayer.RenderArgs renderArgs, WorldMapFeature worldMapFeature, WorldMapStyleLayer.RGBAf rGBAf, float float1) {
			float float2 = this.m_renderCellX;
			float float3 = this.m_renderCellY;
			float float4 = this.m_worldScale;
			float float5 = rGBAf.r;
			float float6 = rGBAf.g;
			float float7 = rGBAf.b;
			float float8 = rGBAf.a;
			for (int int1 = 0; int1 < worldMapFeature.m_geometries.size(); ++int1) {
				WorldMapGeometry worldMapGeometry = (WorldMapGeometry)worldMapFeature.m_geometries.get(int1);
				switch (worldMapGeometry.m_type) {
				case LineString: 
					StrokeGeometry.Point[] pointArray = new StrokeGeometry.Point[worldMapGeometry.m_points.size()];
					WorldMapPoints worldMapPoints = (WorldMapPoints)worldMapGeometry.m_points.get(0);
					for (int int2 = 0; int2 < worldMapPoints.numPoints(); ++int2) {
						float float9 = (float)worldMapPoints.getX(int2);
						float float10 = (float)worldMapPoints.getY(int2);
						pointArray[int2] = StrokeGeometry.newPoint((double)(float2 + float9 * float4), (double)(float3 + float10 * float4));
					}

					StrokeGeometry.Attrs attrs = new StrokeGeometry.Attrs();
					attrs.join = "miter";
					attrs.width = float1;
					ArrayList arrayList = StrokeGeometry.getStrokeGeometry(pointArray, attrs);
					if (arrayList != null) {
						WorldMapRenderer.m_vboLines.setMode(4);
						for (int int3 = 0; int3 < arrayList.size(); ++int3) {
							float float11 = (float)((StrokeGeometry.Point)arrayList.get(int3)).x;
							float float12 = (float)((StrokeGeometry.Point)arrayList.get(int3)).y;
							WorldMapRenderer.m_vboLines.addElement(float11, float12, 0.0F, float5, float6, float7, float8);
						}

						StrokeGeometry.release(arrayList);
					}

				
				}
			}
		}

		public void drawLineString(WorldMapStyleLayer.RenderArgs renderArgs, WorldMapFeature worldMapFeature, WorldMapStyleLayer.RGBAf rGBAf, float float1) {
			if (this.m_renderer.LineString.getValue()) {
				float float2 = this.m_renderCellX;
				float float3 = this.m_renderCellY;
				float float4 = this.m_worldScale;
				float float5 = rGBAf.r;
				float float6 = rGBAf.g;
				float float7 = rGBAf.b;
				float float8 = rGBAf.a;
				WorldMapRenderer.m_vboLines.flush();
				WorldMapRenderer.m_vboLinesUV.flush();
				for (int int1 = 0; int1 < worldMapFeature.m_geometries.size(); ++int1) {
					WorldMapGeometry worldMapGeometry = (WorldMapGeometry)worldMapFeature.m_geometries.get(int1);
					switch (worldMapGeometry.m_type) {
					case LineString: 
						WorldMapPoints worldMapPoints = (WorldMapPoints)worldMapGeometry.m_points.get(0);
						if (this.m_floatArray == null || this.m_floatArray.length < worldMapPoints.numPoints() * 2) {
							this.m_floatArray = new float[worldMapPoints.numPoints() * 2];
						}

						for (int int2 = 0; int2 < worldMapPoints.numPoints(); ++int2) {
							float float9 = (float)worldMapPoints.getX(int2);
							float float10 = (float)worldMapPoints.getY(int2);
							this.m_floatArray[int2 * 2] = float2 + float9 * float4;
							this.m_floatArray[int2 * 2 + 1] = float3 + float10 * float4;
						}

						GL13.glActiveTexture(33984);
						GL11.glDisable(3553);
						GL11.glEnable(3042);
					
					}
				}
			}
		}

		public void drawLineStringTexture(WorldMapStyleLayer.RenderArgs renderArgs, WorldMapFeature worldMapFeature, WorldMapStyleLayer.RGBAf rGBAf, float float1, Texture texture) {
			float float2 = this.m_renderCellX;
			float float3 = this.m_renderCellY;
			float float4 = this.m_worldScale;
			if (texture != null && texture.isReady()) {
				if (texture.getID() == -1) {
					texture.bind();
				}

				for (int int1 = 0; int1 < worldMapFeature.m_geometries.size(); ++int1) {
					WorldMapGeometry worldMapGeometry = (WorldMapGeometry)worldMapFeature.m_geometries.get(int1);
					if (worldMapGeometry.m_type == WorldMapGeometry.Type.LineString) {
						WorldMapRenderer.m_vboLinesUV.setMode(7);
						WorldMapRenderer.m_vboLinesUV.startRun(texture.getTextureId());
						float float5 = float1;
						WorldMapPoints worldMapPoints = (WorldMapPoints)worldMapGeometry.m_points.get(0);
						for (int int2 = 0; int2 < worldMapPoints.numPoints() - 1; ++int2) {
							float float6 = float2 + (float)worldMapPoints.getX(int2) * float4;
							float float7 = float3 + (float)worldMapPoints.getY(int2) * float4;
							float float8 = float2 + (float)worldMapPoints.getX(int2 + 1) * float4;
							float float9 = float3 + (float)worldMapPoints.getY(int2 + 1) * float4;
							float float10 = float9 - float7;
							float float11 = -(float8 - float6);
							Vector2f vector2f = this.m_vector2f.set(float10, float11);
							vector2f.normalize();
							float float12 = float6 + vector2f.x * float5 / 2.0F;
							float float13 = float7 + vector2f.y * float5 / 2.0F;
							float float14 = float8 + vector2f.x * float5 / 2.0F;
							float float15 = float9 + vector2f.y * float5 / 2.0F;
							float float16 = float8 - vector2f.x * float5 / 2.0F;
							float float17 = float9 - vector2f.y * float5 / 2.0F;
							float float18 = float6 - vector2f.x * float5 / 2.0F;
							float float19 = float7 - vector2f.y * float5 / 2.0F;
							float float20 = Vector2f.length(float8 - float6, float9 - float7);
							float float21 = 0.0F;
							float float22 = float20 / (float5 * ((float)texture.getHeight() / (float)texture.getWidth()));
							float float23 = 0.0F;
							float float24 = 0.0F;
							float float25 = 1.0F;
							float float26 = 0.0F;
							float float27 = 1.0F;
							float float28 = float20 / (float5 * ((float)texture.getHeight() / (float)texture.getWidth()));
							WorldMapRenderer.m_vboLinesUV.addQuad(float12, float13, float21, float22, float14, float15, float23, float24, float16, float17, float25, float26, float18, float19, float27, float28, 0.0F, rGBAf.r, rGBAf.g, rGBAf.b, rGBAf.a);
						}
					}
				}
			}
		}

		public void fillPolygon(WorldMapStyleLayer.RenderArgs renderArgs, WorldMapFeature worldMapFeature, WorldMapStyleLayer.RGBAf rGBAf) {
			WorldMapRenderer.m_vboLinesUV.flush();
			float float1 = this.m_renderCellX;
			float float2 = this.m_renderCellY;
			float float3 = this.m_worldScale;
			float float4 = rGBAf.r;
			float float5 = rGBAf.g;
			float float6 = rGBAf.b;
			float float7 = rGBAf.a;
			for (int int1 = 0; int1 < worldMapFeature.m_geometries.size(); ++int1) {
				WorldMapGeometry worldMapGeometry = (WorldMapGeometry)worldMapFeature.m_geometries.get(int1);
				if (worldMapGeometry.m_type == WorldMapGeometry.Type.Polygon) {
					boolean boolean1 = false;
					int int2;
					if (worldMapGeometry.m_triangles == null) {
						if (this.m_triangulationsThisFrame > 500) {
							continue;
						}

						++this.m_triangulationsThisFrame;
						double[] doubleArray = worldMapFeature.m_properties.containsKey("highway") ? new double[]{1.0, 2.0, 4.0, 8.0, 12.0, 18.0} : null;
						worldMapGeometry.triangulate(doubleArray);
						if (worldMapGeometry.m_triangles == null) {
							if (!Core.bDebug) {
								continue;
							}

							WorldMapRenderer.m_vboLines.setMode(1);
							float4 = 1.0F;
							float6 = 0.0F;
							float5 = 0.0F;
							WorldMapRenderer.m_vboLines.setLineWidth(4.0F);
							for (int int3 = 0; int3 < worldMapGeometry.m_points.size(); ++int3) {
								WorldMapPoints worldMapPoints = (WorldMapPoints)worldMapGeometry.m_points.get(int3);
								for (int int4 = 0; int4 < worldMapPoints.numPoints(); ++int4) {
									int2 = worldMapPoints.getX(int4);
									int int5 = worldMapPoints.getY(int4);
									int int6 = worldMapPoints.getX((int4 + 1) % worldMapPoints.numPoints());
									int int7 = worldMapPoints.getY((int4 + 1) % worldMapPoints.numPoints());
									WorldMapRenderer.m_vboLines.reserve(2);
									WorldMapRenderer.m_vboLines.addElement(float1 + (float)int2 * float3, float2 + (float)int5 * float3, 0.0F, float4, float5, float6, float7);
									WorldMapRenderer.m_vboLines.addElement(float1 + (float)int6 * float3, float2 + (float)int7 * float3, 0.0F, float4, float5, float6, float7);
								}
							}

							WorldMapRenderer.m_vboLines.setLineWidth(1.0F);
							continue;
						}

						if (boolean1) {
							this.uploadTrianglesToVBO(worldMapGeometry);
						}
					}

					if (boolean1) {
						GL11.glTranslatef(float1, float2, 0.0F);
						GL11.glScalef(float3, float3, float3);
						GL11.glColor4f(float4, float5, float6, float7);
						if (worldMapGeometry.m_triangles.length / 2 > 2340) {
							int int8 = PZMath.min(worldMapGeometry.m_triangles.length / 2, 2340);
							WorldMapVBOs.getInstance().drawElements(4, worldMapGeometry.m_vboIndex1, worldMapGeometry.m_vboIndex2, int8);
							WorldMapVBOs.getInstance().drawElements(4, worldMapGeometry.m_vboIndex3, worldMapGeometry.m_vboIndex4, worldMapGeometry.m_triangles.length / 2 - int8);
						} else {
							WorldMapVBOs.getInstance().drawElements(4, worldMapGeometry.m_vboIndex1, worldMapGeometry.m_vboIndex2, worldMapGeometry.m_triangles.length / 2);
						}

						GL11.glScalef(1.0F / float3, 1.0F / float3, 1.0F / float3);
						GL11.glTranslatef(-float1, -float2, 0.0F);
					} else {
						WorldMapRenderer.m_vboLines.setMode(4);
						double double1 = 0.0;
						if ((double)this.m_zoomF <= 11.5) {
							double1 = 18.0;
						} else if ((double)this.m_zoomF <= 12.0) {
							double1 = 12.0;
						} else if ((double)this.m_zoomF <= 12.5) {
							double1 = 8.0;
						} else if ((double)this.m_zoomF <= 13.0) {
							double1 = 4.0;
						} else if ((double)this.m_zoomF <= 13.5) {
							double1 = 2.0;
						} else if ((double)this.m_zoomF <= 14.0) {
							double1 = 1.0;
						}

						WorldMapGeometry.TrianglesPerZoom trianglesPerZoom = double1 == 0.0 ? null : worldMapGeometry.findTriangles(double1);
						float[] floatArray;
						float float8;
						float float9;
						float float10;
						float float11;
						float float12;
						float float13;
						if (trianglesPerZoom != null) {
							floatArray = trianglesPerZoom.m_triangles;
							for (int2 = 0; int2 < floatArray.length; int2 += 6) {
								float8 = floatArray[int2];
								float9 = floatArray[int2 + 1];
								float10 = floatArray[int2 + 2];
								float11 = floatArray[int2 + 3];
								float12 = floatArray[int2 + 4];
								float13 = floatArray[int2 + 5];
								WorldMapRenderer.m_vboLines.reserve(3);
								float float14 = 1.0F;
								WorldMapRenderer.m_vboLines.addElement(float1 + float8 * float3, float2 + float9 * float3, 0.0F, float4 * float14, float5 * float14, float6 * float14, float7);
								WorldMapRenderer.m_vboLines.addElement(float1 + float10 * float3, float2 + float11 * float3, 0.0F, float4 * float14, float5 * float14, float6 * float14, float7);
								WorldMapRenderer.m_vboLines.addElement(float1 + float12 * float3, float2 + float13 * float3, 0.0F, float4 * float14, float5 * float14, float6 * float14, float7);
							}
						} else {
							floatArray = worldMapGeometry.m_triangles;
							for (int2 = 0; int2 < floatArray.length; int2 += 6) {
								float8 = floatArray[int2];
								float9 = floatArray[int2 + 1];
								float10 = floatArray[int2 + 2];
								float11 = floatArray[int2 + 3];
								float12 = floatArray[int2 + 4];
								float13 = floatArray[int2 + 5];
								WorldMapRenderer.m_vboLines.reserve(3);
								WorldMapRenderer.m_vboLines.addElement(float1 + float8 * float3, float2 + float9 * float3, 0.0F, float4, float5, float6, float7);
								WorldMapRenderer.m_vboLines.addElement(float1 + float10 * float3, float2 + float11 * float3, 0.0F, float4, float5, float6, float7);
								WorldMapRenderer.m_vboLines.addElement(float1 + float12 * float3, float2 + float13 * float3, 0.0F, float4, float5, float6, float7);
							}
						}
					}
				}
			}
		}

		public void fillPolygon(WorldMapStyleLayer.RenderArgs renderArgs, WorldMapFeature worldMapFeature, WorldMapStyleLayer.RGBAf rGBAf, Texture texture, float float1) {
			WorldMapRenderer.m_vboLines.flush();
			float float2 = this.m_renderCellX;
			float float3 = this.m_renderCellY;
			float float4 = this.m_worldScale;
			float float5 = rGBAf.r;
			float float6 = rGBAf.g;
			float float7 = rGBAf.b;
			float float8 = rGBAf.a;
			for (int int1 = 0; int1 < worldMapFeature.m_geometries.size(); ++int1) {
				WorldMapGeometry worldMapGeometry = (WorldMapGeometry)worldMapFeature.m_geometries.get(int1);
				if (worldMapGeometry.m_type == WorldMapGeometry.Type.Polygon) {
					if (worldMapGeometry.m_triangles == null) {
						worldMapGeometry.triangulate((double[])null);
						if (worldMapGeometry.m_triangles == null) {
							continue;
						}
					}

					GL11.glEnable(3553);
					GL11.glTexParameteri(3553, 10241, 9728);
					GL11.glTexParameteri(3553, 10240, 9728);
					WorldMapRenderer.m_vboLinesUV.setMode(4);
					WorldMapRenderer.m_vboLinesUV.startRun(texture.getTextureId());
					float[] floatArray = worldMapGeometry.m_triangles;
					float float9 = (float)(renderArgs.cellX * 300 + worldMapGeometry.m_minX);
					float float10 = (float)(renderArgs.cellY * 300 + worldMapGeometry.m_minY);
					float float11 = (float)texture.getWidth() * float1;
					float float12 = (float)texture.getHeight() * float1;
					float float13 = (float)texture.getWidthHW();
					float float14 = (float)texture.getHeightHW();
					float float15 = PZMath.floor(float9 / float11) * float11;
					float float16 = PZMath.floor(float10 / float12) * float12;
					for (int int2 = 0; int2 < floatArray.length; int2 += 6) {
						float float17 = floatArray[int2];
						float float18 = floatArray[int2 + 1];
						float float19 = floatArray[int2 + 2];
						float float20 = floatArray[int2 + 3];
						float float21 = floatArray[int2 + 4];
						float float22 = floatArray[int2 + 5];
						float float23 = (float17 + (float)(renderArgs.cellX * 300) - float15) / float1;
						float float24 = (float18 + (float)(renderArgs.cellY * 300) - float16) / float1;
						float float25 = (float19 + (float)(renderArgs.cellX * 300) - float15) / float1;
						float float26 = (float20 + (float)(renderArgs.cellY * 300) - float16) / float1;
						float float27 = (float21 + (float)(renderArgs.cellX * 300) - float15) / float1;
						float float28 = (float22 + (float)(renderArgs.cellY * 300) - float16) / float1;
						float17 = float2 + float17 * float4;
						float18 = float3 + float18 * float4;
						float19 = float2 + float19 * float4;
						float20 = float3 + float20 * float4;
						float21 = float2 + float21 * float4;
						float22 = float3 + float22 * float4;
						float float29 = float23 / float13;
						float float30 = float24 / float14;
						float float31 = float25 / float13;
						float float32 = float26 / float14;
						float float33 = float27 / float13;
						float float34 = float28 / float14;
						WorldMapRenderer.m_vboLinesUV.reserve(3);
						WorldMapRenderer.m_vboLinesUV.addElement(float17, float18, 0.0F, float29, float30, float5, float6, float7, float8);
						WorldMapRenderer.m_vboLinesUV.addElement(float19, float20, 0.0F, float31, float32, float5, float6, float7, float8);
						WorldMapRenderer.m_vboLinesUV.addElement(float21, float22, 0.0F, float33, float34, float5, float6, float7, float8);
					}

					GL11.glDisable(3553);
				}
			}
		}

		void uploadTrianglesToVBO(WorldMapGeometry worldMapGeometry) {
			int[] intArray = new int[2];
			int int1 = worldMapGeometry.m_triangles.length / 2;
			int int2;
			float float1;
			float float2;
			float float3;
			if (int1 > 2340) {
				for (int int3 = 0; int1 > 0; int1 -= int2 * 3) {
					int2 = PZMath.min(int1 / 3, 780);
					WorldMapVBOs.getInstance().reserveVertices(int2 * 3, intArray);
					if (worldMapGeometry.m_vboIndex1 == -1) {
						worldMapGeometry.m_vboIndex1 = intArray[0];
						worldMapGeometry.m_vboIndex2 = intArray[1];
					} else {
						worldMapGeometry.m_vboIndex3 = intArray[0];
						worldMapGeometry.m_vboIndex4 = intArray[1];
					}

					float[] floatArray = worldMapGeometry.m_triangles;
					int int4 = int3 * 3 * 2;
					for (int int5 = (int3 + int2) * 3 * 2; int4 < int5; int4 += 6) {
						float1 = floatArray[int4];
						float2 = floatArray[int4 + 1];
						float3 = floatArray[int4 + 2];
						float float4 = floatArray[int4 + 3];
						float float5 = floatArray[int4 + 4];
						float float6 = floatArray[int4 + 5];
						WorldMapVBOs.getInstance().addElement(float1, float2, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
						WorldMapVBOs.getInstance().addElement(float3, float4, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
						WorldMapVBOs.getInstance().addElement(float5, float6, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
					}

					int3 += int2;
				}
			} else {
				WorldMapVBOs.getInstance().reserveVertices(int1, intArray);
				worldMapGeometry.m_vboIndex1 = intArray[0];
				worldMapGeometry.m_vboIndex2 = intArray[1];
				float[] floatArray2 = worldMapGeometry.m_triangles;
				for (int2 = 0; int2 < floatArray2.length; int2 += 6) {
					float float7 = floatArray2[int2];
					float float8 = floatArray2[int2 + 1];
					float float9 = floatArray2[int2 + 2];
					float1 = floatArray2[int2 + 3];
					float2 = floatArray2[int2 + 4];
					float3 = floatArray2[int2 + 5];
					WorldMapVBOs.getInstance().addElement(float7, float8, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
					WorldMapVBOs.getInstance().addElement(float9, float1, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
					WorldMapVBOs.getInstance().addElement(float2, float3, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
				}
			}
		}

		void outlineTriangles(WorldMapGeometry worldMapGeometry, float float1, float float2, float float3) {
			WorldMapRenderer.m_vboLines.setMode(1);
			float float4 = 1.0F;
			float float5 = 1.0F;
			float float6 = 0.0F;
			float float7 = 0.0F;
			float[] floatArray = worldMapGeometry.m_triangles;
			for (int int1 = 0; int1 < floatArray.length; int1 += 6) {
				float float8 = floatArray[int1];
				float float9 = floatArray[int1 + 1];
				float float10 = floatArray[int1 + 2];
				float float11 = floatArray[int1 + 3];
				float float12 = floatArray[int1 + 4];
				float float13 = floatArray[int1 + 5];
				WorldMapRenderer.m_vboLines.addElement(float1 + float8 * float3, float2 + float9 * float3, 0.0F, float5, float7, float6, float4);
				WorldMapRenderer.m_vboLines.addElement(float1 + float10 * float3, float2 + float11 * float3, 0.0F, float5, float7, float6, float4);
				WorldMapRenderer.m_vboLines.addElement(float1 + float10 * float3, float2 + float11 * float3, 0.0F, float5, float7, float6, float4);
				WorldMapRenderer.m_vboLines.addElement(float1 + float12 * float3, float2 + float13 * float3, 0.0F, float5, float7, float6, float4);
				WorldMapRenderer.m_vboLines.addElement(float1 + float12 * float3, float2 + float13 * float3, 0.0F, float5, float7, float6, float4);
				WorldMapRenderer.m_vboLines.addElement(float1 + float8 * float3, float2 + float9 * float3, 0.0F, float5, float7, float6, float4);
			}
		}

		void outlinePolygon(WorldMapGeometry worldMapGeometry, float float1, float float2, float float3) {
			WorldMapRenderer.m_vboLines.setMode(1);
			float float4 = 1.0F;
			float float5 = 0.8F;
			float float6 = 0.8F;
			float float7 = 0.8F;
			WorldMapRenderer.m_vboLines.setLineWidth(4.0F);
			for (int int1 = 0; int1 < worldMapGeometry.m_points.size(); ++int1) {
				WorldMapPoints worldMapPoints = (WorldMapPoints)worldMapGeometry.m_points.get(int1);
				for (int int2 = 0; int2 < worldMapPoints.numPoints(); ++int2) {
					int int3 = worldMapPoints.getX(int2);
					int int4 = worldMapPoints.getY(int2);
					int int5 = worldMapPoints.getX((int2 + 1) % worldMapPoints.numPoints());
					int int6 = worldMapPoints.getY((int2 + 1) % worldMapPoints.numPoints());
					WorldMapRenderer.m_vboLines.addElement(float1 + (float)int3 * float3, float2 + (float)int4 * float3, 0.0F, float7, float6, float5, float4);
					WorldMapRenderer.m_vboLines.addElement(float1 + (float)int5 * float3, float2 + (float)int6 * float3, 0.0F, float7, float6, float5, float4);
				}
			}

			WorldMapRenderer.m_vboLines.setLineWidth(1.0F);
		}

		public void drawTexture(Texture texture, WorldMapStyleLayer.RGBAf rGBAf, int int1, int int2, int int3, int int4) {
			if (texture != null && texture.isReady()) {
				WorldMapRenderer.m_vboLines.flush();
				WorldMapRenderer.m_vboLinesUV.flush();
				float float1 = this.m_worldScale;
				float float2 = ((float)int1 - this.m_centerWorldX) * float1;
				float float3 = ((float)int2 - this.m_centerWorldY) * float1;
				float float4 = float2 + (float)(int3 - int1) * float1;
				float float5 = float3 + (float)(int4 - int2) * float1;
				float float6 = PZMath.clamp(float2, this.m_renderCellX, this.m_renderCellX + 300.0F * float1);
				float float7 = PZMath.clamp(float3, this.m_renderCellY, this.m_renderCellY + 300.0F * float1);
				float float8 = PZMath.clamp(float4, this.m_renderCellX, this.m_renderCellX + 300.0F * float1);
				float float9 = PZMath.clamp(float5, this.m_renderCellY, this.m_renderCellY + 300.0F * float1);
				if (!(float6 >= float8) && !(float7 >= float9)) {
					float float10 = (float)texture.getWidth() / (float)(int3 - int1);
					float float11 = (float)texture.getHeight() / (float)(int4 - int2);
					GL11.glEnable(3553);
					GL11.glEnable(3042);
					GL11.glDisable(2929);
					if (texture.getID() == -1) {
						texture.bind();
					} else {
						GL11.glBindTexture(3553, Texture.lastTextureID = texture.getID());
						GL11.glTexParameteri(3553, 10241, 9728);
						GL11.glTexParameteri(3553, 10240, 9728);
					}

					float float12 = (float6 - float2) / ((float)texture.getWidthHW() * float1) * float10;
					float float13 = (float7 - float3) / ((float)texture.getHeightHW() * float1) * float11;
					float float14 = (float8 - float2) / ((float)texture.getWidthHW() * float1) * float10;
					float float15 = (float9 - float3) / ((float)texture.getHeightHW() * float1) * float11;
					WorldMapRenderer.m_vboLinesUV.setMode(7);
					WorldMapRenderer.m_vboLinesUV.startRun(texture.getTextureId());
					WorldMapRenderer.m_vboLinesUV.addQuad(float6, float7, float12, float13, float8, float9, float14, float15, 0.0F, rGBAf.r, rGBAf.g, rGBAf.b, rGBAf.a);
				}
			}
		}

		public void drawTextureTiled(Texture texture, WorldMapStyleLayer.RGBAf rGBAf, int int1, int int2, int int3, int int4, int int5, int int6) {
			if (texture != null && texture.isReady()) {
				if (int5 * 300 < int3 && (int5 + 1) * 300 > int1) {
					if (int6 * 300 < int4 && (int6 + 1) * 300 > int2) {
						WorldMapRenderer.m_vboLines.flush();
						float float1 = this.m_worldScale;
						int int7 = texture.getWidth();
						int int8 = texture.getHeight();
						int int9 = (int)(PZMath.floor((float)int5 * 300.0F / (float)int7) * (float)int7);
						int int10 = (int)(PZMath.floor((float)int6 * 300.0F / (float)int8) * (float)int8);
						int int11 = int9 + (int)Math.ceil((double)(((float)(int5 + 1) * 300.0F - (float)int9) / (float)int7)) * int7;
						int int12 = int10 + (int)Math.ceil((double)(((float)(int6 + 1) * 300.0F - (float)int10) / (float)int8)) * int8;
						float float2 = (float)PZMath.clamp(int9, int5 * 300, (int5 + 1) * 300);
						float float3 = (float)PZMath.clamp(int10, int6 * 300, (int6 + 1) * 300);
						float float4 = (float)PZMath.clamp(int11, int5 * 300, (int5 + 1) * 300);
						float float5 = (float)PZMath.clamp(int12, int6 * 300, (int6 + 1) * 300);
						float2 = PZMath.clamp(float2, (float)int1, (float)int3);
						float3 = PZMath.clamp(float3, (float)int2, (float)int4);
						float4 = PZMath.clamp(float4, (float)int1, (float)int3);
						float5 = PZMath.clamp(float5, (float)int2, (float)int4);
						float float6 = (float2 - (float)int1) / (float)int7;
						float float7 = (float3 - (float)int2) / (float)int8;
						float float8 = (float4 - (float)int1) / (float)int7;
						float float9 = (float5 - (float)int2) / (float)int8;
						float2 = (float2 - this.m_centerWorldX) * float1;
						float3 = (float3 - this.m_centerWorldY) * float1;
						float4 = (float4 - this.m_centerWorldX) * float1;
						float5 = (float5 - this.m_centerWorldY) * float1;
						float float10 = float6 * texture.xEnd;
						float float11 = float7 * texture.yEnd;
						float float12 = (float)((int)float8) + (float8 - (float)((int)float8)) * texture.xEnd;
						float float13 = (float)((int)float9) + (float9 - (float)((int)float9)) * texture.yEnd;
						GL11.glEnable(3553);
						if (texture.getID() == -1) {
							texture.bind();
						} else {
							GL11.glBindTexture(3553, Texture.lastTextureID = texture.getID());
							GL11.glTexParameteri(3553, 10241, 9728);
							GL11.glTexParameteri(3553, 10240, 9728);
							GL11.glTexParameteri(3553, 10242, 10497);
							GL11.glTexParameteri(3553, 10243, 10497);
						}

						WorldMapRenderer.m_vboLinesUV.setMode(7);
						WorldMapRenderer.m_vboLinesUV.startRun(texture.getTextureId());
						WorldMapRenderer.m_vboLinesUV.addQuad(float2, float3, float10, float11, float4, float5, float12, float13, 0.0F, rGBAf.r, rGBAf.g, rGBAf.b, rGBAf.a);
						GL11.glDisable(3553);
					}
				}
			}
		}

		public void drawTextureTiled(Texture texture, WorldMapStyleLayer.RGBAf rGBAf, int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8) {
			if (texture != null && texture.isReady()) {
				WorldMapRenderer.m_vboLines.flush();
				WorldMapRenderer.m_vboLinesUV.flush();
				float float1 = this.m_worldScale;
				float float2 = (float)int1;
				float float3 = (float)int2;
				float float4 = (float)int3;
				float float5 = (float)int4;
				float float6 = PZMath.clamp(float2, (float)(int7 * 300), (float)((int7 + 1) * 300));
				float float7 = PZMath.clamp(float3, (float)(int8 * 300), (float)((int8 + 1) * 300));
				float float8 = PZMath.clamp(float4, (float)(int7 * 300), (float)((int7 + 1) * 300));
				float float9 = PZMath.clamp(float5, (float)(int8 * 300), (float)((int8 + 1) * 300));
				float float10 = (float6 - (float)int1) / (float)int5;
				float float11 = (float7 - (float)int2) / (float)int6;
				float float12 = (float8 - (float)int1) / (float)int5;
				float float13 = (float9 - (float)int2) / (float)int6;
				float6 = (float6 - this.m_centerWorldX) * float1;
				float7 = (float7 - this.m_centerWorldY) * float1;
				float8 = (float8 - this.m_centerWorldX) * float1;
				float9 = (float9 - this.m_centerWorldY) * float1;
				float float14 = float10 * texture.xEnd;
				float float15 = float11 * texture.yEnd;
				float float16 = (float)((int)float12) + (float12 - (float)((int)float12)) * texture.xEnd;
				float float17 = (float)((int)float13) + (float13 - (float)((int)float13)) * texture.yEnd;
				GL11.glEnable(3553);
				if (texture.getID() == -1) {
					texture.bind();
				} else {
					GL11.glBindTexture(3553, Texture.lastTextureID = texture.getID());
					GL11.glTexParameteri(3553, 10241, 9728);
					GL11.glTexParameteri(3553, 10240, 9728);
					GL11.glTexParameteri(3553, 10242, 10497);
					GL11.glTexParameteri(3553, 10243, 10497);
				}

				GL11.glColor4f(rGBAf.r, rGBAf.g, rGBAf.b, rGBAf.a);
				GL11.glBegin(7);
				GL11.glTexCoord2f(float14, float15);
				GL11.glVertex2f(float6, float7);
				GL11.glTexCoord2f(float16, float15);
				GL11.glVertex2f(float8, float7);
				GL11.glTexCoord2f(float16, float17);
				GL11.glVertex2f(float8, float9);
				GL11.glTexCoord2f(float14, float17);
				GL11.glVertex2f(float6, float9);
				GL11.glEnd();
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glDisable(3553);
			}
		}

		void renderZones() {
			this.m_zoneSet.clear();
			for (int int1 = 0; int1 < this.m_rasterizeXY.size() - 1; int1 += 2) {
				int int2 = this.m_rasterizeXY_ints[int1];
				int int3 = this.m_rasterizeXY_ints[int1 + 1];
				if (this.m_renderer.m_visited == null || this.m_renderer.m_visited.isCellVisible(int2, int3)) {
					IsoMetaCell metaCell = IsoWorld.instance.MetaGrid.getCellData(int2, int3);
					if (metaCell != null) {
						metaCell.getZonesUnique(this.m_zoneSet);
					}
				}
			}

			this.m_zones.clear();
			this.m_zones.addAll(this.m_zoneSet);
			this.renderZones(this.m_zones, "Forest", 0.0F, 1.0F, 0.0F, 0.25F);
			this.renderZones(this.m_zones, "DeepForest", 0.0F, 0.5F, 0.0F, 0.25F);
			this.renderZones(this.m_zones, "Nav", 0.0F, 0.0F, 1.0F, 0.25F);
			this.renderZones(this.m_zones, "Vegitation", 1.0F, 1.0F, 0.0F, 0.25F);
		}

		void renderZones(ArrayList arrayList, String string, float float1, float float2, float float3, float float4) {
			WorldMapRenderer.m_vboLinesUV.flush();
			float float5 = this.m_worldScale;
			WorldMapRenderer.m_vboLines.setMode(4);
			Iterator iterator = arrayList.iterator();
			while (true) {
				float[] floatArray;
				int int1;
				float float6;
				float float7;
				float float8;
				float float9;
				float float10;
				float float11;
				do {
					IsoMetaGrid.Zone zone;
					label93: do {
						do {
							do {
								if (!iterator.hasNext()) {
									WorldMapRenderer.m_vboLines.setMode(1);
									WorldMapRenderer.m_vboLines.setLineWidth(2.0F);
									iterator = arrayList.iterator();
									while (true) {
										do {
											do {
												do {
													if (!iterator.hasNext()) {
														return;
													}

													zone = (IsoMetaGrid.Zone)iterator.next();
												}									 while (!string.equals(zone.type));

												float float12;
												if (zone.isRectangle()) {
													float float13 = ((float)zone.x - this.m_centerWorldX) * float5;
													float12 = ((float)zone.y - this.m_centerWorldY) * float5;
													float6 = ((float)(zone.x + zone.w) - this.m_centerWorldX) * float5;
													float7 = ((float)(zone.y + zone.h) - this.m_centerWorldY) * float5;
													WorldMapRenderer.m_vboLines.addLine(float13, float12, 0.0F, float6, float12, 0.0F, float1, float2, float3, 1.0F);
													WorldMapRenderer.m_vboLines.addLine(float6, float12, 0.0F, float6, float7, 0.0F, float1, float2, float3, 1.0F);
													WorldMapRenderer.m_vboLines.addLine(float6, float7, 0.0F, float13, float7, 0.0F, float1, float2, float3, 1.0F);
													WorldMapRenderer.m_vboLines.addLine(float13, float7, 0.0F, float13, float12, 0.0F, float1, float2, float3, 1.0F);
												}

												if (zone.isPolygon()) {
													for (int int2 = 0; int2 < zone.points.size(); int2 += 2) {
														float12 = ((float)zone.points.getQuick(int2) - this.m_centerWorldX) * float5;
														float6 = ((float)zone.points.getQuick(int2 + 1) - this.m_centerWorldY) * float5;
														float7 = ((float)zone.points.getQuick((int2 + 2) % zone.points.size()) - this.m_centerWorldX) * float5;
														float8 = ((float)zone.points.getQuick((int2 + 3) % zone.points.size()) - this.m_centerWorldY) * float5;
														WorldMapRenderer.m_vboLines.addLine(float12, float6, 0.0F, float7, float8, 0.0F, float1, float2, float3, 1.0F);
													}
												}
											}								 while (!zone.isPolyline());

											floatArray = zone.polylineOutlinePoints;
										}							 while (floatArray == null);

										for (int1 = 0; int1 < floatArray.length; int1 += 2) {
											float6 = (floatArray[int1] - this.m_centerWorldX) * float5;
											float7 = (floatArray[int1 + 1] - this.m_centerWorldY) * float5;
											float8 = (floatArray[(int1 + 2) % floatArray.length] - this.m_centerWorldX) * float5;
											float9 = (floatArray[(int1 + 3) % floatArray.length] - this.m_centerWorldY) * float5;
											WorldMapRenderer.m_vboLines.addLine(float6, float7, 0.0F, float8, float9, 0.0F, float1, float2, float3, 1.0F);
										}
									}
								}

								zone = (IsoMetaGrid.Zone)iterator.next();
							}				 while (!string.equals(zone.type));

							if (zone.isRectangle()) {
								WorldMapRenderer.m_vboLines.addQuad(((float)zone.x - this.m_centerWorldX) * float5, ((float)zone.y - this.m_centerWorldY) * float5, ((float)(zone.x + zone.w) - this.m_centerWorldX) * float5, ((float)(zone.y + zone.h) - this.m_centerWorldY) * float5, 0.0F, float1, float2, float3, float4);
							}

							if (!zone.isPolygon()) {
								continue label93;
							}

							floatArray = zone.getPolygonTriangles();
						}			 while (floatArray == null);

						for (int1 = 0; int1 < floatArray.length; int1 += 6) {
							float6 = (floatArray[int1] - this.m_centerWorldX) * float5;
							float7 = (floatArray[int1 + 1] - this.m_centerWorldY) * float5;
							float8 = (floatArray[int1 + 2] - this.m_centerWorldX) * float5;
							float9 = (floatArray[int1 + 3] - this.m_centerWorldY) * float5;
							float10 = (floatArray[int1 + 4] - this.m_centerWorldX) * float5;
							float11 = (floatArray[int1 + 5] - this.m_centerWorldY) * float5;
							WorldMapRenderer.m_vboLines.addTriangle(float6, float7, 0.0F, float8, float9, 0.0F, float10, float11, 0.0F, float1, float2, float3, float4);
						}
					}		 while (!zone.isPolyline());

					floatArray = zone.getPolylineOutlineTriangles();
				}	 while (floatArray == null);

				for (int1 = 0; int1 < floatArray.length; int1 += 6) {
					float6 = (floatArray[int1] - this.m_centerWorldX) * float5;
					float7 = (floatArray[int1 + 1] - this.m_centerWorldY) * float5;
					float8 = (floatArray[int1 + 2] - this.m_centerWorldX) * float5;
					float9 = (floatArray[int1 + 3] - this.m_centerWorldY) * float5;
					float10 = (floatArray[int1 + 4] - this.m_centerWorldX) * float5;
					float11 = (floatArray[int1 + 5] - this.m_centerWorldY) * float5;
					WorldMapRenderer.m_vboLines.addTriangle(float6, float7, 0.0F, float8, float9, 0.0F, float10, float11, 0.0F, float1, float2, float3, float4);
				}
			}
		}

		public void render() {
			try {
				PZGLUtil.pushAndLoadMatrix(5889, this.m_projection);
				PZGLUtil.pushAndLoadMatrix(5888, this.m_modelView);
				this.renderInternal();
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			} finally {
				PZGLUtil.popMatrix(5889);
				PZGLUtil.popMatrix(5888);
			}
		}

		private void renderInternal() {
			float float1 = this.m_worldScale;
			int int1 = (int)Math.max(this.uiToWorldX(0.0F, 0.0F), (float)this.m_worldMap.getMinXInSquares()) / 300;
			int int2 = (int)Math.max(this.uiToWorldY(0.0F, 0.0F), (float)this.m_worldMap.getMinYInSquares()) / 300;
			int int3 = (int)Math.min(this.uiToWorldX((float)this.getWidth(), (float)this.getHeight()), (float)(this.m_worldMap.m_maxX * 300)) / 300;
			int int4 = (int)Math.min(this.uiToWorldY((float)this.getWidth(), (float)this.getHeight()), (float)(this.m_worldMap.m_maxY * 300)) / 300;
			int1 = this.m_worldMap.getMinXInSquares();
			int2 = this.m_worldMap.getMinYInSquares();
			int3 = this.m_worldMap.m_maxX;
			int4 = this.m_worldMap.m_maxY;
			GL11.glViewport(this.m_x, Core.height - this.m_height - this.m_y, this.m_width, this.m_height);
			GLVertexBufferObject.funcs.glBindBuffer(GLVertexBufferObject.funcs.GL_ARRAY_BUFFER(), 0);
			GLVertexBufferObject.funcs.glBindBuffer(GLVertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), 0);
			GL11.glPolygonMode(1032, this.m_renderer.Wireframe.getValue() ? 6913 : 6914);
			if (this.m_renderer.ImagePyramid.getValue()) {
				this.renderImagePyramids();
			}

			this.calculateVisibleCells();
			if (this.m_renderer.Features.getValue()) {
				this.renderCellFeatures();
			}

			if (this.m_renderer.ForestZones.getValue()) {
				this.renderZones();
			}

			if (this.m_renderer.VisibleCells.getValue()) {
				this.renderVisibleCells();
			}

			WorldMapRenderer.m_vboLines.flush();
			WorldMapRenderer.m_vboLinesUV.flush();
			GL11.glEnableClientState(32884);
			GL11.glEnableClientState(32886);
			GL13.glActiveTexture(33984);
			GL13.glClientActiveTexture(33984);
			GL11.glEnableClientState(32888);
			GL11.glTexEnvi(8960, 8704, 8448);
			GL11.glPolygonMode(1032, 6914);
			GL11.glEnable(3042);
			SpriteRenderer.ringBuffer.restoreBoundTextures = true;
			SpriteRenderer.ringBuffer.restoreVBOs = true;
			if (this.m_renderer.m_visited != null) {
				this.m_renderer.m_visited.render(this.m_renderOriginX - (float)(this.m_worldMap.getMinXInSquares() - this.m_renderer.m_visited.getMinX() * 300) * float1, this.m_renderOriginY - (float)(this.m_worldMap.getMinYInSquares() - this.m_renderer.m_visited.getMinY() * 300) * float1, int1 / 300, int2 / 300, int3 / 300, int4 / 300, float1, this.m_renderer.BlurUnvisited.getValue());
				if (this.m_renderer.UnvisitedGrid.getValue()) {
					this.m_renderer.m_visited.renderGrid(this.m_renderOriginX - (float)(this.m_worldMap.getMinXInSquares() - this.m_renderer.m_visited.getMinX() * 300) * float1, this.m_renderOriginY - (float)(this.m_worldMap.getMinYInSquares() - this.m_renderer.m_visited.getMinY() * 300) * float1, int1 / 300, int2 / 300, int3 / 300, int4 / 300, float1, this.m_zoomF);
				}
			}

			this.renderPlayers();
			if (this.m_renderer.CellGrid.getValue()) {
				this.renderCellGrid(int1 / 300, int2 / 300, int3 / 300, int4 / 300);
			}

			if (Core.bDebug) {
			}

			this.paintAreasOutsideBounds(int1, int2, int3, int4, float1);
			if (this.m_renderer.WorldBounds.getValue()) {
				this.renderWorldBounds();
			}

			WorldMapRenderer.m_vboLines.flush();
			WorldMapRenderer.m_vboLinesUV.flush();
			GL11.glViewport(0, 0, Core.width, Core.height);
		}

		private void rasterizeCellsCallback(int int1, int int2) {
			int int3 = int1 + int2 * this.m_worldMap.getWidthInCells();
			if (!this.m_rasterizeSet.contains(int3)) {
				for (int int4 = int2 * this.m_rasterizeMult; int4 < int2 * this.m_rasterizeMult + this.m_rasterizeMult; ++int4) {
					for (int int5 = int1 * this.m_rasterizeMult; int5 < int1 * this.m_rasterizeMult + this.m_rasterizeMult; ++int5) {
						if (int5 >= this.m_worldMap.getMinXInCells() && int5 <= this.m_worldMap.getMaxXInCells() && int4 >= this.m_worldMap.getMinYInCells() && int4 <= this.m_worldMap.getMaxYInCells()) {
							this.m_rasterizeSet.add(int3);
							this.m_rasterizeXY.add(int5);
							this.m_rasterizeXY.add(int4);
						}
					}
				}
			}
		}

		private void rasterizeTilesCallback(int int1, int int2) {
			int int3 = int1 + int2 * 1000;
			if (!this.m_rasterizeSet.contains(int3)) {
				if (!((float)int1 < this.m_rasterizeMinTileX) && !((float)int1 > this.m_rasterizeMaxTileX) && !((float)int2 < this.m_rasterizeMinTileY) && !((float)int2 > this.m_rasterizeMaxTileY)) {
					this.m_rasterizeSet.add(int3);
					this.m_rasterizeXY.add(int1);
					this.m_rasterizeXY.add(int2);
				}
			}
		}

		private void calculateVisibleCells() {
			boolean boolean1 = Core.bDebug && this.m_renderer.VisibleCells.getValue();
			int int1 = boolean1 ? 200 : 0;
			float float1 = this.m_worldScale;
			if (1.0F / float1 > 100.0F) {
				this.m_rasterizeXY.clear();
				for (int int2 = this.m_worldMap.getMinYInCells(); int2 <= this.m_worldMap.getMaxYInCells(); ++int2) {
					for (int int3 = this.m_worldMap.getMinXInCells(); int3 <= this.m_worldMap.getMaxYInCells(); ++int3) {
						this.m_rasterizeXY.add(int3);
						this.m_rasterizeXY.add(int2);
					}
				}

				if (this.m_rasterizeXY_ints == null || this.m_rasterizeXY_ints.length < this.m_rasterizeXY.size()) {
					this.m_rasterizeXY_ints = new int[this.m_rasterizeXY.size()];
				}

				this.m_rasterizeXY_ints = this.m_rasterizeXY.toArray(this.m_rasterizeXY_ints);
			} else {
				float float2 = this.uiToWorldX((float)int1 + 0.0F, (float)int1 + 0.0F) / 300.0F;
				float float3 = this.uiToWorldY((float)int1 + 0.0F, (float)int1 + 0.0F) / 300.0F;
				float float4 = this.uiToWorldX((float)(this.getWidth() - int1), 0.0F + (float)int1) / 300.0F;
				float float5 = this.uiToWorldY((float)(this.getWidth() - int1), 0.0F + (float)int1) / 300.0F;
				float float6 = this.uiToWorldX((float)(this.getWidth() - int1), (float)(this.getHeight() - int1)) / 300.0F;
				float float7 = this.uiToWorldY((float)(this.getWidth() - int1), (float)(this.getHeight() - int1)) / 300.0F;
				float float8 = this.uiToWorldX(0.0F + (float)int1, (float)(this.getHeight() - int1)) / 300.0F;
				float float9 = this.uiToWorldY(0.0F + (float)int1, (float)(this.getHeight() - int1)) / 300.0F;
				int int4;
				for (int4 = 1; this.triangleArea(float8 / (float)int4, float9 / (float)int4, float6 / (float)int4, float7 / (float)int4, float4 / (float)int4, float5 / (float)int4) + this.triangleArea(float4 / (float)int4, float5 / (float)int4, float2 / (float)int4, float3 / (float)int4, float8 / (float)int4, float9 / (float)int4) > 80.0F; ++int4) {
				}

				this.m_rasterizeMult = int4;
				this.m_rasterizeXY.clear();
				this.m_rasterizeSet.clear();
				this.m_rasterize.scanTriangle(float8 / (float)int4, float9 / (float)int4, float6 / (float)int4, float7 / (float)int4, float4 / (float)int4, float5 / (float)int4, 0, 1000, this::rasterizeCellsCallback);
				this.m_rasterize.scanTriangle(float4 / (float)int4, float5 / (float)int4, float2 / (float)int4, float3 / (float)int4, float8 / (float)int4, float9 / (float)int4, 0, 1000, this::rasterizeCellsCallback);
				if (this.m_rasterizeXY_ints == null || this.m_rasterizeXY_ints.length < this.m_rasterizeXY.size()) {
					this.m_rasterizeXY_ints = new int[this.m_rasterizeXY.size()];
				}

				this.m_rasterizeXY_ints = this.m_rasterizeXY.toArray(this.m_rasterizeXY_ints);
			}
		}

		void renderVisibleCells() {
			boolean boolean1 = Core.bDebug && this.m_renderer.VisibleCells.getValue();
			int int1 = boolean1 ? 200 : 0;
			float float1 = this.m_worldScale;
			if (!(1.0F / float1 > 100.0F)) {
				WorldMapRenderer.m_vboLines.setMode(4);
				float float2;
				float float3;
				float float4;
				float float5;
				for (int int2 = 0; int2 < this.m_rasterizeXY.size(); int2 += 2) {
					int int3 = this.m_rasterizeXY.get(int2);
					int int4 = this.m_rasterizeXY.get(int2 + 1);
					float2 = this.m_renderOriginX + (float)(int3 * 300 - this.m_worldMap.getMinXInSquares()) * float1;
					float3 = this.m_renderOriginY + (float)(int4 * 300 - this.m_worldMap.getMinYInSquares()) * float1;
					float4 = this.m_renderOriginX + (float)((int3 + 1) * 300 - this.m_worldMap.getMinXInSquares()) * float1;
					float5 = this.m_renderOriginY + (float)((int4 + 1) * 300 - this.m_worldMap.getMinYInSquares()) * float1;
					WorldMapRenderer.m_vboLines.addElement(float2, float3, 0.0F, 0.0F, 1.0F, 0.0F, 0.2F);
					WorldMapRenderer.m_vboLines.addElement(float4, float3, 0.0F, 0.0F, 1.0F, 0.0F, 0.2F);
					WorldMapRenderer.m_vboLines.addElement(float2, float5, 0.0F, 0.0F, 1.0F, 0.0F, 0.2F);
					WorldMapRenderer.m_vboLines.addElement(float4, float3, 0.0F, 0.0F, 0.0F, 1.0F, 0.2F);
					WorldMapRenderer.m_vboLines.addElement(float4, float5, 0.0F, 0.0F, 0.0F, 1.0F, 0.2F);
					WorldMapRenderer.m_vboLines.addElement(float2, float5, 0.0F, 0.0F, 0.0F, 1.0F, 0.2F);
				}

				WorldMapRenderer.m_vboLines.flush();
				float float6 = this.uiToWorldX((float)int1 + 0.0F, (float)int1 + 0.0F) / 300.0F;
				float float7 = this.uiToWorldY((float)int1 + 0.0F, (float)int1 + 0.0F) / 300.0F;
				float float8 = this.uiToWorldX((float)(this.getWidth() - int1), 0.0F + (float)int1) / 300.0F;
				float2 = this.uiToWorldY((float)(this.getWidth() - int1), 0.0F + (float)int1) / 300.0F;
				float3 = this.uiToWorldX((float)(this.getWidth() - int1), (float)(this.getHeight() - int1)) / 300.0F;
				float4 = this.uiToWorldY((float)(this.getWidth() - int1), (float)(this.getHeight() - int1)) / 300.0F;
				float5 = this.uiToWorldX(0.0F + (float)int1, (float)(this.getHeight() - int1)) / 300.0F;
				float float9 = this.uiToWorldY(0.0F + (float)int1, (float)(this.getHeight() - int1)) / 300.0F;
				WorldMapRenderer.m_vboLines.setMode(1);
				WorldMapRenderer.m_vboLines.setLineWidth(4.0F);
				WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + (float5 * 300.0F - (float)this.m_worldMap.getMinXInSquares()) * float1, this.m_renderOriginY + (float9 * 300.0F - (float)this.m_worldMap.getMinYInSquares()) * float1, 0.0F, this.m_renderOriginX + (float3 * 300.0F - (float)this.m_worldMap.getMinXInSquares()) * float1, this.m_renderOriginY + (float4 * 300.0F - (float)this.m_worldMap.getMinYInSquares()) * float1, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F);
				WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + (float3 * 300.0F - (float)this.m_worldMap.getMinXInSquares()) * float1, this.m_renderOriginY + (float4 * 300.0F - (float)this.m_worldMap.getMinYInSquares()) * float1, 0.0F, this.m_renderOriginX + (float8 * 300.0F - (float)this.m_worldMap.getMinXInSquares()) * float1, this.m_renderOriginY + (float2 * 300.0F - (float)this.m_worldMap.getMinYInSquares()) * float1, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F);
				WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + (float8 * 300.0F - (float)this.m_worldMap.getMinXInSquares()) * float1, this.m_renderOriginY + (float2 * 300.0F - (float)this.m_worldMap.getMinYInSquares()) * float1, 0.0F, this.m_renderOriginX + (float5 * 300.0F - (float)this.m_worldMap.getMinXInSquares()) * float1, this.m_renderOriginY + (float9 * 300.0F - (float)this.m_worldMap.getMinYInSquares()) * float1, 0.0F, 0.5F, 0.5F, 0.5F, 1.0F);
				WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + (float8 * 300.0F - (float)this.m_worldMap.getMinXInSquares()) * float1, this.m_renderOriginY + (float2 * 300.0F - (float)this.m_worldMap.getMinYInSquares()) * float1, 0.0F, this.m_renderOriginX + (float6 * 300.0F - (float)this.m_worldMap.getMinXInSquares()) * float1, this.m_renderOriginY + (float7 * 300.0F - (float)this.m_worldMap.getMinYInSquares()) * float1, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F);
				WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + (float6 * 300.0F - (float)this.m_worldMap.getMinXInSquares()) * float1, this.m_renderOriginY + (float7 * 300.0F - (float)this.m_worldMap.getMinYInSquares()) * float1, 0.0F, this.m_renderOriginX + (float5 * 300.0F - (float)this.m_worldMap.getMinXInSquares()) * float1, this.m_renderOriginY + (float9 * 300.0F - (float)this.m_worldMap.getMinYInSquares()) * float1, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F);
			}
		}

		void calcVisiblePyramidTiles(WorldMapImages worldMapImages) {
			if (Core.bDebug) {
			}

			boolean boolean1 = false;
			int int1 = boolean1 ? 200 : 0;
			float float1 = this.m_worldScale;
			int int2 = worldMapImages.getZoom(this.m_zoomF);
			short short1 = 256;
			float float2 = (float)(short1 * (1 << int2));
			int int3 = worldMapImages.getMinX();
			int int4 = worldMapImages.getMinY();
			float float3 = (this.uiToWorldX((float)int1 + 0.0F, (float)int1 + 0.0F) - (float)int3) / float2;
			float float4 = (this.uiToWorldY((float)int1 + 0.0F, (float)int1 + 0.0F) - (float)int4) / float2;
			float float5 = (this.uiToWorldX((float)(this.getWidth() - int1), 0.0F + (float)int1) - (float)int3) / float2;
			float float6 = (this.uiToWorldY((float)(this.getWidth() - int1), 0.0F + (float)int1) - (float)int4) / float2;
			float float7 = (this.uiToWorldX((float)(this.getWidth() - int1), (float)(this.getHeight() - int1)) - (float)int3) / float2;
			float float8 = (this.uiToWorldY((float)(this.getWidth() - int1), (float)(this.getHeight() - int1)) - (float)int4) / float2;
			float float9 = (this.uiToWorldX(0.0F + (float)int1, (float)(this.getHeight() - int1)) - (float)int3) / float2;
			float float10 = (this.uiToWorldY(0.0F + (float)int1, (float)(this.getHeight() - int1)) - (float)int4) / float2;
			if (boolean1) {
				WorldMapRenderer.m_vboLines.setMode(1);
				WorldMapRenderer.m_vboLines.setLineWidth(4.0F);
				WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + float9 * float2 * float1, this.m_renderOriginY + float10 * float2 * float1, 0.0F, this.m_renderOriginX + float7 * float2 * float1, this.m_renderOriginY + float8 * float2 * float1, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F);
				WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + float7 * float2 * float1, this.m_renderOriginY + float8 * float2 * float1, 0.0F, this.m_renderOriginX + float5 * float2 * float1, this.m_renderOriginY + float6 * float2 * float1, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F);
				WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + float5 * float2 * float1, this.m_renderOriginY + float6 * float2 * float1, 0.0F, this.m_renderOriginX + float9 * float2 * float1, this.m_renderOriginY + float10 * float2 * float1, 0.0F, 0.5F, 0.5F, 0.5F, 1.0F);
				WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + float5 * float2 * float1, this.m_renderOriginY + float6 * float2 * float1, 0.0F, this.m_renderOriginX + float3 * float2 * float1, this.m_renderOriginY + float4 * float2 * float1, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F);
				WorldMapRenderer.m_vboLines.addLine(this.m_renderOriginX + float3 * float2 * float1, this.m_renderOriginY + float4 * float2 * float1, 0.0F, this.m_renderOriginX + float9 * float2 * float1, this.m_renderOriginY + float10 * float2 * float1, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F);
			}

			this.m_rasterizeXY.clear();
			this.m_rasterizeSet.clear();
			this.m_rasterizeMinTileX = (float)((int)((float)(this.m_worldMap.getMinXInSquares() - worldMapImages.getMinX()) / float2));
			this.m_rasterizeMinTileY = (float)((int)((float)(this.m_worldMap.getMinYInSquares() - worldMapImages.getMinY()) / float2));
			this.m_rasterizeMaxTileX = (float)(this.m_worldMap.getMaxXInSquares() - worldMapImages.getMinX()) / float2;
			this.m_rasterizeMaxTileY = (float)(this.m_worldMap.getMaxYInSquares() - worldMapImages.getMinY()) / float2;
			this.m_rasterize.scanTriangle(float9, float10, float7, float8, float5, float6, 0, 1000, this::rasterizeTilesCallback);
			this.m_rasterize.scanTriangle(float5, float6, float3, float4, float9, float10, 0, 1000, this::rasterizeTilesCallback);
			if (this.m_rasterizeXY_ints == null || this.m_rasterizeXY_ints.length < this.m_rasterizeXY.size()) {
				this.m_rasterizeXY_ints = new int[this.m_rasterizeXY.size()];
			}

			this.m_rasterizeXY_ints = this.m_rasterizeXY.toArray(this.m_rasterizeXY_ints);
			if (boolean1) {
				WorldMapRenderer.m_vboLines.setMode(4);
				for (int int5 = 0; int5 < this.m_rasterizeXY.size(); int5 += 2) {
					int int6 = this.m_rasterizeXY.get(int5);
					int int7 = this.m_rasterizeXY.get(int5 + 1);
					float float11 = this.m_renderOriginX + (float)int6 * float2 * float1;
					float float12 = this.m_renderOriginY + (float)int7 * float2 * float1;
					float float13 = this.m_renderOriginX + (float)(int6 + 1) * float2 * float1;
					float float14 = this.m_renderOriginY + (float)(int7 + 1) * float2 * float1;
					WorldMapRenderer.m_vboLines.addElement(float11, float12, 0.0F, 0.0F, 1.0F, 0.0F, 0.2F);
					WorldMapRenderer.m_vboLines.addElement(float13, float12, 0.0F, 0.0F, 1.0F, 0.0F, 0.2F);
					WorldMapRenderer.m_vboLines.addElement(float11, float14, 0.0F, 0.0F, 1.0F, 0.0F, 0.2F);
					WorldMapRenderer.m_vboLines.addElement(float13, float12, 0.0F, 0.0F, 0.0F, 1.0F, 0.2F);
					WorldMapRenderer.m_vboLines.addElement(float13, float14, 0.0F, 0.0F, 0.0F, 1.0F, 0.2F);
					WorldMapRenderer.m_vboLines.addElement(float11, float14, 0.0F, 0.0F, 0.0F, 1.0F, 0.2F);
				}

				WorldMapRenderer.m_vboLines.flush();
			}
		}

		void renderImagePyramids() {
			for (int int1 = this.m_worldMap.getImagesCount() - 1; int1 >= 0; --int1) {
				WorldMapImages worldMapImages = this.m_worldMap.getImagesByIndex(int1);
				this.renderImagePyramid(worldMapImages);
				GL11.glDisable(3553);
			}
		}

		void renderImagePyramid(WorldMapImages worldMapImages) {
			float float1 = this.m_worldScale;
			short short1 = 256;
			int int1 = worldMapImages.getZoom(this.m_zoomF);
			float float2 = (float)(short1 * (1 << int1));
			this.calcVisiblePyramidTiles(worldMapImages);
			GL11.glEnable(3553);
			GL11.glEnable(3042);
			WorldMapRenderer.m_vboLinesUV.setMode(4);
			int int2 = PZMath.clamp(worldMapImages.getMinX(), this.m_worldMap.getMinXInSquares(), this.m_worldMap.getMaxXInSquares());
			int int3 = PZMath.clamp(worldMapImages.getMinY(), this.m_worldMap.getMinYInSquares(), this.m_worldMap.getMaxYInSquares());
			int int4 = PZMath.clamp(worldMapImages.getMaxX(), this.m_worldMap.getMinXInSquares(), this.m_worldMap.getMaxXInSquares() + 1);
			int int5 = PZMath.clamp(worldMapImages.getMaxY(), this.m_worldMap.getMinYInSquares(), this.m_worldMap.getMaxYInSquares() + 1);
			for (int int6 = 0; int6 < this.m_rasterizeXY.size() - 1; int6 += 2) {
				int int7 = this.m_rasterizeXY_ints[int6];
				int int8 = this.m_rasterizeXY_ints[int6 + 1];
				TextureID textureID = worldMapImages.getPyramid().getTexture(int7, int8, int1);
				if (textureID != null && textureID.isReady()) {
					WorldMapRenderer.m_vboLinesUV.startRun(textureID);
					float float3 = (float)worldMapImages.getMinX() + (float)int7 * float2;
					float float4 = (float)worldMapImages.getMinY() + (float)int8 * float2;
					float float5 = float3 + float2;
					float float6 = float4 + float2;
					float float7 = PZMath.clamp(float3, (float)int2, (float)int4);
					float float8 = PZMath.clamp(float4, (float)int3, (float)int5);
					float float9 = PZMath.clamp(float5, (float)int2, (float)int4);
					float float10 = PZMath.clamp(float6, (float)int3, (float)int5);
					float float11 = (float7 - this.m_centerWorldX) * float1;
					float float12 = (float8 - this.m_centerWorldY) * float1;
					float float13 = (float9 - this.m_centerWorldX) * float1;
					float float14 = (float8 - this.m_centerWorldY) * float1;
					float float15 = (float9 - this.m_centerWorldX) * float1;
					float float16 = (float10 - this.m_centerWorldY) * float1;
					float float17 = (float7 - this.m_centerWorldX) * float1;
					float float18 = (float10 - this.m_centerWorldY) * float1;
					float float19 = (float7 - float3) / float2;
					float float20 = (float8 - float4) / float2;
					float float21 = (float9 - float3) / float2;
					float float22 = (float8 - float4) / float2;
					float float23 = (float9 - float3) / float2;
					float float24 = (float10 - float4) / float2;
					float float25 = (float7 - float3) / float2;
					float float26 = (float10 - float4) / float2;
					float float27 = 1.0F;
					float float28 = 1.0F;
					float float29 = 1.0F;
					float float30 = 1.0F;
					WorldMapRenderer.m_vboLinesUV.addElement(float11, float12, 0.0F, float19, float20, float27, float28, float29, float30);
					WorldMapRenderer.m_vboLinesUV.addElement(float13, float14, 0.0F, float21, float22, float27, float28, float29, float30);
					WorldMapRenderer.m_vboLinesUV.addElement(float17, float18, 0.0F, float25, float26, float27, float28, float29, float30);
					WorldMapRenderer.m_vboLinesUV.addElement(float13, float14, 0.0F, float21, float22, float27, float28, float29, float30);
					WorldMapRenderer.m_vboLinesUV.addElement(float15, float16, 0.0F, float23, float24, float27, float28, float29, float30);
					WorldMapRenderer.m_vboLinesUV.addElement(float17, float18, 0.0F, float25, float26, float27, float28, float29, float30);
					if (this.m_renderer.TileGrid.getValue()) {
						WorldMapRenderer.m_vboLinesUV.flush();
						WorldMapRenderer.m_vboLines.setMode(1);
						WorldMapRenderer.m_vboLines.setLineWidth(2.0F);
						WorldMapRenderer.m_vboLines.addLine((float3 - this.m_centerWorldX) * float1, (float4 - this.m_centerWorldY) * float1, 0.0F, (float5 - this.m_centerWorldX) * float1, (float4 - this.m_centerWorldY) * float1, 0.0F, 1.0F, 0.0F, 0.0F, 0.5F);
						WorldMapRenderer.m_vboLines.addLine((float3 - this.m_centerWorldX) * float1, (float6 - this.m_centerWorldY) * float1, 0.0F, (float5 - this.m_centerWorldX) * float1, (float6 - this.m_centerWorldY) * float1, 0.0F, 1.0F, 0.0F, 0.0F, 0.5F);
						WorldMapRenderer.m_vboLines.addLine((float5 - this.m_centerWorldX) * float1, (float4 - this.m_centerWorldY) * float1, 0.0F, (float5 - this.m_centerWorldX) * float1, (float6 - this.m_centerWorldY) * float1, 0.0F, 1.0F, 0.0F, 0.0F, 0.5F);
						WorldMapRenderer.m_vboLines.addLine((float3 - this.m_centerWorldX) * float1, (float4 - this.m_centerWorldY) * float1, 0.0F, (float3 - this.m_centerWorldX) * float1, (float6 - this.m_centerWorldY) * float1, 0.0F, 1.0F, 0.0F, 0.0F, 0.5F);
						WorldMapRenderer.m_vboLines.flush();
					}
				}
			}
		}

		void renderImagePyramidGrid(WorldMapImages worldMapImages) {
			float float1 = this.m_worldScale;
			short short1 = 256;
			int int1 = worldMapImages.getZoom(this.m_zoomF);
			float float2 = (float)(short1 * (1 << int1));
			float float3 = ((float)worldMapImages.getMinX() - this.m_centerWorldX) * float1;
			float float4 = ((float)worldMapImages.getMinY() - this.m_centerWorldY) * float1;
			int int2 = (int)Math.ceil((double)((float)(worldMapImages.getMaxX() - worldMapImages.getMinX()) / float2));
			int int3 = (int)Math.ceil((double)((float)(worldMapImages.getMaxY() - worldMapImages.getMinY()) / float2));
			float float5 = float3;
			float float6 = float4;
			float float7 = float3 + (float)int2 * float2 * float1;
			float float8 = float4 + (float)int3 * float2 * float1;
			WorldMapRenderer.m_vboLines.setMode(1);
			WorldMapRenderer.m_vboLines.setLineWidth(2.0F);
			int int4;
			for (int4 = 0; int4 < int2 + 1; ++int4) {
				WorldMapRenderer.m_vboLines.addLine(float3 + (float)int4 * float2 * float1, float6, 0.0F, float3 + (float)int4 * float2 * float1, float8, 0.0F, 1.0F, 0.0F, 0.0F, 0.5F);
			}

			for (int4 = 0; int4 < int3 + 1; ++int4) {
				WorldMapRenderer.m_vboLines.addLine(float5, float4 + (float)int4 * float2 * float1, 0.0F, float7, float4 + (float)int4 * float2 * float1, 0.0F, 1.0F, 0.0F, 0.0F, 0.5F);
			}

			WorldMapRenderer.m_vboLines.flush();
		}

		float triangleArea(float float1, float float2, float float3, float float4, float float5, float float6) {
			float float7 = Vector2f.length(float3 - float1, float4 - float2);
			float float8 = Vector2f.length(float5 - float3, float6 - float4);
			float float9 = Vector2f.length(float1 - float5, float2 - float6);
			float float10 = (float7 + float8 + float9) / 2.0F;
			return (float)Math.sqrt((double)(float10 * (float10 - float7) * (float10 - float8) * (float10 - float9)));
		}

		void paintAreasOutsideBounds(int int1, int int2, int int3, int int4, float float1) {
			float float2 = this.m_renderOriginX - (float)(int1 % 300) * float1;
			float float3 = this.m_renderOriginY - (float)(int2 % 300) * float1;
			float float4 = this.m_renderOriginX + (float)((this.m_worldMap.getMaxXInCells() + 1) * 300 - int1) * float1;
			float float5 = this.m_renderOriginY + (float)((this.m_worldMap.getMaxYInCells() + 1) * 300 - int2) * float1;
			float float6 = 0.0F;
			WorldMapStyleLayer.RGBAf rGBAf = this.m_fill;
			float float7;
			if (int1 % 300 != 0) {
				float7 = this.m_renderOriginX;
				WorldMapRenderer.m_vboLines.setMode(4);
				WorldMapRenderer.m_vboLines.addQuad(float2, float3, float7, float5, float6, rGBAf.r, rGBAf.g, rGBAf.b, rGBAf.a);
			}

			float float8;
			if (int2 % 300 != 0) {
				float8 = this.m_renderOriginX;
				float7 = float8 + (float)this.m_worldMap.getWidthInSquares() * this.m_worldScale;
				float float9 = this.m_renderOriginY;
				WorldMapRenderer.m_vboLines.setMode(4);
				WorldMapRenderer.m_vboLines.addQuad(float8, float3, float7, float9, float6, rGBAf.r, rGBAf.g, rGBAf.b, rGBAf.a);
			}

			if (int3 + 1 != 0) {
				float8 = this.m_renderOriginX + (float)(int3 - int1 + 1) * float1;
				WorldMapRenderer.m_vboLines.setMode(4);
				WorldMapRenderer.m_vboLines.addQuad(float8, float3, float4, float5, float6, rGBAf.r, rGBAf.g, rGBAf.b, rGBAf.a);
			}

			if (int4 + 1 != 0) {
				float8 = this.m_renderOriginX;
				float float10 = this.m_renderOriginY + (float)this.m_worldMap.getHeightInSquares() * float1;
				float7 = this.m_renderOriginX + (float)this.m_worldMap.getWidthInSquares() * float1;
				WorldMapRenderer.m_vboLines.setMode(4);
				WorldMapRenderer.m_vboLines.addQuad(float8, float10, float7, float5, float6, rGBAf.r, rGBAf.g, rGBAf.b, rGBAf.a);
			}
		}

		void renderWorldBounds() {
			float float1 = this.m_renderOriginX;
			float float2 = this.m_renderOriginY;
			float float3 = float1 + (float)this.m_worldMap.getWidthInSquares() * this.m_worldScale;
			float float4 = float2 + (float)this.m_worldMap.getHeightInSquares() * this.m_worldScale;
			this.renderDropShadow();
			WorldMapRenderer.m_vboLines.setMode(1);
			WorldMapRenderer.m_vboLines.setLineWidth(2.0F);
			float float5 = 0.5F;
			WorldMapRenderer.m_vboLines.addLine(float1, float2, 0.0F, float3, float2, 0.0F, float5, float5, float5, 1.0F);
			WorldMapRenderer.m_vboLines.addLine(float3, float2, 0.0F, float3, float4, 0.0F, float5, float5, float5, 1.0F);
			WorldMapRenderer.m_vboLines.addLine(float3, float4, 0.0F, float1, float4, 0.0F, float5, float5, float5, 1.0F);
			WorldMapRenderer.m_vboLines.addLine(float1, float4, 0.0F, float1, float2, 0.0F, float5, float5, float5, 1.0F);
		}

		private void renderDropShadow() {
			float float1 = (float)this.m_renderer.m_dropShadowWidth * ((float)this.m_renderer.getHeight() / 1080.0F) * this.m_worldScale / this.m_renderer.getWorldScale(this.m_renderer.getBaseZoom());
			if (!(float1 < 2.0F)) {
				float float2 = this.m_renderOriginX;
				float float3 = this.m_renderOriginY;
				float float4 = float2 + (float)this.m_worldMap.getWidthInSquares() * this.m_worldScale;
				float float5 = float3 + (float)this.m_worldMap.getHeightInSquares() * this.m_worldScale;
				WorldMapRenderer.m_vboLines.setMode(4);
				WorldMapRenderer.m_vboLines.addElement(float2 + float1, float5, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F);
				WorldMapRenderer.m_vboLines.addElement(float4, float5, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F);
				WorldMapRenderer.m_vboLines.addElement(float2 + float1, float5 + float1, 0.0F, 0.5F, 0.5F, 0.5F, 0.0F);
				WorldMapRenderer.m_vboLines.addElement(float4, float5, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F);
				WorldMapRenderer.m_vboLines.addElement(float4 + float1, float5 + float1, 0.0F, 0.5F, 0.5F, 0.5F, 0.0F);
				WorldMapRenderer.m_vboLines.addElement(float2 + float1, float5 + float1, 0.0F, 0.5F, 0.5F, 0.5F, 0.0F);
				WorldMapRenderer.m_vboLines.addElement(float4, float3 + float1, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F);
				WorldMapRenderer.m_vboLines.addElement(float4 + float1, float3 + float1, 0.0F, 0.5F, 0.5F, 0.5F, 0.0F);
				WorldMapRenderer.m_vboLines.addElement(float4, float5, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F);
				WorldMapRenderer.m_vboLines.addElement(float4 + float1, float3 + float1, 0.0F, 0.5F, 0.5F, 0.5F, 0.0F);
				WorldMapRenderer.m_vboLines.addElement(float4 + float1, float5 + float1, 0.0F, 0.5F, 0.5F, 0.5F, 0.0F);
				WorldMapRenderer.m_vboLines.addElement(float4, float5, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F);
			}
		}

		public void postRender() {
			for (int int1 = 0; int1 < this.m_playerRenderData.length; ++int1) {
				WorldMapRenderer.PlayerRenderData playerRenderData = this.m_playerRenderData[int1];
				if (playerRenderData.m_modelSlotRenderData != null) {
					playerRenderData.m_modelSlotRenderData.postRender();
				}
			}
		}
	}

	private static final class CharacterModelCamera extends ModelCamera {
		float m_worldScale;
		float m_angle;
		float m_playerX;
		float m_playerY;
		boolean m_bVehicle;

		public void Begin() {
			Matrix4f matrix4f = WorldMapRenderer.allocMatrix4f();
			matrix4f.identity();
			matrix4f.translate(this.m_playerX * this.m_worldScale, this.m_playerY * this.m_worldScale, 0.0F);
			matrix4f.rotateX(1.5707964F);
			matrix4f.rotateY(this.m_angle + 4.712389F);
			if (this.m_bVehicle) {
				matrix4f.scale(this.m_worldScale);
			} else {
				matrix4f.scale(1.5F * this.m_worldScale);
			}

			PZGLUtil.pushAndMultMatrix(5888, matrix4f);
			WorldMapRenderer.releaseMatrix4f(matrix4f);
		}

		public void End() {
			PZGLUtil.popMatrix(5888);
		}
	}

	public final class WorldMapBooleanOption extends BooleanConfigOption {

		public WorldMapBooleanOption(String string, boolean boolean1) {
			super(string, boolean1);
			WorldMapRenderer.this.options.add(this);
		}
	}

	public final class WorldMapDoubleOption extends DoubleConfigOption {

		public WorldMapDoubleOption(String string, double double1, double double2, double double3) {
			super(string, double1, double2, double3);
			WorldMapRenderer.this.options.add(this);
		}
	}

	private static final class PlayerRenderData {
		ModelSlotRenderData m_modelSlotRenderData;
		float m_angle;
		float m_x;
		float m_y;
	}
}
