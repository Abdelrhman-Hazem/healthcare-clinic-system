# Fix package declarations
$entityFiles = @('Address.java', 'Appointment.java', 'AppointmentStatus.java', 'AppUser.java', 'Doctor.java', 'Patient.java', 'UserRole.java')
foreach ($f in $entityFiles) {
    $path = "src\main\java\com\kfh\clinic\infrastructure\entity\$f"
    if (Test-Path $path) {
        (Get-Content $path) -replace '^package com\.kfh\.clinic\.domain\.model;', 'package com.kfh.clinic.infrastructure.entity;' | Set-Content $path
    }
}

$repoFiles = @('AppointmentRepository.java', 'AppUserRepository.java', 'DoctorRepository.java', 'PatientRepository.java')
foreach ($f in $repoFiles) {
    $path = "src\main\java\com\kfh\clinic\infrastructure\repository\$f"
    if (Test-Path $path) {
        (Get-Content $path) -replace '^package com\.kfh\.clinic\.domain\.repository;', 'package com.kfh.clinic.infrastructure.repository;' | Set-Content $path
    }
}

$exceptionFiles = @('ApiError.java', 'DuplicateResourceException.java', 'GlobalExceptionHandler.java', 'InvalidRequestException.java', 'ResourceNotFoundException.java')
foreach ($f in $exceptionFiles) {
    $path = "src\main\java\com\kfh\clinic\application\exception\$f"
    if (Test-Path $path) {
        (Get-Content $path) -replace '^package com\.kfh\.clinic\.shared\.exception;', 'package com.kfh.clinic.application.exception;' | Set-Content $path
    }
}

$configFiles = @('JwtProperties.java', 'SecurityConfig.java')
foreach ($f in $configFiles) {
    $path = "src\main\java\com\kfh\clinic\config\$f"
    if (Test-Path $path) {
        (Get-Content $path) -replace '^package com\.kfh\.clinic\.infrastructure\.config;', 'package com.kfh.clinic.config;' | Set-Content $path
    }
}

$securityFiles = @('ActiveSessionService.java', 'CustomUserDetailsService.java', 'JwtAuthenticationFilter.java', 'JwtService.java')
foreach ($f in $securityFiles) {
    $path = "src\main\java\com\kfh\clinic\config\security\$f"
    if (Test-Path $path) {
        (Get-Content $path) -replace '^package com\.kfh\.clinic\.infrastructure\.security;', 'package com.kfh.clinic.config.security;' | Set-Content $path
    }
}

