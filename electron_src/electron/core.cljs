(ns electron.core)

(def ^:private main-window (atom nil))
(def ^:private electron (js/require "electron"))
(def ^:private app (.-app electron))
(def ^:private crash-reporter (.-crashReporter electron))
(def ^:private menu (.-Menu electron))
(def ^:private browser-window (.-BrowserWindow electron))
(def ^:private darwin? (= "darwin" js/process.platform))
(def ^:private is-development? (boolean (or (.-defaultApp js/process)
                                            (re-matches #"[\\/]electron-prebuilt[\\/]" (.-execPath js/process))
                                            (re-matches #"[\\/]electron[\\/]" (.-execPath js/process)))))

(defn- init-menu
  []
  (let [name (.getName app)
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
                                                  :role        "selectall"}]}]
                                     (when is-development?
                                       [{:label   "View"
                                         :submenu [{:label       "Reload"
                                                    :accelerator "CmdOrCtrl+R"
                                                    :click       (fn [_ focusedWindow]
                                                                   (when focusedWindow
                                                                     (.reload focusedWindow)))}
                                                   {:label       "Toggle Full Screen"
                                                    :accelerator (if darwin? "Ctrl+Command+F" "F11")
                                                    :click       (fn [_ focusedWindow]
                                                                   (when focusedWindow
                                                                     (let [full? (.isFullScreen focusedWindow)]
                                                                       (.setFullScreen focusedWindow (not full?)))))}
                                                   {:label       "Toggle Developer Tools"
                                                    :accelerator (if darwin? "Alt+Command+I" "Ctrl+Shift+I")
                                                    :click       (fn [_ focusedWindow]
                                                                   (when focusedWindow
                                                                     (.toggleDevTools focusedWindow)))}]}])
                                     [{:label   "Window"
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
  (.start crash-reporter (clj->js {:productName "CLJSGraphicEqualizer"
                                   :companyName "Vincent Pizzo"
                                   :submitURL   "https://your-domain.com/url-to-submit"
                                   :autoSubmit  true})))

(defn- init-browser-window
  []
  (reset! main-window (browser-window.
                        (clj->js (merge {:width          350
                                         :height         378
                                         :webPreferences {:webSecurity false}
                                         :resizable      is-development?}
                                        (if darwin?
                                          {:titleBarStyle "hidden"}
                                          {:frame false})))))
  ; Path is relative to the compiled js file (main.js in our case)
  (.loadURL @main-window (str "file://" js/__dirname "/public/index.html"))
  (.on @main-window "closed" #(reset! main-window nil)))

(defn- init
  []
  (init-crash-reporter)
  (init-menu)
  (init-browser-window))

(.on app "ready" init)
(.on app "window-all-closed" #(.quit app))
