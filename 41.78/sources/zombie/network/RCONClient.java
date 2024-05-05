package zombie.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import zombie.util.StringUtils;


public class RCONClient {
	private Socket socket;

	public boolean disconnect() {
		try {
			this.socket.close();
			return true;
		} catch (IOException ioException) {
			System.out.println("Disconnect failed: " + ioException.getMessage());
			return false;
		}
	}

	public boolean connect(String string, String string2) {
		try {
			this.socket = new Socket();
			this.socket.setSoTimeout(5000);
			InetSocketAddress inetSocketAddress = new InetSocketAddress(string, Integer.parseInt(string2));
			this.socket.connect(inetSocketAddress, 1000);
			return true;
		} catch (IOException ioException) {
			System.out.println("Connect failed: " + ioException.getMessage());
			return false;
		}
	}

	public boolean auth(String string) {
		try {
			int int1 = (int)(65535L & System.currentTimeMillis());
			RCONClient.RCONMessage rCONMessage = new RCONClient.RCONMessage(int1, 3, string);
			rCONMessage.writeObject(this.socket.getOutputStream());
			RCONClient.RCONMessage rCONMessage2 = new RCONClient.RCONMessage();
			rCONMessage2.readObject(this.socket.getInputStream(), 14);
			if (rCONMessage2.type == 0 && rCONMessage2.id == int1) {
				RCONClient.RCONMessage rCONMessage3 = new RCONClient.RCONMessage();
				rCONMessage3.readObject(this.socket.getInputStream(), 14);
				if (rCONMessage3.type == 2 && rCONMessage2.id == int1) {
					return true;
				} else {
					System.out.println("Authentication failed: auth response");
					return false;
				}
			} else {
				System.out.println("Authentication failed: response value");
				return false;
			}
		} catch (IOException ioException) {
			System.out.println("Authentication failed: timeout");
			return false;
		}
	}

	public String exec(String string) {
		try {
			int int1 = (int)(65535L & System.currentTimeMillis());
			RCONClient.RCONMessage rCONMessage = new RCONClient.RCONMessage(int1, 2, string);
			rCONMessage.writeObject(this.socket.getOutputStream());
			RCONClient.RCONMessage rCONMessage2 = new RCONClient.RCONMessage();
			rCONMessage2.readObject(this.socket.getInputStream(), 0);
			return new String(rCONMessage2.body);
		} catch (IOException ioException) {
			System.out.println("Command execution failed");
			return null;
		}
	}

	public boolean send(String string, String string2) {
		try {
			HttpClient httpClient = HttpClient.newHttpClient();
			HttpRequest httpRequest = HttpRequest.newBuilder().setHeader("Content-type", "application/json").uri(URI.create(string)).POST(BodyPublishers.ofString("{\"text\":\"" + string2 + "\"}")).build();
			HttpResponse httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
			if (httpResponse != null && httpResponse.statusCode() != 200) {
				System.out.println((String)httpResponse.body());
				return false;
			} else {
				return true;
			}
		} catch (Exception exception) {
			System.out.println("Result post failed");
			return false;
		}
	}

	private static void sleep(long long1) {
		try {
			Thread.sleep(long1);
		} catch (Exception exception) {
		}
	}

	public static void main(String[] stringArray) {
		String string = null;
		String string2 = null;
		String string3 = null;
		String string4 = null;
		String string5 = null;
		boolean boolean1 = false;
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			if (!StringUtils.isNullOrEmpty(stringArray[int1])) {
				if (stringArray[int1].equals("-ip")) {
					++int1;
					string = stringArray[int1].trim();
				} else if (stringArray[int1].equals("-port")) {
					++int1;
					string2 = stringArray[int1].trim();
				} else if (stringArray[int1].equals("-password")) {
					++int1;
					string3 = stringArray[int1].trim();
				} else if (stringArray[int1].equals("-command")) {
					++int1;
					string4 = stringArray[int1].trim();
				} else if (stringArray[int1].equals("-webhook")) {
					++int1;
					string5 = stringArray[int1].trim();
				}
			}
		}

		if (!StringUtils.isNullOrEmpty(string) && !StringUtils.isNullOrEmpty(string2) && !StringUtils.isNullOrEmpty(string3) && !StringUtils.isNullOrEmpty(string4)) {
			if (!StringUtils.isNullOrEmpty(string5)) {
				boolean1 = true;
			}

			RCONClient rCONClient = new RCONClient();
			do {
				if (rCONClient.connect(string, string2)) {
					if (rCONClient.auth(string3)) {
						if (boolean1 && !rCONClient.send(string5, String.format("Connected to server %s:%s", string, string2))) {
							break;
						}

						String string6 = null;
						do {
							String string7 = rCONClient.exec(string4);
							if (StringUtils.isNullOrEmpty(string7)) {
								break;
							}

							if (!string7.equals(string6)) {
								if (boolean1) {
									if (!rCONClient.send(string5, string7)) {
										break;
									}

									sleep(5000L);
								} else {
									System.out.println(string7);
								}
							}

							string6 = string7;
						}				 while (boolean1);

						if (boolean1 && !rCONClient.send(string5, "Connection to server lost")) {
							break;
						}
					}

					rCONClient.disconnect();
				}

				if (boolean1) {
					sleep(60000L);
				}
			}	 while (boolean1);
		} else {
			System.out.println("Incorrect arguments");
		}
	}

	private static class RCONMessage {
		private static final byte[] input = new byte[4096];
		private static final ByteBuffer bbr;
		private static final byte[] output;
		private static final ByteBuffer bbw;
		static final int baseSize = 10;
		int size;
		int id;
		int type;
		byte[] body;

		RCONMessage() {
		}

		RCONMessage(int int1, int int2, String string) throws UnsupportedEncodingException {
			this.id = int1;
			this.type = int2;
			this.body = string.getBytes();
			this.size = 10 + string.length();
		}

		private void writeObject(OutputStream outputStream) throws IOException {
			bbw.putInt(this.size);
			bbw.putInt(this.id);
			bbw.putInt(this.type);
			bbw.put(this.body);
			bbw.put((byte)0);
			bbw.put((byte)0);
			outputStream.write(output, 0, this.size + 4);
			bbw.clear();
		}

		private void readObject(InputStream inputStream, int int1) throws IOException {
			if (int1 == 0) {
				inputStream.read(input);
			} else {
				inputStream.read(input, 0, int1);
			}

			this.size = bbr.getInt();
			this.id = bbr.getInt();
			this.type = bbr.getInt();
			if (this.size > 10) {
				this.body = new byte[this.size - 10];
				bbr.get(this.body, 0, this.size - 10);
			}

			bbr.get();
			bbr.get();
			bbr.clear();
		}

		static  {
			bbr = ByteBuffer.wrap(input);
			output = new byte[4096];
			bbw = ByteBuffer.wrap(output);
			bbr.order(ByteOrder.LITTLE_ENDIAN);
			bbw.order(ByteOrder.LITTLE_ENDIAN);
		}
	}
}
