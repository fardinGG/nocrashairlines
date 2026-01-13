package com.nocrashairlines;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Simple Test to Verify JUnit Setup")
class SimpleTest {
    
    @Test
    @DisplayName("Should run basic test")
    void testBasic() {
        assertTrue(true);
        assertEquals(2, 1 + 1);
        System.out.println("Basic test passed!");
    }
    
    @Test
    @DisplayName("Should test string operations")
    void testString() {
        String test = "Hello World";
        assertNotNull(test);
        assertTrue(test.contains("World"));
        System.out.println("String test passed!");
    }
    
    @Test
    @DisplayName("Should test arithmetic")
    void testMath() {
        int result = 5 * 3;
        assertEquals(15, result);
        assertTrue(result > 10);
        System.out.println("Math test passed!");
    }
}