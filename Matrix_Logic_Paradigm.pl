:-include('KB.pl').

% This predicate checks if neo can move up and changes his coordinates if true.
up((NX,NY),X,NY):- NX > 0, X is NX-1.

% This predicate checks if neo can move down and changes his coordinates if true.
down((NX,NY),X,NY):- grid(A,_), A1 is A-1, NX < A1, X is NX+1. 

% This predicate checks if neo can move right and changes his coordinates if true.
right((NX,NY),NX,Y):- grid(_,A), A1 is A-1, NY < A1, Y is NY+1.

% This predicate checks if neo can move left and changes his coordinates if true.
left((NX,NY),NX,Y):- NY > 0, Y is NY-1.

% This predicate checks if there are hostages in neos location and makes him carry them if his current capacity is greater than 0.
carry(_,C,C,[],[],[]).
carry((NX,NY), C, NC, [[NX,NY]|T], [[NX,NY]|L], R):- C > 0, C1 is C-1, carry((NX,NY),C1, NC, T, L, R).
carry((NX,NY), C, NC, [[X,Y]|T], L, [[X,Y]|R]):- (NX \= X ; NY \= Y ; C =0) , carry((NX,NY),C, NC, T, L, R).  

% This predicate makes neo drop all carried hostages if he is at the booth coordinates and keeps track of how many hostages are rescued.
drop((NX,NY), H, L, Sum):- booth(NX,NY), length(H,L1), L1>0, Sum is L+L1.



% This is the predicate that solves the problem by calling solveHelper with the initial state, a variable to store the goal, 
% neo location, hotages list, capacity, an empty list where we can save the carried hostages along the way, and a 0 which then 
% will be added to the number of rescued hostages along the way.
solve(S):- neo_loc(NX,NY), hostages_loc(H), capacity(C), solveHelper(s0,S, (NX,NY), H, C, [],0).


% This is the main successor-state axiom where we check if neo is at a goal state and check the outcome of each action to accomplish that if the current state is not a goal state.
solveHelper(S,S,(NX,NY),_,C,[],N):- booth(NX,NY), capacity(C),hostages_loc(H), length(H,N).
solveHelper(S,G, (NX,NY), H, C, CH, N):- 
    carry((NX,NY), C, NC, H, Carried, NotCarried), length(Carried,LCarried), LCarried>0, append(CH, Carried, NewCarried), solveHelper(result(carry, S),G, (NX, NY), NotCarried, NC, NewCarried, N).

solveHelper(S,G, (NX,NY), H, _, CH, N):-
    drop((NX,NY), CH, N , Sum), capacity(C1), solveHelper(result(drop, S),G, (NX, NY), H, C1, [], Sum).

solveHelper(S,G, (NX,NY), H, C, CH, N):-
    right((NX,NY), X, Y) , solveHelper(result(right, S),G, (X, Y), H, C, CH, N).

solveHelper(S,G, (NX,NY), H, C, CH, N):-
    down((NX,NY), X, Y), solveHelper(result(down, S),G, (X, Y), H, C, CH, N).

solveHelper(S,G, (NX,NY), H, C, CH, N):-
    left((NX,NY), X, Y), solveHelper(result(left, S),G, (X, Y), H, C, CH, N).

solveHelper(S,G, (NX,NY), H, C, CH, N):-
    up((NX,NY), X, Y), solveHelper(result(up, S),G, (X, Y), H, C, CH, N).


% This is a utility predicate to call call_with_depth_limit recursively with different depths.
iterativeDeepening(S,D):- call_with_depth_limit(solve(S),D,R), R\==depth_limit_exceeded.
iterativeDeepening(S,D):- call_with_depth_limit(solve(S),D,R), R==depth_limit_exceeded, D1 is D+1, iterativeDeepening(S,D1).



% This predicate calls iterativeDeepening with a depth of 5 to start the iterative deepening search if S is a variable
% This predicate calls call_with_depth_limit(solve(S),15,R) immediatly with a depth of 15 if S is not a variable
goal(S):-  \+var(S) , call_with_depth_limit(solve(S),15,R), R\==depth_limit_exceeded.
goal(S):- var(S), iterativeDeepening(S,5).