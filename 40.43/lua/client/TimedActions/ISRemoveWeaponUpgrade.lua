require "TimedActions/ISBaseTimedAction"

ISRemoveWeaponUpgrade = ISBaseTimedAction:derive("ISRemoveWeaponUpgrade");

function ISRemoveWeaponUpgrade:isValid()
    if not self.character:getInventory():getItemFromType("Screwdriver") then return false end
    if not self.character:getInventory():contains(self.weapon) then return false end
    return self.weapon:getWeaponPart(self.part:getPartType()) == self.part
end

function ISRemoveWeaponUpgrade:update()
end

function ISRemoveWeaponUpgrade:start()
end

function ISRemoveWeaponUpgrade:stop()
    ISBaseTimedAction.stop(self);
end

function ISRemoveWeaponUpgrade:perform()
    self.weapon:detachWeaponPart(self.part)
    self.character:getInventory():AddItem(self.part);
    -- needed to remove from queue / start next.
    ISBaseTimedAction.perform(self);
end

function ISRemoveWeaponUpgrade:new(character, weapon, part, time)
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.character = character;
    o.weapon = weapon;
    o.part = part;
    o.stopOnWalk = true;
    o.stopOnRun = true;
    o.maxTime = time;
    return o;
end
