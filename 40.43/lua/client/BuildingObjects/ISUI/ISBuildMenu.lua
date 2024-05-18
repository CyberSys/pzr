--***********************************************************
--**                    ROBERT JOHNSON                     **
--**       Contextual menu for building when clicking somewhere on the ground       **
--***********************************************************

ISBuildMenu = {};
ISBuildMenu.planks = 0;
ISBuildMenu.nails = 0;
ISBuildMenu.hinge = 0;
ISBuildMenu.doorknob = 0;
ISBuildMenu.cheat = false;
ISBuildMenu.woodWorkXp = 0;


ISBuildMenu.doBuildMenu = function(player, context, worldobjects, test)

	if test and ISWorldObjectContextMenu.Test then return true end

    if getCore():getGameMode()=="LastStand" then
        return;
    end

    local playerObj = getSpecificPlayer(player)

	if playerObj:getVehicle() then return; end

	ISBuildMenu.woodWorkXp = getSpecificPlayer(player):getPerkLevel(Perks.Woodwork);
	local thump = nil;

	local square = nil;

	-- we get the thumpable item (like wall/door/furniture etc.) if exist on the tile we right clicked
	for i,v in ipairs(worldobjects) do
		square = v:getSquare();
		if instanceof(v, "IsoThumpable") and not v:isDoor() then
			if not MultiStageBuilding.getStages(playerObj, v, ISBuildMenu.cheat):isEmpty() then
				thump = v
			end
		end
    end

    if thump then
        local stages = MultiStageBuilding.getStages(getSpecificPlayer(player), thump, ISBuildMenu.cheat);
        if not stages:isEmpty() then
            for i=0,stages:size()-1 do
                local stage = stages:get(i);
                local option = context:addOption(stage:getDisplayName(), worldobjects, ISBuildMenu.onMultiStageBuild, stage, thump, player);
                local items = stage:getItemsLua();
                local perks = stage:getPerksLua();
                local tooltip = ISToolTip:new();
                tooltip:initialise();
                tooltip:setVisible(false);
                tooltip:setName(stage:getDisplayName());
                tooltip.description = "";
                tooltip:setTexture(stage:getSprite());
                local notAvailable = false;
                if not ISBuildMenu.cheat then
					for x=0,stage:getItemsToKeep():size()-1 do
						local itemString = stage:getItemsToKeep():get(x);
						if (itemString == "Base.Hammer") then
							if (getSpecificPlayer(player):getInventory():containsWithModule("Base.HammerStone")) then
								itemString = "Base.HammerStone"
							else
								itemString = "Base.Hammer"
							end
						end
                        local item = InventoryItemFactory.CreateItem(itemString);
                        if item then
                            if getSpecificPlayer(player):getInventory():containsWithModule(itemString) then
                                tooltip.description = tooltip.description .. " <RGB:1,1,1> " .. item:getName() .. " <LINE> ";
                            else
                                tooltip.description = tooltip.description .. " <RGB:1,0,0> " .. item:getName() .. " <LINE> ";
                                notAvailable = true;
                            end
                        end
                    end
                    tooltip.description = tooltip.description .. " <LINE> ";
                    for x,v in pairs(items) do
                        local item = InventoryItemFactory.CreateItem(x);
                        if item then
                            if instanceof(item, "DrainableComboItem") then
                               local drainable = getSpecificPlayer(player):getInventory():getItemFromType(x);
                               local useLeft = 0;
                                if drainable and drainable:getDrainableUsesInt() >= tonumber(v) then
                                    useLeft = drainable:getDrainableUsesInt()
                                    tooltip.description = tooltip.description .. " <RGB:1,1,1> " .. item:getName() .. " " .. useLeft .. "/" .. v .. " <LINE> ";
                                else
                                    if drainable then
                                        useLeft = drainable:getDrainableUsesInt()
                                    end
                                    tooltip.description = tooltip.description .. " <RGB:1,0,0> " .. item:getName() .. " " .. useLeft .. "/" .. v .. " <LINE> ";
                                    notAvailable = true;
                                end
                            else
                                if getSpecificPlayer(player):getInventory():getItemCount(x) >= tonumber(v) then
                                    tooltip.description = tooltip.description .. " <RGB:1,1,1> " .. item:getName() .. " " .. getSpecificPlayer(player):getInventory():getItemCount(x) .. "/" .. v .. " <LINE> ";
                                else
                                    tooltip.description = tooltip.description .. " <RGB:1,0,0> " .. item:getName() .. " " .. getSpecificPlayer(player):getInventory():getItemCount(x) .. "/" .. v .. " <LINE> ";
                                    notAvailable = true;
                                end
                            end
                        end
                    end
                    tooltip.description = tooltip.description .. " <LINE> ";
                    for x,v in pairs(perks) do
                        local perk = PerkFactory.getPerk(x);
                        if getSpecificPlayer(player):getPerkLevel(x) >= tonumber(v) then
                            tooltip.description = tooltip.description .. " <RGB:1,1,1> " .. getText("IGUI_perks_" .. perk:getName()) .. " " .. getSpecificPlayer(player):getPerkLevel(x) .. "/" ..  v .. " <LINE>";
                        else
                            tooltip.description = tooltip.description .. " <RGB:1,0,0> " .. getText("IGUI_perks_" .. perk:getName()) .. " " .. getSpecificPlayer(player):getPerkLevel(x) .. "/" ..  v .. " <LINE>";
                            notAvailable = true;
                        end
                    end
                    local knownRecipe = stage:getKnownRecipe()
                    if knownRecipe then
                        tooltip.description = tooltip.description .. " <LINE> "
                        if playerObj:getKnownRecipes():contains(stage:getKnownRecipe()) then
                            tooltip.description = tooltip.description .. " <RGB:1,1,1> " .. getText("Tooltip_vehicle_requireRecipe", getRecipeDisplayName(knownRecipe)) .. " <LINE>"
                        else
                            tooltip.description = tooltip.description .. " <RGB:1,0,0> " .. getText("Tooltip_vehicle_requireRecipe", getRecipeDisplayName(knownRecipe)) .. " <LINE>"
                            notAvailable = true
                        end
                    end
                    option.notAvailable = notAvailable;
                end
                option.toolTip = tooltip;
            end
        end
    end

	-- build menu
	-- if we have any thing to build in our inventory
	if ISBuildMenu.haveSomethingtoBuild(player) then

        if test then return ISWorldObjectContextMenu.setTest() end

		local buildOption = context:addOption(getText("ContextMenu_Build"), worldobjects, nil);
		-- create a brand new context menu wich contain our different material (wood, stone etc.) to build
		local subMenu = ISContextMenu:getNew(context);
		-- We create the different option for this new menu (wood, stone etc.)
		-- check if we can build something in wood material
		if haveSomethingtoBuildWood(player) then
			-- we add the subMenu to our current option (Build)
			context:addSubMenu(buildOption, subMenu);

			------------------ WALL ------------------
            local wallOption = subMenu:addOption(getText("ContextMenu_Wall"), worldobjects, nil);
            local subMenuWall = subMenu:getNew(subMenu);
            -- we add our new menu to the option we want
            context:addSubMenu(wallOption, subMenuWall);
			ISBuildMenu.buildWallMenu(subMenuWall, wallOption, player);
			------------------ DOOR ------------------
			local doorOption = subMenu:addOption(getText("ContextMenu_Door"), worldobjects, nil);
			local subMenuDoor = subMenu:getNew(subMenu);
--			 we add our new menu to the option we want (here door)
			context:addSubMenu(doorOption, subMenuDoor);
			ISBuildMenu.buildDoorMenu(subMenuDoor, doorOption, player);
			------------------ DOOR FRAME ------------------
--			ISBuildMenu.buildDoorFrameMenu(subMenu, player);
--~ 			----------------- WINDOWS FRAME-----------------
--			ISBuildMenu.buildWindowsFrameMenu(subMenu, player);
 			------------------ STAIRS ------------------
--			local stairsOption = subMenu:addOption(getText("ContextMenu_Stairs"), worldobjects, nil);
--			local subMenuStairs = subMenu:getNew(subMenu);
			-- we add our new menu to the option we want (here wood)
--			context:addSubMenu(stairsOption, subMenuStairs);
			ISBuildMenu.buildStairsMenu(subMenu, player);
--~ 			------------------ FLOOR ------------------
--			local floorOption = subMenu:addOption(getText("ContextMenu_Floor"), worldobjects, nil);
--			local subMenuFloor = subMenu:getNew(subMenu);
			-- we add our new menu to the option we want (here build)
--			context:addSubMenu(floorOption, subMenuFloor);
			ISBuildMenu.buildFloorMenu(subMenu, player);
			------------------ WOODEN CRATE ------------------
			ISBuildMenu.buildContainerMenu(subMenu, player);
			------------------ BAR ------------------
			local barOption = subMenu:addOption(getText("ContextMenu_Bar"), worldobjects, nil);
			local subMenuBar = subMenu:getNew(subMenu);
			-- we add our new menu to the option we want (here wood)
			context:addSubMenu(barOption, subMenuBar);
			ISBuildMenu.buildBarMenu(subMenuBar, barOption, player);
			------------------ FURNITURE ------------------
			local furnitureOption = subMenu:addOption(getText("ContextMenu_Furniture"), worldobjects, nil);
			local subMenuFurniture = subMenu:getNew(subMenu);
			-- we add our new menu to the option we want (here build)
			context:addSubMenu(furnitureOption, subMenuFurniture);
			ISBuildMenu.buildFurnitureMenu(subMenuFurniture, context, furnitureOption, player);
			------------------ FENCE ------------------
			local fenceOption = subMenu:addOption(getText("ContextMenu_Fence"), worldobjects, nil);
			local subMenuFence = subMenu:getNew(subMenu);
			-- we add our new menu to the option we want (here build)
			context:addSubMenu(fenceOption, subMenuFence);
			ISBuildMenu.buildFenceMenu(subMenuFence, fenceOption, player);
            ------------------ LIGHT SOURCES ------------------
--            local lightOption = subMenu:addOption("Light source", worldobjects, nil);
--            local subMenuLight = subMenu:getNew(subMenu);
            -- we add our new menu to the option we want (here build)
--            context:addSubMenu(lightOption, subMenuLight);
--            ISBuildMenu.buildLightMenu(subMenu, lightOption, player);
            ISBuildMenu.buildLightMenu(subMenu, player);
			------------------ MISC ------------------
			local miscOption = subMenu:addOption(getText("ContextMenu_Misc"), worldobjects, nil);
			local subMenuMisc = subMenu:getNew(subMenu);
			context:addSubMenu(miscOption, subMenuMisc);
			ISBuildMenu.buildMiscMenu(subMenuMisc, miscOption, player);
		end
	end

	-- dismantle stuff
    -- TODO: RJ: removed it for now need to see exactly how it works as now we have a proper right click to dismantle items...
--	if getSpecificPlayer(player):getInventory():contains("Saw") and getSpecificPlayer(player):getInventory():contains("Screwdriver") then
--        if test then return ISWorldObjectContextMenu.setTest() end
--		context:addOption(getText("ContextMenu_Dismantle"), worldobjects, ISBuildMenu.onDismantle, getSpecificPlayer(player));
--	end

	-- destroy item with sledgehammer
    if not isClient() or getServerOptions():getBoolean("AllowDestructionBySledgehammer") then
        local sledgehammer = getSpecificPlayer(player):getInventory():getBestCondition("Sledgehammer")
        if sledgehammer and not sledgehammer:isBroken() then
            if test then return ISWorldObjectContextMenu.setTest() end
            context:addOption(getText("ContextMenu_Destroy"), worldobjects, ISWorldObjectContextMenu.onDestroy, getSpecificPlayer(player))
        end
    end
