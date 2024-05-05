package zombie.network;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoGridSquare;
import zombie.iso.LosUtil;


public class ServerLOS {
	public static ServerLOS instance;
	private ServerLOS.LOSThread thread;
	private ArrayList playersMain = new ArrayList();
	private ArrayList playersLOS = new ArrayList();
	private boolean bMapLoading = false;
	private boolean bSuspended = false;
	boolean bWasSuspended;

	private void noise(String string) {
	}

	public static void init() {
		instance = new ServerLOS();
		instance.start();
	}

	public void start() {
		this.thread = new ServerLOS.LOSThread();
		this.thread.setName("LOS");
		this.thread.setDaemon(true);
		this.thread.start();
	}

	public void addPlayer(IsoPlayer player) {
		synchronized (this.playersMain) {
			if (this.findData(player) == null) {
				ServerLOS.PlayerData playerData = new ServerLOS.PlayerData(player);
				this.playersMain.add(playerData);
				synchronized (this.thread.notifier) {
					this.thread.notifier.notify();
				}
			}
		}
	}

	public void removePlayer(IsoPlayer player) {
		synchronized (this.playersMain) {
			ServerLOS.PlayerData playerData = this.findData(player);
			this.playersMain.remove(playerData);
			synchronized (this.thread.notifier) {
				this.thread.notifier.notify();
			}
		}
	}

	public boolean isCouldSee(IsoPlayer player, IsoGridSquare square) {
		ServerLOS.PlayerData playerData = this.findData(player);
		if (playerData != null) {
			int int1 = square.x - playerData.px + 50;
			int int2 = square.y - playerData.py + 50;
			if (int1 >= 0 && int1 < 100 && int2 >= 0 && int2 < 100) {
				return playerData.visible[int1][int2][square.z];
			}
		}

		return false;
	}

	public void doServerZombieLOS(IsoPlayer player) {
		if (ServerMap.instance.bUpdateLOSThisFrame) {
			ServerLOS.PlayerData playerData = this.findData(player);
			if (playerData != null) {
				if (playerData.status == ServerLOS.UpdateStatus.NeverDone) {
					playerData.status = ServerLOS.UpdateStatus.ReadyInMain;
				}

				if (playerData.status == ServerLOS.UpdateStatus.ReadyInMain) {
					playerData.status = ServerLOS.UpdateStatus.WaitingInLOS;
					this.noise("WaitingInLOS playerID=" + player.OnlineID);
					synchronized (this.thread.notifier) {
						this.thread.notifier.notify();
					}
				}
			}
		}
	}

	public void updateLOS(IsoPlayer player) {
		ServerLOS.PlayerData playerData = this.findData(player);
		if (playerData != null) {
			if (playerData.status == ServerLOS.UpdateStatus.ReadyInLOS || playerData.status == ServerLOS.UpdateStatus.ReadyInMain) {
				if (playerData.status == ServerLOS.UpdateStatus.ReadyInLOS) {
					this.noise("BusyInMain playerID=" + player.OnlineID);
				}

				playerData.status = ServerLOS.UpdateStatus.BusyInMain;
				player.updateLOS();
				playerData.status = ServerLOS.UpdateStatus.ReadyInMain;
				synchronized (this.thread.notifier) {
					this.thread.notifier.notify();
				}
			}
		}
	}

	private ServerLOS.PlayerData findData(IsoPlayer player) {
		for (int int1 = 0; int1 < this.playersMain.size(); ++int1) {
			if (((ServerLOS.PlayerData)this.playersMain.get(int1)).player == player) {
				return (ServerLOS.PlayerData)this.playersMain.get(int1);
			}
		}

		return null;
	}

	public void suspend() {
		this.bMapLoading = true;
		this.bWasSuspended = this.bSuspended;
		while (!this.bSuspended) {
			try {
				Thread.sleep(1L);
			} catch (InterruptedException interruptedException) {
			}
		}

		if (!this.bWasSuspended) {
			this.noise("suspend **********");
		}
	}

	public void resume() {
		this.bMapLoading = false;
		synchronized (this.thread.notifier) {
			this.thread.notifier.notify();
		}
		if (!this.bWasSuspended) {
			this.noise("resume **********");
		}
	}

	private class LOSThread extends Thread {
		public Object notifier = new Object();

