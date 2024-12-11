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

package structra.assignment.framework.model.answer;

import lombok.Data;
import structra.assignment.framework.model.answer.base.Answer;
import structra.assignment.framework.model.gen.answers.AnswerFactory;
import structra.assignment.framework.utils.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * Holds a chunk of data loaded from the database representing an Answer. This class is mainly used
 * to get transformed into * an actual {@link Answer} instance by the {@link AnswerFactory}.
 *
 * @author Riko Torun
 * @author Artur Rausch
 * @implSpec Supported type strings can be obtained from the enum {@link AnswerTypes}
 * @see Answer
 */
@Data
public class AnswerData implements Serializable {

    private final String type;
    private final String text;
    private final Serializable expected;
    private Serializable key;

    public AnswerData(Answer<?> answer) {
        this.type =
                StringUtils.validateAnswerTypeString(answer.getClass().getName())
                        .orElseGet(String::new);
        this.text = answer.getText();
        this.expected = Objects.toString(answer.getExpected());
        this.key = answer.getKey();
    }

    public AnswerData(String type, String text, Serializable expected, Serializable key) {
        this.type = type;
        this.text = text;
        this.expected = expected;
        this.key = key;
    }

    public String getType() { return type;
    }

    public Serializable getExpected() { return expected;
    }

    public Serializable getKey() { return key;
    }

    public String getText() { return text;
    }
}
