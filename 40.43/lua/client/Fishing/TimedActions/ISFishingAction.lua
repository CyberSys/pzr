--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISFishingAction = ISBaseTimedAction:derive("ISFishingAction");
ISFishingAction.splashTimer = 0;
ISFishingAction.swingTimer = 0;

function ISFishingAction:isValid()
	if self.rod ~= self.character:getPrimaryHandItem() then return false end
	return self.lure == self.character:getSecondaryHandItem() or not self.lure;
end

function ISFishingAction:update()
    self.rod:setJobDelta(self:getJobDelta());
    ISFishingAction.splashTimer = ISFishingAction.splashTimer -1;
    if ISFishingAction.splashTimer == 0 and not self.woodenLance then
--        getSoundManager():PlayWorldSound("waterSplash", false, self.character:getSquare(), 1, 20, 1, false)
        self.character:playSound("LureHitWater");
        addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 20, 1)
    end
    ISFishingAction.swingTimer = ISFishingAction.swingTimer - 1;
    if ISFishingAction.swingTimer == 0 then
        self.character:PlayAnim("Idle");
    end

    -- add some boredom if you're not skilled
    if self.fishingLvl < 3 then
        self.character:getBodyDamage():setBoredomLevel(self.character:getBodyDamage():getBoredomLevel() + (ZomboidGlobals.BoredomDecrease * 0.01 * getGameTime():getMultiplier()))
    end
end

function ISFishingAction:start()
    self.rod:setJobType(getText("ContextMenu_Fishing"));
    self.rod:setJobDelta(0.0);
    self.pole = self.character:getPrimaryHandItem();
    self.woodenLance = false;
    if self.pole:getType() == "WoodenLance" then
        self.woodenLance = true;
        end
    if not self.woodenLance then
        self.character:PlayAnimUnlooped("Attack_" .. self.pole:getSwingAnim());
        self.character:SetAnimFrame(0, false);
        self.character:getSpriteDef():setFrameSpeedPerFrame(0.1);
        ISFishingAction.swingTimer = 40;
        ISFishingAction.splashTimer = 20 + ZombRand(10);
--        getSoundManager():PlayWorldSound("waterSplash", false, self.character:getSquare(), 1, 20, 1, false)
        self.character:playSound("CastFishingLine");
        addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 20, 1)
    end
    self.character:setIgnoreMovementForDirection(false);
    self.character:faceThisObject(self.tile);
    self.character:setIgnoreMovementForDirection(true);
end

function ISFishingAction:stop()
    self.character:PlayAnim("Idle");
    self.character:setIgnoreMovementForDirection(false);
    ISBaseTimedAction.stop(self);
    self.rod:setJobDelta(0.0);
end

function ISFishingAction:perform()

    -- needed to remove from queue / start next.
    ISBaseTimedAction.perform(self);
    self.rod:setJobDelta(0.0);

    self.character:PlayAnim("Idle");
    self.character:setIgnoreMovementForDirection(false);

    if self.woodenLance then
