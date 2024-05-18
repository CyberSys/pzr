--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "ISUI/ISPanel"

ISItemsListTable = ISPanel:derive("ISItemsListTable");

local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)
local FONT_HGT_MEDIUM = getTextManager():getFontHeight(UIFont.Medium)
local FONT_HGT_LARGE = getTextManager():getFontHeight(UIFont.Large)

local HEADER_HGT = FONT_HGT_MEDIUM + 2 * 2

function ISItemsListTable:initialise()
    ISPanel.initialise(self);
end


function ISItemsListTable:render()
    ISPanel.render(self);
    
    local y = self.datas.y + self.datas.height + 5
    self:drawText(getText("IGUI_DbViewer_TotalResult") .. self.totalResult, 0, y, 1,1,1,1,UIFont.Small)
    self:drawTextRight(getText("IGUI_ItemList_Info"), self.datas.x + self.datas.width, y, 1,1,1,1,UIFont.Small)

    y = self.filters:getBottom()
    
    self:drawRectBorder(self.datas.x, y, self.datas:getWidth(), HEADER_HGT, 1, self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self:drawRect(self.datas.x, y+1, self.datas:getWidth(), HEADER_HGT, self.listHeaderColor.a, self.listHeaderColor.r, self.listHeaderColor.g, self.listHeaderColor.b);

    local x = 10;
    for _, v in ipairs(self.datas.columns) do
        local size = v.size; if size == 0 then size = 100; end
--        print(v.name, x, v.size)
        self:drawText(v.name, x+3, y+2, 1,1,1,1,UIFont.Small);
        self:drawRectBorder(self.datas.x + x - 10, y, 1, self.datas.itemheight + 1, 1, self.borderColor.r, self.borderColor.g, self.borderColor.b);
        x = x + size;
    end
end

function ISItemsListTable:new (x, y, width, height)
    local o = ISPanel:new(x, y, width, height);
    setmetatable(o, self);
    o.listHeaderColor = {r=0.4, g=0.4, b=0.4, a=0.3};
    o.borderColor = {r=0.4, g=0.4, b=0.4, a=0};
    o.backgroundColor = {r=0, g=0, b=0, a=1};
    o.buttonBorderColor = {r=0.7, g=0.7, b=0.7, a=0.5};
    o.totalResult = 0;
    o.filters = {};
    ISItemsListTable.instance = o;
    return o;
end

function ISItemsListTable:createChildren()
    ISPanel.createChildren(self);
    
    local btnWid = 100
    local btnHgt = math.max(25, FONT_HGT_SMALL + 3 * 2)
    local entryHgt = FONT_HGT_MEDIUM + 2 * 2
    local bottomHgt = 5 + FONT_HGT_SMALL + 5 + btnHgt + 20 + FONT_HGT_LARGE + HEADER_HGT + entryHgt

    self.datas = ISScrollingListBox:new(0, HEADER_HGT, self.width, self.height - bottomHgt - HEADER_HGT);
    self.datas:initialise();
    self.datas:instantiate();
    self.datas.itemheight = FONT_HGT_SMALL + 4 * 2
    self.datas.selected = 0;
    self.datas.joypadParent = self;
    self.datas.font = UIFont.NewSmall;
    self.datas.doDrawItem = self.drawDatas;
    self.datas.drawBorder = true;
    self.datas.parent = self;
    self.datas:addColumn("Type", 0);
    self.datas:addColumn("Name", 150);
    self.datas:addColumn("Category", 350);
    self.datas:setOnMouseDoubleClick(self, ISItemsListTable.addItem);
    self:addChild(self.datas);
    
    self.modify = ISButton:new(0, self.datas.y + self.datas.height + 5 + FONT_HGT_SMALL + 5, btnWid, btnHgt, "Add Item In Inventory", self, ISItemsListTable.onOptionMouseDown);
    self.modify.internal = "ADDITEM";
    self.modify:initialise();
    self.modify:instantiate();
    self.modify.enable = false;
    self.modify.parent = self;
    self.modify.borderColor = self.buttonBorderColor;
    self:addChild(self.modify);

    self.filters = ISLabel:new(0, self.modify:getBottom() + 20, FONT_HGT_LARGE, getText("IGUI_DbViewer_Filters"), 1, 1, 1, 1, UIFont.Large, true)
    self.filters:initialise()
    self.filters:instantiate()
    self:addChild(self.filters)
    
    local x = 0;
    local entryY = self.filters:getBottom() + self.datas.itemheight
    for _, column in pairs(self.datas.columns) do
        local size = column.size;
        if _ == #self.datas.columns then -- last column take all the remaining width
            size = self.datas:getWidth() - x;
        end
        if size == 0 then size = 100; end
        self.entry = ISTextEntryBox:new("", x, entryY, size, entryHgt);
        self.entry.font = UIFont.Medium
        self.entry:initialise();
        self.entry:instantiate();
        self.entry.columnName = column.name;
        self.entry.onTextChange = ISItemsListTable.onFilterChange;
        self.entry.target = self;
        self.entry.parent = self;
        self:addChild(self.entry);
        table.insert(self.filters, self.entry);
        x = x + size;
    end
end

function ISItemsListTable:addItem(item)
    getPlayer():getInventory():AddItem(item:getModuleName() .. "." .. item:getName())
end

function ISItemsListTable:onOptionMouseDown(button, x, y)
    if button.internal == "ADDITEM" then
        local item = button.parent.datas.items[button.parent.datas.selected].item;
--        self:addItem(button.parent.datas.items[button.parent.datas.selected].item);
        local modal = ISTextBox:new(0, 0, 280, 180, "Add x item(s): " .. item:getDisplayName(), "1", self, ISItemsListTable.onAddItem, nil, item);
        modal:initialise();
        modal:addToUIManager();
        modal:setOnlyNumbers(true);
    end
end

function ISItemsListTable:onAddItem(button, item)
    if button.internal == "OK" then
        for i=0,tonumber(button.parent.entry:getText()) - 1 do
            self:addItem(item);
        end
    end
end

function ISItemsListTable:initList(module)
    self.totalResult = 0;
    for x,v in ipairs(module) do
        self.datas:addItem(v:getDisplayName(), v);
        self.totalResult = self.totalResult + 1;
    end
    table.sort(self.datas.items, function(a,b) return not string.sort(a.item:getDisplayName(), b.item:getDisplayName()); end);
end



function ISItemsListTable:update()
    self.modify.enable = self.datas.selected > 0;
end

function ISItemsListTable.onFilterChange(entry, combo)
    local datas = entry.parent.datas;
    if not datas.fullList then datas.fullList = datas.items; end
    entry.parent.totalResult = 0;
    datas:clear();
--print(entry.parent, combo)
--    local filterTxt = entry:getInternalText();
--    if filterTxt == "" then datas.items = datas.fullList; return; end
    for i,v in ipairs(datas.fullList) do -- check every items
        local add = true;
        for j,b in ipairs(entry.parent.filters) do -- check every filters
            local filterTxt = b:getInternalText();
            local txtToCheck = "";
            if b.columnName == "Type" then
                txtToCheck = v.item:getName();
                if not string.match(string.lower(txtToCheck), string.lower(filterTxt)) then
                    add = false;
                end
            end
            if b.columnName == "Name" then
                txtToCheck = v.item:getDisplayName();
                if not string.match(string.lower(txtToCheck), string.lower(filterTxt)) then
                    add = false;
                end
            end
            if b.columnName == "Category" then
                txtToCheck = v.item:getTypeString();
                if not string.match(string.lower(txtToCheck), string.lower(filterTxt)) then
                    add = false;
                end
            end
        end
        if add then
            datas:addItem(i, v.item);
            entry.parent.totalResult = entry.parent.totalResult + 1;
        end
    end
end

function ISItemsListTable:drawDatas(y, item, alt)
    if y + self:getYScroll() + self.itemheight < 0 or y + self:getYScroll() >= self.height then
        return y + self.itemheight
    end
    
    local a = 0.9;

    if self.selected == item.index then
        self:drawRect(0, (y), self:getWidth(), self.itemheight, 0.3, 0.7, 0.35, 0.15);
    end

    if alt then
        self:drawRect(0, (y), self:getWidth(), self.itemheight, 0.3, 0.6, 0.5, 0.5);
    end

    self:drawRectBorder(0, (y), self:getWidth(), self.itemheight, a, self.borderColor.r, self.borderColor.g, self.borderColor.b);

    local xoffset = 10;
    self:drawText(item.item:getName(), xoffset, y + 4, 1, 1, 1, a, self.font);
    self:drawText(item.item:getDisplayName(), self.columns[2].size + xoffset, y + 4, 1, 1, 1, a, self.font);
    self:drawText(item.item:getTypeString(), self.columns[3].size + xoffset, y + 4, 1, 1, 1, a, self.font);
    
    return y + self.itemheight;
end
