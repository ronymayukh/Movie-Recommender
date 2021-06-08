const express = require('express');
const Router = express();
const mysqlConnection  = require("./connection");

Router.get("/:movieId/:userId/:userRating",(req,res)=>{
    const movieId = req.params.movieId;
    const userId = req.params.userId;
    const userRating = req.params.userRating;

    console.log(userId+"rated it"+userRating);

    console.log(userId);

    let sql_query = "SELECT id, vote_average, vote_count FROM movies WHERE id = '"+movieId+"'";

    mysqlConnection.query(sql_query, (err,rows,fields)=>{
        if(!err){

            if(rows.length == 0){
                res.status(404).send("Movie not found"); //if user is not present
            }
            else{
                console.log(rows);
                const vote_average = rows[0].vote_average;
                const vote_count = rows[0].vote_count;
                const userRatingOn10 = userRating*2;
                const new_vote_average = ((vote_average*vote_count) + userRatingOn10)/(vote_count+1);
                const new_vote_count = vote_count+1;
                console.log("NEW :"+new_vote_average+" "+new_vote_count);

                sql_query = "UPDATE movies SET vote_average = "+new_vote_average+", vote_count = "+new_vote_count+" WHERE id = "+movieId;

                mysqlConnection.query(sql_query, (err,rows,fields)=>{
                    if(!err){
                        sql_query = "INSERT INTO ratings (userId, movieId, rating) VALUES ("+userId+", "+movieId+", "+userRating+")";

                        mysqlConnection.query(sql_query, (err,rows,fields)=>{
                            if(!err){
                                res.status(202).send("Successfully Rated!");
                                console.log(sql_query)
                            }else{
                                res.status(404).send("Something happend while updating rating tabel!");
                            }
                        })


                    }else{
                        console.log(err);
                        res.status(404).send("Something went wrong updating Movie Rating");
                    }
                })

            }
            
        }
        else{
            console.log(err);
            res.status(404).send("Something went wrong"); //something went wrong with the db
        }
    })
})


module.exports = Router;