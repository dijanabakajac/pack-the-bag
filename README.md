Pack the bag

Pack the bag application is designed for people who travel often with limited luggage. Application usage is possible only for registered users, and only after registering a user can log in on the system with his username and password. User can change data about his account, or he can delete it if he does not want to use it any more. 
Pack the bag application uses algorithm that works in the following manner : given  a set of items, each with a mass and a numerical value, determine the number of each item to include in a set of items so that the total weight is less than or equal to some given limit and the total value is as large as possible. This type of algorithm belongs to combinatorial optimization algorithms. 
When using this application, user  can enter all items that he is considering to take on the trip with associated weights (in grams) and values (how important this item is for him, for example bottle of water has much greater value than iPod). User can see all the items that he entered. He also enters limitation of his luggage, in grams too. Main functionality of the application is to determine the optimal solution when deciding what to pack, taking into account weight and  value of items and luggage limitation. User can also delete some item from the list, or update it if he change his mind regarding value of some item for example. 
Instructions
Application requres Neo4j server instance so you need to download it from http://www.neo4j.org/ and start it. Next step is to  run command "lein repl" from project root in terminal and in repl run command "(start-server)" to start the application.  When starting the application for the first time, app will automaticaly add one user (username: traveller, password: traveller).

Libraries used

Leiningen

Version - 2.0
Leiningen is the easiest way to use Clojure. With a focus on project automation and declarative configuration, it gets out of your way and lets you focus on your code.
http://leiningen.org/

Clojure

dependency - [org.clojure/clojure "1.5.1"]
Clojure is a dynamic programming language that targets the Java Virtual Machine. It is designed to be a general-purpose language, combining the approachability and interactive development of a scripting language with an efficient and robust infrastructure for multithreaded programming. Clojure is a compiled language - it compiles directly to JVM bytecode, yet remains completely dynamic. Every feature supported by Clojure is supported at runtime. Clojure provides easy access to the Java frameworks, with optional type hints and type inference, to ensure that calls to Java can avoid reflection.
http://clojure.org/

Ring

dependency - [ring/ring-jetty-adapter "1.1.0"] [ring/ring-core "1.2.0"]
Ring is a Clojure web applications library inspired by Python's WSGI and Ruby's Rack. By abstracting the details of HTTP into a simple, unified API, Ring allows web applications to be constructed of modular components that can be shared among a variety of applications, web servers, and web frameworks.
https://github.com/ring-clojure/ring

Valip

dependency - [com.cemerick/valip "0.3.2"]
Valip is a validation library for Clojure. It is primarily designed to validate keyword-string maps, such as one might get from a HTML form.
https://github.com/cemerick/valip

NEO4J

dependency - [clojurewerkz/neocons "1.1.0"]
NEO4J graph database is implemented with neocons library for Clojure developers.
http://www.neo4j.org/

Enlive

dependency - [enlive "1.1.1"]
Enlive is a selector-based (à la CSS) templating library for Clojure.
https://github.com/cgrand/enlive 

Sandbar

dependency - [sandbar "0.4.0-SNAPSHOT"]
Sandbar is a web application library which is designed to be used with Compojure and/or Ring. 
More documentation is located in the Sandbar Wiki.

License

Distributed under the Eclipse Public License, the same as Clojure.



