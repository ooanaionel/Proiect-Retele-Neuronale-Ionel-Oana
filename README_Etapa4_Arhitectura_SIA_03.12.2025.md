# 📘 README – Etapa 4: Arhitectura Completă a Aplicației SIA bazată pe Rețele Neuronale

**Disciplina:** Rețele Neuronale  
**Instituție:** POLITEHNICA București – FIIR  
**Student:** Ionel Oana  
**Link Repository GitHub** https://github.com/ooanaionel/Proiect-Retele-Neuronale-Ionel-Oana
**Data:** 4.12.2025 
---

## Scopul Etapei 4

Această etapă corespunde punctului **5. Dezvoltarea arhitecturii aplicației software bazată pe RN** din lista de 9 etape - slide 2 **RN Specificatii proiect.pdf**.

**Trebuie să livrați un SCHELET COMPLET și FUNCȚIONAL al întregului Sistem cu Inteligență Artificială (SIA). In acest stadiu modelul RN este doar definit și compilat (fără antrenare serioasă).**

---

##  Livrabile Obligatorii

### 1. Tabelul Nevoie Reală → Soluție SIA → Modul Software (max ½ pagină)

| **Nevoie reală concretă** | **Cum o rezolvă SIA-ul vostru** | **Modul software responsabil** |
|---------------------------|--------------------------------|--------------------------------|

|Timp de Sosire Estimativ (ETA) inexact din cauza traficului dinamic |Predicție ETA dinamic (Acuratețe Predictivă) → deviație de la ETA < 10 minute | Modul 2: Rețeaua Neuronală LSTM (Modul Predicție)|
| Pierderi financiare și întârziere (Middle-Mile) din cauza rutelor statice/manuale |Optimizare Dinamică a Rutelor (VRP) → Reducere costuri carburant și operare cu 20% |Modul 2: Algoritm Genetic (Modul Optimizare) |
| Perturbarea operațiunilor de sortare (Last-Mile) din cauza întârzierilor | Replanificare Dinamică a rutei în timp real → Creșterea punctualității livrărilor cu 35% | Modul 3: API RESTful (Interfață Dispecer) + Logică de Replanificare |

---

### 2. Contribuția Voastră Originală la Setul de Date – MINIM 40% din Totalul Observațiilor Finale

**Regula generală:** Din totalul de **N observații finale** în `data/processed/`, **minimum 40%** trebuie să fie **contribuția voastră originală**.


#### Tipuri de contribuții acceptate (exemple din inginerie):

Alegeți UNA sau MAI MULTE dintre variantele de mai jos și **demonstrați clar în repository**:

| **Tip contribuție** | **Exemple concrete din inginerie** | **Dovada minimă cerută** |
|---------------------|-------------------------------------|--------------------------|
| **Date generate prin simulare fizică** | • Traiectorii robot în Gazebo<br>• Vibrații motor cu zgomot aleator calibrat<br>• Consumuri energetice proces industrial simulat | Cod Python/LabVIEW funcțional + grafice comparative (simulat vs real din literatură) + justificare parametri |
| **Date achiziționate cu senzori proprii** | • 500-2000 măsurători accelerometru pe motor<br>• 100-1000 imagini capturate cu cameră montată pe robot<br>• 200-1000 semnale GPS/IMU de pe platformă mobilă<br>• Temperaturi/presiuni procesate din Arduino/ESP32 | Foto setup experimental + CSV-uri produse + descriere protocol achiziție (frecvență, durata, condiții) |
| **Etichetare/adnotare manuală** | • Etichetat manual 1000+ imagini defecte sudură<br>• Anotat 500+ secvențe video cu comportamente robot<br>• Clasificat manual 2000+ semnale vibrații (normal/anomalie)<br>• Marcat manual 1500+ puncte de interes în planuri tehnice | Fișier Excel/JSON cu labels + capturi ecran tool etichetare + log timestamp-uri lucru |
| **Date sintetice prin metode avansate** | • Simulări FEM/CFD pentru date dinamice proces | Cod implementare metodă + exemple before/after + justificare hiperparametri + validare pe subset real |

