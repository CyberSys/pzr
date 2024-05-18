--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

ISFireplaceMenu = {}

function ISFireplaceMenu.OnFillWorldObjectContextMenu(player, context, worldobjects, test)

	if test and ISWorldObjectContextMenu.Test then return true end

	local fireplace = nil

	local objects = {}
	for _,object in ipairs(worldobjects) do
		local square = object:getSquare()
		if square then
			for i=1,square:getObjects():size() do
				local object2 = square:getObjects():get(i-1)
				if instanceof(object2, "IsoFireplace") then
					fireplace = object2
				end
			end
		end
	end

	if not fireplace then return end

	local playerObj = getSpecificPlayer(player)
	local playerInv = playerObj:getInventory()
	local lighter = nil
	local matches = nil
	local literature = playerInv:FindAndReturnCategory("Literature")
	local petrol = nil
	local percedWood = nil
	local branch = nil
	local stick = nil

	-- check the player inventory to add some fuel (logs, planks, books..)
	local fuelList = {}
	local lightWithList = {}
	local fuelAmtList = {}
	local itemCount = {}
	local containers = ISInventoryPaneContextMenu.getContainers(playerObj)
	for i=1,containers:size() do
		local container = containers:get(i-1)
		for j=1,container:getItems():size() do
			local vItem = container:getItems():get(j-1)
			local type = vItem:getType()
			if type == "Lighter" then
				lighter = lighter or vItem
			elseif type == "Matches" then
				matches = matches or vItem
			elseif type == "PetrolCan" then
				petrol = petrol or vItem
			elseif type == "PercedWood" then
				percedWood = percedWood or vItem
			elseif type == "TreeBranch" then
				branch = branch or vItem
			elseif type == "WoodenStick" then
				stick = stick or vItem
			end
			if not playerObj:isEquippedClothing(vItem) then
				if not itemCount[vItem:getName()] then
					if campingFuelType[vItem:getType()] then
						if campingFuelType[vItem:getType()] > 0 then
							table.insert(fuelList, vItem)
						end
					elseif campingFuelCategory[vItem:getCategory()] then
						table.insert(fuelList, vItem)
					end
					if campingLightFireType[vItem:getType()] then
						if campingLightFireType[vItem:getType()] > 0 then
							table.insert(lightWithList, vItem)
							table.insert(fuelAmtList, campingLightFireType[vItem:getType()])
						end
					elseif campingLightFireCategory[vItem:getCategory()] then
						table.insert(lightWithList, vItem)
						table.insert(fuelAmtList, campingLightFireCategory[vItem:getCategory()])
					end
					itemCount[vItem:getName()] = 0
				end
				itemCount[vItem:getName()] = itemCount[vItem:getName()] + 1
			end
		end
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
			subMenuFuel:addOption(label, worldobjects, ISFireplaceMenu.onAddFuel, v, player, fireplace)
		end
	end

	-- Options for lighting a fire
	local lightFromItem = nil
	local lightFromPetrol = nil
	local lightFromKindle = nil
	if #lightWithList > 0 and (lighter or matches) and not fireplace:isLit() then
		lightFromItem = fireplace
	end
	if (lighter or matches) and petrol and not fireplace:isLit() and fireplace:hasFuel() then
		lightFromPetrol = fireplace
	end
	if percedWood and not fireplace:isLit() and fireplace:hasFuel() and playerObj:getStats():getEndurance() > 0 then
		lightFromKindle = fireplace
	end
	if lightFromPetrol or lightFromKindle or lightFromItem then
		if test then return ISWorldObjectContextMenu.setTest() end
		local lightOption = context:addOption(campingText.lightCampfire, worldobjects, nil)
		local subMenuLight = ISContextMenu:getNew(context)
		context:addSubMenu(lightOption, subMenuLight)
		if lightFromPetrol then
			if lighter then
				subMenuLight:addOption(petrol:getName()..' + '..lighter:getName(), worldobjects, ISFireplaceMenu.onLightFromPetrol, player, lighter, petrol, lightFromPetrol)
			end
			if matches then
				subMenuLight:addOption(petrol:getName()..' + '..matches:getName(), worldobjects, ISFireplaceMenu.onLightFromPetrol, player, matches, petrol, lightFromPetrol)
			end
		end
		for i,v in ipairs(lightWithList) do
			local label = v:getName()
			local count = itemCount[v:getName()]
			if count > 1 then
				label = label..' ('..count..')'
			end
			if lighter then
				subMenuLight:addOption(label..' + '..lighter:getName(), worldobjects, ISFireplaceMenu.onLightFromLiterature, player, v, lighter, lightFromItem, fuelAmtList[i])
			end
			if matches then
				subMenuLight:addOption(label..' + '..matches:getName(), worldobjects, ISFireplaceMenu.onLightFromLiterature, player, v, matches, lightFromItem, fuelAmtList[i])
			end
		end
		if lightFromKindle then
			if stick then
				subMenuLight:addOption(percedWood:getName()..' + '..stick:getName(), worldobjects, ISFireplaceMenu.onLightFromKindle, player, percedWood, stick, lightFromKindle)
			elseif branch then
				subMenuLight:addOption(percedWood:getName()..' + '..branch:getName(), worldobjects, ISFireplaceMenu.onLightFromKindle, player, percedWood, branch, lightFromKindle)
			else
				local option = subMenuLight:addOption(percedWood:getName(), worldobjects, nil);
				option.notAvailable = true;
				local tooltip = ISWorldObjectContextMenu.addToolTip()
				tooltip:setName(percedWood:getName())
				tooltip.description = getText("Tooltip_lightFireNoStick")
				option.toolTip = tooltip
			end
		end
	end

	if fireplace:isLit() then
		if test then return ISWorldObjectContextMenu.setTest() end
		context:addOption(campingText.putOutCampfire, worldobjects, ISFireplaceMenu.onExtinguish, player, fireplace)
	end
