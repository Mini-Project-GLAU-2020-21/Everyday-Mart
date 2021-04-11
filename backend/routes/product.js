const express = require("express");
const router = express.Router();

const {isSignedIn, isAuthenticated, isAdmin} = require("../controllers/auth");
const {getUserById} = require("../controllers/user");
const {getProductById, createProduct, getProduct, photo, deleteProduct, updateProduct, getAllProducts, getAllUniqueCategories } = require("../controllers/product");


// params
router.param("userId", getUserById);
router.param("productId", getProductById);




// actual routes

// create route
router.post("/product/create/:userId", isSignedIn, isAuthenticated, isAdmin, createProduct);

//read routes
router.get("/product/:productId", getProduct);
router.get("/product/photo/:productId", photo);


// delete route
router.delete("/product/:productId/:userId", isSignedIn, isAuthenticated, isAdmin, deleteProduct);

// update route
router.put("/product/:productId/:userId", isSignedIn, isAuthenticated, isAdmin, updateProduct);

// listing route
// this will be used to display all the product on homepage
router.get("/products", getAllProducts);


// route to display all the category. so that form here you can select one of any category.
router.get("/products/categories", getAllUniqueCategories)


module.exports = router;