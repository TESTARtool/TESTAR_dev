package nl.ou.testar.visualvalidation.extractor;

public class ExtractorFactory {
    public static TextExtractorInterface CreateDummyExtractor(){
        return new DummyExtractor();
    }

    public static TextExtractorInterface CreateExpectedTextExtractorDesktop(){
        return new ExpectedTextExtractorDesktop();
    }

    public static TextExtractorInterface CreateExpectedTextExtractorWebdriver(){
        return new ExpectedTextExtractorWebdriver();
    }
}
