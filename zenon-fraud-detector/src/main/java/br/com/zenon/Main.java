package br.com.zenon;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Main {
    static void main() {
        Transaction t1 = new Transaction(1, TransactionType.PAYMENT, new BigDecimal("9838.64"),
                new TransactionCustomer("C1231006815", new BigDecimal("170136.0"), new BigDecimal("160296.36")),
                new TransactionCustomer("M1979787155", new BigDecimal("0.0"), new BigDecimal("0.0")),
                false, false);

        Transaction t2 = new Transaction(743, TransactionType.CASH_OUT, new BigDecimal("850002.52"),
                new TransactionCustomer("C1280323807", new BigDecimal("850002.52"), new BigDecimal("0.0")),
                new TransactionCustomer("C873221189", new BigDecimal("6510099.11"), new BigDecimal("7360101.63")),
                true, false);

        IO.println(t1);
        IO.println(t2);

        IO.println("--------------------------------------------------------------------------------------------------------------");

        var trasactionIngestor = new TransactionIngestor();
        List<Transaction> transactions = trasactionIngestor.read("data/PS_20174392719_1491204439457_log.csv");
        IO.println(transactions.size());

        transactions.stream().limit(10).forEach(System.out::println);

        IO.println("--------------------------------------------------------------------------------------------------------------");

        List<Transaction> transactionsBadData = trasactionIngestor.read("data/paysim_with_bad_data.csv");
        IO.println(transactionsBadData.size());
        transactionsBadData.forEach(IO::println);

        //tentando criar objeto invalido
        /*new Transaction(1, TransactionType.PAYMENT, new BigDecimal("9838.64"),
                null,
                new TransactionCustomer("M1979787155", new BigDecimal("0.0"), new BigDecimal("0.0")),
                false, false);*/


        IO.println("--------------------------------------------------------------------------------------------------------------");


        FraudAnalyzer fraudAnalyzer = new FraudAnalyzer(transactions);
        long fraudCount = fraudAnalyzer.countFrauds();
        IO.println(fraudCount);

        //Imprima as 3 fraudes de maior valor (amount).
        List<BigDecimal> highestFraudsAmounts = fraudAnalyzer.findHighestValuesFraudsAmounts(3);
        IO.println("Top 3 fraudes de maior valor:");
        //highestFrauds.stream().map(Transaction::amount).forEach(IO::println);
        highestFraudsAmounts.forEach(amount -> IO.println("- %.2f".formatted(amount)));

        //Obter apenas os nomes dos clientes de origem (nameOrig) dessas fraudes e depois gere uma lista sem repetições (Set ou distinct) com os 5 maiores clientes suspeitos.
        List<String> suspiciousClients = fraudAnalyzer.findTopSuspiciousClients(5);
        IO.println("Top 5 clientes suspeitos:");
        suspiciousClients.forEach(IO::println);

        //Calcule o prejuízo total causado pelas fraudes (soma dos amount).
        BigDecimal totalFraudLoss = fraudAnalyzer.calculateTotalFraudLoss();
        IO.println("Prejuizo total: " + totalFraudLoss);

        //Conte quantas fraudes ocorreram por tipo de transação (CASH_OUT, TRANSFER, etc...).
        Map<TransactionType, Long> fraudCountByType = fraudAnalyzer.countFraudsByType();
        IO.println("Fraude por tipo:");
        //IO.println(fraudCountByType);
        fraudCountByType.forEach((TransactionType type, Long count) -> IO.println("- %s: %d".formatted(type, count)));
    }
}
