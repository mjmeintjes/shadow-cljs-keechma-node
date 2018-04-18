(ns example.routes
  (:require
   [taoensso.timbre :as timbre
    :refer-macros [log  trace  debug  info  warn  error  fatal  report
                   logf tracef debugf infof warnf errorf fatalf reportf
                   spy get-env]]
   [keechma.ssr :as ssr]
   [example.client.app :refer [definition]]
   [bidi.bidi :as bidi]
   [hiccups.runtime :as hic]
   [macchiato.util.response :as r]
   [keechma.app-state :as app-state]
   [example.config :as config :refer [js-path]]
   [promesa.core :as p]
   [promesa.impl :refer [Promise]]
   [goog.object :as go])
  (:import goog.date.DateTime
           goog.date.Interval))


(defn script
  [s]
  [:script
   [:script
    {:dangerously-set-inner-HTML {:__html s}}]])

(defn app-html
  [app]
  [:div
   {:dangerously-set-inner-HTML {:__html (:html app)}}])

(defn default-route
  [req res raise]
  (ssr/render (assoc definition
                     :app-db
                     (app-state/app-db {:request (select-keys req [:url :cookies :headers])}))
              (:url req)
              (fn [app]
                (-> (hic/render-html
                     [:html
                      [:head
                       [:link
                        {:rel "icon"
                         :href "data:;base64,iVBORw0KGgo="}]
                       [:title
                        "Keechma, shadow-cljs and macchiato"]
                       [:meta
                        {:name "viewport"
                         :content "width=device-width, initial-scale=1"}]
                       [:link
                        {:rel "stylesheet"
                         :href "https://unpkg.com/tachyons@4.9.1/css/tachyons.min.css"}]]
                      [:body
                       [:div#app
                        [:div (app-html app)]]
                       (script (str "window.STATE = " (.stringify js/JSON (:app-state app))))
                       [:script {:src (js-path :main)}]
                       (script "example.client.core.main();")]])
                    (r/ok)
                    (r/content-type "text/html")
                    (res)))))

(defn wrap-config
  [handler]
  (fn [req res raise]
    (handler (assoc req :config/env @config/env)
             res
             raise)))

(defn ^:private is-promise? [val]
  (= Promise (type val)))

(defn wrap-promise
  [handler]
  (fn [req res raise]
    (let [r (handler req res raise)]
      (when (is-promise? r)
        (-> r
            (p/catch (fn [err]
                       (def err err)
                       (error (js/JSON.stringify err) (go/get err "stack"))
                       (raise err))))))))



(def routes
  ["/" {true default-route}])

(defn router [req res raise]
  (if-let [{:keys [handler route-params]} (bidi/match-route* routes (:uri req) req)]
    (handler (assoc req :route-params route-params) res raise)))