```markdown
### Contribuția originală la setul de date:

**Total observații finale:** 50.400 (după Etapa 3 + Etapa 4)
**Observații originale:** 50.400 (100%)

**Tipul contribuției:**
[X] Date generate prin simulare fizică  
[ ] Date achiziționate cu senzori proprii  
[ ] Etichetare/adnotare manuală  
[ ] Date sintetice prin metode avansate  

**Descriere detaliată:**
Acest script de generare a fost utilizat pentru a crea setul de date original ("telemetry_raw.csv"), care simulează datele GPS telemetrice și variabilele dinamice necesare pentru antrenarea rețelei LSTM. Am simulat 7 zile de operațiuni cu 20 de camioane distincte, înregistrând o observație la fiecare 2 minute pe parcursul fiecărei curse (perioada de simulare: 2025-11-27 până la 2025-12-03). Metoda de generare se axează pe introducerea de factori dinamici și stocastici pentru a modela cu precizie variația Timpului de Tranzit Efectiv (ETT), depășind astfel limitările datelor bazate pe distanța statică.

Datele au fost generate prin simularea a două categorii de variabile: statice (locația de start/stop, distanța, volumul de marfă) și dinamice (viteza, vremea, traficul). Variabilele dinamice sunt manipulate în funcție de ora din zi (currentTime.getHours()) pentru a modela vârfurile de trafic (orele 7-9 și 16-18), unde viteza medie (care are o bază de 80 km/h) este redusă la 50%-80% din viteză. În plus, au fost introduse condiții meteo severe (Ploaie, Ninsoare, Ceață) cu o probabilitate de 10%, aceste condiții aplicând o penalitate suplimentară de reducere a vitezei cu 30%. Această abordare de simulare fizică este crucială, deoarece oferă modelului LSTM variabilele secvențiale complexe (serii de timp) de care are nevoie pentru a învăța tiparele de întârziere și pentru a reduce marja de eroare a predicției ETA la sub 10 minute.

**Locația codului:** `src/data_acquisition/DataGenerator.js`
**Locația datelor:** `data/processed/telemetry_raw.csv` 

**Dovezi:**
- Grafic comparativ: `docs/generated_vs_real.png`
- Setup experimental: `docs/acquisition_setup.jpg` (dacă aplicabil)
- Tabel statistici: `docs/data_statistics.csv`
---

### 3. Diagrama State Machine a Întregului Sistem (OBLIGATORIE)

**Cerințe:**
- **Minimum 4-6 stări clare** cu tranziții între ele
- **Formate acceptate:** PNG/SVG, pptx, draw.io 
- **Locație:** `docs/state_machine.*` (orice extensie)
- **Legendă obligatorie:** 1-2 paragrafe în acest README: "De ce ați ales acest State Machine pentru nevoia voastră?"

**Stări tipice pentru un SIA:**
```
IDLE → ACQUIRE_DATA → PREPROCESS → INFERENCE → DISPLAY/ACT → LOG → [ERROR] → STOP
                ↑______________________________________________|
