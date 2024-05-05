package zombie.characters;

import java.util.List;
import java.util.Stack;
import zombie.ai.State;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.characters.Moodles.Moodles;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.TraitCollection;
import zombie.core.skinnedmodel.advancedanimation.debug.AnimatorDebugMonitor;
import zombie.core.skinnedmodel.visual.BaseVisual;
import zombie.core.textures.ColorInfo;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Literature;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.scripting.objects.Recipe;
import zombie.ui.UIFont;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehiclePart;


public interface ILuaGameCharacter extends ILuaVariableSource,ILuaGameCharacterAttachedItems,ILuaGameCharacterDamage,ILuaGameCharacterClothing,ILuaGameCharacterHealth {

	String getFullName();

	SurvivorDesc getDescriptor();

	void setDescriptor(SurvivorDesc survivorDesc);

	boolean isRangedWeaponEmpty();

	void setRangedWeaponEmpty(boolean boolean1);

	BaseVisual getVisual();

	BaseCharacterSoundEmitter getEmitter();

	void resetModel();

	void resetModelNextFrame();

	IsoSpriteInstance getSpriteDef();

	boolean hasItems(String string, int int1);

	int getXpForLevel(int int1);

	IsoGameCharacter.XP getXp();

	boolean isAsleep();

	void setAsleep(boolean boolean1);

	int getZombieKills();

	void setForceWakeUpTime(float float1);

	@Deprecated
	void setIgnoreMovementForDirection(boolean boolean1);

	ItemContainer getInventory();

	InventoryItem getPrimaryHandItem();

	void setPrimaryHandItem(InventoryItem inventoryItem);

	InventoryItem getSecondaryHandItem();

	void setSecondaryHandItem(InventoryItem inventoryItem);

	boolean hasEquipped(String string);

	boolean hasEquippedTag(String string);

	boolean isHandItem(InventoryItem inventoryItem);

	boolean isPrimaryHandItem(InventoryItem inventoryItem);

	boolean isSecondaryHandItem(InventoryItem inventoryItem);

	boolean isItemInBothHands(InventoryItem inventoryItem);

	boolean removeFromHands(InventoryItem inventoryItem);

	void setSpeakColourInfo(ColorInfo colorInfo);

	boolean isSpeaking();

	Moodles getMoodles();

	Stats getStats();

	TraitCollection getTraits();

	int getMaxWeight();

	void PlayAnim(String string);

	void PlayAnimWithSpeed(String string, float float1);

	void PlayAnimUnlooped(String string);

	void StartTimedActionAnim(String string);

	void StartTimedActionAnim(String string, String string2);

	void StopTimedActionAnim();

	Stack getCharacterActions();

	void StartAction(BaseAction baseAction);

	void StopAllActionQueue();

	int getPerkLevel(PerkFactory.Perk perk);

	IsoGameCharacter.PerkInfo getPerkInfo(PerkFactory.Perk perk);

	void setPerkLevelDebug(PerkFactory.Perk perk, int int1);

	void LoseLevel(PerkFactory.Perk perk);

	void LevelPerk(PerkFactory.Perk perk, boolean boolean1);

	void LevelPerk(PerkFactory.Perk perk);

	void ReadLiterature(Literature literature);

	void setDir(IsoDirections directions);

	void Callout();

	boolean IsSpeaking();

	void Say(String string);

	void Say(String string, float float1, float float2, float float3, UIFont uIFont, float float4, String string2);

	void setHaloNote(String string);

	void setHaloNote(String string, float float1);

	void setHaloNote(String string, int int1, int int2, int int3, float float1);

	void initSpritePartsEmpty();

	boolean HasTrait(String string);

	void changeState(State state);

	boolean isCurrentState(State state);

	State getCurrentState();

	void pathToLocation(int int1, int int2, int int3);

	void pathToLocationF(float float1, float float2, float float3);

	boolean CanAttack();

