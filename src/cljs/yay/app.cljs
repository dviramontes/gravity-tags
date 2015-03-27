(ns yay.app
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!) ;; enable print at web inspector console

(defonce url "http://feeds.delicious.com/v2/json/tags/dviramontes")

(def tags (atom nil))

(defn get-tags []
  (.done 
   (.ajax js/$ url (clj->js {:crossDomain true 
                             :dataType "jsonp"})) 
   (fn [x] 
     ;;(print (.stringify js/JSON x))
     (reset! tags (js->clj x :keywordize-keys true)))))

(defn child-component []  
  (let []
    (fn []
      [:div.container
       [:div.col-lg-12
        [:h1 "imm.aterial.org"]
        [:p.name "David Viramontes" 
         [:span {:style {:color "pink"}} "@"]
         [:p  "---"]]]])))

(defn parent-component []
  [:div.container
   [:div.row 
    [:div.col-lg-12 [child-component]]]])

(defn react-component []
  (reagent/create-class {:reagent-render parent-component
                         :component-did-mount get-tags}))

(defn init []
  (reagent/render-component [react-component]
                            (.getElementById js/document "mount")))
