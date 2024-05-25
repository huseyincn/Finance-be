package com.huseyincan.financeportfolio.batch;

import com.huseyincan.financeportfolio.dao.Portfolio;
import com.huseyincan.financeportfolio.dto.BinanceResponseDto;
import com.huseyincan.financeportfolio.dto.SymbolPrice;
import com.huseyincan.financeportfolio.repository.PortfolioRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
public class RevenueCalculator {

    private PortfolioRepository repository;
    private final WebClient webClient;

    @Autowired
    public RevenueCalculator(PortfolioRepository repository) {
        this.repository = repository;
        this.webClient = WebClient.builder().baseUrl("https://api.binance.com").build();
    }

    @PostConstruct
    public void runBatchJobAtStartup() {
        runBatchJob();
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void runBatchJob() {
        List<BinanceResponseDto> a = getTheMarketData();
        Map<String, Double> priceList = parseThePairs(a);
        log.info("Batch job started");
        List<Portfolio> portfolios = repository.findAll();
        portfolios.forEach(portfolio -> portfolio.setRevenue(calculateRevenue(portfolio.getElements(), priceList)));
        repository.saveAll(portfolios);
        log.info("Batch job finished");
    }


    private double calculateRevenue(List<SymbolPrice> userP, Map<String, Double> priceList) {
        double totalRevenue = 0;
        for (SymbolPrice symbolPrice : userP) {
            String pair = getThePair(symbolPrice.getSymbol());
            double price = calculatePrice(pair, priceList);
            double oldPrice = calculateOldPriceAsUsdt(pair,symbolPrice.getPrice(),priceList);
            totalRevenue += (price - oldPrice) * symbolPrice.getQuantity();
        }
        return totalRevenue;
    }

    private double calculatePrice(String pair, Map<String, Double> priceList) {
        // Split the pair into base and quote currencies
        log.info(pair + " " + priceList.get(pair).toString());
        String[] currencies = pair.split("/");
        String baseCurrency = currencies[0];
        String quoteCurrency = currencies[1];

        // Base case: if the pair is already in the priceList and the quote currency is "USDT", "BUSD", or "USD", return its price
        if (priceList.containsKey(pair) && (quoteCurrency.equals("USDT") || quoteCurrency.equals("BUSD") || quoteCurrency.equals("USD"))) {
            return priceList.get(pair);
        }

        // Recursive case: find the next pair and multiply its price
        String nextPair = quoteCurrency + "/USDT";
        if (!priceList.containsKey(nextPair)) {
            nextPair = quoteCurrency + "/BUSD";
        }
        if (!priceList.containsKey(nextPair)) {
            nextPair = quoteCurrency + "/USD";
        }
        if (priceList.containsKey(nextPair)) {
            return priceList.get(baseCurrency + "/" + quoteCurrency) * calculatePrice(nextPair, priceList);
        } else {
            throw new IllegalArgumentException("Unable to find a valid price for pair: " + pair);
        }
    }

    private double calculateOldPriceAsUsdt(String initialPair, double oldPrice, Map<String, Double> priceList) {
        // Split the pair into base and quote currencies
        String[] currencies = initialPair.split("/");
        String baseCurrency = currencies[0];
        String quoteCurrency = currencies[1];

        // Base case: if the quote currency is "USDT", "BUSD", or "USD", return the old price
        if (quoteCurrency.equals("USDT") || quoteCurrency.equals("BUSD") || quoteCurrency.equals("USD")) {
            return oldPrice;
        }

        // Recursive case: find the next pair and multiply its price
        String nextPair = quoteCurrency + "/USDT";
        if (!priceList.containsKey(nextPair)) {
            nextPair = quoteCurrency + "/BUSD";
        }
        if (!priceList.containsKey(nextPair)) {
            nextPair = quoteCurrency + "/USD";
        }
        if (priceList.containsKey(nextPair)) {
            return oldPrice * calculateOldPriceAsUsdt(nextPair, priceList.get(nextPair), priceList);
        } else {
            throw new IllegalArgumentException("Unable to find a valid price for pair: " + initialPair);
        }
    }



    private List<BinanceResponseDto> getTheMarketData() {
        return this.webClient.get().uri("/api/v3/ticker/price")
                .retrieve().bodyToFlux(new ParameterizedTypeReference<BinanceResponseDto>() {
                })
                .collectList().block();
    }

    private Map<String, Double> parseThePairs(List<BinanceResponseDto> dtoList) {
        Map<String, Double> mapPairs = new HashMap<>();
        List<String> coins = List.of("USDS", "USDC", "BUSD", "TRY", "USDT", "ETH", "BTC", "LTC", "NEO", "BNB", "QTUM", "EOS", "SNT", "BNT", "GAS",
                "BCC", "HSR", "OAX", "DNT", "MCO", "ICN", "ZRX", "OMG", "WTC", "YOYO", "LRC", "TRX", "SNGLS",
                "STRAT", "BQX", "FUN", "KNC", "CDT", "XVG", "IOTA", "SNM", "LINK", "CVC", "TNT", "REP", "MDA", "MTL",
                "SALT", "NULS", "SUB", "STX", "MTH", "ADX", "ETC", "ENG", "ZEC", "AST", "GNT", "DGD", "BAT", "DASH",
                "POWR", "BTG", "REQ", "XMR", "EVX", "VIB", "ENJ", "VEN", "ARK", "XRP", "MOD", "STORJ", "KMD", "RCN",
                "EDO", "DATA", "DLT", "MANA", "PPT", "RDN", "GXS", "AMB", "ARN", "BCPT", "CND", "GVT", "POE", "BTS",
                "TFUEL", "FUEL", "XZC", "QSP", "LSK", "BCD", "TNB", "ADA", "LEND", "XLM", "CMT", "WAVES", "WABI", "GTO",
                "ICX", "OST", "ELF", "AION", "WINGS", "BRD", "NEBL", "NAV", "VIBE", "LUN", "TRIG", "APPC", "CHAT",
                "RLC", "INS", "PIVX", "IOST", "STEEM", "NANO", "AE", "VIA", "BLZ", "SYS", "RPX", "NCASH", "POA", "ONT",
                "ZIL", "STORM", "XEM", "WAN", "WPR", "QLC", "GRS", "CLOAK", "LOOM", "BCN", "TUSD", "ZEN", "SKY",
                "THETA", "IOTX", "QKC", "AGI", "NXS", "SC", "NPXS", "KEY", "NAS", "MFT", "DENT", "IQ", "ARDR", "HOT",
                "VET", "DOCK", "POLY", "VTHO", "ONG", "PHX", "HC", "PAX", "RVN", "DCR", "MITH", "BCHABC",
                "BCHSV", "REN", "BTT", "FET", "CELR", "MATIC", "ATOM", "PHB", "ONE", "FTM", "BTCB",
                "USDSB", "CHZ", "COS", "ALGO", "GO", "ERD", "DOGE", "BGBP", "DUSK", "ANKR", "WIN", "TUSDB", "COCOS", "PERL",
                "TOMO", "BUSD", "BAND", "BEAM", "HBAR", "XTZ", "NGN", "DGB", "NKN", "GBP", "EUR", "KAVA", "RUB", "UAH",
                "ARPA", "TRY", "CTXC", "AERGO", "BCH", "TROY", "BRL", "VITE", "FTT", "AUD", "OGN", "DREP", "BULL",
                "BEAR", "ETHBULL", "ETHBEAR", "XRPBULL", "XRPBEAR", "EOSBULL", "EOSBEAR", "TCT", "WRX", "LTO", "ZAR",
                "MBL", "COTI", "BKRW", "BNBBULL", "BNBBEAR", "HIVE", "STPT", "SOL", "IDRT", "CTSI", "CHR", "BTCUP",
                "BTCDOWN", "HNT", "JST", "FIO", "BIDR", "STMX", "MDT", "PNT", "COMP", "IRIS", "MKR", "SXP", "SNX",
                "DAI", "ETHUP", "ETHDOWN", "ADAUP", "ADADOWN", "LINKUP", "LINKDOWN", "DOT", "RUNE", "BNBUP", "BNBDOWN",
                "XTZUP", "XTZDOWN", "AVA", "BAL", "YFI", "SRM", "ANT", "CRV", "SAND", "OCEAN", "NMR", "LUNA", "IDEX",
                "RSR", "PAXG", "WNXM", "TRB", "EGLD", "BZRX", "WBTC", "KSM", "SUSHI", "YFII", "DIA", "BEL", "UMA",
                "EOSUP", "TRXUP", "EOSDOWN", "TRXDOWN", "XRPUP", "XRPDOWN", "DOTUP", "DOTDOWN", "NBS", "WING", "SWRV",
                "LTCUP", "LTCDOWN", "CREAM", "UNI", "OXT", "SUN", "AVAX", "BURGER", "BAKE", "FLM", "SCRT", "XVS",
                "CAKE", "SPARTA", "UNIUP", "UNIDOWN", "ALPHA", "ORN", "UTK", "NEAR", "VIDT", "AAVE", "FIL", "SXPUP",
                "SXPDOWN", "INJ", "FILDOWN", "FILUP", "YFIUP", "YFIDOWN", "CTK", "EASY", "AUDIO", "BCHUP", "BCHDOWN",
                "BOT", "AXS", "AKRO", "HARD", "KP3R", "RENBTC", "SLP", "STRAX", "UNFI", "CVP", "BCHA", "FOR", "FRONT",
                "ROSE", "HEGIC", "AAVEUP", "AAVEDOWN", "PROM", "BETH", "SKL", "GLM", "SUSD", "COVER", "GHST", "SUSHIUP",
                "SUSHIDOWN", "XLMUP", "XLMDOWN", "DF", "JUV", "PSG", "BVND", "GRT", "CELO", "TWT", "REEF", "OG", "ATM",
                "ASR", "1INCH", "RIF", "BTCST", "TRU", "DEXE", "CKB", "FIRO", "LIT", "PROS", "VAI", "SFP", "FXS",
                "DODO", "AUCTION", "UFT", "ACM", "PHA", "TVK", "BADGER", "FIS", "OM", "POND", "ALICE", "DEGO", "BIFI",
                "LINA", "FDUSD", "SHIB");
        for (BinanceResponseDto x : dtoList) {
            for (String s : coins) {
                String a = x.getSymbol();
                if (a.equals("RENBTCBTC")) {
                    mapPairs.put("RENBTC/BTC", Double.valueOf(x.getPrice()));
                    break;
                } else if (a.equals("BNBUSDP")) {
                    mapPairs.put("BNB/USDP", Double.valueOf(x.getPrice()));
                    break;
                } else if (a.equals("ARBUSDT")) {
                    mapPairs.put("ARB/USDT", Double.valueOf(x.getPrice()));
                    break;
                } else if (a.equals("WBTC")) {
                    mapPairs.put("WBTC", Double.valueOf(x.getPrice()));
                    break;
                }
                int tmp = a.indexOf(s);
                if (tmp != -1) {
                    String[] tmpe = a.split(s);
                    String pair;
                    if (tmpe[0].equals("")) {
                        pair = s + "/" + tmpe[1];
                    } else {
                        pair = tmpe[0] + "/" + s;
                    }
                    mapPairs.put(pair, Double.valueOf(x.getPrice()));
                    break;
                }
            }
        }
        return mapPairs;
    }

    private String getThePair(String symbol) {
        List<String> coins = List.of("USDS", "USDC", "BUSD", "TRY", "USDT", "ETH", "BTC", "LTC", "NEO", "BNB", "QTUM", "EOS", "SNT", "BNT", "GAS",
                "BCC", "HSR", "OAX", "DNT", "MCO", "ICN", "ZRX", "OMG", "WTC", "YOYO", "LRC", "TRX", "SNGLS",
                "STRAT", "BQX", "FUN", "KNC", "CDT", "XVG", "IOTA", "SNM", "LINK", "CVC", "TNT", "REP", "MDA", "MTL",
                "SALT", "NULS", "SUB", "STX", "MTH", "ADX", "ETC", "ENG", "ZEC", "AST", "GNT", "DGD", "BAT", "DASH",
                "POWR", "BTG", "REQ", "XMR", "EVX", "VIB", "ENJ", "VEN", "ARK", "XRP", "MOD", "STORJ", "KMD", "RCN",
                "EDO", "DATA", "DLT", "MANA", "PPT", "RDN", "GXS", "AMB", "ARN", "BCPT", "CND", "GVT", "POE", "BTS",
                "TFUEL", "FUEL", "XZC", "QSP", "LSK", "BCD", "TNB", "ADA", "LEND", "XLM", "CMT", "WAVES", "WABI", "GTO",
                "ICX", "OST", "ELF", "AION", "WINGS", "BRD", "NEBL", "NAV", "VIBE", "LUN", "TRIG", "APPC", "CHAT",
                "RLC", "INS", "PIVX", "IOST", "STEEM", "NANO", "AE", "VIA", "BLZ", "SYS", "RPX", "NCASH", "POA", "ONT",
                "ZIL", "STORM", "XEM", "WAN", "WPR", "QLC", "GRS", "CLOAK", "LOOM", "BCN", "TUSD", "ZEN", "SKY",
                "THETA", "IOTX", "QKC", "AGI", "NXS", "SC", "NPXS", "KEY", "NAS", "MFT", "DENT", "IQ", "ARDR", "HOT",
                "VET", "DOCK", "POLY", "VTHO", "ONG", "PHX", "HC", "PAX", "RVN", "DCR", "MITH", "BCHABC",
                "BCHSV", "REN", "BTT", "FET", "CELR", "MATIC", "ATOM", "PHB", "ONE", "FTM", "BTCB",
                "USDSB", "CHZ", "COS", "ALGO", "GO", "ERD", "DOGE", "BGBP", "DUSK", "ANKR", "WIN", "TUSDB", "COCOS", "PERL",
                "TOMO", "BUSD", "BAND", "BEAM", "HBAR", "XTZ", "NGN", "DGB", "NKN", "GBP", "EUR", "KAVA", "RUB", "UAH",
                "ARPA", "TRY", "CTXC", "AERGO", "BCH", "TROY", "BRL", "VITE", "FTT", "AUD", "OGN", "DREP", "BULL",
                "BEAR", "ETHBULL", "ETHBEAR", "XRPBULL", "XRPBEAR", "EOSBULL", "EOSBEAR", "TCT", "WRX", "LTO", "ZAR",
                "MBL", "COTI", "BKRW", "BNBBULL", "BNBBEAR", "HIVE", "STPT", "SOL", "IDRT", "CTSI", "CHR", "BTCUP",
                "BTCDOWN", "HNT", "JST", "FIO", "BIDR", "STMX", "MDT", "PNT", "COMP", "IRIS", "MKR", "SXP", "SNX",
                "DAI", "ETHUP", "ETHDOWN", "ADAUP", "ADADOWN", "LINKUP", "LINKDOWN", "DOT", "RUNE", "BNBUP", "BNBDOWN",
                "XTZUP", "XTZDOWN", "AVA", "BAL", "YFI", "SRM", "ANT", "CRV", "SAND", "OCEAN", "NMR", "LUNA", "IDEX",
                "RSR", "PAXG", "WNXM", "TRB", "EGLD", "BZRX", "WBTC", "KSM", "SUSHI", "YFII", "DIA", "BEL", "UMA",
                "EOSUP", "TRXUP", "EOSDOWN", "TRXDOWN", "XRPUP", "XRPDOWN", "DOTUP", "DOTDOWN", "NBS", "WING", "SWRV",
                "LTCUP", "LTCDOWN", "CREAM", "UNI", "OXT", "SUN", "AVAX", "BURGER", "BAKE", "FLM", "SCRT", "XVS",
                "CAKE", "SPARTA", "UNIUP", "UNIDOWN", "ALPHA", "ORN", "UTK", "NEAR", "VIDT", "AAVE", "FIL", "SXPUP",
                "SXPDOWN", "INJ", "FILDOWN", "FILUP", "YFIUP", "YFIDOWN", "CTK", "EASY", "AUDIO", "BCHUP", "BCHDOWN",
                "BOT", "AXS", "AKRO", "HARD", "KP3R", "RENBTC", "SLP", "STRAX", "UNFI", "CVP", "BCHA", "FOR", "FRONT",
                "ROSE", "HEGIC", "AAVEUP", "AAVEDOWN", "PROM", "BETH", "SKL", "GLM", "SUSD", "COVER", "GHST", "SUSHIUP",
                "SUSHIDOWN", "XLMUP", "XLMDOWN", "DF", "JUV", "PSG", "BVND", "GRT", "CELO", "TWT", "REEF", "OG", "ATM",
                "ASR", "1INCH", "RIF", "BTCST", "TRU", "DEXE", "CKB", "FIRO", "LIT", "PROS", "VAI", "SFP", "FXS",
                "DODO", "AUCTION", "UFT", "ACM", "PHA", "TVK", "BADGER", "FIS", "OM", "POND", "ALICE", "DEGO", "BIFI",
                "LINA", "FDUSD", "SHIB");
        for (String s : coins) {
            if (symbol.equals("RENBTCBTC")) {
                return "RENBTC/BTC";
            } else if (symbol.equals("BNBUSDP")) {
                return "BNB/USDP";
            } else if (symbol.equals("ARBUSDT")) {
                return "ARB/USDT";
            } else if (symbol.equals("WBTC")) {
                return "WBTC";
            }
            int tmp = symbol.indexOf(s);
            if (tmp != -1) {
                String[] tmpe = symbol.split(s);
                String pair;
                if (tmpe[0].equals("")) {
                    pair = s + "/" + tmpe[1];
                } else {
                    pair = tmpe[0] + "/" + s;
                }
                return pair;
            }
        }
        return null;
    }
}
