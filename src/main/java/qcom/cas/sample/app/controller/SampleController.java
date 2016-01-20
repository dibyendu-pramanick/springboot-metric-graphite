package qcom.cas.sample.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

@RestController
@RequestMapping("simple")
public class SampleController {

    @Timed
    @RequestMapping("/hello")
    public String helloWorld() {
        return "welcome Dibyendu";
    }
}
