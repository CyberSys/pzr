--***********************************************************
--**                LEMMY/ROBERT JOHNSON                   **
--***********************************************************


ItemPicker = {}
ItemPicker.zombieDensityCap = 8;
ItemPicker.player = nil;

ItemPicker.fillContainer = function(container, player)
    ItemPicker.player = player;
    if isClient() then
        return;
    end
    --print("filling container");
    if container == nil then
        --print("no container found");
        return;
    end
    --print(container);
    --print(container.getSourceGrid);
    local sq = container:getSourceGrid();

    --print(sq);
    if sq == nil then
--~        print("square not found for container");
       return;
    end

	local room = sq:getRoom();
    local roomName = "all";
    if room ~= nil then
--        		print("you're in room : " .. room:getName());
        roomName = room:getName();
    end
--	print("opening container : " .. container:getType());
    -- specif for zombie container, we don't seek any room for them, just use "all"
    if container:getType() == "inventorymale" or container:getType() == "inventoryfemale" then
        local containerDist = SuburbsDistributions["all"][container:getType()];
        ItemPicker.rollItem(containerDist, container, true, player);
        triggerEvent("OnFillContainer", "all" ,container:getType(), container);
        return;
    end

	-- first, we look for the "all" room distribution
	local roomDist = SuburbsDistributions["all"];
	-- if a specific distribution exist for this room and this type of container, we're not doing the "all" distribution
    if room ~= nil and SuburbsDistributions[room:getName()] ~= nil then
--		print("found this room " .. room:getName());
		-- found the room in SuburbsDistributions
		local roomDist2 = SuburbsDistributions[room:getName()];
		local containerDist = roomDist2[container:getType()] or roomDist2["other"];
        if not containerDist then
            containerDist = roomDist2["all"];
            roomName = "all";
        end
--        print ("room: "..room:getName().. " container: "..container:getType());
--		but haven't found any specific distribution for this type of container, so we do the generic one
		if containerDist == nil then
--			print("do generic distrib 1");
			ItemPicker.fillContainerType(roomDist, container, room:getName(), player);
            triggerEvent("OnFillContainer", room:getName(),container:getType(), container);
			return;
		end
    else
        local roomName = nil;

        if room ~= nil then
            roomName = room:getName();
--~             print ("all: "..roomName.. " container: "..container:getType());
        else
            roomName = "all";
--~             print ("all container: "..container:getType());
        end

--        print("do generic distrib 2");
		ItemPicker.fillContainerType(roomDist, container, roomName, player);
        triggerEvent("OnFillContainer", roomName, container:getType(), container);
		return;
	end

    if room == nil then
--~ 		print("can't find room");
        return;
    end
   -- print("found room " .. room:getName());

	-- then the specific distribution (if exist)
    roomDist = SuburbsDistributions[room:getName()];
--    local roomName = room:getName();
--~     print ("room: "..roomName.. " container: "..container:getType());

    if roomDist ~= nil then
       -- print("got room distribution");
		ItemPicker.fillContainerType(roomDist, container, room:getName(), player);
    end
  --  container:sendContentsToRemoteContainer();
    triggerEvent("OnFillContainer", room:getName(),container:getType(), container)
end

ItemPicker.fillContainerType = function(roomDist, container, roomName, character)
--	print ("looking for "..container:getType() .. " in room " .. roomName);
    local doItemContainer = true;
    if NoContainerFillRooms[roomName] ~= nil then
        doItemContainer = false;
    end
	-- first we look if we got a "all" distribution for this room
	local containerDist = roomDist["all"];
	ItemPicker.rollItem(containerDist, container, doItemContainer, character);

	-- then the specific distribution (if exist)
	containerDist = roomDist[container:getType()];
	-- if we don't have a specific distribution for this type in this room, we're gonna look for "other" distrib
	if containerDist == nil then
		containerDist = roomDist["other"];
    end
	ItemPicker.rollItem(containerDist, container, doItemContainer, character);
    --container:sendContentsToRemoteContainer();
end

