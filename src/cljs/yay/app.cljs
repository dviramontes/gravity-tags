(ns yay.app
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!) ;; enable print at web inspector console

(def url "http://feeds.delicious.com/v2/json/tags/dviramontes")

(defn get-tags-from-delicious []
  (-> (.ajax js/$ url (clj->js {:crossDomain true 
                        :dataType "jsonp"})) 
      .success (fn [x]) (println x)))

(defn some-component []
  [:div
   [:h3 "I am a component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red"]
    " text."]])

(defn calling-component []
  [:div.container
     [:div.row 
      [:div.col-lg-12 [some-component]]]])

(defn init []
  (reagent/render-component [calling-component]
                            (.getElementById js/document "mount")))
