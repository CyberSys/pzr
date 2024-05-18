--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISRemoveBurntVehicle = ISBaseTimedAction:derive("ISRemoveBurntVehicle")

function ISRemoveBurntVehicle:isValid()
	return self.vehicle and not self.vehicle:isRemovedFromWorld();
end

function ISRemoveBurntVehicle:update()
	self.character:faceThisObject(self.vehicle)
	self.item:setJobDelta(self:getJobDelta())
	self.item:setJobType(getText("ContextMenu_RemoveBurntVehicle"))
end

function ISRemoveBurntVehicle:start()
	self.item = self.character:getSecondaryHandItem()
end

function ISRemoveBurntVehicle:stop()
	if self.item then
		self.item:setJobDelta(0)
	end
	ISBaseTimedAction.stop(self)
end

function ISRemoveBurntVehicle:perform()
	local totalXp = 5;
	for i=1,math.max(5,self.character:getPerkLevel(Perks.MetalWelding)) do
		if self:checkAddItem("MetalBar", 15) then totalXp = totalXp + 2 end;
		if self:checkAddItem("MetalBar", 15) then totalXp = totalXp + 2 end;
		if self:checkAddItem("MetalBar", 15) then totalXp = totalXp + 2 end;
		if self:checkAddItem("MetalPipe", 15) then totalXp = totalXp + 2 end;
		if self:checkAddItem("MetalPipe", 15) then totalXp = totalXp + 2 end;
		if self:checkAddItem("MetalPipe", 15) then totalXp = totalXp + 2 end;
		if self:checkAddItem("SheetMetal", 25) then totalXp = totalXp + 2 end;
		if self:checkAddItem("SheetMetal", 25) then totalXp = totalXp + 2 end;
		if self:checkAddItem("SheetMetal", 25) then totalXp = totalXp + 2 end;
		if self:checkAddItem("SmallSheetMetal", 15) then totalXp = totalXp + 2 end;
		if self:checkAddItem("SmallSheetMetal", 15) then totalXp = totalXp + 2 end;
		if self:checkAddItem("SmallSheetMetal", 15) then totalXp = totalXp + 2 end;
	end
	for i=1,20 do
		self.item:Use();
	end
	self.character:getXp():AddXP(Perks.MetalWelding, totalXp);
	sendClientCommand(self.character, "vehicle", "remove", { vehicle = self.vehicle:getId() })
	self.item:setJobDelta(0);
	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self)
end

function ISRemoveBurntVehicle:checkAddItem(item, baseChance)
	if ZombRand(baseChance-self.character:getPerkLevel(Perks.MetalWelding)) == 0 then
--		self.character:getInventory():AddItem(item);
		self.vehicle:getSquare():AddWorldInventoryItem(item, ZombRandFloat(0,0.9), ZombRandFloat(0,0.9), 0);
		return true;
	end
	return false;
end

function ISRemoveBurntVehicle:new(character, vehicle)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.vehicle = vehicle
	o.maxTime = 800 - (character:getPerkLevel(Perks.MetalWelding) * 20);
	if getCore():getDebug() or (isClient() and isAdmin()) then o.maxTime = 10 end
	return o
end

