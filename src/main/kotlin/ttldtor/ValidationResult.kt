package ttldtor

enum class ValidationResultType {
    OK, ERROR, WARNING
}

data class ValidationResult(val type: ValidationResultType, val text: String)