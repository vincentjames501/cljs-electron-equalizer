(ns ui.views.mode
  (:require [ui.utils.common :as ui]
            [cljsjs.material]
            [secretary.core :as secretary]
            [ui.db :as db]
            [clojure.string :as str]))

(def ^:private dialog (.-dialog (.-remote (js/require "electron"))))

(defn- begin-with-mode
  [mode]
  (let [encoding-mode (db/cursor [:encoding-mode])
        output (db/cursor [:output])
        selected-files (db/select [:selected-files])]
    (letfn [(cb [file-type save-to]
              (when (seq save-to)
                (reset! output [file-type save-to])
                (reset! encoding-mode mode)
                (secretary/dispatch! "#/encoding")))]
      (if (= 1 (count selected-files))
        (.showSaveDialog dialog
                         (clj->js {:title      "Save To"
                                   :filters    [{:name "Video" :extensions (-> selected-files
                                                                               first
                                                                               (str/split #"\.")
                                                                               last
                                                                               (->> (conj [])))}
                                                {:name "All Files" :extensions ["*"]}]
                                   :properties ["openFile" "createDirectory"]})
                         (partial cb :file))
        (.showOpenDialog dialog
                         (clj->js {:title      "Output Directory"
                                   :properties ["openDirectory" "createDirectory"]})
                         (partial cb :directory))))))

(defn mode-component
  []
  [ui/default-transition
   [ui/wrap-page
    [:div#mode {:key "mode"}
     [:div.instructions "Please choose an equalization setting:"]
     [ui/button "Low" :click-fn #(begin-with-mode :low)]
     [ui/button "Medium" :click-fn #(begin-with-mode :medium)]
     [ui/button "High" :click-fn #(begin-with-mode :high)]
     [ui/button "Cancel" :button-type :accent :click-fn #(secretary/dispatch! "#/")]
     [:div.instructions-small (str "For videos with less distortion, choose low, with "
                                   "more distortion, choose a higher setting.")]]]])
