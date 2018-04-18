(ns example.client.app
  "Shared between nodejs and browser"
  (:require
   [taoensso.timbre :as timbre
    :refer-macros [log  trace  debug  info  warn  error  fatal  report
                   logf tracef debugf infof warnf errorf fatalf reportf
                   spy get-env]]
   [example.client.ui :refer [ui]]
   [example.client.subscriptions :refer [subscriptions]]
   [example.client.controllers :refer [controllers]]))

(def definition
  {:components    ui
   :controllers   controllers
   :subscriptions subscriptions
   :router :history
   :routes [":category/:page"]})

(timbre/set-config!
 {:level (if js/goog.DEBUG :debug :info)
   :ns-whitelist  [] #_["my-app.foo-ns"]
   :ns-blacklist  [] #_["taoensso.*"]

   :middleware [] ; (fns [data]) -> ?data, applied left->right
   :output-fn timbre/default-output-fn ; (fn [data]) -> string

  :appenders
  {:println
   {:enabled?   ^boolean js/goog.DEBUG
    :async?     false
    :min-level  nil
    :output-fn  :inherit
    :fn
    (fn [data]
      (let [{:keys [output_]} data
            formatted-output-str (force output_)]
        (println formatted-output-str)))}}})
