package com.huseyincan.financeportfolio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class FinancePortfolioApplication {

    @Autowired
    private SimpMessagingTemplate template;

    public static void main(String[] args) {
        SpringApplication.run(FinancePortfolioApplication.class, args);
    }


    @Scheduled(fixedRate = 5000)
    public void sendRandomNumber() {
//        try {
//            FxQuote usdgbp = YahooFinance.getFx("USDGBP=X");
//            template.convertAndSend("/topic/messages", usdgbp);
//        } catch (Exception e) {
//            log.error(e.toString());
//        }
    }
}
