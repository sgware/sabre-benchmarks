
(define (domain domain-hospital)

(:requirements :typing :equality :negative-preconditions)

(:types         
	location - object
	emergencyLoc generalLoc staffLoc medicalLoc exteriorLoc - location
	drugLoc familyLoc - location
	character - object
	medicalcharacter patient family - character
	doctor - medicalcharacter
	nurse - medicalcharacter
  intern - character
	symptom - object
	treatment - object
  level - object
)

(:predicates
  (the-doctor ?c - character)
  (the-doctor-treating ?c - character)
  (the-doctor-overworked)
  (the-doctor-mistake)
	(has-symptom ?p - patient ?s - symptom)
	(conscious ?p)
  (unconscious ?p)
	(alive ?p)
	(patient-room ?p - patient ?l - medicalLoc)
	(occupied ?l - medicalLoc)
	(at ?c - character ?l - location)
	(treatment-for ?t - treatment ?s - symptom)
	(male ?c - character)
	(female ?c - character)
	(hospitalised ?p - character)
	(wrong-treatment-prescribed ?p - patient ?t - treatment)
	(treatment-prescribed ?p - patient ?t - treatment)
	(receiving-treatment ?p - patient ?t - treatment)
  (level-of-work ?d - character ?l - level)
  (connected ?l1 ?l2 - level)
)

(:action admit
  :parameters (?d - doctor ?p - patient ?l - medicalLoc ?w1 ?w2 - level)
  :precondition (and
    (alive ?d)
		(alive ?p)
		(at ?p ?l)
		(at ?d ?l)
		(not (occupied ?l))
		(not (hospitalised ?p))
    (level-of-work ?d ?w1)
    (connected ?w1 ?w2)
    (the-doctor ?d)
	)
  :effect (and
    (the-doctor-treating ?p)
		(hospitalised ?p)
		(patient-room ?p ?l)
		(occupied ?l)
    (not (level-of-work ?d ?w1))
    (level-of-work ?d ?w2)
	)
)

(:action walk 
    :parameters (?c - character ?l1 ?l2 - location)
    :precondition (and
      (at ?c ?l1)
      (not (hospitalised ?c))
      )
    :effect (and
      (at ?c ?l2)
      (not (at ?c ?l1))
      )
    )

(:action move 
    :parameters (?c - patient ?l1 ?l2 - location)
    :precondition (and
      (at ?c ?l1)
      )
    :effect (and
      (at ?c ?l2)
      (not (at ?c ?l1))
      )
    )

(:action assess
  :parameters (?d - doctor ?p - patient ?s - symptom ?t - treatment ?l - medicalLoc ?w - level)
  :precondition (and
    (level-of-work ?d ?w)
    (not (= ?w three))
		(alive ?p)
    (the-doctor ?d)
    (the-doctor-treating ?p)
		(at ?p ?l)
		(at ?d ?l)
		(patient-room ?p ?l)
		(has-symptom ?p ?s)
		(treatment-for ?t ?s)
	)
  :effect (and
		(treatment-prescribed ?p ?t)
	)
)

(:action get-work-help 
  :parameters (?d - doctor ?l - level)
  :precondition (and
    (level-of-work ?d ?l)
    (not (= ?l zero))
    )
  :effect (and
    (not (level-of-work ?d ?l))
    (level-of-work ?d zero)
    (not (the-doctor-overworked))
    ))

(:action mistake-assess
  :parameters (?d - doctor ?p - patient ?s - symptom ?t - treatment ?l - medicalLoc ?w - level)
  :precondition (and
    (level-of-work ?d ?w)
    (= ?w three)
		(alive ?p)
    (the-doctor ?d)
    (the-doctor-treating ?p)
		(at ?p ?l)
		(at ?d ?l)
		(has-symptom ?p ?s)
		(treatment-for ?t ?s)
    (not (receiving-treatment ?p ?t))
	)
  :effect (and
		(wrong-treatment-prescribed ?p ?t)
    (the-doctor-overworked)
    (the-doctor-mistake)
	)
)

(:action treat
  :parameters (?d - doctor ?p - patient ?t - treatment ?l - medicalLoc)
  :precondition (and
		(treatment-prescribed ?p ?t)
		(alive ?p)
    (the-doctor ?d)
    (the-doctor-treating ?p)
		(at ?p ?l)
		(at ?d ?l)
		(patient-room ?p ?l)
    (not (receiving-treatment ?p ?t))
	)
  :effect (and
		(not (treatment-prescribed ?p ?t))
		(receiving-treatment ?p ?t)
	)
)

(:action recover
     :parameters (?p - patient ?t - treatment ?s - symptom ?l - medicalLoc)
     :precondition
	(and
		(receiving-treatment ?p ?t)
		(alive ?p)
		(at ?p ?l)
		(patient-room ?p ?l)
		(has-symptom ?p ?s)
	)
     :effect
       (and
		(not (has-symptom ?p ?s))
	)
)

(:action die
     :parameters (?p - patient ?t - treatment ?s - symptom ?l - medicalLoc)
     :precondition
	(and
		(receiving-treatment ?p ?t)
		(alive ?p)
		(at ?p ?l)
		(patient-room ?p ?l)
	)
     :effect
       (and
		(not (alive ?p))
	)
)

(:action lose-consciousness
  :parameters (?p - patient ?t - treatment ?s - symptom ?l - medicalLoc)
  :precondition (and
    (receiving-treatment ?p ?t)
		(conscious ?p)
		(at ?p ?l)
	)
  :effect (and
    (unconscious ?p)
		(not (conscious ?p))
	)
)

)

