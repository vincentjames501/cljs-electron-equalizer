(defproject cljs-graphic-equalizer "0.1.0-SNAPSHOT"
  :source-paths ["src/tools"]
  :description "Graphic Equalizer CLJS"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.28"]
                 [figwheel "0.3.7"]
                 [ring/ring-core "1.4.0"]
                 [reagent "0.5.1" :exclusions [cljsjs/react]]
                 [reagent-utils "0.1.5"]
                 [cljsjs/react-with-addons "0.13.3-0"]
                 [cljsjs/material "1.0.4-0"]
                 [secretary "1.2.3"]]
  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-figwheel "0.3.7"]]
  :cljsbuild
  {:builds
   [{:source-paths ["src/electron"],
     :id           "electron-dev",
     :compiler     {:output-to      "resources/main.js",
                    :optimizations  :simple
                    :pretty-print   true
                    :cache-analysis true}}
    {:source-paths ["src/ui"],
     :id           "frontend-dev",
     :compiler     {:output-dir     "resources/public/js/ui-out"
                    :output-to      "resources/public/js/ui-core.js",
                    :optimizations  :none
                    ; :pretty-print true
                    :source-map     true
                    :cache-analysis true}}]}
  :figwheel {:http-server-root "public"
             :ring-handler     figwheel-middleware/app
             :server-port      3449})
