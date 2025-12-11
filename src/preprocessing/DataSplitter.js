const fs = require('fs');
const path = require('path');

// --- Configurare ---
const INPUT_FILE = path.join(__dirname, '..', '..', 'data', 'processed', 'telemtry_raw.csv');

const OUTPUT_DIR = path.join(__dirname, '..', '..', 'data');
const TRAIN_DIR = path.join(OUTPUT_DIR, 'train');
const VALIDATION_DIR = path.join(OUTPUT_DIR, 'validation');
const TEST_DIR = path.join(OUTPUT_DIR, 'test');

const TRAIN_RATIO = 0.70;
const VALIDATION_RATIO = 0.15;
// Test ratio will be the remainder (0.15)

/**
 * Algoritmul Fisher-Yates (Knuth) pentru amestecarea elementelor într-un array.
 * Asigură o distribuție aleatorie uniformă a setului de date.
 * @param {Array<string>} array Array-ul de rânduri de date.
 * @returns {Array<string>} Array-ul amestecat.
 */
function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
}

/**
 * Scrie rândurile de date într-un fișier, adăugând header-ul.
 * @param {string} filePath Calea completă către fișierul de ieșire.
 * @param {string} header Rândul de antet (header-ul CSV).
 * @param {Array<string>} records Rândurile de date care trebuie scrise.
 */
function writeDataset(filePath, header, records) {
    // Adaugă header-ul la început și unește rândurile cu '\n'
    const content = [header, ...records].join('\n');
    fs.writeFileSync(filePath, content, 'utf8');
    console.log(`[SUCCES] Scris ${records.length} în ${filePath}`);
}

function runSplitter() {
    try {
        // 1. Asigură-te că directoarele de ieșire există
        [TRAIN_DIR, VALIDATION_DIR, TEST_DIR].forEach(dir => {
            if (!fs.existsSync(dir)) {
                fs.mkdirSync(dir, { recursive: true });
            }
        });

        // 2. Citirea conținutului întregului fișier
        const fileContent = fs.readFileSync(INPUT_FILE, 'utf8');
        
        // 3. Separarea rândurilor și identificarea header-ului
        let records = fileContent.split('\n').filter(line => line.trim() !== '');
        
        // Presupunem că primul rând este header-ul
        const header = records.shift(); 
        const totalRecords = records.length;
        
        console.log(`Total înregistrări de împărțit: ${totalRecords}`);

        // 4. Amestecarea (Shuffling)
        const shuffledRecords = shuffleArray(records);

        // 5. Calcularea indicilor de împărțire
        const trainEndIndex = Math.round(totalRecords * TRAIN_RATIO);
        const validationEndIndex = trainEndIndex + Math.round(totalRecords * VALIDATION_RATIO);
        
        // Ajustare pentru a asigura că suma totală este N
        if (validationEndIndex > totalRecords) {
            validationEndIndex = totalRecords;
        }

        // 6. Împărțirea seturilor
        const trainSet = shuffledRecords.slice(0, trainEndIndex);
        const validationSet = shuffledRecords.slice(trainEndIndex, validationEndIndex);
        const testSet = shuffledRecords.slice(validationEndIndex);

        // 7. Scrierea seturilor în fișiere
        writeDataset(path.join(TRAIN_DIR, 'train_set.csv'), header, trainSet);
        writeDataset(path.join(VALIDATION_DIR, 'validation_set.csv'), header, validationSet);
        writeDataset(path.join(TEST_DIR, 'test_set.csv'), header, testSet);
        
        console.log('\n✓ Împărțirea setului de date a fost finalizată cu succes.');

    } catch (error) {
        console.error(`[EROARE] A eșuat împărțirea datelor: ${error.message}`);
        process.exit(1);
    }
}

runSplitter();