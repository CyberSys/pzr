--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISTakeFuel = ISBaseTimedAction:derive("ISTakeFuel");

function ISTakeFuel:isValid()
	local pumpCurrent = tonumber(self.square:getProperties():Val("fuelAmount"))
	return pumpCurrent > 0
end

function ISTakeFuel:update()
	self.petrolCan:setJobDelta(self:getJobDelta())
	self.character:faceLocation(self.square:getX(), self.square:getY())
	local actionCurrent = math.floor(self.itemStart + (self.itemTarget - self.itemStart) * self:getJobDelta() + 0.001)
	local itemCurrent = math.floor(self.petrolCan:getUsedDelta() / self.petrolCan:getUseDelta() + 0.001)
	if actionCurrent > itemCurrent then
		-- FIXME: sync in multiplayer
		local pumpCurrent = tonumber(self.square:getProperties():Val("fuelAmount"))
		self.square:getProperties():Set("fuelAmount", tostring(pumpCurrent - (actionCurrent - itemCurrent)))

		self.petrolCan:setUsedDelta(actionCurrent * self.petrolCan:getUseDelta())
	end
end

function ISTakeFuel:start()
	if self.petrolCan:getType() == "EmptyPetrolCan" then
		local chr = self.character
		local emptyCan = self.petrolCan
		self.petrolCan = chr:getInventory():AddItem("Base.PetrolCan")
		self.petrolCan:setUsedDelta(0)
		if chr:getPrimaryHandItem() == emptyCan then
			chr:setPrimaryHandItem(self.petrolCan)
		end
		if chr:getSecondaryHandItem() == emptyCan then
			chr:setSecondaryHandItem(self.petrolCan)
		end
		chr:getInventory():Remove(emptyCan)
	end

	self.petrolCan:setJobType(getText("ContextMenu_TakeGasFromPump"))
	self.petrolCan:setJobDelta(0.0)

	local pumpCurrent = tonumber(self.square:getProperties():Val("fuelAmount"))
	local itemCurrent = math.floor(self.petrolCan:getUsedDelta() / self.petrolCan:getUseDelta() + 0.001)
	local itemMax = math.floor(1 / self.petrolCan:getUseDelta() + 0.001)
	local take = math.min(pumpCurrent, itemMax - itemCurrent)
	self.action:setTime(take * 50)
	self.itemStart = itemCurrent
	self.itemTarget = itemCurrent + take
end

function ISTakeFuel:stop()
	self.petrolCan:setJobDelta(0.0)
    ISBaseTimedAction.stop(self);
end

function ISTakeFuel:perform()
	self.petrolCan:setJobDelta(0.0)
	local itemCurrent = math.floor(self.petrolCan:getUsedDelta() / self.petrolCan:getUseDelta() + 0.001)
	if self.itemTarget > itemCurrent then
		self.petrolCan:setUsedDelta(self.itemTarget * self.petrolCan:getUseDelta())
		-- FIXME: sync in multiplayer
		local pumpCurrent = tonumber(self.square:getProperties():Val("fuelAmount"))
		self.square:getProperties():Set("fuelAmount", tostring(pumpCurrent + (self.itemTarget - itemCurrent)))
	end
    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISTakeFuel:new(character, square, petrolCan, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
    o.square = square;
	o.petrolCan = petrolCan;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
	return o;
end
