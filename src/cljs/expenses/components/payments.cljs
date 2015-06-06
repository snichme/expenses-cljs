(ns expenses.components.payments
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [expenses.calc :refer [sum-expenses mean-expense pay]]))

(defn round [d x]
  (let [c (reduce #(* 10 %) (range 1 (+ d 2)))]
    (/ (js/Math.round (* x c)) c)))

(defn format-payment [payment]
  (str
   (:name payment)
   " should pay "
   (round 2 (:amount payment))
   " to "
   (:reciver payment)))

(defn payments-as-text [payments owner]
  (om/component
   (apply dom/div #js{:className "payments__text"}
          (map
           #(dom/div #js{:className "payments__row"} (format-payment %))
           payments))))


(defn payments-table-row [payment]
  (dom/tr nil
          (dom/td nil (:name payment))
          (dom/td nil (round 2 (:amount payment)))
          (dom/td nil (:reciver payment))))

(defn payments-as-table [payments owner]
  (om/component
   (apply dom/table #js {:className "payments__table"}
          (dom/tr nil
                 (dom/th nil "Payer")
                 (dom/th nil "Amount")
                 (dom/th nil "Reciver"))
          (map #(payments-table-row %) payments))))

(defn payments [p]
  (let [
        exps (sort-by #(:amount %) (sum-expenses p))
        mean (mean-expense exps)
        payments (pay exps mean 0)]
    (dom/div #js {:className "payments"}
           (dom/h3 #js {:className "payments__header"} "Payments")
           (om/build payments-as-text payments)
           (om/build payments-as-table payments))))
