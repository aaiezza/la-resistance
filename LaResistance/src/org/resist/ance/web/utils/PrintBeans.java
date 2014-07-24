package org.resist.ance.web.utils;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class PrintBeans
{

    private final ApplicationContext APPContext;

    @Autowired
    private PrintBeans( ApplicationContext appContext )
    {
        APPContext = appContext;
    }

    public String printBeans()
    {
        return Arrays.asList( APPContext.getBeanDefinitionNames() ).toString();
    }

}
