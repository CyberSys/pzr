--***********************************************************
--**                    ROBERT JOHNSON                     **
--**   Contextual inventory menu for all the camping stuff **
--***********************************************************

require 'Camping/CCampfireSystem'
require 'Camping/camping_fuel'
require 'Camping/camping_tent'

ISCampingMenu = {};
ISCampingMenu.currentSquare = nil;
ISCampingMenu.campfire = nil;
ISCampingMenu.tent = nil;

ISCampingMenu.doCampingMenu = function(player, context, worldobjects, test)

	if test and ISWorldObjectContextMenu.Test then return true end

	local playerObj = getSpecificPlayer(player)
	
	local makeCampfire = false;
	local addFuel = nil
	local addPetrol = nil
	local lightFromPetrol = nil
	local addTent = false;
	local litCampfire = nil
	local removeCampfire = nil
	local removeTent = nil
	local sleep = nil
	local lightFromKindle = nil
	local lightFromLiterature = nil
	local playerInv = getSpecificPlayer(player):getInventory();
	local lighter = nil
	local matches = nil
	local petrol = nil
	local percedWood = nil
    local branch = nil
	local stick = nil
	local campfireKit = nil
	local tentKit = nil

	local fuelList = {}
	local lightFireList = {}
	local fuelAmtList = {}
	local itemCount = {}

	local containers = ISInventoryPaneContextMenu.getContainers(playerObj)
	for i=1,containers:size() do
		local container = containers:get(i-1)
		for j=1,container:getItems():size() do
			local item = container:getItems():get(j-1)
			local type = item:getType()
			if type == "Lighter" then
				lighter = item
			elseif type == "Matches" then
				matches = item
			elseif type == "PetrolCan" then
				petrol = item
			elseif type == "PercedWood" then
				percedWood = item
			elseif type == "TreeBranch" then
				branch = item
			elseif type == "WoodenStick" then
				stick = item
			elseif type == "CampfireKit" then
				campfireKit = item
			elseif type == "CampingTentKit" then
				tentKit = item
			end

			-- check the player inventory to add some fuel (logs, planks, books..)
			local category = item:getCategory()
			if not playerObj:isEquippedClothing(item) and
					(campingFuelType[type] or campingFuelCategory[category] or
					campingLightFireType[type] or campingLightFireCategory[category]) then
				if not itemCount[item:getName()] then
					if campingFuelType[type] then
						if campingFuelType[type] > 0 then
							table.insert(fuelList, item)
						end
					elseif campingFuelCategory[category] then
						table.insert(fuelList, item)
					end
					if campingLightFireType[type] then
						if campingLightFireType[type] > 0 then
							table.insert(lightFireList, item)
							table.insert(fuelAmtList, campingLightFireType[type])
						end
					elseif campingLightFireCategory[category] then
						table.insert(lightFireList, item)
						table.insert(fuelAmtList, campingLightFireCategory[category])
					end
					itemCount[item:getName()] = 0
				end
				itemCount[item:getName()] = itemCount[item:getName()] + 1
			end
		end
	end
	
	for i,v in ipairs(worldobjects) do
		ISCampingMenu.campfire = CCampfireSystem.instance:getLuaObjectOnSquare(v:getSquare())
		ISCampingMenu.tent = camping.getCurrentTent(v:getSquare());
		local campfire = ISCampingMenu.campfire
		-- we have to be outside
--~ 		if (v:getSquare():getProperties():Is(IsoFlagType.exterior)) then
			ISCampingMenu.currentSquare = v:getSquare();
			if campfireKit and ISCampingMenu.campfire == nil and ISCampingMenu.tent == nil then
				makeCampfire = true;
			end
			if ISCampingMenu.campfire ~= nil then
				if campfire.isLit then
					litCampfire = campfire
				end
				addFuel = campfire
				removeCampfire = campfire
			end
			if (lighter or matches) and petrol and campfire and
					not campfire.isLit and
					campfire.fuelAmt > 0 then
				lightFromPetrol = campfire
			end
			if tentKit and ISCampingMenu.campfire == nil and ISCampingMenu.tent == nil then
				addTent = true;
			end
			if percedWood and campfire and not campfire.isLit and campfire.fuelAmt > 0 and getSpecificPlayer(player):getStats():getEndurance() > 0 then
				lightFromKindle = campfire
			end
			if (lighter or matches) and campfire ~= nil and not campfire.isLit then
				lightFromLiterature = campfire
			end
			if ISCampingMenu.tent ~= nil then
				removeTent = ISCampingMenu.tent
			end
			if ISCampingMenu.tent ~= nil then
				sleep = ISCampingMenu.tent
			end