ItemPicker.tryAddItemToContainer = function(container, itemType)
	local scriptItem = ScriptManager.instance:FindItem(itemType)
	if not scriptItem then
		return nil
	end
	local totalWeight = scriptItem:getActualWeight() * scriptItem:getCount()
	if not container:hasRoomFor(nil, totalWeight) then
		return nil
	end
	-- Consider a tote bag inside a cupboard.  Now rollContainerItem() is trying to spawn plastic bags
	-- inside the tote bag.  The weight of the plastic bags isn't allowed to push the tote bag's total weight
	-- past what the cupboard can hold.
	if instanceof(container:getContainingItem(), "InventoryContainer") and container:getContainingItem():getContainer() then
		if not container:getContainingItem():getContainer():hasRoomFor(nil, totalWeight) then
			return nil
		end
	end
	-- NOTE: This may add multiple items (5 nails for example) but only returns the last, it should return a list.
	return container:AddItem(itemType)
end

ItemPicker.rollItem = function(containerDist, container, doItemContainer, character)
    if not isClient() and not isServer() then
        ItemPicker.player = getPlayer();
        character = getPlayer();
    end
	if containerDist ~= nil and container ~= nil then
--        print("roll item");
		-- we're looking for the zombie density in this area, more zombie density mean more loots
		local zombieDensity = 0;
		local chunk = nil;
        if ItemPicker.player ~= nil and getWorld() then
            chunk = getWorld():getMetaChunk((ItemPicker.player:getX()/10), (ItemPicker.player:getY()/10));
        end
		if chunk then
			zombieDensity = chunk:getLootZombieIntensity();
        end
        if zombieDensity > ItemPicker.zombieDensityCap then
            zombieDensity = ItemPicker.zombieDensityCap;
        end
		local alt = false;
		local itemname = nil;
        local lucky = false;
        local unlucky = false;
        if ItemPicker.player and character then
            lucky = character:HasTrait("Lucky");
            unlucky = character:HasTrait("Unlucky");
        end
		for m = 1, containerDist.rolls do
			for i, k in ipairs(containerDist.items) do
				if not alt then -- first we take the name of the item
					itemname = k;
-- 					print (itemname);
				else -- next step is the random spawn part
                    local itemNumber = k;
                    if lucky then
                        itemNumber = itemNumber * 1.1;
                    end
                    if unlucky then
                        itemNumber = itemNumber * 0.9;
                    end
                    local lootModifier = ItemPicker.getLootModifier(itemname) or 0.6;
					if ZombRand(10000) <= ((((itemNumber*100) * lootModifier) + (zombieDensity * 10))) then
						-- make an item in the container of that type.
						local item = ItemPicker.tryAddItemToContainer(container, itemname);
                        if not item then return; end
                        StashSystem.init();
                        StashSystem.checkStashItem(item);
                        if container:getType() == "freezer" and instanceof(item, "Food") and item:isFreezing() then
                            item:freeze();
                        end
                        if instanceof(item, "Key") then
                            item:takeKeyId();
--                            item:setName("Key " .. item:getKeyId());
                            -- no more than 2 keys per houses
                            if container:getSourceGrid() and container:getSourceGrid():getBuilding() and container:getSourceGrid():getBuilding():getDef() then
                                if container:getSourceGrid():getBuilding():getDef():getKeySpawned() < 2 then
                                    container:getSourceGrid():getBuilding():getDef():setKeySpawned(container:getSourceGrid():getBuilding():getDef():getKeySpawned() + 1);
                                else
                                    container:Remove(item);
                                end
                            end
                        end
                        if WeaponUpgrades[item:getType()] then
                            ItemPicker.doWeaponUpgrade(item);
                        end
                        if not containerDist.noAutoAge then
						    item:setAutoAge();
                        end
                        -- randomized used delta
                        if instanceof(item, "DrainableComboItem") and ZombRand(100) < 40 then
                            local maxUse = 1 / item:getUseDelta();
                            item:setUsedDelta(ZombRand(1,maxUse-1)*item:getUseDelta());
                        end
                        -- randomize weapon condition
                        if instanceof(item, "HandWeapon") and ZombRand(100) < 40 then
                            item:setCondition(ZombRand(1, item:getConditionMax()));
                        end
                        -- if the item is a container, we look to spawn item inside it
                        if(SuburbsDistributions[item:getType()]) then
                            if instanceof(item, "InventoryContainer") and doItemContainer and ZombRand(SuburbsDistributions[item:getType()].fillRand) == 0 then
                                ItemPicker.rollContainerItem(item, character, SuburbsDistributions[item:getType()]);
                            end
                        end
					end
				end
				alt = not alt;
			end
		end
	end
end

