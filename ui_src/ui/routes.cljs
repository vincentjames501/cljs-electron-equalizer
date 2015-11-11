(ns ui.routes
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [ui.views.home :as home]
            [ui.views.mode :as mode]
            [ui.views.encoding :as encoding]
            [reagent.session :as session]
            [ui.db :as db])
  (:import goog.History))

(def current-page (db/cursor [:current-page]))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))
        (js/setInterval (fn []
                          (.upgradeAllRegistered js/componentHandler))
                        500)))
    (.setEnabled true)))

(defroute "/" []
          (reset! current-page #'home/home-component))

(defroute "/mode" []
          (reset! current-page #'mode/mode-component))

(defroute "/encoding" []
          (encoding/start-encoding!)
          (reset! current-page #'encoding/encoding-component))

(defn init! []
  (secretary/set-config! :prefix "#")
  (session/put! :current-page #'home/home-component)
  (hook-browser-navigation!))