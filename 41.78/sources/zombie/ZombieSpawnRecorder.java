package zombie;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.iso.IsoGridSquare;
import zombie.iso.areas.IsoRoom;


public final class ZombieSpawnRecorder {
	public static final ZombieSpawnRecorder instance = new ZombieSpawnRecorder();
	public ZLogger m_logger;
	private final StringBuilder m_stringBuilder = new StringBuilder();

	public void init() {
		if (this.m_logger != null) {
			this.m_logger.write("================================================================================");
		} else {
			LoggerManager.init();
			LoggerManager.createLogger("ZombieSpawn", Core.bDebug);
			this.m_logger = LoggerManager.getLogger("ZombieSpawn");
		}
	}

	public void quit() {
		if (this.m_logger != null) {
			if (this.m_stringBuilder.length() > 0) {
				this.m_logger.write(this.m_stringBuilder.toString());
				this.m_stringBuilder.setLength(0);
			}
		}
	}

	public void record(IsoZombie zombie, String string) {
		if (zombie != null && zombie.getCurrentSquare() != null) {
			if (this.m_logger != null) {
				IsoGridSquare square = zombie.getCurrentSquare();
				this.m_stringBuilder.append("reason = ");
				this.m_stringBuilder.append(string);
				this.m_stringBuilder.append(" x,y,z = ");
				this.m_stringBuilder.append(square.x);
				this.m_stringBuilder.append(',');
				this.m_stringBuilder.append(square.y);
				this.m_stringBuilder.append(',');
				this.m_stringBuilder.append(square.z);
				IsoRoom room = square.getRoom();
				if (room != null && room.def != null) {
					this.m_stringBuilder.append(" room = ");
					this.m_stringBuilder.append(room.def.name);
				}

				this.m_stringBuilder.append(System.lineSeparator());
				if (this.m_stringBuilder.length() >= 1024) {
					this.m_logger.write(this.m_stringBuilder.toString());
					this.m_stringBuilder.setLength(0);
				}
			}
		}
	}

	public void record(ArrayList arrayList, String string) {
		if (arrayList != null) {
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				this.record((IsoZombie)arrayList.get(int1), string);
			}
		}
	}
}
