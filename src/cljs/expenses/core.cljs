(ns expenses.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [cljs.core.async :refer [put! <! chan]]
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [expenses.calc :refer [sum-expenses mean-expense pay]]
   [expenses.actions :as actions]
   [expenses.components.expenses :refer [expenses expenses-plain]]
   [expenses.components.payments :refer [payments]]))


(defonce app-state (atom {:items [
                                  {:name "Mange" :amount 100}
                                  {:name "Natta" :amount 180}
                                  {:name "Vincent" :amount 40}
                                  {:name "Doris" :amount 10}
                                  ]}))


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
               (expenses (:items data))
               (expenses-plain (:items data))
               (payments (:items data))))))

(defn main []
  (om/root app app-state {
                          :target (.getElementById js/document "app")
                          :shared {:action-channel (chan)}}))
