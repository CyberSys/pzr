require "Tutorial/TutorialStep"

TutorialTests = {}
TutorialTests.klight_x = 160;
TutorialTests.klight_y = 156;

TutorialTests.llight_x = 151;
TutorialTests.llight_y = 151;
TutorialTests.ZoomedIn = function()
    return getCore():getZoom(0) == getCore():getMinZoom();
end

TutorialTests.ZoomedOut = function()
    return getCore():getZoom(0) == getCore():getMaxZoom();
end

TutorialTests.HealthOpen = function()
    ISEquippedItem.instance.healthBtn.blinkImage = true;
    if ISHealthPanel.instance ~= nil and ISHealthPanel.instance:isReallyVisible() then
        ISEquippedItem.instance.healthBtn.blinkImage = false;
        return true;
    end
    return false;
end

TutorialTests.SkillsPage = function()
    if ISCharacterInfoWindow.instance then
        ISCharacterInfoWindow.instance.panel.blinkTab = xpSystemText.skills;
    end
    if ISCharacterInfo.instance ~= nil and ISCharacterInfo.instance:isReallyVisible() then
        ISCharacterInfoWindow.instance.panel.blinkTab = nil;
        return true;
    end
    return false;
end

TutorialTests.NotSkillsPage = function()
    if ISCharacterInfoWindow.instance then
        ISCharacterInfoWindow.instance.closeButton.blinkImage = true;
    end
    if ISCharacterInfo.instance ~= nil and not ISCharacterInfo.instance:isReallyVisible() then
        ISCharacterInfoWindow.instance.closeButton.blinkImage = false;
        return true;
    end
    return false;
end

TutorialTests.LookedAround = function()
    if getPlayer(0):getDir() == IsoDirections.W then return true; end
    return false;
end


WelcomeStep = TutorialStep:derive("WelcomeStep");
function WelcomeStep:new () local o = {} setmetatable(o, self)    self.__index = self    return o end

function WelcomeStep:begin()
    local klightSquare = getCell():getGridSquare(TutorialTests.klight_x, TutorialTests.klight_y, 0);
    local llightSquare = getCell():getGridSquare(TutorialTests.llight_x, TutorialTests.llight_y, 0);
    klightSquare:switchLight(true);
    llightSquare:switchLight(false);

    self:addMessage(getText("IGUI_Tutorial1_Welcome1"), getCore():getScreenWidth()/2, getCore():getScreenHeight()/2, 500, 160,
        true);
    if getCore():getMinZoom() ~= getCore():getMaxZoom() then
        self:addMessage(getText("IGUI_Tutorial1_Welcome2",string.lower(Keyboard.getKeyName(getCore():getKey("Zoom in"))),string.lower(Keyboard.getKeyName(getCore():getKey("Zoom out")))), 300, 620, 420, 150,
            false, TutorialTests.ZoomedOut);
        self:addMessage(getText("IGUI_Tutorial1_Welcome2bis"), 300, 620, 420, 80,
            false, TutorialTests.ZoomedIn);
    end

    self:addMessage(getText("IGUI_Tutorial1_Welcome3"), 250, 150, 300, 130,
        false, TutorialTests.HealthOpen);

    self:addMessage(getText("IGUI_Tutorial1_Welcome4"), 670, 460, 410, 170,
        true);

    self:addMessage(getText("IGUI_Tutorial1_Welcome5"), 200, 180, 290, 90,
        false, TutorialTests.SkillsPage);


    self:addMessage(getText("IGUI_Tutorial1_Welcome6"), 210, 150, 340, 120,
        true);

    self:addMessage(getText("IGUI_Tutorial1_Welcome7"), 580, 450, 310, 210,
        true);

    self:addMessage(getText("IGUI_Tutorial1_Welcome8"), 580, 420, 300, 100,
        false, TutorialTests.NotSkillsPage);

    self:addMessage(getText("IGUI_Tutorial1_Welcome9"),  300, 620, 420, 100,
        false, TutorialTests.LookedAround);

    self:addMessage(getText("IGUI_Tutorial1_Welcome10"),  300, 620, 420, 160,
        true);

    self:doMessage();
end

function WelcomeStep:isComplete()
    return TutorialStep.isComplete(self);
end

function WelcomeStep:finish()
    TutorialStep.finish(self);
end


WalkToAdjacent = TutorialStep:derive("WalkToAdjacent");
function WalkToAdjacent:new () local o = {} setmetatable(o, self)    self.__index = self    return o end

