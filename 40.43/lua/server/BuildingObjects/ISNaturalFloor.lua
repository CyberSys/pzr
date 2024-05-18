--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

ISNaturalFloor = ISBuildingObject:derive("ISNaturalFloor");

--************************************************************************--
--** ISNaturalFloor:new
--**
--************************************************************************--
function ISNaturalFloor:create(x, y, z, north, sprite)
	self.sq = getWorld():getCell():getGridSquare(x, y, z);
	self.javaObject = self.sq:addFloor(sprite);
	self.item:Use();
    -- bag is empty, we'll try to find another one
    if not self.character:getInventory():contains(self.item) then
        for i=0, self.character:getInventory():getItems():size() - 1 do
            local testItem = self.character:getInventory():getItems():get(i);
            if testItem:getType() == self.item:getType() then
                self.item = testItem;
                break;
            end
        end
    end
end

function ISNaturalFloor:new(sprite, northSprite, item, character)
	local o = {};
	setmetatable(o, self);
	self.__index = self;
	o:init();
	o:setSprite(sprite);
	o:setNorthSprite(northSprite);
	o.item = item;
    o.character = character;
    o.player = character:getPlayerNum()
    o.noNeedHammer = true;
	return o;
end

function ISNaturalFloor:isValid(square)
	return self.character:getInventory():contains(self.item) and
		square and square:getFloor() ~= nil
end

function ISNaturalFloor:render(x, y, z, square)
	ISBuildingObject.render(self, x, y, z, square)
end
