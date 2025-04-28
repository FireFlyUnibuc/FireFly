package ro.unibuc.hello.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.entity.MoneyRequest;
import ro.unibuc.hello.service.MoneyRequestService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/money-requests")
public class MoneyRequestController {

    private final MoneyRequestService moneyRequestService;
    private final MeterRegistry meterRegistry;

    @Autowired
    public MoneyRequestController(MoneyRequestService moneyRequestService, MeterRegistry meterRegistry) {
        this.moneyRequestService = moneyRequestService;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping
    @Timed(value = "money_requests.get.all", description = "Time taken to get all money requests")
    public List<MoneyRequest> getAllRequests() {
        return moneyRequestService.getAllRequests();
    }

    @GetMapping("/{id}")
    public Optional<MoneyRequest> getRequestById(@PathVariable String id) {
        return moneyRequestService.getRequestById(id);
    }

    @GetMapping("/user/{toAccountId}")
    public List<MoneyRequest> getRequestsForUser(@PathVariable String toAccountId) {
        return moneyRequestService.getRequestsForUser(toAccountId);
    }

    @PostMapping
    public MoneyRequest createRequest(@RequestBody MoneyRequest request) {
        meterRegistry.counter("money_requests.created.count").increment();
        return moneyRequestService.createRequest(request);
    }

    @PutMapping("/{id}/status")
    public MoneyRequest updateRequestStatus(@PathVariable String id, @RequestParam String status) {
        if (!status.equals("APPROVED") && !status.equals("DECLINED")) {
            throw new IllegalArgumentException("Invalid status value.");
        }
        meterRegistry.counter("money_requests.status.updated.count").increment();
        return moneyRequestService.updateRequestStatus(id, status);
    }
}
