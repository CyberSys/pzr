package zombie.core.skinnedmodel;

import java.util.ArrayList;
import zombie.iso.IsoDirections;


public final class CharacterTextures {
	final ArrayList m_animSets = new ArrayList();

	CharacterTextures.CTAnimSet getAnimSet(String string) {
		for (int int1 = 0; int1 < this.m_animSets.size(); ++int1) {
			CharacterTextures.CTAnimSet cTAnimSet = (CharacterTextures.CTAnimSet)this.m_animSets.get(int1);
			if (cTAnimSet.m_name.equals(string)) {
				return cTAnimSet;
			}
		}

		return null;
	}

	DeadBodyAtlas.BodyTexture getTexture(String string, String string2, IsoDirections directions, int int1) {
		CharacterTextures.CTAnimSet cTAnimSet = this.getAnimSet(string);
		if (cTAnimSet == null) {
			return null;
		} else {
			CharacterTextures.CTState cTState = cTAnimSet.getState(string2);
			if (cTState == null) {
				return null;
			} else {
				CharacterTextures.CTEntry cTEntry = cTState.getEntry(directions, int1);
				return cTEntry == null ? null : cTEntry.m_texture;
			}
		}
	}

	void addTexture(String string, String string2, IsoDirections directions, int int1, DeadBodyAtlas.BodyTexture bodyTexture) {
		CharacterTextures.CTAnimSet cTAnimSet = this.getAnimSet(string);
		if (cTAnimSet == null) {
			cTAnimSet = new CharacterTextures.CTAnimSet();
			cTAnimSet.m_name = string;
			this.m_animSets.add(cTAnimSet);
		}

		cTAnimSet.addEntry(string2, directions, int1, bodyTexture);
	}

	void clear() {
		this.m_animSets.clear();
	}

	private static final class CTAnimSet {
		String m_name;
		final ArrayList m_states = new ArrayList();

		CharacterTextures.CTState getState(String string) {
			for (int int1 = 0; int1 < this.m_states.size(); ++int1) {
				CharacterTextures.CTState cTState = (CharacterTextures.CTState)this.m_states.get(int1);
				if (cTState.m_name.equals(string)) {
					return cTState;
				}
			}

			return null;
		}

		void addEntry(String string, IsoDirections directions, int int1, DeadBodyAtlas.BodyTexture bodyTexture) {
			CharacterTextures.CTState cTState = this.getState(string);
			if (cTState == null) {
				cTState = new CharacterTextures.CTState();
				cTState.m_name = string;
				this.m_states.add(cTState);
			}

			cTState.addEntry(directions, int1, bodyTexture);
		}
	}

	private static final class CTState {
		String m_name;
		final CharacterTextures.CTEntryList[] m_entries = new CharacterTextures.CTEntryList[IsoDirections.values().length];

		CTState() {
			for (int int1 = 0; int1 < this.m_entries.length; ++int1) {
				this.m_entries[int1] = new CharacterTextures.CTEntryList();
			}
		}

		CharacterTextures.CTEntry getEntry(IsoDirections directions, int int1) {
			CharacterTextures.CTEntryList cTEntryList = this.m_entries[directions.index()];
			for (int int2 = 0; int2 < cTEntryList.size(); ++int2) {
				CharacterTextures.CTEntry cTEntry = (CharacterTextures.CTEntry)cTEntryList.get(int2);
				if (cTEntry.m_frame == int1) {
					return cTEntry;
				}
			}

			return null;
		}

		void addEntry(IsoDirections directions, int int1, DeadBodyAtlas.BodyTexture bodyTexture) {
			CharacterTextures.CTEntryList cTEntryList = this.m_entries[directions.index()];
			CharacterTextures.CTEntry cTEntry = new CharacterTextures.CTEntry();
			cTEntry.m_frame = int1;
			cTEntry.m_texture = bodyTexture;
			cTEntryList.add(cTEntry);
		}
	}

	private static final class CTEntry {
		int m_frame;
		DeadBodyAtlas.BodyTexture m_texture;
	}

	private static final class CTEntryList extends ArrayList {
	}
}
