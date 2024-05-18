--***********************************************************
--**                    THE INDIE STONE                    **
--**				  Author: turbotutone				   **
--***********************************************************

ISWorldMenuElements = ISWorldMenuElements or {};

function ISWorldMenuElements.ContextDisassemble()
    local self 					= ISMenuElement.new();

    function self.init()
    end

    function self.createMenu( _data )
        if _data.test then return true; end

        local validObjList = {};
        --if _data.object and instanceof(_data.object, "IsoDoor") then
            --print("door");
            --print(_data.object:getSprite():getName());
            --local moveProps = ISMoveableSpriteProps.fromObject( _data.object );
            --if moveProps then
                --local canScrap, chance, perkname = moveProps:canScrapObject( _data.player );
                --if canScrap then
                    --table.insert(validObjList, { object = _data.object, moveProps = moveProps, square = _data.object:getSquare(), chance = chance, perkName = perkname });
                --end
            --end
        --else
            for _,object in ipairs(_data.objects) do
--                if object:getSprite() and object:getSprite():getName() then
--                    print(object:getSprite():getName());
--                end
                local square = object:getSquare();
                if square then
                    local moveProps = ISMoveableSpriteProps.fromObject( object );
                    -- Check for partially-destroyed multi-tile objects.
                    if moveProps.isMultiSprite then
                        local grid = moveProps:getSpriteGridInfo(object:getSquare(), true)
                        if not grid then moveProps = nil end
                    end
                    if moveProps then
                        local resultScrap, chance, perkname = moveProps:canScrapObject( _data.player );
                        if ISMoveableDefinitions:getInstance().isScrapDefinitionValid(moveProps.material) then
                            table.insert(validObjList, { object = object, moveProps = moveProps, square = square, chance = chance, perkName = perkname, resultScrap = resultScrap });
                        end
                    end
                end
            end
        --end

        if #validObjList > 0 then
            local disassembleMenu = _data.context:addOption(getText("ContextMenu_Disassemble"), _data.player, nil);
            local subMenu = ISContextMenu:getNew(_data.context);
            _data.context:addSubMenu(disassembleMenu, subMenu);
            local addedSomething = false;

            for k,v in ipairs(validObjList) do
                if v.resultScrap.craftValid then
                    local perkname = "";
                    if v.perkName then
                        perkname = " (" .. v.perkName .. ")";
                    end
                    local option = subMenu:addOption(Translator.getMoveableDisplayName(v.moveProps.name) .. perkname, _data, self.disassemble, v );
                    addedSomething = true;
                    if v.resultScrap.containerFull and not ISMoveableDefinitions.cheat then
                        option.notAvailable = true;
                        local toolTip = ISToolTip:new();
                        toolTip:initialise();
                        toolTip:setVisible(false);
                        toolTip.description = getText("ContextMenu_ContainerNotEmpty");
                        option.toolTip = toolTip;
                    elseif not ISMoveableDefinitions.cheat and (not v.resultScrap.haveTool or not v.resultScrap.haveTool2) then
                        local scrapDef = ISMoveableDefinitions:getInstance().getScrapDefinition(v.moveProps.material);
                        local missingTools = "";
                        local color = " <RGB:1,1,1> ";
                        local haveTool = false;
                        if scrapDef then
                            local tools = scrapDef.tools;
                            if tools then
                                if #tools <=0 then return true; end
                                for _,v in ipairs(tools) do
                                    local item = InventoryItemFactory.CreateItem(v);
                                    if v and item then
                                        if _data.player:getInventory():FindAndReturn(v) then
                                            haveTool = true;
                                        end
                                        if missingTools == "" then
                                            missingTools = item:getDisplayName();
                                        else
                                            missingTools = missingTools .. "/" .. item:getDisplayName();
                                        end
                                    end
                                end
                                if not haveTool then color = " <RGB:1,0,0> " end
                                missingTools = getText("ContextMenu_MissingTools") .. " <LINE> " .. " " .. color .. " " .. missingTools;
                            end
                            color = " <RGB:1,1,1> ";
                            haveTool = false;
                            local tools2 = scrapDef.tools2;
                            if tools2 then
                               local missingTools2 = "";
                               missingTools = missingTools .. " <LINE> ";
                               if #tools2 <=0 and missingTools == "" then return true; end
                               for _,v in ipairs(tools2) do
                                   local item = InventoryItemFactory.CreateItem(v);
                                   if v and item then
                                       if _data.player:getInventory():FindAndReturn(v) then
                                           haveTool = true;
                                       end
                                       if missingTools2 == "" then
                                           missingTools2 = item:getDisplayName();
                                       else
                                           missingTools2 = missingTools2 .. "/" .. item:getDisplayName();
                                       end
                                   end
                               end
                               if not haveTool then color = " <RGB:1,0,0> " end
                               missingTools = missingTools .. color .. " " .. missingTools2;
                            end
                        end
                        option.notAvailable = true;
                        local toolTip = ISToolTip:new();
                        toolTip:initialise();
                        toolTip:setVisible(false);
                        toolTip.description = missingTools;
                        option.toolTip = toolTip;
                    else
                        local color = "<RED>";
                        if v.chance > 15 and v.chance <= 40 then
                            color = "<ORANGE>";
                        elseif v.chance > 40 then
                            color = "<GREEN>";
                        end
                        local chanceDefinition = color .. " " .. getText("Tooltip_chanceSuccess") .. " " .. v.chance .. "%";
                        local toolTip = ISToolTip:new();
                        toolTip:initialise();
                        toolTip:setVisible(false);
                        toolTip.description = chanceDefinition;
                        option.toolTip = toolTip;
                    end
                end
            end
            if not addedSomething then _data.context:removeLastOption(); end

        end
    end

    function self.disassemble( _data, _v )
        if _v and _v.moveProps and _v.square and _v.object then
--            print("destroying item ".._v.moveProps.name, _v.square:getObjects():contains(_v.object));
            if _v.moveProps:canScrapObject( _data.player ) and _v.square:getObjects():contains(_v.object) then
                if _v.moveProps:scrapWalkToAndEquip( _data.player ) or ISMoveableDefinitions.cheat then
                    ISTimedActionQueue.add(ISMoveablesAction:new(_data.player, _v.square, _v.moveProps, "scrap" ));
                end
            end
        end
    end

    return self;
end
