import React, { useState } from 'react';
import axios from 'axios';
import './FormulaireFacture.css';

const FormulaireFacture = () => {
    const [nomPrenom, setNomPrenom] = useState('');
    const [dateNaissance, setDateNaissance] = useState('');
    const [lieuNaissance, setLieuNaissance] = useState('');
    const [taux, setTaux] = useState('');
    const [services, setServices] = useState('');
    const [typeCarte, setTypeCarte] = useState('');

    const handleSubmit = async (event) => {
        event.preventDefault();

        const data = { nomPrenom, dateNaissance, lieuNaissance, taux, services, typeCarte };

        try {
            const response = await axios.post('http://localhost:8080/api/factures', data);
            alert(`Facture générée avec succès ! ID: ${response.data.id || "non disponible"}`);
        } catch (error) {
            alert("Erreur lors de la génération de la facture");
        }
    };

    return (
        <div className="formulaire-facture">
            <header className="navbar">
                <img src="AWB.png" alt="Logo" className="navbar-logo" /> {/* Remplacez logo.png par le chemin de votre logo */}
                <h1 className="navbar-title">Facture Forfait</h1>
            </header>
            <div className="form-container">
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="nomPrenom">Nom et Prénom:</label>
                        <input type="text" id="nomPrenom" value={nomPrenom} onChange={(e) => setNomPrenom(e.target.value)} required />
                    </div>
                    <div className="form-group">
                        <label htmlFor="dateNaissance">Date de Naissance:</label>
                        <input type="date" id="dateNaissance" value={dateNaissance} onChange={(e) => setDateNaissance(e.target.value)} required />
                    </div>
                    <div className="form-group">
                        <label htmlFor="lieuNaissance">Lieu de Naissance:</label>
                        <input type="text" id="lieuNaissance" value={lieuNaissance} onChange={(e) => setLieuNaissance(e.target.value)} required />
                    </div>
                    <div className="form-group">
                        <label htmlFor="taux">Taux:</label>
                        <input type="number" id="taux" value={taux} onChange={(e) => setTaux(e.target.value)} required />
                    </div>
                    <div className="form-group">
                        <label htmlFor="services">Services:</label>
                        <select id="services" value={services} onChange={(e) => setServices(e.target.value)} required>
                            <option value="">Sélectionnez...</option>
                            <option value="chequie">Chequie</option>
                            <option value="paiement">Paiement</option>
                            <option value="releves">Relevés</option>
                            <option value="distribu">Distribution</option>
                            <option value="assurance">Assurance</option>
                        </select>
                    </div>
                    <div className="form-group">
                        <label htmlFor="typeCarte">Type de Carte:</label>
                        <select id="typeCarte" value={typeCarte} onChange={(e) => setTypeCarte(e.target.value)} required>
                            <option value="">Sélectionnez...</option>
                            <option value="Classique">Classique</option>
                            <option value="Platinium">Platinium</option>
                            <option value="Gold">Gold</option>
                        </select>
                    </div>
                    <button type="submit" className="submit-button">Générer Facture</button>
                </form>
            </div>
        </div>
    );
};

export default FormulaireFacture;
