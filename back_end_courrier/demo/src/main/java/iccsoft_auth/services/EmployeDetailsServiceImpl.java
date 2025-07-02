package iccsoft_auth.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import iccsoft_auth.model.Employe;
import iccsoft_auth.repo.EmployeRepository;


@Service
public class EmployeDetailsServiceImpl implements UserDetailsService {
    @Autowired
    EmployeRepository employeRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employe employe = employeRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return EmployeDetailsImpl.build(employe);
    }

}
