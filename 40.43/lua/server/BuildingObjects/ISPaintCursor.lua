--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "BuildingObjects/ISBuildingObject"

ISPaintCursor = ISBuildingObject:derive("ISPaintCursor");

local PaintColor = {
	PaintBlue = {r=0.48,g=0.62,b=0.82},
	PaintGreen = {r=0.43,g=0.60,b=0.34},
	PaintLightBrown = {r=0.73,g=0.53,b=0.42},
	PaintLightBlue = {r=0.70,g=0.79,b=0.87},
	PaintBrown = {r=0.51,g=0.32,b=0.26},
	PaintOrange = {r=0.78,g=0.50,b=0.30},
	PaintCyan = {r=0.62,g=0.86,b=0.84},
	PaintPink = {r=0.81,g=0.49,b=0.58},
	PaintGrey = {r=0.51,g=0.54,b=0.54},
	PaintTurquoise = {r=0.36,g=0.61,b=0.61},
	PaintPurple = {r=0.75,g=0.36,b=0.71},
	PaintYellow = {r=0.90,g=0.78,b=0.42},
	PaintWhite = {r=0.92,g=0.92,b=0.92},
}

function ISPaintCursor:create(x, y, z, north, sprite)
	local sq = getWorld():getCell():getGridSquare(x, y, z)
	local playerObj = self.character
	local object = self:getObjectList()[self.objectIndex]
	if luautils.walkAdj(playerObj, sq, true) then
		local args = self.args
		if self.action == "paintSign" then
			ISTimedActionQueue.add(ISPaintSignAction:new(playerObj, object, playerObj:getInventory():FindAndReturn(args.paintType), args.sign, args.r, args.g, args.b, 100))
		end
		if self.action == "paintThump" then
			ISTimedActionQueue.add(ISPaintAction:new(playerObj, object, playerObj:getInventory():FindAndReturn(args.paintType), args.paintType, 100))
		end
		if self.action == "plaster" then
			ISTimedActionQueue.add(ISPlasterAction:new(playerObj, object, playerObj:getInventory():FindAndReturn("BucketPlasterFull"), 100))
		end
	end
end

function ISPaintCursor:_isWall(object)
	if object and object:getProperties() then
		return object:getProperties():Is(IsoFlagType.cutW) or object:getProperties():Is(IsoFlagType.cutN)
	end
	return false
end

function ISPaintCursor:_isDoorFrame(object)
	return object and (object:getType() == IsoObjectType.doorFrW or object:getType() == IsoObjectType.doorFrN)
end

function ISPaintCursor:rotateKey(key)
	if key == getCore():getKey("Rotate building") then
		self.objectIndex = self.objectIndex + 1
		local objects = self:getObjectList()
		if self.objectIndex > #objects then
			self.objectIndex = 1
		end
	end
end

function ISPaintCursor:isValid(square)
	self.renderX = square:getX()
	self.renderY = square:getY()
	self.renderZ = square:getZ()
	return #self:getObjectList() > 0
end

function ISPaintCursor:render(x, y, z, square)
	if not self.floorSprite then
		self.floorSprite = IsoSprite.new()
		self.floorSprite:LoadFramesNoDirPageSimple('media/ui/FloorTileCursor.png')
	end

	if not self:isValid(square) then
		self.floorSprite:RenderGhostTileRed(x, y, z)
		return
	end
	self.floorSprite:RenderGhostTileColor(x, y, z, 0, 1, 0, 0.8)

	if self.currentSquare ~= square then
		self.objectIndex = 1
		self.currentSquare = square
	end

	self.renderX = x
	self.renderY = y
	self.renderZ = z

	local objects = self:getObjectList()
	if self.objectIndex >= 1 and self.objectIndex <= #objects then
		local object = objects[self.objectIndex]
		local color = {r=0.8, g=0.8, b=0.8}
		if self.action ~= "plaster" then
			color = PaintColor[self.args.paintType]
			if not color then color = {r=1,g=0,b=0} end
		end
		if self.action == "paintSign" then
