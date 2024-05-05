package zombie.popman;

import java.util.Iterator;
import java.util.LinkedList;
import zombie.core.raknet.UdpConnection;


public class NetworkZombieList {
	final LinkedList networkZombies = new LinkedList();
	public Object lock = new Object();

	public NetworkZombieList.NetworkZombie getNetworkZombie(UdpConnection udpConnection) {
		if (udpConnection == null) {
			return null;
		} else {
			Iterator iterator = this.networkZombies.iterator();
			NetworkZombieList.NetworkZombie networkZombie;
			do {
				if (!iterator.hasNext()) {
					NetworkZombieList.NetworkZombie networkZombie2 = new NetworkZombieList.NetworkZombie(udpConnection);
					this.networkZombies.add(networkZombie2);
					return networkZombie2;
				}

				networkZombie = (NetworkZombieList.NetworkZombie)iterator.next();
			}	 while (networkZombie.connection != udpConnection);

			return networkZombie;
		}
	}

	public static class NetworkZombie {
		final LinkedList zombies = new LinkedList();
		final UdpConnection connection;

		public NetworkZombie(UdpConnection udpConnection) {
			this.connection = udpConnection;
		}
	}
}
