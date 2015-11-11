(ns ui.utils.common
  (:require [reagent.core :as reagent]))

(def css-transition-group
  (reagent/adapt-react-class js/React.addons.CSSTransitionGroup))

(defn default-transition
  [content]
  [css-transition-group {:transition-name           "default"
                         :transition-enter-timeout  1500
                         :transition-leave-timeout  1300
                         :transition-appear-timeout 1000
                         :transition-appear         true}
   content])

(defn button
  [text & {:keys [click-fn button-type] :or {button-type :primary}}]
  [:button.mdl-button.mdl-js-button.mdl-button--raised.mdl-js-ripple-effect
   (merge {:onClick click-fn}
          (when button-type
            {:class-name (str "mdl-button--" (name button-type))}))
   text])

(defn spinner
  []
  [:div.mdl-spinner.mdl-js-spinner.is-active])