package zombie.worldMap.symbols;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.core.textures.Texture;


public final class MapSymbolDefinitions {
	private static MapSymbolDefinitions instance;
	private final ArrayList m_symbolList = new ArrayList();
	private final HashMap m_symbolByID = new HashMap();

	public static MapSymbolDefinitions getInstance() {
		if (instance == null) {
			instance = new MapSymbolDefinitions();
		}

		return instance;
	}

	public void addTexture(String string, String string2, int int1, int int2) {
		MapSymbolDefinitions.MapSymbolDefinition mapSymbolDefinition = new MapSymbolDefinitions.MapSymbolDefinition();
		mapSymbolDefinition.id = string;
		mapSymbolDefinition.texturePath = string2;
		mapSymbolDefinition.width = int1;
		mapSymbolDefinition.height = int2;
		this.m_symbolList.add(mapSymbolDefinition);
		this.m_symbolByID.put(string, mapSymbolDefinition);
	}

	public void addTexture(String string, String string2) {
		Texture texture = Texture.getSharedTexture(string2);
		if (texture == null) {
			this.addTexture(string, string2, 18, 18);
		} else {
			this.addTexture(string, string2, texture.getWidth(), texture.getHeight());
		}
	}

	public int getSymbolCount() {
		return this.m_symbolList.size();
	}

	public MapSymbolDefinitions.MapSymbolDefinition getSymbolByIndex(int int1) {
		return (MapSymbolDefinitions.MapSymbolDefinition)this.m_symbolList.get(int1);
	}

	public MapSymbolDefinitions.MapSymbolDefinition getSymbolById(String string) {
		return (MapSymbolDefinitions.MapSymbolDefinition)this.m_symbolByID.get(string);
	}

	public static void Reset() {
		if (instance != null) {
			getInstance().m_symbolList.clear();
			getInstance().m_symbolByID.clear();
		}
	}

	public static final class MapSymbolDefinition {
		private String id;
		private String texturePath;
		private int width;
		private int height;

		public String getId() {
			return this.id;
		}

		public String getTexturePath() {
			return this.texturePath;
		}

		public int getWidth() {
			return this.width;
		}

		public int getHeight() {
			return this.height;
		}
	}
}
