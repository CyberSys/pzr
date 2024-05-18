--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISPlaceCarBatteryChargerAction = ISBaseTimedAction:derive("ISPlaceCarBatteryChargerAction")

function ISPlaceCarBatteryChargerAction:isValid()
	return self.character:getInventory():contains(self.charger)
end

function ISPlaceCarBatteryChargerAction:start()
end

function ISPlaceCarBatteryChargerAction:update()
end

function ISPlaceCarBatteryChargerAction:stop()
	ISBaseTimedAction.stop(self)
end

function ISPlaceCarBatteryChargerAction:perform()
	local sq = self.character:getSquare()
	local args = { x = sq:getX(), y = sq:getY(), z = sq:getZ() }
	args.item = self.charger
	sendClientCommand(self.character, 'carBatteryCharger', 'place', args)

	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self)
end

function ISPlaceCarBatteryChargerAction:new(character, charger, time)
	local o = ISBaseTimedAction.new(self, character)
	o.stopOnWalk = true
	o.stopOnRun = true
	o.maxTime = time
	o.charger = charger
	return o
end
