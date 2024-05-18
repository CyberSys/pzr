--***********************************************************
--**                    ROBERT JOHNSON                     **
--**       Contextual menu for building stuff when clicking in the inventory        **
--***********************************************************

ISInventoryBuildMenu = {};

ISInventoryBuildMenu.doBuildMenu = function(player, context, worldobjects, test)

	if test and ISWorldObjectContextMenu.Test then return true end

    local gravelBag = {};
	local sandBag = {};
    local dirtBag = {};
    local notFullGravel = {}
    local notFullSand = {}
    local notFullDirt = {}
    local shovel = nil;
    local fillWithGravel = nil;
    local fillWithSand = nil;
    local fillWithDirt = nil;

    local playerInv = getSpecificPlayer(player):getInventory();

    -- do the spill things menu
	for i=0, playerInv:getItems():size() - 1 do
		local testItem = playerInv:getItems():get(i);
		if testItem:getType() == "Gravelbag" then
			table.insert(gravelBag, testItem)
			if testItem:getUsedDelta() + testItem:getUseDelta() <= 1 then
				table.insert(notFullGravel, testItem)
			end
		end
		if testItem:getType() == "Sandbag" then
			table.insert(sandBag, testItem);
			if testItem:getUsedDelta() + testItem:getUseDelta() <= 1 then
				table.insert(notFullSand, testItem)
			end
        end
        if testItem:getType() == "Dirtbag" then
            table.insert(dirtBag, testItem);
			if testItem:getUsedDelta() + testItem:getUseDelta() <= 1 then
				table.insert(notFullDirt, testItem)
			end
        end
        if testItem:getType() == "EmptySandbag" and testItem:getInventory():getItems():size() == 0 then
            table.insert(notFullGravel, testItem)
            table.insert(notFullSand, testItem)
            table.insert(notFullDirt, testItem)
        end
        if testItem:getType() == "Shovel" then
            shovel = testItem;
        end
    end

    -- do the fill bags with thing menu
    if shovel then
		local squares = {}
        for j=#worldobjects,1,-1 do
			local v = worldobjects[j]
			if v:getSquare() then
				local dup = false
				for i=1,#squares do
					if squares[i] == v:getSquare() then dup = true; break end
				end
				if not dup then table.insert(squares, v:getSquare()) end
			end
		end
		for i=1,#squares do
			for j=0,squares[i]:getObjects():size()-1 do
				local v = squares[i]:getObjects():get(j)
				if v:getSprite() and v:getSprite():getName() then
					local spriteName = v:getSprite():getName()
					if (#notFullGravel > 0) and spriteName == "floors_exterior_natural_01_13" or
							spriteName == "blends_street_01_55" or
							spriteName == "blends_street_01_54" or
							spriteName == "blends_street_01_53" or
							spriteName == "blends_street_01_48" then
						fillWithGravel = v;
					elseif (#notFullSand > 0) and (spriteName == "blends_natural_01_0" or
								spriteName == "blends_natural_01_5" or
								spriteName == "blends_natural_01_6" or
								spriteName == "blends_natural_01_7" or
								spriteName == "floors_exterior_natural_01_24") then
						fillWithSand = v;
					elseif (#notFullDirt > 0) and (luautils.stringStarts(spriteName, "blends_natural_01_") or
								luautils.stringStarts(spriteName, "floors_exterior_natural")) then
						fillWithDirt = v;
					end
				end
			end
        end
    end

	if #gravelBag > 0 then
		if test then return ISWorldObjectContextMenu.setTest() end
		local option = context:addOption(getText("ContextMenu_Spill_Gravel"), worldobjects, nil);
		local subMenu = ISContextMenu:getNew(context);
		context:addSubMenu(option, subMenu)
		for _,item in ipairs(gravelBag) do
			subMenu:addOption(item:getName(), item, ISInventoryBuildMenu.onSpillGravel, getSpecificPlayer(player))
		end
    end
	if #sandBag > 0 then
		if test then return ISWorldObjectContextMenu.setTest() end
		local option = context:addOption(getText("ContextMenu_Spill_Sand"), worldobjects, nil);
		local subMenu = ISContextMenu:getNew(context);
		context:addSubMenu(option, subMenu)
		for _,item in ipairs(sandBag) do
			subMenu:addOption(item:getName(), item, ISInventoryBuildMenu.onSpillSand, getSpecificPlayer(player))
		end
    end
    if #dirtBag > 0 then
		if test then return ISWorldObjectContextMenu.setTest() end
		local option = context:addOption(getText("ContextMenu_Spill_Dirt"), worldobjects, nil);
		local subMenu = ISContextMenu:getNew(context);
		context:addSubMenu(option, subMenu)
		for _,item in ipairs(dirtBag) do
			subMenu:addOption(item:getName(), item, ISInventoryBuildMenu.onSpillDirt, getSpecificPlayer(player))
		end
    end

    if fillWithSand then
		if test then return ISWorldObjectContextMenu.setTest() end
		local option = context:addOption(getText("ContextMenu_Take_some_sands"), worldobjects, nil);
		local subMenu = ISContextMenu:getNew(context);
		context:addSubMenu(option, subMenu)
		for _,item in ipairs(notFullSand) do
			subMenu:addOption(item:getName(), fillWithSand, ISInventoryBuildMenu.onTakeThing, getSpecificPlayer(player), item, "Base.Sandbag");
		end
    end
    if fillWithGravel then
		if test then return ISWorldObjectContextMenu.setTest() end
		local option = context:addOption(getText("ContextMenu_Take_some_gravel"), worldobjects, nil);
		local subMenu = ISContextMenu:getNew(context);
		context:addSubMenu(option, subMenu)
		for _,item in ipairs(notFullGravel) do
			subMenu:addOption(item:getName(), fillWithGravel, ISInventoryBuildMenu.onTakeThing, getSpecificPlayer(player), item, "Base.Gravelbag");
		end
    end
    if fillWithDirt then
		if test then return ISWorldObjectContextMenu.setTest() end
		local option = context:addOption(getText("ContextMenu_Take_some_dirt"), worldobjects, nil);
		local subMenu = ISContextMenu:getNew(context);
		context:addSubMenu(option, subMenu)
		for _,item in ipairs(notFullDirt) do
			subMenu:addOption(item:getName(), fillWithDirt, ISInventoryBuildMenu.onTakeThing, getSpecificPlayer(player), item, "Base.Dirtbag");
		end
    end
end

ISInventoryBuildMenu.onTakeThing = function(object, player, emptyBag, newItem)
	if luautils.walkAdj(player, object:getSquare()) then
    ISWorldObjectContextMenu.equip(player, player:getPrimaryHandItem(), "Shovel", true);
		ISTimedActionQueue.add(ISShovelGround:new(player, emptyBag, object, "blends_natural_01_64", newItem));
	end
end

ISInventoryBuildMenu.onSpillGravel = function(gravelBag, player)
    -- sprite, northSprite, item to use
	getCell():setDrag(ISNaturalFloor:new("floors_exterior_natural_01_13", "floors_exterior_natural_01_13", gravelBag, player), player:getPlayerNum());
end

ISInventoryBuildMenu.onSpillSand = function(sandBag, player)
    -- sprite, northSprite, item to use
	getCell():setDrag(ISNaturalFloor:new("floors_exterior_natural_01_24", "floors_exterior_natural_01_24", sandBag, player), player:getPlayerNum());
end

ISInventoryBuildMenu.onSpillDirt = function(dirtBag, player)
-- sprite, northSprite, item to use
    getCell():setDrag(ISNaturalFloor:new("blends_natural_01_64", "blends_natural_01_64", dirtBag, player), player:getPlayerNum());
end


Events.OnFillWorldObjectContextMenu.Add(ISInventoryBuildMenu.doBuildMenu);