WalkToAdjacent.x = 151;
WalkToAdjacent.y = 156;
WalkToAdjacent.highlightFloor = nil
WalkToAdjacent.lastPlayerX = -1;
WalkToAdjacent.lastPlayerY = -1;
WalkToAdjacent.runned = false;
WalkToAdjacent.x2 = 158;
WalkToAdjacent.y2 = 153;
WalkToAdjacent.playerX = -1;
WalkToAdjacent.playerY = -1;
WalkToAdjacent.sneaked = false;
WalkToAdjacent.appleContainer = nil;

WalkToAdjacent.z = 0;
function WalkToAdjacent:begin()
    local m1 = Keyboard.getKeyName(getCore():getKey("Forward"))
    local m2 = Keyboard.getKeyName(getCore():getKey("Left"))
    local m3 = Keyboard.getKeyName(getCore():getKey("Backward"))
    local m4 = Keyboard.getKeyName(getCore():getKey("Right"))
    local moveKeys = getText("IGUI_Tutorial1_MovementKeys", m1, m2, m3, m4)
    
    self:addMessage(getText("IGUI_Tutorial1_WalkTo1", moveKeys),  300, 620, 420, 130,
        false, WalkToAdjacent.inLoc);

    self:addMessage(getText("IGUI_Tutorial1_WalkTo2"),  300, 620, 420, 130,
        false, WalkToAdjacent.strafed);

    local shiftKey = Keyboard.getKeyName(getCore():getKey("Run"))
    
    self:addMessage(getText("IGUI_Tutorial1_WalkTo3", shiftKey, moveKeys),  300, 620, 420, 130,
        false, WalkToAdjacent.inLocRunned);

--    local klightSquare = getCell():getGridSquare(TutorialTests.klight_x, TutorialTests.klight_y, 0);
    local llightSquare = getCell():getGridSquare(TutorialTests.llight_x, TutorialTests.llight_y, 0);
--    klightSquare:switchLight(false);
    llightSquare:switchLight(true);
    local floor = getCell():getGridSquare(WalkToAdjacent.x, WalkToAdjacent.y, 0):getFloor();
    floor:setHighlighted(true, false);
    floor:setBlink(true);
    floor:setHighlightColor(ColorInfo.new(1,0,0,1));
    WalkToAdjacent.highlightFloor = floor
--    getCell():addLamppost(WalkToAdjacent.x, WalkToAdjacent.y, WalkToAdjacent.z, 10, 7, 0, 3);


    self:doMessage();
end
function WalkToAdjacent:inLoc()
    local floor = getCell():getGridSquare(WalkToAdjacent.x, WalkToAdjacent.y, 0):getFloor();
    return math.abs(getPlayer():getCurrentSquare():getX() - WalkToAdjacent.x) < 3 and math.abs(getPlayer():getCurrentSquare():getY() - WalkToAdjacent.y) < 3 and getPlayer():getZ() == 0;
end

function WalkToAdjacent:strafed()
    if WalkToAdjacent.highlightFloor then
        WalkToAdjacent.highlightFloor:setHighlighted(false)
        WalkToAdjacent.highlightFloor = nil
    end
    if not WalkToAdjacent.sneaked and getPlayer():isbSneaking() then
        WalkToAdjacent.sneaked = true;
        WalkToAdjacent.lastPlayerX = getPlayer():getX();
        WalkToAdjacent.lastPlayerY = getPlayer():getY();
    end
    -- highlight the corner the player has to stand near
    if (math.abs(WalkToAdjacent.lastPlayerX - getPlayer():getX()) > 1 or math.abs(WalkToAdjacent.lastPlayerY - getPlayer():getY()) > 1) and getPlayer():isbSneaking() then
        local sq = getCell():getGridSquare(157, 152, 0);
        local objs = sq:getObjects();
        for i = 0, objs:size()-1 do
            local o = objs:get(i);
            local c = o:getContainer();
            if c ~= nil then
                o:setHighlighted(true, false);
                o:setHighlightColor(ColorInfo.new(1,0,0,1));
                o:setBlink(true);
                WalkToAdjacent.appleContainer = o;
            end
        end
--        local klightSquare = getCell():getGridSquare(TutorialTests.klight_x, TutorialTests.klight_y, 0);
--        klightSquare:switchLight(true);
        getSoundManager():PlayWorldSoundImpl("TutorialZombie", false, FightStep.zombieSpawnX, FightStep.zombieSpawnY, 0, 0, 20, 1, false);
        return true;
    end
    return false;
