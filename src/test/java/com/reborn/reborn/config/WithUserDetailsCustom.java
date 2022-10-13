package com.reborn.reborn.config;

import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@WithUserDetails(value = "email@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
public @interface WithUserDetailsCustom {
}
