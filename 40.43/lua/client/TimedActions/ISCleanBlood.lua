--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISCleanBlood = ISBaseTimedAction:derive("ISCleanBlood");

function ISCleanBlood:isValid()
	return self.character:getInventory():contains("Bleach") and (self.character:getInventory():contains("BathTowel") or self.character:getInventory():contains("DishCloth") or self.character:getInventory():contains("Mop"));
end

function ISCleanBlood:update()
end

function ISCleanBlood:start()
end

function ISCleanBlood:stop()
    ISBaseTimedAction.stop(self);
end

function ISCleanBlood:perform()
    local bleach = self.character:getInventory():getItemFromType("Bleach");
    bleach:setThirstChange(bleach:getThirstChange() + 0.05);
    if bleach:getThirstChange() > -0.05 then
        bleach:Use();
    end
    self.square:removeBlood(false, false);
    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISCleanBlood:new(character, square, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.square = square;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
    o.caloriesModifier = 5;
    if character:getAccessLevel() ~= "None" then
        o.maxTime = 1;
    end
	return o;
end
