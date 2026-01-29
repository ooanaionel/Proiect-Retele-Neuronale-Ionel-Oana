async function refreshData() {
    const response = await fetch('/api/v1/simulation/state');
    const state = await response.json();
    updateUI(state);
}

async function verificaStatus() {
    const response = await fetch('/api/v1/simulation/step', { method: 'POST' });
    const state = await response.json();
    updateUI(state);
}

function updateUI(state) {
    // Tabel 1: Camioane în Tranzit
    document.getElementById('trucks-body').innerHTML = state.trucks
        .filter(t => t.inTransit)
        .map(t => `<tr><td>${t.truckId}</td><td>${t.targetHub}</td><td>${t.remainingTime}h</td><td class="small">${t.cargo.map(p => p.awb).join(', ')}</td></tr>`).join('');

    // Tabel 2: Colete la Depozit + Livrate
    const allPkgs = [...state.warehouseStock, ...state.history];
    document.getElementById('stock-body').innerHTML = allPkgs
        .map(p => `<tr><td>${p.awb}</td><td>${p.currentLocation}</td><td><span class="badge ${p.status === 'LIVRAT' ? 'bg-success' : 'badge-tranzit'}">${p.status}</span></td></tr>`).join('');

    // Tabel 3: Flotă Disponibilă
    document.getElementById('available-trucks-body').innerHTML = state.trucks
        .filter(t => !t.inTransit)
        .map(t => `<tr><td>${t.truckId}</td><td>${t.currentLocation}</td></tr>`).join('');

    // Tabel 4: Monitorizare Stoc
    document.getElementById('depot-stock-body').innerHTML = Object.keys(state.oldStock).map(hub => {
        const actual = state.warehouseStock.filter(p => p.currentLocation === hub).length;
        return `<tr>
            <td><b>${hub}</b></td>
            <td>${state.oldStock[hub]}</td>
            <td class="text-primary">+${state.newArrivals[hub] || 0}</td>
            <td>${actual}</td>
        </tr>`;
    }).join('');

   if (state.currentSimTime) {
           document.getElementById('sim-time').innerText = state.currentSimTime;
       } else {
           console.log("Variabila currentSimTime încă nu a ajuns la frontend");
       }
}

// Încărcare inițială
document.addEventListener('DOMContentLoaded', refreshData);