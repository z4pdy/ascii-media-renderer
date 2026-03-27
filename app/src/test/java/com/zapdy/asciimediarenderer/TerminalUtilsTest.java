package com.zapdy.asciimediarenderer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.jline.terminal.Size;

class TerminalUtilsTest {
    @Test 
    void shouldReturnSomeTerminalSize() {
        assertEquals(true, true); 
        Size size = TerminalUtils.getTerminalSize();
        assertNotNull(size);
        assertTrue(size.getColumns() > 0);
        assertTrue(size.getRows() > 0);
    }
}
