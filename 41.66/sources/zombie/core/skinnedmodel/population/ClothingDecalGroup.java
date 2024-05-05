package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;


public class ClothingDecalGroup {
	@XmlElement(name = "name")
	public String m_Name;
	@XmlElement(name = "decal")
	public final ArrayList m_Decals = new ArrayList();
	@XmlElement(name = "group")
	public final ArrayList m_Groups = new ArrayList();
	private final ArrayList tempDecals = new ArrayList();

	public String getRandomDecal() {
		this.tempDecals.clear();
		this.getDecals(this.tempDecals);
		String string = (String)OutfitRNG.pickRandom(this.tempDecals);
		return string == null ? null : string;
	}

	public void getDecals(ArrayList arrayList) {
		arrayList.addAll(this.m_Decals);
		for (int int1 = 0; int1 < this.m_Groups.size(); ++int1) {
			ClothingDecalGroup clothingDecalGroup = ClothingDecals.instance.FindGroup((String)this.m_Groups.get(int1));
			if (clothingDecalGroup != null) {
				clothingDecalGroup.getDecals(arrayList);
			}
		}
	}
}
