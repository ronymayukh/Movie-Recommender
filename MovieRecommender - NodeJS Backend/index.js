const express = require('express')
const {spawn} = require('child_process');
const userRouter = require("./user")
const mysqlConnection  = require("./connection");
const movieRatingRoute = require("./movieRating")
const app = express()
const port = 3000


app.use("/user" , userRouter)
app.use("/movieRating" , movieRatingRoute)

app.get('/top10/:gener', (req, res) => {

var genere = req.params.gener;

var largeDataSet = [];

 const python = spawn('python', ['best10Gener.py',genere]);

 python.stdout.on('data', function (data) {
  largeDataSet.push(data);
 });

 python.on('close', (code,signal) => {
 var list_required = largeDataSet.join("")
 list_required = list_required.trim()
 res.send(list_required)
 });
 
})

app.get('/userSpecific/:uid/:movie', (req, res) => {

    var uid = req.params.uid;
    var movie = req.params.movie;
    console.log("Recommendations" + uid)
    console.log(movie)
    
    var largeDataSet = [];
     const python = spawn('python', ['userSpecific.py',uid,movie]);
    
     python.stdout.on('data', function (data) {
      largeDataSet.push(data);
     });
    
     python.on('close', (code,signal) => {
     var list_required = largeDataSet.join("")
     list_required = list_required.trim()
     res.send(list_required)
     });
     
    })

app.get('/getRand/:uid',(req,res)=>{
    var uid = req.params.uid

    const sql = "SELECT Id FROM movies WHERE id NOT IN (SELECT DISTINCT movieId FROM ratings WHERE userId = "+uid+") ORDER BY RAND() LIMIT 10"
    mysqlConnection.query(sql,(err,row,fields)=>{
        if(!err){
            
            var result = [];

            for (let index = 0; index < row.length; index++) {
                result.push(row[index].Id);
                
            }

            console.log(result);
            res.status(202).send(result)
        }else{
            console.log(err)
        }
    })

})

app.get('/getUserLikedMovies/:uid',(req,res)=>{
    var uid = req.params.uid
    console.log("Liked Movies "+uid)

    const sql = "SELECT * FROM ratings WHERE userId = "+uid+" ORDER BY timestamp DESC"

    mysqlConnection.query(sql,(err,row,fields)=>{
        if(!err){
            
            var result = [];


            for (let index = 0; index < row.length; index++) {

                result.push({movieId: row[index].movieId, userRating: row[index].rating})

            }

            console.log(result);
            res.contentType('application/json');
            res.status(202).send(JSON.stringify(result))
        }else{
            console.log(err)
        }
    })

})
    

app.listen(port, () => console.log(`App listening on port ${port}!`))