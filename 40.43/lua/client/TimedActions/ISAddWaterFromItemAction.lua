--***********************************************************
--**                   THE INDIE STONE                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISAddWaterFromItemAction = ISBaseTimedAction:derive("ISAddWaterFromItemAction")

function ISAddWaterFromItemAction:getWaterMax(isoObject)
	return isoObject:hasModData() and tonumber(isoObject:getModData().waterMax) or 0
end

function ISAddWaterFromItemAction:isValid()
	return self.itemFrom:isWaterSource() and self.character:getInventory():contains(self.itemFrom) and
		self.objectTo:getObjectIndex() ~= -1 and self:getWaterMax(self.objectTo) and
		self.objectTo:getWaterAmount() < self:getWaterMax(self.objectTo)
end

function ISAddWaterFromItemAction:update()
	self.character:faceThisObject(self.objectTo)
	self.itemFrom:setJobDelta(self:getJobDelta())
	local unitsSoFar = math.floor(self.addUnits * self:getJobDelta())
	self.itemFrom:setUsedDelta(self.itemFromStartDelta - unitsSoFar * self.itemFrom:getUseDelta())
end

function ISAddWaterFromItemAction:start()
	self.itemFrom:setJobType(getText("IGUI_JobType_PourOut"))
	self.itemFrom:setJobDelta(0.0)
	self.itemFromStartDelta = self.itemFrom:getUsedDelta()
	local waterAvailable = self.itemFrom:getUsedDelta() / self.itemFrom:getUseDelta()
	if waterAvailable - math.floor(waterAvailable) > 0.99 then
		waterAvailable = math.ceil(waterAvailable)
	end
	local destCapacity = math.ceil(self:getWaterMax(self.objectTo) - self.objectTo:getWaterAmount())
	self.addUnits = math.min(destCapacity, waterAvailable)

	-- The time should be the same as ISTakeWaterAction.
	self.action:setTime(math.max(6, self.addUnits) * 7)
end

function ISAddWaterFromItemAction:stop()
	self.itemFrom:setJobDelta(0.0)
	-- Possibly start() wasn't called yet (because isValid() returned false)
	if self.addUnits and self.addUnits > 0 then
		local unitsSoFar = math.floor(self.addUnits * self:getJobDelta())
		self.itemFrom:setUsedDelta(self.itemFromStartDelta - unitsSoFar * self.itemFrom:getUseDelta())
		self.objectTo:setWaterAmount(self.objectTo:getWaterAmount() + unitsSoFar)
		self.objectTo:transmitModData()
	end
	ISBaseTimedAction.stop(self)
end

function ISAddWaterFromItemAction:perform()
	self.itemFrom:getContainer():setDrawDirty(true)
	self.itemFrom:setJobDelta(0.0)

	self.itemFrom:setUsedDelta(self.itemFromStartDelta - self.addUnits * self.itemFrom:getUseDelta())

	if self.itemFrom:getUsedDelta() < 0.0001 then
		self.itemFrom:Use()
	end

	if self.itemFrom:isTaintedWater() then
		self.objectTo:setTaintedWater(true)
	end
	self.objectTo:setWaterAmount(self.objectTo:getWaterAmount() + self.addUnits)
	self.objectTo:transmitModData()

	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self)
end

function ISAddWaterFromItemAction:new(character, itemFrom, objectTo)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.itemFrom = itemFrom
	o.objectTo = objectTo
	o.stopOnWalk = true
	o.stopOnRun = true
	o.maxTime = 10 -- will set this in start()
	return o
end    	
