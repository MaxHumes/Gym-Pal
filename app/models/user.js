var mongoose = require('mongoose');
var bcrypt   = require('bcrypt-nodejs');

var userSchema = mongoose.Schema({
    local       :{
        username        : String,
        password        : String,
        realname            : String,
	    isAdmin		: Boolean,
        lastLogin: [],
	    gymTimes: [],//we want this to be an array of times with a start and end
	    gymLocations: [],//this should just be an array of id's
	    workOuts: [],
	    _id: mongoose.Schema.ObjectId
    }
});







userSchema.methods.generateHash = function(password) {
    return bcrypt.hashSync(password, bcrypt.genSaltSync(8), null);
};

userSchema.methods.validPassword = function(password) {
    console.log(password);
    console.log(bcrypt.hashSync(password, bcrypt.genSaltSync(8), null));
    return bcrypt.compareSync(password, this.local.password);
};

module.exports  = mongoose.model('User', userSchema);