end

function WalkToAdjacent:inLocRunned()

    if not WalkToAdjacent.runned then
        WalkToAdjacent.runned = getPlayer():IsRunning();
    end
    local complete = math.abs(getPlayer():getCurrentSquare():getX() - WalkToAdjacent.x2) < 1 and math.abs(getPlayer():getCurrentSquare():getY() - WalkToAdjacent.y2) < 1  and getPlayer():getZ() == 0 and WalkToAdjacent.runned;
    if complete then
        WalkToAdjacent.appleContainer:setHighlighted(false)
        WalkToAdjacent.appleContainer:setBlink(false)
        WalkToAdjacent.playerX = getPlayer():getX();
        WalkToAdjacent.playerY = getPlayer():getY();
    end
    return complete;
end

function WalkToAdjacent:isComplete()
    return TutorialStep.isComplete(self);
end

function WalkToAdjacent:finish()
    TutorialStep.finish(self);
end

InventoryLootingStep = TutorialStep:derive("InventoryLootingStep");
InventoryLootingStep.itemToEat = "DeadMouse";
function InventoryLootingStep:new () local o = {} setmetatable(o, self)    self.__index = self    return o end

function InventoryLootingStep:begin()

    ISInventoryPaneContextMenu.dontCreateMenu = true;

    self:addMessage(getText("IGUI_Tutorial1_InvLoot1"),  700, 120, 520, 80,
        false, InventoryLootingStep.focusLootingPanel);

    self:addMessage(getText("IGUI_Tutorial1_InvLoot2"),  700, 420, 420, 100,
        false, InventoryLootingStep.haveItem);

    self:addMessage(getText("IGUI_Tutorial1_InvLoot3"),  700, 420, 420, 90,
        false, InventoryLootingStep.haveWater);

    getPlayerInventory(0):setVisible(true);

    getPlayerLoot(0):setVisible(true);
    getPlayerLoot(0).lootAll:setVisible(false);
    getPlayerLoot(0).blink = true;

    self:doMessage();
end
function InventoryLootingStep:focusLootingPanel()
    local isOpen = getPlayerLoot(0) ~= nil and not getPlayerLoot(0).isCollapsed;
    if isOpen then
        getPlayerLoot(0).blink = false;
        for i,v in ipairs(getPlayerLoot(0).backpacks) do
            if v.inventory:contains(InventoryLootingStep.itemToEat) then
                getPlayerLoot(0).inventoryPane.inventory = v.inventory;
                getPlayerLoot(0).title = v.name;
                getPlayerLoot(0).capacity = v.capacity;
                getPlayerLoot(0):refreshBackpacks();
            end
        end
    end
    return isOpen;
end

function InventoryLootingStep:haveItem()
    getPlayerLoot(0).inventoryPane.highlightItem = InventoryLootingStep.itemToEat;
    if getPlayer():getInventory():contains(InventoryLootingStep.itemToEat) then
        getPlayerLoot(0).inventoryPane.highlightItem = "";
        getPlayerLoot(0).inventoryPane.selected = {};
        return true;
    end
    return false;
end

function InventoryLootingStep:haveWater()
    getPlayerLoot(0).inventoryPane.highlightItem = "WaterBottleEmpty";
    if getPlayer():getInventory():contains("WaterBottleEmpty") then
        getPlayerLoot(0).inventoryPane.highlightItem = "";
        getPlayerLoot(0).inventoryPane.selected = {};
        return true;
    end
    return false;
end

function InventoryLootingStep:isComplete()
    getPlayer():setDir(IsoDirections.NW);
    getPlayer():setX(WalkToAdjacent.playerX);
    getPlayer():setY(WalkToAdjacent.playerY);
    return TutorialStep.isComplete(self);
end

function InventoryLootingStep:finish()
    TutorialStep.finish(self);
end

InventoryUseStep = TutorialStep:derive("InventoryUseStep");
InventoryUseStep.sinkX = 157;
InventoryUseStep.sinkY = 154;
InventoryUseStep.sink = nil;
InventoryUseStep.lastInventory = nil;
InventoryUseStep.clickedOnInventory = false;
InventoryUseStep.panContainer = nil;
function InventoryUseStep:new () local o = {} setmetatable(o, self)    self.__index = self    return o end

