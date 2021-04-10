var express = require('express');
var router = express.Router();

const {signup, signin, signout} = require("../controllers/auth");
const { check, validationResult } = require('express-validator');




router.post(
    "/signup", 
    [
        check("firstname", "First Name should be at least of 3 char.").isLength({ min: 3 }).custom(value => !/\s/.test(value)),
        check("email", "Valid email is required.").isEmail().custom(value => !/\s/.test(value)),
        check("contactNumber", "Valid Mobile Number is required.").isLength({ min: 10 }).isMobilePhone('en-IN'),
        check("password", "password should be at least 5 char.").custom(value => !/\s/.test(value)).isLength({ min: 5 })
    ], 
    signup
);

router.post(
    "/signin", 
    [           
        check("email", "Email field is required.").isEmail(),
        check("password", "Password field is required.").isLength({ min: 1 })
    ], 
    signin
);

router.get("/signout", signout);


module.exports = router;