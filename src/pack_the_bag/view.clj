(ns pack-the-bag.view
  (:use (sandbar stateful-session))
  (:require [net.cgrand.enlive-html :as en]
            [pack-the-bag.neo4j :as n4j]
	          [pack-the-bag.create-html :as hg]))


(en/deftemplate register-user
  (hg/build-html-page [{:temp-sel [:div.topcontent],
			:comp "public/pages/register.html",
			:comp-sel [:form#register-form]}])
[]
  [:title] (en/content "Register user")
  [:div.topcontent] (en/set-attr :class "register")
  [:td#back] (en/content {:tag :a,
			     :attrs {:href "http://localhost:5000/login"},
			     :content "back to login"})
 [:td#error-msgs] (if (not= (session-get :error-register) nil)
		  (en/content {:tag :div,
			       :attrs {:class "help"}
			       :content "Input error"  })))

(en/deftemplate home
   (hg/build-html-page [{:temp-sel [:div.left-column],
			:comp "public/pages/home.html",
			:comp-sel [:form#add-item]}])
  []
  [:title] (en/content "Home")
  [:td#error-msgs] (if (not= (session-get :error-save-item) nil)
			  (en/content {:tag :div,
				       :attrs {:class "help"}
				       :content "Input error"  })))

(en/deftemplate login
  (hg/build-html-page [{:temp-sel [:div.topcontent],
			:comp "public/pages/login.html",
			:comp-sel [:form#login]}])
  []
  [:title] (en/content "Login")
  [:div.topcontent] (en/set-attr :class "login")
  [:td#error-msgs] (if (not= (session-get :login-error) nil)   
			 (en/content {:tag :div,
				       :attrs {:class "help"}
				      :content "You need to be registered."  })))

(en/deftemplate update-item
   (hg/build-html-page [{:temp-sel [:div.left-column],
			:comp "public/pages/home.html",
			:comp-sel [:form#add-item]}])
  [node]
  [:title] (en/content "Edit item")
  [:input#item] (en/set-attr :value (:item (:data node)))
  [:input#weight] (en/set-attr :value (:weight (:data node)))
  [:input#value] (en/set-attr :value (:value (:data node)))
  [:form#add-item] (en/set-attr :action (str"/update-item?id="(:id node)))
  [:td#error-msgs] (if (not= (session-get :error-save-item) nil)
			  (en/content {:tag :div,
				       :attrs {:class "help"}
				       :content "Input error"  })))

(en/defsnippet post-snippet "public/pages/items.html"
  [ [:div.one-item] ]
  [post]
  [:div.one-item] 
  (en/content  { :tag :span,
			     :attrs {:class "span-item"},
			     :content (str (:item post))},
              { :tag :span,
			     :attrs {:class "span-weight"},
			     :content (str (:weight post))},
              { :tag :span,
			     :attrs {:class "span-value"},
			     :content (str (:value post))},
               { :tag :a,
			     :attrs {:href (str"http://localhost:5000/delete-item?id="(:id post))
                :class "link"},
			     :content "Delete item"},
                 { :tag :a,
			     :attrs {:href (str"http://localhost:5000/update-item?id="(:id post))
                :class "link"},
			     :content "Update item"}))

(en/defsnippet best-item-snippet "public/pages/items.html"
  { [:span.item1] [[:div.bbb (en/nth-of-type 1)]]}
  [post]
  [:title] (en/content "Items for bag")
  [:span.item1] (en/content (str post)))

(en/deftemplate all-items 
  (hg/build-html-page [{:temp-sel [:div.left-column],
	:comp "public/pages/items.html",
		:comp-sel [:div.all-items]}
                       {:temp-sel [:div.middle-column],
			:comp "public/pages/items.html",
			:comp-sel [:div.check-bag]}
                       {:temp-sel [:div.right-column],
			:comp "public/pages/items.html",
			:comp-sel [:div.best-choice]}])
  []
  [:title] (en/content "All items")
  [:div.all-items] (en/content (map post-snippet (session-get :all-items)))
  [:div.best-choice] (en/content (map best-item-snippet (session-get :best-choice-items) ))
  [:td#error-msgs] (if (not= (session-get :bag-size-error) nil)
			  (en/content {:tag :div,
				       :attrs {:class "help"}
				       :content "Input error"  })))

(en/deftemplate edit-user
  (hg/build-html-page [{:temp-sel [:div.maincontent],
			:comp "public/pages/register.html",
			:comp-sel [:form#register-form]}])
   [node]
  [:title] (en/content "Edit user")
  [:input#name] (en/set-attr :value (:name (:data node)))
  [:input#surname] (en/set-attr :value (:surname (:data node)))
  [:input#email] (en/set-attr :value (:email (:data node)))
  [:input#username] (en/set-attr :value (:username (:data node)))
  [:input#password] (en/set-attr :value (:password (:data node)))
  [:input#age] (en/set-attr :value (:age (:data node)))
  [:input#city] (en/set-attr :value (:city (:data node)))
  [:input#country] (en/set-attr :value (:country (:data node)))
  [:input#gender-male] (if (= (:gender (:data node)) "Male")
			  (en/set-attr :checked "checked")
			  (en/set-attr :name "gender"))
  [:input#gender-female] (if (= (:gender (:data node)) "Female")
			    (en/set-attr :checked "checked")
			    (en/set-attr :name "gender"))
  [:form#register-form] (en/set-attr :action "/edit-user")
  [:td#error-msgs] (if (not= (session-get :update-error) nil)
		  (en/content {:tag :div,
			       :attrs {:class "help"}
			       :content "Input error"  })))

