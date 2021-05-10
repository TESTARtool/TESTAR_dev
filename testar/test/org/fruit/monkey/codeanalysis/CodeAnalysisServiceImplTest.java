package org.fruit.monkey.codeanalysis;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CodeAnalysisServiceImplTest {

    private Path repositoriesDir;
    private CodeAnalysisService codeAnalysisService;

    private final static String NO_SUPPORTED_LANGUAGES = "no_supported_languages";
    private final static String NO_SUPPORTED_LANGUAGES_AND_OTHERS = "no_supported_languages_and_others";
    private final static String ONE_SUPPORTED_LANGUAGE = "one_supported_language";
    private final static String ONE_SUPPORTED_LANGUAGE_AND_OTHERS = "one_supported_language_and_others";
    private final static String THREE_SUPPORTED_LANGUAGES = "three_supported_languages";
    private final static String THREE_SUPPORTED_LANGUAGES_AND_OTHERS = "three_supported_languages_and_others";

    @Before
    public void setUp() throws URISyntaxException {
        repositoriesDir = Paths.get(this.getClass().getClassLoader().getResource("repositories").toURI());
        codeAnalysisService = new CodeAnalysisServiceImpl();
    }

    @Test
    public void getRepositoryCompositionOneSupportedLanguage()  {
        Path repositoryPath = repositoriesDir.resolve(ONE_SUPPORTED_LANGUAGE);
        int linesOfPython = 812;
        RepositoryLanguageComposition composition = codeAnalysisService.scanRepository(repositoryPath);

        assertEquals(1, composition.getRepositoryLanguages().size());
        assertTrue(composition.getRepositoryLanguages().stream().anyMatch((language) -> language.getSupportedLanguage().getName().equals("Python")));
        assertEquals(linesOfPython, composition.getRepositoryLanguages().stream().filter((language) -> language.getSupportedLanguage().getName().equals("Python")).findFirst().get().getLinesOfCode());
        assertEquals(linesOfPython, composition.getLinesOfCode());
    }


    @Test
    public void getRepositoryCompositionOneSupportedLanguageAndOthers() {
        Path repositoryPath = repositoriesDir.resolve(ONE_SUPPORTED_LANGUAGE_AND_OTHERS);
        int linesOfPython = 812;
        int linesOfOthers = 37;
        RepositoryLanguageComposition composition = codeAnalysisService.scanRepository(repositoryPath);

        assertEquals(2, composition.getRepositoryLanguages().size());

        assertTrue(composition.getRepositoryLanguages().stream().anyMatch((language) -> language.getSupportedLanguage().getName().equals("Python")));
        assertTrue(composition.getRepositoryLanguages().stream().anyMatch((language) -> language.getSupportedLanguage().getName().equals("Others")));

        assertEquals(linesOfPython, composition.getRepositoryLanguages().stream().filter((language) -> language.getSupportedLanguage().getName().equals("Python")).findFirst().get().getLinesOfCode());
        assertEquals(linesOfOthers, composition.getRepositoryLanguages().stream().filter((language) -> language.getSupportedLanguage().getName().equals("Others")).findFirst().get().getLinesOfCode());

        assertEquals(linesOfOthers+linesOfPython, composition.getLinesOfCode());
    }

    @Test
    public void getRepositoryCompositionNoSupportedLanguagesAndOthers() {
        Path repositoryPath = repositoriesDir.resolve(NO_SUPPORTED_LANGUAGES_AND_OTHERS);
        int linesOfOthers = 856;
        RepositoryLanguageComposition composition = codeAnalysisService.scanRepository(repositoryPath);

        assertEquals(1, composition.getRepositoryLanguages().size());

        assertTrue(composition.getRepositoryLanguages().stream().anyMatch((language) -> language.getSupportedLanguage().getName().equals("Others")));

        assertEquals(linesOfOthers, composition.getRepositoryLanguages().stream().filter((language) -> language.getSupportedLanguage().getName().equals("Others")).findFirst().get().getLinesOfCode());

        assertEquals(linesOfOthers, composition.getLinesOfCode());
    }

    @Test
    public void getRepositoryCompositionNoSupportedLanguages() {
        Path repositoryPath = repositoriesDir.resolve(NO_SUPPORTED_LANGUAGES);
        int linesOfCode = 0;
        RepositoryLanguageComposition composition = codeAnalysisService.scanRepository(repositoryPath);
        assertEquals(0, composition.getRepositoryLanguages().size());
        assertEquals(0, composition.getLinesOfCode());
    }

    @Test
    public void getRepositoryCompositionThreeSupportedLanguages() {
        Path repositoryPath = repositoriesDir.resolve(THREE_SUPPORTED_LANGUAGES);
        int linesOfHtml = 2817;
        int linesOfJavascript = 435;
        int linesOfPython = 812;

        RepositoryLanguageComposition composition = codeAnalysisService.scanRepository(repositoryPath);


        assertEquals(3, composition.getRepositoryLanguages().size());

        assertTrue(composition.getRepositoryLanguages().stream().anyMatch((language) -> language.getSupportedLanguage().getName().equals("Python")));
        assertTrue(composition.getRepositoryLanguages().stream().anyMatch((language) -> language.getSupportedLanguage().getName().equals("HTML")));
        assertTrue(composition.getRepositoryLanguages().stream().anyMatch((language) -> language.getSupportedLanguage().getName().equals("JavaScript")));

        assertEquals(linesOfPython, composition.getRepositoryLanguages().stream().filter((language) -> language.getSupportedLanguage().getName().equals("Python")).findFirst().get().getLinesOfCode());
        assertEquals(linesOfHtml, composition.getRepositoryLanguages().stream().filter((language) -> language.getSupportedLanguage().getName().equals("HTML")).findFirst().get().getLinesOfCode());
        assertEquals(linesOfJavascript, composition.getRepositoryLanguages().stream().filter((language) -> language.getSupportedLanguage().getName().equals("JavaScript")).findFirst().get().getLinesOfCode());

        assertEquals(linesOfHtml+linesOfJavascript+linesOfPython, composition.getLinesOfCode());
    }

    @Test
    public void getRepositoryCompositionThreeSupportedLanguagesAndOthers() {
        Path repositoryPath = repositoriesDir.resolve(THREE_SUPPORTED_LANGUAGES_AND_OTHERS);
        int linesOfHtml = 2817;
        int linesOfJavascript = 435;
        int linesOfPython = 812;
        int linesOfOthers = 37;

        RepositoryLanguageComposition composition = codeAnalysisService.scanRepository(repositoryPath);


        assertEquals(4, composition.getRepositoryLanguages().size());

        assertTrue(composition.getRepositoryLanguages().stream().anyMatch((language) -> language.getSupportedLanguage().getName().equals("Python")));
        assertTrue(composition.getRepositoryLanguages().stream().anyMatch((language) -> language.getSupportedLanguage().getName().equals("HTML")));
        assertTrue(composition.getRepositoryLanguages().stream().anyMatch((language) -> language.getSupportedLanguage().getName().equals("JavaScript")));
        assertTrue(composition.getRepositoryLanguages().stream().anyMatch((language) -> language.getSupportedLanguage().getName().equals("Others")));

        assertEquals(linesOfPython, composition.getRepositoryLanguages().stream().filter((language) -> language.getSupportedLanguage().getName().equals("Python")).findFirst().get().getLinesOfCode());
        assertEquals(linesOfHtml, composition.getRepositoryLanguages().stream().filter((language) -> language.getSupportedLanguage().getName().equals("HTML")).findFirst().get().getLinesOfCode());
        assertEquals(linesOfJavascript, composition.getRepositoryLanguages().stream().filter((language) -> language.getSupportedLanguage().getName().equals("JavaScript")).findFirst().get().getLinesOfCode());
        assertEquals(linesOfOthers, composition.getRepositoryLanguages().stream().filter((language) -> language.getSupportedLanguage().getName().equals("Others")).findFirst().get().getLinesOfCode());

        assertEquals(linesOfHtml+linesOfJavascript+linesOfPython+linesOfOthers, composition.getLinesOfCode());
    }

}