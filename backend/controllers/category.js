const Category = require("../models/category");

exports.getCategoryById = (req, res, next, id) => {


    Category.findById(id).exec((err, cate) => {         //cate is being used as shortform of category..we can use category as well
        if(err){
            return res.status(400).json({
                error: "Category not found in DB"
            });
        }
        req.category = cate;                      //cate is being used as shortform of category..as we have declared it as cate above
        next();
    });    
};


exports.createCategory = (req, res) => {
    const category = new Category(req.body);
    category.save((err, category) => {
        if(err){
            return res.status(400).json({
                error: "Not able to save this category in DB"
            });
        }
        res.json({category});
    });
};