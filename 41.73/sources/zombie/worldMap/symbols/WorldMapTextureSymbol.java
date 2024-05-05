package zombie.worldMap.symbols;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import zombie.GameWindow;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.worldMap.UIWorldMap;


public final class WorldMapTextureSymbol extends WorldMapBaseSymbol {
	private String m_symbolID;
	Texture m_texture;

	public WorldMapTextureSymbol(WorldMapSymbols worldMapSymbols) {
		super(worldMapSymbols);
	}

	public void setSymbolID(String string) {
		this.m_symbolID = string;
	}

	public String getSymbolID() {
		return this.m_symbolID;
	}

	public void checkTexture() {
		if (this.m_texture == null) {
			MapSymbolDefinitions.MapSymbolDefinition mapSymbolDefinition = MapSymbolDefinitions.getInstance().getSymbolById(this.getSymbolID());
			if (mapSymbolDefinition == null) {
				this.m_width = 18.0F;
				this.m_height = 18.0F;
			} else {
				this.m_texture = Texture.getSharedTexture(mapSymbolDefinition.getTexturePath());
				this.m_width = (float)mapSymbolDefinition.getWidth();
				this.m_height = (float)mapSymbolDefinition.getHeight();
			}

			if (this.m_texture == null) {
				this.m_texture = Texture.getErrorTexture();
			}
		}
	}

	public WorldMapSymbols.WorldMapSymbolType getType() {
		return WorldMapSymbols.WorldMapSymbolType.Texture;
	}

	public void layout(UIWorldMap uIWorldMap, WorldMapSymbolCollisions worldMapSymbolCollisions, float float1, float float2) {
		this.checkTexture();
		super.layout(uIWorldMap, worldMapSymbolCollisions, float1, float2);
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		GameWindow.WriteString(byteBuffer, this.m_symbolID);
	}

	public void load(ByteBuffer byteBuffer, int int1, int int2) throws IOException {
		super.load(byteBuffer, int1, int2);
		this.m_symbolID = GameWindow.ReadString(byteBuffer);
	}

	public void render(UIWorldMap uIWorldMap, float float1, float float2) {
		if (this.m_collided) {
			this.renderCollided(uIWorldMap, float1, float2);
		} else {
			this.checkTexture();
			float float3 = float1 + this.m_layoutX;
			float float4 = float2 + this.m_layoutY;
			if (this.m_scale > 0.0F) {
				float float5 = this.getDisplayScale(uIWorldMap);
				SpriteRenderer.instance.m_states.getPopulatingActiveState().render(this.m_texture, (float)uIWorldMap.getAbsoluteX().intValue() + float3, (float)uIWorldMap.getAbsoluteY().intValue() + float4, (float)this.m_texture.getWidth() * float5, (float)this.m_texture.getHeight() * float5, this.m_r, this.m_g, this.m_b, this.m_a, (Consumer)null);
			} else {
				uIWorldMap.DrawTextureColor(this.m_texture, (double)float3, (double)float4, (double)this.m_r, (double)this.m_g, (double)this.m_b, (double)this.m_a);
			}
		}
	}

	public void release() {
		this.m_symbolID = null;
		this.m_texture = null;
	}
}
