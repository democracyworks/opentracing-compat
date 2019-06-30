# opentracing-compat

A compatibility layer bridging [`defn-traced`][github-clj-newrelic] with the
OpenTracing API.

## Quick start

Use it just like [`defn-traced`][github-clj-newrelic]:

```clojure
(ns user
  (:require [democracyworks.opentracing.compat :refer [defn-traced]]))

(defn-traced sleep-for
  "Sleep for `x` milliseconds."
  [x]
  (Thread/sleep x))
```

## License

Copyright 2019 Democracy Works, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License.  You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied.  See the License for the
specific language governing permissions and limitations under the License.

[github-clj-newrelic]: https://github.com/TheClimateCorporation/clj-newrelic
