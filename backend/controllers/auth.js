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


exports.signin = (req, res) => {
    const error = validationResult(req);
    const { email, password } = req.body;

    if(!error.isEmpty()){
        return res.status(422).json({
            error: error.array()[0].msg
        });
    };

    User.findOne({email}, (err, user) =>{
        if(err || !user){
            return res.status(400).json({
                error: "User's email does not exists!!!"
            })
        }

        if(!user.authenticate(password)){
            return res.status(401).json({
                error: "Email and password do not match!!!"
            })
        }

        //Create token
        const token = jwt.sign({_id: user._id}, process.env.SECRET_Key);
        //put token in cookie
        res.cookie("token", token, {expire: new Date() + 9999});

        //send response on frontend
        const {_id, firstname, lastname, email, contactNumber, role} = user;
                
        return res.json({ token, user: {_id, firstname, lastname, email, contactNumber, role}})
    })

}


exports.signout = (req, res) => {
    res.clearCookie("token");
    res.json({
        message: "User signout successfully"
    });
};







//protected routes


exports.isSignedIn = expressJwt({
    secret: process.env.SECRET_Key,
    userProperty: "auth"
});


//custom middlewares

exports.isAdmin = (req, res, next) => {
    if( req.profile.role === 0 ){
        return res.status(403).json({
            error: "Access Denied !!! You are not authorized as you are not admin..."
        });
    }
    next();
}