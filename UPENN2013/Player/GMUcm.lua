---------------------
-- Open connection to port 2247 (mailserver)
-- sudo apt-get install lua51-socket-dev
-- sudo apt-get install lua51-socket2
require "socket"

----------------------------------------------
-- Pickle.lua
-- A table serialization utility for lua
-- Steve Dekorte, http://www.dekorte.com, Apr 2000
-- Freeware
----------------------------------------------
-- I found this at: http://lua-users.org/wiki/PickleTable
function pickle(t)
  return Pickle:clone():pickle_(t)
end

Pickle = {
  clone = function (t) local nt={}; for i, v in pairs(t) do nt[i]=v end return nt end 
}

function Pickle:pickle_(root)
  if type(root) ~= "table" then 
    error("can only pickle tables, not ".. type(root).."s")
  end
  self._tableToRef = {}
  self._refToTable = {}
  local savecount = 0
  self:ref_(root)
  local s = ""

  while table.getn(self._refToTable) > savecount do
    savecount = savecount + 1
    local t = self._refToTable[savecount]
    s = s.."{\n"
    for i, v in pairs(t) do
        s = string.format("%s[%s]=%s,\n", s, self:value_(i), self:value_(v))
    end
    s = s.."},\n"
  end

  return string.format("{%s}", s)
end

function Pickle:value_(v)
  local vtype = type(v)
  if     vtype == "string" then return string.format("%q", v)
  elseif vtype == "number" then return v
  elseif vtype == "boolean" then return tostring(v)
  elseif vtype == "table" then return "{"..self:ref_(v).."}"
  else --error("pickle a "..type(v).." is not supported")
  end  
end

function Pickle:ref_(t)
  local ref = self._tableToRef[t]
  if not ref then 
    if t == self then error("can't pickle the pickle class") end
    table.insert(self._refToTable, t)
    ref = table.getn(self._refToTable)
    self._tableToRef[t] = ref
  end
  return ref
end

----------------------------------------------
-- unpickle
----------------------------------------------

function unpickle(s)
  if type(s) ~= "string" then
    error("can't unpickle a "..type(s)..", only strings")
  end
	setDebugTrue();
	--print("s is " .. s);
  local gentables = loadstring("return "..s)
  --print("wat");
	local tables = gentables()
  
  for tnum = 1, table.getn(tables) do
    local t = tables[tnum]
    local tcopy = {}; for i, v in pairs(t) do tcopy[i] = v end
    for i, v in pairs(tcopy) do
      local ni, nv
      if type(i) == "table" then ni = tables[i[1]] else ni = i end
      if type(v) == "table" then nv = tables[v[1]] else nv = v end
      t[i] = nil
      t[ni] = nv
    end
  end
  return tables[1]
end

function set_data(label,table)
	setDebugTrue();
  --print("label is " .. label);
	--print("table is " .. table);
	if(label == "" or label == nil) then
		error("set data label is " .. label .. " which is bad");
	end
	mailbox = label; --code = someHash(label);-- this will be the mailbox
	table_str = pickle(table) .. '`';
	packet = "3," .. mailbox  .. "," .. tostring(string.len(table_str)) .. "," .. table_str;
	tcp:send(packet);
end
function get_data(label)
	setDebugTrue();
	mailbox = label;--code = someHash(label);
	--print("mailbox value is " .. mailbox);
	packet = "2," .. mailbox .. ","
	--tcp:settimeout(.01);

	tcp:send(packet);
	builder_str = "";
	new_str_data,t,p = tcp:receive(1)
	while(new_str_data~= nil and new_str_data ~= "`") do  
		--if(new_str_data!=nil) then
			builder_str = builder_str .. new_str_data;
		--end
		new_str_data,t,p = tcp:receive(1)
	 -- print("new_str is " .. new_str_data);
	end
	--print("builder str is \"" .. builder_str .. " end");
	--print("builder str len is " .. string.len(builder_str) .. " omg");
	if(string.len(builder_str)==1) then
		return 0;
	end
	return unpickle(builder_str)	
end
---------------------
tcp = socket.tcp()
tcp:connect("127.0.0.1", 2247)

