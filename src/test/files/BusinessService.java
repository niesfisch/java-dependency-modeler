package de.marcelsauer.test_files;

import org.springframework.stereotype.Service;

@Service
public class BusinessService {

    private DomainService1 domainService1 = new DomainService1Impl();
    private DomainService2 domainService2;

    public String someMethod (String fingerprint) {
        try {
            return "xxx";
        } catch (Exception1 | Exception2 e) {
            throw new Exception3();
        }
    }

}
