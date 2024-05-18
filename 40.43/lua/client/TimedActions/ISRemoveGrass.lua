--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISRemoveGrass = ISBaseTimedAction:derive("ISRemoveGrass")

function ISRemoveGrass:isValid()
    for i=0,self.square:getObjects():size()-1 do
        local object = self.square:getObjects():get(i);
        if object:getProperties() and object:getProperties():Is(IsoFlagType.canBeRemoved) then
            return true
        end
    end
    return false
end

function ISRemoveGrass:update()
	self.character:faceLocation(self.square:getX(), self.square:getY())
end

function ISRemoveGrass:start()
--	getSoundManager():PlayWorldSound("bushes", self.square, 0.2, 20, 1.0, 3, true)
    self.square:playSound("RemovePlant");
	addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 10, 5)
end

function ISRemoveGrass:stop()
    ISBaseTimedAction.stop(self)
end

function ISRemoveGrass:perform()
	local sq = self.square
	local args = { x = sq:getX(), y = sq:getY(), z = sq:getZ() }
	sendClientCommand(self.character, 'object', 'removeGrass', args)

	ISBaseTimedAction.perform(self)
end

function ISRemoveGrass:new(character, square)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.square = square
	o.stopOnWalk = true
	o.stopOnRun = true
	o.maxTime = 50
	o.spriteFrame = 0
    if character:getAccessLevel() ~= "None" then
        o.maxTime = 1;
    end
	return o
end
