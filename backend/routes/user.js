const express = require("express");
const router = express.Router();



const { getUserById, getUser} = require("../controllers/user");  
const { isSignedIn, isAdmin, isAuthenticated } = require("../controllers/auth");

// params
router.param("userId", getUserById);



//actual routes

// get user routes
router.get("/user/myProfile/:userId", isSignedIn, isAuthenticated, getUser);

module.exports = router;