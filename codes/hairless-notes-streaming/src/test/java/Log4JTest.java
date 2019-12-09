import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4JTest {
    private Logger log = LoggerFactory.getLogger(Log4JTest.class);

    @Test
    public void info() {
        log.info("xxx-info");
    }

}
