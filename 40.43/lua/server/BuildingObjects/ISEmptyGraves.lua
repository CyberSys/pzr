--
-- Created by IntelliJ IDEA.
-- User: RJ
-- Date: 08/06/2017
-- Time: 09:25
-- To change this template use File | Settings | File Templates.
--

--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

ISEmptyGraves = ISBuildingObject:derive("ISEmptyGraves");

--************************************************************************--
--** ISEmptyGraves:new
--**
--************************************************************************--
function ISEmptyGraves:create(x, y, z, north, sprite)
	local cell = getWorld():getCell();
	self.sq = cell:getGridSquare(x, y, z);
	--local thumpable = IsoThumpable.new(cell, self.sq, sprite, north, self);
	self:setInfo(self.sq, north, sprite, cell, "sprite1");
	
	-- name of our 2 sprites needed for the rest of the stairs
	local spriteAName = self.northSprite2;
	local spriteBName = self.northSprite3;
	
	local xa = x;
	local ya = y;
	local xb = x;
	local yb = y;
	
	-- we get the x and y of our next tile (depend if we're placing the stairs north or not)
	if north then
		ya = ya - 1;
		yb = yb - 2;
	else
		-- if we're not north we also change our sprite
		spriteAName = self.sprite2;
		spriteBName = self.sprite3;
		xa = xa - 1;
		xb = xb - 2;
	end
	local squareA = getCell():getGridSquare(xa, ya, z);
	if squareA == nil then
		squareA = IsoGridSquare.new(getCell(), nil, xa, ya, z);
		getCell():ConnectNewSquare(squareA, false);
	end
	
	self:setInfo(squareA, north, spriteAName, cell, "sprite2");

	if self.sq:getZone() then
		self.sq:getZone():setHaveConstruction(true);
	end
end

function ISEmptyGraves:setInfo(square, north, sprite, cell, spriteType)
	for i=0,square:getObjects():size()-1 do
		local object = square:getObjects():get(i);
		if object:getProperties() and object:getProperties():Is(IsoFlagType.canBeRemoved) then
			square:transmitRemoveItemFromSquare(object)
			square:RemoveTileObject(object);
			break
		end
	end
	square:disableErosion();
	local args = { x = square:getX(), y = square:getY(), z = square:getZ() }
	sendClientCommand(nil, 'erosion', 'disableForSquare', args)
	
	self.javaObject = IsoThumpable.new(cell, square, sprite, north, self);
	square:RecalcAllWithNeighbours(true);
	self.javaObject:setName("EmptyGraves");
	self.javaObject:setCanBarricade(false);
	self.javaObject:setIsThumpable(false)
	self.javaObject:getModData()["spriteType"] = spriteType;
	self.javaObject:getModData()["corpses"] = 0;
	square:AddSpecialObject(self.javaObject);
	self.javaObject:transmitCompleteItemToServer();
end

function ISEmptyGraves:new(sprite1, sprite2, northSprite1, northSprite2)
	local o = {};
	setmetatable(o, self);
	self.__index = self;
	o:init();
	o:setSprite(sprite1);
	o:setNorthSprite(northSprite1);
	o.sprite2 = sprite2;
	o.northSprite2 = northSprite2;
	o.noNeedHammer = true;
	o.ignoreNorth = true;
	o.firstItem = "Shovel";
	o.maxTime = 100;
	return o;
end

-- return the health of the new stairs, it's 500 + 100 per carpentry lvl
function ISEmptyGraves:getHealth()
	return 500 + buildUtil.getWoodHealth(self);
end

function ISEmptyGraves:render(x, y, z, square)
	-- render the first part
	local spriteName = self:getSprite()
	local sprite = IsoSprite.new()
	sprite:LoadFramesNoDirPageSimple(spriteName)
	
	local floor = square:getFloor();
	local spriteFree = ISBuildingObject.isValid(self, square) and floor:getTextureName() and (luautils.stringStarts(floor:getTextureName(), "floors_exterior_natural") or luautils.stringStarts(floor:getTextureName(), "blends_natural_01"));
	
	if spriteFree then
		sprite:RenderGhostTile(x, y, z);
	else
		sprite:RenderGhostTileRed(x, y, z);
	end

	local spriteAName = self.northSprite2;
	local spriteAFree = true;
	
	-- we get the x and y of our next tile (depend if we're placing the graves north or not)
	local xa, ya = self:getSquare2Pos(square, self.north)
	
	-- if we're not north we also change our sprite
	if not self.north then
		spriteAName = self.sprite2;
	end
	
	local squareA = getCell():getGridSquare(xa, ya, z);
	if squareA == nil and getWorld():isValidSquare(xa, ya, z) then
		squareA = IsoGridSquare.new(getCell(), nil, xa, ya, z);
		getCell():ConnectNewSquare(squareA, false);
	end
	
	local floorA = squareA:getFloor();
	-- test if the square are free to add our graves
	if not buildUtil.canBePlace(self, squareA) or not (luautils.stringStarts(floorA:getTextureName(), "floors_exterior_natural") or luautils.stringStarts(floorA:getTextureName(), "blends_natural_01")) then
		spriteAFree = false;
	end
	-- render our second stage of the graves
	local spriteA = IsoSprite.new();
	spriteA:LoadFramesNoDirPageSimple(spriteAName);
	if spriteAFree then
		spriteA:RenderGhostTile(xa, ya, z);
	else
		spriteA:RenderGhostTileRed(xa, ya, z);
	end
end

function ISEmptyGraves:isValid(square)
	local floor = square:getFloor();
	if not ISBuildingObject.isValid(self, square) or
			not (luautils.stringStarts(floor:getTextureName(), "floors_exterior_natural") or
			luautils.stringStarts(floor:getTextureName(), "blends_natural_01")) then
		return false
	end
	local xa, ya, za = self:getSquare2Pos(square, self.north)
	local squareA = getCell():getGridSquare(xa, ya, za)
	if not squareA or not buildUtil.canBePlace(self, squareA) then
		return false
	end
	local floorA = squareA:getFloor();
	if not (luautils.stringStarts(floorA:getTextureName(), "floors_exterior_natural") or luautils.stringStarts(floorA:getTextureName(), "blends_natural_01")) then
		return false;
	end
	
	return true
end

function ISEmptyGraves:getSquare2Pos(square, north)
	local x = square:getX()
	local y = square:getY()
	local z = square:getZ()
	if north then
		y = y - 1
	else
		x = x - 1
	end
	return x, y, z
end

function ISEmptyGraves.canDigHere(worldObjects)
	local squares = {}
	local didSquare = {}
	for _,worldObj in ipairs(worldObjects) do
		if not didSquare[worldObj:getSquare()] then
			table.insert(squares, worldObj:getSquare())
			didSquare[worldObj:getSquare()] = true
		end
	end
	for _,square in ipairs(squares) do
		local floor = square:getFloor()
		if floor and floor:getTextureName() and
				(luautils.stringStarts(floor:getTextureName(), "floors_exterior_natural") or
				luautils.stringStarts(floor:getTextureName(), "blends_natural_01")) then
			return true
		end
	end
	return false
end

