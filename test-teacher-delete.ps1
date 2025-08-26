# Test script for Teacher DELETE endpoint
Write-Host "Testing Teacher DELETE endpoint..." -ForegroundColor Green

# Test 1: Delete a teacher that doesn't exist (should return 404)
Write-Host "`nTest 1: Delete non-existent teacher (ID: 999)" -ForegroundColor Yellow
$response1 = Invoke-RestMethod -Uri "http://localhost:8080/teacher/999" -Method DELETE -ErrorAction SilentlyContinue
if ($LASTEXITCODE -eq 0) {
    Write-Host "Response: $($response1 | ConvertTo-Json)" -ForegroundColor Red
} else {
    Write-Host "Expected 404 error received" -ForegroundColor Green
}

# Test 2: Delete a teacher with null ID (should return 400)
Write-Host "`nTest 2: Delete teacher with null ID" -ForegroundColor Yellow
try {
    $response2 = Invoke-RestMethod -Uri "http://localhost:8080/teacher/" -Method DELETE
    Write-Host "Response: $($response2 | ConvertTo-Json)" -ForegroundColor Red
} catch {
    Write-Host "Expected 400 error received" -ForegroundColor Green
}

# Test 3: Delete a teacher that exists and has no courses (should return 200)
Write-Host "`nTest 3: Delete existing teacher with no courses (ID: 1)" -ForegroundColor Yellow
try {
    $response3 = Invoke-RestMethod -Uri "http://localhost:8080/teacher/1" -Method DELETE
    Write-Host "Success! Response: $($response3 | ConvertTo-Json)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nTesting completed!" -ForegroundColor Green










