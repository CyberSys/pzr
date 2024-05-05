--
-- Created by IntelliJ IDEA.
-- User: RJ
-- Date: 20/02/14
-- Time: 11:29
-- To change this template use File | Settings | File Templates.
--
ISSafetyUI = ISPanel:derive("ISSafetyUI");

function ISSafetyUI:initialise()
    ISPanel.initialise(self);
end

function ISSafetyUI:createChildren()
    --ISPanel.createChildren(self)
    self.radialIcon = ISRadialProgressBar:new(0, 0, self.width, self.height, nil);
    self.radialIcon:setVisible(false);
    self:addChild(self.radialIcon);
end


ISSafetyUI.initUI = function()
    if not isClient() then return end
    if getServerOptions():getBoolean("SafetySystem") then
        Events.OnKeyPressed.Add(ISSafetyUI.onKeyPressed);
    end
end

function ISSafetyUI:prerender()
    local gt = getGameTime();
    self.radialIcon:setVisible(false);

    self.drawLock = false;
    if self.toggleTimer > 0 then
        self.radialIcon:setVisible(true);
        self.toggleTimer = self.toggleTimer - gt:getRealworldSecondsSinceLastUpdate() * getPerformance():getFramerate() / getPerformance():getUIRenderFPS();
        if self.toggleTimer<=0 then
            self.toggleTimer = 0;

            self.character:setSafety(not self.character:isSafety());
            self.character:setSafetyCooldown(self.cooldownTimeMax);
            if not self.lastState then
                toggleSafetyServer(self.character)
            end
        end

        local delta = self.toggleTimer / self.toggleTimeMax;
        --delta = 1 - delta;

        self.radialIcon:setValue(delta);
        if self.lastState then
            self.radialIcon:setTexture(self.onTexture);
        else
            self.radialIcon:setTexture(self.offTexture);
        end
        self.drawLock = true;
    end

    if self.character:getSafetyCooldown() > 0 and self.toggleTimer==0 then
        self.radialIcon:setVisible(true);
        self.cooldownTimer = self.cooldownTimeMax - self.toggleTimeMax;
        --print("--------------")
        --print(tostring(self.character:getSafetyCooldown()))
        --print(tostring(self.cooldownTimer))
        --print(tostring(self.cooldownTimeMax))

        local delta = self.character:getSafetyCooldown() / self.cooldownTimeMax;
        delta = 1 - delta;

        self.radialIcon:setValue(delta);

        if self.lastState then
            self.radialIcon:setTexture(self.offTexture);
        else
            self.radialIcon:setTexture(self.onTexture);
        end
        self.drawLock = true;
    end

    --local screenX = getPlayerScreenLeft(self.playerNum)
    --local screenWid = getPlayerScreenWidth(self.playerNum)
    --self:setX(screenX + screenWid - 200)
    --local textWid = getTextManager():MeasureStringX(UIFont.Small, getText("IGUI_PvpZone_NonPvpZone"))
    --local textX = math.min(self.x + self.width/2 - textWid/2, screenX + screenWid - 20 - textWid)
    --self:drawText(getText("IGUI_PvpZone_NonPvpZone"), textX - self.x, - 20, 1, 0, 0, 1, self.Small);

    if NonPvpZone.getNonPvpZone(self.character:getX(), self.character:getY()) then
        self.nonPvpZone = true;
        self:drawTexture(self.disableTexture, 0,0,1,1,1,1);
        self.radialIcon:setVisible(false);
        if self.safetyEnabled then
            if not self.character:isSafety() then
                self.character:setSafety(true);
                toggleSafetyServer(self.character);
            end
        end
        if self:isMouseOver() then
            self:drawText(getText("IGUI_PvpZone_NonPvpZone"), self.width + 10, self.height/2, 1, 0, 0, 1, self.Small);
        end;
    elseif self.safetyEnabled then
        self.nonPvpZone = false;
        if self.toggleTimer > 0 then
            if self.lastState then
                self:drawTexture(self.offLockedTexture,0,0,1,self.backdropAlpha,self.backdropAlpha,self.backdropAlpha);
            else
                self:drawTexture(self.onLockedTexture,0,0,1,self.backdropAlpha,self.backdropAlpha,self.backdropAlpha);
            end
        else
            if self.character:getSafetyCooldown() == 0 then
                if self.character:isSafety() then
                    self:drawTexture(self.onTexture,0,0,1,1,1,1);
                else
                    self:drawTexture(self.offTexture,0,0,1,1,1,1);
                end
            else
                if self.character:isSafety() then
                    self:drawTexture(self.onTexture,0,0,1,self.backdropAlpha,self.backdropAlpha,self.backdropAlpha);
                else
                    self:drawTexture(self.offTexture,0,0,1,self.backdropAlpha,self.backdropAlpha,self.backdropAlpha);
                end
            end
        end
    end
end

function ISSafetyUI:render()
    if self.drawLock then
        self:drawTexture(self.lockTexture, 0,0,1,1,1,1);
    end
    --if true then return end -- remove this to have time displayed again... might need a different position tho for visibility
    if self.character:getSafetyCooldown() > 0 or self.toggleTimer > 0 then
        local x = self:getWidth() + 12;
        local y = -3;
        if self.toggleTimer > 0 then
            self:drawText(tostring(math.ceil(self.toggleTimer)), x, y, 1,1,1,1, UIFont.Small);
        else
            self:drawText(tostring(math.ceil(self.character:getSafetyCooldown())), x, y, 1,1,1,1, UIFont.Small);
        end
    end
end

