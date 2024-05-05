package zombie.chat;

import java.util.ArrayList;
import java.util.HashSet;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.characters.Talker;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoTelevision;
import zombie.network.GameServer;
import zombie.radio.ZomboidRadio;
import zombie.ui.TextDrawObject;
import zombie.ui.UIFont;
import zombie.vehicles.VehiclePart;


public class ChatElement implements Talker {
	protected ChatElement.PlayerLines[] playerLines = new ChatElement.PlayerLines[4];
	protected ChatElementOwner owner;
	protected float historyVal = 1.0F;
	protected boolean historyInRange = false;
	protected float historyRange = 15.0F;
	protected boolean useEuclidean = true;
	protected boolean hasChatToDisplay = false;
	protected int maxChatLines = -1;
	protected int maxCharsPerLine = -1;
	protected String sayLine = null;
	protected String sayLineTag = null;
	protected TextDrawObject sayLineObject = null;
	protected boolean Speaking = false;
	protected String talkerType = "unknown";
	public static boolean doBackDrop = true;
	public static NineGridTexture backdropTexture;
	private int bufferX = 0;
	private int bufferY = 0;
	private static ChatElement.PlayerLinesList[] renderBatch = new ChatElement.PlayerLinesList[4];
	private static HashSet noLogText = new HashSet();

	public ChatElement(ChatElementOwner chatElementOwner, int int1, String string) {
		this.owner = chatElementOwner;
		this.setMaxChatLines(int1);
		this.setMaxCharsPerLine(75);
		this.talkerType = string != null ? string : this.talkerType;
		if (backdropTexture == null) {
			backdropTexture = new NineGridTexture("NineGridBlack", 5);
		}
	}

	public void setMaxChatLines(int int1) {
		int1 = int1 < 1 ? 1 : (int1 > 10 ? 10 : int1);
		if (int1 != this.maxChatLines) {
			this.maxChatLines = int1;
			for (int int2 = 0; int2 < this.playerLines.length; ++int2) {
				this.playerLines[int2] = new ChatElement.PlayerLines(this.maxChatLines);
			}
		}
	}

	public int getMaxChatLines() {
		return this.maxChatLines;
	}

	public void setMaxCharsPerLine(int int1) {
		for (int int2 = 0; int2 < this.playerLines.length; ++int2) {
			this.playerLines[int2].setMaxCharsPerLine(int1);
		}

		this.maxCharsPerLine = int1;
	}

	public boolean IsSpeaking() {
		return this.Speaking;
	}

	public String getTalkerType() {
		return this.talkerType;
	}

	public void setTalkerType(String string) {
		this.talkerType = string == null ? "" : string;
	}

	public String getSayLine() {
		return this.sayLine;
	}

	public String getSayLineTag() {
		return this.Speaking && this.sayLineTag != null ? this.sayLineTag : "";
	}

	public void setHistoryRange(float float1) {
		this.historyRange = float1;
	}

	public void setUseEuclidean(boolean boolean1) {
		this.useEuclidean = boolean1;
	}

	public boolean getHasChatToDisplay() {
		return this.hasChatToDisplay;
	}

	protected float getDistance(IsoPlayer player) {
		if (player == null) {
			return -1.0F;
		} else {
			return this.useEuclidean ? (float)Math.sqrt(Math.pow((double)(this.owner.getX() - player.x), 2.0) + Math.pow((double)(this.owner.getY() - player.y), 2.0)) : Math.abs(this.owner.getX() - player.x) + Math.abs(this.owner.getY() - player.y);
		}
	}

	protected boolean playerWithinBounds(IsoPlayer player, float float1) {
		if (player == null) {
			return false;
		} else {
			return player.getX() > this.owner.getX() - float1 && player.getX() < this.owner.getX() + float1 && player.getY() > this.owner.getY() - float1 && player.getY() < this.owner.getY() + float1;
		}
	}

