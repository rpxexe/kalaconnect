package com.kalaconnect.models;

import java.io.Serializable;
import java.util.List;

public class QuizQuestion implements Serializable {

    private String id;
    private String question;
    private List<String> options;
    private int correctOptionIndex;
    private String explanation;
    private String knowledgeDomain;
    private int selectedOptionIndex = -1;

    public QuizQuestion() {}

    public QuizQuestion(String id, String question, List<String> options,
                        int correctOptionIndex, String explanation, String knowledgeDomain) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.explanation = explanation;
        this.knowledgeDomain = knowledgeDomain;
        this.selectedOptionIndex = -1;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public void setCorrectOptionIndex(int correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getKnowledgeDomain() { return knowledgeDomain; }
    public void setKnowledgeDomain(String knowledgeDomain) { this.knowledgeDomain = knowledgeDomain; }

    public int getSelectedOptionIndex() { return selectedOptionIndex; }
    public void setSelectedOptionIndex(int selectedOptionIndex) { this.selectedOptionIndex = selectedOptionIndex; }

    public boolean isAnswered() {
        return selectedOptionIndex >= 0;
    }

    public boolean isCorrect() {
        return selectedOptionIndex == correctOptionIndex;
    }
}
