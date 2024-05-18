--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require 'Camping/camping_fuel'

ISBBQMenu = {}

local function timeString(timeInMinutes)
	local hourStr = getText("IGUI_Gametime_hour")
	local minuteStr = getText("IGUI_Gametime_minute")
	local hours = math.floor(timeInMinutes / 60)
	local minutes = timeInMinutes % 60
	if hours ~= 1 then hourStr = getText("IGUI_Gametime_hours") end
	if minutes ~= 1 then minuteStr = getText("IGUI_Gametime_minutes") end
	local str = ""
	if hours ~= 0 then
		str = hours .. ' ' .. hourStr
	end
	if str == '' or minutes ~= 0 then
		if str ~= '' then str = str .. ', ' end
		str = str .. minutes .. ' ' .. minuteStr
	end
	return str
end

function ISBBQMenu.OnFillWorldObjectContextMenu(player, context, worldobjects, test)

	if test and ISWorldObjectContextMenu.Test then return true end

	local bbq = nil

	local objects = {}
	for _,object in ipairs(worldobjects) do
		local square = object:getSquare()
		if square then
			for i=1,square:getObjects():size() do
				local object2 = square:getObjects():get(i-1)
				if instanceof(object2, "IsoBarbecue") then
					bbq = object2
				end
			end
		end
	end

	if not bbq then return end

	local playerObj = getSpecificPlayer(player)
	local playerInv = playerObj:getInventory()

	if test then return ISWorldObjectContextMenu.setTest() end
	local option = context:addOption(getText("ContextMenu_BBQInfo"), worldobjects, ISBBQMenu.onDisplayInfo, player, bbq)
	if playerObj:DistToSquared(bbq:getX() + 0.5, bbq:getY() + 0.5) < 2 * 2 then
		option.toolTip = ISToolTip:new()
		option.toolTip:initialise()
		option.toolTip:setVisible(false)
		option.toolTip:setName(bbq:isPropaneBBQ() and getText("IGUI_BBQ_TypePropane") or getText("IGUI_BBQ_TypeCharcoal"))
		option.toolTip.description = getText("IGUI_BBQ_FuelAmount", timeString(bbq:getFuelAmount()))
		if bbq:isPropaneBBQ() and not bbq:hasPropaneTank() then
			option.toolTip.description = option.toolTip.description .. " <LINE> <RGB:1,0,0> " .. getText("IGUI_BBQ_NeedsPropaneTank")
		end
	end

	if bbq:isPropaneBBQ() then
		if bbq:hasFuel() then
			if test then return ISWorldObjectContextMenu.setTest() end
			if bbq:isLit() then
				context:addOption(getText("ContextMenu_Turn_Off"), worldobjects, ISBBQMenu.onToggle, player, bbq)
			else
				context:addOption(getText("ContextMenu_Turn_On"), worldobjects, ISBBQMenu.onToggle, player, bbq)
			end
		end
		local tank = ISBBQMenu.FindPropaneTank(playerObj, bbq)
		if tank then
			if test then return ISWorldObjectContextMenu.setTest() end
			context:addOption(getText("ContextMenu_Insert_Propane_Tank"), worldobjects, ISBBQMenu.onInsertPropaneTank, player, bbq, tank)
		end
		if bbq:hasPropaneTank() then
			if test then return ISWorldObjectContextMenu.setTest() end
			context:addOption(getText("ContextMenu_Remove_Propane_Tank"), worldobjects, ISBBQMenu.onRemovePropaneTank, player, bbq)
		end
		return
	end

	-- Options for adding fuel
	local fuelList = {}
	local literatureList = {}
	local itemCount = {}
	for i = 0, playerInv:getItems():size() - 1 do
		local vItem = playerInv:getItems():get(i)
		if not itemCount[vItem:getName()] then
			if vItem:getType() == "Charcoal" then
				table.insert(fuelList, vItem)
			end

			if vItem:getType() == "UnusableWood" then
				table.insert(fuelList, vItem)
			end

			if vItem:getCategory() == "Literature" then
				table.insert(literatureList, vItem)
				table.insert(fuelList, vItem)
			end

			if campingFuelType[vItem:getType()] then
				if (campingFuelType[vItem:getType()] > 0) then
					table.insert(fuelList, vItem)
				end
			end

			if vItem:getCategory() == "Clothing" then
				if not playerObj:isEquippedClothing(vItem) then
					if campingFuelCategory[vItem:getCategory()] then
						if (campingFuelCategory[vItem:getCategory()] > 0) then
							table.insert(fuelList, vItem)
						end
					end
				end
			end

			itemCount[vItem:getName()] = 0
		end
		itemCount[vItem:getName()] = itemCount[vItem:getName()] + 1
	end

	if #fuelList > 0 then
		if test then return ISWorldObjectContextMenu.setTest() end
		local fuelOption = context:addOption(campingText.addFuel, worldobjects, nil)
		local subMenuFuel = ISContextMenu:getNew(context)
		context:addSubMenu(fuelOption, subMenuFuel)
		for i,v in pairs(fuelList) do
			local label = v:getName()
			local count = itemCount[v:getName()]
			if count > 1 then
				label = label..' ('..count..')'
			end
			subMenuFuel:addOption(label, worldobjects, ISBBQMenu.onAddFuel, v, player, bbq)
		end
	end

	-- Options for lighting
	local lighter = playerInv:FindAndReturn("Lighter")
	local matches = playerInv:FindAndReturn("Matches")
	local literature = playerInv:FindAndReturnCategory("Literature")
	local petrol = playerInv:FindAndReturn("PetrolCan") -- FIXME: require non-empty
	
	local lightFromLiterature = nil
	local lightFromPetrol = nil
	if literature and (lighter or matches) and not bbq:isLit() then
		lightFromLiterature = bbq
	end
	if (lighter or matches) and petrol and not bbq:isLit() and bbq:hasFuel() then
		lightFromPetrol = bbq
	end
	if lightFromPetrol or lightFromLiterature then
		if test then return ISWorldObjectContextMenu.setTest() end
		local lightOption = context:addOption(campingText.lightCampfire, worldobjects, nil)
		local subMenuLight = ISContextMenu:getNew(context)
		context:addSubMenu(lightOption, subMenuLight)
		if lightFromPetrol then
			if lighter then
				subMenuLight:addOption(petrol:getName()..' + '..lighter:getName(), worldobjects, ISBBQMenu.onLightFromPetrol, player, lighter, petrol, lightFromPetrol)
			end
			if matches then
				subMenuLight:addOption(petrol:getName()..' + '..matches:getName(), worldobjects, ISBBQMenu.onLightFromPetrol, player, matches, petrol, lightFromPetrol)
			end
		end
		for i,v in pairs(literatureList) do
			local label = v:getName()
			local count = itemCount[v:getName()]
			if count > 1 then
				label = label..' ('..count..')'
			end
			if lighter then
				subMenuLight:addOption(label..' + '..lighter:getName(), worldobjects, ISBBQMenu.onLightFromLiterature, player, v, lighter, lightFromLiterature)
			end
			if matches then
				subMenuLight:addOption(label..' + '..matches:getName(), worldobjects, ISBBQMenu.onLightFromLiterature, player, v, matches, lightFromLiterature)
			end
		end
	end

	if bbq:isLit() then
		if test then return ISWorldObjectContextMenu.setTest() end
