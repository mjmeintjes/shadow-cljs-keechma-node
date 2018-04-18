(ns example.client.ui
  (:require [example.client.ui.main :as main]))


(def ui
  {:main (assoc main/component :topic :steps)})
