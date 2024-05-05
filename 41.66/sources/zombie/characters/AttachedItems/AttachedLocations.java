package zombie.characters.AttachedItems;

import java.util.ArrayList;


public final class AttachedLocations {
	protected static final ArrayList groups = new ArrayList();

	public static AttachedLocationGroup getGroup(String string) {
		for (int int1 = 0; int1 < groups.size(); ++int1) {
			AttachedLocationGroup attachedLocationGroup = (AttachedLocationGroup)groups.get(int1);
			if (attachedLocationGroup.id.equals(string)) {
				return attachedLocationGroup;
			}
		}

		AttachedLocationGroup attachedLocationGroup2 = new AttachedLocationGroup(string);
		groups.add(attachedLocationGroup2);
		return attachedLocationGroup2;
	}

	public static void Reset() {
		groups.clear();
	}
}
