--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

if isClient() then return end

local ClientCommands = {}
local Commands = {}
Commands.object = {}

ClientCommands.wantNoise = getDebug()
local noise = function(msg)
	if (ClientCommands.wantNoise) then print('ClientCommand: '..msg) end
end

local getThumpableLight = function(x, y, z)
	local gs = getCell():getGridSquare(x, y, z)
	if not gs then return nil end
	for i=0,gs:getSpecialObjects():size()-1 do
		local o = gs:getSpecialObjects():get(i)
		if o and instanceof(o, 'IsoThumpable') and o:getLightSourceFuel() then
			return o
		end
	end
	return nil
end

Commands.object.toggleLight = function(player, args)
	local o = getThumpableLight(args.x, args.y, args.z)
	if o then
		o:toggleLightSource(not o:isLightSourceOn())
		o:sendObjectChange('lightSource')
		o:revisionUp()
	end
end

Commands.object.insertFuel = function(player, args)
	local o = getThumpableLight(args.x, args.y, args.z)
	if o then
		-- when player inventory is synced properly, we will simply send
		-- the index/id of the item to use instead of creating a new item.
		local fuel = InventoryItemFactory.CreateItem(args.fuel)
		fuel:setUsedDelta(args.usedDelta)
		local previous = o:insertNewFuel(fuel, nil)
		o:sendObjectChange('lightSource')
		player:sendObjectChange('removeItemID', { id = args.itemID, type = args.fuel })
		if previous then
			player:sendObjectChange('addItem', { item = previous })
		end
		o:revisionUp()
	else
		noise('no thumpable light found!')
	end
end

Commands.object.removeFuel = function(player, args)
	local o = getThumpableLight(args.x, args.y, args.z)
	if o and o:haveFuel() then
		local fuel = o:removeCurrentFuel(nil)
		if fuel then
			o:sendObjectChange('lightSource')
			o:revisionUp()
			player:sendObjectChange('addItem', { item = fuel })
		end
	end
end

-- FIXME: getting objects by index is fragile, should have a unique ID
local getThumpable = function(x, y, z, index)
	local sq = getCell():getGridSquare(x, y, z)
	if sq and index >= 0 and index < sq:getObjects():size() then
		o = sq:getObjects():get(index)
		if instanceof(o, 'IsoThumpable') then
			return o
		end
	end
	return nil
end

Commands.object.plaster = function(player, args)
	local o = getThumpable(args.x, args.y, args.z, args.index)
	if o then
		o:setSprite(args.sprite)
		o:setPaintable(true)
		o:transmitUpdatedSpriteToClients()
		o:sendObjectChange("paintable")
		o:revisionUp()
	end
end

Commands.object.addSheetRope = function(player, args)
	local sq = getCell():getGridSquare(args.x, args.y, args.z)
	if sq and args.index >= 0 and args.index < sq:getObjects():size() then
		local o = sq:getObjects():get(args.index)
		if instanceof(o, 'IsoWindow') or instanceof(o, 'IsoThumpable') then
			o:addSheetRope(player, args.itemType or "SheetRope")
		elseif IsoWindowFrame.isWindowFrame(o) then
			IsoWindowFrame.addSheetRope(o, player, args.itemType or "SheetRope")
		else
			noise('not a window, window-frame, or thumpable'.. tostring(o))
			for i=0,sq:getObjects():size()-1 do
				noise(i..' '..tostring(sq:getObjects():get(i)))
			end
		end
	else
		noise('sq is null or index is invalid')
	end
end

Commands.object.removeSheetRope = function(player, args)
	local sq = getCell():getGridSquare(args.x, args.y, args.z)
	if sq and args.index >= 0 and args.index < sq:getObjects():size() then
		local o = sq:getObjects():get(args.index)
		if instanceof(o, 'IsoWindow') or instanceof(o, 'IsoThumpable') then
			o:removeSheetRope(player)
		elseif IsoWindowFrame.isWindowFrame(o) then
			IsoWindowFrame.removeSheetRope(o, player)
		end
	end
end

Commands.object.addSheet = function(player, args)
	local sq = getCell():getGridSquare(args.x, args.y, args.z)
	if sq and args.index >= 0 and args.index < sq:getObjects():size() then
		local o = sq:getObjects():get(args.index)
		if instanceof(o, 'IsoDoor') or instanceof(o, 'IsoWindow') or instanceof(o, 'IsoThumpable') then
			o:addSheet(player)
		else
			noise('expected door/window/thumpable got '..tostring(o))
		end
	else
		noise('sq is null or index is invalid')
	end