--		context:addOption(campingText.putOutCampfire, worldobjects, ISBBQMenu.onExtinguish, player, bbq)
	end
end

function ISBBQMenu.onDisplayInfo(worldobjects, player, bbq)
	local playerObj = getSpecificPlayer(player)
	if not AdjacentFreeTileFinder.isTileOrAdjacent(playerObj:getCurrentSquare(), bbq:getSquare()) then
		local adjacent = AdjacentFreeTileFinder.Find(bbq:getSquare(), playerObj)
		if adjacent then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(playerObj, adjacent))
			ISTimedActionQueue.add(ISBBQInfoAction:new(playerObj, bbq))
			return
		end
	else
		ISTimedActionQueue.add(ISBBQInfoAction:new(playerObj, bbq))
	end
end

function ISBBQMenu.FindPropaneTank(player, bbq)
	local tank = player:getInventory():FindAndReturn("Base.PropaneTank")
	if tank and tank:getUsedDelta() > 0 then
		return tank
	end
	for y=bbq:getY()-1,bbq:getY()+1 do
		for x=bbq:getX()-1,bbq:getX()+1 do
			local square = getCell():getGridSquare(x, y, bbq:getZ())
			if square and not square:isSomethingTo(bbq:getSquare()) then
				local wobs = square:getWorldObjects()
				for i=0,wobs:size()-1 do
					local o = wobs:get(i)
					if o:getItem():getFullType() == "Base.PropaneTank" then
						if o:getItem():getUsedDelta() > 0 then
							return o
						end
					end
				end
			end
		end
	end
	return nil
