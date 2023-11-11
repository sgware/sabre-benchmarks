;;;
;;; A domain for modeling stories in a magical kingdom
;;; Created by Stephen G. Ware
;;; Originally used for validating CPOCL narrative structure
;;;
(define (domain fantasy)
  (:requirements :adl :intentionality)
  (:types ; Person and monster are a types of creature.
          person monster - creature
		  ; Items exist.
          valuable - item
		  ; Places exist.
		  place)
  (:predicates ; A creature is alive.
               (alive ?creature - creature)
			   ; A person is single.
			   (single ?person - person)
			   ; A creature is rich.
               (rich ?creature - creature)
			   ; A creature is happy.
               (happy ?creature - creature)
			   ; A creature is hungry.
			   (hungry ?creature - creature)
               ; An object is at a place.
			   (at ?object - object ?place - place)
			   ; A creature has an item.
			   (has ?creature - creature ?item - item)
			   ; An item belongs to a creature
			   (belongsto ?item - item ?creature - creature)
			   ; One creature loves another.
			   (loves ?lover - creature ?love - creature)
			   ; One person has proposed to another.
			   (hasproposed ?proposer - person ?proposee - person)
			   ; One person has accepted another's proposal.
			   (hasaccepted ?person1 - person ?person2 - person)
			   ; Two people are married.
			   (marriedto ?person1 - person ?person2 - person))

  ;; A creature travels from one place to another.
  (:action travel
    :parameters   (?creature - creature ?from - place ?to - place)
    :precondition (and (alive ?creature)
					   (at ?creature ?from))
    :effect       (and (at ?creature ?to)
                       (not (at ?creature ?from)))
    :agents       (?creature))

  ;; One person proposes to another.
  (:action propose
    :parameters   (?proposer - person ?proposee - person ?place - place)
	:precondition (and (alive ?proposer)
	                   (at ?proposer ?place)
					   (alive ?proposee)
					   (at ?proposee ?place)
					   (loves ?proposer ?proposee))
	:effect       (hasproposed ?proposer ?proposee)
	:agents       (?proposer))

  ;; One person accepts another's proposal.
  (:action accept
    :parameters   (?accepter - person ?proposer - person ?place - place)
	:precondition (and (alive ?accepter)
	                   (at ?accepter ?place)
					   (alive ?proposer)
					   (at ?proposer ?place)
					   (hasproposed ?proposer ?accepter))
	:effect       (hasaccepted ?accepter ?proposer)
	:agents       (?accepter))

  ;; Two people marry.
  (:action marry
    :parameters   (?groom - person ?bride - person ?place - place)
	:precondition (and (alive ?groom)
	                   (at ?groom ?place)
					   (hasproposed ?groom ?bride)
					   (single ?groom)
					   (alive ?bride)
					   (at ?bride ?place)
					   (hasaccepted ?bride ?groom)
					   (single ?bride))
	:effect       (and (marriedto ?groom ?bride)
					   (marriedto ?bride ?groom)
					   (not (single ?groom))
					   (not (single ?bride))
	                   (forall (?v - valuable)
					           (when (has ?groom ?v)
							         (rich ?bride)))
					   (when (loves ?groom ?bride)
					         (happy ?groom))
					   (when (loves ?bride ?groom)
					         (happy ?bride)))
	:agents       (?groom ?bride))

  ;; A creature steals an object from another creature.
  (:action steal
    :parameters   (?thief - creature ?victim - creature ?item - item ?place - place)
	:precondition (and (not (= ?thief ?victim))
	                   (alive ?thief)
	                   (at ?thief ?place)
					   (at ?item ?place)
					   (belongsto ?item ?victim))
	:effect       (and (has ?thief ?item)
	                   (when (at ?victim ?place)
					         (intends ?victim (has ?victim ?item)))
					   (when (forall (?v - valuable)
                                     (not (has ?victim ?v)))
					         (not (rich ?victim))))
	:agents       (?thief))

  ;; A creature becomes hungry.
  (:action get-hungry
    :parameters   (?creature - creature)
	:precondition (not (hungry ?creature))
	:effect       (and (hungry ?creature)
					   (intends ?creature (not (hungry ?creature))))
	:agents       (?creature))

  ;; A monster eats another creature.
  (:action eat
    :parameters   (?monster - monster ?creature - creature ?place - place)
	:precondition (and (alive ?monster)
	                   (at ?monster ?place)
					   (hungry ?monster)
					   (alive ?creature)
					   (at ?creature ?place))
	:effect       (and (not (hungry ?monster))
	                   (not (alive ?creature))
					   (not (rich ?creature))
					   (not (happy ?creature)))
	:agents       (?monster)))