function InventoryUseStep:begin()

    ISInventoryPaneContextMenu.dontCreateMenu = false;

    self:addMessage(getText("IGUI_Tutorial1_InvUse1"),  getCore():getScreenWidth() - 320, 160, 420, 130,
        false, InventoryUseStep.eat);

    self:addMessage(getText("IGUI_Tutorial1_InvUse2"),  getCore():getScreenWidth() - 250, 160, 260, 80,
        true);

    self:addMessage(getText("IGUI_Tutorial1_InvUse2Bis"),  300, 520, 320, 130,
        false, InventoryUseStep.fillBottle);

    self:addMessage(getText("IGUI_Tutorial1_InvUse3"),  300, 520, 470, 110,
        false, InventoryUseStep.focusLootingPanel);

    self:addMessage(getText("IGUI_Tutorial1_InvUse4"),  300, 520, 320, 130,
        false, InventoryUseStep.seeWeapon);

    self:addMessage(getText("IGUI_Tutorial1_InvUse5"),  500, 450, 320, 100,
        false, InventoryUseStep.lootWeapon);

    getPlayer():getStats():setHunger(0.2);

    self:doMessage();
end

function InventoryUseStep:focusLootingPanel()
    local isOpen = getPlayerLoot(0) ~= nil and not getPlayerLoot(0).isCollapsed;
    if isOpen then
        getPlayerLoot(0).blink = false;
    else
        getPlayerLoot(0).blink = true;
    end
    return isOpen;
end

function InventoryUseStep:eat()
    getCore():setBlinkingMoodle("Hungry");
    ISInventoryPaneContextMenu.blinkOption = getText("ContextMenu_Eat");
    getPlayer():getBodyDamage():setBoredomLevel(0);
    getPlayer():getBodyDamage():setUnhappynessLevel(0);
    local itemToEat = getPlayer():getInventory():FindAndReturn(InventoryLootingStep.itemToEat);
    if itemToEat then
       if math.abs(itemToEat:getHungChange()) < 0.09 then -- eat 1/4
           if TutorialMessage.instance then
               TutorialMessage.instance:setWidth(300);
               TutorialMessage.instance:setHeight(70);
               TutorialMessage.instance.richtext:setWidth(300-30);
               TutorialMessage.instance.richtext:setHeight(70-42);
               TutorialMessage.instance.richtext.text = getText("IGUI_Tutorial1_InvUse6");
               TutorialMessage.instance.richtext:paginate();
           end
       end
       if math.abs(itemToEat:getHungChange()) < 0.051 then -- eat 1/2
           if TutorialMessage.instance then
               TutorialMessage.instance:setWidth(300);
               TutorialMessage.instance:setHeight(70);
               TutorialMessage.instance.richtext:setWidth(300-30);
               TutorialMessage.instance.richtext:setHeight(70-42);
               TutorialMessage.instance.richtext.text = getText("IGUI_Tutorial1_InvUse7");
               TutorialMessage.instance.richtext:paginate();
           end
       end
    end
    local isOpen = getPlayerInventory(0) ~= nil and not getPlayerInventory(0).isCollapsed;
    if isOpen then
        getPlayerInventory(0).blink = false;
    else
        getPlayerInventory(0).blink = true;
    end
    getPlayerInventory(0).inventoryPane.highlightItem = InventoryLootingStep.itemToEat;
    local complete = Tutorial1.DeadMouse:getHungChange() == 0 and not itemToEat;
    if complete then
        getPlayer():getBodyDamage():setFoodSicknessLevel(40);
        getCore():setBlinkingMoodle("Sick");
    end
    return complete;
end

function InventoryUseStep:fillBottle()
    getCore():setBlinkingMoodle(nil);
    ISInventoryPaneContextMenu.blinkOption = nil;
    local emptybottle = getPlayer():getInventory():FindAndReturn("WaterBottleEmpty");
    if emptybottle then
        ISWorldObjectContextMenu.blinkOption = getText("ContextMenu_Fill") .. emptybottle:getName();
    end
    if not InventoryUseStep.sink then
        local sq = getCell():getGridSquare(InventoryUseStep.sinkX, InventoryUseStep.sinkY, 0);
        local objs = sq:getObjects();
        for i = 0, objs:size()-1 do
            local o = objs:get(i);
            if o:getSprite():getName() == "fixtures_sinks_01_16" then
                o:setHighlighted(true, false);
                o:setHighlightColor(ColorInfo.new(1,0,0,1));
                o:setBlink(true);
                InventoryUseStep.sink = o;
                break
            end
        end
    end
    local bottle = getPlayer():getInventory():FindAndReturn("WaterBottleFull");
    if bottle and bottle:getUsedDelta() == 1 then
        InventoryUseStep.sink:setHighlighted(false)
        InventoryUseStep.lastInventory = getPlayerLoot(0).inventoryPane.inventory;
        return true;
    end
    return false;
