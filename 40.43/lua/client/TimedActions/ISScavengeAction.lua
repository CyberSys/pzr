--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISScavengeAction = ISBaseTimedAction:derive("ISScavengeAction")

function ISScavengeAction:isValid()
	return true;
end

function ISScavengeAction:update()
    if self.character:getAccessLevel() ~= "None" then
        if self.currentTime == 0 then
            self.currentTime = 1
            -- maxTime=1 for admins, maxTime=800 for non-admins
            for i=1,math.floor(800/150) do
                self:scavenge()
            end
        end
        return
    end

    self.currentTime = self.currentTime + getGameTime():getMultiplier();
    if self.currentTime >= 150 then
        self:scavenge();
        self.currentTime = 0;
    end
end

function ISScavengeAction:start()
end

function ISScavengeAction:stop()
    ISBaseTimedAction.stop(self)
end

function ISScavengeAction:perform()
    self.character:getStats():setFatigue(self.character:getStats():getFatigue() + 0.025)
	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self)
end

-- random a plant/insect, more chance in deep forest
function ISScavengeAction:scavenge()
    self.character:getStats():setEndurance(self.character:getStats():getEndurance() - 0.004)
    local zone = self:getScavengeZone();
    local scavengeZoneNumber = ZombRand(5,15) + self.scavengeZoneIncrease;
    if scavengeZoneNumber <= 0 then
        scavengeZoneNumber = 1;
    end
    if zone then
        local plantLeft = tonumber(zone:getName());
        if getGametimeTimestamp() - zone:getLastActionTimestamp() > 50000 then
            --            print("refill zone");
            zone:setName(scavengeZoneNumber .. "");
            zone:setOriginalName(scavengeZoneNumber .. "");
        elseif plantLeft == 0 then return; end
    end

    local item = self:getPlant();
    if item ~= nil then
        self:addOrDropItems(item.type, item.count);
        self.character:getXp():AddXP(Perks.PlantScavenging, 7);
    elseif ZombRand(9) == 0 then -- give some xp even for a fail
        self.character:getXp():AddXP(Perks.PlantScavenging, 1);
    end

    local goods = self:getForestGoods();
    if goods ~= nil then
        self:addOrDropItems(goods.type, goods.count);
        self.character:getXp():AddXP(Perks.PlantScavenging, 3);
    elseif ZombRand(9) == 0 then -- give some xp even for a fail
        self.character:getXp():AddXP(Perks.PlantScavenging, 1);
    end

    local medicinal = nil;
    if self.character:getKnownRecipes():contains("Herbalist") then
        medicinal = self:getMedicinalHerbs();
        if medicinal ~= nil then
            self:addOrDropItems(medicinal.type, medicinal.count);
            self.character:getXp():AddXP(Perks.PlantScavenging, 5);
        elseif ZombRand(9) == 0 then -- give some xp even for a fail
            self.character:getXp():AddXP(Perks.PlantScavenging, 1);
        end
    end

    if item or goods or medicinal then -- got something
        if not zone then -- register the new zone
            --            print("register new zone");
            zone = getWorld():registerZone(scavengeZoneNumber .. "", "PlantScavenge",self.x - 20, self.y - 20, 0, 40, 40);
            zone:setLastActionTimestamp(getGametimeTimestamp());
        else -- update the plant available
            local plantLeft = tonumber(zone:getName());
            zone:setName(plantLeft - 1 .. "");
--            print("got somethiong",plantLeft)
            --            print("plant left : " .. plantLeft - 1);
            zone:setLastActionTimestamp(getGametimeTimestamp());
        end
        if isClient() then zone:sendToServer() end
        return true;
    end
    return false;
end

function ISScavengeAction:addOrDropItems(type, count)
    local inv = self.character:getInventory()
    local items = inv:AddItems(type, count)
    for i=1,items:size() do
        if instanceof(items:get(i-1), "Food") then
            items:get(i-1):setLastAged(getGameTime():getWorldAgeHours())
        end
    end
    if inv:getCapacityWeight() > inv:getEffectiveCapacity(self.character) then
        for i=1,items:size() do
            self.character:getCurrentSquare():AddWorldInventoryItem(items:get(i-1), 0.0, 0.0, 0.0)
            inv:Remove(items:get(i-1))
        end
    end
end