--			if not self.signSprite then
				local sign = self.args.sign
				if object:getProperties():Is("WallW") then
					sign = sign + 8;
				end
				self.signSprite = IsoSprite.new()
				self.signSprite:LoadFramesNoDirPageSimple("constructedobjects_signs_01_" .. sign)
--			end
			self.signSprite:RenderGhostTileColor(x, y, z, color.r, color.g, color.b, 1.0)
		elseif self.action == "plaster" then
			local north = (object:getNorth() and "North") or ""
			local modData = object:getModData()
			local spriteName = Painting[modData.wallType]["plasterTile" .. north]
			self.plasterSprite = IsoSprite.new()
			self.plasterSprite:LoadFramesNoDirPageSimple(spriteName)
			self.plasterSprite:RenderGhostTile(x, y, z)
		else
			object:getSprite():RenderGhostTileColor(x, y, z, color.r, color.g, color.b, 0.8)
		end
	end
end

function ISPaintCursor:onJoypadPressButton(joypadIndex, joypadData, button)
	local playerObj = getSpecificPlayer(joypadData.player)

	if button == Joypad.AButton or button == Joypad.BButton then
		return ISBuildingObject.onJoypadPressButton(self, joypadIndex, joypadData, button)
	end

	if button == Joypad.RBumper then
		self.objectIndex = self.objectIndex + 1
		local objects = self:getObjectList()
		if self.objectIndex > #objects then
			self.objectIndex = 1
		end
	end

	if button == Joypad.LBumper then
		self.objectIndex = self.objectIndex - 1
		if self.objectIndex < 1 then
			local objects = self:getObjectList()
			self.objectIndex = #objects
		end
	end
end

function ISPaintCursor:getAPrompt()
	if #self:getObjectList() > 0 then
		if self.action == "paintSign" then return getText("ContextMenu_PaintSign") end
		if self.action == "paintThump" then return getText("ContextMenu_Paint") end
		if self.action == "plaster" then return getText("ContextMenu_Plaster") end
	end
end

function ISPaintCursor:getLBPrompt()
	if #self:getObjectList() > 1 then
		return "Previous Object"
	end
end

function ISPaintCursor:getRBPrompt()
	if #self:getObjectList() > 1 then
		return "Next Object"
	end
end

function ISPaintCursor:canPaint(object)
	if not object or not object:getSquare() or not object:getSprite() then return false end
	if not object:getSquare():isCouldSee(self.player) then return false end
	if self.action == "paintSign" then
		if object:getProperties():Is("WallN") or object:getProperties():Is("WallW") then
			return true
		end
	end
	if self.action == "paintThump" then
		if instanceof(object, "IsoThumpable") and object:isPaintable() then
			local modData = object:getModData()
			return Painting[modData.wallType][self.args.paintType] ~= nil
		end
	end
	if self.action == "plaster" then
		if instanceof(object, "IsoThumpable") and object:canBePlastered() then
			return true
		end
	end
	return false
end

function ISPaintCursor:getObjectList()
	local square = getCell():getGridSquare(self.renderX, self.renderY, self.renderZ)
	if not square then return {} end
	local objects = {}
	for i = square:getObjects():size(),1,-1 do
		local object = square:getObjects():get(i-1)
		if self:canPaint(object) then
			table.insert(objects, object)
		end
	end
	return objects
end

function ISPaintCursor:new(character, action, args)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o:init()
	o.character = character
	o.player = character:getPlayerNum()
	o.skipBuildAction = true
	o.noNeedHammer = false
	o.skipWalk = true
	o.renderFloorHelper = true
--	o.dragNilAfterPlace = true
	o.action = action
	o.args = args
	o.objectIndex = 1
	o.renderX = -1
	o.renderY = -1
	o.renderZ = -1
	return o
end

