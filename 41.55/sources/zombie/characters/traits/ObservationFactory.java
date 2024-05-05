package zombie.characters.traits;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.interfaces.IListBoxItem;


public final class ObservationFactory {
	public static HashMap ObservationMap = new HashMap();

	public static void init() {
	}

	public static void setMutualExclusive(String string, String string2) {
		((ObservationFactory.Observation)ObservationMap.get(string)).MutuallyExclusive.add(string2);
		((ObservationFactory.Observation)ObservationMap.get(string2)).MutuallyExclusive.add(string);
	}

	public static void addObservation(String string, String string2, String string3) {
		ObservationMap.put(string, new ObservationFactory.Observation(string, string2, string3));
	}

	public static ObservationFactory.Observation getObservation(String string) {
		return ObservationMap.containsKey(string) ? (ObservationFactory.Observation)ObservationMap.get(string) : null;
	}

	public static class Observation implements IListBoxItem {
		private String traitID;
		private String name;
		private String description;
		public ArrayList MutuallyExclusive = new ArrayList(0);

		public Observation(String string, String string2, String string3) {
			this.setTraitID(string);
			this.setName(string2);
			this.setDescription(string3);
		}

		public String getLabel() {
			return this.getName();
		}

		public String getLeftLabel() {
			return this.getName();
		}

		public String getRightLabel() {
			return null;
		}

		public String getDescription() {
			return this.description;
		}

		public void setDescription(String string) {
			this.description = string;
		}

		public String getTraitID() {
			return this.traitID;
		}

		public void setTraitID(String string) {
			this.traitID = string;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String string) {
			this.name = string;
		}
	}
}
