var mongoose = require('mongoose');
var bcrypt   = require('bcrypt-nodejs');


var gymSchema = mongoose.Schema({
    data       :{//Tmp
        _id: mongoose.Schema.ObjectId,
        gymAddress: String,
	    reports: [{
            creatorName:String,
            creatorID: mongoose.Schema.Types.ObjectId,
            date: Date,
            description: String,
            maxCount: Number,
            filledCount: Number,
            timeStart: String,
            timeEnd: String,
            eventName: String,
            goingID:[mongoose.Schema.Types.ObjectId]
        }
        ]
    }
});





//module.exports  = mongoose.model('User', userSchema);
module.exports  = mongoose.model('Gym', gymSchema);