end

Commands.object.removeSheet = function(player, args)
	local sq = getCell():getGridSquare(args.x, args.y, args.z)
	if sq and args.index >= 0 and args.index < sq:getObjects():size() then
		local o = sq:getObjects():get(args.index)
		if instanceof(o, 'IsoCurtain') or instanceof(o, 'IsoDoor') then
			o:removeSheet(player)
		else
			noise('expected curtain/door got '..tostring(o))
		end
	else
		noise('sq is null or index is invalid')
	end
end

Commands.object.openCloseCurtain = function(player, args)
	local sq = getCell():getGridSquare(args.x, args.y, args.z)
	if sq and args.index >= 0 and args.index < sq:getObjects():size() then
		local o = sq:getObjects():get(args.index)
		if instanceof(o, 'IsoDoor') then
			noise('isCurtainOpen='..tostring(o:isCurtainOpen()))
			if args.open ~= o:isCurtainOpen() then
				o:toggleCurtain()
			end
		else
			noise('expected door got '..tostring(o))
		end
	else
		noise('sq is null or index is invalid')
	end
end

Commands.object.plumbObject = function(player, args)
	local sq = getCell():getGridSquare(args.x, args.y, args.z)
	if sq and args.index >= 0 and args.index < sq:getObjects():size() then
		local object = sq:getObjects():get(args.index)
		object:getModData().canBeWaterPiped = false
		object:setUsesExternalWaterSource(true)
		object:transmitModData()
		object:sendObjectChange('usesExternalWaterSource', { value = true })
	else
		noise('sq is null or index is invalid')
	end
end

local function _getWallVineObject(square)
	if not square then return end
	for i=0,square:getObjects():size()-1 do
		local object = square:getObjects():get(i);
		local attached = object:getAttachedAnimSprite()
		if attached then
			for n=1,attached:size() do
				local sprite = attached:get(n-1)
--					if sprite and sprite:getParentSprite() and sprite:getParentSprite():getProperties():Is(IsoFlagType.canBeCut) then
				if sprite and sprite:getParentSprite() and sprite:getParentSprite():getName() and luautils.stringStarts(sprite:getParentSprite():getName(), "f_wallvines_") then
					return object, n-1
				end
			end
		end
	end
end

Commands.object.removeBush = function(player, args)
	local sq = getCell():getGridSquare(args.x, args.y, args.z)
	if sq then
		if args.wallVine then
			local object,index = _getWallVineObject(sq)
			if object and index then
				object:RemoveAttachedAnim(index)
				object:transmitUpdatedSpriteToClients()
				sq:removeErosionObject("WallVines")
			end
			-- and the top one, if any
			local topSq = getCell():getGridSquare(sq:getX(), sq:getY(), sq:getZ() + 1)
			local object,index = _getWallVineObject(topSq)
			if object and index then
				object:RemoveAttachedAnim(index)
				object:transmitUpdatedSpriteToClients()
				topSq:removeErosionObject("WallVines")
			end
		else
			for i=0,sq:getObjects():size()-1 do
				local object = sq:getObjects():get(i);
				if object:getProperties():Is(IsoFlagType.canBeCut) then
					sq:transmitRemoveItemFromSquare(object)
					if ZombRand(2) == 0 then
						sq:AddWorldInventoryItem("Base.TreeBranch", 0, 0, 0);
					end
					if ZombRand(1) == 0 then
						sq:AddWorldInventoryItem("Base.Twigs", 0, 0, 0);
					end
					i = i - 1; -- FIXME: illegal in Lua
				end
			end
		end
	else
		noise('sq is null')
	end
end

Commands.object.removeGrass = function(player, args)
	local sq = getCell():getGridSquare(args.x, args.y, args.z)
	if sq then
		for i=sq:getObjects():size(),1,-1 do
			local object = sq:getObjects():get(i-1)
			if object:getProperties():Is(IsoFlagType.canBeRemoved) then
				sq:transmitRemoveItemFromSquare(object)
			end
		end
	else
		noise('sq is null')
	end
end