end

function ISBuildMenu.haveSomethingtoBuild(player)
--~ 	return true;
	return haveSomethingtoBuildWood(player);
end

function haveSomethingtoBuildWood(player)
	ISBuildMenu.materialOnGround = buildUtil.checkMaterialOnGround(getSpecificPlayer(player):getCurrentSquare())
	if ISBuildMenu.cheat then
		return true;
	end
	ISBuildMenu.planks = 0;
	ISBuildMenu.nails = 0;
	ISBuildMenu.hinge = 0;
	ISBuildMenu.doorknob = 0;
	ISBuildMenu.hasHammer = getSpecificPlayer(player):getInventory():contains("Hammer", true, true) or getSpecificPlayer(player):getInventory():contains("HammerStone", true, true)
	if ISBuildMenu.hasHammer then
		-- most objects require a hammer
	elseif ISBuildMenu.countMaterial(player, "Base.Sandbag") >= 3 or ISBuildMenu.countMaterial(player, "Base.Gravelbag") >= 3 then
		-- no hammer required
	elseif ISBuildMenu.canBuildLogWall(player) then
		-- no hammer required
	else
		return false
	end
	ISBuildMenu.planks = ISBuildMenu.countMaterial(player, "Base.Plank")
	ISBuildMenu.nails = ISBuildMenu.countMaterial(player, "Base.Nails")
	ISBuildMenu.hinge = ISBuildMenu.countMaterial(player, "Base.Hinge")
	ISBuildMenu.doorknob = ISBuildMenu.countMaterial(player, "Base.Doorknob")
	return true;
end

ISBuildMenu.onMultiStageBuild = function(worldobjects, stage, item, player)
    local playerObj = getSpecificPlayer(player);
    if luautils.walkAdj(playerObj, item:getSquare(), false) then
        -- equip required items
        local first = true;
        for i=0,stage:getItemsToKeep():size() - 1 do
            local itemToEquip =  stage:getItemsToKeep():get(i);
			if (itemToEquip == "Base.Hammer") then
				if (playerObj:getInventory():contains("HammerStone", true, true)) then
					itemToEquip = "Base.HammerStone"
				end
			end
            if not playerObj:hasEquipped(itemToEquip) and playerObj:getInventory():getItemFromType(itemToEquip) then
                ISInventoryPaneContextMenu.equipWeapon(playerObj:getInventory():getItemFromType(itemToEquip), first, false, player)
            end
            if not first then
                break;
            end
            first = false;
        end
        ISTimedActionQueue.add(ISMultiStageBuild:new(playerObj, stage, item, stage:getTimeNeeded(playerObj)));
    end
end

ISBuildMenu.canDoStage = function(player, stage)
    if ISBuildMenu.cheat then return true; end
    if stage:getKnownRecipe() and not player:getKnownRecipes():contains(stage:getKnownRecipe()) then
        return false
    end
    local items = stage:getItemsLua();
	for x=0,stage:getItemsToKeep():size()-1 do
		local itemString = stage:getItemsToKeep():get(x)
		if (itemString == "Base.Hammer") then
			if (player:getInventory():containsWithModule("Base.HammerStone")) then
				itemString = "Base.HammerStone"
			else
				itemString = "Base.Hammer"
			end
		end
        local item = InventoryItemFactory.CreateItem(itemString);
        if item then
            if not player:getInventory():containsWithModule(itemString) then
                return false;
            end
        end
    end
    for x,v in pairs(items) do
        local item = InventoryItemFactory.CreateItem(x);
        if item then
            if instanceof(item, "DrainableComboItem") then
                local drainable = player:getInventory():getItemFromType(x);
                local useLeft = 0;
                if (drainable and drainable:getRemainingUses() < tonumber(v)) or not drainable then
                    return false;
                end
            else
                if player:getInventory():getItemCount(x) < tonumber(v) then
                    return false;
                end
            end
        end
    end
    return true;
end

-- **********************************************
-- **                   *MISC*                  **
-- **********************************************
ISBuildMenu.buildMiscMenu = function(subMenu, option, player)
	local crossOption = subMenu:addOption(getText("ContextMenu_Wooden_Cross"), worldobjects, ISBuildMenu.onWoodenCross, square, player);
	local toolTip = ISBuildMenu.canBuild(2,2,0,0,0,0,crossOption, player);
	toolTip:setName(getText("ContextMenu_Wooden_Cross"));
	toolTip:setTexture("location_community_cemetary_01_23");
	ISBuildMenu.requireHammer(crossOption);
	
	local crossOption = subMenu:addOption(getText("ContextMenu_Stone_Pile"), worldobjects, ISBuildMenu.onStonePile, square, player);
	local toolTip = ISBuildMenu.canBuild(0,0,0,0,0,0,crossOption, player);
	-- we add that we need stone too
	local stones = getSpecificPlayer(player):getInventory():getItemCount("Base.Stone", true);
	if stones < 6 and not ISBuildMenu.cheat then
		toolTip.description = toolTip.description .. " <RGB:1,0,0>" .. getItemText("Stone") .. " " .. stones .. "/6 ";
		crossOption.onSelect = nil;
		crossOption.notAvailable = true;
	else
		toolTip.description = toolTip.description .. " <RGB:1,1,1>" .. getItemText("Stone") .. " 6/6 ";
	end
	toolTip:setName(getText("ContextMenu_Stone_Pile"));
	toolTip:setTexture("location_community_cemetary_01_30");
	
	local crossOption = subMenu:addOption(getText("ContextMenu_Wooden_Picket"), worldobjects, ISBuildMenu.onWoodenPicket, square, player);
	local toolTip = ISBuildMenu.canBuild(1,0,0,0,0,0,crossOption, player);
	local ropes = tonumber(getSpecificPlayer(player):getInventory():getItemCount("Base.SheetRope", true));
	-- we add that we need rope too
	if ropes == 0 and not ISBuildMenu.cheat then
		toolTip.description = toolTip.description .. " <RGB:1,0,0>" .. getItemText("Sheet Rope") .. " 0/1 ";
		crossOption.onSelect = nil;
		crossOption.notAvailable = true;
	else
		toolTip.description = toolTip.description .. " <RGB:1,1,1>" .. getItemText("Sheet Rope") .. " 1/1 ";
	end
	toolTip:setName(getText("ContextMenu_Wooden_Picket"));
	toolTip:setTexture("location_community_cemetary_01_31");
end

ISBuildMenu.onWoodenCross = function(worldobjects, square, player)
	local cross = ISSimpleFurniture:new("Wooden Cross", "location_community_cemetary_01_22", "location_community_cemetary_01_23");
	cross.canPassThrough = true;
	cross.canBarricade = false;
	cross.ignoreNorth = true;
	cross.canBeAlwaysPlaced = false;
	cross.isThumpable = false;
	cross.modData["xp:Woodwork"] = 5;
	cross.modData["need:Base.Plank"] = "2";
	cross.modData["need:Base.Nails"] = "2";
	cross.player = player
	cross.maxTime = 80;
	getCell():setDrag(cross, player);
end

ISBuildMenu.onStonePile = function(worldobjects, square, player)
	-- sprite, northSprite, corner
	local cross = ISSimpleFurniture:new("Stone Pile", "location_community_cemetary_01_30", "location_community_cemetary_01_30");
	cross.canPassThrough = true;
	cross.canBarricade = false;
	cross.ignoreNorth = true;
	cross.canBeAlwaysPlaced = false;
	cross.isThumpable = false;
	cross.modData["need:Base.Stone"] = "6";
	cross.player = player
	cross.maxTime = 50;
	cross.noNeedHammer = true;
	getCell():setDrag(cross, player);
end

ISBuildMenu.onWoodenPicket = function(worldobjects, square, player)
	local cross = ISSimpleFurniture:new("Wooden Picket", "location_community_cemetary_01_31", "location_community_cemetary_01_31");
	cross.canPassThrough = true;
	cross.canBarricade = false;
	cross.ignoreNorth = true;
	cross.canBeAlwaysPlaced = false;
	cross.isThumpable = false;
	cross.modData["xp:Woodwork"] = 5;
	cross.modData["need:Base.Plank"] = "1";
	cross.modData["need:Base.SheetRope"] = "1";
	cross.player = player
	cross.maxTime = 50;
	cross.noNeedHammer = true;
	getCell():setDrag(cross, player);
end

-- **********************************************
-- **                   *BAR*                  **
-- **********************************************

ISBuildMenu.buildBarMenu = function(subMenu, option, player)
	local barElemSprite = ISBuildMenu.getBarElementSprites(player);
	local barElemOption = subMenu:addOption(getText("ContextMenu_Bar_Element"), worldobjects, ISBuildMenu.onBarElement, barElemSprite, player);
	local tooltip = ISBuildMenu.canBuild(4,4,0,0,0,7,barElemOption, player);
	tooltip:setName(getText("ContextMenu_Bar_Element"));
	tooltip.description = getText("Tooltip_craft_barElementDesc") .. tooltip.description;
	tooltip:setTexture(barElemSprite.sprite);
	ISBuildMenu.requireHammer(barElemOption)

	local barCornerSprite = ISBuildMenu.getBarCornerSprites(player);
	local barCornerOption = subMenu:addOption(getText("ContextMenu_Bar_Corner"), worldobjects, ISBuildMenu.onBarElement, barCornerSprite, player);
	local tooltip2 = ISBuildMenu.canBuild(4,4,0,0,0,7,barCornerOption, player);
	tooltip2:setName(getText("ContextMenu_Bar_Corner"));
	tooltip2.description = getText("Tooltip_craft_barElementDesc") .. tooltip2.description;
	tooltip2:setTexture(barCornerSprite.sprite);
	ISBuildMenu.requireHammer(barCornerOption)

    if barElemOption.notAvailable and barCornerOption.notAvailable then
        option.notAvailable = true;
    end
end

ISBuildMenu.onBarElement = function(worldobjects, sprite, player)
	-- sprite, northSprite
	local bar = ISWoodenContainer:new(sprite.sprite, sprite.northSprite);
	bar.name = "Bar";
	bar:setEastSprite(sprite.eastSprite);
	bar:setSouthSprite(sprite.southSprite);
    bar.modData["xp:Woodwork"] = 5;
	bar.modData["need:Base.Plank"] = "4";
	bar.modData["need:Base.Nails"] = "4";
	bar.player = player
	bar.renderFloorHelper = true
	getCell():setDrag(bar, player);
end

-- **********************************************
-- **                  *FENCE*                 **
-- **********************************************

