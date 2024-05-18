--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISClearAshes = ISBaseTimedAction:derive("ISClearAshes")

function ISClearAshes:isValid()
	return (self.weapon and self.weapon:getCondition() > 0) or not self.weapon;
end

function ISClearAshes:update()

end

function ISClearAshes:start()
end

function ISClearAshes:stop()
    ISBaseTimedAction.stop(self)
end

function ISClearAshes:perform()
    -- needed to remove from queue / start next.
    ISBaseTimedAction.perform(self)
    self.ashes:getSquare():transmitRemoveItemFromSquare(self.ashes);
    self.ashes:getSquare():getObjects():remove(self.ashes);
end

function ISClearAshes:new(character, ashes, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.stopOnWalk = true
	o.stopOnRun = true
	o.maxTime = time
	o.spriteFrame = 0
	o.ashes = ashes
    if character:getAccessLevel() ~= "None" then
        o.maxTime = 1;
    end
	return o
end