-- FIXME: duplicates code from ISInventoryBuildMenu.lua
local getShovelObject = function(sq)
	for i=sq:getObjects():size(),1,-1 do
		local obj = sq:getObjects():get(i-1)
		if obj:getSprite() and obj:getSprite():getName() then
			local spriteName = obj:getSprite():getName()
			if spriteName == "floors_exterior_natural_01_13" then
				return obj
			elseif spriteName == "blends_natural_01_0" or
					spriteName == "blends_natural_01_5" or
					spriteName == "blends_natural_01_6" or
					spriteName == "blends_natural_01_7" or
					spriteName == "floors_exterior_natural_01_24" then
				return obj
			elseif luautils.stringStarts(spriteName, "blends_natural") or
					luautils.stringStarts(spriteName, "floors_exterior_natural") then
				return obj
			end
		end
	end
	return nil
end

Commands.object.shovelGround = function(player, args)
	local sq = getCell():getGridSquare(args.x, args.y, args.z)
	if sq then
		local o = getShovelObject(sq)
		if instanceof(o, 'IsoObject') then
			o:setSprite(getSprite("blends_natural_01_64"))
			o:RemoveAttachedAnims() -- remove blend tiles
			o:transmitUpdatedSpriteToClients()
			-- remove vegetation
			for i=sq:getObjects():size(),1,-1 do
				o = sq:getObjects():get(i-1)
				-- FIXME: blends_grassoverlays tiles should have 'vegitation' flag
				if o:getSprite() and (
						o:getSprite():getProperties():Is(IsoFlagType.canBeRemoved) or
						(o:getSprite():getProperties():Is(IsoFlagType.vegitation) and o:getType() ~= IsoObjectType.tree) or
						(o:getSprite():getName() and luautils.stringStarts(o:getSprite():getName(), "blends_grassoverlays"))) then
					sq:transmitRemoveItemFromSquare(o)
				end
			end
			sq:revisionUp()
		else
			noise('expected IsoObject got '..tostring(o))
		end
	else
		noise('sq is null')
	end
end

Commands.object.OnDestroyIsoThumpable = function(player, args)
	local thump = getThumpable(args.x, args.y, args.z, args.index)
	if thump then
--		RainCollectorBarrel.OnDestroyIsoThumpable(thump, nil)
--		TrapSystem.OnDestroyIsoThumpable(thump, nil)
	else
		noise('expected IsoThumpable, got '..tostring(thump))
	end
end

Commands.object.triggerRemote = function(player, args)
	if args.id and args.range then
		IsoTrap.triggerRemote(player, args.id, args.range)
	else
		noise('missing args')
	end
end

local _getBarricadeAble = function(x, y, z, index)
	local sq = getCell():getGridSquare(x, y, z)
	if sq and index >= 0 and index < sq:getObjects():size() then
		o = sq:getObjects():get(index)
		if instanceof(o, 'BarricadeAble') then
			return o
		end
	end
	return nil
end

Commands.object.barricade = function(player, args)
	local object = _getBarricadeAble(args.x, args.y, args.z, args.index)
	if object then
		local barricade = IsoBarricade.AddBarricadeToObject(object, player)
		if barricade then
			if args.isMetal then
				local metal = InventoryItemFactory.CreateItem('Base.SheetMetal')
                metal:setCondition(args.condition)
				barricade:addMetal(player, metal)
				barricade:transmitCompleteItemToClients()
				player:sendObjectChange('removeItemID', { id = args.itemID, type = "Base.SheetMetal" })
				player:sendObjectChange('addXp', { perk = Perks.MetalWelding:index(), xp = 6, noMultiplier = true })
            elseif args.isMetalBar then
                local metal = InventoryItemFactory.CreateItem('Base.MetalBar')
                metal:setCondition(args.condition)
                barricade:addMetalBar(player, metal)
                barricade:transmitCompleteItemToClients()
                player:sendObjectChange('removeItemID', { id = args.itemID, type = "Base.MetalBar" })
				player:sendObjectChange('removeItemType', { type = 'Base.MetalBar', count = 2 })
				player:sendObjectChange('addXp', { perk = Perks.MetalWelding:index(), xp = 6, noMultiplier = true })
			else
				local plank = InventoryItemFactory.CreateItem('Base.Plank')
				plank:setCondition(args.condition)
				barricade:addPlank(player, plank)
				if barricade:getNumPlanks() == 1 then
					barricade:transmitCompleteItemToClients()
				else
					barricade:sendObjectChange('state')
				end
				player:sendObjectChange('removeItemID', { id = args.itemID, type = "Base.Plank" })
				player:sendObjectChange('removeItemType', { type = 'Base.Nails', count = 2 })
				player:sendObjectChange('addXp', { perk = Perks.Woodwork:index(), xp = 3, noMultiplier = true })
			end
		end
	else
		noise('expected BarricadeAble')
	end
