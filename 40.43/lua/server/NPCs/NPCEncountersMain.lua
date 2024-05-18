NPCEncountersMain = {}

NPCEncountersMain.Trigger = function(type, data, context)

    print ("Triggered event!");
    if instanceof(context, "BuildingDef") then
        -- pick a room for npcs...

        --NPCFetchQuest.Trigger(type, data, context);
        NPCHouse.Trigger(type, data, context);

    end
end

NPCEncountersMain.FutureTrigger = function(type, data, context)

    if instanceof(context, "BuildingDef") then

	    --NPCFetchQuest.FutureTrigger(type, data, context);
	    NPCHouse.FutureTrigger(type, data, context);
    end

end

Events.OnTriggerNPCEvent.Add(NPCEncountersMain.Trigger);
Events.OnMultiTriggerNPCEvent.Add(NPCEncountersMain.FutureTrigger);