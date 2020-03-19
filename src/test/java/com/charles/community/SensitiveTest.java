package com.charles.community;

import com.charles.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author charles
 * @date 2020/3/19 17:31
 */
@SpringBootTest
public class SensitiveTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "";
        System.out.println(sensitiveFilter.filter(text));

    }
}
