-- Default function to award XP when using a recipe.
function DefaultRecipe_OnGiveXP(recipe, ingredients, result, player)
	for i=1,ingredients:size() do
		if ingredients:get(i-1):getType() == "Plank" or ingredients:get(i-1):getType() == "Log" then
			player:getXp():AddXP(Perks.Woodwork, 1)
		end
	end
	if instanceof(result, "Food") then
		player:getXp():AddXP(Perks.Cooking, 3)
	elseif result:getType() == "Plank" then
		player:getXp():AddXP(Perks.Woodwork, 3)
    end
end

function DismantleElectronics_OnGiveXP(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.Electricity, 2);
end

function NoXP_OnGiveXP(recipe, ingredients, result, player)
end

function GiveSawLogsXP(recipe, ingredients, result, player)
    if player:getPerkLevel(Perks.Woodwork) <= 3 then
        player:getXp():AddXP(Perks.Woodwork, 3);
    else
        player:getXp():AddXP(Perks.Woodwork, 1);
    end
end

function Give3CookingXP(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.Cooking, 3);
end

function Give10CookingXP(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.Cooking, 10);
end

function Give10MWXP(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.MetalWelding, 10);
end

function Give15MWXP(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.MetalWelding, 15);
end

function Give20MWXP(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.MetalWelding, 20);
end

function Give25MWXP(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.MetalWelding, 25);
end

function Give10BSXP(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.Blacksmith, 10);
end

function Give15BSXP(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.Blacksmith, 15);
end

function Give20BSXP(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.Blacksmith, 25);
end

function Give25BSXP(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.Blacksmith, 25);
end

-- check when refilling the blowtorch that blowtorch is not full and propane tank not empty
function RefillBlowTorch_OnTest(item)
    if item:getType() == "BlowTorch" then
        if item:getUsedDelta() == 1 then return false; end
    elseif item:getType() == "PropaneTank" then
        if item:getUsedDelta() == 0 then return false; end
    end
    return true;
end

-- Fill entirely the blowtorch with the remaining propane
function RefillBlowTorch_OnCreate(items, result, player)
    local previousBT = nil;
    local propaneTank = nil;
    for i=0, items:size()-1 do
       if items:get(i):getType() == "BlowTorch" then
           previousBT = items:get(i);
       elseif items:get(i):getType() == "PropaneTank" then
           propaneTank = items:get(i);
       end
    end
    result:setUsedDelta(previousBT:getUsedDelta());

    while result:getUsedDelta() < 1 and propaneTank:getUsedDelta() > 0 do
        result:setUsedDelta((result:getUsedDelta() + result:getUseDelta() * 30));
        propaneTank:Use();
    end

    if result:getUsedDelta() > 1 then
        result:setUsedDelta(1);
    end
end

function OnOpenBoxOfJars(items, result, player)
    player:getInventory():AddItems("Base.JarLid", 6);
end

-- change result quality depending on your BS skill and the tools used
function BSItem_OnCreate(items, result, player)
    local ballPeen = player:getInventory():contains("BallPeenHammer");

    if instanceof(result, "HandWeapon") then
        local condPerc = ZombRand(5 + (player:getPerkLevel(Perks.Blacksmith) * 5), 10 + (player:getPerkLevel(Perks.Blacksmith) * 10));
        if not ballPeen then
            condPerc = condPerc - 20;
        end
        if condPerc < 5 then
            condPerc = 5;
        elseif condPerc > 100 then
            condPerc = 100;
        end
        result:setCondition(round(result:getConditionMax() * (condPerc/100)));
    end
end

-- Return true if recipe is valid, false otherwise
function TorchBatteryRemoval_TestIsValid (sourceItem, result)
	return sourceItem:getUsedDelta() > 0;
end

-- When creating item in result box of crafting panel.
function TorchBatteryRemoval_OnCreate(items, result, player)
  for i=0, items:size()-1 do
	-- we found the battery, we change his used delta according to the battery
	if items:get(i):getType() == "Torch" then
		result:setUsedDelta(items:get(i):getUsedDelta());
		-- then we empty the torch used delta (his energy)
		items:get(i):setUsedDelta(0);
	end
  end


