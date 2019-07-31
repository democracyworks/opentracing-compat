(ns democracyworks.opentracing.compat
  "OpenTracing functionality compatible with [clj-newrelic].

  [clj-newrelic]: https://github.com/TheClimateCorporation/clj-newrelic"
  (:require [clojure.core.specs.alpha :as specs]
            [clojure.spec.alpha :as s]
            [opentracing-clj.core :as tracing]))

(defn- instrument-body
  "Start an OpenTracing span on the `body` in `forms` and return the transformed
  forms as a list.

  Designed to be called on either `defn` forms directly for single-arity
  functions or on the arity definition lists for multi-arity functions."
  [forms span-name body]
  (let [split-index (- (count forms) (count body))
        [first-forms last-forms] (split-at split-index forms)
        s (gensym)]
    `(~@first-forms
      (tracing/with-span [~s {:name ~span-name}]
        ~@last-forms))))

(defn- extract-fn-body
  "Pull the function body forms out of the `conformed` body."
  [conformed]
  (let [body (second conformed)]
    (if (map? body)
      (:body body)
      body)))

(defmacro defn-traced
  "Like `defn`, but wraps the function body in an OpenTracing span that mimics
  the default behavior of the `clj-newrelic` `defn-traced` macro.

  Nesting calls to `defn-traced` works similarly to the default behavior of the
  `defn-traced` macro that it replaces: nested spans will be set as children of
  unfinished spans.

  This macro is a transitional drop-in replacement for `defn-traced`, so if you
  need more control over the tracing behavior you should manipulate spans
  directly."
  [& args]
  (let [[fname body] ((juxt first rest) args)
        span-name (str *ns* \. fname)
        conformed-args (s/conform ::specs/defn-args args)
        [arity-n tail] (:fn-tail conformed-args)]
    (case arity-n
      :arity-1
      `(defn ~fname
         ~@(instrument-body body span-name (extract-fn-body (:body tail))))

      :arity-n
      (let [[first-forms arities last-forms] (partition-by list? args)]
        `(defn
           ~@first-forms
           ~@(map-indexed
              (fn [idx arity]
                (instrument-body
                 arity
                 span-name
                 (extract-fn-body (second (second (nth (:bodies tail) idx))))))
              arities)
           ~@(seq last-forms)))

      ;; Default to bog standard `defn` if we can't figure out the arity for
      ;; some reason, or maybe for "forward compatibility".
      `(defn ~fname ~@body))))

(s/fdef defn-traced
  :args ::specs/defn-args
  :ret any?)
