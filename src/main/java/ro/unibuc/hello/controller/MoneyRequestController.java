package ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import ro.unibuc.hello.entity.MoneyRequest;
import ro.unibuc.hello.service.MoneyRequestService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/money-requests")
public class MoneyRequestController {

    private final MoneyRequestService moneyRequestService;

    @Autowired
    public MoneyRequestController(MoneyRequestService moneyRequestService) {
        this.moneyRequestService = moneyRequestService;
    }

    @GetMapping
    public List<MoneyRequest> getAllRequests() {
        return moneyRequestService.getAllRequests();
    }

    @GetMapping("/{id}")
    public Optional<MoneyRequest> getRequestById(@PathVariable("id") String id) {
        return moneyRequestService.getRequestById(id);
    }

    @GetMapping("/user/{toAccountId}")
    public List<MoneyRequest> getRequestsForUser(@PathVariable("toAccountId") String toAccountId) {
        return moneyRequestService.getRequestsForUser(toAccountId);
    }

    @PostMapping
    public MoneyRequest createRequest(@RequestBody MoneyRequest request) {
        return moneyRequestService.createRequest(request);
    }

    @PutMapping("/{id}/status")
    public MoneyRequest updateRequestStatus(@PathVariable("id") String id,
                                            @RequestParam("status") String status) {
        if (!status.equals("APPROVED") && !status.equals("DECLINED")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value.");
        }
        return moneyRequestService.updateRequestStatus(id, status);
    }
}
