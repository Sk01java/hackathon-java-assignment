# Client Sequence Diagrams

The following sequence diagrams describe end-to-end runtime flows for warehouse, store, and product operations.

## 1) Warehouse - Create Flow

```mermaid
sequenceDiagram
  participant C as Client
  participant WR as WarehouseResourceImpl
  participant CU as CreateWarehouseUseCase
  participant LR as LocationResolver
  participant WS as WarehouseStore

  C->>WR: POST /warehouse (data)
  WR->>CU: create(domainWarehouse)
  CU->>WS: findByBusinessUnitCode(code)
  WS-->>CU: null/not-null
  CU->>LR: resolveByIdentifier(location)
  LR-->>CU: Location or null
  CU->>CU: validate capacity/stock rules
  CU->>WS: create(warehouse)
  CU-->>WR: success
  WR-->>C: 201/200 response payload
```

## 2) Warehouse - Get by ID Flow

```mermaid
sequenceDiagram
  participant C as Client
  participant WR as WarehouseResourceImpl
  participant WS as WarehouseStore

  C->>WR: GET /warehouse/{businessUnitCode}
  WR->>WS: findByBusinessUnitCode(code)
  WS-->>WR: Warehouse or null
  alt found
    WR-->>C: 200 + warehouse
  else not found
    WR-->>C: 404
  end
```

## 3) Warehouse - Replace Flow

```mermaid
sequenceDiagram
  participant C as Client
  participant WR as WarehouseResourceImpl
  participant RU as ReplaceWarehouseUseCase
  participant WS as WarehouseStore
  participant LR as LocationResolver

  C->>WR: PUT /warehouse/{businessUnitCode}
  WR->>RU: replace(domainWarehouse)
  RU->>WS: findByBusinessUnitCode(code)
  WS-->>RU: existing/null
  RU->>RU: validate not archived
  RU->>LR: resolveByIdentifier(newLocation)
  LR-->>RU: Location/null
  RU->>RU: validate capacity/stock
  RU->>WS: update(existing)
  RU-->>WR: success
  WR->>WS: findByBusinessUnitCode(code)
  WS-->>WR: updated warehouse
  WR-->>C: 200 + updated payload
```

## 4) Warehouse - Archive Flow

```mermaid
sequenceDiagram
  participant C as Client
  participant WR as WarehouseResourceImpl
  participant AU as ArchiveWarehouseUseCase
  participant WS as WarehouseStore

  C->>WR: POST/PUT archive endpoint
  WR->>WS: findByBusinessUnitCode(code)
  WS-->>WR: existing/null
  WR->>AU: archive(warehouse)
  AU->>WS: findByBusinessUnitCode(code)
  WS-->>AU: current warehouse
  AU->>AU: validate not already archived
  AU->>WS: update(archivedAt=now)
  AU-->>WR: success
  WR-->>C: 204/200
```

## 5) Store - Create/Update/Patch with Legacy Sync

```mermaid
sequenceDiagram
  participant C as Client
  participant SR as StoreResource
  participant DB as Store Entity/Persistence
  participant EVT as CDI Event Bus
  participant OBS as StoreEventObserver
  participant LG as LegacyStoreManagerGateway

  C->>SR: POST/PUT/PATCH /store
  SR->>DB: persist/update entity
  SR->>EVT: fire StoreCreatedEvent/StoreUpdatedEvent
  SR-->>C: 201/200
  EVT->>OBS: AFTER_SUCCESS observer callback
  OBS->>LG: createStoreOnLegacySystem/updateStoreOnLegacySystem
  LG-->>OBS: legacy sync complete
```

## 6) Store - Delete Flow

```mermaid
sequenceDiagram
  participant C as Client
  participant SR as StoreResource
  participant DB as Store Entity

  C->>SR: DELETE /store/{id}
  SR->>DB: findById(id)
  alt found
    SR->>DB: delete()
    SR-->>C: 204
  else not found
    SR-->>C: 404
  end
```

## 7) Product - CRUD Flow

```mermaid
sequenceDiagram
  participant C as Client
  participant PR as ProductResource
  participant Repo as ProductRepository

  C->>PR: GET /product
  PR->>Repo: listAll(sort by name)
  Repo-->>PR: product list
  PR-->>C: 200

  C->>PR: POST /product
  PR->>Repo: persist(product)
  PR-->>C: 201

  C->>PR: PUT /product/{id}
  PR->>Repo: findById(id)
  PR->>Repo: persist(updated)
  PR-->>C: 200

  C->>PR: DELETE /product/{id}
  PR->>Repo: findById(id)
  PR->>Repo: delete(entity)
  PR-->>C: 204
```

