const express = require('express');
const Router = express();
const mysqlConnection  = require("./connection");

Router.get("/login/:userEmail/:userPassword",(req,res)=>{
    const userEmail = req.params.userEmail;
    const userPassword = req.params.userPassword;

    console.log(userEmail);
    console.log(userPassword);

    let sql_query = "SELECT * FROM users WHERE userEmail = '"+userEmail+"' AND password = '"+userPassword+"'";

    mysqlConnection.query(sql_query, (err,rows,fields)=>{
        if(!err){

            if(rows.length == 0){
                res.status(400).send("User not found"); //if user is not present
            }
            else{
                console.log(rows);
                res.status(202).send(rows[0].userId+"");
                //res.send(); //if user is present
            }
            
        }
        else{
            console.log(err);
            res.status(404).send("Something went wrong"); //something went wrong with the db
        }
    })
})

Router.get("/register/:userName/:userEmail/:userPassword",(req,res)=>{
    const userName = req.params.userName;
    const userEmail = req.params.userEmail;
    const userPassword = req.params.userPassword;

    console.log(userName);
    console.log(userEmail);
    console.log(userPassword);

    let sql_query = "SELECT * FROM users WHERE userEmail = '"+userEmail+"'";

    mysqlConnection.query(sql_query, (err,rows,fields)=>{
        if(!err){

            if(rows.length == 0){
                
                sql_query = "INSERT INTO users (userName, userEmail, password) VALUES ('"+userName+"', '"+userEmail+"', '"+userPassword+"')";
                mysqlConnection.query(sql_query,(er,rows,fields)=>{
                    if(!err){

                        res.status(202).send("Success!!");
                        
                    }
                    else{
                        console.log(err);
                        res.status(404).send("Something went wrong");
                    }
                })


            }
            else{
                res.status(400).send("You are already registered!!"); //if user is present
            }
            
        }
        else{
            console.log(err);
            res.status(404).send("Something went wrong"); //something went wrong with the db
        }
    })
})

module.exports = Router;