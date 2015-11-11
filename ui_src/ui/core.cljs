(ns ui.core
  (:require [figwheel.client :as figwheel :include-macros true]
            [reagent.core :as reagent]
            [cljsjs.material]
            [ui.routes :as routes]
            [ui.db :as db]))

(enable-console-print!)

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3449/figwheel-ws"
  :jsload-callback (fn [] (print "reloaded")))

(defn- current-page-component
  "The current page component bound to the session :current-page key"
  []
  (let [current-page (db/cursor [:current-page])]
    (fn []
      [@current-page])))

(routes/init!)

(reagent/render [current-page-component] (.getElementById js/document "container"))
