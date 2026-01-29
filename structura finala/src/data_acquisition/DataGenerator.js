const fs = require('fs');
const path = require('path');

const hubs = ["HUB_BUCURESTI", "HUB_OTOPENI", "HUB_PLOIESTI", "HUB_BRASOV", "HUB_SIBIU", "HUB_CLUJ", "HUB_ORADEA", "HUB_TIMISOARA", "HUB_ARAD", "HUB_CRAIOVA", "HUB_PITESTI", "HUB_CONSTANTA", "HUB_GALATI", "HUB_IASI", "HUB_BACAU"];

// Mergem doi pași înapoi din src/data_aquisition pentru a pune folderul data în rădăcina proiectului
const DATA_DIR = path.join(__dirname, '../../data');

function generate48hData() {
    const start = new Date("2026-01-21T00:00:00");
    if (!fs.existsSync(DATA_DIR)) fs.mkdirSync(DATA_DIR, { recursive: true });

    for (let i = 0; i < 8; i++) {
        const time = new Date(start.getTime() + i * 6 * 60 * 60 * 1000);
        const datePart = time.toISOString().split('T')[0];
        const fileName = path.join(DATA_DIR, `telemetry_${datePart}_H${time.getHours()}.csv`);

        let content = "AWB_ID,Origin,Final_Destination,Part,Total,Weather,Timestamp\n";

        hubs.forEach(origin => {
            // Generăm un volum random pentru antrenarea LSTM
            const count = Math.floor(Math.random() * 40) + 15;
            for (let j = 0; j < count; j++) {
                const dest = hubs[Math.floor(Math.random() * hubs.length)];
                if (origin === dest) continue;

                const awbBase = `AWB${Math.random().toString(36).substring(7).toUpperCase()}`;
                const totalParts = Math.floor(Math.random() * 3) + 1;

                for (let p = 1; p <= totalParts; p++) {
                    content += `${awbBase},${origin},${dest},${p},${totalParts},SUNNY,${time.toISOString()}\n`;
                }
            }
        });

        fs.writeFileSync(fileName, content);
        console.log(`[GENERATOR] Fișier creat: ${fileName}`);
    }
}

generate48hData();