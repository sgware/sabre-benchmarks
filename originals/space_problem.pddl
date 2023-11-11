;;;
;;; An intergalactic explorer encounters environmental and sentient hazards
;;; Created by Stephen G. Ware
;;;
(define (problem explore)
  (:domain space)
  (:objects ;; People
            zoe - creature
            lizard - creature
            ;; Places
            ship - ship
            cave - place
            surface - landform)
  (:init (habitable ship)
         (safe ship)
         (habitable surface)
         (safe surface)
         (habitable cave)
         (safe cave)
         (alive zoe)
         (safe zoe)
         (at zoe ship)
         (captain zoe ship)
         (alive lizard)
         (safe lizard)
         (at lizard cave)
         (guardian lizard surface)
         (intends zoe (friends zoe lizard))
         (intends zoe (safe zoe))
         (intends zoe (alive zoe))
         (intends lizard (safe lizard))
         (intends lizard (alive lizard)))
  (:goal (not (habitable surface))))