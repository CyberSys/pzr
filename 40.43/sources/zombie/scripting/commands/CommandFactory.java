package zombie.scripting.commands;

import zombie.scripting.commands.Activatable.IsActivated;
import zombie.scripting.commands.Activatable.ToggleActivatable;
import zombie.scripting.commands.Character.ActualizeCommand;
import zombie.scripting.commands.Character.AddEnemy;
import zombie.scripting.commands.Character.AddInventory;
import zombie.scripting.commands.Character.AddToGroup;
import zombie.scripting.commands.Character.AimWhileStationary;
import zombie.scripting.commands.Character.AllowBehaviours;
import zombie.scripting.commands.Character.AllowConversation;
import zombie.scripting.commands.Character.Anger;
import zombie.scripting.commands.Character.Attack;
import zombie.scripting.commands.Character.Die;
import zombie.scripting.commands.Character.EquipItem;
import zombie.scripting.commands.Character.Exists;
import zombie.scripting.commands.Character.FaceCommand;
import zombie.scripting.commands.Character.HasInventory;
import zombie.scripting.commands.Character.HasTrait;
import zombie.scripting.commands.Character.InRange;
import zombie.scripting.commands.Character.IncrementCharacterScriptFlag;
import zombie.scripting.commands.Character.IsAggressive;
import zombie.scripting.commands.Character.IsAggressivePose;
import zombie.scripting.commands.Character.IsCharacterScriptFlagEqualTo;
import zombie.scripting.commands.Character.IsCharacterScriptFlagOver;
import zombie.scripting.commands.Character.IsDead;
import zombie.scripting.commands.Character.IsFriendly;
import zombie.scripting.commands.Character.IsInGroup;
import zombie.scripting.commands.Character.IsInGroupWith;
import zombie.scripting.commands.Character.IsInRoom;
import zombie.scripting.commands.Character.IsInside;
import zombie.scripting.commands.Character.IsLeaderOfGroup;
import zombie.scripting.commands.Character.IsNeutral;
import zombie.scripting.commands.Character.IsNumberOfEnemiesOver;
import zombie.scripting.commands.Character.IsNumberOfEnemiesUnder;
import zombie.scripting.commands.Character.IsNumberOfLocalOver;
import zombie.scripting.commands.Character.IsNumberOfLocalUnder;
import zombie.scripting.commands.Character.IsNumberOfNeutralOver;
import zombie.scripting.commands.Character.IsNumberOfNeutralUnder;
import zombie.scripting.commands.Character.IsOn;
import zombie.scripting.commands.Character.IsOnFloor;
import zombie.scripting.commands.Character.IsPlayer;
import zombie.scripting.commands.Character.IsSpeaking;
import zombie.scripting.commands.Character.MetCountIsOver;
import zombie.scripting.commands.Character.NamedOrder;
import zombie.scripting.commands.Character.Order;
import zombie.scripting.commands.Character.PopOrder;
import zombie.scripting.commands.Character.RemoveNamedOrder;
import zombie.scripting.commands.Character.SayAt;
import zombie.scripting.commands.Character.SayCommand;
import zombie.scripting.commands.Character.SayIdle;
import zombie.scripting.commands.Character.Sleep;
import zombie.scripting.commands.Character.StopAction;
import zombie.scripting.commands.Character.TestStat;
import zombie.scripting.commands.Character.TryToTeamUp;
import zombie.scripting.commands.Character.WalkCommand;
import zombie.scripting.commands.Character.WalkToLastHeardSound;
import zombie.scripting.commands.Character.WalkToLastKnownLocationOf;
import zombie.scripting.commands.Character.WalkWithinRangeOf;
import zombie.scripting.commands.DayNight.IsNight;
import zombie.scripting.commands.Flags.Decrement;
import zombie.scripting.commands.Flags.Increment;
import zombie.scripting.commands.Flags.IsFlagValue;
import zombie.scripting.commands.Flags.IsGreaterThan;
import zombie.scripting.commands.Flags.IsGreaterThanEqualTo;
import zombie.scripting.commands.Flags.IsLessThan;
import zombie.scripting.commands.Flags.IsLessThanEqualTo;
import zombie.scripting.commands.Flags.SetFlag;
import zombie.scripting.commands.Hook.RegisterOneTime;
import zombie.scripting.commands.Lua.LuaCall;
import zombie.scripting.commands.Module.Enabled;
import zombie.scripting.commands.Script.Call;
import zombie.scripting.commands.Script.CallAndWait;
import zombie.scripting.commands.Script.CharactersAlreadyInScript;
import zombie.scripting.commands.Script.IsPlaying;
import zombie.scripting.commands.Script.Pause;
import zombie.scripting.commands.Script.Resume;
import zombie.scripting.commands.Script.StopScript;
import zombie.scripting.commands.Trigger.IsLastFiredParameter;
import zombie.scripting.commands.Trigger.ProcessAlways;
import zombie.scripting.commands.Trigger.ProcessNever;
import zombie.scripting.commands.Trigger.TimeSinceLastRan;
import zombie.scripting.commands.Tutorial.AddHelpIconToUIElement;
import zombie.scripting.commands.Tutorial.AddHelpIconToWorld;
import zombie.scripting.commands.Tutorial.DisableTutorialZombieControl;
import zombie.scripting.commands.Tutorial.SetZombieLimit;
import zombie.scripting.commands.World.CreateZombieSwarm;
import zombie.scripting.commands.World.PlaySoundEffect;
import zombie.scripting.commands.World.PlayWorldSoundEffect;
import zombie.scripting.commands.World.SpawnZombie;
import zombie.scripting.commands.World.StartFire;
import zombie.scripting.commands.quest.AddEquipItemTask;
import zombie.scripting.commands.quest.AddFindItemTask;
import zombie.scripting.commands.quest.AddGotoLocationTask;
import zombie.scripting.commands.quest.AddHardCodedTask;
import zombie.scripting.commands.quest.AddScriptConditionTask;
import zombie.scripting.commands.quest.AddUseItemOnTask;
import zombie.scripting.commands.quest.CreateQuest;
import zombie.scripting.commands.quest.LockQuest;
import zombie.scripting.commands.quest.RunScriptOnComplete;
import zombie.scripting.commands.quest.UnlockLast;
import zombie.scripting.commands.quest.UnlockLastButHide;
import zombie.scripting.commands.quest.UnlockQuest;
import zombie.scripting.commands.quest.UnlockTaskOnComplete;
import zombie.scripting.commands.quest.UnlockTasksOnComplete;


