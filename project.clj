(defproject pack-the-bag "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.cemerick/valip "0.3.2"]
                 [clojurewerkz/neocons "1.1.0"]
                 [ring/ring-core "1.2.0"]
		             [ring/ring-jetty-adapter "1.1.0"]
                 [sandbar "0.4.0-SNAPSHOT"]
                 [enlive "1.1.1"]
                 ]
  :main pack-the-bag.repl
)