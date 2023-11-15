;;;
;;; A domain for modeling stories in the Wild West
;;; Created by Stephen G. Ware
;;; Originally used for validating CPOCL narrative structure
;;;
(define (domain western)
  (:requirements :adl :intentionality)
  (:types ; People and animals are living things.
          person animal - living
          ; Animals are items that can be owned.
          animal - item
          ; Some items are valuable.
          valuable - item
          ; Places exist.
          place
          ; Sicknesses exist
          sickness)
  (:constants ; A place to imprison criminals
              jailhouse - place
              ; The "sickness" of being bitten by a poisonous snake
              snakebite - sickness)
  (:predicates ; A person is alive.
               (alive ?person - person)
               ; A person is not restrained.
               (free ?person - person)
               ; A person is the sheriff.
               (sheriff ?person - person)
               ; A person or thing is at a place.
               (at ?object - object ?place - place)
               ; An item belongs to a person.
               (belongsto ?item - item ?person - person)
               ; A person has an item.
               (has ?person - person ?item - item)
               ; A person is sick with some kind of sickness.
               (sick ?person - person ?sickness - sickness)
               ; An item can cure a sickness.
               (cures ?item - item ?sickness - sickness)
               ; One person loves another.
               (loves ?lover - person ?love - person))

  ; A character gets bitten by a rattlesnake and becomes sick.
  (:action snakebite
    :parameters   (?victim - person)
    :precondition (alive ?victim)
    :effect       (and (sick ?victim snakebite)
                       (intends ?victim (not (sick ?victim snakebite)))
                       (forall (?p - person)
                               (when (loves ?p ?victim)
                                     (intends ?p (not (sick ?victim snakebite)))))))

  ; A character dies of dies of some sickness.
  (:action die
    :parameters   (?person - person ?sickness - sickness)
    :precondition (and (alive ?person)
                       (sick ?person ?sickness))
    :effect       (not (alive ?person)))

  ; A character travels from one location to another.
  (:action travel
    :parameters   (?person - person ?from - place ?to - place)
    :precondition (and (alive ?person)
                       (free ?person)
                       (at ?person ?from))
    :effect       (and (at ?person ?to)
                       (not (at ?person ?from)))
    :agents       (?person))

  ; A character forces a tied up character to move from one place to another.
  (:action forcetravel
    :parameters   (?person - person ?victim - person ?from - place ?to - place)
    :precondition (and (alive ?person)
                       (free ?person)
                       (at ?person ?from)
                       (alive ?victim)
                       (not (free ?victim))
                       (at ?victim ?from))
    :effect       (and (at ?person ?to)
                       (not (at ?person ?from))
                       (at ?victim ?to)
                       (not (at ?victim ?from)))
    :agents       (?person))

  ; One character gives an item to another.
  (:action give
    :parameters   (?giver - person ?receiver - person ?item - item ?place - place)
    :consent      (?giver ?receiver)       
    :precondition (and (alive ?giver)
                       (free ?giver)
                       (at ?giver ?place)
                       (has ?giver ?item)
                       (alive ?receiver)
                       (free ?receiver)
                       (at ?receiver ?place))
    :effect       (and (has ?receiver ?item)
                       (not (has ?giver ?item))
                       (when (belongsto ?item ?giver)
                             (belongsto ?item ?receiver)))
    :agents       (?giver))

  ; One character ties up another.
  (:action tieup
    :parameters   (?person - person ?victim - person ?place - place)
    :precondition (and (alive ?person)
                       (free ?person)
                       (at ?person ?place)
                       (alive ?victim)
                       (at ?victim ?place))
    :effect       (and (not (free ?victim))
                       (intends ?victim (free ?victim)))
    :agents       (?person))

  ; One character unties another.
  (:action untie
    :parameters   (?person - person ?victim - person ?place - place)
    :precondition (and (alive ?person)
                       (free ?person)
                       (at ?person ?place)
                       (alive ?victim)
                       (not (free ?victim))
                       (at ?victim ?place))
    :effect       (free ?victim)
    :agents       (?person))

  ; One character takes an item from a tied up character.
  (:action take
    :parameters   (?taker - person ?item - item ?victim - person ?place - place)
    :precondition (and (not (= ?taker ?victim))
                       (alive ?taker)
                       (free ?taker)
                       (at ?taker ?place)
                       (alive ?victim)
                       (not (free ?victim))
                       (at ?victim ?place)
                       (has ?victim ?item))
    :effect       (and (has ?taker ?item)
                       (not (has ?victim ?item))
                       (when (belongsto ?item ?victim)
                             (and (intends ?victim (has ?victim ?item))
                                  (forall (?s - person)
                                          (when (sheriff ?s)
                                                (intends ?s (and (at ?taker jailhouse)
                                                                 (not (free ?taker))
                                                                 (has ?victim ?item)
                                                                 (free ?victim))))))))
    :agents       (?taker))

  ; One character uses medicine to heal a sick character.
  (:action heal
    :parameters   (?healer - person ?patient - person ?sickness - sickness ?medicine - item ?place - place)
    :precondition (and (cures ?medicine ?sickness)
                       (alive ?healer)
                       (free ?healer)
                       (at ?healer ?place)
                       (has ?healer ?medicine)
                       (alive ?patient)
                       (at ?patient ?place)
                       (sick ?patient ?sickness))
    :effect       (and (not (sick ?patient ?sickness))
                       (not (has ?healer ?medicine)))
    :agents       (?healer ?patient)))