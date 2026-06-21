package com.neurotools.backend.tool.exception

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import com.neurotools.backend.tool.exception.TooManyClickRequestsException
import com.neurotools.backend.tool.exception.InvalidClickTokenException

@RestControllerAdvice
class ApiExceptionHandler {
    private val log = LoggerFactory.getLogger(ApiExceptionHandler::class.java)

    @ExceptionHandler(EntityNotFoundException::class)
    fun notFound(exception: EntityNotFoundException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Not found").also {
            log.debug("Not found: {}", exception.message)
        }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun conflict(exception: DataIntegrityViolationException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Conflict").also {
            log.warn("Data integrity violation: {}", exception.message)
        }

    @ExceptionHandler(TooManyClickRequestsException::class)
    fun tooManyRequests(exception: TooManyClickRequestsException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, exception.message ?: "Too many requests")

    @ExceptionHandler(InvalidClickTokenException::class)
    fun invalidClickToken(exception: InvalidClickTokenException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.message ?: "Invalid click token")

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validation(exception: MethodArgumentNotValidException): ProblemDetail {
        val detail = exception.bindingResult.fieldErrors
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail)
    }
}
