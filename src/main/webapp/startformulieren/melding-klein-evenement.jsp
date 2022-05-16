<%--
  ~ SPDX-FileCopyrightText: 2022 Atos
  ~ SPDX-License-Identifier: EUPL-1.2+
  --%>

<!doctype html>
<html lang="en">
<head>
    <%! String openFormsUrl = System.getenv().get("OPEN_FORMS_URL"); %>
    <%! String sdkVersion = "1.0.2"; %>
    <%! String openFormsSdkCss = String.format("%s/static/sdk/%s/open-forms-sdk.css", openFormsUrl, sdkVersion); %>
    <%! String openFormsSdkJs = String.format("%s/static/sdk/%s/open-forms-sdk.js", openFormsUrl, sdkVersion); %>
    <meta charset="utf-8">
    <title>Startformulieren</title>
    <link href="<%= openFormsSdkCss %>" rel="stylesheet"/>
    <script src="<%= openFormsSdkJs %>"></script>
</head>
<body>
<div id="openforms-container"></div>
<%! String baseUrl = String.format("%s/api/v1/", openFormsUrl); %>
<script>
    const baseUrl = '<%= baseUrl %>';
    const formId = 'melding-klein-evenement';
    const basePath = '/startformulieren';
    const targetNode = document.getElementById('openforms-container');
    const form = new OpenForms.OpenForm(targetNode, {baseUrl, formId, basePath});
    form.init();
</script>
</body>
</html>
