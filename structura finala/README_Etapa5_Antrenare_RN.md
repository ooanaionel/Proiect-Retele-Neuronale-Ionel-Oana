# 📘 README – Etapa 5: Configurarea și Antrenarea Modelului RN

**Disciplina:** Rețele Neuronale  
**Instituție:** POLITEHNICA București – FIIR  
**Student:** Ionel Oana  
**Link Repository GitHub:** (https://github.com/ooanaionel/Proiect-Retele-Neuronale-Ionel-Oana) 
**Data predării:** 11.12.2025

---

## Scopul Etapei 5

Această etapă corespunde punctului **6. Configurarea și antrenarea modelului RN** din lista de 9 etape - slide 2 **RN Specificatii proiect.pdf**.

**Obiectiv principal:** Antrenarea efectivă a modelului RN definit în Etapa 4, evaluarea performanței și integrarea în aplicația completă.

**Pornire obligatorie:** Arhitectura completă și funcțională din Etapa 4:
- State Machine definit și justificat
- Cele 3 module funcționale (Data Logging, RN, UI)
- Minimum 40% date originale în dataset

---

## PREREQUISITE – Verificare Etapa 4 (OBLIGATORIU)

**Înainte de a începe Etapa 5, verificați că aveți din Etapa 4:**

- [x] **State Machine** definit și documentat în `docs/state_machine.*`
- [x] **Contribuție ≥40% date originale** în `data/generated/` (verificabil)
- [x] **Modul 1 (Data Logging)** funcțional - produce CSV-uri
- [x] **Modul 2 (RN)** cu arhitectură definită dar NEANTRENATĂ (`models/untrained_model.h5`)
- [x] **Modul 3 (UI/Web Service)** funcțional cu model dummy
- [x] **Tabelul "Nevoie → Soluție → Modul"** complet în README Etapa 4

** Dacă oricare din punctele de mai sus lipsește → reveniți la Etapa 4 înainte de a continua.**

---


##  Cerințe Structurate pe 3 Niveluri

### Nivel 1 – Obligatoriu pentru Toți (70% din punctaj)

Completați **TOATE** punctele următoare:

1. **Antrenare model** definit în Etapa 4 pe setul final de date (≥40% originale)
2. **Minimum 10 epoci**, batch size 8–32
3. **Împărțire stratificată** train/validation/test: 70% / 15% / 15%
4. **Tabel justificare hiperparametri** (vezi secțiunea de mai jos - OBLIGATORIU)
5. **Metrici calculate pe test set:**
   - **Acuratețe ≥ 65%**
   - **F1-score (macro) ≥ 0.60**
6. **Salvare model antrenat** în `models/trained_model.h5` (Keras/TensorFlow) sau `.pt` (PyTorch) sau `.lvmodel` (LabVIEW)
7. **Integrare în UI din Etapa 4:**
   - UI trebuie să încarce modelul ANTRENAT (nu dummy)
   - Inferență REALĂ demonstrată
   - Screenshot în `docs/screenshots/inference_real.png`

#### Tabel Hiperparametri și Justificări (OBLIGATORIU - Nivel 1)

Completați tabelul cu hiperparametrii folosiți și **justificați fiecare alegere**:

| **Hiperparametru** | **Valoare Aleasă** | **Justificare** |
|--------------------|-------------------|-----------------|
| Learning rate | 0.005 | Valoare preluată din configurația DL4J a scheletului din Etapa 4. Oferă un pas de învățare echilibrat, evitând divergența și asigurând convergență eficientă pentru rețelele RNN/LSTM. |
| Batch size | 32 | Un echilibru optim între stabilitatea gradientului și eficiența computațională. Pentru $N=50.400$ de observații, rezultă $\approx 1575$ de iterații/epocă. Este o dimensiune standard care maximizează utilizarea memoriei GPU/CPU fără a introduce prea mult zgomot. |
| Number of epochs | 10+ (Minimum 10) | Va fi utilizat un mecanism de Early Stopping cu o răbdare (patience) de 5-10 epoci pentru a preveni supra-antrenarea (overfitting) și a opri antrenarea când îmbunătățirea pierderii (Loss) pe setul de validare stagnează. |
| Optimizer | Adam | Un algoritm de optimizare adaptiv (Adaptive Moment Estimation) excelent pentru date de tip serie de timp și modele RNN/LSTM. Calculează rate de învățare individuale pentru fiecare parametru al rețelei. |
| Loss function | Mean Squared Error (MSE) | Esențial pentru problema noastră de Regresie (predicția unei valori continue: Timpul de Tranzit Efectiv ETT). MSE penalizează erorile mari mai |
| Activation functions | Hidden Layers: Tanh / Output Layer: Identity | Tanh (tangentă hiperbolică) este preferat pentru straturile LSTM (în locul ReLU) deoarece produce ieșiri între $(-1, 1)$, ajutând la stabilizarea gradientului în RNN. Identity (liniară) este obligatorie pentru stratul de ieșire deoarece predicem o valoare numerică continuă (timpul în minute). |

**Justificare detaliată batch size:**
```
Am ales batch_size=32 pe un set de date final de N=50.400 de observații, ceea ce duce la 50.400 / 32 = 1.575 de iterații pe epocă.Acest compromis oferă următoarele avantaje:
-Stabilitatea Gradientului: Un batch de 32 de observații reduce variația zgomotului în estimarea gradientului, comparativ cu un batch de 8 (care ar fi prea zgomotos), asigurând o cale de convergență mai lină.
-Viteza de Antrenare: Permite o vectorizare eficientă pe hardware-ul modern (GPU/CPU), oferind o viteză de procesare mult mai mare decât antrenarea stochastică pură (batch size 1) și fără riscul de a depăși memoria (Out of Memory) asociat cu batch-uri extrem de mari (ex: 256 sau mai mult).
-Memoria Utiliizată: Este o alegere conservatoare care se potrivește cu resursele de calcul tipice, asigurând că antrenarea poate fi executată rapid și eficient.
```

---

### Nivel 2 – Recomandat (85-90% din punctaj)

Includeți **TOATE** cerințele Nivel 1 + următoarele:

1. **Early Stopping** - oprirea antrenării dacă `val_loss` nu scade în 5 epoci consecutive
2. **Learning Rate Scheduler** - `ReduceLROnPlateau` sau `StepLR`
3. **Augmentări relevante domeniu:**
   - Vibrații motor: zgomot gaussian calibrat, jitter temporal
   - Imagini industriale: slight perspective, lighting variation (nu rotații simple!)
   - Serii temporale: time warping, magnitude warping
4. **Grafic loss și val_loss** în funcție de epoci salvat în `docs/loss_curve.png`
5. **Analiză erori context industrial** (vezi secțiunea dedicată mai jos - OBLIGATORIU Nivel 2)

**Indicatori țintă Nivel 2:**
- **Acuratețe ≥ 75%**
- **F1-score (macro) ≥ 0.70**

**Resurse învățare (aplicații industriale):**
- Albumentations: https://albumentations.ai/docs/examples/   
- Early Stopping + ReduceLROnPlateau în Keras: https://keras.io/api/callbacks/   
- Scheduler în PyTorch: https://pytorch.org/docs/stable/optim.html#how-to-adjust-learning-rate 

---

### Nivel 3 – Bonus (până la 100%)

**Punctaj bonus per activitate:**

| **Activitate** |  **Livrabil** |
|----------------|--------------|
| Comparare 2+ arhitecturi diferite | Tabel comparativ + justificare alegere finală în README |
| Export ONNX/TFLite + benchmark latență | Fișier `models/final_model.onnx` + demonstrație <50ms |
| Confusion Matrix + analiză 5 exemple greșite | `docs/confusion_matrix.png` + analiză în README |

**Resurse bonus:**
- Export ONNX din PyTorch: [PyTorch ONNX Tutorial](https://pytorch.org/tutorials/beginner/onnx/export_simple_model_to_onnx_tutorial.html)
- TensorFlow Lite converter: [TFLite Conversion Guide](https://www.tensorflow.org/lite/convert)
- Confusion Matrix analiză: [Scikit-learn Confusion Matrix](https://scikit-learn.org/stable/modules/generated/sklearn.metrics.confusion_matrix.html)

---

## Verificare Consistență cu State Machine (Etapa 4)

Antrenarea și inferența trebuie să respecte fluxul din State Machine-ul vostru definit în Etapa 4.

**Exemplu pentru monitorizare vibrații lagăr:**

| **Stare din Etapa 4** | **Implementare în Etapa 5** |
|-----------------------|-----------------------------|
| `ACQUIRE_DATA` | Citire batch date din `data/train/` pentru antrenare |
| `PREPROCESS` | Aplicare scaler salvat din `config/preprocessing_params.pkl` |
| `RN_INFERENCE` | Forward pass cu model ANTRENAT (nu weights random) |
| `THRESHOLD_CHECK` | Clasificare Normal/Uzură pe baza output RN antrenat |
| `ALERT` | Trigger în UI bazat pe predicție modelului real |

**În `src/app/main.py` (UI actualizat):**

Verificați că **TOATE stările** din State Machine sunt implementate cu modelul antrenat:

```python
# ÎNAINTE (Etapa 4 - model dummy):
model = keras.models.load_model('models/untrained_model.h5')  # weights random
prediction = model.predict(input_scaled)  # output aproape aleator

# ACUM (Etapa 5 - model antrenat):
model = keras.models.load_model('models/trained_model.h5')  # weights antrenate
prediction = model.predict(input_scaled)  # predicție REALĂ și corectă
```

---

## Analiză Erori în Context Industrial (OBLIGATORIU Nivel 2)

**Nu e suficient să raportați doar acuratețea globală.** Analizați performanța în contextul aplicației voastre industriale:

### 1. Pe ce clase greșește cel mai mult modelul?

**Exemplu robotică (predicție traiectorii):**
```
Confusion Matrix arată că modelul confundă 'viraj stânga' cu 'viraj dreapta' în 18% din cazuri.
Cauză posibilă: Features-urile IMU (gyro_z) sunt simetrice pentru viraje în direcții opuse.
```

**Completați pentru proiectul vostru:**
```
[Descrieți confuziile principale între clase și cauzele posibile]
```

### 2. Ce caracteristici ale datelor cauzează erori?

**Exemplu vibrații motor:**
```
Modelul eșuează când zgomotul de fond depășește 40% din amplitudinea semnalului util.
În mediul industrial, acest nivel de zgomot apare când mai multe motoare funcționează simultan.
```

**Completați pentru proiectul vostru:**
```
[Identificați condițiile în care modelul are performanță slabă]
```

### 3. Ce implicații are pentru aplicația industrială?

**Exemplu detectare defecte sudură:**
```
FALSE NEGATIVES (defect nedetectat): CRITIC → risc rupere sudură în exploatare
FALSE POSITIVES (alarmă falsă): ACCEPTABIL → piesa este re-inspectată manual

Prioritate: Minimizare false negatives chiar dacă cresc false positives.
Soluție: Ajustare threshold clasificare de la 0.5 → 0.3 pentru clasa 'defect'.
```

**Completați pentru proiectul vostru:**
```
[Analizați impactul erorilor în contextul aplicației voastre și prioritizați]
```

### 4. Ce măsuri corective propuneți?

**Exemplu clasificare imagini piese:**
```
Măsuri corective:
1. Colectare 500+ imagini adiționale pentru clasa minoritară 'zgârietură ușoară'
2. Implementare filtrare Gaussian blur pentru reducere zgomot cameră industrială
3. Augmentare perspective pentru simulare unghiuri camera variabile (±15°)
4. Re-antrenare cu class weights: [1.0, 2.5, 1.2] pentru echilibrare
```

**Completați pentru proiectul vostru:**
```
[Propuneți minimum 3 măsuri concrete pentru îmbunătățire]
```

---

## Structura Repository-ului la Finalul Etapei 5

**Clarificare organizare:** Vom folosi **README-uri separate** pentru fiecare etapă în folderul `docs/`:

```
proiect-rn-[prenume-nume]/
├── README.md                           # Overview general proiect (actualizat)
├── etapa3_analiza_date.md         # Din Etapa 3
├── etapa4_arhitectura_sia.md      # Din Etapa 4
├── etapa5_antrenare_model.md      # ← ACEST FIȘIER (completat)
│
├── docs/
│   ├── state_machine.png              # Din Etapa 4
│   ├── loss_curve.png                 # NOU - Grafic antrenare
│   ├── confusion_matrix.png           # (opțional - Nivel 3)
│   └── screenshots/
│       ├── inference_real.png         # NOU - OBLIGATORIU
│       └── ui_demo.png                # Din Etapa 4
│
├── data/                               # Din Etapa 3-4 (NESCHIMBAT)
│   ├── raw/
│   ├── generated/                     # Contribuția voastră 40%
│   ├── processed/
│   ├── train/
│   ├── validation/
│   └── test/
│
├── src/
│   ├── data_acquisition/              # Din Etapa 4
│   ├── preprocessing/                 # Din Etapa 3
│   │   └── combine_datasets.py        # NOU (dacă ați adăugat date în Etapa 4)
│   ├── neural_network/
│   │   ├── model.py                   # Din Etapa 4
│   │   ├── train.py                   # NOU - Script antrenare
│   │   └── evaluate.py                # NOU - Script evaluare
│   └── app/
│       └── main.py                    # ACTUALIZAT - încarcă model antrenat
│
├── models/
│   ├── untrained_model.h5             # Din Etapa 4
│   ├── trained_model.h5               # NOU - OBLIGATORIU
│   └── final_model.onnx               # (opțional - Nivel 3 bonus)
│
├── results/                            # NOU - Folder rezultate antrenare
│   ├── training_history.csv           # OBLIGATORIU - toate epoch-urile
│   ├── test_metrics.json              # Metrici finale pe test set
│   └── hyperparameters.yaml           # Hiperparametri folosiți
│
├── config/
│   └── preprocessing_params.pkl       # Din Etapa 3 (NESCHIMBAT)
│
├── requirements.txt                    # Actualizat
└── .gitignore
```

**Diferențe față de Etapa 4:**
- Adăugat `docs/etapa5_antrenare_model.md` (acest fișier)
- Adăugat `docs/loss_curve.png` (Nivel 2)
- Adăugat `models/trained_model.h5` - OBLIGATORIU
- Adăugat `results/` cu history și metrici
- Adăugat `src/neural_network/train.py` și `evaluate.py`
- Actualizat `src/app/main.py` să încarce model antrenat

---

## Instrucțiuni de Rulare (Actualizate față de Etapa 4)

### 1. Setup mediu (dacă nu ați făcut deja)

```bash
pip install -r requirements.txt
```

### 2. Pregătire date (DACĂ ați adăugat date noi în Etapa 4)

```bash
# Combinare + reprocesare dataset complet
python src/preprocessing/combine_datasets.py
python src/preprocessing/data_cleaner.py
python src/preprocessing/feature_engineering.py
python src/preprocessing/data_splitter.py --stratify --random_state 42
```

### 3. Antrenare model

```bash
python src/neural_network/train.py --epochs 50 --batch_size 32 --early_stopping

# Output așteptat:
# Epoch 1/50 - loss: 0.8234 - accuracy: 0.6521 - val_loss: 0.7891 - val_accuracy: 0.6823
# ...
# Epoch 23/50 - loss: 0.3456 - accuracy: 0.8234 - val_loss: 0.4123 - val_accuracy: 0.7956
# Early stopping triggered at epoch 23
# ✓ Model saved to models/trained_model.h5
```

### 4. Evaluare pe test set

```bash
python src/neural_network/evaluate.py --model models/trained_model.h5

# Output așteptat:
# Test Accuracy: 0.7823
# Test F1-score (macro): 0.7456
# ✓ Metrics saved to results/test_metrics.json
# ✓ Confusion matrix saved to docs/confusion_matrix.png
```

### 5. Lansare UI cu model antrenat

```bash
streamlit run src/app/main.py

# SAU pentru LabVIEW:
# Deschideți WebVI și rulați main.vi
```

**Testare în UI:**
1. Introduceți date de test (manual sau upload fișier)
2. Verificați că predicția este DIFERITĂ de Etapa 4 (când era random)
3. Verificați că confidence scores au sens (ex: 85% pentru clasa corectă)
4. Faceți screenshot → salvați în `docs/screenshots/inference_real.png`

---

## Checklist Final – Bifați Totul Înainte de Predare

### Prerequisite Etapa 4 (verificare)
- [ ] State Machine există și e documentat în `docs/state_machine.*`
- [ ] Contribuție ≥40% date originale verificabilă în `data/generated/`
- [ ] Cele 3 module din Etapa 4 funcționale

### Preprocesare și Date
- [ ] Dataset combinat (vechi + nou) preprocesat (dacă ați adăugat date)
- [ ] Split train/val/test: 70/15/15% (verificat dimensiuni fișiere)
- [ ] Scaler din Etapa 3 folosit consistent (`config/preprocessing_params.pkl`)

### Antrenare Model - Nivel 1 (OBLIGATORIU)
- [ ] Model antrenat de la ZERO (nu fine-tuning pe model pre-antrenat)
- [ ] Minimum 10 epoci rulate (verificabil în `results/training_history.csv`)
- [ ] Tabel hiperparametri + justificări completat în acest README
- [ ] Metrici calculate pe test set: **Accuracy ≥65%**, **F1 ≥0.60**
- [ ] Model salvat în `models/trained_model.h5` (sau .pt, .lvmodel)
- [ ] `results/training_history.csv` există cu toate epoch-urile

### Integrare UI și Demonstrație - Nivel 1 (OBLIGATORIU)
- [ ] Model ANTRENAT încărcat în UI din Etapa 4 (nu model dummy)
- [ ] UI face inferență REALĂ cu predicții corecte
- [ ] Screenshot inferență reală în `docs/screenshots/inference_real.png`
- [ ] Verificat: predicțiile sunt diferite față de Etapa 4 (când erau random)

### Documentație Nivel 2 (dacă aplicabil)
- [ ] Early stopping implementat și documentat în cod
- [ ] Learning rate scheduler folosit (ReduceLROnPlateau / StepLR)
- [ ] Augmentări relevante domeniu aplicate (NU rotații simple!)
- [ ] Grafic loss/val_loss salvat în `docs/loss_curve.png`
- [ ] Analiză erori în context industrial completată (4 întrebări răspunse)
- [ ] Metrici Nivel 2: **Accuracy ≥75%**, **F1 ≥0.70**

### Documentație Nivel 3 Bonus (dacă aplicabil)
- [ ] Comparație 2+ arhitecturi (tabel comparativ + justificare)
- [ ] Export ONNX/TFLite + benchmark latență (<50ms demonstrat)
- [ ] Confusion matrix + analiză 5 exemple greșite cu implicații

### Verificări Tehnice
- [ ] `requirements.txt` actualizat cu toate bibliotecile noi
- [ ] Toate path-urile RELATIVE (nu absolute: `/Users/...` )
- [ ] Cod nou comentat în limba română sau engleză (minimum 15%)
- [ ] `git log` arată commit-uri incrementale (NU 1 commit gigantic)
- [ ] Verificare anti-plagiat: toate punctele 1-5 respectate

### Verificare State Machine (Etapa 4)
- [ ] Fluxul de inferență respectă stările din State Machine
- [ ] Toate stările critice (PREPROCESS, INFERENCE, ALERT) folosesc model antrenat
- [ ] UI reflectă State Machine-ul pentru utilizatorul final

### Pre-Predare
- [ ] `docs/etapa5_antrenare_model.md` completat cu TOATE secțiunile
- [ ] Structură repository conformă: `docs/`, `results/`, `models/` actualizate
- [ ] Commit: `"Etapa 5 completă – Accuracy=X.XX, F1=X.XX"`
- [ ] Tag: `git tag -a v0.5-model-trained -m "Etapa 5 - Model antrenat"`
- [ ] Push: `git push origin main --tags`
- [ ] Repository accesibil (public sau privat cu acces profesori)

---

## Livrabile Obligatorii (Nivel 1)

Asigurați-vă că următoarele fișiere există și sunt completate:

1. **`docs/etapa5_antrenare_model.md`** (acest fișier) cu:
   - Tabel hiperparametri + justificări (complet)
   - Metrici test set raportate (accuracy, F1)
   - (Nivel 2) Analiză erori context industrial (4 paragrafe)

2. **`models/trained_model.h5`** (sau `.pt`, `.lvmodel`) - model antrenat funcțional

3. **`results/training_history.csv`** - toate epoch-urile salvate

4. **`results/test_metrics.json`** - metrici finale:

Exemplu:
```json
{
  "test_accuracy": 0.7823,
  "test_f1_macro": 0.7456,
  "test_precision_macro": 0.7612,
  "test_recall_macro": 0.7321
}
```

5. **`docs/screenshots/inference_real.png`** - demonstrație UI cu model antrenat

6. **(Nivel 2)** `docs/loss_curve.png` - grafic loss vs val_loss

7. **(Nivel 3)** `docs/confusion_matrix.png` + analiză în README

---

## Predare și Contact

**Predarea se face prin:**
1. Commit pe GitHub: `"Etapa 5 completă – Accuracy=X.XX, F1=X.XX"`
2. Tag: `git tag -a v0.5-model-trained -m "Etapa 5 - Model antrenat"`
3. Push: `git push origin main --tags`

---

**Mult succes! Această etapă demonstrează că Sistemul vostru cu Inteligență Artificială (SIA) funcționează în condiții reale!**