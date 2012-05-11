package de.marcelsauer.test_files.package_a;

import de.marcelsauer.test_files.C;
import de.marcelsauer.test_files.package_b.B;
import org.log4j.*;
import org.springframework.stereotype.Service;

@Service
public class A implements AInterface, SomeOtherInterface {

    private static final Logger L = Logger.getLogger(A.class);

    private static final String SOME_STRING = null;

    private final B b = new B();
    private final C c = new C();
    private final long d = 4L;

}
