package zombie.core.Styles;


public class GeometryData {
	private final FloatList vertexData;
	private final ShortList indexData;

	public GeometryData(FloatList floatList, ShortList shortList) {
		this.vertexData = floatList;
		this.indexData = shortList;
	}

	public void clear() {
		this.vertexData.clear();
		this.indexData.clear();
	}

	public FloatList getVertexData() {
		return this.vertexData;
	}

	public ShortList getIndexData() {
		return this.indexData;
	}
}
