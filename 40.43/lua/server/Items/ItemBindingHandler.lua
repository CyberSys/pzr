--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

ItemBindingHandler = {};

ItemBindingHandler.onKeyPressed = function(key)
	local weapon = nil;
	local sledge = nil;
	local playerObj = getSpecificPlayer(0)
    local remove = false;
    if playerObj and not playerObj:IsAiming() then
	-- looking for the better handweapon
		if key == getCore():getKey("Equip/Unequip Handweapon") then
            if playerObj:getPrimaryHandItem() ~= nil and instanceof(playerObj:getPrimaryHandItem(), "HandWeapon") and playerObj:getPrimaryHandItem():getSubCategory() == "Swinging" then
                remove = true;
            else
                local weaponDmg = 0;
                local it = playerObj:getInventory():getItems();
                for i=0, it:size() - 1 do
                    local item = it:get(i);
                    if instanceof(item, "HandWeapon") and item:getSubCategory() == "Swinging" and weaponDmg < ((item:getMaxDamage() + item:getMinDamage()) / 2) and item:getCondition() > 0 then
						if item:getType() == "Sledgehammer" then
							sledge = item;
						else
                        	weapon = item;
						end
                        weaponDmg = ((item:getMaxDamage() + item:getMinDamage()) / 2);
                    end
                end
            end
		elseif key == getCore():getKey("Equip/Unequip Firearm") then -- looking for the better firearm
            if playerObj:getPrimaryHandItem() ~= nil and instanceof(playerObj:getPrimaryHandItem(), "HandWeapon") and playerObj:getPrimaryHandItem():getSubCategory() == "Firearm" then
                remove = true;
            else
                local weaponDmg = 0;
                local it = playerObj:getInventory():getItems();
                for i=0, it:size() - 1 do
                    local item = it:get(i);
                    if instanceof(item, "HandWeapon") and item:getSubCategory() == "Firearm" and weaponDmg < ((item:getMaxDamage() + item:getMinDamage()) / 2) and item:getCondition() > 0 then
                        weapon = item;
                        weaponDmg = ((item:getMaxDamage() + item:getMinDamage()) / 2);
                    end
                end
            end
		elseif key == getCore():getKey("Equip/Unequip Stab weapon") then 	-- looking for the better stab weapon
            if playerObj:getPrimaryHandItem() ~= nil and instanceof(playerObj:getPrimaryHandItem(), "HandWeapon") and playerObj:getPrimaryHandItem():getSubCategory() == "Stab" then
                remove = true;
            else
                local weaponDmg = 0;
                local it = playerObj:getInventory():getItems();
                for i=0, it:size() - 1 do
                    local item = it:get(i);
                    if instanceof(item, "HandWeapon") and item:getSubCategory() == "Stab" and weaponDmg < ((item:getMaxDamage() + item:getMinDamage()) / 2) and item:getCondition() > 0 then
                        weapon = item;
                        weaponDmg = ((item:getMaxDamage() + item:getMinDamage()) / 2);
                    end
                end
            end
        end
        if remove then
            ISTimedActionQueue.add(ISUnequipAction:new(playerObj, playerObj:getPrimaryHandItem(), 50));
		end
		-- equip axe/baseball bat in priority of the sledgehammer
		if ((weapon and weapon:getType() ~= "Axe" and weapon:getType() ~= "BaseballBat" and weapon:getType() ~= "BaseballBatNails") or not weapon) and sledge and sledge ~= playerObj:getPrimaryHandItem() then
			ISTimedActionQueue.add(ISEquipWeaponAction:new(playerObj, sledge, 50, true, sledge:isTwoHandWeapon()));
		elseif weapon ~= nil and weapon ~= playerObj:getPrimaryHandItem() then
			ISTimedActionQueue.add(ISEquipWeaponAction:new(playerObj, weapon, 50, true, weapon:isTwoHandWeapon()));
		end
		if key == getCore():getKey("Equip/Turn On/Off Light Source") then
			-- The 'F' key turns headlights on/off when driving.
			if key == getCore():getKey("ToggleVehicleHeadlights") then
				local vehicle = playerObj:getVehicle()
				if vehicle and vehicle:isDriver(playerObj) then
					if vehicle:hasHeadlights() then
						ISVehicleMenu.onToggleHeadlights(playerObj)
					end
					return
				end
			end
			if playerObj:getPrimaryHandItem() ~= nil and playerObj:getPrimaryHandItem():getLightStrength() > 0 then
				playerObj:getPrimaryHandItem():setActivated(not playerObj:getPrimaryHandItem():isActivated());
			elseif playerObj:getSecondaryHandItem() ~= nil and playerObj:getSecondaryHandItem():getLightStrength() > 0 then
				playerObj:getSecondaryHandItem():setActivated(not playerObj:getSecondaryHandItem():isActivated());
			else
				local lightStrength = 0;
				local lightSource = nil;
				local it = playerObj:getInventory():getItems();
				for i=0, it:size() - 1 do
					local item = it:get(i);
					if item:getLightStrength() > lightStrength then
						lightSource = item;
						lightStrength = item:getLightStrength();
					end
				end
				if lightSource ~= nil then
					ISTimedActionQueue.add(ISEquipWeaponAction:new(playerObj, lightSource, 50, false));
					lightSource:setActivated(true);
				end
			end
		end
	end
end

function ItemBindingHandler.equipBestWeapon(playerObj, subCategory)
	if not (playerObj and not playerObj:isDead() and not playerObj:IsAiming()) then return end
	if instanceof(playerObj:getPrimaryHandItem(), "HandWeapon") and playerObj:getPrimaryHandItem():getSubCategory() == subCategory then
		ISTimedActionQueue.add(ISUnequipAction:new(playerObj, playerObj:getPrimaryHandItem(), 50))
		return
	end
	local weapon = nil
	local weaponDmg = 0
	local it = playerObj:getInventory():getItems()
	for i=1,it:size() do
		local item = it:get(i-1)
		if instanceof(item, "HandWeapon") and item:getSubCategory() == subCategory and
				weaponDmg < ((item:getMaxDamage() + item:getMinDamage()) / 2) and item:getCondition() > 0 then
			weapon = item
			weaponDmg = ((item:getMaxDamage() + item:getMinDamage()) / 2)
		end
	end
	if weapon and weapon ~= playerObj:getPrimaryHandItem() then
		ISTimedActionQueue.add(ISEquipWeaponAction:new(playerObj, weapon, 50, true, weapon:isTwoHandWeapon()))
	end
end

Events.OnKeyPressed.Add(ItemBindingHandler.onKeyPressed);
