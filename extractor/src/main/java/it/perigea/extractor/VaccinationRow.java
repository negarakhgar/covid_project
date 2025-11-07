package it.perigea.extractor;

public record VaccinationRow(
        String codistat_comune_dom,
        String comune_dom,
        String provincia_dom,
        String tot_solo_dose_1,
        String tot_dose_2_unica,
        String tot_dose_addizionale_booster,
        String totdose_richimm_rich2
) {}
