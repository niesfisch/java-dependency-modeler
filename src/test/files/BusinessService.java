package de.marcelsauer.test_files;

import org.springframework.stereotype.Service;

@Service
public class BusinessService {

    private DomainService1 domainService1 = new DomainService1Impl();
    private DomainService2 domainService2;

}
