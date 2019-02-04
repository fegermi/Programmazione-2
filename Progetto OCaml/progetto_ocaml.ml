(* 2° progetto PR2 *)
(* Germinario Federico 545081 *)

type ide = string;;
type exp = Eint of int | Ebool of bool | Den of ide | Prod of exp * exp | Sum of exp * exp | Diff of exp * exp |
	Eq of exp * exp | Minus of exp | IsZero of exp | Or of exp*exp | And of exp*exp | Not of exp |
	Ifthenelse of exp * exp * exp | Let of ide * exp * exp | Fun of ide * exp | FunCall of exp * exp |Letrec of ide * exp * exp
	  | Str of ide                                                        
		| Edict of (ide * exp) list (* Dizionario *)
		| Select of exp * ide       (* Selezione elemento dizionario *)
		| Add of exp * ide * exp    (* Aggiunta di un elemento al dizionario *)
		| Remove of exp * ide       (* Rimozione di un elemento dal dizionario *)
		| Clear of exp              (* Svuotamento dizionario *)
		| ApplyOver of exp * exp    (* Applicazione di funzione a tutti i valori del dizionario *)

(* ambiente polimorfo *)
type 't env = ide -> 't;;
let emptyenv (v : 't) = function x -> v;;
let applyenv (r : 't env) (i : ide) = r i;;
let bind (r : 't env) (i : ide) (v : 't) = function x -> if x = i then v else applyenv r x;;

(*tipi esprimibili*)
type evT = Int of int 
           | Bool of bool 
					 | String of string 
           | Unbound  
           | FunVal of evFun  
           | RecFunVal of ide * evFun 
					 | DictVal of (ide * evT) list
            and evFun = ide * exp * evT env 

(* rts *)
(* type checking *)
let typecheck (s : string) (v : evT) : bool = match s with
	"int" -> (match v with
		Int(_) -> true |
		_ -> false) |
	"bool" -> (match v with
		Bool(_) -> true |
		_ -> false) |
	_ -> failwith("not a valid type");;

(* funzioni primitive *)
let prod x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Int(n*u))
	else failwith("Type error");;

let sum x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Int(n+u))
	else failwith("Type error");;

let diff x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Int(n-u))
	else failwith("Type error");;

let eq x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Bool(n=u))
	else failwith("Type error");;

let minus x = if (typecheck "int" x) 
	then (match x with
	   	Int(n) -> Int(-n))
	else failwith("Type error");;

let iszero x = if (typecheck "int" x)
	then (match x with
		Int(n) -> Bool(n=0))
	else failwith("Type error");;

let vel x y = if (typecheck "bool" x) && (typecheck "bool" y)
	then (match (x,y) with
		(Bool(b),Bool(e)) -> (Bool(b||e)))
	else failwith("Type error");;

let et x y = if (typecheck "bool" x) && (typecheck "bool" y)
	then (match (x,y) with
		(Bool(b),Bool(e)) -> Bool(b&&e))
	else failwith("Type error");;

let non x = if (typecheck "bool" x)
	then (match x with
		Bool(true) -> Bool(false) |
		Bool(false) -> Bool(true))
	else failwith("Type error");;

(* interprete *)
let rec eval (e : exp) (r : evT env) : evT = match e with
	Eint n -> Int n |
	Ebool b -> Bool b |
	IsZero a -> iszero (eval a r) |
	Den i -> applyenv r i |
	Str s -> String s |
	Eq(a, b) -> eq (eval a r) (eval b r) |
	Prod(a, b) -> prod (eval a r) (eval b r) |
	Sum(a, b) -> sum (eval a r) (eval b r) |
	Diff(a, b) -> diff (eval a r) (eval b r) |
	Minus a -> minus (eval a r) |
	And(a, b) -> et (eval a r) (eval b r) |
	Or(a, b) -> vel (eval a r) (eval b r) |
	Not a -> non (eval a r) |
	Ifthenelse(a, b, c) -> 
		let g = (eval a r) in
			if (typecheck "bool" g) 
				then (if g = Bool(true) then (eval b r) else (eval c r))
				else failwith ("non boolean guard") |
	Let(i, e1, e2) -> eval e2 (bind r i (eval e1 r)) |
	Fun(i, a) -> FunVal(i, a, r) |
	FunCall(f, eArg) -> 
		let fClosure = (eval f r) in
			(match fClosure with
				FunVal(arg, fBody, fDecEnv) -> 
					eval fBody (bind fDecEnv arg (eval eArg r)) |
				RecFunVal(g, (arg, fBody, fDecEnv)) -> 
					let aVal = (eval eArg r) in
						let rEnv = (bind fDecEnv g fClosure) in
							let aEnv = (bind rEnv arg aVal) in
								eval fBody aEnv |
				_ -> failwith("non functional value"))|
				
	Letrec(f, funDef, letBody) ->
  	(match funDef with
    	Fun(i, fBody) -> let r1 = (bind r f (RecFunVal(f, (i, fBody, r)))) in
                         			                eval letBody r1 |
            		_ -> failwith("non functional def")) |
								
	Edict(d) -> let dEval = evalList d r in 
								if (checkDuplicate dEval) then DictVal(dEval) 
																					else failwith("duplicate keys") |	       
																	                                  
	Select(d, i) -> (match (eval d r) with  
											 DictVal(dict) -> lookup i dict 
											| _ -> failwith("wrong select value")) |
	
	Add(d, i, e) -> (match (eval d r) with 
	  								  DictVal(dict) -> if (isPresent i dict) then failwith("key already present")	
										                                          else DictVal((i, eval e r) :: dict)														
										 | _ -> failwith("wrong select value"))|
																																					
  Remove(d, i) -> (match (eval d r) with  
											DictVal(dict) -> DictVal(rm i dict) 
											| _ -> failwith("wrong select value")) | 
											
	Clear(d) -> (match (eval d r) with  
								  DictVal(_) -> DictVal([]) 
									| _ -> failwith("wrong select value")) |
											
	(* per scelta implementativa non si posso utilizzare funzioni ricorsive *)										
	ApplyOver(f, d) -> (let funCall( (f : exp) , (eArg : evT) ) =  
																	let fClosure = (eval f r) in 
																			(match fClosure with
																					FunVal(arg, fBody, fDecEnv) -> 
																							eval fBody (bind fDecEnv arg eArg)    
																					| _ -> failwith("non functional value") )
																							
												   in let rec evalApplyOver (dict : (ide * evT) list ) (r : evT env)  =
            										 	match dict with
                            				[ ] ->  [ ]
																		| (key, v) :: rest -> if (typecheck "int" v) then (key, funCall(f, v) ) :: evalApplyOver rest r
																			                                           else (key, v) :: evalApplyOver rest r	 
													
													 in match (eval d r) with 
															DictVal(dict) -> (match (eval f r) with 
																					          | FunVal(_, _, _) -> DictVal(evalApplyOver dict r)  
													                          | _ -> failwith ("invalid type fun")
																									)
															| _ -> failwith("invalid type dict")
												)

