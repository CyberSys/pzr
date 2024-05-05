package zombie.worldMap.styles;

import java.util.ArrayList;
import zombie.core.math.PZMath;
import zombie.worldMap.WorldMapFeature;


public class WorldMapLineStyleLayer extends WorldMapStyleLayer {
	public final ArrayList m_fill = new ArrayList();
	public final ArrayList m_lineWidth = new ArrayList();

	public WorldMapLineStyleLayer(String string) {
		super(string);
	}

	public String getTypeString() {
		return "Line";
	}

	public void render(WorldMapFeature worldMapFeature, WorldMapStyleLayer.RenderArgs renderArgs) {
		WorldMapStyleLayer.RGBAf rGBAf = this.evalColor(renderArgs, this.m_fill);
		if (!(rGBAf.a < 0.01F)) {
			float float1;
			if (worldMapFeature.m_properties.containsKey("width")) {
				float1 = PZMath.tryParseFloat((String)worldMapFeature.m_properties.get("width"), 1.0F) * renderArgs.drawer.getWorldScale();
			} else {
				float1 = this.evalFloat(renderArgs, this.m_lineWidth);
			}

			renderArgs.drawer.drawLineString(renderArgs, worldMapFeature, rGBAf, float1);
			WorldMapStyleLayer.RGBAf.s_pool.release((Object)rGBAf);
		}
	}
}
