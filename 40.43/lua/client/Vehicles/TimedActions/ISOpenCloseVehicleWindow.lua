--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISOpenCloseVehicleWindow = ISBaseTimedAction:derive("ISOpenCloseVehicleWindow")

function ISOpenCloseVehicleWindow:isValid()
	-- TODO: if zombie reaching in the window, can't close it
	local installed = not self.part:getItemType() or (self.part:getInventoryItem() ~= nil)
	return installed and self.window and
		(self.window:isOpen() ~= self.open) and
		not self.window:isDestroyed()
end

function ISOpenCloseVehicleWindow:update()
	-- TODO: animate window + character
end

function ISOpenCloseVehicleWindow:start()
end

function ISOpenCloseVehicleWindow:stop()
	ISBaseTimedAction.stop(self)
end

function ISOpenCloseVehicleWindow:perform()
	local args = { vehicle = self.vehicle:getId(), part = self.part:getId(), open = self.open }
	sendClientCommand(self.character, 'vehicle', 'setWindowOpen', args)

	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self)
end

function ISOpenCloseVehicleWindow:new(character, part, open, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.vehicle = part:getVehicle()
	o.part = part
	o.window = part:getWindow()
	o.open = open
	o.maxTime = time
	return o
end

