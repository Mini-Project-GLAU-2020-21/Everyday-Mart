const User = require("../models/user");
const { check, validationResult } = require('express-validator');
var jwt = require('jsonwebtoken');
var expressJwt = require('express-jwt');





exports.signup = (req, res) => {

    const error = validationResult(req);

    if(!error.isEmpty()){
        return res.status(422).json({
            error: error.array()[0].msg
        });
    };

   
    const user = new User(req.body)
    user.save((err, user) => {
        if(err || !user){
            return res.status(400).json({
                error: "Not able to save your details in our database."
            });
        }
        res.json({
            firstname: user.firstname,
            lastname: user.l_name,
            email: user.email,
            id: user._id,
            contactNumber: user.contactNumber
        });
    });
};


