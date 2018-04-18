(ns example.config
  (:require
   [taoensso.timbre :as timbre
    :refer-macros [log  trace  debug  info  warn  error  fatal  report
                   logf tracef debugf infof warnf errorf fatalf reportf
                   spy get-env]]

   [macchiato.env :as config]
   [cljs.reader :as reader]
   ["fs" :as fs]
   [mount.core :refer [defstate]]))

(defstate env :start (config/env))

(defn read-edn [path]
  (reader/read-string (fs/readFileSync path "utf8")))

(defstate manifest
  :start (->> (read-edn "public/assets/app/js/manifest.edn")
              (map (juxt :module-id :output-name))
              (into {})))

(defn js-path
  [module]
  (str "/assets/app/js/" (get @manifest module)))
