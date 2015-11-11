(ns ui.views.encoding
  (:require [ui.utils.common :as ui]
            [reagent.core :refer [atom]]
            [secretary.core :as secretary]
            [ui.utils.encoder :as encoder]
            [ui.db :as db]
            [clojure.string :as str]))

(def fs (js/require "fs"))

(defn- file-exists?
  [f]
  (try (.lstatSync fs f)
       true
       (catch :default _ false)))

(def encoding-state (db/cursor [:encoding-state]))

(defn- go-home!
  []
  (secretary/dispatch! "#/"))

(defn- ends-with?
  [s pattern]
  (= (first (.match s (str pattern "$"))) pattern))

(defn- get-output-file
  [file-type output selected-file]
  (let [selected-file-name-with-extension (last (str/split selected-file #"/"))
        selected-file-name-segments (str/split selected-file-name-with-extension #"\.")
        selected-file-name (apply str (butlast selected-file-name-segments))
        extension (last selected-file-name-segments)]
    (if (= file-type :directory)
      (loop [i 0]
        (let [potential-file (str output "/" selected-file-name "-equalized" (when (pos? i) (inc i)) "." extension)]
          (if-not (file-exists? potential-file)
            potential-file
            (recur (inc i)))))
      (if (ends-with? output extension)
        output
        (loop [i 0]
          (let [potential-file (str output (when (pos? i) (inc i)) "." extension)]
            (if-not (file-exists? potential-file)
              potential-file
              (recur (inc i)))))))))

(defn- encode!
  [file-index file-count]
  (let [encoding-mode (db/select [:encoding-mode])
        selected-files (db/cursor [:selected-files])
        selected-file (first @selected-files)
        [file-type output] (db/select [:output])]
    (print "Mode" encoding-mode)
    (print "File" selected-file)
    (print "File Type" file-type)
    (print "Output" output)
    (swap! encoding-state
           assoc
           :process
           (encoder/run-process selected-file
                                (get-output-file file-type output selected-file)
                                encoding-mode
                                (fn [e]
                                  (.log js/console (clj->js e))
                                  (if (= 1 (count @selected-files))
                                    (do (reset! encoding-state {:progress 100 :process nil :state :success})
                                        (.setTimeout js/window go-home! 3000))
                                    (do (swap! selected-files rest)
                                        (encode! (inc file-index) file-count))))
                                (fn [e]
                                  (.log js/console (clj->js e))
                                  (reset! encoding-state {:progress 100 :process nil :state :fail})
                                  (js/alert "There was a problem equalizing your selected file(s).")
                                  (.setTimeout js/window go-home! 5000))
                                (fn [progress]
                                  (swap! encoding-state
                                         assoc
                                         :progress
                                         (int (/ (+ (* 100 file-index) progress) file-count))))))))

(defn start-encoding!
  []
  (reset! encoding-state {:progress 0 :process nil :state :encoding})
  (encode! 0 (count (db/select [:selected-files]))))

(defn- cancel-encoding!
  [_]
  (swap! encoding-state (fn [state]
                          (when-let [process (:process state)]
                            (.kill process "SIGTSTP")
                            {:progress 0 :process nil :state :paused})))
  (go-home!))

(defn- checkmark
  [icon primary?]
  [:button#complete.mdl-button.mdl-js-button.mdl-button--fab.mdl-js-ripple-effect.mdl-button--colored
   {:key icon :class-name (str "mdl-button--" (if primary? "primary" "accent"))}
   [:i.material-icons icon]])

(defn encoding-component []
  [ui/default-transition
   [:div#encoding {:key "encoding"}
    (case (:state @encoding-state)
      :encoding
      [:div
       [:div.encoding-progress (str (:progress @encoding-state) "%")]
       [ui/spinner]
       [ui/button "Cancel" :button-type :accent :click-fn cancel-encoding!]]
      :success
      [ui/default-transition
       [:div
        [checkmark "check" true]
        [:h4 "Success!"]]]
      :fail
      [ui/default-transition
       [:div
        [checkmark "clear" false]
        [:h4 "Whoops!"]]])]])