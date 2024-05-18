--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISHotwireVehicle = ISBaseTimedAction:derive("ISHotwireVehicle")

function ISHotwireVehicle:isValid()
	local vehicle = self.character:getVehicle()
	return vehicle ~= nil and
--		vehicle:isEngineWorking() and
		vehicle:isDriver(self.character) and
		not vehicle:isEngineRunning() and 
		not vehicle:isEngineStarted()
end

function ISHotwireVehicle:update()
end

function ISHotwireVehicle:start()
end

function ISHotwireVehicle:stop()
	ISBaseTimedAction.stop(self)
end

function ISHotwireVehicle:perform()
--	local vehicle = self.character:getVehicle()
	sendClientCommand(self.character, 'vehicle', 'hotwireEngine', {electricSkill=self.character:getPerkLevel(Perks.Electricity)})

	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self)
end

function ISHotwireVehicle:new(character)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.maxTime = 200 - (character:getPerkLevel(Perks.Electricity) * 3);
	return o
end