ISBuildMenu.buildFenceMenu = function(subMenu, option, player)
	local stakeOption = subMenu:addOption(getText("ContextMenu_Wooden_Stake"), worldobjects, ISBuildMenu.onWoodenFenceStake, square, player);
	local toolTip = ISBuildMenu.canBuild(1,2,0,0,0,5,stakeOption, player);
	toolTip:setName(getText("ContextMenu_Wooden_Stake"));
	toolTip.description = getText("Tooltip_craft_woodenStakeDesc") .. toolTip.description;
	toolTip:setTexture("fencing_01_19");
	ISBuildMenu.requireHammer(stakeOption)

	local barbedOption = subMenu:addOption(getText("ContextMenu_Barbed_Fence"), worldobjects, ISBuildMenu.onBarbedFence, square, player);
	local tooltip2 = ISBuildMenu.canBuild(0,0,0,0,1,5,barbedOption, player);
	-- we add that we need a Barbed wire too
	if not getSpecificPlayer(player):getInventory():contains("BarbedWire") and not ISBuildMenu.cheat then
		tooltip2.description = tooltip2.description .. " <RGB:1,0,0>" .. getItemText("Barbed wire") .. " 0/1 ";
		barbedOption.onSelect = nil;
		barbedOption.notAvailable = true;
	else
		tooltip2.description = tooltip2.description .. " <RGB:1,1,1>" .. getItemText("Barbed wire") .. " 1 ";
	end
	tooltip2:setName(getText("ContextMenu_Barbed_Fence"));
	tooltip2.description = getText("Tooltip_craft_barbedFenceDesc") .. tooltip2.description;
	tooltip2:setTexture("fencing_01_20");
	ISBuildMenu.requireHammer(barbedOption)

	local woodenFenceSprite = ISBuildMenu.getWoodenFenceSprites(player);
	local fenceOption = subMenu:addOption(getText("ContextMenu_Wooden_Fence"), worldobjects, ISBuildMenu.onWoodenFence, square, woodenFenceSprite, player);
	local tooltip3 = ISBuildMenu.canBuild(2,3,0,0,0,2,fenceOption, player);
	tooltip3:setName(getText("ContextMenu_Wooden_Fence"));
	tooltip3.description = getText("Tooltip_craft_woodenFenceDesc") .. tooltip3.description;
	tooltip3:setTexture(woodenFenceSprite.sprite);
	ISBuildMenu.requireHammer(fenceOption)

	local sandBagOption = subMenu:addOption(getText("ContextMenu_Sang_Bag_Wall"), worldobjects, ISBuildMenu.onSangBagWall, square, player);
	local tooltip4 = ISBuildMenu.canBuild(0,0,0,0,0,0,sandBagOption, player);
	-- we add that we need 3 sand bag too
	if ISBuildMenu.countMaterial(player,"Base.Sandbag") < 3 and not ISBuildMenu.cheat then
		tooltip4.description = tooltip4.description .. " <RGB:1,0,0>" .. getItemText("Sand bag") .. " " .. ISBuildMenu.countMaterial(player, "Base.Sandbag") .. "/3 ";
		sandBagOption.onSelect = nil;
		sandBagOption.notAvailable = true;
	else
		tooltip4.description = tooltip4.description .. " <RGB:1,1,1>" .. getItemText("Sand bag") .. " 3 ";
	end
	tooltip4:setName(getText("ContextMenu_Sang_Bag_Wall"));
	tooltip4.description = getText("Tooltip_craft_sandBagDesc") .. tooltip4.description;
	tooltip4:setTexture("carpentry_02_12");

    local gravelBagOption = subMenu:addOption(getText("ContextMenu_Gravel_Bag_Wall"), worldobjects, ISBuildMenu.onGravelBagWall, square, player);
    local tooltip5 = ISBuildMenu.canBuild(0,0,0,0,0,0,gravelBagOption, player);
    -- we add that we need 3 gravel bag too
    if ISBuildMenu.countMaterial(player, "Base.Gravelbag") < 3 and not ISBuildMenu.cheat then
        tooltip5.description = tooltip5.description .. " <RGB:1,0,0>" .. getItemText("Gravel bag") .. " " .. ISBuildMenu.countMaterial(player, "Base.Gravelbag") .. "/3 ";
        gravelBagOption.onSelect = nil;
        gravelBagOption.notAvailable = true;
    else
        tooltip5.description = tooltip5.description .. " <RGB:1,1,1>" .. getItemText("Gravel bag") .. " 3 ";
    end
    tooltip5:setName(getText("ContextMenu_Gravel_Bag_Wall"));
    tooltip5.description = getText("Tooltip_craft_gravelBagDesc") .. tooltip5.description;
    tooltip5:setTexture("carpentry_02_12");

    if stakeOption.notAvailable and barbedOption.notAvailable and fenceOption.notAvailable and sandBagOption.notAvailable and gravelBagOption.notAvailable then
        option.notAvailable = true;
    end
end

ISBuildMenu.onBarbedFence = function(worldobjects, square, player)
-- sprite, northSprite, corner
	local fence = ISWoodenWall:new("fencing_01_20", "fencing_01_21", nil);
	-- we can place our fence every where
--	fence.canBeAlwaysPlaced = true;
	fence.hoppable = true;
    fence.canBarricade = false
    fence.modData["xp:Woodwork"] = 5;
	fence.modData["need:Base.BarbedWire"] = "1";
	fence.player = player
	fence.name = "Barbed Fence"
    getCell():setDrag(fence, player);
end

ISBuildMenu.onWoodenFenceStake = function(worldobjects, square, player)
	-- sprite, northSprite, corner
	local fence = ISWoodenWall:new("fencing_01_19", "fencing_01_19", nil);
	fence.canPassThrough = true;
	fence.canBarricade = false
	-- we can place our fence every where
	fence.canBeAlwaysPlaced = true;
    fence.modData["xp:Woodwork"] = 5;
	fence.modData["need:Base.Plank"] = "1";
	fence.modData["need:Base.Nails"] = "2";
	fence.player = player
	fence.name = "Wooden Stake"
    getCell():setDrag(fence, player);
end

ISBuildMenu.onSangBagWall = function(worldobjects, square, player)
	-- sprite, northSprite, corner
	local fence = ISWoodenWall:new("carpentry_02_12", "carpentry_02_13", nil);
	fence:setEastSprite("carpentry_02_14");
	fence:setSouthSprite("carpentry_02_15");
    fence.hoppable = true;
    fence.canBarricade = false
	-- but it slow you
--	fence.crossSpeed = 0.3;
	fence.modData["need:Base.Sandbag"] = "3";
    fence.modData["xp:Woodwork"] = 5;
	fence.player = player
	fence.renderFloorHelper = true
	fence.noNeedHammer = true
	fence.name = "Sand Bag Wall"
    getCell():setDrag(fence, player);
end

ISBuildMenu.onGravelBagWall = function(worldobjects, square, player)
-- sprite, northSprite, corner
    local fence = ISWoodenWall:new("carpentry_02_12", "carpentry_02_13", nil);
    fence:setEastSprite("carpentry_02_14");
    fence:setSouthSprite("carpentry_02_15");
    fence.hoppable = true;
    fence.canBarricade = false
    -- but it slow you
--    fence.crossSpeed = 0.3;
    fence.modData["need:Base.Gravelbag"] = "3";
    fence.modData["xp:Woodwork"] = 5;
    fence.player = player
	fence.renderFloorHelper = true
	fence.noNeedHammer = true
	fence.name = "Gravel Bag Wall"
    getCell():setDrag(fence, player);
end

ISBuildMenu.onWoodenFence = function(worldobjects, square, sprite, player)
	-- sprite, northSprite, corner
	local fence = ISWoodenWall:new(sprite.sprite, sprite.northSprite, sprite.corner);
	-- you can hopp a fence
	fence.hoppable = true;
	fence.isThumpable = false;
    fence.canBarricade = false
    fence.modData["xp:Woodwork"] = 5;
	fence.modData["need:Base.Plank"] = "2";
	fence.modData["need:Base.Nails"] = "3";
	fence.player = player
	fence.name = "Wooden Fence"
	getCell():setDrag(fence, player);
end

-- **********************************************
-- **          *LIGHT SOURCES*                 **
-- **********************************************
ISBuildMenu.buildLightMenu = function(subMenu, player)
    local sprite = ISBuildMenu.getPillarLampSprite(player);
    local lampOption = subMenu:addOption(getText("ContextMenu_Lamp_on_Pillar"), worldobjects, ISBuildMenu.onPillarLamp, square, sprite, player);
    local toolTip = ISBuildMenu.canBuild(2,4,0,0,0,4,lampOption, player);
    if not getSpecificPlayer(player):getInventory():contains("Torch") and not ISBuildMenu.cheat then
        toolTip.description = toolTip.description .. " <RGB:1,0,0>" .. getItemText("Flashlight") .. " 0/1 ";
        lampOption.onSelect = nil;
        lampOption.notAvailable = true;
    else
        toolTip.description = toolTip.description .. " <RGB:1,1,1>" .. getItemText("Flashlight") .. " 1 ";
    end
    if not getSpecificPlayer(player):getInventory():contains("Rope") and not ISBuildMenu.cheat then
        toolTip.description = toolTip.description .. " <LINE> <RGB:1,0,0>" .. getItemText("Rope") .." 0/1 ";
        lampOption.onSelect = nil;
        lampOption.notAvailable = true;
    else
        toolTip.description = toolTip.description .. " <LINE> <RGB:1,1,1>" .. getItemText("Rope") .. " 1 ";
    end
    toolTip:setName(getText("ContextMenu_Lamp_on_Pillar"));
    toolTip.description = getText("ContextMenu_Lamp_on_Pillar") .. " " .. toolTip.description;
    toolTip:setTexture("carpentry_02_59");
	ISBuildMenu.requireHammer(lampOption)

--    if lampOption.notAvailable then
--        option.notAvailable = true;
--    end
end

ISBuildMenu.onPillarLamp = function(worldobjects, square, sprite, player)
-- sprite, northSprite
    local lamp = ISLightSource:new(sprite.sprite, sprite.northSprite, getSpecificPlayer(player));
    lamp.offsetX = 5;
    lamp.offsetY = 5;
    lamp.modData["need:Base.Plank"] = "2";
    lamp.modData["need:Base.Rope"] = "1";
    lamp.modData["need:Base.Nails"] = "4";
    lamp.modData["xp:Woodwork"] = 5;
--    lamp.modData["need:Base.Torch"] = "1";
    lamp:setEastSprite(sprite.eastSprite);
    lamp:setSouthSprite(sprite.southSprite);
    lamp.fuel = "Base.Battery";
    lamp.baseItem = "Base.Torch";
    lamp.radius = 10;
    lamp.player = player
    getCell():setDrag(lamp, player);
end

-- **********************************************
-- **                  *WALL*                  **
-- **********************************************

ISBuildMenu.buildWallMenu = function(subMenu, option, player)
    local sprite = ISBuildMenu.getWoodenWallFrameSprites(player);
    local wallOption = subMenu:addOption(getText("ContextMenu_Wooden_Wall_Frame"), worldobjects, ISBuildMenu.onWoodenWallFrame, sprite, player);
    local tooltip = ISBuildMenu.canBuild(2, 2, 0, 0, 0, 2, wallOption, player);
    tooltip:setName(getText("ContextMenu_Wooden_Wall_Frame"));
    tooltip.description = getText("Tooltip_craft_woodenWallFrameDesc") .. tooltip.description;
    tooltip:setTexture(sprite.sprite);
    ISBuildMenu.requireHammer(wallOption)

