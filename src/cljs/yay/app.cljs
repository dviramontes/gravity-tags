(ns yay.app
  (:require 
   [clojure.walk :as walk]
   [reagent.core :as reagent :refer [atom]]))

(enable-console-print!) ;; enable print at web inspector console

(defonce 
  ;; "http://feeds.delicious.com/v2/json/tags/dviramontes"
  ;; delicious api is currently down
  url "tags.json")  

(def tags (atom {}))

(defn get-tags []
  (.done 
   (.ajax js/$ url (clj->js {:crossDomain false 
                             :dataType "json"
                             })) 
   (fn [x] 
     (reset! tags (walk/keywordize-keys 
                   (frequencies (:tags (js->clj x :keywordize-keys true))))))))

(defn info-component []
  [:div.navbar
   [:h1 {:style {:color "white" :padding "2.25em" 
                 :background "white" 
                 :background-image "url(img/header-bg.png)"
                 :background-repeat "repeat-y"
                 :background-position "left"
                 }}
    ;;(take 400 (cycle "//"))
    ]
   [:ul.list-inline
    [:li [:h4 "David Viramontes"]]
    [:li [:span {:style {:color "pink"}} [:b " @ "]]
     [:li [:span "linkedin"]]
     [:li [:span "github"]]
     [:li [:span "twitter"]]
     [:li [:span "behance"]]
     [:li [:span "tumblr"]]
     [:li [:span "soundcloud"]]]]
   [:hr]])

(defn tags-component []
  (fn []
    [:ul.list-unstyled

     #_(-> val
        (map @tags)
        (#(apply max %))
        print)

     
     (for [tag @tags
           :let [x (when-not (empty? @tags)
                 (key (apply max-key val @tags)))]] 
 
       ^{:key tag} [:li (str (key tag)) ", " [:em (val tag)]])]))

(defn parent-component []
  [:div.container
   [:div.row 
    [:div.col-lg-12 [info-component]]
    [:div.col-lg-12 [tags-component]]]])

(defn react-component []
  (reagent/create-class {
                         :component-will-mount get-tags
                         :reagent-render parent-component
                         }))

(defn init []
  (reagent/render-component [react-component]
                            (.getElementById js/document "mount")))
