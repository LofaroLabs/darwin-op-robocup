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
  local gentables = loadstring("return "..s)
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


---------------------
tcp = socket.tcp()
tcp:connect("127.0.0.1", 2247)

---------------------
-- Create test table
---------------------
data = {}
data.x = 1
data.y = 2
data.velx = 3
data.vely = 4

---------------------
-- Serialize the table named data and send it to the mailserver
---------------------
str_data = pickle(data)
print("Current data:");
for i,v in pairs(data) do print(i,v) end
print("Enter a code to put this into.")
code_num = io.read()
code = tostring(code_num)
-- ACTION,CODE,LEN,DATA
-- Action 0 == push
-- Action 1 == pop
-- Action 2 == peek
-- Action 3 == put (replace)
-- Packet Format: [ACTION,CODE,DATA_LENGTH,DATA]
-- Packet Format: [ACTION,CODE,]
packet = "3," .. code .. "," .. tostring(string.len(str_data)) .. "," .. str_data
print("Packet Generated: "..packet)
print("Sending packet...")
tcp:send(packet)
print("Sleeping 2 seconds")
os.execute("sleep " .. tonumber(2))

tcp:settimeout(2)
packet = "2," .. code .. ","
print("Packet generated: "..packet)
print("Sending: ")
tcp:send(packet)
print("Waiting to receive")
new_str_data,t,p = tcp:receive(49)
print("Got: " .. new_str_data)
new_data = unpickle(new_str_data)
print("Unpickled...")
for i,v in pairs(new_data) do print(i,v) end
tcp:close()
