package structra.assignment.framework.llm.gen.questions;

import com.google.gson.*;
import lombok.NonNull;
import structra.assignment.framework.llm.context.SystemContextBuilder;
import structra.assignment.framework.llm.context.specification.MultipleChoiceContext;
import structra.assignment.framework.llm.context.specification.OpenQuestionContext;
import structra.assignment.framework.model.StringConstants;
import structra.assignment.framework.model.answer.AnswerData;
import structra.assignment.framework.model.answer.concrete.BooleanAnswer;
import structra.assignment.framework.model.answer.concrete.TextAnswer;
import structra.assignment.framework.model.gen.QuizzMaker;
import structra.assignment.framework.model.question.QuestionData;
import structra.assignment.framework.model.question.QuestionType;
import structra.assignment.framework.model.question.concrete.MultiCheckboxQuestion;
import structra.assignment.framework.model.question.concrete.OpenAnswerQuestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MultipleChoiceTarget implements QuestionGenerationTarget<MultiCheckboxQuestion>{


    private final String prompt;


    public MultipleChoiceTarget(String prompt) {
        Objects.requireNonNull(prompt, "Prompt can not be null");
        this.prompt = prompt;
    }


    public String getBasePrompt() {
        return prompt;
    }

    public MultiCheckboxQuestion parse(String input) {
        Objects.requireNonNull(input, "Input string cannot be null");

        try {
            JsonObject object = JsonParser.parseString(input).getAsJsonObject();
            List<AnswerData> answerData = constructAnswerData(object);
            QuestionData questionData = parseQuestionData(object, answerData);
            return (MultiCheckboxQuestion) QuizzMaker.createQuestion(questionData);
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            return createErrorQuestion(); // Return error question on parsing failure
        }
    }

    private List<AnswerData> constructAnswerData(JsonObject answerObject) throws JsonIOException {

        JsonArray answerList = answerObject.getAsJsonArray(StringConstants.Answers.ANSWERS_NAME);
        List<AnswerData> answerData = new ArrayList<>();

        for(JsonElement x : answerList){

            answerData.add(new AnswerData(
                    BooleanAnswer.class.getName(),
                    x.getAsJsonObject().get(StringConstants.Answers.ANSWER_TEXT).getAsString(),
                    x.getAsJsonObject().get(StringConstants.Answers.EXPECTED_ANSWER).getAsString(),
                    "")
            );
        }
        return answerData;
    }

    private QuestionData parseQuestionData(JsonObject questionObject, List<AnswerData> answerData)
            throws JsonIOException {
        JsonObject question =
                questionObject.getAsJsonObject(StringConstants.Questions.QUESTIONS_NAME);

        return new QuestionData(
                QuestionType.MULTIPLE_CHOICE.toString(),
                question.get(StringConstants.Questions.QUESTION_TEXT).toString(),
                question.get(StringConstants.Questions.QUESTION_DIFFICULTY).getAsDouble(),
                question.get(StringConstants.Overall.POINTS_POSSIBLE).getAsInt(),
                question.get(StringConstants.Questions.QUESTION_EXPLANATION).getAsString(),
                "",
                answerData,
                false);
    }

    private MultiCheckboxQuestion createErrorQuestion() {
        AnswerData errorAnswer =
                new AnswerData(
                        BooleanAnswer.class.getName(),
                        "ok", // Adjust error message as needed
                        "",
                        "");

        QuestionData errorQuestion =
                new QuestionData(
                        QuestionType.MULTIPLE_CHOICE.toString(),
                        "An error occurred while generating the question. Please try again.",
                        0.0,
                        0,
                        "Error in question generation",
                        "",
                        Collections.singletonList(errorAnswer),
                        false);

        return (MultiCheckboxQuestion) QuizzMaker.createQuestion(errorQuestion);
    }

    @Override
    public @NonNull String getTargetContext() {
        return new SystemContextBuilder()
                .addContext(MultipleChoiceContext.FORMAT)
                .addContext(MultipleChoiceContext.PROPER_EXPLANATION)
                .build();
    }


}
