(ns example.client.reloading
  (:require
   [taoensso.timbre :as timbre
    :refer-macros [log  trace  debug  info  warn  error  fatal  report
                   logf tracef debugf infof warnf errorf fatalf reportf
                   spy get-env]]
   ;;[orchestra-cljs.spec.test :as st]
   [example.client.core :as core]))

(defn reload
  []
  (info "reloading/reload")
  ;;(st/instrument)
  (core/reload))
