--[[
HFA.LUA
Sean Luke
Version 1.0
May 2014


A simple package to make it easy to implement hierarchical finite-state
automata or HFA in the form of Moore machines, for purposes of programming
agent behaviors.  This package is not as useful for automata which are intended
for lexing strings.  An HFA is a finite-state automaton whose states correspond
to basic behaviors (a hard-coded behavior you have constructed) or to other 
automata.  While HFA contain other HFA, HFA are not permitted to be recursive: 
that is, you cannot have an HFA which ultimately contains itself.



-- Behaviors --

Let's begin with the notion of a BEHAVIOR.  A behavior is a simple object 
(a dictionary) which contains four things:

1. A NAME (a descriptive string)
2. A backpointer to a PARENT of the behavior (or to nil if there is no parent).
   This item may get changed dynamically to various things as the automaton
   is running.  Before any of the functions below (start/stop/go) are called,
   the PARENT will have been set.
3. A START function.  This function is called when the behavior is started.
   The function takes a single argument, which is the behavior itself.
   This value may be nil.
4. A GO function.  This function is called immediately after START, and
   rapidly / repeatedly thereafter.  The function takes a single argument,
   which is the behavior itself.  This value may be nil.
6. A STOP function.  This function is called when the behavior is stopped.
   The function takes a single argument, which is the behavior itself.
   After being stopped, a behavior can be started again.  This value may be nil.

Imagine you had a behavior called myBehavior.  Then you could access these as:

myBehavior.name
myBehavior.parent
myBehavior.start
myBehavior.stop
myBehavior.go

A BASIC BEHAVIOR is a hard-coded behavior you have created yourself.  You don't
have to implement all three functions (start/stop/go).  For example, if you have
a one-shot behavior (like "kickBall"), you might implement it by simply providing
the start function (to kick the ball) and setting the other functions to nil.
Or if you have a continuous function (like "forward"), you might do this by
providing a go function (to move the robot forward by an epsilon), and set
the others to nil.  Or if you have an asynchronous behavior (like "walk"),
you might implement the start function to tell the robot to begin walking,
set the go function to nil, and set the stop function to tell the robot to stop
walking.  And so on.

A utility function is provided to you to make this easy:

makeBehavior(name, start, stop, go)   		returns a behavior.

In most cases you'd store the behavior in a global variable with the same name.  So
if you were creating a behavior called FOO, you'd do so like this:

foo = makeBehavior("foo", ... )
                
                                      
                                 
-- Automata --

An HFA is a behavior associated with a set of SUB-BEHAVIORS in the form of STATES.
For purposes of this library, a state and a behavior are the same thing.  At any 
time, an active HFA has a CURRENT STATE (that is, a current sub-behavior).  There 
is a special initial state called START (not to be confused with the start
function).  This is the only state which is not a behavior: it is actually just
the string "start".  Thus we have (for some HFA called myHFA):

myHFA.name			(Because an hfa is a behavior)
myHFA.parent		(Likewise)
myHFA.start			(Likewise.  Set to startHFA, see below.)
myHFA.stop			(Likewise.  Set to stopHFA, see below.)
myHFA.go			(Likewise.  Set to goHFA, see below.)
myHFA.current		(The current state)

An HFA is is associated with a TRANSITION FUNCTION which takes the HFA as an 
argument and returns what the current state ought to be at this time.  This is
typically done by examining the present setting of the current state and some 
world information to determine the new current state.  More on transition
functions in a bit, but for the moment, note that the transition functino is
stored here:

myHFA.transition

Because an HFA is a behavior, it has a start, stop and go function stored.  All
HFA use exactly the same start, stop, and go functions, and so when you make an HFA
you don't provide them: the HFA set them up themselves.  These special private
functions are called startHFA(...), stopHFA(...), and goHFA(...).

The startHFA(...) function initializes the current state of the HFA to the START
state, among other things.

The stopHFA(...) function sets the current state of the HFA to nil, among other
things.  If the current state wasn't already nil or the "start" state, then its
STOP function is also called recursively.

The goHFA(...) function is where the interesting work is done.  This function first
calls the transition function to determine the new current state.  If the current
state is different from the previous one, then the START function is called on the
new current state, and furthermore if the previous current state wasn't nil and 
wasn't "start", then the previous current state's STOP function is also called.
Regardless, the current state is changed, and GO function is then called on the
new current state.

Notice that in goHFA(...) the transition function is called FIRST, followed by 
the GO function (and possibly START and STOP).  This means that the "start" 
state never has its GO function called, ever.  This is intentional.  The "start"
state is just meant to be a dummy state which provides us with a way to define
a transition funtion which, in essence, will specify the initial behavior performed
by the HFA.  That's why the "start" state isn't even a behavior: it's just the
string "start".  It's defined in the global variable:

start = "start"

Also notice that the HFA doesn't actually store any of its sub-behaviors.  They're
just specified by the transition function based on the current state of the HFA.
 
An HFA can also be INTERRUPTABLE.  This means that when STOP is called on the behavior,
the current state is NOT set to nil, and the previous current state doees NOT
have its STOP function called.  Additionally, when the START function is called,
the current state is NOT reset to the "start" state, but simply remains the old
current state.  What's the point of this?  It allows us to transition away from
the HFA, then back again, without resetting it: it just continues right where it
left off.  This is typically used for HFA where you'd temporarily transition away 
from the HFA to handle some kind of "interrupt", not becuase you're finished with 
the HFA.  In truth, this is a relatively less common need, so unless you know what
you're doing, don't make your HFA interruptable.

myHFA.interruptable

HFA are created with a different utility function than basic hard-coded behaviors.
You call makeHFA, providing the name, the transition function, and whether or not
you want the HFA to be interruptable (usually false).

makeHFA(name, transition, interruptable)   		returns an HFA.

Again, when creating an HFA, you'd typically store it in a global variable of the
same name as the HFA, thus if you were creating an HFA called FOO, you'd say:

foo = makeHFA("foo", ... )



-- Transition Functions --

Transition functions for automata can be complex: they subsume all the edges in the
finite-state automaton.  It'd be very inconvenient to have to write a transition
function on your own.  Two items are provided to make this easier on you.

First, there is utility function makeTransition(...).  This function takes a single
argument, TRANSITIONS, which is a dictionary.  The KEYS of the dictionary
are behaviors.  The VALUES are either BEHAVIORS or are themselves TRANSITION FUNCTIONS
(which, as normal, take the HFA as a single argument and return a desired new current
state).

makeTransition(transitions)				returns a transition function

The function generated by makeTransition(...) works like this.  When called to
determine the next current state, it queries the HFA for the present current state.
It then looks that state up in the dictionary as the key.  If the resulting value is
a behavior, this behavior is returned as the new current state.  If the resulting
value is a transition function, this function is called and its return value is returned
as the new current state.

Thus every state in the HFA can have its own transition function which says where
to go next if you're presently in that state; or it can explicitly declare another state
to unilaterally go to next.  This second case is equivalent to an unlabeled (or "epsilon")
edge in an FSA.

Thus the most common pattern for making an HFA called FOO would be:

foo = makeHFA("foo", makeTransition(...), false)

For example, you might have something like this.  See if you can make out what's going on:

foo = makeHFA("foo", makeTransition(
	{
	["start"] = forward,
	["forward"] = function(hfa) if (closeToBall()) then return kick else return forward end end,
	["kick"] = rotate,
	["rotate"] = function(hfa) if (ballAhead()) then return forward else return rotate end end,
	}), false)

Second, you can create behaviors which specify their own transitions.  More specifically,
if a behavior is the current state, and that behavior's GO function returns a behavior
as its return value, this new behavior will be treated as the next current state.  Though
you'd never access this, the return value of the GO function is stored here.

myHFA.goReturnValue

This second feature can be convenient, but it has two drawbacks.  First, it means that
your behavior is no longer necessarily modular: it can no longer be easily used in 
different contexts in different HFA.  Second, even if you use this gizmo for all of your
behaviors, you still have to specify the transition function for the "start" state.

Note that the transition function takes precedence over this second feature.   Even if
your behavior takes advantage of this second feature in its GO function, if you specify
a transition for the behavior in your transition function, the transition will be used
instead.

If you do not specify a transition function, or your transition function does not 
have a transition for a specific current state and that behavior returns nothing, then
no transition is made: the behavior stays the current state.  No warning is issued.



-- Running the HFA --

So you've constructed a hierarchy of automata and basic behaviors.  How do you get the
whole thing going?  With the utility function PULSE(...)

pulse(behavior)			initially calls START on the behavior, then GO.  Thereafter
						just calls GO on the behavior.

Typically you'd just set up your HFA and then call pulse(...) on it forever.  If for 
some reason you need to reset the HFA after calling pulse(...) on it some number of times,
you should do the following:

1. call STOP on the behavior
2. set the global variable PULSED to false

Example:

pulse(myHFA)
pulse(myHFA)
pulse(myHFA)
-- ... and so on, then to reset:
myHFA.stop(myHFA)
pulsed = false
-- we're now ready to begin pulsing again...



-- Flags --

HFA come with a variety of gizmos which you will probably find convenient.  Foremost
are FLAGS, which permit a child HFA to inform its parent HFA that it believes it
has completed its task ("done") or that it has "failed".  A flag is set by the
child behavior transitioning to a FLAG BEHAVIOR as its current state.  When this happens,
the corresponding flag is set in the *parent* (not the child).  The parent's transition
functions can test for this flag to determine whether they should now transition away from
the child as the parent's current state, and go somewhere else.  Flags are reset when
startHFA(...) is called on the parent and also when the parent's goHFA(...) function
transitions to a new current state.

There are four flag behaviors available:

done		raises the "done" flag and immediately transitions to the "start" state
failed		raises the "failed" flag and immediately transitions to the "start" state
sayDone		raises the "done" flag
sayFailed	raises the "failed" flag

The first two are the most common, since once you're done (or have failed) you probably
have no further actions you want to do and might as well start over from the top if for
some reason your parent HFA doesn't act on this flag being raised.  For
these two, there's no reason to declare a transition since the transition is
automatic.  For the second two (setDone, setFailed), you need to provide a transition
of some kind, typically a unilateral one.  These second two are much less common.

The flags themselves are stored in the parent here (you'll rarely touch this):

myHFA.done			(false by default)
myHFA.failed		(false by default)

Flags may be PROPAGATED.  It's a common pattern in building HFA that you see transition
functions of the form "if DONE has been rased, then transition to DONE".   This is
essentially saying "If my child thinks he's done, then I'm done too." (likewise "failed").
Because it's so common, rather than require this transition, an HFA can declare that all
of its flags should be propagated to its parent.  This means that if its child sets a flag
in the HFA, the same flag will immediateliy be set in the HFA's parent.  If the parent
likewise has propagation set, it'll be set in the grandparent too, and so on.  If this
feels like throwing an exception, you're on the right track.

You set propagation in your hfa like this:

myHFA.propagateFlags = true

There is an internal function used by the various flag behaviors to set the appropriate
flag and also potentially propagate it recursively:

setFlag(hfa, flag)		sets the given flag in the HFA.  Example: setFlag(myHFA, "failed")



-- COUNTERS AND TIMERS --

Each HFA also has one COUNTER and one TIMER.  HFA nested within one another do not share
these: they have separate counters and timers.

A COUNTER is just an integer which starts at zero (when the HFA's startHFA(...) function is
called) and may be increased or reset to zero by certain behaviors which the HFA may
transition to as its current state.  These behaviors are:

bumpCounter			increments the counter by 1
resetCounter		sets the counter to 0

The current counter value is stored here:

myHFA.counter

... but it's better style to test the current counter value like this:

currentCounter(myHFA)

Testing the value of a counter is useful in a transition function.  Let's say that
you wanted to go forward for three steps and if you were still not close enough
to the ball, you transferred to the run state, then go back to forward.  You might say:

foo = makeHFA("foo", makeTransition(
	{
	["start"] = forward,
	["forward"] = function(hfa) if (closeToBall()) then return kick else return bumpCounter end end,
	["kick"] = rotate,
	["rotate"] = function(hfa) if (ballAhead()) then return forward else return rotate end end,
	["bumpCounter"] = function(hfa) if (hfa.counter > 3) then return run else return forward end end,
	["run"] = resetCounter,
	["resetCounter"] = forward,
	}), false)
	
Similarly, a TIMER is an integer which stores a time interval in seconds.  When the HFA's
startHFA(...) function is called, the timer is set to the current time.  A single behavior
can likewise update the timer to the current time:

resetTimer			updates the timer to the current time

The current timer value is stored here:

myHFA.timer

... but almost certainly you'd access it using the more useful function:

currentTimer(myHFA)

This gives you the current time, minus the timer value, and thus the number of seconds which
have elapsed since the timer was last reset.  Revisiting the example above, imagine you want
to go forward not for three steps but rather for three seconds, then do a run at the ball.  You
might say:

foo = makeHFA("foo", makeTransition(
	{
	["start"] = forward,
	["forward"] = function(hfa)	
		if (closeToBall()) then return kick 
		elseif  currentTimer(hfa) > 3 then return run
		else return forward 
		end
	end,
	["kick"] = rotate,
	["rotate"] = function(hfa) if (ballAhead()) then return forward else return rotate end end,
	["run"] = resetTimer,
	["resetTimer"] = forward,
	}), false)



-- STUFF TO NOTE --

You should be aware of the fact that this library effectively creates directed acyclic
graphs (DAGs) of HFA.  That is, the exact same behavior may be used by different
parent behaviors.  This could have an effect if the behavior maintains some internal
state which is not reset when STOP or START are called.  For example, imagine you had
a behavior called COUNT:

theCount = 0
count = makeBehavior("count", nil, nil, 
	function(behavior) theCount = theCount + 1; print(theCount) end
	)

Now imagine you had two HFAs which both include this behavior among their states.
You're in HFA #1 and it calls count four times, resulting in the numbers 1...4 being
printed to the screen.  Then you're in HFA #2 and it calls count five times.  This
results in the numbers 5...9 being printed to the screen.  This happens because the
same behavior is being used, not multiple copies of it.  If you want multiple copies
of behaviors, you'll need to make them.  Keep this in mind.



... it's always a good sign when the comments are much longer than the code, right?
]]--



require("string")
start = "start"
-- startHFA(hfa)
-- Private internal function which is the START function for an HFA.
startHFA = function(hfa)
	hfa.done = false;
	hfa.failed = false;
	hfa.counter = 0;
	-- maybe this is too costly and we should restrict it to the resetTimer function?
	hfa.timer = os.time()
	hfa.goReturnValue = nil;
	if (not(hfa.interruptable)) then
		hfa.current = nil;
	end
end

-- stopHFA(hfa)
-- Private internal function which is the STOP function for an HFA.
stopHFA = function(hfa)
	if (not(hfa.currentBehavior == nil) and
		not(hfa.currentBehavior.stop == nil) and
		not(hfa.interruptable)) then
		hfa.currentBehavior.stop(hfa.currentBehavior)
	end
end

-- goHFA(hfa)
-- Private internal function which is the GO function for an HFA.
goHFA = function(hfa)
	-- TRANSITION
	local newBehavior = nil
	if (not(hfa.transition == nil)) then
		if (type(hfa.transition)=="function") then
			newBehavior = hfa.transition(hfa)
		else
			newBehavior = hfa.transition
		end
        end	
	if (newBehavior == nil and not(hfa.goReturnValue==nil)) then
		newBehavior = hfa.goReturnValue
		hfa.goReturnValue = nil
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
		hfa.goReturnValue = hfa.currentBehavior.go(hfa.currentBehavior)
	end
end

-- start
-- This is the start state.  There's no associated behavior,
-- it's just the string "start"
start = "start"

-- makeTransition(transitions)
-- transitions is a dictionary whose keys are
-- behaviors (states) and whose values are either OTHER behaviors
-- or transition functions of the form foo(hfa) which, when called,
-- return what the next state should be.  If the value is a behavior (state),
-- then the HFA will ALWAYS transition to this second state when it is currently in
-- the state associated with key.  If the value is a transition function, then the
-- HFA will call the transition function and transition to the state indicated.
makeTransition = function(transitions)
	return function(hfa)
		print("transitions[start] = " .. tostring(transitions[start]));
		print("about to print a")
		transitions[start][start]()
		if (hfa.currentBehavior == nil) then
			return transitions[start].transition(hfa)
		else
			return transitions[currentBehavior].transition(hfa)
		end
	end
end

-- makebehavior(name, start, stop, go)
-- Creates a behavior with the given name and start/stop/go functions.  Any
-- of these functions can be nil.
makeBehavior = function(name, start, stop, go)
	return { ["name"] = name, ["start"] = start, ["stop"] = stop, ["go"] = go, ["parent"] = nil }
end

-- makeHFA(name, transition, interruptable)
-- Creates a potentially interruptable HFA with the given name and transition function.
-- Though the transition function can be nil, it's almost certainlly not appropriate to do so.
makeHFA = function(name, transition, interruptable)
	return { ["goReturnValue"] = nil, ["name"] = name, 
			 ["start"] = startHFA, ["stop"] = stopHFA, ["go"] = goHFA, 
		 	 ["transition"] = transition, ["interruptable"] = interruptable, ["parent"] = nil,
			 ["counter"] = 0, ["timer"] = 0, ["done"] = false, ["failed"] = false, ["current"] = nil,
			 ["propagateFlags"] = false }
end

-- Has pulse(...) been called at least once?
pulsed = false

-- Pulses the behavior.  This is the top-level stepping procedure for your HFA.
pulse = function(behavior)
			if (not pulsed) then
				pulsed = true
				if (not(behavior.start == nil)) then
					behavior.start(behavior)
				end
			end
			if (not(behavior.go == nil)) then
				behavior.go(behavior)
			end
		end
		



-- UTILITY BEHAVIORS --

-- bumpCounter: increments the HFA's counter by 1
bumpCounter = makeBehavior("bumpCounter", 
	function(behavior) 
		if ((not behavior == nil) and (not behavior.parent == nil)) then 
			behavior.parent.counter = behavior.paranet.counter + 1
		end
	end, nil, nil)

-- zeroCounter: sets the HFA's counter to 0
resetCounter = makeBehavior("resetCounter",
	function(behavior) 
		if ((not behavior == nil) and (not behavior.parent == nil)) then 
			behavior.parent.counter = 0
		end
	end, nil, nil)
	
-- currentCounter(hfa) returns the HFA's current counter value
currentCounter = function(hfa) return hfa.counter end

-- resetTimer: resets the HFA's current timer value
resetTimer = makeBehavior("resetTimer",
	function(behavior)
		if ((not behavior == nil) and (not behavior.parent == nil)) then
			behavior.parent.timer = os.time()
		end
	end, nil, nil)

-- currentTimer(hfa) returns the difference between the current time and the
--                   HFA's current timer value
currentTimer = function(hfa) return os.time() - hfa.timer end

-- setFlag(hfa, flag).  Internal private function used by various flag behaviors
--                      to set a flag in an HFA, potentially recursively if
--                      propagation is turned on.
setFlag = function(hfa, flag)
	if (not hfa.parent == nil) then
		hfa.parent[flag] = true
		if (hfa.parent.propagateFlags == true) then
			setFlag(hfa.parent, flag)
		end
	end
end

-- done: sets the "done" flag in the HFA's parent, and transitions to "start"
done = makeBehavior("done", nil, nil,
	function(behavior) 
		if (not behavior == nil and (not behavior.parent == nil)) then
			behavior.current = start
			setFlag(behavior.parent, "done")
		end
	end)

-- sayDone: sets the "done" flag in the HFA's parent
sayDone = makeBehavior("sayDone", 
	function(behavior)
		if (not behavior == nil and (not behavior.parent == nil)) then
			setFlag(behavior.parent, "done")
		end
	end, nil, nil)
	
-- failed: sets the "failed" flag in the HFA's parent, and transitions to "start"
failed = makeBehavior("failed", nil, nil,
	function(behavior) 
		if (not behavior == nil and (not behavior.parent == nil)) then
			behavior.current = start
			setFlag(behavior.parent, "failed")
		end
	end)

-- sayFailed: sets the "failed" flag in the HFA's parent
sayFailed = makeBehavior("sayFailed", 
	function(behavior)
		if (not behavior == nil and (not behavior.parent == nil)) then
			setFlag(behavior.parent, "failed")
		end
	end, nil, nil)
	

-- END HFA.LUA


printAStart = function(hfa)
	print("a")
end
printAStop = function(hfa)

end
printAGo = function(hfa)

end
printA = makeBehavior("printA", printAStart, printAStop, printAGo);

printBStart = function(hfa)
	print("b")
end
printBStop = function(hfa)

end
printBGo = function(hfa)

end
printB= makeBehavior("printB", printAStart, printAStop, printAGo);

myArray =  {
		[start] = printA,
		["printA"] = printB, 
		["printB"] = printA,
	}
--print(myArray[start]);
--myArray[start][start]();
foo = makeHFA("foo", makeTransition(
        {
		[start] = printA,
		["printA"] = printB, 
		["printB"] = printA,
	}), false)

while 1 do
	pulse(foo);
end