end

-- Return true if recipe is valid, false otherwise
function TorchBatteryInsert_TestIsValid (sourceItem, result)
	if sourceItem:getType() == "Torch" then
		return sourceItem:getUsedDelta() == 0; -- Only allow the battery inserting if the flashlight has no battery left in it.
	end
	return true -- the battery
end

-- When creating item in result box of crafting panel.
function TorchBatteryInsert_OnCreate(items, result, player)
  for i=0, items:size()-1 do
	-- we found the battery, we change his used delta according to the battery
	if items:get(i):getType() == "Battery" then
		result:setUsedDelta(items:get(i):getUsedDelta());
	end
  end
end

function MakeBowlOfSoup4_OnCreate(items, result, player)
    local addType = "Base.Pot"
    for i=0,items:size() - 1 do
        if items:get(i):getType() == "PotOfSoup" or items:get(i):getType() == "PotOfSoupRecipe" or items:get(i):getType() == "RicePan" or items:get(i):getType() == "PastaPan"or items:get(i):getType() == "PastaPot"or items:get(i):getType() == "RicePot" or items:get(i):getType() == "WaterPotRice" then
            result:setBaseHunger(items:get(i):getBaseHunger() / 4);
            result:setHungChange(items:get(i):getBaseHunger() / 4);
            result:setThirstChange(items:get(i):getThirstChange() / 4);
            result:setBoredomChange(items:get(i):getBoredomChange() / 4);
            result:setUnhappyChange(items:get(i):getUnhappyChange() / 4);
            result:setCarbohydrates(items:get(i):getCarbohydrates() / 4);
            result:setLipids(items:get(i):getLipids() / 4);
            result:setProteins(items:get(i):getProteins() / 4);
            result:setCalories(items:get(i):getCalories() / 4);
            result:setTaintedWater(items:get(i):isTaintedWater())
            if string.contains(items:get(i):getType(), "Pan") then
                addType = "Base.Saucepan"
            end
         end
    end
    player:getInventory():AddItem(addType);
end

function MakeBowlOfSoup2_OnCreate(items, result, player)
    local addType = "Base.Pot"
    for i=0,items:size() - 1 do
        if items:get(i):getType() == "PotOfSoup" or items:get(i):getType() == "PotOfSoupRecipe" or items:get(i):getType() == "RicePan" or items:get(i):getType() == "PastaPan" or items:get(i):getType() == "PastaPot"or items:get(i):getType() == "RicePot" or items:get(i):getType() == "WaterPotRice" then
            result:setBaseHunger(items:get(i):getBaseHunger() / 2);
            result:setHungChange(items:get(i):getBaseHunger() / 2);
            result:setThirstChange(items:get(i):getThirstChange() / 2);
            result:setBoredomChange(items:get(i):getBoredomChange() / 2);
            result:setUnhappyChange(items:get(i):getUnhappyChange() / 2);
            result:setCarbohydrates(items:get(i):getCarbohydrates() / 2);
            result:setLipids(items:get(i):getLipids() / 2);
            result:setProteins(items:get(i):getProteins() / 2);
            result:setCalories(items:get(i):getCalories() / 2);
            result:setTaintedWater(items:get(i):isTaintedWater())
            if string.contains(items:get(i):getType(), "Pan") then
                addType = "Base.Saucepan"
            end
        end
    end
    player:getInventory():AddItem(addType);
end

function OpenCandyPackage_OnCreate(items, result, player)
    player:getInventory():AddItem("Base.MintCandy");
    player:getInventory():AddItem("Base.MintCandy");
    player:getInventory():AddItem("Base.MintCandy");
    player:getInventory():AddItem("Base.MintCandy");
    player:getInventory():AddItem("Base.MintCandy");
    player:getInventory():AddItem("Base.MintCandy");
end

