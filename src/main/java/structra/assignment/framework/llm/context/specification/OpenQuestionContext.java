/* MIT License
 *
 * Copyright (c) 2024 Riko Torun, Artur Rausch, Lennart Köhler, Moritz Wiedemann, Tim Stöcker, Souren Ishkhanian
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package structra.assignment.framework.llm.context.specification;

import structra.assignment.framework.llm.MachineLearningModel;
import structra.assignment.framework.model.StringConstants;
import structra.assignment.framework.model.answer.concrete.TextAnswer;
import structra.assignment.framework.model.question.concrete.OpenAnswerQuestion;

/**
 * Represents all necessary context to create an {@link OpenAnswerQuestion} and {@link TextAnswer}
 * using a {@link MachineLearningModel} instance.
 *
 * @author Riko Torun
 */
public enum OpenQuestionContext implements SystemContext {
    FORMAT(
            "EXCLUSIVELY return JSON, NOTHING ELSE. Structure MUST follow EXACTLY, "
                    + "NEVER use \\n. Provide 1 Question and 1 Answer. Respond adhering "
                    + "EXACTLY to format: %s"),
    PROPER_EXPLANATION(
            "The explanation should explain why question is correct."
                    + "It should not contain what this questions aims to achieve");

    private final String contextMessage;

    OpenQuestionContext(String contextMessage) {
        this.contextMessage = contextMessage;
    }

    /**
     * Generates the JSON format template for the questions and answers.
     *
     * @return The formatted JSON string template.
     */
    private static String getOpenAnswerQuestionFormatTemplate() {
        final String STRING = "string";
        final String DOUBLE = "double";
        final String LONG = "long";

        return String.format(
                MachineLearningModel.DEFAULT_DELIMITER
                        + "{\"%s\": {"
                        + "\"%s\": %s, "
                        + "\"%s\": %s, "
                        + "\"%s\": %s, "
                        + "\"%s\": %s"
                        + "}, "
                        + "\"%s\": {"
                        + "\"%s\": %s, "
                        + "\"%s\": %s"
                        + "}"
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
                STRING);
    }

    /**
     * @return {@inheritDoc}
     */
    @Override
    public String getContext() {return String.format(contextMessage, getOpenAnswerQuestionFormatTemplate());}
}
