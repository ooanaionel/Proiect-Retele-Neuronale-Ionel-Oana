const fs = require('fs');
const path = require('path');

// --- Configurații Globale ---
const NUM_CAMIOANE = 20;
const PERIOADA_ZILE = 7; // O saptamana
const FRECVENTA_MIN = 2; // La 2 minute
const FISIER_IESIRE = path.join(__dirname, 'telemetry_raw.csv');

const LOCATII = ["HUB_BUC", "DEP_BV", "HUB_CLJ", "DEP_TM", "HUB_IASI", "DEP_AG", "DEP_CT", "DEP_GL"];
const VREME = ["Senin", "Ploaie", "Ceata", "Ninsoare", "Vant"];
const HEADER = "timestamp,id_cursa,id_camion,locatie_start,locatie_stop,distanta_km,ora_start,viteza_medie_curenta,conditii_meteo,intensitate_trafic,volum_marfa_m3,ett_real_min\n";

let totalObservatii = 0;
let idCursa = 1;

// --- Funcții Utilitare ---

/**
 * Returneaza un numar intreg aleatoriu intre min si max (inclusiv).
 */
function getRandomInt(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

/**
 * Genereaza o cursa de baza cu datele statice.
 */
function generateBaseCourse() {
    const startLoc = LOCATII[getRandomInt(0, LOCATII.length - 1)];
    let stopLoc;
    do {
        stopLoc = LOCATII[getRandomInt(0, LOCATII.length - 1)];
    } while (startLoc === stopLoc);

    const distantaKm = (100 + Math.random() * 350).toFixed(1);
    const volumMarfa = (10.0 + Math.random() * 45.0).toFixed(1);

    // Simuleaza ETT de baza (minute): (Distanta / 70 km/h) * 60 min/h
    const ettBaza = (parseFloat(distantaKm) / 70.0) * 60;
    // Adauga variatie (pana la 50% intarziere)
    const ettReal = (ettBaza * (1.0 + Math.random() * 0.5)).toFixed(1); 
    
    // Simulam ca observatiile se inregistreaza pe toata durata ETT-ului + o mica marja
    const numObservatiiPeSegment = Math.floor(parseFloat(ettReal) / FRECVENTA_MIN) + 10;

    return {
        startLoc, stopLoc, distantaKm, volumMarfa, ettReal, numObservatiiPeSegment
    };
}

/**
 * Genereaza logica dinamica (viteza, trafic, vreme) pe baza orei.
 */
function getDynamicFactors(hour) {
    let factorViteza = 1.0;
    let intensitateTrafic = 10;
    let conditiiMeteo = VREME[0]; // Implicit: Senin

    if (hour >= 7 && hour <= 9) { // Ore de varf dimineata
        factorViteza = 0.5 + Math.random() * 0.3; // Viteza redusa
        intensitateTrafic = getRandomInt(60, 90);
    } else if (hour >= 16 && hour <= 18) { // Ore de varf seara
        factorViteza = 0.6 + Math.random() * 0.3; 
        intensitateTrafic = getRandomInt(70, 95); 
    } else if (hour >= 0 && hour <= 4) { // Noapte
        factorViteza = 0.9 + Math.random() * 0.1; // Viteza mare
        intensitateTrafic = getRandomInt(5, 20); 
    }

    // Logica Vremea (evenimente rare)
    if (Math.random() < 0.1) { // 10% sanse de vreme rea
        conditiiMeteo = VREME[getRandomInt(1, VREME.length - 1)]; // Evitam 'Senin'
        if (conditiiMeteo === "Ninsoare" || conditiiMeteo === "Ploaie" || conditiiMeteo === "Ceață") {
            factorViteza *= 0.7; // Reduce viteza
        }
    }

    // Variație gaussiană (simulând variația naturală de viteză)
    let vitezaMedieCurenta = 80.0 * factorViteza * (1 + (Math.random() * 0.1 - 0.05));
    
    // Asigura o viteza minima si rotunjeste
    vitezaMedieCurenta = Math.max(5.0, vitezaMedieCurenta);

    return {
        vitezaMedieCurenta: vitezaMedieCurenta.toFixed(1),
        intensitateTrafic,
        conditiiMeteo
    };
}

// --- Funcția Principală de Generare ---
function generateTelemetryData() {
    let dataOutput = HEADER;
    let totalRows = 0;
    let startDay = new Date('2025-11-27T00:00:00');

    for (let zi = 0; zi < PERIOADA_ZILE; zi++) {
        let currentTime = new Date(startDay);

        for (let camionId = 1; camionId <= NUM_CAMIOANE; camionId++) {
            
            // Simulam ca fiecare camion incepe cursa la un moment diferit in prima zi
            currentTime.setHours(getRandomInt(0, 23));
            currentTime.setMinutes(getRandomInt(0, 59));
            currentTime.setSeconds(0);
            
            const baseCourse = generateBaseCourse();
            
            const oraStart = `${String(currentTime.getHours()).padStart(2, '0')}:${String(currentTime.getMinutes()).padStart(2, '0')}:${String(currentTime.getSeconds()).padStart(2, '0')}`;

            for (let i = 0; i < baseCourse.numObservatiiPeSegment; i++) {
                
                const dynamicFactors = getDynamicFactors(currentTime.getHours());
                
                const row = [
                    currentTime.toISOString().replace('T', ' ').substring(0, 19), // timestamp format
                    idCursa,
                    camionId,
                    baseCourse.startLoc,
                    baseCourse.stopLoc,
                    baseCourse.distantaKm,
                    oraStart,
                    dynamicFactors.vitezaMedieCurenta,
                    dynamicFactors.conditiiMeteo,
                    dynamicFactors.intensitateTrafic,
                    baseCourse.volumMarfa,
                    baseCourse.ettReal
                ].join(',');
                
                dataOutput += row + '\n';
                
                // Actualizeaza timpul pentru urmatoarea observatie
                currentTime.setMinutes(currentTime.getMinutes() + FRECVENTA_MIN);
                totalRows++;
            }
            idCursa++; // Urmatoarea cursa
        }
        startDay.setDate(startDay.getDate() + 1); // Trecerea la ziua urmatoare (daca PERIOADA_ZILE > 1)
    }

    // --- Salvarea Fișierului ---
    try {
        fs.writeFileSync(FISIER_IESIRE, dataOutput);
        console.log(` Generare finalizată. Fișierul '${FISIER_IESIRE}' a fost creat.`);
        console.log(`   Total observații generate: ${totalRows}`);
    } catch (err) {
        console.error("Eroare la scrierea în fișier:", err);
    }
}

// Rulare
generateTelemetryData();