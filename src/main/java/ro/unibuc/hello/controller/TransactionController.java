package ro.unibuc.hello.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.entity.Transaction;
import ro.unibuc.hello.service.TransactionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final MeterRegistry meterRegistry;

    @Autowired
    public TransactionController(TransactionService transactionService, MeterRegistry meterRegistry) {
        this.transactionService = transactionService;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping
    @Timed(value = "transactions.get.all", description = "Time taken to get all transactions")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    public Optional<Transaction> getTransactionById(@PathVariable String id) {
        return transactionService.getTransactionById(id);
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        meterRegistry.counter("transactions.created.count").increment();
        return transactionService.saveTransaction(transaction);
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable String id) {
        meterRegistry.counter("transactions.deleted.count").increment();
        transactionService.deleteTransaction(id);
    }
}
