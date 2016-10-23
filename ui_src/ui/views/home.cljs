(ns ui.views.home
  (:require [ui.utils.common :as ui]
            [secretary.core :as secretary]
            [ui.db :as db]))

(def ^:private dialog (.-dialog (.-remote (js/require "electron"))))

(defn- select-files
  []
  (let [selected-files (db/cursor [:selected-files])]
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
   [ui/wrap-page
    [:div#home {:key "home"}
     [drop-component]
     [ui/button "Select video(s) to equalize" :click-fn select-files]]]])