end

function ISFireplaceMenu.toPlayerInventory(playerObj, item)
    if item:getContainer() ~= playerObj:getInventory() then
        local action = ISInventoryTransferAction:new(playerObj, item, item:getContainer(), playerObj:getInventory())
        ISTimedActionQueue.add(action)
    end
end

function ISFireplaceMenu.onAddFuel(worldobjects, fuelType, player, fireplace)
	local fuelAmt = campingFuelType[fuelType:getType()] or campingFuelCategory[fuelType:getCategory()]
	if not fuelAmt or fuelAmt < 0 then return end
	local playerObj = getSpecificPlayer(player)
	ISFireplaceMenu.toPlayerInventory(playerObj, fuelType)
	if luautils.walkAdj(playerObj, fireplace:getSquare(), true) then
		if playerObj:isEquipped(fuelType) then
			ISTimedActionQueue.add(ISUnequipAction:new(playerObj, fuelType, 50));
		end
		ISTimedActionQueue.add(ISFireplaceAddFuel:new(playerObj, fireplace, fuelType, fuelAmt * 60, 100))
	end
end

function ISFireplaceMenu.onLightFromLiterature(worldobjects, player, literature, lighter, fireplace, fuelAmt)
	local playerObj = getSpecificPlayer(player)
	ISFireplaceMenu.toPlayerInventory(playerObj, literature)
	ISFireplaceMenu.toPlayerInventory(playerObj, lighter)
	if luautils.walkAdj(playerObj, fireplace:getSquare(), true) then
		if playerObj:isEquipped(literature) then
			ISTimedActionQueue.add(ISUnequipAction:new(playerObj, literature, 50));
		end
		ISTimedActionQueue.add(ISFireplaceLightFromLiterature:new(playerObj, literature, lighter, fireplace, fuelAmt, 100))
	end
end

function ISFireplaceMenu.onLightFromPetrol(worldobjects, player, lighter, petrol, fireplace)
	local playerObj = getSpecificPlayer(player)
	ISFireplaceMenu.toPlayerInventory(playerObj, lighter)
	ISFireplaceMenu.toPlayerInventory(playerObj, petrol)
	if luautils.walkAdj(playerObj, fireplace:getSquare(), true) then
		ISTimedActionQueue.add(ISFireplaceLightFromPetrol:new(playerObj, fireplace, lighter, petrol, 20))
	end
end

function ISFireplaceMenu.onLightFromKindle(worldobjects, player, percedWood, stickOrBranch, fireplace)
	local playerObj = getSpecificPlayer(player)
	ISFireplaceMenu.toPlayerInventory(playerObj, percedWood)
	ISFireplaceMenu.toPlayerInventory(playerObj, stickOrBranch)
	if luautils.walkAdj(playerObj, fireplace:getSquare(), true) then
		ISTimedActionQueue.add(ISFireplaceLightFromKindle:new(playerObj, percedWood, stickOrBranch, fireplace, 1500))
	end
end

function ISFireplaceMenu.onExtinguish(worldobjects, player, fireplace)
	local playerObj = getSpecificPlayer(player)
	if luautils.walkAdj(playerObj, fireplace:getSquare()) then
		ISTimedActionQueue.add(ISFireplaceExtinguish:new(playerObj, fireplace, 60))
	end
end

Events.OnFillWorldObjectContextMenu.Add(ISFireplaceMenu.OnFillWorldObjectContextMenu)

