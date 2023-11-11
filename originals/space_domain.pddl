;;;
;;; A domain for modeling encounters with extraterrestrial guardians of volatile planets
;;; Created by Stephen G. Ware
;;; Originally used for validating CPOCL narrative structure
;;;
(define (domain space)
  (:requirements :adl :intentionality)
  (:types ; A creature is any living thing
          creature
          ; There are two types of places: landforms and ships.
          landform ship - place)
  (:predicates ; A creature is alive.
               (alive ?creature - creature)
               ; A creature is stunned.
               (stunned ?creature - creature)
			   ; A place is habitable.
			   (habitable ?place - place)
			   ; A place is safe.
			   (safe ?place - place)
			   ; A creature is safe.
			   (safe ?creature - creature)
			   ; A landform is erupting lava.
			   (erupting ?landform - place)
               ; An creature is at a place.
               (at ?creature - creature ?place - place)
               ; Two creatures are fighting.
               (fighting ?creature1 - creature ?creature2 - creature)
			   ; Two creatures are friends.
			   (friends ?creature1 - creature ?creature2 - creature)
			   ; A creature is captain of a ship
			   (captain ?creature - creature ?ship - ship)
			   ; A creature is a guardian of a place.
			   (guardian ?creature - creature ?place - place))

  ;; A creature walks from one landform to another.
  (:action walk
    :parameters   (?creature - creature ?from - landform ?to - landform)
    :precondition (and (not (= ?from ?to))
                       (alive ?creature)
                       (not (stunned ?creature))
                       (at ?creature ?from)
                       (habitable ?to)
                       (safe ?to)
                       (forall (?c - creature)
                               (and (not (fighting ?creature ?c))
                                    (not (fighting ?c ?creature)))))
    :effect       (and (at ?creature ?to)
                       (not (at ?creature ?from))
                       (when (not (safe ?creature))
                             (safe ?creature)))
    :agents       (?creature))

  ;; A creature teleports from a ship to a place.
  (:action teleport-from-ship
    :parameters   (?creature - creature ?from - ship ?to - place)
    :precondition (and (not (= ?from ?to))
                       (alive ?creature)
                       (not (stunned ?creature))
                       (at ?creature ?from)
                       (habitable ?to)
                       (safe ?to)
                       (captain ?creature ?from))
    :effect       (and (at ?creature ?to)
                       (not (at ?creature ?from))
                       (when (not (safe ?creature))
                             (safe ?creature))
                       (forall (?c - creature)
                               (and (not (fighting ?creature ?c))
                                    (not (fighting ?c ?creature))
                                    (when (guardian ?c ?to)
                                          (intends ?c (not (alive ?creature)))))))
    :agents       (?creature))

  ;; A creature teleports from a place to a ship.
  (:action teleport-to-ship
    :parameters   (?creature - creature ?from - place ?to - ship)
    :precondition (and (not (= ?from ?to))
                       (alive ?creature)
                       (not (stunned ?creature))
                       (at ?creature ?from)
                       (habitable ?to)
                       (safe ?to)
                       (captain ?creature ?to))
    :effect       (and (at ?creature ?to)
                       (not (at ?creature ?from))
                       (when (not (safe ?creature))
                             (safe ?creature))
                       (forall (?c - creature)
                              (and (not (fighting ?creature ?c))
                                   (not (fighting ?c ?creature)))))
    :agents       (?creature))
  ;; One creature starts a fight with another.
  (:action attack
    :parameters   (?attacker - creature ?victim - creature ?place - place)
    :precondition (and (alive ?attacker)
                       (not (stunned ?attacker))
                       (at ?attacker ?place)
                       (alive ?victim)
                       (not (stunned ?victim))
                       (at ?victim ?place))
    :effect       (and (fighting ?attacker ?victim)
                       (intends ?victim (not (fighting ?attacker ?victim))))
    :agents       (?attacker))

  ;; One creatures kills another to end a fight.
  (:action kill
    :parameters   (?killer - creature ?victim - creature)
    :precondition (and (alive ?killer)
                       (not (stunned ?killer))
                       (alive ?victim)
                       (or (fighting ?killer ?victim)
                           (fighting ?victim ?killer)))
    :effect       (and (not (alive ?victim))
                       (when (fighting ?killer ?victim)
                             (not (fighting ?killer ?victim)))
                       (when (fighting ?victim ?killer)
                             (not (fighting ?victim ?killer))))
    :agents       (?killer))

  ;; One creatures stuns another to end a fight.
  (:action stun
    :parameters   (?stunner - creature ?victim - creature)
    :precondition (and (alive ?stunner)
                       (not (stunned ?stunner))
                       (alive ?victim)
                       (not (stunned ?victim))
                       (or (fighting ?stunner ?victim)
                           (fighting ?victim ?stunner)))
    :effect       (and (stunned ?victim)
                       (when (fighting ?stunner ?victim)
                             (not (fighting ?stunner ?victim)))
                       (when (fighting ?victim ?stunner)
                             (not (fighting ?victim ?stunner))))
    :agents       (?stunner))

  ;; A stunned creature breaks free.
  (:action break-free
    :parameters   (?victim - creature)
    :precondition (and (alive ?victim)
                       (stunned ?victim))
    :effect       (not (stunned ?victim))
    :agents       (?victim))

  ;; One creature makes peace with another.
  (:action make-peace
    :parameters   (?peacemaker - creature ?creature - creature ?place - place)
    :precondition (and (alive ?peacemaker)
                       (not (stunned ?peacemaker))
                       (at ?peacemaker ?place)
                       (alive ?creature)
                       (at ?creature ?place)
                       (not (fighting ?peacemaker ?creature))
                       (not (fighting ?creature ?peacemaker)))
    :effect       (and (friends ?peacemaker ?creature)
                       (friends ?creature ?peacemaker))
    :agents       (?peacemaker))

  ;; A volcano begins to errupt.
  (:action begin-erupt
    :parameters   (?landform - landform)
    :effect       (and (erupting ?landform)
                       (forall (?c - creature)
                               (when (at ?c ?landform)
                                     (and (not (safe ?c))
                                          (intends ?c (safe ?c)))))))

  ;; A volcano errupts.
  (:action erupt
    :parameters   (?landform - landform)
    :precondition (erupting ?landform)
    :effect       (and (not (habitable ?landform))
                       (not (erupting ?landform))
                       (forall (?c - creature)
                               (when (at ?c ?landform)
                                     (not (alive ?c)))))))