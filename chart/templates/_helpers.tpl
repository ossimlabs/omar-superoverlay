{{- define "omar-superoverlay.imagePullSecret" }}
{{- printf "{\"auths\": {\"%s\": {\"auth\": \"%s\"}}}" .Values.global.imagePullSecret.registry (printf "%s:%s" .Values.global.imagePullSecret.username .Values.global.imagePullSecret.password | b64enc) | b64enc }}
{{- end }}

{{/* Template for env vars */}}
{{- define "omar-superoverlay.envVars" -}}
  {{- range $key, $value := .Values.envVars }}
  - name: {{ $key | quote }}
    value: {{ $value | quote }}
  {{- end }}
{{- end -}}

{{- define "omar-superoverlay.volumeMounts.configmaps" -}}
{{- range $configmap := .Values.configmaps}}
- name: {{ $configmap.internalName | quote }}
  mountPath: {{ $configmap.mountPath | quote }}
  {{- if $configmap.subPath }}
  subPath: {{ $configmap.subPath | quote }}
  {{- end }}
{{- end -}}
{{- end -}}

{{- define "omar-superoverlay.volumeMounts" -}}
{{- include "omar-superoverlay.volumeMounts.configmaps" . -}}
{{- end -}}

{{- define "omar-superoverlay.volumes.configmaps" -}}
{{- range $configmap := .Values.configmaps}}
- name: {{ $configmap.internalName | quote }}
  configMap:
    name: {{ $configmap.name | quote }}
{{- end -}}
{{- end -}}

{{- define "omar-superoverlay.volumes" -}}
{{- include "omar-superoverlay.volumes.configmaps" . -}}
{{- end -}}