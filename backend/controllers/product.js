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




exports.createProduct = (req, res) => {
    let form = new formidable.IncomingForm();
    form.keepExtensions = true;

    form.parse(req, (err, fields, file) => {
        if (err) {
            return res.status(400).json({
                error: "Problem with file (image)"
            });
        }

        // destructure the field
        const { price, name, description, category, stock } = fields;


        if(
            !name ||
            !description ||
            !price ||
            !category ||
            !stock 
        ) {
            return res.status(400).json({
                error: "Please include all the fields"
            });
        }
        
        let product = new Product(fields);


        // handling file here
        if(file.photo){
            if(file.photo.size > 3000000) {           // checking the size of the file should be less than 3000000 bytes
                return res.status(400).json({
                    error: "File is too big"
                })
            }
            product.photo.data = fs.readFileSync(file.photo.path)
            product.photo.contentType = file.photo.type

        }

        // saving file to DB
        product.save((err, product) => {
            if (err) {
                return res.status(400).json({
                    error: "Failed to save this product."
                });
            }
            return res.json(product)
        });
    });
};







exports.deleteProduct = (req, res) => {
    let product = req.product;
    product.remove((err, deletedproduct) => {
        if(err) {
            return res.status(400).json({
                error: "Failed to delete this product."
            });
        }
        res.json({
            message: "Deletion was successfull. Deleted product :", deletedproduct
        });
    });
};




exports.updateProduct = (req, res) => {  

    let form = new formidable.IncomingForm();
    form.keepExtensions = true;


    form.parse(req, (err, fields, file) => {
        if (err) {
            return res.status(400).json({
                error: "Problem with file (image)"
            });
        }




        
        // destructure the field
        
        const { price, name, description, category, stock } = fields;

        if(!name || !description || !price || !category || !stock ) {
            return res.status(400).json({
                error: "Please include all the fields"
            });
        }
        



        // updation code
        let product = req.product;
        product = _.extend(product, fields);

        // handle file here
        if(file.photo){
            if(file.photo.size > 3000000) {           // checking the size of the file should be less than 3000000 bytes
                return res.status(400).json({
                    error: "File is too big"
                });
            }
            product.photo.data = fs.readFileSync(file.photo.path)
            product.photo.contentType = file.photo.type

        }

        // saving file to DB
        product.save((err, product) => {
            if (err) {
                return res.status(400).json({
                    error: "Updation of this product in DB failed."
                });
            }
            return res.json(product);
        });
    });
};




// product listing
exports.getAllProducts = (req, res) => {
    let limit = req.query.limit ? parseInt(req.query.limit) : 8;

    let sortBy = req.query.sortBy ? req.query.sortBy : "_id";

    Product.find()
    .select("-photo")
    .populate("category")
    .sort([[sortBy, "asc"]])                    
    .limit(limit)                               
    .exec((err, products) => {
        if(err) {
            return res.status(400).json({
                error: "No Product found"
            });
        }
        res.json(products)
    });
};


// all unique categories
exports.getAllUniqueCategories = (req, res) => {
    Product.distinct("category", {}, (err, category) =>{
        if(err) {
            return res.status(400).json({
                error: "No category found"
            });
        }
        res.json(category);
    });
};



exports.updateStock = (req, res, next) => {
    let myOperations = req.body.order.product.map(prod => {
        return {
            updateOne: {
                filter: {_id: prod._id},
                update: {$inc: {stock: -prod.count, sold: +prod.count}}
            }
        };
    });

    Product.bulkWrite(myOperations, {}, (err, products) => {
        if(err) {
            return res.status(400).json({
                error: "Bulk operation failed"
            });
        }
        next();
    });
};



// middleware

exports.photo = (req, res, next) => {           
    if(req.product.photo){                      
        res.set("Content-Type", req.product.photo.contentType)
        return res.send(req.product.photo.data)
    }
    next();
};