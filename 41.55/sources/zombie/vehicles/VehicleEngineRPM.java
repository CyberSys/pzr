package zombie.vehicles;

import java.util.Iterator;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;
import zombie.scripting.objects.BaseScriptObject;


public class VehicleEngineRPM extends BaseScriptObject {
	public static final int MAX_GEARS = 8;
	private static final int VERSION1 = 1;
	private static final int VERSION = 1;
	private String m_name;
	public final EngineRPMData[] m_rpmData = new EngineRPMData[8];

	public String getName() {
		return this.m_name;
	}

	public void Load(String string, String string2) throws RuntimeException {
		this.m_name = string;
		int int1 = -1;
		ScriptParser.Block block = ScriptParser.parse(string2);
		block = (ScriptParser.Block)block.children.get(0);
		Iterator iterator = block.values.iterator();
		String string3;
		do {
			String string4;
			do {
				if (!iterator.hasNext()) {
					if (int1 == -1) {
						throw new RuntimeException(String.format("unknown vehicleEngineRPM VERSION \"%s\"", block.type));
					}

					int int2 = 0;
					for (Iterator iterator2 = block.children.iterator(); iterator2.hasNext(); ++int2) {
						ScriptParser.Block block2 = (ScriptParser.Block)iterator2.next();
						if (!"data".equals(block2.type)) {
							throw new RuntimeException(String.format("unknown block vehicleEngineRPM.%s", block2.type));
						}

						if (int2 == 8) {
							throw new RuntimeException(String.format("too many vehicleEngineRPM.data blocks, max is %d", 8));
						}

						this.m_rpmData[int2] = new EngineRPMData();
						this.LoadData(block2, this.m_rpmData[int2]);
					}

					return;
				}

				ScriptParser.Value value = (ScriptParser.Value)iterator.next();
				string4 = value.getKey().trim();
				string3 = value.getValue().trim();
			}	 while (!"VERSION".equals(string4));

			int1 = PZMath.tryParseInt(string3, -1);
		} while (int1 >= 0 && int1 <= 1);

		throw new RuntimeException(String.format("unknown vehicleEngineRPM VERSION \"%s\"", string3));
	}

	private void LoadData(ScriptParser.Block block, EngineRPMData engineRPMData) {
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string = value.getKey().trim();
			String string2 = value.getValue().trim();
			if ("afterGearChange".equals(string)) {
				engineRPMData.afterGearChange = PZMath.tryParseFloat(string2, 0.0F);
			} else {
				if (!"gearChange".equals(string)) {
					throw new RuntimeException(String.format("unknown value vehicleEngineRPM.data.%s", value.string));
				}

				engineRPMData.gearChange = PZMath.tryParseFloat(string2, 0.0F);
			}
		}

		iterator = block.children.iterator();
		ScriptParser.Block block2;
		do {
			if (!iterator.hasNext()) {
				return;
			}

			block2 = (ScriptParser.Block)iterator.next();
		} while ("xxx".equals(block2.type));

		throw new RuntimeException(String.format("unknown block vehicleEngineRPM.data.%s", block2.type));
	}

	public void reset() {
		for (int int1 = 0; int1 < this.m_rpmData.length; ++int1) {
			if (this.m_rpmData[int1] != null) {
				this.m_rpmData[int1].reset();
			}
		}
	}
}
