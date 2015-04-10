/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import java.math.BigInteger;

/**
 *
 * @author Otmar
 */
public class AutomataStats {
    
    private BigInteger numberOfScenarios;
    private Integer shortestScenario;
    private Integer longestScenarioSize;
    private String shortestScenarioExample;
    private String longestScenarioExample;

    /**
     * @return the numberOfScenarios
     */
    public BigInteger getNumberOfScenarios() {
        return numberOfScenarios;
    }

    /**
     * @param numberOfScenarios the numberOfScenarios to set
     */
    public void setNumberOfScenarios(BigInteger numberOfScenarios) {
        this.numberOfScenarios = numberOfScenarios;
    }

    /**
     * @return the shortestScenario
     */
    public Integer getShortestScenario() {
        return shortestScenario;
    }

    /**
     * @param shortestScenario the shortestScenario to set
     */
    public void setShortestScenario(Integer shortestScenario) {
        this.shortestScenario = shortestScenario;
    }

    /**
     * @return the longestScenario
     */
    public Integer getLongestScenario() {
        return longestScenarioSize;
    }

    /**
     * @param longestScenario the longestScenario to set
     */
    public void setLongestScenario(Integer longestScenario) {
        this.longestScenarioSize = longestScenario;
    }

    /**
     * @return the shortestScenarioExample
     */
    public String getShortestScenarioExample() {
        return shortestScenarioExample;
    }

    /**
     * @param shortestScenarioExample the shortestScenarioExample to set
     */
    public void setShortestScenarioExample(String shortestScenarioExample) {
        this.shortestScenarioExample = shortestScenarioExample;
    }

    /**
     * @return the longestScenarioExample
     */
    public String getLongestScenarioExample() {
        return longestScenarioExample;
    }

    /**
     * @param longestScenarioExample the longestScenarioExample to set
     */
    public void setLongestScenarioExample(String longestScenarioExample) {
        this.longestScenarioExample = longestScenarioExample;
    }
}
