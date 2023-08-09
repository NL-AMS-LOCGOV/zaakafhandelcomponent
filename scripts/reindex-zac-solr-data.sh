#!/bin/bash

echo "Send requests to ZAC to reindex zaak, taak and document data (in Solr). This assumes a locally running ZAC instance on the default port 8080."

## Note that we first need to mark all items for re-indexation per item type
curl http://localhost:8080/rest/indexeren/herindexeren/ZAAK \
    && curl http://localhost:8080/rest/indexeren/herindexeren/TAAK \
    && curl http://localhost:8080/rest/indexeren/herindexeren/DOCUMENT \
    && curl http://localhost:8080/rest/indexeren/1000
