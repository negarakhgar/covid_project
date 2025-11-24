import React, { useEffect, useState } from "react";
import "./App.css";
import { AGGREGATOR_BASE, EXTRACTOR_BASE } from "./config";

function App() {
    const [extracting, setExtracting] = useState(false);
    const [extractMessage, setExtractMessage] = useState("");

    const [regionTotals, setRegionTotals] = useState({
        single: null,
        double: null,
    });

    const [maxMunicipalities, setMaxMunicipalities] = useState({
        single: null,
        double: null,
    });

    const [provincesData, setProvincesData] = useState([]);
    const [selectedProvince, setSelectedProvince] = useState("");
    const [provinceTotals, setProvinceTotals] = useState({
        single: null,
        double: null,
    });
    const [municipalities, setMunicipalities] = useState([]);

    const [loadingProvince, setLoadingProvince] = useState(false);
    const [error, setError] = useState("");

    function getTotal(data) {
        if (!data) return 0;
        if (typeof data.total === "number") return data.total;
        return 0;
    }

    // load general data on first render
    useEffect(() => {
        async function loadInitialData() {
            try {
                setError("");

                const [
                    singleTotalRes,
                    doubleTotalRes,
                    groupProvinceRes,
                    maxSingleRes,
                    maxDoubleRes,
                ] = await Promise.all([
                    fetch(`${AGGREGATOR_BASE}/total/single-dose`),
                    fetch(`${AGGREGATOR_BASE}/total/double-dose`),
                    fetch(`${AGGREGATOR_BASE}/group-by-province`),
                    fetch(`${AGGREGATOR_BASE}/max/single-dose`),
                    fetch(`${AGGREGATOR_BASE}/max/double-dose`),
                ]);

                const singleTotalJson = await singleTotalRes.json();
                const doubleTotalJson = await doubleTotalRes.json();
                const groupProvinceJson = await groupProvinceRes.json();
                const maxSingleJson = await maxSingleRes.json();
                const maxDoubleJson = await maxDoubleRes.json();

                setRegionTotals({
                    single: getTotal(singleTotalJson),
                    double: getTotal(doubleTotalJson),
                });

                setProvincesData(groupProvinceJson || []);

                setMaxMunicipalities({
                    single: maxSingleJson || null,
                    double: maxDoubleJson || null,
                });
            } catch (e) {
                console.error(e);
                setError("Errore nel caricamento dei dati iniziali.");
            }
        }

        loadInitialData();
    }, []);

    async function loadProvince(name) {
        if (!name) return;
        setSelectedProvince(name);
        setLoadingProvince(true);
        setError("");

        try {
            const [singleRes, doubleRes, muniRes] = await Promise.all([
                fetch(`${AGGREGATOR_BASE}/province/${name}/single-dose`),
                fetch(`${AGGREGATOR_BASE}/province/${name}/double-dose`),
                fetch(`${AGGREGATOR_BASE}/province/${name}/group-by-municipality`),
            ]);

            const singleJson = await singleRes.json();
            const doubleJson = await doubleRes.json();
            const muniJson = await muniRes.json();

            setProvinceTotals({
                single: getTotal(singleJson),
                double: getTotal(doubleJson),
            });

            setMunicipalities(muniJson || []);
        } catch (e) {
            console.error(e);
            setError("Errore nel caricamento dei dati della provincia.");
        } finally {
            setLoadingProvince(false);
        }
    }

    async function handleExtract() {
        setExtracting(true);
        setExtractMessage("");
        setError("");

        try {
            const res = await fetch(`${EXTRACTOR_BASE}/extract`);
            const text = await res.text();
            setExtractMessage(text);
        } catch (e) {
            console.error(e);
            setError("Errore durante l'estrazione dei dati.");
        } finally {
            setExtracting(false);
        }
    }

    return (
        <div className="app">
            <header className="header">
                <div>
                    <h1>Vaccinazioni COVID-19 - Lombardia</h1>
                    <p>Dashboard delle somministrazioni per dose, provincia e comune.</p>
                </div>

                <div className="extract-block">
                    <button onClick={handleExtract} disabled={extracting}>
                        {extracting ? "Estrazione in corso..." : "Esegui estrazione dati"}
                    </button>
                    {extractMessage && (
                        <span className="extract-message">{extractMessage}</span>
                    )}
                </div>
            </header>

            <main className="main">
                {error && <div className="error">{error}</div>}

                {/* REGIONAL TOTALS */}
                <section className="section">
                    <h2>Totali regionali</h2>
                    <div className="card-row">
                        <div className="card">
                            <h3>Prima dose (solo)</h3>
                            <p className="value">
                                {regionTotals.single !== null
                                    ? regionTotals.single.toLocaleString("it-IT")
                                    : "…"}
                            </p>
                        </div>
                        <div className="card">
                            <h3>Doppia dose</h3>
                            <p className="value">
                                {regionTotals.double !== null
                                    ? regionTotals.double.toLocaleString("it-IT")
                                    : "…"}
                            </p>
                        </div>
                    </div>

                    <div className="max-row">
                        <h3>Comuni con il massimo numero di vaccinati</h3>
                        <ul>
                            <li>
                                <strong>Prima dose:</strong>{" "}
                                {maxMunicipalities.single
                                    ? `${maxMunicipalities.single._id} (${maxMunicipalities.single.total.toLocaleString(
                                        "it-IT"
                                    )})`
                                    : "n/d"}
                            </li>
                            <li>
                                <strong>Doppia dose:</strong>{" "}
                                {maxMunicipalities.double
                                    ? `${maxMunicipalities.double._id} (${maxMunicipalities.double.total.toLocaleString(
                                        "it-IT"
                                    )})`
                                    : "n/d"}
                            </li>
                        </ul>
                    </div>
                </section>

                {/* PROVINCES */}
                <section className="section">
                    <h2>Somministrazioni per provincia</h2>
                    <div className="table-wrapper">
                        <table>
                            <thead>
                            <tr>
                                <th>Provincia</th>
                                <th>Prima dose</th>
                                <th>Doppia dose</th>
                                <th>Booster</th>
                                <th>Richiamo</th>
                            </tr>
                            </thead>
                            <tbody>
                            {provincesData.map((p, idx) => (
                                <tr
                                    key={idx}
                                    onClick={() => loadProvince(p._id)}
                                    className={
                                        selectedProvince === p._id ? "row-selected" : ""
                                    }
                                >
                                    <td>{p._id}</td>
                                    <td>{p.singleDose?.toLocaleString("it-IT")}</td>
                                    <td>{p.doubleDose?.toLocaleString("it-IT")}</td>
                                    <td>{p.booster?.toLocaleString("it-IT")}</td>
                                    <td>{p.recall?.toLocaleString("it-IT")}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                    <p className="hint">
                        Suggerimento: clicca su una riga per vedere il dettaglio dei comuni
                        della provincia.
                    </p>
                </section>

                {/* MUNICIPALITIES */}
                <section className="section">
                    <div className="section-header">
                        <h2>
                            Dettaglio comuni{" "}
                            {selectedProvince ? `- ${selectedProvince}` : ""}
                        </h2>
                    </div>

                    {!selectedProvince && (
                        <p>Seleziona una provincia dalla tabella sopra.</p>
                    )}

                    {selectedProvince && (
                        <>
                            <div className="card-row">
                                <div className="card">
                                    <h3>Totale prima dose ({selectedProvince})</h3>
                                    <p className="value">
                                        {provinceTotals.single !== null
                                            ? provinceTotals.single.toLocaleString("it-IT")
                                            : "…"}
                                    </p>
                                </div>
                                <div className="card">
                                    <h3>Totale doppia dose ({selectedProvince})</h3>
                                    <p className="value">
                                        {provinceTotals.double !== null
                                            ? provinceTotals.double.toLocaleString("it-IT")
                                            : "…"}
                                    </p>
                                </div>
                            </div>

                            {loadingProvince ? (
                                <p>Caricamento dati dei comuni…</p>
                            ) : (
                                <div className="table-wrapper">
                                    <table>
                                        <thead>
                                        <tr>
                                            <th>Comune</th>
                                            <th>Prima dose</th>
                                            <th>Doppia dose</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {municipalities.map((m, idx) => (
                                            <tr key={idx}>
                                                <td>{m._id}</td>
                                                <td>
                                                    {m.singleDose?.toLocaleString("it-IT") ?? "0"}
                                                </td>
                                                <td>
                                                    {m.doubleDose?.toLocaleString("it-IT") ?? "0"}
                                                </td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </>
                    )}
                </section>
            </main>

            <footer className="footer">
                <small>
                    Progetto microservizi Perigea · Data Extractor &amp; Data Aggregator
                </small>
            </footer>
        </div>
    );
}

export default App;


export default App;
