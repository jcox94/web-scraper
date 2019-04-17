(ns web-scraper.core
  (:import [org.jsoup Jsoup])
  (:require [clojure.string :as str]))

(def page
  (.get
    (Jsoup/connect "http://books.toscrape.com")))

(defn get-titles
  []
  (-> page
      (.select "article")
      (.select "h3")
      .html))

(defn parse-titles
  [html]
  (let [unparsed-titles
        (re-seq #"title.*" html)]
    (map
      (comp
        #(subs % 1 (- (count %) 1))
        #(re-find #"\".*\"" %))
      unparsed-titles)))

(defn get-cost
  []
  (-> page
      (.select "article")
      (.select "p")
      (.text)))

(defn parse-cost
  [data]
  (let [costs
        (re-seq #"\d+.\d{2}" data)]
    (map bigdec costs)))

(defn get-ratings
  []
  (let [html
        (-> page
            (.select "article")
            (.select "p"))]
    (filter #(.hasClass % "star-rating") html)))

(defn parse-ratings
  [data]
  (map
    #(cond
       (str/includes? % "One") 1
       (str/includes? % "Two") 2
       (str/includes? % "Three") 3
       (str/includes? % "Four") 4
       (str/includes? % "Five") 5
       :else "No rating")
    data))

(defn book-info
  [title rating cost]
  {:title title
   :rating rating
   :cost cost})

(defn map-book-data
  []
  (let [titles
        (parse-titles
          (get-titles))
        costs
        (parse-cost
          (get-cost))
        ratings
        (parse-ratings
          (get-ratings))]
    (map book-info titles ratings costs)))






