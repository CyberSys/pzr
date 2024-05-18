--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISRemoveBaitAction = ISBaseTimedAction:derive("ISRemoveBaitAction");

function ISRemoveBaitAction:isValid()
	self.trap:updateFromIsoObject()
	return self.trap:getIsoObject() ~= nil and self.trap.bait ~= nil
end

function ISRemoveBaitAction:update()
end

function ISRemoveBaitAction:start()
end

function ISRemoveBaitAction:stop()
    ISBaseTimedAction.stop(self);
end

function ISRemoveBaitAction:perform()
	local sq = self.trap:getSquare()
	local args = { x = sq:getX(), y = sq:getY(), z = sq:getZ() }
	CTrapSystem.instance:sendCommand(self.character, 'removeBait', args)

	ISBaseTimedAction.perform(self);
end

function ISRemoveBaitAction:new(character, trap, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.trap = trap;
    o.maxTime = time;
	return o;
end
