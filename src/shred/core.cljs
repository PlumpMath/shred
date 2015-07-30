(ns ^:figwheel-always shred.core
  (:require
    [reagent.core :as reagent :refer [atom]]
    [re-frame.core :refer [register-handler path register-sub dispatch subscribe]]
    [re-com.core :refer [h-box v-box box gap line border label title alert-box input-text button p]
     :refer-macros [handler-fn]]
    [re-com.util :refer [get-element-by-id item-for-id]]
    ))

;; TODO: Update the app-state from the browser.
;; -- Done: Create a form that accepts text inputs
;; -- Figure out how to grab the text in the inputs
;; -- Figure out how to line up the input text boxes with re-com
;; -- Figure out how to update the app-state atom with the text grabbed from the inputs

;; TODO: Design the exercise player
;; TODO: Design the session design interface
;; TODO: Make the Content area center justified

;; TODO: Choose shortcuts for structural movement keys (intellij)
;; TODO: Add macro for select defun and reformat (intellij)

;; Done: Add exercise data directly to app-state and then retrieve it from a component
;; Done: Figure out how to get 2 or more Jtab components on same page
;; Done: Figure out how to get the BPM of the exercise on the title line right justified.
;; Done: Manage todos in Intellij?
;; Done: Figure out how to render multiple "exercises" in one "session"

(enable-console-print!)

;; (println "Edits to this text should show up in your developer console.")
;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom
                     {:ex1 {:jtid "ex1"
                            :name "Exercise 1"
                            :desc "Alternate pick 1st and 2nd finger to the 12th fret and back. Repeat 10 times."
                            :tab  "C / / Bm"
                            :bpm  "65"}
                      :ex2 {:jtid "ex2"
                            :name "Exercise 2"
                            :desc "Alternate pick 1st and 3nd finger to the 12th fret and back. Repeat 10 times."
                            :tab  "$1 1 3 1 3 2 4 2 4 3 5 3 5 4 6 4 6 5 7 5 7 6 8 6 8"
                            :bpm  "65"}}
                     ))

(defn jtab-component
  [id tab]
  (let [tabdiv (str "div#" id)]
    (reagent/create-class
      {:component-did-mount (fn [] (.render js/jtab tabdiv tab))
       :reagent-render      (fn [] [tabdiv])
       :display-name        "jtab-component"
       })))

(defn exercise
  "jtid is the ID of the div where the tab will be displayed"
  ;[jtid name desc tab bpm]
  [exinfo]
  (let [jtid (:jtid exinfo)
        name (:name exinfo)
        desc (:desc exinfo)
        tab (:tab exinfo)
        bpm (:bpm exinfo)]
    [v-box
     :size "auto"
     :gap "8px"
     :children
     [[h-box
       :size "auto"
       :children [
                  [title
                   :label (str name ":")
                   :level :level2]
                  [box
                   :size "auto"
                   :justify :end
                   :padding "0px 20px 0px 0px"
                   :child [title
                           :label (str bpm " BPM")
                           :level :level2]]]]
      [label :label desc]
      [jtab-component jtid tab]]]))

(defn session
  []
  [v-box
   :size "auto"
   :margin "0px 0px 0px 10px"
   :children [
              [title
               :label "Tues May 19th, 2015"
               :level :level1
               :underline? true]
              [exercise (get-in @app-state [:ex1])]
              [exercise (get-in @app-state [:ex2])]
              ]])

(defn labeled-input
  [name default]
  (let [text-val (atom "")]
    [h-box
     :size "auto"
     :children [
                [label :label name]
                [gap :size "10px"]
                [input-text
                 :model default
                 :on-change #(reset! text-val %)]]]))

(defn tooltip-info
  []
  (fn
    []
    [v-box
     :children [
                [:p.info-heading "Hello."]
                [:code
                 "This is code. Hear me roar"]
                ]]))


(defn test-layout
  []
  (let [name-val (reagent/atom "")
        desc-val (reagent/atom "")
        tab-val (reagent/atom "")
        bpm-val (reagent/atom "")
        map-val (reagent/atom {:jtid "" :name "" :desc "" :tab "" :bpm ""})
        tooltip-info [v-box
                      :children [
                                 [:p.info-heading "Hello."]
                                 [:code "This is code"]
                                 [:code "Hear me roar"]]]]
    (fn
      []
      [v-box
       :size "auto"
       :width "600px"
       :children [
                  [box
                   ;:size "auto"
                   :child "Text Input test:"]
                  [gap :size "10px"]
                  [input-text
                   :model name-val
                   :on-change #(reset! name-val %)
                   :placeholder "Exercise name "]
                  [input-text
                   :model desc-val
                   :on-change #(reset! desc-val %)
                   :placeholder "Exercise description "]
                  [input-text
                   :model tab-val
                   :on-change #(reset! tab-val %)
                   :placeholder "Exercise tab "]
                  [input-text
                   :model bpm-val
                   :on-change #(reset! bpm-val %)
                   :placeholder "Exercise bpm "]
                  [gap :size "10px"]
                  [button
                   :label "Submit"
                   :tooltip tooltip-info
                   :tooltip-position :right-center
                   :on-click #(reset! map-val
                                      {:jtid "ex3"
                                       :name (if @name-val @name-val "nil")
                                       :desc (if @desc-val @desc-val "nil")
                                       :tab  (if @tab-val @tab-val "nil")
                                       :bpm  (if @bpm-val @bpm-val "nil")})
                   :class "btn-default"]
                  [gap :size "10px"]
                  [h-box
                   :size "auto"
                   :children [
                              [p "Current value of map-val:  " (if @map-val @map-val "nil")]]]
                  ]
       ])))


(defn exercise-form
  ;; Todo: line up input boxes
  [form-name]
  (let [text-val (reagent/atom "")
        form-val (reagent/atom "")]
    [v-box
     :size "auto"
     :margin "0px 0px 0px 10px"
     :children [
                [title
                 :label form-name
                 :level :level2
                 :margin-bottom "0.5em"]
                [labeled-input "Exercise Name:" "Exercise 3"]
                [labeled-input "Exercise Desc:" "E.g Alternate pick 1st and 3nd finger to the 12th fret."]
                [labeled-input "Exercise Tab:" "The exercise in JTab format"]
                [labeled-input "Starting BPM:" "E.g. 63"]
                [button
                 :label "Submit"
                 :tooltip "Foo"
                 :tooltip-position :below-center
                 ;;:on-click          #(swap! app-state update-in [:ex3])
                 :on-click #(println "Foo")
                 :class "btn-default"]
                ]
     ]))

(defn layout []
  (let [mouse-over? (reagent/atom false)]
    (fn []
      [v-box
       :size "auto"
       :children [
                  [h-box
                   :size "auto"
                   :children [
                              ;[box
                              ; :size "200px"
                              ; :child "Nav"
                              ; :style (if @mouse-over? {:background-color "silver"}
                              ;                         {:background-color "#e8e8e8"})
                              ; :attr {:on-mouse-over (handler-fn (reset! mouse-over? true))
                              ;        :on-mouse-out  (handler-fn (reset! mouse-over? false))}]
                              [session]
                              ]
                   ]
                  [test-layout]
                  [exercise-form "Exercise Input"]
                  ;[box
                  ; :child "Footer"
                  ; ;;:style {:background-color "silver"}
                  ; ]
                  ]]
      )))

(reagent/render-component [test-layout]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  (swap! app-state update-in [:__figwheel_counter] inc)
  )
