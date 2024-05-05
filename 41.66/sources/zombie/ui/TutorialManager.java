package zombie.ui;

import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoStove;
import zombie.network.GameServer;


public final class TutorialManager {
	public static boolean Debug = false;
	public boolean Active = false;
	public boolean ActiveControlZombies = false;
	public float TargetZombies = 0.0F;
	public TutorialManager.Stage stage;
	public IsoSurvivor wife;
	private IsoZombie zombie;
	public IsoStove tutorialStove;
	public IsoBuilding tutBuilding;
	public boolean DoorsLocked;
	public int BarricadeCount;
	public String PrefMusic;
	public IsoSurvivor gunnut;
	public boolean StealControl;
	public int AlarmTime;
	public boolean ProfanityFilter;
	public int Timer;
	public int AlarmTickTime;
	public boolean DoneFirstSleep;
	public boolean wifeKilledByEarl;
	public boolean warnedHammer;
	public boolean TriggerFire;
	public boolean CanDragWife;
	public boolean AllowSleep;
	public boolean skipped;
	private boolean bDoneDeath;
	boolean bDoGunnutDeadTalk;
	public String millingTune;
	IsoRadio radio;
	public static TutorialManager instance = new TutorialManager();

	public TutorialManager() {
		this.stage = TutorialManager.Stage.getBelt;
		this.wife = null;
		this.DoorsLocked = true;
		this.BarricadeCount = 0;
		this.PrefMusic = null;
		this.StealControl = false;
		this.AlarmTime = 0;
		this.ProfanityFilter = false;
		this.Timer = 0;
		this.AlarmTickTime = 160;
		this.DoneFirstSleep = false;
		this.wifeKilledByEarl = false;
		this.warnedHammer = false;
		this.TriggerFire = false;
		this.CanDragWife = false;
		this.AllowSleep = false;
		this.skipped = false;
		this.bDoneDeath = false;
		this.bDoGunnutDeadTalk = true;
		this.millingTune = "tune1.ogg";
		this.radio = null;
	}

	public boolean AllowUse(IsoObject object) {
		return true;
	}

	public void CheckWake() {
	}

	public void CreateQuests() {
		try {
			for (int int1 = 0; int1 < IsoWorld.instance.CurrentCell.getStaticUpdaterObjectList().size(); ++int1) {
				IsoObject object = (IsoObject)IsoWorld.instance.CurrentCell.getStaticUpdaterObjectList().get(int1);
				if (object instanceof IsoRadio) {
					this.radio = (IsoRadio)object;
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			this.radio = null;
		}
	}

	public void init() {
		if (!GameServer.bServer) {
			if (this.Active) {
				;
			}
		}
	}

	public void update() {
	}

	private void ForceKillZombies() {
		IsoWorld.instance.ForceKillAllZombies();
	}

	public static enum Stage {

		getBelt,
		RipSheet,
		Apply,
		FindShed,
		getShedItems,
		EquipHammer,
		BoardUpHouse,
		FindFood,
		InHouseFood,
		KillZombie,
		StockUp,
		ExploreHouse,
		BreakBarricade,
		getSoupIngredients,
		MakeSoupPot,
		LightStove,
		Distraction,
		InvestigateSound,
		Alarm,
		Mouseover,
		Escape,
		ShouldBeOk;

		private static TutorialManager.Stage[] $values() {
			return new TutorialManager.Stage[]{getBelt, RipSheet, Apply, FindShed, getShedItems, EquipHammer, BoardUpHouse, FindFood, InHouseFood, KillZombie, StockUp, ExploreHouse, BreakBarricade, getSoupIngredients, MakeSoupPot, LightStove, Distraction, InvestigateSound, Alarm, Mouseover, Escape, ShouldBeOk};
		}
	}
}
