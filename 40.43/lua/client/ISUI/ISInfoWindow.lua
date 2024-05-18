require "ISUI/ISPanel"
require "ISUI/ISRichTextPanel"
require "ISUI/ISButton"

ISInfoWindow = ISPanel:derive("ISInfoWindow");


--************************************************************************--
--** ISInfoWindow:initialise
--**
--************************************************************************--

function ISInfoWindow:initialise()
    ISPanel.initialise(self);
end

--************************************************************************--
--** ISPanel:instantiate
--**
--************************************************************************--
function ISInfoWindow:createChildren()

    -- CREATE TUTORIAL PANEL
    local panel = ISRichTextPanel:new(0, 0, self.width, self.height);
    panel:initialise();

    self:addChild(panel);
    --panel:paginate();
    self.richtext = panel;

end

function ISInfoWindow:setInfo(item)
   if item == self.lastUsed then
       return;
   end

   if instanceof(item, "InventoryItem")  then
       self.richtext.text = ISInventoryItemTooltip.createFromItem(item);
       self.richtext:paginate();
       self.lastUsed = item;
   end
end

--************************************************************************--
--** ISInfoWindow:render
--**
--************************************************************************--
function ISInfoWindow:prerender()
    self:drawRect(0, 0, self.width, self.height, self.backgroundColor.a, self.backgroundColor.r, self.backgroundColor.g, self.backgroundColor.b);
    self:drawRect(0, 0, self.width, 1, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self:drawRect(0, self.height-1, self.width, 1, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self:drawRect(0, 0, 1, self.height, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self:drawRect(0+self.width-1, 0, 1, self.height, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);
    --self:setStencilRect(0,0,self.width-1, self.height-1);
end

function ISInfoWindow:render()

    --self:clearStencilRect();

end


--************************************************************************--
--** ISInfoWindow:update
--**
--************************************************************************--
function ISInfoWindow:update()


    local w = getCore():getScreenWidth();
    local h = getCore():getScreenHeight();

    self:setHeight(self.richtext:getHeight());
    self:setX(w - self:getWidth());
    self:setY(h - self:getHeight());

end


ISInfoWindow.getInstance = function()
    if ISInfoWindow.instance ~= nil then
        return ISInfoWindow.instance;
    end;
    ISInfoWindow.instance = ISInfoWindow:new(0, 0, 100, 0);
    ISInfoWindow.instance:initialise();
    ISInfoWindow.instance:addToUIManager();
    return ISInfoWindow.instance;
end

--************************************************************************--
--** ISInfoWindow:new
--**
--************************************************************************--
function ISInfoWindow:new (x, y, width, height)
    local o = {}
    --o.data = {}
    o = ISPanel:new(x, y, width, height);
    setmetatable(o, self)
    self.__index = self
    o.x = x;
    o.y = y;
    o.borderColor = {r=1, g=1, b=1, a=0.7};
    o.backgroundColor = {r=0, g=0, b=0, a=0.5};
    o.width = width;
    o.height = height;
    o.anchorLeft = true;
    o.anchorRight = false;
    o.anchorTop = true;
    o.anchorBottom = false;
    return o
end
