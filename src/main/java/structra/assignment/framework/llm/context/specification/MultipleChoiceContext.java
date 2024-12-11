package structra.assignment.framework.llm.context.specification;

import structra.assignment.framework.llm.MachineLearningModel;
import structra.assignment.framework.model.StringConstants;

public enum MultipleChoiceContext implements SystemContext{
    FORMAT(
            "EXCLUSIVELY return JSON, NOTHING ELSE. Structure MUST follow EXACTLY, "
                    + "NEVER use \\n. Provide 1 Question and 4 possible Answer choices which may or may not be "
                    + "true. At least 1 of the Answers should always be true. Also give a boolean value for each "
                    + "Answer which tells if the Answer is true or false. "
                    + "Respond adhering EXACTLY to format: %s"),
    PROPER_EXPLANATION(
            "The explanation should explain why question is correct."
                    + "It should not contain what this questions aims to achieve");

    private final String contextMessage;

    MultipleChoiceContext(String contextMessage) {
        this.contextMessage = contextMessage;
    }


    private static String getOpenAnswerQuestionFormatTemplate() {
        final String STRING = "string";
        final String DOUBLE = "double";
        final String LONG = "long";
        final String BOOLEAN = "boolean";

        return String.format(
                MachineLearningModel.DEFAULT_DELIMITER
                        + "{\"%s\": {"
                        + "\"%s\": %s, "
                        + "\"%s\": %s, "
                        + "\"%s\": %s, "
                        + "\"%s\": %s"
                        + "}, "
                        + "\"%s\": [ {"
                        + "\"%s\": %s, "
                        + "\"%s\": %s"
                        + "},{"
                        + "\"%s\": %s, "
                        + "\"%s\": %s"
                        + "},{"
                        + "\"%s\": %s, "
                        + "\"%s\": %s"
                        + "},{"
                        + "\"%s\": %s, "
                        + "\"%s\": %s"
                        + "}]"
                        + "}"
                        + MachineLearningModel.DEFAULT_DELIMITER
                        + ". ",
                StringConstants.Questions.QUESTIONS_NAME,
                StringConstants.Questions.QUESTION_TEXT,
                STRING,
                StringConstants.Questions.QUESTION_DIFFICULTY,
                DOUBLE,
                StringConstants.Overall.POINTS_POSSIBLE,
                LONG,
                StringConstants.Questions.QUESTION_EXPLANATION,
                STRING,
                StringConstants.Answers.ANSWERS_NAME,
                StringConstants.Answers.ANSWER_TEXT,
                STRING,
                StringConstants.Answers.EXPECTED_ANSWER,
                BOOLEAN,
                StringConstants.Answers.ANSWER_TEXT,
                STRING,
                StringConstants.Answers.EXPECTED_ANSWER,
                BOOLEAN,
                StringConstants.Answers.ANSWER_TEXT,
                STRING,
                StringConstants.Answers.EXPECTED_ANSWER,
                BOOLEAN,
                StringConstants.Answers.ANSWER_TEXT,
                STRING,
                StringConstants.Answers.EXPECTED_ANSWER,
                BOOLEAN);
    }

    @Override
    public String getContext() {return String.format(contextMessage, getOpenAnswerQuestionFormatTemplate());}

}
