package com.ozodbek.gymbuddy

import com.ozodbek.gymbuddy.domain.AuthValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidatorTest {
    @Test fun validEmailPasses() = assertNull(AuthValidator.validateEmail("a@b.com"))
    @Test fun invalidEmailFails() = assertNotNull(AuthValidator.validateEmail("not-an-email"))
    @Test fun blankEmailFails() = assertNotNull(AuthValidator.validateEmail(" "))
    @Test fun shortPasswordFails() = assertNotNull(AuthValidator.validatePassword("12345"))
    @Test fun sixCharPasswordPasses() = assertNull(AuthValidator.validatePassword("123456"))
    @Test fun mismatchFails() =
        assertEquals("Passwords do not match", AuthValidator.validatePasswordConfirmation("abcdef", "abcdeg"))
    @Test fun matchPasses() = assertNull(AuthValidator.validatePasswordConfirmation("abcdef", "abcdef"))
}
