package lab4;

import java.util.Map;

public class FullAnswer {
    private String packageId;
    private Map<String, String> results;

    public FullAnswer(String packageId, Map<String, String> results) {
        this.packageId = packageId;
        this.results = results;
    }

    public String getPackageId() {
        return packageId;
    }

    public Map<String, String> getResults() {
        return results;
    }
}
