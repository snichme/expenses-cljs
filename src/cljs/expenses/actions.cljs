(ns expenses.actions
  (:require
   [om.core :as om :include-macros true]))


(defn delete-expense [app {:keys [name] :as item}]
  (om/transact! app :items
                (fn [items]
                  (into [] (remove #(= item %) items)))))

(defn new-expense []
  {:name "" :amount 0})

(defn add-expense [app]
  (om/transact! app :items
                (fn [items]
                  (conj items (new-expense)))))


(defn handle [type app val]
  (case type
    :destroy (delete-expense app val)
    :add (add-expense app)
    nil))
