---------------------
-- Open connection to port 2247 (mailserver)
-- sudo apt-get install lua51-socket-dev
-- sudo apt-get install lua51-socket2
require "socket"
require "Body"

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
	--keep track of number of times called
	if(wcm.get_horde_numTimesCalled ~=nil) then
		local timesCalled = wcm.get_horde_numTimesCalled();
		timesCalled = timesCalled +1;
		wcm.set_horde_numTimesCalled(timesCalled);
  end
	--get the base time
	local time_start = Body.get_time();
	setDebugTrue();
  --print("start of set");
	--print("label is " .. label);
	--print("table is " .. table);
	if(label == "" or label == nil) then
		error("set data label is " .. label .. " which is bad");
	end
	mailbox = label; --code = someHash(label);-- this will be the mailbox
	--make a 4 character string
	--local table_len_string = "";
	-- this will represent a number. if it's <1000 then you need to pad zeros to the begining
	--old one looks like this
  --table_str = pickle(table) .. '`';
	local table_str = pickle(table);
	local table_length = string.len(table_str);
	--new one will look like this
	--table_str = fourCharstr .. pickledDataVariable;
	if table_length == 0 then
		table_str = '0000' .. table_length .. table_str;
	elseif table_length > 0 and table_length < 10 then
		--single digit
		table_str = '000' .. table_length .. table_str;
	elseif table_length > 9 and table_length < 100 then
		--double digit
		table_str = '00' .. table_length .. table_str;
	elseif table_length > 99 and table_length < 1000 then
		--triple digit
		table_str = '0' .. table_length .. table_str;
	elseif table_length > 999 and table_length < 10000 then
		--quad digit
		table_str = table_length .. table_str;
	end	
	
	--print("Length of table_str: " .. table_length);
	packet = "3," .. mailbox  .. "," .. tostring(string.len(table_str)) .. "," .. table_str;
	tcp:send(packet);
--print total time
	local time_end = Body.get_time();
	local delta_time = time_end - time_start;
	--print("set_data time in sec: " .. delta_time);
end

function get_data(label)
	--keep track of number of times called
	local timesCalled = wcm.get_horde_numTimesCalled();
	timesCalled = timesCalled +1;
	wcm.set_horde_numTimesCalled(timesCalled);
	-- get base time
	local time_start = Body.get_time();
	local recieve_time_start = 0;
	local send_time_start = 0;
	local delta_time = 0;

	--setDebugTrue();
	print("start of Get");
	mailbox = label;
	packet = "2," .. mailbox .. ","
  delta_time = Body.get_time() - time_start;
  print("BEGINNING OF GET TIME IN SEC: " .. delta_time);


	send_time_start = Body.get_time();
	tcp:send(packet);
	local delta_time1 = Body.get_time() - send_time_start;
	print("SEND TIME in SEC: " .. delta_time1);

	local first_four_char,t,p = tcp:receive(4);
	local table_length = tonumber(first_four_char);
  
	recieve_time_start = Body.get_time();
	new_str_data,t,p = tcp:receive(table_length);
	local delta_time2 = Body.get_time() - recieve_time_start;
  print("RECIEVE TIME IN SEC: " .. delta_time2);

	-- print out total time taken
	delta_time = Body.get_time() - time_start;
  local time_minus_send_recieve = delta_time - delta_time1 - delta_time2;

	print("TIME WITHOUT SEND AND RECIEVE: " .. time_minus_send_recieve);

	print("GET_DATA TIME IN SEC: " .. delta_time);

	if(new_str_data)==1 then
		return 0;
	end
	return unpickle(new_str_data)	
end
---------------------
tcp = socket.tcp()
tcp:connect("127.0.0.1", 2247)