--	local sprite = ISBuildMenu.getWoodenWallSprites(player);
--	local wallOption = subMenu:addOption(getText("ContextMenu_Wooden_Wall"), worldobjects, ISBuildMenu.onWoodenWall, sprite, player);
--	local tooltip = ISBuildMenu.canBuild(3, 3, 0, 0, 0, 2, wallOption, player);
--	tooltip:setName(getText("ContextMenu_Wooden_Wall"));
--	tooltip.description = getText("Tooltip_craft_woodenWallDesc") .. tooltip.description;
--	tooltip:setTexture(sprite.sprite);
--	ISBuildMenu.requireHammer(wallOption)

	local pillarOption = subMenu:addOption(getText("ContextMenu_Wooden_Pillar"), worldobjects, ISBuildMenu.onWoodenPillar, player);
	local tooltip = ISBuildMenu.canBuild(2, 3, 0, 0, 0, 2, pillarOption, player);
	tooltip:setName(getText("ContextMenu_Wooden_Pillar"));
	tooltip.description = getText("Tooltip_craft_woodenPillarDesc") .. tooltip.description;
	tooltip:setTexture("walls_exterior_wooden_01_27");
	ISBuildMenu.requireHammer(pillarOption)

    local logOption = subMenu:addOption(getText("ContextMenu_Log_Wall"), worldobjects, ISBuildMenu.onLogWall, player);
    local tooltip = ISBuildMenu.canBuild(0, 0, 0, 0, 0, 0, logOption, player);
    tooltip:setName(getText("ContextMenu_Log_Wall"));
    local numLog = ISBuildMenu.countMaterial(player, "Base.Log")
    if numLog < 4 and not ISBuildMenu.cheat then
        tooltip.description = tooltip.description .. " <RGB:1,0,0>" .. getItemText("Log") .. " " .. numLog .. "/4 <LINE> ";
        logOption.onSelect = nil;
        logOption.notAvailable = true;
    else
        tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. getItemText("Log") .. " 4 <LINE> ";
    end
    tooltip:setTexture("carpentry_02_80");

    -- log wall require either 4 ripped sheet, 4 twine or 2 ropes
    local numRippedSheets = ISBuildMenu.countMaterial(player, "Base.RippedSheets") + ISBuildMenu.countMaterial(player, "Base.RippedSheetsDirty")
    local numTwine = ISBuildMenu.countMaterial(player, "Base.Twine")
    local numRope = ISBuildMenu.countMaterial(player, "Base.Rope")
    if numRippedSheets >= 4 or ISBuildMenu.cheat then
        tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. getItemText("Ripped Sheets") .. " 4 ";
    elseif numTwine >= 4 then
        tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. getItemText("Twine") .. " 4 ";
    elseif numRope >= 2 then
        tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. getItemText("Rope") .. " 2 ";
    else
        tooltip.description = tooltip.description .. " <RGB:1,0,0>" .. getItemText("Ripped Sheets") .. " " .. numRippedSheets .. "/4 <LINE> " .. getText("ContextMenu_or") .. " " .. getItemText("Twine") .. " " .. numTwine .. "/4 <LINE> " .. getText("ContextMenu_or") .. " " ..   getItemText("Rope") .. " " .. numRope .. "/2 ";
        logOption.onSelect = nil;
        logOption.notAvailable = true;
    end
    tooltip.description = getText("Tooltip_craft_wallLogDesc") .. tooltip.description;

    if wallOption.notAvailable and logOption.notAvailable and pillarOption.notAvailable then
        option.notAvailable = true;
    end
end

ISBuildMenu.onWoodenPillar = function(worldobjects, player)
	local wall = ISWoodenWall:new("walls_exterior_wooden_01_27", "walls_exterior_wooden_01_27", nil);
	wall.modData["need:Base.Plank"] = "2";
	wall.modData["need:Base.Nails"] = "3";
    wall.modData["xp:Woodwork"] = 3;
	wall.modData["wallType"] = "pillar";
	wall.canBePlastered = true;
	wall.canPassThrough = true;
	wall.canBarricade = false
    wall.player = player;
    wall.name = "Wooden Pillar"
	getCell():setDrag(wall, player);
end

ISBuildMenu.canBuildLogWall = function(player)
    local logs = ISBuildMenu.countMaterial(player, "Base.Log")
    local sheets = ISBuildMenu.countMaterial(player, "Base.RippedSheets") + ISBuildMenu.countMaterial(player, "Base.RippedSheetsDirty")
    local twine = ISBuildMenu.countMaterial(player, "Base.Twine")
    local rope = ISBuildMenu.countMaterial(player, "Base.Rope")
    return logs >= 4 and (sheets >= 4 or twine >= 4 or rope >= 2)
end

ISBuildMenu.onLogWall = function(worldobjects, player)
    local wall = ISWoodenWall:new("carpentry_02_80", "carpentry_02_81", nil);
    wall.modData["need:Base.Log"] = "4";
	local sheets = ISBuildMenu.countMaterial(player, "Base.RippedSheets");
	local sheetsDirty = ISBuildMenu.countMaterial(player, "Base.RippedSheetsDirty");
	if sheets > 4 then sheets = 4; sheetsDirty = 0 end
	if sheetsDirty > 4 then sheetsDirty = 4; sheets = 0 end
	if sheets < 4 and sheetsDirty > 0 then sheetsDirty = 4 - sheets; end
    if sheets + sheetsDirty >= 4 then
		if sheets > 0 then wall.modData["need:Base.RippedSheets"] = tostring(sheets); end
		if sheetsDirty > 0 then wall.modData["need:Base.RippedSheetsDirty"] = tostring(sheetsDirty); end
    elseif ISBuildMenu.countMaterial(player, "Base.Twine") >= 4 then
        wall.modData["need:Base.Twine"] = "4";
    elseif ISBuildMenu.countMaterial(player, "Base.Rope") >= 2 then
        wall.modData["need:Base.Rope"] = "2";
    end
    wall.modData["xp:Woodwork"] = 5;
    wall.player = player;
	wall.noNeedHammer = true
	wall.canBarricade = false
	wall.name = "Log Wall"
    getCell():setDrag(wall, player);
end

ISBuildMenu.onWoodenWall = function(worldobjects, sprite, player)
	-- sprite, northSprite, corner
	local wall = ISWoodenWall:new(sprite.sprite, sprite.northSprite, sprite.corner);
    if getSpecificPlayer(player):getPerkLevel(Perks.Woodwork) >= 8 then
	    wall.canBePlastered = true;
    end
	wall.canBarricade = false
	-- set up the required material
    wall.modData["wallType"] = "wall";
    wall.modData["xp:Woodwork"] = 5;
	wall.modData["need:Base.Plank"] = "3";
	wall.modData["need:Base.Nails"] = "3";
    wall.player = player;
    getCell():setDrag(wall, player);
end

ISBuildMenu.onWoodenWallFrame = function(worldobjects, sprite, player)
-- sprite, northSprite, corner
    local wall = ISWoodenWall:new(sprite.sprite, sprite.northSprite, sprite.corner);
    wall.canBarricade = false
    wall.name = "WoodenWallFrame";
    -- set up the required material
    wall.modData["xp:Woodwork"] = 5;
    wall.modData["need:Base.Plank"] = "2";
    wall.modData["need:Base.Nails"] = "2";
    wall.health = 50;
    wall.player = player;
    getCell():setDrag(wall, player);
end

-- **********************************************
-- **              *WINDOWS FRAME*             **
-- **********************************************
ISBuildMenu.buildWindowsFrameMenu = function(subMenu, player)
	local sprite = ISBuildMenu.getWoodenWindowsFrameSprites(player);
	local wallOption = subMenu:addOption(getText("ContextMenu_Windows_Frame"), worldobjects, ISBuildMenu.onWoodenWindowsFrame, square, sprite, player);
	local tooltip = ISBuildMenu.canBuild(4, 4, 0, 0, 0, 2, wallOption, player);
	tooltip:setName(getText("ContextMenu_Windows_Frame"));
	tooltip.description = getText("Tooltip_craft_woodenFrameDesc") .. tooltip.description;
	tooltip:setTexture(sprite.sprite);
	ISBuildMenu.requireHammer(wallOption)
end

ISBuildMenu.onWoodenWindowsFrame = function(worldobjects, square, sprite, player)
	-- sprite, northSprite, corner
	local frame = ISWoodenWall:new(sprite.sprite, sprite.northSprite, sprite.corner);
    if getSpecificPlayer(player):getPerkLevel(Perks.Woodwork) >= 8 then
	    frame.canBePlastered = true;
    end
	frame.hoppable = true;
	frame.isThumpable = false
	-- set up the required material
    frame.modData["xp:Woodwork"] = 5;
    frame.modData["wallType"] = "windowsframe";
	frame.modData["need:Base.Plank"] = "4";
	frame.modData["need:Base.Nails"] = "4";
	frame.player = player
	frame.name = "Window Frame"
	getCell():setDrag(frame, player);
end

-- **********************************************
-- **                  *FLOOR*                 **
-- **********************************************

ISBuildMenu.buildFloorMenu = function(subMenu, player)
	-- simple wooden floor
    local floorSprite = ISBuildMenu.getWoodenFloorSprites(player);
	local floorOption = subMenu:addOption(getText("ContextMenu_Wooden_Floor"), worldobjects, ISBuildMenu.onWoodenFloor, square, floorSprite, player);
	local tooltip = ISBuildMenu.canBuild(1,1,0,0,0,1,floorOption, player);
	tooltip:setName(getText("ContextMenu_Wooden_Floor"));
	tooltip.description = getText("Tooltip_craft_woodenFloorDesc") .. tooltip.description;
	tooltip:setTexture(floorSprite.sprite);
	ISBuildMenu.requireHammer(floorOption)
end

ISBuildMenu.onWoodenFloor = function(worldobjects, square, sprite, player)
	-- sprite, northSprite
	local foor = ISWoodenFloor:new(sprite.sprite, sprite.northSprite)
	foor.modData["need:Base.Plank"] = "1";
    foor.modData["xp:Woodwork"] = 3;
	foor.modData["need:Base.Nails"] = "1";
	foor.player = player
	getCell():setDrag(foor, player);
end

ISBuildMenu.onWoodenBrownFloor = function(worldobjects, square, player)
	-- sprite, northSprite
	local foor = ISWoodenFloor:new("TileFloorInt_24", "TileFloorInt_24")
	foor.modData["need:Base.Plank"] = "1";
    foor.modData["xp:Woodwork"] = 3;
	foor.modData["need:Base.Nails"] = "1";
	getCell():setDrag(foor, player);
end

ISBuildMenu.onWoodenLightBrownFloor = function(worldobjects, square, player)
	-- sprite, northSprite
	local foor = ISWoodenFloor:new("TileFloorInt_6", "TileFloorInt_6")
	foor.modData["need:Base.Plank"] = "1";
    foor.modData["xp:Woodwork"] = 3;
	foor.modData["need:Base.Nails"] = "1";
	foor.player = player
	getCell():setDrag(foor, player);
end

-- **********************************************
-- **               *CONTAINER*                **
-- **********************************************

ISBuildMenu.buildContainerMenu = function(subMenu, player)
    local crateSprite = ISBuildMenu.getWoodenCrateSprites(player);
	local crateOption = subMenu:addOption(getText("ContextMenu_Wooden_Crate"), worldobjects, ISBuildMenu.onWoodenCrate, square, crateSprite, player);
	local toolTip = ISBuildMenu.canBuild(3,3,0,0,0,3,crateOption, player);
	toolTip:setName(getText("ContextMenu_Wooden_Crate"));
	toolTip.description = getText("Tooltip_craft_woodenCrateDesc") .. toolTip.description;
	toolTip:setTexture(crateSprite.sprite);
	ISBuildMenu.requireHammer(crateOption)
end