function MakeBowlOfStew4_OnCreate(items, result, player)
    for i=0,items:size() - 1 do
        if items:get(i):getType() == "PotOfStew" then
            result:setBaseHunger(items:get(i):getBaseHunger() / 4);
            result:setHungChange(items:get(i):getBaseHunger() / 4);
            result:setThirstChange(items:get(i):getThirstChange() / 4);
            result:setBoredomChange(items:get(i):getBoredomChange() / 4);
            result:setUnhappyChange(items:get(i):getUnhappyChange() / 4);
            result:setCarbohydrates(items:get(i):getCarbohydrates() / 4);
            result:setLipids(items:get(i):getLipids() / 4);
            result:setProteins(items:get(i):getProteins() / 4);
            result:setCalories(items:get(i):getCalories() / 4);
            result:setTaintedWater(items:get(i):isTaintedWater())
        end
    end
    player:getInventory():AddItem("Base.Pot");
end

function MakeBowlOfStew2_OnCreate(items, result, player)
    for i=0,items:size() - 1 do
        if items:get(i):getType() == "PotOfStew" then
            result:setBaseHunger(items:get(i):getBaseHunger() / 2);
            result:setHungChange(items:get(i):getBaseHunger() / 2);
            result:setThirstChange(items:get(i):getThirstChange() / 2);
            result:setBoredomChange(items:get(i):getBoredomChange() / 2);
            result:setUnhappyChange(items:get(i):getUnhappyChange() / 2);
            result:setCarbohydrates(items:get(i):getCarbohydrates() / 2);
            result:setLipids(items:get(i):getLipids() / 2);
            result:setProteins(items:get(i):getProteins() / 2);
            result:setCalories(items:get(i):getCalories() / 2);
            result:setTaintedWater(items:get(i):isTaintedWater())
        end
    end
    player:getInventory():AddItem("Base.Pot");
end

function SliceBreadDough_TestIsValid(sourceItem, result)
    if sourceItem:getFullType() == "Base.BreadDough" then
        return sourceItem:isCooked()
    end
    return true
end

