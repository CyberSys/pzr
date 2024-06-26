-----------------------------------------------------------------------
--                          ROBERT JOHNSON                           --
-----------------------------------------------------------------------

camping = {}

camping.tentSprites = {
	sheet = {
		frontLeft = "TileIndieStoneTentFrontLeft",
		backLeft = "TileIndieStoneTentBackLeft",
		frontRight = "TileIndieStoneTentFrontRight",
		backRight = "TileIndieStoneTentBackRight"
	},
	tarp = {
		frontLeft = "camping_01_3",
		backLeft = "camping_01_2",
		frontRight = "camping_01_0",
		backRight = "camping_01_1"
	},
}

function camping.findTentSprites(sprite)
	for _,sprites in pairs(camping.tentSprites) do
		if sprite == sprites.frontLeft or sprite == sprites.backLeft or
				sprite == sprites.frontRight or sprite == sprites.backRight then
			return sprites
		end
	end
	return nil
end

-- add a tent to the ground
function camping.addTent(grid, sprite)
	if not grid then return end
	if camping.findTentObject(grid) then return end

	local tentSprites = camping.findTentSprites(sprite)
	if not tentSprites then return end

	local tent = IsoObject.new(grid, sprite, "Tent")
	grid:AddTileObject(tent)
	tent:transmitCompleteItemToClients()

	local dx = 0
	local dy = 0
	if sprite == tentSprites.frontLeft then
		sprite = tentSprites.backLeft
		dx = -1
	elseif sprite == tentSprites.frontRight then
		sprite = tentSprites.backRight
		dy = -1
	else
		error "expected front tent sprite"
	end

	grid = getCell():getGridSquare(grid:getX() + dx, grid:getY() + dy, grid:getZ())
	tent = IsoObject.new(grid, sprite, "Tent")
	grid:AddTileObject(tent)
	tent:transmitCompleteItemToClients()

	return tent;
end

-- remove a tent
function camping.removeTent(tent)
	if not tent then return end
	local grid = tent:getSquare()
	if not grid then return end

	local tentSprites = camping.findTentSprites(tent:getSpriteName())
	if not tentSprites then return end

	grid:transmitRemoveItemFromSquare(tent)

	local dx = 0
	local dy = 0
	if tent:getSpriteName() == tentSprites.frontLeft then
		dx = -1
	elseif tent:getSpriteName() == tentSprites.backLeft then
		dx = 1
	elseif tent:getSpriteName() == tentSprites.frontRight then
		dy = -1
	elseif tent:getSpriteName() == tentSprites.backRight then
		dy = 1
	end

	grid = getCell():getGridSquare(grid:getX() + dx, grid:getY() + dy, grid:getZ())
	tent = camping.findTentObject(grid)
	if not tent then return end
	grid:transmitRemoveItemFromSquare(tent)
end

function camping.isTentObject(object)
	if not object then return false end
	return object:getObjectName() == "Tent"
end

function camping.findTentObject(square)
	if not square then return nil end
	for i=0,square:getObjects():size()-1 do
		local object = square:getObjects():get(i)
		if camping.isTentObject(object) then
			return object
		end
	end
	return nil
end

function camping.tentAt(x, y, z)
	return camping.findTentObject(getCell():getGridSquare(x, y, z))
end

-- return the tent on the gridSquare the player is standing on
-- or from the gridsquare in parameter (if from context menu for example)
camping.getCurrentTent = function(grid)
	if not grid then return nil end
	return camping.findTentObject(grid)
end

if isClient() then return end

local function noise(message) if getDebug() then print('tent: '..message) end end

local function OnClientCommand(module, command, player, args)
	if module ~= 'camping' then return end
	local argStr = ''
	for k,v in pairs(args) do argStr = argStr..' '..k..'='..v end
	noise('OnClientCommand '..module..' '..command..' '..argStr)
	if command == 'addTent' then
		local gs = getCell():getGridSquare(args.x, args.y, args.z)
		if gs then
			camping.addTent(gs, args.sprite)
		end
	elseif command == 'removeTent' then
		local tent = camping.tentAt(args.x, args.y, args.z)
		if tent then
			camping.removeTent(tent)
			local kit = InventoryItemFactory.CreateItem("camping.CampingTentKit")
			player:sendObjectChange('addItem', { item = kit } )
		end
	end
end

Events.OnClientCommand.Add(OnClientCommand)

