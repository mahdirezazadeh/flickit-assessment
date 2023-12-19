package org.flickit.assessment.core.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class QualityAttributeValue {

    private final UUID id;
    private final QualityAttribute qualityAttribute;
    private final List<Answer> answers;
    private Set<MaturityScore> maturityScores = new HashSet<>();
    private MaturityLevel maturityLevel;
    private Double confidenceValue;

    public void calculate(List<MaturityLevel> maturityLevels) {
        Map<Long, Double> totalScore = calcTotalScore(maturityLevels);
        Map<Long, Double> gainedScore = calcGainedScore(maturityLevels);
        Map<Long, Double> percentScore = calcPercent(totalScore, gainedScore);
        maturityLevels.forEach(ml -> {
            long maturityLevelId = ml.getId();
            MaturityScore maturityScore = new MaturityScore(maturityLevelId, percentScore.get(maturityLevelId));
            maturityScores.add(maturityScore);
        });
        maturityLevel = findGainedMaturityLevel(percentScore, maturityLevels);
    }

    private Map<Long, Double> calcTotalScore(List<MaturityLevel> maturityLevels) {
        if (qualityAttribute.getQuestions() == null)
            return new HashMap<>();
        return maturityLevels.stream()
            .flatMap(ml ->
                qualityAttribute.getQuestions().stream()
                    .filter(question -> !isMarkedAsNotApplicable(question.getId()))
                    .map(question -> question.findImpactByMaturityLevel(ml))
                    .filter(Objects::nonNull)
                    .map(impact -> new MaturityLevelScore(ml, impact.getWeight()))
            ).collect(groupingBy(x -> x.maturityLevel().getId(), summingDouble(MaturityLevelScore::score)));
    }

    private boolean isMarkedAsNotApplicable(Long questionId) {
        return answers.stream()
            .anyMatch(answer -> answer.getQuestionId().equals(questionId) && Boolean.TRUE.equals(answer.getIsNotApplicable()));
    }

    private Map<Long, Double> calcGainedScore(List<MaturityLevel> maturityLevels) {
        return maturityLevels.stream()
            .flatMap(ml ->
                answers.stream()
                    .filter(answer ->  !Boolean.TRUE.equals(answer.getIsNotApplicable()) && answer.getSelectedOption() != null)
                    .map(answer -> answer.findImpactByMaturityLevel(ml))
                    .filter(Objects::nonNull)
                    .map(impact -> new MaturityLevelScore(ml, impact.calculateScore()))
            ).collect(groupingBy(x -> x.maturityLevel().getId(), summingDouble(MaturityLevelScore::score)));
    }

    private Map<Long, Double> calcPercent(Map<Long, Double> totalScore, Map<Long, Double> gainedScore) {
        return totalScore.entrySet().stream()
            .map(e -> {
                Double gained = gainedScore.get(e.getKey());
                double percent = gained != null ? (gained / e.getValue()) * 100 : 0;
                return Map.entry(e.getKey(), percent);
            }).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private MaturityLevel findGainedMaturityLevel(Map<Long, Double> percentScore, List<MaturityLevel> maturityLevels) {
        List<MaturityLevel> sortedMaturityLevels = maturityLevels.stream()
            .sorted(comparingInt(MaturityLevel::getIndex))
            .toList();

        MaturityLevel result = null;
        for (MaturityLevel ml : sortedMaturityLevels) {
            if (!passLevel(percentScore, ml))
                break;
            result = ml;
        }
        return result;
    }

    private boolean passLevel(Map<Long, Double> percentScore, MaturityLevel ml) {
        List<LevelCompetence> levelCompetences = ml.getLevelCompetences();

        return levelCompetences.isEmpty() || levelCompetences.stream()
            .allMatch(levelCompetence -> {
                Long mlId = levelCompetence.getMaturityLevelId();
                return !percentScore.containsKey(mlId) || percentScore.get(mlId) >= levelCompetence.getValue();
            });
    }

    private record MaturityLevelScore(MaturityLevel maturityLevel, double score) {
    }

    public int getWeightedLevel() {
        Assert.notNull(maturityLevel, () -> "maturityLevel should not be null");
        return maturityLevel.getValue() * qualityAttribute.getWeight();
    }

    public void calculateConfidenceValue() {
        var questionIdToWeightMap = findAnsweredQuestions();
        if (questionIdToWeightMap == null || questionIdToWeightMap.isEmpty()) {
            this.confidenceValue = null;
            return;
        }
        Double totalScore = calcConfidenceTotalScore(questionIdToWeightMap);
        Double gainedScore = calcConfidenceGainedScore(questionIdToWeightMap);
        this.confidenceValue = (gainedScore / totalScore) * 100;
    }

    private Map<Long, Double> findAnsweredQuestions() {
        if (answers == null || qualityAttribute.getQuestions() == null) {
            return Collections.emptyMap();
        }
        List<Answer> validAnswers = answers.stream()
            .filter(x -> (Boolean.TRUE.equals(x.getIsNotApplicable()) || x.getSelectedOption() != null))
            .toList();
        return qualityAttribute.getQuestions().stream()
            .filter(q -> validAnswers.stream().anyMatch(a -> a.getQuestionId().equals(q.getId())))
            .collect(Collectors.toMap(Question::getId, QualityAttributeValue::calculateQuestionWeight));
    }

    private static double calculateQuestionWeight(Question question) {
        Assert.notNull(question.getImpacts(), () -> "Question impacts must not be null.");
        Assert.notEmpty(question.getImpacts(), () -> "Question impacts must not be empty.");
        double sum = question.getImpacts().stream()
            .mapToDouble(QuestionImpact::getWeight)
            .sum();
        return sum / question.getImpacts().size();
    }

    private Double calcConfidenceTotalScore(Map<Long, Double> questionIdToWeightMap) {
        return questionIdToWeightMap.keySet().stream()
            .mapToDouble(question -> {
                Double questionWeight = questionIdToWeightMap.get(question);
                return questionWeight * ConfidenceLevel.getMaxLevel().getId();
            })
            .sum();
    }

    private double calcConfidenceGainedScore(Map<Long, Double> questionIdToWeightMap) {
        return answers.stream()
            .filter(answer ->  Boolean.TRUE.equals(answer.getIsNotApplicable()) || answer.getSelectedOption() != null)
            .mapToDouble(answer -> questionIdToWeightMap.get(answer.getQuestionId()) * answer.getConfidenceLevelId())
            .sum();
    }

    public Double getWeightedConfidenceValue() {
        return confidenceValue * qualityAttribute.getWeight();
    }
}
