(ns pack-the-bag.repl
  "The starting namespace for the project. This is the namespace that
  users will land in when they start a Clojure REPL. It exists to
  provide convenience functions like 'go' and 'dev-server'."
  (:use [clojure.repl]
	[clojure.java.shell :only [sh]]
  [clojure.string :only [join]])
  (:import [java.io InputStreamReader
		    BufferedReader]
	   [java.lang Runtime])
  (:require [clojure.java.browse :as browse]
	    [pack-the-bag.server :as server]
	    [pack-the-bag.neo4j :as n4j]
      [cljs.reader :refer [read-string]]
    
      )
  (:refer-clojure :exclude [read-string]))

(defn parse-double
  "Parse string to double"
  [s]
  (if (and (string? s) (re-matches #"\s*[+-]?\d+(\.\d+(M|M|N)?)?\s*" s))
    (read-string s)))

(defn cmd-term
  "Execute command prompt/terminal command"
  [command]
  (let [process (. (Runtime/getRuntime) exec command)
      stdin (.getInputStream process)
      isr (InputStreamReader. stdin)
      br (BufferedReader. isr)]
      (let [seq (line-seq br)]
	seq)))

(defn neo4j-linux
  ""
  []
  (try (= (re-find #"Linux" (str (cmd-term "uname -a"))) "Linux")
  (catch Exception e (do false))))

(defn neo4j-win
  ""
  []
  (try (= (re-find #"OSArchitecture" (str (cmd-term "wmic OS get OSArchitecture"))) "OSArchitecture")
  (catch Exception e (do false))))

(defn neo4j-stop-linux
  ""
  []
  (cmd-term "neo4j-community/bin/neo4j stop"))

(defn neo4j-stop-win
  ""
  []
  (cmd-term "neo4j-community/bin/Neo4j.bat stop"))

(defn neo4j-status-linux
  ""
  []
  (neo4j-stop-linux)
  false)

(defn neo4j-status-win
  ""
  []
  (neo4j-stop-win)
  false)

(defn neo4j-status
  ""
  []
  (if (neo4j-linux)
      (neo4j-status-linux)
      (if (neo4j-win)
	  (neo4j-status-win)
	  false)))

(defn neo4j-start-linux
  ""
  []
  (cmd-term "neo4j-community/bin/neo4j start"))

(defn neo4j-start-win
  ""
  []
  (cmd-term "neo4j-community/bin/Neo4j.bat start"))

(defn neo4j-start
  "Start neo4j server"
  []
  (if (not (neo4j-status))
	(if (neo4j-linux)
	    (neo4j-start-linux)
	    (if (neo4j-win)
		(neo4j-start-win)))))

(defn neo4j-stop
  "Stop neo4j server"
  []
  (if (not (neo4j-status))
	(if (neo4j-linux)
	    (neo4j-stop-linux)
	    (if (neo4j-win)
		(neo4j-stop-win)))))

(defn insert-initials-values
  []
 (n4j/create-node "Item" {                             
			     :item "Map"
           :weight(parse-double (str 20))
           :value(parse-double (str 3))})
 
 (n4j/create-node "Item" {                             
			     :item "Water"
           :weight(parse-double (str 50))
           :value(parse-double (str 4))})
 
 (n4j/create-node "Traveler" {                            
			     :name "Dijana"
			     :email "dijanabakajac@gmail.com"
			     :surname "Bakajac"
           :username "traveller"
			     :password "traveller"
			     :age "24"
		       :city "Belgrade"
			     :country "Serbia"
			     :gender "Female"}))


(defonce server (ref nil))          

(defn start-server
  "Start the development server and open the host application in the
  default browser."
  []
 (n4j/connect-neo4j)
(insert-initials-values)
(dosync (ref-set server (server/run-server)))
(future (Thread/sleep 3000)
      (browse/browse-url "http://localhost:5000/login")))

(defn stop-server
  "Stop server"
  []
  ;(neo4j-stop)
  (.stop server))

(defn restart-server
  "Restart server"
  []
  (stop-server)
  (start-server))

(defn -main [& args]
  (start-server))

(println)
(println "Type (start-server) to launch server.")
(println "Type (restart-server) to restart server.")
(println "Type (stop-server) to stop server.")
(println)
