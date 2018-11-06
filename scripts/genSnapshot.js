const fs = require('fs');

const files = fs.readdirSync('../snapshots');

let out = '';

files.forEach(file => {
    console.log(file);
    out = out + `
<p align="center">
    <img src="snapshots/${file}" width="30%">
</p>
    `;
})

console.log(out);

fs.writeFileSync('./genREADME.md', out);