/**
 * The MIT License
 *
 * Copyright (C) 2021 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.astrapi69.id.generate.enumtype;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * The enum class {@link SequenceValue} holds constants for default sequence values
 */
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SequenceValue
{

	/** The enum value for the default initial value */
	INITIAL_VALUE(SequenceValue.DEFAULT_SEQUENCE_INITIAL_VALUE),

	/** The enum value for the default sequence prefix */
	SEQUENCE_PREFIX(SequenceValue.DEFAULT_SEQUENCE_PREFIX),

	/** The enum value for the default sequence id key for the map */
	SEQUENCE_ID_KEY(SequenceValue.DEFAULT_SEQUENCE_ID_KEY);

	public static final String DEFAULT_SEQUENCE_INITIAL_VALUE = "1";
	public static final String DEFAULT_SEQUENCE_PREFIX = "sequence_";
	public static final String DEFAULT_SEQUENCE_ID_KEY = DEFAULT_SEQUENCE_PREFIX + "id";

	/** The value */
	String value;

}
