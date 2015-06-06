(ns expenses.calc)

(def expenses [
               {:name "Magnus" :amount 20}
               {:name "Magnus" :amount 50}
               {:name "Natta" :amount 100}
               {:name "Natta" :amount 30}
               {:name "Natta2" :amount 300}
               {:name "Vincent" :amount 400}
               ])

(defn total [exps]
       (reduce + (map :amount exps)))

(defn sum-expenses [exps]
  (let [x exps]
    (map (fn [x]
           {:name (first x) :amount (total (second x))})
           (group-by :name x))))

(defn mean-expense [exps]
  (/ (total exps) (count exps)))

(defn pay [exps mean extras]
  (let [
        payer (first exps)
        reciver (second exps)
        amount (+ extras (- mean (:amount payer)))
        s {:name (:name payer) :amount amount :reciver (:name reciver)}]
    (if (nil? reciver)
      '()
      (conj (pay (rest exps) mean amount) s))))

; (let [
;       exps (sort-by #(:amount %) (sum-expenses expenses))
;       mean (mean-expense exps)
;       groups (partition-by #(< 0 (- mean (:amount %))) exps)
;       payers (first groups)
;       recivers (second groups)]
;   (pay exps mean 0))