function SlicePie_OnCreate(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "PieWholeRaw" or item:getType() == "CakeRaw" then
            result:setBaseHunger(item:getBaseHunger() / 5);
            result:setHungChange(item:getBaseHunger() / 5);
            result:setBoredomChange(item:getBoredomChange() / 5)
            result:setUnhappyChange(item:getUnhappyChange() / 5)
            result:setCalories(item:getCalories() / 5)
            result:setCarbohydrates(item:getCarbohydrates() / 5)
            result:setLipids(item:getLipids() / 5)
            result:setProteins(item:getProteins() / 5)
        end
    end
    player:getInventory():AddItem("Base.BakingPan");
end

function CutFish_TestIsValid(sourceItem, result)
    if instanceof(sourceItem, "Food") then
        return sourceItem:getActualWeight() > 0.6
    end
    return true
end

function CutFish_OnCreate(items, result, player)
    local fish = nil;
    for i=0,items:size() - 1 do
        if instanceof(items:get(i), "Food") then
            fish = items:get(i);
            break;
        end
    end
    if fish then
        local hunger = math.max(fish:getBaseHunger(), fish:getHungChange())
        result:setBaseHunger(hunger / 2);
        result:setHungChange(hunger / 2);
        result:setActualWeight((fish:getActualWeight() * 0.9) / 2)
        result:setWeight(result:getActualWeight());
        result:setCustomWeight(true)
        result:setCarbohydrates(fish:getCarbohydrates() / 2);
        result:setLipids(fish:getLipids() / 2);
        result:setProteins(fish:getProteins() / 2);
        result:setCalories(fish:getCalories() / 2);
        result:setCooked(fish:isCooked());
    end
end

function CutFillet_TestIsValid(sourceItem, result)
    if instanceof(sourceItem, "Food") then
        return sourceItem:getActualWeight() > 1.0
    end
    return true
end

function CutFillet_OnCreate(items, result, player)
    local fillet = nil
    for i=0,items:size() - 1 do
        if items:get(i):getType() == "FishFillet" then
            fillet = items:get(i)
            break
        end
    end
    if fillet then
        local hunger = math.max(fillet:getBaseHunger(), fillet:getHungChange())
        fillet:setBaseHunger(hunger * 0.5)
        fillet:setHungChange(fillet:getBaseHunger())
        fillet:setActualWeight(fillet:getActualWeight() * 0.5)

        result:setBaseHunger(fillet:getBaseHunger())
        result:setHungChange(fillet:getBaseHunger())
        result:setActualWeight(fillet:getActualWeight())
        result:setCustomWeight(true)
        result:setCarbohydrates(fillet:getCarbohydrates());
        result:setLipids(fillet:getLipids());
        result:setProteins(fillet:getProteins());
        result:setCalories(fillet:getCalories());
    end
end

function CutAnimal_OnCreate(items, result, player)
    local anim = nil;
    for i=0,items:size() - 1 do
        if instanceof(items:get(i), "Food") then
            anim = items:get(i);
            break;
        end
    end
    if anim then
        result:setBaseHunger(anim:getBaseHunger() + 0.02);
        result:setHungChange(anim:getBaseHunger() + 0.02);
    end
end

-- give the bowl back
function OnPutCakeBatterInBaking(items,result,player)
    player:getInventory():AddItem("Base.Bowl");
end

-- set the age of the food to the can, you need to cook it to have a 2-3 months preservation
function CannedFood_OnCreate(items, result, player)
    local food = nil;
    for i=0,items:size() - 1 do
        if instanceof(items:get(i), "Food") then
            if not food or (food:getAge() < items:get(i):getAge()) then
                food = items:get(i);
--                print("got food with age " .. food:getAge())
            end
        end
    end
--    print("new jared food age " .. food:getAge() .. " and max age " .. food:getOffAgeMax());
    result:setAge(food:getAge());
    result:setOffAgeMax(food:getOffAgeMax());
    result:setOffAge(food:getOffAge());
end

-- set back the age of the food and give the jar back
function OpenCannedFood_OnCreate(items, result, player)
    local jar = items:get(0);
    local aged = jar:getAge() / jar:getOffAgeMax();

    result:setAge(result:getOffAgeMax() * aged);

    player:getInventory():AddItem("Base.EmptyJar");

--    print("you're new food have age " .. result:getAge());
end

-- you cook your can, now set the correct food age/max age
function CannedFood_OnCooked(cannedFood)
    local aged = cannedFood:getAge() / cannedFood:getOffAgeMax();
    cannedFood:setOffAgeMax(90);
    cannedFood:setOffAge(60);
    cannedFood:setAge(cannedFood:getOffAgeMax() * aged);
--    print("new jared food age " .. cannedFood:getAge() .. " and max age " .. cannedFood:getOffAgeMax());
end

-- give back the rope used
function Split2LogsStack_OnCreate(items, result, player)
    player:getInventory():AddItem("Base.Rope");
    player:getInventory():AddItem("Base.Rope");
end

-- give back the rope used
function Split3LogsStack_OnCreate(items, result, player)
    player:getInventory():AddItem("Base.Rope");
    player:getInventory():AddItem("Base.Rope");
    player:getInventory():AddItem("Base.Rope");
end

-- give back the rope used
function Split4LogsStack_OnCreate(items, result, player)
    player:getInventory():AddItem("Base.Rope");
    player:getInventory():AddItem("Base.Rope");
    player:getInventory():AddItem("Base.Rope");
    player:getInventory():AddItem("Base.Rope");
end

function Dismantle_OnCreate(items, result, player)
    player:getInventory():AddItem("Base.ElectronicsScrap");
end

function Dismantle2_OnCreate(items, result, player)
    player:getInventory():AddItem("Base.ElectronicsScrap");
    player:getInventory():AddItem("Base.ElectronicsScrap");
end

function SpikedBat_OnCreate(items, result, player)
    for i=1,items:size() do
        local item = items:get(i-1)
        if item:getType() == "BaseballBat" then
            result:setCondition(item:getCondition())
            break
        end
    end
end

function OpenEggCarton_OnCreate(items, result, player)
    result:setAge(items:get(0):getAge());
end

--[[ ############# Radio stuff ############## --]]

function DismantleRadioSpecial_OnCreate(items, result, player, selectedItem)
    local success = 50 + (player:getPerkLevel(Perks.Electricity)*5);
    if ZombRand(0,100)<success then
        player:getInventory():AddItem("Radio.ScannerModule");
    end
    DismantleRadioTwoWay_OnCreate(items, result, player, selectedItem);
end
function DismantleRadioHAM_OnCreate(items, result, player, selectedItem)
    local success = 50 + (player:getPerkLevel(Perks.Electricity)*5);
    if ZombRand(0,100)<success then
        player:getInventory():AddItem("Radio.RadioTransmitter");
    end
    if ZombRand(0,100)<success then
        player:getInventory():AddItem("Base.LightBulbGreen");
    end
    DismantleRadio_OnCreate(items, result, player, selectedItem);
end
function DismantleRadioTwoWay_OnCreate(items, result, player, selectedItem)
    local success = 50 + (player:getPerkLevel(Perks.Electricity)*5);
    if ZombRand(0,100)<success then
        player:getInventory():AddItem("Radio.RadioTransmitter");
    end
    if ZombRand(0,100)<success then
        player:getInventory():AddItem("Base.LightBulbGreen");
    end
    DismantleRadio_OnCreate(items, result, player, selectedItem);
end
function DismantleRadio_OnCreate(items, result, player, selectedItem)           --TODO adding return items/chance based on selectedItem value
    local success = 50 + (player:getPerkLevel(Perks.Electricity)*5);
    for i=1,ZombRand(1,4) do
        local r = ZombRand(1,4);
        if r==1 then
            player:getInventory():AddItem("Base.ElectronicsScrap");
        elseif r==2 then
            player:getInventory():AddItem("Radio.ElectricWire");
        elseif r==3 then
            player:getInventory():AddItem("Base.Aluminum");
        end
    end
    if ZombRand(0,100)<success then
        player:getInventory():AddItem("Base.Amplifier");
    end
    if ZombRand(0,100)<success then
        player:getInventory():AddItem("Base.LightBulb");
    end
    if ZombRand(0,100)<success then
        player:getInventory():AddItem("Radio.RadioReceiver");
    end
    --if selectedItem then
        --print("Main item "..selectedItem:getName());
    --end
    for i=1,items:size() do
        local item = items:get(i-1)
        if instanceof(item, "Radio") then
            item:getDeviceData():getBattery(player:getInventory())
            item:getDeviceData():getHeadphones(player:getInventory())
            break
        end
    end
end
function DismantleRadioTV_OnCreate(items, result, player, selectedItem)
    local success = 50 + (player:getPerkLevel(Perks.Electricity)*5);
    for i=1,ZombRand(1,6) do
        local r = ZombRand(1,4);
        if r==1 then
            player:getInventory():AddItem("Base.ElectronicsScrap");
        elseif r==2 then
            player:getInventory():AddItem("Radio.ElectricWire");
        elseif r==3 then
            player:getInventory():AddItem("Base.Aluminum");
        end
    end
    if ZombRand(0,100)<success then
        player:getInventory():AddItem("Base.Amplifier");
    end
    if ZombRand(0,100)<success then
        player:getInventory():AddItem("Base.LightBulb");
    end
    if selectedItem then
        --print("Main item "..selectedItem:getName());
        if selectedItem:getType()~="TvAntique" then
            if ZombRand(0,100)<success then
                player:getInventory():AddItem("Base.LightBulbRed");
            end
            if ZombRand(0,100)<success then
                player:getInventory():AddItem("Base.LightBulbGreen");
            end
        end
    end
end
function DismantleRadio_OnGiveXP(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.Electricity, 2);
end

local function getRandomValue(valmin, valmax, perkLevel)
    local range = valmax-valmin;
    local r = ZombRandFloat(range*((perkLevel-1)/10),range*(perkLevel/10));
    return valmin+r;
end

function RadioCraft_OnCreate(items, result, player, selectedItem)
    --TransmitRange		= 5000,
    if result and result:getDeviceData() then
        local data = result:getDeviceData();
        local perk = player:getPerkLevel(Perks.Electricity);
        local perkInvert = 10-perk;
        result:setActualWeight(getRandomValue(1.5,3.0,perk));
        data:setUseDelta(getRandomValue(0.007,0.030,perkInvert));
        data:setBaseVolumeRange(getRandomValue(8,16,perk));
        data:setMinChannelRange(getRandomValue(200,88000,perkInvert));
        data:setMaxChannelRange(getRandomValue(108000,1000000,perkInvert));
        data:setTransmitRange(getRandomValue(500,5000,perk));
        data:setHasBattery(false);
        data:setPower(0);
        data:transmitBattryChange();
        if perk == 10 then
            if ZombRand(0,100)<25 then --on max level 25% chance to craft a hightier device. Superior range, very low power consumption.
                data:setIsHighTier(true);
                data:setTransmitRange(ZombRand(5500,7500));
                data:setUseDelta(ZombRand(0.002,0.007));
            end
        end
    end
end
function RadioCraft_OnGiveXP(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.Electricity, player:getPerkLevel(Perks.Electricity)*5);
end

-- smoking cigarettes gives more bonus to a smoker
function OnEat_Cigarettes(food, character)
    if character:HasTrait("Smoker") then
        character:getBodyDamage():setUnhappynessLevel(character:getBodyDamage():getUnhappynessLevel() - 10);
        if character:getBodyDamage():getUnhappynessLevel() < 0 then
            character:getBodyDamage():setUnhappynessLevel(0);
        end
        character:getStats():setStress(character:getStats():getStress() - 10);
        if character:getStats():getStress() then
            character:getStats():setStress(0);
        end
        character:getStats():setStressFromCigarettes(0);
        character:setTimeSinceLastSmoke(0);
    else
--        character:getBodyDamage():setUnhappynessLevel(character:getBodyDamage():getUnhappynessLevel() + 5);
--        if character:getBodyDamage():getUnhappynessLevel() > 100 then
--            character:getBodyDamage():setUnhappynessLevel(100);
--        end
        character:getBodyDamage():setFoodSicknessLevel(character:getBodyDamage():getFoodSicknessLevel() + 14);
        if character:getBodyDamage():getFoodSicknessLevel() > 100 then
            character:getBodyDamage():setFoodSicknessLevel(100);
        end
    end
end

-- give either 2 or 6 ripped sheets depending on what you ripped
function RipClothing_OnCreate(items, result, player, selectedItem)
    local isInfected = false;
    local isDirty = false;
    local ragItem = "Base.RippedSheets";
    local numItems = 6;
    for i=0,items:size() - 1 do
        if instanceof (items:get(i), "Clothing") and (items:get(i):isBloody() or items:get(i):isDirty()) then
            isDirty = true;
            if (items:get(i):isInfected()) then
                isInfected = true;
            end
        end
    end
    
    local isUnderwear = false;
    for i=0,items:size() - 1 do
        if items:get(i):getType() == "Underwear1" or items:get(i):getType() == "Underwear2" then
            isUnderwear = true;
            break
        end
    end

    -- clothing is bloody, remove the previous rag and do a bloody one
    if isDirty then
        -- FIXME: the item will be added back to the player's inventory in ISCraftAction.
        player:getInventory():Remove(result);
        ragItem = "Base.RippedSheetsDirty";
    end
    if not isUnderwear then
        numItems = 6
        player:getInventory():AddItems(ragItem, numItems);
    else
        numItems = 1
        player:getInventory():AddItems(ragItem, numItems);
    end

    if isInfected then
        for i=(player:getInventory():getItems():size() - numItems), player:getInventory():getItems():size()-1 do
            if player:getInventory():getItems():get(i):getType() == "RippedSheetsDirty" then
                player:getInventory():getItems():get(i):setInfected(true)
            end
        end
    end
end

-- check clothings are dirty or bloody
function WashClothing_TestIsValid (sourceItem, result)
    if instanceof(sourceItem, "Clothing") then
        return sourceItem:isDirty() or sourceItem:isBloody();
    else
        return true;
    end
end

-- wash the clothing!
function WashClothing_OnCreate(items, result, player, selectedItem)
    for i=0,items:size() - 1 do
        if instanceof (items:get(i), "Clothing") then
            items:get(i):setDirtyness(0);
            items:get(i):setBloodLevel(0);
            return;
        end
    end
end