end

function InventoryUseStep:seeWeapon()
    ISWorldObjectContextMenu.blinkOption = nil;
    if not InventoryUseStep.panContainer then
        local sq = getCell():getGridSquare(160, 152, 0);
        if sq ~= nil then
            local objs = sq:getObjects();
            for i = 0, objs:size()-1 do
                local o = objs:get(i);
                local c = o:getContainer();
                if c ~= nil then
                    c:AddItem("Base.Pan");
                    o:setHighlighted(true, false);
                    o:setHighlightColor(ColorInfo.new(1,0,0,1));
                    o:setBlink(true);
                    InventoryUseStep.panContainer = o;
                    break;
                end
            end
        end
    end
    local isOpen = getPlayerLoot(0) ~= nil and not getPlayerLoot(0).isCollapsed;
    if isOpen then
        getPlayerLoot(0).blink = false;
--        if not InventoryUseStep.clickedOnInventory then
            getPlayerLoot(0).blinkContainer = true;
--        end
    else
        getPlayerLoot(0).blink = true;
    end
--    if InventoryUseStep.lastInventory ~= getPlayerLoot(0).inventoryPane.inventory then
--        InventoryUseStep.clickedOnInventory = true;
--        getPlayerLoot(0).blinkContainer = false;
--    end

    if getPlayerLoot(0).inventoryPane.inventory:contains("Pan") and not getPlayerLoot(0).isCollapsed then return true; end

    return false;
end

function InventoryUseStep:lootWeapon()
    if InventoryUseStep.panContainer then
        InventoryUseStep.panContainer:setHighlighted(false)
        InventoryUseStep.panContainer:setBlink(false)
        InventoryUseStep.panContainer = nil
    end
    ISInventoryPaneContextMenu.blinkOption = getText("ContextMenu_Equip_Primary");
    local playerLootOpen = not getPlayerLoot(0).isCollapsed and getPlayerLoot(0).inventoryPane.inventory:contains("Pan");
    local playerInvOpen = not getPlayerInventory(0).isCollapsed;
    getPlayerLoot(0).inventoryPane.highlightItem = "Pan";
    getPlayerInventory(0).inventoryPane.highlightItem = "Pan";
    getPlayerLoot(0).blinkContainer = false;
    if playerLootOpen then
        getPlayerLoot(0).blink = false;
    elseif getPlayerLoot(0).inventoryPane.inventory:contains("Pan") then
        getPlayerLoot(0).blink = true;
    end
    if playerInvOpen then
        getPlayerInventory(0).blink = false;
    elseif getPlayerInventory(0).inventoryPane.inventory:contains("Pan") then
        getPlayerInventory(0).blink = true;
    end
    if getPlayer():getPrimaryHandItem() and getPlayer():getPrimaryHandItem():getType() == "Pan" then
        getPlayerLoot(0).inventoryPane.highlightItem = nil;
        getPlayerInventory(0).inventoryPane.highlightItem = nil;
        getPlayerInventory(0).blink = false;
        getPlayerLoot(0).blink = false;
        return true;
    end
    return false;
end

function InventoryUseStep:isComplete()
    return TutorialStep.isComplete(self);
end

function InventoryUseStep:finish()
    ISInventoryPaneContextMenu.blinkOption = nil;
    TutorialStep.finish(self);
end

FightStep = TutorialStep:derive("FightStep");
FightStep.windowX = 163;
FightStep.windowY = 154;
FightStep.window = nil;
FightStep.climbThrough = false;
FightStep.zombieSpawnX = 168;
FightStep.zombieSpawnY = 154;
FightStep.zombie = nil;
FightStep.zombieSawYou = false;
FightStep.highlightFloor = nil;
FightStep.tickBeforeHordeSpawn = 0;
function FightStep:new () local o = {} setmetatable(o, self)    self.__index = self    return o end