--        getSoundManager():PlayWorldSound("waterSplash", false, self.character:getSquare(), 1, 20, 1, false);
        self.character:playSound("StrikeWithFishingSpear");
        addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 20, 1)
    end

    -- get the fishing zone to see how many fishes left
    local updateZone = self:getFishingZone();
    local fishLeft = 99;
    if updateZone then
        fishLeft = tonumber(updateZone:getName());
        if getGametimeTimestamp() - updateZone:getLastActionTimestamp() > 20000 then
            updateZone:setName(ZombRand(10,25) + self.fishingZoneIncrease .. "");
        end
        if fishLeft == 0 then return; end
    end


    if self:attractFish() then -- caught something !
        local fish = self:getFish();
        if updateZone then
            local fishLeft = tonumber(updateZone:getName());
            updateZone:setName(fishLeft - 1 .. "");
            updateZone:setLastActionTimestamp(getGametimeTimestamp());
            if isClient() then updateZone:sendToServer() end
        end
    else
        if ZombRand(9) == 0 then -- give some xp even for a fail
            self.character:getXp():AddXP(Perks.Fishing, 1);
        end
        if self.lureProperties and ZombRand(100) <= self.lureProperties.chanceOfBreak then -- maybe remove the lure
            self.character:getSecondaryHandItem():Use();
            self.character:setSecondaryHandItem(nil);
        end
    end

    if not updateZone then -- register a new fishing zone
        local newZone = getWorld():registerZone(ZombRand(10,25) + self.fishingZoneIncrease .. "", "Fishing",self.tile:getSquare():getX() - 20, self.tile:getSquare():getY() - 20,self.tile:getSquare():getZ(), 40, 40);
        newZone:setLastActionTimestamp(getGametimeTimestamp());
        if isClient() then newZone:sendToServer() end
    end

    if not self.woodenLance then
        local lure = ISWorldObjectContextMenu.getFishingLure(self.character, self.rod)
        if lure then
            ISWorldObjectContextMenu.equip(self.character, self.character:getSecondaryHandItem(), lure:getType(), false);
            ISTimedActionQueue.add(ISFishingAction:new(self.character, self.tile, self.rod, lure));
        end
    else
        ISTimedActionQueue.add(ISFishingAction:new(self.character, self.tile, self.rod, nil));
    end
end

function ISFishingAction:getFishingZone()
    local zones = getWorld():getMetaGrid():getZonesAt(self.tile:getSquare():getX(), self.tile:getSquare():getY(), self.tile:getSquare():getZ());
    if zones then
        for i=0,zones:size()-1 do
            if zones:get(i):getType() == "Fishing" then
                return zones:get(i);
            end
        end
    end
end

-- get a fish by the number
-- if plastic lure : 15/100 it's a big, 25/100 medium and 60/100 it's a little/lure fish
-- if living lure : 20/100 it's a big, 30/100 it's a medium and 50/100 it's a little/lure fish
function ISFishingAction:getFish()
    local fishSizeNumber = ZombRand(100);
    local fish = {};
    -- we gonna determine the fish size and give player's xp
    -- first, if we have a plastic lure
    if self.plasticLure then
        if fishSizeNumber <= 15 then
            fish.size = "Big";
            self.character:getXp():AddXP(Perks.Fishing, 7);
        elseif fishSizeNumber <= 25 then
            fish.size = "Medium";
            self.character:getXp():AddXP(Perks.Fishing, 5);
        else
            fish.size = "Small";
            self.character:getXp():AddXP(Perks.Fishing, 3);
        end
    else -- living lure size
        if fishSizeNumber <= 20 then
            fish.size = "Big";
            self.character:getXp():AddXP(Perks.Fishing, 7);
        elseif fishSizeNumber <= 30 then
            fish.size = "Medium";
            self.character:getXp():AddXP(Perks.Fishing, 5);
        else
            fish.size = "Small";
            self.character:getXp():AddXP(Perks.Fishing, 3);
        end
    end
    fish.fish = self:getFishByLure();
    if fish.fish.name then -- if no name then it's a "trash" item
    -- then we may broke our line
        if not self:brokeLine(fish) then
            -- we gonna create our fish
            self:createFish(fish, fish.fish);
--            getSoundManager():PlayWorldSound("getFish", false, self.character:getSquare(), 1, 20, 1, false)
            self.character:playSound("CatchFish");
            addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 20, 1)
        end
    else
        self.character:getInventory():AddItem(fish.fish.item);
        if not self.woodenLance then
--            getSoundManager():PlayWorldSound("getFish", false, self.character:getSquare(), 1, 20, 1, false)
            self.character:playSound("CatchTrashWithRod");
            addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 20, 1)
        end
    end

    -- remove the lure
    if not self.plasticLure and self.character:getSecondaryHandItem() then
        self.character:getSecondaryHandItem():Use();
        self.character:setSecondaryHandItem(nil);
    end
end

