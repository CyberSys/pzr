--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISInstallVehiclePart = ISBaseTimedAction:derive("ISInstallVehiclePart")

function ISInstallVehiclePart:isValid()
	if ISVehicleMechanics.cheat then return true; end
	return self.vehicle:canInstallPart(self.character, self.part) and self.character:getInventory():contains(self.item);
			
--			and
--			self.vehicle:isInArea(self.part:getArea(), self.character)
end

function ISInstallVehiclePart:update()
	self.character:faceThisObject(self.vehicle)
	self.item:setJobDelta(self:getJobDelta())
end

function ISInstallVehiclePart:start()
	self.item:setJobType("Install")
end

function ISInstallVehiclePart:stop()
	self.item:setJobDelta(0)
	ISBaseTimedAction.stop(self)
end

function ISInstallVehiclePart:perform()
	self.item:setJobDelta(0)
--	self.character:addMechanicsItem(self.item:getID() .. self.vehicle:getMechanicalID() .. "1", getGameTime():getCalender():getTimeInMillis());

	if self.item == self.character:getPrimaryHandItem() then
		self.character:setPrimaryHandItem(nil)
	end
	if self.item == self.character:getSecondaryHandItem() then
		self.character:setSecondaryHandItem(nil)
	end
	self.character:getInventory():DoRemoveItem(self.item)

	local usedDelta = instanceof(self.item, "DrainableComboItem") and self.item:getUsedDelta() or nil
	local perksTable = VehicleUtils.getPerksTableForChr(self.part:getTable("install").skills, self.character)
	local args = { vehicle = self.vehicle:getId(), part = self.part:getId(),
					item = self.item:getFullType(), itemID = self.item:getID(),
					usedDelta = usedDelta, condition = self.item:getCondition(),
					modData = self.item:hasModData() and self.item:getModData() or nil,
					perks = perksTable,
					mechanicSkill = self.character:getPerkLevel(Perks.Mechanics),
					contentAmount = self.item:getItemCapacity() }
	sendClientCommand(self.character, 'vehicle', 'installPart', args)

	local pdata = getPlayerData(self.character:getPlayerNum());
	if pdata ~= nil then
		pdata.playerInventory:refreshBackpacks();
		pdata.lootInventory:refreshBackpacks();
	end
	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self)
end

function ISInstallVehiclePart:new(character, part, item, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.vehicle = part:getVehicle()
	o.part = part
	o.item = item
	o.maxTime = time - (character:getPerkLevel(Perks.Mechanics) * (time/15));
	if ISVehicleMechanics.cheat then o.maxTime = 1; end
	o.jobType = getText("Tooltip_Vehicle_Installing", item:getDisplayName());
	return o
end