```

### Justificarea State Machine-ului ales:

Am ales o arhitectură de tip Monitorizare Continuă și Replanificare Dinamică, deoarece proiectul necesită o buclă de feedback constantă pentru a adapta rutele în timp real la factori dinamici și stocastici (trafic, vreme). Această buclă asigură că modelul nu doar planifică, ci și reacționează la evenimente neprevăzute, un aspect critic în logistica inter-depozit (Middle-Mile).

Stările principale sunt:
1. IDLE: Sistemul așteaptă o nouă cerere de rutare (lista de depozite de vizitat) din partea Dispecerului sau a sistemului ERP/WMS.
2. ACQUIRE_DYNAMIC_DATA: Colectează datele de intrare externe critice (vremea curentă de pe traseu și datele de trafic) prin API-uri.
3. PREDICT_ETA_LSTM: Modulul LSTM rulează inferența, generând o matrice dinamică de Timpi de Tranzit Estimați (ETT-uri) de înaltă precizie între toate hub-urile, pe baza datelor secvențiale colectate. Aceste ETT-uri devin funcția de cost.
4. OPTIMIZE_ROUTE_GA: Modulul Algoritm Genetic (GA) utilizează matricea de ETT-uri dinamice pentru a calcula Ruta Optimă care minimizează simultan timpul, costul și distanța, respectând constrângerile de capacitate a vehiculelor.
5. EXECUTE_MONITOR: Ruta optimă este transmisă Dispecerului și Modulului Mobil (Șofer). Sistemul trece în modul de monitorizare, înregistrând datele GPS Telemetrice ale camionului.
...

Tranzițiile critice sunt:
- EXECUTE_MONITOR → CRITICAL_CHECK: Se întâmplă la fiecare interval fix (ex: 5 minute) sau după un segment important de rută parcurs, pentru a evalua starea curentă.

- CRITICAL_CHECK → PREDICT_ETA_LSTM: Aceasta este tranziția cheie de Replanificare Dinamică. Este declanșată doar dacă deviația față de ETA-ul inițial depășește un prag critic (ex: > 15 minute). Sistemul re-evaluează costurile și re-optimizează ruta din punctul curent.

Starea ERROR este esențială pentru că datele externe (API-uri Meteo/Trafic) se pot deconecta. Dacă apare o eroare, sistemul intră într-o strategie de Fall-back (ex: folosește ultimul cost dinamic cunoscut sau, ca ultimă soluție, revine la costul static bazat pe distanța kilometrică) și alertează dispecerul, fără a opri operațiunile.

Bucla de feedback este dublă:

1. Bucla de Control (Timp Real): EXECUTE_MONITOR → CRITICAL_CHECK → PREDICT_ETA_LSTM → OPTIMIZE_ROUTE_GA → EXECUTE_MONITOR.
2. Bucla de Învățare (Offline): Datele GPS telemetrice colectate în EXECUTE_MONITOR sunt stocate în Baza de Date Istorică pentru re-antrenarea periodică a modelului LSTM, asigurând adaptabilitatea și acuratețea predictivă continuă.
```

---

### 4. Scheletul Complet al celor 3 Module Cerute la Curs (slide 7)

Toate cele 3 module trebuie să **pornească și să ruleze fără erori** la predare. Nu trebuie să fie perfecte, dar trebuie să demonstreze că înțelegeți arhitectura.

| **Modul** | **Tehnologie aleasa** | **Cerință minimă funcțională (la predare)** |
|-----------|----------------------------------|----------------------------------------------|
| **1. Data Logging / Acquisition** | Node.js/JavaScript (generator) | MUST: Scriptul rulează (ex: node generate_data.js), creează fișierul CSV cu datele originale.|
| **2. Neural Network Module** | Java (Spring Boot, DL4J, JGAP) | MUST: O clasă Java LSTM_Model (DL4J) definită și configurată. Clasa RouteOptimizerGA (JGAP) definită. Proiectul Maven/Gradle compilează fără erori. |
| **3. Web Service / UI** | Java Spring Boot (API RESTful) | MUST: Un endpoint /optimize_route pornește, primește o cerere HTTP POST și returnează un răspuns JSON  (chiar dacă rezultatul e neantrenat) |

#### Detalii per modul:

#### **Modul 1: Data Logging / Acquisition**

**Funcționalități obligatorii:**
- [x] Cod rulează fără erori: `src/data_acquisition/dataGenerate.js` 
- [x] Generează CSV în format compatibil cu preprocesarea din Etapa 3
- [x] Include minimum 40% date originale în dataset-ul final
- [x] Documentație în cod: ce date generează, cu ce parametri

#### **Modul 2: Neural Network Module**

**Funcționalități obligatorii:**
- [ ] Arhitectură RN definită și compilată fără erori
- [ ] Model poate fi salvat și reîncărcat
- [ ] Include justificare pentru arhitectura aleasă (în docstring sau README)
- [ ] **NU trebuie antrenat** cu performanță bună (weights pot fi random)


#### **Modul 3: Web Service / UI**

