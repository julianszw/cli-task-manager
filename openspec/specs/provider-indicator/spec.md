# Provider Indicator

## Purpose
Define que el nombre de la lista activa se muestre acompañado del nombre de su
proveedor, atenuado (en gris), para anticipar la futura integración de múltiples
servicios de tareas. Ejemplo: "Trámites (Google Tasks)" con "Google Tasks" en gris.

## Requirements

### Requirement: Proveedor junto al nombre de la lista
El sistema DEBE mostrar el nombre del proveedor de la lista activa junto a su
nombre, atenuado y entre paréntesis.

#### Scenario: Lista con proveedor
- **GIVEN** una lista activa con proveedor Google Tasks
- **WHEN** se muestra el nombre de la lista activa
- **THEN** se muestra el nombre de la lista seguido del proveedor, por ejemplo "Trámites (Google Tasks)"
- **AND** el nombre del proveedor se muestra atenuado (gris)

#### Scenario: El proveedor es informativo
- **GIVEN** una lista activa con proveedor
- **WHEN** se muestra o se opera con la lista
- **THEN** el proveedor es un texto informativo, separado del nombre de la lista
- **AND** el proveedor no forma parte del nombre editable de la lista
