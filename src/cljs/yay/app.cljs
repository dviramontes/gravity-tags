(ns yay.app
  (:require 
   [clojure.walk :as walk]
   ;; [cljsjs.d3 :as d3]
   ;; [cljsjs.jquery :as jquery]
   [reagent.core :as reagent :refer [atom]]))

(enable-console-print!) ;; enable print at web inspector console

(def url 
  ;; "http://feeds.delicious.com/v2/json/tags/dviramontes"
  "tags.json"
  )  

(def tags (atom {}))


(defn get-tags [self]
  (let [width 680
        height 500
        xoffset (/ width 2)
        yoffset (/ height 2)
        theme (.category20c (aget js/d3 "scale"))
        DOM (reagent/dom-node self)
        
        svg (.. js/d3
                (select DOM)
                (select "svg"))]
    (.done 
     (.ajax js/$ url #js {:crossDomain true :dataType "json"}) 
     (fn [x]
       let [tags (walk/keywordize-keys
                  (frequencies (:tags (js->clj x :keywordize-keys true))))]
       (.. svg      
           (attr "width" width)
           (attr "height" height)
           (style "border" "1px red solid")
           (style "background-color" "white")
           (append "g")
           (attr "class" "solarSystem")
           (attr "transform" (str "translate(" xoffset ", " yoffset ")")))

       (.. js/d3
           (select ".solarSystem")
           (selectAll "circle")
           (data (array 1 2 3 4 5))
           (enter)
           (append "circle")
           (attr "cy" 100)
           (attr "cx" (fn [d i] (* 20 d)))
           (attr "r" (fn [d i] 
                       (print d)
                       (* 10 d)))
           (attr "fill" (fn [d i] (theme i))))))
       ))

(defn info-component []
  [:div.navbar
   [:h1 {:style {:color "white" :padding "2.25em" 
                 :background "white" 
                 :background-image "url(img/header-bg.png)"
                 :background-repeat "repeat-y"
                 :background-position "left"
                 }}]
   [:ul.list-inline
    [:li [:h4 "David Viramontes"]]
    [:li [:span {:style {:color "pink"}} [:b " @ "]]
     [:li [:span "linkedin"]]
     [:li [:span "github"]]
     [:li [:span "twitter"]]
     [:li [:span "ello"]]
     [:li [:span "behance"]]
     [:li [:span "tumblr"]]
     [:li [:span "soundcloud"]]]]
   [:hr]])

(defn tags-component []
  (fn []
    [:ul.list-unstyled
     (let [star (when-not (empty? @tags)
                  (key (apply max-key val @tags)))]      
       (for [tag @tags] 
         ^{:key tag} [:li (str (key tag)) ", " [:em (val tag)]]))]))

(defn d3-component []
  (fn []
    [:div.parent
     [:p.text-center "d3 component"]
     [:div#viz [:svg]]]))

(defn parent-component []
  [:div.container
   [:div.row 
    [:div.col-lg-12 [info-component]]
    [:div.col-lg-12 [d3-component]]
    [:div.col-lg-12 [tags-component]]
    ]])

(defn react-component []
  (reagent/create-class {:component-did-mount get-tags
                         :reagent-render parent-component}))

(defn init []
  (reagent/render-component [react-component]
                            (.getElementById js/document "mount")))
