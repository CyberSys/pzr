--***********************************************************
--**                    ROBERT JOHNSON                     **
--**          Contextual menu with all our painting        **
--***********************************************************

ISPaintMenu = {};


ISPaintMenu.doPaintMenu = function(player, context, worldobjects, test)

	if test and ISWorldObjectContextMenu.Test then return true end

	local playerObj = getSpecificPlayer(player)

	local playerInv = playerObj:getInventory()

	local thump = nil;
	local square = nil;
    local paintableWall = nil;
    local paintableItem = nil;

	-- we get the thumpable item (like wall/door/furniture etc.) if exist on the tile we right clicked
	for i,v in ipairs(worldobjects) do
		square = v:getSquare();
        if v:getSprite() and v:getSprite():getProperties():Is("IsPaintable") then
            paintableItem = v;
        end
		if instanceof(v, "IsoThumpable") then
			thump = v;
        end
        if v:getProperties():Is("WallN") or v:getProperties():Is("WallW") then
            paintableWall = v;
        end
    end

    local joypad = JoypadState.players[player+1] or false

    -- if the item can be plastered
    if (joypad or (thump and thump:canBePlastered())) and ((playerObj:getPerkLevel(Perks.Woodwork) >= 4 and playerInv:contains("BucketPlasterFull")) or ISBuildMenu.cheat) then
		if test then return ISWorldObjectContextMenu.setTest() end
		context:addOption(getText("ContextMenu_Plaster"), worldobjects, ISPaintMenu.onPlaster, player, thump, square);
	end

    -- paint various sign
    if (paintableWall or joypad) and (ISBuildMenu.cheat or playerInv:contains("Paintbrush")) then
		if test then return ISWorldObjectContextMenu.setTest() end
        local paintOption = context:addOption(getText("ContextMenu_PaintSign"), worldobjects, nil);
        local subMenuPaint = ISContextMenu:getNew(context);
        -- we add our new menu to the option we want (here paint)
        context:addSubMenu(paintOption, subMenuPaint);
        ISPaintMenu.player = player
        if (ISBuildMenu.cheat or playerInv:contains("PaintBlue")) then
            ISPaintMenu.addSignOption(subMenuPaint, getText("ContextMenu_Blue"), paintableWall, "PaintBlue", 0.48,0.62,0.82);
        end
        if (ISBuildMenu.cheat or playerInv:contains("PaintGreen")) then
            ISPaintMenu.addSignOption(subMenuPaint, getText("ContextMenu_Green"), paintableWall, "PaintGreen", 0.43,0.60,0.34);
        end
        if (ISBuildMenu.cheat or playerInv:contains("PaintLightBrown")) then
            ISPaintMenu.addSignOption(subMenuPaint, getText("ContextMenu_Light_Brown"), paintableWall, "PaintLightBrown", 0.73,0.53,0.42);
        end
        if (ISBuildMenu.cheat or playerInv:contains("PaintLightBlue")) then
            ISPaintMenu.addSignOption(subMenuPaint, getText("ContextMenu_Light_Blue"), paintableWall, "PaintLightBlue", 0.70,0.79,0.87);
        end
        if (ISBuildMenu.cheat or playerInv:contains("PaintBrown")) then
            ISPaintMenu.addSignOption(subMenuPaint, getText("ContextMenu_Brown"), paintableWall, "PaintBrown", 0.51,0.32,0.26);
        end
        if (ISBuildMenu.cheat or playerInv:contains("PaintOrange")) then
            ISPaintMenu.addSignOption(subMenuPaint, getText("ContextMenu_Orange"), paintableWall, "PaintOrange", 0.78,0.50,0.30);
        end
        if (ISBuildMenu.cheat or playerInv:contains("PaintCyan")) then
            ISPaintMenu.addSignOption(subMenuPaint, getText("ContextMenu_Cyan"), paintableWall, "PaintCyan", 0.62,0.86,0.84);
        end
        if (ISBuildMenu.cheat or playerInv:contains("PaintPink")) then
            ISPaintMenu.addSignOption(subMenuPaint, getText("ContextMenu_Pink"), paintableWall, "PaintPink", 0.81,0.49,0.58);
        end
        if (ISBuildMenu.cheat or playerInv:contains("PaintGrey")) then
            ISPaintMenu.addSignOption(subMenuPaint, getText("ContextMenu_Grey"), paintableWall, "PaintGrey", 0.51,0.54,0.54);
        end
        if (ISBuildMenu.cheat or playerInv:contains("PaintTurquoise")) then
            ISPaintMenu.addSignOption(subMenuPaint, getText("ContextMenu_Turquoise"), paintableWall, "PaintTurquoise", 0.36,0.61,0.61);
        end
        if (ISBuildMenu.cheat or playerInv:contains("PaintPurple")) then
            ISPaintMenu.addSignOption(subMenuPaint, getText("ContextMenu_Purple"), paintableWall, "PaintPurple", 0.75,0.36,0.71);
        end
        if (ISBuildMenu.cheat or playerInv:contains("PaintYellow")) then
            ISPaintMenu.addSignOption(subMenuPaint, getText("ContextMenu_Yellow"), paintableWall, "PaintYellow", 0.90,0.78,0.42);
        end
        if (ISBuildMenu.cheat or playerInv:contains("PaintWhite")) then
            ISPaintMenu.addSignOption(subMenuPaint, getText("ContextMenu_White"), paintableWall, "PaintWhite", 0.92,0.92,0.92);
        end
        if (ISBuildMenu.cheat or playerInv:contains("PaintRed")) then
            ISPaintMenu.addSignOption(subMenuPaint, getText("ContextMenu_Red"), paintableWall, "PaintRed", 0.92,0.1,0.1);
        end
        if (ISBuildMenu.cheat or playerInv:contains("PaintBlack")) then
            ISPaintMenu.addSignOption(subMenuPaint, getText("ContextMenu_Black"), paintableWall, "PaintBlack", 0.1,0.1,0.1);
        end
    end

	-- if the item can be paint
	if joypad and (ISBuildMenu.cheat or playerInv:contains("Paintbrush")) then
		local paintOption = context:addOption(getText("ContextMenu_Paint"), worldobjects, nil)
		local subMenuPaint = ISContextMenu:getNew(context)
		context:addSubMenu(paintOption, subMenuPaint)
		if ISBuildMenu.cheat or playerInv:contains("PaintBlue") then
			subMenuPaint:addOption(getText("ContextMenu_Blue"), worldobjects, ISPaintMenu.onPaint, player, thump, "PaintBlue")
		end
		if ISBuildMenu.cheat or playerInv:contains("PaintBrown") then
			subMenuPaint:addOption(getText("ContextMenu_Brown"), worldobjects, ISPaintMenu.onPaint, player, thump, "PaintBrown")
		end
		if ISBuildMenu.cheat or playerInv:contains("PaintCyan") then
			subMenuPaint:addOption(getText("ContextMenu_Cyan"), worldobjects, ISPaintMenu.onPaint, player, thump, "PaintCyan")
		end
		if ISBuildMenu.cheat or playerInv:contains("PaintGreen") then
			subMenuPaint:addOption(getText("ContextMenu_Green"), worldobjects, ISPaintMenu.onPaint, player, thump, "PaintGreen")
		end
		if ISBuildMenu.cheat or playerInv:contains("PaintGrey") then
			subMenuPaint:addOption(getText("ContextMenu_Grey"), worldobjects, ISPaintMenu.onPaint, player, thump, "PaintGrey")
		end
		if ISBuildMenu.cheat or playerInv:contains("PaintLightBlue") then
			subMenuPaint:addOption(getText("ContextMenu_Light_Blue"), worldobjects, ISPaintMenu.onPaint, player, thump, "PaintLightBlue")
		end
		if ISBuildMenu.cheat or playerInv:contains("PaintLightBrown") then
			subMenuPaint:addOption(getText("ContextMenu_Light_Brown"), worldobjects, ISPaintMenu.onPaint, player, thump, "PaintLightBrown")
		end
		if ISBuildMenu.cheat or playerInv:contains("PaintOrange") then
			subMenuPaint:addOption(getText("ContextMenu_Orange"), worldobjects, ISPaintMenu.onPaint, player, thump, "PaintOrange")
		end
		if ISBuildMenu.cheat or playerInv:contains("PaintPink") then
			subMenuPaint:addOption(getText("ContextMenu_Pink"), worldobjects, ISPaintMenu.onPaint, player, thump, "PaintPink")
		end
		if ISBuildMenu.cheat or playerInv:contains("PaintPurple") then
			subMenuPaint:addOption(getText("ContextMenu_Purple"), worldobjects, ISPaintMenu.onPaint, player, thump, "PaintPurple")
		end
		if ISBuildMenu.cheat or playerInv:contains("PaintTurquoise") then
			subMenuPaint:addOption(getText("ContextMenu_Turquoise"), worldobjects, ISPaintMenu.onPaint, player, thump, "PaintTurquoise")
		end
		if ISBuildMenu.cheat or playerInv:contains("PaintWhite") then
			subMenuPaint:addOption(getText("ContextMenu_White"), worldobjects, ISPaintMenu.onPaint, player, thump, "PaintWhite")
		end
		if ISBuildMenu.cheat or playerInv:contains("PaintYellow") then
			subMenuPaint:addOption(getText("ContextMenu_Yellow"), worldobjects, ISPaintMenu.onPaint, player, thump, "PaintYellow")
        end
        if ISBuildMenu.cheat or playerInv:contains("PaintRed") then
            subMenuPaint:addOption(getText("ContextMenu_Red"), worldobjects, ISPaintMenu.onPaint, player, thump, "PaintRed")
        end
        if ISBuildMenu.cheat or playerInv:contains("PaintBlack") then
            subMenuPaint:addOption(getText("ContextMenu_Black"), worldobjects, ISPaintMenu.onPaint, player, thump, "PaintBlack")
        end
	elseif ((thump and thump:isPaintable()) or paintableItem) and (ISBuildMenu.cheat or playerInv:contains("Paintbrush")) then
        local item = thump;
        if paintableItem then item = paintableItem; end
		if test then return ISWorldObjectContextMenu.setTest() end
		local modData = nil;
        if thump then thump:getModData(); end
		local paintOption = context:addOption(getText("ContextMenu_Paint"), worldobjects, nil);
		local subMenuPaint = ISContextMenu:getNew(context);
		-- we add our new menu to the option we want (here paint)
		context:addSubMenu(paintOption, subMenuPaint);
        local addedMenu = false;
        local wallType = "";
        if paintableItem then
            wallType = paintableItem:getSprite():getProperties():Val("PaintingType");
        end
		-- blue
		if ((modData and Painting[modData["wallType"]]["PaintBlue"]) or (Painting[wallType] and Painting[wallType]["PaintBlue"])) and (ISBuildMenu.cheat or playerInv:contains("PaintBlue")) then
			subMenuPaint:addOption(getText("ContextMenu_Blue"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintBlue");
            addedMenu = true;
		end
		if ((modData and Painting[modData["wallType"]]["PaintBrown"]) or (Painting[wallType] and Painting[wallType]["PaintBrown"])) and (ISBuildMenu.cheat or playerInv:contains("PaintBrown")) then
			subMenuPaint:addOption(getText("ContextMenu_Brown"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintBrown");
            addedMenu = true;
		end
		if ((modData and Painting[modData["wallType"]]["PaintCyan"]) or (Painting[wallType] and Painting[wallType]["PaintCyan"])) and (ISBuildMenu.cheat or playerInv:contains("PaintCyan")) then
			subMenuPaint:addOption(getText("ContextMenu_Cyan"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintCyan");
            addedMenu = true;
		end
		if ((modData and Painting[modData["wallType"]]["PaintGreen"]) or (Painting[wallType] and Painting[wallType]["PaintGreen"])) and (ISBuildMenu.cheat or playerInv:contains("PaintGreen")) then
			subMenuPaint:addOption(getText("ContextMenu_Green"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintGreen");
            addedMenu = true;
		end
		if ((modData and Painting[modData["wallType"]]["PaintGrey"]) or (Painting[wallType] and Painting[wallType]["PaintGrey"])) and (ISBuildMenu.cheat or playerInv:contains("PaintGrey")) then
			subMenuPaint:addOption(getText("ContextMenu_Grey"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintGrey");
            addedMenu = true;
		end
		if ((modData and Painting[modData["wallType"]]["PaintLightBlue"]) or (Painting[wallType] and Painting[wallType]["PaintLightBlue"])) and (ISBuildMenu.cheat or playerInv:contains("PaintLightBlue")) then
			subMenuPaint:addOption(getText("ContextMenu_Light_Blue"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintLightBlue");
            addedMenu = true;
		end
		if ((modData and Painting[modData["wallType"]]["PaintLightBrown"]) or (Painting[wallType] and Painting[wallType]["PaintLightBrown"])) and (ISBuildMenu.cheat or playerInv:contains("PaintLightBrown")) then
			subMenuPaint:addOption(getText("ContextMenu_Light_Brown"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintLightBrown");
            addedMenu = true;
		end
		if ((modData and Painting[modData["wallType"]]["PaintOrange"]) or (Painting[wallType] and Painting[wallType]["PaintOrange"])) and (ISBuildMenu.cheat or playerInv:contains("PaintOrange")) then
			subMenuPaint:addOption(getText("ContextMenu_Orange"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintOrange");
            addedMenu = true;
		end
		if ((modData and Painting[modData["wallType"]]["PaintPink"]) or (Painting[wallType] and Painting[wallType]["PaintPink"])) and (ISBuildMenu.cheat or playerInv:contains("PaintPink")) then
			subMenuPaint:addOption(getText("ContextMenu_Pink"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintPink");
            addedMenu = true;
		end
		if ((modData and Painting[modData["wallType"]]["PaintPurple"]) or (Painting[wallType] and Painting[wallType]["PaintPurple"])) and (ISBuildMenu.cheat or playerInv:contains("PaintPurple")) then
			subMenuPaint:addOption(getText("ContextMenu_Purple"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintPurple");
            addedMenu = true;
		end
		if ((modData and Painting[modData["wallType"]]["PaintTurquoise"]) or (Painting[wallType] and Painting[wallType]["PaintTurquoise"])) and (ISBuildMenu.cheat or playerInv:contains("PaintTurquoise")) then
			subMenuPaint:addOption(getText("ContextMenu_Turquoise"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintTurquoise");
            addedMenu = true;
		end
		if ((modData and Painting[modData["wallType"]]["PaintWhite"]) or (Painting[wallType] and Painting[wallType]["PaintWhite"])) and (ISBuildMenu.cheat or playerInv:contains("PaintWhite")) then
			subMenuPaint:addOption(getText("ContextMenu_White"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintWhite");
            addedMenu = true;
		end
		if ((modData and Painting[modData["wallType"]]["PaintYellow"]) or (Painting[wallType] and Painting[wallType]["PaintYellow"])) and (ISBuildMenu.cheat or playerInv:contains("PaintYellow")) then
			subMenuPaint:addOption(getText("ContextMenu_Yellow"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintYellow");
            addedMenu = true;
        end
        if ((modData and Painting[modData["wallType"]]["PaintRed"]) or (Painting[wallType] and Painting[wallType]["PaintRed"])) and (ISBuildMenu.cheat or playerInv:contains("PaintRed")) then
            subMenuPaint:addOption(getText("ContextMenu_Red"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintRed");
            addedMenu = true;
        end
        if ((modData and Painting[modData["wallType"]]["PaintBlack"]) or (Painting[wallType] and Painting[wallType]["PaintBlack"])) and (ISBuildMenu.cheat or playerInv:contains("PaintBlack")) then
            subMenuPaint:addOption(getText("ContextMenu_Black"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintBlack");
            addedMenu = true;
        end
        if OtherPainting[wallType] and OtherPainting[wallType]["PaintBlue"] and (ISBuildMenu.cheat or playerInv:contains("PaintBlue")) then
            subMenuPaint:addOption(getText("ContextMenu_Blue"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintBlue");
            addedMenu = true;
        end
        if OtherPainting[wallType] and OtherPainting[wallType]["PaintBrown"] and (ISBuildMenu.cheat or playerInv:contains("PaintBrown")) then
            subMenuPaint:addOption(getText("ContextMenu_Brown"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintBrown");
            addedMenu = true;
        end
        if OtherPainting[wallType] and OtherPainting[wallType]["PaintCyan"] and (ISBuildMenu.cheat or playerInv:contains("PaintCyan")) then
            subMenuPaint:addOption(getText("ContextMenu_Cyan"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintCyan");
            addedMenu = true;
        end
        if OtherPainting[wallType] and OtherPainting[wallType]["PaintGreen"] and (ISBuildMenu.cheat or playerInv:contains("PaintGreen")) then
            subMenuPaint:addOption(getText("ContextMenu_Green"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintGreen");
            addedMenu = true;
        end
        if OtherPainting[wallType] and OtherPainting[wallType]["PaintGrey"] and (ISBuildMenu.cheat or playerInv:contains("PaintGrey")) then
            subMenuPaint:addOption(getText("ContextMenu_Grey"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintGrey");
            addedMenu = true;
        end
        if OtherPainting[wallType] and OtherPainting[wallType]["PaintLightBlue"] and (ISBuildMenu.cheat or playerInv:contains("PaintLightBlue")) then
            subMenuPaint:addOption(getText("ContextMenu_Light_Blue"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintLightBlue");
            addedMenu = true;
        end
        if OtherPainting[wallType] and OtherPainting[wallType]["PaintLightBrown"] and (ISBuildMenu.cheat or playerInv:contains("PaintLightBrown")) then
            subMenuPaint:addOption(getText("ContextMenu_Light_Brown"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintLightBrown");
            addedMenu = true;
        end
        if OtherPainting[wallType] and OtherPainting[wallType]["PaintOrange"] and (ISBuildMenu.cheat or playerInv:contains("PaintOrange")) then
            subMenuPaint:addOption(getText("ContextMenu_Orange"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintOrange");
            addedMenu = true;
        end
        if OtherPainting[wallType] and OtherPainting[wallType]["PaintPink"] and (ISBuildMenu.cheat or playerInv:contains("PaintPink")) then
            subMenuPaint:addOption(getText("ContextMenu_Pink"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintPink");
            addedMenu = true;
        end
        if OtherPainting[wallType] and OtherPainting[wallType]["PaintPurple"] and (ISBuildMenu.cheat or playerInv:contains("PaintPurple")) then
            subMenuPaint:addOption(getText("ContextMenu_Purple"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintPurple");
            addedMenu = true;
        end
        if OtherPainting[wallType] and OtherPainting[wallType]["PaintTurquoise"] and (ISBuildMenu.cheat or playerInv:contains("PaintTurquoise")) then
            subMenuPaint:addOption(getText("ContextMenu_Turquoise"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintTurquoise");
            addedMenu = true;
        end
        if OtherPainting[wallType] and OtherPainting[wallType]["PaintYellow"] and (ISBuildMenu.cheat or playerInv:contains("PaintYellow")) then
            subMenuPaint:addOption(getText("ContextMenu_Yellow"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintYellow");
            addedMenu = true;
        end
        if OtherPainting[wallType] and OtherPainting[wallType]["PaintRed"] and (ISBuildMenu.cheat or playerInv:contains("PaintRed")) then
            subMenuPaint:addOption(getText("ContextMenu_Red"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintRed");
            addedMenu = true;
        end
        if OtherPainting[wallType] and OtherPainting[wallType]["PaintBlack"] and (ISBuildMenu.cheat or playerInv:contains("PaintBlack")) then
            subMenuPaint:addOption(getText("ContextMenu_Black"), worldobjects, ISPaintMenu.onPaint, player, item, "PaintBlack");
            addedMenu = true;
        end
        if not addedMenu then
            context:removeLastOption();
        end
	end
end

ISPaintMenu.addSignOption = function(subMenuPaint, name, wall, painting, r,g,b)
    local blueOption = subMenuPaint:addOption(name, nil, nil);
    local subMenuBlue = ISContextMenu:getNew(subMenuPaint);
    subMenuPaint:addSubMenu(blueOption, subMenuBlue);

    subMenuBlue:addOption(getText("ContextMenu_SignSkull"), wall, ISPaintMenu.onPaintSign, ISPaintMenu.player, painting, 36, r,g,b);
    subMenuBlue:addOption(getText("ContextMenu_SignRightArrow"), wall, ISPaintMenu.onPaintSign, ISPaintMenu.player, painting, 32, r,g,b);
    subMenuBlue:addOption(getText("ContextMenu_SignLeftArrow"), wall, ISPaintMenu.onPaintSign, ISPaintMenu.player, painting, 33, r,g,b);
    subMenuBlue:addOption(getText("ContextMenu_SignDownArrow"), wall, ISPaintMenu.onPaintSign, ISPaintMenu.player, painting, 34, r,g,b);
    subMenuBlue:addOption(getText("ContextMenu_SignUpArrow"), wall, ISPaintMenu.onPaintSign, ISPaintMenu.player, painting, 35, r,g,b);
end

ISPaintMenu.onPaintSign = function(wall, player, painting, sign, r,g,b)
    local playerObj = getSpecificPlayer(player)
    if JoypadState.players[player+1] then
        local bo = ISPaintCursor:new(playerObj, "paintSign", { paintType=painting, sign=sign, r=r, g=g, b=b })
        getCell():setDrag(bo, bo.player)
        return
    end
    if luautils.walkAdj(playerObj, wall:getSquare()) then
        ISTimedActionQueue.add(ISPaintSignAction:new(playerObj, wall, playerObj:getInventory():FindAndReturn(painting), sign, r,g,b,100));
    end
end

ISPaintMenu.onPaint = function(worldobjects, player, thumpable, painting)
    local playerObj = getSpecificPlayer(player)
    if JoypadState.players[player+1] then
        local bo = ISPaintCursor:new(playerObj, "paintThump", { paintType=painting })
        getCell():setDrag(bo, bo.player)
        return
    end
    if luautils.walkAdj(playerObj, thumpable:getSquare()) then
	    ISTimedActionQueue.add(ISPaintAction:new(playerObj, thumpable, playerObj:getInventory():FindAndReturn(painting), painting, 100));
    end
end

ISPaintMenu.onPlaster = function(worldobjects, player, thumpable, square)
    local playerObj = getSpecificPlayer(player)
    if JoypadState.players[player+1] then
        local bo = ISPaintCursor:new(playerObj, "plaster")
        getCell():setDrag(bo, bo.player)
        return
    end
 	if luautils.walkAdj(playerObj, square) then
		ISTimedActionQueue.add(ISPlasterAction:new(playerObj, thumpable, playerObj:getInventory():FindAndReturn("BucketPlasterFull"), 100));
 	end
end


Events.OnFillWorldObjectContextMenu.Add(ISPaintMenu.doPaintMenu);
