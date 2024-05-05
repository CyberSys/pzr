package zombie.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.debug.DebugLog;


public class RCONServer {
	public static final int SERVERDATA_RESPONSE_VALUE = 0;
	public static final int SERVERDATA_AUTH_RESPONSE = 2;
	public static final int SERVERDATA_EXECCOMMAND = 2;
	public static final int SERVERDATA_AUTH = 3;
	private static RCONServer instance;
	private ServerSocket welcomeSocket;
	private RCONServer.ServerThread thread;
	private String password;
	private ConcurrentLinkedQueue toMain = new ConcurrentLinkedQueue();

	private RCONServer(int int1, String string) {
		this.password = string;
		try {
			this.welcomeSocket = new ServerSocket();
			if (GameServer.IPCommandline != null) {
				this.welcomeSocket.bind(new InetSocketAddress(GameServer.IPCommandline, int1));
			} else {
				this.welcomeSocket.bind(new InetSocketAddress(int1));
			}

			DebugLog.log("RCON: listening on port " + int1);
		} catch (IOException ioException) {
			DebugLog.log("RCON: error creating socket on port " + int1);
			ioException.printStackTrace();
			try {
				this.welcomeSocket.close();
				this.welcomeSocket = null;
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}

			return;
		}

		this.thread = new RCONServer.ServerThread();
		this.thread.start();
	}

	private void updateMain() {
		for (RCONServer.ExecCommand execCommand = (RCONServer.ExecCommand)this.toMain.poll(); execCommand != null; execCommand = (RCONServer.ExecCommand)this.toMain.poll()) {
			execCommand.update();
		}
	}

	public void quit() {
		if (this.welcomeSocket != null) {
			try {
				this.welcomeSocket.close();
			} catch (IOException ioException) {
			}

			this.welcomeSocket = null;
			this.thread.quit();
			this.thread = null;
		}
	}

	public static void init(int int1, String string) {
		instance = new RCONServer(int1, string);
	}

	public static void update() {
		if (instance != null) {
			instance.updateMain();
		}
	}

	public static void shutdown() {
		if (instance != null) {
			instance.quit();
		}
	}

	private class ServerThread extends Thread {
		private ArrayList connections = new ArrayList();
		public boolean bQuit;

		public ServerThread() {
			this.setName("RCONServer");
		}

		public void run() {
			while (!this.bQuit) {
				this.runInner();
			}
		}

		private void runInner() {
			try {
				Socket socket = RCONServer.this.welcomeSocket.accept();
				for (int int1 = 0; int1 < this.connections.size(); ++int1) {
					RCONServer.ClientThread clientThread = (RCONServer.ClientThread)this.connections.get(int1);
					if (!clientThread.isAlive()) {
						this.connections.remove(int1--);
					}
				}

				if (this.connections.size() >= 5) {
					socket.close();
					return;
				}

				DebugLog.log("RCON: new connection " + socket.toString());
				RCONServer.ClientThread clientThread2 = new RCONServer.ClientThread(socket, RCONServer.this.password);
				this.connections.add(clientThread2);
				clientThread2.start();
			} catch (IOException ioException) {
				if (!this.bQuit) {
					ioException.printStackTrace();
				}
			}
		}

		public void quit() {
			this.bQuit = true;
			while (this.isAlive()) {
				try {
					Thread.sleep(50L);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
			}

			for (int int1 = 0; int1 < this.connections.size(); ++int1) {
				RCONServer.ClientThread clientThread = (RCONServer.ClientThread)this.connections.get(int1);
				clientThread.quit();
			}
		}
	}

	private static class ExecCommand {
		public int ID;
		public String command;
		public String response;
		public RCONServer.ClientThread thread;

		public ExecCommand(int int1, String string, RCONServer.ClientThread clientThread) {
			this.ID = int1;
			this.command = string;
			this.thread = clientThread;
		}

		public void update() {
			this.response = GameServer.rcon(this.command);
			if (this.thread.isAlive()) {
				this.thread.toThread.add(this);
			}
		}
	}

	private static class ClientThread extends Thread {
		public Socket socket;
		public boolean bAuth;
		public boolean bQuit;
		private String password;
		private InputStream in;
		private OutputStream out;
		private ConcurrentLinkedQueue toThread = new ConcurrentLinkedQueue();
		private int pendingCommands;