ISBuildMenu.onWoodenCrate = function(worldobjects, square, crateSprite, player)
	-- sprite, northSprite
	local crate = ISWoodenContainer:new(crateSprite.sprite, crateSprite.northSprite);
	crate.renderFloorHelper = true
	crate.canBeAlwaysPlaced = true;
    crate.modData["xp:Woodwork"] = 3;
	crate.modData["need:Base.Plank"] = "3";
	crate.modData["need:Base.Nails"] = "3";
	crate:setEastSprite(crateSprite.eastSprite);
	crate.player = player
	getCell():setDrag(crate, player);
end

-- **********************************************
-- **              *FURNITURE*                 **
-- **********************************************

ISBuildMenu.buildFurnitureMenu = function(subMenu, context, option, player)
	-- add the table submenu
	local tableOption = subMenu:addOption(getText("ContextMenu_Table"), worldobjects, nil);
	local subMenuTable = subMenu:getNew(subMenu);
	context:addSubMenu(tableOption, subMenuTable);

	-- add all our table option
	local tableSprite = ISBuildMenu.getWoodenTableSprites(player);
	local smallTableOption = subMenuTable:addOption(getText("ContextMenu_Small_Table"), worldobjects, ISBuildMenu.onSmallWoodTable, square, tableSprite, player);
	local tooltip = ISBuildMenu.canBuild(5,4,0,0,0,3,smallTableOption, player);
	tooltip:setName(getText("ContextMenu_Small_Table"));
	tooltip.description = getText("Tooltip_craft_smallTableDesc") .. tooltip.description;
	tooltip:setTexture(tableSprite.sprite);
	ISBuildMenu.requireHammer(smallTableOption)

	local largeTableSprite = ISBuildMenu.getLargeWoodTableSprites(player);
	local largeTableOption = subMenuTable:addOption(getText("ContextMenu_Large_Table"), worldobjects, ISBuildMenu.onLargeWoodTable, square, largeTableSprite, player);
	local tooltip2 = ISBuildMenu.canBuild(6,4,0,0,0,4,largeTableOption, player);
	tooltip2:setName(getText("ContextMenu_Large_Table"));
	tooltip2.description = getText("Tooltip_craft_largeTableDesc") .. tooltip2.description;
	tooltip2:setTexture(largeTableSprite.sprite1);
	ISBuildMenu.requireHammer(largeTableOption)

	local drawerSprite = ISBuildMenu.getTableWithDrawerSprites(player);
	local drawerTableOption = subMenuTable:addOption(getText("ContextMenu_Table_with_Drawer"), worldobjects, ISBuildMenu.onSmallWoodTableWithDrawer, square, drawerSprite, player);
	local tooltip3 = ISBuildMenu.canBuild(5,4,0,0,0,5,drawerTableOption, player);
	-- we add that we need a Drawer too
	if not getSpecificPlayer(player):getInventory():contains("Drawer") and not ISBuildMenu.cheat then
		tooltip3.description = tooltip3.description .. " <RGB:1,0,0>" .. getItemText("Drawer") .. " 0/1 <LINE>";
		drawerTableOption.onSelect = nil;
		drawerTableOption.notAvailable = true;
	else
		tooltip3.description = tooltip3.description .. " <RGB:1,1,1>" .. getItemText("Drawer") .. " 1/1 <LINE>";
	end
	tooltip3:setName(getText("ContextMenu_Table_with_Drawer"));
	tooltip3.description = getText("Tooltip_craft_tableDrawerDesc") .. tooltip3.description;
	tooltip3:setTexture(drawerSprite.sprite);
	ISBuildMenu.requireHammer(drawerTableOption)

    if smallTableOption.notAvailable and largeTableOption.notAvailable and drawerTableOption.notAvailable then
        tableOption.notAvailable = true;
    end

	-- now the chair
	local chairSprite = ISBuildMenu.getWoodenChairSprites(player);
	local chairOption = subMenu:addOption(getText("ContextMenu_Wooden_Chair"), worldobjects, ISBuildMenu.onWoodChair, square, chairSprite, player);
	local tooltip4 = ISBuildMenu.canBuild(5,4,0,0,0,2,chairOption, player);
	tooltip4:setName(getText("ContextMenu_Wooden_Chair"));
	tooltip4.description = getText("Tooltip_craft_woodenChairDesc") .. tooltip4.description;
	tooltip4:setTexture(chairSprite.northSprite);
	ISBuildMenu.requireHammer(chairOption)

	-- rain collector barrel
	local barrelOption = subMenu:addOption(getText("ContextMenu_Rain_Collector_Barrel"), worldobjects, ISBuildMenu.onCreateBarrel, player, "carpentry_02_54", RainCollectorBarrel.smallWaterMax);
	local tooltip = ISBuildMenu.canBuild(4,4,0,0,0,4,barrelOption, player);
    -- we add that we need 4 garbage bag too
    if ISBuildMenu.countMaterial(player, "Base.Garbagebag") < 4 and not ISBuildMenu.cheat then
        tooltip.description = tooltip.description .. " <RGB:1,0,0>" .. getItemText("Garbage Bag") .. " " .. ISBuildMenu.countMaterial(player, "Base.Garbagebag") .. "/4 ";
        barrelOption.onSelect = nil;
        barrelOption.notAvailable = true;
    else
        tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. getItemText("Garbage Bag") .. " 4/4 ";
    end
	tooltip:setName(getText("ContextMenu_Rain_Collector_Barrel"));
	tooltip.description = getText("Tooltip_craft_rainBarrelDesc") .. tooltip.description;
	tooltip:setTexture("carpentry_02_54");
	ISBuildMenu.requireHammer(barrelOption)

    -- rain collector barrel
    local barrel2Option = subMenu:addOption(getText("ContextMenu_Rain_Collector_Barrel"), worldobjects, ISBuildMenu.onCreateBarrel, player, "carpentry_02_52", RainCollectorBarrel.largeWaterMax);
    local tooltip = ISBuildMenu.canBuild(4,4,0,0,0,7,barrel2Option, player);
    -- we add that we need 4 garbage bag too
    if ISBuildMenu.countMaterial(player, "Base.Garbagebag") < 4 and not ISBuildMenu.cheat then
        tooltip.description = tooltip.description .. " <RGB:1,0,0>" .. getItemText("Garbage Bag") .. " " .. ISBuildMenu.countMaterial(player, "Base.Garbagebag") .. "/4 ";
        barrel2Option.onSelect = nil;
        barrel2Option.notAvailable = true;
    else
        tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. getItemText("Garbage Bag") .. " 4/4 ";
    end
    tooltip:setName(getText("ContextMenu_Rain_Collector_Barrel"));
    tooltip.description = getText("Tooltip_craft_rainBarrelDesc") .. tooltip.description;
    tooltip:setTexture("carpentry_02_52");
	ISBuildMenu.requireHammer(barrel2Option)

    -- compost
    local compostOption = subMenu:addOption(getText("ContextMenu_Compost"), worldobjects, ISBuildMenu.onCompost, player, "camping_01_19");
    local tooltip4 = ISBuildMenu.canBuild(5,4,0,0,0,2,compostOption, player);
    tooltip4:setName(getText("ContextMenu_Compost"));
    tooltip4.description = getText("Tooltip_craft_compostDesc") .. tooltip4.description;
    tooltip4:setTexture("camping_01_19");
    ISBuildMenu.requireHammer(compostOption)

    local bookSprite = ISBuildMenu.getBookcaseSprite(player);
    local bookOption = subMenu:addOption(getText("ContextMenu_Bookcase"), worldobjects, ISBuildMenu.onBookcase, square, bookSprite, player);
    local tooltip5 = ISBuildMenu.canBuild(5,4,0,0,0,5,bookOption, player);
    tooltip5:setName(getText("ContextMenu_Bookcase"));
    tooltip5.description = getText("Tooltip_craft_bookcaseDesc") .. tooltip5.description;
    tooltip5:setTexture(bookSprite.sprite);
	ISBuildMenu.requireHammer(bookOption)

    local book2Sprite = ISBuildMenu.getSmallBookcaseSprite(player);
    local book2Option = subMenu:addOption(getText("ContextMenu_SmallBookcase"), worldobjects, ISBuildMenu.onSmallBookcase, square, book2Sprite, player);
    local tooltip7 = ISBuildMenu.canBuild(3,3,0,0,0,3,book2Option, player);
    tooltip7:setName(getText("ContextMenu_SmallBookcase"));
    tooltip7.description = getText("Tooltip_craft_smallBookcaseDesc") .. tooltip7.description;
    tooltip7:setTexture(book2Sprite.sprite);
	ISBuildMenu.requireHammer(book2Option)

    local shelveSprite = ISBuildMenu.getShelveSprite(player);
    local shelveOption = subMenu:addOption(getText("ContextMenu_Shelves"), worldobjects, ISBuildMenu.onShelve, square, shelveSprite, player);
    local tooltip6 = ISBuildMenu.canBuild(1,2,0,0,0,2,shelveOption, player);
    tooltip6:setName(getText("ContextMenu_Shelves"));
    tooltip6.description = getText("Tooltip_craft_shelvesDesc") .. tooltip6.description;
    tooltip6:setTexture(shelveSprite.sprite);
	ISBuildMenu.requireHammer(shelveOption)

    local shelve2Sprite = ISBuildMenu.getDoubleShelveSprite(player);
    local shelve2Option = subMenu:addOption(getText("ContextMenu_DoubleShelves"), worldobjects, ISBuildMenu.onDoubleShelve, square, shelve2Sprite, player);
    local tooltip8 = ISBuildMenu.canBuild(2,4,0,0,0,2,shelve2Option, player);
    tooltip8:setName(getText("ContextMenu_DoubleShelves"));
    tooltip8.description = getText("Tooltip_craft_doubleShelvesDesc") .. tooltip8.description;
    tooltip8:setTexture(shelve2Sprite.sprite);
	ISBuildMenu.requireHammer(shelve2Option)

    -- bed
    local bedSprite = ISBuildMenu.getBedSprite(player);
    local bedOption = subMenu:addOption(getText("ContextMenu_Bed"), worldobjects, ISBuildMenu.onBed, square, bedSprite, player);
    local tooltip9 = ISBuildMenu.canBuild(6,4,0,0,0,4,bedOption, player);
    -- we add that we need a mattress too
    if ISBuildMenu.countMaterial(player, "Base.Mattress") < 1 and not ISBuildMenu.cheat then
        tooltip9.description = tooltip9.description .. " <RGB:1,0,0>" .. getItemText("Mattress") .. " 0/1 ";
        bedOption.onSelect = nil;
        bedOption.notAvailable = true;
    else
        tooltip9.description = tooltip9.description .. " <RGB:1,1,1>" .. getItemText("Mattress") .. " 1/1 ";
    end
    tooltip9:setName(getText("ContextMenu_Bed"));
    tooltip9.description = getText("Tooltip_craft_bedDesc") .. tooltip9.description;
    tooltip9:setTexture(bedSprite.northSprite1);
	ISBuildMenu.requireHammer(bedOption)

    local signSprite = ISBuildMenu.getSignSprite(player);
    local signOption = subMenu:addOption(getText("ContextMenu_Sign"), worldobjects, ISBuildMenu.onSign, square, signSprite, player);
    local tooltip10 = ISBuildMenu.canBuild(3,3,0,0,0,1,signOption, player);
    tooltip10:setName(getText("ContextMenu_Sign"));
    tooltip10.description = getText("Tooltip_craft_signDesc") .. tooltip10.description;
    tooltip10:setTexture(signSprite.sprite);
	ISBuildMenu.requireHammer(signOption)

    if tableOption.notAvailable and signOption.notAvailable and bedOption.notAvailable and shelve2Option.notAvailable and shelveOption.notAvailable and book2Option.notAvailable and bookOption.notAvailable and barrel2Option.notAvailable and barrelOption.notAvailable and chairOption.notAvailable then
        option.notAvailable = true;
    end
