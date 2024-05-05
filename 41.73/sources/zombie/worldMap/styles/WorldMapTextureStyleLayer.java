package zombie.worldMap.styles;

import java.util.ArrayList;
import zombie.core.textures.Texture;
import zombie.worldMap.WorldMapFeature;


public class WorldMapTextureStyleLayer extends WorldMapStyleLayer {
	public int m_worldX1;
	public int m_worldY1;
	public int m_worldX2;
	public int m_worldY2;
	public boolean m_useWorldBounds = false;
	public final ArrayList m_fill = new ArrayList();
	public final ArrayList m_texture = new ArrayList();
	public boolean m_tile = false;

	public WorldMapTextureStyleLayer(String string) {
		super(string);
	}

	public String getTypeString() {
		return "Texture";
	}

	public boolean filter(WorldMapFeature worldMapFeature, WorldMapStyleLayer.FilterArgs filterArgs) {
		return false;
	}

	public void render(WorldMapFeature worldMapFeature, WorldMapStyleLayer.RenderArgs renderArgs) {
	}

	public void renderCell(WorldMapStyleLayer.RenderArgs renderArgs) {
		if (this.m_useWorldBounds) {
			this.m_worldX1 = renderArgs.renderer.getWorldMap().getMinXInSquares();
			this.m_worldY1 = renderArgs.renderer.getWorldMap().getMinYInSquares();
			this.m_worldX2 = renderArgs.renderer.getWorldMap().getMaxXInSquares() + 1;
			this.m_worldY2 = renderArgs.renderer.getWorldMap().getMaxYInSquares() + 1;
		}

		WorldMapStyleLayer.RGBAf rGBAf = this.evalColor(renderArgs, this.m_fill);
		if (rGBAf.a < 0.01F) {
			WorldMapStyleLayer.RGBAf.s_pool.release((Object)rGBAf);
		} else {
			Texture texture = this.evalTexture(renderArgs, this.m_texture);
			if (texture == null) {
				WorldMapStyleLayer.RGBAf.s_pool.release((Object)rGBAf);
			} else {
				if (this.m_tile) {
					renderArgs.drawer.drawTextureTiled(texture, rGBAf, this.m_worldX1, this.m_worldY1, this.m_worldX2, this.m_worldY2, renderArgs.cellX, renderArgs.cellY);
				} else {
					renderArgs.drawer.drawTexture(texture, rGBAf, this.m_worldX1, this.m_worldY1, this.m_worldX2, this.m_worldY2);
				}

				WorldMapStyleLayer.RGBAf.s_pool.release((Object)rGBAf);
			}
		}
	}
}
