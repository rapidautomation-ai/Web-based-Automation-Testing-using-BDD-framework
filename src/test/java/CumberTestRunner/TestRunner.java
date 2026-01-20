package CumberTestRunner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

//@RunWith(Cucumber.class)
@CucumberOptions(
        features= "src/test/resources/Features",
        glue= "automation/test/stepsdefs",
        plugin = {"html:target/reports/cucumber.html"})


public class TestRunner extends AbstractTestNGCucumberTests {
}
