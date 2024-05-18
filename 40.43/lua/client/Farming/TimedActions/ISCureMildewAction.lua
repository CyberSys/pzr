--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISCureMildewAction = ISBaseTimedAction:derive("ISCureMildewAction");

function ISCureMildewAction:isValid()
	self.plant:updateFromIsoObject()
	return self.plant:getIsoObject() ~= nil
end

function ISCureMildewAction:update()
	self.character:faceThisObject(self.plant:getObject())
end

function ISCureMildewAction:start()
end

function ISCureMildewAction:stop()
    ISBaseTimedAction.stop(self);
end

function ISCureMildewAction:perform()
	local sq = self.plant:getSquare()
	local args = { x = sq:getX(), y = sq:getY(), z = sq:getZ(), uses = self.uses }
	CFarmingSystem.instance:sendCommand(self.character, 'cureMildew', args)

	-- Hack until server manages player inventory
	local level = self.plant.mildewLvl
	for i=1,self.uses do
		if level < 100 then
			self.item:Use()
			level = level - 5
		end
	end

    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISCureMildewAction:new(character, item, uses, plant, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.item = item;
	o.uses = uses;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
    o.plant = plant;
	return o;
end
