package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @PersistenceContext
    private EntityManager em;

    @GetMapping("/accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/accounts/{cpf}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Account> getAccountByCpf(@PathVariable(value = "cpf") Long accountCpf)
            throws NotFoundAccount {

        Account account = accountRepository.findById(accountCpf)
                .orElseThrow(() -> new NotFoundAccount("Account " + accountCpf + " not found"));
        return ResponseEntity.ok().body(account);
    }

    @PostMapping(
            value = "/accounts", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@Valid @RequestBody Account account) throws UnauthorizedEmailAccount {
        TypedQuery query = em.createQuery("select a from Account a where a.email = ?1", Account.class);
        query.setParameter(1, account.getEmail());

        List<Account> accountList = query.getResultList();
        if (accountList.size() > 0){
            throw new UnauthorizedEmailAccount("Email duplicated");
        }

        return accountRepository.save(account);
    }

    @PutMapping("/accounts/{cpf}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Account> updateAccount(
            @PathVariable(value = "cpf") Long accountCpf, @Valid @RequestBody Account accountDetails)
            throws NotFoundAccount, UnauthorizedEmailAccount {

        TypedQuery query = em.createQuery("select a from Account a where a.email = ?1", Account.class);
        query.setParameter(1, accountDetails.getEmail());

        List<Account> accountList = query.getResultList();
        if (accountList.size() > 0){
            throw new UnauthorizedEmailAccount("Email duplicated");
        }

        Account account = accountRepository.findById(accountCpf)
                .orElseThrow(() -> new NotFoundAccount("Account " + accountCpf + " not found"));

        account.setBirthday(accountDetails.getBirthday());
        account.setCpf(accountDetails.getCpf());
        account.setEmail(accountDetails.getEmail());
        account.setFullName(accountDetails.getFullName());

        final Account updatedAccount = accountRepository.save(account);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/account/{cpf}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Boolean> deleteAccount(@PathVariable(value = "cpf") Long accountCpf) throws NotFoundAccount {
        Account account = accountRepository.findById(accountCpf)
                .orElseThrow(() -> new NotFoundAccount("Account " + accountCpf + " not found"));

        accountRepository.delete(account);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}
