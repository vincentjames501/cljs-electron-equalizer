(ns ui.utils.encoder)

(def proc (js/require "child_process"))

(defn- duration-str->float
  [s]
  (when (seq s)
    (when-let [[hours minutes seconds] (.split s ":")]
      (+ (js/parseFloat seconds) (* (js/parseInt minutes) 60) (* (js/parseInt hours) 60 60)))))

(defn- get-duration
  [s]
  (duration-str->float (last (.match (str s) #"Duration: (.*?), start:"))))

(defn- get-current-progress
  [duration s]
  (when-let [current (duration-str->float (last (.match (str s) #"time=(.*?) bitrate")))]
    (int (* (/ current duration) 100))))

(defn- parse-input
  [duration progress-fn data]
  (if @duration
    (when-let [current-progress (get-current-progress @duration data)]
      (progress-fn current-progress))
    (when-let [found-duration (get-duration data)]
      (reset! duration found-duration))))

(defn- get-encoding-opts
  [encoding-mode]
  (case encoding-mode
    :low "equalizer=f=440:width_type=o:width=2:g=5"
    :medium "equalizer=f=440:width_type=o:width=2:g=5"
    :high "equalizer=f=440:width_type=o:width=2:g=5"))

(defn run-process
  [input output encoding-mode success-fn error-fn progress-fn]
  (let [duration (atom nil)
        spawned-processes (.spawn proc
                                  (str js/__dirname "/ffmpeg")
                                  (clj->js ["-y"            ; Overwrite
                                            "-i" input      ; Input File
                                            "-af" (get-encoding-opts encoding-mode) ; Equalizer
                                            output          ; Output File
                                            ]))]
    (.on (.-stderr spawned-processes) "data" (partial parse-input duration progress-fn))
    (.on (.-stdout spawned-processes) "data" (partial parse-input duration progress-fn))
    (.on spawned-processes "error" error-fn)
    (.on spawned-processes "exit" success-fn)))