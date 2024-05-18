--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISPlasterAction = ISBaseTimedAction:derive("ISPlasterAction");

function ISPlasterAction:isValid()
	return ISBuildMenu.cheat or self.character:getInventory():contains("BucketPlasterFull");
end

function ISPlasterAction:update()
end

function ISPlasterAction:start()
end

function ISPlasterAction:stop()
    ISBaseTimedAction.stop(self);
end

function ISPlasterAction:perform()
	local modData = self.thumpable:getModData();
    local north = "";
    if self.thumpable:getNorth() then
        north = "North";
    end
    local sprite = Painting[modData["wallType"]]["plasterTile" .. north];
	local obj = self.thumpable
	local index = obj:getObjectIndex()
	local args = { x=obj:getX(), y=obj:getY(), z=obj:getZ(), index=index, sprite = sprite }
	sendClientCommand(self.character, 'object', 'plaster', args)

	if not ISBuildMenu.cheat then
		self.plasterBucket:Use();
	end
	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISPlasterAction:new(character, thumpable, plasterBucket, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.thumpable = thumpable;
	o.plasterBucket = plasterBucket;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
    o.caloriesModifier = 8;
    if ISBuildMenu.cheat then o.maxTime = 1; end
	return o;
end
