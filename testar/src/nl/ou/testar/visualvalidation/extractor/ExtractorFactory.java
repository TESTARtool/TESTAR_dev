package nl.ou.testar.visualvalidation.extractor;

public class ExtractorFactory {
    public static TextExtractorInterface CreateTextExtractor(){
        return CreateExpectedTextExtractor();
    }

    private static TextExtractorInterface CreateDummyExtractor(){
        return new DummyExtractor();
    }

    private static TextExtractorInterface CreateExpectedTextExtractor(){
        return new ExpectedTextExtractor();
    }
}
