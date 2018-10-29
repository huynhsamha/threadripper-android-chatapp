const fs = require('fs');

const files = fs.readdirSync('../snapshots');

let out = '';

files.forEach(file => {
    console.log(file);
    out = out + `<img src="snapshots/${file}" width="30%">\n`;
})

console.log(out);

fs.writeFileSync('./genREADME.md', out);