end

-- create a new barrel to drag a ghost render of the barrel under the mouse
ISBuildMenu.onCreateBarrel = function(worldobjects, player, sprite, waterMax)
	local barrel = RainCollectorBarrel:new(player, sprite, waterMax);
	-- we now set his the mod data the needed material
	-- by doing this, all will be automatically consummed, drop on the ground if destoryed etc.
	barrel.modData["need:Base.Plank"] = "4";
	barrel.modData["need:Base.Nails"] = "4";
    barrel.modData["need:Base.Garbagebag"] = "4";
    barrel.modData["xp:Woodwork"] = 5;
    -- and now allow the item to be dragged by mouse
	barrel.player = player
	getCell():setDrag(barrel, player);
end

ISBuildMenu.onCompost = function(worldobjects, player, sprite)
    local compost = ISCompost:new(player, sprite);
    compost.modData["need:Base.Plank"] = "5";
    compost.modData["need:Base.Nails"] = "4";
    compost.modData["xp:Woodwork"] = 5;
    compost.player = player
    compost.notExterior = true;
    getCell():setDrag(compost, player);
end

ISBuildMenu.onBed = function(worldobjects, square, sprite, player)
    local furniture = ISDoubleTileFurniture:new("Bed", sprite.sprite1, sprite.sprite2, sprite.northSprite1, sprite.northSprite2);
    furniture.modData["xp:Woodwork"] = 5;
    furniture.modData["need:Base.Plank"] = "6";
    furniture.modData["need:Base.Nails"] = "4";
    furniture.modData["need:Base.Mattress"] = "1";
    furniture.player = player
    getCell():setDrag(furniture, player);
end

ISBuildMenu.onSmallWoodTable = function(worldobjects, square, sprite, player)
	-- name, sprite, northSprite
	local furniture = ISSimpleFurniture:new("Small Table", sprite.sprite, sprite.sprite);
    furniture.modData["xp:Woodwork"] = 3;
	furniture.modData["need:Base.Plank"] = "5";
	furniture.modData["need:Base.Nails"] = "4";
	furniture.player = player
	getCell():setDrag(furniture, player);
end

ISBuildMenu.onSmallWoodTableWithDrawer = function(worldobjects, square, sprite, player)
	-- name, sprite, northSprite
	local furniture = ISSimpleFurniture:new("Small Table with Drawer", sprite.sprite, sprite.northSprite);
    furniture.modData["xp:Woodwork"] = 5;
	furniture.modData["need:Base.Plank"] = "5";
	furniture.modData["need:Base.Nails"] = "4";
	furniture.modData["need:Base.Drawer"] = "1";
	furniture:setEastSprite(sprite.eastSprite);
	furniture:setSouthSprite(sprite.southSprite);
	furniture.isContainer = true;
	furniture.player = player
	getCell():setDrag(furniture, player);
end

ISBuildMenu.onLargeWoodTable = function(worldobjects, square, sprite, player)
	-- name, sprite, northSprite
	local furniture = ISDoubleTileFurniture:new("Large Table", sprite.sprite1, sprite.sprite2, sprite.northSprite1, sprite.northSprite2);
    furniture.modData["xp:Woodwork"] = 5;
	furniture.modData["need:Base.Plank"] = "6";
	furniture.modData["need:Base.Nails"] = "4";
	furniture.player = player
	getCell():setDrag(furniture, player);
end

ISBuildMenu.onWoodChair = function(worldobjects, square, sprite, player)
	-- name, sprite, northSprite
	local furniture = ISSimpleFurniture:new("Wooden Chair", sprite.sprite, sprite.northSprite);
    furniture.modData["xp:Woodwork"] = 3;
	furniture.modData["need:Base.Plank"] = "5";
	furniture.modData["need:Base.Nails"] = "4";
	-- our chair have 4 tiles (north, east, south and west)
	-- then we define our east and south sprite
	furniture:setEastSprite(sprite.eastSprite);
	furniture:setSouthSprite(sprite.southSprite);
	furniture.canPassThrough = true;
	furniture.player = player
	getCell():setDrag(furniture, player);
end

ISBuildMenu.onBookcase = function(worldobjects, square, sprite, player)
    -- name, sprite, northSprite
    local furniture = ISSimpleFurniture:new("Bookcase", sprite.sprite, sprite.northSprite);
    furniture.canBeAlwaysPlaced = true;
    furniture.isContainer = true;
    furniture.containerType = "shelves";
    furniture.modData["xp:Woodwork"] = 5;
    furniture.modData["need:Base.Plank"] = "5";
    furniture.modData["need:Base.Nails"] = "4";
    -- our chair have 4 tiles (north, east, south and west)
    -- then we define our east and south sprite
    furniture:setEastSprite(sprite.eastSprite);
    furniture:setSouthSprite(sprite.southSprite);
    furniture.player = player
    getCell():setDrag(furniture, player);
end

ISBuildMenu.onSmallBookcase = function(worldobjects, square, sprite, player)
-- name, sprite, northSprite
    local furniture = ISSimpleFurniture:new("Small Bookcase", sprite.sprite, sprite.northSprite);
    furniture.canBeAlwaysPlaced = true;
    furniture.isContainer = true;
    furniture.containerType = "shelves";
    furniture.modData["xp:Woodwork"] = 3;
    furniture.modData["need:Base.Plank"] = "3";
    furniture.modData["need:Base.Nails"] = "3";
    -- our chair have 4 tiles (north, east, south and west)
    -- then we define our east and south sprite
    furniture:setEastSprite(sprite.eastSprite);
    furniture:setSouthSprite(sprite.southSprite);
    furniture.player = player
    getCell():setDrag(furniture, player);
end

ISBuildMenu.onShelve = function(worldobjects, square, sprite, player)
    -- name, sprite, northSprite
    local furniture = ISSimpleFurniture:new("Shelves", sprite.sprite, sprite.northSprite);
    furniture.isContainer = true;
    furniture.needToBeAgainstWall = true;
    furniture.blockAllTheSquare = false;
    furniture.containerType = "shelves";
    furniture.modData["xp:Woodwork"] = 3;
    furniture.modData["need:Base.Plank"] = "1";
    furniture.modData["need:Base.Nails"] = "2";
    furniture.player = player
    getCell():setDrag(furniture, player);
end

ISBuildMenu.onSign = function(worldobjects, square, sprite, player)
-- name, sprite, northSprite
    local furniture = ISSimpleFurniture:new("Wooden Sign", sprite.sprite, sprite.northSprite);
    furniture.blockAllTheSquare = false;
    furniture.modData["xp:Woodwork"] = 3;
    furniture.modData["need:Base.Plank"] = "3";
    furniture.modData["need:Base.Nails"] = "3";
    furniture.player = player
    getCell():setDrag(furniture, player);
end

ISBuildMenu.onDoubleShelve = function(worldobjects, square, sprite, player)
-- name, sprite, northSprite
    local furniture = ISSimpleFurniture:new("Double Shelves", sprite.sprite, sprite.northSprite);
    furniture.isContainer = true;
    furniture.needToBeAgainstWall = true;
    furniture.blockAllTheSquare = false;
    furniture.containerType = "shelves";
    furniture.modData["xp:Woodwork"] = 5;
    furniture.modData["need:Base.Plank"] = "2";
    furniture.modData["need:Base.Nails"] = "4";
    furniture.player = player
    getCell():setDrag(furniture, player);
end

-- **********************************************
-- **                 *STAIRS*                 **
-- **********************************************

ISBuildMenu.buildStairsMenu = function(subMenu, player)
--	local darkStairsOption = subMenu:addOption(getText("ContextMenu_Dark_Wooden_Stairs"), worldobjects, ISBuildMenu.onDarkWoodenStairs, square, player);
--	local tooltip = ISBuildMenu.canBuild(8,8,0,0,0,3,darkStairsOption, player);
--	tooltip:setName(getText("ContextMenu_Dark_Wooden_Stairs"));
--	tooltip.description = getText("Tooltip_craft_stairsDesc") .. tooltip.description;
--	tooltip:setTexture("fixtures_stairs_01_16");

	local stairsOption = subMenu:addOption(getText("ContextMenu_Stairs"), worldobjects, ISBuildMenu.onBrownWoodenStairs, square, player);
	local tooltip2 = ISBuildMenu.canBuild(15,15,0,0,0,6,stairsOption, player);
	tooltip2:setName(getText("ContextMenu_Stairs"));
	tooltip2.description = getText("Tooltip_craft_stairsDesc") .. tooltip2.description;
	tooltip2:setTexture("carpentry_02_88");
	ISBuildMenu.requireHammer(stairsOption)

--	local lightStairsOption = subMenu:addOption(getText("ContextMenu_Light_Brown_Wooden_Stairs"), worldobjects, ISBuildMenu.onLightBrownWoodenStairs, square, player);
--	local tooltip3 = ISBuildMenu.canBuild(8,8,0,0,0,3,lightStairsOption, player);
--	tooltip3:setName(getText("ContextMenu_Light_Brown_Wooden_Stairs"));
--	tooltip3.description = getText("Tooltip_craft_stairsDesc") .. tooltip3.description;
--	tooltip3:setTexture("fixtures_stairs_01_32");

--    if darkStairsOption.notAvailable and stairsOption.notAvailable and lightStairsOption.notAvailable then
--        option.notAvailable = true;
--    end
end

ISBuildMenu.onDarkWoodenStairs = function(worldobjects, square, player)
	local stairs = ISWoodenStairs:new("fixtures_stairs_01_16", "fixtures_stairs_01_17", "fixtures_stairs_01_18", "fixtures_stairs_01_24", "fixtures_stairs_01_25", "fixtures_stairs_01_26", "fixtures_stairs_01_22", "fixtures_stairs_01_23");
    stairs.modData["xp:Woodwork"] = 5;
	stairs.modData["need:Base.Plank"] = "8";
	stairs.modData["need:Base.Nails"] = "8";
    stairs.isThumpable = false;
    stairs.player = player
    getCell():setDrag(stairs, player);
end

ISBuildMenu.onBrownWoodenStairs = function(worldobjects, square, player)
    local stairs = ISWoodenStairs:new("carpentry_02_88", "carpentry_02_89", "carpentry_02_90", "carpentry_02_96", "carpentry_02_97", "carpentry_02_98", "carpentry_02_94", "carpentry_02_95");
    stairs.modData["xp:Woodwork"] = 5;
    stairs.modData["need:Base.Plank"] = "15";
    stairs.modData["need:Base.Nails"] = "15";
    stairs.player = player
    getCell():setDrag(stairs, player);
end

ISBuildMenu.onLightBrownWoodenStairs = function(worldobjects, square, player)
    local stairs = ISWoodenStairs:new("fixtures_stairs_01_32", "fixtures_stairs_01_33", "fixtures_stairs_01_34", "fixtures_stairs_01_40", "fixtures_stairs_01_41", "fixtures_stairs_01_42", "fixtures_stairs_01_38", "fixtures_stairs_01_39");
    stairs.modData["xp:Woodwork"] = 5;
    stairs.modData["need:Base.Plank"] = "8";
	stairs.modData["need:Base.Nails"] = "8";
    stairs.player = player
    getCell():setDrag(stairs, player);
