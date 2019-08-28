(ns scraper.main
    (:require [scraper.scraping :as scraping])
    (:require [yaml.core :as yaml])
    (:require [clj-http.client :as client])
    (:require [net.cgrand.enlive-html :as html]))

(defn print-items [items]
  (doseq [item items]
    (doseq [field item]
      (println field))))

(def config
  (scraping/parse-scraper (yaml/from-file "templates/amazon-co-uk.yml")))

(defn page [search-term number]
  (print-items (scraping/scrape-page search-term number config)))

; uncomment to try manual scraping from repl
;(def base-url "https://www.amazon.co.uk/s?k=$SEARCH_TERM&page=$PAGE")
;(defn fetch-url [url] (html/html-snippet (:body (client/get url))))
;(def html (fetch-url base-url))
;(def items (html/select html (:items scraper)))
;(def item (first items))
;(def scrape-and-print (print-items (scraping/scrape-items html scraper)))
