const express = require("express");
const router = express.Router();

const {isSignedIn, isAuthenticated, isAdmin} = require("../controllers/auth");
const {getUserById, pushOrderInPurchaseList} = require("../controllers/user");
const {updateStock, getProductById} = require("../controllers/product");

const {getOrderById, createOrder, getAllOrders, getOrderStatus, updateStatus} = require("../controllers/order");




// params
router.param("userId", getUserById);
router.param("orderId", getOrderById);




// actual routes

// create
router.post("/order/create/:userId", isSignedIn, isAuthenticated, pushOrderInPurchaseList, updateStock, createOrder);

// read
router.get("/order/all/:userId", isSignedIn, isAuthenticated, isAdmin, getAllOrders);


// routes for status of orders
router.get("/order/status/:userId", isSignedIn, isAuthenticated, isAdmin, getOrderStatus);
router.put("/order/:orderId/order/:userId", isSignedIn, isAuthenticated, isAdmin, updateStatus);

module.exports = router;