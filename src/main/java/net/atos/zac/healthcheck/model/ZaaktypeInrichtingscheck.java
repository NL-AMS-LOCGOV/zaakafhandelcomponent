package net.atos.zac.healthcheck.model;

import java.util.ArrayList;
import java.util.List;

import net.atos.client.zgw.ztc.model.Zaaktype;

/**
 * 4 statustype; Intake, In behandeling, Heropend, Afgerond: met Afgerond als laatste statustypevolgnummer
 * min 1 resultaattype
 * Roltypen, omschrijving generiek: initiator en behandelaar. 1 overig roltype
 * Informatieobjecttype: e-mail
 * indien zaak besluit heeft, Besluittype
 */
public class ZaaktypeInrichtingscheck {

    private final Zaaktype zaaktype;

    private boolean statustypeIntakeAanwezig;

    private boolean statustypeInBehandelingAanwezig;

    private boolean statustypeHeropendAanwezig;

    private boolean statustypeAfgerondAanwezig;

    private boolean statustypeAfgerondLaatsteVolgnummer;

    private boolean resultaattypeAanwezig;

    private boolean rolInitiatorAanwezig;

    private boolean rolBehandelaarAanwezig;

    private boolean rolOverigeAanwezig;

    private boolean informatieobjecttypeEmailAanwezig;

    private boolean besluittypeAanwezig;

    private final List<String> resultaattypesMetVerplichtBesluit = new ArrayList<>();

    private boolean zaakafhandelParametersValide;

    public ZaaktypeInrichtingscheck(final Zaaktype zaaktype) {
        this.zaaktype = zaaktype;
    }

    public boolean isStatustypeIntakeAanwezig() {
        return statustypeIntakeAanwezig;
    }

    public void setStatustypeIntakeAanwezig(final boolean statustypeIntakeAanwezig) {
        this.statustypeIntakeAanwezig = statustypeIntakeAanwezig;
    }

    public boolean isStatustypeInBehandelingAanwezig() {
        return statustypeInBehandelingAanwezig;
    }

    public void setStatustypeInBehandelingAanwezig(final boolean statustypeInBehandelingAanwezig) {
        this.statustypeInBehandelingAanwezig = statustypeInBehandelingAanwezig;
    }

    public boolean isStatustypeHeropendAanwezig() {
        return statustypeHeropendAanwezig;
    }

    public void setStatustypeHeropendAanwezig(final boolean statustypeHeropendAanwezig) {
        this.statustypeHeropendAanwezig = statustypeHeropendAanwezig;
    }

    public boolean isStatustypeAfgerondAanwezig() {
        return statustypeAfgerondAanwezig;
    }

    public void setStatustypeAfgerondAanwezig(final boolean statustypeAfgerondAanwezig) {
        this.statustypeAfgerondAanwezig = statustypeAfgerondAanwezig;
    }

    public boolean isStatustypeAfgerondLaatsteVolgnummer() {
        return statustypeAfgerondLaatsteVolgnummer;
    }

    public void setStatustypeAfgerondLaatsteVolgnummer(final boolean statustypeAfgerondLaatsteVolgnummer) {
        this.statustypeAfgerondLaatsteVolgnummer = statustypeAfgerondLaatsteVolgnummer;
    }

    public boolean isRolInitiatorAanwezig() {
        return rolInitiatorAanwezig;
    }

    public void setRolInitiatorAanwezig(final boolean rolInitiatorAanwezig) {
        this.rolInitiatorAanwezig = rolInitiatorAanwezig;
    }

    public boolean isRolBehandelaarAanwezig() {
        return rolBehandelaarAanwezig;
    }

    public void setRolBehandelaarAanwezig(final boolean rolBehandelaarAanwezig) {
        this.rolBehandelaarAanwezig = rolBehandelaarAanwezig;
    }

    public boolean isRolOverigeAanwezig() {
        return rolOverigeAanwezig;
    }

    public void setRolOverigeAanwezig(final boolean rolOverigeAanwezig) {
        this.rolOverigeAanwezig = rolOverigeAanwezig;
    }

    public boolean isInformatieobjecttypeEmailAanwezig() {
        return informatieobjecttypeEmailAanwezig;
    }

    public void setInformatieobjecttypeEmailAanwezig(final boolean informatieobjecttypeEmailAanwezig) {
        this.informatieobjecttypeEmailAanwezig = informatieobjecttypeEmailAanwezig;
    }

    public boolean isBesluittypeAanwezig() {
        return besluittypeAanwezig;
    }

    public void setBesluittypeAanwezig(final boolean besluittypeAanwezig) {
        this.besluittypeAanwezig = besluittypeAanwezig;
    }

    public List<String> getResultaattypesMetVerplichtBesluit() {
        return resultaattypesMetVerplichtBesluit;
    }

    public void addResultaattypesMetVerplichtBesluit(final String resultaattypeMetVerplichtBesluit) {
        this.resultaattypesMetVerplichtBesluit.add(resultaattypeMetVerplichtBesluit);
    }

    public boolean isResultaattypeAanwezig() {
        return resultaattypeAanwezig;
    }

    public void setResultaattypeAanwezig(final boolean resultaattypeAanwezig) {
        this.resultaattypeAanwezig = resultaattypeAanwezig;
    }

    public Zaaktype getZaaktype() {
        return zaaktype;
    }

    public boolean isZaakafhandelParametersValide() {
        return zaakafhandelParametersValide;
    }

    public void setZaakafhandelParametersValide(final boolean zaakafhandelParametersValide) {
        this.zaakafhandelParametersValide = zaakafhandelParametersValide;
    }

    public boolean isValide() {
        return statustypeIntakeAanwezig && statustypeInBehandelingAanwezig && statustypeHeropendAanwezig && statustypeAfgerondAanwezig && statustypeAfgerondLaatsteVolgnummer &&
                rolInitiatorAanwezig && rolBehandelaarAanwezig && rolOverigeAanwezig && informatieobjecttypeEmailAanwezig && resultaattypeAanwezig &&
                zaakafhandelParametersValide && (resultaattypesMetVerplichtBesluit.isEmpty() || besluittypeAanwezig);
    }
}
