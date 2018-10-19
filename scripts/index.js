const fake = require('fakerator')();
const async = require('async');
const axios = require('axios');
const qs = require('querystring');

const fakeUser = () => {
    const first_name = fake.names.firstName();
    const last_name = fake.names.lastName();
    const username = fake.internet.userName(first_name, last_name);
    const email = fake.internet.email(first_name, last_name);
    const photo_url = fake.internet.avatar();
    const displayName = first_name + ' ' + last_name;
    const password = '1';

    return { username, email, displayName, password }
}

const users = [];

for (let i=0;i<20;i++) {
    users.push(fakeUser());
}

async.eachSeries(users, (user, cb) => {
    console.log(user);
    console.log(qs.stringify(user));
    axios.post('http://vre.hcmut.edu.vn/threadripper/api/signup', qs.stringify(user))
    .then(() => cb()).catch(err => cb(err))
}, (err) => {
    if (err) console.log(err);
    else console.log('OK');

    process.exit(0);
})