		public void run() {
			while (true) {
				try {
					this.runInner();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}

		private void runInner() {
			MPStatistic.getInstance().ServerLOS.Start();
			synchronized (ServerLOS.this.playersMain) {
				ServerLOS.this.playersLOS.clear();
				ServerLOS.this.playersLOS.addAll(ServerLOS.this.playersMain);
			}
			for (int int1 = 0; int1 < ServerLOS.this.playersLOS.size(); ++int1) {
				ServerLOS.PlayerData playerData = (ServerLOS.PlayerData)ServerLOS.this.playersLOS.get(int1);
				if (playerData.status == ServerLOS.UpdateStatus.WaitingInLOS) {
					playerData.status = ServerLOS.UpdateStatus.BusyInLOS;
					ServerLOS.this.noise("BusyInLOS playerID=" + playerData.player.OnlineID);
					this.calcLOS(playerData);
					playerData.status = ServerLOS.UpdateStatus.ReadyInLOS;
				}

				if (ServerLOS.this.bMapLoading) {
					break;
				}
			}

			MPStatistic.getInstance().ServerLOS.End();
			while (this.shouldWait()) {
				ServerLOS.this.bSuspended = true;
				synchronized (this.notifier) {
					try {
						this.notifier.wait();
					} catch (InterruptedException interruptedException) {
					}
				}
			}

			ServerLOS.this.bSuspended = false;
		}

		private void calcLOS(ServerLOS.PlayerData playerData) {
			boolean boolean1 = false;
			if (playerData.px == (int)playerData.player.getX() && playerData.py == (int)playerData.player.getY() && playerData.pz == (int)playerData.player.getZ()) {
				boolean1 = true;
			}

			playerData.px = (int)playerData.player.getX();
			playerData.py = (int)playerData.player.getY();
			playerData.pz = (int)playerData.player.getZ();
			playerData.player.initLightInfo2();
			if (!boolean1) {
				byte byte1 = 0;
				int int1;
				int int2;
				int int3;
				for (int1 = 0; int1 < LosUtil.XSIZE; ++int1) {
					for (int2 = 0; int2 < LosUtil.YSIZE; ++int2) {
						for (int3 = 0; int3 < LosUtil.ZSIZE; ++int3) {
							LosUtil.cachedresults[int1][int2][int3][byte1] = 0;
						}
					}
				}

				try {
					IsoPlayer.players[byte1] = playerData.player;
					int1 = playerData.px;
					int2 = playerData.py;
					for (int3 = -50; int3 < 50; ++int3) {
						for (int int4 = -50; int4 < 50; ++int4) {
							for (int int5 = 0; int5 < 8; ++int5) {
								IsoGridSquare square = ServerMap.instance.getGridSquare(int3 + int1, int4 + int2, int5);
								if (square != null) {
									square.CalcVisibility(byte1);
									playerData.visible[int3 + 50][int4 + 50][int5] = square.isCouldSee(byte1);
								}
							}
						}
					}
				} finally {
					IsoPlayer.players[byte1] = null;
				}
			}
		}

		private boolean shouldWait() {
			if (ServerLOS.this.bMapLoading) {
				return true;
			} else {
				for (int int1 = 0; int1 < ServerLOS.this.playersLOS.size(); ++int1) {
					ServerLOS.PlayerData playerData = (ServerLOS.PlayerData)ServerLOS.this.playersLOS.get(int1);
					if (playerData.status == ServerLOS.UpdateStatus.WaitingInLOS) {
						return false;
					}
				}

				synchronized (ServerLOS.this.playersMain) {
					if (ServerLOS.this.playersLOS.size() != ServerLOS.this.playersMain.size()) {
						return false;
					} else {
						return true;
					}
				}
			}
		}
	}

	private class PlayerData {
		public IsoPlayer player;
		public ServerLOS.UpdateStatus status;
		public int px;
		public int py;
		public int pz;
		public boolean[][][] visible;

		public PlayerData(IsoPlayer player) {
			this.status = ServerLOS.UpdateStatus.NeverDone;
			this.visible = new boolean[100][100][8];
			this.player = player;
		}
	}

	static enum UpdateStatus {

		NeverDone,
		WaitingInLOS,
		BusyInLOS,
		ReadyInLOS,
		BusyInMain,
		ReadyInMain;

		private static ServerLOS.UpdateStatus[] $values() {
			return new ServerLOS.UpdateStatus[]{NeverDone, WaitingInLOS, BusyInLOS, ReadyInLOS, BusyInMain, ReadyInMain};
		}
	}

	public static final class ServerLighting implements IsoGridSquare.ILighting {
		private static final byte LOS_SEEN = 1;
		private static final byte LOS_COULD_SEE = 2;
		private static final byte LOS_CAN_SEE = 4;
		private static ColorInfo lightInfo = new ColorInfo();
		private byte los;

		public int lightverts(int int1) {
			return 0;
		}

		public float lampostTotalR() {
			return 0.0F;
		}

		public float lampostTotalG() {
			return 0.0F;
		}

		public float lampostTotalB() {
			return 0.0F;
		}

		public boolean bSeen() {
			return (this.los & 1) != 0;
		}

		public boolean bCanSee() {
			return (this.los & 4) != 0;
		}

		public boolean bCouldSee() {
			return (this.los & 2) != 0;
		}

		public float darkMulti() {
			return 0.0F;
		}

		public float targetDarkMulti() {
			return 0.0F;
		}

		public ColorInfo lightInfo() {
			lightInfo.r = 1.0F;
			lightInfo.g = 1.0F;
			lightInfo.b = 1.0F;
			return lightInfo;
		}

		public void lightverts(int int1, int int2) {
		}

		public void lampostTotalR(float float1) {
		}

		public void lampostTotalG(float float1) {
		}

		public void lampostTotalB(float float1) {
		}

		public void bSeen(boolean boolean1) {
			if (boolean1) {
				this.los = (byte)(this.los | 1);
			} else {
				this.los &= -2;
			}
		}

		public void bCanSee(boolean boolean1) {
			if (boolean1) {
				this.los = (byte)(this.los | 4);
			} else {
				this.los &= -5;
			}
		}

		public void bCouldSee(boolean boolean1) {
			if (boolean1) {
				this.los = (byte)(this.los | 2);
			} else {
				this.los &= -3;
			}
		}

		public void darkMulti(float float1) {
		}

		public void targetDarkMulti(float float1) {
		}

		public int resultLightCount() {
			return 0;
		}

		public IsoGridSquare.ResultLight getResultLight(int int1) {
			return null;
		}

		public void reset() {
			this.los = 0;
		}
	}
}