		public ClientThread(Socket socket, String string) {
			this.socket = socket;
			this.password = string;
			try {
				this.in = socket.getInputStream();
				this.out = socket.getOutputStream();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			this.setName("RCONClient" + socket.getLocalPort());
		}

		public void run() {
			if (this.in != null) {
				if (this.out != null) {
					while (!this.bQuit) {
						try {
							this.runInner();
						} catch (SocketException socketException) {
							this.bQuit = true;
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}

					try {
						this.socket.close();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}

					DebugLog.log("RCON: connection closed " + this.socket.toString());
				}
			}
		}

		private void runInner() throws IOException {
			byte[] byteArray = new byte[4];
			int int1 = this.in.read(byteArray, 0, 4);
			if (int1 < 0) {
				this.bQuit = true;
			} else {
				ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
				byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
				int int2 = byteBuffer.getInt();
				int int3 = int2;
				byte[] byteArray2 = new byte[int2];
				do {
					int1 = this.in.read(byteArray2, int2 - int3, int3);
					if (int1 < 0) {
						this.bQuit = true;
						return;
					}

					int3 -= int1;
				}	 while (int3 > 0);

				byteBuffer = ByteBuffer.wrap(byteArray2);
				byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
				int int4 = byteBuffer.getInt();
				int int5 = byteBuffer.getInt();
				String string = new String(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit() - byteBuffer.position() - 2);
				this.handlePacket(int4, int5, string);
			}
		}

		private void handlePacket(int int1, int int2, String string) throws IOException {
			if (!"players".equals(string)) {
				DebugLog.log("RCON: ID=" + int1 + " Type=" + int2 + " Body=\'" + string + "\' " + this.socket.toString());
			}

			ByteBuffer byteBuffer;
			switch (int2) {
			case 0: 
				if (this.checkAuth()) {
					byteBuffer = ByteBuffer.allocate(14);
					byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
					byteBuffer.putInt(byteBuffer.capacity() - 4);
					byteBuffer.putInt(int1);
					byteBuffer.putInt(0);
					byteBuffer.putShort((short)0);
					this.out.write(byteBuffer.array());
					this.out.write(byteBuffer.array());
				}

				break;
			
			case 1: 
			
			default: 
				DebugLog.log("RCON: unknown packet Type=" + int2);
				break;
			
			case 2: 
				if (this.checkAuth()) {
					RCONServer.ExecCommand execCommand = new RCONServer.ExecCommand(int1, string, this);
					++this.pendingCommands;
					RCONServer.instance.toMain.add(execCommand);
					while (this.pendingCommands > 0) {
						execCommand = (RCONServer.ExecCommand)this.toThread.poll();
						if (execCommand != null) {
							--this.pendingCommands;
							this.handleResponse(execCommand);
						} else {
							try {
								Thread.sleep(50L);
							} catch (InterruptedException interruptedException) {
							}
						}
					}
				}

				break;
			
			case 3: 
				this.bAuth = string.equals(this.password);
				if (!this.bAuth) {
					DebugLog.log("RCON: password doesn\'t match");
					this.bQuit = true;
				}

				byteBuffer = ByteBuffer.allocate(14);
				byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
				byteBuffer.putInt(byteBuffer.capacity() - 4);
				byteBuffer.putInt(int1);
				byteBuffer.putInt(0);
				byteBuffer.putShort((short)0);
				this.out.write(byteBuffer.array());
				byteBuffer.clear();
				byteBuffer.putInt(byteBuffer.capacity() - 4);
				byteBuffer.putInt(this.bAuth ? int1 : -1);
				byteBuffer.putInt(2);
				byteBuffer.putShort((short)0);
				this.out.write(byteBuffer.array());
			
			}
		}

		public void handleResponse(RCONServer.ExecCommand execCommand) {
			String string = execCommand.response;
			if (string == null) {
				string = "";
			}

			ByteBuffer byteBuffer = ByteBuffer.allocate(12 + string.length() + 2);
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			byteBuffer.putInt(byteBuffer.capacity() - 4);
			byteBuffer.putInt(execCommand.ID);
			byteBuffer.putInt(0);
			byteBuffer.put(string.getBytes());
			byteBuffer.putShort((short)0);
			try {
				this.out.write(byteBuffer.array());
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		private boolean checkAuth() throws IOException {
			if (this.bAuth) {
				return true;
			} else {
				this.bQuit = true;
				ByteBuffer byteBuffer = ByteBuffer.allocate(14);
				byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
				byteBuffer.putInt(byteBuffer.capacity() - 4);
				byteBuffer.putInt(-1);
				byteBuffer.putInt(2);
				byteBuffer.putShort((short)0);
				this.out.write(byteBuffer.array());
				return false;
			}
		}

		public void quit() {
			if (this.socket != null) {
				try {
					this.socket.close();
				} catch (IOException ioException) {
				}
			}

			this.bQuit = true;
			while (this.isAlive()) {
				try {
					Thread.sleep(50L);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
			}
		}
	}
}
