# Firestore Schema (Draft)

This file documents collections added for V2 flexibility. Field names are stable; types may evolve.

## Collections

### products
- uid (doc id)
- idNumber (number)
- name (string)
- barCode (number)
- transportPackage (string)
- price (number, optional, RSD no VAT)

### uoms
- uid (doc id)
- code (string) e.g. kg, pcs, crate
- displayName (string) e.g. Kilogram, Pieces
- kind (string) COUNT | WEIGHT | VOLUME
- decimals (number)
- active (boolean)

### packageTypes
- uid (doc id)
- label (string) e.g. Plastic Crate 10kg
- tareWeightKg (number)
- defaultQuantity (number)
- defaultUomUid (string)
- active (boolean)

### conversionRules
- uid (doc id)
- productUid (string)
- fromUomUid (string)
- toUomUid (string)
- factor (number)
- rounding (number) decimal places
- active (boolean)
