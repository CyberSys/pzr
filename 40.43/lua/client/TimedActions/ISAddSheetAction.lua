--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISAddSheetAction = ISBaseTimedAction:derive("ISAddSheetAction");

function ISAddSheetAction:isValid()
	if self.item:HasCurtains() then return false end
	return self.character:getInventory():contains("Sheet");
end

function ISAddSheetAction:update()
	self.character:faceThisObjectAlt(self.item)
end

function ISAddSheetAction:start()
end

function ISAddSheetAction:stop()
    ISBaseTimedAction.stop(self);
end

function ISAddSheetAction:perform()
	local obj = self.item
	local index = obj:getObjectIndex()
	local args = { x=obj:getX(), y=obj:getY(), z=obj:getZ(), index=index }
	sendClientCommand(self.character, 'object', 'addSheet', args)

    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISAddSheetAction:new(character, item, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.item = item;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
	return o;
end
