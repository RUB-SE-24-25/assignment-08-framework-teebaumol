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

package structra.assignment.framework.model.answer.base;

import lombok.NonNull;
import structra.assignment.framework.model.Data;
import structra.assignment.framework.model.question.base.Question;

import java.io.Serializable;
import java.util.Objects;

/**
 * The abstract Answer class holds information about some user input with respect to some expected
 * input.<br>
 * In order to evaluate Answers for their correctness (I) Each answers Input type is specified by
 * the generic T in order to allow easy answer comparison. In order to evaluate an Answer the second
 * one should be called. Since an Answer must provide a valid expected input the expected input must
 * not be null.
 *
 * @param <T> the specified Answer Type
 * @author Lennart Köhler
 * @see Question
 */
@lombok.Data
public abstract class Answer<T extends Serializable> implements Data {
    public static final String NAME = "Answer";

    protected String text;
    protected T input;
    protected final T initialInput;

    protected T expected;
    private Serializable key;
    private int index;

    /**
     * Creates a new Answer with the given initial input, expected input and Text this Answer should
     * have
     *
     * @param initialInput the initial input for this question or null if none is given
     * @param expected the non-null expected input
     * @param text the Text this answer should have or null if none is given.
     * @throws NullPointerException if the expected input is null
     */
    public Answer(T initialInput, @NonNull T expected, String text) {
        this.text = text;
        this.input = initialInput;
        this.initialInput = initialInput;
        this.expected = Objects.requireNonNull(expected);
    }

    @Override
    public Serializable getKey() {
        return key;
    }

    @Override
    public void setKey(Serializable key) {
        this.key = key;
    }

    public String getText() { return text;
    }

    public T getExpected() { return expected;
    }
}
