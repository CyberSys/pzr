require "ISUI/ISCollapsableWindow"

SourceWindow = ISCollapsableWindow:derive("SourceWindow");

local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)

function SourceWindow:onMouseDoubleClickBreakpointToggle(item)
    local line = self.sourceView.selected;
    local file = self.filename;

    toggleBreakpoint(file, line);
end

SourceWindow.map = {}

function SourceWindow:initialise()

    ISCollapsableWindow.initialise(self);

    self.title = getShortenedFilename(self.filename);
end
function SourceWindow:onSourceMouseWheel(del)
    self:setYScroll(self:getYScroll() - (del*18*6));
    return true;
end

function SourceWindow:reloadFile()

    reloadLuaFile(self.filename);
    local y = self.sourceView:getYScroll();
    self:fill();
    self.sourceView:setYScroll(y);
    return true;
end
function SourceWindow:fill()

    self.sourceView:clear();
    local br = getGameFilesTextInput(self.filename);
    local count = 1;
    if br ~= nil then
        local str = "test";
        while str ~= nil do
            str = br:readLine();
            if str ~= nil then
                --         print(str);
                str = str:gsub("\t", "    ")
                self.sourceView:addItem(count.."    "..str, str);
                count = count + 1;
            end
        end

    end
    endTextFileInput();
end
function SourceWindow:createChildren()
    local buttonHgt = math.max(24, FONT_HGT_SMALL + 3 * 2)

    --print("instance");
    ISCollapsableWindow.createChildren(self);

    self.sourceView = ISScrollingListBox:new(0, self:titleBarHeight(), self.width, self.height - self:resizeWidgetHeight() - buttonHgt - self:titleBarHeight());
    self.sourceView.filename = self.filename;
    self.sourceView.anchorRight = true;
    self.sourceView.anchorBottom = true;
    self.sourceView:initialise();
    self.sourceView.doDrawItem = SourceWindow.doDrawItem;
    self.sourceView.prerender = SourceWindow.renderSrc;
    self.sourceView.backgroundColor = {r=0.98, g=0.98, b=0.99, a=1}
    self.sourceView.itemheight = 20;
    self.sourceView:setOnMouseDoubleClick(self, SourceWindow.onMouseDoubleClickBreakpointToggle);

    self:addChild(self.sourceView);
    self.mapView = ISButton:new(0, self:getHeight() - self:resizeWidgetHeight() - buttonHgt, self:getWidth(), buttonHgt, "reload file", self, SourceWindow.reloadFile);
    self.mapView.anchorTop = false;
    self.mapView.anchorRight = true;
    self.mapView.anchorBottom = true;

    self.mapView:initialise();
    self:addChild(self.mapView);

    self.sourceView.onMouseWheel = SourceWindow.onSourceMouseWheel;

    self:fill();
--[[
    -- Do corner x + y widget
    local resizeWidget = ISResizeWidget:new(self.width-10, self.height-10, 10, 10, self);
    resizeWidget:initialise();
    self:addChild(resizeWidget);

    self.resizeWidget = resizeWidget;

    -- Do bottom y widget
    resizeWidget = ISResizeWidget:new(0, self.height-10, self.width-10, 10, self, true);
    resizeWidget.anchorRight = true;
    resizeWidget:initialise();
    self:addChild(resizeWidget);

    self.resizeWidget2 = resizeWidget;
--]]
end

function SourceWindow:renderSrc()
    self:setStencilRect(0,0,self.width+1, self.height);
    self:drawRect(0, -self:getYScroll(), self.width, self.height, self.backgroundColor.a, self.backgroundColor.r, self.backgroundColor.g, self.backgroundColor.b);
    --self:drawRectBorder(0, 0, self.width, self.height, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);

    local y = 0;
    local alt = false;
    if self.items == nil then
        self:clearStencilRect();
        self:repaintStencilRect(0,0,self.width, self.height)
        return;
    end

    local itemheight = self.itemheight;

    local i = 1;
    for k, v in ipairs(self.items) do
        itemheight = self.itemheight;

        if y + self:getYScroll() < -32 or y + self:getYScroll() > self:getHeight()+32 then
            y = y + self.itemheight;
            i = i + 1;
            v.index = i;
        else
            v.index = i;
            y = self:doDrawItem(y, v, alt);

    --        alt = not alt;
            i = i + 1;
        end
    end

    self:setScrollHeight((y));
    self:clearStencilRect();
    self:repaintStencilRect(0,0,self.width, self.height)
end


function SourceWindow:doDrawItem(y, item, alt)
    if self.selected == item.index then
        self:drawRect(0, (y+3), self:getWidth(), self.itemheight-1, 0.2, 0.6, 0.7, 0.8);

    end

    if hasBreakpoint(self.filename, item.index) then
        self:drawRect(0, (y+3), self:getWidth(), self.itemheight-1, 0.3, 0.8, 0.6, 0.4);
    end

    if isCurrentExecutionPoint(self.filename, item.itemindex) then
        self:drawRect(0, (y+3), self:getWidth(), self.itemheight-1, 0.6, 0.6, 0.8, 0.7);
    end

  --  self:drawRectBorder(0, (y), self:getWidth(), self.itemheight, 0.5, self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self:drawText(item.text, 15, (y)+6, 0, 0, 0, 1, UIFont.Code);
    y = y + self.itemheight;
    return y;

end

function SourceWindow:new (x, y, width, height, filename)

     print("creating new sourcewindow: "..filename);
    local o = {}

     local del = getCore():getScreenWidth() / 1920;
     x = getCore():getScreenWidth()-(700*del);
     y = 48;
     width = (700*del);
     height = getCore():getScreenHeight() - (getCore():getScreenHeight()/3) -48
    --o.data = {}
    o = ISCollapsableWindow:new(x, y, width, height);
    setmetatable(o, self)
    self.__index = self
    o.backgroundColor = {r=0, g=0, b=0, a=1.0};
    o.filename = filename;
    o.keepOnScreen = false
    return o
end
