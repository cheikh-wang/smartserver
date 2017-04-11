package com.smartserver.sample;

import com.smartserver.annotation.Controller;
import com.smartserver.annotation.RequestMapping;

/**
 * author: cheikh.wang on 17/4/7
 * email: wanghonghi@126.com
 */
@Controller("hello")
public class SampleController {

    @RequestMapping("/world")
    public String test(String name, String age) {
        return "hello world";
    }
}
