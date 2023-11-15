(define (problem p1-hospital)

(:objects 
	smith robinson jones - patient
  ross young - patient
  dixon lindsay gregory - patient

	mceldry - nurse
	newman - family 
	hathaway green hendry brown  - doctor

  theIntern - intern

	admissions - medicalLoc
	patientrooma patientroomb patientroomc - medicalLoc
  patientroomd patientroome patientroomd - medicalLoc
  lounge canteen - location

	symptoma symptomb symptomc symptomd - symptom
	treatmenta treatmentb treatmentc - treatment
  zero one two three - level
)

(:init
  (the-doctor hathaway)

  (alive theIntern)
  (at theIntern canteen)

  (alive jones)
  (has-symptom jones symptoma)

  (alive ross)
  (has-symptom ross symptoma)

  (alive young)
  (has-symptom young symptomb)

(alive dixon)
  (at dixon admissions)
  (has-symptom dixon symptoma)
  (alive lindsay)
  (at lindsay admissions)
  (has-symptom lindsay symptoma)
  (alive gregory)
  (at gregory admissions)
  (has-symptom gregory symptoma)

  (treatment-for treatmenta symptoma)
  (treatment-for treatmentb symptomb)
  (treatment-for treatmentc symptomc)

	(alive hathaway)
  (alive green)
  (alive hendry) 
  (alive  brown)

  (at jones patientrooma)
  (at hathaway patientrooma) 

  (level-of-work hathaway zero)
  (level-of-work green zero)
  (level-of-work hendry zero)
  (level-of-work brown zero)
  (level-of-work theIntern zero)

  (connected zero one)
  (connected one two)
  (connected two three)
)

(:goal (and
	(not (has-symptom jones symptoma))
))

)


