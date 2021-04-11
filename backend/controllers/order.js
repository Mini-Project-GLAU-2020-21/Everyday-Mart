const { Order, ProductCartSchema } = require("../models/order");
const order = require("../models/order");







exports.getOrderById = (req, res, next, id) => {
    Order.findById(id)
    .populate("products.product", "name price")   
    // picking different products stored in that order and displaying        name and price      of each one         in one by one manner
    .exec((err, order) => {
        if (err){
            return res.status(400).json({
                error: "No order found in DB"
            });
        }
        req.order = order;
        next();
    });
};