end

Commands.object.unbarricade = function(player, args)
	local object = _getBarricadeAble(args.x, args.y, args.z, args.index)
	if object and object:isBarricaded() then
		local barricade = object:getBarricadeForCharacter(player)
		if barricade then
			if barricade:isMetal() then
				local metal = barricade:removeMetal(nil)
				if metal then
					player:sendObjectChange('addItem', { item = metal })
				end
			elseif barricade:isMetalBar() then
				local bar = barricade:removeMetalBar(nil)
				if bar then
					player:sendObjectChange('addItem', { item = bar })
					local bar2 = InventoryItemFactory.CreateItem('Base.MetalBar')
					bar2:setCondition(bar:getCondition())
					player:sendObjectChange('addItem', { item = bar2 })
					local bar3 = InventoryItemFactory.CreateItem('Base.MetalBar')
					bar3:setCondition(bar:getCondition())
					player:sendObjectChange('addItem', { item = bar3 })
				end
			else
				local plank = barricade:removePlank(nil)
				if barricade:getNumPlanks() > 0 then
					barricade:sendObjectChange('state')
				end
				if plank then
					player:sendObjectChange('addItem', { item = plank })
					player:sendObjectChange('addXp', { perk = Perks.Woodwork:index(), xp = 2, noMultiplier = true })
					player:sendObjectChange('addXp', { perk = Perks.Strength:index(), xp = 2 })
				end
			end
		end
	end
end

Commands.object.takeWater = function(player, args)
	-- This takeWater command works for *any* object that the player takes
	-- water from, including sinks and rain barrels.
	local gs = getCell():getGridSquare(args.x, args.y, args.z)
	if gs then
		for i=0,gs:getObjects():size()-1 do
			local obj = gs:getObjects():get(i)
			if obj:useWater(args.units) > 0 then
				obj:transmitModData()
				break
			end
		end
	end
end

Commands.fireplace = {}

local getFireplace = function(x, y, z)
	local gs = getCell():getGridSquare(x, y, z)
	if not gs then return nil end
	for i=0,gs:getObjects():size()-1 do
		local o = gs:getObjects():get(i)
		if o and instanceof(o, 'IsoFireplace') then
			return o
		end
	end
	return nil
end

Commands.fireplace.addFuel = function(player, args)
	local fp = getFireplace(args.x, args.y, args.z)
	if fp then
		fp:addFuel(args.fuelAmt)
		fp:sendObjectChange('state')
		fp:revisionUp()
	end
end

Commands.fireplace.light = function(player, args)
	local fp = getFireplace(args.x, args.y, args.z)
	if fp then
		if args.fuelAmt then
			fp:addFuel(args.fuelAmt)
		end
		if not fp:isLit() and fp:hasFuel() then
			fp:setLit(true)
		end
		fp:sendObjectChange('state')
		fp:revisionUp()
	end
end

Commands.fireplace.extinguish = function(player, args)
	local fp = getFireplace(args.x, args.y, args.z)
	if fp and fp:isLit() then
		fp:extinguish()
		fp:sendObjectChange('state')
		fp:revisionUp()
	end
end

Commands.bbq = {}

local getBarbecue = function(x, y, z)
	local gs = getCell():getGridSquare(x, y, z)
	if not gs then return nil end
	for i=0,gs:getObjects():size()-1 do
		local o = gs:getObjects():get(i)
		if o and instanceof(o, 'IsoBarbecue') then
			return o
		end
	end
	return nil
end

Commands.bbq.insertPropaneTank = function(player, args)
	local bbq = getBarbecue(args.x, args.y, args.z)
	if bbq then
		local tank = bbq:removePropaneTank()
		if tank then
			player:getSquare():AddWorldInventoryItem(tank, 0.5, 0.5, 0)
		end
		tank = InventoryItemFactory.CreateItem("Base.PropaneTank")
		tank:setUsedDelta(args.delta)
		bbq:setPropaneTank(tank)
		bbq:sendObjectChange('state')
		bbq:revisionUp()
	end
end

Commands.bbq.removePropaneTank = function(player, args)
	local bbq = getBarbecue(args.x, args.y, args.z)
	if bbq and bbq:hasPropaneTank() then
		local tank = bbq:removePropaneTank()
		bbq:sendObjectChange('state')
		bbq:revisionUp()
		player:getSquare():AddWorldInventoryItem(tank, 0.5, 0.5, 0)
	end
