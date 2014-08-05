(ns org.akvo.flow.dashboard
  (:require [org.akvo.flow.app-state :refer (app-state)]
            [org.akvo.flow.routes]
            [org.akvo.flow.components.header :refer (header)]
            [org.akvo.flow.components.devices :as devices]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secretary.core :as secretary :include-macros true :refer (defroute)]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

(enable-console-print!)

(defmulti page (fn [data owner]
                 (:current-page data)))

(defmethod page :surveys [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "Surveys"))))

(defmethod page [:devices :devices-list] [data owner]
  (reify om/IRender
    (render [this]
      (om/build devices/devices data {:opts devices/devices-list}))))

(defmethod page :data [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "Data"))))

(defmethod page :reports [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "Reports"))))

(defmethod page :maps [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "Maps"))))

(defmethod page :users [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "Users"))))

(defmethod page :messages [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "Messages"))))

(defn app [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil
               (om/build header data)
               (dom/div #js {:id "pageWrap"}
                        (om/build page data))))))

(defn widget [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil (:text data)))))

(om/root app
         app-state
         {:target (.getElementById js/document "app")})






