--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISAddFuel = ISBaseTimedAction:derive("ISAddFuel");

function ISAddFuel:isValid()
	return self.generator:getObjectIndex() ~= -1 and
		self.character:getInventory():contains(self.petrol)
end

function ISAddFuel:update()
	self.character:faceThisObject(self.generator)
end

function ISAddFuel:start()
end

function ISAddFuel:stop()
    ISBaseTimedAction.stop(self);
end

function ISAddFuel:perform()
    local endFuel = 0;
    while self.petrol and self.petrol:getUsedDelta() > 0 and self.generator:getFuel() + endFuel < 100 do
        self.petrol:Use();
        endFuel = endFuel + 10;
    end

    self.generator:setFuel(self.generator:getFuel() + endFuel)
    
    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISAddFuel:new(character, generator, petrolCan, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = getSpecificPlayer(character);
    o.petrol = petrolCan;
	o.generator = generator;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
	if getCore():getDebug() then o.maxTime = 1; end
	return o;
end
