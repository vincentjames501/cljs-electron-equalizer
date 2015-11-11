(ns ui.views.home
  (:require [ui.utils.common :as ui]
            [secretary.core :as secretary]
            [ui.db :as db]))

(defn- select-files
  []
  (let [remote (js/require "remote")
        dialog (.require remote "dialog")
        selected-files (db/cursor [:selected-files])]
    (.showOpenDialog dialog
                     (clj->js {:title      "Open File(s)"
                               :filters    [{:name "Movies" :extensions ["mp4" "mov" "mkv" "avi"]}
                                            {:name "All Files" :extensions ["*"]}]
                               :properties ["openFile" "multiSelections" "createDirectory"]})
                     (fn [files]
                       (let [files (js->clj files)]
                         (when (seq files)
                           (reset! selected-files (js->clj files))
                           (secretary/dispatch! "#/mode")))))))

(defn drop-component
  []
  [:img.drop-img {:src "img/reel.png"}])

(defn home-component []
  [ui/default-transition
   [:div#home {:key "home"}
    [drop-component]
    [ui/button "Select video(s) to equalize" :click-fn select-files]]])