	void smashCarWindow(VehiclePart vehiclePart);

	void smashWindow(IsoWindow window);

	void openWindow(IsoWindow window);

	void closeWindow(IsoWindow window);

	void climbThroughWindow(IsoWindow window);

	void climbThroughWindow(IsoWindow window, Integer integer);

	void climbThroughWindowFrame(IsoObject object);

	void climbSheetRope();

	void climbDownSheetRope();

	boolean canClimbSheetRope(IsoGridSquare square);

	boolean canClimbDownSheetRopeInCurrentSquare();

	boolean canClimbDownSheetRope(IsoGridSquare square);

	void climbThroughWindow(IsoThumpable thumpable);

	void climbThroughWindow(IsoThumpable thumpable, Integer integer);

	void climbOverFence(IsoDirections directions);

	boolean isAboveTopOfStairs();

	double getHoursSurvived();

	boolean isOutside();

	boolean isFemale();

	void setFemale(boolean boolean1);

	boolean isZombie();

	boolean isEquipped(InventoryItem inventoryItem);

	boolean isEquippedClothing(InventoryItem inventoryItem);

	boolean isAttachedItem(InventoryItem inventoryItem);

	void faceThisObject(IsoObject object);

	void facePosition(int int1, int int2);

	void faceThisObjectAlt(IsoObject object);

	int getAlreadyReadPages(String string);

	void setAlreadyReadPages(String string, int int1);

	boolean isSafety();

	void setSafety(boolean boolean1);

	float getSafetyCooldown();

	void setSafetyCooldown(float float1);

	float getMeleeDelay();

	void setMeleeDelay(float float1);

	float getRecoilDelay();

	void setRecoilDelay(float float1);

	int getMaintenanceMod();

	float getHammerSoundMod();

	float getWeldingSoundMod();

	boolean isGodMod();

	void setGodMod(boolean boolean1);

	BaseVehicle getVehicle();

	void setVehicle(BaseVehicle baseVehicle);

	float getInventoryWeight();

	List getKnownRecipes();

	boolean isRecipeKnown(Recipe recipe);

	boolean isRecipeKnown(String string);

	long playSound(String string);

	void stopOrTriggerSound(long long1);

	void addWorldSoundUnlessInvisible(int int1, int int2, boolean boolean1);

	boolean isKnownPoison(InventoryItem inventoryItem);

	String getBedType();

	void setBedType(String string);

	PolygonalMap2.Path getPath2();

	void setPath2(PolygonalMap2.Path path);

	PathFindBehavior2 getPathFindBehavior2();

	IsoObject getBed();

	void setBed(IsoObject object);

	boolean isReading();

	void setReading(boolean boolean1);

	float getTimeSinceLastSmoke();

	void setTimeSinceLastSmoke(float float1);

	boolean isInvisible();

	void setInvisible(boolean boolean1);

	boolean isDriving();

	boolean isInARoom();

	boolean isUnlimitedCarry();

	void setUnlimitedCarry(boolean boolean1);

	boolean isBuildCheat();

	void setBuildCheat(boolean boolean1);

	boolean isFarmingCheat();

	void setFarmingCheat(boolean boolean1);

	boolean isHealthCheat();

	void setHealthCheat(boolean boolean1);

	boolean isMechanicsCheat();

	void setMechanicsCheat(boolean boolean1);

	boolean isMovablesCheat();

	void setMovablesCheat(boolean boolean1);

	boolean isTimedActionInstantCheat();

	void setTimedActionInstantCheat(boolean boolean1);

	boolean isTimedActionInstant();

	boolean isShowAdminTag();

	void setShowAdminTag(boolean boolean1);

	void reportEvent(String string);

	AnimatorDebugMonitor getDebugMonitor();

	void setDebugMonitor(AnimatorDebugMonitor animatorDebugMonitor);

	boolean isAiming();

	void resetBeardGrowingTime();

	void resetHairGrowingTime();
}
