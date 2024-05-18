--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISBBQLightFromLiterature = ISBaseTimedAction:derive("ISBBQLightFromLiterature")

function ISBBQLightFromLiterature:isValid()
	return self.bbq:getObjectIndex() ~= -1  and
		self.character:getInventory():contains(self.lighter) and
		self.character:getInventory():contains(self.item) and
		not self.bbq:isLit()
end

function ISBBQLightFromLiterature:update()
	self.character:faceThisObject(self.bbq)
	self.item:setJobDelta(self:getJobDelta())
end

function ISBBQLightFromLiterature:start()
	self.item:setJobType(campingText.lightFromLiterature)
	self.item:setJobDelta(0.0)
end

function ISBBQLightFromLiterature:stop()
	ISBaseTimedAction.stop(self)
    self.item:setJobDelta(0.0)
end

function ISBBQLightFromLiterature:perform()
	self.item:getContainer():setDrawDirty(true)
    self.item:setJobDelta(0.0)
	self.character:getInventory():Remove(self.item)
	self.lighter:Use()
	local bbq = self.bbq
	local args = { x = bbq:getX(), y = bbq:getY(), z = bbq:getZ() }
	sendClientCommand(self.character, 'bbq', 'light', args)

    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self)
end

function ISBBQLightFromLiterature:new(character, item, lighter, bbq, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.stopOnWalk = true
	o.stopOnRun = true
	o.maxTime = time
	-- custom fields
	o.bbq = bbq
	o.item = item
	o.lighter = lighter
	return o
end