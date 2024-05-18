--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISBBQInsertPropaneTank = ISBaseTimedAction:derive("ISBBQInsertPropaneTank");

function ISBBQInsertPropaneTank:isValid()
	if instanceof(self.tank, "IsoWorldInventoryObject") then
		return self.bbq:getObjectIndex() ~= -1 and self.tank:getWorldObjectIndex() ~= -1
	end
	return self.bbq:getObjectIndex() ~= -1 and
			self.character:getInventory():contains(self.tank)
end

function ISBBQInsertPropaneTank:update()
	self.character:faceThisObject(self.bbq)
end

function ISBBQInsertPropaneTank:start()
end

function ISBBQInsertPropaneTank:stop()
    ISBaseTimedAction.stop(self);
end

function ISBBQInsertPropaneTank:perform()
	local tank = self.tank
	if instanceof(self.tank, "IsoWorldInventoryObject") then
		tank = self.tank:getItem()
		self.tank:getSquare():transmitRemoveItemFromSquare(self.tank)
	else
		if self.tank == self.character:getPrimaryHandItem() then
			self.character:setPrimaryHandItem(nil)
		end
		if self.tank == self.character:getSecondaryHandItem() then
			self.character:setSecondaryHandItem(nil)
		end
		self.character:getInventory():Remove(self.tank) -- TODO: server controls inventory
	end
	local bbq = self.bbq
	local args = { x = bbq:getX(), y = bbq:getY(), z = bbq:getZ(), delta = tank:getUsedDelta() }
	sendClientCommand(self.character, 'bbq', 'insertPropaneTank', args)

	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self)
end

function ISBBQInsertPropaneTank:new (character, bbq, tank, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.stopOnWalk = true
	o.stopOnRun = true
	o.maxTime = time
	-- custom fields
	o.bbq = bbq
	o.tank = tank
	return o
end
