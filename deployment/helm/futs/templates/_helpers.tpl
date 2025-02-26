{{/*
Expand the name of the chart.
*/}}
{{- define "fhir-ui-data-model-translation-service.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "fhir-ui-data-model-translation-service.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{- define "fhir-ui-data-model-translation-service.fullversionname" -}}
{{- if .Values.istio.enable }}
{{- $name := include "fhir-ui-data-model-translation-service.fullname" . }}
{{- $version := regexReplaceAll "\\.+" .Chart.Version "-" }}
{{- printf "%s-%s" $name $version | trunc 63 }}
{{- else }}
{{- include "fhir-ui-data-model-translation-service.fullname" . }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "fhir-ui-data-model-translation-service.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "fhir-ui-data-model-translation-service.labels" -}}
helm.sh/chart: {{ include "fhir-ui-data-model-translation-service.chart" . }}
{{ include "fhir-ui-data-model-translation-service.selectorLabels" . }}
fhirProfileVersion: {{ .Values.required.profiles.version | quote }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- with .Values.customLabels }}
{{ toYaml . }}
{{- end }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "fhir-ui-data-model-translation-service.selectorLabels" -}}
app: {{ include "fhir-ui-data-model-translation-service.name" . }}
version: {{ .Chart.AppVersion | quote }}
fhirProfile: {{ .Values.required.profiles.name | quote }}
app.kubernetes.io/name: {{ include "fhir-ui-data-model-translation-service.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Deployment labels
*/}}
{{- define "fhir-ui-data-model-translation-service.deploymentLabels" -}}
istio-validate-jwt: "{{ .Values.istio.validateJwt | required ".Values.istio.validateJwt is required" }}"
fhirProfileVersion: {{ .Values.required.profiles.version | quote }}
{{- with .Values.deploymentLabels }}
{{ toYaml . }}
{{- end }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "fhir-ui-data-model-translation-service.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "fhir-ui-data-model-translation-service.fullversionname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}
