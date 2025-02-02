package com.eventsourcing.bankAccount.delivery;


import com.eventsourcing.bankAccount.commands.*;
import com.eventsourcing.bankAccount.dto.*;
import com.eventsourcing.bankAccount.queries.BankAccountQueryService;
import com.eventsourcing.bankAccount.queries.FindAllOrderByBalance;
import com.eventsourcing.bankAccount.queries.GetBankAccountByIDQuery;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/bank")
@Slf4j
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountCommandService commandService;
    private final BankAccountQueryService queryService;
    private final ULID idGen;

    @GetMapping("{aggregateId}")
    public ResponseEntity<BankAccountResponseDTO> getBankAccount(@PathVariable String aggregateId) {
        final var result = queryService.handle(new GetBankAccountByIDQuery(aggregateId));
        log.info("Get bank account result: {}", result);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<String> createBankAccount(@Valid @RequestBody CreateBankAccountRequestDTO dto) {
        final var aggregateID = idGen.nextValue().toString();
        final var id = commandService.handle(new CreateBankAccountCommand(aggregateID, dto.email(), dto.userName(), dto.address()));
        log.info("Created bank account id: {}", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PostMapping(path = "/deposit/{aggregateId}")
    public ResponseEntity<Void> depositAmount(@Valid @RequestBody DepositAmountRequestDTO dto, @PathVariable String aggregateId) {
        commandService.handle(new DepositAmountCommand(aggregateId, dto.amount()));
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/email/{aggregateId}")
    public ResponseEntity<Void> changeEmail(@Valid @RequestBody ChangeEmailRequestDTO dto, @PathVariable String aggregateId) {
        commandService.handle(new ChangeEmailCommand(aggregateId, dto.newEmail()));
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/address/{aggregateId}")
    public ResponseEntity<Void> changeAddress(@Valid @RequestBody ChangeAddressRequestDTO dto, @PathVariable String aggregateId) {
        commandService.handle(new ChangeAddressCommand(aggregateId, dto.newAddress()));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/balance")
    public ResponseEntity<Page<BankAccountResponseDTO>> getAllOrderByBalance(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                             @RequestParam(name = "size", defaultValue = "10") Integer size) {

        final var result = queryService.handle(new FindAllOrderByBalance(page, size));
        log.info("Get all by balance page: {}, size: {}, result: {}", page, size, result);
        return ResponseEntity.ok(result);
    }
}
