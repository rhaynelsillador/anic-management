package com.sillador.strecs.admin;//package com.sillador.strecs;


import com.sillador.strecs.admin.entity.Account;
import com.sillador.strecs.admin.entity.Permission;
import com.sillador.strecs.admin.entity.Role;
import com.sillador.strecs.admin.repository.AccountRepository;
import com.sillador.strecs.admin.repository.PermissionRepository;
import com.sillador.strecs.admin.repository.RoleRepository;
import com.sillador.strecs.admin.service.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AccountDataInit implements ApplicationRunner {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {

        Optional<Account> accountOptional = accountRepository.findByEmail("rhaynel.samar.sillador@gmail.com");
        if(accountOptional.isEmpty()) {
            Account account = new Account();
            account.setEmail("rhaynel.samar.sillador@gmail.com");
            account.setPassword(accountService.hashPassword("12345678"));
            account.setEnabled(true);
            account.setCreatedAt(LocalDateTime.now());
            account.setUpdatedAt(LocalDateTime.now());
            account.setUsername("rhaynel");

            accountRepository.save(account);
        }

        if(roleRepository.findByName("Admin").isEmpty()) {
            Role role = new Role();
            role.setName("Admin");
            role.setDescription("Admin Account");
            role.setAccounts(new HashSet<>(accountRepository.findAll()));

            roleRepository.save(role);

            Account account = accountOptional.orElse(null);
            account.setRoles(new HashSet<>(roleRepository.findAll()));

            accountRepository.save(account);
        }




        accountOptional = accountRepository.findByEmail("clarissa.cepeda.sillador@gmail.com");
        if(accountOptional.isEmpty()) {
            Account account = new Account();
            account.setEmail("clarissa.cepeda.sillador@gmail.com");
            account.setPassword(accountService.hashPassword("12345678"));
            account.setEnabled(true);
            account.setCreatedAt(LocalDateTime.now());
            account.setUpdatedAt(LocalDateTime.now());
            account.setUsername("clarissa");

            accountRepository.save(account);


            if(roleRepository.findByName("Teacher").isEmpty()) {

                Set<Account> teacher = new HashSet<>();
                teacher.add(account);

                Role role = new Role();
                role.setName("Teacher");
                role.setDescription("Teacher Account");
                role.setAccounts(teacher);

                roleRepository.save(role);

            }
        }else{
            Account account = accountOptional.get();
            account.setRoles(new HashSet<>(roleRepository.findAllByName("Teacher")));

            accountRepository.save(account);
        }


    }
}
