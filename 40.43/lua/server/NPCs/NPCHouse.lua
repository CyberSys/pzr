NPCHouse = {}

NPCHouse.TriggerTryGetSquares =  function(type, data, context)


	for i = 0, 10 do
		local squares = BuildingHelper.getFreeTilesFromRandomRoomInBuilding(context, 2);

		local square = squares[1];
		local square2 = squares[2];

		if(square ~= nil and square2 ~= nil) then
			return square, square2;
		end

	end

	return nil;
end

NPCHouse.Trigger =  function(type, data, context)

	local square, square2 = NPCHouse.TriggerTryGetSquares(type, data, context);

	if(square ~= nil and square2 ~= nil) then
		-- create npc
		local x = square:getX();
		local y = square:getY();
		local z = square:getZ();
		local x2 = square2:getX();
		local y2 = square2:getY();
		local z2 = square2:getZ();
		--print("creating survivor");
		local survivordescX = SurvivorFactory.CreateSurvivor();
		local survivordescY = SurvivorFactory.CreateSurvivor();
		--print("creating survivor at "..x..", "..y..", "..z);
		local X = IsoSurvivor.new(survivordescX, getCell(), x, y, z);
		local Y = IsoSurvivor.new(survivordescY, getCell(), x2, y2, z2);
		Y:getInventory():AddItem("Base.Shotgun");
		Y:getInventory():AddItem("Base.ShotgunShells");
		X:getInventory():AddItem("Base.Shotgun");
		X:getInventory():AddItem("Base.ShotgunShells");
		X:setAllowBehaviours(false);
		Y:setAllowBehaviours(false);
		X:faceDirection(Y);
		Y:faceDirection(X);
		-- group em together.
		survivordescX:AddToGroup(survivordescY:getGroup());
		data.X = X;
		data.Y = Y;
		data.survivordescX = survivordescX;
		data.survivordescY = survivordescY;
		-- add the survivor desc to the building, so he'll be here next time we spool in.
		context:addCharacter(data.survivordescX);
		context:addCharacter(data.survivordescY);
	else
		--print("could not find squares." );
	end

end

NPCHouse.FutureTrigger = function(type, data, context)
	if data.X ~= nil and data.Y ~= nil and data.done ~= true then
		if data.X:getBuilding() == getPlayer():getBuilding() and (data.X:CanSee(getPlayer()) or data.Y:CanSee(getPlayer())) then
			getScriptManager():PlayInstanceScript("npchouse"..data.X:getDescriptor():getID(), "Base.NPCHouseYHostileXHero", {P = getPlayer(), X = data.X, Y = data.Y } );
			data.done = true;
		end
	end
end