(* valutazione dizionario d nell' ambiente r *)							 													
and evalList (d : (ide * exp) list ) (r : evT env) : (ide * evT) list = 
	match d with
  |	[ ] -> [ ]
  | (key, v) :: rest -> (key, eval v r) :: evalList rest r

(* controlla se nel dizionario ci sono chiavi duplicate *)
and checkDuplicate (d: (ide * evT) list) : bool =
	match d with
	| [ ] -> true
	| (key, _ )::ls -> if (isPresent key ls) then false
																					 else checkDuplicate ls
(* controlla se nel dizionario è gia presente la chiave i *)
and isPresent (i: ide) (d: (ide * evT) list) : bool =
	match d with
	| [ ] -> false
	| (key, _ )::ls -> if(key = i) then true 
												else isPresent i ls

(* restituisce la chiave i se presente nel dizionario *)
and lookup (i: ide) (d: (ide * evT) list) : evT = 
	match d with 
	| [ ] -> failwith("value not present")
	| (key, v) :: rest -> if (i = key) then v else (lookup i rest)

(* restituisce un dizionario senza la chiave i se presente *)
and rm (i: ide) (d: (ide * evT) list) : (ide * evT) list = 
	match d with 
	| [ ] -> failwith("value not present")
	| (key, v) :: rest -> if (i = key) then rest else ( (key, v) :: rm i rest )
						
;;

(* =============================  TESTS  ============================= *)

let env0 = emptyenv Unbound;;

let e1 = FunCall(Fun("y", Sum(Den "y", Eint 1)), Eint 3);;
eval e1 env0;;
(* risultato: 4 *)

let e2 = FunCall(Let("x", Eint 2, Fun("y", Sum(Den "y", Den "x"))), Eint 3);;
eval e2 env0;;
(* risultato: 5 *)

(* Dizionario vuoto *)
let my_dict0 = Edict( [ ] );;
eval my_dict0 env0;;

(* Edict tests *)
(* Edict test: Failure "duplicate keys" *)
let my_dict0  = Edict( ["nome", Str "Federico"; "matricola", Sum(Eint 505080, Eint 1); "nome", Str "stefano"] );;
eval my_dict0 env0;;

let my_dict0  = Edict( ["nome", Str "Federico"; "matricola", Sum(Eint 505080, Eint 1)] );;
eval my_dict0 env0;;

(* Select tests *)
let selNome  = Select(my_dict0, "nome");;
eval selNome env0;;
(* - : evT = String "Federico" *)

(* Select test: Failure "value not present" *)
let selCognome  = Select(my_dict0, "cognome");;
eval selCognome env0;;

(* Add test *)
let my_dict1 = Add(my_dict0, "eta", Eint 21);;
eval my_dict1 env0;;
(* - : evT = DictVal[("eta", Int 21); ("nome", String "Federico"); ("matricola", Int 505081)] *)

(* Remove tests *)
let my_dict2 = Remove(my_dict1, "nome");;
eval my_dict2 env0;; 
(* - : evT = DictVal[("eta", Int 21); ("matricola", Int 505081)] *)

(* Remove test: Failure "value not present" *)
let my_dict3 = Remove(my_dict2, "cognome");;
eval my_dict3 env0;; 

(* ApplyOver tests *)
let my_dict4 = ApplyOver( Fun("x", Sum(Den "x", Eint 5)) , my_dict2);; 
eval my_dict4 env0;; 
(* - : evT = DictVal[("eta", Int 26); ("matricola", Int 505086)] *)

let my_dict5 = ApplyOver( Fun("x", Sum(Den "x", Eint 1)) , my_dict1);;
eval my_dict5 env0;; 
(* - : evT = DictVal[("eta", Int 22); ("nome", String "Federico"); ("matricola", Int 505082)] *)

let my_dict6 = ApplyOver( Fun("x", Prod(Den "x", Eint 2)) , my_dict1);;
eval my_dict6 env0;; 
(* - : evT = DictVal[("eta", Int 42); ("nome", String "Federico"); ("matricola", Int 1010162)] *)

(* Clear test *)
let my_dict7 = Clear(my_dict4);;
eval my_dict7 env0;;
(* - : evT = DictVal [] *)