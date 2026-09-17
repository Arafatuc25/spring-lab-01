package kz.iitu.springlab.notify;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component("upper")
@Order(3)
public class UpperNotifier implements Notifier {

    private static final Logger log =
            LoggerFactory.getLogger(UpperNotifier.class);

    @PostConstruct
    void init() {
        log.info("UPPER NOTIFIER >> initialized");
    }

    @Override
    public String send(String message) {
        return "upper: " + message.toUpperCase(Locale.ROOT);
    }

    @Override
    public String channel() {
        return "upper";
    }
}