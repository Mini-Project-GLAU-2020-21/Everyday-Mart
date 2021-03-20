var mongoose = require("mongoose");
const crypto = require("crypto");
const uuidv1 = require("uuid/v1");


var userSchema = new mongoose.Schema({
    firstname: {
        type: String,
        required: true,
        maxlength: 32,
        trim: true
    },
    lastname: {
        type: String,
        maxlength: 32,
        trim: true
    },
    contactNumber: {
        type: Number,
        maxlength: 10
    },
    email: {
        type: String,
        trim: true,
        required: true,
        unique: true
    },
    encry_password: {
        type: String,
        required: true
    },
    salt: String,
    role: {
        type: Number,
        default: 0
    },
    purchases: {
        type: Array,
        default: []
    }
},
    {timestamps: true }
);




module.exports = mongoose.model("User", userSchema)