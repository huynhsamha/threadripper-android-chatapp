const users = require('./db.json');

const async = require('async');
const axios = require('axios');
const qs = require('querystring');


async.eachSeries(users, (user, cb) => {
    console.log(user);

    axios.post('http://vre.hcmut.edu.vn/threadripper/api/signup2', qs.stringify(user))
    .then(() => cb()).catch(err => cb(err))

}, (err) => {
    if (err) console.log(err);
    else console.log('OK');

    process.exit(0);
})
