package zombie.core.Styles;


public interface Style {

	void setupState();

	void resetState();

	int getStyleID();

	AlphaOp getAlphaOp();

	boolean getRenderSprite();

	GeometryData build();

	void render(int int1, int int2);
}
