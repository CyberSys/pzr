package zombie.chat;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.network.chat.ChatType;


public final class ChatUtility {
	private static final boolean useEuclidean = true;
	private static final HashMap allowedChatIcons = new HashMap();
	private static final HashMap allowedChatIconsFull = new HashMap();
	private static final StringBuilder builder = new StringBuilder();
	private static final StringBuilder builderTest = new StringBuilder();

	private ChatUtility() {
	}

	public static float getScrambleValue(IsoObject object, IsoPlayer player, float float1) {
		return getScrambleValue(object.getX(), object.getY(), object.getZ(), object.getSquare(), player, float1);
	}

	public static float getScrambleValue(float float1, float float2, float float3, IsoGridSquare square, IsoPlayer player, float float4) {
		float float5 = 1.0F;
		boolean boolean1 = false;
		boolean boolean2 = false;
		if (square != null && player.getSquare() != null) {
			if (player.getBuilding() != null && square.getBuilding() != null && player.getBuilding() == square.getBuilding()) {
				if (player.getSquare().getRoom() == square.getRoom()) {
					float5 = (float)((double)float5 * 2.0);
					boolean2 = true;
				} else if (Math.abs(player.getZ() - float3) < 1.0F) {
					float5 = (float)((double)float5 * 2.0);
				}
			} else if (player.getBuilding() != null || square.getBuilding() != null) {
				float5 = (float)((double)float5 * 0.5);
				boolean1 = true;
			}

			if (Math.abs(player.getZ() - float3) >= 1.0F) {
				float5 = (float)((double)float5 - (double)float5 * (double)Math.abs(player.getZ() - float3) * 0.25);
				boolean1 = true;
			}
		}

		float float6 = float4 * float5;
		float float7 = 1.0F;
		if (float5 > 0.0F && playerWithinBounds(float1, float2, player, float6)) {
			float float8 = getDistance(float1, float2, player);
			if (float8 >= 0.0F && float8 < float6) {
				float float9 = float6 * 0.6F;
				if (!boolean2 && (boolean1 || !(float8 < float9))) {
					if (float6 - float9 != 0.0F) {
						float7 = (float8 - float9) / (float6 - float9);
						if (float7 < 0.2F) {
							float7 = 0.2F;
						}
					}
				} else {
					float7 = 0.0F;
				}
			}
		}

		return float7;
	}

	public static boolean playerWithinBounds(IsoObject object, IsoObject object2, float float1) {
		return playerWithinBounds(object.getX(), object.getY(), object2, float1);
	}

	public static boolean playerWithinBounds(float float1, float float2, IsoObject object, float float3) {
		if (object == null) {
			return false;
		} else {
			return object.getX() > float1 - float3 && object.getX() < float1 + float3 && object.getY() > float2 - float3 && object.getY() < float2 + float3;
		}
	}

	public static float getDistance(IsoObject object, IsoPlayer player) {
		return player == null ? -1.0F : (float)Math.sqrt(Math.pow((double)(object.getX() - player.x), 2.0) + Math.pow((double)(object.getY() - player.y), 2.0));
	}

	public static float getDistance(float float1, float float2, IsoPlayer player) {
		return player == null ? -1.0F : (float)Math.sqrt(Math.pow((double)(float1 - player.x), 2.0) + Math.pow((double)(float2 - player.y), 2.0));
	}

