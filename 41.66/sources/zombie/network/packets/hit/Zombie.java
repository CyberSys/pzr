package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.PersistentOutfits;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.ModelManager;
import zombie.inventory.types.HandWeapon;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.network.ServerMap;
import zombie.network.packets.INetworkPacket;


public class Zombie extends Character implements INetworkPacket {
	protected IsoZombie zombie;
	protected short zombieFlags;
	protected String attackOutcome;
	protected String attackPosition;

	public void set(IsoZombie zombie, boolean boolean1) {
		super.set(zombie);
		this.zombie = zombie;
		this.zombieFlags = 0;
		this.zombieFlags |= (short)(zombie.isStaggerBack() ? 1 : 0);
		this.zombieFlags |= (short)(zombie.isFakeDead() ? 2 : 0);
		this.zombieFlags |= (short)(zombie.isBecomeCrawler() ? 4 : 0);
		this.zombieFlags |= (short)(zombie.isCrawling() ? 8 : 0);
		this.zombieFlags |= (short)(zombie.isKnifeDeath() ? 16 : 0);
		this.zombieFlags |= (short)(zombie.isJawStabAttach() ? 32 : 0);
		this.zombieFlags |= (short)(boolean1 ? 64 : 0);
		this.zombieFlags |= (short)(zombie.getVariableBoolean("AttackDidDamage") ? 128 : 0);
		this.attackOutcome = zombie.getVariableString("AttackOutcome");
		this.attackPosition = zombie.getPlayerAttackPosition();
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		super.parse(byteBuffer, udpConnection);
		this.zombieFlags = byteBuffer.getShort();
		this.attackOutcome = GameWindow.ReadString(byteBuffer);
		this.attackPosition = GameWindow.ReadString(byteBuffer);
		if (GameServer.bServer) {
			this.zombie = ServerMap.instance.ZombieMap.get(this.ID);
			this.character = this.zombie;
		} else if (GameClient.bClient) {
			this.zombie = (IsoZombie)GameClient.IDToZombieMap.get(this.ID);
			this.character = this.zombie;
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
		byteBufferWriter.putShort(this.zombieFlags);
		byteBufferWriter.putUTF(this.attackOutcome);
		byteBufferWriter.putUTF(this.attackPosition);
	}

	public boolean isConsistent() {
		return super.isConsistent() && this.zombie != null;
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + "\n\tZombie [attack-position=" + this.attackPosition + " | isStaggerBack=" + ((this.zombieFlags & 1) != 0) + " | isFakeDead=" + ((this.zombieFlags & 2) != 0) + " | isBecomeCrawler=" + ((this.zombieFlags & 4) != 0) + " | isCrawling=" + ((this.zombieFlags & 8) != 0) + " | isKnifeDeath=" + ((this.zombieFlags & 16) != 0) + " | isJawStabAttach=" + ((this.zombieFlags & 32) != 0) + " | isHelmetFall=" + ((this.zombieFlags & 64) != 0) + " | attackDidDamage=" + ((this.zombieFlags & 128) != 0) + " | attack-outcome=" + this.attackOutcome + " ]";
	}

	void process() {
		super.process();
		this.zombie.setVariable("AttackOutcome", this.attackOutcome);
		this.zombie.setPlayerAttackPosition(this.attackPosition);
		this.zombie.setStaggerBack((this.zombieFlags & 1) != 0);
		this.zombie.setFakeDead((this.zombieFlags & 2) != 0);
		this.zombie.setBecomeCrawler((this.zombieFlags & 4) != 0);
		this.zombie.setCrawler((this.zombieFlags & 8) != 0);
		this.zombie.setKnifeDeath((this.zombieFlags & 16) != 0);
		this.zombie.setJawStabAttach((this.zombieFlags & 32) != 0);
		this.zombie.setVariable("AttackDidDamage", (this.zombieFlags & 128) != 0);
	}

	protected void react(HandWeapon handWeapon) {
		if (this.zombie.isJawStabAttach()) {
			this.zombie.setAttachedItem("JawStab", handWeapon);
		}

		if (GameServer.bServer && (this.zombieFlags & 64) != 0 && !PersistentOutfits.instance.isHatFallen(this.zombie)) {
			PersistentOutfits.instance.setFallenHat(this.zombie, true);
			if (ServerGUI.isCreated()) {
				PersistentOutfits.instance.removeFallenHat(this.zombie.getPersistentOutfitID(), this.zombie);
				ModelManager.instance.ResetNextFrame(this.zombie);
			}
		}

		this.react();
	}

	public IsoGameCharacter getCharacter() {
		return this.zombie;
	}
}
