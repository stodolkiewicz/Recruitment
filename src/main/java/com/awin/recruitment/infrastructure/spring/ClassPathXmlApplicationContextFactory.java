package com.awin.recruitment.infrastructure.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class ClassPathXmlApplicationContextFactory {

    private static final String COMMON_DI_CONFIGURATION = "/di.common.xml";
    private static final String DEVELOPMENT_DI_CONFIGURATION = "/di.development.xml";

    private ClassPathXmlApplicationContextFactory() { }

    public static ClassPathXmlApplicationContext create() {

        return new ClassPathXmlApplicationContext(
            COMMON_DI_CONFIGURATION,
            DEVELOPMENT_DI_CONFIGURATION
        );
    }
}