function ISFishingAction:getFishByLure()
    local fish = Fishing.fishes[ZombRand(#Fishing.fishes) + 1];
    -- check if this fish can be attracted by this lure
    for i,v in ipairs(fish.lure) do
        if (self.lure and v == self.lure:getType()) or self.woodenLance then
            -- give another chance for trash item
            if not fish.name and ZombRand(2) == 0 then
                return self:getFishByLure();
            else
                return fish;
            end
        end
    end
    return self:getFishByLure();
end

-- create the fish we just get
-- we randomize is weight and size according to his size
-- then we set his new name
function ISFishingAction:createFish(fishType, fish)
--    local fish = Fishing.fishes[fishType.fishType];
    local fishToCreate = InventoryItemFactory.CreateItem(fish.item);
    local size = nil;
    local weight = nil;
    -- now we set the size (for the name) and weight (for hunger) according to his size (little, medium and big)
    if fishType.size == "Small" then
        size = ZombRand(fish.little.minSize, fish.little.maxSize);
        weight = size / fish.little.weightChange;
        fishToCreate:setCalories(fishToCreate:getCalories() * 0.9);
        fishToCreate:setLipids(fishToCreate:getLipids() * 0.9);
        fishToCreate:setCarbohydrates(fishToCreate:getCarbohydrates() * 0.9);
        fishToCreate:setProteins(fishToCreate:getProteins() * 0.9);
    elseif fishType.size == "Medium" then
        size = ZombRand(fish.medium.minSize, fish.medium.maxSize);
        weight = size / fish.medium.weightChange;
    else
        size = ZombRand(fish.big.minSize, fish.big.maxSize);
        weight = size / fish.big.weightChange;
        fishToCreate:setCalories(fishToCreate:getCalories() * 1.5);
        fishToCreate:setLipids(fishToCreate:getLipids() * 1.5);
        fishToCreate:setCarbohydrates(fishToCreate:getCarbohydrates() * 1.5);
        fishToCreate:setProteins(fishToCreate:getProteins() * 1.5);
    end
    -- the fish name is like : Big Trout - 26cm
    if not fish.noNameChange then
        fishToCreate:setName(getText("IGUI_Fish_" .. fishType.size) .. " " .. getText("IGUI_Fish_" .. string.gsub(fish.name, "%s+", "")) .. " - " .. size .. "cm");
    end
    -- hunger reduction is weight of the fish div by 7, and set it to negative
    fishToCreate:setBaseHunger(- weight / 7);
    fishToCreate:setHungChange(fishToCreate:getBaseHunger());
    -- weight is kg * 2.2 (in pound)
    fishToCreate:setActualWeight(weight * 2.2);
    fishToCreate:setCustomWeight(true)
    self.character:getInventory():AddItem(fishToCreate);
end

------ broken line risk : (every skills pts lower by 1 this number)
-------- * big fish 9/100 risk to broke the line
-------- * medium fish 6/100
-------- * little fish 2/100
function ISFishingAction:brokeLine(fish)
    local brokenLineNumber = ZombRand(100);
    local breakRodeNumber = 0;
    if fish.size == "Small" then
        breakRodeNumber = 8 - self.fishingLvl;
    elseif fish.size == "Medium" then
        breakRodeNumber = 12 - self.fishingLvl;
    else
        breakRodeNumber = 22 - self.fishingLvl;
    end
    if not string.match(self.pole:getType(), "TwineLine") then -- a rod with twine line have more chance to break
       breakRodeNumber = breakRodeNumber - 2;
    end
    if breakRodeNumber < 0 then
        breakRodeNumber = 0;
    end
    if self.pole:getType() == "CraftedFishingRod" then -- a crafted rode have more chance to break
        breakRodeNumber = breakRodeNumber + 3;
    end
    if self.pole:getType() == "WoodenLance" then -- a wooden lance have more chance to break
        breakRodeNumber = breakRodeNumber + 5;
    end
    if brokenLineNumber <= breakRodeNumber then
        return self:brokeThisLine();
    end
    return false;
end

function ISFishingAction:brokeThisLine()
    if self.pole:getType() == "CraftedFishingRod" or self.pole:getType() == "CraftedFishingRodTwineLine" then
        self.character:getInventory():AddItem("Base.WoodenStick");
        self.character:playSound("BreakFishingLine")
    elseif self.pole:getType() == "FishingRod" or self.pole:getType() == "FishingRodTwineLine" then
        self.character:getInventory():AddItem("Base.FishingRodBreak");
        self.character:playSound("BreakFishingLine")
    elseif self.pole:getType() == "WoodenLance" then
        -- nothing giving back from broke wooden lance
        self.character:playSound(self.pole:getBreakSound())
    end
    self.character:getInventory():Remove(self.pole);
    self.character:setPrimaryHandItem(nil);
    if self.lure then
        self.character:getInventory():Remove(self.lure);
        self.character:setSecondaryHandItem(nil);
    end
--    getSoundManager():PlaySound("waterSplash", false, 1, 0)
    return true;
end

-- Depend on what lure you used :
-- Living lure is easier but can escape and always removed after getting something
-- Plastic lure are for good fisherman, almost never disapear but harder to get something
function ISFishingAction:attractFish()
    local attractNumber = ZombRand(self.attractNumber);
    -- a bit more chance during dawn and dusk
    local currentHour = math.floor(math.floor(GameTime:getInstance():getTimeOfDay() * 3600) / 3600);
    if (currentHour >= 4 and currentHour <= 6) or (currentHour >= 18 and currentHour <= 20) then
        attractNumber = attractNumber - 10;
    end
    if self.woodenLance then -- less chance to catch something with a wooden lance
        attractNumber = attractNumber + 10;
    end
    -- harder chance of getting fish during winter
    if (getGameTime():getMonth() + 1) >= 11 or (getGameTime():getMonth() + 1) <= 2 then
        attractNumber = attractNumber + 20;
    end
    -- start with plastic lure
    if self.plasticLure and attractNumber <= (10 + (self.fishingLvl * 2.5)) then
        return true;
    elseif not self.plasticLure and attractNumber <= (20 + (self.fishingLvl * 1.5)) then
        return true;
    end
--    return true;
    return false;
end

function ISFishingAction:new(character, tile, rod, lure)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.stopOnWalk = true;
	o.stopOnRun = true;
    o.fishingLvl = character:getPerkLevel(Perks.Fishing);
    o.rod = rod
    o.lure = lure
    if o.lure then
        o.lureProperties = Fishing.lure[o.lure:getType()];
        o.plasticLure = o.lureProperties.plastic;
    else
        o.plasticLure = true;
    end
    if not o.lure then -- wooden lance fishing
        o.maxTime = 300 + ZombRand(300) - (o.fishingLvl * 5)
    else
        if o.plasticLure then
            o.maxTime = 700 + ZombRand(300) - (o.fishingLvl * 5);
        else
            o.maxTime = 500 + ZombRand(300) - (o.fishingLvl * 5);
        end
    end
    o.tile = tile;
    -- sandbox settings
    o.fishingZoneIncrease = 0;
    if SandboxVars.NatureAbundance == 1 then -- very poor
        o.fishingZoneIncrease = -10;
    elseif SandboxVars.NatureAbundance == 2 then -- poor
        o.fishingZoneIncrease = -5;
    elseif SandboxVars.NatureAbundance == 3 then -- abundant
        o.fishingZoneIncrease = 5;
    elseif SandboxVars.NatureAbundance == 4 then -- very abundant
        o.fishingZoneIncrease = 10;
    end

    o.attractNumber = 100;
    if SandboxVars.NatureAbundance == 1 then -- very poor
        o.attractNumber = 140;
    elseif SandboxVars.NatureAbundance == 2 then -- poor
        o.attractNumber = 120;
    elseif SandboxVars.NatureAbundance == 3 then -- abundant
        o.attractNumber = 80;
    elseif SandboxVars.NatureAbundance == 4 then -- very abundant
        o.attractNumber = 60;
    end

    o.caloriesModifier = 1.2;

    return o;
end
