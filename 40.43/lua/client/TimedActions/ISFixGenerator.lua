--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISFixGenerator = ISBaseTimedAction:derive("ISFixGenerator");

function ISFixGenerator:isValid()
	return self.generator:getObjectIndex() ~= -1 and
		not self.generator:isActivated() and
		self.generator:getCondition() < 100 and
		self.character:getInventory():contains("ElectronicsScrap")
end

function ISFixGenerator:update()
	self.character:faceThisObject(self.generator)
end

function ISFixGenerator:start()
end

function ISFixGenerator:stop()
    ISBaseTimedAction.stop(self);
end

function ISFixGenerator:perform()
    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);

    self.generator:setCondition(self.generator:getCondition() + 4 + (1*(self.character:getPerkLevel(PerksElectronics))/2))
    self.character:getInventory():RemoveOneOf("ElectronicsScrap");

    if self.generator:getCondition() < 100 and self.character:getInventory():contains("ElectronicsScrap") then
        ISTimedActionQueue.add(ISFixGenerator:new(self.character, self.generator, 150));
    end
end

function ISFixGenerator:new(character, generator, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.generator = generator;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time - (o.character:getPerkLevel(Perks.Electronics) * 3);
    o.caloriesModifier = 4;
	return o;
end
