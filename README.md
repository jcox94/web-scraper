A web scraper written in Clojure that pulls information from http://books.toscrape.com/.

To run using command line: java -jar web-scraper-0.1.jar

Can filter books on cost and rating. Example: java -jar web-scraper-0.1.jar cost 45 rating 3
  *This will only include books with a cost of $45 or more and a rating of 3 stars or more.
