### Deploy rules to Kubernetes cluster as configmap

    kubectl create configmap zaakafhandelcomponent-policies [-n namespace] --from-file=zaak-acties.rego

### Start OPA locally

    opa run --server zac.rego --log-format=text --log-level=debug

### Evaluate rules locally

    opa eval -i input.json -d zac.rego "data.net.atos.zac.zaak_acties" -f pretty
