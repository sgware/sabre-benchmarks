;;;
;;; A fair maiden is faced with two marriage proposals.
;;; Created by Stephen G. Ware
;;; Originally used for validating CPOCL narrative structure
;;;
(define (problem marriage)
  (:domain fantasy)
  (:objects ;; People
            talia - person
            rory - person
            vince - person
            gargax - monster
            ;; Places
            village - place
            cave - place
            ;; Things
            money - valuable
            treasure - valuable)
  (:init (alive talia)
         (at talia village)
         (single talia)
         (loves talia rory)
         (alive vince)
         (at vince village)
         (has vince money)
         (rich vince)
         (single vince)
         (loves vince talia)
         (alive rory)
         (at rory village)
         (single rory)
         (loves rory talia)
         (alive gargax)
         (at gargax cave)
         (at treasure cave)
         (belongsto treasure gargax)
         (rich gargax)
         (intends talia (alive talia))
         (intends talia (rich talia))
         (intends talia (happy talia))
         (intends vince (alive vince))
         (intends vince (rich vince))
         (intends vince (happy vince))
         (intends rory (alive rory))
         (intends rory (happy rory))
         (intends gargax (alive gargax))
         (intends gargax (rich gargax)))
  (:goal (and (happy talia)
              (rich talia)
              (alive vince))))