	public void SayDebug(int int1, String string) {
		if (!GameServer.bServer && int1 >= 0 && int1 < this.maxChatLines) {
			for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
				IsoPlayer player = IsoPlayer.players[int2];
				if (player != null) {
					ChatElement.PlayerLines playerLines = this.playerLines[int2];
					if (int1 < playerLines.chatLines.length) {
						if (playerLines.chatLines[int1].getOriginal() != null && playerLines.chatLines[int1].getOriginal().equals(string)) {
							playerLines.chatLines[int1].setInternalTickClock((float)playerLines.lineDisplayTime);
						} else {
							playerLines.chatLines[int1].setSettings(true, true, true, true, true, true);
							playerLines.chatLines[int1].setInternalTickClock((float)playerLines.lineDisplayTime);
							playerLines.chatLines[int1].setCustomTag("default");
							playerLines.chatLines[int1].setDefaultColors(1.0F, 1.0F, 1.0F, 1.0F);
							playerLines.chatLines[int1].ReadString(UIFont.Medium, string, this.maxCharsPerLine);
						}
					}
				}
			}

			this.sayLine = string;
			this.sayLineTag = "default";
			this.hasChatToDisplay = true;
		}
	}

	public void Say(String string) {
		this.addChatLine(string, 1.0F, 1.0F, 1.0F, UIFont.Dialogue, 25.0F, "default", false, false, false, false, false, true);
	}

	public void addChatLine(String string, float float1, float float2, float float3, float float4) {
		this.addChatLine(string, float1, float2, float3, UIFont.Dialogue, float4, "default", false, false, false, false, false, true);
	}

	public void addChatLine(String string, float float1, float float2, float float3) {
		this.addChatLine(string, float1, float2, float3, UIFont.Dialogue, 25.0F, "default", false, false, false, false, false, true);
	}

	public void addChatLine(String string, float float1, float float2, float float3, UIFont uIFont, float float4, String string2, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, boolean boolean5, boolean boolean6) {
		if (!GameServer.bServer) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null) {
					if (player.Traits.Deaf.isSet()) {
						if (this.owner instanceof IsoTelevision) {
							if (!((IsoTelevision)this.owner).isFacing(player)) {
								continue;
							}
						} else if (this.owner instanceof IsoRadio || this.owner instanceof VehiclePart) {
							continue;
						}
					}

					float float5 = this.getScrambleValue(player, float4);
					if (float5 < 1.0F) {
						ChatElement.PlayerLines playerLines = this.playerLines[int1];
						TextDrawObject textDrawObject = playerLines.getNewLineObject();
						if (textDrawObject != null) {
							textDrawObject.setSettings(boolean1, boolean2, boolean3, boolean4, boolean5, boolean6);
							textDrawObject.setInternalTickClock((float)playerLines.lineDisplayTime);
							textDrawObject.setCustomTag(string2);
							String string3;
							if (float5 > 0.0F) {
								string3 = ZomboidRadio.getInstance().scrambleString(string, (int)(100.0F * float5), true, "...");
								textDrawObject.setDefaultColors(0.5F, 0.5F, 0.5F, 1.0F);
							} else {
								string3 = string;
								textDrawObject.setDefaultColors(float1, float2, float3, 1.0F);
							}

							textDrawObject.ReadString(uIFont, string3, this.maxCharsPerLine);
							this.sayLine = string;
							this.sayLineTag = string2;
							this.hasChatToDisplay = true;
						}
					}
				}
			}
		}
	}

	protected float getScrambleValue(IsoPlayer player, float float1) {
		if (this.owner == player) {
			return 0.0F;
		} else {
			float float2 = 1.0F;
			boolean boolean1 = false;
			boolean boolean2 = false;
			if (this.owner.getSquare() != null && player.getSquare() != null) {
				if (player.getBuilding() != null && this.owner.getSquare().getBuilding() != null && player.getBuilding() == this.owner.getSquare().getBuilding()) {
					if (player.getSquare().getRoom() == this.owner.getSquare().getRoom()) {
						float2 = (float)((double)float2 * 2.0);
						boolean2 = true;
					} else if (Math.abs(player.getZ() - this.owner.getZ()) < 1.0F) {
						float2 = (float)((double)float2 * 2.0);
					}
				} else if (player.getBuilding() != null || this.owner.getSquare().getBuilding() != null) {
					float2 = (float)((double)float2 * 0.5);
					boolean1 = true;
				}

				if (Math.abs(player.getZ() - this.owner.getZ()) >= 1.0F) {
					float2 = (float)((double)float2 - (double)float2 * (double)Math.abs(player.getZ() - this.owner.getZ()) * 0.25);
					boolean1 = true;
				}
			}

			float float3 = float1 * float2;
			float float4 = 1.0F;
			if (float2 > 0.0F && this.playerWithinBounds(player, float3)) {
				float float5 = this.getDistance(player);
				if (float5 >= 0.0F && float5 < float3) {
					float float6 = float3 * 0.6F;
					if (boolean2 || !boolean1 && float5 < float6) {
						float4 = 0.0F;
					} else if (float3 - float6 != 0.0F) {
						float4 = (float5 - float6) / (float3 - float6);
						if (float4 < 0.2F) {
							float4 = 0.2F;
						}
					}
				}
			}

			return float4;
		}
	}

	protected void updateChatLines() {
		this.Speaking = false;
		boolean boolean1 = false;
		if (this.hasChatToDisplay) {
			this.hasChatToDisplay = false;
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				float float1 = 1.25F * GameTime.getInstance().getMultiplier();
				int int2 = this.playerLines[int1].lineDisplayTime;
				TextDrawObject[] textDrawObjectArray = this.playerLines[int1].chatLines;
				int int3 = textDrawObjectArray.length;
				for (int int4 = 0; int4 < int3; ++int4) {
					TextDrawObject textDrawObject = textDrawObjectArray[int4];
					float float2 = textDrawObject.updateInternalTickClock(float1);
					if (!(float2 <= 0.0F)) {
						this.hasChatToDisplay = true;
						if (!boolean1 && !textDrawObject.getCustomTag().equals("radio")) {
							float float3 = float2 / ((float)int2 / 2.0F);
							if (float3 >= 1.0F) {
								this.Speaking = true;
							}

							boolean1 = true;
						}

						float1 *= 1.2F;
					}
				}
			}
		}

		if (!this.Speaking) {
			this.sayLine = null;
			this.sayLineTag = null;
		}
	}

	protected void updateHistory() {
		if (this.hasChatToDisplay) {
			this.historyInRange = false;
			IsoPlayer player = IsoPlayer.getInstance();
			if (player != null) {
				if (player == this.owner) {
					this.historyVal = 1.0F;
				} else {
					if (this.playerWithinBounds(player, this.historyRange)) {
						this.historyInRange = true;
					} else {
						this.historyInRange = false;
					}

					if (this.historyInRange && this.historyVal != 1.0F) {
						this.historyVal += 0.04F;
						if (this.historyVal > 1.0F) {
							this.historyVal = 1.0F;
						}
					}

					if (!this.historyInRange && this.historyVal != 0.0F) {
						this.historyVal -= 0.04F;
						if (this.historyVal < 0.0F) {
							this.historyVal = 0.0F;
						}
					}
				}
			}
		} else if (this.historyVal != 0.0F) {
			this.historyVal = 0.0F;
		}
	}

	public void update() {
		if (!GameServer.bServer) {
			this.updateChatLines();
			this.updateHistory();
		}
	}

	public void renderBatched(int int1, int int2, int int3) {
		this.renderBatched(int1, int2, int3, false);
	}

	public void renderBatched(int int1, int int2, int int3, boolean boolean1) {
		if (int1 < this.playerLines.length && this.hasChatToDisplay && !GameServer.bServer) {
			this.playerLines[int1].renderX = int2;
			this.playerLines[int1].renderY = int3;
			this.playerLines[int1].ignoreRadioLines = boolean1;
			if (renderBatch[int1] == null) {
				renderBatch[int1] = new ChatElement.PlayerLinesList();
			}

			renderBatch[int1].add(this.playerLines[int1]);
		}
	}

	public void clear(int int1) {
		this.playerLines[int1].clear();
	}

	public static void RenderBatch(int int1) {
		if (renderBatch[int1] != null && renderBatch[int1].size() > 0) {
			for (int int2 = 0; int2 < renderBatch[int1].size(); ++int2) {
				ChatElement.PlayerLines playerLines = (ChatElement.PlayerLines)renderBatch[int1].get(int2);
				playerLines.render();
			}

			renderBatch[int1].clear();
		}
	}

	public static void NoRender(int int1) {
		if (renderBatch[int1] != null) {
			renderBatch[int1].clear();
		}
	}

	public static void addNoLogText(String string) {
		if (string != null && !string.isEmpty()) {
			noLogText.add(string);
		}
	}

	class PlayerLines {
		protected int lineDisplayTime = 314;
		protected int renderX = 0;
		protected int renderY = 0;
		protected boolean ignoreRadioLines = false;
		protected TextDrawObject[] chatLines;

		public PlayerLines(int int1) {
			this.chatLines = new TextDrawObject[int1];
			for (int int2 = 0; int2 < this.chatLines.length; ++int2) {
				this.chatLines[int2] = new TextDrawObject(0, 0, 0, true, true, true, true, true, true);
				this.chatLines[int2].setDefaultFont(UIFont.Medium);
			}
		}

		public void setMaxCharsPerLine(int int1) {
			for (int int2 = 0; int2 < this.chatLines.length; ++int2) {
				this.chatLines[int2].setMaxCharsPerLine(int1);
			}
		}

		public TextDrawObject getNewLineObject() {
			if (this.chatLines != null && this.chatLines.length > 0) {
				TextDrawObject textDrawObject = this.chatLines[this.chatLines.length - 1];
				textDrawObject.Clear();
				for (int int1 = this.chatLines.length - 1; int1 > 0; --int1) {
					this.chatLines[int1] = this.chatLines[int1 - 1];
				}

				this.chatLines[0] = textDrawObject;
				return this.chatLines[0];
			} else {
				return null;
			}
		}

		public void render() {
			if (!GameServer.bServer) {
				if (ChatElement.this.hasChatToDisplay) {
					int int1 = 0;
					TextDrawObject[] textDrawObjectArray = this.chatLines;
					int int2 = textDrawObjectArray.length;
					for (int int3 = 0; int3 < int2; ++int3) {
						TextDrawObject textDrawObject = textDrawObjectArray[int3];
						if (textDrawObject.getEnabled()) {
							if (textDrawObject.getWidth() > 0 && textDrawObject.getHeight() > 0) {
								float float1 = textDrawObject.getInternalClock();
								if (!(float1 <= 0.0F) && (!textDrawObject.getCustomTag().equals("radio") || !this.ignoreRadioLines)) {
									float float2 = float1 / ((float)this.lineDisplayTime / 4.0F);
									if (float2 > 1.0F) {
										float2 = 1.0F;
									}

									this.renderY -= textDrawObject.getHeight() + 1;
									boolean boolean1 = textDrawObject.getDefaultFontEnum() != UIFont.Dialogue;
									if (ChatElement.doBackDrop && ChatElement.backdropTexture != null) {
										ChatElement.backdropTexture.renderInnerBased(this.renderX - textDrawObject.getWidth() / 2, this.renderY, textDrawObject.getWidth(), textDrawObject.getHeight(), 0.0F, 0.0F, 0.0F, 0.4F);
									}

									if (int1 == 0) {
										textDrawObject.Draw((double)this.renderX, (double)this.renderY, boolean1, float2);
									} else if (ChatElement.this.historyVal > 0.0F) {
										textDrawObject.Draw((double)this.renderX, (double)this.renderY, boolean1, float2 * ChatElement.this.historyVal);
									}

									++int1;
								}
							} else {
								++int1;
							}
						}
					}
				}
			}
		}

		void clear() {
			if (ChatElement.this.hasChatToDisplay) {
				ChatElement.this.hasChatToDisplay = false;
				for (int int1 = 0; int1 < this.chatLines.length; ++int1) {
					if (!(this.chatLines[int1].getInternalClock() <= 0.0F)) {
						this.chatLines[int1].Clear();
						this.chatLines[int1].updateInternalTickClock(this.chatLines[int1].getInternalClock());
					}
				}

				ChatElement.this.historyInRange = false;
				ChatElement.this.historyVal = 0.0F;
			}
		}
	}

	class PlayerLinesList extends ArrayList {
	}
}
