;;;
;;; Rancher Hank responds to his son being bitten by a snake.
;;; Created by Stephen G. Ware
;;; Originally used for validating CPOCL narrative structure
;;;
(define (problem robbery)
  (:domain western)
  ; All the people, places, and things in the problem
  (:objects
     ; A saloon
     saloon - place
     ; Hank's ranch
     ranch - place
     ; The town general store
     generalstore - place
     ; Hank, a cattle rancher
     hank - person
     ; Timmy, Hank's son.
     timmy - person
     ; Will, the sheriff
     will - person
     ; Carl, the shopkeeper.
     carl - person
     ; Antivenom to cure a snakebite
     antivenom - item)
  ; Initial state of the world
  (:init
     ; Hank lives on his ranch and loves his son.
     (alive hank)
     (free hank)
     (at hank ranch)
     (loves hank timmy)
     ; Timmy is Hank's son, and also lives at the ranch.
     (alive timmy)
     (free timmy)
     (at timmy ranch)
     (loves timmy hank)
     ; Will is the sheriff.  He is waiting at the saloon, plotting the downfall of all lawbreakers.
     (alive will)
     (free will)
     (at will saloon)
     ; Carl is the manager of the town general store.
     (alive carl)
     (free carl)
     (at carl generalstore)
     (has carl antivenom)
     ; Antivenom cures a snakebite.
     (cures antivenom snakebite))
  ; Goal state: Timmy is dead and Hank is tied up.
  (:goal (and (not (alive timmy))
              (not (free hank)))))