package net.atos.zac.flowable.delegate;

import java.util.logging.Logger;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.el.FixedValue;

import net.atos.zac.flowable.FlowableHelper;

public class UpdateZaakJavaDelegate extends AbstractDelegate {

    private static final Logger LOG = Logger.getLogger(UpdateZaakJavaDelegate.class.getName());

    private static final String TOELICHTING = "Aangepast vanuit proces";

    @SuppressWarnings("UnusedDeclaration") //wordt gezet vanuit Flowable runtime
    private FixedValue statustypeOmschrijving;

    @SuppressWarnings("UnusedDeclaration") //wordt gezet vanuit Flowable runtime
    private FixedValue resultaattypeOmschrijving;

    @Override
    public void execute(final DelegateExecution execution) {
        final var flowableHelper = FlowableHelper.getInstance();
        final var zaak = flowableHelper.getZrcClientService().readZaakByID(getZaakIdentificatie(execution));

        if (statustypeOmschrijving != null) {
            final var statustypeOmschrijving = this.statustypeOmschrijving.getExpressionText();
            LOG.info("Zaak '%s': Aanmaken Status met statustype omschrijving '%s'"
                             .formatted(zaak.getUuid(), statustypeOmschrijving));
            flowableHelper.getZgwApiService().createStatusForZaak(zaak, statustypeOmschrijving, TOELICHTING);
        }

        if (resultaattypeOmschrijving != null) {
            final var resultaattypeOmschrijving = this.resultaattypeOmschrijving.getExpressionText();
            LOG.info("Zaak '%s': Aanmaken Status met statustype omschrijving '%s'"
                             .formatted(zaak.getUuid(), resultaattypeOmschrijving));
            flowableHelper.getZgwApiService().createResultaatForZaak(zaak, resultaattypeOmschrijving, TOELICHTING);
        }
    }
}
