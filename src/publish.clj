(ns publish
  (:require [clojure.edn :as edn]
            [hiccup.core :as html]))

(defn painting
  [{:keys [path caption]}]
  [:figure {:class "painting"}
   [:a {:href path}
    [:img {:src path}
     [:figcaption
      (map (fn [line]
             [:span {:class "line"} line])
           caption)]]]])

(defn publish
  []
  (html/html
   [:html
    [:head
     [:meta {:charset "UTF-8"}]
     [:meta {:name "viewport" :content "width=device-wdith, initial-scale=1"}]
     [:link {:rel "stylesheet" :href "styles.css"}]]
    [:body
     [:h1
      [:a {:href "http://benfle.com"}
       "Benoît Fleury"]]
     [:p {:class "subtitle"}
      "Paintings"]
     [:div {:id "paintings"}
      (map painting
           (reverse (:paintings (edn/read-string (slurp "paintings.edn")))))]]]))

(defn -main
  [& args]
  (spit "index.html" (publish)))
