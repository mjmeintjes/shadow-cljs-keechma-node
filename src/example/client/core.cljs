(ns ^:figwheel-always example.client.core
  (:require
   [taoensso.timbre :as timbre
    :refer-macros [log  trace  debug  info  warn  error  fatal  report
                   logf tracef debugf infof warnf errorf fatalf reportf
                   spy get-env]]

   [keechma.app-state :as app-state]

   [goog.object :as go]
   [example.client.app :as app]))

(go/set js/window "onerror"
        (fn [msg url line-no column-no err]
          (error (js->clj [msg url line-no column-no err]))))

(def app-definition
  (merge app/definition
         {:html-element  (.getElementById js/document "app")}))

(defonce running-app (atom nil))

(defn start-app! []
  (let [app-state (update (app-state/deserialize-app-state {} (.-STATE js/window))
                          :app-db
                          (fn [app-db]
                            (-> (dissoc app-db :internal :cookies :request))))]
    (reset! running-app (app-state/start!
                         (assoc app-definition :initial-data app-state)))))

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (info "dev mode")))


(defn reload []
  (let [current @running-app]
    (info "(re)loading")
    (when current
      (info "STOPPING AND STARTING")
      (app-state/stop! current start-app!)
      (start-app!))))

(defn stop [_])

(defn ^:export main []
  (dev-setup)
  (start-app!))

(timbre/merge-config!
 {:appenders
  {}})
