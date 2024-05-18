--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISFireplaceExtinguish = ISBaseTimedAction:derive("ISFireplaceExtinguish");

function ISFireplaceExtinguish:isValid()
	return self.fireplace:getObjectIndex() ~= -1 and self.fireplace:isLit()
end

function ISFireplaceExtinguish:update()
	self.character:faceThisObject(self.fireplace)
end

function ISFireplaceExtinguish:start()
end

function ISFireplaceExtinguish:stop()
    ISBaseTimedAction.stop(self);
end

function ISFireplaceExtinguish:perform()
	local fp = self.fireplace
	local args = { x = fp:getX(), y = fp:getY(), z = fp:getZ() }
	sendClientCommand(self.character, 'fireplace', 'extinguish', args)

	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self)
end

function ISFireplaceExtinguish:new (character, fireplace, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.stopOnWalk = true
	o.stopOnRun = true
	o.maxTime = time
	-- custom fields
	o.fireplace = fireplace
    if character:getAccessLevel() ~= "None" then
        o.maxTime = 1;
    end
	return o
end
