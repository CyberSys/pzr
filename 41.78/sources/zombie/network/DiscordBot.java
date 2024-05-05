package zombie.network;

import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;


public class DiscordBot {
	private DiscordAPI api;
	private Collection channels;
	private Channel current;
	private String currentChannelName;
	private String currentChannelID;
	private String name;
	private DiscordSender sender;

	public DiscordBot(String string, DiscordSender discordSender) {
		this.name = string;
		this.sender = discordSender;
		this.current = null;
	}

	public void connect(boolean boolean1, String string, String string2, String string3) {
		if (string == null || string.isEmpty()) {
			DebugLog.log(DebugType.Network, "DISCORD: token not configured");
			boolean1 = false;
		}

		if (!boolean1) {
			DebugLog.log(DebugType.Network, "*** DISCORD DISABLED ****");
			this.current = null;
		} else {
			this.api = Javacord.getApi(string, true);
			this.api.connect(new DiscordBot.Connector());
			DebugLog.log(DebugType.Network, "*** DISCORD ENABLED ****");
			this.currentChannelName = string2;
			this.currentChannelID = string3;
		}
	}

	private void setChannel(String string, String string2) {
		Collection collection = this.getChannelNames();
		if ((string == null || string.isEmpty()) && !collection.isEmpty()) {
			string = (String)collection.iterator().next();
			DebugLog.log(DebugType.Network, "DISCORD: set default channel name = \"" + string + "\"");
		}

		if (string2 != null && !string2.isEmpty()) {
			this.setChannelByID(string2);
		} else {
			if (string != null) {
				this.setChannelByName(string);
			}
		}
	}

	public void sendMessage(String string, String string2) {
		if (this.current != null) {
			this.current.sendMessage(string + ": " + string2);
			DebugLog.log(DebugType.Network, "DISCORD: User \'" + string + "\' send message: \'" + string2 + "\'");
		}
	}

	private Collection getChannelNames() {
		ArrayList arrayList = new ArrayList();
		this.channels = this.api.getChannels();
		Iterator iterator = this.channels.iterator();
		while (iterator.hasNext()) {
			Channel channel = (Channel)iterator.next();
			arrayList.add(channel.getName());
		}

		return arrayList;
	}

	private void setChannelByName(String string) {
		this.current = null;
		Iterator iterator = this.channels.iterator();
		while (iterator.hasNext()) {
			Channel channel = (Channel)iterator.next();
			if (channel.getName().equals(string)) {
				if (this.current != null) {
					DebugLog.log(DebugType.Network, "Discord server has few channels with name \'" + string + "\'. Please, use channel ID instead");
					this.current = null;
					return;
				}

				this.current = channel;
			}
		}

		if (this.current == null) {
			DebugLog.log(DebugType.Network, "DISCORD: channel \"" + string + "\" is not found. Try to use channel ID instead");
		} else {
			DebugLog.log(DebugType.Network, "Discord enabled on channel: " + string);
		}
	}

	private void setChannelByID(String string) {
		this.current = null;
		Iterator iterator = this.channels.iterator();
		while (iterator.hasNext()) {
			Channel channel = (Channel)iterator.next();
			if (channel.getId().equals(string)) {
				DebugLog.log(DebugType.Network, "Discord enabled on channel with ID: " + string);
				this.current = channel;
				break;
			}
		}

		if (this.current == null) {
			DebugLog.log(DebugType.Network, "DISCORD: channel with ID \"" + string + "\" not found");
		}
	}

	class Connector implements FutureCallback {

		public void onSuccess(DiscordAPI discordAPI) {
			DebugLog.log(DebugType.Network, "*** DISCORD API CONNECTED ****");
			DiscordBot.this.setChannel(DiscordBot.this.currentChannelName, DiscordBot.this.currentChannelID);
			discordAPI.registerListener(DiscordBot.this.new Listener());
			discordAPI.updateUsername(DiscordBot.this.name);
			if (DiscordBot.this.current != null) {
				DebugLog.log(DebugType.Network, "*** DISCORD INITIALIZATION SUCCEEDED ****");
			} else {
				DebugLog.log(DebugType.Network, "*** DISCORD INITIALIZATION FAILED ****");
			}
		}

		public void onFailure(Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	class Listener implements MessageCreateListener {

		public void onMessageCreate(DiscordAPI discordAPI, Message message) {
			if (DiscordBot.this.current != null) {
				if (!discordAPI.getYourself().getId().equals(message.getAuthor().getId())) {
					if (message.getChannelReceiver().getId().equals(DiscordBot.this.current.getId())) {
						DebugLog.log(DebugType.Network, "DISCORD: get message on current channel");
						DebugType debugType = DebugType.Network;
						String string = message.getContent();
						DebugLog.log(debugType, "DISCORD: send message = \"" + string + "\" for " + message.getAuthor().getName() + ")");
						String string2 = this.replaceChannelIDByItsName(discordAPI, message);
						string2 = this.removeSmilesAndImages(string2);
						if (!string2.isEmpty() && !string2.matches("^\\s$")) {
							DiscordBot.this.sender.sendMessageFromDiscord(message.getAuthor().getName(), string2);
						}
					}
				}
			}
		}

		private String replaceChannelIDByItsName(DiscordAPI discordAPI, Message message) {
			String string = message.getContent();
			Pattern pattern = Pattern.compile("<#(\\d+)>");
			Matcher matcher = pattern.matcher(message.getContent());
			if (matcher.find()) {
				for (int int1 = 1; int1 <= matcher.groupCount(); ++int1) {
					Channel channel = discordAPI.getChannelById(matcher.group(int1));
					if (channel != null) {
						string = string.replaceAll("<#" + matcher.group(int1) + ">", "#" + channel.getName());
					}
				}
			}

			return string;
		}

		private String removeSmilesAndImages(String string) {
			StringBuilder stringBuilder = new StringBuilder();
			char[] charArray = string.toCharArray();
			int int1 = charArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				Character character = charArray[int2];
				if (!Character.isLowSurrogate(character) && !Character.isHighSurrogate(character)) {
					stringBuilder.append(character);
				}
			}

			return stringBuilder.toString();
		}
	}
}
