--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISPaintAction = ISBaseTimedAction:derive("ISPaintAction");

function ISPaintAction:isValid()
	return true;
end

function ISPaintAction:update()
end

function ISPaintAction:start()
end

function ISPaintAction:stop()
    ISBaseTimedAction.stop(self);
end

function ISPaintAction:perform()
	local modData = self.thumpable:getModData();
    local north = "";
    if self.isThump then
        if self.thumpable:getNorth() then
            north = "North";
        end
    else
        if self.thumpable:getSprite():getProperties():Is("WallN") == true or self.thumpable:getSprite():getProperties():Is(IsoFlagType.WindowN) == true or self.thumpable:getSprite():getProperties():Is("DoorWallN") == true then
            north = "North";
        end
        if self.thumpable:getSprite():getProperties():Is("WallNW") == true then
            north = "Corner";
        end
    end
    local sprite = nil;
    local paintingType = self.thumpable:getSprite():getProperties():Val("PaintingType");
    if self.isThump then
        if Painting[modData["wallType"]] then
            sprite = Painting[modData["wallType"]][self.painting .. north];
        end
    else
        sprite = Painting[paintingType][self.painting .. north];
    end
	self.thumpable:cleanWallBlood();
    if not sprite then
        local color = OtherPainting[paintingType][self.painting];
        self.thumpable:setCustomColor(ColorInfo.new(color.r, color.g, color.b, 1));
        self.thumpable:transmitCustomColor();
	else
	    self.thumpable:setSpriteFromName(sprite);
        self.thumpable:transmitUpdatedSpriteToServer();
    end
    if not ISBuildMenu.cheat then
        self.paintPot:Use();
    end
	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISPaintAction:new(character, thumpable, paintPot, painting, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.thumpable = thumpable;
	o.painting = painting;
	o.paintPot = paintPot;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
    o.isThump = true;
    if not instanceof(thumpable, "IsoThumpable") then
        o.isThump = false;
    end
    if ISBuildMenu.cheat then o.maxTime = 1; end
    o.caloriesModifier = 4;
	return o;
end
