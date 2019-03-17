module.exports = function(app, passport) {

var User       = require('../app/models/user');
var Gym       = require('../app/models/gym');

    app.get('/', function(req, res) {
        res.json({status:"Not logged in"});
    });
    app.get('/logout', function(req, res) {
        req.logout();
        res.redirect('/');
    });

    app.post('/login', passport.authenticate('local-login', {
        successRedirect : '/passAuth', 
        failureRedirect : '/login'
    }));
    app.get('/login', function(req, res) {
        res.json({status:"Failed"});
    });
    app.get('/passAuth', function(req, res) {
        res.json({status:"Passed"});
    });
    
    app.post('/listEvents', function(req, res) {
            if(req.body.gymAddr){
                
            Gym.findOne({ 'data.gymAddress' :  req.body.gymAddr }, function(err, gym) {
                console.log(gym);
                if(gym!=null){
                    
                    res.json(gym.data.reports);
                }            else{res.json({status:"Failed"});}
            });
            
            }
            else{res.json({status:"Failed"});}
    });
       
    
    app.post('/createEvent',isLoggedIn,function(req, res) {
        if(req.body.eventName&&req.user.local.realname && req.user._id && req.body.date && req.body.desc && req.body.maxpeoplecount && req.body.sTime && req.body.eTime && req.body.gymAddr){
            Gym.findOne({ 'data.gymAddress' :  req.body.gymAddr }, function(err, gym) {
                if (gym != null) {
                    var eventExists=false;
                    for(var i=0;i<gym.data.reports.length;i++){
                        
                        if(gym.data.reports[i].creatorID==req.user.id && gym.data.reports[i].eventName==req.body.eventName){
                            
                            eventExists=true;
                        }
                    }
                    console.log(eventExists);
                    if(!eventExists){
                    var reportData = {
                        creatorName:req.user.local.realname,
                        creatorID: req.user._id,
                        eventName: req.body.eventName,
                        date: new Date(req.body.date),
                        description: req.body.desc,
                        maxCount: req.body.maxpeoplecount,
                        filledCount: 0,
                        timeStart: req.body.sTime,
                        timeEnd: req.body.eTime
                    };
                    
                    console.log(reportData);
                    gym.data.reports.push(reportData);
                    
                    gym.save();
                    res.json({status:"Passed"});
                    }
                    else{res.json({status:"Failed"});}
                }
                else{
                    var newGym                = new Gym();
                    newGym.data.gymAddress    = req.body.gymAddr;
                    var reportData = {
                        creatorName:req.user.local.realname,
                        creatorID: req.user._id,
                        eventName: req.body.eventName,
                        date: new Date(req.body.date),
                        description: req.body.desc,
                        maxCount: req.body.maxpeoplecount,
                        filledCount: 0,
                        timeStart: req.body.sTime,
                        timeEnd: req.body.eTime
                    };
                    console.log(reportData);
                    newGym.data.reports.push(reportData);
                    newGym.save();
                    res.json({status:"Passed"});

                }
            });
            
            
        } 
        else{console.log("something is null");res.json({status:"Failed"});}
        
    });


    app.post('/deleteEvent',isLoggedIn,function(req, res) {
        //var newReport            = new Report();            
        if(req.body.eventName && req.user._id && req.body.gymAddr){
            Gym.findOne({ 'data.gymAddress' :  req.body.gymAddr }, function(err, gym) {
                if (gym != null) {
                    for(var i=0;i<gym.data.reports.length;i++){
                        
                        if(gym.data.reports[i].creatorID==req.user.id && gym.data.reports[i].eventName==req.body.eventName){
                            gym.data.reports.splice(i,1);
                            eventExists=true;
                            
                        }
                    }
                    gym.save();
                    res.json({status:"Passed"});
                }
                else{
                    res.json({status:"Passed"});

                }
            });
            
            
        } 
        else{console.log("something is null");res.json({status:"Failed"});}
        
    });

    app.post('/isInEvent',isLoggedIn,function(req, res) {
        if( req.user._id && req.body.eventID && req.body.gymAddr){
            Gym.findOne({ 'data.gymAddress' :  req.body.gymAddr }, function(err, gym) {
                if (gym != null) {
                    for(var i=0;i<gym.data.reports.length;i++){
                        
                        if(gym.data.reports[i]._id==req.body.eventID ){
                            var contin=false;
                            for(var q=0;q<gym.data.reports[i].goingID.length;q++){
                                    if(gym.data.reports[i].goingID[q]==req.user.id){
                                        contin=true;
                                        res.json({status:"Passed"});
                                    }
                            }
                            if(contin==false){   
                                res.json({status:"Failed"});
                            }
                                                            
                        }
                    }
                    gym.save();
                }
                else{
                    res.json({status:"Failed"});

                }
            });
            
            
        } 
        else{console.log("something is null");res.json({status:"Failed"});}
        
    });

    app.post('/leaveEvent',isLoggedIn,function(req, res) {
        if( req.user._id && req.body.eventID && req.body.gymAddr){
            Gym.findOne({ 'data.gymAddress' :  req.body.gymAddr }, function(err, gym) {
                if (gym != null) {
                    for(var i=0;i<gym.data.reports.length;i++){
                        
                        if(gym.data.reports[i]._id==req.body.eventID ){
                            for(var q=0;q<gym.data.reports[i].goingID.length;q++){
                                    if(gym.data.reports[i].goingID[q]==req.user.id){
                                        gym.data.reports[i].filledCount-=1;
                                        gym.data.reports[i].goingID.splice(q,1);
                                    }
                            }
                                                            
                        }
                    }
                    gym.save();
                    res.json({status:"Passed"});
                }
                else{
                    res.json({status:"Passed"});

                }
            });
            
            
        } 
        else{console.log("something is null");res.json({status:"Failed"});}
        
    });
    app.post('/joinEvent',isLoggedIn,function(req, res) {
        if( req.user._id && req.body.eventID && req.body.gymAddr){
            Gym.findOne({ 'data.gymAddress' :  req.body.gymAddr }, function(err, gym) {
                if (gym != null) {
                    for(var i=0;i<gym.data.reports.length;i++){
                        
                        if(gym.data.reports[i]._id==req.body.eventID ){
                            var alreadyContains=false;
                            for(var q=0;q<gym.data.reports[i].goingID.length;q++){
                                    if(gym.data.reports[i].goingID[q]==req.user.id){
                                        alreadyContains=true;
                                    }
                            }
                            if(!alreadyContains && gym.data.reports[i].filledCount < gym.data.reports[i].maxCount ){
                                gym.data.reports[i].filledCount=gym.data.reports[i].filledCount+1;//+=?
                            gym.data.reports[i].goingID.push(req.user._id);
                            
                                res.json({status:"Passed"});
                            }else{
                    res.json({status:"Failed"});}
                            
                        }
                    }
                    gym.save();
                }
                else{
                    res.json({status:"Passed"});

                }
            });
            
            
        } 
        else{console.log("something is null");res.json({status:"Failed"});}
        
    });
    
    app.post('/createAccount',function(req,res){
        if(req.body.username && req.body.password && req.body.realname){
            

            
            User.findOne({ 'local.username' :  req.body.username.toLowerCase() }, function(err, user) {
                if (user != null) {
                    res.json({status:"Failed"});
                    return;
                }
                else{
                    
                    var newUser                = new User();
                    console.log(newUser);
                    newUser.local.username    = req.body.username.toLowerCase();
                    newUser.local.password = newUser.generateHash(req.body.password);
                    newUser.local.realname     = req.body.realname;
                    console.log(newUser);
                    newUser.save();
                    res.json({status:"Passed"});
                }
            });
            
        }
        else{res.json({status:"Failed"});}
    });

        

};

function isLoggedIn(req, res, next) {
    if (req.isAuthenticated()){
        return next();
    }
    res.redirect('/');
}
function isAdminAccount(req, res, next) {
    var user            = req.user;
    if (req.isAuthenticated()&&user.local.isAdmin==true){
        return next();
    }
}
