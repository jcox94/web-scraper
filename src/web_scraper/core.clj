(ns web-scraper.core
  (:import [org.jsoup Jsoup])
  (:require [clojure.string :as str])
  (:gen-class))

(def page
  (.get
    (Jsoup/connect "http://books.toscrape.com")))

(defn get-titles
  "Recieves page and pulls out html with title information"
  []
  (-> page
      (.select "article")
      (.select "h3")
      .html))

(defn parse-titles
  "Parses out titles received from get-titles"
  [html]
  (let [unparsed-titles
        (re-seq #"title.*" html)]
    (map
      (comp
        #(subs % 1 (- (count %) 1))
        #(re-find #"\".*\"" %))
      unparsed-titles)))

(defn get-cost
  "Recieves page and pulls out text with cost information"
  []
  (-> page
      (.select "article")
      (.select "p")
      (.text)))

(defn parse-cost
  "Parses out costs received from get-cost"
  [data]
  (let [costs
        (re-seq #"\d+.\d{2}" data)]
    (map bigdec costs)))

(defn get-ratings
  "Recieves page and pulls out html with rating information"
  []
  (let [html
        (-> page
            (.select "article")
            (.select "p"))]
    (filter #(.hasClass % "star-rating") html)))

(defn parse-ratings
  "Parses out ratings received from get-ratings"
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
  "Puts together the titles, cost, and ratings for each book into
  the book-info map, and puts them all into a list"
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

(defn filter-books
  "filters books based on a list of options"
  [args]
  (loop [options args
         books (map-book-data)]
    (if (= options '())
      books
      (let [[key num] (take 2 options)
            option (keyword key)
            amount (bigdec num)]
          (recur
            (drop 2 options)
            (filter
              #(>= (option %) amount)
              books))))))


(defn print-books
  [books]
  (doall
    (map
      #(println
         (str (:title %) "\n\t cost: $" (:cost %) "\n\t rating: " (:rating %)))
      books)))

(defn -main
  [& args]
  (if args
    (print-books (filter-books args))
    (print-books (map-book-data))))












