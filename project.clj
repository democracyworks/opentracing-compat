(defproject democracyworks/opentracing-compat "0.1.0-SNAPSHOT"
  :description "A compatibility library bridging clj-newrelic with the OpenTracing API."
  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"
            :distribution :repo}
  :url "https://github.com/democracyworks/opentracing-compat"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [opentracing-clj "0.1.4"]]
  :profiles {:dev {:dependencies [[io.opentracing/opentracing-mock "0.32.0"]]}})
