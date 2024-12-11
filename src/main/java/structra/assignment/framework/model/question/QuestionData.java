/*
 * MIT License
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

package structra.assignment.framework.model.question;

import lombok.Data;
import lombok.NonNull;
import structra.assignment.framework.model.answer.AnswerData;
import structra.assignment.framework.model.gen.questions.QuestionFactory;
import structra.assignment.framework.model.question.base.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Holds a chunk of data loaded from the database representing a Question. This class is mainly used
 * to get transformed into an actual {@link Question} instance by the {@link QuestionFactory}.
 *
 * @implSpec Supported type strings can be obtained from the enum {@link QuestionType}
 * @implSpec Supported type strings can be obtained from the enum {@link QuestionType}
 * @see Question
 * @see QuestionFactory
 * @see Question
 * @see QuestionFactory
 */
@Data
public class QuestionData {
    private final String type;
    private final String text;
    private final String imageLink;
    private final long pointsPossible;
    private final String explanation;
    private final float difficulty;
    private final Collection<AnswerData> answers;

    private final boolean shuffledAnswers;

    public QuestionData(
            String type,
            String text,
            Number difficulty,
            long pointsPossible,
            String explanation,
            String imgLink,
            Collection<AnswerData> answers,
            boolean shuffledAnswers) {
        this.type = type;
        this.text = text;
        this.imageLink = imgLink;
        this.pointsPossible = pointsPossible;
        this.explanation = explanation;
        this.difficulty = difficulty.floatValue();
        this.answers = answers;
        this.shuffledAnswers = shuffledAnswers;
    }

    public QuestionData(@NonNull QuestionType type) {
        this(type.toString(), "", 0.5f, 1, "", "", new ArrayList<>(), false);
    }

    public String getType() { return type;
    }

    public Collection<AnswerData> getAnswers() {return answers;
    }

    public Object getDifficulty() { return difficulty;
    }

    public String getText() { return text;
    }

    public long getPointsPossible() {return pointsPossible;
    }

    public String getExplanation() {return explanation;
    }

    public String getImageLink() {return imageLink;
    }

    public boolean isShuffledAnswers() { return true;
    }
}
