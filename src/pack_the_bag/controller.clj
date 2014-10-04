(ns pack-the-bag.controller
  (:use (sandbar stateful-session))
  (:require [pack-the-bag.neo4j :as n4j]
            [cljs.reader :refer [read-string]]
	          [pack-the-bag.view :as bv]
            [pack-the-bag.check-user-input :refer [login-credential-errors user-input-errors 
                                                    item-input-errors
                                                    bag-size-input-errors]])
  (:refer-clojure :exclude [read-string]))

(defn parse-double
  "Parse string to double"
  [s]
  (if (and (string? s) (re-matches #"\s*[+-]?\d+(\.\d+(M|M|N)?)?\s*" s))
    (read-string s)))

(defn logged-in
  "Checks if user is logged in"
  [response-fn]
  (if (= (session-get :id) nil)
      (bv/login)
      (do (session-pop! :login-try 1)
	  response-fn)))

(defn not-logged-in
  "Checks if user is logged in"
  [response-fn]
  (if (= (session-get :id) nil)
      response-fn
      (bv/home)))

 (defn check-user
  "Check if user exists in databse"
  [req-params]
  (let [user (:username req-params)
	password (:password req-params)]
    (doseq [[id
	     name
	     surname
		   email
       username
	     age
	     city
	     country
	     gender]
	    (:data (n4j/cypher-query (str "start n=node("(clojure.string/join ","(n4j/get-type-indexes "Traveler"))")
					   where n.username = '"user"' and n.password = '"password"'				
					   return ID(n),
						  n.name,
						  n.surname,
						  n.email,
						  n.username,
              n.password,
						  n.age,
						  n.city,
						  n.country,
						  n.gender;")))]
	(session-put! :id id)
	(session-put! :name name)
	(session-put! :surname surname)
	(session-put! :age age)
	(session-put! :city city)
	(session-put! :country country)
	(session-put! :gender gender))
	(session-put! :login-error 1)))
 
 (defstruct item  :item :weight :value)
 (defstruct item2 :id :item :weight :value)
 
 (defn all-items
  "Select all items from database"
  []
  (let [data (n4j/cypher-query (str "start n=node("(clojure.string/join ","(n4j/get-type-indexes "Item"))")				   				
					   return ID(n),
						  n.item,
						  n.weight,
						  n.value;"))] 
   (let [items (vec (map #(apply struct item2 %) (partition 4 (apply concat (:data data)))))]
         (session-put! :all-items  items))))
 
 (defn add-item
  "Save new item in database"
  [req-params]
 (if-let [user-errors (item-input-errors {:item (:item req-params)                          
					    :weight (:weight req-params)
              :value (:value req-params)})]
   (session-put! :error-save-item user-errors)
    (n4j/create-node "Item" {                             
			     :item (:item req-params)
           :weight(parse-double (str (:weight req-params)))
           :value(parse-double (str (:value req-params)))})))
 
(declare mm) ;forward decl for memoization function
 
(defn m 
 "Algorithm for items in the bag" 
 [i w items]
  (cond
    (< i 0) [0 []]
    (= w 0) [0 []]
    :else
    (let [{wi :weight vi :value} (get items i)]
      (if (> wi w)
        (mm (dec i) w items)
        (let [[vn sn :as no]  (mm (dec i) w items)
              [vy sy :as yes] (mm (dec i) (- w wi) items)]
          (if (> (+ vy vi) vn)
            [(+ vy vi) (conj sy i)]
            no))))))

(def mm (memoize m))
 
 (defn check-bag
  "Define which item should be in bag"
  [req-params]
  (if-let [user-errors (bag-size-input-errors {:gr (:gr req-params)})]
  (session-put! :bag-size-error user-errors)
  (let [gr (:gr req-params)
	      data (n4j/cypher-query (str "start n=node("(clojure.string/join ","(n4j/get-type-indexes "Item"))")				   				
					   return 
						  n.item,
						  n.weight,
						  n.value;"))]
      
  (let [items (vec (map #(apply struct item %) (partition 3 (apply concat (:data data)))))]
  (let [[value indexes] (m (-> items count dec) (parse-double gr) items)
      names (map (comp :item items) indexes)]
  (session-put! :best-choice-items names))))))
 

  (defn update-user
  "Update user in database"
  [req-params]
  (if-let [user-errors (user-input-errors {:name (:name req-params)
					    :surname (:surname req-params)
					    :email (:email req-params)
					    :username (:username req-params)
					    :password (:password req-params)
					    :age (:age req-params)
					    :city (:city req-params)
					    :country (:country req-params)
					    :gender (:gender req-params)})]
   (session-put! :update-error user-errors)
    (let [node (n4j/read-node (session-get :id))]
      (n4j/update-node node
		       {:name (:name req-params)
			      :surname (:surname req-params)
			      :email (:email req-params)
			      :username (:username req-params)
		      	:password (:password req-params)
			      :age (:age req-params)
			      :city (:city req-params)
		      	:country (:country req-params)
			      :gender (:gender req-params)}))))
  
  (defn delete-user
  "Delete user from database"
  [id]
  (n4j/delete-node "Traveler" id))
  
  (defn update-item
  "Update item in database"
  [req-params]
  (if-let [user-errors (item-input-errors {:item (:item req-params)
					    :weight (:weight req-params)
					    :value (:value req-params)})]
   (session-put! :update-error user-errors)
    (let [node (n4j/read-node (read-string (str (:id req-params))))]
      (n4j/update-node node
		       {:item (:item req-params)
			      :weight (parse-double (str (:weight req-params)))
			      :value (parse-double (str (:value req-params)))}))))
  
  (defn delete-item
  "Delete item from database"
  [id]
  (n4j/delete-node "Item" id))
  
 (defn save-user
  "Save newly registered user in database"
  [req-params]
 (if-let [user-errors (user-input-errors {:name (:name req-params)
					    :surname (:surname req-params)
					    :email (:email req-params)
					    :username (:username req-params)
					    :password (:password req-params)
					    :age (:age req-params)
					    :city (:city req-params)
					    :country (:country req-params)
					    :gender (:gender req-params)})]  
   (session-put! :error-register user-errors)
    (n4j/create-node "Traveler" {                            
			     :name (:name req-params)
			     :email (:email req-params)
			     :surname (:surname req-params)
           :username (:username req-params)
			     :password (:password req-params)
			     :age (read-string (:age req-params))
		       :city (:city req-params)
			     :country (:country req-params)
			     :gender (:gender req-params)})))