--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISRemoveTrapAction = ISBaseTimedAction:derive("ISRemoveTrapAction");

function ISRemoveTrapAction:isValid()
	self.trap:updateFromIsoObject()
	return self.trap:getIsoObject() ~= nil
end

function ISRemoveTrapAction:update()
end

function ISRemoveTrapAction:start()
end

function ISRemoveTrapAction:stop()
    ISBaseTimedAction.stop(self);
end

function ISRemoveTrapAction:perform()
	local sq = self.trap:getSquare()
	local args = { x = sq:getX(), y = sq:getY(), z = sq:getZ() }
	CTrapSystem.instance:sendCommand(self.character, 'remove', args)

	ISBaseTimedAction.perform(self);
end

function ISRemoveTrapAction:new(character, trap, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.trap = trap;
    o.maxTime = time;
	return o;
end