--~ 			break;
--~ 		end
	end

	if #fuelList > 0 and addFuel then
		if test then return ISWorldObjectContextMenu.setTest() end
		local fuelOption = context:addOption(campingText.addFuel, worldobjects, nil);
		local subMenuFuel = ISContextMenu:getNew(context);
		context:addSubMenu(fuelOption, subMenuFuel);
		for i,v in pairs(fuelList) do
			local label = v:getName()
			local count = itemCount[v:getName()]
			if count > 1 then
				label = label..' ('..count..')'
			end
			subMenuFuel:addOption(label, worldobjects, ISCampingMenu.onAddFuel, v, player, addFuel);
		end
	end

	if makeCampfire then
		if test then return ISWorldObjectContextMenu.setTest() end
		context:addOption(campingText.placeCampfire, worldobjects, ISCampingMenu.onPlaceCampfire, player, campfireKit);
	end
--[[
	if addPetrol then
		if test then return ISWorldObjectContextMenu.setTest() end
		context:addOption(campingText.addPetrol, worldobjects, ISCampingMenu.onAddPetrol, player, petrol, addPetrol);
	end
]]--
	if addTent then
		if test then return ISWorldObjectContextMenu.setTest() end
		context:addOption(campingText.addTent, worldobjects, ISCampingMenu.onAddTent, player, tentKit);
	end
	if removeTent then
		if test then return ISWorldObjectContextMenu.setTest() end
		context:addOption(campingText.removeTent, worldobjects, ISCampingMenu.onRemoveTent, player, removeTent);
	end
	if sleep then
        if not isClient() or getServerOptions():getBoolean("SleepAllowed") then
            if test then return ISWorldObjectContextMenu.setTest() end
    --		context:addOption(campingText.sleepInTent, worldobjects, ISCampingMenu.onSleep, player, sleep);
            ISCampingMenu.doSleepOption(context, sleep, player, getSpecificPlayer(player));
            if getSpecificPlayer(player):getStats():getEndurance() < 0.75 then
                context:addOption(getText("ContextMenu_Rest"), worldobjects, ISCampingMenu.onRest, player, sleep);
            end
        end
	end
	if lightFromPetrol or lightFromKindle or (lightFromLiterature and #lightFireList > 0) then
		if test then return ISWorldObjectContextMenu.setTest() end
		local lightOption = context:addOption(campingText.lightCampfire, worldobjects, nil);
		local subMenuLight = ISContextMenu:getNew(context);
		context:addSubMenu(lightOption, subMenuLight);
		if lightFromPetrol then
			if lighter then
				subMenuLight:addOption(petrol:getName()..' + '..lighter:getName(), worldobjects, ISCampingMenu.onLightFromPetrol, player, lighter, petrol, lightFromPetrol)
			end
			if matches then
				subMenuLight:addOption(petrol:getName()..' + '..matches:getName(), worldobjects, ISCampingMenu.onLightFromPetrol, player, matches, petrol, lightFromPetrol)
			end
		end
		for i,v in pairs(lightFireList) do
			local label = v:getName()
			local count = itemCount[v:getName()]
			local fuelAmt = fuelAmtList[i]
			if count > 1 then
				label = label..' ('..count..')'
			end
			if lighter then
				subMenuLight:addOption(label..' + '..lighter:getName(), worldobjects, ISCampingMenu.onLightFromLiterature, player, v, lighter, lightFromLiterature, fuelAmt)
			end
			if matches then
				subMenuLight:addOption(label..' + '..matches:getName(), worldobjects, ISCampingMenu.onLightFromLiterature, player, v, matches, lightFromLiterature, fuelAmt)
			end
		end
		if lightFromKindle then
			if stick then
				subMenuLight:addOption(percedWood:getName()..' + '..stick:getName(), worldobjects, ISCampingMenu.onLightFromKindle, player, percedWood, stick, lightFromKindle);
			elseif branch then
				subMenuLight:addOption(percedWood:getName()..' + '..branch:getName(), worldobjects, ISCampingMenu.onLightFromKindle, player, percedWood, branch, lightFromKindle);
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
	if litCampfire then
		if test then return ISWorldObjectContextMenu.setTest() end
		context:addOption(campingText.putOutCampfire, worldobjects, ISCampingMenu.onPutOutCampfire, player, litCampfire)
	end
	if removeCampfire then
		if test then return ISWorldObjectContextMenu.setTest() end
		context:addOption(campingText.removeCampfire, worldobjects, ISCampingMenu.onRemoveCampfire, player, removeCampfire);
	end
end

function ISCampingMenu.toPlayerInventory(playerObj, item)
    if item:getContainer() ~= playerObj:getInventory() then
        local action = ISInventoryTransferAction:new(playerObj, item, item:getContainer(), playerObj:getInventory())
        ISTimedActionQueue.add(action)
    end
end

ISCampingMenu.doSleepOption = function(context, bed, player, playerObj)
    local sleepOption = context:addOption(getText("ContextMenu_Sleep"), bed, ISWorldObjectContextMenu.onSleep, player);
    -- Not tired enough
    if playerObj:getStats():getFatigue() <= 0.3 then
        sleepOption.notAvailable = true;
        local tooltip = ISWorldObjectContextMenu.addToolTip();
        tooltip:setName(getText("ContextMenu_Sleeping"));
        tooltip.description = getText("IGUI_Sleep_NotTiredEnough");
        sleepOption.toolTip = tooltip;
    end
    -- Sleeping pills counter those sleeping problems
    if playerObj:getSleepingTabletEffect() < 2000 then
        -- In pain, can still sleep if really tired
        if playerObj:getMoodles():getMoodleLevel(MoodleType.Pain) >= 2 and playerObj:getStats():getFatigue() <= 0.85 then
            sleepOption.notAvailable = true;
            local tooltip = ISWorldObjectContextMenu.addToolTip();
            tooltip:setName(getText("ContextMenu_Sleeping"));
            tooltip.description = getText("ContextMenu_PainNoSleep");
            sleepOption.toolTip = tooltip;
            -- In panic
        elseif playerObj:getMoodles():getMoodleLevel(MoodleType.Panic) >= 1 then
            sleepOption.notAvailable = true;
            local tooltip = ISWorldObjectContextMenu.addToolTip();
            tooltip:setName(getText("ContextMenu_Sleeping"));
            tooltip.description = getText("ContextMenu_PanicNoSleep");
            sleepOption.toolTip = tooltip;
            -- tried to sleep not so long ago
        elseif (playerObj:getHoursSurvived() - playerObj:getLastHourSleeped()) <= 1 then
            sleepOption.notAvailable = true;
            local sleepTooltip = ISWorldObjectContextMenu.addToolTip();
            sleepTooltip:setName(getText("ContextMenu_Sleeping"));
            sleepTooltip.description = getText("ContextMenu_NoSleepTooEarly");
            sleepOption.toolTip = sleepTooltip;
        end
    end
end

ISCampingMenu.onAddFuel = function(worldobjects, fuelItem, player, campfire)
	local fuelAmt = campingFuelType[fuelItem:getType()] or campingFuelCategory[fuelItem:getCategory()]
	if not fuelAmt or fuelAmt < 0 then return end
	local playerObj = getSpecificPlayer(player)
	ISCampingMenu.toPlayerInventory(playerObj, fuelItem)
	if luautils.walkAdj(playerObj, campfire:getSquare(), true) then
		if playerObj:isEquipped(fuelItem) then
			ISTimedActionQueue.add(ISUnequipAction:new(playerObj, fuelItem, 50));
		end
		ISTimedActionQueue.add(ISAddFuelAction:new(playerObj, campfire, fuelItem, fuelAmt * 60, 100));
	end
--	ISTimedActionQueue.add(ISInventoryTransferAction:new(getSpecificPlayer(player), fuelType, getSpecificPlayer(player):getInventory(), campfire.tile:getItemContainer()));
end

ISCampingMenu.onPlaceCampfire = function(worldobjects, player, campfireKit)
	local playerObj = getSpecificPlayer(player)
	ISCampingMenu.toPlayerInventory(playerObj, campfireKit)
	local bo = campingCampfire:new(getSpecificPlayer(player))
	bo.player = player
	getCell():setDrag(bo, player)
end

ISCampingMenu.onPutOutCampfire = function(worldobjects, player, campfire)
	local playerObj = getSpecificPlayer(player)
	if luautils.walkAdj(playerObj, campfire:getSquare()) then
		ISTimedActionQueue.add(ISPutOutCampfireAction:new(playerObj, campfire, 60));
	end
end

ISCampingMenu.onRemoveCampfire = function(worldobjects, player, campfire)
	local playerObj = getSpecificPlayer(player)
	if luautils.walkAdj(playerObj, campfire:getSquare()) then
		ISTimedActionQueue.add(ISRemoveCampfireAction:new(playerObj, campfire, 60));
	end
end

ISCampingMenu.onLightFromLiterature = function(worldobjects, player, literature, lighter, campfire, fuelAmt)
	local playerObj = getSpecificPlayer(player)
	ISCampingMenu.toPlayerInventory(playerObj, literature)
	ISCampingMenu.toPlayerInventory(playerObj, lighter)
	if luautils.walkAdj(playerObj, campfire:getSquare(), true) then
		if playerObj:isEquipped(literature) then
			ISTimedActionQueue.add(ISUnequipAction:new(playerObj, literature, 50));
		end
		ISTimedActionQueue.add(ISLightFromLiterature:new(playerObj, literature, lighter, campfire, fuelAmt, 100));
	end
end

ISCampingMenu.onLightFromKindle = function(worldobjects, player, percedWood, stickOrBranch, campfire)
	local playerObj = getSpecificPlayer(player)
	ISCampingMenu.toPlayerInventory(playerObj, percedWood)
	ISCampingMenu.toPlayerInventory(playerObj, stickOrBranch)
	if luautils.walkAdj(playerObj, campfire:getSquare(), true) then
		ISTimedActionQueue.add(ISLightFromKindle:new(playerObj, percedWood, stickOrBranch, campfire, 1500));
	end
end
--[[
ISCampingMenu.onAddPetrol = function(worldobjects, player, petrol, campfire)
	local playerObj = getSpecificPlayer(player)
	ISCampingMenu.toPlayerInventory(playerObj, petrol)
	if luautils.walkAdj(playerObj, campfire:getSquare(), true) then
		ISTimedActionQueue.add(ISAddPetrolAction:new(playerObj, campfire, petrol, 10));
	end
end
]]--
ISCampingMenu.onLightFromPetrol = function(worldobjects, player, lighter, petrol, campfire)
	local playerObj = getSpecificPlayer(player)
	ISCampingMenu.toPlayerInventory(playerObj, lighter)
	ISCampingMenu.toPlayerInventory(playerObj, petrol)
	if luautils.walkAdj(playerObj, campfire:getSquare(), true) then
		ISTimedActionQueue.add(ISLightFromPetrol:new(playerObj, campfire, lighter, petrol, 20));
	end
end

ISCampingMenu.onAddTent = function(worldobjects, player, tentKit)
	local playerObj = getSpecificPlayer(player)
	ISCampingMenu.toPlayerInventory(playerObj, tentKit)
	local bo = campingTent:new(getSpecificPlayer(player), camping.tentSprites.tarp)
	bo.player = player
	getCell():setDrag(bo, player);
end

ISCampingMenu.onRemoveTent = function(worldobjects, player, tent)
	local playerObj = getSpecificPlayer(player)
	if luautils.walkAdj(playerObj, tent:getSquare()) then
		ISTimedActionQueue.add(ISRemoveTentAction:new(playerObj, tent, 60));
	end
end

ISCampingMenu.onSleep = function(worldobjects, player, tent)
	local playerObj = getSpecificPlayer(player)
	if luautils.walkAdj(playerObj, tent:getSquare()) then
		ISTimedActionQueue.add(ISSleepInTentAction:new(playerObj, tent, 0));
	end
end

ISCampingMenu.onRest = function(worldobjects, player, tent)
	local playerObj = getSpecificPlayer(player)
	if luautils.walkAdj(playerObj, tent:getSquare()) then
		ISTimedActionQueue.add(ISRestAction:new(playerObj));
	end
end

Events.OnFillWorldObjectContextMenu.Add(ISCampingMenu.doCampingMenu);
