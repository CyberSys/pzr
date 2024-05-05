package zombie.worldMap.styles;

import java.util.ArrayList;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.popman.ObjectPool;
import zombie.worldMap.WorldMapFeature;
import zombie.worldMap.WorldMapRenderer;


public abstract class WorldMapStyleLayer {
	public String m_id;
	public float m_minZoom = 0.0F;
	public WorldMapStyleLayer.IWorldMapStyleFilter m_filter;
	public String m_filterKey;
	public String m_filterValue;

	public WorldMapStyleLayer(String string) {
		this.m_id = string;
	}

	public abstract String getTypeString();

	static int findStop(float float1, ArrayList arrayList) {
		if (arrayList.isEmpty()) {
			return -2;
		} else if (float1 <= ((WorldMapStyleLayer.Stop)arrayList.get(0)).m_zoom) {
			return -1;
		} else {
			for (int int1 = 0; int1 < arrayList.size() - 1; ++int1) {
				if (float1 <= ((WorldMapStyleLayer.Stop)arrayList.get(int1 + 1)).m_zoom) {
					return int1;
				}
			}

			return arrayList.size() - 1;
		}
	}

	protected WorldMapStyleLayer.RGBAf evalColor(WorldMapStyleLayer.RenderArgs renderArgs, ArrayList arrayList) {
		if (arrayList.isEmpty()) {
			return ((WorldMapStyleLayer.RGBAf)WorldMapStyleLayer.RGBAf.s_pool.alloc()).init(1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			float float1 = renderArgs.drawer.m_zoomF;
			int int1 = findStop(float1, arrayList);
			int int2 = int1 == -1 ? 0 : int1;
			int int3 = PZMath.min(int1 + 1, arrayList.size() - 1);
			WorldMapStyleLayer.ColorStop colorStop = (WorldMapStyleLayer.ColorStop)arrayList.get(int2);
			WorldMapStyleLayer.ColorStop colorStop2 = (WorldMapStyleLayer.ColorStop)arrayList.get(int3);
			float float2 = int2 == int3 ? 1.0F : (PZMath.clamp(float1, colorStop.m_zoom, colorStop2.m_zoom) - colorStop.m_zoom) / (colorStop2.m_zoom - colorStop.m_zoom);
			float float3 = PZMath.lerp((float)colorStop.r, (float)colorStop2.r, float2) / 255.0F;
			float float4 = PZMath.lerp((float)colorStop.g, (float)colorStop2.g, float2) / 255.0F;
			float float5 = PZMath.lerp((float)colorStop.b, (float)colorStop2.b, float2) / 255.0F;
			float float6 = PZMath.lerp((float)colorStop.a, (float)colorStop2.a, float2) / 255.0F;
			return ((WorldMapStyleLayer.RGBAf)WorldMapStyleLayer.RGBAf.s_pool.alloc()).init(float3, float4, float5, float6);
		}
	}

	protected float evalFloat(WorldMapStyleLayer.RenderArgs renderArgs, ArrayList arrayList) {
		if (arrayList.isEmpty()) {
			return 1.0F;
		} else {
			float float1 = renderArgs.drawer.m_zoomF;
			int int1 = findStop(float1, arrayList);
			int int2 = int1 == -1 ? 0 : int1;
			int int3 = PZMath.min(int1 + 1, arrayList.size() - 1);
			WorldMapStyleLayer.FloatStop floatStop = (WorldMapStyleLayer.FloatStop)arrayList.get(int2);
			WorldMapStyleLayer.FloatStop floatStop2 = (WorldMapStyleLayer.FloatStop)arrayList.get(int3);
			float float2 = int2 == int3 ? 1.0F : (PZMath.clamp(float1, floatStop.m_zoom, floatStop2.m_zoom) - floatStop.m_zoom) / (floatStop2.m_zoom - floatStop.m_zoom);
			return PZMath.lerp(floatStop.f, floatStop2.f, float2);
		}
	}

	protected Texture evalTexture(WorldMapStyleLayer.RenderArgs renderArgs, ArrayList arrayList) {
		if (arrayList.isEmpty()) {
			return null;
		} else {
			float float1 = renderArgs.drawer.m_zoomF;
			int int1 = findStop(float1, arrayList);
			int int2 = int1 == -1 ? 0 : int1;
			int int3 = PZMath.min(int1 + 1, arrayList.size() - 1);
			WorldMapStyleLayer.TextureStop textureStop = (WorldMapStyleLayer.TextureStop)arrayList.get(int2);
			WorldMapStyleLayer.TextureStop textureStop2 = (WorldMapStyleLayer.TextureStop)arrayList.get(int3);
			if (textureStop == textureStop2) {
				return float1 < textureStop.m_zoom ? null : textureStop.texture;
			} else if (!(float1 < textureStop.m_zoom) && !(float1 > textureStop2.m_zoom)) {
				float float2 = int2 == int3 ? 1.0F : (PZMath.clamp(float1, textureStop.m_zoom, textureStop2.m_zoom) - textureStop.m_zoom) / (textureStop2.m_zoom - textureStop.m_zoom);
				return float2 < 0.5F ? textureStop.texture : textureStop2.texture;
			} else {
				return null;
			}
		}
	}

	public boolean filter(WorldMapFeature worldMapFeature, WorldMapStyleLayer.FilterArgs filterArgs) {
		return this.m_filter == null ? false : this.m_filter.filter(worldMapFeature, filterArgs);
	}

	public abstract void render(WorldMapFeature worldMapFeature, WorldMapStyleLayer.RenderArgs renderArgs);

	public void renderCell(WorldMapStyleLayer.RenderArgs renderArgs) {
	}

	public static class Stop {
		public float m_zoom;

		Stop(float float1) {
			this.m_zoom = float1;
		}
	}

	public static final class RGBAf {
		public float r;
		public float g;
		public float b;
		public float a;
		public static final ObjectPool s_pool = new ObjectPool(WorldMapStyleLayer.RGBAf::new);

		public RGBAf() {
			this.r = this.g = this.b = this.a = 1.0F;
		}

		public WorldMapStyleLayer.RGBAf init(float float1, float float2, float float3, float float4) {
			this.r = float1;
			this.g = float2;
			this.b = float3;
			this.a = float4;
			return this;
		}
	}

	public static final class RenderArgs {
		public WorldMapRenderer renderer;
		public WorldMapRenderer.Drawer drawer;
		public int cellX;
		public int cellY;
	}

	public static class ColorStop extends WorldMapStyleLayer.Stop {
		public int r;
		public int g;
		public int b;
		public int a;

		public ColorStop(float float1, int int1, int int2, int int3, int int4) {
			super(float1);
			this.r = int1;
			this.g = int2;
			this.b = int3;
			this.a = int4;
		}
	}

	public static class FloatStop extends WorldMapStyleLayer.Stop {
		public float f;

		public FloatStop(float float1, float float2) {
			super(float1);
			this.f = float2;
		}
	}

	public static class TextureStop extends WorldMapStyleLayer.Stop {
		public String texturePath;
		public Texture texture;

		public TextureStop(float float1, String string) {
			super(float1);
			this.texturePath = string;
			this.texture = Texture.getTexture(string);
		}
	}

	public interface IWorldMapStyleFilter {

		boolean filter(WorldMapFeature worldMapFeature, WorldMapStyleLayer.FilterArgs filterArgs);
	}

	public static final class FilterArgs {
		public WorldMapRenderer renderer;
	}
}
