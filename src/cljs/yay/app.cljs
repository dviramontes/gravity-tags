(ns yay.app
  (:require 
   [clojure.walk :as walk]
   ;; [cljsjs.d3 :as d3]
   [cljsjs.jquery :as $]
   [reagent.core :as reagent :refer [atom]]))

(enable-console-print!) ;; enable print at web inspector console

(def url 
  "http://feeds.delicious.com/v2/json/tags/dviramontes" ;; or 
  "tags.json")  

(def tags (atom {}))

(defn get-tags [self]
  (let [width 680

        height 500
        
        xoffset (/ width 3)
        
        yoffset (/ height 2)

        theme (.category20c (aget js/d3 "scale"))

        DOM (reagent/dom-node self)
        
        svg (.. js/d3
                (select DOM)
                (select "svg"))]

    (.done 

     (.ajax js/$ url #js {:crossDomain true :dataType "json"}) 
     
     (fn [json-data]
     
       (let [tags (.-tags json-data)

             t-freq (walk/keywordize-keys
                   (frequencies (js->clj tags :keywordize-keys true)))

             star (key (apply max-key val t-freq))
             
             star-offset (get t-freq star)

             tag-in-freq #((keyword %) t-freq)

             t0 (.now js/Date)]

         (.. svg      
             (attr "width" width)
             (attr "height" height)
             ;; (style "border" "1px red solid")
             (style "background-color" "black")
             (append "g")
             (attr "class" "solarSystem")
             (attr "transform" (str "translate(" xoffset ", " yoffset ")")))

         (.. js/d3
             (select "g.solarSystem")
             (selectAll "circle")
             (data tags (fn [d i] i))
             (enter)
             (append "circle")
             (attr "cy" 0)
             (attr "cx" (fn [d i] 
                          #_(print (+ (tag-in-freq d) i))
                          (if (= d (name star))
                            0
                            (+ (tag-in-freq d) i star-offset))))
             (attr "r" (fn [d i]                        
                         ((keyword d) t-freq)))
             (attr "fill" (fn [d i] (theme i
                                     #_(if (= (keyword d) star)
                                       10
                                       (tag-in-freq d))))))
         (.. js/d3
             (timer (fn []
                      (let [phi0 45
                            speed 0.001
                            delta (- (.now js/Date) t0)]

                        (do 
                          (print delta)
                          (.. js/d3
                              (select "g.solarSystem")
                              (selectAll "circle")
                              (attr "transform" 
                                    (fn [d i]
                                      (str "rotate(" (* delta speed) ")")))))
                        ))))
         )))))

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
     #_(for [tag @tags] 
         ^{:key tag} [:li (str (key tag)) ", " [:em (val tag)]])]))

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
    [:div.col-lg-12 [tags-component]]]])

(defn react-component []
  (reagent/create-class {:component-did-mount get-tags
                         :reagent-render parent-component}))

(defn init []
  (reagent/render-component [react-component]
                            (.getElementById js/document "mount")))
