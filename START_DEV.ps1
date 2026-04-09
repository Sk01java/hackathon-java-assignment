# Script to start Quarkus development mode

Write-Host "🚀 Starting Quarkus Development Mode..."
Write-Host ""

# Kill any existing Java processes
Write-Host "Cleaning up any existing processes..."
Stop-Process -Name java -Force -ErrorAction SilentlyContinue | Out-Null
Start-Sleep -Seconds 2

# Change to project directory
cd C:\hackathon-java-assignment

# Start Quarkus dev mode
Write-Host "Starting Quarkus..."
Write-Host ""

.\mvnw.cmd quarkus:dev

# When the application is running, display this info
Write-Host ""
Write-Host "=================================================="
Write-Host "✅ Quarkus is running!"
Write-Host "=================================================="
Write-Host ""
Write-Host "📱 Access the application at:"
Write-Host "  - Swagger UI:     http://localhost:8080/q/swagger-ui/"
Write-Host "  - Dev UI:         http://localhost:8080/q/dev"
Write-Host "  - Application:    http://localhost:8080"
Write-Host ""
Write-Host "🔥 Hot reload enabled - code changes reflect immediately"
Write-Host "🧪 Press 'r' in dev mode to run tests"
Write-Host ""

