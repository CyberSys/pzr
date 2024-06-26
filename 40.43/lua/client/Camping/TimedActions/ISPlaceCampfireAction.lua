--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISPlaceCampfireAction = ISBaseTimedAction:derive("ISPlaceCampfireAction");

function ISPlaceCampfireAction:isValid()
	return self.character:getInventory():contains(self.item)
end

function ISPlaceCampfireAction:update()
	self.item:setJobDelta(self:getJobDelta());
	self.character:faceLocation(self.sq:getX(), self.sq:getY())
end

function ISPlaceCampfireAction:start()
	self.item:setJobType(campingText.placeCampfire);
	self.item:setJobDelta(0.0);
end

function ISPlaceCampfireAction:stop()
    ISBaseTimedAction.stop(self);
    self.item:setJobDelta(0.0);
end

function ISPlaceCampfireAction:perform()
	if self.character:getPrimaryHandItem() == self.item then
		self.character:setPrimaryHandItem(nil);
	end
	if self.character:getSecondaryHandItem() == self.item then
		self.character:setSecondaryHandItem(nil);
	end
	self.item:getContainer():setDrawDirty(true);
    self.item:setJobDelta(0.0);
	self.character:getInventory():Remove("CampfireKit");

	local args = { x = self.sq:getX(), y = self.sq:getY(), z = self.sq:getZ() }
	CCampfireSystem.instance:sendCommand(self.character, 'addCampfire', args)

	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISPlaceCampfireAction:new (character, sq, item, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.item = item;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
    o.sq = sq;
	return o
end
