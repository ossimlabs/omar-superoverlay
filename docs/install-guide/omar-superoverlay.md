# OMAR Superoverlay

## Purpose

The OMAR Superoverlay application allows functionality for generating and servicing KMLs, which allows users to search and view imagery within O2 in external GeoINT tools.

## Installation in Openshift

**Assumption:** The omar-superoverlay docker image is pushed into the OpenShift server's internal docker registry and available to the project.

### Environment variables

|Variable|Value|
|------|------|
|SPRING_PROFILES_ACTIVE|Comma separated profile tags (*e.g. production, dev*)|
|SPRING_CLOUD_CONFIG_LABEL|The Git branch from which to pull config files (*e.g. master*)|
