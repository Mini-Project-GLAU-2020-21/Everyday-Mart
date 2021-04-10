const Product = require("../models/product");
const formidable = require("formidable");
const _ = require("lodash");
const fs = require("fs");
const { size, sortBy } = require("lodash");



exports.getProductById = (req, res, next, id) => {
    Product.findById(id)
    .populate("category")
    .exec((err, product) => {
        if (err) {
            return res.status(400).json({
                error: "Product not found"
            })
        }
        req.product = product;
        next();
    });
};


exports.getProduct = (req, res) => {         
    req.product.photo = undefined           
    return res.json(req.product)
}