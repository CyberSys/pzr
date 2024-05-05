package zombie.worldMap.styles;

import java.util.ArrayList;
import zombie.core.textures.Texture;
import zombie.worldMap.WorldMapFeature;


public class WorldMapPolygonStyleLayer extends WorldMapStyleLayer {
	public final ArrayList m_fill = new ArrayList();
	public final ArrayList m_texture = new ArrayList();
	public final ArrayList m_scale = new ArrayList();

	public WorldMapPolygonStyleLayer(String string) {
		super(string);
	}

	public String getTypeString() {
		return "Polygon";
	}

	public void render(WorldMapFeature worldMapFeature, WorldMapStyleLayer.RenderArgs renderArgs) {
		WorldMapStyleLayer.RGBAf rGBAf = this.evalColor(renderArgs, this.m_fill);
		if (rGBAf.a < 0.01F) {
			WorldMapStyleLayer.RGBAf.s_pool.release((Object)rGBAf);
		} else {
			float float1 = this.evalFloat(renderArgs, this.m_scale);
			Texture texture = this.evalTexture(renderArgs, this.m_texture);
			if (texture != null && texture.isReady()) {
				renderArgs.drawer.fillPolygon(renderArgs, worldMapFeature, rGBAf, texture, float1);
			} else {
				renderArgs.drawer.fillPolygon(renderArgs, worldMapFeature, rGBAf);
			}

			WorldMapStyleLayer.RGBAf.s_pool.release((Object)rGBAf);
		}
	}
}