function FightStep:begin()

    self:addMessage(getText("IGUI_Tutorial1_Fight1"),  300, 500, 520, 110, false, FightStep.WalkToWindow);

    self:addMessage(getText("IGUI_Tutorial1_Fight2", Keyboard.getKeyName(getCore():getKey("Interact"))),  300, 520, 520, 100, false, FightStep.OpenWindow);

    self:addMessage(getText("IGUI_Tutorial1_Fight3", Keyboard.getKeyName(getCore():getKey("Interact"))),  getCore():getScreenWidth()/2, getCore():getScreenHeight()/2 + 250, 520, 85, false, FightStep.ClimbThroughWindow);

    self:addMessage(getText("IGUI_Tutorial1_Fight4"),  getCore():getScreenWidth()/2, getCore():getScreenHeight()/2 + 250, 520, 120, false, FightStep.Aim);

    self:addMessage(getText("IGUI_Tutorial1_Fight5", Keyboard.getKeyName(getCore():getKey("Forward")) .. Keyboard.getKeyName(getCore():getKey("Left")) .. Keyboard.getKeyName(getCore():getKey("Backward")) .. Keyboard.getKeyName(getCore():getKey("Right"))),  getCore():getScreenWidth()/2, getCore():getScreenHeight()/2 + 250, 520, 100, false, FightStep.SneakBehindZombie);

    self:addMessage(getText("IGUI_Tutorial1_Fight6"),  getCore():getScreenWidth()/2, getCore():getScreenHeight()/2 + 150, 520, 90, false, FightStep.HitZombie);

    self:addMessage(getText("IGUI_Tutorial1_Fight7"),  getCore():getScreenWidth()/2, getCore():getScreenHeight()/2 + 150, 520, 100, false, FightStep.KillZombie);

    self:addMessage(getText("IGUI_Tutorial1_Fight8", Keyboard.getKeyName(getCore():getKey("Toggle Survival Guide"))),  getCore():getScreenWidth()/2, getCore():getScreenHeight()/2 + 250, 500, 140, true);

    self:addMessage(getText("IGUI_Tutorial1_Fight9", Keyboard.getKeyName(getCore():getKey("Shout"))),  300, 520, 560, 120, false, FightStep.Shout);

    self:addMessage(getText("IGUI_Tutorial1_Fight10"),  getCore():getScreenWidth()/2, getCore():getScreenHeight()/2 + 250, 520, 110, false, FightStep.isPlayedDead);

--    getPlayer():getInventory():AddItem("Base.Pan");

    local sq = getCell():getGridSquare(FightStep.windowX, FightStep.windowY, 0);
    if sq ~= nil then
        local objs = sq:getObjects();
        for i = 0, objs:size()-1 do
            local o = objs:get(i);
            if instanceof(o, "IsoWindow") then
                o:setHighlighted(true, false);
                o:setHighlightColor(ColorInfo.new(1,0,0,1));
                o:setBlink(true);
                FightStep.window = o;
                break;
            end
        end
    end

    self:doMessage();
end

function FightStep:WalkToWindow()
    return math.abs(getPlayer():getCurrentSquare():getX() - FightStep.windowX) <= 1 and math.abs(getPlayer():getCurrentSquare():getY() - FightStep.windowY) <= 1;
end

function FightStep:OpenWindow()
    if FightStep.window:IsOpen() then
        FightStep.window:setHighlighted(false)
       FightStep.zombie = createZombie(FightStep.zombieSpawnX, FightStep.zombieSpawnY, 0, nil, 0,IsoDirections.E);
       while not FightStep.zombie:getDescriptor():isFemale() do
           FightStep.zombie:removeFromWorld();
           FightStep.zombie:removeFromSquare();
           FightStep.zombie = createZombie(FightStep.zombieSpawnX, FightStep.zombieSpawnY, 0, nil, 0,IsoDirections.E);
       end
       FightStep.zombie:setUseless(true);
       return true;
    end
    return false;
end

FightStep.wasOpen = true;

function FightStep:ClimbThroughWindow()
    if not FightStep.window:IsOpen() then
        FightStep.wasOpen = false;
        if TutorialMessage.instance then
            TutorialMessage.instance:setWidth(300);
            TutorialMessage.instance:setHeight(120);
            TutorialMessage.instance.richtext:setWidth(300-30);
            TutorialMessage.instance.richtext:setHeight(120-42);
            TutorialMessage.instance.richtext.text = getText("IGUI_Tutorial1_Fight3Bis", Keyboard.getKeyName(getCore():getKey("Interact")));
            TutorialMessage.instance.richtext:paginate();
        end
    end
    if not FightStep.wasOpen and FightStep.window:IsOpen() then
        FightStep.wasOpen = true;
        TutorialMessage.instance:setWidth(300);
        TutorialMessage.instance:setHeight(70);
