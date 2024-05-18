--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISOpenMechanicsUIAction = ISBaseTimedAction:derive("ISOpenMechanicsUIAction")

function ISOpenMechanicsUIAction:isValid()
	return true;
end

function ISOpenMechanicsUIAction:update()
	self.character:faceThisObject(self.vehicle)
end

function ISOpenMechanicsUIAction:start()

end

function ISOpenMechanicsUIAction:stop()
	ISBaseTimedAction.stop(self)
end

function ISOpenMechanicsUIAction:perform()
	local ui = getPlayerMechanicsUI(self.character:getPlayerNum());
	ui.vehicle = self.vehicle;
	ui.usedHood = self.usedHood
	ui:initParts();
	ui:setVisible(true, JoypadState.players[self.character:getPlayerNum()+1])
	ui:addToUIManager()
	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self)
end

function ISOpenMechanicsUIAction:new(character, vehicle, usedHood)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.vehicle = vehicle
	o.usedHood = usedHood
	o.maxTime = 200 - (character:getPerkLevel(Perks.Mechanics) * (200/15));
	if vehicle:getScript() and vehicle:getScript():getWheelCount() == 0 then
		o.maxTime = 1
	end
	if ISVehicleMechanics.cheat or getCore():getDebug() or(isClient() and isAdmin()) then o.maxTime = 1; end
	return o
end

