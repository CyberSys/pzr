package zombie.worldMap.symbols;

import java.util.ArrayList;
import java.util.Objects;
import zombie.Lua.LuaManager;
import zombie.ui.UIFont;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.worldMap.UIWorldMap;


public class WorldMapSymbolsV1 {
	private static final Pool s_textPool = new Pool(WorldMapSymbolsV1.WorldMapTextSymbolV1::new);
	private static final Pool s_texturePool = new Pool(WorldMapSymbolsV1.WorldMapTextureSymbolV1::new);
	private final UIWorldMap m_ui;
	private final WorldMapSymbols m_uiSymbols;
	private final ArrayList m_symbols = new ArrayList();

	public WorldMapSymbolsV1(UIWorldMap uIWorldMap, WorldMapSymbols worldMapSymbols) {
		Objects.requireNonNull(uIWorldMap);
		this.m_ui = uIWorldMap;
		this.m_uiSymbols = worldMapSymbols;
		this.reinit();
	}

	public WorldMapSymbolsV1.WorldMapTextSymbolV1 addTranslatedText(String string, UIFont uIFont, float float1, float float2) {
		WorldMapTextSymbol worldMapTextSymbol = this.m_uiSymbols.addTranslatedText(string, uIFont, float1, float2, 1.0F, 1.0F, 1.0F, 1.0F);
		WorldMapSymbolsV1.WorldMapTextSymbolV1 worldMapTextSymbolV1 = ((WorldMapSymbolsV1.WorldMapTextSymbolV1)s_textPool.alloc()).init(this, worldMapTextSymbol);
		this.m_symbols.add(worldMapTextSymbolV1);
		return worldMapTextSymbolV1;
	}

	public WorldMapSymbolsV1.WorldMapTextSymbolV1 addUntranslatedText(String string, UIFont uIFont, float float1, float float2) {
		WorldMapTextSymbol worldMapTextSymbol = this.m_uiSymbols.addUntranslatedText(string, uIFont, float1, float2, 1.0F, 1.0F, 1.0F, 1.0F);
		WorldMapSymbolsV1.WorldMapTextSymbolV1 worldMapTextSymbolV1 = ((WorldMapSymbolsV1.WorldMapTextSymbolV1)s_textPool.alloc()).init(this, worldMapTextSymbol);
		this.m_symbols.add(worldMapTextSymbolV1);
		return worldMapTextSymbolV1;
	}

	public WorldMapSymbolsV1.WorldMapTextureSymbolV1 addTexture(String string, float float1, float float2) {
		WorldMapTextureSymbol worldMapTextureSymbol = this.m_uiSymbols.addTexture(string, float1, float2, 1.0F, 1.0F, 1.0F, 1.0F);
		WorldMapSymbolsV1.WorldMapTextureSymbolV1 worldMapTextureSymbolV1 = ((WorldMapSymbolsV1.WorldMapTextureSymbolV1)s_texturePool.alloc()).init(this, worldMapTextureSymbol);
		this.m_symbols.add(worldMapTextureSymbolV1);
		return worldMapTextureSymbolV1;
	}

	public int hitTest(float float1, float float2) {
		return this.m_uiSymbols.hitTest(this.m_ui, float1, float2);
	}

	public int getSymbolCount() {
		return this.m_symbols.size();
	}

	public WorldMapSymbolsV1.WorldMapBaseSymbolV1 getSymbolByIndex(int int1) {
		return (WorldMapSymbolsV1.WorldMapBaseSymbolV1)this.m_symbols.get(int1);
	}

	public void removeSymbolByIndex(int int1) {
		this.m_uiSymbols.removeSymbolByIndex(int1);
		((WorldMapSymbolsV1.WorldMapBaseSymbolV1)this.m_symbols.remove(int1)).release();
	}

	public void clear() {
		this.m_uiSymbols.clear();
		this.reinit();
	}

	void reinit() {
		int int1;
		for (int1 = 0; int1 < this.m_symbols.size(); ++int1) {
			((WorldMapSymbolsV1.WorldMapBaseSymbolV1)this.m_symbols.get(int1)).release();
		}

		this.m_symbols.clear();
		for (int1 = 0; int1 < this.m_uiSymbols.getSymbolCount(); ++int1) {
			WorldMapBaseSymbol worldMapBaseSymbol = this.m_uiSymbols.getSymbolByIndex(int1);
			WorldMapTextSymbol worldMapTextSymbol = (WorldMapTextSymbol)Type.tryCastTo(worldMapBaseSymbol, WorldMapTextSymbol.class);
			if (worldMapTextSymbol != null) {
				WorldMapSymbolsV1.WorldMapTextSymbolV1 worldMapTextSymbolV1 = ((WorldMapSymbolsV1.WorldMapTextSymbolV1)s_textPool.alloc()).init(this, worldMapTextSymbol);
				this.m_symbols.add(worldMapTextSymbolV1);
			}

			WorldMapTextureSymbol worldMapTextureSymbol = (WorldMapTextureSymbol)Type.tryCastTo(worldMapBaseSymbol, WorldMapTextureSymbol.class);
			if (worldMapTextureSymbol != null) {
				WorldMapSymbolsV1.WorldMapTextureSymbolV1 worldMapTextureSymbolV1 = ((WorldMapSymbolsV1.WorldMapTextureSymbolV1)s_texturePool.alloc()).init(this, worldMapTextureSymbol);
				this.m_symbols.add(worldMapTextureSymbolV1);
			}
		}
	}