	public static UdpConnection findConnection(int int1) {
		UdpConnection udpConnection = null;
		if (GameServer.udpEngine != null) {
			for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int2);
				for (int int3 = 0; int3 < udpConnection2.playerIDs.length; ++int3) {
					if (udpConnection2.playerIDs[int3] == int1) {
						udpConnection = udpConnection2;
						break;
					}
				}
			}
		}

		if (udpConnection == null) {
			DebugLog.log("Connection with PlayerID =\'" + int1 + "\' not found!");
		}

		return udpConnection;
	}

	public static UdpConnection findConnection(String string) {
		UdpConnection udpConnection = null;
		if (GameServer.udpEngine != null) {
			for (int int1 = 0; int1 < GameServer.udpEngine.connections.size() && udpConnection == null; ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				for (int int2 = 0; int2 < udpConnection2.players.length; ++int2) {
					if (udpConnection2.players[int2] != null && udpConnection2.players[int2].username.equalsIgnoreCase(string)) {
						udpConnection = udpConnection2;
						break;
					}
				}
			}
		}

		if (udpConnection == null) {
			DebugLog.log("Player with nickname = \'" + string + "\' not found!");
		}

		return udpConnection;
	}

	public static IsoPlayer findPlayer(int int1) {
		IsoPlayer player = null;
		if (GameServer.udpEngine != null) {
			for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int2);
				for (int int3 = 0; int3 < udpConnection.playerIDs.length; ++int3) {
					if (udpConnection.playerIDs[int3] == int1) {
						player = udpConnection.players[int3];
						break;
					}
				}
			}
		}

		if (player == null) {
			DebugLog.log("Player with PlayerID =\'" + int1 + "\' not found!");
		}

		return player;
	}

	public static String findPlayerName(int int1) {
		return findPlayer(int1).getUsername();
	}

	public static IsoPlayer findPlayer(String string) {
		IsoPlayer player = null;
		if (GameClient.bClient) {
			player = GameClient.instance.getPlayerFromUsername(string);
		} else if (GameServer.bServer) {
			player = GameServer.getPlayerByUserName(string);
		}

		if (player == null) {
			DebugLog.log("Player with nickname = \'" + string + "\' not found!");
		}

		return player;
	}

	public static ArrayList getAllowedChatStreams() {
		String string = ServerOptions.getInstance().ChatStreams.getValue();
		string = string.replaceAll("\"", "");
		String[] stringArray = string.split(",");
		ArrayList arrayList = new ArrayList();
		arrayList.add(ChatType.server);
		String[] stringArray2 = stringArray;
		int int1 = stringArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			String string2 = stringArray2[int2];
			byte byte1 = -1;
			switch (string2.hashCode()) {
			case 97: 
				if (string2.equals("a")) {
					byte1 = 2;
				}

				break;
			
			case 102: 
				if (string2.equals("f")) {
					byte1 = 6;
				}

				break;
			
			case 114: 
				if (string2.equals("r")) {
					byte1 = 1;
				}

				break;
			
			case 115: 
				if (string2.equals("s")) {
					byte1 = 0;
				}

				break;
			
			case 119: 
				if (string2.equals("w")) {
					byte1 = 3;
				}

				break;
			
			case 121: 
				if (string2.equals("y")) {
					byte1 = 4;
				}

				break;
			
			case 3669: 
				if (string2.equals("sh")) {
					byte1 = 5;
				}

				break;
			
			case 96673: 
				if (string2.equals("all")) {
					byte1 = 7;
				}

			
			}

			switch (byte1) {
			case 0: 
				arrayList.add(ChatType.say);
				break;
			
			case 1: 
				arrayList.add(ChatType.radio);
				break;
			
			case 2: 
				arrayList.add(ChatType.admin);
				break;
			
			case 3: 
				arrayList.add(ChatType.whisper);
				break;
			
			case 4: 
				arrayList.add(ChatType.shout);
				break;
			
			case 5: 
				arrayList.add(ChatType.safehouse);
				break;
			
			case 6: 
				arrayList.add(ChatType.faction);
				break;
			
			case 7: 
				arrayList.add(ChatType.general);
			
			}
		}

		return arrayList;
	}

	public static boolean chatStreamEnabled(ChatType chatType) {
		ArrayList arrayList = getAllowedChatStreams();
		return arrayList.contains(chatType);
	}

	public static void InitAllowedChatIcons() {
		allowedChatIcons.clear();
		Texture.collectAllIcons(allowedChatIcons, allowedChatIconsFull);
	}

	private static String getColorString(String string, boolean boolean1) {
		if (Colors.ColorExists(string)) {
			Color color = Colors.GetColorByName(string);
			if (boolean1) {
				float float1 = color.getRedFloat();
				return float1 + "," + color.getGreenFloat() + "," + color.getBlueFloat();
			} else {
				int int1 = color.getRed();
				return int1 + "," + color.getGreen() + "," + color.getBlue();
			}
		} else {
			if (string.length() <= 11 && string.contains(",")) {
				String[] stringArray = string.split(",");
				if (stringArray.length == 3) {
					int int2 = parseColorInt(stringArray[0]);
					int int3 = parseColorInt(stringArray[1]);
					int int4 = parseColorInt(stringArray[2]);
					if (int2 != -1 && int3 != -1 && int4 != -1) {
						if (boolean1) {
							return (float)int2 / 255.0F + "," + (float)int3 / 255.0F + "," + (float)int4 / 255.0F;
						}

						return int2 + "," + int3 + "," + int4;
					}
				}
			}

			return null;
		}
	}

	private static int parseColorInt(String string) {
		try {
			int int1 = Integer.parseInt(string);
			return int1 >= 0 && int1 <= 255 ? int1 : -1;
		} catch (Exception exception) {
			return -1;
		}
	}

	public static String parseStringForChatBubble(String string) {
		try {
			builder.delete(0, builder.length());
			builderTest.delete(0, builderTest.length());
			string = string.replaceAll("\\[br/]", "");
			string = string.replaceAll("\\[cdt=", "");
			char[] charArray = string.toCharArray();
			boolean boolean1 = false;
			boolean boolean2 = false;
			int int1 = 0;
			for (int int2 = 0; int2 < charArray.length; ++int2) {
				char char1 = charArray[int2];
				if (char1 != '*') {
					if (boolean1) {
						builderTest.append(char1);
					} else {
						builder.append(char1);
					}
				} else if (!boolean1) {
					boolean1 = true;
				} else {
					String string2 = builderTest.toString();
					builderTest.delete(0, builderTest.length());
					String string3 = getColorString(string2, false);
					if (string3 != null) {
						if (boolean2) {
							builder.append("[/]");
						}

						builder.append("[col=");
						builder.append(string3);
						builder.append(']');
						boolean1 = false;
						boolean2 = true;
					} else if (int1 < 10 && (string2.equalsIgnoreCase("music") || allowedChatIcons.containsKey(string2.toLowerCase()))) {
						if (boolean2) {
							builder.append("[/]");
							boolean2 = false;
						}

						builder.append("[img=");
						builder.append(string2.equalsIgnoreCase("music") ? "music" : (String)allowedChatIcons.get(string2.toLowerCase()));
						builder.append(']');
						boolean1 = false;
						++int1;
					} else {
						builder.append('*');
						builder.append(string2);
					}
				}
			}

			if (boolean1) {
				builder.append('*');
				String string4 = builderTest.toString();
				if (string4.length() > 0) {
					builder.append(string4);
				}

				if (boolean2) {
					builder.append("[/]");
				}
			}

			return builder.toString();
		} catch (Exception exception) {
			exception.printStackTrace();
			return string;
		}
	}

	public static String parseStringForChatLog(String string) {
		try {
			builder.delete(0, builder.length());
			builderTest.delete(0, builderTest.length());
			char[] charArray = string.toCharArray();
			boolean boolean1 = false;
			boolean boolean2 = false;
			int int1 = 0;
			for (int int2 = 0; int2 < charArray.length; ++int2) {
				char char1 = charArray[int2];
				if (char1 != '*') {
					if (boolean1) {
						builderTest.append(char1);
					} else {
						builder.append(char1);
					}
				} else if (!boolean1) {
					boolean1 = true;
				} else {
					String string2 = builderTest.toString();
					builderTest.delete(0, builderTest.length());
					String string3 = getColorString(string2, true);
					if (string3 != null) {
						builder.append(" <RGB:");
						builder.append(string3);
						builder.append('>');
						boolean1 = false;
						boolean2 = true;
					} else {
						if (int1 < 10 && (string2.equalsIgnoreCase("music") || allowedChatIconsFull.containsKey(string2.toLowerCase()))) {
							if (boolean2) {
								builder.append(" <RGB:");
								builder.append("1.0,1.0,1.0");
								builder.append('>');
								boolean2 = false;
							}

							String string4 = string2.equalsIgnoreCase("music") ? "Icon_music_notes" : (String)allowedChatIconsFull.get(string2.toLowerCase());
							Texture texture = Texture.getSharedTexture(string4);
							if (Texture.getSharedTexture(string4) != null) {
								int int3 = (int)((float)texture.getWidth() * 0.5F);
								int int4 = (int)((float)texture.getHeight() * 0.5F);
								if (string2.equalsIgnoreCase("music")) {
									int3 = (int)((float)texture.getWidth() * 0.75F);
									int4 = (int)((float)texture.getHeight() * 0.75F);
								}

								builder.append("<IMAGE:");
								builder.append(string4);
								builder.append("," + int3 + "," + int4 + ">");
								boolean1 = false;
								++int1;
								continue;
							}
						}

						builder.append('*');
						builder.append(string2);
					}
				}
			}

			if (boolean1) {
				builder.append('*');
				String string5 = builderTest.toString();
				if (string5.length() > 0) {
					builder.append(string5);
				}
			}

			return builder.toString();
		} catch (Exception exception) {
			exception.printStackTrace();
			return string;
		}
	}
}
