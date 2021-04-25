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
    :caption ["Cattedrale di Santa Maria del Fiore (Florence)"]}
   {:path "paintings/painting-0008.jpeg"
    :caption ["Old man" "Picture by Kevin Kelly" "in Asia Grace"]}
   {:path "paintings/painting-0009.jpeg"
    :caption ["Trixie"]}
   {:path "paintings/painting-0010.jpeg"
    :caption ["Lilies"]}
   {:path "paintings/painting-0011.jpeg"
    :caption ["Reproduction" "John Singer Sargent" "\"Head of an Arab\", 1891"]}
   {:path "paintings/painting-0012.jpeg"
    :caption ["My grandmother, Marie Thérèse"]}
   {:path "paintings/painting-0013.jpeg"
    :caption ["My daughter, Penelope"]}
   {:path "paintings/painting-0014.jpeg"
    :caption ["Reproduction" "John Singer Sargent" "\"A Bedouin Arab\", 1891"]}])

(defn file->bytes
  [file]
  (with-open [in (io/input-stream file)
              out (ByteArrayOutputStream.)]
    (io/copy in out)
    (.toByteArray out)))

(defn ensure-images
  "Ensure the original and thumbnail images exist.

  Add the thumbnail bytes as Base64 string to each painting map."
  [paintings]
  (->> paintings
       (map (fn [painting]
              (let [original (io/file (:path painting))
                    thumbnail (io/file "./thumbnails" (.getName original))]
                (when (not (.exists original))
                  (throw (ex-info "Original not found." painting)))
                (when (not (.exists thumbnail))
                  (let [args [(.getPath original) "-resize" "200x200" (.getPath thumbnail)]
                        {:keys [exit err]} (apply sh "convert" args)]
                    (when (not (= 0 exit))
                      (throw (ex-info "Error generating the thumbnail."
                                      {:err err})))))
                (assoc painting :thumbnail-b64 (.encodeToString (Base64/getEncoder)
                                                                (file->bytes thumbnail))))))
       doall))

(defn publish
  []
  (html/html5
   {:lang "en"}
   [:head
    [:title "Benoit Fleury - Paintings"]
    [:meta {:name "description"
            :content "A gallery of Benoit Fleury's oil paintings."}]
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]
    [:style {:type "text/css"}
     (slurp (io/resource "styles.css"))]]
   [:body
    [:h1
     [:a {:href "http://benfle.com"}
      "BENOIT FLEURY"]]
    [:p {:class "subtitle"}
     "Paintings"]
    [:div {:id "paintings"}
     (->> paintings
          reverse
          ensure-images
          (map (fn [{:keys [path caption thumbnail-b64]}]
                 [:figure {:class "painting"}
                  [:a {:href path}
                   [:img {:src (str "data:image/jpeg;base64," thumbnail-b64)
                          :alt (str/join ", " caption)}
                    [:figcaption
                     (map (fn [line]
                            [:span {:class "line"} line])
                          caption)]]]])))]
    [:script
     "(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
      m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
      })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
      ga('create', 'UA-44073995-1', 'benfle.com');
      ga('send', 'pageview');"]]))

(defn -main
  [& args]
  (spit "index.html" (publish)))
