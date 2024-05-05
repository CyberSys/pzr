package zombie.characters.WornItems;

import java.util.ArrayList;


public final class BodyLocations {
	protected static final ArrayList groups = new ArrayList();

	public static BodyLocationGroup getGroup(String string) {
		for (int int1 = 0; int1 < groups.size(); ++int1) {
			BodyLocationGroup bodyLocationGroup = (BodyLocationGroup)groups.get(int1);
			if (bodyLocationGroup.id.equals(string)) {
				return bodyLocationGroup;
			}
		}

		BodyLocationGroup bodyLocationGroup2 = new BodyLocationGroup(string);
		groups.add(bodyLocationGroup2);
		return bodyLocationGroup2;
	}

	public static void Reset() {
		groups.clear();
	}
}
