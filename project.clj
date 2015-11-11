(defproject cljs-graphic-equalizer "0.1.0-SNAPSHOT"
  :source-paths ["src/tools"]
  :description "Graphic Equalizer CLJS"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [figwheel "0.5.0-SNAPSHOT"]
                 [ring/ring-core "1.4.0"]
                 [reagent "0.5.1" :exclusions [cljsjs/react]]
                 [reagent-utils "0.1.5"]
                 [cljsjs/react-with-addons "0.13.3-0"]
                 [cljsjs/material "1.0.4-0"]
                 [secretary "1.2.3"]]
  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-SNAPSHOT"]]
  :clean-targets ^{:protect false} ["resources/main.js"
                                    "resources/public/js/ui-core.js"
                                    "resources/public/js/ui-core.js.map"
                                    "resources/public/js/ui-out"
                                    "resources/public/js/ui-release-out"]
  :cljsbuild
  {:builds
   [{:source-paths ["electron_src"]
     :id           "electron-dev"
     :compiler     {:output-to      "resources/main.js"
                    :optimizations  :simple
                    :pretty-print   true
                    :cache-analysis true}}
    {:source-paths ["ui_src" "dev_src"]
     :id           "frontend-dev"
     :compiler     {:output-to      "resources/public/js/ui-core.js"
                    :output-dir     "resources/public/js/ui-out"
                    :source-map     "resources/public/js/ui-core.js.map"
                    :asset-path     "js/ui-out"
                    :optimizations  :none
                    :cache-analysis true
                    :main           "dev.core"}}
    {:source-paths ["electron_src"]
     :id           "electron-release"
     :compiler     {:output-to     "resources/main.js"
                    :optimizations :simple}}
    {:source-paths ["ui_src"]
     :id           "frontend-release"
     :compiler     {:output-to     "resources/public/js/ui-core.js"
                    :output-dir    "resources/public/js/ui-release-out"
                    :source-map    "resources/public/js/ui-core.js.map"
                    :asset-path    "js/ui-release-out"
                    :optimizations :simple
                    :main          "ui.core"}}]}
  :figwheel {:http-server-root "public"
             :ring-handler     tools.figwheel-middleware/app
             :server-port      3449})