ItemPicker.rollContainerItem = function(bag, character, containerDist)
    if containerDist then
        local zombieDensity = 0;
        local chunk = nil;
        if ItemPicker.player ~= nil then
            chunk = getWorld():getMetaChunk((ItemPicker.player:getX()/10), (ItemPicker.player:getY()/10));
        end
        if chunk then
            zombieDensity = chunk:getLootZombieIntensity();
        end
        if zombieDensity > ItemPicker.zombieDensityCap then
            zombieDensity = ItemPicker.zombieDensityCap;
        end
        local alt = false;
        local itemname = nil;
        for m = 1, containerDist.rolls do
            for i, k in ipairs(containerDist.items) do
                if not alt then -- first we take the name of the item
                    itemname = k;
                else -- next step is the random spawn part
                    local lootModifier = ItemPicker.getLootModifier(itemname) or 0.6;
                    if ZombRand(10000) <= ((((k*100) * lootModifier) + (zombieDensity * 10))) then
                        -- make an item in the container of that type
                        local item = ItemPicker.tryAddItemToContainer(bag:getItemContainer(), itemname);
                        if not item then return end
                        if instanceof(item, "Key") then
                            item:takeKeyId();
                            item:setName("Key " .. item:getKeyId());
                        end
                        item:setAutoAge();
                    end
                end
                alt = not alt;
            end
        end
    end
end

ItemPicker.getLootModifier = function(itemname)
    local item = ScriptManager.instance:FindItem(itemname)
    if not item then return; end
    local lootModifier = ZomboidGlobals.OtherLootModifier;
    if item:getTypeString() == "Food" then
        lootModifier = ZomboidGlobals.FoodLootModifier;
    end
    if item:getTypeString() == "Weapon" or item:getTypeString() == "WeaponPart" or item:getDisplayCategory() == "Ammo" then
        lootModifier = ZomboidGlobals.WeaponLootModifier;
    end
    return lootModifier;
end

ItemPicker.doOverlaySprite = function(sq)
    if isClient() then return; end
    if not sq or not sq:getRoom() or sq:isOverlayDone() then return; end
    for j=0,sq:getObjects():size()-1 do
        local obj = sq:getObjects():get(j);
        if obj and obj:getContainer() and not obj:getContainer():isExplored() then
            ItemPicker.fillContainer(obj:getContainer(), getPlayer());
            obj:getContainer():setExplored(true);
            if isServer() then
                sendItemsInContainer(obj, obj:getContainer());
            end
        end
        ItemPicker.updateOverlaySprite(obj)
    end
    sq:setOverlayDone(true)
end

-- NOTE: this function is called from Java directly
ItemPicker.updateOverlaySprite = function(obj)
	if not obj then return end
	if instanceof(obj, "IsoStove") then return end
	local sq = obj:getSquare();
    if not sq then return end
    local room = "other";
    if sq:getRoom() then
        room = sq:getRoom():getName();
    end
    local overlaySpriteName = ""
	local container = obj:getContainer()
	if obj:getSprite() and obj:getSprite():getName() and ((container and container:getItems() and container:getItems():size() > 0) or not container) then
		if overlayMap[obj:getSprite():getName()] then
			local overlayName = overlayMap[obj:getSprite():getName()][room] or overlayMap[obj:getSprite():getName()]["other"]
			if overlayName and overlayName[1] ~= "none" then
				if not container and ZombRand(2) == 0 then return; end -- no container tile have 1/2 chance of having a sprite
				overlaySpriteName = overlayName[1]
				if #overlayName > 1 and container and container:getItems() and container:getItems():size() < 7 then
					overlaySpriteName = overlayName[2]
				end
			end
		end
	end
	obj:setOverlaySprite(overlaySpriteName)
end

ItemPicker.doWeaponUpgrade = function(item)
--    print("do weapon upgrade");
    -- randomize a non upgraded weapon (most of the time)
--   if ZombRand(4) == 0 then return; end
    -- now randomize a random number of upgrade
    local upgradeList = WeaponUpgrades[item:getType()];
    local randUpgrade = ZombRand(#upgradeList);
--    print("randomize " .. randUpgrade);
    for i=1,randUpgrade do
        local upgrade = WeaponUpgrades[item:getType()][ZombRand(#upgradeList) + 1];
--        print(upgrade);
        local doIt = false;
        local part = InventoryItemFactory.CreateItem(upgrade);
        item:attachWeaponPart(part)
    end
end

debugContainer = function(room, containerType, container)
    print("room ");
    print(room);
    print("filled container ");
    print(containerType);
end

--Events.OnFillContainer.Add(debugContainer);

--Events.LoadGridsquare.Add(ItemPicker.doOverlaySprite);