end

-- **********************************************
-- **                 *DOOR*                   **
-- **********************************************

ISBuildMenu.buildDoorMenu = function(subMenu, option, player)
	local frameSprite = ISBuildMenu.getWoodenDoorFrameSprites(player);
	local doorFrameOption = subMenu:addOption(getText("ContextMenu_Door_Frame"), worldobjects, ISBuildMenu.onWoodenDoorFrame, square, frameSprite, player);
	local tooltip = ISBuildMenu.canBuild(4,4,0,0,0,2,doorFrameOption, player);
	tooltip:setName(getText("ContextMenu_Door_Frame"));
	tooltip.description = getText("Tooltip_craft_doorFrameDesc") .. tooltip.description;
	tooltip:setTexture(frameSprite.sprite);
	ISBuildMenu.requireHammer(doorFrameOption)
	
	local sprite = ISBuildMenu.getWoodenDoorSprites(player);
	local doorsOption = subMenu:addOption(getText("ContextMenu_Wooden_Door"), worldobjects, ISBuildMenu.onWoodenDoor, square, sprite, player);
	local tooltip = ISBuildMenu.canBuild(4,4,2,1,0,3,doorsOption, player);
	tooltip:setName(getText("ContextMenu_Wooden_Door"));
	tooltip.description = getText("Tooltip_craft_woodenDoorDesc") .. tooltip.description;
	tooltip:setTexture(sprite.sprite);
	ISBuildMenu.requireHammer(doorsOption)
	
	local sprite = {};
	sprite.sprite = "fixtures_doors_fences_01_";

	local doorsOption2 = subMenu:addOption(getText("ContextMenu_Double_Wooden_Door"), worldobjects, ISBuildMenu.onDoubleWoodenDoor, square, sprite, 104, player);
	local tooltip = ISBuildMenu.canBuild(12,12,4,2,0,6,doorsOption2, player);
	tooltip:setName(getText("ContextMenu_Double_Wooden_Door"));
	tooltip.description = getText("Tooltip_craft_doubleWoodenDoorDesc") .. tooltip.description;
	tooltip:setTexture(sprite.sprite .. "105");
	ISBuildMenu.requireHammer(doorsOption2)
	
	if doorsOption.notAvailable and doorsOption2.notAvailable then
		option.notAvailable = true;
	end
end

ISBuildMenu.onDoubleWoodenDoor = function(worldobjects, square, sprite, spriteIndex, player)
	local door = ISDoubleDoor:new(sprite.sprite, spriteIndex);
	door.modData["xp:Woodwork"] = 6;
	door.modData["need:Base.Plank"] = "12";
	door.modData["need:Base.Nails"] = "12";
	door.modData["need:Base.Hinge"] = "4";
	door.modData["need:Base.Doorknob"] = "2";
	door.player = player
	getCell():setDrag(door, player);
end

ISBuildMenu.onWoodenDoor = function(worldobjects, square, sprite, player)
	-- sprite, northsprite, openSprite, openNorthSprite
	local door = ISWoodenDoor:new(sprite.sprite, sprite.northSprite, sprite.openSprite, sprite.openNorthSprite);
    door.modData["xp:Woodwork"] = 3;
	door.modData["need:Base.Plank"] = "4";
	door.modData["need:Base.Nails"] = "4";
	door.modData["need:Base.Hinge"] = "2";
	door.modData["need:Base.Doorknob"] = "1";
	door.player = player
	getCell():setDrag(door, player);
end

ISBuildMenu.onFarmDoor = function(worldobjects, square, player)
	-- sprite, northsprite, openSprite, openNorthSprite
	getCell():setDrag(ISWoodenDoor:new("TileDoors_8", "TileDoors_9", "TileDoors_10", "TileDoors_11"), player);
end

-- **********************************************
-- **              *DOOR FRAME*                **
-- **********************************************

ISBuildMenu.buildDoorFrameMenu = function(subMenu, player)
	local frameSprite = ISBuildMenu.getWoodenDoorFrameSprites(player);
	local doorFrameOption = subMenu:addOption(getText("ContextMenu_Door_Frame"), worldobjects, ISBuildMenu.onWoodenDoorFrame, square, frameSprite, player);
	local tooltip = ISBuildMenu.canBuild(4,4,0,0,0,2,doorFrameOption, player);
	tooltip:setName(getText("ContextMenu_Door_Frame"));
	tooltip.description = getText("Tooltip_craft_doorFrameDesc") .. tooltip.description;
	tooltip:setTexture(frameSprite.sprite);
	ISBuildMenu.requireHammer(doorFrameOption)
end

ISBuildMenu.onWoodenDoorFrame = function(worldobjects, square, sprite, player)
	-- sprite, northSprite, corner
	local doorFrame = ISWoodenDoorFrame:new(sprite.sprite, sprite.northSprite, sprite.corner)
    if getSpecificPlayer(player):getPerkLevel(Perks.Woodwork) >= 8 then
	    doorFrame.canBePlastered = true;
    end
    doorFrame.modData["xp:Woodwork"] = 5;
    doorFrame.modData["wallType"] = "doorframe";
	doorFrame.modData["need:Base.Plank"] = "4";
	doorFrame.modData["need:Base.Nails"] = "4";
	doorFrame.player = player
	doorFrame.name = "WoodenDoorFrameLvl" .. ISBuildMenu.getSpriteLvl(player);
	getCell():setDrag(doorFrame, player);
end

-- **********************************************
-- **            SPRITE FUNCTIONS              **
-- **********************************************

ISBuildMenu.getBedSprite = function(player)
    local sprite = {};
    sprite.sprite1 = "carpentry_02_73";
    sprite.sprite2 = "carpentry_02_72";
    sprite.northSprite1 = "carpentry_02_74";
    sprite.northSprite2 = "carpentry_02_75";
    return sprite;
end

ISBuildMenu.getLargeWoodTableSprites = function(player)
	local spriteLvl = ISBuildMenu.getSpriteLvl(player);
	local sprite = {};
	if spriteLvl == 1 then
		sprite.sprite1 = "carpentry_01_25";
		sprite.sprite2 = "carpentry_01_24";
		sprite.northSprite1 = "carpentry_01_26";
		sprite.northSprite2 = "carpentry_01_27";
	elseif spriteLvl == 2 then
		sprite.sprite1 = "carpentry_01_29";
		sprite.sprite2 = "carpentry_01_28";
		sprite.northSprite1 = "carpentry_01_30";
		sprite.northSprite2 = "carpentry_01_31";
	else
		sprite.sprite1 = "carpentry_01_33";
		sprite.sprite2 = "carpentry_01_32";
		sprite.northSprite1 = "carpentry_01_34";
		sprite.northSprite2 = "carpentry_01_35";
	end
	return sprite;
end

ISBuildMenu.getTableWithDrawerSprites = function(player)
	local spriteLvl = ISBuildMenu.getSpriteLvl(player);
	local sprite = {};
	if spriteLvl == 1 then
		sprite.sprite = "carpentry_02_0";
		sprite.northSprite = "carpentry_02_2";
		sprite.southSprite = "carpentry_02_1";
		sprite.eastSprite = "carpentry_02_3";
	elseif spriteLvl == 2 then
		sprite.sprite = "carpentry_02_4";
		sprite.northSprite = "carpentry_02_6";
		sprite.southSprite = "carpentry_02_5";
		sprite.eastSprite = "carpentry_02_7";
	else
		sprite.sprite = "carpentry_02_8";
		sprite.northSprite = "carpentry_02_10";
		sprite.southSprite = "carpentry_02_9";
		sprite.eastSprite = "carpentry_02_11";
	end
	return sprite;
end

ISBuildMenu.getWoodenFenceSprites = function(player)
	local spriteLvl = ISBuildMenu.getSpriteLvl(player);
	local sprite = {};
	if spriteLvl == 1 then
		sprite.sprite = "carpentry_02_40";
		sprite.northSprite = "carpentry_02_41";
		sprite.corner = "carpentry_02_43";
	elseif spriteLvl == 2 then
		sprite.sprite = "carpentry_02_44";
		sprite.northSprite = "carpentry_02_45";
		sprite.corner = "carpentry_02_47";
	else
		sprite.sprite = "carpentry_02_48";
		sprite.northSprite = "carpentry_02_49";
		sprite.corner = "carpentry_02_51";
	end
	return sprite;
end

ISBuildMenu.getWoodenFloorSprites = function(player)
    local spriteLvl = ISBuildMenu.getSpriteLvl(player);
    local sprite = {};
    if spriteLvl == 1 then
        sprite.sprite = "carpentry_02_58";
        sprite.northSprite = "carpentry_02_58";
    elseif spriteLvl == 2 then
        sprite.sprite = "carpentry_02_57";
        sprite.northSprite = "carpentry_02_57";
    else
        sprite.sprite = "carpentry_02_56";
        sprite.northSprite = "carpentry_02_56";
    end
    if ISBuildMenu.cheat then
        sprite.sprite = "carpentry_02_56";
        sprite.northSprite = "carpentry_02_56";
    end
    return sprite;
end

ISBuildMenu.getWoodenCrateSprites = function(player)
    local spriteLvl = ISBuildMenu.getSpriteLvl(player);
    local sprite = {};
    if spriteLvl <= 2 then
        sprite.sprite = "carpentry_01_19";
        sprite.northSprite = "carpentry_01_19";
    else
        sprite.sprite = "carpentry_01_16";
        sprite.northSprite = "carpentry_01_16";
    end
    return sprite;
end

ISBuildMenu.getWoodenChairSprites = function(player)
	local spriteLvl = ISBuildMenu.getSpriteLvl(player);
	local sprite = {};
	if spriteLvl == 1 then
		sprite.sprite = "carpentry_01_36";
		sprite.northSprite = "carpentry_01_38";
		sprite.southSprite = "carpentry_01_39";
		sprite.eastSprite = "carpentry_01_37";
	elseif spriteLvl == 2 then
		sprite.sprite = "carpentry_01_40";
		sprite.northSprite = "carpentry_01_42";
		sprite.southSprite = "carpentry_01_41";
		sprite.eastSprite = "carpentry_01_43";
	else
		sprite.sprite = "carpentry_01_45";
		sprite.northSprite = "carpentry_01_44";
		sprite.southSprite = "carpentry_01_46";
		sprite.eastSprite = "carpentry_01_47";
	end
	return sprite;
end

ISBuildMenu.getWoodenDoorSprites = function(player)
	local spriteLvl = ISBuildMenu.getSpriteLvl(player);
	local sprite = {};
	if spriteLvl == 1 then
		sprite.sprite = "carpentry_01_48";
		sprite.northSprite = "carpentry_01_49";
		sprite.openSprite = "carpentry_01_50";
		sprite.openNorthSprite = "carpentry_01_51";
	elseif spriteLvl == 2 then
		sprite.sprite = "carpentry_01_52";
		sprite.northSprite = "carpentry_01_53";
		sprite.openSprite = "carpentry_01_54";
		sprite.openNorthSprite = "carpentry_01_55";
	else
		sprite.sprite = "carpentry_01_56";
		sprite.northSprite = "carpentry_01_57";
		sprite.openSprite = "carpentry_01_58";
		sprite.openNorthSprite = "carpentry_01_59";
	end
	return sprite;
end

