(ns ui.core
  (:require [reagent.core :as reagent]
            [cljsjs.material]
            [ui.routes :as routes]
            [ui.db :as db]))

(enable-console-print!)

(defn- current-page-component
  "The current page component bound to the session :current-page key"
  []
  (let [current-page (db/cursor [:current-page])]
    (fn []
      [@current-page])))

(routes/init!)

(reagent/render [current-page-component] (.getElementById js/document "container"))
