package zombie.worldMap.symbols;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.core.Translator;
import zombie.network.GameServer;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.worldMap.UIWorldMap;


public final class WorldMapTextSymbol extends WorldMapBaseSymbol {
	String m_text;
	boolean m_translated = false;
	UIFont m_font;

	public WorldMapTextSymbol(WorldMapSymbols worldMapSymbols) {
		super(worldMapSymbols);
		this.m_font = UIFont.Handwritten;
	}

	public void setTranslatedText(String string) {
		this.m_text = string;
		this.m_translated = true;
		if (!GameServer.bServer) {
			this.m_width = (float)TextManager.instance.MeasureStringX(this.m_font, this.getTranslatedText());
			this.m_height = (float)TextManager.instance.getFontHeight(this.m_font);
		}
	}

	public void setUntranslatedText(String string) {
		this.m_text = string;
		this.m_translated = false;
		if (!GameServer.bServer) {
			this.m_width = (float)TextManager.instance.MeasureStringX(this.m_font, this.getTranslatedText());
			this.m_height = (float)TextManager.instance.getFontHeight(this.m_font);
		}
	}

	public String getTranslatedText() {
		return this.m_translated ? this.m_text : Translator.getText(this.m_text);
	}

	public String getUntranslatedText() {
		return this.m_translated ? null : this.m_text;
	}

	public WorldMapSymbols.WorldMapSymbolType getType() {
		return WorldMapSymbols.WorldMapSymbolType.Text;
	}

	public boolean isVisible() {
		return this.m_owner.getMiniMapSymbols() ? false : super.isVisible();
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		GameWindow.WriteString(byteBuffer, this.m_text);
		byteBuffer.put((byte)(this.m_translated ? 1 : 0));
	}

	public void load(ByteBuffer byteBuffer, int int1, int int2) throws IOException {
		super.load(byteBuffer, int1, int2);
		this.m_text = GameWindow.ReadString(byteBuffer);
		this.m_translated = byteBuffer.get() == 1;
	}

	public void render(UIWorldMap uIWorldMap, float float1, float float2) {
		if (this.m_width == 0.0F || this.m_height == 0.0F) {
			this.m_width = (float)TextManager.instance.MeasureStringX(this.m_font, this.getTranslatedText());
			this.m_height = (float)TextManager.instance.getFontHeight(this.m_font);
		}

		if (this.m_collided) {
			this.renderCollided(uIWorldMap, float1, float2);
		} else {
			float float3 = float1 + this.m_layoutX;
			float float4 = float2 + this.m_layoutY;
			if (this.m_scale > 0.0F) {
				uIWorldMap.DrawText(this.m_font, this.getTranslatedText(), (double)float3, (double)float4, (double)this.getDisplayScale(uIWorldMap), (double)this.m_r, (double)this.m_g, (double)this.m_b, (double)this.m_a);
			} else {
				uIWorldMap.DrawText(this.m_font, this.getTranslatedText(), (double)float3, (double)float4, (double)this.m_r, (double)this.m_g, (double)this.m_b, (double)this.m_a);
			}
		}
	}

	public void release() {
		this.m_text = null;
	}
}
