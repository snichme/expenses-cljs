(ns expenses.components.expenses
  (:require
   [cljs.core.async :refer [put! <! chan]]
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]))


(defn expense-row [todo owner opts]
  (reify
    om/IRender
    (render [_]
      (let [comm (:action-channel (om/get-shared owner))]
        (dom/div #js {:className "expenses__row"}
                 (dom/button #js {
                                  :className "expenses__deletebutton"
                                  :onClick #(put! comm [:destroy todo])
                                  } "X")
                 (dom/input #js {
                                 :type "text"
                                 :className "expenses__name"
                                 :value (:name todo)
                                 :onChange #(om/transact! todo :name
                                                          (fn [xs] (.. % -target -value)))
                                 })
                 (dom/input #js {
                                 :type "number"
                                 :className "expenses__amount"
                                 :value (:amount todo)
                                 :onChange #(om/transact! todo :amount
                                                          (fn [xs] (js/parseInt (.. % -target -value) 10)))
                                 }))))))


(defn add-expense-button [expenses owner]
  (reify
    om/IRender
    (render [_]
      (let [
            comm (:action-channel (om/get-shared owner))]
        (dom/div #js {:className "expenses__row" }
                 (dom/button #js {
                                  :onClick #(put! comm [:add])
                                  :className "expenses__addbutton"
                                  } "Add expense"))))))

(defn expenses [exps comm]
  (dom/div #js {:className "expenses"}
           (dom/h3 #js {:className "expenses__header"} "Expenses")
           (om/build add-expense-button exps)
           (apply dom/div #js {:className "expenses__rows"} (om/build-all expense-row exps))
           (om/build add-expense-button exps)))

(defn parse-expense [raw]
  (let [re (js/RegExp. "^(.*?)\\s?([0-9]+(\\.|,)?[0-9]+?)?$")
        matches (.exec re raw)]
    (into [] (rest matches))))

(defn plain-expenses-to-formatted [raw]
  (let [rows (.split raw "\n")]
    (into [] (map (fn [row]
                    (let [
                          v (parse-expense row)
                          name (clojure.string/trim (first v))
                          amount (-> v second clojure.string/trim js/parseFloat)
                          ]
                      {:name name :amount amount}))
                  rows))))


(defn format-expense-to-plain [exps]
  (let [
        space 5
        longest (+ (apply max (into [] (map #(count (clojure.string/trim  (:name %))) exps))) space)
        filler (fn [s] (apply str (repeat (- longest (count s)) " ")))]
    (clojure.string/join "\n"
                         (map #(clojure.string/join (filler (:name %)) (vals %)) exps))))

(defn expenses-as-text [items owner]
  (reify
    om/IInitState
    (init-state [_]
      {:exps (format-expense-to-plain items)})

    om/IWillReceiveProps
    (will-receive-props [this next-props]
      (om/set-state! owner :exps (format-expense-to-plain next-props)))

    om/IRenderState
    (render-state [_ state]
      (let [
            submit (fn [e]
                     (let [
                           value (.. e -target -value)
                           exps (plain-expenses-to-formatted value)]
                       (om/transact! items (fn [_] exps))))
            update (fn [e]
                     (om/set-state! owner :exps (.. e -target -value)))]

        (dom/div #js {:className "expenses"}
                 (dom/h3 #js {:className "expenses__header"} "Expenses")
                 (dom/textarea #js {
                                    :className "expenses__plain"
                                    :value (:exps state)
                                    :rows 5
                                    :cols 40
                                    :onChange update
                                    :onBlur submit
                                    }))))))
(defn expenses-plain [items]
  (dom/div #js {:className "expenses"}
           (om/build expenses-as-text items)))
