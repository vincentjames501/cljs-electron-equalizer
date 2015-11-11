(ns electron.core)

(def app (js/require "app"))
(def browser-window (js/require "browser-window"))
(def crash-reporter (js/require "crash-reporter"))

(def main-window (atom nil))

(defn init-browser []
 (reset! main-window (browser-window.
                      (clj->js {:width           350
                                :height          370
                                :title-bar-style "hidden"
                                :web-preferences {:web-security false}
                                ;:resizable       false
                                })))
 ; Path is relative to the compiled js file (main.js in our case)
 (.loadUrl @main-window (str "file://" js/__dirname "/public/index.html"))
 (.on @main-window "closed" #(reset! main-window nil)))

(.start crash-reporter)
(.on app "ready" init-browser)
