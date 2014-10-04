(ns pack-the-bag.create-html
   "Namespace for generating html pages"
  (:require [net.cgrand.enlive-html :as en]))

(defn generate-html-resource
  "Using parameter template creates html resource and map variable
   components with following properties: temp-sel, comp and comp-sel."
  [template components]
  (en/html-resource
    (en/transform
      (en/html-resource template)
      (:temp-sel components)
      (fn [selected-tag]
	  (assoc selected-tag :content (en/select (en/html-resource (:comp components)) (:comp-sel components)))))))

(defn build-html-page
  "Build an html page using vector of maps.  Each map should contain
   template selector where a component, like other map property will be 
  added. Also, component selector is representing some or all of component HTML file."
  [files-and-selectors]
  (reduce generate-html-resource (en/html-resource "public/template.html") files-and-selectors))

