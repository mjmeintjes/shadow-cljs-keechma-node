(ns example.core
  (:require
   [taoensso.timbre :as timbre
    :refer-macros [log  trace  debug  info  warn  error  fatal  report
                   logf tracef debugf infof warnf errorf fatalf reportf
                   spy get-env]]
   [taoensso.tufte.timbre :as tufte-timbre]
   [example.config :refer [env]]
   [macchiato.middleware.defaults :as defaults]
   [example.routes :refer [router] :as routes]
   [macchiato.server :as http]
   [mount.core :as mount :refer [defstate]]))

(mount/in-cljc-mode)

(defstate server
  :start   (let [host (or (:host @env) "0.0.0.0")
                 port (or (some-> @env :port js/parseInt) 3001)]
             (http/start
              {:handler    (-> router
                               routes/wrap-promise
                               routes/wrap-config
                               (defaults/wrap-defaults (assoc-in defaults/site-defaults
                                                                 [:security :anti-forgery]
                                                                 false)))
               :host       host
               :port       port
               :cookies {:signed? false}
               :on-success #(info "example started on" host ":" port)}))
  :stop (do
          (println "CLOSING SERVER")
          (.close @server
                  (fn []))))

(defn start
  "Hook to start. Also used as a hook for hot code reload."
  []
  (js/console.warn "start called")
  (mount/start))

(defn stop
  "Hot code reload hook to shut down resources so hot code reload can work"
  [done]
  (js/console.warn "stop called")
  (mount/stop)
  ;; not really done, as server might still be closing
  (done))

(defn main
  []
  (info "STARTING NODE APP")
  (start))

(tufte-timbre/add-timbre-logging-handler! {})

(timbre/merge-config!
 {:appenders {}})

(timbre/set-level! (get @env :log-level :debug))
