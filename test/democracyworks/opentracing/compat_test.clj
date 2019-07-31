(ns democracyworks.opentracing.compat-test
  (:require [clojure.test :refer [deftest is testing]]
            [democracyworks.opentracing.compat :refer [defn-traced]]
            [opentracing-clj.core :as tracing])
  (:import (io.opentracing.mock MockTracer)))

(defn-traced child
  []
  (apply + (range 10)))

(defn-traced parent
  []
  (child))

(defn-traced multi-arity
  "This one has a docstring."
  ([]
   (multi-arity 42))
  ([x]
   {:pre [(= x 42)]}
   (apply + (range x))))

(defn untraced
  []
  (apply + (range 10)))

(deftest defn-traced-test
  (testing "calling child on its own creates a single root span"
    (binding [tracing/*tracer* (MockTracer.)]
      (is (= 45 (child)))
      (is (= 1 (count (.finishedSpans tracing/*tracer*))))
      (is (zero? (.parentId (first (.finishedSpans tracing/*tracer*))))
          "the parentId should be zero for root spans")))
  (testing "parent and child create two spans with a parent/child relationship"
    ;; Spans are appended in order of finishing, so the child should finish
    ;; first and have a non-zero parent ID, then the parent will finish and
    ;; should have a parent ID of zero since it is the root span.
    (binding [tracing/*tracer* (MockTracer.)]
      (is (= 45 (parent)))
      (is (= 2 (count (.finishedSpans tracing/*tracer*))))
      (is (pos? (.parentId (first (.finishedSpans tracing/*tracer*))))
          "the child span has a non-zero parent ID")
      (is (zero? (.parentId (second (.finishedSpans tracing/*tracer*))))
          "the parent span has a parent ID of 0 (is the root span)")))
  (testing "multi-arity functions work as expected"
    (binding [tracing/*tracer* (MockTracer.)]
      (is (= 861 (multi-arity)))
      (is (= "This one has a docstring." (:doc (meta #'multi-arity))))
      (is (= 2 (count (.finishedSpans tracing/*tracer*))))
      (is (pos? (.parentId (first (.finishedSpans tracing/*tracer*))))
          "the child span has a non-zero parent ID")
      (is (zero? (.parentId (second (.finishedSpans tracing/*tracer*))))
          "the parent span has a parent ID of 0 (is the root span)")))
  (testing "untraced function creates no spans"
    (binding [tracing/*tracer* (MockTracer.)]
      (is (= 45 (untraced)))
      (is (zero? (count (.finishedSpans tracing/*tracer*)))))))
