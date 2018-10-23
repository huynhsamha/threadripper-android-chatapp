const fake = require('fakerator')();
const async = require('async');
const axios = require('axios');
const qs = require('querystring');

const fakeUser = () => {
    const first_name = fake.names.firstName();
    const last_name = fake.names.lastName();
    const username = fake.internet.userName(first_name, last_name);
    const email = fake.internet.email(first_name, last_name);
    const avatarUrl = fake.internet.avatar();
    const displayName = first_name + ' ' + last_name;
    const password = String(username).substr(0,1) + '1';

    return { username, email, displayName, password, avatarUrl }
}

const users = [
// {
//     username: 'ha',
//     email: 'huynhha@gmail.com',
//     password: '1',
//     displayName: 'Huynh Ha',
//     avatarUrl: 'https://abc.com/xyz.png'
// }
];

for (let i=0;i<50;i++) {
    users.push(fakeUser());
}

async.eachSeries(users, (user, cb) => {
    console.log(user);
    console.log(qs.stringify(user));
    axios.post('http://vre.hcmut.edu.vn/threadripper/api/signup2', qs.stringify(user))
    .then(() => cb()).catch(err => cb(err))
}, (err) => {
    if (err) console.log(err);
    else console.log('OK');

    process.exit(0);
})
