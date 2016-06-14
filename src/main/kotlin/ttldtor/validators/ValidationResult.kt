package ttldtor.validators

enum class ValidationResultType {
    OK, ERROR, WARNING
}

data class ValidationResult(val type: ValidationResultType, val text: String)