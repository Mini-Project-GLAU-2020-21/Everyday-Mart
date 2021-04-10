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
