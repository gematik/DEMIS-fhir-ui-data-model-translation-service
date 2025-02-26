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
{{ if .Chart.AppVersion -}}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- with .Values.customLabels }}
{{ toYaml . }}
{{- end }}
{{ end -}}

{{/*
Selector labels
*/}}
{{- define "fhir-ui-data-model-translation-service.selectorLabels" -}}
app: {{ .Values.fullName }}
{{- end }}
