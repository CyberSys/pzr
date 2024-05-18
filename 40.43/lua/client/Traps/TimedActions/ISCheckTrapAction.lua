--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISCheckTrapAction = ISBaseTimedAction:derive("ISCheckTrapAction");

function ISCheckTrapAction:isValid()
	self.trap:updateFromIsoObject()
	return self.trap:getIsoObject() ~= nil and self.trap.animal.type ~= nil;
end

function ISCheckTrapAction:update()
end

function ISCheckTrapAction:start()
end

function ISCheckTrapAction:stop()
    ISBaseTimedAction.stop(self);
end

function ISCheckTrapAction:perform()
	local sq = self.trap:getSquare()
	local args = { x = sq:getX(), y = sq:getY(), z = sq:getZ() }
	CTrapSystem.instance:sendCommand(self.character, 'removeAnimal', args)

	ISBaseTimedAction.perform(self);
end

function ISCheckTrapAction:new(character, trap, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.trap = trap;
    o.maxTime = time;
	return o;
end
