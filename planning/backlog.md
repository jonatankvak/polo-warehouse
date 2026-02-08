# Backlog

## P0

### V2-001 Define UOM and conversion schemas
- Priority: P0
- Status: done
- Acceptance:
  - UOM, PackageType, ConversionRule models defined
  - Firestore collections documented
- Dependencies: none
- Notes: align with V2 model

### V2-002 Multi-UOM quantities on handling unit
- Priority: P0
- Status: todo
- Acceptance:
  - Create/read/update supports quantities: [{uom, value}]
  - Migration path documented
- Dependencies: V2-001

### V2-003 Bottom bar restructure
- Priority: P0
- Status: todo
- Acceptance:
  - Bottom bar includes Home, Scan, Tasks, Inventory
  - Create moved to quick action
- Dependencies: none

### V2-004 Guided scan action resolver
- Priority: P0
- Status: todo
- Acceptance:
  - Scan result proposes valid next action based on state
- Dependencies: V2-002

### V2-005 Task queue MVP
- Priority: P0
- Status: todo
- Acceptance:
  - Task list for verify/dispatch with status updates
- Dependencies: V2-003

## P1

### V2-006 Movement ledger
- Priority: P1
- Status: todo
- Acceptance:
  - Append-only movement events for all transitions
- Dependencies: V2-002

### V2-007 Exceptions workflow
- Priority: P1
- Status: todo
- Acceptance:
  - Mismatch/wrong location/duplicate scan exceptions
  - Resolution states tracked
- Dependencies: V2-006

### V2-008 Partial unpack/consume
- Priority: P1
- Status: todo
- Acceptance:
  - Partial quantity updates, residual HU preserved
- Dependencies: V2-002, V2-006

## P2

### V2-009 Workflow templates
- Priority: P2
- Status: todo
- Acceptance:
  - Configurable state machine with guards
- Dependencies: V2-006

### V2-010 Offline scan queue
- Priority: P2
- Status: todo
- Acceptance:
  - Offline actions queued and synced
- Dependencies: V2-006
