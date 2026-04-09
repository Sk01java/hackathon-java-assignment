# 🚀 Quick Reference Commands

## Start Development Mode

```powershell
# Change to project directory
cd C:\hackathon-java-assignment

# Option 1: Direct command
.\mvnw.cmd quarkus:dev

# Option 2: Using the startup script
.\START_DEV.ps1
```

**Expected Output:**
```
2026-04-07 21:00:00,000 INFO  [io.quarkus] (main) java-code-assignment 1.0.0-SNAPSHOT on JVM 
2026-04-07 21:00:00,000 INFO  [io.quarkus] (main) Started in 25.123s. Listening on: http://localhost:8080
```

## Testing

```powershell
# Run all tests
.\mvnw.cmd clean test

# Run specific test class
.\mvnw.cmd test -Dtest=ArchiveWarehouseUseCaseTest

# Run specific test method
.\mvnw.cmd test -Dtest=ArchiveWarehouseUseCaseTest#testConcurrentArchiveAndStockUpdateCausesOptimisticLockException

# Run with debug output
.\mvnw.cmd test -X

# Run integration tests
.\mvnw.cmd verify
```

## Building

```powershell
# Clean build
.\mvnw.cmd clean build

# Package (creates JAR)
.\mvnw.cmd package

# Skip tests during build
.\mvnw.cmd package -DskipTests
```

## Compilation

```powershell
# Compile source code
.\mvnw.cmd compile

# Force recompilation
.\mvnw.cmd clean compile
```

## API Testing

### Using cURL
```bash
# List all warehouses
curl http://localhost:8080/warehouse

# Create a warehouse
curl -X POST http://localhost:8080/warehouse \
  -H "Content-Type: application/json" \
  -d '{
    "businessUnitCode": "WH-001",
    "location": "AMSTERDAM-001",
    "capacity": 50,
    "stock": 10
  }'

# List all stores
curl http://localhost:8080/store

# Create a store
curl -X POST http://localhost:8080/store \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Store 1",
    "quantityProductsInStock": 100
  }'
```

### Using PowerShell
```powershell
# List warehouses
Invoke-WebRequest -Uri "http://localhost:8080/warehouse" | Select-Object -ExpandProperty Content

# Create warehouse
$body = @{
    businessUnitCode = "WH-002"
    location = "TILBURG-001"
    capacity = 30
    stock = 15
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/warehouse" -Method POST -Body $body -ContentType "application/json"
```

## Dev Mode Console Commands

While dev mode is running, you can press:

- **r** - Run all tests
- **s** - Force sync (hot reload)
- **u** - Check for updates
- **d** - Toggle debug mode
- **h** - Show help
- **q** - Quit dev mode

## Troubleshooting Commands

```powershell
# Kill stuck Java processes
Stop-Process -Name java -Force -ErrorAction SilentlyContinue

# Check if port 8080 is in use
netstat -ano | Select-String "8080"

# Clean all Maven artifacts
.\mvnw.cmd clean

# Force Maven to re-download dependencies
.\mvnw.cmd dependency:resolve

# Check Maven version
.\mvnw.cmd --version

# Get Java version
java -version
```

## IDE Integration

### IntelliJ IDEA
- Right-click on test → "Run" or "Debug"
- Or use Run menu → Run/Debug configurations

### VS Code with Quarkus Tools Extension
- Use Command Palette (Ctrl+Shift+P) → "Quarkus: Start Dev Mode"
- Or use debug panel

## Common Issues & Fixes

### "Port already in use"
```powershell
Stop-Process -Name java -Force
Start-Sleep -Seconds 2
.\mvnw.cmd quarkus:dev
```

### "Tests failing with database errors"
```powershell
.\mvnw.cmd clean install
```

### "Datasource not configured"
✅ Already fixed! No action needed.

### "Code changes not reflecting in dev mode"
Press 'r' in dev mode console to force reload:
- Or manually restart: Press 'q' then restart with `.\mvnw.cmd quarkus:dev`

## Performance Tips

- Keep dev mode running between changes (hot reload is fast)
- Use IDE debugging instead of System.out.println for troubleshooting
- Run tests locally before pushing (catches issues early)
- Use `%dev.quarkus.log.level=DEBUG` in application.properties for detailed logs

## Documentation Links

- Quarkus Guides: https://quarkus.io/guides/
- REST API Documentation: http://localhost:8080/q/swagger-ui/ (when running)
- Dev UI: http://localhost:8080/q/dev (when running)

---

**Pro Tip:** Keep this file open while developing for quick reference!

