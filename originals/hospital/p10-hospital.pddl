
(define (problem p10-hospital)

(:objects 
	smith robinson jones - patient
  young ross - patient
  dixon lindsay gregory - patient

	mceldry - nurse
	newman - family 
	hathaway hendry brown green  - doctor
  
  theIntern - intern

	admissions - medicalLoc
	patientrooma patientroomb patientroomc - medicalLoc
  patientroomd patientroome patientroomd - medicalLoc
  lounge canteen - location

	symptoma symptomb symptomc - symptom
	treatmenta treatmentb treatmentc - treatment
  zero one two three - level
)

(:init
  (the-doctor green)

  (alive theIntern)
  (at theIntern canteen)

  (alive jones)
  (has-symptom jones symptoma)
 
  (alive ross)
  (has-symptom ross symptoma)

  (alive young)
  (has-symptom young symptomb)
  
  (alive smith)
  (has-symptom smith symptomb)
  
  (alive robinson)
  (has-symptom robinson symptomb)

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
  
  (at jones admissions)
  (alive hathaway)
  (at hathaway admissions) 
  
  (at robinson admissions)
  (at smith admissions)
  
  (conscious jones)
  (conscious robinson)
  (conscious smith)
  
  (alive hendry)
  (at hendry canteen)
  (alive green)
  (at green lounge)
  (alive brown)
  (at brown lounge)
  
  (connected zero one)
  (connected one two)
  (connected two three)
  (level-of-work hathaway zero)
  (level-of-work theIntern zero)
  
  (level-of-work green zero)
  (level-of-work hendry two)
  (level-of-work brown zero)
)

(:goal (and
    (the-doctor-mistake)
))
)


