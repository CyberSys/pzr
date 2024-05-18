--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

ISWoodenWall = ISBuildingObject:derive("ISWoodenWall");

--************************************************************************--
--** ISWoodenWall:new
--**
--************************************************************************--
function ISWoodenWall:create(x, y, z, north, sprite)
	local cell = getWorld():getCell();
	self.sq = cell:getGridSquare(x, y, z);
	self.javaObject = IsoThumpable.new(cell, self.sq, sprite, north, self);
	buildUtil.setInfo(self.javaObject, self);
	buildUtil.consumeMaterial(self);
	if not self.health then
        self.javaObject:setMaxHealth(self:getHealth());
    else
        self.javaObject:setMaxHealth(self.health);
    end
    self.javaObject:setHealth(self.javaObject:getMaxHealth());
    self.javaObject:setName(self.name);
	-- the sound that will be played when our wall will be broken
	self.javaObject:setBreakSound("BreakObject");
	-- add the item to the ground
    self.sq:AddSpecialObject(self.javaObject, self:getObjectIndex());
    self.sq:RecalcAllWithNeighbours(true);
--~ 	ISWoodenWall:checkCorner(x,y,z,north);
--~ 	buildUtil.checkCorner(x,y,z,north,self, self.javaObject);
--	buildUtil.addWoodXp(self);
	self.javaObject:transmitCompleteItemToServer();
    if self.sq:getZone() then
        self.sq:getZone():setHaveConstruction(true);
    end
end

function ISWoodenWall:checkCorner(x,y,z,north)
	local found = false;
	local sx = x;
	local sy = y;
	local sq2 = getCell():getGridSquare(x + 1, y - 1, z);
	for i = 0, sq2:getSpecialObjects():size() - 1 do
		local item = sq2:getSpecialObjects():get(i);
		if instanceof(item, "IsoThumpable") and item:getNorth() ~= north  then
			found = true;
			sx = x + 1;
			sy = y;
			break;
		end
	end
	if found then
		ISWoodenWall:addCorner(sx,sy,z,north);
	end
end

function ISWoodenWall:addCorner(x,y,z, north)
	local sq = getCell():getGridSquare(x, y, z);
	local corner = IsoThumpable.new(getCell(), sq, "TileWalls_51", north, self);
	corner:setCorner(true);
	corner:setCanBarricade(false);
	sq:AddSpecialObject(corner);
	corner:transmitCompleteItemToServer();
end

function ISWoodenWall:new(sprite, northSprite, corner)
	local o = {};
	setmetatable(o, self);
	self.__index = self;
	o:init();
	o:setSprite(sprite);
	o:setNorthSprite(northSprite);
	o.corner = corner;
	o.canBarricade = true;
	o.name = "Wooden Wall";
	return o;
end

-- return the health of the new wall, it's 200 + 100 per carpentry lvl
function ISWoodenWall:getHealth()
    if self.sprite == "carpentry_02_80" then -- log walls are stronger
	    return 400 + buildUtil.getWoodHealth(self);
    else
        return 200 + buildUtil.getWoodHealth(self);
    end
end

function ISWoodenWall:isValid(square)
	if not self:haveMaterial(square) then return false end
	if not buildUtil.canBePlace(self, square) then return false end
    if buildUtil.stairIsBlockingPlacement( square, true, (self.nSprite==4 or self.nSprite==2) ) then return false; end
    if not square:hasFloor(self.north) then return false; end
	return square:isFreeOrMidair(false);
end

function ISWoodenWall:render(x, y, z, square)
	ISBuildingObject.render(self, x, y, z, square)
end

function ISWoodenWall:getObjectIndex()
	local north = self.nSprite==4 or self.nSprite==2
	if self.sq:HasStairs() then
		for i = self.sq:getObjects():size(),1,-1 do
			local object = self.sq:getObjects():get(i-1)
			if object:isStairsObject() then
				return i-1
			end
		end
	end
	return -1
end
