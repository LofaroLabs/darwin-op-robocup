require("string")

startHFA = function(hfa)
	hfa.done = false;
	hfa.failed = false;
	hfa.counter = 0;
	-- maybe this is too costly and we should restrict it to the zeroTimer function?
	hfa.timer = os.time()
	hfa.behaviorTransition = nil;
	if (not(hfa.interruptable)) then
		hfa.current = nil;
	end
end

stopHFA = function(hfa)
	if (not(hfa.currentBehavior == nil) and
		not(hfa.currentBehavior.stop == nil) and
		not(hfa.interruptable)) then
		hfa.currentBehavior.stop(hfa.currentBehavior)
	end
end

goHFA = function(hfa)
	-- TRANSITION
	local newBehavior = nil
	if (not(hfa.transition == nil)) then
		if (type(hfa.transition)=="function") then
			newBehavior = hfa.transition(hfa)
		else
			newBehavior = hfa.transition
		end
	elseif (not(hfa.behaviorTransition==nil)) then
		newBehavior = hfa.behaviorTransition
		hfa.behaviorTransition = nil
	end
	
	if (not(newBehavior == nil)) then
		if (not(newBehavior == hfa.currentBehavior)) then
			if ((not(hfa.currentBehavior == nil)) and
				(not(hfa.currentBehavior.stop == nil))) then
					hfa.currentBehavior.stop(hfa.currentBehavior)
			end
			if (not(newBehavior == nil)) then
				 if (not(newBehavior.start == nil)) then
				 	newBehavior.parent = hfa
					newBehavior.start(newBehavior)
				end
			else
				print("WARNING (goHFA): NULL new behavior")
			end
			hfa.currentBehavior = newBehavior
			hfa.done = false
			hfa.failed = false
		end
	end
			
	-- EXECUTION
	if (hfa.currentBehavior == null) then
		print("WARNING (goHFA): null behavior")
	elseif (not(hfa.currentBehavior.go == null)) then
		hfa.behaviorTransition = hfa.currentBehavior.go(hfa.currentBehavior)
	end
end

start = "start"

makeTransition = function(transitions)
	return function(hfa)
		if (hfa.currentBehavior == nil) then
			return transitions[start].transition(hfa)
		else
			return transitions[currentBehavior].transition(hfa)
		end
	end
end

makeBehavior = function(name, start, stop, go)
	return { ["name"] = name, ["start"] = start, ["stop"] = stop, ["go"] = go, ["parent"] = nil }
end

makeHFA = function(name, transition, interruptable)
	return { ["behaviorTransition"] = nil, ["name"] = name, 
			 ["start"] = startHFA, ["stop"] = stopHFA, ["go"] = goHFA, 
		 	 ["transition"] = transition, ["interruptable"] = interruptable, ["parent"] = nil,
			 ["counter"] = 0, ["timer"] = 0, ["done"] = false, ["failed"] = false, ["current"] = nil,
			 ["propagateFlags"] = false }
end

bumpCounter = makeBehavior("bumpCounter", 
	function(behavior) 
		if ((not behavior == nil) and (not behavior.parent == nil)) then 
			behavior.parent.counter = behavior.paranet.counter + 1
		end
	end, nil, nil)

zeroCounter = makeBehavior("zeroCounter",
	function(behavior) 
		if ((not behavior == nil) and (not behavior.parent == nil)) then 
			behavior.parent.counter = 0
		end
	end, nil, nil)
	
currentCounter = function(hfa) return hfa.counter end
currentTimer = function(hfa) return hfa.timer - os.time() end

zeroTimer = makeBehavior("zeroTimer",
	function(behavior)
		if ((not behavior == nil) and (not behavior.parent == nil)) then
			behavior.parent.timer = os.time()
		end
	end, nil, nil)

setFlag = function(hfa, flag)
	if (not hfa.parent == nil) then
		hfa.parent[flag] = true
		if (hfa.parent.propagateFlags == true) then
			setFlag(hfa.parent, flag)
		end
	end
end

done = makeBehavior("done", nil, nil,
	function(hfa) 
		if (not hfa == nil) then
			hfa.current = start
			setFlag(hfa, "done")
		end
	end)

sayDone = makeBehavior("sayDone", 
	function(hfa)
		if (not hfa == nil) then
			setFlag(hfa, "done")
		end
	end, nil, nil)
	
failed = makeBehavior("failed", nil, nil,
	function(hfa) 
		if (not hfa == nil) then
			hfa.current = start
			setFlag(hfa, "failed")
		end
	end)

sayFailed = makeBehavior("sayFailed", 
	function(hfa)
		if (not hfa == nil) then
			setFlag(hfa, "failed")
		end
	end, nil, nil)
	

started = false
pulse = function(behavior)
			if (not started) then
				started = true
				if (not(behavior.start == nil)) then
					behavior.start(behavior)
				end
			end
			if (not(behavior.go == nil)) then
				behavior.go(behavior)
			end
		end
			 
--foo = ... 
--bar = ... 	 
--			 
--sean = makeHFA("sean", makeTransition(
--	{
--	[start] = function(hfa) if sky is black then return foo else return done end 
--	[foo] = function(hfa) if sky is red then return failed else return foo end
--	}, false)
-- pulse(sean)
			 
			 