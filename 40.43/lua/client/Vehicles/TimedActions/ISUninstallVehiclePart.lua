--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISUninstallVehiclePart = ISBaseTimedAction:derive("ISUninstallVehiclePart")

function ISUninstallVehiclePart:isValid()
	if ISVehicleMechanics.cheat then return true; end
	return self.part:getInventoryItem() and self.vehicle:canUninstallPart(self.character, self.part)
			
--			and
--			self.vehicle:isInArea(self.part:getArea(), self.character)
end

function ISUninstallVehiclePart:update()
	self.character:faceThisObject(self.vehicle)
end

function ISUninstallVehiclePart:start()
end

function ISUninstallVehiclePart:stop()
    ISBaseTimedAction.stop(self)
end

function ISUninstallVehiclePart:perform()
	local perksTable = VehicleUtils.getPerksTableForChr(self.part:getTable("install").skills, self.character)
	local args = { vehicle = self.vehicle:getId(), part = self.part:getId(),
					perks = perksTable,
					mechanicSkill = self.character:getPerkLevel(Perks.Mechanics),
					contentAmount = self.part:getContainerContentAmount() }
	sendClientCommand(self.character, 'vehicle', 'uninstallPart', args)

	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self)
end

function ISUninstallVehiclePart:new(character, part, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.vehicle = part:getVehicle()
	o.part = part
	o.maxTime = time - (character:getPerkLevel(Perks.Mechanics) * (time/15));
	if ISVehicleMechanics.cheat then o.maxTime = 1; end
	o.jobType = getText("Tooltip_Vehicle_Uninstalling", part:getInventoryItem():getDisplayName());
	return o
end

