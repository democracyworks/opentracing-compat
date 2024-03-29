(defproject democracyworks/opentracing-compat "0.2.0"
  :description "A compatibility library bridging clj-newrelic with the OpenTracing API."
  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"
            :distribution :repo}
  :url "https://github.com/democracyworks/opentracing-compat"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [opentracing-clj "0.2.2"]]
  :profiles {:dev {:dependencies [[io.opentracing/opentracing-mock "0.33.0"]]}})
