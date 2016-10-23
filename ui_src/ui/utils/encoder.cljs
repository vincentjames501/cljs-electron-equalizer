(ns ui.utils.encoder)

(def ^:private proc (js/require "child_process"))
(def ^:private darwin? (= "darwin" js/process.platform))

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
    :low "equalizer=f=80:width_type=q:width=20:g=-90,equalizer=f=100:width_type=q:width=20:g=-60,equalizer=f=180:width_type=q:width=20:g=-3"
    :medium "equalizer=f=80:width_type=q:width=20:g=-90,equalizer=f=100:width_type=q:width=20:g=-90,equalizer=f=180:width_type=q:width=20:g=-3"
    :high "equalizer=f=80:width_type=q:width=20:g=-90,equalizer=f=100:width_type=q:width=20:g=-90,equalizer=f=150:width_type=q:width=20:g=-40,equalizer=f=250:width_type=q:width=20:g=-3"))

(defn run-process
  [input output encoding-mode success-fn error-fn progress-fn]
  (let [duration (atom nil)
        proc (.spawn proc
                     (str js/__dirname (if darwin? "/ffmpeg" "\\ffmpeg.exe"))
                     (clj->js ["-y"            ; Overwrite
                               "-i" input      ; Input File
                               "-af" (get-encoding-opts encoding-mode) ; Equalizer
                               "-c:v" "copy"   ; Keep Original Video Source
                               output          ; Output File
                               ]))]
    (.on (.-stderr proc) "data" (partial parse-input duration progress-fn))
    (.on (.-stdout proc) "data" (partial parse-input duration progress-fn))
    (.on proc "error" error-fn)
    (.on proc "exit" success-fn)))