end

Commands.bbq.toggle = function(player, args)
	local bbq = getBarbecue(args.x, args.y, args.z)
	if bbq and bbq:hasFuel() then
		bbq:toggle()
		bbq:sendObjectChange('state')
		bbq:revisionUp()
	end
end

Commands.bbq.addFuel = function(player, args)
	local bbq = getBarbecue(args.x, args.y, args.z)
	if bbq then
		bbq:addFuel(args.fuelAmt)
		bbq:sendObjectChange('state')
		bbq:revisionUp()
	end
end

Commands.bbq.light = function(player, args)
	local bbq = getBarbecue(args.x, args.y, args.z)
	if bbq and bbq:hasFuel() and not bbq:isLit() then
		bbq:setLit(true)

		bbq:sendObjectChange('state')
		bbq:revisionUp()
	end
end

-- -- -- -- --

local function findCarBatteryCharger(x, y, z)
	local gs = getCell():getGridSquare(x, y, z)
	if not gs then return nil end
	for i=1,gs:getObjects():size() do
		local o = gs:getObjects():get(i-1)
		if o and instanceof(o, 'IsoCarBatteryCharger') then
			return o
		end
	end
	noise('no car-battery charger at '..x..','..y..','..z)
	return nil
end

Commands.carBatteryCharger = {}

Commands.carBatteryCharger.connectBattery = function(player, args)
	local charger = findCarBatteryCharger(args.x, args.y, args.z)
	if not charger then return end
	local battery = charger:getBattery()
	if battery then
		noise('car-battery charger already has a battery')
	else
		player:sendObjectChange('removeItemID', { id = args.battery:getID(), type = args.battery:getFullType() })
		charger:setBattery(args.battery)
		charger:sync()
	end
end

Commands.carBatteryCharger.removeBattery = function(player, args)
	local charger = findCarBatteryCharger(args.x, args.y, args.z)
	if not charger then return end
	local battery = charger:getBattery()
	if battery then
		charger:setBattery(nil)
		charger:sync()
		player:sendObjectChange('addItem', { item = battery })
	else
		noise('car-battery charger does not have a battery')
	end
end

Commands.carBatteryCharger.activate = function(player, args)
	local charger = findCarBatteryCharger(args.x, args.y, args.z)
	if not charger then return end
	charger:setActivated(args.activate)
	charger:sync()
end

Commands.carBatteryCharger.place = function(player, args)
	local square = getCell():getGridSquare(args.x, args.y, args.z)
	for i=1,square:getObjects():size() do
		local o = square:getObjects():get(i-1)
		if o and instanceof(o, 'IsoCarBatteryCharger') then
			noise("can't place more than one car-battery charger on a square")
			return
		end
	end
	player:sendObjectChange('removeItemID', { id = args.item:getID(), type = args.item:getFullType() })
	local charger = IsoCarBatteryCharger.new(args.item, getCell(), square)
	square:AddSpecialObject(charger)
	charger:transmitCompleteItemToClients()
end

Commands.carBatteryCharger.take = function(player, args)
	local charger = findCarBatteryCharger(args.x, args.y, args.z)
	if not charger then return end
	if charger:getBattery() then
		noise('cannot remove car-battery charger connected to a battery')
		return
	end
	local item = charger:getItem()
	if item then
		player:sendObjectChange('addItem', { item = item })
	end
	charger:getSquare():transmitRemoveItemFromSquare(charger)
end

-- -- -- -- --

Commands.player = {}
Commands.player.wakeOther = function(player, args)
	local otherPlayer = getPlayerByOnlineID(args.id)
	if otherPlayer then
		otherPlayer:sendObjectChange("wakeUp")
	end
end

Commands.erosion = {};
Commands.erosion.disableForSquare = function(player, args)
    local sq = getCell():getGridSquare(args.x, args.y, args.z);
    if sq ~= nil then
        sq:disableErosion();
    end
end

ClientCommands.OnClientCommand = function(module, command, player, args)
	if Commands[module] and Commands[module][command] then
		local argStr = ''
		for k,v in pairs(args) do argStr = argStr..' '..k..'='..tostring(v) end
		noise('received '..module..' '..command..' '..tostring(player)..argStr)
		Commands[module][command](player, args)
	end
end

Events.OnClientCommand.Add(ClientCommands.OnClientCommand)
