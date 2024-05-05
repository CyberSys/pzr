package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.characters.skills.PerkFactory;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.network.packets.INetworkPacket;


public class Player extends Character implements INetworkPacket {
	protected IsoPlayer player;
	protected short playerIndex;
	protected short playerFlags;
	protected float charge;
	protected float perkAiming;
	protected float combatSpeed;
	protected String attackType;
	protected AttackVars attackVars = new AttackVars();
	ArrayList hitList = new ArrayList();

	public void set(IsoPlayer player, boolean boolean1) {
		super.set(player);
		this.playerIndex = player.isLocal() ? (short)player.getPlayerNum() : -1;
		this.player = player;
		this.playerFlags = 0;
		this.playerFlags |= (short)(player.isAimAtFloor() ? 1 : 0);
		this.playerFlags |= (short)(player.isDoShove() ? 2 : 0);
		this.playerFlags |= (short)(player.isAttackFromBehind() ? 4 : 0);
		this.playerFlags |= (short)(boolean1 ? 8 : 0);
		this.charge = player.useChargeDelta;
		this.perkAiming = (float)player.getPerkLevel(PerkFactory.Perks.Aiming);
		this.combatSpeed = player.getVariableFloat("CombatSpeed", 1.0F);
		this.attackType = player.getAttackType();
		this.attackVars.copy(player.attackVars);
		this.hitList.clear();
		this.hitList.addAll(player.hitList);
	}

	public void parsePlayer(UdpConnection udpConnection) {
		if (GameServer.bServer) {
			if (udpConnection != null && this.playerIndex != -1) {
				this.player = GameServer.getPlayerFromConnection(udpConnection, this.playerIndex);
			} else {
				this.player = (IsoPlayer)GameServer.IDToPlayerMap.get(this.ID);
			}

			this.character = this.player;
		} else if (GameClient.bClient) {
			this.player = (IsoPlayer)GameClient.IDToPlayerMap.get(this.ID);
			if (this.player == null) {
				IsoPlayer player = IsoPlayer.getInstance();
				if (player != null) {
					this.player = new IsoPlayer(player.getCell(), new SurvivorDesc(), (int)player.x, (int)player.y, (int)player.z);
				}
			}

			this.character = this.player;
		}
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		super.parse(byteBuffer, udpConnection);
		this.playerIndex = byteBuffer.getShort();
		this.playerFlags = byteBuffer.getShort();
		this.charge = byteBuffer.getFloat();
		this.perkAiming = byteBuffer.getFloat();
		this.combatSpeed = byteBuffer.getFloat();
		this.attackType = GameWindow.ReadString(byteBuffer);
		this.attackVars.parse(byteBuffer, udpConnection);
		byte byte1 = byteBuffer.get();
		for (int int1 = 0; int1 < byte1; ++int1) {
			HitInfo hitInfo = new HitInfo();
			hitInfo.parse(byteBuffer, udpConnection);
			this.hitList.add(hitInfo);
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
		byteBufferWriter.putShort(this.playerIndex);
		byteBufferWriter.putShort(this.playerFlags);
		byteBufferWriter.putFloat(this.charge);
		byteBufferWriter.putFloat(this.perkAiming);
		byteBufferWriter.putFloat(this.combatSpeed);
		byteBufferWriter.putUTF(this.attackType);
		this.attackVars.write(byteBufferWriter);
		byte byte1 = (byte)this.hitList.size();
		byteBufferWriter.putByte(byte1);
		for (int int1 = 0; int1 < byte1; ++int1) {
			((HitInfo)this.hitList.get(int1)).write(byteBufferWriter);
		}
	}

	public boolean isConsistent() {
		return super.isConsistent() && this.player != null;
	}

	public String getDescription() {
		String string = "";
		byte byte1 = (byte)Math.min(100, this.hitList.size());
		for (int int1 = 0; int1 < byte1; ++int1) {
			HitInfo hitInfo = (HitInfo)this.hitList.get(int1);
			string = string + hitInfo.getDescription();
		}

		String string2 = super.getDescription();
		return string2 + "\n\tPlayer [ player " + (this.player == null ? "?" : "\"" + this.player.getUsername() + "\"") + " | charge=" + this.charge + " | perkAiming=" + this.perkAiming + " | combatSpeed=" + this.combatSpeed + " | attackType=\"" + this.attackType + "\" | isAimAtFloor=" + ((this.playerFlags & 1) != 0) + " | isDoShove=" + ((this.playerFlags & 2) != 0) + " | isAttackFromBehind=" + ((this.playerFlags & 4) != 0) + " | isCriticalHit=" + ((this.playerFlags & 8) != 0) + " | _bodyDamage=" + (this.player == null ? "?" : this.player.getBodyDamage().getHealth()) + this.attackVars.getDescription() + "\n\t hitList=[" + string + "](count=" + this.hitList.size() + ") ]";
	}

	void process() {
		super.process();
		this.player.useChargeDelta = this.charge;
		this.player.setVariable("recoilVarX", this.perkAiming / 10.0F);
		this.player.setAttackType(this.attackType);
		this.player.setVariable("CombatSpeed", this.combatSpeed);
		this.player.setVariable("AimFloorAnim", (this.playerFlags & 1) != 0);
		this.player.setAimAtFloor((this.playerFlags & 1) != 0);
		this.player.setDoShove((this.playerFlags & 2) != 0);
		this.player.setAttackFromBehind((this.playerFlags & 4) != 0);
		this.player.setCriticalHit((this.playerFlags & 8) != 0);
	}

	void attack(HandWeapon handWeapon, boolean boolean1) {
		if (GameClient.bClient) {
			this.player.attackStarted = false;
			this.player.attackVars.copy(this.attackVars);
			this.player.hitList.clear();
			this.player.hitList.addAll(this.hitList);
			this.player.pressedAttack(false);
			if (this.player.isAttackStarted() && handWeapon.isRanged() && !this.player.isDoShove()) {
				this.player.startMuzzleFlash();
			}

			if (handWeapon.getPhysicsObject() != null) {
				this.player.Throw(handWeapon);
			}
		} else if (GameServer.bServer && boolean1 && !this.player.getSafety().isEnabled()) {
			this.player.getSafety().setCooldown(this.player.getSafety().getCooldown() + (float)ServerOptions.getInstance().SafetyCooldownTimer.getValue());
			GameServer.sendChangeSafety(this.player.getSafety());
		}
	}

	public IsoGameCharacter getCharacter() {
		return this.player;
	}

	public IsoPlayer getPlayer() {
		return this.player;
	}

	boolean isRelevant(UdpConnection udpConnection) {
		return udpConnection.RelevantTo(this.positionX, this.positionY);
	}
}
