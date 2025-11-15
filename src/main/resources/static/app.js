const API_URL = "/etudiants";

function chargerEtudiants() {
    fetch(API_URL)
    .then(response => response.json())
    .then(data => {
        const table = document.getElementById("etudiants-table");
        table.innerHTML = "";
        data.forEach(e => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${e.id}</td>
                <td>${e.nom}</td>
                <td>${e.prenom}</td>
                <td>${e.email}</td>
                <td>${e.niveau}</td>
                <td>
                    <button onclick="supprimerEtudiant(${e.id})">Supprimer</button>
                </td>
            `;
            table.appendChild(row);
        });
    });
}

function ajouterEtudiant() {
    const nom = document.getElementById("nom").value;
    const prenom = document.getElementById("prenom").value;
    const email = document.getElementById("email").value;
    const niveau = document.getElementById("niveau").value;

    fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nom, prenom, email, niveau })
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById("nom").value = "";
        document.getElementById("prenom").value = "";
        document.getElementById("email").value = "";
        document.getElementById("niveau").value = "";
        chargerEtudiants();
    });
}

function supprimerEtudiant(id) {
    fetch(`${API_URL}/${id}`, { method: "DELETE" })
    .then(() => chargerEtudiants());
}

// Charger les étudiants au démarrage
window.onload = chargerEtudiants;
