--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISAddItemInRecipe = ISBaseTimedAction:derive("ISAddItemInRecipe");

function ISAddItemInRecipe:isValid()
	return self.character:getInventory():contains(self.baseItem) and self.recipe:getItemsCanBeUse(self.character, self.baseItem, nil):contains(self.usedItem)
end

function ISAddItemInRecipe:update()
    self.baseItem:setJobDelta(self:getJobDelta());
end

function ISAddItemInRecipe:start()
    self.baseItem:setJobType(getText("IGUI_JobType_AddingIngredient", self.usedItem:getDisplayName(), self.baseItem:getDisplayName()));
    self.character:getEmitter():playSoundImpl("AddItemInRecipe", nil)
end

function ISAddItemInRecipe:stop()
    ISBaseTimedAction.stop(self);
    self.baseItem:setJobDelta(0.0);
end

function ISAddItemInRecipe:perform()
    -- needed to remove from queue / start next.
    ISBaseTimedAction.perform(self);
    
    self.baseItem:setJobDelta(0.0);
    if self.character:getPrimaryHandItem() == self.baseItem then
        self.character:setPrimaryHandItem(nil);
    elseif self.character:getSecondaryHandItem() == self.baseItem then
        self.character:setSecondaryHandItem(nil);
    end

    self.baseItem = self.recipe:addItem(self.baseItem, self.usedItem, self.character);
    
    ISAddItemInRecipe.checkName(self.baseItem, self.recipe);
end

-- Generate the recipe's name
ISAddItemInRecipe.checkName = function(baseItem, recipe)
    local foodTypeList = {};
    local finalName = "";
    local count = 0;
    if not baseItem:getExtraItems() then
        return;
    end
    if instanceof(baseItem, "Food") and not baseItem:isCustomName() then -- Don't do anything if it's custom (unique recipe or changed by the player)
        for i=0,baseItem:getExtraItems():size()-1 do
            count = 0;
            for i,v in pairs(foodTypeList) do
                if v ~= "" then count = count + 1 end
            end
            local food = InventoryItemFactory.CreateItem(baseItem:getExtraItems():get(i));
            local name = food:getEvolvedRecipeName() or food:getDisplayName();
            if instanceof(food, "Food") then
                if food:getFoodType() == "NoExplicit" then -- no explicit appear only if there's no other ingredient inside the recipe
                    if count == 0 then
                        foodTypeList[food:getFoodType()] = food:getDisplayName();
                    end
                else
                    foodTypeList["NoExplicit"] = "";
                    if not foodTypeList[getText("ContextMenu_FoodType_" .. food:getFoodType())] then -- first we show the name of food, if there's more than 1 time this type of food, we show the food type
                        foodTypeList[getText("ContextMenu_FoodType_" .. food:getFoodType())] = name;
                    elseif foodTypeList[getText("ContextMenu_FoodType_" .. food:getFoodType())] ~= food:getDisplayName() then -- only if the name is different (you can add 4x tomato, it'll stay as Tomato and not "vegetables")
                        foodTypeList[getText("ContextMenu_FoodType_" .. food:getFoodType())] = getText("ContextMenu_FoodType_" .. food:getFoodType());
                    end
                end
            end
        end
        count = 0;
        for i,v in pairs(foodTypeList) do
            if v ~= "" then count = count + 1 end
        end

        if count > 2 then -- avoid too big name
            baseItem:setName(getText("ContextMenu_EvolvedRecipe_" .. recipe:getUntranslatedName()));
        else -- do the name
            for i,v in pairs(foodTypeList) do
--                print(v);
                if v ~= "" then
                    if finalName ~= "" then
                        finalName = finalName .. " " .. getText("ContextMenu_EvolvedRecipe_and") .. " ";
                    end
                    finalName = finalName .. v;
                end
            end
            baseItem:setName(getText("ContextMenu_EvolvedRecipe_RecipeName", finalName , getText("ContextMenu_EvolvedRecipe_" .. recipe:getUntranslatedName())));
        end
    end
    ISInventoryPage.dirtyUI();
end

function ISAddItemInRecipe:new(character, recipe, baseItem, usedItem, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.recipe = recipe;
    o.baseItem = baseItem;
    o.usedItem = usedItem;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = 100 - (character:getPerkLevel(Perks.Cooking) * 2.5);
    if character:getAccessLevel() ~= "None" then
        o.maxTime = 1;
    end
    o.jobType = getText("IGUI_JobType_AddingIngredient", usedItem:getDisplayName(), baseItem:getDisplayName());
	return o;
end
