(define (domain domain-basketball)

(:requirements :adl :typing :negative-preconditions :equality)

(:types         
	item - object
	place - object
	crime - object
	actor - object

	citizen - actor
	police - actor
	detective inspector - police

  allpolice - actor
  trafficcop - actor

  vehicle - item
  car motorbike - vehicle
)

(:predicates
  (arrester ?p - actor)
  (arrester-at ?p - place)
 
	(at ?c - actor ?p - place)
	(has ?c - actor ?t - item)
	(alive ?a - actor)

	(under-arrest ?a - actor)
	(angry ?a - actor)
	(knows-clue ?p - police ?c - crime ?i - item)
	(suspect ?a - actor ?c - crime)
	(murder-clue ?i - item)
	(clue ?c - crime ?i - item ?p - place)
	(is-basketball-place ?p - place)
	(is-theft ?c - crime)
	(is-arrest-place ?p - place)
	(is-murder ?c - crime)
  
  (has-car ?a - actor ?c - vehicle)
  (working ?c - vehicle)
)

(:action travel
  :parameters (?a - actor ?c - car ?p1 ?p2 - place)
  :precondition (and 
    (working ?c)
	  (at ?a ?p1)
	  (alive ?a)
    (has-car ?a ?c)
  )
  :effect (and 
	  (at ?a ?p2)
	  (not (at ?a ?p1))
    (when 
      (and (arrester ?a))
      (and (arrester-at ?p2)
        (not (arrester-at ?p1))
    ))
  )
)

(:action arrest
  :parameters (?p - police ?a - actor ?p1 ?p2 - place ?c - crime)
  :precondition (and 
	  (at ?p ?p1)
	  (at ?a ?p1)
	  (not (= ?p1 ?p2))
	  (is-arrest-place ?p2)
	  (alive ?p)
	  (alive ?a)
	  (suspect ?a ?c)
    (arrester ?p)
  )
  :effect (and 
	  (at ?a ?p2)
	  (not (at ?a ?p1))
	  (at ?p ?p2)
	  (not (at ?p ?p1))
	  (under-arrest ?a)
  )
)


(:action steal
  :parameters (?a ?b - citizen ?c - crime ?i - item ?p - place)
  :precondition (and 
	  (at ?a ?p)
	  (at ?b ?p)
	  (has ?b ?i)
	  (not (= ?a ?b))
	  (alive ?a)
	  (is-theft ?c)
  )
  :effect (and 
	  (has ?a ?i)
	  (not (has ?b ?i))
	  (angry ?b)
	  (clue ?c ?i ?p)
  )
)

(:action play-basketball
  :parameters (?a ?b - citizen ?p - place)
  :precondition (and 
	  (is-basketball-place ?p)
	  (at ?a ?p)
	  (alive ?a)
	  (at ?b ?p)
	  (alive ?b)
    (not (= ?a ?b))
  )
  :effect (and 
 	  (not (angry ?a))
 	  (not (angry ?b))
  )
)
	
(:action kill
  :parameters (?a ?b - citizen ?c - crime ?i - item ?p - place)
  :precondition (and 
	(at ?a ?p)
	(at ?b ?p)
	(alive ?a)
	(alive ?b)
	(not (= ?a ?b))
	(has ?a ?i)
	(is-murder ?c)
  (not (under-arrest ?a))
  )
  :effect (and 
	(not (alive ?b))
	(clue ?c ?i ?p)
  )
)

(:action findclues
  :parameters (?a - police ?c - crime ?i - item ?p - place)
  :precondition (and 
	  (at ?a ?p)
	  (alive ?a)
	  (clue ?c ?i ?p)
  )
  :effect (and 
	  (knows-clue ?a ?c ?i)
  )
)
 
(:action shareclues
  :parameters (?a ?b - police ?c - crime ?i - item ?p - place)
  :precondition (and 
	  (at ?a ?p)
	  (alive ?a)
	  (at ?b ?p)
	  (alive ?b)
	  (clue ?c ?i ?p)
	  (knows-clue ?a ?c ?i)
  )
  :effect (and 
	  (knows-clue ?b ?c ?i)
  )
)

(:action suspect-of-crime
  :parameters (?a - police ?b - citizen ?c - crime ?i - item ?p - place)
  :precondition (and 
	  (at ?a ?p)
	  (alive ?a)
	  (at ?b ?p)
	  (alive ?b)
	  (knows-clue ?a ?c ?i)
	  (has ?b ?i)
  )
  :effect (and 
	  (suspect ?b ?c)
  )
)
 
)
  
