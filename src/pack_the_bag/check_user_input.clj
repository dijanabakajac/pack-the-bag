(ns pack-the-bag.check-user-input
  (:require [valip.core :refer [validate]]
	    [valip.predicates :refer [present? matches email-address? decimal-string?]]))

(defn bag-size-input-errors
  "Credentials for check bag form"
  [params]
  (validate params
	    [:gr present? "Bag size can't be empty."]
      [:gr decimal-string? "Bag size can't be string."]))

(defn item-input-errors
  "Credentials for add item form"
  [params]
  (validate params
	    [:item present? "Item can't be empty."]
	    [:weight present? "Weight can't be empty."]
      [:weight decimal-string? "Weight can't be string."]
	    [:value present? "Value can't be empty."]
      [:value decimal-string? "Value can't be string."]))

(defn login-credential-errors
  "Credentials for login form"
  [params]
  (validate params
	    [:username present? "Username can't be empty."]
	    [:password present? "Password can't be empty."]))

(defn user-input-errors
  "Credentials for register form"
  [params]
  (validate params
	    [:name present? "Name can't be empty."]
	    [:surname present? "Surname can't be empty."]
	    [:email present? "Email can't be empty."]
	    [:email email-address? "Email not in valid format."]
	    [:password present? "Password can't be empty."]
		  [:username present? "Username can't be empty."]
	    [:age present? "Age can't be empty."]
	    [:city present? "City can't be empty."]
	    [:country present? "Country can't be empty."]
	    [:gender present? "Gender can't be empty."]))
