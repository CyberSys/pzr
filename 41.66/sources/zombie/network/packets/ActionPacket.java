package zombie.network.packets;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class ActionPacket implements INetworkPacket {
	private short id;
	private boolean operation;
	private float reloadSpeed;
	private boolean override;
	private String primary;
	private String secondary;
	private final HashMap variables = new HashMap();
	private IsoGameCharacter character;

	public void set(boolean boolean1, BaseAction baseAction) {
		this.character = baseAction.chr;
		this.id = baseAction.chr.getOnlineID();
		this.operation = boolean1;
		this.reloadSpeed = baseAction.chr.getVariableFloat("ReloadSpeed", 1.0F);
		this.override = baseAction.overrideHandModels;
		this.primary = baseAction.getPrimaryHandItem() == null ? baseAction.getPrimaryHandMdl() : baseAction.getPrimaryHandItem().getStaticModel();
		this.secondary = baseAction.getSecondaryHandItem() == null ? baseAction.getSecondaryHandMdl() : baseAction.getSecondaryHandItem().getStaticModel();
		Iterator iterator = baseAction.animVariables.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			this.variables.put(string, baseAction.chr.getVariableString(string));
		}

		if (this.variables.containsValue("DetachItem") || this.variables.containsValue("AttachItem")) {
			this.variables.put("AttachAnim", baseAction.chr.getVariableString("AttachAnim"));
		}

		if (this.variables.containsValue("Loot")) {
			this.variables.put("LootPosition", baseAction.chr.getVariableString("LootPosition"));
		}
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.id = byteBuffer.getShort();
		this.operation = byteBuffer.get() != 0;
		this.reloadSpeed = byteBuffer.getFloat();
		this.override = byteBuffer.get() != 0;
		this.primary = GameWindow.ReadString(byteBuffer);
		this.secondary = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			this.variables.put(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer));
		}

		if (GameServer.bServer) {
			this.character = (IsoGameCharacter)GameServer.IDToPlayerMap.get(this.id);
		} else if (GameClient.bClient) {
			this.character = (IsoGameCharacter)GameClient.IDToPlayerMap.get(this.id);
		} else {
			this.character = null;
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putShort(this.id);
		byteBufferWriter.putBoolean(this.operation);
		byteBufferWriter.putFloat(this.reloadSpeed);
		byteBufferWriter.putBoolean(this.override);
		byteBufferWriter.putUTF(this.primary);
		byteBufferWriter.putUTF(this.secondary);
		byteBufferWriter.putInt(this.variables.size());
		Iterator iterator = this.variables.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			byteBufferWriter.putUTF((String)entry.getKey());
			byteBufferWriter.putUTF((String)entry.getValue());
		}
	}

	public boolean isConsistent() {
		boolean boolean1 = this.character instanceof IsoPlayer;
		if (!boolean1 && Core.bDebug) {
			DebugLog.log(DebugType.Multiplayer, "[Action] is not consistent");
		}

		return boolean1;
	}

	public String getDescription() {
		StringBuilder stringBuilder = (new StringBuilder("[ ")).append("character=").append(this.id);
		if (this.isConsistent()) {
			stringBuilder.append(" \"").append(((IsoPlayer)this.character).getUsername()).append("\"");
		}

		stringBuilder.append(" | ").append("operation=").append(this.operation ? "start" : "stop").append(" | ").append("variables=").append(this.variables.size()).append(" | ");
		Iterator iterator = this.variables.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			stringBuilder.append((String)entry.getKey()).append("=").append((String)entry.getValue()).append(" | ");
		}

		stringBuilder.append("override=").append(this.override).append(" ").append("primary=\"").append(this.primary == null ? "" : this.primary).append("\" ").append("secondary=\"").append(this.secondary == null ? "" : this.secondary).append("\" ]");
		return stringBuilder.toString();
	}

	public boolean isRelevant(UdpConnection udpConnection) {
		return this.isConsistent() && udpConnection.RelevantTo(this.character.getX(), this.character.getY());
	}

	public void process() {
		if (this.isConsistent()) {
			if (this.operation) {
				BaseAction baseAction = new BaseAction(this.character);
				this.variables.forEach((baseActionx,var2)->{
					if (!"true".equals(var2) && !"false".equals(var2)) {
						baseAction.setAnimVariable(baseActionx, var2);
					} else {
						baseAction.setAnimVariable(baseActionx, Boolean.parseBoolean(var2));
					}
				});

				if ("Reload".equals(this.variables.get("PerformingAction"))) {
					this.character.setVariable("ReloadSpeed", this.reloadSpeed);
				}

				this.character.setVariable("IsPerformingAnAction", true);
				this.character.getNetworkCharacterAI().setAction(baseAction);
				this.character.getNetworkCharacterAI().setOverride(this.override, this.primary, this.secondary);
				this.character.getNetworkCharacterAI().startAction();
			} else if (this.character.getNetworkCharacterAI().getAction() != null) {
				this.character.getNetworkCharacterAI().stopAction();
			}
		} else {
			DebugLog.Multiplayer.warn("Action error: player id=" + this.id + " not fond");
		}
	}
}
