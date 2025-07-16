package com.example.mspr4;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Mspr4ApplicationTests {

	@Test
	void contextLoads() {
		assert true;
	}

	@Test
    void applicationMainMethodExists() {
        // Vérifie que la classe principale existe
        try {
            Class.forName("com.example.mspr4.Mspr4Application");
            assert true;
        } catch (ClassNotFoundException e) {
            assert false : "Classe principale Mspr4Application non trouvée";
        }
    }
}
