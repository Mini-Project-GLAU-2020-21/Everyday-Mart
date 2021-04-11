const { Order, ProductCartSchema } = require("../models/order");
const order = require("../models/order");







exports.getOrderById = (req, res, next, id) => {
    Order.findById(id)
    .populate("products.product", "name price")   
    // picking different products stored in that order and displaying        name and price      of each one         in one by one manner
    .exec((err, order) => {
        if (err){
            return res.status(400).json({
                error: "No order found in DB."
            });
        }
        req.order = order;
        next();
    });
};




exports.createOrder = (req, res) => {
    req.body.order.user = req.profile;
    const order = new Order(req.body.order);
    order.save((err, order) => {
        if(err) {
            return res.status(400).json({
                error: "Failed to save your order in DB."
            });
        }
        res.json(order);
    });
};


exports.getAllOrders = (req, res) => {
    Order.find()
    .populate("user", "_id firstname email")
    .exec((err, order) => {
        if(err) {
            return res.status(400).json({
                error: "Found no order in DB."
            });
        }
        res.json(order);
    });
};


exports.getOrderStatus = (req, res) => {
    res.json(Order.schema.path("status").enumValues);
};