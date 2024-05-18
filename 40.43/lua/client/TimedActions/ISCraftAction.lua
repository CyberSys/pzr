--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISCraftAction = ISBaseTimedAction:derive("ISCraftAction");

function ISCraftAction:isValid()
	return RecipeManager.IsRecipeValid(self.recipe, self.character, self.item, self.containers) and not self.character:isDriving();
end

function ISCraftAction:update()
	self.item:setJobDelta(self:getJobDelta());
--    if self.recipe:getSound() and (not self.craftSound or not self.craftSound:isPlaying()) then
--        self.craftSound = getSoundManager():PlayWorldSoundWav(self.recipe:getSound(), self.character:getCurrentSquare(), 0, 2, 1, true);
--    end
end

function ISCraftAction:start()
    if self.recipe:getSound() then
        self.character:getEmitter():playSound(self.recipe:getSound());
    end
	self.item:setJobType(self.recipe:getName());
	self.item:setJobDelta(0.0);
end

function ISCraftAction:stop()
    ISBaseTimedAction.stop(self);
    self.item:setJobDelta(0.0);
end

function ISCraftAction:perform()
    if self.craftSound and self.craftSound:isPlaying() then
        self.craftSound:stop();
    end
    if self.container:getType() == "floor" then
        self.fromFloor = true;
    else
        self.fromFloor = false;
    end
    self.container:setDrawDirty(true);
    self.item:setJobDelta(0.0);
	local resultItemCreated = RecipeManager.PerformMakeItem(self.recipe, self.item, self.character, self.containers);
    if resultItemCreated and instanceof(resultItemCreated, "DrainableComboItem") and self.recipe:getResult():getDrainableCount() > 0 then
        resultItemCreated:setUsedDelta(resultItemCreated:getUseDelta() * self.recipe:getResult():getDrainableCount());
    end
	if resultItemCreated and self.recipe:getResult():getCount() > 1 then
		-- FIXME: this does not call the recipe's OnCreate lua function
		local itemsAdded = self.container:AddItems(resultItemCreated:getFullType(), self.recipe:getResult():getCount());
		-- now we modify the variables of the item created, for example if you create a nailed baseball bat, it'll have the condition of the used baseball bat
		if itemsAdded and instanceof(resultItemCreated, "Food") then
			for i=0, itemsAdded:size()-1 do
				local newItem = itemsAdded:get(i);
				newItem:setCooked(resultItemCreated:isCooked());
				newItem:setRotten(resultItemCreated:isRotten());
				newItem:setBurnt(resultItemCreated:isBurnt());
				newItem:setAge(resultItemCreated:getAge());
				newItem:setHungChange(resultItemCreated:getHungChange());
				newItem:setBaseHunger(resultItemCreated:getBaseHunger());
				newItem:setBoredomChange(resultItemCreated:getBoredomChange());
				newItem:setUnhappyChange(resultItemCreated:getUnhappyChange());
                newItem:setPoisonDetectionLevel(resultItemCreated:getPoisonDetectionLevel());
                newItem:setPoisonPower(resultItemCreated:getPoisonPower());
                newItem:setCarbohydrates(resultItemCreated:getCarbohydrates());
                newItem:setLipids(resultItemCreated:getLipids());
                newItem:setProteins(resultItemCreated:getProteins());
                newItem:setCalories(resultItemCreated:getCalories());
                newItem:setTaintedWater(resultItemCreated:isTaintedWater());
                newItem:setActualWeight(resultItemCreated:getActualWeight());
			end
		end
		if itemsAdded and instanceof(resultItemCreated, "HandWeapon") then
			for i=0, itemsAdded:size()-1 do
				local newItem = itemsAdded:get(i);
				newItem:setCondition(resultItemCreated:getCondition());
			end
        end
        if itemsAdded and self.fromFloor then
            for i=1,itemsAdded:size() do
				self.character:getCurrentSquare():AddWorldInventoryItem(itemsAdded:get(i-1),
					self.character:getX() - math.floor(self.character:getX()),
					self.character:getY() - math.floor(self.character:getY()),
					self.character:getZ() - math.floor(self.character:getZ()))
				-- NOTE: AddWorldInventoryItem() sets the item's container to null
				itemsAdded:get(i-1):setContainer(self.container)
            end
        end
		if itemsAdded and not self.fromFloor then
			for i=1,itemsAdded:size() do
				self:addOrDropItem(itemsAdded:get(i-1))
			end
		end
	elseif resultItemCreated then
		if self.fromFloor then
			self.character:getCurrentSquare():AddWorldInventoryItem(resultItemCreated,
				self.character:getX() - math.floor(self.character:getX()),
				self.character:getY() - math.floor(self.character:getY()),
				self.character:getZ() - math.floor(self.character:getZ()))
			self.container:AddItem(resultItemCreated)
        else
			self:addOrDropItem(resultItemCreated)
		end
    end

	ISInventoryPage.dirtyUI()

	if self.onCompleteFunc then
		local args = self.onCompleteArgs
		self.onCompleteFunc(args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8])
	end

    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISCraftAction:addOrDropItem(item)
	local inv = self.character:getInventory()
	if not inv:contains(item) then
		inv:AddItem(item)
	end
	if inv:getCapacityWeight() > inv:getEffectiveCapacity(self.character) then
		if inv:contains(item) then
			inv:Remove(item)
		end
		self.character:getCurrentSquare():AddWorldInventoryItem(item,
			self.character:getX() - math.floor(self.character:getX()),
			self.character:getY() - math.floor(self.character:getY()),
			self.character:getZ() - math.floor(self.character:getZ()))
	end
end

function ISCraftAction:setOnComplete(func, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
	self.onCompleteFunc = func
	self.onCompleteArgs = { arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8 }
end

function ISCraftAction:new(character, item, time, recipe, container, containers)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.item = item;
	o.recipe = recipe;
	o.container = container;
	-- If the recipe can be done from the floor, then we can use items from nearby containers.
	-- If the recipe cannot be done from the floor, then all the items must already be in the player's inventory.
    o.containers = recipe:isCanBeDoneFromFloor() and containers or nil;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
    if character:getAccessLevel() ~= "None" then
        o.maxTime = 1;
    end
    o.jobType = recipe:getName();
	return o;
end
