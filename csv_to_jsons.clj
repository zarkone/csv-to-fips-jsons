(ns csv-to-jsons.csv-to-jsons
  (:require [babashka.curl :as curl]
            [cheshire.core :as cheshire]
            [clojure.data.csv :refer [read-csv]]))

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map keyword)    ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn cast-ints [csv-maps]
  (map (fn [csv-record]
         (reduce (fn [res k]
                   (update res k #(try
                                    (Long/parseLong %)
                                    (catch Exception e
                                      (:Combined_Key res)))))
                 csv-record
                 [:FIPS
                  :Deaths
                  :Recovered
                  :Confirmed
                  :Active]))
       csv-maps))

(defn main []
  (let [csv-maps (->  "./today.csv" slurp read-csv csv-data->maps cast-ints)]
    (println "CSV has " (count csv-maps) "records.")
    (doseq [csv-map csv-maps]
      (let [fips (:FIPS csv-map)
            json-filename (format "./out/%05d.json" fips)]
        (println "Writing " json-filename )
        (->> csv-map
             (cheshire/generate-string)
             (spit json-filename))))
    (println "Done.")))

(main)
