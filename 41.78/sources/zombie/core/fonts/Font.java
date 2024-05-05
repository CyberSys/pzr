package zombie.core.fonts;

import zombie.core.Color;


public interface Font {

	void drawString(float float1, float float2, String string);

	void drawString(float float1, float float2, String string, Color color);

	void drawString(float float1, float float2, String string, Color color, int int1, int int2);

	int getHeight(String string);

	int getWidth(String string);

	int getWidth(String string, boolean boolean1);

	int getWidth(String string, int int1, int int2);

	int getWidth(String string, int int1, int int2, boolean boolean1);

	int getLineHeight();
}
