package zombie.ui;

import java.io.FileNotFoundException;
import zombie.characters.IsoGameCharacter;
import zombie.core.Color;
import zombie.core.textures.Texture;


public class StatsPage extends DraggableWindow {
	IsoGameCharacter chr;
	int drawY = 0;

	public StatsPage(IsoGameCharacter gameCharacter) throws FileNotFoundException {
		super(100, 100, "ContainerTitlebar", "StatsPage");
		this.chr = gameCharacter;
		this.width = 128.0F;
		this.height = 256.0F;
	}

	public void drawBarStat(String string, float float1, boolean boolean1) {
		float float2 = float1;
		float float3 = float1;
		if (boolean1) {
			float3 = 1.0F - float1;
		} else {
			float2 = 1.0F - float1;
		}

		Color color = new Color(float2, float3, 0.0F);
		byte byte1 = 48;
		this.DrawText(string, 5.0, (double)this.drawY, 1.0, 1.0, 1.0, 1.0);
		this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), (double)byte1, (double)(this.drawY + 3), (double)((int)(float1 * 75.0F)), 8.0, color);
		this.drawY += 12;
	}

	public void drawBarStat(String string, float float1, Color color) {
		byte byte1 = 48;
		this.DrawText(string, 5.0, (double)this.drawY, 1.0, 1.0, 1.0, 1.0);
		this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), (double)byte1, (double)(this.drawY + 3), (double)((int)(float1 * 75.0F)), 8.0, color);
		this.drawY += 12;
	}

	public void render() {
		super.render();
		if (this.isVisible()) {
			this.drawY = 20;
			this.drawBarStat("Hunger", this.chr.getStats().hunger, true);
			this.drawBarStat("Fatigue", this.chr.getStats().fatigue, true);
			this.drawY += 10;
			this.drawBarStat("Stress", this.chr.getStats().stress, true);
			this.drawBarStat("Morale", this.chr.getStats().morale, false);
		}
	}
}
