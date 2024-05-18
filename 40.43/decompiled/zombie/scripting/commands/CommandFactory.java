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
   public static BaseCommand CreateCommand(String var0) {
      if (var0.equals("CreateQuest")) {
         return new CreateQuest();
      } else if (var0.equals("SetModuleAlias")) {
         return new SetModuleAlias();
      } else if (var0.equals("IsOn")) {
         return new IsOn();
      } else if (var0.equals("SayAt")) {
         return new SayAt();
      } else if (var0.equals("Exists")) {
         return new Exists();
      } else if (var0.equals("TestStat")) {
         return new TestStat();
      } else if (var0.equals("Enabled")) {
         return new Enabled();
      } else if (var0.equals("LuaCall")) {
         return new LuaCall();
      } else if (var0.equals("AddHelpIconToWorld")) {
         return new AddHelpIconToWorld();
      } else if (var0.equals("AddHelpIconToUIElement")) {
         return new AddHelpIconToUIElement();
      } else if (var0.equals("AimWhileStationary")) {
         return new AimWhileStationary();
      } else if (var0.equals("HasTrait")) {
         return new HasTrait();
      } else if (var0.equals("Die")) {
         return new Die();
      } else if (var0.equals("LockQuest")) {
         return new LockQuest();
      } else if (var0.equals("IsDead")) {
         return new IsDead();
      } else if (var0.equals("StopAllScriptsExceptContaining")) {
         return new StopAllScriptsExceptContaining();
      } else if (var0.equals("StopAllScriptsContaining")) {
         return new StopAllScriptsContaining();
      } else if (var0.equals("IsInRoom")) {
         return new IsInRoom();
      } else if (var0.equals("Attack")) {
         return new Attack();
      } else if (var0.equals("InRange")) {
         return new InRange();
      } else if (var0.equals("Increment")) {
         return new Increment();
      } else if (var0.equals("Decrement")) {
         return new Decrement();
      } else if (var0.equals("IsLessThan")) {
         return new IsLessThan();
      } else if (var0.equals("IsLessThanEqualTo")) {
         return new IsLessThanEqualTo();
      } else if (var0.equals("IsGreaterThan")) {
         return new IsGreaterThan();
      } else if (var0.equals("IsGreaterThanEqualTo")) {
         return new IsGreaterThanEqualTo();
      } else if (var0.equals("DisableTutorialZombieControl")) {
         return new DisableTutorialZombieControl();
      } else if (var0.equals("IsAggressivePose")) {
         return new IsAggressivePose();
      } else if (var0.equals("IsAggressive")) {
         return new IsAggressive();
      } else if (var0.equals("IsNeutral")) {
         return new IsNeutral();
      } else if (var0.equals("IsFriendly")) {
         return new IsFriendly();
      } else if (var0.equals("Equip")) {
         return new EquipItem();
      } else if (var0.equals("AllowConversation")) {
         return new AllowConversation();
      } else if (var0.equals("IsNumberOfEnemiesUnder")) {
         return new IsNumberOfEnemiesUnder();
      } else if (var0.equals("IsNumberOfEnemiesOver")) {
         return new IsNumberOfEnemiesOver();
      } else if (var0.equals("IsPlayer")) {
         return new IsPlayer();
      } else if (var0.equals("Order")) {
         return new Order();
      } else if (var0.equals("CharactersAlreadyInScript")) {
         return new CharactersAlreadyInScript();
      } else if (var0.equals("PopOrder")) {
         return new PopOrder();
      } else if (var0.equals("IsLastFiredParameter")) {
         return new IsLastFiredParameter();
      } else if (var0.equals("PlaySoundEffect")) {
         return new PlaySoundEffect();
      } else if (var0.equals("StopScript")) {
         return new StopScript();
      } else if (var0.equals("WalkWithinRangeOf")) {
         return new WalkWithinRangeOf();
      } else if (var0.equals("IsNumberOfLocalUnder")) {
         return new IsNumberOfLocalUnder();
      } else if (var0.equals("IsNumberOfLocalOver")) {
         return new IsNumberOfLocalOver();
      } else if (var0.equals("IsCharacterScriptFlagOver")) {
         return new IsCharacterScriptFlagOver();
      } else if (var0.equals("IsCharacterScriptFlagEqualTo")) {
         return new IsCharacterScriptFlagEqualTo();
      } else if (var0.equals("IncrementCharacterScriptFlag")) {
         return new IncrementCharacterScriptFlag();
      } else if (var0.equals("MetCountIsOver")) {
         return new MetCountIsOver();
      } else if (var0.equals("Anger")) {
         return new Anger();
      } else if (var0.equals("NamedOrder")) {
         return new NamedOrder();
      } else if (var0.equals("RemoveNamedOrder")) {
         return new RemoveNamedOrder();
      } else if (var0.equals("IsNumberOfNeutralUnder")) {
         return new IsNumberOfNeutralUnder();
      } else if (var0.equals("IsNumberOfNeutralOver")) {
         return new IsNumberOfNeutralOver();
      } else if (var0.equals("IsLeaderOfGroup")) {
         return new IsLeaderOfGroup();
      } else if (var0.equals("Call")) {
         return new Call();
      } else if (var0.equals("AllowBehaviours")) {
         return new AllowBehaviours();
      } else if (var0.equals("CallWait")) {
         return new CallAndWait();
      } else if (var0.equals("WalkToLastHeardSound")) {
         return new WalkToLastHeardSound();
      } else if (var0.equals("PlayWorldSound")) {
         return new PlayWorldSoundEffect();
      } else if (var0.equals("IsInside")) {
         return new IsInside();
      } else if (var0.equals("CreateZombieSwarm")) {
         return new CreateZombieSwarm();
      } else if (var0.equals("StartFire")) {
         return new StartFire();
      } else if (var0.equals("TimeSinceLastRan")) {
         return new TimeSinceLastRan();
      } else if (var0.equals("UnlockButHide")) {
         return new UnlockLastButHide();
      } else if (var0.equals("IsPlaying")) {
         return new IsPlaying();
      } else if (var0.equals("IsOnFloor")) {
         return new IsOnFloor();
      } else if (var0.equals("StopAction")) {
         return new StopAction();
      } else if (var0.equals("StopAllScriptsExcept")) {
         return new StopAllScriptsExcept();
      } else if (var0.equals("WalkToLastKnownLocationOf")) {
         return new WalkToLastKnownLocationOf();
      } else if (var0.equals("PauseAllScriptsExcept")) {
         return new PauseAllScriptsExcept();
      } else if (var0.equals("ResumeAllScriptsExcept")) {
         return new ResumeAllScriptsExcept();
      } else if (var0.equals("AddInventory")) {
         return new AddInventory();
      } else if (var0.equals("IsActivated")) {
         return new IsActivated();
      } else if (var0.equals("Toggle")) {
         return new ToggleActivatable();
      } else if (var0.equals("SetZombieLimit")) {
         return new SetZombieLimit();
      } else if (var0.equals("IsSpeaking")) {
         return new IsSpeaking();
      } else if (var0.equals("RegisterOneTime")) {
         return new RegisterOneTime();
      } else if (var0.equals("Sleep")) {
         return new Sleep();
      } else if (var0.equals("SpawnZombie")) {
         return new SpawnZombie();
      } else if (var0.equals("HasInventory")) {
         return new HasInventory();
      } else if (var0.equals("AddUseItemOnTask")) {
         return new AddUseItemOnTask();
      } else if (var0.equals("ProcessNever")) {
         return new ProcessNever();
      } else if (var0.equals("ProcessAlways")) {
         return new ProcessAlways();
      } else if (var0.equals("IsNight")) {
         return new IsNight();
      } else if (var0.equals("Is")) {
         return new IsFlagValue();
      } else if (var0.equals("Set")) {
         return new SetFlag();
      } else if (var0.equals("Wait")) {
         return new WaitCommand();
      } else if (var0.equals("AddGotoLocationTask")) {
         return new AddGotoLocationTask();
      } else if (var0.equals("AddHardCodedTask")) {
         return new AddHardCodedTask();
      } else if (var0.equals("AddScriptConditionTask")) {
         return new AddScriptConditionTask();
      } else if (var0.equals("RunScriptOnComplete")) {
         return new RunScriptOnComplete();
      } else if (var0.equals("AddFindItemTask")) {
         return new AddFindItemTask();
      } else if (var0.equals("AddEquipItemTask")) {
         return new AddEquipItemTask();
      } else if (var0.equals("UnlockTaskOnComplete")) {
         return new UnlockTaskOnComplete();
      } else if (var0.equals("UnlockNextTasksOnComplete")) {
         return new UnlockTasksOnComplete();
      } else if (var0.equals("Unlock")) {
         return new UnlockLast();
      } else if (var0.equals("Walk")) {
         return new WalkCommand();
      } else if (var0.equals("UnlockQuest")) {
         return new UnlockQuest();
      } else if (var0.equals("LockHud")) {
         return new LockHud();
      } else if (var0.equals("LoadTexturePage")) {
         return new LoadTexturePage();
      } else if (var0.equals("Actualize")) {
         return new ActualizeCommand();
      } else if (var0.equals("Face")) {
         return new FaceCommand();
      } else if (var0.equals("Say")) {
         return new SayCommand();
      } else if (var0.equals("Pause")) {
         return new Pause();
      } else if (var0.equals("Resume")) {
         return new Resume();
      } else if (var0.equals("SayIdle")) {
         return new SayIdle();
      } else if (var0.equals("AddEnemy")) {
         return new AddEnemy();
      } else if (var0.equals("AddToGroup")) {
         return new AddToGroup();
      } else if (var0.equals("IsInGroup")) {
         return new IsInGroup();
      } else if (var0.equals("IsInGroupWith")) {
         return new IsInGroupWith();
      } else {
         return var0.equals("TryToTeamUp") ? new TryToTeamUp() : null;
      }
   }
}
