(ns scraper.main
    (:require [yaml.core :as yaml])
    (:require [clj-http.client :as client])
    (:require [net.cgrand.enlive-html :as html]))

(def ^:dynamic *base-url* 
  "https://www.amazon.co.uk/s?k=cardboard+boxes&crid=2GG8KOUJMLJRY&sprefix=cardboard%2Caps%2C143&ref=nb_sb_ss_i_1_9")

(defn fetch-url [url]
  (html/html-snippet (:body (client/get url))))

(def template (yaml/from-file "templates/amazon-co-uk.yml"))

(defn split-by-space [str]
  (clojure.string/split str #" "))

(defn parse-selector-word [str] ;; TODO handle tag.class (intersection) and other tag:function
  (let [[matches tag num] (re-find #"(.*):nth-child\((\d+)\)" str)]
    (if matches
      [(keyword tag) (html/nth-child (Integer/parseInt num))]
      (keyword str))))

(defn parse-extractor [str]
  (apply comp (reverse (map keyword (split-by-space str)))))

(defn parse-selector [selector-def]
  {:name (:name selector-def)
   :path (into [] (map parse-selector-word (split-by-space (:path selector-def))))
   :extractor (parse-extractor (:extractor selector-def))})

(def selectors
  (map parse-selector (:fields template)))

(defn scrape-field [html selector]
  (let [path (:path selector)
        extract (:extractor selector)]
    {(:name selector) (first (flatten (map extract (html/select html path))))}))

(defn scrape-item [item]
  (map #(scrape-field item %) selectors))


;; draft

(defn fetchit []
  (fetch-url *base-url*))

(def items (html/select (fetchit) [:.s-search-results :> :div]))

(def item (first items))

(defn parseit []
  (scrape-item item))
