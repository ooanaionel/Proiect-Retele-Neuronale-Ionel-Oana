const fs = require('fs');
const path = require('path');

const DATA_DIR = path.join(__dirname, '../../data');
const PROCESSED_DIR = path.join(DATA_DIR, 'processed');

if (!fs.existsSync(PROCESSED_DIR)) fs.mkdirSync(PROCESSED_DIR, { recursive: true });

function splitData() {
    const files = fs.readdirSync(DATA_DIR).filter(f => f.endsWith('.csv'));
    let allRecords = [];

    console.log("[SPLITTER] Se citesc fișierele din: " + DATA_DIR);

    files.forEach(file => {
        const filePath = path.join(DATA_DIR, file);
        const data = fs.readFileSync(filePath, 'utf8');
        const lines = data.split('\n').slice(1); // Fără header
        lines.forEach(line => {
            if (line.trim()) allRecords.push(line);
        });
    });

    // Shuffle (Amestecare)
    allRecords.sort(() => Math.random() - 0.5);

    const trainIdx = Math.floor(allRecords.length * 0.7);
    const valIdx = Math.floor(allRecords.length * 0.85);

    const train = allRecords.slice(0, trainIdx);
    const val = allRecords.slice(trainIdx, valIdx);
    const test = allRecords.slice(valIdx);

    const header = "AWB_ID,Origin,Final_Destination,Part,Total,Weather,Timestamp\n";

    fs.writeFileSync(path.join(PROCESSED_DIR, 'train.csv'), header + train.join('\n'));
    fs.writeFileSync(path.join(PROCESSED_DIR, 'validation.csv'), header + val.join('\n'));
    fs.writeFileSync(path.join(PROCESSED_DIR, 'test.csv'), header + test.join('\n'));

    console.log(`[SPLITTER] Gata! Datele sunt în ${PROCESSED_DIR}`);
    console.log(`-> Train: ${train.length} | Val: ${val.length} | Test: ${test.length}`);
}

splitData();