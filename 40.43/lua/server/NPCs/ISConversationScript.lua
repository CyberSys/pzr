require "ISBaseObject"

ISConversationScript = ISBaseObject:derive("ISConversationScript");

ISConversationScript.IDMax = 1;


function ISConversationScript:addPart(name, character)
	if not instanceof(character, "IsoGameCharacter") then
		character = character:getInstance();
	end
	if character == nil then
		--print("Character not instanced.");
		return;
	end
	self.parts[name] = character;
end



function ISConversationScript:setDialogSubstitute(key, val)

    self.subs[key] = val;

end

function ISConversationScript:isPlaying()
if self.inst == nil then
return false;
end

return not self.inst:finished();
end

function ISConversationScript:execute()
	self.inst = getScriptManager():PlayInstanceScript(self.script.."_"..self.id, self.script, self.parts, self.subs );

	--print("playing "..self.script);
end

--************************************************************************--
--** ISConversationScript:new
--**
--************************************************************************--
function ISConversationScript:new (script)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.script = script;
	o.parts = {}
	o.subs = {}
	o.lists = {}
	o.id = ISConversationScript.IDMax;
	return o
end