	public static void setExposed(LuaManager.Exposer exposer) {
		exposer.setExposed(WorldMapSymbolsV1.class);
		exposer.setExposed(WorldMapSymbolsV1.WorldMapTextSymbolV1.class);
		exposer.setExposed(WorldMapSymbolsV1.WorldMapTextureSymbolV1.class);
	}

	public static class WorldMapTextSymbolV1 extends WorldMapSymbolsV1.WorldMapBaseSymbolV1 {
		WorldMapTextSymbol m_textSymbol;

		WorldMapSymbolsV1.WorldMapTextSymbolV1 init(WorldMapSymbolsV1 worldMapSymbolsV1, WorldMapTextSymbol worldMapTextSymbol) {
			super.init(worldMapSymbolsV1, worldMapTextSymbol);
			this.m_textSymbol = worldMapTextSymbol;
			return this;
		}

		public void setTranslatedText(String string) {
			if (!StringUtils.isNullOrWhitespace(string)) {
				this.m_textSymbol.setTranslatedText(string);
				this.m_owner.m_uiSymbols.invalidateLayout();
			}
		}

		public void setUntranslatedText(String string) {
			if (!StringUtils.isNullOrWhitespace(string)) {
				this.m_textSymbol.setUntranslatedText(string);
				this.m_owner.m_uiSymbols.invalidateLayout();
			}
		}

		public String getTranslatedText() {
			return this.m_textSymbol.getTranslatedText();
		}

		public String getUntranslatedText() {
			return this.m_textSymbol.getUntranslatedText();
		}

		public boolean isText() {
			return true;
		}
	}

	public static class WorldMapTextureSymbolV1 extends WorldMapSymbolsV1.WorldMapBaseSymbolV1 {
		WorldMapTextureSymbol m_textureSymbol;

		WorldMapSymbolsV1.WorldMapTextureSymbolV1 init(WorldMapSymbolsV1 worldMapSymbolsV1, WorldMapTextureSymbol worldMapTextureSymbol) {
			super.init(worldMapSymbolsV1, worldMapTextureSymbol);
			this.m_textureSymbol = worldMapTextureSymbol;
			return this;
		}

		public String getSymbolID() {
			return this.m_textureSymbol.getSymbolID();
		}

		public boolean isTexture() {
			return true;
		}
	}

	protected static class WorldMapBaseSymbolV1 extends PooledObject {
		WorldMapSymbolsV1 m_owner;
		WorldMapBaseSymbol m_symbol;

		WorldMapSymbolsV1.WorldMapBaseSymbolV1 init(WorldMapSymbolsV1 worldMapSymbolsV1, WorldMapBaseSymbol worldMapBaseSymbol) {
			this.m_owner = worldMapSymbolsV1;
			this.m_symbol = worldMapBaseSymbol;
			return this;
		}

		public float getWorldX() {
			return this.m_symbol.m_x;
		}

		public float getWorldY() {
			return this.m_symbol.m_y;
		}

		public float getDisplayX() {
			this.m_owner.m_uiSymbols.checkLayout(this.m_owner.m_ui);
			return this.m_symbol.m_layoutX + this.m_owner.m_ui.getAPIv1().worldOriginX();
		}

		public float getDisplayY() {
			this.m_owner.m_uiSymbols.checkLayout(this.m_owner.m_ui);
			return this.m_symbol.m_layoutY + this.m_owner.m_ui.getAPIv1().worldOriginY();
		}

		public float getDisplayWidth() {
			this.m_owner.m_uiSymbols.checkLayout(this.m_owner.m_ui);
			return this.m_symbol.widthScaled(this.m_owner.m_ui);
		}

		public float getDisplayHeight() {
			this.m_owner.m_uiSymbols.checkLayout(this.m_owner.m_ui);
			return this.m_symbol.heightScaled(this.m_owner.m_ui);
		}

		public void setAnchor(float float1, float float2) {
			this.m_symbol.setAnchor(float1, float2);
		}

		public void setPosition(float float1, float float2) {
			this.m_symbol.setPosition(float1, float2);
			this.m_owner.m_uiSymbols.invalidateLayout();
		}

		public void setCollide(boolean boolean1) {
			this.m_symbol.setCollide(boolean1);
		}

		public void setVisible(boolean boolean1) {
			this.m_symbol.setVisible(boolean1);
		}

		public boolean isVisible() {
			return this.m_symbol.isVisible();
		}

		public void setRGBA(float float1, float float2, float float3, float float4) {
			this.m_symbol.setRGBA(float1, float2, float3, float4);
		}

		public float getRed() {
			return this.m_symbol.m_r;
		}

		public float getGreen() {
			return this.m_symbol.m_g;
		}

		public float getBlue() {
			return this.m_symbol.m_b;
		}

		public float getAlpha() {
			return this.m_symbol.m_a;
		}

		public void setScale(float float1) {
			this.m_symbol.setScale(float1);
		}

		public boolean isText() {
			return false;
		}

		public boolean isTexture() {
			return false;
		}
	}
}
