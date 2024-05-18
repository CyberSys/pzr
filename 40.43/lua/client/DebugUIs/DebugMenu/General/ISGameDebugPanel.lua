--***********************************************************
--**                    THE INDIE STONE                    **
--**				  Author: turbotutone				   **
--***********************************************************

require "DebugUIs/DebugMenu/Base/ISDebugSubPanelBase";

ISGameDebugPanel = ISDebugSubPanelBase:derive("ISGameDebugPanel");

function ISGameDebugPanel:initialise()
    ISPanel.initialise(self);
end

function ISGameDebugPanel:createChildren()
    ISPanel.createChildren(self);

    local v, obj;

    local x,y,w,margin = 10,10,self.width-30,5;

    self:initHorzBars(x,w);

    self.sliderOptions = {};

    y, obj = ISDebugUtils.addLabel(self,"game_title",x+(w/2),y,"General Game Options", UIFont.Medium);
    obj.center = true;

    local gt = getGameTime();
    self:addSliderOption(gt,"GameSpeed", 0, 1000, 0.1, "getTrueMultiplier", "setMultiplier");

    local barMod = 3;
    local y2, label, value, slider;
    for k,v in ipairs(self.sliderOptions) do
        y2,label = ISDebugUtils.addLabel(self,v,x,y,v.title or v.var, UIFont.Small);

        y2,value = ISDebugUtils.addLabel(self,v,x+(w/2)-20,y,"0", UIFont.Small, false);
        y,slider = ISDebugUtils.addSlider(self,v,x+(w/2),y,w/2, 18, ISGameDebugPanel.onSliderChange);
        slider.valueLabel = value;

        v.label = label;
        v.labelValue = value;
        v.slider = slider;
        --slider:setCurrentValue(v.java[v.get](v.java[v.get]));
        slider:setValues(v.min, v.max, v.step, v.step*10, true);
        local val = v.java[v.get](v.java) + v.applyMod;
        --print(v.var.." = "..tostring(val))
        slider:setCurrentValue(val);

        y = ISDebugUtils.addHorzBar(self,y+barMod)+barMod;
    end


    self:setScrollHeight(y+10);
end

function ISGameDebugPanel:addSliderOption(_java, _var, _min, _max, _step, _get, _set)
    local option = {
        java = _java,
        var = _var,
        min = _min,
        max = _max,
        step = _step or 0.01,
        get = _get or "get".._var,
        set = _set or "set".._var,
        applyMod = 0,
    };
    table.insert(self.sliderOptions,option);
    return option;
end

function ISGameDebugPanel:prerender()
    ISDebugSubPanelBase.prerender(self);

    local val;
    for k,v in ipairs(self.sliderOptions) do
        val = v.java[v.get](v.java);
        v.slider.currentValue = val + v.applyMod;

        if v.slider.pretext then
            v.labelValue:setName(v.slider.pretext..ISDebugUtils.printval(val,3));
        else
            v.labelValue:setName(ISDebugUtils.printval(val,3));
        end
    end
end

function ISGameDebugPanel:onClick(_button)
end

function ISGameDebugPanel:onSliderChange(_newval, _slider)
    local v = _slider.customData;

    if v.var=="GameSpeed" then
        if _newval<1 then
            _newval = 1;
        end
    end
    v.java[v.set](v.java,_newval-v.applyMod);
end

function ISGameDebugPanel:onTicked(_index, _selected, _arg1, _arg2, _tickbox)
end

function ISGameDebugPanel:onTickedValue(_index, _selected, _arg1, _arg2, _tickbox)
end

function ISGameDebugPanel:update()
    ISPanel.update(self);
end

function ISGameDebugPanel:new(x, y, width, height, doStencil)
    local o = {};
    o = ISDebugSubPanelBase:new(x, y, width, height, doStencil);
    setmetatable(o, self);
    self.__index = self;
    return o;
end


