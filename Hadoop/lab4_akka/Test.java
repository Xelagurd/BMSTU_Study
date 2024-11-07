package lab4;

import java.util.List;

public class Test {
    private String testName;
    private String expectedResult;
    private List<Object> params;


    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public String getTestName() {
        return testName;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public List<Object> getParams() {
        return params;
    }

    public String toString() {
        String sParams = "";
        for (Object e : params) {
            sParams += e.toString() + " ";
        }
        sParams = sParams.substring(0, sParams.length() - 1);
        return testName + "  " + expectedResult + " [" + sParams + "]";
    }
}