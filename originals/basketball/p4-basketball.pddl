(define (problem problem-basketball)
 
(:requirements :adl :typing :negative-preconditions)

(:objects
	alice - citizen
	bob - citizen
	charlie - citizen
	david - citizen
	sherlock - detective
	lestrade - inspector
  acar bcar ccar dcar scar lcar - car
  tcar - car 
  theTrafficCop - trafficcop
  theBike - motorbike

	ahome - place
	bhome - place
	chome - place
	dhome - place
	basketcourt - place
	downtown - place
	vase - item
	basketball - item
	bat - item
	gun - item
	handcuff - item
	theft - crime
	murder - crime
)

(:init
	(alive alice) 
	(at alice downtown)
  (has-car alice acar)
  (working acar)

	(alive bob) 
	(at bob bhome)
  (has-car bob bcar)
  (working bcar) 

	(alive charlie) 
	(at charlie downtown)
  (has-car charlie ccar)
  (working ccar) 

	(alive david) 
	(at david dhome)
  (has-car david dcar)
  (working dcar) 

	(alive sherlock)
	(at sherlock downtown)
  (has-car sherlock scar)
  (working scar) 

	(alive lestrade)
	(at lestrade downtown)
  (has-car theTrafficCop theBike)
  (working theBike)

  (arrester sherlock)

  (alive theTrafficCop)
	(at theTrafficCop downtown)
  (has-car theTrafficCop tcar)
  (working tcar)

	(has charlie bat)
  (has bob gun)
  (has alice basketball)
  (has david basketball)
	
	(angry alice)
	(angry charlie)
	
	(murder-clue vase)
	(murder-clue bat)
	(murder-clue gun)

	(is-basketball-place basketcourt)
	(is-theft theft)
	(is-arrest-place downtown)
	(is-murder murder)
)
(:goal 	(and
	(under-arrest charlie)
))

)
