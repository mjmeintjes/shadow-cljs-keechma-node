(ns example.client.ui.main
  (:require
   [keechma.ui-component :as ui]
   [keechma.toolbox.ui :refer [sub> <cmd route>]]))

(defn render [ctx]
  (let [route (route> ctx)]
    [:div
     [:h1 "Hello world"]
     (str route)]))

(def component (ui/constructor {:renderer render}))
