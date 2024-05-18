--***********************************************************
--**              	  ROBERT JOHNSON                       **
--***********************************************************

ISItemsListViewer = ISPanel:derive("ISItemsListViewer");
ISItemsListViewer.messages = {};

local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)

--************************************************************************--
--** ISItemsListViewer:initialise
--**
--************************************************************************--

function ISItemsListViewer:initialise()
    ISPanel.initialise(self);
    local btnWid = 100
    local btnHgt = math.max(25, FONT_HGT_SMALL + 3 * 2)
    local padBottom = 10

    local top = 50
    self.panel = ISTabPanel:new(10, top, self.width - 10 * 2, self.height - padBottom - btnHgt - padBottom - top);
    self.panel:initialise();
    self.panel.borderColor = { r = 0, g = 0, b = 0, a = 0};
    self.panel.target = self;
    self:addChild(self.panel);

    self.ok = ISButton:new(10, self:getHeight() - padBottom - btnHgt, btnWid, btnHgt, getText("IGUI_CraftUI_Close"), self, ISItemsListViewer.onClick);
    self.ok.internal = "CLOSE";
    self.ok.anchorTop = false
    self.ok.anchorBottom = true
    self.ok:initialise();
    self.ok:instantiate();
    self.ok.borderColor = {r=1, g=1, b=1, a=0.1};
    self:addChild(self.ok);
    
    self:initList();
end

function ISItemsListViewer:initList()
    self.items = getAllItems();
    -- we gonna separate items by module
    self.module = {};
    for i=0,self.items:size()-1 do
        local item = self.items:get(i);
        if not item:getObsolete() then
            if not self.module[item:getModuleName()] then self.module[item:getModuleName()] = {}; end
            table.insert(self.module[item:getModuleName()], item);
        end
    end

    for i,l in pairs(self.module) do
        -- we ignore the "Moveables" module
        if i ~= "Moveables" then
            local cat1 = ISItemsListTable:new(0, 0, self.panel.width, self.panel.height - self.panel.tabHeight);
            cat1:initialise();
            self.panel:addView(i, cat1);
            cat1.parent = self;
            cat1:initList(l)
        end
    end
    self.panel:activateView("Base");
end

function ISItemsListViewer:prerender()
    local z = 20;
    local splitPoint = 100;
    local x = 10;
    self:drawRect(0, 0, self.width, self.height, self.backgroundColor.a, self.backgroundColor.r, self.backgroundColor.g, self.backgroundColor.b);
    self:drawRectBorder(0, 0, self.width, self.height, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self:drawText(getText("IGUI_AdminPanel_ItemList"), self.width/2 - (getTextManager():MeasureStringX(UIFont.Medium, getText("IGUI_AdminPanel_ItemList")) / 2), z, 1,1,1,1, UIFont.Medium);
end

function ISItemsListViewer:onClick(button)
    if button.internal == "CLOSE" then
        self:setVisible(false);
        self:removeFromUIManager();
    end
end

function ISItemsListViewer.OnOpenPanel()
    if ISItemsListViewer.instance then
        ISItemsListViewer.instance:close()
    end
    local modal = ISItemsListViewer:new(50, 200, 680, 650, getPlayer())
    modal:initialise();
    modal:addToUIManager();
end

--************************************************************************--
--** ISItemsListViewer:new
--**
--************************************************************************--
function ISItemsListViewer:new(x, y, width, height, player)
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
    ISItemsListViewer.instance = o;
    return o;
end