public class CommandFactory {

	public static BaseCommand CreateCommand(String string) {
		if (string.equals("CreateQuest")) {
			return new CreateQuest();
		} else if (string.equals("SetModuleAlias")) {
			return new SetModuleAlias();
		} else if (string.equals("IsOn")) {
			return new IsOn();
		} else if (string.equals("SayAt")) {
			return new SayAt();
		} else if (string.equals("Exists")) {
			return new Exists();
		} else if (string.equals("TestStat")) {
			return new TestStat();
		} else if (string.equals("Enabled")) {
			return new Enabled();
		} else if (string.equals("LuaCall")) {
			return new LuaCall();
		} else if (string.equals("AddHelpIconToWorld")) {
			return new AddHelpIconToWorld();
		} else if (string.equals("AddHelpIconToUIElement")) {
			return new AddHelpIconToUIElement();
		} else if (string.equals("AimWhileStationary")) {
			return new AimWhileStationary();
		} else if (string.equals("HasTrait")) {
			return new HasTrait();
		} else if (string.equals("Die")) {
			return new Die();
		} else if (string.equals("LockQuest")) {
			return new LockQuest();
		} else if (string.equals("IsDead")) {
			return new IsDead();
		} else if (string.equals("StopAllScriptsExceptContaining")) {
			return new StopAllScriptsExceptContaining();
		} else if (string.equals("StopAllScriptsContaining")) {
			return new StopAllScriptsContaining();
		} else if (string.equals("IsInRoom")) {
			return new IsInRoom();
		} else if (string.equals("Attack")) {
			return new Attack();
		} else if (string.equals("InRange")) {
			return new InRange();
		} else if (string.equals("Increment")) {
			return new Increment();
		} else if (string.equals("Decrement")) {
			return new Decrement();
		} else if (string.equals("IsLessThan")) {
			return new IsLessThan();
		} else if (string.equals("IsLessThanEqualTo")) {
			return new IsLessThanEqualTo();
		} else if (string.equals("IsGreaterThan")) {
			return new IsGreaterThan();
		} else if (string.equals("IsGreaterThanEqualTo")) {
			return new IsGreaterThanEqualTo();
		} else if (string.equals("DisableTutorialZombieControl")) {
			return new DisableTutorialZombieControl();
		} else if (string.equals("IsAggressivePose")) {
			return new IsAggressivePose();
		} else if (string.equals("IsAggressive")) {
			return new IsAggressive();
		} else if (string.equals("IsNeutral")) {
			return new IsNeutral();
		} else if (string.equals("IsFriendly")) {
			return new IsFriendly();
		} else if (string.equals("Equip")) {
			return new EquipItem();
		} else if (string.equals("AllowConversation")) {
			return new AllowConversation();
		} else if (string.equals("IsNumberOfEnemiesUnder")) {
			return new IsNumberOfEnemiesUnder();
		} else if (string.equals("IsNumberOfEnemiesOver")) {
			return new IsNumberOfEnemiesOver();
		} else if (string.equals("IsPlayer")) {
			return new IsPlayer();
		} else if (string.equals("Order")) {
			return new Order();
		} else if (string.equals("CharactersAlreadyInScript")) {
			return new CharactersAlreadyInScript();
		} else if (string.equals("PopOrder")) {
			return new PopOrder();
		} else if (string.equals("IsLastFiredParameter")) {
			return new IsLastFiredParameter();
		} else if (string.equals("PlaySoundEffect")) {
			return new PlaySoundEffect();
		} else if (string.equals("StopScript")) {
			return new StopScript();
		} else if (string.equals("WalkWithinRangeOf")) {
			return new WalkWithinRangeOf();
		} else if (string.equals("IsNumberOfLocalUnder")) {
			return new IsNumberOfLocalUnder();
		} else if (string.equals("IsNumberOfLocalOver")) {
			return new IsNumberOfLocalOver();
		} else if (string.equals("IsCharacterScriptFlagOver")) {
			return new IsCharacterScriptFlagOver();
		} else if (string.equals("IsCharacterScriptFlagEqualTo")) {
			return new IsCharacterScriptFlagEqualTo();
		} else if (string.equals("IncrementCharacterScriptFlag")) {
			return new IncrementCharacterScriptFlag();
		} else if (string.equals("MetCountIsOver")) {
			return new MetCountIsOver();
		} else if (string.equals("Anger")) {
			return new Anger();
		} else if (string.equals("NamedOrder")) {
			return new NamedOrder();
		} else if (string.equals("RemoveNamedOrder")) {
			return new RemoveNamedOrder();
		} else if (string.equals("IsNumberOfNeutralUnder")) {
			return new IsNumberOfNeutralUnder();
		} else if (string.equals("IsNumberOfNeutralOver")) {
			return new IsNumberOfNeutralOver();
		} else if (string.equals("IsLeaderOfGroup")) {
			return new IsLeaderOfGroup();
		} else if (string.equals("Call")) {
			return new Call();
		} else if (string.equals("AllowBehaviours")) {
			return new AllowBehaviours();
		} else if (string.equals("CallWait")) {
			return new CallAndWait();
		} else if (string.equals("WalkToLastHeardSound")) {
			return new WalkToLastHeardSound();
		} else if (string.equals("PlayWorldSound")) {
			return new PlayWorldSoundEffect();
		} else if (string.equals("IsInside")) {
			return new IsInside();
		} else if (string.equals("CreateZombieSwarm")) {
			return new CreateZombieSwarm();
		} else if (string.equals("StartFire")) {
			return new StartFire();
		} else if (string.equals("TimeSinceLastRan")) {
			return new TimeSinceLastRan();
		} else if (string.equals("UnlockButHide")) {
			return new UnlockLastButHide();
		} else if (string.equals("IsPlaying")) {
			return new IsPlaying();
		} else if (string.equals("IsOnFloor")) {
			return new IsOnFloor();
		} else if (string.equals("StopAction")) {
			return new StopAction();
		} else if (string.equals("StopAllScriptsExcept")) {
			return new StopAllScriptsExcept();
		} else if (string.equals("WalkToLastKnownLocationOf")) {
			return new WalkToLastKnownLocationOf();
		} else if (string.equals("PauseAllScriptsExcept")) {
			return new PauseAllScriptsExcept();
		} else if (string.equals("ResumeAllScriptsExcept")) {
			return new ResumeAllScriptsExcept();
		} else if (string.equals("AddInventory")) {
			return new AddInventory();
		} else if (string.equals("IsActivated")) {
			return new IsActivated();
		} else if (string.equals("Toggle")) {
			return new ToggleActivatable();
		} else if (string.equals("SetZombieLimit")) {
			return new SetZombieLimit();
		} else if (string.equals("IsSpeaking")) {
			return new IsSpeaking();
		} else if (string.equals("RegisterOneTime")) {
			return new RegisterOneTime();
		} else if (string.equals("Sleep")) {
			return new Sleep();
		} else if (string.equals("SpawnZombie")) {
			return new SpawnZombie();
		} else if (string.equals("HasInventory")) {
			return new HasInventory();
		} else if (string.equals("AddUseItemOnTask")) {
			return new AddUseItemOnTask();
		} else if (string.equals("ProcessNever")) {
			return new ProcessNever();
		} else if (string.equals("ProcessAlways")) {
			return new ProcessAlways();
		} else if (string.equals("IsNight")) {
			return new IsNight();
		} else if (string.equals("Is")) {
			return new IsFlagValue();
		} else if (string.equals("Set")) {
			return new SetFlag();
		} else if (string.equals("Wait")) {
			return new WaitCommand();
		} else if (string.equals("AddGotoLocationTask")) {
			return new AddGotoLocationTask();
		} else if (string.equals("AddHardCodedTask")) {
			return new AddHardCodedTask();
		} else if (string.equals("AddScriptConditionTask")) {
			return new AddScriptConditionTask();
		} else if (string.equals("RunScriptOnComplete")) {
			return new RunScriptOnComplete();
		} else if (string.equals("AddFindItemTask")) {
			return new AddFindItemTask();
		} else if (string.equals("AddEquipItemTask")) {
			return new AddEquipItemTask();
		} else if (string.equals("UnlockTaskOnComplete")) {
			return new UnlockTaskOnComplete();
		} else if (string.equals("UnlockNextTasksOnComplete")) {
			return new UnlockTasksOnComplete();
		} else if (string.equals("Unlock")) {
			return new UnlockLast();
		} else if (string.equals("Walk")) {
			return new WalkCommand();
		} else if (string.equals("UnlockQuest")) {
			return new UnlockQuest();
		} else if (string.equals("LockHud")) {
			return new LockHud();
		} else if (string.equals("LoadTexturePage")) {
			return new LoadTexturePage();
		} else if (string.equals("Actualize")) {
			return new ActualizeCommand();
		} else if (string.equals("Face")) {
			return new FaceCommand();
		} else if (string.equals("Say")) {
			return new SayCommand();
		} else if (string.equals("Pause")) {
			return new Pause();
		} else if (string.equals("Resume")) {
			return new Resume();
		} else if (string.equals("SayIdle")) {
			return new SayIdle();
		} else if (string.equals("AddEnemy")) {
			return new AddEnemy();
		} else if (string.equals("AddToGroup")) {
			return new AddToGroup();
		} else if (string.equals("IsInGroup")) {
			return new IsInGroup();
		} else if (string.equals("IsInGroupWith")) {
			return new IsInGroupWith();
		} else {
			return string.equals("TryToTeamUp") ? new TryToTeamUp() : null;
		}
	}
}
