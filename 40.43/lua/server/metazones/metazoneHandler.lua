
function doMapZones()
    local dirs = getLotDirectories()
    for i=dirs:size(),1,-1 do
        local file = 'media/maps/'..dirs:get(i-1)..'/objects.lua'
        if fileExists(file) then
			getWorld():removeZonesForLotDirectory(dirs:get(i-1))
            reloadLuaFile(file)
            for k,v in ipairs(objects) do
				vzone = getWorld():registerVehiclesZone(v.name, v.type, v.x, v.y, v.z, v.width, v.height, v.properties)
				if vzone == nil then
					getWorld():registerZoneNoOverlap(v.name, v.type, v.x, v.y, v.z, v.width, v.height)
				end
				table.wipe(v)
            end
            table.wipe(objects)
        else
            print('can\'t find map objects file: '..file)
        end
		getWorld():checkVehiclesZones();
    end
end

Events.OnLoadMapZones.Add(doMapZones);