**Funcționalități MINIME obligatorii:**
- [ ] Propunere Interfață ce primește input de la user (formular, file upload, sau API endpoint)
- [ ] Includeți un screenshot demonstrativ în `docs/screenshots/`

**Ce NU e necesar în Etapa 4:**
- UI frumos/profesionist cu grafică avansată
- Funcționalități multiple (istorice, comparații, statistici)
- Predicții corecte (modelul e neantrenat, e normal să fie incorect)
- Deployment în cloud sau server de producție

**Scop:** Prima demonstrație că pipeline-ul end-to-end funcționează: input user → preprocess → model → output.


## Structura Repository-ului la Finalul Etapei 4 (OBLIGATORIE)

**Verificare consistență cu Etapa 3:**

```
proiect-rn-[nume-prenume]/
├── data/
│   ├── raw/
│   ├── processed/
│   ├── generated/  # Date originale
│   ├── train/
│   ├── validation/
│   └── test/
├── src/
│   ├── data_acquisition/
│   ├── preprocessing/  # Din Etapa 3
│   ├── neural_network/
│   └── app/  # UI schelet
├── docs/
│   ├── state_machine.*           #(state_machine.png sau state_machine.pptx sau state_machine.drawio)
│   └── [alte dovezi]
├── models/  # Untrained model
├── config/
├── README.md
├── README_Etapa3.md              # (deja existent)
├── README_Etapa4_Arhitectura_SIA.md              # ← acest fișier completat (în rădăcină)
└── requirements.txt  # Sau .lvproj
```

**Diferențe față de Etapa 3:**
- Adăugat `data/generated/` pentru contribuția dvs originală
- Adăugat `src/data_acquisition/` - MODUL 1
- Adăugat `src/neural_network/` - MODUL 2
- Adăugat `src/app/` - MODUL 3
- Adăugat `models/` pentru model neantrenat
- Adăugat `docs/state_machine.png` - OBLIGATORIU
- Adăugat `docs/screenshots/` pentru demonstrație UI

---

## Checklist Final – Bifați Totul Înainte de Predare

### Documentație și Structură
- [x] Tabelul Nevoie → Soluție → Modul complet (minimum 2 rânduri cu exemple concrete completate in README_Etapa4_Arhitectura_SIA.md)
- [X] Declarație contribuție 40% date originale completată în README_Etapa4_Arhitectura_SIA.md
- [X] Cod generare/achiziție date funcțional și documentat
- [X] Dovezi contribuție originală: grafice + log + statistici în `docs/`
- [ ] Diagrama State Machine creată și salvată în `docs/state_machine.*`
- [ ] Legendă State Machine scrisă în README_Etapa4_Arhitectura_SIA.md (minimum 1-2 paragrafe cu justificare)
- [ ] Repository structurat conform modelului de mai sus (verificat consistență cu Etapa 3)

### Modul 1: Data Logging / Acquisition
- [X] Cod rulează fără erori 
- [X] Produce minimum 40% date originale din dataset-ul final
- [X] CSV generat în format compatibil cu preprocesarea din Etapa 3
- [X] Documentație în `src/data_acquisition/README.md` cu:
  - [X] Metodă de generare/achiziție explicată
  - [X] Parametri folosiți (frecvență, durată, zgomot, etc.)
  - [X] Justificare relevanță date pentru problema voastră
- [X] Fișiere în `data/generated/` conform structurii

### Modul 2: Neural Network
- [ ] Arhitectură RN definită și documentată în cod (docstring detaliat) - versiunea inițială 
- [ ] README în `src/neural_network/` cu detalii arhitectură curentă

### Modul 3: Web Service / UI
- [ ] Propunere Interfață ce pornește fără erori (comanda de lansare testată)
- [ ] Screenshot demonstrativ în `docs/screenshots/ui_demo.png`
- [ ] README în `src/app/` cu instrucțiuni lansare (comenzi exacte)

---

**Predarea se face prin commit pe GitHub cu mesajul:**  
`"Etapa 4 completă - Arhitectură SIA funcțională"`

**Tag obligatoriu:**  
`git tag -a v0.4-architecture -m "Etapa 4 - Skeleton complet SIA"`


