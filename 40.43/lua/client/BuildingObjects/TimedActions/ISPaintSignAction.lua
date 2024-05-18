--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISPaintSignAction = ISBaseTimedAction:derive("ISPaintSignAction");

function ISPaintSignAction:isValid()
	return true;
end

function ISPaintSignAction:update()
end

function ISPaintSignAction:start()
end

function ISPaintSignAction:stop()
    ISBaseTimedAction.stop(self);
end

function ISPaintSignAction:perform()
    if self.wall:getProperties():Is("WallW") then
        self.sign = self.sign + 8;
    end
    self.wall:setOverlaySprite("constructedobjects_signs_01_" .. self.sign, self.r,self.g,self.b,1);
    if not ISBuildMenu.cheat then
        self.paintPot:Use();
    end
	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISPaintSignAction:new(character, wall, paintPot, sign,r,g,b)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.wall = wall;
	o.paintPot = paintPot;
	o.sign = sign;
    o.r = r;
    o.g = g;
    o.b = b;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = 100;
    if ISBuildMenu.cheat then o.maxTime = 1; end
    o.caloriesModifier = 4;
	return o;
end
