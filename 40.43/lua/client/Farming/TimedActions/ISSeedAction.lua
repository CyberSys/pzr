--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISSeedAction = ISBaseTimedAction:derive("ISSeedAction");

function ISSeedAction:isValid()
	self.plant:updateFromIsoObject()
	return self.plant:getIsoObject() ~= nil
end

function ISSeedAction:update()
	self.character:faceThisObject(self.plant:getObject())
end

function ISSeedAction:start()
end

function ISSeedAction:stop()
    ISBaseTimedAction.stop(self);
end

function ISSeedAction:perform()
	for i=1, self.nbOfSeed do
		local seed = self.seeds[i];
		self.character:getInventory():Remove(seed);
	end

	local sq = self.plant:getSquare()
	local args = { x = sq:getX(), y = sq:getY(), z = sq:getZ(), typeOfSeed = self.typeOfSeed }
	CFarmingSystem.instance:sendCommand(self.character, 'seed', args)

    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISSeedAction:new(character, seeds, nbOfSeed, typeOfSeed, plant, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.seeds = seeds;
	o.nbOfSeed = nbOfSeed;
	o.typeOfSeed = typeOfSeed;
    o.plant = plant;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
	return o;
end
