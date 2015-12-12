(ns yay.app
  (:require
    [clojure.walk :as walk]
    ;; [cljsjs.d3 :as d3]
    [cljsjs.jquery :as $]
    [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(def url
  ;; "http://feeds.delicious.com/av2/json/tags/dviramontes"
  "tags.json")

(def tags (atom {}))

(defn get-tags [self]
      (let [width (.-innerWidth js/window)

            height (.-innerHeight js/window)

            xoffset (/ width 5)

            yoffset (/ height 2)

            theme (.category20 (aget js/d3 "scale"))

            DOM (reagent/dom-node self)

            svg (.. js/d3
                    (select DOM)
                    (select "svg"))
            t0 (.now js/Date)]

           (.done

             (.ajax js/$ url #js {:crossDomain true :dataType "json"})

             (fn [json-data]

                 (let [tags-prop (.-tags json-data)

                       t-freq (walk/keywordize-keys
                                (frequencies (js->clj tags-prop :keywordize-keys true)))

                       star (key (apply max-key val t-freq))

                       star-offset (get t-freq star)

                       tag-in-freq #((keyword %) t-freq)]

                      (reset! tags t-freq)

                      (println (str "your star is : " (name star)))

                      (.. svg
                          (attr "width" width)
                          (attr "height" height)
                          (style "border" "1px grey solid")
                          (style "background-color" "black")
                          (append "g")
                          (attr "class" "solarSystem")
                          (attr "transform" (str "translate(" xoffset ", " yoffset ")")))

                      (.. js/d3
                          (select "g.solarSystem")
                          (selectAll "circle")
                          (data tags-prop (fn [d i] i))
                          (enter)
                          (append "circle")
                          (attr "cy" 0)
                          (attr "cx" (fn [d i]
                                         (if (= d (name star))
                                           0
                                           (+ (tag-in-freq d) i star-offset))))
                          (attr "r" (fn [d i]
                                        ((keyword d) t-freq)))
                          (attr "fill" (fn [d i] (theme i
                                                        (if (= (keyword d) star)
                                                          10
                                                          (tag-in-freq d))))))
                      (.. js/d3
                          (timer (fn []
                                     (let [phi0 45
                                           speed 0.01
                                           delta (- (.now js/Date) t0)]

                                          (.. js/d3
                                              (select "g.solarSystem")
                                              (selectAll "circle")
                                              (attr "transform"
                                                    (fn [d i]
                                                        (str "rotate(" (* t0 speed i) ")")))))))))))))

(defn tags-component []
      [:ul.list-unstyled
       (for [tag (reverse (sort-by second @tags))]
            ^{:key tag} [:li [:span.badge (str (key tag)) " " [:em (val tag)]]])])

(defn info-component []
      [:div.navbar.jumbotron
       [:ul.list-inline.text-center
        [:li [:h2 [:span.title-badge "Gravity Bookmarks"]]]
        [:li.pull-right {:style {:color "white"}}
         [:a {:href "https://github.com/dviramontes/gravity-bookmarks"}
          [:span.mega-octicon.octicon-octoface]]]]
       [:hr]
       [tags-component]])


(defn d3-component []
      [:div.parent
       [:div#viz [:svg]]])

(defn parent-component []
      [:div
       [:div.container-fluid
        [d3-component]]
       [:div.container
        [:div.row
         [:div.col-lg-12 [info-component]]]]])

(defn react-component []
      (reagent/create-class
        {:component-did-mount get-tags
         :reagent-render      parent-component}))

(defn init []
      (reagent/render-component [react-component]
                                (.getElementById js/document "mount")))
