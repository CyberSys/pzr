package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import zombie.core.math.PZMath;
import zombie.debug.LineDrawer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.popman.ObjectPool;
import zombie.util.Type;


public class VehicleStorySpawner {
	private static final VehicleStorySpawner instance = new VehicleStorySpawner();
	private static final Vector2 s_vector2_1 = new Vector2();
	private static final Vector2 s_vector2_2 = new Vector2();
	private static final ObjectPool s_elementPool = new ObjectPool(VehicleStorySpawner.Element::new);
	private static final int[] s_AABB = new int[4];
	public final ArrayList m_elements = new ArrayList();
	public final HashMap m_storyParams = new HashMap();

	public static VehicleStorySpawner getInstance() {
		return instance;
	}

	public void clear() {
		s_elementPool.release((List)this.m_elements);
		this.m_elements.clear();
		this.m_storyParams.clear();
	}

	public VehicleStorySpawner.Element addElement(String string, float float1, float float2, float float3, float float4, float float5) {
		VehicleStorySpawner.Element element = ((VehicleStorySpawner.Element)s_elementPool.alloc()).init(string, float1, float2, float3, float4, float5);
		this.m_elements.add(element);
		return element;
	}

	public void setParameter(String string, boolean boolean1) {
		this.m_storyParams.put(string, boolean1 ? Boolean.TRUE : Boolean.FALSE);
	}

	public void setParameter(String string, float float1) {
		this.m_storyParams.put(string, float1);
	}

	public void setParameter(String string, int int1) {
		this.m_storyParams.put(string, int1);
	}

	public void setParameter(String string, Object object) {
		this.m_storyParams.put(string, object);
	}

	public boolean getParameterBoolean(String string) {
		return (Boolean)this.getParameter(string, Boolean.class);
	}

	public float getParameterFloat(String string) {
		return (Float)this.getParameter(string, Float.class);
	}

	public int getParameterInteger(String string) {
		return (Integer)this.getParameter(string, Integer.class);
	}

	public String getParameterString(String string) {
		return (String)this.getParameter(string, String.class);
	}

	public Object getParameter(String string, Class javaClass) {
		return Type.tryCastTo(this.m_storyParams.get(string), javaClass);
	}

	public void spawn(float float1, float float2, float float3, float float4, VehicleStorySpawner.IElementSpawner iElementSpawner) {
		for (int int1 = 0; int1 < this.m_elements.size(); ++int1) {
			VehicleStorySpawner.Element element = (VehicleStorySpawner.Element)this.m_elements.get(int1);
			Vector2 vector2 = s_vector2_1.setLengthAndDirection(element.direction, 1.0F);
			vector2.add(element.position);
			this.rotate(float1, float2, vector2, float4);
			this.rotate(float1, float2, element.position, float4);
			element.direction = Vector2.getDirection(vector2.x - element.position.x, vector2.y - element.position.y);
			element.z = float3;
			element.square = IsoWorld.instance.CurrentCell.getGridSquare((double)element.position.x, (double)element.position.y, (double)float3);
			iElementSpawner.spawn(this, element);
		}
	}

	public Vector2 rotate(float float1, float float2, Vector2 vector2, float float3) {
		float float4 = vector2.x;
		float float5 = vector2.y;
		vector2.x = float1 + (float)((double)float4 * Math.cos((double)float3) - (double)float5 * Math.sin((double)float3));
		vector2.y = float2 + (float)((double)float4 * Math.sin((double)float3) + (double)float5 * Math.cos((double)float3));
		return vector2;
	}

	public void getAABB(float float1, float float2, float float3, float float4, float float5, int[] intArray) {
		Vector2 vector2 = s_vector2_1.setLengthAndDirection(float5, 1.0F);
		Vector2 vector22 = s_vector2_2.set(vector2);
		vector22.tangent();
		vector2.x *= float4 / 2.0F;
		vector2.y *= float4 / 2.0F;
		vector22.x *= float3 / 2.0F;
		vector22.y *= float3 / 2.0F;
		float float6 = float1 + vector2.x;
		float float7 = float2 + vector2.y;
		float float8 = float1 - vector2.x;
		float float9 = float2 - vector2.y;
		float float10 = float6 - vector22.x;
		float float11 = float7 - vector22.y;
		float float12 = float6 + vector22.x;
		float float13 = float7 + vector22.y;
		float float14 = float8 - vector22.x;
		float float15 = float9 - vector22.y;
		float float16 = float8 + vector22.x;
		float float17 = float9 + vector22.y;
		float float18 = PZMath.min(float10, PZMath.min(float12, PZMath.min(float14, float16)));
		float float19 = PZMath.max(float10, PZMath.max(float12, PZMath.max(float14, float16)));
		float float20 = PZMath.min(float11, PZMath.min(float13, PZMath.min(float15, float17)));
		float float21 = PZMath.max(float11, PZMath.max(float13, PZMath.max(float15, float17)));
		intArray[0] = (int)PZMath.floor(float18);
		intArray[1] = (int)PZMath.floor(float20);
		intArray[2] = (int)PZMath.ceil(float19);
		intArray[3] = (int)PZMath.ceil(float21);
	}

	public void render(float float1, float float2, float float3, float float4, float float5, float float6) {
		LineDrawer.DrawIsoRectRotated(float1, float2, float3, float4, float5, float6, 0.0F, 0.0F, 1.0F, 1.0F);
		float float7 = 1.0F;
		float float8 = 1.0F;
		float float9 = 1.0F;
		float float10 = 1.0F;
		float float11 = Float.MAX_VALUE;
		float float12 = Float.MAX_VALUE;
		float float13 = -3.4028235E38F;
		float float14 = -3.4028235E38F;
		for (Iterator iterator = this.m_elements.iterator(); iterator.hasNext(); float14 = PZMath.max(float14, (float)s_AABB[3])) {
			VehicleStorySpawner.Element element = (VehicleStorySpawner.Element)iterator.next();
			Vector2 vector2 = s_vector2_1.setLengthAndDirection(element.direction, 1.0F);
			LineDrawer.DrawIsoLine(element.position.x, element.position.y, float3, element.position.x + vector2.x, element.position.y + vector2.y, float3, float7, float8, float9, float10, 1);
			LineDrawer.DrawIsoRectRotated(element.position.x, element.position.y, float3, element.width, element.height, element.direction, float7, float8, float9, float10);
			this.getAABB(element.position.x, element.position.y, element.width, element.height, element.direction, s_AABB);
			float11 = PZMath.min(float11, (float)s_AABB[0]);
			float12 = PZMath.min(float12, (float)s_AABB[1]);
			float13 = PZMath.max(float13, (float)s_AABB[2]);
		}
	}

	public static final class Element {
		String id;
		final Vector2 position = new Vector2();
		float direction;
		float width;
		float height;
		float z;
		IsoGridSquare square;

		VehicleStorySpawner.Element init(String string, float float1, float float2, float float3, float float4, float float5) {
			this.id = string;
			this.position.set(float1, float2);
			this.direction = float3;
			this.width = float4;
			this.height = float5;
			this.z = 0.0F;
			this.square = null;
			return this;
		}
	}

	public interface IElementSpawner {

		void spawn(VehicleStorySpawner vehicleStorySpawner, VehicleStorySpawner.Element element);
	}
}
