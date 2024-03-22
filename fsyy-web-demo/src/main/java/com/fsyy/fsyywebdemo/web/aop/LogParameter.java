package com.fsyy.fsyywebdemo.web.aop;

public class LogParameter {
    private long startTime;
    private String input;

    public LogParameter(String input, long startTime) {
        this.startTime = startTime;
        this.input = input;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    @Override
    public String toString() {
        return "LogParameter{" +
                "startTime=" + startTime +
                ", input='" + input + '\'' +
                '}';
    }
}
