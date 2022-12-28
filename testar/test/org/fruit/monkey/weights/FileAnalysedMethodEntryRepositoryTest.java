package org.fruit.monkey.weights;

import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FileAnalysedMethodEntryRepositoryTest {

    private String filePath;

    @After
    public void cleanup() {
        var file = new File(filePath);
        if(file.exists()) {
            file.delete();
        }
    }

    @Test
    public void shouldSerializeData() {
        filePath = getClass().getClassLoader().getResource("analyzed-methods-repository").getPath()
                + "/" + "serialized-repository";
        var repository = new FileAnalysedMethodEntryRepository(filePath);
        repository.saveAll(prepareData());

        var file = new File(filePath);
        assertTrue(file.exists());
    }

    @Test
    public void shouldReadValues() {
        filePath = getClass().getClassLoader().getResource("analyzed-methods-repository").getPath()
                + "/" + "serialized-repository";
        var repository = new FileAnalysedMethodEntryRepository(filePath);
        repository.saveAll(prepareData());

        var readEntry1 = repository.findByClassName("com.example.Order");
        var readEntry2 = repository.findByClassName("com.example.OrderManager");

        assertEquals(1, readEntry1.size());
        assertEquals(1, readEntry2.size());

        assertNull(readEntry1.get(0).getId());
        assertEquals("com.example.Order", readEntry1.get(0).getClassName());
        assertEquals("addProduct", readEntry1.get(0).getMethodName());
        assertTrue(readEntry1.get(0).getParameters().containsAll(List.of("String", "String")));
        assertEquals(BigDecimal.valueOf(0.8), readEntry1.get(0).getCoverage());
        assertEquals(1, readEntry1.get(0).getIssues().size());
        assertEquals(new Issue(null, "key", "BUG", "MAJOR", 12L), readEntry1.get(0).getIssues().get(0));

        assertNull(readEntry2.get(0).getId());
        assertEquals("com.example.OrderManager", readEntry2.get(0).getClassName());
        assertEquals("submitOrder", readEntry2.get(0).getMethodName());
        assertTrue(readEntry2.get(0).getParameters().containsAll(List.of("String", "Long")));
        assertEquals(BigDecimal.ZERO, readEntry2.get(0).getCoverage());
        assertEquals(2, readEntry2.get(0).getIssues().size());
        assertTrue(readEntry2.get(0).getIssues().containsAll(
                List.of(new Issue(null, "key", "CODE_SMELL", "MINOR", 22L),
                        new Issue(null, "key", "VULNERABILITY", "BLOCKER", 51L))));
    }



    private HashMap<String, List<AnalysedMethodEntry>> prepareData() {
        return new HashMap<>() {{
            put("com.example.Order", List.of(
                    new AnalysedMethodEntry(null, "com.example.Order", "addProduct",
                                            List.of("String", "String"), BigDecimal.valueOf(0.8),
                                            List.of(new Issue(null, "key", "BUG", "MAJOR", 12L)))
            ));

            put("com.example.OrderManager", List.of(
                    new AnalysedMethodEntry(null, "com.example.OrderManager", "submitOrder",
                                            List.of("String", "Long"), BigDecimal.ZERO,
                                            List.of(new Issue(null, "key", "CODE_SMELL", "MINOR", 22L),
                                                    new Issue(null, "key", "VULNERABILITY", "BLOCKER", 51L)))
            ));
        }};
    }

}