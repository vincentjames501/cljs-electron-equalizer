(ns ui.db
  (:require [reagent.core :as reagent]))

(def ^:private app-state
  (reagent/atom {:current-page   nil
                 :selected-files nil
                 :output         nil
                 :encoding-mode  nil
                 :encoding-state {:progress 0
                                  :process  nil
                                  :state    :paused}}))

(defn cursor
  [path]
  (reagent/cursor app-state path))

(defn select
  [path]
  (get-in @app-state path))