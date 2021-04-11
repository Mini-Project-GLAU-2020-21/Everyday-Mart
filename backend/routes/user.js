const express = require("express");
const router = express.Router();



const { getUserById, getUser, updateUser, userPurchaseList } = require("../controllers/user");  
const { isSignedIn, isAdmin, isAuthenticated } = require("../controllers/auth");

// params
router.param("userId", getUserById);



//actual routes

// get user routes
router.get("/user/myProfile/:userId", isSignedIn, isAuthenticated, getUser);
//update user
router.put("/user/:userId", isSignedIn, isAuthenticated, updateUser);

// get order list of user.
router.get("/orders/user/:userId", isSignedIn, isAuthenticated, userPurchaseList);

module.exports = router;