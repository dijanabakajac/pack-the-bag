(ns pack-the-bag.server
  "Requests and responses on server"
  (:use compojure.core
	(sandbar stateful-session)
	[ring.middleware.params]
	[ring.middleware.multipart-params])
  (:require [compojure.handler :as handler]
	    [compojure.route :as route]
      [pack-the-bag.neo4j :as n4j]
	    [ring.adapter.jetty :as jetty]
	    [pack-the-bag.view :as bv]
	    [pack-the-bag.controller :as bc]))

;; defroutes macro defines a function that chains individual route
;; functions together. The request map is passed to each function in
;; turn, until a non-nil response is returned.
(defroutes app-routes
  ; to serve document root address
  (GET "/home"
    []
    (bc/logged-in (do
     (session-put! :error-save-item nil)
      (bv/home))))

  (GET "/login"
      [] (do 
    (session-put! :login-error nil)
    (bc/logged-in (bv/home))))
  
  (GET "/logout"
    []
    (do (destroy-session!)
	  (bc/logged-in (bv/home))))
  
  (POST "/login"
    request
    (do (bc/check-user(:params request))
        (bc/logged-in (bv/home))))
  
   (GET "/register"
    []
    (session-put! :error-register nil)
    (bc/not-logged-in (bv/register-user)))
   
   
   (GET "/all-items"
    []
   (session-put! :best-choice-items nil)
   (session-put! :bag-size-error nil)
   (bc/logged-in (do
                       (bc/all-items)
                       (bv/all-items))))
     
   (POST "/add-item"
    request
   (bc/logged-in (do (session-put! :error-save-item nil)                     
                       (session-put! :calculated-price nil)
                       (bc/add-item (:params request))
			                 (bc/logged-in (bv/home)))))
     
   (POST "/check-bag"
    request  
   (session-put! :error-save-item nil)
   (session-put! :bag-size-error nil)
   (bc/logged-in (do
                       (bc/check-bag (:params request))
                       (bv/all-items))))
       
       
   (GET "/delete-user"
    []
   (do (bc/logged-in (bc/delete-user (read-string (str (session-get :id)))))
	 (destroy-session!)
	 (bc/logged-in (bv/home))))
       
   (GET "/delete-item"
    par
   (session-put! :best-choice-items nil)
   (session-put! :bag-size-error nil)
   (bc/logged-in (do
                       (bc/delete-item (read-string (str (:id (:params par)))))
                       (bc/all-items)
                       (bv/all-items))))
     
     
   (GET "/edit-user"
      [] 
   (do (session-put! :update-error nil)
       (bc/logged-in (bv/edit-user (n4j/read-node (session-get :id))))))
  
   (POST "/edit-user"
    request
    (bc/logged-in (do (session-put! :update-error nil)
                       (bc/update-user (:params request))
			(bc/logged-in (bv/edit-user (n4j/read-node (session-get :id)))))))
 
   (POST "/update-user"
    request
    (bc/logged-in (do (bc/update-user (:params request))
			                   (bc/logged-in (bv/home)))))
     
     
   (POST "/save-user"
    request
   (do 
   (bc/not-logged-in (do (session-put! :error-register nil)
                            (bc/save-user (:params request))
   (bc/not-logged-in (bv/register-user))))))
 
  (GET "/update-item"
    request
      (bc/logged-in  
                     (bv/update-item (n4j/read-node (read-string (str (:id (:params request))))))))
  
  (POST "/update-item"
    request
   (bc/logged-in (do 
                   (println "param request update item " (:params request))
                       (bc/update-item (:params request))
	                     (bc/all-items)
                       (bv/all-items))))
  
)

;; site function creates a handler suitable for a standard website,
;; adding a bunch of standard ring middleware to app-route:
(def handler (-> (handler/site app-routes)
		 wrap-stateful-session
		 wrap-params
		 wrap-multipart-params))

(defn run-server
  "Run jetty server"
  []
  (jetty/run-jetty handler {:port 5000 :join? false}))