ISBuildMenu.getWoodenTableSprites = function(player)
	local spriteLvl = ISBuildMenu.getSpriteLvl(player);
	local sprite = {};
	if spriteLvl == 1 then
		sprite.sprite = "carpentry_01_60";
	elseif spriteLvl == 2 then
		sprite.sprite = "carpentry_01_61";
	else
		sprite.sprite = "carpentry_01_62";
	end
	return sprite;
end

ISBuildMenu.getSmallBookcaseSprite = function(player)
    local sprite = {};
    sprite.sprite = "furniture_shelving_01_23";
    sprite.northSprite = "furniture_shelving_01_19";
    return sprite;
end

ISBuildMenu.getBookcaseSprite = function(player)
    local spriteLvl = ISBuildMenu.getSpriteLvl(player);
    local sprite = {};
    if spriteLvl <= 2 then
        sprite.northSprite = "carpentry_02_64";
        sprite.sprite = "carpentry_02_65";
        sprite.eastSprite = "carpentry_02_66";
        sprite.southSprite = "carpentry_02_67";
    else
        sprite.northSprite = "furniture_shelving_01_40";
        sprite.sprite = "furniture_shelving_01_41";
        sprite.eastSprite = "furniture_shelving_01_42";
        sprite.southSprite = "furniture_shelving_01_43";
    end
    return sprite;
end

ISBuildMenu.getSignSprite = function(player)
    local sprite = {};
    sprite.sprite = "constructedobjects_signs_01_27";
    sprite.northSprite = "constructedobjects_signs_01_11";
    return sprite;
end

ISBuildMenu.getDoubleShelveSprite = function(player)
    local sprite = {};
    sprite.sprite = "furniture_shelving_01_2";
    sprite.northSprite = "furniture_shelving_01_1";
    return sprite;
end

ISBuildMenu.getShelveSprite = function(player)
    local sprite = {};
    sprite.sprite = "carpentry_02_68";
    sprite.northSprite = "carpentry_02_69";
    return sprite;
end

ISBuildMenu.getPillarLampSprite = function(player)
    local sprite = {};
    sprite.sprite = "carpentry_02_61";
    sprite.northSprite = "carpentry_02_60";
    sprite.southSprite = "carpentry_02_59";
    sprite.eastSprite = "carpentry_02_62";
    return sprite;
end

ISBuildMenu.getWoodenWallFrameSprites = function(player)
    local sprite = {};
    sprite.sprite = "carpentry_02_100";
    sprite.northSprite = "carpentry_02_101";
    return sprite;
end

ISBuildMenu.getWoodenWallSprites = function(player)
	local spriteLvl = ISBuildMenu.getSpriteLvl(player);
	local sprite = {};
	if spriteLvl == 1 then
		sprite.sprite = "walls_exterior_wooden_01_44";
		sprite.northSprite = "walls_exterior_wooden_01_45";
	elseif spriteLvl == 2 then
		sprite.sprite = "walls_exterior_wooden_01_40";
		sprite.northSprite = "walls_exterior_wooden_01_41";
	else
		sprite.sprite = "walls_exterior_wooden_01_24";
		sprite.northSprite = "walls_exterior_wooden_01_25";
    end
    if ISBuildMenu.cheat then
        sprite.sprite = "walls_exterior_wooden_01_24";
        sprite.northSprite = "walls_exterior_wooden_01_25";
    end
	sprite.corner = "walls_exterior_wooden_01_27";
	return sprite;
end

ISBuildMenu.getWoodenWindowsFrameSprites = function(player)
	local spriteLvl = ISBuildMenu.getSpriteLvl(player);
	local sprite = {};
	if spriteLvl == 1 then
		sprite.sprite = "walls_exterior_wooden_01_52";
		sprite.northSprite = "walls_exterior_wooden_01_53";
	elseif spriteLvl == 2 then
		sprite.sprite = "walls_exterior_wooden_01_48";
		sprite.northSprite = "walls_exterior_wooden_01_49";
	else
		sprite.sprite = "walls_exterior_wooden_01_32";
		sprite.northSprite = "walls_exterior_wooden_01_33";
	end
	sprite.corner = "walls_exterior_wooden_01_27";
	return sprite;
end

ISBuildMenu.getWoodenDoorFrameSprites = function(player)
	local spriteLvl = ISBuildMenu.getSpriteLvl(player);
	local sprite = {};
	if spriteLvl == 1 then
		sprite.sprite = "walls_exterior_wooden_01_54";
		sprite.northSprite = "walls_exterior_wooden_01_55";
	elseif spriteLvl == 2 then
		sprite.sprite = "walls_exterior_wooden_01_50";
		sprite.northSprite = "walls_exterior_wooden_01_51";
	else
		sprite.sprite = "walls_exterior_wooden_01_34";
		sprite.northSprite = "walls_exterior_wooden_01_35";
	end
	sprite.corner = "walls_exterior_wooden_01_27";
	return sprite;
end

ISBuildMenu.getBarCornerSprites = function(player)
	local spriteLvl = ISBuildMenu.getSpriteLvl(player);
	local sprite = {};
	if spriteLvl == 1 then
		sprite.southSprite = "carpentry_02_32";
		sprite.sprite = "carpentry_02_34";
		sprite.northSprite = "carpentry_02_36";
		sprite.eastSprite = "carpentry_02_38";
	elseif spriteLvl == 2 then
		sprite.southSprite = "carpentry_02_24";
		sprite.sprite = "carpentry_02_26";
		sprite.northSprite = "carpentry_02_28";
		sprite.eastSprite = "carpentry_02_30";
	else
		sprite.southSprite = "carpentry_02_16";
		sprite.sprite = "carpentry_02_18";
		sprite.northSprite = "carpentry_02_20";
		sprite.eastSprite = "carpentry_02_22";
	end
	return sprite;
end

ISBuildMenu.getBarElementSprites = function(player)
	local spriteLvl = ISBuildMenu.getSpriteLvl(player);
	local sprite = {};
	if spriteLvl == 1 then
		sprite.southSprite = "carpentry_02_33";
		sprite.sprite = "carpentry_02_35";
		sprite.northSprite = "carpentry_02_37";
		sprite.eastSprite = "carpentry_02_39";
	elseif spriteLvl == 2 then
		sprite.southSprite = "carpentry_02_25";
		sprite.sprite = "carpentry_02_27";
		sprite.northSprite = "carpentry_02_29";
		sprite.eastSprite = "carpentry_02_31";
	else
		sprite.southSprite = "carpentry_02_17";
		sprite.sprite = "carpentry_02_19";
		sprite.northSprite = "carpentry_02_21";
		sprite.eastSprite = "carpentry_02_23";
	end
	return sprite;
end

ISBuildMenu.getSpriteLvl = function(player)
	-- 0 to 1 wood work xp mean lvl 1 sprite
	if getSpecificPlayer(player):getPerkLevel(Perks.Woodwork) <= 3 then
		return 1;
	-- 2 to 3 wood work xp mean lvl 2 sprite
	elseif getSpecificPlayer(player):getPerkLevel(Perks.Woodwork) <= 6 then
		return 2;
	-- 4 to 5 wood work xp mean lvl 3 sprite
	else
		return 3;
	end
end

-- **********************************************
-- **                DISMANTLE                 **
-- **********************************************

ISBuildMenu.onDismantle = function(worldobjects, player)
	local bo = ISDestroyCursor:new(player, true)
	getCell():setDrag(bo, bo.player)
end

-- **********************************************
-- **                  OTHER                   **
-- **********************************************

-- Create our toolTip, depending on the required material
ISBuildMenu.canBuild = function(plankNb, nailsNb, hingeNb, doorknobNb, baredWireNb, carpentrySkill, option, player)
	-- create a new tooltip
	local tooltip = ISBuildMenu.addToolTip();
	-- add it to our current option
	option.toolTip = tooltip;
	local result = true;
	if ISBuildMenu.cheat then
		return tooltip;
	end
	tooltip.description = "<LINE> <LINE>" .. getText("Tooltip_craft_Needs") .. ": <LINE>";
	-- now we gonna test all the needed material, if we don't have it, they'll be in red into our toolip
	if ISBuildMenu.planks < plankNb then
		tooltip.description = tooltip.description .. " <RGB:1,0,0>" .. getItemText("Plank") .. " " .. ISBuildMenu.planks .. "/" .. plankNb .. " <LINE>";
		result = false;
	elseif plankNb > 0 then
		tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. getItemText("Plank") .. " " .. plankNb .. " <LINE>";
	end
	if ISBuildMenu.nails < nailsNb then
		tooltip.description = tooltip.description .. " <RGB:1,0,0>" .. getItemText("Nails") .. " " .. ISBuildMenu.nails .. "/" .. nailsNb .. " <LINE>";
		result = false;
	elseif nailsNb > 0 then
		tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. getItemText("Nails") .. " " .. nailsNb .. " <LINE>";
	end
	if ISBuildMenu.doorknob < doorknobNb then
		tooltip.description = tooltip.description .. " <RGB:1,0,0>" .. getItemText("Doorknob") .. " " .. ISBuildMenu.doorknob .. "/" .. doorknobNb .. " <LINE>";
		result = false;
	elseif doorknobNb > 0 then
		tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. getItemText("Doorknob") .. " " .. doorknobNb .. " <LINE>";
	end
	if ISBuildMenu.hinge < hingeNb then
		tooltip.description = tooltip.description .. " <RGB:1,0,0>" .. getItemText("Door Hinge") .. " " .. ISBuildMenu.hinge .. "/" .. hingeNb .. " <LINE>";
		result = false;
	elseif hingeNb > 0 then
		tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. getItemText("Door Hinge") .. " " .. hingeNb .. " <LINE>";
	end
	if getSpecificPlayer(player):getPerkLevel(Perks.Woodwork) < carpentrySkill then
		tooltip.description = tooltip.description .. " <RGB:1,0,0>" .. getText("IGUI_perks_Carpentry") .. " " .. getSpecificPlayer(player):getPerkLevel(Perks.Woodwork) .. "/" .. carpentrySkill .. " <LINE>";
		result = false;
	elseif carpentrySkill > 0 then
		tooltip.description = tooltip.description .. " <RGB:1,1,1>" .. getText("IGUI_perks_Carpentry") .. " " .. carpentrySkill .. " <LINE>";
	end
	if not result then
		option.onSelect = nil;
		option.notAvailable = true;
	end
	tooltip.description = " " .. tooltip.description .. " "
	return tooltip;
end

ISBuildMenu.addToolTip = function()
	local toolTip = ISToolTip:new();
	toolTip:initialise();
	toolTip:setVisible(false);
	toolTip.footNote = getText("Tooltip_craft_pressToRotate", Keyboard.getKeyName(getCore():getKey("Rotate building")))
	return toolTip;
end

ISBuildMenu.countMaterial = function(player, fullType)
    local inv = getSpecificPlayer(player):getInventory()
    local count = 0
    local items = inv:getItemsFromFullType(fullType, true)
    for i=1,items:size() do
        local item = items:get(i-1)
        if not instanceof(item, "InventoryContainer") or item:getInventory():getItems():isEmpty() then
            count = count + 1
        end
    end
    local type = string.split(fullType, "\\.")[2]
    for k,v in pairs(ISBuildMenu.materialOnGround) do
        if k == type then count = count + v end
    end
    return count
end

ISBuildMenu.requireHammer = function(option)
	if not ISBuildMenu.hasHammer and not ISBuildMenu.cheat then
		option.notAvailable = true
		option.onSelect = nil
	end
end

Events.OnFillWorldObjectContextMenu.Add(ISBuildMenu.doBuildMenu);
