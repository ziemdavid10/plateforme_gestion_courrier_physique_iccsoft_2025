package iccsoft_auth.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import iccsoft_auth.model.Employe;
import iccsoft_auth.repo.EmployeRepository;


@Service
public class EmployeDetailsServiceImpl implements UserDetailsService {
    @Autowired
    EmployeRepository employeRepository;
    
    @Autowired
    RestTemplate restTemplate;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Loading user by username: " + username);
        
        // Essayer de trouver l'utilisateur localement d'abord
        Employe employe = employeRepository.findByUsername(username).orElse(null);
        
        if (employe != null) {
            System.out.println("User found locally: " + username + " with role: " + employe.getRole());
            return EmployeDetailsImpl.build(employe);
        }
        
        System.out.println("User not found locally: " + username);
        
        // Pour l'instant, on ne cherche que localement pour éviter les problèmes
        // TODO: Implémenter la synchronisation avec le service utilisateur si nécessaire
        
        throw new UsernameNotFoundException("User Not Found with username: " + username);
    }

    public Employe findByUsername(String username) {
        return employeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));
    }

    public Employe updateEmploye(Employe employe) {
        return employeRepository.save(employe);
    }

}