end

function ISBBQMenu.onAddFuel(worldobjects, fuelType, player, bbq)
	local fuelAmt = 30 -- minutes
	if not fuelAmt or fuelAmt < 0 then return end

	local playerObj = getSpecificPlayer(player)

	if (campingFuelType[fuelType:getType()]) then
		if (campingFuelType[fuelType:getType()] > 0) then
			fuelAmt = campingFuelType[fuelType:getType()] * 60;
		end
	end



	if campingFuelCategory[fuelType:getCategory()] then
		if campingFuelCategory[fuelType:getCategory()] > 0 then
			fuelAmt = campingFuelCategory[fuelType:getCategory()] * 60
		end
	end

	if luautils.walkAdj(playerObj, bbq:getSquare()) then
		ISTimedActionQueue.add(ISBBQAddFuel:new(playerObj, bbq, fuelType, fuelAmt, 100))
	end
end

function ISBBQMenu.onLightFromLiterature(worldobjects, player, literature, lighter, bbq)
	local playerObj = getSpecificPlayer(player)
	if luautils.walkAdj(playerObj, bbq:getSquare()) then
		ISTimedActionQueue.add(ISBBQLightFromLiterature:new(playerObj, literature, lighter, bbq, 100))
	end
end

function ISBBQMenu.onLightFromPetrol(worldobjects, player, lighter, petrol, bbq)
	local playerObj = getSpecificPlayer(player)
	if luautils.walkAdj(playerObj, bbq:getSquare()) then
		ISTimedActionQueue.add(ISBBQLightFromPetrol:new(playerObj, bbq, lighter, petrol, 20))
	end
end

function ISBBQMenu.onExtinguish(worldobjects, player, bbq)
	local playerObj = getSpecificPlayer(player)
	if luautils.walkAdj(playerObj, bbq:getSquare()) then
		ISTimedActionQueue.add(ISBBQExtinguish:new(playerObj, bbq, 60))
	end
end

function ISBBQMenu.onInsertPropaneTank(worldobjects, player, bbq, tank)
	local playerObj = getSpecificPlayer(player)
	local square = bbq:getSquare()
	if instanceof(tank, "IsoWorldInventoryObject") then
		if playerObj:getSquare() ~= tank:getSquare() then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(playerObj, tank:getSquare()))
		end
		ISTimedActionQueue.add(ISBBQInsertPropaneTank:new(playerObj, bbq, tank, 100))
	elseif luautils.walkAdj(playerObj, square) then
		ISTimedActionQueue.add(ISBBQInsertPropaneTank:new(playerObj, bbq, tank, 100))
	end
end

function ISBBQMenu.onRemovePropaneTank(worldobjects, player, bbq, tank)
	local playerObj = getSpecificPlayer(player)
	if luautils.walkAdj(playerObj, bbq:getSquare()) then
		ISTimedActionQueue.add(ISBBQRemovePropaneTank:new(playerObj, bbq, 100))
	end
end

function ISBBQMenu.onToggle(worldobjects, player, bbq, tank)
	local playerObj = getSpecificPlayer(player)
	if luautils.walkAdj(playerObj, bbq:getSquare()) then
		ISTimedActionQueue.add(ISBBQToggle:new(playerObj, bbq, 50))
	end
end

Events.OnFillWorldObjectContextMenu.Add(ISBBQMenu.OnFillWorldObjectContextMenu)

