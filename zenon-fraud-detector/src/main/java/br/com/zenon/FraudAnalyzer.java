package br.com.zenon;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FraudAnalyzer {

    private final List<Transaction> transactions;

    public FraudAnalyzer(List<Transaction> transactions) {
        Objects.requireNonNull(transactions);
        this.transactions = transactions;
    }

    public long countFrauds(){
        return this.transactions
                .stream()
                .filter(Transaction::isFraud)
                .count();
    }


    public List<BigDecimal> findHighestValuesFraudsAmounts(int limit) {
        return this.highValueFraudStream()
                .map(Transaction::amount)
                .limit(limit)
                .toList();
    }

    public List<String> findTopSuspiciousClients(int limit) {
        return this.highValueFraudStream()
                .map(transaction -> transaction.origin().name())
                .distinct()
                .limit(limit)
                .toList();
    }

    public BigDecimal calculateTotalFraudLoss() {
        return this.fraudStream()
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<TransactionType, Long> countFraudsByType() {
        return this.fraudStream()
                .collect(Collectors.groupingBy(Transaction::type, Collectors.counting()));
    }

    private Stream<Transaction> fraudStream() {
        return transactions
                .stream().
                filter(Transaction::isFraud);
    }

    private Stream<Transaction> highValueFraudStream() {
        return fraudStream()
                .sorted(Comparator.comparing(Transaction::amount).reversed());
    }
}
