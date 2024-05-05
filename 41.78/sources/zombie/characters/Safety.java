package zombie.characters;

import java.nio.ByteBuffer;
import zombie.core.math.PZMath;
import zombie.iso.areas.NonPvpZone;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;


public class Safety {
	protected boolean enabled;
	protected boolean last;
	protected float cooldown;
	protected float toggle;
	protected IsoGameCharacter character;

	public Safety() {
	}

	public Safety(IsoGameCharacter gameCharacter) {
		this.character = gameCharacter;
		this.enabled = true;
		this.last = true;
		this.cooldown = 0.0F;
		this.toggle = 0.0F;
	}

	public void copyFrom(Safety safety) {
		this.enabled = safety.enabled;
		this.last = safety.last;
		this.cooldown = safety.cooldown;
		this.toggle = safety.toggle;
	}

	public Object getCharacter() {
		return this.character;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean boolean1) {
		this.enabled = boolean1;
	}

	public boolean isLast() {
		return this.last;
	}

	public void setLast(boolean boolean1) {
		this.last = boolean1;
	}

	public float getCooldown() {
		return this.cooldown;
	}

	public void setCooldown(float float1) {
		this.cooldown = float1;
	}

	public float getToggle() {
		return this.toggle;
	}

	public void setToggle(float float1) {
		this.toggle = float1;
	}

	public boolean isToggleAllowed() {
		return ServerOptions.getInstance().PVP.getValue() && NonPvpZone.getNonPvpZone(PZMath.fastfloor(this.character.getX()), PZMath.fastfloor(this.character.getY())) == null && (!ServerOptions.getInstance().SafetySystem.getValue() || this.getCooldown() == 0.0F && this.getToggle() == 0.0F);
	}

	public void toggleSafety() {
		if (this.isToggleAllowed()) {
			if (GameClient.bClient) {
				GameClient.sendChangeSafety(this);
			} else {
				this.setToggle((float)ServerOptions.getInstance().SafetyToggleTimer.getValue());
				this.setLast(this.isEnabled());
				if (this.isEnabled()) {
					this.setEnabled(!this.isEnabled());
				}

				if (GameServer.bServer) {
					GameServer.sendChangeSafety(this);
				}
			}
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) {
		this.enabled = byteBuffer.get() != 0;
		this.last = byteBuffer.get() != 0;
		this.cooldown = byteBuffer.getFloat();
		this.toggle = byteBuffer.getFloat();
	}

	public void save(ByteBuffer byteBuffer) {
		byteBuffer.put((byte)(this.enabled ? 1 : 0));
		byteBuffer.put((byte)(this.last ? 1 : 0));
		byteBuffer.putFloat(this.cooldown);
		byteBuffer.putFloat(this.toggle);
	}

	public String getDescription() {
		return "enabled=" + this.enabled + " last=" + this.last + " cooldown=" + this.cooldown + " toggle=" + this.toggle;
	}
}
