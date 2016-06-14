package ttldtor.validators

import ttldtor.poko.LogSite

class LogSiteValidator {
    fun validate(logSite: LogSite): ValidationResult {
        return ValidationResult(ValidationResultType.OK, "")
    }
}