(ns expenses.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [expenses.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'expenses.core-test))
    0
    1))
