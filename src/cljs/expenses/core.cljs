(ns expenses.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [cljs.core.async :refer [put! <! chan]]
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [expenses.calc :refer [sum-expenses mean-expense pay]]
   [expenses.actions :as actions]
   [expenses.components.expenses :refer [expenses expenses-plain]]
   [expenses.components.payments :refer [payments]])
  (:import
   [goog.i18n NumberFormatSymbols]
   [goog.i18n NumberFormatSymbols_sv_SE]
   [goog.i18n NumberFormatSymbols_en_US]))

(set! NumberFormatSymbols NumberFormatSymbols_sv_SE)

(defonce app-state (atom {:items [
                                  {:name "Mange" :amount 100}
                                  {:name "Natta" :amount 180}
                                  {:name "Vincent" :amount 40}
                                  {:name "Doris" :amount 10}
                                  ]}))

(defn set-lang [lang]
  (println lang)
  (case lang
    :se (set! NumberFormatSymbols NumberFormatSymbols_sv_SE)
    :en (set! NumberFormatSymbols NumberFormatSymbols_en_US)))

(defn language-selector [app owner]
  (om/component
   (dom/ul nil
           (dom/li nil
                   (dom/a #js {
                            :href "#"
                            :onClick (fn [_] (set-lang :se))} "Svenska"))
           (dom/li nil
                   (dom/a #js {
                            :href "#"
                            :onClick (fn [_] (set-lang :en))} "Engelska"))
           )))
(defn header [app owner]
  (om/component
   (dom/div #js{:className "header"}
            (om/build language-selector app))))

(defn app [data owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (let [c (:action-channel (om/get-shared owner))]
        (go
          (while true
            (let [[type value] (<! c)]
              (actions/handle type data value))))))
    om/IRender
    (render [_]
      (dom/div #js {:className "app"}
               (om/build header data)
               (expenses (:items data))
               (expenses-plain (:items data))
               (payments (:items data))))))

(defn main []
  (om/root app app-state {
                          :target (.getElementById js/document "app")
                          :shared {:action-channel (chan)}}))