--        TutorialMessage.instance.richtext:setWidth(300-30);
--        TutorialMessage.instance.richtext:setHeight(70-42);
        TutorialMessage.instance.richtext.text = getText("IGUI_Tutorial1_Fight3", Keyboard.getKeyName(getCore():getKey("Interact")));
        TutorialMessage.instance.richtext:paginate();
    end
    if getPlayer():getCurrentState():equals(ClimbThroughWindowState.instance()) then
        FightStep.climbThrough = true;
    end
    if FightStep.climbThrough and getPlayer():getCurrentSquare():getX() == FightStep.windowX and getPlayer():getCurrentSquare():getY() == FightStep.windowY then
        return true;
    end
    return false;
end

function FightStep:Aim()
    FightStep.zombie:setImmortalTutorialZombie(true);
    FightStep.zombie:getBodyDamage():RestoreToFullHealth();
    return getPlayer():IsAiming();
end

function FightStep:SneakBehindZombie()
    if not FightStep.highlightFloor then
        local floor = getCell():getGridSquare(FightStep.zombieSpawnX - 1, FightStep.zombieSpawnY, 0):getFloor();
        floor:setHighlighted(true, false);
        floor:setBlink(true);
        floor:setHighlightColor(ColorInfo.new(1,1,1,1));
        FightStep.highlightFloor = floor
    end
    FightStep.zombie:setImmortalTutorialZombie(true);
    if getPlayer():getCurrentSquare():getX() == (FightStep.zombieSpawnX -1) and getPlayer():getCurrentSquare():getY() == FightStep.zombieSpawnY then
        FightStep.highlightFloor:setHighlighted(false)
        return true;
    end
end

function FightStep:HitZombie()
    FightStep.zombie:setImmortalTutorialZombie(false);
    if (FightStep.zombie:getCurrentState() == nil) then
        return false;
    end
    return FightStep.zombie:getCurrentState():equals(StaggerBackDieState.instance());
end

function FightStep:KillZombie()
    local complete = getPlayer():getZombieKills() > 0;
    if complete then
        getPlayer():setDir(IsoDirections.SE);
        getCore():setTutorialDone(true);
    end
    return complete;
end

function FightStep:Shout()
    local complete = getPlayer():isSpeaking();
    if complete then
        getPlayer():setDir(IsoDirections.SE);
    end
    return complete;
end

function FightStep:isPlayedDead()
    FightStep.tickBeforeHordeSpawn = FightStep.tickBeforeHordeSpawn + 1;
    if FightStep.tickBeforeHordeSpawn == 70 then
        createHordeFromTo(getPlayer():getX() - 13, getPlayer():getY() - 13, getPlayer():getX(), getPlayer():getY(), 100);
        createHordeFromTo(getPlayer():getX() + 20, getPlayer():getY() + 20, getPlayer():getX(), getPlayer():getY(), 100);
        createHordeFromTo(getPlayer():getX() + 20, getPlayer():getY() - 13, getPlayer():getX(), getPlayer():getY(), 100);
        createHordeFromTo(getPlayer():getX() - 13, getPlayer():getY() + 20, getPlayer():getX(), getPlayer():getY(), 100);
        addSound(getPlayer(), getPlayer():getX(), getPlayer():getY(), 0, 100, 100);
    end
    if getPlayer():isDead() and not getCore():isTutorialDone() then
        getCore():setTutorialDone(true);
        getCore():saveOptions();
    end
    if getPlayer():isDead() then
        if not FightStep.timeOfDeath then
            FightStep.timeOfDeath = getTimestamp()
            if TutorialMessage.instance then
                TutorialMessage.instance:setVisible(false)
            end
        end
        if FightStep.timeOfDeath + 15 > getTimestamp() then
            addSound(getPlayer(), getPlayer():getX() + 20, getPlayer():getY() + 20, 0, 100, 100); -- Move the zed away to see the reanimation
        end
    end
    return false;
end

function FightStep:isComplete()
    return TutorialStep.isComplete(self);
end

function FightStep:finish()
    TutorialStep.finish(self);
end
