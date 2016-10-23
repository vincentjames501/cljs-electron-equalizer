(ns electron.core)

(def ^:private main-window (atom nil))
(def ^:private app (js/require "app"))
(def ^:private darwin? (= "darwin" js/process.platform))
(def ^:private is-development? (boolean (or (.-defaultApp js/process)
                                            (re-matches #"[\\/]electron-prebuilt[\\/]" (.-execPath js/process))
                                            (re-matches #"[\\/]electron[\\/]" (.-execPath js/process)))))

(defn- init-menu
  []
  (let [menu (js/require "menu")
        name (.getName app)
        template (cond-> []
                         darwin?
                         (conj {:label   name
                                :submenu [{:label (str "About " name)
                                           :role  "about"}
                                          {:type "separator"}
                                          {:label   "Services"
                                           :role    "services"
                                           :submenu []}
                                          {:type "separator"}
                                          {:label       (str "Hide " name)
                                           :accelerator "Command+H"
                                           :role        "hide"}
                                          {:label       "Hide Others"
                                           :accelerator "Command+Shift+H"
                                           :role        "hideothers"}
                                          {:label "Show All"
                                           :role  "unhide"}
                                          {:type "separator"}
                                          {:label       "Quit"
                                           :accelerator "Command+Q"
                                           :click       (fn [] (.quit app))}]})
                         :always
                         (-> (concat [{:label   "Edit"
                                       :submenu [{:label       "Undo"
                                                  :accelerator "CmdOrCtrl+Z"
                                                  :role        "undo"}
                                                 {:label       "Redo"
                                                  :accelerator "Shift+CmdOrCtrl+Z"
                                                  :role        "redo"}
                                                 {:type "separator"}
                                                 {:label       "Cut"
                                                  :accelerator "CmdOrCtrl+X"
                                                  :role        "cut"}
                                                 {:label       "Copy"
                                                  :accelerator "CmdOrCtrl+C"
                                                  :role        "copy"}
                                                 {:label       "Paste"
                                                  :accelerator "CmdOrCtrl+V"
                                                  :role        "paste"}
                                                 {:label       "Select All"
                                                  :accelerator "CmdOrCtrl+A"
                                                  :role        "selectall"}]}
                                      {:label   "View"
                                       :submenu [{:label       "Reload"
                                                  :accelerator "CmdOrCtrl+R"
                                                  :click       (fn [_ focusedWindow]
                                                                 (when focusedWindow
                                                                   (.reload focusedWindow)))}
                                                 {:label       "Toggle Full Screen"
                                                  :accelerator (if darwin? "Ctrl+Command+F" "F11")
                                                  :click       (fn [_ focusedWindow]
                                                                 (when focusedWindow
                                                                   (.setFullScreen focusedWindow (not (.isFullScreen focusedWindow)))))}
                                                 {:label       "Toggle Developer Tools"
                                                  :accelerator (if darwin? "Alt+Command+I" "Ctrl+Shift+I")
                                                  :click       (fn [_ focusedWindow]
                                                                 (when focusedWindow
                                                                   (.toggleDevTools focusedWindow)))}]}
                                      {:label   "Window"
                                       :role    "window"
                                       :submenu [{:label       "Minimize"
                                                  :accelerator "CmdOrCtrl+M"
                                                  :role        "minimize"}
                                                 {:label       "Close"
                                                  :accelerator "CmdOrCtrl+W"
                                                  :role        "close"}]}])
                             vec)

                         darwin?
                         (update-in [3 :submenu] conj
                                    {:type "separator"}
                                    {:label "Bring All to Front"
                                     :role  "front"})

                         :always
                         clj->js)]
    (.setApplicationMenu menu (.buildFromTemplate menu template))))

(defn- init-crash-reporter
  []
  (let [crash-reporter (js/require "crash-reporter")]
    (.start crash-reporter)))

(defn- init-browser-window
  []
  (let [browser-window (js/require "browser-window")]
    (reset! main-window (browser-window.
                          (clj->js {:width           350
                                    :height          370
                                    :title-bar-style "hidden"
                                    :web-preferences {:web-security false}
                                    ;:resizable       false
                                    })))
    ; Path is relative to the compiled js file (main.js in our case)
    (.loadUrl @main-window (str "file://" js/__dirname "/public/index.html"))
    (.on @main-window "closed" #(reset! main-window nil))))

(defn- init
  []
  (init-crash-reporter)
  (init-menu)
  (init-browser-window))

(.on app "ready" init)
(.on app "window-all-closed" #(.quit app))
