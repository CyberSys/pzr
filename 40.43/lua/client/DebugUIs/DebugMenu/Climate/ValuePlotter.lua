--***********************************************************
--**                    THE INDIE STONE                    **
--**				  Author: turbotutone				   **
--***********************************************************
require "ISUI/ISPanel"

ValuePlotter = ISPanel:derive("ValuePlotter");

function ValuePlotter:initialise()
    ISPanel.initialise(self)
end

function ValuePlotter:createChildren()
end

function ValuePlotter:update()
    ISPanel.update(self);
end

function ValuePlotter:prerender()
    ISPanel.prerender(self);
end

function ValuePlotter:render()
    ISPanel.render(self);
    --local p,w,h = self.waveParams, self:getWidth(), self:getHeight();

    -- grid lines
    --[[
    if self.doGridLines then
        local c = self.isOn and self.gridColor or self.greyCol;
        local interval = h/self.gridVertSpacing;
        for i = 1, self.gridVertSpacing-1 do
            self:drawRect(1, i*interval, w-2, 1, c.a, c.r, c.g, c.b);
        end
        interval = w/self.gridHorzSpacing;
        for i = 1, self.gridHorzSpacing-1 do
            self:drawRect(i*interval, 1, 1, h-2, c.a, c.r, c.g, c.b);
        end
    end

    if self.isOn then
        local mean = (h/2);
        for i = 0, w-1 do
            local index = ((i+self.offset) % (#self.wave-1))+1;
            if self.wave[index] then
                self:drawRect(i, mean+self.wave[index], 1, 1, 1.0, 0.0, 1.0, 0.0);
            else
                print("Missing index: "..tostring(index).." max indexes: "..tostring(#self.wave));
            end
        end
    end
    --]]
    local w = self:getWidth();
    local h = self:getHeight();
    local c,val;

    for i=1,#self.horzBars do
        c = self.horzBars[i].col;
        self:drawRect(0, h*self.horzBars[i].val, w, 1, c.a, c.r, c.g, c.b);
    end

    --local elemcnt = #self.his;
    local prevVal = {};
    local drawY,drawH;
    local index = self.indexPointer;
    for i = 1, self.maxPlotPoints do
        index = self.indexPointer-(i-1);
        if index < 1 then
            index = self.maxPlotPoints + index;
        end

        if self.vertBars[index] then
            c = self.vertBars[index];
            self:drawRect(w-i, 0, 1, self.height, c.a, c.r, c.g, c.b);
        end
        if self.his[index] then
            for j = 1, #self.his[index] do
                if self.vars[j].enabled then
                    c = self.vars[j].color;
                    val = self.his[index][j];
                    --if self.vars[j].offset>0 then
                    --val = val+self.vars[j].offset;
                    --end
                    if val<self.vars[j].min then val = self.vars[j].min; end
                    if val>self.vars[j].max then val = self.vars[j].max; end
                    val = (val-self.vars[j].min)/self.vars[j].diff;
                    val = self.height-(self.height*val);

                    drawY = val;
                    drawH = 1;
                    if prevVal[j]~=nil then
                        if val>prevVal[j] then
                            drawY = prevVal[j];
                            drawH = val-prevVal[j];
                        elseif val<prevVal[j] then
                            drawH = prevVal[j]-val;
                        end
                    end
                    if drawH<1 then drawH = 1; end
                    self:drawRect(w-i, drawY, 1, drawH, c.a, c.r, c.g, c.b);
                    prevVal[j] = val;
                end
            end
        end
    end
end

--add a new plotpoint, have to supply data for each defined variable
function ValuePlotter:addPlotPoint(dataset,vertbarCol)
    for i = 1, #dataset do
        if self.vars[i] and self.vars[i].offset>0 then
            dataset[i] = dataset[i]+self.vars[i].offset;
        end
    end

    self.his[self.indexPointer] = dataset;
    if vertbarCol then
        self.vertBars[self.indexPointer] = vertbarCol;
    else
        self.vertBars[self.indexPointer] = false;
    end

    self.indexPointer = self.indexPointer + 1;
    if self.indexPointer > self.maxPlotPoints then
        self.indexPointer = 1;
    end

    --[[
    table.insert(self.his, 1, dataset);
    if vertbarCol then
        table.insert(self.vertBars, 1, vertbarCol);
    else
        table.insert(self.vertBars, 1, false);
    end
    if #self.his>self.maxPlotPoints then
        table.remove(self.his);
        table.remove(self.vertBars);
    end
    --]]
end

function ValuePlotter:getDataSet()
    return self.his;
end

function ValuePlotter:getVars()
    return self.vars;
end

function ValuePlotter:getVarCount()
    return #self.vars;
end

function ValuePlotter:clearHistory()
    self.his = {};
end

--plotpoints = total points to plot
--function ValuePlotter:init(plotPoints)

--end
function ValuePlotter:setVariableEnabled(_name,_bool)
    for i=1,#self.vars do
        if self.vars[i].name==_name then
            self.vars[i].enabled = _bool;
        end
    end
end

function ValuePlotter:defineVariable(name, color, minVal, maxVal)
    local min = minVal; local max = maxVal;
    local offset = 0;
    if minVal<0 then
        min = math.abs(min);
        max = max+min;
        offset = min;
        min = 0;
    end
    table.insert(self.vars, {
        name = name,
        color = color,
        min = min,
        max = max,
        diff = max-min,
        offset = offset,
        enabled = false,
        --history = {},
    });
end

--add horizontal line in variable scope, variable must be defined prior
function ValuePlotter:setHorzLine(value,col)
    table.insert(self.horzBars,{val=value,col=col});
end


function ValuePlotter:new (x, y, width, height, maxPlotPoints)
    local o = ISPanel:new(x, y, width, height);
    setmetatable(o, self)
    self.__index = self
    o.x = x;
    o.y = y;
    o.background = true;
    o.backgroundColor = {r=0, g=0, b=0, a=1.0};
    o.borderColor = {r=0.8, g=0.8, b=0.8, a=1};
    o.gridColor = {r=0.0, g=0.3, b=0.0, a=1};
    o.greyCol = { r=0.4,g=0.4,b=0.4,a=1};
    o.width = width;
    o.height = height;
    o.anchorLeft = true;
    o.anchorRight = false;
    o.anchorTop = true;
    o.anchorBottom = false;

    o.doGridLines = true;
    o.gridHorzSpacing = 20;
    o.gridVertSpacing = 2;
    o.maxPlotPoints = maxPlotPoints;
    o.indexPointer = 1;
    o.vars = {};
    o.his = {}; --history plotdata
    o.vertBars = {};
    o.horzBars = {};
    return o
end

--[[

require "ISUI/ISPanel"

ValuePlotter = ISPanel:derive("ValuePlotter");

function ValuePlotter:initialise()
    ISPanel.initialise(self)
end

function ValuePlotter:createChildren()
end

function ValuePlotter:update()
    ISPanel.update(self);
end

function ValuePlotter:prerender()
    ISPanel.prerender(self);
end

function ValuePlotter:render()
    ISPanel.render(self);
    --local p,w,h = self.waveParams, self:getWidth(), self:getHeight();

    -- grid lines
    local w = self:getWidth();
    local h = self:getHeight();
    local c,val;

    for i=1,#self.horzBars do
        c = self.horzBars[i].col;
        self:drawRect(0, h*self.horzBars[i].val, w, 1, c.a, c.r, c.g, c.b);
    end

    local elemcnt = #self.his;
    local prevVal = {};
    local drawY,drawH;
    for i = 1, elemcnt do
        if self.vertBars[i] then
            c = self.vertBars[i];
            self:drawRect(w-i, 0, 1, self.height, c.a, c.r, c.g, c.b);
        end
        for j = 1, #self.his[i] do
            if self.vars[j].enabled then
                c = self.vars[j].color;
                val = self.his[i][j];
                --if self.vars[j].offset>0 then
                --val = val+self.vars[j].offset;
                --end
                if val<self.vars[j].min then val = self.vars[j].min; end
                if val>self.vars[j].max then val = self.vars[j].max; end
                val = (val-self.vars[j].min)/self.vars[j].diff;
                val = self.height-(self.height*val);

                drawY = val;
                drawH = 1;
                if prevVal[j]~=nil then
                    if val>prevVal[j] then
                        drawY = prevVal[j];
                        drawH = val-prevVal[j];
                    elseif val<prevVal[j] then
                        drawH = prevVal[j]-val;
                    end
                end
                if drawH<1 then drawH = 1; end
                self:drawRect(w-i, drawY, 1, drawH, c.a, c.r, c.g, c.b);
                prevVal[j] = val;
            end
        end
    end
    end

    --add a new plotpoint, have to supply data for each defined variable
    function ValuePlotter:addPlotPoint(dataset,vertbarCol)
    for i = 1, #dataset do
    if self.vars[i] and self.vars[i].offset>0 then
    dataset[i] = dataset[i]+self.vars[i].offset;
    end
    end
    table.insert(self.his, 1, dataset);
    if vertbarCol then
    table.insert(self.vertBars, 1, vertbarCol);
    else
    table.insert(self.vertBars, 1, false);
    end
    if #self.his>self.maxPlotPoints then
    table.remove(self.his);
    table.remove(self.vertBars);
    end
    end

    function ValuePlotter:getDataSet()
    return self.his;
    end

    function ValuePlotter:getVars()
    return self.vars;
    end

    function ValuePlotter:getVarCount()
    return #self.vars;
    end

    function ValuePlotter:clearHistory()
    self.his = {};
    end

    --plotpoints = total points to plot
    --function ValuePlotter:init(plotPoints)

    --end
    function ValuePlotter:setVariableEnabled(_name,_bool)
    for i=1,#self.vars do
    if self.vars[i].name==_name then
    self.vars[i].enabled = _bool;
    end
    end
    end

    function ValuePlotter:defineVariable(name, color, minVal, maxVal)
    local min = minVal; local max = maxVal;
    local offset = 0;
    if minVal<0 then
    min = math.abs(min);
    max = max+min;
    offset = min;
    min = 0;
    end
    table.insert(self.vars, {
    name = name,
    color = color,
    min = min,
    max = max,
    diff = max-min,
    offset = offset,
    enabled = false,
    --history = {},
    });
    end

    --add horizontal line in variable scope, variable must be defined prior
    function ValuePlotter:setHorzLine(value,col)
    table.insert(self.horzBars,{val=value,col=col});
    end


    function ValuePlotter:new (x, y, width, height, maxPlotPoints)
    local o = ISPanel:new(x, y, width, height);
    setmetatable(o, self)
    self.__index = self
    o.x = x;
    o.y = y;
    o.background = true;
    o.backgroundColor = {r=0, g=0, b=0, a=1.0};
    o.borderColor = {r=0.8, g=0.8, b=0.8, a=1};
    o.gridColor = {r=0.0, g=0.3, b=0.0, a=1};
    o.greyCol = { r=0.4,g=0.4,b=0.4,a=1};
    o.width = width;
    o.height = height;
    o.anchorLeft = true;
    o.anchorRight = false;
    o.anchorTop = true;
    o.anchorBottom = false;

    o.doGridLines = true;
    o.gridHorzSpacing = 20;
    o.gridVertSpacing = 2;
    o.maxPlotPoints = maxPlotPoints;
    o.vars = {};
    o.his = {}; --history plotdata
    o.vertBars = {};
    o.horzBars = {};
    return o
    end



--]]

