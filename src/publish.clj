(ns publish
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            [hiccup.page :as html])
  (:import [java.util Base64]
           [java.io ByteArrayOutputStream]))

(def paintings
  [{:path "paintings/painting-0001.jpeg"
    :caption ["Reproduction" "John Singer Sargent" "\"An Out-of-Doors Study\", 1889"]}
   {:path "paintings/painting-0002.jpeg"
    :caption ["Reproduction" "Caravaggio" "\"Narcissus\", 1599"]}
   {:path "paintings/painting-0003.jpeg"
    :caption ["Reproduction" "Rembrandt van Rijn" "\"Self-Portrait\", 1659"]}
   {:path "paintings/painting-0004.jpeg"
    :caption ["Reproduction" "John Singer Sargent" "\"Carnation, Lily, Lily, Rose\", 1885"]}
   {:path "paintings/painting-0005.jpeg"
    :caption ["Reproduction" "John Singer Sargent" "\"The Fountain\", 1907"]}
   {:path "paintings/painting-0006.jpeg"
    :caption ["Reproduction" "John Singer Sargent" "\"Fishing for Oysters at Cancale\", 1878"]}
   {:path "paintings/painting-0007.jpeg"
    :caption ["Florence Cathedral"]}
   {:path "paintings/painting-0008.jpeg"
    :caption ["Old man" "Photograph by Kevin Kelly" "in Asia Grace"]}
   {:path "paintings/painting-0009.jpeg"
    :caption ["Trixie"]}
   {:path "paintings/painting-0010.jpeg"
    :caption ["Lilies"]}
   {:path "paintings/painting-0011.jpeg"
    :caption ["Reproduction" "John Singer Sargent" "\"Head of an Arab\", 1891"]}
   {:path "paintings/painting-0012.jpeg"
    :caption ["Marie Thérèse"]}
   {:path "paintings/painting-0013.jpeg"
    :caption ["Penelope"]}
   {:path "paintings/painting-0014.jpeg"
    :caption ["Reproduction" "John Singer Sargent" "\"A Bedouin Arab\", 1891"]}
   {:path "paintings/painting-0015.jpeg"
    :caption ["Lord Bertrand Russel" "Photograph by Yousuf Karsh, 1949"]}
   {:path "paintings/painting-0016.jpeg"
    :caption ["A portrait of the artist as a young man." "Photograph, 1986"]}
   {:path "paintings/painting-0017.jpeg"
    :caption ["Photograph by Amy Carroll, 2022"]}
   {:path "paintings/painting-0018.jpeg"
    :caption ["Rose"]}])

(defn publish
  []
  (let [root-url "https://benfle.com"]
    (html/html5
     {:lang "en"}
     [:head
      [:meta {:charset "UTF-8"}]
      [:meta {:name "viewport"
              :content "width=device-width, initial-scale=1"}]
      [:title "Benoît Fleury - Oil Paintings"]
      [:meta {:name "author"
              :content "Benoît Fleury"}]
      [:meta {:name "description"
              :content "A gallery of Benoit Fleury's oil paintings."}]
      [:style {:type "text/css"}
       (slurp (io/resource "styles.css"))]]
     [:body

      [:header
       [:h1
        [:a {:href root-url}
         "Benoît Fleury"]
        "Oil Paintings"]]

      [:article
       [:p
        "I started oil painting in 2018 thanks to "
        [:a {:href "https://www.drawmixpaint.com/"}
         "Mark Carder's Draw Mix Paint method"]
        "."]

       [:div#paintings
        (->> paintings
             reverse
             (map (fn [{:keys [path caption thumbnail-b64]}]
                    [:a {:href path}
                     [:figure {:class "painting"}
                      [:img {:src path
                             :alt (str/join ", " caption)}]
                      [:figcaption
                       (map (fn [line] [:span.line line])
                            caption)]]])))]]])))

(defn -main
  [& args]
  (spit "index.html"
        (publish)))
