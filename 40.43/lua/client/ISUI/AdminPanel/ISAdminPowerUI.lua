--***********************************************************
--**              	  ROBERT JOHNSON                       **
--***********************************************************

ISAdminPowerUI = ISPanel:derive("ISAdminPowerUI");
ISAdminPowerUI.messages = {};

local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)

--************************************************************************--
--** ISAdminPowerUI:initialise
--**
--************************************************************************--

function ISAdminPowerUI:initialise()
    ISPanel.initialise(self);
    local btnWid = 100
    local btnHgt = math.max(25, FONT_HGT_SMALL + 3 * 2)
    local padBottom = 10

    self.ok = ISButton:new(10, self:getHeight() - padBottom - btnHgt, btnWid, btnHgt, getText("IGUI_RadioSave"), self, ISAdminPowerUI.onClick);
    self.ok.internal = "SAVE";
    self.ok.anchorTop = false
    self.ok.anchorBottom = true
    self.ok:initialise();
    self.ok:instantiate();
    self.ok.borderColor = {r=1, g=1, b=1, a=0.1};
    self:addChild(self.ok);
    
    self.tickBox = ISTickBox:new(30, 50, 100, FONT_HGT_SMALL + 5, "Admin Powers", self, self.onTicked)
    self.tickBox.backgroundColor.a = 1
    self.tickBox.background = true
    self.tickBox.choicesColor = {r=1, g=1, b=1, a=1}
    self.tickBox.leftMargin = 2
    self.tickBox:setFont(UIFont.Small)
    self:addChild(self.tickBox);

    self.richText = ISRichTextLayout:new(self.width - 30 * 2)
    self.richText.marginLeft = 0
    self.richText.marginTop = 0
    self.richText.marginRight = 0
    self.richText.marginBottom = 0
    self.richText:setText(getText("IGUI_AdminPanel_ShowAdminTag"))
    self.richText:initialise()
    self.richText:paginate()

    self:addAdminPowerOptions()
end

function ISAdminPowerUI:addAdminPowerOptions()
    self.tickBox:addOption("Invisible");
    self.tickBox.selected[1] = self.player:isInvisible();
    self.tickBox:addOption("God mode");
    self.tickBox.selected[2] = self.player:isGodMod();
    self.tickBox:addOption("No Clip");
    self.tickBox.selected[3] = self.player:isNoClip();
    self.tickBox:addOption("Unlimited Carry");
    self.tickBox.selected[4] = self.player:isUnlimitedCarry();
    self.tickBox:addOption(getText("IGUI_AdminPanel_BuildCheat"));
    self.tickBox.selected[5] = ISBuildMenu.cheat;
    self.tickBox:addOption(getText("IGUI_AdminPanel_MechanicsCheat"));
    self.tickBox.selected[6] = ISVehicleMechanics.cheat;
    self.tickBox:addOption(getText("IGUI_AdminPanel_HealthCheat"));
    self.tickBox.selected[7] = ISHealthPanel.cheat;
    self.tickBox:addOption(getText("IGUI_AdminPanel_MoveableCheat"));
    self.tickBox.selected[8] = ISMoveableDefinitions.cheat;

    self.tickBox:setWidthToFit()

    self:setHeight(self.tickBox:getBottom() + 40 +
        self.richText:getHeight() + 20 + self.ok:getHeight() + 10)
end

function ISAdminPowerUI:onTicked(index, selected)
    
end

function ISAdminPowerUI:prerender()
    local z = 20;
    local splitPoint = 100;
    local x = 10;
    self:drawRect(0, 0, self.width, self.height, self.backgroundColor.a, self.backgroundColor.r, self.backgroundColor.g, self.backgroundColor.b);
    self:drawRectBorder(0, 0, self.width, self.height, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self:drawText(getText("IGUI_AdminPanel_AdminPower"), self.width/2 - (getTextManager():MeasureStringX(UIFont.Medium, getText("IGUI_AdminPanel_AdminPower")) / 2), z, 1,1,1,1, UIFont.Medium);

    self.richText:render(30, self.ok.y - 20 - self.richText:getHeight(), self)
end

function ISAdminPowerUI:onClick(button)
    if button.internal == "SAVE" then
        self.player:setInvisible(self.tickBox.selected[1]);
        self.player:setGodMod(self.tickBox.selected[2]);
        self.player:setNoClip(self.tickBox.selected[3]);
        self.player:setUnlimitedCarry(self.tickBox.selected[4]);
        ISBuildMenu.cheat = self.tickBox.selected[5];
        self.player:setBuildCheat(ISBuildMenu.cheat);
        ISVehicleMechanics.cheat = self.tickBox.selected[6];
        self.player:setMechanicsCheat(ISVehicleMechanics.cheat);
        ISHealthPanel.cheat = self.tickBox.selected[7];
        self.player:setHealthCheat(ISHealthPanel.cheat);
        ISMoveableDefinitions.cheat = self.tickBox.selected[8];
--        self.player:setHealthCheat(ISMoveableDefinitions.cheat); -- TODO: add a boolean to store this (didn't do it now to not touch saving as we're close to public release)
    
        self.player:setShowAdminTag(false);
        for i,v in pairs(self.tickBox.selected) do
            if self.tickBox.selected[i] then
                self.player:setShowAdminTag(true);
                break;
            end
        end
    
        sendPlayerExtraInfo(self.player)
    
        self:setVisible(false);
        self:removeFromUIManager();
    end
end

ISAdminPowerUI.onGameStart = function()
    if getPlayer():isBuildCheat() then ISBuildMenu.cheat = true; end
    if getPlayer():isMechanicsCheat() then ISVehicleMechanics.cheat = true; end
    if getPlayer():isHealthCheat() then ISHealthPanel.cheat = true; end
end

--************************************************************************--
--** ISAdminPowerUI:new
--**
--************************************************************************--
function ISAdminPowerUI:new(x, y, width, height, player)
    local o = {}
    x = getCore():getScreenWidth() / 2 - (width / 2);
    y = getCore():getScreenHeight() / 2 - (height / 2);
    o = ISPanel:new(x, y, width, height);
    setmetatable(o, self)
    self.__index = self
    o.borderColor = {r=0.4, g=0.4, b=0.4, a=1};
    o.backgroundColor = {r=0, g=0, b=0, a=0.8};
    o.width = width;
    o.height = height;
    o.player = player;
    o.moveWithMouse = true;
    ISAdminPowerUI.instance = o;
    return o;
end

Events.OnGameStart.Add(ISAdminPowerUI.onGameStart)
