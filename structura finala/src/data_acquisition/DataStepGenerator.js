const fs = require('fs');
const path = require('path');

const hubs = ["HUB_BUCURESTI", "HUB_OTOPENI", "HUB_PLOIESTI", "HUB_BRASOV", "HUB_SIBIU", "HUB_CLUJ", "HUB_ORADEA", "HUB_TIMISOARA", "HUB_ARAD", "HUB_CRAIOVA", "HUB_PITESTI", "HUB_CONSTANTA", "HUB_GALATI", "HUB_IASI", "HUB_BACAU"];

const PROJECT_ROOT = process.cwd();
const NEW_DATA_DIR = path.join(PROJECT_ROOT, 'data', 'simulation_steps');
const OLD_DATA_DIR = path.join(PROJECT_ROOT, 'data', 'generated');

function generateNextStep() {
    try {
        if (!fs.existsSync(NEW_DATA_DIR)) fs.mkdirSync(NEW_DATA_DIR, { recursive: true });

        const getFiles = (dir) => fs.existsSync(dir)
            ? fs.readdirSync(dir).filter(f => f.endsWith('.csv')).map(f => path.join(dir, f))
            : [];

        const allFilePaths = [...getFiles(OLD_DATA_DIR), ...getFiles(NEW_DATA_DIR)];

        // ✅ LOGICĂ DE SIGURANȚĂ: Dacă ai șters tot, pornim de aici
        let lastDay = "2026-01-22";
        let lastHour = 12; // Pornim de la H12 ca să generăm H18 (ultimul din zi)

        if (allFilePaths.length > 0) {
            allFilePaths.sort((a, b) => fs.statSync(a).mtimeMs - fs.statSync(b).mtimeMs);
            const lastFileName = path.basename(allFilePaths[allFilePaths.length - 1]);

            const match = lastFileName.match(/telemetry_(\d{4}-\d{2}-\d{2})_H(\d+)/);
            if (match) {
                lastDay = match[1];
                lastHour = parseInt(match[2]);
            }
        }

        let nextDay = lastDay;
        let nextHour;

        // Logica ta specifică de ore
        if (lastHour === 0) nextHour = 6;
        else if (lastHour === 6) nextHour = 12;
        else if (lastHour === 12) nextHour = 18;
        else if (lastHour === 18) {
            nextHour = 0;
        }

        // Schimbăm ziua doar după ce trecem de ora 0 spre ora 6
        if (lastHour === 0 && nextHour === 6) {
            let d = new Date(lastDay);
            d.setDate(d.getDate() + 1);
            nextDay = d.toISOString().split('T')[0];
        }

        const fileName = `telemetry_${nextDay}_H${nextHour}.csv`;
        const fullPath = path.join(NEW_DATA_DIR, fileName);

        // ✅ GENERARE RANDOM COLETE (Logica cerută de tine)
        const targetCount = Math.floor(Math.random() * (300 - 150 + 1)) + 150;
        let actualGenerated = 0;
        let content = "AWB_ID,Origin,Final_Destination,Part,Total,Weather,Timestamp\n";

        while (actualGenerated < targetCount) {
            const origin = hubs[Math.floor(Math.random() * hubs.length)];
            const dest = hubs[Math.floor(Math.random() * hubs.length)];
            if (origin === dest) continue;

            const awb = `AWB${Math.random().toString(36).substring(7).toUpperCase()}`;
            content += `${awb},${origin},${dest},1,1,SUNNY,${nextDay}T${nextHour}:00:00\n`;
            actualGenerated++;
        }

        fs.writeFileSync(fullPath, content);
        console.log(`[SUCCES] Generat: ${fileName} cu ${actualGenerated} colete.`);

    } catch (err) {
        console.error("[ERROR]", err);
        process.exit(1);
    }
}

generateNextStep();