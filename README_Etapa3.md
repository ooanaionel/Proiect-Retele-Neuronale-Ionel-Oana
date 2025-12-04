2. Descrierea Setului de Date pentru Proiect
Setul de date se împarte în două categorii principale de intrare (Input) pentru sistem: Date Interne (Telemetrice), necesare pentru antrenarea modelului LSTM și Date Externe (Contextuale), folosite atât pentru antrenare, cât și pentru predicția în timp real.

2.1 Sursa datelor

Origine: Date interne de la vehicule (GPS/Telemetrice) , Date externe de la servicii terțe (Meteo, Trafic) , și date din Sistemul ERP/WMS al firmei de curierat.

Modul de achiziție: Senzori reali (GPS, Telemetrie) / Fișier extern (ERP/WMS, Comenzi) / API Call (Meteo, Trafic).

Perioada / condițiile colectării: Antrenarea inițială a modelului LSTM se realizează pe 7 zile de date istorice. Colectarea continuă se face în timp real în condiții operaționale variate (trafic, vreme, ore de vârf).


2.2 Caracteristicile dataset-ului
Număr total de observații: 50.400 ; Modelul LSTM este antrenat pe 7 zile de date istorice , cu o frecvență de înregistrare a datelor GPS telemetrice de 2 minute (pentru fiecare vehicul/cursă). 

Număr de caracteristici (features): Estimativ: 10+ (vezi descrierea de mai jos).

Tipuri de date: Numerice (viteză, timp parcurs, consum), Categoriale (ziua săptămânii, tipul de camion, condiții meteo), Temporale (ora exactă, data).

Format fișiere: JSON (Date GPS Telemetrice, Comenzi, Ieșire) , CSV (Comenzi/Task-uri, probabil pentru istoric) , API Call (Date Externe).

2.3 
| **Caracteristică** | **Tip** | **Unitate** | **Descriere** |
| ID cursa/Ruta | categorial | - |identificator unic al calatoriei|
| Locatie start/stop | categorial | - | Identificatorul depozitului de plecare și sosire (nod) |
| Ora start/ora curenta | temporal | HH:MM:SS | Ora exactă la momentul observat/plecării |
| Ziua Săptămânii/Luna | categorial | - | Ziua/Luna (influențăază traficul de vârf) |
| Distanta | numeric | km | Distanța kilometrică fixă între depozite |
| Viteza Medie | numeric | km/h |Viteza medie înregistrată pe segmentul de drum (Date GPS Telemetrice) |
| Condiții Meteo | categorial | - | Starea vremii |
| Intensitate Trafic | numeric | % | Nivelul de congestie raportat de API (ex: 0-100%) |
| Volum Marfă/Capacitate | numeric | m3/ tone | Volumul de marfă încărcat în camion |
| Timp de Tranzit Real (ETT) | numeric | minute | Variabila Țintă: Timpul efectiv necesar pentru a parcurge ruta (calculat din datele GPS) |

3. Analiza Exploratorie a Datelor (EDA) – Sintetic
3.1 Statistici descriptive aplicate
Medie, mediană, deviație standard: Aplicată pe variabila țintă Timpul de Tranzit Real (ETT) și pe Viteza Medie pentru a înțelege variațiile în funcție de ora din zi sau de condițiile meteo.

Min–max și quartile: Identificarea valorilor extreme și a distribuției pe caracteristici cheie (ETT, Distanță, Viteză).

Distribuții pe caracteristici (histograme): Analiza distribuției ETT pe intervale orare și pe Ziua Săptămânii pentru a vizualiza tiparele de trafic .

Identificarea outlierilor (IQR / percentile): Căutarea valorilor aberante pentru ETT (oprire neprevăzută sau eroare de înregistrare).


3.2 Analiza calității datelor
Detectarea valorilor lipsă (% pe coloană): Focalizare pe coloanele esențiale (Viteza Medie, Ora Curentă, ETT).

Detectarea valorilor inconsistente sau eronate: Verificarea integrității datelor temporale (secvențe cronologice) și a valorilor de viteză care depășesc limitele fizice.

Identificarea caracteristicilor redundante sau puternic corelate: Corelația dintre Distanța Statică și ETT mediu.


3.3 Probleme identificate (Exemple ipotetice bazate pe context)
Feature Viteza Medie are un procentaj mic de valori lipsă din cauza pierderii semnalului GPS.

Distribuția feature Timp de Tranzit Real (ETT) este puternic neuniformă: Variația timpului de tranzit este mult mai mare la orele de vârf (dimineață/seară).

Dezechilibru în datele Meteo/Trafic: Cazurile critice (ploaie puternică, zăpadă, accidente) care influențează cel mai mult ETT-ul sunt subreprezentate în eșantionul mic de 7 zile.


4. Preprocesarea Datelor
4.1 Curățarea datelor
Eliminare duplicatelor: Eliminarea înregistrărilor redundante.
Tratarea valorilor lipsă:

-Viteza Medie / Locație GPS lipsă: Imputare secvențială cu media mobilă (Rolling Average) sau cu valoarea anterioară (Forward Fill) pentru a menține secvența de timp.
-ETT lipsă (variabila țintă): Rândurile în care nu se poate calcula un ETT final (datorită datelor incomplete) vor fi eliminate.

Tratarea outlierilor: Outlierii de tipul "viteză fizic imposibilă" vor fi limitați (limitare la percentila 99.9% sau înlocuire cu mediana).


4.2 Transformarea caracteristicilor
Normalizare (pentru LSTM): Aplicarea Standardizării (Z-score) pe caracteristicile numerice cheie (ETT, Viteză Medie, Intensitate Trafic).

Encoding pentru variabile categoriale:
One-Hot Encoding pentru variabilele discrete (Ziua Săptămânii, Condiții Meteo).
Encoding (sau mapare ID) pentru ID Locație/Depozit.
Ajustarea dezechilibrului de clasă: Deoarece eșantionul este mic (100.800 de observații), se va acorda o atenție sporită pentru a nu introduce zgomot. O simplă pondere (weighting) în funcția de pierdere a modelului LSTM poate fi preferată în locul oversampling-ului (SMOTE), dacă datele critice sunt prea rare.

4.3 Structurarea seturilor de date
Împărțire recomandată (Split bazat pe timp):
Train: Primele 5 zile de date (pentru învățarea tiparelor).
Validation: Următoarea zi (pentru ajustarea hiperparametrilor).
Test: Ultima zi de date (pentru evaluarea finală a performanței modelului).
Principii respectate:
Fără scurgere de informație (data leakage): Setul de testare trebuie să conțină evenimente posterioare celor din setul de antrenare (split pe timp).
Statistici calculate DOAR pe train: Parametrii de Standardizare/Normalizare se calculează doar pe setul de antrenare și se aplică pe validare și test.

4.4 Salvarea rezultatelor preprocesării
Date preprocesate în data/processed/
Seturi train/val/test în foldere dedicate
Parametrii de preprocesare (media și deviația standard etc.) salvați în config/preprocessing_config.json pentru reproductibilitate.

5. Fișiere Generate în Această Etapă
data/raw/ – date brute
data/processed/ – date curățate & transformate
data/train/, data/validation/, data/test/ – seturi finale
src/preprocessing/ – codul de preprocesare
data/README.md – descrierea dataset-ului

6. Stare Etapă (de completat de student)
[x] Structură repository configurată

[x] Dataset analizat (EDA realizată)

[ ] Date preprocesate

[ ] Seturi train/val/test generate

[ ] Documentație actualizată în README + data/README.md
