const User = require("../models/user");

exports.getUserById = (req, res, next, id) => {
    User.findById(id).exec((err, user) => {
        if(err || !user){
            return res.status(400).json({
                error: "No user found in DB"
            });
        }
        req.profile = user;
        next();
    });
};



exports.getUser = (req, res) => {
    req.profile.salt = undefined;
    req.profile.encry_password = undefined;
    req.profile.createdAt = undefined;
    return res.json(req.profile);
};



// to update user except password.
exports.updateUser = (req, res) => {
    User.findByIdAndUpdate(
        {_id : req.profile._id},
        {$set: req.body},
        {new: true, useFindAndModify: false},
        (err, user) => {
            if(err){
                return res.status(400).json({
                    error: "You are not authorized to update this detail."
                });
            }
            user.salt = undefined;
            user.encry_password = undefined;
            user.createdAt = undefined;
            res.json(user);
        }
    );
};


// to get user's purchase list
exports.userPurchaseList = (req, res) => {
    Order.find({user: req.profile._id})
    .populate("user", "_id firstname")
    .exec( (err, order) => {
        if(err){
            return res.status(400).json({
                error: "No Order in this account"
            });
        }
        return res.json(order)
    });
};
