require('dotenv').config()

const mongoose = require("mongoose");
const express = require("express");
const app = express();
const bodyParser = require("body-parser");
const cookieParser = require("cookie-parser");
const cors = require("cors");

//My routes



//DB connection
mongoose.connect(process.env.DATABASE, { 
    useNewUrlParser: true,
    useUnifiedTopology: true,
    useCreateIndex: true
}).then(() => {
    console.log("DB CONNECTED...");
}).catch( () => {
    console.log("DB GOT OOPS!!")
});


//These are middlewares.
app.use(bodyParser.json());             //use url encoded instead of bodyParser.jason()....go and check dis on bodyParser site,How to use it.
app.use(cookieParser());
app.use(cors());


//Routes



//Port
const port = process.env.PORT || process.env.Local_PORT;


//Starting a server
app.listen(port, () => {
    console.log(`App is running at ${port}`);
});