--[[
function ISSafetyUI:renderOLD()
    local screenX = getPlayerScreenLeft(self.playerNum)
    local screenWid = getPlayerScreenWidth(self.playerNum)
    self:setX(screenX + screenWid - 150)
    if NonPvpZone.getNonPvpZone(self.character:getX(), self.character:getY()) then
        self.nonPvpZone = true;
        local textWid = getTextManager():MeasureStringX(UIFont.Small, getText("IGUI_PvpZone_NonPvpZone"))
        local textX = math.min(self.x + self.width/2 - textWid/2, screenX + screenWid - 20 - textWid)
        self:drawText(getText("IGUI_PvpZone_NonPvpZone"), textX - self.x, - 20, 1, 0, 0, 1, self.Small);
        if self.safetyEnabled then
            self:drawTexture(self.disableTexture, 0,0,1,1,1,1);
            if not self.character:isSafety() then
                self.character:setSafety(true);
                toggleSafetyServer(self.character);
            end
        end
    elseif self.safetyEnabled then
        self.nonPvpZone = false;
        if self.character:getSafetyCooldown() == 0 then
            if self.character:isSafety() then
                self:drawTexture(self.onTexture, 0,0,1,1,1,1);
            else
                self:drawTexture(self.offTexture,0,0,1,1,1,1);
            end
        else
            self:drawTexture(self.pendingTexture,0,0,1,1,1,1);
            self:drawTextCentre(tostring(math.ceil(self.character:getSafetyCooldown())), self:getWidth() / 2, (self:getHeight() / 2) - 10, 1,1,1,1, UIFont.Small);
        end
    end
end
--]]

ISSafetyUI.onKeyPressed = function(key)
    if key == getCore():getKey("Toggle Safety") then
        if getPlayerSafetyUI(0) then
            getPlayerSafetyUI(0):toggleSafety()
        end
    end
end

function ISSafetyUI:toggleSafety()
    self.toggleTimeMax = getServerOptions():getInteger("SafetyToggleTimer")
    self.cooldownTimeMax = getServerOptions():getInteger("SafetyCooldownTimer")

    if getServerOptions():getBoolean("SafetySystem") and self.character:getSafetyCooldown() == 0 then
        --ISTimedActionQueue.clear(self.character);
        --ISTimedActionQueue.add(ISToggleSafetyAction:new(self.character));
        self.lastState = self.character:isSafety();
        self.toggleTimer = self.toggleTimeMax;

        if self.lastState or self.toggleTimeMax==0 then
            toggleSafetyServer(self.character);
            if self.toggleTimeMax==0 then
                self.character:setSafety(not self.character:isSafety());
                self.character:setSafetyCooldown(self.cooldownTimeMax);
            end
        end

        ISLogSystem.logAction(self);

    end
end

function ISSafetyUI:onMouseUp(x, y)
    if not self.nonPvpZone and self.safetyEnabled then
        self:toggleSafety()
    end
end

function ISSafetyUI:getExtraLogData()
    return {
        (self.character:isSafety() and "Safety On") or "Safety Off",
    };
end

function ISSafetyUI:new(x, y, playerNum)
    local onTexture = getTexture("media/ui/pvpicon_off.png"); --getTexture("media/ui/SafetyON.png");
    local o = ISPanel:new(x, y, onTexture:getWidth(), onTexture:getHeight());
    setmetatable(o, self)
    self.__index = self
    o.x = x;
    o.y = y;
    o.borderColor = {r=0, g=0, b=0, a=0};
    o.backgroundColor = {r=0, g=0, b=0, a=0};
    o.width = onTexture:getWidth();
    o.height = onTexture:getHeight();
    o.anchorLeft = true;
    o.anchorRight = false;
    o.anchorTop = true;
    o.safetyEnabled = getServerOptions():getBoolean("SafetySystem");
    o.anchorBottom = false;
    --    o.pvpText = "Disabled";
    o.offTexture = getTexture("media/ui/pvpicon_on.png"); --getTexture("media/ui/SafetyOFF.png");
    o.pendingTexture = getTexture("media/ui/SafetyPEND.png"); --getTexture("media/ui/SafetyPEND.png");
    o.disableTexture = getTexture("media/ui/pvpicon_off.png"); --getTexture("media/ui/SafetyDISABLE.png");
    o.onTexture = onTexture;
    o.offLockedTexture = getTexture("media/ui/pvpicon_on.png"); --getTexture("media/ui/safetyOffLocked.png");
    o.onLockedTexture = getTexture("media/ui/pvpicon_off.png"); --getTexture("media/ui/safetyOnLocked.png");
    o.lockTexture = getTexture("media/ui/pvpicon_clock.png");
    o.noBackground = true;
    o.nonPvpZone = false;
    o.playerNum = playerNum;
    o.character = getSpecificPlayer(playerNum);

    o.toggleTimeMax = getServerOptions():getInteger("SafetyToggleTimer"); -- * 30 * 1.6;
    o.cooldownTimeMax = getServerOptions():getInteger("SafetyCooldownTimer");
    o.backdropAlpha = 0.5; -- alpha of the backdrop transition too state
    o.toggleTimer = 0;
    o.cooldownTimer = 0;
    o.lastState = o.character:isSafety();

    return o
end

Events.OnGameStart.Add(ISSafetyUI.initUI);

if not isClient() then
    Events.OnKeyPressed.Add(
        function(key)
            if key == getCore():getKey("Toggle Safety") then
                IsoPlayer.setCoopPVP(not IsoPlayer.getCoopPVP())
            end
        end
    )
end