function ISScavengeAction:getPlant()
    -- no foraging from november to february
    if (getGameTime():getMonth() + 1) >= 11 or (getGameTime():getMonth() + 1) <= 2 then
        return nil;
    end

    local level = self.character:getPerkLevel(Perks.PlantScavenging);
    local baseChance = 3;
    if self.zone:getType() == "DeepForest" then
        baseChance = 7;
    end
    baseChance = baseChance + (4 * level);
    baseChance = baseChance + self.bonusFindingChance;

    -- harder to find food during early months
    if (getGameTime():getMonth() + 1) == 3 or (getGameTime():getMonth() + 1) == 4 then
        baseChance = baseChance - 2;
    end

    if baseChance > (25 + self.bonusFindingChance) then
        baseChance = 25 + self.bonusFindingChance;
    end

    local possibilities = {};
    -- hydrocraft compatibility
    for i,v in ipairs(scavenges.plants) do
        if v.skill <= level then
            table.insert(possibilities, v);
        end
    end
    -- check for all the scavenging possibilities
    if self.options["Insects"] then
        for i,v in ipairs(scavenges.insects) do
            if v.skill <= level then
                table.insert(possibilities, v);
            end
        end
    end
    if self.options["Mushrooms"] then
        for i,v in ipairs(scavenges.mushrooms) do
            if v.skill <= level then
                table.insert(possibilities, v);
            end
        end
    end
    if self.options["Berries"] then
        for i,v in ipairs(scavenges.berries) do
            if v.skill <= level then
                table.insert(possibilities, v);
            end
        end
    end

    --    baseChance = 100;

    -- you now get more than 1 plants
    if #possibilities > 0 and ZombRand(100) < baseChance then
        local result = {};
        local item = possibilities[ZombRand(#possibilities) + 1];
        local count = ZombRand(item.minCount, item.maxCount) + (level / 2);
        if count > item.maxCount then
            count = item.maxCount;
        end
        result.type = item.type;
        result.count = count;
        return result;
    end

    return nil;
end

function ISScavengeAction:getMedicinalHerbs()
    if not self.options["MedicinalPlants"] then
        return nil;
    end
    -- no foraging from november to february
    if (getGameTime():getMonth() + 1) >= 11 or (getGameTime():getMonth() + 1) <= 2 then
        return nil;
    end

    local level = self.character:getPerkLevel(Perks.PlantScavenging);
    local baseChance = 3;
    if self.zone:getType() == "DeepForest" then
        baseChance = 7;
    end
    baseChance = baseChance + (4 * level);
    baseChance = baseChance + self.bonusFindingChance;

    if baseChance > (25 + self.bonusFindingChance) then
        baseChance = 25 + self.bonusFindingChance;
    end

    local possibilities = {};
    -- check for all the scavenging possibilities
    for i,v in ipairs(scavenges.medicinalPlants) do
        if v.skill <= level then
            table.insert(possibilities, v);
        end
    end

    --    baseChance = 100;

    -- you now get more than 1 plants
    if #possibilities > 0 and ZombRand(100) < baseChance then
        local result = {};
        local item = possibilities[ZombRand(#possibilities) + 1];
        local count = ZombRand(item.minCount, item.maxCount) + (level / 2);
        if count > item.maxCount then
            count = item.maxCount;
        end
        result.type = item.type;
        result.count = count;
        return result;
    end

    return nil;
end

function ISScavengeAction:getForestGoods()
    if not self.options["ForestGoods"] then
        return nil;
    end
    local level = self.character:getPerkLevel(Perks.PlantScavenging);
    local baseChance = 8;
    if self.zone:getType() == "DeepForest" then
        baseChance = 15;
    end
    baseChance = baseChance + (4 * level);
    baseChance = baseChance + self.bonusFindingChance;
    if baseChance > (30 + self.bonusFindingChance) then
        baseChance = 30 + self.bonusFindingChance;
    end

    local possibilities = {};
    -- check for all the scavenging possibilities
    for i,v in ipairs(scavenges.forestGoods) do
        if v.skill <= level then
            table.insert(possibilities, v);
        end
    end

    --    baseChance = 100;

    -- you now get more than 1 plants
    if #possibilities > 0 and ZombRand(100) < baseChance then
        local result = {};
        local item = possibilities[ZombRand(#possibilities) + 1];
        local count = ZombRand(item.minCount, item.maxCount) + (level);
        if count > item.maxCount then
            count = item.maxCount;
        end
        result.type = item.type;
        result.count = count;
        return result;
    end

    return nil;
end

function ISScavengeAction:getScavengeZone()
    local zones = getWorld():getMetaGrid():getZonesAt(self.x, self.y, 0);
    if zones then
        for i=0,zones:size()-1 do
            if zones:get(i):getType() == "PlantScavenge" then
                return zones:get(i);
            end
        end
    end
end

ISScavengeAction.getScavengingZone = function(x,y)
    local zones = getWorld():getMetaGrid():getZonesAt(x, y, 0);
    if zones then
        for i=0,zones:size()-1 do
            if zones:get(i):getType() == "PlantScavenge" then
                return zones:get(i);
            end
        end
    end
end

function ISScavengeAction:new(character, zone, options, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.zone = zone
    o.options = options;
	o.stopOnWalk = true
	o.stopOnRun = true
	o.maxTime = 800
    o.x = character:getSquare():getX();
    o.y = character:getSquare():getY();
    o.currentTime = 0;

    -- sandbox settings
    o.scavengeZoneIncrease = 0;
    if SandboxVars.NatureAbundance == 1 then -- very poor
        o.scavengeZoneIncrease = -5;
    elseif SandboxVars.NatureAbundance == 2 then -- poor
        o.scavengeZoneIncrease = -2;
    elseif SandboxVars.NatureAbundance == 4 then -- abundant
        o.scavengeZoneIncrease = 2;
    elseif SandboxVars.NatureAbundance == 5 then -- very abundant
        o.scavengeZoneIncrease = 5;
    end

    o.bonusFindingChance = 0;
    if SandboxVars.NatureAbundance == 1 then -- very poor
        o.bonusFindingChance = -7;
    elseif SandboxVars.NatureAbundance == 2 then -- poor
        o.bonusFindingChance = -3;
    elseif SandboxVars.NatureAbundance == 4 then -- abundant
        o.bonusFindingChance = 3;
    elseif SandboxVars.NatureAbundance == 5 then -- very abundant
        o.bonusFindingChance = 7;
    end
    if character:getAccessLevel() ~= "None" then
        o.maxTime = 5;
    end
	return o
end